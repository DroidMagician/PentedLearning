package com.pented.learningapp.homeScreen.home.watchVideo

import android.app.Dialog
import android.content.Intent
import android.content.pm.ActivityInfo
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.net.Uri
import android.os.*
import android.util.Log
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.view.WindowManager
import android.widget.*
import androidx.annotation.NonNull
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModelProvider
import com.amazonaws.services.s3.model.GeneratePresignedUrlRequest
import com.google.android.exoplayer2.*
import com.google.android.exoplayer2.SimpleExoPlayer
import com.google.android.exoplayer2.source.*
import com.google.android.exoplayer2.source.hls.HlsMediaSource
import com.google.android.exoplayer2.trackselection.DefaultTrackSelector
import com.google.android.exoplayer2.trackselection.MappingTrackSelector
import com.google.android.exoplayer2.trackselection.TrackSelectionArray
import com.google.android.exoplayer2.upstream.DataSource
import com.google.android.exoplayer2.upstream.DefaultBandwidthMeter
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory
import com.google.android.exoplayer2.util.MimeTypes
import com.google.gson.Gson
import com.pented.learningapp.R
import com.pented.learningapp.amazonS3.S3Util
import com.pented.learningapp.base.BaseActivity
import com.pented.learningapp.base.BaseViewModel
import com.pented.learningapp.databinding.ActivityWatchVideoBinding
import com.pented.learningapp.helper.*
import com.pented.learningapp.homeScreen.home.model.TopicVideoResponseModel
import com.pented.learningapp.homeScreen.home.model.VideoQuestionResponseModel
import com.pented.learningapp.homeScreen.home.questionAnswer.QuestionAnswerActivity
import com.pented.learningapp.homeScreen.home.subjectTopic.ChapterWithAnimation2Activity
import com.pented.learningapp.homeScreen.home.viewModel.WatchVideoVM
import com.pented.learningapp.retrofit.API.Companion.VIDEO_BASE_URL
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.PlayerConstants
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.PlayerConstants.PlayerState
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.YouTubePlayer
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.listeners.AbstractYouTubePlayerListener
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.utils.YouTubePlayerTracker
import kotlinx.android.synthetic.main.activity_otpactivity.*
import kotlinx.android.synthetic.main.activity_watch_video.*
import kotlinx.android.synthetic.main.activity_watch_video.mainFrame
import kotlinx.android.synthetic.main.row_top_candidates.view.*
import kotlinx.android.synthetic.main.transparent_progressbar_layout.*
import java.net.URL
import java.util.*
import java.util.concurrent.Executors
import kotlin.collections.ArrayList

const val STATE_RESUME_WINDOW = "resumeWindow"
const val STATE_RESUME_POSITION = "resumePosition"
const val STATE_PLAYER_FULLSCREEN = "playerFullscreen"
const val STATE_PLAYER_PLAYING = "playerOnPlay"
var totalWatchCount = 0
class WatchVideoActivity : BaseActivity<ActivityWatchVideoBinding>() {
    private lateinit var simpleExoPlayer: SimpleExoPlayer
    private lateinit var mediaDataSourceFactory: DataSource.Factory
    var fullscreenButton: ImageView? = null
    var fullscreen = false
    var videoWatchedTime: Long = 0
    private var isFullscreen = false
    var isFirstTimePaused = false
    var isFromGoBack = false

    private var handler: Handler? = null
    private var customeCountDownTimer: CustomCountDownTimer? = null
    private var handlerOneMinute: MyHandler? = null
    private var handlerOneMinuteMain: CustomHandler? = null
    var questionList = ArrayList<VideoQuestionResponseModel.Question>()
    var videoId = 0
    val tracker: YouTubePlayerTracker = YouTubePlayerTracker()
    var topicVideosList = ArrayList<TopicVideoResponseModel.Video>()
    override fun layoutID() = R.layout.activity_watch_video
    private val playerStatesHistory: List<Pair<Date, String>> = ArrayList()
    override fun viewModel(): BaseViewModel = ViewModelProvider(this).get(WatchVideoVM::class.java)
    lateinit var watchVideoVM: WatchVideoVM
    private val mainHandler: Handler? = null
    private lateinit var trackSelector: DefaultTrackSelector
    private lateinit var exoQuality: ImageButton
    private var currentWindow = 0
    private var trackDialog: Dialog? = null
    private val BANDWIDTH_METER = DefaultBandwidthMeter()
    var videoRendererIndex = 0
    private var playbackPosition: Long = 15000
    var trackGroups: TrackGroupArray? = null
    val HI_BITRATE = 2097152
    private var isPlayerPlaying = true
    val MI_BITRATE = 1048576
    //        val LO_BITRATE = 524288
    val LO_BITRATE = 32768
     val MAX_HEIGHT = 539

     val MAX_WIDTH = 959
     val HLS_STATIC_URL = "https://bitdash-a.akamaihd.net/content/MI201109210084_1/m3u8s/f08e80da-bf1d-4e3d-8899-f0f6155f6efa.m3u8"
   //  val HLS_STATIC_URL = "https://d3qwsj35pay17l.cloudfront.net/output/hls/1.m3u8"
   var isVideoEnded=false
    override fun onSaveInstanceState(outState: Bundle) {
        if(::simpleExoPlayer.isInitialized)
        {
            simpleExoPlayer?.currentWindowIndex?.let { outState.putInt(STATE_RESUME_WINDOW, it) }
            simpleExoPlayer?.currentPosition?.let { outState.putLong(STATE_RESUME_POSITION, it) }
            outState.putBoolean(STATE_PLAYER_FULLSCREEN, isFullscreen)
            outState.putBoolean(STATE_PLAYER_PLAYING, isPlayerPlaying)
            super.onSaveInstanceState(outState)
        }

    }
    override fun initActivity() {
        isFromGoBack = false
        watchVideoVM = (getViewModel() as WatchVideoVM)
        isFirstTimePaused = false
        init()
        observer()
        listner()
    }

    override fun onBackPressed() {
        super.onBackPressed()
        youTubePlayerView?.release()
        if (Build.VERSION.SDK_INT < 16) {
            window.clearFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN)
        } else {
            val decorView = window.decorView
            // Show Status Bar.
            val uiOptions = View.SYSTEM_UI_FLAG_VISIBLE
            decorView.systemUiVisibility = uiOptions
        }
        if (::simpleExoPlayer.isInitialized) {
            simpleExoPlayer?.release()
        }

    }



    override fun onDestroy() {

        youTubePlayerView?.release()
        if (Build.VERSION.SDK_INT < 16) {
            window.clearFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN)
        } else {
            val decorView = window.decorView
            // Show Status Bar.
            val uiOptions = View.SYSTEM_UI_FLAG_VISIBLE
            decorView.systemUiVisibility = uiOptions
        }
        if (::simpleExoPlayer.isInitialized) {
            simpleExoPlayer?.release()
        }
        isFirstTimePaused = false
        handler?.removeCallbacksAndMessages(null);
        super.onDestroy()
    }

    fun showCongratulationsDialog(quotient: Int) {
        val dialog = Dialog(this@WatchVideoActivity)
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.setCancelable(false)
        dialog.setContentView(R.layout.dialog_congratulations)
        dialog.getWindow()?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

        var btnThanks = dialog.findViewById<Button>(R.id.btnThanks)
        var txtPoints = dialog.findViewById<TextView>(R.id.txtPoints)
        txtPoints.text = "$quotient Points"

        btnThanks.setOnClickListener {
            dialog.dismiss()
        }
        try {
            dialog.show()
        } catch (e: Exception) {
            dialog.dismiss()
        }

    }

    private fun listner() {
        btnGoBack.setOnClickListener {
            isFromGoBack = true
            watchVideoVM.earnPointsRequestModel.ModuleId =
                ChapterWithAnimation2Activity.mainTopicVideoId.toString()
            watchVideoVM.earnPointsRequestModel.PointType = "TopicVideo"
            watchVideoVM.earnPointsRequestModel.Point = totalWatchCount.toString()
            watchVideoVM.earnPointsRequestModel.SubjectId = Constants.subjectId

            watchVideoVM.earnPointsRequestModel.VideoMinutes = "0"
            watchVideoVM.earnPointsRequestModel.VideoCompleted = null
            watchVideoVM.addPoints()
            Log.e("TotalWatchCount","On Back is ${totalWatchCount.toString()}")


        }
        quality_lo.setOnClickListener {
            val parameters = trackSelector.buildUponParameters()
                .setMaxVideoBitrate(LO_BITRATE)
                .setForceHighestSupportedBitrate(true)
                .build()
            trackSelector.parameters = parameters
        }

        quality_mi.setOnClickListener {
            val parameters = trackSelector.buildUponParameters()
                .setMaxVideoBitrate(MI_BITRATE)
                .setForceHighestSupportedBitrate(true)
                .build()
            trackSelector.parameters = parameters
        }
        quality_hi.setOnClickListener {
            val parameters = trackSelector.buildUponParameters()
                .setMaxVideoBitrate(HI_BITRATE)
                .setForceHighestSupportedBitrate(true)
                .build()
            trackSelector.parameters = parameters
        }

        btnNext.setOnClickListener {
            isFromGoBack = false

            watchVideoVM.earnPointsRequestModel.ModuleId =
                ChapterWithAnimation2Activity.mainTopicVideoId.toString()
            watchVideoVM.earnPointsRequestModel.PointType = "TopicVideo"
            watchVideoVM.earnPointsRequestModel.Point = totalWatchCount.toString()
            watchVideoVM.earnPointsRequestModel.SubjectId = Constants.subjectId

            watchVideoVM.earnPointsRequestModel.VideoMinutes = "0"
            watchVideoVM.earnPointsRequestModel.VideoCompleted = null
            watchVideoVM.addPoints()

            Log.e("TotalWatchCount","On Next is ${totalWatchCount.toString()}")
            //startActivity(QuestionAnswerActivity::class.java)
        }
    }

    private fun observer() {
        watchVideoVM.observedErrorMessageChanges().observe(this, { event ->
            event?.getContentIfNotHandled()?.let {
                showErrorMessage(it, this, mainFrame)
            }
        })
        watchVideoVM.observedQuestionAnswerData().observe(this, { event ->
            event?.getContentIfNotHandled()?.let {
                questionList.clear()

                it.data.Questions?.let { it1 -> questionList.addAll(it1) }

            }
        })
        watchVideoVM.observedtopicVideoDataData().observe(this, { event ->
            event?.getContentIfNotHandled()?.let {
                topicVideosList.clear()
                it.data.Videos?.let { it1 -> topicVideosList.addAll(it1) }

                txtPoints.text = "${it?.data?.Points} Points"
                if (topicVideosList.size > 0) {
                    txtTopicName.text = topicVideosList[0].TopicVideoTitle
                    Executors.newSingleThreadExecutor().submit(Runnable {
                        // You can perform your task here.
                        // Log.e("UUID", "Is ${getDeviceId(this@GetStartedActivity)}")
                        var s3Client = S3Util.getS3Client()
                        var objKey: String? = null
                        val ol = s3Client.listObjects("pentedapp")
//                        for (objectSummary in ol.objectSummaries) {
//                            println(objectSummary.key)
//                            objKey = objectSummary.key
//                            Log.e("Object is", "Here ${objectSummary.key}")
//                        }

                        val request = GeneratePresignedUrlRequest(
                            "pentedapp",
                            "${topicVideosList[0].S3Bucket?.BucketFolderPath}${topicVideosList[0].S3Bucket?.FileName}"
                        )
                        val objectURL: URL = s3Client.generatePresignedUrl(request)
                        Log.e("Final URL is", "Here $objectURL")
                        //var clip = Uri.parse(objectURL.toString().replace(" ", "%20"))
                        val fixedUrl: String = objectURL?.toString()?.replace(" ", "%20") ?: ""
                        Log.e("Final URL new is", "Here $fixedUrl")
//                        STREAM_URLSTRING = fixedUrl
                        // STREAM_URLSTRING = "https://d3qwsj35pay17l.cloudfront.net/output/hls/1.m3u8"
                        STREAM_URLSTRING =
                            "http://demo.unified-streaming.com/video/tears-of-steel/tears-of-steel.ism/.m3u8"
                        //initializePlayer()


                    })
                    Handler(Looper.getMainLooper()).postDelayed({
                        //  initializePlayer(yourObject.S3Bucket.FileName)
                    }, 1000)
                }
            }
        })


        watchVideoVM.observedChanges().observe(this, { event ->
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
                    Constants.POINT_ADDED -> {
                        if(isFromGoBack)
                        {
                            finish()
                        }
                        else{
                            if (questionList.size > 0) {
                                startActivityWithDataKey(QuestionAnswerActivity::class.java, videoId, "VideoId")
                                if (::simpleExoPlayer.isInitialized) {
                                    simpleExoPlayer.release()
                                }
                                finish()
                            } else {
                                if (::simpleExoPlayer.isInitialized) {
                                    simpleExoPlayer.release()
                                }
                                finish()
                                if(Constants.isFromNormalVideoList)
                                {
                                    sendBroadcast(Intent("PlayNextNormalVideo"))
                                }
                                else
                                {
                                    sendBroadcast(Intent("StartNowTopicVideo"))
                                }

                            }
                        }
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
        animationView.visibility = View.VISIBLE
        Utils.getNonWindowTouchable(this)
    }

    public fun hideDialog() {
        lilProgressBar.visibility = View.GONE
        animationView.visibility = View.GONE
        Utils.getWindowTouchable(this)
    }

    private val runnableOneMinute: Runnable = object : Runnable {
        override fun run() {
            Log.e("OneMinute", "done")
            handlerOneMinuteMain?.postDelayed(this, 5000) // reschedule the handler
        }
    }

    private val runnable: Runnable = object : Runnable {
        override fun run() {
            if (::simpleExoPlayer.isInitialized) {
                videoWatchedTime = simpleExoPlayer.getCurrentPosition() / 1000
                val minutes: Long = simpleExoPlayer.getCurrentPosition() / 1000 / 60
                val seconds = (simpleExoPlayer.getCurrentPosition() / 1000 % 60)
                var totalSeconds = (simpleExoPlayer.getCurrentPosition() / 1000)

                val dividend = totalSeconds?.toInt()
                val divisor = 60

                val quotient = dividend?.div(divisor)
                val remainder = dividend?.rem(divisor)

//                println("Quotient = $quotient")
//                println("Remainder = $remainder")
//                Log.e(
//                    "Watched",
//                    "totalSeconds ${totalSeconds} quotient ${quotient} remainder${remainder}"
//                )
                if (remainder == 0 && (totalSeconds.toInt() != 0)) {
                    watchVideoVM.earnPointsRequestModel.ModuleId =
                        ChapterWithAnimation2Activity.mainTopicVideoId.toString()
                    watchVideoVM.earnPointsRequestModel.PointType = "TopicVideo"
                    watchVideoVM.earnPointsRequestModel.Point = Constants.POINT_PLUS_VIDEO
                    watchVideoVM.earnPointsRequestModel.SubjectId = Constants.subjectId

                    watchVideoVM.earnPointsRequestModel.VideoMinutes = quotient?.toString()
                    watchVideoVM.earnPointsRequestModel.VideoCompleted = false
                    //watchVideoVM.addPoints()
                    ChapterWithAnimation2Activity.topicDataList[ChapterWithAnimation2Activity.current].VideoPlayDuration = quotient.toString()

                    quotient?.toString()?.let {
                        watchVideoVM.addDuration(
                            ChapterWithAnimation2Activity.mainTopicVideoId.toString(), it
                        )
                    }
                }
                handler?.postDelayed(this, 1000) // reschedule the handler
            }

        }
    }


    private val runnableyouTube: Runnable = object : Runnable {
        override fun run() {
            if (youTubePlayerView != null) {
                var totalSeconds = tracker.currentSecond

                val dividend = totalSeconds?.toInt()
                val divisor = 60


                val quotient = dividend?.div(divisor)
                val remainder = dividend?.rem(divisor)
                Log.e("You Tube", "Time quotient== $quotient Reminder == ${remainder}")

                if (remainder == 0 && (totalSeconds.toInt() != 0)) {
                    watchVideoVM.earnPointsRequestModel.ModuleId =
                        ChapterWithAnimation2Activity.mainTopicVideoId.toString()
                    watchVideoVM.earnPointsRequestModel.PointType = "TopicVideo"
                    watchVideoVM.earnPointsRequestModel.SubjectId = Constants.subjectId

                    watchVideoVM.earnPointsRequestModel.Point = Constants.POINT_PLUS_VIDEO
                    watchVideoVM.earnPointsRequestModel.VideoMinutes = quotient?.toString()
                    watchVideoVM.earnPointsRequestModel.VideoCompleted = false
                  //  watchVideoVM.addPoints()

                    quotient?.toString()?.let {
                        watchVideoVM.addDuration(ChapterWithAnimation2Activity.mainTopicVideoId.toString(),
                            it
                        )
                    }


                }
                handler?.postDelayed(this, 1000) // reschedule the handler
            }

        }
    }

    fun oneMinuteTimer()
    {

        customeCountDownTimer =   object : CustomCountDownTimer(61000, 1000) {
            override fun onTick(millisUntilFinished: Long) {
                Log.e("One", "Second $${millisUntilFinished / 1000}")

            }

            override fun onFinish() {
                Log.e("Finished", "Yes")
//                watchVideoVM.earnPointsRequestModel.ModuleId =
//                    ChapterWithAnimation2Activity.mainTopicVideoId.toString()
//                watchVideoVM.earnPointsRequestModel.PointType = "TopicVideo"
//                watchVideoVM.earnPointsRequestModel.Point = Constants.POINT_PLUS_VIDEO
//                watchVideoVM.earnPointsRequestModel.SubjectId = Constants.subjectId
//
//                watchVideoVM.earnPointsRequestModel.VideoMinutes = "0"
//                watchVideoVM.earnPointsRequestModel.VideoCompleted = null
//                watchVideoVM.addPoints()
                totalWatchCount++
                customeCountDownTimer?.start()
                //oneMinuteTimer()
            }
        }.start()

    }

    private fun init() {
        totalWatchCount = 0
        isVideoEnded=false
        oneMinuteTimer()
        if(ChapterWithAnimation2Activity.isLastVideo)
        {
            btnNext.text  = "Exit"
        }
        else{
            btnNext.text = "Next"
        }
        if (intent.hasExtra("topicVideo")) {

            val gson = Gson()
            val yourObject = gson.fromJson<TopicVideoResponseModel.Video>(
                intent.getStringExtra("topicVideo"),
                TopicVideoResponseModel.Video::class.java
            )
            txtPoints.text = "${yourObject.Points?.toInt()} Points"
            yourObject.TopicVideoId.let {
                if (it != null) {
                    videoId = it
                }
            }
            videoId?.toString()?.let { watchVideoVM.getQuestionAnswer(it) }
            txtTopicName.text = yourObject.TopicVideoTitle
          //  Constants.videoNameForSolution = yourObject.TopicVideoTitle
            Log.e("CompletedPercentage", "Inside Watch Video" + yourObject?.Points)
            yourObject.CompletedPercentage?.toFloat().let {
                if (it != null) {
                    progressView1.progress = it
                }
            }
            Executors.newSingleThreadExecutor().submit(Runnable {
                // You can perform your task here.
                // Log.e("UUID", "Is ${getDeviceId(this@GetStartedActivity)}")
                var s3Client = S3Util.getS3Client()
                var objKey: String? = null
                val ol = s3Client.listObjects("pentedapp")
//                        for (objectSummary in ol.objectSummaries) {
//                            println(objectSummary.key)
//                            objKey = objectSummary.key
//                            Log.e("Object is", "Here ${objectSummary.key}")
//                        }

                val request = GeneratePresignedUrlRequest(
                    "pentedapp",
                    "${yourObject.S3Bucket?.BucketFolderPath}${yourObject.S3Bucket?.FileName}"
                )

                val objectURL: URL = s3Client.generatePresignedUrl(request)
                Log.e("Final URL is", "Here $objectURL")
                //var clip = Uri.parse(objectURL.toString().replace(" ", "%20"))
                val fixedUrl: String = objectURL?.toString()?.replace(" ", "%20") ?: ""
                Log.e("Final URL new is", "Here $fixedUrl")
//                STREAM_URLSTRING = fixedUrl
                STREAM_URLSTRING =
                    "https://multiplatform-f.akamaihd.net/i/multi/will/bunny/big_buck_bunny_,640x360_400,640x360_700,640x360_1000,950x540_1500,.f4v.csmil/master.m3u8"

//                STREAM_URLSTRING = "https://d3qwsj35pay17l.cloudfront.net/output/hls/1.m3u8"
                //initializePlayer()

            })
            if (savedInstanceState != null) {
                currentWindow = savedInstanceState?.getInt(STATE_RESUME_WINDOW) ?: 0
                //playbackPosition = savedInstanceState?.getLong(STATE_RESUME_POSITION)?: 0
                isFullscreen = savedInstanceState?.getBoolean(STATE_PLAYER_FULLSCREEN) ?: false
                isPlayerPlaying = savedInstanceState?.getBoolean(STATE_PLAYER_PLAYING)?: false
            }
            if (yourObject.S3Bucket != null && (yourObject.S3Bucket.FileName != null)) {
                youTubePlayerView.visibility = View.GONE
                playerView.visibility = View.VISIBLE
                Handler(Looper.getMainLooper()).postDelayed({
                    if (::simpleExoPlayer.isInitialized) {

                    } else {
                        initializePlayer(
                            yourObject.S3Bucket.FileName,
                            yourObject.VideoPlayDuration?.toInt(),
                            yourObject.VideoPlayCompleted
                        )
                    }
                }, 1000)
            } else if (!yourObject.Youtubelink.isNullOrBlank()) {
                var youTubeId: String? = null
                var listStrings = yourObject.Youtubelink?.split("=")
                listStrings?.get(listStrings.size - 1)?.let {
                    youTubeId = it
                }
                var currentSecond = 0f
                youTubePlayerView.visibility = View.VISIBLE
                playerView.visibility = View.GONE
                Constants.ifFullScreen = false

//                if(intent.hasExtra("VideoLink"))
//                {
//                    youTubeId = intent.getStringExtra("VideoLink")
//                }

                Log.e("Full screen", " ${Constants.ifFullScreen}")

                handler = Handler() // new handler


                handler?.postDelayed(runnableyouTube, 2000)

                if (Build.VERSION.SDK_INT < 16) {
                    window.setFlags(
                        WindowManager.LayoutParams.FLAG_FULLSCREEN,
                        WindowManager.LayoutParams.FLAG_FULLSCREEN
                    )
                } else {
                    val decorView: View = window.decorView
                    // Hide Status Bar.
                    val uiOptions: Int = View.SYSTEM_UI_FLAG_FULLSCREEN
                    decorView.setSystemUiVisibility(uiOptions)
                }
                //Todo
//                getLifecycle().addObserver(youTubePlayerView)

                var isAPICalled = 0

                youTubePlayerView.addYouTubePlayerListener(object :
                    AbstractYouTubePlayerListener() {
                    override fun onReady(@NonNull youTubePlayer: YouTubePlayer) {
                        val videoId = youTubeId
                        youTubePlayer.addListener(tracker)
                        videoId?.let { youTubePlayer.loadVideo(it, 0f) }
                    }

                    override fun onStateChange(
                        youTubePlayer: YouTubePlayer,
                        state: PlayerConstants.PlayerState
                    ) {
                        super.onStateChange(youTubePlayer, state)
                        onNewState(state);
                    }

                    override fun onCurrentSecond(youTubePlayer: YouTubePlayer, second: Float) {
                        super.onCurrentSecond(youTubePlayer, second)


                    }
                })


                youTubePlayerView.getPlayerUiController()
                    .setFullScreenButtonClickListener(View.OnClickListener {
                        Log.e("Full screen", "Clicked ${Constants.ifFullScreen}")
                        if (Constants.ifFullScreen) {
                            bottomLayout.visibility = View.VISIBLE
                            Constants.ifFullScreen = false
                            requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
                        } else {
                            bottomLayout.visibility = View.GONE
                            Constants.ifFullScreen = true
                            requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE
                        }
                        // youTubePlayerView.toggleFullScreen()
                    })
            } else {
                Toast.makeText(this@WatchVideoActivity, "No Video Found", Toast.LENGTH_SHORT).show()
            }


//            var topicVideoId = intent.getIntExtra("topicVideoId",0)
//            Log.e("topicVideoId",topicVideoId.toString())
//            watchVideoVM.getTopicVideo(topicVideoId.toString())
        }

        handler = Handler() // new handler


        handler?.postDelayed(runnable, 2000)

//        handlerOneMinuteMain = CustomHandler()
//
//        handlerOneMinute = MyHandler()
//
//        handlerOneMinute?.postDelayed(runnableOneMinute, 2000)

        fullscreenButton = playerView.findViewById(R.id.exo_fullscreen_icon)
        fullscreenButton?.setOnClickListener(View.OnClickListener {
            if (fullscreen) {
                fullscreenButton?.setImageDrawable(
                    ContextCompat.getDrawable(
                        this@WatchVideoActivity,
                        R.drawable.ic_fullscreen_open
                    )
                )
                bottomLayout.visibility = View.VISIBLE
                window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_VISIBLE
                if (supportActionBar != null) {
                    supportActionBar!!.show()
                }
                requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
                val params = playerView.layoutParams as RelativeLayout.LayoutParams
                params.width = ViewGroup.LayoutParams.MATCH_PARENT
                // params.height = (200 * applicationContext.resources.displayMetrics.density).toInt()
                params.height = ViewGroup.LayoutParams.MATCH_PARENT
                playerView.layoutParams = params
                fullscreen = false
            } else {
                fullscreenButton?.setImageDrawable(
                    ContextCompat.getDrawable(
                        this@WatchVideoActivity,
                        R.drawable.ic_fullscreen_close
                    )
                )
                bottomLayout.visibility = View.GONE
                window.decorView.systemUiVisibility = (View.SYSTEM_UI_FLAG_FULLSCREEN
                        or View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
                        or View.SYSTEM_UI_FLAG_HIDE_NAVIGATION)
                if (supportActionBar != null) {
                    supportActionBar!!.hide()
                }
                requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE
                val params = playerView.layoutParams as RelativeLayout.LayoutParams
                params.width = ViewGroup.LayoutParams.MATCH_PARENT
                params.height = ViewGroup.LayoutParams.MATCH_PARENT
                playerView.layoutParams = params
                fullscreen = true
            }
        })
    }

    private fun onNewState(newState: PlayerConstants.PlayerState) {

        val playerState: String = playerStateToString(newState)
        Log.e("playerState", "===" + playerState)
        if (playerState == "ENDED") {
            var totalSeconds = tracker.videoDuration

            val dividend = totalSeconds?.toInt()
            val divisor = 60

            val quotient = dividend?.div(divisor)
            val remainder = dividend?.rem(divisor)
            watchVideoVM.earnPointsRequestModel.ModuleId =
                ChapterWithAnimation2Activity.mainTopicVideoId.toString()
            watchVideoVM.earnPointsRequestModel.PointType = "TopicVideo"
            watchVideoVM.earnPointsRequestModel.Point = Constants.POINT_PLUS_VIDEO
            watchVideoVM.earnPointsRequestModel.SubjectId = Constants.subjectId

            watchVideoVM.earnPointsRequestModel.VideoMinutes = quotient?.toString()
            watchVideoVM.earnPointsRequestModel.VideoCompleted = true
            watchVideoVM.earnPointsRequestModel.IsCompleted = true
            watchVideoVM.addPoints()
            totalWatchCount++
            quotient?.toString()?.let {
                watchVideoVM.addDuration(
                    ChapterWithAnimation2Activity.mainTopicVideoId.toString(),
                    it,
                    true
                )
            }
            customeCountDownTimer?.cancel()
            showCongratulationsDialog(totalWatchCount)
        }
        else if(playerState == "PLAYING" && isFirstTimePaused)
        {
            customeCountDownTimer?.resume()
        }

        else if(playerState == "PAUSED")
        {
            isFirstTimePaused = true
            customeCountDownTimer?.pause()
        }
        else if(playerState == "BUFFERING")
        {
            isFirstTimePaused = true
            customeCountDownTimer?.pause()
        }
        Log.e("playerState", "Is ===${playerState}")
        addToList(playerState, playerStatesHistory as MutableList<Pair<Date, String>>)
    }

    private fun addToList(playerState: String, stateHistory: MutableList<Pair<Date, String>>) {
        if (stateHistory.size >= 15) stateHistory.removeAt(0)
        stateHistory.add(Pair(Date(), playerState))
    }

    private fun buildMediaSource(uri: Uri): MediaSource? {
        val dataSourceFactory: DataSource.Factory =
            DefaultDataSourceFactory(this, "exoplayer-codelab")
        return HlsMediaSource.Factory(dataSourceFactory).createMediaSource(uri)
    }

    private fun initializePlayer(
        fileName: String,
        watchedDuration: Int? = 0,
        isComplated: Boolean = false
    ) {
        var filenameFinal = fileName.split(".")
        var HLS_STATIC_URL = "$VIDEO_BASE_URL${filenameFinal[0]}.m3u8"

         val mediaItem = MediaItem.Builder()
            .setUri(HLS_STATIC_URL)
            .setMimeType(MimeTypes.APPLICATION_M3U8)
            .build()
        exoQuality = playerView.findViewById(R.id.exo_quality)


        exoQuality.setOnClickListener{
            if(trackDialog == null){
                initPopupQuality()
            }
            trackDialog?.show()
        }

        trackSelector = DefaultTrackSelector(this)
        trackSelector.setParameters(
            trackSelector.buildUponParameters().setMaxVideoSize(
                MAX_WIDTH,
                MAX_HEIGHT
            )
        )

//        val mappedTrackInfo: MappingTrackSelector.MappedTrackInfo? =
//            trackSelector!!.currentMappedTrackInfo
//
//        if (mappedTrackInfo != null) {
//
//            MappingTrackSelector.MappedTrackInfo.RENDERER_SUPPORT_NO_TRACKS
//            var dialog = CustomTrackSelectionDialogBuilder(
//                this,
//                "Select Video Resolution",
//                trackSelector,
//                0
//            );
//            dialog.setShowDisableOption(false);
//            dialog.setAllowAdaptiveSelections(false);
//            dialog.build().show();
//        }
//
//
//
//
//        val defaultTrackParam = trackSelector.buildUponParameters().build()
//        trackSelector.parameters = defaultTrackParam

       // val player: SimpleExoPlayer = ExoPlayerFactory.newSimpleInstance(this, trackSelector)

//       var player = ExoPlayerFactory.newSimpleInstance(
//            DefaultRenderersFactory(this),
//            DefaultTrackSelector(trackSelectionFactory),
//            DefaultLoadControl()
//        )
     //   playerView.setPlayer(player)
//        simpleExoPlayer = SimpleExoPlayer.Builder(this)
//            .setMediaSourceFactory(mediaSourceFactory)
//            .build()
//        trackSelector.setParameters(
//            trackSelector
//                .buildUponParameters()
//                .setAllowVideoMixedMimeTypeAdaptiveness(true)
//        )
//        val parameters = trackSelector.buildUponParameters()
//            .setMaxVideoBitrate(LO_BITRATE)
//            .setForceHighestSupportedBitrate(true)
//            .build()
//        trackSelector.parameters = parameters
        Log.e("watchedDuration", "== ${playbackPosition}")
        simpleExoPlayer = SimpleExoPlayer.Builder(this)
            .setTrackSelector(trackSelector)
            .build()
        simpleExoPlayer.setMediaItem(mediaItem)
        simpleExoPlayer.prepare()
        playerView.player = simpleExoPlayer
        simpleExoPlayer.playWhenReady = isPlayerPlaying
        Log.e("isComplated", "==$isComplated")
        var seekToPos = (watchedDuration ?: 0)*60*1000
        if(!isComplated)
        {
            simpleExoPlayer.seekTo(currentWindow, seekToPos.toLong())
        }

//        Looper.myLooper()?.let {
//            Handler(it).postDelayed({
//
////                watchedDuration?.times(1000)?.let { }
//                Log.e("watchedDuration","== ${watchedDuration}")
//            }, 5000)
//        }
        //Working code
     //   mediaSource?.let { simpleExoPlayer.addMediaSource(it) }


        // This is the body of the logic for see if there are even video tracks
// It also does some field setting
        // This is the body of the logic for see if there are even video tracks
// It also does some field setting

        simpleExoPlayer.addListener(object : Player.Listener {
            //            override fun onPlayerStateChanged(playWhenReady: Boolean, playbackState: Int) {
//                if(playbackState == Player.STATE_READY){
//                    exoQuality.visibility = View.VISIBLE
//                }
//
//            }
            override fun onTracksChanged(
                trackGroups: TrackGroupArray,
                trackSelections: TrackSelectionArray
            ) {
                Log.e("TAG", "onTracksChanged: ")
            }

            override fun onLoadingChanged(isLoading: Boolean) {}
            override fun onIsPlayingChanged(isPlaying: Boolean) {
                super.onIsPlayingChanged(isPlaying)
//                if(isPlaying && isFirstTimePaused)
//                {
//                    Log.e("Timer","Resumed")
//                }
//                else{
//                    isFirstTimePaused = true
//                    Log.e("Timer","Stopped")
//                }
            }

            override fun onPlayerStateChanged(playWhenReady: Boolean, playbackState: Int) {
                Log.e("Playback", "States are ${playbackState}")

                if (playWhenReady && playbackState == Player.STATE_READY && isFirstTimePaused) {
                    // media actually playing
                    customeCountDownTimer?.resume()
                    Log.e("Timer", "Resumed")
                } else if (playWhenReady) {
                    //handlerOneMinuteMain?.pause()
                    // might be idle (plays after prepare()),
                    // buffering (plays when data available)
                    // or ended (plays when seek away from end)
                } else {
                    // player paused in any state
                    isFirstTimePaused = true
                    customeCountDownTimer?.pause()
                    Log.e("Timer", "Stopped")
                }
                if (playbackState == ExoPlayer.STATE_BUFFERING) {
                    progressBarPlayer.visibility = View.VISIBLE
                }
                if (playbackState == ExoPlayer.STATE_READY) {
                    progressBarPlayer.visibility = View.GONE

//                    val mainThreadHandler = Handler(Looper.getMainLooper())
//                    mainThreadHandler.post {
//                        Timer().scheduleAtFixedRate(object : TimerTask() {
//                            override fun run() {
//                                val minutes: Long = simpleExoPlayer.getCurrentPosition() / 1000 / 60
//                                val seconds = (simpleExoPlayer.getCurrentPosition() / 1000 % 60)
//
//                                videoWatchedTime = simpleExoPlayer.getCurrentPosition() / 1000
//                                Log.e("Watched", "timing is ${minutes}min ${seconds}sec")
//                            }
//                        }, 0, 1000)
//                    }

//                    Handler().postDelayed({ //Do your work
//
//                    }, 1000)
                }
                if (playbackState == ExoPlayer.STATE_ENDED) {
                    if (::simpleExoPlayer.isInitialized) {
                        if (!isVideoEnded) {
                            var totalSeconds = (simpleExoPlayer.duration / 1000)

                            val dividend = totalSeconds?.toInt()
                            val divisor = 60

                            val quotient = dividend?.div(divisor)
                            val remainder = dividend?.rem(divisor)
                            watchVideoVM.earnPointsRequestModel.ModuleId =
                                ChapterWithAnimation2Activity.mainTopicVideoId.toString()
                            watchVideoVM.earnPointsRequestModel.PointType = "TopicVideo"
                            watchVideoVM.earnPointsRequestModel.SubjectId = Constants.subjectId
                            watchVideoVM.earnPointsRequestModel.Point = Constants.POINT_PLUS_VIDEO
                            watchVideoVM.earnPointsRequestModel.VideoMinutes = quotient?.toString()
                            watchVideoVM.earnPointsRequestModel.VideoCompleted = true
                            watchVideoVM.earnPointsRequestModel.IsCompleted = true
                            watchVideoVM.addPoints()
                            totalWatchCount++
                            quotient?.toString()?.let {
                                watchVideoVM.addDuration(
                                    ChapterWithAnimation2Activity.mainTopicVideoId.toString(),
                                    it,
                                    true
                                )
                            }
                            customeCountDownTimer?.cancel()
                            showCongratulationsDialog(totalWatchCount)
                            isVideoEnded = true
                        }

                    }

                }
                if (playbackState == ExoPlayer.EVENT_PLAYBACK_STATE_CHANGED) {
                    Log.e("PLAYBACK", "Changed")
                }

            }

            fun onPlayerError(error: ExoPlaybackException?) {

            }

            fun onPositionDiscontinuity() {
                Log.e("TAG", "onPositionDiscontinuity: ")
            }

            override fun onPlaybackParametersChanged(playbackParameters: PlaybackParameters) {}
        })
//        simpleExoPlayer.addListener(object : Player.EventListener {
//            fun onTimelineChanged(timeline: Timeline?, manifest: Any?) {}
//
//        })
        if (::simpleExoPlayer.isInitialized) {
           // simpleExoPlayer.playWhenReady = true
//            Handler().postDelayed({
//                watchedDuration?.times(1000)?.let { simpleExoPlayer?.seekTo(0, it) }
//                Log.e("watchedDuration","== ${watchedDuration}")
//            }, 10000)

//            simpleExoPlayer.prepare()
//            Handler(Looper.getMainLooper()).postDelayed({ //Do your work
//                videoWatchedTime = simpleExoPlayer.getCurrentPosition() / 1000
//                Log.e("Watched", "timing is videoWatchedTime")
//            }, 1000)
//            simpleExoPlayer.setRepeatMode(Player.REPEAT_MODE_OFF);
//            playerView.setShutterBackgroundColor(Color.TRANSPARENT)
//            playerView.player = simpleExoPlayer
//            playerView.requestFocus()


            Log.e("Current", " is == ${Gson().toJson(simpleExoPlayer.currentTrackGroups.length)}")
            Log.e("Current", " is == ${Gson().toJson(simpleExoPlayer.currentTrackSelections)}")

        }

        if (::simpleExoPlayer.isInitialized) {
//            val mappedTrackInfo = trackSelector?.currentMappedTrackInfo
//            for (i in 0 until mappedTrackInfo?.rendererCount!!) {
//                val trackGroups = mappedTrackInfo?.getTrackGroups(i)
//                if (trackGroups.length != 0) {
//                    when (simpleExoPlayer!!.getRendererType(i)) {
//                        C.TRACK_TYPE_VIDEO -> {
//                            videoRendererIndex = i
//                            // return true
//                        }
//                    }
//                }
//            }


//            simpleExoPlayer!!.addListener(object : Player.Listener {
//                fun onTracksInfoChanged(tracksInfo: TracksInfo?) {
//                    // Update UI using current TracksInfo.
//                }
//            })
        }
    }


    // QUALITY SELECTOR

    private fun initPopupQuality() {
        val mappedTrackInfo = trackSelector.currentMappedTrackInfo
        var videoRenderer : Int? = null

        if(mappedTrackInfo == null) return else exoQuality.visibility = View.VISIBLE

        for(i in 0 until mappedTrackInfo.rendererCount){
            if(isVideoRenderer(mappedTrackInfo, i)){
                videoRenderer = i
            }
        }

        if(videoRenderer == null){
            exoQuality.visibility = View.GONE
            return
        }

        val trackSelectionDialogBuilder = CustomTrackSelectionDialogBuilder(
            this,
            getString(R.string.qualitySelector),
            trackSelector,
            videoRenderer
        )
        trackSelectionDialogBuilder.setTrackNameProvider{
            // Override function getTrackName
            getString(R.string.exo_track_resolution_pixel, it.height)
        }
        trackDialog = trackSelectionDialogBuilder.build()
    }

    private fun isVideoRenderer(
        mappedTrackInfo: MappingTrackSelector.MappedTrackInfo,
        rendererIndex: Int
    ): Boolean {
        val trackGroupArray = mappedTrackInfo.getTrackGroups(rendererIndex)
        if (trackGroupArray.length == 0) {
            return false
        }
        val trackType = mappedTrackInfo.getRendererType(rendererIndex)
        return C.TRACK_TYPE_VIDEO == trackType
    }

    private fun playerStateToString(state: PlayerState): String {
        return when (state) {
            PlayerState.UNKNOWN -> "UNKNOWN"
            PlayerState.UNSTARTED -> "UNSTARTED"
            PlayerState.ENDED -> "ENDED"
            PlayerState.PLAYING -> "PLAYING"
            PlayerState.PAUSED -> "PAUSED"
            PlayerState.BUFFERING -> "BUFFERING"
            PlayerState.VIDEO_CUED -> "VIDEO_CUED"
            else -> "status unknown"
        }
    }

    private fun releasePlayer() {
        simpleExoPlayer.release()
    }

    public override fun onStart() {
        super.onStart()
//        LocaleManager.setNewLocale(
//            this@WatchVideoActivity,
//            LocaleManager.ENGLISH
//        );
        // if (Util.SDK_INT > 23) initializePlayer()

    }


    public override fun onResume() {
        super.onResume()
       // customeCountDownTimer?.resume()
        // initializePlayer()
        //  if (Util.SDK_INT <= 23) initializePlayer()

    }

    public override fun onPause() {
        super.onPause()
        watchVideoVM.earnPointsRequestModel.ModuleId = ""
        customeCountDownTimer?.pause()

        if (::simpleExoPlayer.isInitialized) {
            simpleExoPlayer?.setPlayWhenReady(false)
        }

        // if (Util.SDK_INT <= 23) releasePlayer()

    }

    public override fun onStop() {
        super.onStop()
     //   youTubePlayerView?.release()
        if (Build.VERSION.SDK_INT < 16) {
            window.clearFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN)
        } else {
            val decorView = window.decorView
            // Show Status Bar.
            val uiOptions = View.SYSTEM_UI_FLAG_VISIBLE
            decorView.systemUiVisibility = uiOptions
        }
        if (::simpleExoPlayer.isInitialized) {
            simpleExoPlayer?.setPlayWhenReady(false)
       //     simpleExoPlayer?.release()
        }

//        if (Constants.headerlanguageid?.toInt() == 1) {
//            LocaleManager.setNewLocale(
//                this@WatchVideoActivity,
//                LocaleManager.GUJARATI
//            );
//            // setNewLocale(requireActivity(), LocaleManager.GUJARATI)
//        } else if (Constants.headerlanguageid?.toInt() == 2) {
//            LocaleManager.setNewLocale(
//                this@WatchVideoActivity,
//                LocaleManager.HINDI
//            );
//            // setNewLocale(requireActivity(), LocaleManager.HINDI)
//        } else if (Constants.headerlanguageid?.toInt() == 3) {
//            LocaleManager.setNewLocale(
//                this@WatchVideoActivity,
//                LocaleManager.ENGLISH
//            );
//            //  setNewLocale(requireActivity(), LocaleManager.ENGLISH)
//        }
        //  if (Util.SDK_INT > 23) releasePlayer()
    }

    companion object {
        var STREAM_URL: Uri? = null
        var STREAM_URLSTRING: String = ""
    }
}