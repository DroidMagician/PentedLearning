package com.pented.learningapp.homeScreen.home.model

import com.pented.learningapp.authScreens.model.VerifyOTPResponseModel
import com.pented.learningapp.base.BaseResponse
import com.pented.learningapp.helper.model.CommonResponseModel
import com.pented.learningapp.helper.model.S3Bucket
import java.io.Serializable

class TopicVideoResponseModel: BaseResponse<TopicVideoResponseModel.Data>(),Serializable {

     inner class Data(
         val Completed: Int?,
         val Points: Int?,
         val Topic: String?,
         val TopicTitle: String?,
         val TopicDescription: String?,
         val TopicId: Int?,
         val Videos: List<Video>?
     )

     data class Video(
         val TopicVideoId: Int?,
         val TopicVideoTitle: String?,
         val TopicVideoShortTitle: String?,
         val TopicVideoDescription: String?,
         var VideoPlayDuration: String?,
         val S3Bucket: S3Bucket?,
         val Youtubelink: String?,
         var planet_img : Int,
         var isRocketVisible:Boolean = false,
         var IsCompleted:Boolean = false,
         var VideoPlayCompleted:Boolean = false,
         var Points:String ? = null,
         var CompletedPercentage:Double?
     )
 }