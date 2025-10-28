package com.pented.learningapp

import android.app.Activity
import android.app.Application
import android.content.Context
import android.content.res.Configuration
import android.os.Bundle
import android.provider.Settings
import android.util.Log
import android.view.WindowManager
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleObserver
import androidx.lifecycle.OnLifecycleEvent
import androidx.lifecycle.ProcessLifecycleOwner
import com.facebook.FacebookSdk
import com.facebook.LoggingBehavior
import com.facebook.appevents.AppEventsConstants.EVENT_NAME_ACTIVATED_APP
import com.facebook.appevents.AppEventsLogger
import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.messaging.FirebaseMessaging
import com.pented.learningapp.helper.AppSignatureHelper
import com.pented.learningapp.helper.Constants
import com.pented.learningapp.helper.SharedPrefs
import com.pented.learningapp.multiLanguageSupport.LocaleManager
import com.pented.learningapp.retrofit.Retrofit

import java.util.*


@Suppress("DEPRECATION")
class MyApplication: Application(), LifecycleObserver {
    companion object Singleton {
         lateinit var app: MyApplication
        fun getInstance(): MyApplication {
            return app
        }
    }

    override fun onCreate() {
        super.onCreate()
        app = this
        Constants.headerappversion = BuildConfig.VERSION_NAME
        FacebookSdk.setAutoInitEnabled(true)
        FacebookSdk.sdkInitialize(this.getApplicationContext());
        FacebookSdk.fullyInitialize();
        FacebookSdk.setAdvertiserIDCollectionEnabled(true);
        AppEventsLogger.activateApp(this);
        val logger = AppEventsLogger.newLogger(this)
        FacebookSdk.setAutoLogAppEventsEnabled(true);
//        FacebookSdk.setIsDebugEnabled(true);
        FacebookSdk.addLoggingBehavior(LoggingBehavior.APP_EVENTS);
        logger.logEvent(EVENT_NAME_ACTIVATED_APP)
        logger.logEvent("App_Install");

        if (BuildConfig.DEBUG) {
            //It's not a release version.
            //Do nothing
        }
        else{
            setupActivityListener()
        }

        Constants.headerdevicemodel = android.os.Build.MODEL
        var studentObj = SharedPrefs.getLoginDetail(this)
        Constants.headerstandardid = studentObj?.StandardId?.toString()
        Constants.headerlanguageid = studentObj?.LanguageId?.toString()
        Constants.headerdeviceUUID = getDeviceId(this)
        Retrofit.init()

        val appSignatureHelper = AppSignatureHelper(this)
        appSignatureHelper.getAppSignatures()
        FirebaseMessaging.getInstance().token.addOnCompleteListener(OnCompleteListener { task ->
            if (!task.isSuccessful) {
                Log.e("TAG", "Fetching FCM registration token failed", task.exception)
                return@OnCompleteListener
            }

            // Get new FCM registration token
            val token = task.result

            // Log and toast
            // val msg = getString(R.string.msg_token_fmt, token)
            Log.e("token--", token)
            SharedPrefs.storeFcmToken(baseContext, token.toString())
            //Toast.makeText(baseContext, msg, Toast.LENGTH_SHORT).show()
        })
        // AppEventsLogger.activateApp(this);

        ProcessLifecycleOwner.get().lifecycle.addObserver(this)
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_START)
    fun onMoveToForeground() { // app moved to foreground
        Log.e("AppLifeCycle", "Comes In Forgroud")
    }

    private fun setupActivityListener() {
        registerActivityLifecycleCallbacks(object : ActivityLifecycleCallbacks {
            override fun onActivityCreated(activity: Activity, savedInstanceState: Bundle?) {
//                activity.window.setFlags(
//                    WindowManager.LayoutParams.FLAG_SECURE,
//                    WindowManager.LayoutParams.FLAG_SECURE
//                )
            }

            override fun onActivityStarted(activity: Activity) {}
            override fun onActivityResumed(activity: Activity) {}
            override fun onActivityPaused(activity: Activity) {}
            override fun onActivityStopped(activity: Activity) {}
            override fun onActivitySaveInstanceState(activity: Activity, outState: Bundle) {}
            override fun onActivityDestroyed(activity: Activity) {}
        })
    }


    @OnLifecycleEvent(Lifecycle.Event.ON_STOP)
    fun onMoveToBackground() { // app moved to background
        Log.e("AppLifeCycle", "Comes In Backgroud")
    }
    override fun attachBaseContext(base: Context?) {
        super.attachBaseContext(base?.let { LocaleManager.setLocale(it) })
       // MultiDex.install(this)
    }
    fun getDeviceId(context: Context): String? {
        var deviceId: String ? = null
        deviceId =  Settings.Secure.getString(
            context.getContentResolver(),
            Settings.Secure.ANDROID_ID
        )
        //1f969c393b9d5281
//        val teleManager = getSystemService(TELEPHONY_SERVICE) as TelephonyManager
//        var tmSerial = teleManager.simSerialNumber
//        var tmDeviceId = teleManager.deviceId
//        var androidId =
//            Settings.Secure.getString(contentResolver, Settings.Secure.ANDROID_ID)
//        if (tmSerial == null) tmSerial = "1"
//        if (tmDeviceId == null) tmDeviceId = "1"
//        if (androidId == null) androidId = "1"
//        val deviceUuid = UUID(
//            androidId.hashCode().toLong(),
//            tmDeviceId.hashCode().toLong() shl 32 or tmSerial.hashCode()
//                .toLong()
//        )
//        deviceId =   deviceUuid.toString()

//        deviceId = if (Build.VERSION.SDK_INT >= 26) {
//            getSystemService(TelephonyManager::class.java).deviceId
//        } else {
//            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M) {
//                getSystemService(TelephonyManager::class.java).deviceId
//            } else {
//                val teleManager = getSystemService(TELEPHONY_SERVICE) as TelephonyManager
//                var tmSerial = teleManager.simSerialNumber
//                var tmDeviceId = teleManager.deviceId
//                var androidId =
//                    Settings.Secure.getString(contentResolver, Settings.Secure.ANDROID_ID)
//                if (tmSerial == null) tmSerial = "1"
//                if (tmDeviceId == null) tmDeviceId = "1"
//                if (androidId == null) androidId = "1"
//                val deviceUuid = UUID(
//                    androidId.hashCode().toLong(),
//                    tmDeviceId.hashCode().toLong() shl 32 or tmSerial.hashCode()
//                        .toLong()
//                )
//                deviceUuid.toString()
//            }
//        }
        return deviceId
    }
    override fun onConfigurationChanged(newConfig: Configuration) {
        super.onConfigurationChanged(newConfig)
        LocaleManager.setLocale(this)
    }
//    companion object {
//        lateinit var instance : MyApplication
//    }
}