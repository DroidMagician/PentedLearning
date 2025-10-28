package com.pented.learningapp.activity

import android.animation.ObjectAnimator
import android.animation.ValueAnimator
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.DisplayMetrics
import android.view.View
import android.view.animation.*
import android.widget.LinearLayout
import android.widget.RelativeLayout
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.pented.learningapp.R
import com.pented.learningapp.adapter.PlanetAdapter
import com.pented.learningapp.model.PlanetsModel
import kotlinx.android.synthetic.main.activity_chapter_with_animation.*
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.util.*


class ChapterWithAnimationActivity : AppCompatActivity(), View.OnClickListener {

     lateinit var mAnimationSlide : Animation
     lateinit var mAnimationSlide_ : Animation


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_chapter_with_animation)


        llLetsStart.visibility = View.GONE

        ivBack.setOnClickListener(this)
      //  vRocket.setOnClickListener(this)

//        Timer().schedule(2000){
//
//        }


//        GlobalScope.launch { // launch new coroutine in background and continue
//            delay(2000L) // non-blocking delay for 2 second (default time unit is ms)
//
//            runOnUiThread {
//                llInitial.visibility = View.GONE
//                llLetsStart.visibility = View.VISIBLE
//            }
//        }



        //getting recyclerview from xml
        val recyclerView = findViewById(R.id.rvPlanetChapter) as RecyclerView


        recyclerView.apply {
            layoutManager = LinearLayoutManager(applicationContext).apply {
                reverseLayout = true
            }
        }
        //crating an arraylist to store users using the data class user
        val planetsModel = ArrayList<PlanetsModel>()

        //adding some dummy data to the list
        planetsModel.add(PlanetsModel("Linear equations",R.drawable.ic_teal_world))
        planetsModel.add(PlanetsModel("Decimals",R.drawable.ic_blue_world))
        planetsModel.add(PlanetsModel("Equations",R.drawable.ic_yellow_world))
        planetsModel.add(PlanetsModel("Latitude",R.drawable.ic_red_world))

        //creating our adapter
        val adapter = PlanetAdapter(planetsModel,this)

        //now adding the adapter to recyclerview
        recyclerView.adapter = adapter


        recyclerView.layoutParams = LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.MATCH_PARENT,
            0,
            1.18f)



    }

    override fun onClick(v: View?) {
        when(v?.id){
            R.id.vRocket->{

                tvLetsStart.visibility = View.GONE
                ivRocket.setImageResource(R.drawable.ic_flying_rocket_new)

                val root = findViewById<View>(R.id.rlMain) as RelativeLayout

                val set = AnimationSet(true)

                val fadeIn = FadeIn(3000)
                fadeIn!!.startOffset = 0
                set.addAnimation(fadeIn)


                val dm = DisplayMetrics()
                this.windowManager.defaultDisplay.getMetrics(dm)
                val statusBarOffset = dm.heightPixels - root.measuredHeight

                val originalPos = IntArray(2)
                llLetsStart.getLocationOnScreen(originalPos)

                var xDest = dm.widthPixels / 2
                xDest -= llLetsStart.measuredWidth / 2
                val yDest: Int = (dm.heightPixels / 2 - llLetsStart.measuredHeight / 2
                        - statusBarOffset)

                val anim = TranslateAnimation(
                    1F, (xDest
                            - originalPos[0]).toFloat(), 1F, (yDest - originalPos[1]).toFloat()
                )
                anim.duration = 3000
                set.addAnimation(anim)

                val fadeOut = FadeOut(1000)
                fadeOut!!.startOffset = 3000
                set.addAnimation(fadeOut)

                set.fillAfter = true
                set.isFillEnabled = true
                llLetsStart.startAnimation(set)



                Handler(Looper.getMainLooper()).postDelayed({
                    val m1 = ValueAnimator.ofFloat(1f, 2f)
                    m1.duration = 800
//                        m1.startDelay = 100 //Optional Delay

                    m1.interpolator = LinearInterpolator()
                    m1.addUpdateListener { animation ->
                        (rvPlanetChapter.getLayoutParams() as LinearLayout.LayoutParams).weight =
                            animation.animatedValue as Float
                        rvPlanetChapter.requestLayout()
                    }
                    m1.start()

//                        mAnimationSlide = AnimationUtils.loadAnimation(applicationContext, R.anim.visible_to_bottom);
//                        rvPlanetChapter.startAnimation(mAnimationSlide)
//
//                        val layoutParams: RelativeLayout.LayoutParams = RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT,RelativeLayout.LayoutParams.MATCH_PARENT)
//                        layoutParams.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM)
//                        llLetsStart.layoutParams = layoutParams

                    llLetsStart.animate()
//                            .translationX(((root.width - llLetsStart.getWidth()) / 2).toFloat())
                        .translationY(((root.height - llLetsStart.getHeight()) / 2).toFloat())
                        .setInterpolator(AccelerateInterpolator())
                        .setDuration(800)

                    GlobalScope.launch {
                        delay(850L)
                        runOnUiThread {
                            llStartNow.visibility = View.VISIBLE
                        }
                    }

                    GlobalScope.launch {
                        delay(4000L)
                        runOnUiThread {
                            llStartNow.visibility = View.GONE
                            rvPlanetChapter.isLayoutFrozen = true
                        }
                    }
                }, 3500)

//                GlobalScope.launch {
//                    delay(3500L)
//                    runOnUiThread {
//
//                    }
//                }
            }

            R.id.ivBack->{
                finish()
            }
        }
    }

    private fun FadeIn(t: Int): Animation? {
        val fade: Animation
        fade = AlphaAnimation(1.0f, 1.0f)
        fade.setDuration(t.toLong())
        fade.setInterpolator(AccelerateInterpolator())
        return fade
    }

    private fun FadeOut(t: Int): Animation? {
        val fade: Animation
        fade = AlphaAnimation(1.0f, 1.0f)
        fade.setDuration(t.toLong())
        fade.setInterpolator(AccelerateInterpolator())
        return fade
    }
}