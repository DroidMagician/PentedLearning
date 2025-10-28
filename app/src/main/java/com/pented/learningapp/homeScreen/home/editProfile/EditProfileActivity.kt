package com.pented.learningapp.homeScreen.home.editProfile

import android.animation.ValueAnimator
import android.app.Dialog
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.util.Log
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.view.animation.*
import android.widget.Button
import android.widget.LinearLayout
import androidx.lifecycle.ViewModelProvider
import com.pented.learningapp.MainActivity
import com.pented.learningapp.R
import com.pented.learningapp.authScreens.GetStartedActivity
import com.pented.learningapp.authScreens.model.GetDropdownResponseModel
import com.pented.learningapp.authScreens.model.GetLanguagesResponseModel
import com.pented.learningapp.authScreens.model.VerifyOTPResponseModel
import com.pented.learningapp.base.BaseActivity
import com.pented.learningapp.base.BaseViewModel
import com.pented.learningapp.databinding.ActivityEditProfileBinding
import com.pented.learningapp.helper.Constants
import com.pented.learningapp.helper.SharedPrefs
import com.pented.learningapp.helper.Utils
import com.pented.learningapp.homeScreen.home.editProfile.viewModel.EditProfileVM
import com.pented.learningapp.homeScreen.subscription.ChooseYourSubscriptionActivity
import kotlinx.android.synthetic.main.activity_edit_profile.*
import kotlinx.android.synthetic.main.transparent_progressbar_layout.*
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList


class EditProfileActivity : BaseActivity<ActivityEditProfileBinding>() {

    override fun layoutID() = R.layout.activity_edit_profile

    lateinit var editProfileVM: EditProfileVM
    var isProfileDetailsExpand = false

    companion object {
        var studentProfile: VerifyOTPResponseModel.Data? = null
    }

    var selectedPostion = 0
    var selectedPostionLanguage = 0
    var dropdownList = ArrayList<GetDropdownResponseModel.Data>()
    var languagesList = ArrayList<GetLanguagesResponseModel.Data>()

    override fun viewModel(): BaseViewModel = ViewModelProvider(this).get(EditProfileVM::class.java)
    override fun initActivity() {
        init()
        observer()
        listner()
    }

    private fun listner() {
        ivEditProfile.setOnClickListener {
            startActivity(EditProfileDetailsActivity::class.java)
        }
        layoutforprofileimage.setOnClickListener {
            startActivity(EditProfileDetailsActivity::class.java)
        }
        lilSubscription.setOnClickListener {
            startActivity(ChooseYourSubscriptionActivity::class.java)
        }
        btnLogout.setOnClickListener {
            showLogoutDialog()

        }
        btnDeleteAccount.setOnClickListener {
            showDeleteAccountDialog()

        }
        headerLayoutProfile.setOnClickListener {
            startActivity(EditProfileDetailsActivity::class.java)
//            if (isProfileDetailsExpand) {
//                isProfileDetailsExpand = false
//                imgProfileArrow.animate().rotation(0f).start()
//                expandOrCollapseView(bottomProfile, false)
//            } else {
//                isProfileDetailsExpand = true
//                imgProfileArrow.animate().rotation(180f).start()
//                expandOrCollapseView(bottomProfile, true)
//            }

        }
        ivBack.setOnClickListener {
            onBackPressed()
            startActivity(MainActivity::class.java)
            finishAffinity()
        }
    }

    override fun onBackPressed() {
        super.onBackPressed()
        startActivity(MainActivity::class.java)
        finishAffinity()
    }
    fun showLogoutDialog() {
        val dialog = Dialog(this@EditProfileActivity)
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.setCancelable(false)
        dialog.setContentView(R.layout.dialog_logout)
        dialog.getWindow()?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        dialog.window?.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT)
        var lilRemindMeLater = dialog.findViewById<LinearLayout>(R.id.lilRemindMeLater)
        var llSubscribe = dialog.findViewById<LinearLayout>(R.id.llSubscribe)
        var btnLogout = dialog.findViewById<Button>(R.id.btnLogout)


        lilRemindMeLater.setOnClickListener {
            dialog.dismiss()
        }

        btnLogout.setOnClickListener {
            dialog.dismiss()
            val preferences = SharedPrefs.getSharedPreference(this)
            val editor = preferences?.edit()
            editor?.clear()
            editor?.apply()
            startActivity(GetStartedActivity::class.java)
            finishAffinity()
        }

        llSubscribe.setOnClickListener {
            dialog.dismiss()
            val preferences = SharedPrefs.getSharedPreference(this)
            val editor = preferences?.edit()
            editor?.clear()
            editor?.apply()
            startActivity(GetStartedActivity::class.java)
            finishAffinity()
        }

        dialog.show()
    }

    fun showDeleteAccountDialog() {
        val dialog = Dialog(this@EditProfileActivity)
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.setCancelable(false)
        dialog.setContentView(R.layout.dialog_delete_account)
        dialog.getWindow()?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        dialog.window?.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT)
        var lilRemindMeLater = dialog.findViewById<LinearLayout>(R.id.lilRemindMeLater)
        var llSubscribe = dialog.findViewById<LinearLayout>(R.id.llSubscribe)
        var btnLogout = dialog.findViewById<Button>(R.id.btnLogout)


        lilRemindMeLater.setOnClickListener {
            dialog.dismiss()
        }

        btnLogout.setOnClickListener {
            dialog.dismiss()
            editProfileVM.deleteAccount()
        }

        llSubscribe.setOnClickListener {
            dialog.dismiss()
            editProfileVM.deleteAccount()
        }

        dialog.show()
    }
    fun expandOrCollapseView(v: View, expand: Boolean) {
        if (expand) {
            v.measure(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT)
            val targetHeight = v.measuredHeight
            //v.layoutParams.height = 0
            v.visibility = View.VISIBLE
            val valueAnimator = ValueAnimator.ofInt(targetHeight)
            valueAnimator.addUpdateListener { animation ->
                //    v.layoutParams.height = animation.animatedValue as Int
                v.requestLayout()
            }
            valueAnimator.interpolator = DecelerateInterpolator()
            valueAnimator.duration = 500
            valueAnimator.start()
        } else {
            val initialHeight = v.measuredHeight
            val valueAnimator = ValueAnimator.ofInt(initialHeight, 0)
            valueAnimator.interpolator = DecelerateInterpolator()
            valueAnimator.addUpdateListener { animation ->
                //      v.layoutParams.height = animation.animatedValue as Int
                v.requestLayout()
                if (animation.animatedValue as Int == 0) v.visibility = View.GONE
            }
            valueAnimator.interpolator = DecelerateInterpolator()
            valueAnimator.duration = 500
            valueAnimator.start()
        }
    }

    //sample usage
    private fun observer() {
        editProfileVM.observedErrorMessageChanges().observe(this, { event ->
            event?.getContentIfNotHandled()?.let {
                showErrorMessage(it, this, mainFrame)
            }
        })

        editProfileVM.observerDropdownChange().observe(this, { event ->
            event?.getContentIfNotHandled()?.let {
                dropdownList.clear()
                dropdownList.addAll(it.data)
                //hideDialog()

            }
        })

        editProfileVM.observerLanguageChange().observe(this, { event ->
            event?.getContentIfNotHandled()?.let {
                languagesList.clear()
                languagesList.addAll(it.data)
                editProfileVM.callGetProfileData()
                //hideDialog()

            }
        })


        editProfileVM.observedStudentProfileData().observe(this, { event ->
            event?.getContentIfNotHandled()?.let {
                var userImage = it.data?.S3Bucket?.FileName?.let { it1 ->
                    it?.data?.S3Bucket?.BucketFolderPath?.let { it2 ->
                        Utils.getUrlFromS3Details(
                            BucketFolderPath = it2,
                            FileName = it1
                        )
                    }
                }
                studentProfile = it.data

                for (i in 0 until dropdownList.size) {
                    if (dropdownList[i].Id?.toInt() == studentProfile?.StandardId?.toInt()) {
                        selectedPostion = i
                        break
                    }
                }
                for (i in 0 until languagesList.size) {
                    if (languagesList[i].LanguageId == studentProfile?.LanguageId?.toInt()) {
                        selectedPostionLanguage = i
                    }
                }
                Utils.loadCircleImageUser(image, userImage.toString())
                if (it.data?.SubscriptionExpiryDate.isNullOrBlank()) {
                    lilExpirePeriod.visibility = View.GONE
                } else {
                    lilExpirePeriod.visibility = View.VISIBLE
                }

                if (it.data?.SubscriptionExpiryDate != null) {
                    val inputFormat = SimpleDateFormat("yyyy-MM-dd'T'hh:mm:ss")
                    val outputFormat = SimpleDateFormat("dd-MM-yyyy hh:mm a")
                    val date: Date = inputFormat.parse(it.data?.SubscriptionExpiryDate)
                    //  val formattedDate: String = outputFormat.format(date)
                    var cal = Calendar.getInstance()
                    cal.setTime(date)

                    val seconds: Long =
                        (cal.getTimeInMillis() - Calendar.getInstance().getTimeInMillis()) / 1000
                    val hours = (seconds / 3600).toInt()
                    Log.e("Hours ${hours / 24}", "Seconds$seconds")
                 //   txtExpiryDate.text = "Trial is ending in ${hours / 24} days !!"

                }


                txtName.text = it?.data?.Name
                txtNumber.text = it?.data?.MobileNumber
                txtRenking.text = "${(it?.data?.MonthPoints ?: "0")}"
                txtCourseDone.text = "${it?.data?.CourseCompleted.toString()} %"
                txtPoints.text = "${it?.data?.Points.toString()}"
                txtPointsDesc.text = "${it?.data?.Points.toString()} Points"
                tvTheChamp.text = "${it?.data?.LevelTitle.toString()}"
                txtEmail.text = "${it?.data?.Email}"
                var dividend = it.data.Points?.toInt() ?: 0
                val divisor = 1000
                val quotient = dividend / divisor
                val remainder = dividend % divisor
                var finalMax = (quotient+1) * 1000
                Log.e("FinalMax","Is === ${finalMax}")
                Log.e("quotient","Is === ${quotient}")
                Log.e("remainder","Is === ${remainder}")
                progressView1.max = finalMax.toFloat()
                it.data.Points?.toFloat().let {
                    if (it != null) {
                        progressView1.progress = it
                    }
                }

                txtSchoolName.text = "${it?.data?.SchoolName}"
                spClass.text = "${dropdownList[selectedPostion].Value}"
                txtLanguage.text = "${languagesList[selectedPostionLanguage].LanguageName}"
                txtAddress.text = "${it?.data?.Adress}"
            }
        })

        editProfileVM.observedChanges().observe(this, { event ->
            event?.getContentIfNotHandled()?.let {
                when (it) {
                    Constants.VISIBLE -> {
                        showDialog()
                    }
                    Constants.HIDE -> {
                        hideDialog()
                    }
                    Constants.NAVIGATE -> {

                    }
                    else -> {
                        showMessage(it, this, mainFrame)
                    }
                }
            }
        })
        editProfileVM.observedChangesForDeleteAccount().observe(this, { event ->
            event?.getContentIfNotHandled()?.let {
                when (it) {
                    Constants.VISIBLE -> {
                        showDialog()
                    }
                    Constants.HIDE -> {
                        hideDialog()
                    }
                    Constants.NAVIGATE -> {
                        val preferences = SharedPrefs.getSharedPreference(this)
                        val editor = preferences?.edit()
                        editor?.clear()
                        editor?.apply()
                        startActivity(GetStartedActivity::class.java)
                        finishAffinity()
                    }
                    else -> {
                        showMessage(it, this, mainFrame)
                    }
                }
            }
        })
    }

    override fun onResume() {
        super.onResume()
        if (Constants.isProfileUpdated) {
            Constants.isProfileUpdated = false
            if (Constants.isApiCalling) {
                editProfileVM.callGetDropdownList()
            }
        }
    }

    fun expandOrCollapse(v: View, exp_or_colpse: String) {
        var anim: TranslateAnimation? = null
        if (exp_or_colpse == "expand") {
            anim = TranslateAnimation(0.0f, 0.0f, (-v.height).toFloat(), 0.0f)
            v.visibility = View.VISIBLE
        } else {
            anim = TranslateAnimation(0.0f, 0.0f, 0.0f, (-v.height).toFloat())
            val collapselistener: Animation.AnimationListener =
                object : Animation.AnimationListener {
                    override fun onAnimationStart(animation: Animation?) {}
                    override fun onAnimationRepeat(animation: Animation?) {}
                    override fun onAnimationEnd(animation: Animation?) {
                        v.visibility = View.GONE
                    }
                }
            anim.setAnimationListener(collapselistener)
        }

        // To Collapse
        //
        anim.duration = 300
        anim.interpolator = AccelerateInterpolator(0.5f)
        v.startAnimation(anim)
    }

    public fun showDialog() {
        Utils.hideKeyboard(this)
        lilProgressBar.visibility = View.VISIBLE
        animationView.visibility = View.VISIBLE
        Utils.getNonWindowTouchable(this)
    }

    public fun hideDialog() {
        lilProgressBar.visibility = View.GONE
        animationView.visibility = View.GONE
        Utils.getWindowTouchable(this)
    }

    private fun init() {
        editProfileVM = (getViewModel() as EditProfileVM)
        editProfileVM.callGetDropdownList()
        var cal = Calendar.getInstance()
        var monthName = SimpleDateFormat("MMMM", Locale.ENGLISH).format(cal.getTime())
        txtRanking.text = "$monthName's Point"

        var animation = AnimationUtils.loadAnimation(this@EditProfileActivity, R.anim.bounce);
        txtPoints.startAnimation(animation)


        if (Constants.isApiCalling) {

        }
    }
}