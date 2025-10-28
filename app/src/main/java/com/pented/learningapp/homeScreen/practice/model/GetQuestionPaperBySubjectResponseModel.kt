package com.pented.learningapp.homeScreen.practice.model

import com.pented.learningapp.base.BaseArrayResponse
import com.pented.learningapp.homeScreen.home.weekendTestSeries.model.WeekendTestSeriesResponseModel
import java.io.Serializable

class GetQuestionPaperBySubjectResponseModel: BaseArrayResponse<GetQuestionPaperBySubjectResponseModel.Data>(),Serializable {
     inner class Data(
         val AnswerSPDF3Bucket: AnswerSPDF3Bucket?,
         val Description: String?,
         val DurationInMinutes: Int?,
         val QuestionPaperId: Int?,
         val Points: Int?,
         val QuestionPDFS3Bucket: QuestionPDFS3Bucket?,
         val SolutionVideos: List<SolutionVideo>?,
         val Title: String?,
         val TotalQuestions: Int?
     )

     data class AnswerSPDF3Bucket(
         val BucketFolderPath: String?,
         val FileName: String?,
         val FileType: String?
     )

     data class QuestionPDFS3Bucket(
         val BucketFolderPath: String?,
         val FileName: String?,
         val FileType: String?
     )

     data class SolutionVideo(
         val Description: String?,
         val Youtubelink: String?,
         val SolutionVideoS3Bucket: SolutionVideoS3Bucket?,
         val Title: String?
     )

     data class SolutionVideoS3Bucket(
         val BucketFolderPath: String?,
         val FileName: String?,
         val FileType: String?
     )
 }