package com.pented.learningapp.homeScreen.subjects.model

import com.pented.learningapp.base.BaseResponse
import com.pented.learningapp.helper.model.S3Bucket
import com.pented.learningapp.homeScreen.home.model.GetHomeDataResponseModel

class SubjectListResponseModel: BaseResponse<SubjectListResponseModel.Data>() {
    inner class Data(
        val Subjects: List<Subject> = ArrayList()
    )

    data class Subject(
        var Chapters: ArrayList<Chapter> = ArrayList(),
        val EarnedPoints: Double?,
        val TotalChapters: Int?,
        val ViewedChapters: Int?,
        val Id: Int?,
        val Name: String?,
        val S3Bucket: S3Bucket?,
        val TotalPoints: Double?
    )

    data class Chapter(
        val CompletedPencentage: Double?,
        val EarnedPoints: Double?,
        val Id: Int?,
        var SubjectId: Int?,
        val Name: String?,
        val TotalPoints: Double?
    )

}