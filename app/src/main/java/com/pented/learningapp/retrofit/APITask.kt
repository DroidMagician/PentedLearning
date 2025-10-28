package com.pented.learningapp.retrofit

import com.google.android.gms.location.LocationSettingsRequest
import com.google.zxing.qrcode.encoder.QRCode
import com.pented.learningapp.authScreens.model.AddLocationRequestModel
import com.pented.learningapp.authScreens.model.GetStartedRequestModel
import com.pented.learningapp.authScreens.model.RegisterRequestModel
import com.pented.learningapp.helper.model.CommonRequestModel
import com.pented.learningapp.homeScreen.home.editProfile.model.UpdateProfileRequestModel
import com.pented.learningapp.homeScreen.home.impQuestions.model.GetImpQuestionRequestModel
import com.pented.learningapp.homeScreen.home.model.EarnPointsRequestModel
import com.pented.learningapp.homeScreen.leaderboard.model.GetLeaderboardRequestModel
import com.pented.learningapp.homeScreen.subscription.model.ApplyCouponRequestModel
import com.pented.learningapp.homeScreen.subscription.model.CapturePaymentRequestModel
import com.pented.learningapp.homeScreen.subscription.model.InitPaymentRequestModel
import com.pented.learningapp.notification.model.ReadNotificationRequestModel
import io.reactivex.disposables.Disposable


class APITask : BaseAPITask() {

    private val apiCall: APICall = Retrofit.getRetrofit().create(APICall::class.java)


    companion object Singleton {
        fun getInstance(): APITask {
            return APITask()
        }

        const val getStarted = 1
        const val verifyOTP = 2
        const val getDropDownList = 3
        const val registerUser = 4
        const val getHomeData = 5
        const val getSubject = 6
        const val getTopicVideo = 7
        const val getVideoQuestion = 8
        const val getExamBluePrint = 9
        const val getLiveClass = 10
        const val getTestSeries = 11
        const val applyCoupon = 12
        const val getLeaderBoard = 13
        const val getStudentProfile = 14
        const val updateStudentProfile = 15
        const val getSubscriptionList = 16
        const val getLanguages = 17
        const val getSubjectList = 18
        const val initPayment = 19
        const val capturePayment = 20
        const val earnPoints = 21
        const val scanQr = 22
        const val getSchoolName = 23
        const val getStudentList = 24
        const val getCityList = 25
        const val setStudentLocation = 26
        const val getModelQuestionPaper = 27
        const val getModelQuestionPaperDetails = 28
        const val getImpQuestions = 29
        const val askDoubt = 30
        const val addDuration = 31
        const val setStudentRating = 32
        const val getOtherStudentProfile = 33
        const val getAllNotification = 34
        const val readNotification = 35
        const val deleteAccount = 36
        const val setStandard = 37

    }


    fun callGetStared(listener: OnResponseListener, params: GetStartedRequestModel): Disposable? {
        return getRequest(apiCall.login(params), listener, getStarted)
    }
    fun callVerifyOTP(listener: OnResponseListener, params: GetStartedRequestModel): Disposable? {
        return getRequest(apiCall.verifyOTP(params), listener, verifyOTP)
    }
    fun getDropdownList(listener: OnResponseListener): Disposable? {
        return getRequest(apiCall.getDropdownList(), listener, getDropDownList)
    }
    fun registerUser(listener: OnResponseListener, params: RegisterRequestModel): Disposable? {
        return getRequest(apiCall.registerUser(params), listener, registerUser)
    }

    fun getHomeData(listener: OnResponseListener): Disposable? {
        return getRequest(apiCall.getHomeData(), listener,getHomeData)
    }

    fun getSubject(listener: OnResponseListener,subjectId:String,searchText:String): Disposable? {
        return getRequest(apiCall.getSubject(subjectId,searchText), listener,getSubject)
    }

    fun getTopicVideo(listener: OnResponseListener,topicId:String): Disposable? {
        return getRequest(apiCall.getTopicVideos(topicId), listener, getTopicVideo)
    }

    fun getVideoQuestion(listener: OnResponseListener,videoId:String): Disposable? {
        return getRequest(apiCall.getVideoQuestions(videoId), listener, getVideoQuestion)
    }

    fun getExamBluePrint(listener: OnResponseListener): Disposable? {
        return getRequest(apiCall.getExamBluePrint(), listener, getExamBluePrint)
    }

    fun getLiveClass(listener: OnResponseListener): Disposable? {
        return getRequest(apiCall.getLiveClass(), listener, getLiveClass)
    }

    fun getWeekendTestSeries(listener: OnResponseListener): Disposable? {
        return getRequest(apiCall.getWeekendTestSeries(), listener, getTestSeries)
    }

    fun applyCoupon(listener: OnResponseListener, params: String): Disposable? {
        return getRequest(apiCall.applyCoupon(params), listener, applyCoupon)
    }

    fun getLeaderBoard(listener: OnResponseListener, params: GetLeaderboardRequestModel): Disposable? {
        return getRequest(apiCall.getLeaderBoard(params), listener, getLeaderBoard)
    }

    fun getStudentProfile(listener: OnResponseListener): Disposable? {
        return getRequest(apiCall.getStudentProfile(), listener, getStudentProfile)
    }
    fun updateStudentProfile(listener: OnResponseListener, params: UpdateProfileRequestModel): Disposable? {
        return getRequest(apiCall.updateProfile(params), listener, updateStudentProfile)
    }
    fun setStudentStandard(listener: OnResponseListener, params: UpdateProfileRequestModel): Disposable? {
        return getRequest(apiCall.setStandard(params), listener, setStandard)
    }
    fun getSubscriptionList(listener: OnResponseListener): Disposable? {
        return getRequest(apiCall.getSubscriptionList(), listener, getSubscriptionList)
    }
    fun getLanguagesList(listener: OnResponseListener): Disposable? {
        return getRequest(apiCall.getLanguagesList(), listener, getLanguages)
    }
    fun getSubjectList(listener: OnResponseListener): Disposable? {
        return getRequest(apiCall.getSubjectList(), listener, getSubjectList)
    }
    fun initPayment(listener: OnResponseListener, params: InitPaymentRequestModel): Disposable? {
        return getRequest(apiCall.initPayment(params), listener, initPayment)
    }
    fun capturePayment(listener: OnResponseListener, params: CapturePaymentRequestModel): Disposable? {
        return getRequest(apiCall.capturePayment(params), listener, capturePayment)
    }
    fun addPoints(listener: OnResponseListener, params: EarnPointsRequestModel): Disposable? {
        return getRequest(apiCall.addPoints(params), listener, earnPoints)
    }
    fun scanQR(listener: OnResponseListener, barcode:String ): Disposable? {
        return getRequest(apiCall.scanBarcode(barcode), listener, scanQr)
    }
    fun getSchoolName(listener: OnResponseListener): Disposable? {
        return getRequest(apiCall.getSchoolName(), listener, getSchoolName)
    }
    fun getModelQuestionPaper(listener: OnResponseListener): Disposable? {
        return getRequest(apiCall.getModelQuestionPaper(), listener, getModelQuestionPaper)
    }
    fun getModelQuestionPaperDetails(listener: OnResponseListener,subjectId:Int): Disposable? {
        return getRequest(apiCall.getModelQuestionPaperDetails(subjectId), listener, getModelQuestionPaperDetails)
    }
    fun getStudentList(listener: OnResponseListener,name:String?): Disposable? {
        return getRequest(apiCall.getStudentList(name ?: ""), listener, getStudentList)
    }
    fun getOtherStudentProfile(listener: OnResponseListener,name:String?): Disposable? {
        return getRequest(apiCall.getOtherStudentProfile(name ?: ""), listener, getOtherStudentProfile)
    }
    fun getCityList(listener: OnResponseListener): Disposable? {
        return getRequest(apiCall.getCityList(), listener, getCityList)
    }

    fun setStudentLocation(listener: OnResponseListener, params: AddLocationRequestModel): Disposable? {
        return getRequest(apiCall.setStudentLocation(params), listener, setStudentLocation)
    }

    fun setStudentRating(listener: OnResponseListener, params: CommonRequestModel): Disposable? {
        return getRequest(apiCall.setStudentRating(params), listener, setStudentRating)
    }

    fun getImpQuestions(listener: OnResponseListener, params: GetImpQuestionRequestModel): Disposable? {
        return getRequest(apiCall.getImpQuestions(params), listener, getImpQuestions)
    }

    fun askDoubt(listener: OnResponseListener, params: String): Disposable? {
        return getRequest(apiCall.askDoubt(params), listener, askDoubt)
    }

    fun addDuration(listener: OnResponseListener, params: CommonRequestModel): Disposable? {
        return getRequest(apiCall.addDuration(params), listener, addDuration)
    }

    fun getAllNotification(listener: OnResponseListener): Disposable? {
        return getRequest(apiCall.getAllNotificationList(), listener, getAllNotification)
    }

    fun readNotification(listener: OnResponseListener, params: ReadNotificationRequestModel): Disposable? {
        return getRequest(apiCall.readNotification(params), listener, readNotification)
    }

    fun deleteAccount(listener: OnResponseListener, params: CommonRequestModel): Disposable? {
        return getRequest(apiCall.deleteAccount(params), listener, deleteAccount)
    }
}