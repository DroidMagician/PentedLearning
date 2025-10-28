package com.pented.learningapp.homeScreen.home.examBlueprints.model

import com.pented.learningapp.authScreens.model.GetDropdownResponseModel
import com.pented.learningapp.base.BaseArrayResponse
import com.pented.learningapp.helper.model.S3Bucket

class ExamBluePrintResponseModel: BaseArrayResponse<ExamBluePrintResponseModel.Data>() {
    inner class Data(
        val Description: String?,
        val Part: String?,
        val S3Bucket: S3Bucket?,
        val SubjectName: String?,
        val Title: String?
    )

}