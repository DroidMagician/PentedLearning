package com.pented.learningapp.homeScreen.practice.model

import com.pented.learningapp.base.BaseArrayResponse
import com.pented.learningapp.helper.model.S3Bucket
import com.pented.learningapp.homeScreen.home.weekendTestSeries.model.WeekendTestSeriesResponseModel

class GetQuestionPaperResponseModel: BaseArrayResponse<GetQuestionPaperResponseModel.Data>() {
    inner class Data(
        var QuestionPapers: List<QuestionPaper>?,
        val SubjectId: Int?,
        val Points: Int?,
        val TotalQuestionPapers: Int?,
        val S3Bucket: S3Bucket?,
        val SubjectTitle: String?
    )

    data class QuestionPaper(
        val DurationInMinutes: Int?,
        val Title: String?,
        var SubjectId: Int?,
        var SubjectTitle: String?,
        val TotalQuestions: Int?
    )
}