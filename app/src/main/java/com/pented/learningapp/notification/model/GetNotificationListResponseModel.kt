package com.pented.learningapp.notification.model

import com.pented.learningapp.base.BaseArrayResponse

class GetNotificationListResponseModel : BaseArrayResponse<GetNotificationListResponseModel.Data>() {
    inner class Data(
        val Id: Int?,
        val Type: String?,
        var Title: String?,
        var Message: String?,
        var NotificationDate: String?,
        var IsRead: Boolean =false
    )
}