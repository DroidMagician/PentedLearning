package com.pented.learningapp.homeScreen.home.viewModel

import android.app.Application
import androidx.lifecycle.MutableLiveData
import com.pented.learningapp.base.BaseViewModel
import com.pented.learningapp.helper.Constants
import com.pented.learningapp.helper.Event
import com.pented.learningapp.helper.Utils
import com.pented.learningapp.homeScreen.home.model.SubjectResponseModel
import com.pented.learningapp.retrofit.APITask
import com.pented.learningapp.retrofit.OnResponseListener

class SubjectVM(val context: Application) : BaseViewModel(context), OnResponseListener {
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


    private var subjectData = MutableLiveData<Event<SubjectResponseModel>>()
    private fun setHomeDataMessage(msg: SubjectResponseModel) {
        subjectData.value = Event(msg)
    }

    fun observedSubjectData() = subjectData


    fun callSubjectDataData(subjectId:String,searchText:String) {
        setMessage(Constants.VISIBLE)

//        Log.e("Forgot", "Model ${Gson().toJson(forgotReqModel)}")
        APITask.getInstance().getSubject(this,subjectId,searchText)?.let { mDisposable?.add(it) }

    }



    override fun <T> onResponseReceived(response: T, requestCode: Int) {
        setMessage(Constants.HIDE)
        if (response != null && requestCode == APITask.getSubject) {
            if((response as SubjectResponseModel).status == "200")
            {
                var responseMOdel = (response as SubjectResponseModel)
                //setMessage((response as GetHomeDataResponseModel).msg)
                setHomeDataMessage(responseMOdel)
            }
            else{
                setMessage((response as SubjectResponseModel).msg)
            }
        }

    }

    override fun onResponseError(message: String, requestCode: Int, responseCode: Int) {

        setErrorMessage(message)
        setMessage(Constants.HIDE)
    }
}