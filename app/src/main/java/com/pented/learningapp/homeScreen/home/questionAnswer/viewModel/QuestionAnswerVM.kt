package com.pented.learningapp.homeScreen.home.questionAnswer.viewModel

import android.app.Application
import android.util.Log
import androidx.lifecycle.MutableLiveData
import com.google.gson.Gson
import com.pented.learningapp.base.BaseViewModel
import com.pented.learningapp.helper.Constants
import com.pented.learningapp.helper.Event
import com.pented.learningapp.helper.Utils
import com.pented.learningapp.homeScreen.home.model.*
import com.pented.learningapp.retrofit.APITask
import com.pented.learningapp.retrofit.OnResponseListener

class QuestionAnswerVM(val context: Application) : BaseViewModel(context), OnResponseListener {
    private var messageString = MutableLiveData<Event<String>>()
    private fun setMessage(msg: String) {
        messageString.value = Event(msg)
    }
    var earnPointsRequestModel = EarnPointsRequestModel()
    fun observedChanges() = messageString
    private var errorMessageString = MutableLiveData<Event<String>>()
    private fun setErrorMessage(msg: String) {
        errorMessageString.value = Event(msg)
    }

    fun observedErrorMessageChanges() = errorMessageString


    private var questionAnswerData = MutableLiveData<Event<VideoQuestionResponseModel>>()
    private fun setquestionAnswerDataMessage(msg: VideoQuestionResponseModel) {
        questionAnswerData.value = Event(msg)
    }

    fun observedtopicVideoDataData() = questionAnswerData

    fun addPoints() {
        //setMessage(Constants.VISIBLE)
        APITask.getInstance().addPoints(this,earnPointsRequestModel)?.let { mDisposable?.add(it) }
    }
    fun getQuestionAnswer(videoId:String) {
        setMessage(Constants.VISIBLE)

//        Log.e("Forgot", "Model ${Gson().toJson(forgotReqModel)}")
        APITask.getInstance().getVideoQuestion(this,videoId)?.let { mDisposable?.add(it) }

    }



    override fun <T> onResponseReceived(response: T, requestCode: Int) {
        setMessage(Constants.HIDE)
        if (response != null && requestCode == APITask.getVideoQuestion) {
            if((response as VideoQuestionResponseModel).status == "200")
            {
                var responseMOdel = (response as VideoQuestionResponseModel)
                //setMessage((response as GetHomeDataResponseModel).msg)
                setquestionAnswerDataMessage(responseMOdel)
            }
            else{
                setMessage((response as VideoQuestionResponseModel).msg)
            }
        }
        else if (response != null && requestCode == APITask.earnPoints) {
            Log.e("Forgot", "Response :  ${Gson().toJson(response)}")
            var addPoints = response as EarnPointResponseModel
            if((response as EarnPointResponseModel).status == "200")
            {
                setMessage(addPoints.msg)
            }
//            else {
//                setMessage(addPoints.msg)
//            }
        }

    }

    override fun onResponseError(message: String, requestCode: Int, responseCode: Int) {

        setErrorMessage(message)
        setMessage(Constants.HIDE)
    }
}