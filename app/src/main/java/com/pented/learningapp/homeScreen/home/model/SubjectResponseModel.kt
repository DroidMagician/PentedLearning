package com.pented.learningapp.homeScreen.home.model

import com.pented.learningapp.authScreens.model.VerifyOTPResponseModel
import com.pented.learningapp.base.BaseResponse
import com.pented.learningapp.helper.model.CommonResponseModel

class SubjectResponseModel : BaseResponse<SubjectResponseModel.Data>() {
    inner class Data(
        val Chapters: ArrayList<Chapter>?,
        val CompletedPencentage: Double?,
        val CompletedTopics: Int?,
        val SubjectId: Int?,
        val SubjectImageName: String?,
        val SubjectName: String?,
        val SubjectPoints: Int?,
        val TotalTopics: Int?
    )


}