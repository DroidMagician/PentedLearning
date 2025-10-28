package com.pented.learningapp

import android.Manifest
import android.app.Dialog
import android.content.*
import android.content.pm.PackageManager
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.net.Uri
import android.os.Build
import android.os.Handler
import android.os.Looper
import android.provider.Settings
import android.transition.TransitionManager
import android.util.Log
import android.view.View
import android.view.Window
import android.widget.ImageView
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.constraintlayout.widget.ConstraintSet
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.ViewModelProvider
import com.android.installreferrer.api.InstallReferrerClient
import com.android.installreferrer.api.InstallReferrerStateListener
import com.android.installreferrer.api.ReferrerDetails
import com.karumi.dexter.Dexter
import com.karumi.dexter.PermissionToken
import com.karumi.dexter.listener.PermissionDeniedResponse
import com.karumi.dexter.listener.PermissionGrantedResponse
import com.karumi.dexter.listener.PermissionRequest
import com.karumi.dexter.listener.single.PermissionListener
import com.pented.learningapp.authScreens.viewModel.RegisterVM
import com.pented.learningapp.base.BaseActivity
import com.pented.learningapp.base.BaseViewModel
import com.pented.learningapp.databinding.ActivityMainBinding
import com.pented.learningapp.databinding.ActivityWeekendTestSeriesBinding
import com.pented.learningapp.enum.notificationTypes
import com.pented.learningapp.helper.Constants
import com.pented.learningapp.helper.JustCopyItVIewModel
import com.pented.learningapp.helper.SharedPrefs
import com.pented.learningapp.homeScreen.home.HomeFragment
import com.pented.learningapp.homeScreen.home.liveClasses.TodayLiveClassesActivity
import com.pented.learningapp.homeScreen.leaderboard.LeaderBoardFragment
import com.pented.learningapp.homeScreen.practice.TestFragment
import com.pented.learningapp.homeScreen.scanQR.ScanQRcodeFragment
import com.pented.learningapp.homeScreen.subjects.SubjectsFragment
import com.pented.learningapp.homeScreen.subscription.ChooseYourSubscriptionActivity
import com.willy.ratingbar.ScaleRatingBar
import kotlinx.android.synthetic.main.activity_main.*
import java.util.*
import java.util.concurrent.TimeUnit
import kotlin.math.roundToInt


class MainActivity : BaseActivity<ActivityWeekendTestSeriesBinding>() {

    override fun layoutID() = R.layout.activity_main
    val REQUEST_PERMISSION_SETTING = 101
    lateinit var receiver: MyReceiver
    override fun viewModel(): BaseViewModel = ViewModelProvider(this).get(JustCopyItVIewModel::class.java)
    private lateinit var binding: ActivityMainBinding
    private lateinit var referrerClient: InstallReferrerClient

    lateinit var justCopyItVIewModel: JustCopyItVIewModel
    override fun initActivity() {
        Constants.count = 0
        binding = ActivityMainBinding.inflate(layoutInflater)

        justCopyItVIewModel = (getViewModel() as JustCopyItVIewModel)
        Constants.rateCount = 0
        Log.e("Tokenis====","Main Activity======"+SharedPrefs.getToken(this@MainActivity))
            val homeFragment = HomeFragment()
            val testFragment = TestFragment()
            val qrScanFragment = ScanQRcodeFragment()
            val subjectFragment = SubjectsFragment()
            val leaderBoardFragment = LeaderBoardFragment()
            val fm: FragmentManager = supportFragmentManager
            var active: Fragment = homeFragment
            setCurrentFragment(homeFragment)

    var loginObject = SharedPrefs.getLoginDetail(this@MainActivity)

        receiver = MyReceiver(Handler(Looper.myLooper()!!))
        val intentfilter = IntentFilter()
        intentfilter.addAction(Constants.BACKPRESSED)
        intentfilter.addAction(Constants.GO_TO_LEADERBORARD)
        intentfilter.addAction("StartNow")
//        registerReceiver(receiver, intentfilter)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
           registerReceiver(receiver, intentfilter, RECEIVER_EXPORTED)
        } else {
            @Suppress("UnspecifiedRegisterReceiverFlag")
            registerReceiver(receiver, intentfilter)
        }
            underlineSelectedItem(rlBottom, R.id.item_home)
            bottomNavigationView.setOnNavigationItemSelectedListener { item ->
                underlineSelectedItem(rlBottom, item.itemId)
                if(Constants.rateCount == 3)
                {
                    if(loginObject?.date == null)
                    {
                        loginObject?.date = Calendar.getInstance()
                        loginObject?.let { SharedPrefs.storeLoginDetail(this@MainActivity, it) }
                        if(Constants.isAppRated == false)
                        {
                            showRateDialog()
                        }

                    }
                    else{
                        if(loginObject?.date != null)
                        {
                          var daysDiff =   countDaysBetweenTwoCalendar(loginObject?.date!!, Calendar.getInstance())
                           Log.e("Days","Diff is here ${daysDiff}")
                            //Display Rate dialog for every week after every 7 days
                            if(daysDiff != 0 && daysDiff % 7 == 0 && (Constants.isAppRated == false))
                            {
                                showRateDialog()
                            }
                        }
                    }

                }
                when(item?.itemId)
                {
                    R.id.item_home -> {
                        underline.visibility = View.VISIBLE
                        Constants.rateCount = Constants.rateCount+1
                        setCurrentFragment(homeFragment)
                    }
                    R.id.item_subjects -> {
                        Constants.selectedUserId = "0"
                        if(Constants.isLockSubjects)
                        {
                            startActivity(ChooseYourSubscriptionActivity::class.java)
                        }else
                        {
                            underline.visibility = View.VISIBLE
                            Constants.rateCount = Constants.rateCount+1
                            setCurrentFragment(subjectFragment)
                        }

                    }
                    R.id.item_scan -> {
                        Constants.selectedUserId = "0"
                        if(Constants.isLockScan)
                        {
                            startActivity(ChooseYourSubscriptionActivity::class.java)
                        }
                        else
                        {
                            underline.visibility = View.GONE
                            Log.e("Item", "Scan Activity")
                            Dexter.withContext(this@MainActivity)
                                .withPermission(Manifest.permission.CAMERA)
                                .withListener(object : PermissionListener {
                                    @RequiresApi(Build.VERSION_CODES.KITKAT)
                                    override fun onPermissionGranted(response: PermissionGrantedResponse) {
                                        Constants.rateCount = Constants.rateCount+1
                                        setCurrentFragment(qrScanFragment)
                                    }

                                    override fun onPermissionDenied(response: PermissionDeniedResponse) {
                                        // check for permanent denial of permission
                                        if (response.isPermanentlyDenied) {
                                            // navigate user to app settings
                                            Toast.makeText(this@MainActivity,"Please grant camera permission to access this feature",Toast.LENGTH_SHORT).show()
                                            val intent =
                                                Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
                                            val uri = Uri.fromParts(
                                                "package",
                                                packageName,
                                                null
                                            )
                                            intent.data = uri
                                            startActivityForResult(intent, REQUEST_PERMISSION_SETTING)
                                        }
                                    }

                                    override fun onPermissionRationaleShouldBeShown(
                                        permission: PermissionRequest?,
                                        token: PermissionToken
                                    ) {
                                        token.continuePermissionRequest()
                                    }
                                }).check()

                        }




                    }
                    R.id.item_test -> {
                        Constants.selectedUserId = "0"
                        if(Constants.isLockPractice)
                        {
                            startActivity(ChooseYourSubscriptionActivity::class.java)
                        }
                        else
                        {
                            underline.visibility = View.VISIBLE
                            Constants.rateCount = Constants.rateCount+1
                            setCurrentFragment(testFragment)
                        }
                    }
                    R.id.item_leaderboard -> {
                        underline.visibility = View.VISIBLE
                        Constants.rateCount = Constants.rateCount+1
                        setCurrentFragment(leaderBoardFragment)
                    }
                }
                Log.e("ItemId", "Is ${item} is ${item?.itemId}")
                true
            }

        referrerClient = InstallReferrerClient.newBuilder(this).build()
        referrerClient.startConnection(object : InstallReferrerStateListener {

            override fun onInstallReferrerSetupFinished(responseCode: Int) {
                when (responseCode) {
                    InstallReferrerClient.InstallReferrerResponse.OK -> {
                        // Connection established.
                        val response: ReferrerDetails = referrerClient.installReferrer
                        val referrerUrl: String = response.installReferrer
                        val referrerClickTime: Long = response.referrerClickTimestampSeconds
                        val appInstallTime: Long = response.installBeginTimestampSeconds
                        val instantExperienceLaunched: Boolean = response.googlePlayInstantParam
                        Log.e("Referral Connected"," with response ${response}")
                    }
                    InstallReferrerClient.InstallReferrerResponse.FEATURE_NOT_SUPPORTED -> {
                        // API not available on the current Play Store app.
                    }
                    InstallReferrerClient.InstallReferrerResponse.SERVICE_UNAVAILABLE -> {
                        // Connection couldn't be established.
                    }
                }
            }

            override fun onInstallReferrerServiceDisconnected() {
                // Try to restart the connection on the next request to
                // Google Play by calling the startConnection() method.
            }
        })

        //Check for Notification Permission
        if ((ContextCompat.checkSelfPermission(
               this@MainActivity,
                Manifest.permission.POST_NOTIFICATIONS
            ) != PackageManager.PERMISSION_GRANTED)) {
            ActivityCompat.requestPermissions(
                this@MainActivity,
                arrayOf(Manifest.permission.POST_NOTIFICATIONS),
                200
            )
        }
        redirectNotification()
        }


    override fun onBackPressed() {
        if (bottomNavigationView.selectedItemId == R.id.item_home)
        {
            super.onBackPressed();
            finish();
        }
        else
        {
            bottomNavigationView.selectedItemId = R.id.item_home;
        }
    }
    inner class MyReceiver(handler: Handler) : BroadcastReceiver() {
        var handler: Handler = handler // Handler used to execute code on the UI thread
        override fun onReceive(context: Context?, intent: Intent?) {
            Log.e("Intent", "Action ${intent?.action}")
            handler.post {
                run {
                    if (intent?.action.equals(Constants.BACKPRESSED)){
                        bottomNavigationView?.selectedItemId = R.id.item_home;
                    }
                    else  if (intent?.action.equals(Constants.GO_TO_LEADERBORARD)){
                        bottomNavigationView?.selectedItemId = R.id.item_leaderboard;
                    }
                }
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        receiver?.let {
            unregisterReceiver(it)
        }

    }
    fun countDaysBetweenTwoCalendar(calendarStart: Calendar, calendarEnd: Calendar) : Int{
        val millionSeconds = calendarEnd.timeInMillis - calendarStart.timeInMillis
        val days = TimeUnit.MILLISECONDS.toDays(millionSeconds) //this way not round number
        val daysRounded = (millionSeconds / (1000.0 * 60 * 60 * 24)).roundToInt()
        return daysRounded
    }
    private fun showRateDialog() {
        val dialog = Dialog(this@MainActivity)
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.setCancelable(false)
        dialog.setContentView(R.layout.dialog_rate)
        dialog.getWindow()?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        var imgCancel = dialog.findViewById<ImageView>(R.id.imgCancel)
        var simpleRatingBar = dialog.findViewById<ScaleRatingBar>(R.id.simpleRatingBar)
        simpleRatingBar.setOnRatingChangeListener { ratingBar, rating, fromUser ->
            dialog.dismiss()
            justCopyItVIewModel.setStudentAppRating()
            val uri: Uri = Uri.parse("market://details?id=$packageName")
            val goToMarket = Intent(Intent.ACTION_VIEW, uri)
            // To count with Play market backstack, After pressing back button,
            // to taken back to our application, we need to add following flags to intent.
            goToMarket.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY or
                    Intent.FLAG_ACTIVITY_NEW_DOCUMENT or
                    Intent.FLAG_ACTIVITY_MULTIPLE_TASK)
            try {
                startActivity(goToMarket)
            } catch (e: ActivityNotFoundException) {
                startActivity(Intent(Intent.ACTION_VIEW,
                    Uri.parse("http://play.google.com/store/apps/details?id=$packageName")))
            }
        }
        imgCancel.setOnClickListener {
            dialog.dismiss()
        }
        dialog.show()
    }


    private fun setCurrentFragment(fragment: Fragment) =
        supportFragmentManager.beginTransaction().apply {
            replace(R.id.flFrame, fragment)
            this.commitAllowingStateLoss()
        }

    private fun underlineSelectedItem(view: View, itemId: Int) {
        val constraintLayout: ConstraintLayout = view as ConstraintLayout
        TransitionManager.beginDelayedTransition(constraintLayout)
        val constraintSet = ConstraintSet()
        constraintSet.clone(constraintLayout)
        constraintSet.setHorizontalBias(
            R.id.underline,
            getItemPosition(itemId) * 0.24f
        )
        constraintSet.applyTo(constraintLayout)
    }

    private fun getItemPosition(itemId: Int): Int {
        return when (itemId) {
            R.id.item_home -> 0
            R.id.item_subjects -> 1
            R.id.item_scan -> 2
            R.id.item_test -> 3
            R.id.item_leaderboard -> 4
            else -> 0
        }
    }


    fun redirectNotification()
    {
        Log.e("notificationType==3","=========${ Constants.notificationType}")
        when(Constants.notificationType)
        {
            notificationTypes.General.notificationType -> {
                // No Action
                //Listing - No action
                //Bar - Home Page
                Constants.notificationType = "500"
            }
            notificationTypes.UpdateApp.notificationType -> {
                openPlayStore()
                //Play Store
                Constants.notificationType = "500"
            }
            notificationTypes.VideoFinished.notificationType -> {
                // No Action
                //List - No action
                // Bar - Home Page
                Constants.notificationType = "500"
            }
            notificationTypes.CorrectAnswer.notificationType -> {
                // No Action
                //List -
                Constants.notificationType = "500"
            }
            notificationTypes.RateApp.notificationType -> {
                openPlayStore()
                Constants.notificationType = "500"
            }
            notificationTypes.ApplySubscription.notificationType -> {
                startActivity(ChooseYourSubscriptionActivity::class.java)
                Constants.notificationType = "500"
            }
            notificationTypes.LiveLectureStart.notificationType -> {
                startActivity(TodayLiveClassesActivity::class.java)
                Constants.notificationType = "500"
                //List - Live Latcure screen
            }
            notificationTypes.LiveLectureJoined.notificationType -> {
                startActivity(TodayLiveClassesActivity::class.java)
                Constants.notificationType = "500"
                //List - Live Latcure screen
            }
            notificationTypes.SubscriptionWillExpire.notificationType -> {
                startActivity(ChooseYourSubscriptionActivity::class.java)
                Constants.notificationType = "500"
            }
            notificationTypes.TodayTotalPoints.notificationType -> {
                sendBroadcast(Intent(Constants.GO_TO_LEADERBORARD))
                Constants.notificationType = "500"
            }
        }
    }


    private fun openPlayStore() {
        val uri: Uri = Uri.parse("market://details?id=$packageName")
        val goToMarket = Intent(Intent.ACTION_VIEW, uri)
        // To count with Play market backstack, After pressing back button,
        // to taken back to our application, we need to add following flags to intent.
        goToMarket.addFlags(
            Intent.FLAG_ACTIVITY_NO_HISTORY or
                    Intent.FLAG_ACTIVITY_NEW_DOCUMENT or
                    Intent.FLAG_ACTIVITY_MULTIPLE_TASK)
        try {
            startActivity(goToMarket)
        } catch (e: ActivityNotFoundException) {
            startActivity(
                Intent(
                    Intent.ACTION_VIEW,
                    Uri.parse("http://play.google.com/store/apps/details?id=$packageName"))
            )
        }
    }

}