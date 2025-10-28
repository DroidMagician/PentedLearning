package com.pented.learningapp.homeScreen.practice.activity

import android.app.Dialog
import android.content.Intent
import android.content.pm.ActivityInfo
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.net.Uri
import android.os.Build
import android.os.Handler
import android.os.Looper
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
import com.google.android.exoplayer2.source.*
import com.google.android.exoplayer2.source.hls.HlsMediaSource
import com.google.android.exoplayer2.trackselection.DefaultTrackSelector
import com.google.android.exoplayer2.trackselection.MappingTrackSelector
import com.google.android.exoplayer2.trackselection.TrackSelectionArray
import com.google.android.exoplayer2.upstream.DataSource
import com.google.android.exoplayer2.upstream.DefaultBandwidthMeter
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory
import com.google.android.exoplayer2.util.MimeTypes
import com.google.android.exoplayer2.util.Util
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.pented.learningapp.R
import com.pented.learningapp.amazonS3.S3Util
import com.pented.learningapp.base.BaseActivity
import com.pented.learningapp.base.BaseViewModel
import com.pented.learningapp.databinding.ActivityWatchSolutionVideoBinding
import com.pented.learningapp.helper.Constants
import com.pented.learningapp.helper.CustomCountDownTimer
import com.pented.learningapp.helper.CustomTrackSelectionDialogBuilder
import com.pented.learningapp.helper.Utils
import com.pented.learningapp.helper.exoplayer.ClassAdaptiveTrackSelection
import com.pented.learningapp.homeScreen.home.model.TopicVideoResponseModel
import com.pented.learningapp.homeScreen.home.watchVideo.totalWatchCount
import com.pented.learningapp.homeScreen.practice.model.GetQuestionPaperBySubjectResponseModel
import com.pented.learningapp.homeScreen.scanQR.viewModel.WatchScannedVideoVM
import com.pented.learningapp.retrofit.API.Companion.VIDEO_BASE_URL
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.PlayerConstants
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.YouTubePlayer
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.listeners.AbstractYouTubePlayerListener
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.utils.YouTubePlayerTracker
import kotlinx.android.synthetic.main.activity_watch_solution_video.*
import kotlinx.android.synthetic.main.transparent_progressbar_layout.*
import java.net.URL
import java.util.*
import java.util.concurrent.Executors
import kotlin.collections.ArrayList


class SolutionVideoActivity : BaseActivity<ActivityWatchSolutionVideoBinding>() {
    private lateinit var simpleExoPlayer: SimpleExoPlayer
    private lateinit var mediaDataSourceFactory: DataSource.Factory
    var fullscreenButton: ImageView? = null
    var fullscreen = false
    var videoWatchedTime: Long = 0
    private var handler: Handler? = null
    var isVideoEnded=false
    var isFromExit=false

    var topicVideosList = ArrayList<TopicVideoResponseModel.Video>()
    val tracker: YouTubePlayerTracker = YouTubePlayerTracker()
    var solutionVideoId = 0
    var congratulationPopupCount = 0
    private lateinit var trackSelector: DefaultTrackSelector
    private lateinit var exoQuality: ImageButton
    private var currentWindow = 0
    private var trackDialog: Dialog? = null
    private val BANDWIDTH_METER = DefaultBandwidthMeter()
    var videoRendererIndex = 0
    var trackGroups: TrackGroupArray? = null
    val HI_BITRATE = 2097152
    val MI_BITRATE = 1048576

    //        val LO_BITRATE = 524288
    val LO_BITRATE = 32768
    val MAX_HEIGHT = 539
    val MAX_WIDTH = 959

    var isFirstTimePaused = false
    private var customeCountDownTimer: CustomCountDownTimer? = null


    override fun layoutID() = R.layout.activity_watch_solution_video
    override fun viewModel(): BaseViewModel =
        ViewModelProvider(this).get(WatchScannedVideoVM::class.java)

    lateinit var watchVideoVM: WatchScannedVideoVM
    override fun initActivity() {
        watchVideoVM = (getViewModel() as WatchScannedVideoVM)
        isFirstTimePaused = false
        congratulationPopupCount = 0
        init()
        observer()
        listner()
    }

    override fun onDestroy() {
        if (::simpleExoPlayer.isInitialized) {
            simpleExoPlayer?.release()
        }
        isFirstTimePaused = false
        handler?.removeCallbacksAndMessages(null);
        super.onDestroy()
    }

    private fun listner() {
        btnExit.setOnClickListener {

            watchVideoVM.earnPointsRequestModel.ModuleId = Constants.questionPaperId //Question paper ID
            watchVideoVM.earnPointsRequestModel.PointType = "QuestionPaper"
            watchVideoVM.earnPointsRequestModel.Point = totalWatchCount.toString()
            watchVideoVM.earnPointsRequestModel.SubjectId = Constants.subjectIdFromQuestion

            watchVideoVM.earnPointsRequestModel.VideoMinutes = "0"
            watchVideoVM.earnPointsRequestModel.VideoCompleted = null
            watchVideoVM.addPoints()
            isFromExit = true

        }
        btnContinue.setOnClickListener {

            watchVideoVM.earnPointsRequestModel.ModuleId = Constants.questionPaperId //Question paper ID
            watchVideoVM.earnPointsRequestModel.PointType = "QuestionPaper"
            watchVideoVM.earnPointsRequestModel.Point = totalWatchCount.toString()
            watchVideoVM.earnPointsRequestModel.SubjectId = Constants.subjectIdFromQuestion

            watchVideoVM.earnPointsRequestModel.VideoMinutes = "0"
            watchVideoVM.earnPointsRequestModel.VideoCompleted = null
            watchVideoVM.addPoints()
            isFromExit = false
        }
    }

    private fun observer() {
        watchVideoVM.observedErrorMessageChanges().observe(this, { event ->
            event?.getContentIfNotHandled()?.let {
                showErrorMessage(it, this, mainFrame)
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
                        STREAM_URLSTRING = fixedUrl
                        //initializePlayer()


                    })
                    Handler(Looper.getMainLooper()).postDelayed({
                        initializePlayer(topicVideosList[0].S3Bucket?.FileName ?: "")
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
                        if (isFromExit) {
                            sendBroadcast(Intent(Constants.BACKPRESSED))
                            finish()
                        }
                        else{
                            finish()
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

    private val runnable: Runnable = object : Runnable {
        override fun run() {
            if (::simpleExoPlayer.isInitialized) {
                videoWatchedTime = simpleExoPlayer.getCurrentPosition() / 1000
                val minutes: Long = simpleExoPlayer.getCurrentPosition() / 1000 / 60
                val seconds = (simpleExoPlayer.getCurrentPosition() / 1000 % 60)
                Log.e("Watched", "timing is $minutes:$seconds")
                handler?.postDelayed(this, 1000) // reschedule the handler
            }

        }
    }

    private fun init() {
        isVideoEnded=false
        oneMinuteTimer()
        if (intent.hasExtra(Constants.EXTRA)) {

            val gson = Gson()
            val yourObject = gson.fromJson<GetQuestionPaperBySubjectResponseModel.SolutionVideo>(
                intent.getStringExtra(Constants.EXTRA),
                GetQuestionPaperBySubjectResponseModel.SolutionVideo::class.java
            )
            txtPoints.text = "${Constants.QuestionPaperVideoPoints} Points"
            Log.e("yourObject", "Is Here ${Gson().toJson(yourObject)}")
            txtTopicName.text = yourObject.Title
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
                    "${yourObject.SolutionVideoS3Bucket?.BucketFolderPath}${yourObject.SolutionVideoS3Bucket?.FileName}"
                )
                val objectURL: URL = s3Client.generatePresignedUrl(request)
                Log.e("Final URL is", "Here $objectURL")
                //var clip = Uri.parse(objectURL.toString().replace(" ", "%20"))
                val fixedUrl: String = objectURL?.toString()?.replace(" ", "%20") ?: ""
                Log.e("Final URL new is", "Here $fixedUrl")
                STREAM_URLSTRING = fixedUrl
                //initializePlayer()
            })
            //impVideoId = yourObject.IMPQuestionId ?: 0

            if (yourObject.SolutionVideoS3Bucket != null && (yourObject.SolutionVideoS3Bucket.FileName != null)) {
                youTubePlayerView.visibility = View.GONE
                playerView.visibility = View.VISIBLE
                Handler(Looper.getMainLooper()).postDelayed({
                    initializePlayer(yourObject.SolutionVideoS3Bucket.FileName ?: "")
                }, 1000)
            } else if (!yourObject.Youtubelink.isNullOrBlank()) {
                var youTubeId: String? = null
                val gson = GsonBuilder().disableHtmlEscaping().create()
                var finalYoutubeLink =  gson.toJson(yourObject.Youtubelink).replace("\\\\", "\\")
                Log.e("Youtubelink","===${finalYoutubeLink}")
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
                getLifecycle().addObserver(youTubePlayerView)
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
                Toast.makeText(this@SolutionVideoActivity, "No Video Found", Toast.LENGTH_SHORT)
                    .show()
            }

//            Handler(Looper.getMainLooper()).postDelayed({
//                initializePlayer()
//            }, 1500)
//            var topicVideoId = intent.getIntExtra("topicVideoId",0)
//            Log.e("topicVideoId",topicVideoId.toString())
//            watchVideoVM.getTopicVideo(topicVideoId.toString())
        }
        handler = Handler() // new handler


        handler?.postDelayed(runnable, 2000)
        fullscreenButton = playerView.findViewById(R.id.exo_fullscreen_icon)
        fullscreenButton?.setOnClickListener(View.OnClickListener {
            if (fullscreen) {
                fullscreenButton?.setImageDrawable(
                    ContextCompat.getDrawable(
                        this@SolutionVideoActivity,
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
                        this@SolutionVideoActivity,
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

    fun oneMinuteTimer()
    {

        customeCountDownTimer =   object : CustomCountDownTimer(61000, 1000) {
            override fun onTick(millisUntilFinished: Long) {
                Log.e("One", "Second $${millisUntilFinished / 1000}")
            }

            override fun onFinish() {
                Log.e("Finished", "Yes")
//                watchVideoVM.earnPointsRequestModel.ModuleId = Constants.questionPaperId //Question paper ID
//                watchVideoVM.earnPointsRequestModel.PointType = "QuestionPaper"
//                watchVideoVM.earnPointsRequestModel.Point = Constants.POINT_PLUS_VIDEO
//                watchVideoVM.earnPointsRequestModel.SubjectId = Constants.subjectIdFromQuestion
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

    private fun onNewState(newState: PlayerConstants.PlayerState) {

        val playerState: String = playerStateToString(newState)
        if (playerState == "ENDED") {
            var totalSeconds = tracker.videoDuration

            val dividend = totalSeconds?.toInt()
            val divisor = 60

            val quotient = dividend?.div(divisor)
            val remainder = dividend?.rem(divisor)
            watchVideoVM.earnPointsRequestModel.ModuleId = Constants.questionPaperId //Question paper ID
            watchVideoVM.earnPointsRequestModel.PointType = "QuestionPaper"
            watchVideoVM.earnPointsRequestModel.Point = Constants.POINT_PLUS_VIDEO
            watchVideoVM.earnPointsRequestModel.SubjectId = Constants.subjectIdFromQuestion

            watchVideoVM.earnPointsRequestModel.VideoMinutes = quotient?.toString()
            watchVideoVM.earnPointsRequestModel.VideoCompleted = true
            watchVideoVM.addPoints()
            totalWatchCount++
            customeCountDownTimer?.cancel()
            if(congratulationPopupCount == 0)
            {
                showCongratulationsDialog(totalWatchCount)
                congratulationPopupCount++
            }

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

    }

    fun showCongratulationsDialog(quotient: Int) {
        val dialog = Dialog(this@SolutionVideoActivity)
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

    private fun playerStateToString(state: PlayerConstants.PlayerState): String {
        return when (state) {
            PlayerConstants.PlayerState.UNKNOWN -> "UNKNOWN"
            PlayerConstants.PlayerState.UNSTARTED -> "UNSTARTED"
            PlayerConstants.PlayerState.ENDED -> "ENDED"
            PlayerConstants.PlayerState.PLAYING -> "PLAYING"
            PlayerConstants.PlayerState.PAUSED -> "PAUSED"
            PlayerConstants.PlayerState.BUFFERING -> "BUFFERING"
            PlayerConstants.PlayerState.VIDEO_CUED -> "VIDEO_CUED"
            else -> "status unknown"
        }
    }

    private fun addToList(playerState: String, stateHistory: MutableList<Pair<Date, String>>) {
        if (stateHistory.size >= 15) stateHistory.removeAt(0)
        stateHistory.add(Pair(Date(), playerState))
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
//                    watchVideoVM.earnPointsRequestModel.ModuleId =
//                        ChapterWithAnimation2Activity.mainTopicId.toString()
//                    watchVideoVM.earnPointsRequestModel.PointType = "TopicVideo"
//                    watchVideoVM.earnPointsRequestModel.Point = quotient?.toString()
//                    watchVideoVM.earnPointsRequestModel.VideoMinutes = quotient?.toString()
//                    watchVideoVM.earnPointsRequestModel.VideoCompleted = false
//                    watchVideoVM.addPoints()

                }
                handler?.postDelayed(this, 1000) // reschedule the handler
            }

        }
    }

    private fun buildMediaSource(uri: Uri): MediaSource? {
        val dataSourceFactory: DataSource.Factory =
            DefaultDataSourceFactory(this, "exoplayer-codelab")
        return HlsMediaSource.Factory(dataSourceFactory).createMediaSource(uri)
    }


    private fun initializePlayer(fileName: String) {

        var filenameFinal = fileName.split(".")
        var HLS_STATIC_URL = "$VIDEO_BASE_URL${filenameFinal[0]}.m3u8"

        val mediaItem = MediaItem.Builder()
            .setUri(HLS_STATIC_URL)
            .setMimeType(MimeTypes.APPLICATION_M3U8)
            .build()
        exoQuality = playerView.findViewById(R.id.exo_quality)


        exoQuality.setOnClickListener {
            if (trackDialog == null) {
                initPopupQuality()
            }
            trackDialog?.show()
        }

        trackSelector = DefaultTrackSelector(this)
        trackSelector.setParameters(
            trackSelector.buildUponParameters().setMaxVideoSize(MAX_WIDTH, MAX_HEIGHT)
        )

        val mappedTrackInfo: MappingTrackSelector.MappedTrackInfo? =
            trackSelector!!.currentMappedTrackInfo

        if (mappedTrackInfo != null) {

            MappingTrackSelector.MappedTrackInfo.RENDERER_SUPPORT_NO_TRACKS
            var dialog = CustomTrackSelectionDialogBuilder(
                this,
                "Select Video Resolution",
                trackSelector,
                0
            );
            dialog.setShowDisableOption(false);
            dialog.setAllowAdaptiveSelections(false);
            dialog.build().show();
        }
        var trackSelectionFactory = ClassAdaptiveTrackSelection.Factory(DefaultBandwidthMeter())

        mediaDataSourceFactory = DefaultDataSourceFactory(
            this, Util.getUserAgent(
                this,
                "mediaPlayerSample"
            )
        )

        //   val mediaSource = buildMediaSource(Uri.parse(Uri.decode(WatchVideoActivity.STREAM_URLSTRING)))


//        val mediaSource = ProgressiveMediaSource.Factory(mediaDataSourceFactory).createMediaSource(
//            MediaItem.fromUri(STREAM_URLSTRING)
//        )

        val mediaSourceFactory: MediaSourceFactory = DefaultMediaSourceFactory(
            mediaDataSourceFactory
        )


        val defaultTrackParam = trackSelector.buildUponParameters().build()
        trackSelector.parameters = defaultTrackParam


        trackSelector.setParameters(
            trackSelector
                .buildUponParameters()
                .setAllowVideoMixedMimeTypeAdaptiveness(true)
        )
        val parameters = trackSelector.buildUponParameters()
            .setMaxVideoBitrate(LO_BITRATE)
            .setForceHighestSupportedBitrate(true)
            .build()
        trackSelector.parameters = parameters

        simpleExoPlayer = SimpleExoPlayer.Builder(this)
            //.setMediaSourceFactory(mediaSourceFactory)
            .setTrackSelector(trackSelector)
            .build().apply {
                playWhenReady = true
                seekTo(2 * 1000)
                setMediaItem(mediaItem)
                prepare()
            }


        mediaDataSourceFactory = DefaultDataSourceFactory(
            this, Util.getUserAgent(
                this,
                "mediaPlayerSample"
            )
        )


//        val mediaSource = ProgressiveMediaSource.Factory(mediaDataSourceFactory).createMediaSource(
//            MediaItem.fromUri(STREAM_URLSTRING)
//        )
//
//        val mediaSourceFactory: MediaSourceFactory = DefaultMediaSourceFactory(
//            mediaDataSourceFactory
//        )
//
//
//        simpleExoPlayer = SimpleExoPlayer.Builder(this)
//            .setMediaSourceFactory(mediaSourceFactory)
//            .build()

//        simpleExoPlayer.addMediaSource(mediaSource)
        simpleExoPlayer.addListener(object : Player.EventListener {
            fun onTimelineChanged(timeline: Timeline?, manifest: Any?) {}
            override fun onTracksChanged(
                trackGroups: TrackGroupArray,
                trackSelections: TrackSelectionArray
            ) {
                Log.e("TAG", "onTracksChanged: ")
            }

            override fun onLoadingChanged(isLoading: Boolean) {}
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
                    Log.e("Video", "Ended")

                    if (::simpleExoPlayer.isInitialized) {
                        if (!isVideoEnded) {
                            var totalSeconds = (simpleExoPlayer.duration / 1000)

                            val dividend = totalSeconds?.toInt()
                            val divisor = 60

                            val quotient = dividend?.div(divisor)
                            val remainder = dividend?.rem(divisor)
                            watchVideoVM.earnPointsRequestModel.ModuleId =
                                Constants.questionPaperId //Question paper ID
                            watchVideoVM.earnPointsRequestModel.PointType = "QuestionPaper"
                            watchVideoVM.earnPointsRequestModel.Point = Constants.POINT_PLUS_VIDEO
                            watchVideoVM.earnPointsRequestModel.SubjectId =
                                Constants.subjectIdFromQuestion

                            watchVideoVM.earnPointsRequestModel.VideoMinutes = quotient?.toString()
                            watchVideoVM.earnPointsRequestModel.VideoCompleted = true
                            watchVideoVM.earnPointsRequestModel.IsCompleted = true
                            watchVideoVM.addPoints()
                            totalWatchCount++
//                            watchVideoVM.addDuration(
//                                ChapterWithAnimation2Activity.mainTopicVideoId.toString(),
//                                quotient?.toString(),
//                                true
//                            )
                            customeCountDownTimer?.cancel()
                            if (congratulationPopupCount == 0) {
                                showCongratulationsDialog(totalWatchCount)
                                congratulationPopupCount++
                            }

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
        simpleExoPlayer.playWhenReady = true
        simpleExoPlayer.prepare()
        Handler(Looper.getMainLooper()).postDelayed({ //Do your work
            videoWatchedTime = simpleExoPlayer.getCurrentPosition() / 1000
            Log.e("Watched", "timing is videoWatchedTime")
        }, 1000)
        simpleExoPlayer.setRepeatMode(Player.REPEAT_MODE_OFF);
        playerView.setShutterBackgroundColor(Color.TRANSPARENT)
        playerView.player = simpleExoPlayer
        playerView.requestFocus()
    }
    // QUALITY SELECTOR

    private fun initPopupQuality() {
        val mappedTrackInfo = trackSelector.currentMappedTrackInfo
        var videoRenderer: Int? = null

        if (mappedTrackInfo == null) return else exoQuality.visibility = View.VISIBLE

        for (i in 0 until mappedTrackInfo.rendererCount) {
            if (isVideoRenderer(mappedTrackInfo, i)) {
                videoRenderer = i
            }
        }

        if (videoRenderer == null) {
            exoQuality.visibility = View.GONE
            return
        }

        val trackSelectionDialogBuilder = CustomTrackSelectionDialogBuilder(
            this,
            getString(R.string.qualitySelector),
            trackSelector,
            videoRenderer
        )
        trackSelectionDialogBuilder.setTrackNameProvider {
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

    private fun releasePlayer() {
        simpleExoPlayer.release()
    }

    public override fun onStart() {
        super.onStart()

        // if (Util.SDK_INT > 23) initializePlayer()

    }

    public override fun onResume() {
        super.onResume()
        // initializePlayer()
        //  if (Util.SDK_INT <= 23) initializePlayer()

    }

    public override fun onPause() {
        super.onPause()
        customeCountDownTimer?.pause()
        watchVideoVM.earnPointsRequestModel.ModuleId = ""
        if (::simpleExoPlayer.isInitialized) {
            simpleExoPlayer?.setPlayWhenReady(false)
        }

        // if (Util.SDK_INT <= 23) releasePlayer()

    }

    public override fun onStop() {
        super.onStop()

        //  if (Util.SDK_INT > 23) releasePlayer()
    }

    companion object {
        var STREAM_URL: Uri? = null
        var STREAM_URLSTRING: String = ""
    }
}