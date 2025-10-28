package com.pented.learningapp.homeScreen.home.subjectTopic.viewModel

import android.app.Application
import androidx.lifecycle.MutableLiveData
import com.pented.learningapp.base.BaseViewModel
import com.pented.learningapp.helper.Constants
import com.pented.learningapp.helper.Event
import com.pented.learningapp.helper.Utils
import com.pented.learningapp.homeScreen.home.model.TopicVideoResponseModel
import com.pented.learningapp.retrofit.APITask
import com.pented.learningapp.retrofit.OnResponseListener

class SubjectTopicVM(val context: Application) : BaseViewModel(context), OnResponseListener {
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


    private var subjectData = MutableLiveData<Event<TopicVideoResponseModel>>()
    private fun setHomeDataMessage(msg: TopicVideoResponseModel) {
        subjectData.value = Event(msg)
    }

    fun observedTopicContentListData() = subjectData


    fun callTopicListData(topicId:String) {
        setMessage(Constants.VISIBLE)
        APITask.getInstance().getTopicVideo(this,topicId)?.let { mDisposable?.add(it) }

    }



    override fun <T> onResponseReceived(response: T, requestCode: Int) {
        setMessage(Constants.HIDE)
        if (response != null && requestCode == APITask.getTopicVideo) {
            if((response as TopicVideoResponseModel).status == "200")
            {
                var responseMOdel = (response as TopicVideoResponseModel)
                //setMessage((response as GetHomeDataResponseModel).msg)
                setHomeDataMessage(responseMOdel)
            }
            else{
                setMessage((response as TopicVideoResponseModel).msg)
            }
        }

    }

    override fun onResponseError(message: String, requestCode: Int, responseCode: Int) {

        setErrorMessage(message)
        setMessage(Constants.HIDE)
    }
}