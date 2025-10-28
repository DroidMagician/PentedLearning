package com.pented.learningapp.base

import android.content.Intent
import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import androidx.fragment.app.Fragment
import com.airbnb.lottie.LottieAnimationView
import com.google.gson.Gson
import com.pented.learningapp.helper.Constants
import com.pented.learningapp.helper.Utils
import com.pented.learningapp.BR
import java.io.Serializable
import java.util.*

//import com.meddietscore.BR


abstract class BaseFragment<T : ViewDataBinding> : Fragment() {

    abstract fun layoutID(): Int

    abstract fun viewModel(): BaseViewModel

    abstract fun initFragment()
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = DataBindingUtil.inflate<T>(inflater, layoutID(), container, false)
        binding.lifecycleOwner = this
        binding.setVariable(BR.viewModel, viewModel())
        return binding.root
    }

    protected fun showMessage(
        message: String,
        context: Context = requireActivity(),
        view: View = binding.root
    ) {
        Toast.makeText(context, message,Toast.LENGTH_SHORT).show()
    }

    protected fun showErrorMessage(
        message: String,
        context: Context = requireActivity(),
        view: View = BaseActivity.binding.root
    ) {
        Toast.makeText(context, message,Toast.LENGTH_SHORT).show()
//        Utils.showToastPopup(
//            context, view,
//            message, "error"
//        )
    }

    fun getViewModel(): Any = viewModel()
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initFragment()
    }

    fun startActivity(cls: Class<*>) {
        getAct().startActivity(cls)
    }

    fun startActivityWithData(cls: Class<*>, obj: Any) {
        val intent = Intent(getAct(), cls)
        if (obj is Serializable) intent.putExtra(Constants.EXTRA, obj)
        getAct().startActivity(intent)
        getAct().overridePendingTransitionEnter()
    }

    fun startActivityWithDataKey(cls: Class<*>, obj: Any, key: String) {
        val intent = Intent(getAct(), cls)
        if (obj is Serializable) intent.putExtra(key, obj)
        getAct().startActivity(intent)
        getAct().overridePendingTransitionEnter()
    }

    fun getObjectFromExtra(obj: Any): Any {

        val gson = Gson()
        var objResponse = gson.fromJson(
            getAct().intent.getStringExtra(Constants.EXTRA),
            obj::class.java
        )
        return objResponse
    }

    fun startActivityWithObjectData(cls: Class<*>, obj: Any) {
        var dataString: String = Gson().toJson(obj)
        val intent = Intent(getAct(), cls)
        intent.putExtra(Constants.EXTRA, dataString)
        getAct().startActivity(intent)
        getAct().overridePendingTransitionEnter()
    }

    fun startActivitywithResult(cls: Class<*>, resultCode: Int) {
        getAct().startActivitywithResult(cls, resultCode)
    }

    fun startActivityWithStringData(cls: Class<*>, venueName: String) {
        getAct().startActivityWithStringData(cls, venueName)
    }

    private fun getAct(): BaseActivity<*> {
        return activity as BaseActivity<*>
    }

    companion object {
        lateinit var binding: ViewDataBinding
    }
}