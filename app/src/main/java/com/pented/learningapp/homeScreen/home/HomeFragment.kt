package com.pented.learningapp.homeScreen.home

import android.app.Dialog
import android.content.ActivityNotFoundException
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.net.Uri
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.View
import android.view.Window
import android.view.WindowManager
import android.view.animation.AnimationUtils
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.request.RequestOptions
import com.pented.learningapp.BR
import com.pented.learningapp.R
import com.pented.learningapp.activity.NotificationActivity
import com.pented.learningapp.adapter.SubjectAdapter
import com.pented.learningapp.authScreens.model.GetDropdownResponseModel
import com.pented.learningapp.authScreens.model.GetSchoolNameResponseModel
import com.pented.learningapp.base.BaseFragment
import com.pented.learningapp.base.BaseViewModel
import com.pented.learningapp.base.BindingAdapter
import com.pented.learningapp.databinding.FragmentTestBinding
import com.pented.learningapp.helper.*
import com.pented.learningapp.homeScreen.home.editProfile.EditProfileActivity
import com.pented.learningapp.homeScreen.home.editProfile.EditProfileActivity.Companion.studentProfile

import com.pented.learningapp.homeScreen.home.examBlueprints.ExamBlueprintsActivity
import com.pented.learningapp.homeScreen.home.impQuestions.ImpSubjectListActivity
import com.pented.learningapp.homeScreen.home.liveClasses.TodayLiveClassesActivity
import com.pented.learningapp.homeScreen.home.model.GetHomeDataResponseModel
import com.pented.learningapp.homeScreen.home.otherUserProfile.OtherStudentProfileActivity
import com.pented.learningapp.homeScreen.home.viewModel.HomeVM
import com.pented.learningapp.homeScreen.home.weekendTestSeries.WeekEndTestSeriesActivity
import com.pented.learningapp.homeScreen.leaderboard.model.GetLeaderboardResponseModel
import com.pented.learningapp.homeScreen.subjects.adapter.AdapterSubjectsRecycler
import com.pented.learningapp.homeScreen.subscription.ChooseYourSubscriptionActivity
import com.pented.learningapp.model.SubjectModel
import com.pented.learningapp.notification.NotificationActivityNew
import com.pented.learningapp.widget.chipview.Chip
import com.pented.learningapp.widget.chipview.ChipViewAdapter
import kotlinx.android.synthetic.main.activity_edit_profile.image
import kotlinx.android.synthetic.main.activity_edit_profile.lilExpirePeriod
import kotlinx.android.synthetic.main.activity_edit_profile.progressView1
import kotlinx.android.synthetic.main.activity_edit_profile.spClass
import kotlinx.android.synthetic.main.activity_edit_profile.tvTheChamp
import kotlinx.android.synthetic.main.activity_edit_profile.txtAddress
import kotlinx.android.synthetic.main.activity_edit_profile.txtCourseDone
import kotlinx.android.synthetic.main.activity_edit_profile.txtEmail
import kotlinx.android.synthetic.main.activity_edit_profile.txtLanguage
import kotlinx.android.synthetic.main.activity_edit_profile.txtName
import kotlinx.android.synthetic.main.activity_edit_profile.txtNumber
import kotlinx.android.synthetic.main.activity_edit_profile.txtPoints
import kotlinx.android.synthetic.main.activity_edit_profile.txtPointsDesc
import kotlinx.android.synthetic.main.activity_edit_profile.txtRenking
import kotlinx.android.synthetic.main.activity_edit_profile.txtSchoolName
import kotlinx.android.synthetic.main.fragment_home.*
import kotlinx.android.synthetic.main.transparent_progressbar_layout.*
import java.net.URLEncoder
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList


class HomeFragment : BaseFragment<FragmentTestBinding>() {
//https://blog.magezon.com/wp-content/uploads/2020/08/fashion-banner.png
    //offer image

    override fun layoutID() = R.layout.fragment_home
    lateinit var adapterRecycler: AdapterSubjectsRecycler

    override fun viewModel(): BaseViewModel = ViewModelProvider(this).get(HomeVM::class.java)
    lateinit var homeVM: HomeVM
    var phoneNumber: String? = null
    var UsersList: ArrayList<GetLeaderboardResponseModel.Student> =
        ArrayList<GetLeaderboardResponseModel.Student>()
    var filterUsersList: ArrayList<GetLeaderboardResponseModel.Student> =
        ArrayList<GetLeaderboardResponseModel.Student>()
    var timer = Timer()
    var searchUserList: ArrayList<GetSchoolNameResponseModel.Data> =
        ArrayList<GetSchoolNameResponseModel.Data>()
    var DELAY: Long = 500
    var generalTextWatcher: TextWatcher? = null
    var searchValue: String? = null
    var selectedPostion = 0
    var dropdownList = ArrayList<GetDropdownResponseModel.Data>()
    var standards = ArrayList<String>()

    companion object {
        var unreadNotiCount: Int = 0
        fun newInstance(): HomeFragment {
            return HomeFragment()
        }

        var subjectList = ArrayList<GetHomeDataResponseModel.Subject>()
    }

    override fun initFragment() {
        //getting recyclerview from xml
        init()
        observer()
        listner()

    }

    fun showCongratulationsDialog() {
        val dialog = Dialog(requireActivity())
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.setCancelable(false)
        dialog.setContentView(R.layout.dialog_congratulations)
        dialog.getWindow()?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

        var btnThanks = dialog.findViewById<Button>(R.id.btnThanks)
        var txtPoints = dialog.findViewById<TextView>(R.id.txtPoints)
        var txtTitle = dialog.findViewById<TextView>(R.id.txtTitle)
        var txtDescription = dialog.findViewById<TextView>(R.id.txtDescription)
        txtTitle.text = "You have won"
        txtPoints.text = "${Constants.registerPoints} Points"
        txtDescription.text =
            "Thank you for registration with Pented. You have won ${Constants.registerPoints} points for first 1000 students registration schemes.\n" +
                    "अब  पढ़ना होगा आसान!!!"

        btnThanks.setOnClickListener {
            dialog.dismiss()
        }
        try {
            dialog.show()
        } catch (e: Exception) {
            dialog.dismiss()
        }

    }

    private fun observer() {
        homeVM.observedErrorMessageChanges().observe(this, { event ->
            event?.getContentIfNotHandled()?.let {
                showErrorMessage(it, requireActivity(), mainFrame)
            }
        })


        homeVM.observerStudentListResponseData().observe(this, { event ->
            event?.getContentIfNotHandled()?.let {
                searchUserList.clear()
                searchUserList.addAll(it.data)
                if (searchUserList.size == 0) {
                    searchUserList.clear()
                    recycler_view_bottom.visibility = View.GONE
                    Toast.makeText(requireContext(), "No result found", Toast.LENGTH_SHORT).show()
                } else {
                    setLeaderboardData()
                }
                Utils.hideKeyboard(requireActivity())

            }
        })

        homeVM.observerDropdownChange().observe(this, { event ->
            event?.getContentIfNotHandled()?.let {
                dropdownList.clear()
                standards.clear()
                if (!it.data.isNullOrEmpty()) {
                    dropdownList.addAll(it.data)
                }
                for (standard in dropdownList) {
                    standard.Value?.let { standards.add(it) }
                }

                Log.e("standards", "is ${standards.size}")

                val aa = ArrayAdapter(
                    requireContext(), R.layout.row_spinner_class, standards
                )

                // Constants.headerlanguageid = dropdownList[0].LanguageId?.toString()
                // Constants.headerstandardid = dropdownList[0].Id
                // Set layout to use when the list of choices appear
                aa?.setDropDownViewResource(R.layout.row_dropdown_class)
                // Set Adapter to Spinner
                spClassHome?.adapter = aa

                spClassHome?.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
                    override fun onNothingSelected(parent: AdapterView<*>?) {

                    }

                    override fun onItemSelected(
                        parent: AdapterView<*>?, view: View?, position: Int, id: Long
                    ) {
                        Log.e("On Item Selection", "Value is ${position}")




                        dropdownList[position].LanguageId?.let {
                            homeVM.registerRequestModel.LanguageId = it.toString();
                            SharedPrefs.setSelectedLanguage(
                                requireContext(), it
                            )
                        }
                        homeVM.registerRequestModel.LanguageId = Constants.headerstandardid;
                        homeVM.setStandard();
                        Constants.headerstandardid = dropdownList[position].Id
//                        if (dropdownList[position].LanguageId == 1) {
//                            LocaleManager.setNewLocale(
//                                this@EditProfileDetailsActivity,
//                                LocaleManager.GUJARATI
//                            );
//                            // setNewLocale(requireActivity(), LocaleManager.GUJARATI)
//                        } else if (dropdownList[position].LanguageId == 2) {
//                            LocaleManager.setNewLocale(
//                                this@EditProfileDetailsActivity,
//                                LocaleManager.HINDI
//                            );
//                            // setNewLocale(requireActivity(), LocaleManager.HINDI)
//                        } else if (dropdownList[position].LanguageId == 3) {
//                            LocaleManager.setNewLocale(
//                                this@EditProfileDetailsActivity,
//                                LocaleManager.ENGLISH
//                            );
//                            //  setNewLocale(requireActivity(), LocaleManager.ENGLISH)
//                        }
                    }
                }
                var selectedPostion = -1
                for (i in 0 until dropdownList.size) {
                    if (dropdownList[i].Id?.toInt() == EditProfileActivity.studentProfile?.StandardId) {
                        selectedPostion = i
                        break
                    }
                }
                Log.e("Selected Position 1", "Is ${selectedPostion}")
                spClassHome.setSelection(selectedPostion)
                hideDialog()
                //  Log.e("Data is", "Here ${it.data[0].Value}")
            }
        })


        homeVM.observedStudentProfileData().observe(this, { event ->
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
                    if (dropdownList[i].Id?.toInt() == it.data?.StandardId?.toInt()) {
                        selectedPostion = i
                        break
                    }
                }
                Log.e("Selected Position", "Is ${selectedPostion}")
                spClassHome.setSelection(selectedPostion)
//                Utils.loadCircleImageUser(image, userImage.toString())
//                if (it.data?.SubscriptionExpiryDate.isNullOrBlank()) {
//                    lilExpirePeriod.visibility = View.GONE
//                } else {
//                    lilExpirePeriod.visibility = View.VISIBLE
//                }
//
//                if (it.data?.SubscriptionExpiryDate != null) {
//                    val inputFormat = SimpleDateFormat("yyyy-MM-dd'T'hh:mm:ss")
//                    val outputFormat = SimpleDateFormat("dd-MM-yyyy hh:mm a")
//                    val date: Date = inputFormat.parse(it.data?.SubscriptionExpiryDate)
//                    //  val formattedDate: String = outputFormat.format(date)
//                    var cal = Calendar.getInstance()
//                    cal.setTime(date)
//
//                    val seconds: Long =
//                        (cal.getTimeInMillis() - Calendar.getInstance().getTimeInMillis()) / 1000
//                    val hours = (seconds / 3600).toInt()
//                    Log.e("Hours ${hours / 24}", "Seconds$seconds")
//                    //   txtExpiryDate.text = "Trial is ending in ${hours / 24} days !!"
//
//                }
//

//                txtName.text = it?.data?.Name
//                txtNumber.text = it?.data?.MobileNumber
//                txtRenking.text = "${(it?.data?.MonthPoints ?: "0")}"
//                txtCourseDone.text = "${it?.data?.CourseCompleted.toString()} %"
//                txtPoints.text = "${it?.data?.Points.toString()}"
//                txtPointsDesc.text = "${it?.data?.Points.toString()} Points"
//                tvTheChamp.text = "${it?.data?.LevelTitle.toString()}"
//                txtEmail.text = "${it?.data?.Email}"
//                var dividend = it.data.Points?.toInt() ?: 0
//                val divisor = 1000
//                val quotient = dividend / divisor
//                val remainder = dividend % divisor
//                var finalMax = (quotient+1) * 1000
//                Log.e("FinalMax","Is === ${finalMax}")
//                Log.e("quotient","Is === ${quotient}")
//                Log.e("remainder","Is === ${remainder}")
//                progressView1.max = finalMax.toFloat()
//                it.data.Points?.toFloat().let {
//                    if (it != null) {
//                        progressView1.progress = it
//                    }
//                }
//
//                txtSchoolName.text = "${it?.data?.SchoolName}"
//                spClass.text = "${dropdownList[selectedPostion].Value}"
//                txtLanguage.text = "${languagesList[selectedPostionLanguage].LanguageName}"
//                txtAddress.text = "${it?.data?.Adress}"
            }
        })


        homeVM.observerLeaderBoardData().observe(this, { event ->
            event?.getContentIfNotHandled()?.let {

                var animation = AnimationUtils.loadAnimation(requireActivity(), R.anim.bounce);
                UsersList.clear()
                var count = 0
                if (!it.data.Top3Students.isNullOrEmpty()) {
                    for (topstud in it.data.Top3Students!!) {
                        count++
                        // topstud.Ranking = count
                    }
                }
                if (!it.data.OtherStudents.isNullOrEmpty()) {
                    for (bottmstud in it.data.OtherStudents!!) {
                        count++
                        //bottmstud.Ranking = count
                    }
                }

                it.data.Top3Students?.let { it1 -> UsersList.addAll(it1) }
                it.data.OtherStudents?.let { it1 -> UsersList.addAll(it1) }
            }
        })


        homeVM.observedHomeChanges().observe(this, { event ->
            event?.getContentIfNotHandled()?.let {
                subjectList.clear()


                showHideMenuIcons(it.data.MenuSetting)

                if (it.data.Freedays > 0) {
                    lilExpirePeriod.visibility = View.VISIBLE
                    txtExpiryDate.text =
                        "Your free period is ending in ${it.data.Freedays} days. !!"
                } else {
                    lilExpirePeriod.visibility = View.GONE
                }
                Constants.isAppRated = it.data.AppRated
                lilExpirePeriod.setOnClickListener {
                    startActivity(ChooseYourSubscriptionActivity::class.java)
                }
                if (it.data.SubscriptionExpiryDate != null) {
                    try {
                        val inputFormat = SimpleDateFormat("yyyy-MM-dd'T'hh:mm:ss")
                        val outputFormat = SimpleDateFormat("dd-MM-yyyy hh:mm a")
                        val date: Date = inputFormat.parse(it.data?.SubscriptionExpiryDate)
                        //  val formattedDate: String = outputFormat.format(date)
                        var cal = Calendar.getInstance()
                        cal.setTime(date)

                        val seconds: Long =
                            (cal.getTimeInMillis() - Calendar.getInstance()
                                .getTimeInMillis()) / 1000
                        val hours = (seconds / 3600).toInt()
                        Log.e("Days ${hours / 24}", "Seconds$seconds")
                        var days = hours / 24
                        if (days < 0) {
                            Constants.isLockLiveClass = true
                            Constants.isLockTestSeries = true
                            Constants.isLockExamBluePrint = true
                            Constants.isLockAskDoubts = true
                            Constants.isLockSubjects = true
                            Constants.isLockScan = true
                            Constants.isLockPractice = true
                            Constants.isLockIMPQuestions = true
                        } else {
                            starImage.visibility = View.VISIBLE
                            Constants.isLockLiveClass = false
                            Constants.isLockTestSeries = false
                            Constants.isLockExamBluePrint = false
                            Constants.isLockAskDoubts = false
                            Constants.isLockSubjects = false
                            Constants.isLockScan = false
                            Constants.isLockIMPQuestions = false
                            Constants.isLockPractice = false
                            lilExpirePeriod.visibility = View.VISIBLE
                            homeVM.isPremiumUser = true
                            txtExpiryDate.text = "You are premium user."
                            txtSubscribe.visibility = View.GONE
                            lilExpirePeriod.setOnClickListener(null)
                        }
                    } catch (e: Exception) {
                        Log.e("TAG", "observer: ${e.message}")
                    }

                } else if (it.data.Freedays > 0) {
                    Constants.isLockLiveClass = true
                    Constants.isLockScan = false
                    Constants.isLockTestSeries = false
                    Constants.isLockExamBluePrint = false
                    Constants.isLockAskDoubts = true
                    Constants.isLockSubjects = false
                    Constants.isLockIMPQuestions = false
                    Constants.isLockPractice = false
                } else {
                    Constants.isLockLiveClass = true
                    Constants.isLockTestSeries = true
                    Constants.isLockExamBluePrint = true
                    Constants.isLockAskDoubts = true
                    Constants.isLockSubjects = true
                    Constants.isLockIMPQuestions = true
                    Constants.isLockScan = true
                    Constants.isLockPractice = true
                }

//                lockLiveClass.visibility =
//                    if (Constants.isLockLiveClass) View.VISIBLE else View.GONE
//                lockWeekEndTest.visibility =
//                    if (Constants.isLockTestSeries) View.VISIBLE else View.GONE
//                lockExamBluePrint.visibility =
//                    if (Constants.isLockExamBluePrint) View.VISIBLE else View.GONE
//                lockAskDoubts.visibility =
//                    if (Constants.isLockAskDoubts) View.VISIBLE else View.GONE
//                lockImpQuestion.visibility =
//                    if (Constants.isLockIMPQuestions) View.VISIBLE else View.GONE


                if (lilExpirePeriod.visibility == View.VISIBLE) {
                    lockLiveClass.visibility = View.INVISIBLE
                    lockWeekEndTest.visibility = View.INVISIBLE
                    lockExamBluePrint.visibility = View.INVISIBLE
                    lockAskDoubts.visibility = View.INVISIBLE
                    lockImpQuestion.visibility = View.INVISIBLE
                }
                if (Constants.isLockLiveClass) lockLiveClass.setImageResource(R.drawable.ic_lock) else lockLiveClass.setImageResource(
                    R.drawable.free
                )

                if (Constants.isLockTestSeries) lockWeekEndTest.setImageResource(R.drawable.ic_lock) else lockWeekEndTest.setImageResource(
                    R.drawable.free
                )
                if (Constants.isLockExamBluePrint) lockExamBluePrint.setImageResource(R.drawable.ic_lock) else lockExamBluePrint.setImageResource(
                    R.drawable.free
                )
                if (Constants.isLockAskDoubts) lockAskDoubts.setImageResource(R.drawable.ic_lock) else lockAskDoubts.setImageResource(
                    R.drawable.free
                )
                if (Constants.isLockIMPQuestions) lockImpQuestion.setImageResource(R.drawable.ic_lock) else lockImpQuestion.setImageResource(
                    R.drawable.free
                )

                it.data.Subjects?.let { it1 -> subjectList.addAll(it1) }

                if (it.data.Alert?.ShowAlert == true) {
                    if (it.data.Alert?.AlertType == "1" && (Constants.popupCount == 0)) {
                        showReminderDialog(
                            it.data.Alert?.Title ?: "",
                            it.data.Alert?.Description ?: ""
                        )
                        Constants.popupCount++
                    } else if (it.data.Alert?.AlertType == "2") {
                        //Title Discription Update //Force update only one button // Redirect to play store
                        showUpdateDialog(
                            it.data.Alert?.Title ?: "",
                            it.data.Alert?.Description ?: ""
                        )
                        Constants.popupCount++
                    } else if (it.data.Alert?.AlertType == "3" && (Constants.popupCount == 0)) {
                        //Information // Ok button
                        showInformationDialog(
                            it.data.Alert?.Title ?: "",
                            it.data.Alert?.Description ?: ""
                        )
                        Constants.popupCount++
                    }

                }
                setHomeAdapter()

                setTodaysLiveClasses(it.data.TodayLiveClasses)
                setExamBluePrints(it.data.ExamBluePrints)
                setWeekEndTestSeries(it.data.WeekendTests)
                setImpQuestions(it.data.Subjects)
                txtAskedDoughts.text = "${it.data.AskedDoubtCount} doubts asked this month"
                phoneNumber = it.data.WhatsAppAskDoubt
                tvTheChamp.text = it?.data?.LevelTitle
                txtPoints.text = "${it?.data?.Points} points"

                var dividend = it.data.Points?.toInt() ?: 0
                val divisor = 1000
                val quotient = dividend / divisor
                val remainder = dividend % divisor
                var finalMax = (quotient + 1) * 1000
                Log.e("FinalMax", "Is === ${finalMax}")
                Log.e("quotient", "Is === ${quotient}")
                Log.e("remainder", "Is === ${remainder}")
                progressView1.max = finalMax.toFloat()

//                Glide.with(requireActivity())
//                    .load("https://img.freepik.com/premium-vector/summer-sale-horizontal-banner-template-social-media-ads-vector-summer-sale-banner-beautiful-seaside-landscape-with-palm-branches_565728-704.jpg").apply(RequestOptions().centerCrop())
//                    .error(R.drawable.user)
//                    .placeholder(R.drawable.user)
//                    .into(imageViewTopper)
//                val pulse = AnimationUtils.loadAnimation(requireContext(), R.anim.pulse)
//                joinNowButton?.startAnimation(pulse)
//                Glide.with(requireActivity())
//                    .load("https://myexam.allen.in/wp-content/uploads/2024/06/ALLEN-Dominates-JEE-Advanced-2024-4-Students-in-Top-10-Including-AIR-1-Ved-Lahoti.jpg").apply(RequestOptions().fitCenter())
//                    .error(R.drawable.user)
//                    .placeholder(R.drawable.user)
//                    .into(topperImage)

                var BannerImageUrl = Utils.getUrlFromS3Details(
                    BucketFolderPath = it?.data?.BannerBucket?.BucketFolderPath ?: "",
                    FileName = it?.data?.BannerBucket?.FileName ?: ""
                );
                var ChampionImageUrl = Utils.getUrlFromS3Details(
                    BucketFolderPath = it?.data?.ChampionBucket?.BucketFolderPath ?: "",
                    FileName = it?.data?.ChampionBucket?.FileName ?: ""
                );

                Log.e("BannerImageUrl", "Is === ${BannerImageUrl}")
                Log.e("ChampionImageUrl", "Is === ${ChampionImageUrl}")
                if (homeVM.isPremiumUser && it.data.ShowToPremiumUsers == true) {
                    imageViewTopper.visibility = View.VISIBLE
                    topperCard.visibility = View.VISIBLE
                }
                if (!homeVM.isPremiumUser && it.data.ShowToNormalUsers == true) {
                    imageViewTopper.visibility = View.VISIBLE
                    topperCard.visibility = View.VISIBLE
                }

                Glide.with(requireActivity())
                    .load(BannerImageUrl).apply(RequestOptions().centerCrop())
                    .error(R.drawable.banner_placeholder)
                    .diskCacheStrategy(DiskCacheStrategy.AUTOMATIC)
                    .dontTransform()
                    .dontAnimate()
                    .placeholder(R.drawable.banner_placeholder)
                    .into(imageViewTopper)
                val pulse = AnimationUtils.loadAnimation(requireContext(), R.anim.pulse)
                joinNowButton?.startAnimation(pulse)
                Glide.with(requireActivity())
                    .load(ChampionImageUrl).apply(RequestOptions().fitCenter())
                    .error(R.drawable.banner_placeholder)
                    .diskCacheStrategy(DiskCacheStrategy.AUTOMATIC)
                    .placeholder(R.drawable.banner_placeholder)
                    .dontTransform()
                    .dontAnimate()
                    .into(topperImage)

                imageViewTopper.setOnClickListener {
                    startActivity(ChooseYourSubscriptionActivity::class.java)
                }

                joinNowButton.setOnClickListener {
                    startActivity(ChooseYourSubscriptionActivity::class.java)
                }

                it.data.Points?.toFloat().let {
                    if (it != null) {
                        progressView1.progress = it
                    }
                }
                unreadNotiCount = it.data.UnreadNotificationCount ?: 0
                if (it.data.UnreadNotificationCount ?: 0 > 0) {
                    ivNotification.setImageResource(R.drawable.ic_notifications_unread)
                } else {
                    ivNotification.setImageResource(R.drawable.ic_notification_bell)
                }
            }
        })
        homeVM.observedChanges().observe(this, { event ->
            event?.getContentIfNotHandled()?.let {
                when (it) {
                    Constants.VISIBLE -> {
                        showDialog()
                    }

                    Constants.HIDE -> {
                        hideDialog()

                        if (Constants.count == 0) {
                            // showReminderDialog()
                        }
                        Constants.count = Constants.count + 1

                    }

                    Constants.NAVIGATE -> {

                    }

                    else -> {
                        Toast.makeText(requireActivity(), it, Toast.LENGTH_SHORT).show()
                        // showMessage(it, requireActivity(), mainFrame)
                        Utils.hideKeyboard(requireActivity())

                    }
                }
            }
        })

    }

    private fun showHideMenuIcons(menuSetting: GetHomeDataResponseModel.MenuSetting?) {
        if (menuSetting?.ShowLiveClass == true) {
            liveClasses.visibility = View.VISIBLE
        } else {
            liveClasses.visibility = View.GONE
        }

        if (menuSetting?.ShowImpQuestion == true) {
            cardIMPQuestion.visibility = View.VISIBLE
        } else {
            cardIMPQuestion.visibility = View.GONE
        }

        if (menuSetting?.ShowWeekendTest == true) {
            weekEndSeries.visibility = View.VISIBLE
        } else {
            weekEndSeries.visibility = View.GONE
        }

        if (menuSetting?.ShowExambluePrint == true) {
            examBlueprints.visibility = View.VISIBLE
        } else {
            examBlueprints.visibility = View.GONE
        }

        if (menuSetting?.ShowAskDoubt == true) {
            cardAskDoughts.visibility = View.VISIBLE
        } else {
            cardAskDoughts.visibility = View.GONE
        }
    }

    private fun setTodaysLiveClasses(todayLiveClasses: List<GetHomeDataResponseModel.TodayLiveClasse>?) {
        val chipList: MutableList<Chip> = ArrayList()
        if (todayLiveClasses != null) {
//            todayLiveClasses.sortedBy { todayLiveClasse -> todayLiveClasse.Time }
            for (liveClass in todayLiveClasses) {
                chipList.add(
                    Tag(
                        liveClass.SubjectName,
                        "- ${liveClass.Time}",
                        "by ${liveClass.TeacherName}",
                        1
                    )
                )
            }
            // Adapter
            val adapterLayout: ChipViewAdapter = MainChipViewAdapter(requireActivity())


            text_chip_attrs.setAdapter(adapterLayout);
            text_chip_attrs.chipList = chipList
            text_chip_attrs.refresh()
        }


    }

    private fun setExamBluePrints(examBluePrints: List<String>?) {
        val chipList: MutableList<Chip> = ArrayList()
        if (examBluePrints != null) {
            for (bluePrint in examBluePrints) {
                chipList.add(Tag(bluePrint, "", "", 1))
            }
            // Adapter
            val adapterLayout: ChipViewAdapter = MainChipViewAdapter(requireActivity())


            chipExamBlue.setAdapter(adapterLayout);
            chipExamBlue.chipList = chipList
        }


    }

    private fun setImpQuestions(examBluePrints: List<GetHomeDataResponseModel.Subject>?) {
        val chipList: MutableList<Chip> = ArrayList()
        if (examBluePrints != null) {
            for (bluePrint in examBluePrints) {
                chipList.add(Tag(bluePrint.Name, "", "", 1))
            }
            // Adapter
            val adapterLayout: ChipViewAdapter = MainChipViewAdapter(requireActivity())

            chipImpQuestion.setAdapter(adapterLayout);
            chipImpQuestion.chipList = chipList
        }


    }

    private fun setWeekEndTestSeries(weekendTestSeries: List<GetHomeDataResponseModel.WeekendTestSery>?) {
        val chipList: MutableList<Chip> = ArrayList()
        if (weekendTestSeries != null) {
            for (test in weekendTestSeries) {
                chipList.add(Tag(test.DayName + " - ", test.Time, "", 1))
            }
            // Adapter
            val adapterLayout: ChipViewAdapter = MainChipViewAdapter(requireActivity())


            chipWeekendTest.setAdapter(adapterLayout);
            chipWeekendTest.chipList = chipList
        }


    }

    override fun onResume() {
        super.onResume()
        // var loginUser = SharedPrefs.getLoginDetail(requireActivity())
        //  Log.e("User Image","Is ="+loginUser?.S3Bucket?.BucketFolderPath +"FileName= "+loginUser?.S3Bucket?.FileName)
        Log.e("Tokenis====", "Home Resume======" + SharedPrefs.getToken(requireActivity()))


        var studentObj = SharedPrefs.getLoginDetail(requireActivity())
        if (Constants.isApiCalling) {
            homeVM.callGetHomeData()
            //  homeVM.callGetLeaderBoard()
        }
        if (studentObj != null) {
            var url = Utils.getUrlFromS3Details(
                BucketFolderPath = studentObj?.S3Bucket?.BucketFolderPath ?: "",
                FileName = studentObj?.S3Bucket?.FileName ?: ""
            )

            if (!Utils.isNullorBlank(url.toString())) {
                Glide.with(requireActivity())
                    .load(url.toString()).apply(RequestOptions().centerCrop())
                    .error(R.drawable.user)
                    .placeholder(R.drawable.user)
                    .into(profileImage)
            }
        }
    }


    private fun setHomeAdapter() {
        rvSubjects.adapter = BindingAdapter(
            layoutId = R.layout.row_home_subjects_api,
            br = BR.model,
            list = ArrayList(subjectList),
            clickListener = { view, position ->
                when (view.id) {
                    R.id.mainLayout -> {
//                        val intent = Intent(context, SubjectActivity::class.java)
//                        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
//                        context.startActivity(intent)

                        if (subjectList[position].IsLock == false) {
                            subjectList[position]?.Id?.let {
                                startActivityWithData(
                                    SubjectActivity::class.java,
                                    it
                                )
                            }
                        } else {
                            startActivity(ChooseYourSubscriptionActivity::class.java)
                        }

                    }
                }
            })
        //adding a layoutmanager
        rvSubjects.layoutManager = GridLayoutManager(requireActivity().applicationContext, 3)

    }

    fun showReminderDialog(title: String, description: String) {
        val dialog = Dialog(requireActivity())
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.setCancelable(false)
        dialog.setContentView(R.layout.dialog_subscribe)
        dialog.getWindow()?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

        var lilRemindMeLater = dialog.findViewById<LinearLayout>(R.id.lilRemindMeLater)
        var llSubscribe = dialog.findViewById<LinearLayout>(R.id.llSubscribe)
        var txtTitle = dialog.findViewById<TextView>(R.id.txtTitle)
        var txtDescription = dialog.findViewById<TextView>(R.id.txtDescription)
        txtTitle.text = title
        txtDescription.text = description

        lilRemindMeLater.setOnClickListener {
            dialog.dismiss()
        }

        llSubscribe.setOnClickListener {
            dialog.dismiss()
            startActivity(ChooseYourSubscriptionActivity::class.java)
        }

        dialog.show()
    }

    fun showUpdateDialog(title: String, description: String) {
        val dialog = Dialog(requireActivity())
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.setCancelable(false)
        dialog.setContentView(R.layout.dialog_update_app)
        dialog.getWindow()?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        dialog.getWindow()?.setLayout(
            WindowManager.LayoutParams.MATCH_PARENT,
            WindowManager.LayoutParams.MATCH_PARENT
        )
        //  var lilRemindMeLater = dialog.findViewById<LinearLayout>(R.id.lilRemindMeLater)
        var btnUpdateApp = dialog.findViewById<Button>(R.id.btnUpdateApp)
        var txtTitle = dialog.findViewById<TextView>(R.id.txtTitle)
        var txtDescription = dialog.findViewById<TextView>(R.id.txtDescription)
        txtTitle.text = title
        txtDescription.text = description
        txtTitle.setOnClickListener {
            Log.e("Update", "Clicked")
        }
//        lilRemindMeLater.setOnClickListener {
//            dialog.dismiss()
//        }

        btnUpdateApp.setOnClickListener {
            Log.e("Update", "Clicked 1")
            dialog.dismiss()
            val uri: Uri = Uri.parse("market://details?id=${requireActivity().packageName}")
            val goToMarket = Intent(Intent.ACTION_VIEW, uri)
            // To count with Play market backstack, After pressing back button,
            // to taken back to our application, we need to add following flags to intent.
            goToMarket.addFlags(
                Intent.FLAG_ACTIVITY_NO_HISTORY or
                        Intent.FLAG_ACTIVITY_NEW_DOCUMENT or
                        Intent.FLAG_ACTIVITY_MULTIPLE_TASK
            )
            try {
                startActivity(goToMarket)
            } catch (e: ActivityNotFoundException) {
                startActivity(
                    Intent(
                        Intent.ACTION_VIEW,
                        Uri.parse("http://play.google.com/store/apps/details?id=${requireActivity().packageName}")
                    )
                )
            }
        }

        dialog.show()
    }

    fun showInformationDialog(title: String, description: String) {
        val dialog = Dialog(requireActivity())
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.setCancelable(false)
        dialog.setContentView(R.layout.dialog_information_app)
        dialog.getWindow()?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        dialog.getWindow()?.setLayout(
            WindowManager.LayoutParams.MATCH_PARENT,
            WindowManager.LayoutParams.MATCH_PARENT
        )
        //  var lilRemindMeLater = dialog.findViewById<LinearLayout>(R.id.lilRemindMeLater)
        var btnUpdateApp = dialog.findViewById<Button>(R.id.btnUpdateApp)
        var txtTitle = dialog.findViewById<TextView>(R.id.txtTitle)
        var txtDescription = dialog.findViewById<TextView>(R.id.txtDescription)
        txtTitle.text = title
        txtDescription.text = description
        txtTitle.setOnClickListener {
            Log.e("Update", "Clicked")
        }
//        lilRemindMeLater.setOnClickListener {
//            dialog.dismiss()
//        }
        btnUpdateApp.setOnClickListener {
            Log.e("Update", "Clicked 1")
            dialog.dismiss()
        }
        dialog.show()
    }

    public fun showDialog() {
        Utils.hideKeyboard(requireActivity())
        lilProgressBar.visibility = View.VISIBLE
        animationView.visibility = View.VISIBLE
        Utils.getNonWindowTouchable(requireActivity())
    }

    public fun hideDialog() {
        lilProgressBar.visibility = View.GONE
        animationView.visibility = View.GONE
        Utils.getWindowTouchable(requireActivity())
    }

    private fun init() {
        homeVM = (getViewModel() as HomeVM)
        if (Constants.registerPoints != null && (Constants.registerPoints?.toInt() ?: 0 > 0)) {
            showCongratulationsDialog()
            Constants.registerPoints = null
        }
        Log.e("Tokenis====", "Home Init======" + SharedPrefs.getToken(requireActivity()))

        var loginUser = SharedPrefs.getLoginDetail(requireActivity())
        Log.e(
            "User Image",
            "Is =" + loginUser?.S3Bucket?.BucketFolderPath + "FileName= " + loginUser?.S3Bucket?.FileName
        )

        //https://ibb.co/Z1Mt2swd
        if (Constants.isApiCalling) {
            homeVM.callGetProfileData()
            // homeVM.callGetHomeData()
            //homeVM.callGetLeaderBoard()
        } else {

            Constants.count = Constants.count + 1

            val recyclerView = view?.findViewById(R.id.rvSubjects) as RecyclerView

            //adding a layoutmanager
            recyclerView.layoutManager = GridLayoutManager(requireActivity().applicationContext, 3)


            //crating an arraylist to store users using the data class user
            val users = ArrayList<SubjectModel>()

            //adding some dummy data to the list
            users.add(SubjectModel(R.drawable.ic_maths, "Maths"))
            users.add(SubjectModel(R.drawable.ic_science, "Science"))
            users.add(SubjectModel(R.drawable.ic_social_science, "Social\nscience"))
            users.add(SubjectModel(R.drawable.ic_english, "English"))
            users.add(SubjectModel(R.drawable.ic_hindi, "Hindi"))

            //creating our adapter
            val adapter = activity?.let { SubjectAdapter(users, it) }
            //now adding the adapter to recyclerview
            recyclerView.adapter = adapter
        }
        if (Constants.isApiCalling) {
            dropdownList.clear()
            homeVM.callGetDropdownList()
        }
    }


    private fun listner() {

//        searchLayout.setOnClickListener {
//            requireActivity().sendBroadcast(Intent(Constants.GO_TO_LEADERBORARD))
//        }

        cardAskDoughts.setOnClickListener {
            if (Constants.isLockAskDoubts) {
                startActivity(ChooseYourSubscriptionActivity::class.java)
            } else {

                val packageManager = requireActivity().packageManager
                val i = Intent(Intent.ACTION_VIEW)

                try {
                    val url =
                        "https://api.whatsapp.com/send?phone=" + phoneNumber + "&text=" + URLEncoder.encode(
                            "message",
                            "UTF-8"
                        )
                    i.setPackage("com.whatsapp")
                    i.data = Uri.parse(url)
                    if (i.resolveActivity(packageManager) != null) {
                        requireActivity().startActivity(i)
                    } else {
                        Toast.makeText(requireActivity(), "Whatsapp not found", Toast.LENGTH_SHORT)
                            .show()
                    }
                } catch (e: Exception) {
                    Toast.makeText(requireActivity(), "Whatsapp not found", Toast.LENGTH_SHORT)
                        .show()
                    e.printStackTrace()
                }
                homeVM.askYourDought(phoneNumber?.replace("+", "") ?: "")
            }

        }

        profileImage.setOnClickListener {
            //customeCountDownTimer?.resume()
            startActivity(EditProfileActivity::class.java)
        }
        ivNotification.setOnClickListener {
            // customeCountDownTimer?.pause()
            startActivity(NotificationActivityNew::class.java)
        }
        weekEndSeries.setOnClickListener {
            if (Constants.isLockTestSeries) {
                startActivity(ChooseYourSubscriptionActivity::class.java)
            } else {
                startActivity(WeekEndTestSeriesActivity::class.java)
            }
        }

        cardIMPQuestion.setOnClickListener {
            if (Constants.isLockIMPQuestions) {
                startActivity(ChooseYourSubscriptionActivity::class.java)
            } else {
                startActivity(ImpSubjectListActivity::class.java)
            }
        }
        chipImpQuestion.setOnChipClickListener {
            if (Constants.isLockIMPQuestions) {
                startActivity(ChooseYourSubscriptionActivity::class.java)
            } else {
                startActivity(ImpSubjectListActivity::class.java)
            }
        }
        chipImpQuestion.setOnClickListener {
            if (Constants.isLockIMPQuestions) {
                startActivity(ChooseYourSubscriptionActivity::class.java)
            } else {
                startActivity(ImpSubjectListActivity::class.java)
            }
        }

        examBlueprints.setOnClickListener {
            if (Constants.isLockExamBluePrint) {
                startActivity(ChooseYourSubscriptionActivity::class.java)
            } else {
                startActivity(ExamBlueprintsActivity::class.java)
            }

        }
        liveClasses.setOnClickListener {
            if (Constants.isLockLiveClass) {
                startActivity(ChooseYourSubscriptionActivity::class.java)
            } else {
                startActivity(TodayLiveClassesActivity::class.java)
            }

        }

        scrollExamBluePrint.setOnClickListener {
            if (Constants.isLockExamBluePrint) {
                startActivity(ChooseYourSubscriptionActivity::class.java)
            } else {
                startActivity(ExamBlueprintsActivity::class.java)
            }
        }

        chipExamBlue.setOnChipClickListener {
            if (Constants.isLockExamBluePrint) {
                startActivity(ChooseYourSubscriptionActivity::class.java)
            } else {
                startActivity(ExamBlueprintsActivity::class.java)
            }

        }
        scrollWeekendTest.setOnClickListener {
            if (Constants.isLockTestSeries) {
                startActivity(ChooseYourSubscriptionActivity::class.java)
            } else {
                startActivity(WeekEndTestSeriesActivity::class.java)
            }
        }
        chipWeekendTest.setOnChipClickListener {
            if (Constants.isLockTestSeries) {
                startActivity(ChooseYourSubscriptionActivity::class.java)
            } else {
                startActivity(WeekEndTestSeriesActivity::class.java)
            }
        }
        text_chip_attrs.setOnClickListener {
            if (Constants.isLockLiveClass) {
                startActivity(ChooseYourSubscriptionActivity::class.java)
            } else {
                startActivity(TodayLiveClassesActivity::class.java)
            }
        }


        icCross.setOnClickListener {
            edtSearch.setText("")
            Utils.hideKeyboard(requireActivity())
            recycler_view_bottom.visibility = View.GONE
            searchValue = ""
            Constants.selectedUserId = "0"
            //setLeaderboardData()
        }

        generalTextWatcher = object : TextWatcher {
            override fun afterTextChanged(s: Editable) {
                timer = Timer()
                timer.schedule(object : TimerTask() {
                    override fun run() {
                        Log.e("Search Text", "Is == ${edtSearch?.text.toString()}")
                        if (s.isNotEmpty()) {
                            searchValue = edtSearch?.text.toString()
                            homeVM.getStudentList(searchValue ?: "")
//                            requireActivity().runOnUiThread(Runnable {
//
//                              //  setLeaderboardData(searchValue ?: "")
//                            })
                            // homeVM.getSuggestionsList(edtSearch.text.toString())
                        }
                    }
                }, DELAY)
            }

            override fun beforeTextChanged(
                s: CharSequence, start: Int,
                count: Int, after: Int
            ) {
            }

            override fun onTextChanged(
                s: CharSequence, start: Int,
                before: Int, count: Int
            ) {
                if (s.isEmpty()) {
                    requireActivity().runOnUiThread(Runnable {
                        searchValue = ""
                        Constants.selectedUserId = "0"
                        recycler_view_bottom?.visibility = View.GONE
                    })
                    //ic_cross.visibility = View.GONE
                    //SEARCH_TEXT = ""
                    // searchLayout.visibility = View.GONE
                    //  SEARCH_TRANSACTION_ID = ""
                    // SEARCH_TRANSACTION_TYPE = ""
                    // sendBroadcast(Intent(Constants.BROADCAST_CLEAR_SEARCH))
                } else {
                    //ic_cross.visibility = View.VISIBLE
                }
                timer.cancel() //Terminates this timer,discarding any currently scheduled tasks.
                timer.purge() //Removes all cancelled tasks from this timer's task queue.
            }
        }
        edtSearch.addTextChangedListener(generalTextWatcher)
    }

    private fun setLeaderboardData() {
        recycler_view_bottom.visibility = View.VISIBLE
        recycler_view_bottom.adapter = BindingAdapter(
            layoutId = R.layout.row_search_user_list_api,
            br = BR.model,
            list = ArrayList(searchUserList),
            clickListener = { view, position ->
                when (view.id) {
                    R.id.mainLayout -> {
                        edtSearch.setText("")
                        Utils.hideKeyboard(requireActivity())
                        recycler_view_bottom.visibility = View.GONE

                        Constants.selectedUserId = searchUserList[position].Id.toString()
                        startActivity(OtherStudentProfileActivity::class.java)
                        //requireActivity().sendBroadcast(Intent(Constants.GO_TO_LEADERBORARD))
                    }
                }
            })
        if (!searchValue.isNullOrBlank()) {
//            filterUsersList.clear()
//            for (weekendTestSeries in UsersList)
//            {
//                if(weekendTestSeries.Name?.contains(searchValue,true) == true || weekendTestSeries.Name?.contains(searchValue,true) == true)
//                {
//                    filterUsersList.add(weekendTestSeries)
//                }
//            }
//
//            if(filterUsersList.size > 0)
//            {
//                recycler_view_bottom.visibility = View.VISIBLE
//            }
//            else
//            {
//
//            }


        }
//        else
//        {
//            recycler_view_bottom.adapter = BindingAdapter(
//                layoutId = R.layout.row_bottom_candidates_api,
//                br = BR.model,
//                list = ArrayList(UsersList),
//                clickListener = { view, position ->
//                    when (view.id) {
//                        R.id.mainLayout -> {
//
//                        }
//                    }
//                })
//        }


    }

    fun openYoutubeLink(youtubeID: String) {
        val intentApp = Intent(Intent.ACTION_VIEW, Uri.parse("vnd.youtube:" + youtubeID))
        val intentBrowser =
            Intent(Intent.ACTION_VIEW, Uri.parse("http://www.youtube.com/watch?v=" + youtubeID))
        try {
            this.startActivity(intentApp)
        } catch (ex: ActivityNotFoundException) {
            this.startActivity(intentBrowser)
        }

    }


}