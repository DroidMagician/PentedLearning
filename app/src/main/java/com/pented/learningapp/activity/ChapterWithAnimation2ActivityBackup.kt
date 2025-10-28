package com.pented.learningapp.activity

import android.animation.ValueAnimator
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.res.Resources
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.DisplayMetrics
import android.util.Log
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.LinearSmoothScroller
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.SCROLL_STATE_IDLE
import androidx.recyclerview.widget.RecyclerView.SmoothScroller
import com.pented.learningapp.R
import com.pented.learningapp.adapter.PlanetAdapterNew
import com.pented.learningapp.helper.Constants
import com.pented.learningapp.helper.Utils
import com.pented.learningapp.model.PlanetsModel
import kotlinx.android.synthetic.main.activity_chapter_with_animation.*
import kotlinx.android.synthetic.main.activity_chapter_with_animation2.*
import kotlinx.android.synthetic.main.activity_chapter_with_animation2.ivBack
import java.util.*
import kotlin.collections.ArrayList


class ChapterWithAnimation2ActivityBackup : AppCompatActivity() {
    lateinit var receiver: MyReceiver
    val planetsModel = ArrayList<PlanetsModel>()
    lateinit var recyclerView: RecyclerView
    var current = 0
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_chapter_with_animation2)

    //    rvPlanetChapter.isLayoutFrozen = true
        //getting recyclerview from xml
        var smoothScroller: SmoothScroller =
            object : LinearSmoothScroller(this@ChapterWithAnimation2ActivityBackup) {
                override fun getVerticalSnapPreference(): Int {
                    return SNAP_TO_END
                }
            }
        val dip = current * 200f
        val r: Resources = resources
        val px = convertDpToPixel(340f, this@ChapterWithAnimation2ActivityBackup)
        recyclerView = findViewById(R.id.rvPlanetChapter) as RecyclerView
        Handler(Looper.myLooper()!!).postDelayed({
            //  recyclerView.isLayoutFrozen = true
        }, 1000)
        val displayMetrics = DisplayMetrics()
        windowManager.defaultDisplay.getMetrics(displayMetrics)
        val height = displayMetrics.heightPixels
        val width = displayMetrics.widthPixels
        val firstpx = convertDpToPixel(100f, this@ChapterWithAnimation2ActivityBackup)
        animationView.animate()
          //  .translationYBy(120f)
            .translationY(-firstpx)
            .setDuration(1000);
        Handler(Looper.myLooper()!!).postDelayed({
            txtLetsStart.animate()
                //.translationYBy(120f)
                .translationY(-firstpx)
                .setDuration(1000);
            val params = animationView.getLayoutParams() as ViewGroup.MarginLayoutParams
            params.setMargins(
                params.leftMargin, params.topMargin,
                params.rightMargin, 0
            )
            //animationView.setPadding(0, 0, 0, 0);
        }, 1100)
        if (height < 2000) {

        } else {

        }
        val dip1 = current * 300f
        val r1: Resources = resources
        val pxel = convertDpToPixel(300f, this@ChapterWithAnimation2ActivityBackup)
        Log.e("pxel", "Is $pxel and px is $px")
        txtLetsStart.setOnClickListener {

            animationView.setImageResource(R.drawable.ic_flying_rocket_new);
            txtLetsStart.visibility = View.GONE
            animationView.animate()
                //.translationYBy(120f)
                .translationY(-pxel)
                .setDuration(2000);
            Handler(Looper.myLooper()!!).postDelayed({
                animationView.visibility = View.INVISIBLE
                animationView.animate().translationX(0f).translationY(0f);

                val params = recyclerView.layoutParams as LinearLayout.LayoutParams
                val animator = ValueAnimator.ofInt(params.bottomMargin, 0)
                animator.addUpdateListener { valueAnimator ->
                    params.bottomMargin = (valueAnimator.animatedValue as Int)!!
                    recyclerView.requestLayout()
                }
                animator.duration = 700
                animator.start()
                //   recyclerView.setPadding(0, 0, 0, 0);
                for (planet in planetsModel) {
                    planet.isRocketVisible = false
                }
                planetsModel[current].isRocketVisible = true
                recyclerView.adapter?.notifyDataSetChanged()
                Handler(Looper.myLooper()!!).postDelayed({
                    lilStartNow.visibility = View.VISIBLE

                }, 300)

            }, 2100)


        }
        Log.e("height", "Is" + height)
        receiver = MyReceiver(Handler(Looper.myLooper()!!))
        val intentfilter = IntentFilter()
        intentfilter.addAction("DoAnimation")
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            registerReceiver(receiver, intentfilter, RECEIVER_EXPORTED)
        } else {
            @Suppress("UnspecifiedRegisterReceiverFlag")
            registerReceiver(receiver, intentfilter)
        }
//        registerReceiver(receiver, intentfilter)


        txtStartNow.setOnClickListener {
            lilStartNow.visibility = View.GONE
            Utils.getNonWindowTouchable(this@ChapterWithAnimation2ActivityBackup)
            for (planet in planetsModel) {
                planet.isRocketVisible = false
            }
            recyclerView.adapter?.notifyDataSetChanged()
            current++
            animationView.visibility = View.VISIBLE
            animationView.animate()
                //.translationYBy(120f)
                .translationY(-px)
                .setDuration(2000);
//            Handler(Looper.myLooper()!!).postDelayed({
//                animationView.visibility= View.GONE
//            }, 2000)

            Handler(Looper.myLooper()!!).postDelayed({
                animationView.visibility = View.INVISIBLE
                animationView.animate().translationX(0f).translationY(0f);

                smoothScroll(recyclerView, current, 150)
                //recyclerView.smoothSnapToPosition(current)
                for (planet in planetsModel) {
                    planet.isRocketVisible = false
                }
                if (current < planetsModel.size) {
                    planetsModel[current].isRocketVisible = true

                    recyclerView.adapter?.notifyDataSetChanged()
                    Handler(Looper.myLooper()!!).postDelayed({
                        //recyclerView.isLayoutFrozen = true
                        lilStartNow.visibility = View.VISIBLE
                    }, 1000)
                }
            }, 2100)
            //   recyclerView.smoothScrollBy(0, -(px*2).toInt(),PathInterpolatorCompat.create(0f,0f,0f,0f),2000)
        }
        ivBack.setOnClickListener {
            current = 0
            finish()

        }

        recyclerView.apply {
            layoutManager = LinearLayoutManager(applicationContext).apply {
                reverseLayout = true

            }
        }
//        recyclerView.setHasFixedSize(true);
        recyclerView.scrollToPosition(0);
        //val disabler: OnItemTouchListener = RecyclerViewDisabler()
        recyclerView.setOnTouchListener { v, event -> true }
        val onScrollListener: RecyclerView.OnScrollListener =
            object : RecyclerView.OnScrollListener() {
                override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
                    when (newState) {
                        SCROLL_STATE_IDLE ->                    //we reached the target position
                            Utils.getWindowTouchable(this@ChapterWithAnimation2ActivityBackup)
                    }
                }
            }
        recyclerView.addOnScrollListener(onScrollListener)
//        val height = animationView.height
//        ObjectAnimator.ofFloat(animationView, "translationY", height.toFloat(),0.toFloat()).apply {
//            duration = 3000
//            start()
//        }

//        val animate = TranslateAnimation(0f, 0f, animationView.getHeight().toFloat()+5000f, 10000f)
//        animate.duration = 3000
//        animate.fillAfter = true
//        animationView.startAnimation(animate)
        //crating an arraylist to store users using the data class user

        //adding some dummy data to the list
        var planetList = ArrayList<Int>()
        planetList.add(R.drawable.ic_teal_world)
        planetList.add(R.drawable.ic_blue_world)
        planetList.add(R.drawable.ic_yellow_world)
        planetList.add(R.drawable.ic_red_world)
        for(topics in Constants.selectedTopicsList)
        {
            planetsModel.add(PlanetsModel(topics.Name!!, planetList.get(Random().nextInt(planetList.size))))
        }
//        planetsModel.add(PlanetsModel("Equations", R.drawable.ic_teal_world))
//        planetsModel.add(PlanetsModel("Decimals", R.drawable.ic_blue_world))
//        planetsModel.add(PlanetsModel("Equations", R.drawable.ic_yellow_world))
//        planetsModel.add(PlanetsModel("Latitude1", R.drawable.ic_red_world))
//        planetsModel.add(PlanetsModel("Latitude2", R.drawable.ic_red_world))
//        planetsModel.add(PlanetsModel("Latitude3", R.drawable.ic_red_world))
//        planetsModel.add(PlanetsModel("Latitude4", R.drawable.ic_red_world))

        //creating our adapter
        //val adapter = PlanetAdapterNew(planetsModel, this)
//        Handler(Looper.myLooper()!!).postDelayed({
//            planetsModel[0].isRocketVisible = true
//            recyclerView.adapter?.notifyDataSetChanged()
//            animationView.visibility= View.GONE
//            animationView.animate().translationX(0f).translationY(0f);
//        }, 4000)

        //now adding the adapter to recyclerview
        //recyclerView.adapter = adapter


    }

    fun convertDpToPixel(dp: Float, context: Context): Float {
        return dp * (context.resources.displayMetrics.densityDpi.toFloat() / DisplayMetrics.DENSITY_DEFAULT)
    }

    fun RecyclerView.smoothSnapToPosition(
        position: Int,
        snapMode: Int = LinearSmoothScroller.SNAP_TO_END
    ) {
        val smoothScroller = object : LinearSmoothScroller(this.context) {
            override fun getVerticalSnapPreference(): Int = snapMode
            override fun getHorizontalSnapPreference(): Int = snapMode
        }
        smoothScroller.targetPosition = position
        layoutManager?.startSmoothScroll(smoothScroller)
    }

    @Throws(IllegalArgumentException::class)
    private fun smoothScroll(rv: RecyclerView, toPos: Int, duration: Int) {
        val TARGET_SEEK_SCROLL_DISTANCE_PX =
            10000 // See androidx.recyclerview.widget.LinearSmoothScroller
        var itemHeight =
            rv.getChildAt(0).height // Height of first visible view! NB: ViewGroup method!
        itemHeight = itemHeight  // Example pixel Adjustment for decoration?
        val fvPos =
            (rv.layoutManager as LinearLayoutManager?)!!.findFirstCompletelyVisibleItemPosition()
        var i = Math.abs((fvPos - toPos) * itemHeight)
        if (i == 0) {
            i = Math.abs(rv.getChildAt(0).y).toInt()
        }
        val totalPix = i // Best guess: Total number of pixels to scroll
        val smoothScroller: SmoothScroller = object : LinearSmoothScroller(rv.context) {
            override fun getVerticalSnapPreference(): Int {
                return SNAP_TO_END
            }

            override fun calculateTimeForScrolling(dx: Int): Int {
                var ms = (duration * dx / totalPix.toFloat()).toInt()
                // Now double the interval for the last fling.
                if (dx < TARGET_SEEK_SCROLL_DISTANCE_PX) {
                    ms = ms * 2
                } // Crude deceleration!
                //lg(format("For dx=%d we allot %dms", dx, ms));
                return ms
            }
        }
        //lg(format("Total pixels from = %d to %d = %d [ itemHeight=%dpix ]", fvPos, toPos, totalPix, itemHeight));
        smoothScroller.targetPosition = toPos
        rv.layoutManager!!.startSmoothScroll(smoothScroller)
    }

    inner class MyReceiver(handler: Handler) : BroadcastReceiver() {
        var handler: Handler = handler // Handler used to execute code on the UI thread
        override fun onReceive(context: Context?, intent: Intent?) {
            Log.e("Intent", "Action ${intent?.action}")
            handler.post {
                run {
                    if (intent?.action.equals("DoAnimation")) {
                        //Handler().postDelayed({ recyclerView.smoothScrollBy(0, -1000) }, 200)
                        //recyclerView.scrollToPosition(PlanetAdapterNew.Selectedposition + 1)
                        //recyclerView.smoothScrollToPosition(3)
                        //  recyclerView.smoothScrollToPosition(PlanetAdapterNew.Selectedposition)

                        //recyclerView.scrollToPosition(PlanetAdapterNew.Selectedposition + 1)
//                        for(planet in planetsModel)
//                        {
//                            planet.isRocketVisible = false
//                        }
//                        recyclerView.adapter?.notifyDataSetChanged()
//                        animationView.visibility = View.VISIBLE
//                        animationView.animate()
//                                .translationYBy(120f)
//                                .translationY(-2500f)
//                                .setDuration(4000);
//                        Handler(Looper.myLooper()!!).postDelayed({
//                            animationView.visibility= View.GONE
//                            animationView.animate().translationX(0f).translationY(0f);
//                            for(planet in planetsModel)
//                            {
//                                planet.isRocketVisible = false
//                            }
//                            planetsModel[PlanetAdapterNew.Selectedposition].isRocketVisible = true
//                            recyclerView.adapter?.notifyDataSetChanged()
//                        }, 4000)
                        current = current + 1
                        var finalValue = 850 * current.toFloat()
//                        animationView.animate()
//                                //.translationYBy(120f)
//                                .translationY(-finalValue)
//                                .setDuration(2000);
//
//                        recyclerView.animate()
//                                //.translationYBy(120f)
//                                .translationY(-finalValue)
//                                .setDuration(2000);
//                        Handler(Looper.myLooper()!!).postDelayed({
//                            recyclerView.post {
//                                recyclerView.smoothScrollToPosition(1)
//                                // Here adapter.getItemCount()== child count
//                            }
//                        }, 1000)


                    }
                }


            }
        }
    }
}