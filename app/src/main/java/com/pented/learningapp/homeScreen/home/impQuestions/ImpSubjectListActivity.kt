package com.pented.learningapp.homeScreen.home.impQuestions

import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.View
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.RecyclerView
import com.pented.learningapp.BR
import com.pented.learningapp.R
import com.pented.learningapp.base.BaseActivity
import com.pented.learningapp.base.BaseViewModel
import com.pented.learningapp.base.BindingAdapter
import com.pented.learningapp.databinding.ActivityImpSubjectsBinding
import com.pented.learningapp.helper.Constants
import com.pented.learningapp.helper.Utils
import com.pented.learningapp.homeScreen.home.HomeFragment
import com.pented.learningapp.homeScreen.home.impQuestions.model.GetImpQuestionResponseModel
import com.pented.learningapp.homeScreen.home.impQuestions.viewModel.ImpSubjectVM
import com.pented.learningapp.homeScreen.home.model.GetHomeDataResponseModel
import kotlinx.android.synthetic.main.activity_imp_subjects.*
import kotlinx.android.synthetic.main.transparent_progressbar_layout.*
import java.util.*
import kotlin.collections.ArrayList

class ImpSubjectListActivity : BaseActivity<ActivityImpSubjectsBinding>() {
    override fun layoutID() = R.layout.activity_imp_subjects
    val topicDataList = ArrayList<GetImpQuestionResponseModel.Data>()
    lateinit var impQuestionsVM: ImpSubjectVM
    lateinit var recyclerView: RecyclerView

    override fun viewModel(): BaseViewModel = ViewModelProvider(this).get(ImpSubjectVM::class.java)
    var timer = Timer()
    var DELAY: Long = 500
    var generalTextWatcher: TextWatcher? = null
    var searchValue: String? = null
    var subjectListAPIFilter: ArrayList<GetHomeDataResponseModel.Subject> = ArrayList<GetHomeDataResponseModel.Subject>()

    override fun initActivity() {
        init()
        listner()
        observer()
    }

    private fun observer() {

                setTopicVideoListAdapter()


        impQuestionsVM.observedChanges().observe(this, { event ->
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

    public fun showDialog() {
        Utils.hideKeyboard(this)
        lilProgressBar.visibility = View.VISIBLE
        //animationView.visibility = View.VISIBLE
        Utils.getNonWindowTouchable(this)
    }

    public fun hideDialog() {
        lilProgressBar.visibility = View.GONE
        // animationView.visibility = View.GONE
        Utils.getWindowTouchable(this)
    }
    private fun listner() {
        ivBack.setOnClickListener {
            onBackPressed()
        }


        ivSearch.setOnClickListener {
            lilSearch.visibility = View.VISIBLE
            Utils.showKeyboard(this@ImpSubjectListActivity, edtSearch)
            ivSearch.visibility = View.GONE
        }
        icCross.setOnClickListener {
            edtSearch.setText("")
            Utils.hideKeyboard(this@ImpSubjectListActivity)
            ivSearch.visibility = View.VISIBLE
            lilSearch.visibility = View.GONE
            setTopicVideoListAdapter()
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
                                setTopicVideoListAdapter(searchValue ?: "")
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
                        setTopicVideoListAdapter()
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

    override fun onStart() {
        super.onStart()
    }

    override fun onStop() {
        super.onStop()
    }

    private fun init() {
        impQuestionsVM = (getViewModel() as ImpSubjectVM)

        setTopicVideoListAdapter()
    }

    private fun setTopicVideoListAdapter(searchValue:String  = "") {
        if(searchValue.isNullOrBlank())
        {
            recycler_view.adapter = BindingAdapter(
                layoutId = R.layout.row_imp_subjects,
                br = BR.model,
                list = ArrayList(HomeFragment.subjectList),
                clickListener = { view, position ->
                    when (view.id) {
                        R.id.lilMain -> {
                            HomeFragment.subjectList[position].Id?.let { it1 ->
                                startActivityWithDataKey(ImpQuestionVideoListNewActivity::class.java,
                                    it1,"subjectId")
                            }
                        }
                    }
                })
        }
        else{
            subjectListAPIFilter.clear()
            for (examBluePrint in HomeFragment.subjectList)
            {
                if(examBluePrint.Name?.contains(searchValue,true)==true || examBluePrint?.Name?.contains(
                        searchValue,ignoreCase = true
                    ) == true)
                {
                    subjectListAPIFilter.add(examBluePrint)
                }
            }
            recycler_view.adapter = BindingAdapter(
                layoutId = R.layout.row_imp_subjects,
                br = BR.model,
                list = ArrayList(subjectListAPIFilter),
                clickListener = { view, position ->
                    when (view.id) {
                        R.id.lilMain -> {
                            subjectListAPIFilter[position].Id?.let { it1 ->
                                startActivityWithDataKey(ImpQuestionVideoListNewActivity::class.java,
                                    it1,"subjectId")
                            }
                        }
                    }
                })
        }

    }


    override fun onDestroy() {
        super.onDestroy()
    }

}