package com.pented.learningapp.notification.viewModel

import android.app.Application
import android.util.Log
import androidx.lifecycle.MutableLiveData
import com.pented.learningapp.base.BaseViewModel
import com.pented.learningapp.helper.Constants
import com.pented.learningapp.helper.Event
import com.pented.learningapp.helper.model.CommonResponseModel
import com.pented.learningapp.notification.model.GetNotificationListResponseModel
import com.pented.learningapp.notification.model.ReadNotificationRequestModel
import com.pented.learningapp.retrofit.APITask
import com.pented.learningapp.retrofit.OnResponseListener

class NotificationVM(val context: Application) : BaseViewModel(context), OnResponseListener {
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


    private var NotificationData = MutableLiveData<Event<GetNotificationListResponseModel>>()
    private fun setNotificationDataMessage(msg: GetNotificationListResponseModel) {
        NotificationData.value = Event(msg)
    }

    fun observedNotificationListData() = NotificationData


    fun callGetNotificationListData() {
        setMessage(Constants.VISIBLE)
        APITask.getInstance().getAllNotification(this)?.let { mDisposable?.add(it) }
    }

    fun readNotification(notificationId:Int) {
        setMessage(Constants.VISIBLE)
        var readNotificationListResponseModel = ReadNotificationRequestModel()
        readNotificationListResponseModel.NotificationIds.add(notificationId)
        APITask.getInstance().readNotification(this,readNotificationListResponseModel)?.let { mDisposable?.add(it) }

    }



    override fun <T> onResponseReceived(response: T, requestCode: Int) {
        setMessage(Constants.HIDE)
        Log.e("requestCode","===$requestCode")
        if (response != null && requestCode == APITask.getAllNotification) {
            if((response as GetNotificationListResponseModel).status == "200")
            {
                var responseMOdel = (response as GetNotificationListResponseModel)
                //setMessage((response as GetHomeDataResponseModel).msg)
                setNotificationDataMessage(responseMOdel)
            }
            else{
                setMessage((response as GetNotificationListResponseModel).message)
            }
        }
       else if (response != null && requestCode == APITask.readNotification) {
            if((response as CommonResponseModel).status == "200")
            {
                setMessage(Constants.NAVIGATE)
            }
            else{
              //  setMessage((response as CommonResponseModel).msg)
            }
        }
    }

    override fun onResponseError(message: String, requestCode: Int, responseCode: Int) {

        setErrorMessage(message)
        setMessage(Constants.HIDE)
    }
}