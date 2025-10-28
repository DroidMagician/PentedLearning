package com.pented.learningapp.homeScreen.home.impQuestions

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Build
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.RecyclerView
import com.google.gson.Gson
import com.pented.learningapp.BR
import com.pented.learningapp.R
import com.pented.learningapp.base.BaseActivity
import com.pented.learningapp.base.BaseViewModel
import com.pented.learningapp.base.BindingAdapter
import com.pented.learningapp.databinding.ActivityImpQuestionVideoListNewBinding
import com.pented.learningapp.helper.Constants
import com.pented.learningapp.helper.Constants.isFromNormalVideoList
import com.pented.learningapp.helper.JustCopyItVIewModel
import com.pented.learningapp.helper.Utils
import com.pented.learningapp.homeScreen.home.impQuestions.model.GetImpQuestionResponseModel
import com.pented.learningapp.homeScreen.home.impQuestions.viewModel.ImpQuestionsVM
import com.pented.learningapp.homeScreen.home.subjectTopic.ChapterWithAnimation2Activity
import com.pented.learningapp.homeScreen.home.watchVideo.WatchVideoActivity
import kotlinx.android.synthetic.main.activity_imp_question_video_list_new.*
import kotlinx.android.synthetic.main.transparent_progressbar_layout.*
import java.util.*
import kotlin.collections.ArrayList

class ImpQuestionVideoListNewActivity : BaseActivity<ActivityImpQuestionVideoListNewBinding>() {
    override fun layoutID() = R.layout.activity_imp_question_video_list_new
    val topicDataList = ArrayList<GetImpQuestionResponseModel.Data>()
    lateinit var impQuestionsVM: ImpQuestionsVM
    lateinit var recyclerView: RecyclerView
    var planetList = ArrayList<Int>()
    var currentBadge = 0
//    var videoSolutionList: ArrayList<GetQuestionPaperBySubjectResponseModel.SolutionVideo> =
//        ArrayList<GetQuestionPaperBySubjectResponseModel.SolutionVideo>()
//    var pdfSolutiontList: ArrayList<GetQuestionPaperBySubjectResponseModel.AnswerSPDF3Bucket> =
//        ArrayList<GetQuestionPaperBySubjectResponseModel.AnswerSPDF3Bucket>()
lateinit var receiver: MyReceiver
    companion object{
        var currentVideoIMP = -1
        var isLastVideo = false
        var impTopicVideoId = -1
    }

    override fun viewModel(): BaseViewModel = ViewModelProvider(this).get(ImpQuestionsVM::class.java)

    override fun initActivity() {
        init()
        listner()
        observer()
    }

    private fun observer() {
        impQuestionsVM.observedErrorMessageChanges().observe(this, { event ->
            event?.getContentIfNotHandled()?.let {
                showErrorMessage(it, this, mainFrame)
            }
        })
        impQuestionsVM.observedTopicContentListData().observe(this, { event ->
            event?.getContentIfNotHandled()?.let {
                // Log.e("Topic","Data is ${it.data.Videos?.get(0)?.TopicVideoTitle}")

                for(topics in it.data)
                {
                    topics.planet_img = getBadgeImage()
                    //   planetsModel.add(PlanetsModel(topics.Name!!, planetList.get(Random().nextInt(planetList.size))))
                    topicDataList.add(topics)
                }
              //  topicDataList.addAll(it.data)
                setTopicVideoListAdapter()
            }
        })

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
    }

    override fun onStart() {
        super.onStart()
    }

    override fun onStop() {
        super.onStop()
    }
    private fun getBadgeImage(): Int {
        if(currentBadge >= planetList.size)
        {
            currentBadge = 0
        }
        Log.e("currentBadge","= $currentBadge")
        Log.e("badgeImageList", "= ${planetList.size}")
        var badgeImage = planetList.get(currentBadge)
        currentBadge++
        return badgeImage
    }
    private fun init() {
        impQuestionsVM = (getViewModel() as ImpQuestionsVM)
        currentBadge = 0
        planetList.add(R.drawable.ic_teal_world)
        planetList.add(R.drawable.ic_blue_world)
        planetList.add(R.drawable.ic_yellow_world)
        planetList.add(R.drawable.ic_red_world)

        if(intent.hasExtra("subjectId"))
        {
            var subjectId = intent.getIntExtra("subjectId",0)
            impQuestionsVM.callGetImpVideoListData(subjectId.toString())
        }
        txtChapterName.text = resources.getText(R.string.todays_important_questions)



        receiver = MyReceiver(Handler(Looper.myLooper()!!))
        val intentfilter = IntentFilter()
        intentfilter.addAction("PlayNextIMPVideo")
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            registerReceiver(receiver, intentfilter, RECEIVER_EXPORTED)
        } else {
            @Suppress("UnspecifiedRegisterReceiverFlag")
            registerReceiver(receiver, intentfilter)
        }
//        registerReceiver(receiver, intentfilter)
//
//        yourObject.SolutionVideos?.let { videoSolutionList.addAll(it) }
//
//        if (videoSolutionList.size > 0) {
//            recycler_view_videos.visibility = View.VISIBLE
//            setVideoSolutionAdapter()
//            imgSaperator.visibility = View.VISIBLE
//        } else {
//            recycler_view_videos.visibility = View.GONE
//            imgSaperator.visibility = View.GONE
//        }
//
//        yourObject.AnswerSPDF3Bucket?.let { pdfSolutiontList.add(it) }
        setTopicVideoListAdapter()
//        var examBlueprintModel = SolutionModel("Solution for question 1","Maths blueprint",true)
//        var examBlueprintModel1 = SolutionModel("Solution for question 2","Maths blueprint",false)
//        examBlueprintList.add(examBlueprintModel)
//        examBlueprintList.add(examBlueprintModel1)

    }

    private fun setTopicVideoListAdapter() {
        recycler_view_videos.adapter = BindingAdapter(
            layoutId = R.layout.row_imp_videos_new,
            br = BR.model,
            list = ArrayList(topicDataList),
            clickListener = { view, position ->
                when (view.id) {
                    R.id.lilMain -> {
                        if(position == topicDataList.size -1)
                        {
                            isLastVideo = true
                        }
                        else{
                            isLastVideo = false
                        }
                      //  var intent = Intent(this@ImpQuestionVideoListNewActivity, WatchVideoActivity::class.java)
                        //  intent.putExtra("topicVideoId",topicDataList[current].TopicVideoId)
                        currentVideoIMP = position
                        isFromNormalVideoList = true
                        currentVideoIMP = position
                        //intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT)
                        impTopicVideoId = topicDataList[position].TopicVideoId?.toInt() ?: 0
                        //intent.putExtra("topicVideo",gson.toJson(topicDataList[position]))

                        Log.e("topicVideoId===",Gson().toJson(topicDataList[position]))
                        Log.e("current===","Is $position")
                        val gson = Gson()
                        var intent = Intent(this@ImpQuestionVideoListNewActivity, WatchImpVideoActivity::class.java)
                        // intent.putExtra("topicVideoId",topicDataList[current].TopicVideoId)
                        intent.putExtra("topicVideo",gson.toJson(topicDataList[currentVideoIMP]))
                        Log.e("current","Is $currentVideoIMP")
                        startActivity(intent)
                    //startActivity(intent)
//                        startActivityWithObjectData(
//                            SolutionVideoActivity::class.java,
//                            videoSolutionList[position]
//                        )
                    }
                }
            })
    }

    private fun setVideoSolutionAdapter() {

    }
    override fun onDestroy() {
        super.onDestroy()
        unregisterReceiver(receiver)
    }

    inner class MyReceiver(handler: Handler) : BroadcastReceiver() {
        var handler: Handler = handler // Handler used to execute code on the UI thread
        override fun onReceive(context: Context?, intent: Intent?) {
            Log.e("Intent", "Action ${intent?.action}")
            handler.post {
                run {
                    if (intent?.action.equals("PlayNextIMPVideo")) {
                        if (currentVideoIMP < topicDataList.size-1) {
                            currentVideoIMP++
                           // val gson = Gson()
                          //  var intent = Intent(this@ImpQuestionVideoListNewActivity, WatchVideoActivity::class.java)
                            //  intent.putExtra("topicVideoId",topicDataList[current].TopicVideoId)
                            currentVideoIMP = currentVideoIMP
                            isFromNormalVideoList = true
                            if(currentVideoIMP == topicDataList.size -1)
                            {
                                isLastVideo = true
                            }
                            else{
                                isLastVideo = false
                            }
                        //    intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT)
                            currentVideoIMP = currentVideoIMP
                            impTopicVideoId = topicDataList[currentVideoIMP].TopicVideoId?.toInt() ?: 0

                         //   intent.putExtra("topicVideo",gson.toJson(topicDataList[currentVideoIMP]))
//                            Log.e("topicVideoId===",gson.toJson(topicDataList[currentVideoIMP]))
                            Log.e("current===","Is $currentVideoIMP")
                          //  startActivity(intent)
                            val gson = Gson()
                            var intent = Intent(this@ImpQuestionVideoListNewActivity, WatchImpVideoActivity::class.java)
                            // intent.putExtra("topicVideoId",topicDataList[current].TopicVideoId)
                            intent.putExtra("topicVideo",gson.toJson(topicDataList[currentVideoIMP]))
                            Log.e("current","Is $currentVideoIMP")
                            startActivity(intent)
                        }

                    }

                }


            }
        }
    }

}