package com.pented.learningapp.homeScreen.home

import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.View
import android.view.animation.AnimationUtils
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.pented.learningapp.R
import com.pented.learningapp.adapter.ChaptersAdapter
import com.pented.learningapp.adapter.ChaptersAdapterApi
import com.pented.learningapp.base.BaseActivity
import com.pented.learningapp.databinding.ActivitySubjectBinding
import com.pented.learningapp.helper.Constants
import com.pented.learningapp.helper.Utils
import com.pented.learningapp.homeScreen.home.impQuestions.ImpQuestionVideoListActivity
import com.pented.learningapp.homeScreen.home.impQuestions.ImpQuestionVideoListNewActivity
import com.pented.learningapp.homeScreen.home.model.Chapter
import com.pented.learningapp.homeScreen.home.model.Topic
import com.pented.learningapp.homeScreen.home.viewModel.SubjectVM
import com.pented.learningapp.model.ChaptersModel
import com.pented.learningapp.model.TopicsModel
import kotlinx.android.synthetic.main.activity_subject.*
import kotlinx.android.synthetic.main.transparent_progressbar_layout.*
import java.util.*
import kotlin.collections.ArrayList

class SubjectActivity : BaseActivity<ActivitySubjectBinding>(){
    override fun viewModel() = ViewModelProvider(this).get(SubjectVM::class.java)
    override fun layoutID() = R.layout.activity_subject
    var chapterList: ArrayList<Chapter> = ArrayList<Chapter>()
    var chapterListFilter: ArrayList<Chapter> = ArrayList<Chapter>()
    var timer = Timer()
    var DELAY: Long = 500
    var subjectId :Int ? = null
    var generalTextWatcher: TextWatcher? = null
    var searchValue: String? = null
    lateinit var subjectVM: SubjectVM
    override fun initActivity() {
        subjectVM = (getViewModel() as SubjectVM)
        init()
        observer()
        listener()
    }

    private fun listener() {
        ivBack.setOnClickListener { finish() }

        llSubscribe.setOnClickListener {
            llMainGetNoti.visibility = View.GONE
            llImpQue.visibility = View.VISIBLE
            tvSubscribed.visibility = View.VISIBLE
        }

        tvSubscribed.setOnClickListener {
            llMainGetNoti.visibility = View.VISIBLE
            llImpQue.visibility = View.GONE
            tvSubscribed.visibility = View.GONE
        }

        ivSearch.setOnClickListener {
            lilSearch.visibility = View.VISIBLE
            Utils.showKeyboard(this@SubjectActivity, edtSearch)
            ivSearch.visibility = View.GONE
        }
        icCross.setOnClickListener {
            edtSearch.setText("")
            Utils.hideKeyboard(this@SubjectActivity)
            ivSearch.visibility = View.VISIBLE
            lilSearch.visibility = View.GONE
            val subjectId = intent.getSerializableExtra(Constants.EXTRA) as Int?
            Log.e("Subject ID","Is ${subjectId}")
            if(Constants.isApiCalling)
            {
                subjectId?.toString()?.let { subjectVM.callSubjectDataData(it,"") }
            }
        }

        llImpQue.setOnClickListener {
            subjectId?.let { it1 ->
                startActivityWithDataKey(ImpQuestionVideoListNewActivity::class.java,
                    it1,"subjectId")
            }
        }

        generalTextWatcher = object : TextWatcher {
            override fun afterTextChanged(s: Editable) {
                timer = Timer()
                timer.schedule(object : TimerTask() {
                    override fun run() {
                        Log.e("Search Text", "Is == ${edtSearch.text.toString()}")
                        if (s.isNotEmpty()) {
                            searchValue = edtSearch.text.toString()
                            runOnUiThread(Runnable {
                                setChapterAdapterAPI(searchValue ?: "")
                            })

                            // homeVM.getSuggestionsList(edtSearch.text.toString())
                        }
                    }
                }, DELAY)
            }

            override fun beforeTextChanged(
                s: CharSequence, start: Int,
                count: Int, after: Int
            ) {
            }

            override fun onTextChanged(
                s: CharSequence, start: Int,
                before: Int, count: Int
            ) {
                if (s.isEmpty()) {
                    runOnUiThread(Runnable {
                        val subjectId = intent.getSerializableExtra(Constants.EXTRA) as Int?
                        Log.e("Subject ID","Is ${subjectId}")
                        if(Constants.isApiCalling)
                        {
                            subjectId?.toString()?.let { subjectVM.callSubjectDataData(it,"") }
                        }
                    })
                    //ic_cross.visibility = View.GONE
                    //SEARCH_TEXT = ""
                    // searchLayout.visibility = View.GONE
                    //  SEARCH_TRANSACTION_ID = ""
                    // SEARCH_TRANSACTION_TYPE = ""
                    // sendBroadcast(Intent(Constants.BROADCAST_CLEAR_SEARCH))
                } else {
                    //ic_cross.visibility = View.VISIBLE
                }
                timer.cancel() //Terminates this timer,discarding any currently scheduled tasks.
                timer.purge() //Removes all cancelled tasks from this timer's task queue.
            }
        }
        edtSearch.addTextChangedListener(generalTextWatcher)
    }

    private fun observer() {
        subjectVM.observedErrorMessageChanges().observe(this, { event ->
            event?.getContentIfNotHandled()?.let {
                showErrorMessage(it, this, mainFrame)
            }
        })
        subjectVM.observedSubjectData().observe(this, { event ->
            event?.getContentIfNotHandled()?.let {
                chapterList.clear()
                txtSubjectName.text = it.data.SubjectName
                it.data.CompletedPencentage?.toFloat().let {
                    if (it != null) {
                        progressView1.progress = it
                    }
                }
                txtPoints.text = "${it.data.SubjectPoints?.toString()} Points"
               // Log.e("BluePrint Data is","Here"+it.data[0].Description)
                it.data.Chapters?.let { it1 -> chapterList.addAll(it1) }
                var complatedCount = 0
                for (chapter in chapterList)
                {
                    if(chapter.IsCompleted == true)
                    {
                        complatedCount++
                    }
                }
                tvCompletedChapters.text = "$complatedCount / ${chapterList.size}  chapters completed"
                Log.e("chapterList","Is"+chapterList.size)
                setChapterAdapterAPI()
            }
        })

        subjectVM.observedChanges().observe(this, { event ->
            event?.getContentIfNotHandled()?.let {
                when (it) {
                    Constants.VISIBLE -> {
                        showDialog()
                    }
                    Constants.HIDE -> {
                        hideDialog()
                    }
                    Constants.NAVIGATE -> {

                    }
                    else -> {
                        showMessage(it, this, mainFrame)
                    }
                }
            }
        })
    }

    private fun setChapterAdapterAPI(searchValue: String = "") {
        //adding a layoutmanager
        rvChapters.layoutManager = LinearLayoutManager(applicationContext)
        //creating our adapter
        if(!searchValue.isNullOrBlank())
        {
            chapterListFilter.clear()
            for (chapter in chapterList)
            {

                if(chapter.Topics?.size ?: 0 > 0)
                {
                    var isChapterFound = false
                    var chapterList = ArrayList<Topic>()
                    for(chapter in chapter.Topics!!)
                    {
                        if(chapter.Name?.contains(searchValue) == true)
                        {
                            isChapterFound = true
                            chapterList.add(chapter)
                        }
                    }
                    if(isChapterFound)
                    {
                        chapter.Topics = chapterList
                        chapterListFilter.add(chapter)
                    }
                    else{
                        if(chapter?.Name?.contains(searchValue) == true)
                        {
                            chapterListFilter.add(chapter)
                        }
                    }

                }
                else
                {
                    if(chapter?.Name?.contains(searchValue) == true)
                    {
                        chapterListFilter.add(chapter)
                    }
                }
            }
            val adapter = ChaptersAdapterApi(chapterListFilter,this)

            //now adding the adapter to recyclerview
            rvChapters.adapter = adapter
        }
        else
        {
            val adapter = ChaptersAdapterApi(chapterList,this)

            //now adding the adapter to recyclerview
            rvChapters.adapter = adapter
        }

    }

    public fun showDialog() {
        Utils.hideKeyboard(this)
        lilProgressBar.visibility = View.VISIBLE
        animationView.visibility = View.VISIBLE
        Utils.getNonWindowTouchable(this)
    }

    public fun hideDialog() {
        lilProgressBar.visibility = View.GONE
        animationView.visibility = View.GONE
        Utils.getWindowTouchable(this)
    }
    private fun init() {

        if (intent.hasExtra(Constants.EXTRA))
        {
            subjectId = intent.getSerializableExtra(Constants.EXTRA) as Int?
        }
        else if(intent.hasExtra("subjectId"))
        {
            subjectId = intent.getIntExtra("subjectId",0)
        }
        Constants.subjectId = subjectId?.toString()
        Log.e("Subject ID","Is ${subjectId}")
        var animation = AnimationUtils.loadAnimation(this@SubjectActivity, R.anim.bounce);
        icImpIcon.startAnimation(animation)
        if(Constants.isApiCalling)
        {
            subjectId?.toString()?.let { subjectVM.callSubjectDataData(it,"") }
        }
        else
        {
           //adding a layoutmanager
            rvChapters.layoutManager = LinearLayoutManager(applicationContext)


            //crating an arraylist to store users using the data class user
            val chaptersModel = ArrayList<ChaptersModel>()

            val topicsModel1 = ArrayList<TopicsModel>()

            topicsModel1.add(TopicsModel("Properties of rational numbers"))
            topicsModel1.add(TopicsModel("Decimals and fractions"))
            topicsModel1.add(TopicsModel("Longitude and latitude"))
            topicsModel1.add(TopicsModel("Properties of rational numbers"))
            topicsModel1.add(TopicsModel("Decimals and fractions"))
            topicsModel1.add(TopicsModel("Longitude and latitude"))
            //adding some dummy data to the list
            chaptersModel.add(ChaptersModel("Linear equations",topicsModel1))


            val topicsModel2 = ArrayList<TopicsModel>()

            topicsModel2.add(TopicsModel("Dice and cards"))
            topicsModel2.add(TopicsModel("Calculating probability"))
            topicsModel2.add(TopicsModel("Finding high and low"))
            topicsModel2.add(TopicsModel("Dice and cards"))
            topicsModel2.add(TopicsModel("Calculating probability"))
            topicsModel2.add(TopicsModel("Finding high and low"))

            chaptersModel.add(ChaptersModel("Probability",topicsModel2))



            val topicsModel3 = ArrayList<TopicsModel>()

            topicsModel3.add(TopicsModel("Triangles"))
            topicsModel3.add(TopicsModel("The special shapes of geometry"))
            topicsModel3.add(TopicsModel("Practice and tasks"))
            topicsModel3.add(TopicsModel("Triangles"))
            topicsModel3.add(TopicsModel("The special shapes of geometry"))
            topicsModel3.add(TopicsModel("Practice and tasks"))

            chaptersModel.add(ChaptersModel("Probability",topicsModel3))


            //creating our adapter
            val adapter = ChaptersAdapter(chaptersModel,this)

            //now adding the adapter to recyclerview
            rvChapters.adapter = adapter
        }



    }

}