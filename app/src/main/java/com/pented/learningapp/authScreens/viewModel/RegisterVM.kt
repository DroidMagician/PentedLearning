package com.pented.learningapp.authScreens.viewModel

import android.app.Application
import android.content.Context
import android.util.Log
import androidx.lifecycle.MutableLiveData
import com.amazonaws.mobileconnectors.s3.transferutility.TransferListener
import com.amazonaws.mobileconnectors.s3.transferutility.TransferState
import com.google.gson.Gson
import com.pented.learningapp.BuildConfig
import com.pented.learningapp.R
import com.pented.learningapp.amazonS3.S3Util
import com.pented.learningapp.authScreens.model.*
import com.pented.learningapp.base.BaseViewModel
import com.pented.learningapp.helper.Constants
import com.pented.learningapp.helper.Event
import com.pented.learningapp.helper.SharedPrefs
import com.pented.learningapp.helper.model.CommonResponseModel
import com.pented.learningapp.homeScreen.home.model.EarnPointResponseModel
import com.pented.learningapp.homeScreen.home.model.EarnPointsRequestModel
import com.pented.learningapp.myUtils.Keys
import com.pented.learningapp.retrofit.APITask
import com.pented.learningapp.retrofit.OnResponseListener
import com.pented.learningapp.retrofit.Retrofit
import java.io.File
import java.util.*


class RegisterVM(val context: Application) : BaseViewModel(context), OnResponseListener {
    var getDropdownResponseModel = GetDropdownResponseModel()
    var languagesResponseModel = GetLanguagesResponseModel()
    var getSchoolNameResponseModel = GetSchoolNameResponseModel()
    var registerRequestModel = RegisterRequestModel()
    var studentLocationRequestModel = AddLocationRequestModel()

    private var attachedAccount = MutableLiveData<Event<GetDropdownResponseModel>>()
    private fun setDropdownData(attachedAccount: GetDropdownResponseModel) {
        this.attachedAccount.postValue(Event(attachedAccount))
    }

    fun observerDropdownChange() = attachedAccount

    private var languagesList = MutableLiveData<Event<GetLanguagesResponseModel>>()
    private fun setLanguageData(attachedAccount: GetLanguagesResponseModel) {
        this.languagesList.postValue(Event(attachedAccount))
    }

    fun observerLanguageChange() = languagesList


    private var schoolNameList = MutableLiveData<Event<GetSchoolNameResponseModel>>()
    private fun setSchoolNameData(attachedAccount: GetSchoolNameResponseModel) {
        this.schoolNameList.postValue(Event(attachedAccount))
    }

    fun observerSchoolNameChange() = schoolNameList

    fun addPoints() {
        //setMessage(Constants.VISIBLE)
        var earnPointsRequestModel = EarnPointsRequestModel()
//        earnPointsRequestModel.ModuleId =
//            ChapterWithAnimation2Activity.mainTopicVideoId.toString()

        // initialize a Random object somewhere; you should only need one
        // initialize a Random object somewhere; you should only need one
        val random = Random()

// generate a random integer from 0 to 899, then add 100

// generate a random integer from 0 to 899, then add 100
        val x: Int = random.nextInt(50) + 100
        earnPointsRequestModel.PointType = "Registered"
        earnPointsRequestModel.Point = x.toString()
        Constants.registerPoints = x

        earnPointsRequestModel.VideoMinutes = "0"
        earnPointsRequestModel.VideoCompleted = null

        APITask.getInstance().addPoints(this, earnPointsRequestModel)?.let { mDisposable?.add(it) }
    }
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


     fun callGetDropdownList() {
         setMessage(Constants.VISIBLE)
         APITask.getInstance().getDropdownList(this)?.let { mDisposable?.add(it) }
    }

    fun setStudentLocation() {
        setMessage(Constants.VISIBLE)
        APITask.getInstance().setStudentLocation(this, studentLocationRequestModel)?.let { mDisposable?.add(
            it
        ) }
    }


    fun callSchoolNameList() {
        setMessage(Constants.VISIBLE)
        APITask.getInstance().getSchoolName(this)?.let { mDisposable?.add(it) }
    }

    fun callLanguageList() {
        setMessage(Constants.VISIBLE)
        APITask.getInstance().getLanguagesList(this)?.let { mDisposable?.add(it) }
    }

    fun callRegisterUser() {
        Log.e("registerRequestModel", "Is ==== ${Gson().toJson(registerRequestModel)}")
        Log.e("headerstandardid", "Is ==== ${Gson().toJson(Constants.headerstandardid)}")
        Log.e("headerlanguageid", "Is ==== ${Gson().toJson(Constants.headerlanguageid)}")

        if(isValid())
        {
            setMessage(Constants.VISIBLE)
            registerRequestModel.FCMToken = SharedPrefs.getFcmToken(context)
           APITask.getInstance().registerUser(this, registerRequestModel)?.let { mDisposable?.add(it) }
        }

    }
    private fun isValid(): Boolean {
        Log.e("Context", "Is $context")
        return if (registerRequestModel.Name.isNullOrBlank()) {
            setMessage(context.getString(R.string.enter_name))
            false
        }
        else if(Constants.headerlanguageid.isNullOrBlank())
        {
            setMessage(context.getString(R.string.select_language))
            false
        }
        else if(Constants.headerstandardid.isNullOrBlank())
        {
            setMessage(context.getString(R.string.select_standard))
            false
        }
//        else if(registerRequestModel.EmailId.isNullOrBlank())
//        {
//            setMessage(context.getString(R.string.enter_your_email_id))
//            false
//        }
        else if(registerRequestModel.Address.isNullOrBlank())
        {
            setMessage(context.getString(R.string.enter_address))
            false
        }
        else true
    }

    override fun <T> onResponseReceived(response: T, requestCode: Int) {
        setMessage(Constants.HIDE)
        if (response != null && requestCode == APITask.getDropDownList) {
            Log.e("Forgot", "Response :  ${Gson().toJson(response)}")
            getDropdownResponseModel = response as GetDropdownResponseModel
            setDropdownData(getDropdownResponseModel)
            //setMessage((response as GetDropdownResponseModel).message)
//            if((response as GetDropdownResponseModel).status == "200")
//            {
//
//            }
//            else{
//
//            }
        }
        else if (response != null && requestCode == APITask.getLanguages) {
            Log.e("Forgot", "Response :  ${Gson().toJson(response)}")
            languagesResponseModel = response as GetLanguagesResponseModel
            setLanguageData(languagesResponseModel)
            if((response as GetLanguagesResponseModel).status == "200")
            {
                callSchoolNameList()

            }
            else{
                setMessage((response as GetLanguagesResponseModel).message)
            }
        }
        else if (response != null && requestCode == APITask.getSchoolName) {
            Log.e("Forgot", "Response :  ${Gson().toJson(response)}")
            getSchoolNameResponseModel = response as GetSchoolNameResponseModel

            if((response as GetSchoolNameResponseModel).status == "200")
            {
                setSchoolNameData(getSchoolNameResponseModel)
                setMessage(Constants.NAVIGATE)
            }
            else{
                setMessage((response as GetSchoolNameResponseModel).message)
            }
        }
        else if(requestCode == APITask.setStudentLocation)
        {
            var  registerResponse = response as CommonResponseModel
            if((response as CommonResponseModel).status == "200")
            {
                //setMessage((response as CommonResponseModel).msg)
                addPoints()

            }
            else
            {
                setMessage((response as CommonResponseModel).msg)
            }
        }
        else if(requestCode == APITask.registerUser)
        {
            var  registerResponse = response as VerifyOTPResponseModel
            Log.e("Register", "Response :  ${Gson().toJson(registerResponse)}")
            Log.e(
                "Register",
                "Response : before ${registerResponse.data.AccessToken?.access_token}"
            )
            Log.e("Register", "Response : before pref ${SharedPrefs.getToken(context)}")
            SharedPrefs.storeToken(context, registerResponse.data.AccessToken?.access_token)
            Log.e("Register", "Response : after ${SharedPrefs.getToken(context)}")

            SharedPrefs.storeLoginDetail(context, registerResponse.data)
            Retrofit.init()
            if((response as VerifyOTPResponseModel).status == "200")
            {
                setMessage((response as VerifyOTPResponseModel).msg)
                Constants.headerstandardid = registerResponse.data?.StandardId?.toString()
                Constants.headerlanguageid = registerResponse.data?.LanguageId?.toString()
                setStudentLocation()
//                setMessage((response as VerifyOTPResponseModel).msg)
//                setMessage(Constants.SUCCESS)
            }
            else
            {
                setMessage((response as VerifyOTPResponseModel).msg)
            }
        }
        else if(requestCode == APITask.earnPoints)
        {
            var  registerResponse = response as EarnPointResponseModel
            if((response as EarnPointResponseModel).status == "200")
            {
                //setMessage((response as CommonResponseModel).msg)
                setMessage(Constants.SUCCESS)
            }
            else
            {
                setMessage((response as EarnPointResponseModel).msg)
            }
        }
    }
    fun uploadProfileImage(mSelectedMediaPath: String, imageFileName: String, context: Context) {
        val transferUtility = S3Util.getTransferUtility(context)

        val originalImageTransferUtility = transferUtility.upload(
            Keys.bucketName(true),
            "Images/StudentProfile/$imageFileName",
            File(mSelectedMediaPath)
        )

        originalImageTransferUtility.setTransferListener(object : TransferListener {
            override fun onStateChanged(id: Int, state: TransferState) {

                if (TransferState.COMPLETED == originalImageTransferUtility.state) {
                    Log.e("Upload ", "Complated")
                }
            }

            override fun onProgressChanged(id: Int, bytesCurrent: Long, bytesTotal: Long) {

            }

            override fun onError(id: Int, ex: Exception) {
                setMessage(Constants.HIDE)
                setMessage("Something went wrong. Please try again later!")
            }
        })
    }
    override fun onResponseError(message: String, requestCode: Int, responseCode: Int) {

        setErrorMessage(message)
        setMessage(Constants.HIDE)
    }
}