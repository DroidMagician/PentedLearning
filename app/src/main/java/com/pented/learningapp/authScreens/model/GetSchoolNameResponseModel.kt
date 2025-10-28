package com.pented.learningapp.authScreens.model

import com.pented.learningapp.base.BaseArrayResponse

class GetSchoolNameResponseModel: BaseArrayResponse<GetSchoolNameResponseModel.Data>() {
    inner class Data(
        val Id: String?,
        var isSelected: Boolean = false,
        val Value: String?
    )
}