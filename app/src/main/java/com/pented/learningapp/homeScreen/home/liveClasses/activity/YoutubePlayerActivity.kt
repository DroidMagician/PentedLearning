package com.pented.learningapp.homeScreen.home.liveClasses.activity

import android.content.pm.ActivityInfo
import android.os.Build
import android.util.Log
import android.view.View
import android.view.WindowManager
import android.widget.LinearLayout
import androidx.annotation.NonNull
import androidx.lifecycle.ViewModelProvider
import com.pented.learningapp.R
import com.pented.learningapp.base.BaseActivity
import com.pented.learningapp.base.BaseViewModel
import com.pented.learningapp.databinding.ActivityYoutubePlayerBinding
import com.pented.learningapp.helper.Constants.ifFullScreen
import com.pented.learningapp.helper.JustCopyItVIewModel
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.YouTubePlayer
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.listeners.AbstractYouTubePlayerListener
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.listeners.YouTubePlayerFullScreenListener
import kotlinx.android.synthetic.main.activity_youtube_player.*


class YoutubePlayerActivity : BaseActivity<ActivityYoutubePlayerBinding>() {
    override fun viewModel(): BaseViewModel = ViewModelProvider(this).get(JustCopyItVIewModel::class.java)

    override fun layoutID() = R.layout.activity_youtube_player
    override fun initActivity() {
        init()
        observer()
        listner()
    }

    private fun listner() {

    }

    private fun observer() {

    }

    override fun onDestroy() {
        super.onDestroy()
        youTubePlayerView?.release()
        if (Build.VERSION.SDK_INT < 16) {
            window.clearFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN)
        } else {
            val decorView = window.decorView
            // Show Status Bar.
            val uiOptions = View.SYSTEM_UI_FLAG_VISIBLE
            decorView.systemUiVisibility = uiOptions
        }
    }

    private fun init() {
        // Hide Status Bar
        var youTubeId:String ? = null
        if(intent.hasExtra("VideoLink"))
        {
            youTubeId = intent.getStringExtra("VideoLink")
        }
        Log.e("Full screen", " $ifFullScreen")
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
        youTubePlayerView.addYouTubePlayerListener(object : AbstractYouTubePlayerListener() {
            override fun onReady(@NonNull youTubePlayer: YouTubePlayer) {
                val videoId = youTubeId
                videoId?.let { youTubePlayer.loadVideo(it, 0f) }
            }
        })

//        youTubePlayerView.addFullScreenListener(object : YouTubePlayerFullScreenListener {
//            override fun onYouTubePlayerEnterFullScreen() {
//                requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE
//                youTubePlayerView.enterFullScreen()
//
//            }
//
//            override fun onYouTubePlayerExitFullScreen() {
//
//                requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
//                youTubePlayerView.exitFullScreen()
//
//            }
//        })
        youTubePlayerView.getPlayerUiController().setFullScreenButtonClickListener(View.OnClickListener {
            Log.e("Full screen", "Clicked $ifFullScreen")
            if(ifFullScreen)
            {
                ifFullScreen = false
                requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
            }
            else
            {
                ifFullScreen = true
                requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE
            }
           // youTubePlayerView.toggleFullScreen()
        })
//        youTubePlayerViewVertical.getPlayerUiController().setFullScreenButtonClickListener(View.OnClickListener {
//            Log.e("Full screen", "Clicked")
//            requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE
//            if (Build.VERSION.SDK_INT < 16) {
//                window.setFlags(
//                    WindowManager.LayoutParams.FLAG_FULLSCREEN,
//                    WindowManager.LayoutParams.FLAG_FULLSCREEN
//                )
//            } else {
//                val decorView: View = window.decorView
//                // Hide Status Bar.
//                val uiOptions: Int = View.SYSTEM_UI_FLAG_FULLSCREEN
//                decorView.setSystemUiVisibility(uiOptions)
//            }
//            youTubePlayerViewVertical.visibility = View.GONE
//            youTubePlayerView.visibility = View.VISIBLE
//            youTubePlayerViewVertical.release()
//            getLifecycle().addObserver(youTubePlayerView)
//            youTubePlayerView.addYouTubePlayerListener(object : AbstractYouTubePlayerListener() {
//                override fun onReady(@NonNull youTubePlayer: YouTubePlayer) {
//                    val videoId = "6Tm9bN40b6I"
//                    youTubePlayer.loadVideo(videoId, 0f)
//                }
//            })
//        })
//
//        youTubePlayerView.getPlayerUiController().setFullScreenButtonClickListener(View.OnClickListener {
//            Log.e("Full screen", "Clicked")
//            requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
//            if (Build.VERSION.SDK_INT < 16) {
//                window.clearFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN)
//            } else {
//                val decorView = window.decorView
//                // Show Status Bar.
//                val uiOptions = View.SYSTEM_UI_FLAG_VISIBLE
//                decorView.systemUiVisibility = uiOptions
//            }
//            youTubePlayerView.release()
//            ifFullScreen = false
//            youTubePlayerView.visibility = View.GONE
//            youTubePlayerViewVertical.visibility = View.VISIBLE
//            getLifecycle().addObserver(youTubePlayerViewVertical)
//            youTubePlayerViewVertical.addYouTubePlayerListener(object :
//                AbstractYouTubePlayerListener() {
//                override fun onReady(@NonNull youTubePlayer: YouTubePlayer) {
//                    val videoId = "6Tm9bN40b6I"
//                    youTubePlayer.loadVideo(videoId, 0f)
//                }
//            })
//        })
    }

}