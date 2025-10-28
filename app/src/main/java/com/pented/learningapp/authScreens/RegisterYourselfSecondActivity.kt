package com.pented.learningapp.authScreens

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.ImageView
import androidx.lifecycle.ViewModelProvider
import com.pented.learningapp.R
import com.pented.learningapp.base.BaseActivity
import com.pented.learningapp.databinding.ActivityRegisterYourselfBinding
import com.pented.learningapp.helper.JustCopyItVIewModel
import kotlinx.android.synthetic.main.activity_register_yourself_second.*

class RegisterYourselfSecondActivity : BaseActivity<ActivityRegisterYourselfBinding>() {

    lateinit var ivArrayDotsPager: Array<ImageView>
    override fun viewModel() = ViewModelProvider(this).get(JustCopyItVIewModel::class.java)
    override fun layoutID() = R.layout.activity_register_yourself_second
    lateinit var justCopyItVIewModel: JustCopyItVIewModel
    override fun initActivity() {
        justCopyItVIewModel = (getViewModel() as JustCopyItVIewModel)
        listener()
    }

    private fun listener() {
        llBack.setOnClickListener {
            onBackPressed()
        }
    }
}