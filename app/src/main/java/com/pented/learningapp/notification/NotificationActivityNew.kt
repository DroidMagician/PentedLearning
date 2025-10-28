package com.pented.learningapp.notification

import android.content.ActivityNotFoundException
import android.content.Intent
import android.net.Uri
import android.util.Log
import android.view.View
import androidx.lifecycle.ViewModelProvider
import com.pented.learningapp.BR
import com.pented.learningapp.R
import com.pented.learningapp.base.BaseActivity
import com.pented.learningapp.base.BaseViewModel
import com.pented.learningapp.base.BindingAdapter
import com.pented.learningapp.databinding.ActivityNotificationNewBinding
import com.pented.learningapp.enum.notificationTypes
import com.pented.learningapp.helper.Constants
import com.pented.learningapp.helper.Utils
import com.pented.learningapp.homeScreen.home.HomeFragment
import com.pented.learningapp.homeScreen.home.liveClasses.TodayLiveClassesActivity
import com.pented.learningapp.homeScreen.subscription.ChooseYourSubscriptionActivity
import com.pented.learningapp.notification.model.GetNotificationListResponseModel
import com.pented.learningapp.notification.viewModel.NotificationVM
import kotlinx.android.synthetic.main.activity_notification.ivBack
import kotlinx.android.synthetic.main.activity_notification_new.*
import kotlinx.android.synthetic.main.transparent_progressbar_layout.*

class NotificationActivityNew : BaseActivity<ActivityNotificationNewBinding>() {
    override fun layoutID() = R.layout.activity_notification_new
    val notificationList = ArrayList<GetNotificationListResponseModel.Data>()
    lateinit var notificationVM: NotificationVM
    override fun viewModel(): BaseViewModel = ViewModelProvider(this).get(NotificationVM::class.java)
    var notiType:String = ""
    override fun initActivity() {
        init()
        listner()
        observer()
    }

    private fun observer() {

        notificationVM.observedNotificationListData().observe(this, { event ->
            event?.getContentIfNotHandled()?.let {
                notificationList.clear()
                notificationList.addAll(it.data)
                Log.e("notificationList","== ${notificationList.size}")
                setNotificationList()
                var unreadCount = it.data.filter { it.IsRead == false }.count()
                if(unreadCount > 0)
                {
                    txtUnreadNotiCount.visibility = View.VISIBLE
                }
                else{
                    txtUnreadNotiCount.visibility = View.GONE
                }
                txtUnreadNotiCount.text = "${unreadCount} Pending"
            }
        })

        notificationVM.observedChanges().observe(this, { event ->
            event?.getContentIfNotHandled()?.let {
                when (it) {
                    Constants.VISIBLE -> {
                        showDialog()
                    }
                    Constants.HIDE -> {
                        hideDialog()
                    }
                    Constants.NAVIGATE -> {
                        notificationVM.callGetNotificationListData()

                    }
                    else -> {
                        showMessage(it, this, notiFrame)
                    }
                }
            }
        })
    }

    private fun setNotificationList() {
        recyclerNotification.adapter = BindingAdapter(
            layoutId = R.layout.row_notification_api,
            br = BR.model,
            list = ArrayList(notificationList),
            clickListener = { view, position ->
                when (view.id) {
                    R.id.mainLayout -> {
                        notificationVM.readNotification(notificationList[position].Id?.toInt() ?: 0 )
                        notiType = notificationList[position].Type.toString()
                        when(notiType)
                        {
                            notificationTypes.General.notificationType -> {
                                // No Action
                                //Listing - No action
                                //Bar - Home Page
                            }
                            notificationTypes.UpdateApp.notificationType -> {
                                openPlayStore()
                                //Play Store
                            }
                            notificationTypes.VideoFinished.notificationType -> {
                                // No Action
                                //List - No action
                                // Bar - Home Page
                            }
                            notificationTypes.CorrectAnswer.notificationType -> {
                                // No Action
                                //List -
                            }
                            notificationTypes.RateApp.notificationType -> {
                                openPlayStore()
                            }
                            notificationTypes.ApplySubscription.notificationType -> {
                                startActivity(ChooseYourSubscriptionActivity::class.java)
                            }
                            notificationTypes.LiveLectureStart.notificationType -> {
                                if(Constants.isLockLiveClass)
                                {
                                    startActivity(ChooseYourSubscriptionActivity::class.java)
                                }
                                else
                                {
                                    startActivity(TodayLiveClassesActivity::class.java)
                                }
                                //List - Live Latcure screen
                            }
                            notificationTypes.LiveLectureJoined.notificationType -> {
                                if(Constants.isLockLiveClass)
                                {
                                    startActivity(ChooseYourSubscriptionActivity::class.java)
                                }
                                else
                                {
                                    startActivity(TodayLiveClassesActivity::class.java)
                                }
                                //List - Live Latcure screen
                            }
                            notificationTypes.SubscriptionWillExpire.notificationType -> {
                                startActivity(ChooseYourSubscriptionActivity::class.java)
                            }
                            notificationTypes.TodayTotalPoints.notificationType -> {
                                finish()
                                sendBroadcast(Intent(Constants.GO_TO_LEADERBORARD))
                            }
                        }
                    }
                }
            })
    }

    private fun openPlayStore() {
        val uri: Uri = Uri.parse("market://details?id=$packageName")
        val goToMarket = Intent(Intent.ACTION_VIEW, uri)
        // To count with Play market backstack, After pressing back button,
        // to taken back to our application, we need to add following flags to intent.
        goToMarket.addFlags(
            Intent.FLAG_ACTIVITY_NO_HISTORY or
                Intent.FLAG_ACTIVITY_NEW_DOCUMENT or
                Intent.FLAG_ACTIVITY_MULTIPLE_TASK)
        try {
            startActivity(goToMarket)
        } catch (e: ActivityNotFoundException) {
            startActivity(
                Intent(
                    Intent.ACTION_VIEW,
                Uri.parse("http://play.google.com/store/apps/details?id=$packageName"))
            )
        }
    }

    public fun showDialog() {
        Utils.hideKeyboard(this)
        lilProgressBar.visibility = View.VISIBLE
        //animationView.visibility = View.VISIBLE
        Utils.getNonWindowTouchable(this)
    }

    override fun onResume() {
        super.onResume()
        if(::notificationVM.isInitialized)
        {
            notificationVM.callGetNotificationListData()
        }
    }

    public fun hideDialog() {
        lilProgressBar.visibility = View.GONE
        // animationView.visibility = View.GONE
        Utils.getWindowTouchable(this)
    }
    private fun listner() {
        ivBack.setOnClickListener {
            finish()
        }
    }

    private fun init() {
        notificationVM = (getViewModel() as NotificationVM)

    }

}