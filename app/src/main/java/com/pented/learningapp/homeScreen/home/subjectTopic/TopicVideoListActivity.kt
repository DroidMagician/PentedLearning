package com.pented.learningapp.homeScreen.home.subjectTopic

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Build
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.View
import androidx.lifecycle.ViewModelProvider
import com.google.gson.Gson
import com.pented.learningapp.BR
import com.pented.learningapp.R
import com.pented.learningapp.base.BaseActivity
import com.pented.learningapp.base.BaseViewModel
import com.pented.learningapp.base.BindingAdapter
import com.pented.learningapp.databinding.ActivityTopicVideoListBinding
import com.pented.learningapp.helper.Constants
import com.pented.learningapp.helper.Constants.isFromNormalVideoList
import com.pented.learningapp.helper.JustCopyItVIewModel
import com.pented.learningapp.helper.Utils
import com.pented.learningapp.homeScreen.home.subjectTopic.ChapterWithAnimation2Activity.Companion.topicDataList
import com.pented.learningapp.homeScreen.home.watchVideo.WatchVideoActivity
import kotlinx.android.synthetic.main.activity_chapter_with_animation2.*
import kotlinx.android.synthetic.main.activity_topic_video_list.*
import kotlinx.android.synthetic.main.activity_topic_video_list.ivBack
import kotlinx.android.synthetic.main.activity_topic_video_list.mainFrame
import kotlinx.android.synthetic.main.transparent_progressbar_layout.*
import java.util.*
import kotlin.collections.ArrayList

class TopicVideoListActivity : BaseActivity<ActivityTopicVideoListBinding>() {
    override fun layoutID() = R.layout.activity_topic_video_list
   // val topicDataList = ArrayList<TopicVideoResponseModel.Video>()
    var topicId:Int? = null
    var chapterName:String? = null
//    var videoSolutionList: ArrayList<GetQuestionPaperBySubjectResponseModel.SolutionVideo> =
//        ArrayList<GetQuestionPaperBySubjectResponseModel.SolutionVideo>()
//    var pdfSolutiontList: ArrayList<GetQuestionPaperBySubjectResponseModel.AnswerSPDF3Bucket> =
//        ArrayList<GetQuestionPaperBySubjectResponseModel.AnswerSPDF3Bucket>()
lateinit var receiver: MyReceiver
    companion object{
        var currentVideo = -1
    }

    override fun viewModel(): BaseViewModel =
        ViewModelProvider(this).get(JustCopyItVIewModel::class.java)
    lateinit var subjectTopicVM: JustCopyItVIewModel
    override fun initActivity() {
        subjectTopicVM = (getViewModel() as JustCopyItVIewModel)
        init()
        subjectTopicVM.observedTopicContentListData().observe(this, { event ->
            event?.getContentIfNotHandled()?.let {
                // Log.e("Topic","Data is ${it.data.Videos?.get(0)?.TopicVideoTitle}")
                topicDataList.clear()
                for(i  in 0 until it?.data?.Videos?.size!!)
                {
//                    if(i<5)
//                    {
//                        it?.data?.Videos!![i].IsCompleted = true
//                    }
                    topicDataList.add(it?.data?.Videos!![i])
                }

                //  it.data.Videos?.let { it1 -> topicDataList.addAll(it1) }
                setTopicVideoListAdapter()
            }
        })
        subjectTopicVM.observedChanges().observe(this, { event ->
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
        listner()
    }

    private fun listner() {
        ivBack.setOnClickListener {
            onBackPressed()
        }
        btnStartYourJourney.setOnClickListener {
            val intent = Intent(this@TopicVideoListActivity, ChapterWithAnimation2Activity::class.java)
            intent.putExtra("topicID",topicId)
            intent.putExtra("chapterName",chapterName)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
            startActivity(intent)
            finish()
        }
    }

    override fun onStart() {
        super.onStart()
    }

    override fun onStop() {
        super.onStop()
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
    private fun init() {
        topicDataList.clear()



        receiver = MyReceiver(Handler(Looper.myLooper()!!))
        val intentfilter = IntentFilter()
        intentfilter.addAction("PlayNextNormalVideo")
//        registerReceiver(receiver, intentfilter)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            registerReceiver(receiver, intentfilter,RECEIVER_EXPORTED)
        } else {
            @Suppress("UnspecifiedRegisterReceiverFlag")
            registerReceiver(receiver, intentfilter)
        }

        if(intent.hasExtra("topicID"))
        {
             topicId = intent.getIntExtra("topicID",0)
            subjectTopicVM.callTopicListData(topicId.toString())

        }
        if(intent.hasExtra("chapterName"))
        {
           chapterName = intent.getStringExtra("chapterName") ?: ""
            Log.e("Inside Video List","Chapter Name ${chapterName}")
            txtHeading.text = chapterName
        }

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
       // setTopicVideoListAdapter()
//        var examBlueprintModel = SolutionModel("Solution for question 1","Maths blueprint",true)
//        var examBlueprintModel1 = SolutionModel("Solution for question 2","Maths blueprint",false)
//        examBlueprintList.add(examBlueprintModel)
//        examBlueprintList.add(examBlueprintModel1)

    }

    private fun setTopicVideoListAdapter() {
        var planetList = ArrayList<Int>()
        planetList.add(R.drawable.ic_teal_world)
        planetList.add(R.drawable.ic_blue_world)
        planetList.add(R.drawable.ic_yellow_world)
        planetList.add(R.drawable.ic_red_world)
        for(topics in topicDataList)
        {
            topics.isRocketVisible = false
            topics.planet_img = planetList.get(Random().nextInt(planetList.size))
            //   planetsModel.add(PlanetsModel(topics.Name!!, planetList.get(Random().nextInt(planetList.size))))
        }
        recycler_view_videos.adapter = BindingAdapter(
            layoutId = R.layout.row_chapter_videos,
            br = BR.model,
            list = ArrayList(ChapterWithAnimation2Activity.topicDataList),
            clickListener = { view, position ->
                when (view.id) {
                    R.id.lilMain -> {
                        val gson = Gson()
                        if(position == ChapterWithAnimation2Activity.topicDataList.size -1)
                        {
                            ChapterWithAnimation2Activity.isLastVideo = true
                        }
                        else{
                            ChapterWithAnimation2Activity.isLastVideo = false
                        }
                        var intent = Intent(this@TopicVideoListActivity, WatchVideoActivity::class.java)
                        //  intent.putExtra("topicVideoId",topicDataList[current].TopicVideoId)
                        currentVideo = position
                        isFromNormalVideoList = true
                        ChapterWithAnimation2Activity.current = position
                        intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT)
                        ChapterWithAnimation2Activity.mainTopicVideoId = ChapterWithAnimation2Activity.topicDataList[position].TopicVideoId ?: 0
                        intent.putExtra("topicVideo",gson.toJson(ChapterWithAnimation2Activity.topicDataList[position]))
                        Log.e("topicVideoId===",gson.toJson(ChapterWithAnimation2Activity.topicDataList[position]))
                        Log.e("current===","Is $position")
                        startActivity(intent)
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
                    if (intent?.action.equals("PlayNextNormalVideo")) {
                        if (currentVideo < ChapterWithAnimation2Activity.topicDataList.size-1) {
                            currentVideo++
                            val gson = Gson()
                            var intent = Intent(this@TopicVideoListActivity, WatchVideoActivity::class.java)
                            //  intent.putExtra("topicVideoId",topicDataList[current].TopicVideoId)
                            currentVideo = currentVideo
                            isFromNormalVideoList = true
                            if(currentVideo == ChapterWithAnimation2Activity.topicDataList.size -1)
                            {
                                ChapterWithAnimation2Activity.isLastVideo = true
                            }
                            else{
                                ChapterWithAnimation2Activity.isLastVideo = false
                            }
                            intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT)
                            ChapterWithAnimation2Activity.current = currentVideo
                            ChapterWithAnimation2Activity.mainTopicVideoId = ChapterWithAnimation2Activity.topicDataList[currentVideo].TopicVideoId ?: 0

                            intent.putExtra("topicVideo",gson.toJson(ChapterWithAnimation2Activity.topicDataList[currentVideo]))
                            Log.e("topicVideoId===",gson.toJson(ChapterWithAnimation2Activity.topicDataList[currentVideo]))
                            Log.e("current===","Is $currentVideo")
                            startActivity(intent)
                        }

                    }

                }


            }
        }
    }

}