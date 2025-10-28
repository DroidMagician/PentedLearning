package com.pented.learningapp.homeScreen.practice.viewModel

import android.app.Application
import androidx.lifecycle.MutableLiveData
import com.pented.learningapp.base.BaseViewModel
import com.pented.learningapp.helper.Constants
import com.pented.learningapp.helper.Event
import com.pented.learningapp.helper.Utils
import com.pented.learningapp.homeScreen.practice.model.GetQuestionPaperBySubjectResponseModel
import com.pented.learningapp.retrofit.APITask
import com.pented.learningapp.retrofit.OnResponseListener

class QuestionPaperListVM(val context: Application) : BaseViewModel(context), OnResponseListener {
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


    private var questionPaperListData = MutableLiveData<Event<GetQuestionPaperBySubjectResponseModel>>()
    private fun setQuestionPaperListMessage(msg: GetQuestionPaperBySubjectResponseModel) {
        questionPaperListData.value = Event(msg)
    }

    fun observedQuestionPaperListData() = questionPaperListData


    fun callGetQuestionPaperList(subjectId:Int) {
        setMessage(Constants.VISIBLE)

        APITask.getInstance().getModelQuestionPaperDetails(this,subjectId)?.let { mDisposable?.add(it) }

    }



    override fun <T> onResponseReceived(response: T, requestCode: Int) {
        setMessage(Constants.HIDE)
        if (response != null && requestCode == APITask.getModelQuestionPaperDetails) {
            if((response as GetQuestionPaperBySubjectResponseModel).status == "200")
            {
                var responseMOdel = (response as GetQuestionPaperBySubjectResponseModel)
                //setMessage((response as GetHomeDataResponseModel).msg)
                setQuestionPaperListMessage(responseMOdel)
            }
            else{
                setMessage((response as GetQuestionPaperBySubjectResponseModel).message)
            }
        }

    }

    override fun onResponseError(message: String, requestCode: Int, responseCode: Int) {

        setErrorMessage(message)
        setMessage(Constants.HIDE)
    }
}