package com.pented.learningapp.homeScreen.leaderboard.model

class GetLeaderboardRequestModel {
//    var StudentIds: ArrayList<Int> = ArrayList<Int>()
    var Student_Id: String ?  = null
//    var Cities: ArrayList<String> = ArrayList<String>()
    var StandardIds: ArrayList<Int> = ArrayList<Int>()
    var SchoolName: String ?  = null
    var Address: String ?  = null
    var StudentName: String ?  = null
    var PageNumber: Int = 0
    var NearMe: Boolean = false
    var Month: Int = 0
    var Year: Int = 0
}