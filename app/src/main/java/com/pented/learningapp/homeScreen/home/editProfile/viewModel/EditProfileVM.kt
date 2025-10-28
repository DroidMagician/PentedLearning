package com.pented.learningapp.homeScreen.home.editProfile.viewModel

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
import com.pented.learningapp.helper.model.CommonRequestModel
import com.pented.learningapp.helper.model.CommonResponseModel


class EditProfileVM(val context: Application) : BaseViewModel(context), OnResponseListener {
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
    private var isAccountDeleted = MutableLiveData<Event<String>>()

    private fun isAccountDeleted(msg: String) {
        isAccountDeleted.value = Event(msg)
    }

    fun observedChangesForDeleteAccount() = isAccountDeleted

    fun observerDropdownChange() = attachedAccount
    private var languagesList = MutableLiveData<Event<GetLanguagesResponseModel>>()
    private fun setLanguageData(attachedAccount: GetLanguagesResponseModel) {
        this.languagesList.postValue(Event(attachedAccount))
    }
    fun callLanguageList() {
        //setMessage(Constants.VISIBLE)
        APITask.getInstance().getLanguagesList(this)?.let { mDisposable?.add(it) }
    }
    fun observerLanguageChange() = languagesList

    fun callGetDropdownList() {
        setMessage(Constants.VISIBLE)
        APITask.getInstance().getDropdownList(this)?.let { mDisposable?.add(it) }
    }

    private var studentProfileData = MutableLiveData<Event<VerifyOTPResponseModel>>()
    private fun setStudentProfile(msg: VerifyOTPResponseModel) {
        studentProfileData.value = Event(msg)
    }
    fun deleteAccount() {
        setMessage(Constants.VISIBLE)
        var commonRequestModel = CommonRequestModel()
        APITask.getInstance().deleteAccount(this,commonRequestModel)?.let { mDisposable?.add(it) }
    }
    fun observedStudentProfileData() = studentProfileData


    fun observedErrorMessageChanges() = errorMessageString


     fun callGetProfileData() {
    //     setMessage(Constants.VISIBLE)

//        Log.e("Forgot", "Model ${Gson().toJson(forgotReqModel)}")
         APITask.getInstance().getStudentProfile(this)?.let { mDisposable?.add(it) }
    }

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
        if (response != null && requestCode == APITask.getStudentProfile) {
            Log.e("Forgot", "Response :  ${Gson().toJson(response)}")
            setMessage(Constants.HIDE)
            if((response as VerifyOTPResponseModel).status == "200")
            {
                SharedPrefs.storeLoginDetail(context,(response as VerifyOTPResponseModel).data)
                setStudentProfile((response as VerifyOTPResponseModel))

            }
            else
            {
                setMessage((response as VerifyOTPResponseModel).msg)
            }
        }
        else if (response != null && requestCode == APITask.getDropDownList) {
            Log.e("Forgot", "Response :  ${Gson().toJson(response)}")
            getDropdownResponseModel = response as GetDropdownResponseModel
            setDropdownData(getDropdownResponseModel)
            if((response as GetDropdownResponseModel).status == "200")
            {

                callLanguageList()
            }
            else{
                setMessage((response as GetDropdownResponseModel).message)
            }
        }
        else if (response != null && requestCode == APITask.getLanguages) {
            Log.e("Forgot", "Response :  ${Gson().toJson(response)}")
            languagesResponseModel = response as GetLanguagesResponseModel
            setLanguageData(languagesResponseModel)
            if((response as GetLanguagesResponseModel).status == "200")
            {
                setMessage(Constants.NAVIGATE)
            }
            else{
                setMessage((response as GetLanguagesResponseModel).message)
            }
        }
        else if (response != null && requestCode == APITask.deleteAccount) {
            Log.e("DeleteAccount", "Response :  ${Gson().toJson(response)}")
//            isAccountDeleted(true)
            isAccountDeleted(Constants.HIDE)
            if((response as CommonResponseModel).status == "200")
            {
                Log.e("DeleteAccount","Deleted")
                isAccountDeleted(response.msg)
                isAccountDeleted(Constants.NAVIGATE)
            }
            else{
                Log.e("DeleteAccount","Issue")
                isAccountDeleted(response.msg)
            }
        }
    }

    override fun onResponseError(message: String, requestCode: Int, responseCode: Int) {

        setErrorMessage(message)
        setMessage(Constants.HIDE)
    }
}