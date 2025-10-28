package com.pented.learningapp.homeScreen.subscription.model

import com.pented.learningapp.authScreens.model.VerifyOTPResponseModel
import com.pented.learningapp.base.BaseResponse

 class InitPaymentResponseModel: BaseResponse<InitPaymentResponseModel.Data>() {
    inner class Data(
        val OrderId: String?,
    )
}