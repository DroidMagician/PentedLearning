package com.pented.learningapp.authScreens.model

import com.pented.learningapp.base.BaseArrayResponse

class GetLanguagesResponseModel: BaseArrayResponse<GetLanguagesResponseModel.Data>() {
    inner class Data(
        val LanguageId: Int?,
        var isSelected: Boolean = false,
        val LanguageName: String?
    )
}