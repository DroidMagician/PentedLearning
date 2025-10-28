package com.pented.learningapp.retrofit

import android.content.Context
import com.pented.learningapp.MyApplication
import com.pented.learningapp.R
import com.pented.learningapp.helper.Utils
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.observers.DisposableObserver
import io.reactivex.schedulers.Schedulers
import retrofit2.Response

open class BaseAPITask {

    private fun isInternetAvailable(context: Context): Boolean {
        return Utils.isConnected(context)
    }

    private fun noInternetError(context: Context): String {
        return context.getString(R.string.e_no_internet)
    }


    protected fun <T> getRequest(
        request: Observable<Response<T>>,
        mListener: OnResponseListener,
        requestCode: Int
    ): DisposableObserver<*>? {
        return if (isInternetAvailable(MyApplication.getInstance())) {
            //Log.e("FCM TOKEN","${SharedPrefs.getFcmToken(MyApplication.getInstance())}")
            request.subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeWith(APICallback(mListener, requestCode, request))
        } else {
            mListener.onResponseError(noInternetError(MyApplication.getInstance()), requestCode, 0)
            null
        }

    }

}