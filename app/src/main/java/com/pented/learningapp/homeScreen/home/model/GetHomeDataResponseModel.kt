package com.pented.learningapp.homeScreen.home.model
import com.pented.learningapp.authScreens.model.VerifyOTPResponseModel
import com.pented.learningapp.base.BaseResponse
import com.pented.learningapp.helper.model.CommonResponseModel
import com.pented.learningapp.helper.model.S3Bucket

class GetHomeDataResponseModel : BaseResponse<GetHomeDataResponseModel.Data>() {
    inner class Data(
        val AskedDoubtCount: Int?,
        var Points: Int?,
        val Level: Int?,
        val AppRated: Boolean? = false,
        val ShowToPremiumUsers: Boolean? = false,
        val ShowToNormalUsers: Boolean? = false,
        var Freedays: Int = 0,
        val ExamBluePrints: List<String>?,
        val StudentInfo: StudentInfo?,
        val MenuSetting: MenuSetting?,
        val Subjects: List<Subject>?,
        val Alert: Alert?,
        val TodayLiveClasses: List<TodayLiveClasse>?,
        val UnreadNotificationCount: Int?,
        val WeekendTests: List<WeekendTestSery>?,
        val WhatsAppAskDoubt: String?,
        var SubscriptionExpiryDate: String?,
        val LevelTitle: String?,
        val BannerBucket: S3Bucket?,
        val ChampionBucket: S3Bucket?
    )

    data class StudentInfo(
        val Id: Int?,
        val ImageName: String?
    )

    //public enum AlertTypes
    //    {
    //        [Display(Name = "Subscribe")]
    //        Subscribe = 1,
    //
    //        [Display(Name = "Update App")]
    //        UpdateApp = 2,
    //    }

    data class Alert(

        //   [Display(Name = "Subscribe")]
        //        Subscribe = 1,
        //
        //             [Display(Name = "Update App")]
        //        UpdateApp = 2,

        val ShowAlert: Boolean?,
        val Title: String?,
        val Description: String?,
        val AlertType: String?
    )

    data class Subject(
        val CompletedPercentage: Double?,
        val IconUrl: String?,
        val Id: Int?,
        val S3Bucket: S3Bucket?,
        val IsLock: Boolean?,
        val Name: String?
    )

    data class MenuSetting(
        val ShowLiveClass: Boolean? = false,
        val ShowImpQuestion: Boolean? = false,
        val ShowWeekendTest: Boolean? = false,
        val ShowExambluePrint: Boolean? = false,
        val ShowAskDoubt: Boolean? = false
    )

    data class TodayLiveClasse(
        val SubjectName: String?,
        val TeacherName: String?,
        val Time: String?
    )

    data class WeekendTestSery(
        val DayName: String?,
        val Time: String?
    )
}