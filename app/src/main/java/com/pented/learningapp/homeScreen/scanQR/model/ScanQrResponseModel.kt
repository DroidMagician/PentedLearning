package com.pented.learningapp.homeScreen.scanQR.model

import com.pented.learningapp.base.BaseResponse
import com.pented.learningapp.helper.model.S3Bucket
import com.pented.learningapp.homeScreen.home.model.GetHomeDataResponseModel
import java.io.Serializable

class ScanQrResponseModel : BaseResponse<ScanQrResponseModel.Data>(),Serializable{
     inner class Data(
         val Completed: Int?,
         val Points: Int?,
         val SubjectId: Int?,
         val S3Bucket: S3Bucket?,
         val Topic: String?,
         val Youtubelink: String?,
         val TopicId: Int?,
         val TopicVideoId: Int?,
         val TopicVideoTitle: String?,
         val TopicVideoShortTitle: String?,
         val TopicVideoDescription: String?,
         val IsCompleted: Boolean?,
         val CompletedPercentage: String = "0"
     )
 }