package com.pented.learningapp.homeScreen.home.otherUserProfile.viewModel

import android.app.Application
import android.util.Log
import androidx.lifecycle.MutableLiveData
import com.pented.learningapp.helper.Event
import com.pented.learningapp.retrofit.APITask
import com.pented.learningapp.retrofit.OnResponseListener
import com.google.gson.Gson
import com.pented.learningapp.authScreens.model.GetDropdownResponseModel
import com.pented.learningapp.authScreens.model.GetLanguagesResponseModel
import com.pented.learningapp.authScreens.model.VerifyOTPResponseModel
import com.pented.learningapp.base.BaseViewModel
import com.pented.learningapp.helper.Constants
import com.pented.learningapp.helper.SharedPrefs
import com.pented.learningapp.helper.Utils
import com.pented.learningapp.homeScreen.home.otherUserProfile.model.GetOtherStudentResponseModel


class OtherUserProfileVM(val context: Application) : BaseViewModel(context), OnResponseListener {
    private var messageString = MutableLiveData<Event<String>>()
    private fun setMessage(msg: String) {
        messageString.value = Event(msg)
    }
    var languagesResponseModel = GetLanguagesResponseModel()

    var getDropdownResponseModel = GetDropdownResponseModel()
    fun observedChanges() = messageString
    private var errorMessageString = MutableLiveData<Event<String>>()
    private fun setErrorMessage(msg: String) {
        errorMessageString.value = Event(msg)
    }
    private var attachedAccount = MutableLiveData<Event<GetDropdownResponseModel>>()
    private fun setDropdownData(attachedAccount: GetDropdownResponseModel) {
        this.attachedAccount.postValue(Event(attachedAccount))
    }

    fun observerDropdownChange() = attachedAccount
    private var languagesList = MutableLiveData<Event<GetLanguagesResponseModel>>()
    private fun setLanguageData(attachedAccount: GetLanguagesResponseModel) {
        this.languagesList.postValue(Event(attachedAccount))
    }
    fun callGetOtherStudentProfile(studentId:String) {
        //setMessage(Constants.VISIBLE)
        APITask.getInstance().getOtherStudentProfile(this,studentId)?.let { mDisposable?.add(it) }
    }
    fun observerLanguageChange() = languagesList


    private var OtherstudentProfileData = MutableLiveData<Event<GetOtherStudentResponseModel>>()
    private fun setOtherStudentProfile(msg: GetOtherStudentResponseModel) {
        OtherstudentProfileData.value = Event(msg)
    }

    fun observedStudentProfileData() = OtherstudentProfileData


    fun observedErrorMessageChanges() = errorMessageString




//    private fun isValid(): Boolean {
//        Log.e("Context", "Is $context")
//        return if (getStartedRequestModel.MobileNumber.isNullOrBlank()) {
//            setMessage(context.getString(R.string.enter_phon_number))
//            false
//        }
//        else if(getStartedRequestModel?.MobileNumber?.length!! < 10)
//        {
//            setMessage(context.getString(R.string.enter_valid_phon_number))
//            false
//        }
//        else true
//    }

    override fun <T> onResponseReceived(response: T, requestCode: Int) {
      //  setMessage(Constants.HIDE)
        if (response != null && requestCode == APITask.getOtherStudentProfile) {
            Log.e("Forgot", "Response :  ${Gson().toJson(response)}")
            setMessage(Constants.HIDE)
            if((response as GetOtherStudentResponseModel).status == "200")
            {
                setOtherStudentProfile((response as GetOtherStudentResponseModel))

            }
            else
            {
                setMessage((response as GetOtherStudentResponseModel).msg)
            }
        }

    }

    override fun onResponseError(message: String, requestCode: Int, responseCode: Int) {

        setErrorMessage(message)
        setMessage(Constants.HIDE)
    }
}