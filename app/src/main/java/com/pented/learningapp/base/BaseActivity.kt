package com.pented.learningapp.base

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.content.pm.PackageManager.GET_META_DATA
import android.net.ConnectivityManager
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import com.google.gson.Gson
import com.pented.learningapp.BR
import com.pented.learningapp.R
import com.pented.learningapp.helper.Constants
import com.pented.learningapp.multiLanguageSupport.LocaleManager
import java.io.Serializable


/*
*
*   This activity_otp_verification is the base of all activity_otp_verification, While extending it to another activity_otp_verification
*   please pass corresponding activity_otp_verification's auto generated Data Binding Class.
*   also please do not forgot to pass ViewModel as we have use ViewModel in XML.
*
*/

abstract class BaseActivity<T : ViewDataBinding> : AppCompatActivity() {


    abstract fun layoutID(): Int

    abstract fun viewModel(): BaseViewModel

    abstract fun initActivity()

    var savedInstanceState: Bundle? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        resetTitles()
        this.savedInstanceState = savedInstanceState
        binding = DataBindingUtil.setContentView<T>(this, layoutID())
        binding.lifecycleOwner = this
        binding.setVariable(BR.viewModel, viewModel())
//        binding.setVariable(BR.handler, this)
        initActivity()

    }

    override fun attachBaseContext(newBase: Context?) {
        super.attachBaseContext(newBase?.let { LocaleManager.setLocale(it) })

    }
    protected open fun resetTitles() {
        try {
            val info = packageManager.getActivityInfo(componentName, GET_META_DATA)
            if (info.labelRes != 0) {
                setTitle(info.labelRes)
            }
        } catch (e: PackageManager.NameNotFoundException) {
            e.printStackTrace()
        }
    }
//    open fun getBaseSavedInstanceState():Bundle{
//        return savedInstanceState
//    }

    fun getViewModel(): Any = viewModel()

    fun startActivity(cls: Class<*>) {
        val intent = Intent(this, cls)
        startActivity(intent)
        overridePendingTransitionEnter()
    }

    fun startActivityForResults(cls: Class<*>, code: Int) {
        val intent = Intent(this, cls)
        startActivityForResult(intent, code)
        overridePendingTransitionEnter()
    }

    fun startActivitywithResult(cls: Class<*>, resultCode: Int) {
        val intent = Intent(this, cls)
        startActivityForResult(intent, resultCode)
        overridePendingTransitionEnter()
    }

    fun startActivityWithStringData(cls: Class<*>, venueName: String) {
        val intent = Intent(this, cls)
        val bundle = Bundle()
        bundle.putString("DATA", venueName)
        intent.putExtras(bundle)
        startActivity(intent)
        overridePendingTransitionEnter()
    }

    fun startActivityWithData(cls: Class<*>, obj: Any) {
        val intent = Intent(this, cls)
        if (obj is Serializable) intent.putExtra(Constants.EXTRA, obj)
        startActivity(intent)
        overridePendingTransitionEnter()
    }

    fun startActivityWithDataKey(cls: Class<*>, obj: Any, key: String) {
        val intent = Intent(this, cls)
        if (obj is Serializable) intent.putExtra(key, obj)
        startActivity(intent)
        overridePendingTransitionEnter()
    }

    fun getObjectFromExtra(obj: Any): Any {

        val gson = Gson()
        var objResponse = gson.fromJson(intent.getStringExtra(Constants.EXTRA), obj::class.java)
        return objResponse
    }

    fun startActivityWithObjectData(cls: Class<*>, obj: Any) {
        var dataString: String = Gson().toJson(obj)
        val intent = Intent(this, cls)
        intent.putExtra(Constants.EXTRA, dataString)
        startActivity(intent)
        overridePendingTransitionEnter()
    }


    override fun onBackPressed() {
        super.onBackPressed()
        overridePendingTransitionExit()
    }

//    protected fun initTransparentToolbar(
//        title: String = "",
//        headingColor: Int = R.color.orange,
//        backButtonColor: Int = R.color.colorPrimary,
//        semiTitle: String = "",
//        isMoreVisible: Boolean = false,
//        isChatVisible: Boolean = false,
//        isPlusVisible: Boolean = false,
//        colorBgToolbar: Int = android.R.color.transparent
//    ) {
//        setSupportActionBar(toolbar)
//        supportActionBar?.let {
//            val drawable: Drawable? =
//                ContextCompat.getDrawable(it.themedContext, R.drawable.ic_back_arrow)
//            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
//                drawable?.setTint(ContextCompat.getColor(this, backButtonColor))
//            }
//            it.setHomeAsUpIndicator(drawable)
//            it.setDisplayShowTitleEnabled(false)
//            it.setDisplayHomeAsUpEnabled(false)
//            it.setDisplayShowHomeEnabled(false)
//        }
//        toolbar.setBackgroundColor(ContextCompat.getColor(this, colorBgToolbar))
//        toolbar.setNavigationOnClickListener { v -> onBackPressed() }
//        icBack.setOnClickListener { v -> onBackPressed() }
//        if (isMoreVisible) {
//            imgMore.visibility = View.VISIBLE
//        } else {
//            imgMore.visibility = View.GONE
//        }
//        if (isPlusVisible) {
//            imgPlus.visibility = View.VISIBLE
//        } else {
//            imgPlus.visibility = View.GONE
//        }
//        imgChat.visibility = if (isChatVisible) View.VISIBLE else View.GONE
//        tv_heading.text = title
//        tv_extra.text = semiTitle
//        tv_heading.setTextColor(ContextCompat.getColor(this, headingColor))
//    }

    fun onBackPress(view: View) {
        view.setOnClickListener {
            onBackPressed()
        }
    }


    /**
     * Overrides the pending Activity transition by performing the "Enter" animation.
     */


    fun overridePendingTransitionEnter() {
        overridePendingTransition(R.anim.slide_from_right, R.anim.slide_to_left)
    }

    /**
     * Overrides the pending Activity transition by performing the "Exit" animation.
     */
    private fun overridePendingTransitionExit() {
        overridePendingTransition(R.anim.slide_from_left, R.anim.slide_to_right)
    }

    fun overridePendingTransitionDown() {
        overridePendingTransition(R.anim.slide_up, R.anim.slide_down)
    }

    protected fun showMessage(message: String, context: Context = this, view: View = binding.root) {
        Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
//        Utils.showToastPopup(
//            context, view,
//            message, "validation"
//        )
    }

    protected fun showErrorMessage(
            message: String,
            context: Context = this,
            view: View = binding.root
    ) {
        Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
//        Utils.showToastPopup(
//            context, view,
//            message, "error"
//        )
    }

    @SuppressLint("MissingPermission")
    fun isConnected(context: Context): Boolean {
        val connectivityManager =
            context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val info = connectivityManager.activeNetworkInfo
        return info != null && info.isConnected
    }



    companion object {
        lateinit var binding: ViewDataBinding
    }


}