package com.pented.learningapp.myUtils

import java.util.regex.Pattern

class UtilsPented {

    fun isValidMobile(phone: String): Boolean {
        return if (!Pattern.matches("[a-zA-Z]+", phone)) {
            phone.length > 6 && phone.length <= 10
        } else false
    }


}