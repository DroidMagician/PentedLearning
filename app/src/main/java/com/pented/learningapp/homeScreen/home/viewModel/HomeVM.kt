package com.pented.learningapp.homeScreen.home.viewModel

import android.app.Application
import android.util.Log
import androidx.lifecycle.MutableLiveData
import com.google.gson.Gson
import com.pented.learningapp.authScreens.model.GetDropdownResponseModel
import com.pented.learningapp.authScreens.model.GetSchoolNameResponseModel
import com.pented.learningapp.helper.Event
import com.pented.learningapp.retrofit.APITask
import com.pented.learningapp.retrofit.OnResponseListener
import com.pented.learningapp.authScreens.model.GetStartedRequestModel
import com.pented.learningapp.authScreens.model.VerifyOTPResponseModel
import com.pented.learningapp.base.BaseViewModel
import com.pented.learningapp.helper.Constants
import com.pented.learningapp.helper.SharedPrefs
import com.pented.learningapp.helper.Utils
import com.pented.learningapp.helper.model.CommonResponseModel
import com.pented.learningapp.homeScreen.home.editProfile.model.UpdateProfileRequestModel
import com.pented.learningapp.homeScreen.home.model.GetHomeDataResponseModel
import com.pented.learningapp.homeScreen.leaderboard.model.GetLeaderboardRequestModel
import com.pented.learningapp.homeScreen.leaderboard.model.GetLeaderboardResponseModel


class HomeVM(val context: Application) : BaseViewModel(context), OnResponseListener {
    var getStartedRequestModel = GetStartedRequestModel()
    var responseModel = VerifyOTPResponseModel()
    var registerRequestModel = UpdateProfileRequestModel()
    var getDropdownResponseModel = GetDropdownResponseModel()

    var isPremiumUser:Boolean = false
    var leaderboardResponseModel = GetLeaderboardResponseModel()

    private var liveClass = MutableLiveData<Event<GetLeaderboardResponseModel>>()
    private fun setLiveClassData(attachedAccount: GetLeaderboardResponseModel) {
        this.liveClass.postValue(Event(attachedAccount))
    }

    var getStudentListResponse = GetSchoolNameResponseModel()

    fun observerLeaderBoardData() = liveClass


    private var StudentListResponse = MutableLiveData<Event<GetSchoolNameResponseModel>>()
    private fun setStudentListResponseData(attachedAccount: GetSchoolNameResponseModel) {
        this.StudentListResponse.postValue(Event(attachedAccount))
    }

    fun observerStudentListResponseData() = StudentListResponse


    private var attachedAccount = MutableLiveData<Event<GetDropdownResponseModel>>()
    private fun setDropdownData(attachedAccount: GetDropdownResponseModel) {
        this.attachedAccount.postValue(Event(attachedAccount))
    }

    fun observerDropdownChange() = attachedAccount

    fun getStudentList(name: String? = null) {
        // setMessage(Constants.VISIBLE)
        APITask.getInstance().getStudentList(this, name)?.let { mDisposable?.add(it) }
    }

    private var studentProfileData = MutableLiveData<Event<VerifyOTPResponseModel>>()
    private fun setStudentProfile(msg: VerifyOTPResponseModel) {
        studentProfileData.value = Event(msg)
    }
    fun observedStudentProfileData() = studentProfileData


    fun callGetProfileData() {
        //     setMessage(Constants.VISIBLE)

//        Log.e("Forgot", "Model ${Gson().toJson(forgotReqModel)}")
        APITask.getInstance().getStudentProfile(this)?.let { mDisposable?.add(it) }
    }

    fun setStandard() {
             setMessage(Constants.VISIBLE)

//        Log.e("Forgot", "Model ${Gson().toJson(forgotReqModel)}")
        APITask.getInstance().setStudentStandard(this,registerRequestModel)?.let { mDisposable?.add(it) }
    }

    fun callGetLeaderBoard() {
        //  setMessage(Constants.VISIBLE)
        var getLeaderboardRequestModel = GetLeaderboardRequestModel()
        APITask.getInstance().getLeaderBoard(this, getLeaderboardRequestModel)
            ?.let { mDisposable?.add(it) }
    }

    private var messageString = MutableLiveData<Event<String>>()
    private fun setMessage(msg: String) {
        messageString.value = Event(msg)
    }

    fun askYourDought(number: String) {
        setMessage(Constants.VISIBLE)
        //  var getLeaderboardRequestModel = GetLeaderboardRequestModel()
        APITask.getInstance().askDoubt(this, number)?.let { mDisposable?.add(it) }
    }

    fun observedChanges() = messageString
    private var errorMessageString = MutableLiveData<Event<String>>()
    private fun setErrorMessage(msg: String) {
        errorMessageString.value = Event(msg)
    }

    fun observedErrorMessageChanges() = errorMessageString


    private var homeData = MutableLiveData<Event<GetHomeDataResponseModel>>()
    private fun setHomeDataMessage(msg: GetHomeDataResponseModel) {
        homeData.value = Event(msg)
    }

    fun observedHomeChanges() = homeData


    fun callGetHomeData() {
        setMessage(Constants.VISIBLE)

//        Log.e("Forgot", "Model ${Gson().toJson(forgotReqModel)}")
        APITask.getInstance().getHomeData(this)?.let { mDisposable?.add(it) }

    }


    fun callGetDropdownList() {
        setMessage(Constants.VISIBLE)
        getDropdownResponseModel = GetDropdownResponseModel()
        APITask.getInstance().getDropdownList(this)?.let { mDisposable?.add(it) }
    }

    override fun <T> onResponseReceived(response: T, requestCode: Int) {
        setMessage(Constants.HIDE)
        if (response != null && requestCode == APITask.getHomeData) {
            var responseMOdel = (response as GetHomeDataResponseModel)
            if (responseMOdel.status == "200") {
                setHomeDataMessage(responseMOdel)

            } else {
                setMessage(responseMOdel.msg)
            }
            //setMessage((response as GetHomeDataResponseModel).msg)

        }
       else if (response != null && requestCode == APITask.setStandard) {
            Log.e("getStudentProfile", "setStandard :  ${Gson().toJson(response)}")
            setMessage(Constants.HIDE)
            callGetHomeData()
        }
        else if (response != null && requestCode == APITask.getStudentProfile) {
            Log.e("getStudentProfile", "getStudentProfile :  ${Gson().toJson(response)}")
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
            Log.e("getDropDownList", "Here :  ${Gson().toJson(response)}")
            getDropdownResponseModel = response as GetDropdownResponseModel
            setDropdownData(getDropdownResponseModel)
            if ((response as GetDropdownResponseModel).status == "200") {

            } else {
                setMessage((response as GetDropdownResponseModel).message)
            }
        } else if (response != null && requestCode == APITask.getStudentList) {
            Log.e("Forgot", "Response :  ${Gson().toJson(response)}")
            getStudentListResponse = response as GetSchoolNameResponseModel

            if ((response as GetSchoolNameResponseModel).status == "200") {
                setStudentListResponseData(getStudentListResponse)
            } else {
                setMessage((response as GetSchoolNameResponseModel).message)
            }
        } else if (response != null && requestCode == APITask.getStarted) {
            Log.e("Forgot", "Response :  ${Gson().toJson(response)}")
            setMessage((response as CommonResponseModel).msg)
        } else if (response != null && requestCode == APITask.getLeaderBoard) {
            Log.e("Forgot", "Response :  ${Gson().toJson(response)}")
            leaderboardResponseModel = response as GetLeaderboardResponseModel

            if ((response as GetLeaderboardResponseModel).status == "200") {
                setLiveClassData(leaderboardResponseModel)
            } else {
                setMessage((response as GetLeaderboardResponseModel).msg)
            }
        }

    }

    override fun onResponseError(message: String, requestCode: Int, responseCode: Int) {
        setErrorMessage(message)
        setMessage(Constants.HIDE)
    }
}