package com.pented.learningapp.enum

enum class notificationTypes(val notificationType : String) {
    General("0"),
    UpdateApp("1"),
    VideoFinished("2"),
    CorrectAnswer("3"),
    RateApp("4"),
    ApplySubscription("5"),
    LiveLectureStart("6"),
    LiveLectureJoined("7"),
    SubscriptionWillExpire("8"),
    TodayTotalPoints("9")
}