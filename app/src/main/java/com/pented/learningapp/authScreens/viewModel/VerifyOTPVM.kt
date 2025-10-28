package com.pented.learningapp.authScreens.viewModel

import android.app.Application
import android.util.Log
import androidx.lifecycle.MutableLiveData
import com.google.gson.Gson
import com.pented.learningapp.helper.Event
import com.pented.learningapp.retrofit.APITask
import com.pented.learningapp.retrofit.OnResponseListener
import com.pented.learningapp.R
import com.pented.learningapp.authScreens.GetStartedActivity
import com.pented.learningapp.authScreens.model.AddLocationRequestModel
import com.pented.learningapp.authScreens.model.GetStartedRequestModel
import com.pented.learningapp.authScreens.model.VerifyOTPResponseModel
import com.pented.learningapp.base.BaseViewModel
import com.pented.learningapp.helper.Constants
import com.pented.learningapp.helper.SharedPrefs
import com.pented.learningapp.helper.Utils
import com.pented.learningapp.helper.model.CommonResponseModel


class VerifyOTPVM(val context: Application) : BaseViewModel(context), OnResponseListener {
    var getStartedRequestModel = GetStartedRequestModel()
    var responseModel = VerifyOTPResponseModel()
    var studentLocationRequestModel = AddLocationRequestModel()

    private var messageString = MutableLiveData<Event<String>>()
    private fun setMessage(msg: String) {
        messageString.value = Event(msg)
    }

    fun observedChanges() = messageString
    private var errorMessageString = MutableLiveData<Event<String>>()
    private fun setErrorMessage(msg: String) {
        errorMessageString.value = Event(msg)
    }

    fun observedErrorMessageChanges() = errorMessageString

    fun callGetStartedAPI() {
        setMessage(Constants.VISIBLE)

//        Log.e("Forgot", "Model ${Gson().toJson(forgotReqModel)}")
        APITask.getInstance().callGetStared(this, getStartedRequestModel)?.let { mDisposable?.add(it) }

    }

    fun setStudentLocation() {
        setMessage(Constants.VISIBLE)
        studentLocationRequestModel.Latitude = GetStartedActivity.currentLatitude?.toString().toString()
        studentLocationRequestModel.Longitude = GetStartedActivity.currentLongitude?.toString().toString()
        APITask.getInstance().setStudentLocation(this,studentLocationRequestModel)?.let { mDisposable?.add(it) }
    }
     fun callVerifyOTP() {
      if(isValid())
      {
          setMessage(Constants.VISIBLE)
          getStartedRequestModel.FCMToken = SharedPrefs.getFcmToken(context)
          APITask.getInstance().callVerifyOTP(this, getStartedRequestModel)?.let { mDisposable?.add(it) }
      }
    }

    private fun isValid(): Boolean {
        Log.e("Context", "Is $context")
        return if (getStartedRequestModel.OTP.isNullOrBlank()) {
            setMessage(context.getString(R.string.enter_OTP))
            false
        }
        else if(getStartedRequestModel?.OTP?.length!! < 4)
        {
            setMessage(context.getString(R.string.enter_valid_OTP))
            false
        }
        else true
    }

    override fun <T> onResponseReceived(response: T, requestCode: Int) {
        setMessage(Constants.HIDE)
        if (response != null && requestCode == APITask.verifyOTP) {
            var responseMOdel = (response as VerifyOTPResponseModel)

            if((response as VerifyOTPResponseModel).status == "200")
            {
                Log.e("Forgot", "Response :  ${responseMOdel.data.AccessToken?.access_token}")
                setMessage((response as VerifyOTPResponseModel).msg)
                SharedPrefs.storeToken(context,responseMOdel.data.AccessToken?.access_token)
                if(response.data.IsNew == false)
                {
                    SharedPrefs.storeLoginDetail(context,responseMOdel.data)
                    Constants.headerstandardid = responseMOdel.data?.StandardId?.toString()
                    Constants.headerlanguageid = responseMOdel.data?.LanguageId?.toString()
                }
                responseModel = responseMOdel
                if(responseMOdel.data?.IsNew == true)
                {
                    setMessage(Constants.NAVIGATE)
                }
                else
                {
                    setStudentLocation()
                }
                Constants.registerPoints = responseMOdel.data?.EarnedPoints?.toInt()
            }
            else
            {
                setMessage((response as VerifyOTPResponseModel).msg)
            }

        }
        else if(requestCode == APITask.setStudentLocation)
        {
            var  registerResponse = response as CommonResponseModel
            if((response as CommonResponseModel).status == "200")
            {
                //setMessage((response as CommonResponseModel).msg)
                setMessage(Constants.NAVIGATE)
            }
            else
            {
                setMessage((response as CommonResponseModel).msg)
            }
        }
        else if (response != null && requestCode == APITask.getStarted) {
            Log.e("Forgot", "Response :  ${Gson().toJson(response)}")
            setMessage((response as CommonResponseModel).msg)
        }
    }

    override fun onResponseError(message: String, requestCode: Int, responseCode: Int) {

        setErrorMessage(message)
        setMessage(Constants.HIDE)
    }
}