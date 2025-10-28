package com.pented.learningapp.homeScreen.home.otherUserProfile.model

import com.pented.learningapp.authScreens.model.VerifyOTPResponseModel
import com.pented.learningapp.base.BaseResponse
import com.pented.learningapp.helper.model.S3Bucket

class GetOtherStudentResponseModel: BaseResponse<GetOtherStudentResponseModel.Data>() {
    inner class Data(
        val Adress: Any?,
        val BadgeBucket: BadgeBucket?,
        val CourseCompleted: Double?,
        val Email: String?,
        val LanguageId: Int?,
        val Level: Int?,
        val LevelTitle: String?,
        val MobileNumber: Any?,
        val MonthPoints: Int?,
        val Name: String?,
        val Points: Int?,
        val S3Bucket: S3Bucket?,
        val SchoolName: Any?,
        val StandardId: Int?,
        val SubscriptionExpiryDate: Any?,
        val UserId: Int?
    )

    data class BadgeBucket(
        val BucketFolderPath: String?,
        val FileName: String?,
        val FileType: Int?
    )
}