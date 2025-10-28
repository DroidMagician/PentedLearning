package com.pented.learningapp.homeScreen.subscription.viewModel

import android.app.Application
import androidx.lifecycle.MutableLiveData
import com.pented.learningapp.helper.Event
import com.pented.learningapp.retrofit.APITask
import com.pented.learningapp.retrofit.OnResponseListener
import com.pented.learningapp.base.BaseViewModel
import com.pented.learningapp.helper.Constants
import com.pented.learningapp.helper.Utils
import com.pented.learningapp.homeScreen.home.model.GetSubscriptionDataResponseModel
import com.pented.learningapp.homeScreen.subscription.model.*


class SubscriptionVM(val context: Application) : BaseViewModel(context), OnResponseListener {
    var initPaymentResponseModel = InitPaymentRequestModel()
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


    private var subscriptionData = MutableLiveData<Event<GetSubscriptionDataResponseModel>>()
    private fun setSubscriptionDataMessage(msg: GetSubscriptionDataResponseModel) {
        subscriptionData.value = Event(msg)
    }

    fun observedHomeChanges() = subscriptionData


    private var applyCouponCodeData = MutableLiveData<Event<ApplyCouponResponseModel>>()
    private fun setApplyCouponCodeMessage(msg: ApplyCouponResponseModel) {
        applyCouponCodeData.value = Event(msg)
    }

    fun observedApplyCouponCodeChanges() = applyCouponCodeData


    private var initPaymentData = MutableLiveData<Event<InitPaymentResponseModel>>()
    private fun setinitPaymentMessage(msg: InitPaymentResponseModel) {
        initPaymentData.value = Event(msg)
    }

    fun observedinitPaymentChanges() = initPaymentData


    private var capturePaymentData = MutableLiveData<Event<CapturePaymentResponseModel>>()
    private fun setcapturePaymentMessage(msg: CapturePaymentResponseModel) {
        capturePaymentData.value = Event(msg)
    }

    fun observedcapturePaymentChanges() = capturePaymentData


    fun callGetSubscriptionData() {
        setMessage(Constants.VISIBLE)

//        Log.e("Forgot", "Model ${Gson().toJson(forgotReqModel)}")
        APITask.getInstance().getSubscriptionList(this)?.let { mDisposable?.add(it) }

    }

    fun applyCouponCode(couponCode:String,subscriptionId:ArrayList<Int>)
    {
//        var applyCouponCode = ApplyCouponRequestModel()
//        applyCouponCode.CouponCode = couponCode
//        applyCouponCode.SubscriptionIds = subscriptionId
        APITask.getInstance().applyCoupon(this,couponCode)?.let { mDisposable?.add(it) }
        setMessage(Constants.VISIBLE)
    }

    fun initPayment()
    {
        APITask.getInstance().initPayment(this,initPaymentResponseModel)?.let { mDisposable?.add(it) }
        setMessage(Constants.VISIBLE)
    }

    fun capturePayment( paymentId:String)
    {
        var capturePaymentRequestModel = CapturePaymentRequestModel()
        capturePaymentRequestModel.PaymentId = paymentId
        APITask.getInstance().capturePayment(this,capturePaymentRequestModel)?.let { mDisposable?.add(it) }
        setMessage(Constants.VISIBLE)
    }


    override fun <T> onResponseReceived(response: T, requestCode: Int) {
        setMessage(Constants.HIDE)
        if (response != null && requestCode == APITask.getSubscriptionList) {
            var responseMOdel = (response as GetSubscriptionDataResponseModel)
            if(responseMOdel.status == "200")
            {
                setSubscriptionDataMessage(responseMOdel)
            }
            else{
                setMessage(responseMOdel.msg)
            }

        }
       else if (response != null && requestCode == APITask.applyCoupon) {
            var responseMOdel = (response as ApplyCouponResponseModel)
            if(responseMOdel.status == "200")
            {
                setApplyCouponCodeMessage(responseMOdel)
            }
            else{
                setMessage(responseMOdel.msg)
            }

        }
        else if (response != null && requestCode == APITask.initPayment) {
            var responseMOdel = (response as InitPaymentResponseModel)
            if(responseMOdel.status == "200")
            {
                setinitPaymentMessage(responseMOdel)
            }
            else{
                setMessage(responseMOdel.msg)
            }

        }
        else if (response != null && requestCode == APITask.capturePayment) {
            var responseMOdel = (response as CapturePaymentResponseModel)
            if(responseMOdel.status == "200")
            {
                setcapturePaymentMessage(responseMOdel)
            }
            else{
                setMessage(responseMOdel.msg)
            }

        }

    }

    override fun onResponseError(message: String, requestCode: Int, responseCode: Int) {

        setErrorMessage(message)
        setMessage(Constants.HIDE)
    }
}