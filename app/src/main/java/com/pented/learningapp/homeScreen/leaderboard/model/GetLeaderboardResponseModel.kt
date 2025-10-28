package com.pented.learningapp.homeScreen.leaderboard.model

import com.pented.learningapp.base.BaseResponse
import com.pented.learningapp.helper.model.S3Bucket
import com.pented.learningapp.homeScreen.home.model.GetHomeDataResponseModel

class GetLeaderboardResponseModel: BaseResponse<GetLeaderboardResponseModel.Data>() {
    inner class Data(
        val OtherStudents: ArrayList<Student>?,
        val Rank: Int?,
        val Top3Students: List<Student>?,
        var badgeImage: Int?,
        val TotalCompetitors: Int?
    )

    data class Student(
        val Adress: String?,
        val BadgeBucket: BadgeBucket?,
        val Email: String?,
        val LanguageId: Int?,
        var badgeImage: Int?,
        var Ranking: Int?,
        val Level: Int?,
        val LevelTitle: String?,
        val MobileNumber: String?,
        val Name: String?,
        val Points: Int?,
        val UserId: Int?,
        var Rank: Int?,
        var isSelected: Boolean =false,
        var isFilterNameSelected: Boolean =false,
        val S3Bucket: S3Bucket?,
        val StandardId: Int?
    )

    data class OtherStudent(
        val Adress: String?,
        val BadgeBucket: BadgeBucket?,
        val Email: String?,
        val LanguageId: Int?,
        val Level: Int?,
        val LevelTitle: String?,
        val MobileNumber: String?,
        val Name: String?,
        val Points: Int?,
        var Ranking: Int?,
        val S3Bucket: S3Bucket?,
        val StandardId: Int?
    )

    data class Top3Student(
        val Adress: String?,
        val BadgeBucket: BadgeBucketX?,
        val Email: String?,
        val LanguageId: Int?,
        val Level: Int?,
        val LevelTitle: String?,
        val MobileNumber: String?,
        val Name: String?,
        val Points: Int?,
        var Ranking: Int?,
        val S3Bucket: S3Bucket?,
        val StandardId: Int?
    )

    data class BadgeBucket(
        val BucketFolderPath: String?,
        val FileName: String?,
        val FileType: String?
    )



    data class BadgeBucketX(
        val BucketFolderPath: String?,
        val FileName: String?,
        val FileType: String?
    )


}