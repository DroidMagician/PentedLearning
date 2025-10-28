package com.pented.learningapp.homeScreen.subscription.model

import com.pented.learningapp.authScreens.model.VerifyOTPResponseModel
import com.pented.learningapp.base.BaseResponse

 class CapturePaymentResponseModel: BaseResponse<CapturePaymentResponseModel.Data>() {
    inner class Data(
        val Status: String?,
        val Amount: Int?
    )
}