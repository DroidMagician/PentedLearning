package com.pented.learningapp.homeScreen.home.model

import com.pented.learningapp.base.BaseResponse
import com.pented.learningapp.helper.model.CommonResponseModel
import com.pented.learningapp.helper.model.S3Bucket
import java.io.Serializable

class VideoQuestionResponseModel: BaseResponse<VideoQuestionResponseModel.Data>(),Serializable {
     inner class Data(
         val Questions: List<Question>?
     )

     data class Question(
         val CorrectAns: String?,
         val Youtubelink: String?,
         val Id: Int?,
         val S3Bucket:S3Bucket?,
         val MCQ: String?,
         val QuestionDetails: String?,
         val QuestionTitle: String?,
         val FileName: String?,
         val SolutionVideoFile: String?,
         val SolutuionDetails: String?
     )
 }