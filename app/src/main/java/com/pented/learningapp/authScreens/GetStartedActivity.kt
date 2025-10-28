package com.pented.learningapp.authScreens

import android.Manifest
import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import android.net.Uri
import android.os.AsyncTask
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModelProvider
import com.amazonaws.services.s3.model.GeneratePresignedUrlRequest
import com.pented.learningapp.MainActivity
import com.pented.learningapp.R
import com.pented.learningapp.amazonS3.S3Util
import com.pented.learningapp.authScreens.viewModel.GetStartedVM
import com.pented.learningapp.base.BaseActivity
import com.pented.learningapp.databinding.ActivityGetStartedBinding
import com.pented.learningapp.enum.notificationTypes
import com.pented.learningapp.helper.Constants
import com.pented.learningapp.helper.SharedPrefs
import com.pented.learningapp.helper.Utils
import com.pented.learningapp.homeScreen.home.liveClasses.TodayLiveClassesActivity
import com.pented.learningapp.homeScreen.subscription.ChooseYourSubscriptionActivity
import com.pented.learningapp.multiLanguageSupport.LocaleManager
import kotlinx.android.synthetic.main.activity_get_started.*
import kotlinx.android.synthetic.main.transparent_progressbar_layout.*
import java.net.URL
import java.util.*
import java.util.concurrent.Executors


class GetStartedActivity : BaseActivity<ActivityGetStartedBinding>(),
    LocationListener {
    override fun viewModel() = ViewModelProvider(this).get(GetStartedVM::class.java)
    override fun layoutID() = R.layout.activity_get_started
    lateinit var getStatedVM: GetStartedVM
    private lateinit var locationManager: LocationManager
    private val locationPermissionCode = 2

    private val readPhoneStatePermissionCode = 3
    companion object{
        var currentLatitude:Double = -1.0
        var currentLongitude:Double = -1.0
    }
    override fun initActivity() {
        getStatedVM = (getViewModel() as GetStartedVM)
        Constants.popupCount = 0
        init()
        observer()
        listener()
    }
    override fun onLocationChanged(location: Location) {

        Log.e("Location", "Latitude: " + location.latitude + " , Longitude: " + location.longitude)
    }

    override fun onStatusChanged(provider: String?, status: Int, extras: Bundle?) {}
    override fun onProviderDisabled(provider: String) {
       // super.onProviderDisabled(provider)
    }

    override fun onProviderEnabled(provider: String) {
       // super.onProviderEnabled(provider)
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == locationPermissionCode) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                locationManager = getSystemService(Context.LOCATION_SERVICE) as LocationManager
                if ((ContextCompat.checkSelfPermission(
                        this@GetStartedActivity,
                        Manifest.permission.ACCESS_FINE_LOCATION
                    ) != PackageManager.PERMISSION_GRANTED)) {
                    ActivityCompat.requestPermissions(
                        this@GetStartedActivity,
                        arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                        locationPermissionCode
                    )
                }
                locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 5000, 5f, this)
            }
            else {

            }
        }
    }
    private fun observer() {
        getStatedVM.observedErrorMessageChanges().observe(this, { event ->
            event?.getContentIfNotHandled()?.let {
                showErrorMessage(it, this, mainFrame)
            }
        })


        getStatedVM.observedChanges().observe(this, { event ->
            event?.getContentIfNotHandled()?.let {
                when (it) {
                    Constants.VISIBLE -> {
                        showDialog()
                    }
                    Constants.HIDE -> {
                        hideDialog()
                    }
                    Constants.NAVIGATE -> {
                        startActivityWithDataKey(
                            OTPActivity::class.java,
                            etPhone.text.toString(),
                            "phone_number"
                        )
                    }
                    else -> {
                        showMessage(it, this, mainFrame)
                    }
                }
            }
        })



    }


    private fun init() {
        if(intent.hasExtra("Type"))
        {
            var type = intent?.getStringExtra("Type")
            if (type != null) {
                Constants.notificationType = type
            }
            Log.e("Type","Value $type")
            Log.e("notificationType==5","=========${ Constants.notificationType}")
        }

//        var deviceId: String? = ""
//        deviceId = if (Build.VERSION.SDK_INT >= 26) {
//            getSystemService(TelephonyManager::class.java).imei
//        } else {
//            getSystemService(TelephonyManager::class.java).deviceId
//        }
//        val tManager = getSystemService(Context.TELEPHONY_SERVICE) as TelephonyManager
//        val uuid = tManager.deviceId
        var loginData = SharedPrefs.getLoginDetail(this@GetStartedActivity)

        locationManager =getSystemService(Context.LOCATION_SERVICE) as LocationManager
        if ((ContextCompat.checkSelfPermission(
                this@GetStartedActivity,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED)) {
            ActivityCompat.requestPermissions(
                this@GetStartedActivity,
                arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                locationPermissionCode
            )
        }
        else
        {
            locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 5000, 5f, this)
        }

        if ((ContextCompat.checkSelfPermission(
                this@GetStartedActivity,
                Manifest.permission.READ_PHONE_STATE
            ) != PackageManager.PERMISSION_GRANTED)) {
            ActivityCompat.requestPermissions(
                this@GetStartedActivity,
                arrayOf(Manifest.permission.READ_PHONE_STATE),
                readPhoneStatePermissionCode
            )
        }

        if(loginData != null)
        {
            Log.e("Login data", "Is" + loginData.Name)
            Log.e("Login data", "Is" + loginData.MobileNumber)

            if (loginData.LanguageId == 1) {
                LocaleManager.setNewLocale(
                    this@GetStartedActivity,
                    LocaleManager.GUJARATI
                );
                // setNewLocale(requireActivity(), LocaleManager.GUJARATI)
            } else if (loginData.LanguageId == 2) {
                LocaleManager.setNewLocale(
                    this@GetStartedActivity,
                    LocaleManager.HINDI
                );
                // setNewLocale(requireActivity(), LocaleManager.HINDI)
            } else if (loginData.LanguageId == 3) {
                LocaleManager.setNewLocale(
                    this@GetStartedActivity,
                    LocaleManager.ENGLISH
                );
                //  setNewLocale(requireActivity(), LocaleManager.ENGLISH)
            }
            startActivity(MainActivity::class.java)
            finishAffinity()
        }
        Utils.setScreenViewAnalytics(
            getString(R.string.get_started),
            "GetStartedActivity"
        )
        Executors.newSingleThreadExecutor().submit(Runnable {
            // You can perform your task here.
            // Log.e("UUID", "Is ${getDeviceId(this@GetStartedActivity)}")
            var s3Client = S3Util.getS3Client()
            var objKey: String? = null
            val ol = s3Client.listObjects("pentedapp")
            for (objectSummary in ol.objectSummaries) {
                println(objectSummary.key)
                objKey = objectSummary.key
                // Log.e("Object is", "Here ${objectSummary.key}")
            }


            var cal = GregorianCalendar.getInstance();
            cal.setTime(Date());
            cal.add(Calendar.DAY_OF_YEAR, +7);
            var daysBeforeDate = cal.getTime();


            val request = GeneratePresignedUrlRequest(
                "pentedapp",
                "Images/StudentProfile/cropped7884368680272099885.jpg"
            )
            request.expiration = daysBeforeDate
            val objectURL: URL = s3Client.generatePresignedUrl(request)
            Log.e("Final URL is Started", "Here $objectURL")
        })


    }

     class UpdateTask : AsyncTask<String?, String?, String?>() {
         override fun doInBackground(vararg params: String?): String? {

            // val s3Client = AmazonS3Client()
//             val request = GetObjectRequest("pentedsolutionvideos", objKey)
//             //request.withRange(0, numberOfBytesToGet)
//             val s3Object = s3Client.getObject(request)
//             Log.e("Stream Object is", "Here ${s3Object.getObjectContent()}")
//s3Object.getObjectContent() has a stream to your object.
             return null;
         }
     }

    private fun listener() {
        btnContinue.setOnClickListener {
           // startActivityWithDataKey(OTPActivity::class.java,etPhone.text.toString(),"phone_number")

            Constants.headerlanguageid = null
            Constants.headerstandardid = null

            if ((ContextCompat.checkSelfPermission(
                    this@GetStartedActivity,
                    Manifest.permission.ACCESS_FINE_LOCATION
                ) == PackageManager.PERMISSION_GRANTED)) {
                val myLocation = locationManager.getLastKnownLocation(LocationManager.PASSIVE_PROVIDER)

                myLocation?.latitude.let {
                    if (it != null) {
                        currentLatitude = it
                    }
                }
                myLocation?.longitude.let {
                    if (it != null) {
                        currentLongitude = it
                    }
                }
            }

             getStatedVM.callGetStartedAPI()
        }
        llSignin.setOnClickListener {
            startActivity(YourSecurityQuestionActivity::class.java)
        }
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

    override fun onNewIntent(intent: Intent?) {
        super.onNewIntent(intent)
        Log.e("On","New Intent")
        var extras = intent?.extras
        if(extras == null)
            return
        var type = extras.getString("Type")
        if (type != null) {
            Constants.notificationType = type
        }
        Log.e("Type","Value123 $type")
        var loginData = SharedPrefs.getLoginDetail(this@GetStartedActivity)
        Log.e("notificationType==2","=========${ Constants.notificationType}")
        if(loginData == null)
        {
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
                    Constants.notificationType = "500"
                    //Play Store
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
                  //  startActivity(ChooseYourSubscriptionActivity::class.java)
                    Constants.notificationType = "500"
                }
                notificationTypes.LiveLectureStart.notificationType -> {
                    //startActivity(TodayLiveClassesActivity::class.java)
                    //List - Live Latcure screen
                    Constants.notificationType = "500"
                }
                notificationTypes.LiveLectureJoined.notificationType -> {
                   // startActivity(TodayLiveClassesActivity::class.java)
                    //List - Live Latcure screen
                    Constants.notificationType = "500"
                }
                notificationTypes.SubscriptionWillExpire.notificationType -> {
                 //   startActivity(ChooseYourSubscriptionActivity::class.java)
                    Constants.notificationType = "500"
                }
                notificationTypes.TodayTotalPoints.notificationType -> {
                //    sendBroadcast(Intent(Constants.GO_TO_LEADERBORARD))
                    Constants.notificationType = "500"
                }
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