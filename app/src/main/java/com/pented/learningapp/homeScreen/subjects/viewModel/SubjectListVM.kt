package com.pented.learningapp.homeScreen.subjects.viewModel

import android.app.Application
import androidx.lifecycle.MutableLiveData
import com.pented.learningapp.base.BaseViewModel
import com.pented.learningapp.helper.Constants
import com.pented.learningapp.helper.Event
import com.pented.learningapp.helper.Utils
import com.pented.learningapp.homeScreen.subjects.model.SubjectListResponseModel
import com.pented.learningapp.retrofit.APITask
import com.pented.learningapp.retrofit.OnResponseListener

class SubjectListVM(val context: Application) : BaseViewModel(context), OnResponseListener {
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


    private var subjectData = MutableLiveData<Event<SubjectListResponseModel>>()
    private fun setSubjectDataMessage(msg: SubjectListResponseModel) {
        subjectData.value = Event(msg)
    }

    fun observedSubjectData() = subjectData


    fun callSubjectData() {
        setMessage(Constants.VISIBLE)

//        Log.e("Forgot", "Model ${Gson().toJson(forgotReqModel)}")
        APITask.getInstance().getSubjectList(this)?.let { mDisposable?.add(it) }

    }



    override fun <T> onResponseReceived(response: T, requestCode: Int) {
        setMessage(Constants.HIDE)
        if (response != null && requestCode == APITask.getSubjectList) {
            if((response as SubjectListResponseModel).status == "200")
            {
                var responseMOdel = (response as SubjectListResponseModel)
                //setMessage((response as GetHomeDataResponseModel).msg)
                setSubjectDataMessage(responseMOdel)
            }
            else{
                setMessage((response as SubjectListResponseModel).msg)
            }
        }

    }

    override fun onResponseError(message: String, requestCode: Int, responseCode: Int) {

        setErrorMessage(message)
        setMessage(Constants.HIDE)
    }
}