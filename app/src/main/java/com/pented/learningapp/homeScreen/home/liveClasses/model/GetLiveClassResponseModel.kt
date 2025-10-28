package com.pented.learningapp.homeScreen.home.liveClasses.model

import com.pented.learningapp.base.BaseArrayResponse
import com.pented.learningapp.helper.model.S3Bucket
import com.pented.learningapp.homeScreen.home.examBlueprints.model.ExamBluePrintResponseModel

 class GetLiveClassResponseModel: BaseArrayResponse<GetLiveClassResponseModel.Data>() {
     inner class Data(
         var Date: String?,
         val Description: String?,
         val RecordingLink: String?,
         val JoinLink: String?,
         var difference: String?,
         var LiveLectureId: String?,
         var SubjectId: String?,

         var totalMillis: Long?,
         var isCurrentlyRunning: Boolean = false,
         val S3Bucket: S3Bucket?,
         val SubjectName: String?,
         val TeacherName: String?,
         var Time: String?,
         var Datetime: String?,
         val TopicTitle: String?
     )

//     data class S3Bucket(
//         val BucketFolderPath: String?,
//         val FileName: String?,
//         val FileType: String?
//     )
 }