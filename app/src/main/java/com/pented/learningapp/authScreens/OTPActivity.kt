package com.pented.learningapp.authScreens

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import android.os.CountDownTimer
import android.provider.Settings
import android.telephony.TelephonyManager
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.View
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModelProvider
import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.messaging.FirebaseMessaging
import com.pented.learningapp.MainActivity
import com.pented.learningapp.R
import com.pented.learningapp.authScreens.viewModel.VerifyOTPVM
import com.pented.learningapp.base.BaseActivity
import com.pented.learningapp.databinding.ActivityOtpactivityBinding
import com.pented.learningapp.helper.Constants
import com.pented.learningapp.helper.SharedPrefs
import com.pented.learningapp.helper.Utils
import com.pented.learningapp.multiLanguageSupport.LocaleManager
import kotlinx.android.synthetic.main.activity_otpactivity.*
import kotlinx.android.synthetic.main.transparent_progressbar_layout.*


class OTPActivity : BaseActivity<ActivityOtpactivityBinding>() {

    var timeStr = ""
    var uuid:String ? = null
    override fun viewModel() = ViewModelProvider(this).get(VerifyOTPVM::class.java)
    override fun layoutID() = R.layout.activity_otpactivity
    lateinit var verifyOTPVM: VerifyOTPVM
    private val readPhoneStatePermissionCode = 3

    override fun initActivity() {
        init()
        observer()
        listener()
    }

    private fun observer() {
        verifyOTPVM.observedErrorMessageChanges().observe(this, { event ->
            event?.getContentIfNotHandled()?.let {
                showErrorMessage(it, this, mainFrame)
            }
        })


        verifyOTPVM.observedChanges().observe(this, { event ->
            event?.getContentIfNotHandled()?.let {
                when (it) {
                    Constants.VISIBLE -> {
                        showDialog()
                    }
                    Constants.HIDE -> {
                        hideDialog()
                    }
                    Constants.SUCCESS -> {

                    }
                    Constants.NAVIGATE -> {
                        // startActivity( RegisterYourselfActivity::class.java)
                        if (verifyOTPVM.responseModel.data.IsNew == false) {
                            if (verifyOTPVM.responseModel.data.LanguageId == 1) {
                                LocaleManager.setNewLocale(
                                    this@OTPActivity,
                                    LocaleManager.GUJARATI
                                );
                                // setNewLocale(requireActivity(), LocaleManager.GUJARATI)
                            } else if (verifyOTPVM.responseModel.data.LanguageId == 2) {
                                LocaleManager.setNewLocale(
                                    this@OTPActivity,
                                    LocaleManager.HINDI
                                );
                                // setNewLocale(requireActivity(), LocaleManager.HINDI)
                            } else if (verifyOTPVM.responseModel.data.LanguageId == 3) {
                                LocaleManager.setNewLocale(
                                    this@OTPActivity,
                                    LocaleManager.ENGLISH
                                );
                                //  setNewLocale(requireActivity(), LocaleManager.ENGLISH)
                            }
                            Constants.notificationType = "500"
                            startActivity(MainActivity::class.java)
                            finishAffinity()
                        } else {
                            Constants.notificationType = "500"
                            startActivity(RegisterYourselfActivity::class.java)
                        }

                    }
                    else -> {
                        showMessage(it, this, mainFrame)
                    }
                }
            }
        })

    }

    private fun init() {
        verifyOTPVM = (getViewModel() as VerifyOTPVM)
        val permissionCheck =
            ContextCompat.checkSelfPermission(this, Manifest.permission.READ_PHONE_STATE)

        if (permissionCheck != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(
                this,
                arrayOf(Manifest.permission.READ_PHONE_STATE),
                readPhoneStatePermissionCode
            )
            uuid = getDeviceId(this@OTPActivity)
        } else {
            //TODO
        }

        if (intent != null) {
            tvPhoneNumber.text = "+91-" + intent.getStringExtra("phone_number")
        }
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
    }

    private fun listener() {
        llBack.setOnClickListener {
            onBackPressed()
        }
        llSignin.setOnClickListener{
            startActivity(YourSecurityQuestionActivity::class.java)
        }
        btnContinue.setOnClickListener{
            Constants.phoneNumber = intent.getStringExtra("phone_number").toString()
            Constants.OTP = otpView.text.toString()
            verifyOTPVM.getStartedRequestModel.MobileNumber = intent.getStringExtra("phone_number")
            verifyOTPVM.getStartedRequestModel.OTP = otpView.text.toString()
            verifyOTPVM.getStartedRequestModel.AppVersion = "1.0"
            verifyOTPVM.getStartedRequestModel.DeviceModel = android.os.Build.MODEL
            verifyOTPVM.getStartedRequestModel.FCMToken = SharedPrefs.getFcmToken(this@OTPActivity)
            verifyOTPVM.getStartedRequestModel.UUID = uuid
            verifyOTPVM.callVerifyOTP()

           }
        llResendOtp.setOnClickListener{

            verifyOTPVM.getStartedRequestModel.MobileNumber = intent.getStringExtra("phone_number")
            verifyOTPVM.callGetStartedAPI()

            llResendOtp.visibility = View.GONE
            tvSeconds.visibility = View.VISIBLE
            tvTimeRemaining.visibility = View.VISIBLE
            object : CountDownTimer(61000, 1000) {
                override fun onTick(millisUntilFinished: Long) {
                    tvSeconds.text =  ""+millisUntilFinished / 1000 + " s"
                }

                override fun onFinish() {

                    tvSeconds.visibility = View.INVISIBLE
                    tvTimeRemaining.visibility = View.INVISIBLE
                    llResendOtp.visibility = View.VISIBLE
                    timeStr = "Finished"
                }
            }.start()
        }
        object : CountDownTimer(61000, 1000) {
            override fun onTick(millisUntilFinished: Long) {
                tvSeconds.text =  ""+millisUntilFinished / 1000 + " s"
            }

            override fun onFinish() {

                tvSeconds.visibility = View.INVISIBLE
                tvTimeRemaining.visibility = View.INVISIBLE
                llResendOtp.visibility = View.VISIBLE
                timeStr = "Finished"
            }
        }.start()



        etOtp1.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                etOtp2.requestFocus()
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
            }
        })

        etOtp2.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                etOtp3.requestFocus()
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
            }
        })

        etOtp3.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                etOtp4.requestFocus()
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
            }
        })


    }

    @SuppressLint("MissingPermission")
    fun getDeviceId(context: Context): String? {
        val deviceId: String
        deviceId = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
          //  val tm = getSystemService(TELEPHONY_SERVICE) as TelephonyManager
            Settings.Secure.getString(
                context.contentResolver,
                Settings.Secure.ANDROID_ID
            )
        } else {

            val mTelephony = context.getSystemService(TELEPHONY_SERVICE) as TelephonyManager
            if (mTelephony.deviceId != null) {
                mTelephony.deviceId
            } else {
                Settings.Secure.getString(
                    context.contentResolver,
                    Settings.Secure.ANDROID_ID
                )
            }
        }
        return deviceId
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
}