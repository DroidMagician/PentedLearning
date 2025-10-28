package com.pented.learningapp.homeScreen.home.model

import com.pented.learningapp.authScreens.model.GetDropdownResponseModel
import com.pented.learningapp.authScreens.model.VerifyOTPResponseModel
import com.pented.learningapp.base.BaseArrayResponse
import com.pented.learningapp.base.BaseResponse
import com.pented.learningapp.helper.model.CommonResponseModel
import com.pented.learningapp.helper.model.S3Bucket

class GetSubscriptionDataResponseModel: BaseResponse<GetSubscriptionDataResponseModel.Data>() {

    inner class Data(
        val Subjects: List<Subjects>?,
        val ContactNumber : String ? = null
    )

    data class Subjects(
        val SubjectId: Int?,
        val SubjectName: String?,
        val SubjectDescription: String?,
        val SubscriptionFee: Int?,
        val S3Bucket: S3Bucket?,
        var IsSubscribed: Boolean=false,
        var isSelected: Boolean=false
    )



}