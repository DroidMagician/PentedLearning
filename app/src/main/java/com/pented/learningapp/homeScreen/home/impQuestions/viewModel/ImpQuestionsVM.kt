package com.pented.learningapp.homeScreen.home.impQuestions.viewModel

import android.app.Application
import androidx.lifecycle.MutableLiveData
import com.pented.learningapp.base.BaseViewModel
import com.pented.learningapp.helper.Constants
import com.pented.learningapp.helper.Event
import com.pented.learningapp.helper.Utils
import com.pented.learningapp.homeScreen.home.impQuestions.model.GetImpQuestionRequestModel
import com.pented.learningapp.homeScreen.home.impQuestions.model.GetImpQuestionResponseModel
import com.pented.learningapp.retrofit.APITask
import com.pented.learningapp.retrofit.OnResponseListener

class ImpQuestionsVM(val context: Application) : BaseViewModel(context), OnResponseListener {
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


    private var subjectData = MutableLiveData<Event<GetImpQuestionResponseModel>>()
    private fun setImpQuestionsDataMessage(msg: GetImpQuestionResponseModel) {
        subjectData.value = Event(msg)
    }

    fun observedTopicContentListData() = subjectData


    fun callGetImpVideoListData(topicId:String) {
        setMessage(Constants.VISIBLE)
        var getImpQuestionRequestModel = GetImpQuestionRequestModel()
        getImpQuestionRequestModel.SubjectId = topicId?.toInt()
        APITask.getInstance().getImpQuestions(this,getImpQuestionRequestModel)?.let { mDisposable?.add(it) }

    }



    override fun <T> onResponseReceived(response: T, requestCode: Int) {
        setMessage(Constants.HIDE)
        if (response != null && requestCode == APITask.getImpQuestions) {
            if((response as GetImpQuestionResponseModel).status == "200")
            {
                var responseMOdel = (response as GetImpQuestionResponseModel)
                //setMessage((response as GetHomeDataResponseModel).msg)
                setImpQuestionsDataMessage(responseMOdel)
            }
            else{
                setMessage((response as GetImpQuestionResponseModel).message)
            }
        }

    }

    override fun onResponseError(message: String, requestCode: Int, responseCode: Int) {

        setErrorMessage(message)
        setMessage(Constants.HIDE)
    }
}