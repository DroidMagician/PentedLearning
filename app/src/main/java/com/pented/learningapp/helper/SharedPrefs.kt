package com.pented.learningapp.helper


import android.content.Context
import android.content.SharedPreferences
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.pented.learningapp.authScreens.model.VerifyOTPResponseModel
import java.lang.reflect.Type
import java.util.*


object SharedPrefs {

    private const val REGISTER_DATA = "registerDetail"
    private const val LOGIN_DATA = "loginDetail"
    private const val SHARED_PREF = "sharedPreference"
    private const val LOGIN = "login"
    private const val TOKEN = "token"
    private const val FCM_TOKEN = "fcmToken"
    private const val GET_URL = "get_url"
    private const val NOTI_COUNT = "notiCount"
    const val MAILING_ADDRESS = "mailingAddress"
    const val STORE_EMAIL_PASS = "STOREEMAILPASS"
    const val SELECTED_LANGUAGE = "SELECTED_LANGUAGE"


     fun getSharedPreference(context: Context): SharedPreferences? {
        var sp: SharedPreferences? = null
        try {
            sp = context.getSharedPreferences(SHARED_PREF, Context.MODE_PRIVATE)
        } catch (ignored: Exception) {

        }

        return sp
    }

    fun getLoginDetail(context: Context): VerifyOTPResponseModel.Data? {
        val gson = Gson()
        val json = getSharedPreference(context)
            ?.getString(LOGIN_DATA, "")
        return gson.fromJson<VerifyOTPResponseModel.Data>(json, VerifyOTPResponseModel.Data::class.java)
    }

    fun storeLoginDetail(
        context: Context,
        userObject: VerifyOTPResponseModel.Data
    ) {
        val prefsEditor = getSharedPreference(context)?.edit()
        val gson = Gson()
        val json = gson.toJson(userObject)
        prefsEditor?.putString(LOGIN_DATA, json)
        prefsEditor?.apply()
    }

    fun setMailingAddressFlag(context: Context, isSet: Boolean) {
        val prefsEditor = getSharedPreference(context)?.edit()
        prefsEditor?.putBoolean(MAILING_ADDRESS, isSet)
        prefsEditor?.apply()
    }

    fun getMailingAddressFlag(context: Context): Boolean {
        return getSharedPreference(context)?.getBoolean(MAILING_ADDRESS, false) ?: false
    }


    fun setSelectedLanguage(context: Context, value: Int) {
        val prefsEditor = getSharedPreference(context)?.edit()
        prefsEditor?.putInt(SELECTED_LANGUAGE, value)
        prefsEditor?.apply()
    }

    fun getSelectedLanguage(context: Context): Int {
        return getSharedPreference(context)?.getInt(SELECTED_LANGUAGE, -1) ?: -1
    }

//    fun storeRegisterDetail(
//        context: Context,
//        userObject: LoginResponseModel
//    ) {
//
//        val prefsEditor = getSharedPreference(context)?.edit()
//        val gson = Gson()
//        val json = gson.toJson(userObject)
//        prefsEditor?.putString(REGISTER_DATA, json)
//        prefsEditor?.apply()
//    }


    fun isLoggedIn(
        context: Context
    ): Boolean {
        return getSharedPreference(context)?.getBoolean(LOGIN, false) ?: false
    }


    fun setLoggedIn(
        context: Context,
        boolean: Boolean
    ) {
        val prefsEditor = getSharedPreference(context)?.edit()
        prefsEditor?.putBoolean(LOGIN, boolean)
        prefsEditor?.apply()
    }

    fun setCount(
        context: Context,
        count: Int
    ) {
        val prefsEditor = getSharedPreference(context)?.edit()
        prefsEditor?.putInt(NOTI_COUNT, count)
        prefsEditor?.apply()
    }

    fun getCount(
        context: Context
    ): Int {
        return getSharedPreference(context)?.getInt(NOTI_COUNT, 0) ?: 0
    }


    fun storeToken(context: Context, string: String?) {
        val prefsEditor = getSharedPreference(context)?.edit()
        prefsEditor?.putString(TOKEN, string)
        prefsEditor?.apply()
    }

    fun clearToken(context: Context) {
        getSharedPreference(context)?.edit()?.remove(TOKEN)?.apply()
    }

    fun getToken(context: Context): String? {
        return getSharedPreference(context)?.getString(TOKEN, "")
    }

    fun clearPref(context: Context, prefName: String) {
        getSharedPreference(context)?.edit()?.remove(prefName)?.apply()
    }

    fun storeFcmToken(context: Context, string: String) {

        val prefsEditor = getSharedPreference(context)?.edit()
        prefsEditor?.putString(FCM_TOKEN, string)
        prefsEditor?.apply()
    }

    fun getFcmToken(context: Context): String? {
        return getSharedPreference(context)?.getString(FCM_TOKEN, "")
    }


    fun removeAll(context: Context) {
        getSharedPreference(context)?.edit()?.clear()?.apply()
    }

    fun storeEmailPass(context: Context, arrayList: ArrayList<HashMap<String, String?>>) {
        val db = getSharedPreference(context)

        val collection: SharedPreferences.Editor? = db?.edit()
        val gson = Gson()
        val arrayList1 = gson.toJson(arrayList)

        collection?.putString(STORE_EMAIL_PASS, arrayList1)
        collection?.apply()
    }

    fun getEmailPass(
        context: Context
    ): ArrayList<HashMap<String, String?>> {
        val db = getSharedPreference(context)
        val gson = Gson()
        val arrayListString: String? = db?.getString(STORE_EMAIL_PASS, null)
        if (arrayListString != null) {
            val type: Type = object : TypeToken<ArrayList<HashMap<String, String?>?>?>() {}.type
            return gson.fromJson(arrayListString, type)
        } else {
            return ArrayList<HashMap<String, String?>>()
        }
    }

    fun setURL(context: Context, url: String) {
        val prefsEditor = getSharedPreference(context)?.edit()
        prefsEditor?.putString(GET_URL, url)
        prefsEditor?.apply()
    }

    fun getURL(context: Context): String? {
        return getSharedPreference(context)?.getString(GET_URL, "")
    }
}
