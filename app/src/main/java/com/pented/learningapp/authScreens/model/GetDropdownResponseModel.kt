
package com.pented.learningapp.authScreens.model

import com.pented.learningapp.base.BaseArrayResponse

class GetDropdownResponseModel : BaseArrayResponse<GetDropdownResponseModel.Data>() {
    inner class Data(
            val LanguageId: Int?,
            val Id: String?,
            var isSelected: Boolean?,
            var isFilterNameSelected: Boolean =false,
            val Value: String?
    )
}


