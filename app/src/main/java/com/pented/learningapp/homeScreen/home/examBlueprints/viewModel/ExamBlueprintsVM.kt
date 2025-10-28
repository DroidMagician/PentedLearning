package com.pented.learningapp.homeScreen.home.examBlueprints.viewModel

import android.app.Application
import android.util.Log
import androidx.lifecycle.MutableLiveData
import com.google.gson.Gson
import com.pented.learningapp.base.BaseViewModel
import com.pented.learningapp.helper.Constants
import com.pented.learningapp.helper.Event
import com.pented.learningapp.helper.Utils
import com.pented.learningapp.homeScreen.home.examBlueprints.model.ExamBluePrintResponseModel
import com.pented.learningapp.retrofit.APITask
import com.pented.learningapp.retrofit.OnResponseListener


class ExamBlueprintsVM(val context: Application) : BaseViewModel(context), OnResponseListener {
    var getExamBluePrintResponseModel = ExamBluePrintResponseModel()

    private var examBluePrint = MutableLiveData<Event<ExamBluePrintResponseModel>>()
    private fun setExamBlueprintData(attachedAccount: ExamBluePrintResponseModel) {
        this.examBluePrint.postValue(Event(attachedAccount))
    }

    fun observerExamBlueprintData() = examBluePrint


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


     fun callGetExamBluePrints() {
         setMessage(Constants.VISIBLE)
         APITask.getInstance().getExamBluePrint(this)?.let { mDisposable?.add(it) }
    }




    override fun <T> onResponseReceived(response: T, requestCode: Int) {
        setMessage(Constants.HIDE)
        if (response != null && requestCode == APITask.getExamBluePrint) {
            Log.e("Forgot", "Response :  ${Gson().toJson(response)}")
            getExamBluePrintResponseModel = response as ExamBluePrintResponseModel

            if((response as ExamBluePrintResponseModel).status == "200")
            {
                setExamBlueprintData(getExamBluePrintResponseModel)
            }
            else{
                setMessage((response as ExamBluePrintResponseModel).message)
            }
        }

    }

    override fun onResponseError(message: String, requestCode: Int, responseCode: Int) {
        setErrorMessage(message)
        setMessage(Constants.HIDE)
    }
}