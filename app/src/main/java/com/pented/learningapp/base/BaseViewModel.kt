package com.pented.learningapp.base

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.pented.learningapp.helper.Event
import com.hadilq.liveevent.LiveEvent
import com.pented.learningapp.MyApplication
import io.reactivex.disposables.CompositeDisposable

open class BaseViewModel(application: Application = MyApplication()) : AndroidViewModel(application){
//    override fun <T> onResponseReceived(response: T, requestCode: Int) {
//        var response = response as CommonModel
//        onApiSuccess(response)
//    }
//
//    override fun onResponseError(message: String, requestCode: Int, responseCode: Int) {
//        if(responseCode == 417)
//        {
//            onApiFailure(responseCode)
//        }
//        else
//        {
//            onApiFailure(message)
//        }
//        //onApiFailure(message)
//    }

    protected var mDisposable: CompositeDisposable? = null
   // var spotifyTokenRequestModelComman = SpotifyTokenRequestModel()

    override fun onCleared() {
        super.onCleared()
        mDisposable?.dispose()
    }

    private val _snackBarError = MutableLiveData<Event<Any>>()
    val snackBarError: LiveData<Event<Any>> get() = _snackBarError
    fun setSnackBarError(errMsg: Any) {
        _snackBarError.value = Event(errMsg)  // Trigger the event by setting a new Event as a new value
    }

//    var apitaskC = APITask()
//
//    fun setSpotifyTokenC() {
//        // setProgressBar(View.VISIBLE)
//        // apiCall.getLookingForList()
//        apitaskC.callSaveToken(this,spotifyTokenRequestModelComman).let { mDisposable?.add(it!!) }
//    }


    private val _apiSuccess = MutableLiveData<Any>()
    val apiSuccess : LiveData<Any> get() = _apiSuccess
    fun onApiSuccess(any: Any) {
        _apiSuccess.value = any  // Trigger the event by setting a new Event as a new value
    }

    private val _apiFailure = MutableLiveData<Any>()
    val apiFailure : LiveData<Any> get() = _apiFailure
    fun onApiFailure(any: Any) {
        _apiFailure.value = any  // Trigger the event by setting a new Event as a new value
    }

    protected var disposable : CompositeDisposable = CompositeDisposable()
//    protected val apiInterface: APICall by inject()
//    private val _apiSuccessSingleEvent = MutableLiveData<Event<Any>>()
//    val apiSuccessSingleEvent: LiveData<Event<Any>> get() = _apiSuccessSingleEvent
//    fun onApiSuccessSingleEvent(any: Any) {
//        _apiSuccessSingleEvent.value = Event(any)  // Trigger the event by setting a new Event as a new value
//    }

    private val _progressBar = LiveEvent<Int>()
    val progressBar: LiveEvent<Int> = _progressBar
    fun setProgressBar(viewInt: Int) {
        _progressBar.value = viewInt  // Trigger the event by setting a new Event as a new value
    }

}