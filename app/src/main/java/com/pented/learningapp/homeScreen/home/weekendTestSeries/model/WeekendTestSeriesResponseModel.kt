package com.pented.learningapp.homeScreen.home.weekendTestSeries.model

import com.pented.learningapp.base.BaseArrayResponse
import com.pented.learningapp.homeScreen.home.liveClasses.model.GetLiveClassResponseModel

 class WeekendTestSeriesResponseModel: BaseArrayResponse<WeekendTestSeriesResponseModel.Data>() {
     inner class Data(
         val Date: String?,
         val Description: String?,
         val Duration: Int?,
         val Part: String?,
         val TotalQuestion: String?,
         val S3Bucket: S3Bucket?,
         val SubjectName: String?,
         val TotalMark: String?,
         val TestUrl: String?,
         val Time: String?,
         val Title: String?,
         val Total: Int?
     )

     data class S3Bucket(
         val BucketFolderPath: String?,
         val FileName: String?,
         val FileType: String?
     )
 }