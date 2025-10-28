package com.pented.learningapp.helper

import com.pented.learningapp.homeScreen.home.model.Topic
import com.pented.learningapp.homeScreen.subjects.model.SubjectListResponseModel


object Constants {

    val PROFILEADDRESS: String = "PROFILEADDRESS"
    val MAILINGADDRESS: String = "MAILINGADDRESS"
    val FINGERPRINTDIALOG: String = "FINGERPRINTDIALOG"
    val BIOMETRICSDIALOG: String = "BIOMETRICSDIALOG"
    val SENDINVITE: String = "SENDINVITE"
    var ServiceType: String = "stripeUS"
    val SAVE: String = "SAVE"
    val POINT_PLUS_VIDEO: String = "1"
    var ifFullScreen = true
    var totalFreeDays = 3

    var isLockLiveClass = true
    var isLockTestSeries = true
    var isLockExamBluePrint = true
    var isLockAskDoubts = true
    var isLockSubjects = true
    var isLockScan = true
    var isLockPractice= true
    var isLockIMPQuestions= true
    var popupCount = 0
    var videoNameForSolution:String ? = null
    var subjectIdForScanQR:String ? = null

    var SubscriptionExpiryDateFinal = "2022-12-15T15:57:49.133"
    var subjectListApiBackup: ArrayList<SubjectListResponseModel.Subject> = ArrayList<SubjectListResponseModel.Subject>()
    var selectedTopicsList: ArrayList<Topic> = ArrayList<Topic>()
    var headerlanguageid: String ?  = null
    var headerstandardid: String ?  = null
    var headerdevicemodel: String ?  = null
    var headerdeviceUUID: String ?  = null
    var headerappversion: String ?  = null
    var subjectId: String ?  = null
    var subjectIdFromQuestion: String ?  = null
    var questionPaperId: String ?  = null
    var QuestionPaperVideoPoints: String   = "0"
    var notificationType: String   = "500"
    var registerPoints:Int ? = null
    var isAppRated:Boolean ? = false

    var isFromNormalVideoList :Boolean = false
    val ONBACKPRESS: String = "ONBACKPRESS"
    var count:Int = 0
    var rateCount:Int = 0
    val DELETEDUSERS: String = "DELETEDUSERS"
    val ADDUSERS: String = "ADDUSERS"
    val ACTIVEUSERS: String = "ACTIVEUSERS"
    val PHYSICALVIEW: String = "PHYSICALVIEW"
    val COPYCARDNUM: String = "COPYCARDNUM"
    val COPYCVV: String = "COPYCVV"
    val EDIT: String = "EDIT"
    val STATE: String = "STATE"
    var ADDEDITCARD: Boolean = false
    var ADDRESSCHANGED: Boolean = false
    var isFromBackgroud: Boolean = false
    var isApiCalling: Boolean = true
    var isProfileUpdated: Boolean = true
    val COUNTRY: String = "COUNTRY"
    val FILTERLEADERBOARD: String = "FILTER_LEADERBOARD"
    val ACTIVATECARDCLICK = "ACTIVATECARDCLICK"
    var phoneNumber = ""
    var OTP = ""
    //    val PRIVACY_LINK = "https://tryjeeves.com/legal.html"
    var PRIVACY_LINK = ""
    val NAVIGATE = "navigate"
    const val SHOW_PROGRESS = "showProgress"
    const val CUSTOMER_CREATED = "customerCreated"
    const val CARDSTATUSERROR = "CARDSTATUSERROR"
    const val HIDE_PROGRESS = "hideProgress"
    var SEARCH_TEXT = ""
    var SEARCH_TRANSACTION_ID = ""
    var SEARCH_TRANSACTION_TYPE = ""
    val NO_CONTENT = "noContent"
    val CLEARDATE = "clearDate"
    val VISIBLE = "visible"
    val POINT_ADDED = "pointAdded"
    val VIRTUALCARD = "VIRTUALCARD"
    val CARDIMAGE = "CARDIMAGE"
    val DELETEDCARD = "DELETEDCARD"
    val CREATEDCARD = "CREATEDCARD"
    val EditPinClick = "EditPinClick"
    val ViewPinClick = "ViewPinClick"
    val PHYSICALCARDSWITCH = "PHYSICALCARDSWITCH"
    val ADDRESS_CHANGE_REQUEST = "addressChangeRequest"
    val REPLACE_CARD_CLICK = "replaceCardClick"
    val REPLACE_CARD_SUCCESS = "replaceCardSuccess"
    val BROADCAST_OPENDRAWER = "openDrawer"
    val BROADCAST_CLOSE_OTP = "closeOTP"
    val BROADCAST_ERROR_MESSAGE_OTP = "errorMessageOTP"
    val BROADCAST_SEND_OTP = "sendOTP"
    val BROADCAST_ERROR_OTP = "errorOTP"
    val BROADCAST_SUCESS_OTP = "SuccessOTP"
    val BROADCAST_CLOSEDRAWER = "closeDrawer"
    val BROADCAST_NEXT_STEP = "nextStep"
    val BROADCAST_THIRD_STEP = "thirdStep"
    val BROADCAST_PREVIOUS_STEP = "previousStep"
    val BROADCAST_OPENCARDS = "openCards"
    val BROADCAST_EXPORT_CSV = "exportCSV"
    val BROADCAST_EXPORT_PDF = "exportPDF"
    val BROADCAST_EXPORT_EXCEL = "exportExcel"
    val BROADCAST_CLEAR_SEARCH = "clearSearch"
    val BROADCAST_OPENTRANSACTIONS = "openTransactions"
    val CLEAR_STACK = "CLEAR_STACK"
    val FROM_REPLACE_CARD = "FromReplaceCard"
    var isFromReplaceCard = false
    var UPDATE_STATUS = "UpdateStatus"
    var selectedUserId = "0"
    val HIDE = "hide"
    val NO_DATA = "NoData"
    val SUCCESS = "Success"
    val NODATAFOUND = "nodatafound"
    val BACKPRESSED = "backpressed"
    val REFRESH_LIVE_LACTURES = "refreshLiveLacture"
    val GO_TO_LEADERBORARD = "goToLeaderBoard"
    var isTransactionFragment = false
    val MOVE_To = "moveto"
    val EXTRA = "extra"
    const val FINISH = "finish"

    //message Types
    var TEXT = "text"
    var IMAGE = "image"
    var OFFER = "offer"
    var senderId: String? = null
    var receiverID: String? = null

    //    var apiKey = "eyJhbGciOiJSUzI1NiIsInR5cCI6IkpXVCJ9.eyJ1c2VySWQiOjMsImlhdCI6MTU3NjgzMDA0MCwiaXNzIjoibXVzaWNhbGx5In0.iv-st1XnZbdultvHK4cUDLjsRP8Hz4yEJWHm9ZhzDZSr4GFi4vyaGfFRAwUXS-A2siPCI1S6gyv5jqglUwCfj8s5HPOg3GI1nOHATjZpNYkzhIykfkbaljAaqSzzCxMlRL4H3jcKfqJL3X5yfuXq4QwZLfuOMp3jF_nsuYV396E"
    var apiKey =
        "eyJhbGciOiJSUzI1NiIsInR5cCI6IkpXVCJ9.eyJ1c2VySWQiOjIsImlhdCI6MTU3NzMzNjIxNCwiaXNzIjoibXVzaWNhbGx5In0.yBKocKM1kf5YKwJLK7tcCQy-Mi0kz7gHDzKsqsb_MKUbNQfz6LzuOjT1U9eKvhKotJ6oWaL763szDo3_FrHSPNGVequRrEIX6HRUiRcXaOqn6go_fN_iYhDS5udZ7rhtovyogvYaBqOo2qj-yNkjWQNeAQ-zMjJXldqHfs5hjXo"

    interface PREF {
        companion object {

        }
    }

    var cardTypes = ArrayList<String>()
    var cards = ArrayList<Int>()
    var users = ArrayList<Int>()
    var isReceiptAtteched = false
    var isReceiptNotAtteched = false
    var isInActivityError = false


    var TEAMMANAGEMENTAPICALL = false




}
