package com.pented.learningapp.homeScreen.home.model

import com.pented.learningapp.base.BaseResponse
import com.pented.learningapp.helper.model.S3Bucket

class GetStudentProfileResponseModel: BaseResponse<GetStudentProfileResponseModel.Data>() {
     inner class Data(



         val Adress: String?,
         val BadgeBucket: BadgeBucket?,
         val CourseCompleted: Double?,
         val Email: String?,
         val LanguageId: Int?,
         val Level: Int?,
         val LevelTitle: String?,
         val MobileNumber: String?,
         val Name: String?,
         val Points: Int?,
         val Ranking: Int?,
         val S3Bucket: S3Bucket?,
         val StandardId: Int?,
         val SubscriptionExpiryDate: String?
     )

     data class BadgeBucket(
         val BucketFolderPath: String?,
         val FileName: String?,
         val FileType: String?
     )


 }