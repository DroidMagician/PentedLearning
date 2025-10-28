package com.pented.learningapp.homeScreen.scanQR.viewModel

import android.app.Application
import androidx.lifecycle.MutableLiveData
import com.pented.learningapp.helper.Event
import com.pented.learningapp.retrofit.APITask
import com.pented.learningapp.retrofit.OnResponseListener
import com.pented.learningapp.base.BaseViewModel
import com.pented.learningapp.helper.Constants
import com.pented.learningapp.helper.Utils
import com.pented.learningapp.homeScreen.scanQR.model.ScanQrResponseModel


class ScanQRVM(val context: Application) : BaseViewModel(context), OnResponseListener {

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


    private var scanQR = MutableLiveData<Event<ScanQrResponseModel>>()
    private fun setscanQRMessage(msg: ScanQrResponseModel) {
        scanQR.value = Event(msg)
    }

    fun observedscanQRChanges() = scanQR


    fun callscanQRData(barcode:String) {
        setMessage(Constants.VISIBLE)
        APITask.getInstance().scanQR(this,barcode)?.let { mDisposable?.add(it) }

    }



    override fun <T> onResponseReceived(response: T, requestCode: Int) {
        setMessage(Constants.HIDE)
        if (response != null && requestCode == APITask.scanQr) {
            var responseMOdel = (response as ScanQrResponseModel)
            if(responseMOdel.status == "200")
            {
                setscanQRMessage(responseMOdel)

            }
            else{
                setMessage(responseMOdel.msg)
            }
            //setMessage((response as GetHomeDataResponseModel).msg)
        }
    }

    override fun onResponseError(message: String, requestCode: Int, responseCode: Int) {

        setErrorMessage(message)
        setMessage(Constants.HIDE)
    }
}