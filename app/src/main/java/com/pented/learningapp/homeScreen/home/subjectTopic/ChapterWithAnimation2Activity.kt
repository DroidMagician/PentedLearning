package com.pented.learningapp.homeScreen.home.subjectTopic

import android.animation.ValueAnimator
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.res.Resources
import android.os.Build
import android.os.Handler
import android.os.Looper
import android.util.DisplayMetrics
import android.util.Log
import android.view.View
import android.view.ViewGroup
import android.view.animation.AnimationUtils
import android.widget.LinearLayout
import android.widget.Toast
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.LinearSmoothScroller
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.SCROLL_STATE_IDLE
import androidx.recyclerview.widget.RecyclerView.SmoothScroller
import com.google.gson.Gson
import com.pented.learningapp.R
import com.pented.learningapp.adapter.PlanetAdapterNew
import com.pented.learningapp.base.BaseActivity
import com.pented.learningapp.databinding.ActivityChapterWithAnimation2Binding
import com.pented.learningapp.helper.Constants
import com.pented.learningapp.helper.Utils
import com.pented.learningapp.homeScreen.home.model.TopicVideoResponseModel
import com.pented.learningapp.homeScreen.home.subjectTopic.viewModel.SubjectTopicVM
import com.pented.learningapp.homeScreen.home.watchVideo.WatchVideoActivity
import kotlinx.android.synthetic.main.activity_chapter_with_animation2.*
import kotlinx.android.synthetic.main.activity_chapter_with_animation2.animationView
import kotlinx.android.synthetic.main.activity_chapter_with_animation2.ivBack
import kotlinx.android.synthetic.main.activity_chapter_with_animation2.mainFrame
import kotlinx.android.synthetic.main.transparent_progressbar_layout.*
import java.util.*
import kotlin.collections.ArrayList


class ChapterWithAnimation2Activity: BaseActivity<ActivityChapterWithAnimation2Binding>(){
    override fun viewModel() = ViewModelProvider(this).get(SubjectTopicVM::class.java)
    override fun layoutID() = R.layout.activity_chapter_with_animation2
    lateinit var receiver: MyReceiver
    var handler :  Handler  ? = null
    var handler1 :  Handler  ? = null
    var handler2 :  Handler  ? = null
    var handler3 :  Handler  ? = null
    var handler4 :  Handler  ? = null
    var handler5 :  Handler  ? = null
    companion object{
        var mainTopicId = 0
        var mainTopicVideoId = 0
        var titleName = ""
        val topicDataList = ArrayList<TopicVideoResponseModel.Video>()
        var current = 0
        var isLastVideo = false
    }
    var isFirstTime = false
   // val planetsModel = ArrayList<PlanetsModel>()
    var isAllVideoComplated = false

    lateinit var subjectTopicVM: SubjectTopicVM
    lateinit var recyclerView: RecyclerView
    var firstpx = 0f
    override fun initActivity() {
        subjectTopicVM = (getViewModel() as SubjectTopicVM)
        init()
        observer()
        listener()
    }

//    override fun onCreate(savedInstanceState: Bundle?) {
//        super.onCreate(savedInstanceState)
//        setContentView(R.layout.activity_chapter_with_animation2)
//        init()
//        observer()
//        listener()
//    }


    private fun listener() {

    }

    private fun observer() {
        subjectTopicVM.observedErrorMessageChanges().observe(this, { event ->
            event?.getContentIfNotHandled()?.let {
                showErrorMessage(it, this, mainFrame)
            }
        })
        subjectTopicVM.observedTopicContentListData().observe(this, { event ->
            event?.getContentIfNotHandled()?.let {
               // Log.e("Topic","Data is ${it.data.Videos?.get(0)?.TopicVideoTitle}")
                it?.data?.Completed?.toFloat().let {
                    if (it != null) {
                        progressView1.progress = it
                    }
                }
                topicDataList.clear()
                for(i  in 0 until it?.data?.Videos?.size!!)
                {
//                    if(i<5)
//                    {
//                        it?.data?.Videos!![i].IsCompleted = true
//                    }
                    topicDataList.add(it?.data?.Videos!![i])
                }
                if(topicDataList.any { it.VideoPlayCompleted == false })
                {
                    isAllVideoComplated = false
                }
                else{
                    isAllVideoComplated = true
                }
                animationView.animate()
                    //  .translationYBy(120f)
                    .translationY(-firstpx)
                    .setDuration(1000);
                handler1 = Handler(Looper.myLooper()!!)
                handler1?.postDelayed({
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
                handler =  Handler(Looper.getMainLooper())
                handler?.postDelayed({
                    //Do something after 3s
                    if(isFirstTime)
                    {
                        if(topicDataList.size > 0)
                        {
                            txtLetsStart.performClick()
                            isFirstTime = false
                        }
                    }
                }, 3500)
              //  it.data.Videos?.let { it1 -> topicDataList.addAll(it1) }
                setTopicDataListAdapter()
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
    }

    private fun setTopicDataListAdapter() {
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
//        planetsModel.add(PlanetsModel("Equations", R.drawable.ic_teal_world))
//        planetsModel.add(PlanetsModel("Decimals", R.drawable.ic_blue_world))
//        planetsModel.add(PlanetsModel("Equations", R.drawable.ic_yellow_world))
//        planetsModel.add(PlanetsModel("Latitude1", R.drawable.ic_red_world))
//        planetsModel.add(PlanetsModel("Latitude2", R.drawable.ic_red_world))
//        planetsModel.add(PlanetsModel("Latitude3", R.drawable.ic_red_world))
//        planetsModel.add(PlanetsModel("Latitude4", R.drawable.ic_red_world))

        //creating our adapter
        val adapter = PlanetAdapterNew(topicDataList, this)
//        Handler(Looper.myLooper()!!).postDelayed({
//            planetsModel[0].isRocketVisible = true
//            recyclerView.adapter?.notifyDataSetChanged()
//            animationView.visibility= View.GONE
//            animationView.animate().translationX(0f).translationY(0f);
//        }, 4000)

        //now adding the adapter to recyclerview
        recyclerView.adapter = adapter
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

    override fun onDestroy() {
        super.onDestroy()
        handler?.removeCallbacksAndMessages(null)
        handler1?.removeCallbacksAndMessages(null)
        handler2?.removeCallbacksAndMessages(null)
        handler3?.removeCallbacksAndMessages(null)
        handler4?.removeCallbacksAndMessages(null)
        unregisterReceiver(receiver)
    }
    private fun init() {
        topicDataList.clear()
        isFirstTime = true
        current = 0
        if(intent.hasExtra("topicID"))
        {
            var topicId = intent.getIntExtra("topicID",0)
            mainTopicId = topicId
            subjectTopicVM.callTopicListData(topicId.toString())
        }
        if(intent.hasExtra("chapterName"))
        {
            titleName = intent.getStringExtra("chapterName") ?: ""
            txtChapterName.text = intent.getStringExtra("chapterName")
        }


        var animation = AnimationUtils.loadAnimation(this@ChapterWithAnimation2Activity, R.anim.bounce);
        imgChapterList.startAnimation(animation)



        imgChapterList.setOnClickListener {
            handler?.removeCallbacksAndMessages(null)
            val intent = Intent(this@ChapterWithAnimation2Activity, TopicVideoListActivity::class.java)
            intent.putExtra("topicID", mainTopicId)
            intent.putExtra("chapterName",titleName)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
            startActivity(intent)
            //startActivity(TopicVideoListActivity::class.java)
        }

        var smoothScroller: SmoothScroller =
            object : LinearSmoothScroller(this@ChapterWithAnimation2Activity) {
                override fun getVerticalSnapPreference(): Int {
                    return SNAP_TO_END
                }
            }
        val dip = current * 200f
        val r: Resources = resources
        val px = convertDpToPixel(340f, this@ChapterWithAnimation2Activity)
        recyclerView = findViewById<RecyclerView?>(R.id.rvPlanetChapter)
        Handler(Looper.myLooper()!!).postDelayed({
            //  recyclerView.isLayoutFrozen = true
        }, 1000)
        val displayMetrics = DisplayMetrics()
        windowManager.defaultDisplay.getMetrics(displayMetrics)
        val height = displayMetrics.heightPixels
        val width = displayMetrics.widthPixels
       firstpx = convertDpToPixel(100f, this@ChapterWithAnimation2Activity)

        if (height < 2000) {

        } else {

        }
        val dip1 = current * 300f
        val r1: Resources = resources
        val pxel = convertDpToPixel(300f, this@ChapterWithAnimation2Activity)
        Log.e("pxel", "Is $pxel and px is $px")
        txtLetsStart.setOnClickListener {
            if(topicDataList.size > 0)
            {
                handler?.removeCallbacksAndMessages(null)
                animationView.setImageResource(R.drawable.ic_flying_rocket_new);
                txtLetsStart.visibility = View.GONE
                animationView.animate()
                    //.translationYBy(120f)
                    .translationY(-pxel)
                    .setDuration(2000);
               handler2 = Handler(Looper.myLooper()!!)
                handler2?.postDelayed({
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
                    for (planet in topicDataList) {
                        planet.isRocketVisible = false
                    }
                    topicDataList[current].isRocketVisible = true
                    recyclerView.adapter?.notifyDataSetChanged()

                    if( topicDataList[current].VideoPlayCompleted == true && (!isAllVideoComplated))
                    {
                        sendBroadcast(Intent("StartNowTopicVideo"))
                    }
                    else
                    {
                        val gson = Gson()
                        if(current == topicDataList.size -1)
                        {
                            isLastVideo = true
                        }
                        else{
                            isLastVideo = false
                        }
                        var intent =
                            Intent(this@ChapterWithAnimation2Activity, WatchVideoActivity::class.java)
                        // intent.putExtra("topicVideoId",topicDataList[current].TopicVideoId)
                        Constants.isFromNormalVideoList = false
                        mainTopicVideoId = topicDataList[current].TopicVideoId ?: 0

                        intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT)
                        intent.putExtra("topicVideo", gson.toJson(topicDataList[current]))
                        Log.e("current topicVideo", "Is ${gson.toJson(topicDataList[current])}")
                        startActivity(intent)
                    }

                    Handler(Looper.myLooper()!!).postDelayed({
                        // lilStartNow.visibility = View.VISIBLE
                    }, 300)

                }, 2100)

            }
            else
            {
                Toast.makeText(this@ChapterWithAnimation2Activity,"There is no any video", Toast.LENGTH_SHORT).show()
            }


        }
        Log.e("height", "Is" + height)
        receiver = MyReceiver(Handler(Looper.myLooper()!!))
        val intentfilter = IntentFilter()
        intentfilter.addAction("DoAnimation")
        intentfilter.addAction("StartNowTopicVideo")
        intentfilter.addAction("PlayNext")
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            registerReceiver(receiver, intentfilter, RECEIVER_EXPORTED)
        } else {
            @Suppress("UnspecifiedRegisterReceiverFlag")
                    registerReceiver(receiver, intentfilter)
        }

        txtStartNow.setOnClickListener {
            //lilStartNow.visibility = View.GONE
            Utils.getNonWindowTouchable(this@ChapterWithAnimation2Activity)
            for (planet in topicDataList) {
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

           handler3 = Handler(Looper.myLooper()!!)
            handler3?.postDelayed({
                animationView.visibility = View.INVISIBLE
                animationView.animate().translationX(0f).translationY(0f);

                smoothScroll(recyclerView, current, 150)
                //recyclerView.smoothSnapToPosition(current)
                for (planet in topicDataList) {
                    planet.isRocketVisible = false
                }
                if (current < topicDataList.size) {
                    topicDataList[current].isRocketVisible = true

                    recyclerView.adapter?.notifyDataSetChanged()
                    Handler(Looper.myLooper()!!).postDelayed({
                        //recyclerView.isLayoutFrozen = true
                        //   lilStartNow.visibility = View.VISIBLE
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
                            Utils.getWindowTouchable(this@ChapterWithAnimation2Activity)
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
                    else if(intent?.action?.equals("StartNowTopicVideo") == true)
                    {

                        val px = convertDpToPixel(340f, this@ChapterWithAnimation2Activity)
                        Utils.getNonWindowTouchable(this@ChapterWithAnimation2Activity)
                        for (planet in topicDataList) {
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
                        handler4 = Handler(Looper.myLooper()!!)
                        handler4?.postDelayed({
                            animationView.visibility = View.INVISIBLE
                            animationView.animate().translationX(0f).translationY(0f);

                            smoothScroll(recyclerView, current, 150)
                            //recyclerView.smoothSnapToPosition(current)
                            for (planet in topicDataList) {
                                planet.isRocketVisible = false
                            }
                            if (current < topicDataList.size) {

                                Log.e("topicDataList","Is ${Gson().toJson(topicDataList)}")
                                if( topicDataList[current].VideoPlayCompleted == true && (!isAllVideoComplated))
                                {
                                    sendBroadcast(Intent("StartNowTopicVideo"))
                                }
                                else
                                {
                                    topicDataList[current].isRocketVisible = true

                                    recyclerView.adapter?.notifyDataSetChanged()
                                    if(current == topicDataList.size -1)
                                    {
                                        isLastVideo = true
                                    }
                                    else{
                                        isLastVideo = false
                                    }
                                    val gson = Gson()
                                    var intent = Intent(this@ChapterWithAnimation2Activity, WatchVideoActivity::class.java)
                                    //  intent.putExtra("topicVideoId",topicDataList[current].TopicVideoId)
                                    Constants.isFromNormalVideoList = false
                                    mainTopicVideoId = topicDataList[current].TopicVideoId ?: 0
                                    intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT)
                                    intent.putExtra("topicVideo",gson.toJson(topicDataList[current]))
                                    Log.e("topicVideoId===",gson.toJson(topicDataList[current]))
                                    Log.e("current===","Is $current")
                                    startActivity(intent)
                                }

                                Handler(Looper.myLooper()!!).postDelayed({
                                    //recyclerView.isLayoutFrozen = true
                                    //   lilStartNow.visibility = View.VISIBLE
                                }, 1000)
                            }
                        }, 2100)
                    }
                }


            }
        }
    }
}