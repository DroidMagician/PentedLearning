package com.pented.learningapp.homeScreen.home.model

import com.pented.learningapp.helper.model.S3Bucket

data class Chapter(
    val Id: Int?,
    val Name: String?,
    val IsCompleted: Boolean?,
    var Topics: ArrayList<Topic>?
    )

    data class Topic(
        val Id: Int?,
        val S3Bucket: S3Bucket?,
        val ImageName: String?,
        val IsCompleted: Boolean?,
        val Name: String?
    )