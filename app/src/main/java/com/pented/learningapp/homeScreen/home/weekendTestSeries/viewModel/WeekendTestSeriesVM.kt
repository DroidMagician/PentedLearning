package com.pented.learningapp.homeScreen.home.weekendTestSeries.viewModel

import android.app.Application
import android.util.Log
import androidx.lifecycle.MutableLiveData
import com.google.gson.Gson
import com.pented.learningapp.base.BaseViewModel
import com.pented.learningapp.helper.Constants
import com.pented.learningapp.helper.Event
import com.pented.learningapp.helper.Utils
import com.pented.learningapp.homeScreen.home.weekendTestSeries.model.WeekendTestSeriesResponseModel
import com.pented.learningapp.retrofit.APITask
import com.pented.learningapp.retrofit.OnResponseListener


class WeekendTestSeriesVM(val context: Application) : BaseViewModel(context), OnResponseListener {
    var getWeekendTestSeriesResponseModel = WeekendTestSeriesResponseModel()

    private var WeekendTestSeries = MutableLiveData<Event<WeekendTestSeriesResponseModel>>()
    private fun setWeekendTestSeriesData(attachedAccount: WeekendTestSeriesResponseModel) {
        this.WeekendTestSeries.postValue(Event(attachedAccount))
    }

    fun observerWeekendTestSeriesData() = WeekendTestSeries


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


     fun callWeekendTestSeries() {
         setMessage(Constants.VISIBLE)
         APITask.getInstance().getWeekendTestSeries(this)?.let { mDisposable?.add(it) }
    }




    override fun <T> onResponseReceived(response: T, requestCode: Int) {
        setMessage(Constants.HIDE)
        if (response != null && requestCode == APITask.getTestSeries) {
            Log.e("Forgot", "Response :  ${Gson().toJson(response)}")
            getWeekendTestSeriesResponseModel = response as WeekendTestSeriesResponseModel

            if((response as WeekendTestSeriesResponseModel).status == "200")
            {
                setWeekendTestSeriesData(getWeekendTestSeriesResponseModel)
            }
            else{
                setMessage((response as WeekendTestSeriesResponseModel).message)
            }
        }

    }

    override fun onResponseError(message: String, requestCode: Int, responseCode: Int) {

        setErrorMessage(message)
        setMessage(Constants.HIDE)
    }
}