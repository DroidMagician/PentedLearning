package com.pented.learningapp.retrofit

import com.pented.learningapp.myUtils.FileUtil

class API {
    companion object {


         val STRIPE_BASE_URL = if(FileUtil.isDevEnvironment) "https://issuing-key.stripe.com/v1/" else "https://issuing-key.stripe.com/v1/"

        //   const val VIDEO_BASE_URL = "https://d3qwsj35pay17l.cloudfront.net/output/hls/"
        const val VIDEO_BASE_URL = "https://cdn.pentedlearning.in/output/hls/"

        //        var BASE_URL: String? = "http://pented.in/API/api/"
        //   var BASE_URL: String? = "http://pented-alb-849301993.ap-south-1.elb.amazonaws.com/API/api/"
        // var BASE_URL: String? = " http://mobapi.pentedlearning.in/api/"
        //https://api.nice-stonebraker.45-142-237-22.plesk.page
//        var BASE_URL: String? = if(FileUtil.isDevEnvironment) "https://api.nice-stonebraker.45-142-237-22.plesk.page/api/" else "http://devapi.pentedlearning.in/api/"
        var BASE_URL: String? = if(FileUtil.isDevEnvironment) "https://api.nice-stonebraker.45-142-237-22.plesk.page/api/" else "http://mobapi.pentedlearning.in/api/"
        // var BASE_URL: String? = "http://13.233.99.39/API/api/"

        //Get DROP DOWN
        const val GET_DROP_DOWN_LIST_STANDARD = "Dropdown/Standards"

        //Get Started
        const val GET_STARTED = "Auth/GetStarted"

        //VERIFY OTP
        const val VERIFY_OTP = "Auth/OTP/Verify"

        //Register User
        const val REGISTER_USER = "Student/Register"

        //Get Home Data
        const val GET_HOME_DATA = "Student/Home"

        //Get Subject
        const val GET_Subject = "Subject"

        //Get Subject
        const val GET_TOPIC_VIDEOS = "Topic/Videos"

        //Get Subject
        const val GET_VIDEO_QUESTIONS = "Topic/Video/Questions"

        //Get ExamBluePrint
        const val GET_EXAM_BLUEPRINT = "ExamBluePrint/list"

        //Get Liveclass
        const val GET_LIVECLASS = "Liveclass/list"

        //Get TestSeries
        const val GET_TEST_SERIES = "WeekendTest/list"

        //Apply Coupon
        const val APPLY_COUPON = "Coupon/apply"

        //Get Leaderboard
        const val GET_LEADERBOARD = "Student/Leaderboard"


        //Get Student Profile
        const val GET_STUDENT_PROFILE = "Student/Profile"

        //Get Subscription List
//        const val GET_SUBSCRIPTION_LIST = "Subscription/list"
        const val GET_SUBSCRIPTION_LIST = "Subject/subscriptions"

        //Get languages List
        const val GET_LANGUAGES = "Student/languages"

        //Update Student Location
        const val SET_LOCATION = "Student/Location"

        //Get Subject List
        const val GET_SUBJECT_LIST = "Subject/list"

        //Init Payment
        const val PAYMENT_INIT = "Payment/Init"

        //Capture Payment
        const val PAYMENT_CAPTURE = "Payment/Capture"

        //Earn Points
        const val EARN_POINTS = "Point/Plus"

        //SCAN Barcode
        const val SCAN_BARCODE = "Topic/Video/Scan"

        //Get languages
        const val GET_LANGUAGES_LIST = "Dropdown/Languages"

        //Get Students
        const val GET_STUDENT_LIST = "Dropdown/Students"

        //Get Cities
        const val GET_CITIES_LIST = "Dropdown/Cities"

//        //Get Standards
//        const val GET_STANDARDS_LIST = "Dropdown/Standards"

        //Get IMP Question
        const val GET_IMP_QUESTIONS = "Subject/IMPQuestions"

        //Get Question Paper List
        const val GET_QUESTION_PAPERS = "QuestionPaper/subjects"

        //Set Student Location
        const val SET_STUDENT_LOCATION = "Student/Location"

        //Set Student Rating
        const val SET_STUDENT_RATING = "Student/AppRating"

        //Get School Name List
        const val GET_SCHOOL_NAME_LIST = "Dropdown/SchoolNames"

        //Ask Doubt
        const val ASK_DOUBT = "Student/AskDoubt"

        //Ask Doubt
        const val SET_STANDARD = "Student/Standard"

        //Record
        const val ADD_DURATION = "Topic/Video/Play/Record"

        //Get Other Student Profile
        const val GET_OTHER_STUDENT_PROFILE = "Student/Profile"

        //Get All Notification List
        const val GET_ALL_NOTIFICATION = "Notification/List"

        //READ Notification
        const val READ_NOTIFICATION = "Notification/Read"

        //Delete Account
        //https://api.nice-stonebraker.45-142-237-22.plesk.page/swagger/ui/index#!/Student/Student_DeleteAccount
        const val DELETE_ACCOUNT = "Student/DeleteAccount"
    }
}