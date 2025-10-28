package com.pented.learningapp.retrofit

import com.pented.learningapp.authScreens.model.*
import com.pented.learningapp.helper.model.CommonRequestModel
import com.pented.learningapp.helper.model.CommonResponseModel
import com.pented.learningapp.homeScreen.home.editProfile.model.UpdateProfileRequestModel
import com.pented.learningapp.homeScreen.home.examBlueprints.model.ExamBluePrintResponseModel
import com.pented.learningapp.homeScreen.home.impQuestions.model.GetImpQuestionRequestModel
import com.pented.learningapp.homeScreen.home.impQuestions.model.GetImpQuestionResponseModel
import com.pented.learningapp.homeScreen.home.liveClasses.model.GetLiveClassResponseModel
import com.pented.learningapp.homeScreen.home.model.*
import com.pented.learningapp.homeScreen.home.otherUserProfile.model.GetOtherStudentResponseModel
import com.pented.learningapp.homeScreen.home.weekendTestSeries.model.WeekendTestSeriesResponseModel
import com.pented.learningapp.homeScreen.leaderboard.model.GetLeaderboardRequestModel
import com.pented.learningapp.homeScreen.leaderboard.model.GetLeaderboardResponseModel
import com.pented.learningapp.homeScreen.practice.model.GetQuestionPaperBySubjectResponseModel
import com.pented.learningapp.homeScreen.practice.model.GetQuestionPaperResponseModel
import com.pented.learningapp.homeScreen.scanQR.model.ScanQrResponseModel
import com.pented.learningapp.homeScreen.subjects.model.SubjectListResponseModel
import com.pented.learningapp.homeScreen.subscription.model.*
import com.pented.learningapp.notification.model.GetNotificationListResponseModel
import com.pented.learningapp.notification.model.ReadNotificationRequestModel
import io.reactivex.Observable
import retrofit2.Response
import retrofit2.http.*


interface APICall {

    @POST(API.GET_STARTED)
    fun login(@Body params: GetStartedRequestModel): Observable<Response<CommonResponseModel>>

    @POST(API.VERIFY_OTP)
    fun verifyOTP(@Body params: GetStartedRequestModel): Observable<Response<VerifyOTPResponseModel>>

    @GET(API.GET_DROP_DOWN_LIST_STANDARD)
    fun getDropdownList(): Observable<Response<GetDropdownResponseModel>>

    @GET(API.GET_LANGUAGES_LIST)
    fun getLanguagesList(): Observable<Response<GetLanguagesResponseModel>>

    @GET(API.GET_SCHOOL_NAME_LIST)
    fun getSchoolName(): Observable<Response<GetSchoolNameResponseModel>>

    @GET("${API.GET_STUDENT_LIST}/{name}")
    fun getStudentList(@Path("name") name:String = ""): Observable<Response<GetSchoolNameResponseModel>>


    @GET("${API.GET_OTHER_STUDENT_PROFILE}/{studentid}")
    fun getOtherStudentProfile(@Path("studentid") name:String = ""): Observable<Response<GetOtherStudentResponseModel>>


    @GET(API.GET_CITIES_LIST)
    fun getCityList(): Observable<Response<GetSchoolNameResponseModel>>

    @GET(API.GET_EXAM_BLUEPRINT)
    fun getExamBluePrint(): Observable<Response<ExamBluePrintResponseModel>>

    @GET(API.GET_LIVECLASS)
    fun getLiveClass(): Observable<Response<GetLiveClassResponseModel>>

    @GET(API.GET_TEST_SERIES)
    fun getWeekendTestSeries(): Observable<Response<WeekendTestSeriesResponseModel>>


    @GET(API.GET_SUBJECT_LIST)
    fun getSubjectList(): Observable<Response<SubjectListResponseModel>>


    @POST(API.REGISTER_USER)
    fun registerUser(@Body params: RegisterRequestModel): Observable<Response<VerifyOTPResponseModel>>

    @GET(API.GET_HOME_DATA)
    fun getHomeData(): Observable<Response<GetHomeDataResponseModel>>

    @GET(API.GET_QUESTION_PAPERS)
    fun getModelQuestionPaper(): Observable<Response<GetQuestionPaperResponseModel>>


    @GET(API.GET_Subject)
    fun getSubject(@Query("subjectId") subjectId:String,@Query("searchText") searchText:String): Observable<Response<SubjectResponseModel>>

    @GET(API.GET_TOPIC_VIDEOS)
    fun getTopicVideos(@Query("topicId") topicId	:String): Observable<Response<TopicVideoResponseModel>>

    @GET(API.GET_VIDEO_QUESTIONS)
    fun getVideoQuestions(@Query("videoId") videoId:String): Observable<Response<VideoQuestionResponseModel>>

    @GET("${API.APPLY_COUPON}/{couponcode}")
    fun applyCoupon(@Path("couponcode") barcode:String): Observable<Response<ApplyCouponResponseModel>>


    @GET("${API.ASK_DOUBT}/{phonenumber}")
    fun askDoubt(@Path("phonenumber") phonenumber:String): Observable<Response<CommonResponseModel>>


    @PATCH(API.SET_STUDENT_LOCATION)
    fun setStudentLocation(@Body params: AddLocationRequestModel): Observable<Response<CommonResponseModel>>

    @PATCH(API.SET_STUDENT_RATING)
    fun setStudentRating(@Body params: CommonRequestModel): Observable<Response<CommonResponseModel>>


    @POST(API.GET_LEADERBOARD)
    fun getLeaderBoard(@Body params: GetLeaderboardRequestModel): Observable<Response<GetLeaderboardResponseModel>>

    @POST(API.GET_IMP_QUESTIONS)
    fun getImpQuestions(@Body params: GetImpQuestionRequestModel): Observable<Response<GetImpQuestionResponseModel>>


    @GET(API.GET_STUDENT_PROFILE)
    fun getStudentProfile(): Observable<Response<VerifyOTPResponseModel>>

    @PATCH(API.GET_STUDENT_PROFILE)
    fun updateProfile(@Body params: UpdateProfileRequestModel): Observable<Response<CommonResponseModel>>

    @PATCH(API.SET_STANDARD)
    fun setStandard(@Body params: UpdateProfileRequestModel): Observable<Response<CommonResponseModel>>


    @GET(API.GET_SUBSCRIPTION_LIST)
    fun getSubscriptionList(): Observable<Response<GetSubscriptionDataResponseModel>>

    @POST(API.PAYMENT_INIT)
    fun initPayment(@Body params: InitPaymentRequestModel): Observable<Response<InitPaymentResponseModel>>

    @POST(API.PAYMENT_CAPTURE)
    fun capturePayment(@Body params: CapturePaymentRequestModel): Observable<Response<CapturePaymentResponseModel>>

    @POST(API.EARN_POINTS)
    fun addPoints(@Body params: EarnPointsRequestModel): Observable<Response<EarnPointResponseModel>>

    @POST(API.ADD_DURATION)
    fun addDuration(@Body params: CommonRequestModel): Observable<Response<CommonResponseModel>>

    @GET("${API.SCAN_BARCODE}/{barcode}")
    fun scanBarcode(@Path("barcode") barcode:String): Observable<Response<ScanQrResponseModel>>

    @GET("${API.GET_QUESTION_PAPERS}/{subjectId}")
    fun getModelQuestionPaperDetails(@Path("subjectId") subjectId:Int): Observable<Response<GetQuestionPaperBySubjectResponseModel>>

    @GET(API.GET_ALL_NOTIFICATION)
    fun getAllNotificationList(): Observable<Response<GetNotificationListResponseModel>>

    @POST(API.READ_NOTIFICATION)
    fun readNotification(@Body params: ReadNotificationRequestModel): Observable<Response<CommonResponseModel>>

    @PATCH(API.DELETE_ACCOUNT)
    fun deleteAccount(@Body params: CommonRequestModel): Observable<Response<CommonResponseModel>>

}