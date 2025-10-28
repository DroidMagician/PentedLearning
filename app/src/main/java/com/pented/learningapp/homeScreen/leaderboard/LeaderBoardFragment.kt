package com.pented.learningapp.homeScreen.leaderboard

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Build
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.View
import android.view.ViewTreeObserver
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import androidx.appcompat.app.AppCompatActivity.RECEIVER_EXPORTED
import androidx.lifecycle.ViewModelProvider
import com.pented.learningapp.BR
import com.pented.learningapp.MainActivity
import com.pented.learningapp.R
import com.pented.learningapp.authScreens.RegisterYourselfActivity
import com.pented.learningapp.authScreens.model.GetDropdownResponseModel
import com.pented.learningapp.authScreens.model.GetSchoolNameResponseModel
import com.pented.learningapp.base.BaseFragment
import com.pented.learningapp.base.BaseViewModel
import com.pented.learningapp.base.BindingAdapter
import com.pented.learningapp.databinding.FragmentLeaderbordBinding
import com.pented.learningapp.databinding.FragmentTestBinding
import com.pented.learningapp.helper.Constants
import com.pented.learningapp.helper.JustCopyItVIewModel
import com.pented.learningapp.helper.Utils
import com.pented.learningapp.homeScreen.home.liveClasses.viewModel.LiveClassVM
import com.pented.learningapp.homeScreen.home.otherUserProfile.OtherStudentProfileActivity
import com.pented.learningapp.homeScreen.leaderboard.activity.FilterActivity
import com.pented.learningapp.homeScreen.leaderboard.model.GetLeaderboardRequestModel
import com.pented.learningapp.homeScreen.leaderboard.model.GetLeaderboardResponseModel
import com.pented.learningapp.homeScreen.leaderboard.model.LeaderBoardModel
import com.pented.learningapp.homeScreen.leaderboard.viewModel.LeaderboardVM
import com.pented.learningapp.homeScreen.practice.activity.SolutionActivity
import kotlinx.android.synthetic.main.activity_filter.*
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.fragment_leaderbord.*
import kotlinx.android.synthetic.main.fragment_leaderbord.ivBack
import kotlinx.android.synthetic.main.fragment_leaderbord.mainFrame
import kotlinx.android.synthetic.main.transparent_progressbar_layout.*


class LeaderBoardFragment: BaseFragment<FragmentLeaderbordBinding>() {

    override fun layoutID() = R.layout.fragment_leaderbord
   // lateinit var adapterRecycler: AdapterSectionRecycler
    var topUsers: ArrayList<LeaderBoardModel> = ArrayList<LeaderBoardModel>()
    var bottomUsers: ArrayList<LeaderBoardModel> = ArrayList<LeaderBoardModel>()
    var badgeImageList = ArrayList<Int>()
    var currentBadge = 0
    lateinit var receiver: MyReceiver
    var isLoading = false
    var count  = 0
    private var isLastPage_Dashboard = false
    private var TOTAL_PAGES_Dashboard: Int = 0
    private var currentPage_Dashboard = 0
    var topUsersList: ArrayList<GetLeaderboardResponseModel.Student> = ArrayList<GetLeaderboardResponseModel.Student>()
    var bottomUsersList: ArrayList<GetLeaderboardResponseModel.Student> = ArrayList<GetLeaderboardResponseModel.Student>()
    companion object{
    //    var UsersList: ArrayList<GetLeaderboardResponseModel.Student> = ArrayList<GetLeaderboardResponseModel.Student>()
        var getLeaderboardRequestModel = GetLeaderboardRequestModel()
        var getStudentList:ArrayList<GetSchoolNameResponseModel.Data> = ArrayList<GetSchoolNameResponseModel.Data>()
        var getCityList:ArrayList<GetSchoolNameResponseModel.Data> = ArrayList<GetSchoolNameResponseModel.Data>()
        var getStandardList:ArrayList<GetDropdownResponseModel.Data> = ArrayList<GetDropdownResponseModel.Data>()
        var getSchoolNameLis:ArrayList<GetSchoolNameResponseModel.Data> = ArrayList<GetSchoolNameResponseModel.Data>()
    }



    override fun viewModel(): BaseViewModel = ViewModelProvider(this).get(LeaderboardVM::class.java)
    lateinit var leaderboardVM: LeaderboardVM
    override fun initFragment() {
        init()
        observer()
        listeners()
    }

    private fun init() {
        leaderboardVM = (getViewModel() as LeaderboardVM)
        currentBadge = 0
        count  = 0
        currentPage_Dashboard = 0
        badgeImageList.add(R.drawable.badge_one)
        badgeImageList.add(R.drawable.badge_two)
        badgeImageList.add(R.drawable.badge_three)
        badgeImageList.add(R.drawable.badge_four)


        getLeaderboardRequestModel.SchoolName = null
        getLeaderboardRequestModel.Address = null
        getLeaderboardRequestModel.StudentName = null
        getLeaderboardRequestModel.StandardIds = ArrayList<Int>()
        getLeaderboardRequestModel.Month = 0
        getLeaderboardRequestModel.Year = 0


        receiver = MyReceiver(Handler(Looper.myLooper()!!))
        val intentfilter = IntentFilter()
        intentfilter.addAction(Constants.FILTERLEADERBOARD)
//        requireActivity().registerReceiver(receiver, intentfilter)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            requireActivity().registerReceiver(receiver, intentfilter, RECEIVER_EXPORTED)
        } else {
            @Suppress("UnspecifiedRegisterReceiverFlag")
            requireActivity().registerReceiver(receiver, intentfilter)
        }
        if(Constants.isApiCalling)
        {
            leaderboardVM.callGetLeaderBoard(currentPage_Dashboard)
//            leaderboardVM.getStudentList()
//            leaderboardVM.getCityList()
            leaderboardVM.getStandardList()
//            leaderboardVM.getSchoolNameList()
        }
        else
        {
            topUsers.add(LeaderBoardModel(R.drawable.dummyprofile1,"Shia kumari",R.drawable.badge8,"Next topper","All India rank : 1","Points : 8,250"))
            topUsers.add(LeaderBoardModel(R.drawable.dummyprofile2,"Abbas sadik kumar",R.drawable.badge8,"Next topper","All India rank : 2","Points : 5,285"))
            topUsers.add(LeaderBoardModel(R.drawable.dummyprofile2,"Abbas sadik kumar",R.drawable.badge7,"Next topper","All India rank : 3","Points : 4,126"))

            bottomUsers.add(LeaderBoardModel(R.drawable.dummyprofile3,"Malika arora kumari s...",R.drawable.badge4,"The champ","All India rank : 7","Points : 2,126"))
            bottomUsers.add(LeaderBoardModel(R.drawable.dummyprofile4,"Kartik Singh",R.drawable.badge4,"The champ","All India rank : 240","Points : 112"))
            bottomUsers.add(LeaderBoardModel(R.drawable.dummyprofile5,"Radhe shyam",R.drawable.badge1,"Upcoming master","All India rank : 2,240","Points : 11"))
            bottomUsers.add(LeaderBoardModel(R.drawable.dummyprofile3,"Malika arora kumari s...",R.drawable.badge1,"Upcoming master","All India rank : 2,245","Points : 10"))

        }
    }

    override fun onDestroy() {
        super.onDestroy()
        requireActivity().unregisterReceiver(receiver)
    }
    inner class MyReceiver(handler: Handler) : BroadcastReceiver() {
        var handler: Handler = handler // Handler used to execute code on the UI thread
        override fun onReceive(context: Context?, intent: Intent?) {
            Log.e("Intent", "Action ${intent?.action}")
            handler.post {
                run {
                    if (intent?.action.equals(Constants.FILTERLEADERBOARD)){
                        currentPage_Dashboard = 0
                        leaderboardVM.callGetLeaderBoard(currentPage_Dashboard)
                    }
                }
            }
        }
    }
    private fun observer() {
        leaderboardVM.observedErrorMessageChanges().observe(this, { event ->
            event?.getContentIfNotHandled()?.let {
                progressBarData.visibility = View.GONE
                showErrorMessage(it, requireActivity(), mainFrame)
            }
        })

        leaderboardVM.observerStudentListResponseData().observe(this, { event ->
            event?.getContentIfNotHandled()?.let {
                getStudentList.clear()
                getStudentList.addAll(it.data)

            }
        })

        leaderboardVM.observerCityListResponseData().observe(this, { event ->
            event?.getContentIfNotHandled()?.let {
                getCityList.clear()
                getCityList.addAll(it.data)

            }
        })

        leaderboardVM.observerStandardListResponseData().observe(this, { event ->
            event?.getContentIfNotHandled()?.let {
                getStandardList.clear()
                for (standard in it.data)
                {
                    if(standard.Id == Constants.headerstandardid)
                    {
                        standard.isSelected = true
                    }
                    getStandardList.add(standard)
                }

            }
        })

        leaderboardVM.observerSchoolNameListData().observe(this, { event ->
            event?.getContentIfNotHandled()?.let {
                getSchoolNameLis.clear()
                getSchoolNameLis.addAll(it.data)

            }
        })


        leaderboardVM.observerLeaderBoardData().observe(this, { event ->
            event?.getContentIfNotHandled()?.let {

                if(currentPage_Dashboard == 0)
                {
                    count  = 0
                    txtYourRank.text = "Your Rank : ${it.data.Rank}"
                }

                var animation = AnimationUtils.loadAnimation(requireActivity(), R.anim.bounce);
                lilRank.startAnimation(animation)
                if(currentPage_Dashboard == 0)
                {
                    topUsersList.clear()
                    bottomUsersList.clear()
                }



               // UsersList.clear()

                if(!it.data.Top3Students.isNullOrEmpty())
                {
                    for (topstud in it.data.Top3Students!!)
                    {
                        count++
                        topstud.Ranking = count
                        topstud.badgeImage = getBadgeImage()
                        if(topstud.UserId  == Constants.selectedUserId?.toInt())
                        {
                            topstud.isSelected = true
                        }

                    }
                }

                if(!it.data.OtherStudents.isNullOrEmpty())
                {
                    for (bottmstud in it.data.OtherStudents!!)
                    {
                        count++
                        bottmstud.Ranking = count
                        bottmstud.badgeImage = getBadgeImage()
                        if(bottmstud.UserId  == Constants.selectedUserId?.toInt())
                        {
                            bottmstud.isSelected = true
                        }
                    }
                }

                if(it.data.OtherStudents == null || (it.data.OtherStudents?.size ?: 0 < 25))
                {
                    isLastPage_Dashboard = true
                }
                else{
                    isLastPage_Dashboard = false
                    isLoading = false
                }

                it.data.Top3Students?.let { it1 -> topUsersList.addAll(it1) }
                it.data.OtherStudents?.let { it1 -> bottomUsersList.addAll(it1) }

                if(!topUsersList.isEmpty() && (topUsersList.size ?: 0 >= 3))
                {
                    topThreeText.visibility = View.VISIBLE
                }
                else{
                    topThreeText.visibility = View.GONE
                }

                if(!bottomUsersList.isEmpty() && (bottomUsersList.size ?: 0 > 0))
                {
                    txtOtherStudent.visibility = View.VISIBLE
                }
                else{
                    txtOtherStudent.visibility = View.GONE
                }

              //  it.data.Top3Students?.let { it1 -> UsersList.addAll(it1) }
               // it.data.OtherStudents?.let { it1 -> UsersList.addAll(it1) }
                txtOtherStudent.text = "${bottomUsersList.size} candidates in your competition for this month"
              //  TOTAL_PAGES_Dashboard = it.data.pages!!
//                if (currentPage_Dashboard < TOTAL_PAGES_Dashboard - 1) {
//                    Log.e("TAG", "pagination: Pending condition true")
//                    isLoading = false
//                    isLastPage_Dashboard = false
//                } else {
//                    isLastPage_Dashboard = true
//                    Log.e("TAG", "pagination: Pending condition false")
//                }
                setTopCandidates()
                val recyclerViewState =
                    recycler_view_bottom.layoutManager?.onSaveInstanceState()
                setBottomCandidates()
                recycler_view_bottom.layoutManager?.onRestoreInstanceState(
                    recyclerViewState
                )

            }
        })

        leaderboardVM.observedChanges().observe(this, { event ->
            event?.getContentIfNotHandled()?.let {
                when (it) {
                    Constants.VISIBLE -> {
                        showDialog()
                    }
                    Constants.HIDE -> {
                        hideDialog()
                        progressBarData.visibility = View.GONE
                    }
                    Constants.NAVIGATE -> {

                    }
                    Constants.NO_DATA -> {
                        showMessage("No Data Found", requireActivity(), mainFrame)
                        topUsersList.clear()
                        bottomUsersList.clear()
                        topThreeText.visibility = View.GONE
                        txtOtherStudent.visibility = View.GONE
                        setTopCandidates()
                        setBottomCandidates()
                    }
                    else -> {
                        progressBarData.visibility = View.GONE
                        showMessage(it, requireActivity(), mainFrame)
                    }
                }
            }
        })
    }



    private fun setLeaderboardAdapter() {

    }
    private fun getBadgeImage(): Int {
        if(currentBadge >= badgeImageList.size)
        {
            currentBadge = 0
        }
        Log.e("currentBadge","= $currentBadge")
        Log.e("badgeImageList", "= ${badgeImageList.size}")
        var badgeImage = badgeImageList.get(currentBadge)
        currentBadge++
        return badgeImage
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
    public fun setTopCandidates()
    {
        recycler_view_top.adapter = BindingAdapter(
            layoutId = R.layout.row_top_candidates_api,
            br = BR.model,
            list = ArrayList(topUsersList),
            clickListener = { view, position ->
                when (view.id) {
                    R.id.mainLayout -> {
                      topUsersList[position].UserId?.toString().let {
                          if (it != null) {
                              Constants.selectedUserId  = it
                          }
                        }
                        startActivity(OtherStudentProfileActivity::class.java)
                    }
                }
            })
    }
    public fun setBottomCandidates()
    {
        recycler_view_bottom.adapter = BindingAdapter(
            layoutId = R.layout.row_bottom_candidates_api,
            br = BR.model,
            list = ArrayList(bottomUsersList),
            clickListener = { view, position ->
                when (view.id) {
                    R.id.mainLayout -> {
                        bottomUsersList[position].UserId?.toString().let {
                            if (it != null) {
                                Constants.selectedUserId  = it
                            }
                        }
                        startActivity(OtherStudentProfileActivity::class.java)
                    }
                }
            })
    }
    public fun setAdapter()
    {
        recycler_view_top.adapter = BindingAdapter(
            layoutId = R.layout.row_top_candidates,
            br = BR.model,
            list = ArrayList(topUsers),
            clickListener = { view, position ->
                when (view.id) {
//                    R.id.solution -> {
//                        startActivity(SolutionActivity::class.java)
//                    }
                }
            })
        recycler_view_bottom.adapter = BindingAdapter(
            layoutId = R.layout.row_bottom_candidates,
            br = BR.model,
            list = ArrayList(bottomUsers),
            clickListener = { view, position ->
                when (view.id) {
//                    R.id.solution -> {
//                        startActivity(SolutionActivity::class.java)
//                    }
                }
            })
    }
    private fun listeners() {
        ivBack.setOnClickListener {
            requireActivity().sendBroadcast(Intent(Constants.BACKPRESSED))
        }

        ivSearch.setOnClickListener {
            startActivity(FilterActivity::class.java)
        }


        scrollLeaderboard?.viewTreeObserver?.addOnScrollChangedListener(ViewTreeObserver.OnScrollChangedListener {
            val view = scrollLeaderboard?.childCount?.minus(1)?.let {
                scrollLeaderboard.getChildAt(
                    it
                )
            } as View?
            if (view != null) {
                val diff: Int =
                    view?.bottom?.minus(
                        (scrollLeaderboard.getHeight() + scrollLeaderboard
                            .getScrollY())
                    ) ?:0

                if (diff == 0) {

                    if (!isLoading && !isLastPage_Dashboard) {
                        Log.e("currentPage_Pending", "Is before" + currentPage_Dashboard)
                        //Load next page here
                        progressBarData.visibility = View.VISIBLE
                        currentPage_Dashboard += 1
                        leaderboardVM.callGetLeaderBoard(currentPage_Dashboard,false)
//                        Log.e("currentPage_Pending", "Is after" + currentPage_Dashboard)
//                        dashboardVM.dashboardListRequestModel.page = currentPage_Dashboard
//
//                        dashboardVM.dashboardListRequestModel.startDate = finalStartDate
//                        dashboardVM.dashboardListRequestModel.endDate = finalEndDate
//
//                        dashboardVM.getDashboardList(
//                            isProgressVisible = false
//                        )
                        isLoading = true
                        Log.d("TAG", "pagination:  currentPage " + currentPage_Dashboard)
                        Log.d("TAG", "pagination:  scroll condition true")
                    } else {
                    //    progressBarData.visibility = View.GONE
                    }


                    Log.e("At End", "Detected")

                } else {
                  //  progressBarData.visibility = View.GONE
                }
            }

        })

    }
}