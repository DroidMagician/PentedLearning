package com.pented.learningapp.authScreens.model

import com.pented.learningapp.base.BaseResponse
import com.pented.learningapp.helper.model.S3Bucket
import java.util.*

class VerifyOTPResponseModel: BaseResponse<VerifyOTPResponseModel.Data>() {
     inner class Data(
         val BadgeBucket: BadgeBucket?,
         val CourseCompleted: Double?,
         val Level: Int?,
         val LevelTitle: String?,
         val Points: Int? = 0,
         val MonthPoints: Int = 0,
         val Ranking: Int?,
         val SubscriptionExpiryDate: String?,
         val SchoolName: String?,
         val AccessToken: AccessToken?,
         val Adress: String?,
         val Email: String?,
         val EarnedPoints: String?,
         val IsNew: Boolean?,
         val LanguageId: Int?,
         val MobileNumber: String?,
         val Name: String?,
         val S3Bucket: S3Bucket?,
         val StandardId: Int?,
         var date: Calendar? = null,
         )
    data class BadgeBucket(
        val BucketFolderPath: String?,
        val FileName: String?,
        val FileType: String?
    )
     data class AccessToken(
         val access_token: String?,
         val error: String?,
         val token_type: String?
     )
 }