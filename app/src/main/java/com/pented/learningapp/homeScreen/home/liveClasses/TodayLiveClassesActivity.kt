package com.pented.learningapp.homeScreen.home.liveClasses

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.net.Uri
import android.os.Build
import android.os.Handler
import android.os.Looper
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.lifecycle.ViewModelProvider
import com.pented.learningapp.BR
import com.pented.learningapp.R
import com.pented.learningapp.base.BaseActivity
import com.pented.learningapp.base.BaseViewModel
import com.pented.learningapp.base.BindingAdapter
import com.pented.learningapp.databinding.ActivityLiveClassesBinding
import com.pented.learningapp.helper.Constants
import com.pented.learningapp.helper.Utils
import com.pented.learningapp.helper.Utils.getFormatedDate
import com.pented.learningapp.helper.Utils.getFormatedDateLiveLature
import com.pented.learningapp.homeScreen.home.liveClasses.activity.YoutubePlayerActivity
import com.pented.learningapp.homeScreen.home.liveClasses.model.GetLiveClassResponseModel
import com.pented.learningapp.homeScreen.home.liveClasses.model.LiveClassesModel
import com.pented.learningapp.homeScreen.home.liveClasses.viewModel.LiveClassVM
import kotlinx.android.synthetic.main.activity_live_classes.*
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.transparent_progressbar_layout.*
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.*
import java.util.concurrent.TimeUnit


class TodayLiveClassesActivity : BaseActivity<ActivityLiveClassesBinding>() {

    override fun layoutID() = R.layout.activity_live_classes
    var timer = Timer()
    var DELAY: Long = 500
    var generalTextWatcher: TextWatcher? = null
    var searchValue: String? = null
    var videoDuration = 1
    var liveClassesList: ArrayList<LiveClassesModel> = ArrayList<LiveClassesModel>()
    var expiredClassesList: ArrayList<LiveClassesModel> = ArrayList<LiveClassesModel>()
    var liveClassesListApi: ArrayList<GetLiveClassResponseModel.Data> = ArrayList<GetLiveClassResponseModel.Data>()
    var expiredLiveClassesListApi: ArrayList<GetLiveClassResponseModel.Data> = ArrayList<GetLiveClassResponseModel.Data>()
    var futuredLiveClassesListApi: ArrayList<GetLiveClassResponseModel.Data> = ArrayList<GetLiveClassResponseModel.Data>()
    var liveClassesListApiFilter: ArrayList<GetLiveClassResponseModel.Data> = ArrayList<GetLiveClassResponseModel.Data>()
    var liveClassesListApiFilterFuture: ArrayList<GetLiveClassResponseModel.Data> = ArrayList<GetLiveClassResponseModel.Data>()
    override fun viewModel(): BaseViewModel = ViewModelProvider(this).get(LiveClassVM::class.java)
    lateinit var liveClassVM: LiveClassVM
    lateinit var receiver: MyReceiver
    override fun initActivity() {
        init()
        observer()
        listner()
    }


    fun getFormattedDate(date: Date?): String? {
        val cal = Calendar.getInstance()
        cal.time = date
        //2nd of march 2015
        val day = cal[Calendar.DATE]
        return if (!(day > 10 && day < 19)) when (day % 10) {
            1 -> SimpleDateFormat("d'st' 'of' MMMM yyyy").format(date)
            2 -> SimpleDateFormat("d'nd' 'of' MMMM yyyy").format(date)
            3 -> SimpleDateFormat("d'rd' 'of' MMMM yyyy").format(date)
            else -> SimpleDateFormat("d'th' 'of' MMMM yyyy").format(date)
        } else SimpleDateFormat("d'th' 'of' MMMM yyyy").format(date)
    }
    private fun listner() {
        ivBack.setOnClickListener {
            onBackPressed()
        }
        ivSearch.setOnClickListener {
            lilSearch.visibility = View.VISIBLE
            Utils.showKeyboard(this@TodayLiveClassesActivity, edtSearch)
            ivSearch.visibility = View.GONE
        }
        icCross.setOnClickListener {
            edtSearch.setText("")
            Utils.hideKeyboard(this@TodayLiveClassesActivity)
            ivSearch.visibility = View.VISIBLE
            lilSearch.visibility = View.GONE
            setLiveClassAdapterAPI()
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
                                setLiveClassAdapterAPI(searchValue ?: "")
                                setFutureLiveClassAdapter(searchValue ?: "")
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
                        setLiveClassAdapterAPI()
                        setFutureLiveClassAdapter()
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
        liveClassVM.observedErrorMessageChanges().observe(this, { event ->
            event?.getContentIfNotHandled()?.let {
                showErrorMessage(it, this, mainFrame)
            }
        })
        liveClassVM.observerLiveClassData().observe(this, { event ->
            event?.getContentIfNotHandled()?.let {

                separator_after_live.visibility = View.GONE
                separator_after_expired.visibility = View.GONE
                recycler_view_ongoing.visibility = View.GONE
                liveClassesListApi.clear()
                futuredLiveClassesListApi.clear()
                // Log.e("Live class is","Here"+it.data[0].Description)

                liveClassesListApi.addAll(it.data)
//                for (i in 0..it.data.size-1)
//                {
//                    var date = 27
//                    var time = 6
//                    var myOnj = it.data.get(i);
//                    myOnj.Datetime = "2024-10-${date + 1}T${time + 1}:30 PM"
//                    liveClassesListApi.add(myOnj)
//                }
                Collections.sort<GetLiveClassResponseModel.Data>(liveClassesListApi, sortItems())
//                sortArray(liveClassesListApi);
//                liveClassesListApi.sortByDescending { data ->data.totalMillis  }
                expiredLiveClassesListApi.clear()
                for (liveclass in liveClassesListApi) {
                    liveclass.Datetime?.let { it1 -> getFormatedDate(it1, "") }
                    liveclass.Date = getFormatedDateLiveLature(liveclass.Datetime ?: "", "")

//                    var dateString = liveclass.Date
//                    val formatter: DateFormat = SimpleDateFormat("DD mm yyyy", Locale.getDefault())
//                    var date = formatter.parse("04 October, 2021") as Date
                    val inputFormat = SimpleDateFormat("yyyy-MM-dd'T'h:mm a")
                    val outputFormat = SimpleDateFormat("dd-MM-yyyy hh:mm a")
                    val date: Date = inputFormat.parse(liveclass.Datetime)
                    //  val formattedDate: String = outputFormat.format(date)
                    var cal = Calendar.getInstance()
                    cal.setTime(date)

                    val seconds: Long =
                        (cal.getTimeInMillis() - Calendar.getInstance().getTimeInMillis()) / 1000
                    val hours = (seconds / 3600).toInt()
                    Log.e("Hours $hours", "Seconds$seconds")
                    var time1 = cal.time
                    var time2 = Calendar.getInstance().time
                    var diffrence =
                        (cal.getTimeInMillis() - Calendar.getInstance().getTimeInMillis())
                    liveclass.difference = formatElapsedTime(diffrence / 1000)
                    liveclass.totalMillis = diffrence



                    if (time1.compareTo(time2) > 0) {
                        futuredLiveClassesListApi.add(liveclass)
                        Log.e("app", "Date1 is after Date2");
                    } else if (time1.compareTo(time2) < 0) {
                        Log.e("app", "Date1 is before Date2");

                        try {
                            var cal2 = Calendar.getInstance()
                            cal2.setTime(date)
                            cal2.add(Calendar.HOUR_OF_DAY, 1)

                            val calendar3 = Calendar.getInstance()
                            val x = calendar3.time
                            if (x.after(cal.time) && x.before(cal2.time)) {
                                Log.e("In Between TIme", "Found")
                                liveclass.isCurrentlyRunning = true
                                futuredLiveClassesListApi.add(liveclass)
                            } else {
                                expiredLiveClassesListApi.add(liveclass)
                                liveclass.isCurrentlyRunning = false
                                Log.e("In Between TIme", "Not Found")
                            }
                        } catch (e: ParseException) {
                            Log.e("In Between TIme", "Exception Found")
                            e.printStackTrace()
                        }

                    } else if (time1.compareTo(time2) == 0) {
                        Log.e("app", "Date1 is equal to Date2");
                    }
                }
//                sortArray(futuredLiveClassesListApi);
//                var myObj = futuredLiveClassesListApi.first();
//                myObj.Datetime = "2024-10-27T5:30 PM"
//                myObj.Date = "2024-10-27T00:00:00"
//                myObj.Time = "5:30 PM"
//                futuredLiveClassesListApi.add(myObj)
//                sortArray(expiredLiveClassesListApi);
//                futuredLiveClassesListApi.sortByDescending { data ->data.totalMillis  }
//                expiredLiveClassesListApi.sortByDescending { data ->data.totalMillis  }

                Log.e(
                    "Expired",
                    "List ${expiredLiveClassesListApi.size} Future ${futuredLiveClassesListApi.size}"
                )

                if (expiredLiveClassesListApi.size > 0) {
                    setLiveClassAdapterAPI()
                }
                if (futuredLiveClassesListApi.size > 0) {
                    recycler_view.visibility = View.VISIBLE
                    setFutureLiveClassAdapter()
                }

                if (expiredClassesList.size > 0 && futuredLiveClassesListApi.size > 0) {
                    separator_after_live.visibility = View.VISIBLE
                }

            }
        })

        liveClassVM.observedChanges().observe(this, { event ->
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

    fun formatElapsedTime(seconds: Long): String? {
        var seconds = seconds
        val hours: Long = TimeUnit.SECONDS.toHours(seconds)
        seconds -= TimeUnit.HOURS.toSeconds(hours)
        val minutes: Long = TimeUnit.SECONDS.toMinutes(seconds)
        seconds -= TimeUnit.MINUTES.toSeconds(minutes)
        return String.format("starting in %02d:%02d:%02d h", hours, minutes, seconds)
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

    override fun onDestroy() {
        super.onDestroy()
        unregisterReceiver(receiver)
    }
    private fun init() {
        liveClassVM = (getViewModel() as LiveClassVM)
        receiver = MyReceiver(Handler(Looper.myLooper()!!))
        val intentfilter = IntentFilter()
        intentfilter.addAction(Constants.REFRESH_LIVE_LACTURES)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            registerReceiver(receiver, intentfilter,RECEIVER_EXPORTED)
        } else {
            @Suppress("UnspecifiedRegisterReceiverFlag")
            registerReceiver(receiver, intentfilter)
        }
//        registerReceiver(receiver, intentfilter)
        if(Constants.isApiCalling)
        {

            liveClassVM.callGetLiveClasss()
        }
        else
        {
            var examBlueprintModel = LiveClassesModel("Maths", "Starting in 4:24 h")
            var examBlueprintModel1 = LiveClassesModel("Science", "Starting in 2:24 h")

            var examBlueprintModel2 = LiveClassesModel("Linear equations", "Starting in 4:24 h")
            var examBlueprintModel3 = LiveClassesModel("Social geometry", "Starting in 2:24 h")


            liveClassesList.add(examBlueprintModel)
            liveClassesList.add(examBlueprintModel1)
            expiredClassesList.add(examBlueprintModel2)
            expiredClassesList.add(examBlueprintModel3)


            //setExamBluePrintAdapter()
        }

    }
    private fun setLiveClassAdapterAPI(searchValue: String = "") {
//        recycler_view.adapter = BindingAdapter(
//            layoutId = R.layout.row_live_classes,
//            br = BR.model,
//            list = ArrayList(liveClassesList),
//            clickListener = { view, position ->
//                when (view.id) {
//                    R.id.lilMain -> {
//
//                    }
//                }
//            })
        if(!searchValue.isNullOrBlank())
        {
            liveClassesListApiFilter.clear()
            for (liveClass in expiredLiveClassesListApi)
            {
                if(liveClass.SubjectName?.contains(searchValue) == true || liveClass.TopicTitle?.contains(
                        searchValue
                    ) == true)
                {
                    liveClassesListApiFilter.add(liveClass)
                }
            }
//            sortArray(liveClassesListApiFilter);
//            liveClassesListApiFilter.sortByDescending { data ->data.totalMillis  }

            recycler_view_expired.adapter = BindingAdapter(
                layoutId = R.layout.row_expired_classes_api,
                br = BR.model,
                list = ArrayList(liveClassesListApiFilter),
                clickListener = { view, position ->

                    Log.e("Position","$position and Size = ${liveClassesListApiFilter.size}")
                    when (view.id) {
                        R.id.getRecording -> {
                            if(!liveClassesListApiFilter[position].RecordingLink.isNullOrBlank())
                            {
                                var listStrings = liveClassesListApiFilter[position].RecordingLink?.split("=")
                                listStrings?.get(listStrings.size-1)?.let {
                                    startActivityWithDataKey(YoutubePlayerActivity::class.java,
                                        it,"VideoLink")
                                }
                            }
                            else{
                                Toast.makeText(this@TodayLiveClassesActivity,"No recorded video found",Toast.LENGTH_SHORT).show()
                            }

                        }
                        R.id.lilMain -> {

                            if(!liveClassesListApiFilter[position].RecordingLink.isNullOrBlank())
                            {
                                var listStrings = liveClassesListApiFilter[position].RecordingLink?.split("=")
                                listStrings?.get(listStrings.size-1)?.let {
                                    startActivityWithDataKey(YoutubePlayerActivity::class.java,
                                        it,"VideoLink")
                                }
                            }
                            else{
                                Toast.makeText(this@TodayLiveClassesActivity,"No recorded video found",Toast.LENGTH_SHORT).show()
                            }

//                                if (liveClassesListApiFilter[position].JoinLink?.contains("zoom") == true) {
//                                    val intent = Intent(
//                                        Intent.ACTION_VIEW,
//                                        Uri.parse("zoomus://zoom.us/join?confno=7618757358&pwd=ghR5nb")
//                                    )
//                                    if (intent.resolveActivity(packageManager) != null) {
//                                        liveClassVM.addPoints()
//                                        startActivity(intent)
//                                    }
//                                } else if (liveClassesListApiFilter[position].JoinLink?.contains("meet") == true) {
//                                    val conference =
//                                        Uri.parse("https://meet.google.com/fdb-nbqn-eqa")
//                                    val mapIntent = Intent(Intent.ACTION_VIEW, conference)
//
//                                    val packageManager = packageManager
//                                    val activities = packageManager.queryIntentActivities(
//                                        mapIntent,
//                                        0
//                                    )
//
//                                    val isIntentSafe = activities.size > 0
//
//                                    if (isIntentSafe) {
//                                        liveClassVM.addPoints()
//                                        startActivity(mapIntent)
//                                    }
//                                }
//
                        }
                    }
                })
        }
        else
        {
            recycler_view_expired.adapter = BindingAdapter(
                layoutId = R.layout.row_expired_classes_api,
                br = BR.model,
                list = ArrayList(expiredLiveClassesListApi),
                clickListener = { view, position ->
                    when (view.id) {
                        R.id.getRecording -> {
                            if(!expiredLiveClassesListApi[position].RecordingLink.isNullOrBlank())
                            {
                              //  var url = URLEncoder.encode(expiredLiveClassesListApi[position].RecordingLink, "utf-8")
                             //   Log.e("url", "Is ==$url")
                                var listStrings = expiredLiveClassesListApi[position].RecordingLink?.split("=")
                                Log.e("Youtube","Video Id=== ${listStrings?.get(listStrings.size-1)}")
                                listStrings?.get(listStrings.size-1)?.let {
                                    startActivityWithDataKey(YoutubePlayerActivity::class.java,
                                        it,"VideoLink")
                                }
                            }
                            else{
                                Toast.makeText(this@TodayLiveClassesActivity,"No recorded video found",Toast.LENGTH_SHORT).show()
                            }
                        }
                        R.id.lilMain -> {

                            if(!expiredLiveClassesListApi[position].RecordingLink.isNullOrBlank())
                            {
                                var listStrings = expiredLiveClassesListApi[position].RecordingLink?.split("=")
                                //var url = URLEncoder.encode(expiredLiveClassesListApi[position].RecordingLink, "utf-8")
                                //Log.e("url", "Is ==$url")
                                listStrings?.get(listStrings.size-1)?.let {
                                    startActivityWithDataKey(YoutubePlayerActivity::class.java,
                                        it,"VideoLink")
                                }
                                Log.e("Youtube","Video Id=== ${listStrings?.get(listStrings.size-1)}")
                            }
                            else{
                                Toast.makeText(this@TodayLiveClassesActivity,"No recorded video found",Toast.LENGTH_SHORT).show()
                            }
//                            if (expiredLiveClassesListApi[position].JoinLink?.contains("zoom") == true) {
//                                val intent = Intent(
//                                    Intent.ACTION_VIEW,
//                                    Uri.parse("zoomus://zoom.us/join?confno=7618757358&pwd=ghR5nb")
//                                )
//                                if (intent.resolveActivity(packageManager) != null) {
//                                    liveClassVM.addPoints()
//                                    startActivity(intent)
//                                }
//                            } else if (expiredLiveClassesListApi[position].JoinLink?.contains("meet") == true) {
//                                val conference = Uri.parse("https://meet.google.com/fdb-nbqn-eqa")
//                                val mapIntent = Intent(Intent.ACTION_VIEW, conference)
//
//                                val packageManager = packageManager
//                                val activities = packageManager.queryIntentActivities(mapIntent, 0)
//
//                                val isIntentSafe = activities.size > 0
//
//                                if (isIntentSafe) {
//                                    liveClassVM.addPoints()
//                                    startActivity(mapIntent)
//                                }
//                            }

                        }
                    }
                })
        }



//        recycler_view_ongoing.adapter = BindingAdapter(
//            layoutId = R.layout.row_live_classes,
//            br = BR.model,
//            list = ArrayList(liveClassesList),
//            clickListener = { view, position ->
//                when (view.id) {
//                    R.id.lilMain -> {
//
//                    }
//                }
//            })
    }
    inner class MyReceiver(handler: Handler) : BroadcastReceiver() {
        var handler: Handler = handler // Handler used to execute code on the UI thread
        override fun onReceive(context: Context?, intent: Intent?) {
            Log.e("Intent", "Action ${intent?.action}")
            handler.post {
                run {
                    if (intent?.action.equals(Constants.REFRESH_LIVE_LACTURES)){
//                        futuredLiveClassesListApi.clear()
//                        expiredLiveClassesListApi.clear()
//                        for (liveclass in liveClassesListApi) {
//                            liveclass.Datetime?.let { it1 -> getFormatedDate(it1, "") }
//                            liveclass.Date = getFormatedDateLiveLature(liveclass.Datetime ?: "", "")
//
////                    var dateString = liveclass.Date
////                    val formatter: DateFormat = SimpleDateFormat("DD mm yyyy", Locale.getDefault())
////                    var date = formatter.parse("04 October, 2021") as Date
//                            val inputFormat = SimpleDateFormat("yyyy-MM-dd'T'h:mm a")
//                            val outputFormat = SimpleDateFormat("dd-MM-yyyy hh:mm a")
//                            val date: Date = inputFormat.parse(liveclass.Datetime)
//                            //  val formattedDate: String = outputFormat.format(date)
//                            var cal = Calendar.getInstance()
//                            cal.setTime(date)
//
//                            val seconds: Long =
//                                (cal.getTimeInMillis() - Calendar.getInstance().getTimeInMillis()) / 1000
//                            val hours = (seconds / 3600).toInt()
//                            Log.e("Hours $hours", "Seconds$seconds")
//                            var time1 = cal.time
//                            var time2 = Calendar.getInstance().time
//                            var diffrence =
//                                (cal.getTimeInMillis() - Calendar.getInstance().getTimeInMillis())
//                            liveclass.difference = formatElapsedTime(diffrence / 1000)
//                            liveclass.totalMillis = diffrence
//
//
//
//                            if (time1.compareTo(time2) > 0) {
//                                futuredLiveClassesListApi.add(liveclass)
//                                Log.e("app", "Date1 is after Date2");
//                            } else if (time1.compareTo(time2) < 0) {
//                                Log.e("app", "Date1 is before Date2");
//
//                                try {
//                                    var cal2 = Calendar.getInstance()
//                                    cal2.setTime(date)
//                                    cal2.add(Calendar.HOUR_OF_DAY, 1)
//
//                                    val calendar3 = Calendar.getInstance()
//                                    val x = calendar3.time
//                                    if (x.after(cal.time) && x.before(cal2.time)) {
//                                        Log.e("In Between TIme", "Found")
//                                        liveclass.isCurrentlyRunning = true
//                                        futuredLiveClassesListApi.add(liveclass)
//                                    } else {
//                                        expiredLiveClassesListApi.add(liveclass)
//                                        liveclass.isCurrentlyRunning = false
//                                        Log.e("In Between TIme", "Not Found")
//                                    }
//                                } catch (e: ParseException) {
//                                    Log.e("In Between TIme", "Exception Found")
//                                    e.printStackTrace()
//                                }
//
//                            } else if (time1.compareTo(time2) == 0) {
//                                Log.e("app", "Date1 is equal to Date2");
//                            }
//
//                        }
//
//                        Log.e(
//                            "Expired",
//                            "List ${expiredLiveClassesListApi.size} Future ${futuredLiveClassesListApi.size}"
//                        )
//
//                        if (expiredLiveClassesListApi.size > 0) {
//                            setLiveClassAdapterAPI()
//                        }
//                        if (futuredLiveClassesListApi.size > 0) {
//                            recycler_view.visibility = View.VISIBLE
//                            setFutureLiveClassAdapter()
//                        }
//
//                        if (expiredClassesList.size > 0 && futuredLiveClassesListApi.size > 0) {
//                            separator_after_live.visibility = View.VISIBLE
//                        }


                           liveClassVM.callGetLiveClasss()
                    }
                }
            }
        }
    }

    private fun setFutureLiveClassAdapter(searchValue: String = "") {
        if(!searchValue.isNullOrBlank()) {
            liveClassesListApiFilterFuture.clear()
            for (liveClass in futuredLiveClassesListApi) {
                if (liveClass.SubjectName?.contains(searchValue) == true || liveClass.TopicTitle?.contains(
                        searchValue
                    ) == true
                ) {
                    liveClassesListApiFilterFuture.add(liveClass)
                }
            }
//            sortArray(liveClassesListApiFilterFuture);
//            liveClassesListApiFilterFuture.sortByDescending { data ->data.totalMillis  }
            recycler_view.adapter = BindingAdapter(
                layoutId = R.layout.row_live_classes,
                br = BR.model,
                list = ArrayList(liveClassesListApiFilterFuture),
                clickListener = { view, position ->
                    when (view.id) {
                        R.id.getRecording -> {
                            if(liveClassesListApiFilterFuture[position].isCurrentlyRunning)
                            {
                                if (liveClassesListApiFilterFuture[position].JoinLink?.contains("zoom") == true) {
                                    var meetingIdsPassword = liveClassesListApiFilterFuture[position].JoinLink?.split("?")
                                    if(meetingIdsPassword?.size == 2)
                                    {
                                        var meetingId  = meetingIdsPassword[0].split("/").last()
                                        var password = meetingIdsPassword[1].split("=").last()
                                        Log.e("MeetingId","Is =${meetingId} Password ${password}")

                                        val intent = Intent(
                                            Intent.ACTION_VIEW,
                                            Uri.parse("zoomus://zoom.us/join?confno=$meetingId&pwd=$password")
                                        )
                                        if (intent.resolveActivity(packageManager) != null) {
                                            liveClassVM.addPoints(liveClassesListApiFilterFuture[position].LiveLectureId,liveClassesListApiFilterFuture[position].SubjectId)
                                            startActivity(intent)
                                        }
                                        else{
                                            Toast.makeText(this@TodayLiveClassesActivity,"No app found",Toast.LENGTH_SHORT).show()
                                        }
                                    }
                                } else if (liveClassesListApiFilterFuture[position].JoinLink?.contains("meet") == true) {
                                    var meetiD = liveClassesListApiFilterFuture[position].JoinLink?.split("/")?.last()
                                    val conference =
                                        Uri.parse("https://meet.google.com/$meetiD")
                                    val mapIntent = Intent(Intent.ACTION_VIEW, conference)

                                    val packageManager = packageManager
                                    val activities = packageManager.queryIntentActivities(
                                        mapIntent,
                                        0
                                    )

                                    val isIntentSafe = activities.size > 0

                                    if (isIntentSafe) {
                                        liveClassVM.addPoints(liveClassesListApiFilterFuture[position].LiveLectureId,liveClassesListApiFilterFuture[position].SubjectId)
                                        startActivity(mapIntent)
                                    }
                                    else{
                                        Toast.makeText(this@TodayLiveClassesActivity,"No app found",Toast.LENGTH_SHORT).show()
                                    }
                                }
                            }
                        }
                        R.id.lilMain -> {
                            if(liveClassesListApiFilterFuture[position].isCurrentlyRunning)
                            {
                                if (liveClassesListApiFilterFuture[position].JoinLink?.contains("zoom") == true) {
                                    var meetingIdsPassword = liveClassesListApiFilterFuture[position].JoinLink?.split("?")
                                    if(meetingIdsPassword?.size == 2)
                                    {
                                        var meetingId  = meetingIdsPassword[0].split("/").last()
                                        var password = meetingIdsPassword[1].split("=").last()
                                        Log.e("MeetingId","Is =${meetingId} Password ${password}")

                                        val intent = Intent(
                                            Intent.ACTION_VIEW,
                                            Uri.parse("zoomus://zoom.us/join?confno=$meetingId&pwd=$password")
                                        )
                                        if (intent.resolveActivity(packageManager) != null) {
                                            liveClassVM.addPoints(liveClassesListApiFilterFuture[position].LiveLectureId,liveClassesListApiFilterFuture[position].SubjectId)
                                            startActivity(intent)
                                        }
                                    }
                                } else if (liveClassesListApiFilterFuture[position].JoinLink?.contains("meet") == true) {
                                    var meetiD = liveClassesListApiFilterFuture[position].JoinLink?.split("/")?.last()
                                    val conference =
                                        Uri.parse("https://meet.google.com/$meetiD")
                                    val mapIntent = Intent(Intent.ACTION_VIEW, conference)

                                    val packageManager = packageManager
                                    val activities = packageManager.queryIntentActivities(
                                        mapIntent,
                                        0
                                    )

                                    val isIntentSafe = activities.size > 0

                                    if (isIntentSafe) {
                                        liveClassVM.addPoints(liveClassesListApiFilterFuture[position].LiveLectureId,liveClassesListApiFilterFuture[position].SubjectId)
                                        startActivity(mapIntent)
                                    }
                                    else{
                                        Toast.makeText(this@TodayLiveClassesActivity,"No app found",Toast.LENGTH_SHORT).show()
                                    }
                                }
                            }
                        }
                    }
                })

//        recycler_view_expired.adapter = BindingAdapter(
//            layoutId = R.layout.row_expired_classes,
//            br = BR.model,
//            list = ArrayList(liveClassesList),
//            clickListener = { view, position ->
//                when (view.id) {
//                    R.id.lilMain -> {
//
//                    }
//                }
//            })
//
//        recycler_view_ongoing.adapter = BindingAdapter(
//            layoutId = R.layout.row_live_classes,
//            br = BR.model,
//            list = ArrayList(liveClassesList),
//            clickListener = { view, position ->
//                when (view.id) {
//                    R.id.lilMain -> {
//
//                    }
//                }
//            })
        }
        else
        {
            recycler_view.adapter = BindingAdapter(
                layoutId = R.layout.row_live_classes,
                br = BR.model,
                list = ArrayList(futuredLiveClassesListApi),
                clickListener = { view, position ->
                    when (view.id) {
                        R.id.getRecording -> {
                            if(futuredLiveClassesListApi[position].isCurrentlyRunning)
                            {
                                if (futuredLiveClassesListApi[position].JoinLink?.contains("zoom") == true) {
                                    var meetingIdsPassword = futuredLiveClassesListApi[position].JoinLink?.split("?")
                                    if(meetingIdsPassword?.size == 2)
                                    {
                                        var meetingId  = meetingIdsPassword[0].split("/").last()
                                        var password = meetingIdsPassword[1].split("=").last()
                                        Log.e("MeetingId","Is =${meetingId} Password ${password}")

                                        val intent = Intent(
                                            Intent.ACTION_VIEW,
                                            Uri.parse("zoomus://zoom.us/join?confno=$meetingId&pwd=$password")
                                        )
                                        if (intent.resolveActivity(packageManager) != null) {
                                            liveClassVM.addPoints(futuredLiveClassesListApi[position].LiveLectureId,futuredLiveClassesListApi[position].SubjectId)
                                            startActivity(intent)
                                        }
                                        else{
                                            Toast.makeText(this@TodayLiveClassesActivity,"No app found",Toast.LENGTH_SHORT).show()
                                        }
                                    }
                                } else if (futuredLiveClassesListApi[position].JoinLink?.contains("meet") == true) {
                                    var meetiD = futuredLiveClassesListApi[position].JoinLink?.split("/")?.last()
                                    val conference =
                                        Uri.parse("https://meet.google.com/$meetiD")
                                    val mapIntent = Intent(Intent.ACTION_VIEW, conference)

                                    val packageManager = packageManager
                                    val activities = packageManager.queryIntentActivities(
                                        mapIntent,
                                        0
                                    )

                                    val isIntentSafe = activities.size > 0

                                    if (isIntentSafe) {
                                        liveClassVM.addPoints(futuredLiveClassesListApi[position].LiveLectureId,futuredLiveClassesListApi[position].SubjectId)
                                        startActivity(mapIntent)
                                    }
                                    else{
                                        Toast.makeText(this@TodayLiveClassesActivity,"No app found",Toast.LENGTH_SHORT).show()
                                    }
                                }
                            }
                        }
                        R.id.lilMain -> {
                            if(futuredLiveClassesListApi[position].isCurrentlyRunning)
                            {
                                if (futuredLiveClassesListApi[position].JoinLink?.contains("zoom") == true) {
                                    var meetingIdsPassword = futuredLiveClassesListApi[position].JoinLink?.split("?")
                                    if(meetingIdsPassword?.size == 2)
                                    {
                                        var meetingId  = meetingIdsPassword[0].split("/").last()
                                        var password = meetingIdsPassword[1].split("=").last()
                                        Log.e("MeetingId","Is =${meetingId} Password ${password}")

                                        val intent = Intent(
                                            Intent.ACTION_VIEW,
                                            Uri.parse("zoomus://zoom.us/join?confno=$meetingId&pwd=$password")
                                        )
                                        if (intent.resolveActivity(packageManager) != null) {
                                            liveClassVM.addPoints(futuredLiveClassesListApi[position].LiveLectureId,futuredLiveClassesListApi[position].SubjectId)
                                            startActivity(intent)
                                        }
                                        else{
                                            Toast.makeText(this@TodayLiveClassesActivity,"No app found",Toast.LENGTH_SHORT).show()
                                        }
                                    }
                                } else if (futuredLiveClassesListApi[position].JoinLink?.contains("meet") == true) {
                                    var meetiD = futuredLiveClassesListApi[position].JoinLink?.split("/")?.last()
                                    val conference =
                                        Uri.parse("https://meet.google.com/$meetiD")
                                    val mapIntent = Intent(Intent.ACTION_VIEW, conference)

                                    val packageManager = packageManager
                                    val activities = packageManager.queryIntentActivities(
                                        mapIntent,
                                        0
                                    )

                                    val isIntentSafe = activities.size > 0

                                    if (isIntentSafe) {
                                        liveClassVM.addPoints(futuredLiveClassesListApi[position].LiveLectureId,futuredLiveClassesListApi[position].SubjectId)
                                        startActivity(mapIntent)
                                    }
                                    else{
                                        Toast.makeText(this@TodayLiveClassesActivity,"No app found",Toast.LENGTH_SHORT).show()
                                    }
                                }
                            }
                        }
                    }
                })

//        recycler_view_expired.adapter = BindingAdapter(
//            layoutId = R.layout.row_expired_classes,
//            br = BR.model,
//            list = ArrayList(liveClassesList),
//            clickListener = { view, position ->
//                when (view.id) {
//                    R.id.lilMain -> {
//
//                    }
//                }
//            })
//
//        recycler_view_ongoing.adapter = BindingAdapter(
//            layoutId = R.layout.row_live_classes,
//            br = BR.model,
//            list = ArrayList(liveClassesList),
//            clickListener = { view, position ->
//                when (view.id) {
//                    R.id.lilMain -> {
//
//                    }
//                }
//            })
        }

    }

    class sortItems : Comparator<GetLiveClassResponseModel.Data> {
        // Method of this class
        // @Override
        override fun compare(
            a: GetLiveClassResponseModel.Data,
            b: GetLiveClassResponseModel.Data
        ): Int {
            // Returning the value after comparing the objects
            // this will sort the data in Ascending order
            val simpleDateFormat = SimpleDateFormat("yyyy-MM-dd'T'hh:mm a")
            try {
                return a.Datetime?.let {
                    simpleDateFormat.parse(it)
                        ?.compareTo(b.Datetime?.let { it1 -> simpleDateFormat.parse(it1) })
                } ?: 0
            } catch (e: ParseException) {
                e.printStackTrace()
                return 0
            }
        }
    }
    private fun sortArray(arraylist: ArrayList<GetLiveClassResponseModel.Data>) {
        //"Datetime":"2024-10-26T6:30 PM"
        val simpleDateFormat = SimpleDateFormat("yyyy-MM-dd'T'hh:mm a") // your own date format
        Collections.sort(arraylist) { o1, o2 ->
            try {
                Log.e("TimeStemp1 ","=== ${simpleDateFormat.parse(o1.Datetime)} Value ${simpleDateFormat.parse(o2.Datetime)}");

                simpleDateFormat.parse(o2.Datetime)?.compareTo(simpleDateFormat.parse(o1.Datetime)) ?: 0
            } catch (e: ParseException) {
                e.printStackTrace()
                0
            }
        }

        for (i in 0 until arraylist.size)
        {
            Log.e("LiveLectureObject","${arraylist[i]}")
        }
    }


//    private fun sortArray( arraylist:ArrayList<GetLiveClassResponseModel.Data>) {
//        var simpleDateFormat =  SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'"); //your own date format
//        Collections.sort(arraylist,  Comparator<GetLiveClassResponseModel.Data>() {
//
//        });
//        }
}