package com.pented.learningapp.homeScreen.home.impQuestions.model

import com.pented.learningapp.base.BaseArrayResponse
import com.pented.learningapp.helper.model.S3Bucket
import com.pented.learningapp.homeScreen.practice.model.GetQuestionPaperBySubjectResponseModel
import java.io.Serializable

class GetImpQuestionResponseModel: BaseArrayResponse<GetImpQuestionResponseModel.Data>(),Serializable {
    inner class Data(
        val TopicVideoId: String?,
        val TopicVideoShortTitle: String?,
        val TopicVideoTitle: String?,
        val TopicVideoDescription: String?,
        val VideoPlayDuration: String?,
        val CompletedPercentage: String?,
        val VideoPlayCompleted: Boolean?,
        val Points: String?,
        val IMPTitle: String?,
        val Youtubelink: String?,
        var planet_img: Int?,
        var Totalpoint: Int?,
        var IMPQuestionId: Int?,
        var isRocketVisible: Boolean = false,
        val S3Bucket: S3Bucket?
    )

}