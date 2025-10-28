package com.pented.learningapp.homeScreen.home.liveClasses.viewModel

import android.app.Application
import android.util.Log
import androidx.lifecycle.MutableLiveData
import com.google.gson.Gson
import com.pented.learningapp.base.BaseViewModel
import com.pented.learningapp.helper.Constants
import com.pented.learningapp.helper.Event
import com.pented.learningapp.helper.Utils
import com.pented.learningapp.homeScreen.home.liveClasses.model.GetLiveClassResponseModel
import com.pented.learningapp.homeScreen.home.model.EarnPointResponseModel
import com.pented.learningapp.homeScreen.home.model.EarnPointsRequestModel
import com.pented.learningapp.retrofit.APITask
import com.pented.learningapp.retrofit.OnResponseListener


class LiveClassVM(val context: Application) : BaseViewModel(context), OnResponseListener {
    var getliveClassResponseModel = GetLiveClassResponseModel()

    private var liveClass = MutableLiveData<Event<GetLiveClassResponseModel>>()
    private fun setLiveClassData(attachedAccount: GetLiveClassResponseModel) {
        this.liveClass.postValue(Event(attachedAccount))
    }

    fun observerLiveClassData() = liveClass


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


     fun callGetLiveClasss() {
         setMessage(Constants.VISIBLE)
         APITask.getInstance().getLiveClass(this)?.let { mDisposable?.add(it) }
    }


    fun addPoints(liveLectureId: String?,subjectID: String?) {
        var earnPointsRequestModel = EarnPointsRequestModel()
        earnPointsRequestModel.Point = "5"
        earnPointsRequestModel.PointType = "LiveLecture"
        earnPointsRequestModel.ModuleId = liveLectureId
        earnPointsRequestModel.SubjectId = subjectID
        setMessage(Constants.VISIBLE)
        APITask.getInstance().addPoints(this,earnPointsRequestModel)?.let { mDisposable?.add(it) }
    }




    override fun <T> onResponseReceived(response: T, requestCode: Int) {
        setMessage(Constants.HIDE)
        if (response != null && requestCode == APITask.getLiveClass) {
            Log.e("Forgot", "Response :  ${Gson().toJson(response)}")
            getliveClassResponseModel = response as GetLiveClassResponseModel

            if((response as GetLiveClassResponseModel).status == "200")
            {
                setLiveClassData(getliveClassResponseModel)
            }
            else{
                setMessage((response as GetLiveClassResponseModel).message)
            }
        }
        else if (response != null && requestCode == APITask.earnPoints) {
            Log.e("Forgot", "Response :  ${Gson().toJson(response)}")
            var addPoints = response as EarnPointResponseModel

            if((response as EarnPointResponseModel).status == "200")
            {
                setMessage(addPoints.msg)
            }
            else {
              //  setMessage(addPoints.msg)
            }
        }

    }

    override fun onResponseError(message: String, requestCode: Int, responseCode: Int) {

        setErrorMessage(message)
        setMessage(Constants.HIDE)
    }
}