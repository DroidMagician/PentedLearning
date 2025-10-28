package com.pented.learningapp.retrofit

import android.app.Dialog
import android.app.TaskStackBuilder
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.util.Log
import android.view.Window
import android.widget.Button
import android.widget.LinearLayout
import android.widget.TextView
import com.pented.learningapp.MyApplication
import com.pented.learningapp.R
import com.pented.learningapp.authScreens.GetStartedActivity
import com.pented.learningapp.helper.SharedPrefs
import com.pented.learningapp.homeScreen.subscription.ChooseYourSubscriptionActivity
import io.reactivex.Observable
import io.reactivex.disposables.Disposable
import io.reactivex.observers.DisposableObserver
import org.json.JSONObject
import retrofit2.Response

class APICallback<T>(
    private val mListener: OnResponseListener,
    private val requestCode: Int,
    val request: Observable<Response<T>>
) :
    DisposableObserver<Response<T>>() {

    protected var mDisposable: Disposable? = null
    val Message = "Message"
    override fun onComplete() {

    }

    override fun onNext(response: Response<T>) {
        /*- OK(200, "OK"),
        - NON_AUTHORITATIVE_INFORMATION(203, "Non-Authoritative Information")
        - NO_CONTENT(204, "No Content")
        - ALREADY_REPORTED(208, "Already Reported")
        - BAD_REQUEST(400, "Bad Request")
        - UNAUTHORIZED(401, "Unauthorized")
        - NOT_FOUNToast.makeText(getContext(), e.getMessage(), Toast.LENGTH_LONG).show();D(404, "Not Found")
        - INTERNAL_SERVER_ERROR(500, "Internal Server Error")*/

        var jobj: JSONObject? = null
        var mApplication = MyApplication.getInstance()
        try {
            jobj = JSONObject(response.errorBody()?.string())
        } catch (e: Exception) {
            jobj = JSONObject("{}")
        }
        Log.e("Response Code", "Is ${response.code()}")
        // Log.e("Response New Token", "Is ${response.headers()["Refresh-Token"]?.takeLast(10)}")
//        SharedPrefs.storeToken(
//            MyApplication.getInstance(),
//            (response.headers()["Refresh-Token"]
//                ?: SharedPrefs.getToken(MyApplication.getInstance())).toString()
//        )
        // Log.e("SharedPref","Updated Value"+ SharedPrefs.getToken(MyApplication.getInstance())?.takeLast(10))
        when (response.code()) {
            200 -> {

                mListener.onResponseReceived(response.body(), requestCode)
                //callRefreshToken()
            }
            201 -> {

                mListener.onResponseReceived(response.body(), requestCode)
                //callRefreshToken()
            }
            203 -> {

                mListener.onResponseError(
                    jobj?.getString(Message) ?: "Non-Authoritative Information",
                    requestCode,
                    response.code()
                )
            }
            204 -> {

                mListener.onResponseError(
                    "No Content",
                    requestCode,
                    response.code()
                )
            }
            208 -> {

                mListener.onResponseError(
                    "You have used this address in your product. You can't delete this address",
                    requestCode,
                    response.code()
//                mListener.onResponseError(
//                jobj?.getString(Message) ?: "Already Reported",
//                requestCode,
//                response.code()
                )
            }
            302 -> {

                mListener.onResponseError(
                    jobj?.getString(Message) ?: "Check input",
                    requestCode,
                    response.code()
                )
            }
            400 ->
                if (jobj?.has("errors") == true) {
                    mListener.onResponseError(

                        jobj?.getJSONObject("errors")?.getString("msg") ?: "Bad Request",
                        requestCode,
                        response.code()
                    )
                }

            401 ->
            {
                val loginIntent = Intent(mApplication, GetStartedActivity::class.java)
                loginIntent.putExtra("TIMEOUT", true)
                val preferences = SharedPrefs.getSharedPreference(mApplication)
                val editor = preferences?.edit()
                editor?.clear()
                editor?.apply()
                TaskStackBuilder.create(mApplication)
                    .addNextIntentWithParentStack(loginIntent)
                    .startActivities()
                mListener.onResponseError(
                    jobj?.getString(Message) ?: "Something went wrong, Please try later",
                    requestCode,
                    response.code()
                )
            }

            404 -> {
                if (jobj?.has("errors") == true) {
                    mListener.onResponseError(

                        jobj?.getJSONObject("errors")?.getString("msg") ?: "Bad Request",
                        requestCode,
                        response.code()
                    )
                }
            }
            422 -> {
                if (jobj?.has("errors") == true) {
                    mListener.onResponseError(

                        jobj?.getJSONObject("errors")?.getString("msg") ?: "Bad Request",
                        requestCode,
                        response.code()
                    )
                }
            }
            406 -> {
//                showReminderDialog()
                val loginIntent = Intent(mApplication, GetStartedActivity::class.java)
                loginIntent.putExtra("TIMEOUT", true)
                val preferences = SharedPrefs.getSharedPreference(mApplication)
             val editor = preferences?.edit()
             editor?.clear()
            editor?.apply()
                TaskStackBuilder.create(mApplication)
                    .addNextIntentWithParentStack(loginIntent)
                    .startActivities()
                mListener.onResponseError(
                    jobj?.getString(Message) ?: "Something went wrong, Please try later",
                    requestCode,
                    response.code()
                )
            }

            500 -> {
                if (jobj?.has("errors") == true) {
                    mListener.onResponseError(
                        jobj?.getJSONObject("errors")?.getString("msg") ?: "Bad Request",
                        requestCode,
                        response.code()
                    )
                }
                else{
                    mListener.onResponseError(
                        "Something went wrong, Please try later",
                        requestCode,
                        0
                    )
                }
            }

            else -> {
                mListener.onResponseError(
                    "Something went wrong, Please try later",
                    requestCode,
                    0
                )
            }
        }
    }

    //    fun callRefreshToken() {
//        SharedPrefs.clearToken(MyApplication.getInstance())
//        val refreshTokenRequestModel = RefreshTokenRequestModel()
//        Log.e("API_KEY","API ${SharedPrefs.getToken(MyApplication.getInstance()) ?: null}")
//        refreshTokenRequestModel.refresh_token = SharedPrefs.getLoginDetail(MyApplication.getInstance())?.data?.refresh_token
//
//        mDisposable = APITask.getInstance()
//            .callAccesToken(object : OnResponseListener {
//            override fun <T> onResponseReceived(response: T, requestCode: Int) {
//                SharedPrefs.storeToken(MyApplication.getInstance(), (response as RegisterResponceModel).data.token as String)
//                request.subscribeOn(Schedulers.io())
//                    .observeOn(AndroidSchedulers.mainThread())
//                    .subscribeWith(APICallback(mListener, requestCode, request))
//            }
//
//            override fun onResponseError(message: String, requestCode: Int, responseCode: Int) {
//                mListener.onResponseError("Something went wrong, Please try later", requestCode, 0)
//            }
//        }, refreshTokenRequestModel,requestCode)
//    }
    override fun onError(e: Throwable) {
        //if (e is java.net.ConnectException) {
        Log.e("Error", "APi ${e.localizedMessage}")
        mListener.onResponseError(e.localizedMessage, requestCode, 0)
        //}
    }


}
