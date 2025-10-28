package com.pented.learningapp.homeScreen.home.viewModel

import android.app.Application
import androidx.lifecycle.MutableLiveData
import com.pented.learningapp.base.BaseViewModel
import com.pented.learningapp.helper.Constants
import com.pented.learningapp.helper.Event
import com.pented.learningapp.helper.Utils
import com.pented.learningapp.helper.model.CommonRequestModel
import com.pented.learningapp.homeScreen.home.model.*
import com.pented.learningapp.retrofit.APITask
import com.pented.learningapp.retrofit.OnResponseListener

class WatchVideoVM(val context: Application) : BaseViewModel(context), OnResponseListener {
    private var messageString = MutableLiveData<Event<String>>()
    var earnPointsRequestModel = EarnPointsRequestModel()
    private fun setMessage(msg: String) {
        messageString.value = Event(msg)
    }

    fun observedChanges() = messageString



    private var questionAnswerData = MutableLiveData<Event<VideoQuestionResponseModel>>()
    private fun setquestionAnswerDataMessage(msg: VideoQuestionResponseModel) {
        questionAnswerData.value = Event(msg)
    }

    fun observedQuestionAnswerData() = questionAnswerData



    private var errorMessageString = MutableLiveData<Event<String>>()
    private fun setErrorMessage(msg: String) {
        errorMessageString.value = Event(msg)
    }

    fun observedErrorMessageChanges() = errorMessageString


    private var topicVideoData = MutableLiveData<Event<TopicVideoResponseModel>>()
    private fun settopicVideoDataMessage(msg: TopicVideoResponseModel) {
        topicVideoData.value = Event(msg)
    }

    fun observedtopicVideoDataData() = topicVideoData

    fun addPoints() {
        //setMessage(Constants.VISIBLE)
        APITask.getInstance().addPoints(this,earnPointsRequestModel)?.let { mDisposable?.add(it) }
    }

    fun addDuration(topicVideoId:String,duration:String,isComplated:Boolean = false) {
        //setMessage(Constants.VISIBLE)
        var commonRequestModel = CommonRequestModel()
        commonRequestModel.TopicVideoId = topicVideoId
        commonRequestModel.Duration = duration
        commonRequestModel.IsCompleted = isComplated
        APITask.getInstance().addDuration(this,commonRequestModel)?.let { mDisposable?.add(it) }
    }

    fun getTopicVideo(topicId:String) {
        setMessage(Constants.VISIBLE)

//        Log.e("Forgot", "Model ${Gson().toJson(forgotReqModel)}")
        APITask.getInstance().getTopicVideo(this,topicId)?.let { mDisposable?.add(it) }

    }

    fun getQuestionAnswer(videoId:String) {
        setMessage(Constants.VISIBLE)

//        Log.e("Forgot", "Model ${Gson().toJson(forgotReqModel)}")
        APITask.getInstance().getVideoQuestion(this,videoId)?.let { mDisposable?.add(it) }

    }

    override fun <T> onResponseReceived(response: T, requestCode: Int) {
        setMessage(Constants.HIDE)
        if (response != null && requestCode == APITask.getTopicVideo) {
            if((response as TopicVideoResponseModel).status == "200")
            {
                var responseMOdel = (response as TopicVideoResponseModel)
                //setMessage((response as GetHomeDataResponseModel).msg)
                settopicVideoDataMessage(responseMOdel)
            }
            else{
                setMessage((response as SubjectResponseModel).msg)
            }
        }
        else  if (response != null && requestCode == APITask.getVideoQuestion) {
            if((response as VideoQuestionResponseModel).status == "200")
            {
                var responseMOdel = (response as VideoQuestionResponseModel)
                //setMessage((response as GetHomeDataResponseModel).msg)
                setquestionAnswerDataMessage(responseMOdel)
            }
            else{
              //  setMessage((response as VideoQuestionResponseModel).msg)
            }
        }
        else  if (response != null && requestCode == APITask.earnPoints) {
            if((response as EarnPointResponseModel).status == "200")
            {
                setMessage(Constants.POINT_ADDED)
            }
            else{
              //  setMessage((response as VideoQuestionResponseModel).msg)
            }
        }

    }

    override fun onResponseError(message: String, requestCode: Int, responseCode: Int) {

        setErrorMessage(message)
        setMessage(Constants.HIDE)
    }
}