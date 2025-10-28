package com.pented.learningapp.authScreens.viewModel

import android.app.Application
import android.util.Log
import androidx.lifecycle.MutableLiveData
import com.pented.learningapp.helper.Event
import com.pented.learningapp.retrofit.APITask
import com.pented.learningapp.retrofit.OnResponseListener
import com.google.gson.Gson
import com.pented.learningapp.R
import com.pented.learningapp.authScreens.model.GetStartedRequestModel
import com.pented.learningapp.base.BaseViewModel
import com.pented.learningapp.helper.Constants
import com.pented.learningapp.helper.SharedPrefs
import com.pented.learningapp.helper.Utils
import com.pented.learningapp.helper.model.CommonResponseModel


class GetStartedVM(val context: Application) : BaseViewModel(context), OnResponseListener {
    var getStartedRequestModel = GetStartedRequestModel()

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
      if(isValid())
      {
          setMessage(Constants.VISIBLE)
          getStartedRequestModel.FCMToken = SharedPrefs.getFcmToken(context)
//        Log.e("Forgot", "Model ${Gson().toJson(forgotReqModel)}")
          APITask.getInstance().callGetStared(this, getStartedRequestModel)?.let { mDisposable?.add(it) }
      }
    }

    private fun isValid(): Boolean {
        Log.e("Context", "Is $context")
        return if (getStartedRequestModel.MobileNumber.isNullOrBlank()) {
            setMessage(context.getString(R.string.enter_phon_number))
            false
        }
        else if(getStartedRequestModel?.MobileNumber?.length!! < 10)
        {
            setMessage(context.getString(R.string.enter_valid_phon_number))
            false
        }
        else true
    }

    override fun <T> onResponseReceived(response: T, requestCode: Int) {
        setMessage(Constants.HIDE)
        if (response != null && requestCode == APITask.getStarted) {
            Log.e("Forgot", "Response :  ${Gson().toJson(response)}")
            setMessage((response as CommonResponseModel).msg)
            if((response as CommonResponseModel).status == "200")
            {
                setMessage(Constants.NAVIGATE)
            }
        }
    }

    override fun onResponseError(message: String, requestCode: Int, responseCode: Int) {

        setErrorMessage(message)
        //setErrorMessage(message)
        setMessage(Constants.HIDE)
    }
}