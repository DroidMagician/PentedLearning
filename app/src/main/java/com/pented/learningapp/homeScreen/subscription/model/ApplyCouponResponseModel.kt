package com.pented.learningapp.homeScreen.subscription.model

import com.pented.learningapp.authScreens.model.VerifyOTPResponseModel
import com.pented.learningapp.base.BaseResponse

 class ApplyCouponResponseModel: BaseResponse<ApplyCouponResponseModel.Data>() {
    inner class Data(
        val OfferType: String?,
        val OfferValue: Int?,
        val MaxDiscount: Int?,
        val Id: Int?
    )
}