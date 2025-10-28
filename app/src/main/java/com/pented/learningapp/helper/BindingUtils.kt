package com.pented.learningapp.helper


import android.content.Intent
import android.media.MediaMetadataRetriever
import android.os.CountDownTimer
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.View
import android.view.animation.AnimationUtils
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.widget.AppCompatImageView
import androidx.appcompat.widget.AppCompatTextView
import androidx.core.content.ContextCompat
import androidx.databinding.BindingAdapter
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.pented.learningapp.R
import com.pented.learningapp.enum.notificationTypes
import com.pented.learningapp.helper.model.S3Bucket
import com.pented.learningapp.homeScreen.home.liveClasses.model.GetLiveClassResponseModel
import com.pented.learningapp.homeScreen.practice.model.GetQuestionPaperBySubjectResponseModel
import com.pented.learningapp.homeScreen.subscription.ChooseYourSubscriptionActivity
import com.pented.learningapp.myUtils.toTimeAgo
import com.pented.learningapp.widget.circularprogressindicator.CircularProgressIndicator
import de.hdodenhof.circleimageview.CircleImageView
import kotlinx.android.synthetic.main.activity_subject.*
import kotlinx.android.synthetic.main.fragment_home.*
import java.lang.Exception
import java.text.SimpleDateFormat
import java.util.*
import java.util.concurrent.TimeUnit
import kotlin.math.roundToInt


class BindingUtils {
    companion object {

        //        @BindingAdapter("bind:handler")
//        @JvmStatic
//        fun bindViewPagerAdapter(view: ViewPager, str: Fragment) {
//            val adapter = PagerAdapter(str.childFragmentManager)
//            view.adapter = adapter
//            adapter.addFrag(DealsInYourAreaFragment(), "Deals In Your Area")
//            adapter.addFrag(RecentSearchFragment(), "Recent Search")
//            adapter.notifyDataSetChanged()
//        }
//
//        @BindingAdapter("bind:handlerChat")
//        @JvmStatic
//        fun bindViewPagerAdapterChat(view: ViewPager, str: Fragment) {
//            val adapter = PagerAdapter(str.childFragmentManager)
//            view.adapter = adapter
//            adapter.addFrag(YourRentalsChatChildFragment(), "Renting")
//            adapter.addFrag(YourItemsChatChildFragment(), "Listed")
//            adapter.notifyDataSetChanged()
//        }
//
//        @BindingAdapter("bind:handlerYourRentals")
//        @JvmStatic
//        fun bindViewPagerAdapterYourRentals(view: ViewPager, str: Fragment) {
//            val adapter = PagerAdapter(str.childFragmentManager)
//            view.adapter = adapter
//            adapter.addFrag(YourRentalsChildFragment(), "Renting")
//            adapter.addFrag(YourItemsChildFragment(), "Listed")
//            adapter.notifyDataSetChanged()
//        }
//
//
//        @BindingAdapter("bind:pager")
//        @JvmStatic
//        fun bindViewPagerTabs(view: TabLayout, pagerView: ViewPager) {
//            view.setupWithViewPager(pagerView, true)
//        }
//
//        @BindingAdapter("setUpWithViewpager")
//        @JvmStatic
//        fun setUpWithViewpager(tabLayout: TabLayout, viewPager: ViewPager) {
//            viewPager.addOnAdapterChangeListener(ViewPager.OnAdapterChangeListener { viewPager1, oldAdapter, newAdapter ->
//                if (oldAdapter == null && newAdapter == null) {
//                    return@OnAdapterChangeListener
//                }
//                Log.i("TAG", "onAdapterChanged")
//                tabLayout.setupWithViewPager(viewPager1)
//            })
//        }
//
//        @BindingAdapter("itemImage")
//        @JvmStatic
//        fun loadImage(view: AppCompatImageView, imageUrl: String) {
//            Glide.with(view.getContext())
//                .load(imageUrl).apply(RequestOptions().centerCrop())
//                .error(com.kedarent.R.drawable.ic_item_placeholder)
//                .placeholder(com.kedarent.R.drawable.ic_item_placeholder)
//                .into(view)
//        }
//
//        @BindingAdapter("itemMyImage")
//        @JvmStatic
//        fun loadMyImage(view: MyImageView, imageUrl: String) {
//            Glide.with(view.getContext())
//                .load(imageUrl).apply(RequestOptions().centerCrop())
//                .error(com.kedarent.R.drawable.ic_item_placeholder)
//                .placeholder(com.kedarent.R.drawable.ic_item_placeholder)
//                .into(view)
//        }
//
//        @BindingAdapter("circleImage")
//        @JvmStatic
//        fun loadCircleImage(view: CircleImageView, imageUrl: String) {
//            Glide.with(view.getContext())
//                .load(imageUrl).apply(RequestOptions().centerCrop())
//                .error(com.kedarent.R.drawable.ic_item_placeholder)
//                .placeholder(com.kedarent.R.drawable.ic_item_placeholder)
//                .into(view)
//        }
//

        @BindingAdapter("itemMyImageURL")
        @JvmStatic
        fun loadMyImage(view: ImageView, imageUrl: S3Bucket) {
            var finalURl = Utils.getUrlFromS3Details(
                BucketFolderPath = imageUrl.BucketFolderPath ?: "",
                FileName = imageUrl.FileName ?: ""
            )
            Log.e("Final URL is", "UTILS Here $finalURl")
            Glide.with(view.getContext())
                .load(finalURl.toString())
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .error(R.drawable.pented_circle)
                .placeholder(R.drawable.pented_circle)
                .into(view)
        }


        @BindingAdapter("setNotificationIconBasedonType")
        @JvmStatic
        fun setNotificationIconBasedonType(view: AppCompatImageView, type: String) {
            when(type)
            {

                notificationTypes.General.notificationType -> {
                    view.setImageResource(R.drawable.notifications_bell)
                    //Listing - No action
                    //Bar - Home Page
                }
                notificationTypes.UpdateApp.notificationType -> {
                    view.setImageResource(R.drawable.update_app)
                    //Play Store
                }
                notificationTypes.VideoFinished.notificationType -> {
                    view.setImageResource(R.drawable.notifications_bell)
                    //List - No action
                    // Bar - Home Page
                }
                notificationTypes.CorrectAnswer.notificationType -> {
                    view.setImageResource(R.drawable.check)
                    //List -
                }
                notificationTypes.RateApp.notificationType -> {
                    view.setImageResource(R.drawable.rating)
                }
                notificationTypes.ApplySubscription.notificationType -> {
                    view.setImageResource(R.drawable.ic_end_trial)
                }
                notificationTypes.LiveLectureStart.notificationType -> {
                    view.setImageResource(R.drawable.live)
                    //List - Live Latcure screen
                }
                notificationTypes.LiveLectureJoined.notificationType -> {
                    view.setImageResource(R.drawable.live)
                    //List - Live Latcure screen
                }
                notificationTypes.SubscriptionWillExpire.notificationType -> {
                    view.setImageResource(R.drawable.ic_end_trial)
                }
                notificationTypes.TodayTotalPoints.notificationType -> {
                    view.setImageResource(R.drawable.notifications_bell)
                }


            }
        }

        @BindingAdapter("getTimeAgo")
        @JvmStatic
        fun getTimeAgo(view: AppCompatTextView, date: String) {
           try {
               val inputFormat = SimpleDateFormat("yyyy-MM-dd'T'hh:mm:ss")
               var d = inputFormat.parse(date)
               view.text = d.time?.toTimeAgo()
           }
           catch (e:Exception)
           {
               view.text = ""
           }
        }

        @BindingAdapter("itemMyImageURLWithoutPlaceholder")
        @JvmStatic
        fun itemMyImageURLWithoutPlaceholder(view: ImageView, imageUrl: S3Bucket) {
            var finalURl = Utils.getUrlFromS3Details(
                BucketFolderPath = imageUrl.BucketFolderPath ?: "",
                FileName = imageUrl.FileName ?: ""
            )
            Log.e("Final URL is", "UTILS Here $finalURl")
            Glide.with(view.getContext())
                .load(finalURl.toString())
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                //.error(R.drawable.pented_circle)
               // .placeholder(R.drawable.pented_circle)
                .into(view)
        }

        @BindingAdapter("setWeekendTestDate")
        @JvmStatic
        fun setWeekendTestDate(view: TextView, date: String) {
            val inputFormat = SimpleDateFormat("yyyy-MM-dd'T'hh:mm:ss")
            val outputFormat = SimpleDateFormat("MMM d, yyyy")
            val date: Date = inputFormat.parse(date)
             val formattedDate: String = outputFormat.format(date)
            view.text = formattedDate
        }

        @BindingAdapter("setPlanetImage")
        @JvmStatic
        fun setPlanetImage(view: ImageView, image: Int) {
            view.setImageResource(image)
        }

        @BindingAdapter("animateLiveIcon","modelData")
        @JvmStatic
        fun animateLiveIcon(view: ImageView?, image: String?, imageUrl: GetLiveClassResponseModel.Data?) {
            object : CountDownTimer(imageUrl?.totalMillis!!, 1000) {
                // adjust the milli seconds here
                override fun onTick(millisUntilFinished: Long) {
//                    view.setText(
//                        "" + String.format(
//                            "%d min, %d sec",
//                            TimeUnit.MILLISECONDS.toMinutes(millisUntilFinished),
//                            TimeUnit.MILLISECONDS.toSeconds(millisUntilFinished) -
//                                    TimeUnit.MINUTES.toSeconds(
//                                        TimeUnit.MILLISECONDS.toMinutes(
//                                            millisUntilFinished
//                                        )
//                                    )
//                        )
//                    )
                    var seconds = millisUntilFinished/1000
                    val hours: Long = TimeUnit.SECONDS.toHours(seconds)
                    seconds -= TimeUnit.HOURS.toSeconds(hours)
                    val minutes: Long = TimeUnit.SECONDS.toMinutes(seconds)
                    seconds -= TimeUnit.MINUTES.toSeconds(minutes)
                    var animation = AnimationUtils.loadAnimation(view?.context, R.anim.bounce);
                    view?.startAnimation(animation)
                    view?.visibility = View.VISIBLE
                    Log.e("On", "Tick")
                }

                override fun onFinish() {
                    //Refresh API of Live Lacture
//                    view.context.sendBroadcast(Intent(Constants.REFRESH_LIVE_LACTURES))
                    view?.visibility = View.GONE
                    this.cancel()

                }
            }.start()


        }

        @BindingAdapter("getMyImageURLFromVideo")
        @JvmStatic
        fun getMyImageURLFromVideo(
            view: ImageView,
            imageUrl: GetQuestionPaperBySubjectResponseModel.SolutionVideo
        ) {
            var finalURl = Utils.getUrlFromS3Details(
                BucketFolderPath = imageUrl.SolutionVideoS3Bucket?.BucketFolderPath ?: "",
                FileName = imageUrl.SolutionVideoS3Bucket?.FileName ?: ""
            )
            val fixedUrl: String = finalURl?.toString()?.replace(" ", "%20") ?: ""
            Log.e("Final URL is", "UTILS Here $fixedUrl")
            val retriever = MediaMetadataRetriever()
            retriever.setDataSource(fixedUrl, HashMap())
            val image =
                retriever.getFrameAtTime(2000000, MediaMetadataRetriever.OPTION_CLOSEST_SYNC)
            Glide.with(view.getContext())
                .load(image)
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .error(R.drawable.pented_circle)
                .placeholder(R.drawable.pented_circle)
                .into(view)
        }

        @BindingAdapter("showCountDownTimer")
        @JvmStatic
        fun showCountDownTimer(view: TextView, imageUrl: GetLiveClassResponseModel.Data) {
             object : CountDownTimer(imageUrl.totalMillis!!, 1000) {
                // adjust the milli seconds here
                override fun onTick(millisUntilFinished: Long) {
//                    view.setText(
//                        "" + String.format(
//                            "%d min, %d sec",
//                            TimeUnit.MILLISECONDS.toMinutes(millisUntilFinished),
//                            TimeUnit.MILLISECONDS.toSeconds(millisUntilFinished) -
//                                    TimeUnit.MINUTES.toSeconds(
//                                        TimeUnit.MILLISECONDS.toMinutes(
//                                            millisUntilFinished
//                                        )
//                                    )
//                        )
//                    )
                    var seconds = millisUntilFinished/1000
                    val hours: Long = TimeUnit.SECONDS.toHours(seconds)
                    seconds -= TimeUnit.HOURS.toSeconds(hours)
                    val minutes: Long = TimeUnit.SECONDS.toMinutes(seconds)
                    seconds -= TimeUnit.MINUTES.toSeconds(minutes)
                    if(seconds.toInt() == 0 && (minutes.toInt() == 0) && (hours.toInt() == 0))
                    {
                        Handler(Looper.getMainLooper()).postDelayed({
                            view.context.sendBroadcast(Intent(Constants.REFRESH_LIVE_LACTURES))
                        }, 1000)
                    }

                    if(hours > 24)
                    {
                        view.setText(
                            hoursToDays(hours.toDouble())
                        )
                    }
                    else{
                        view.setText(
                            String.format("starting in %02d:%02d:%02d h", hours, minutes, seconds)
                        )

                    }
//                    view.setText(
//                        String.format("starting in %02d:%02d:%02d h", hours, minutes, seconds)
//                    )
                    Log.e("On", "Tick")
                }

                override fun onFinish() {
                    //Refresh API of Live Lacture
//                    view.context.sendBroadcast(Intent(Constants.REFRESH_LIVE_LACTURES))
                        Log.e("On", "Finish")
                    view.setText("Lacture is Started")
                    this.cancel()

                }
            }.start()
        }
        fun hoursToDays(hours: Double): String {
            val days = (hours / 24)
            val roundedDays = days.roundToInt()
            return "starting in $roundedDays days"
        }

        @BindingAdapter("loadImage")
        @JvmStatic
        fun loadImage(view: ImageView, imageUrl: Int) {

            view.setImageResource(imageUrl)
//            Glide.with(view.getContext())
//                .load(imageUrl).apply(RequestOptions().circleCrop())
//                .error(com.kedarent.R.drawable.ic_item_placeholder)
//                .placeholder(com.kedarent.R.drawable.ic_item_placeholder)
//                .into(view)
        }

        @BindingAdapter("loadCircleImage")
        @JvmStatic
        fun loadCircleImage(view: CircleImageView, imageUrl: Int) {

            view.setImageResource(imageUrl)
        }


        @BindingAdapter("setQuestionPaperDuaration")
        @JvmStatic
        fun setQuestionPaperDuaration(view: TextView, duration: Int) {

            var minutes = Integer.toString(duration % 60)
            minutes = if (minutes.length == 1) "0$minutes" else minutes
            view.text = (duration / 60).toString() + ":" + minutes +" hours"
//             var text  = ("%d:%02d", hours, minutes)
//            view.setImageResource(imageUrl)
        }


        @BindingAdapter("setOptionBackground")
        @JvmStatic
        fun setOptionBackground(view: LinearLayout, status: String) {
            if(status.isNullOrBlank())
            {
                view.setBackgroundResource(R.drawable.option_bg)
            }
            else
            {
                when(status)
                {
                    "Selected" -> {
                        view.setBackgroundResource(R.drawable.option_blue_bg)
                    }
                    "Correct" -> {
                        view.setBackgroundResource(R.drawable.option_green_bg)
                    }
                    "Wrong" -> {
                        view.setBackgroundResource(R.drawable.option_red_bg)
                    }
                }
            }

        }

        @BindingAdapter("setOptionTextColor")
        @JvmStatic
        fun setOptionTextColor(view: TextView, status: String) {
            if(status.isNullOrBlank())
            {
                view.setTextColor(
                    ContextCompat.getColor(
                        view.context,
                        R.color.resend_otp_gray
                    )
                )
            }
            else
            {
                when(status)
                {
                    "Selected" -> {
                        view.setTextColor(ContextCompat.getColor(view.context, R.color.white))
                    }
                    "Correct" -> {
                        view.setTextColor(ContextCompat.getColor(view.context, R.color.white))
                    }
                    "Wrong" -> {
                        view.setTextColor(ContextCompat.getColor(view.context, R.color.white))
                    }
                }
            }
        }


        @BindingAdapter("loadCircleImageApi")
        @JvmStatic
        fun loadCircleImageApi(view: CircleImageView, imageUrl: S3Bucket) {
            var finalURl = Utils.getUrlFromS3Details(
                BucketFolderPath = imageUrl.BucketFolderPath ?: "",
                FileName = imageUrl.FileName ?: ""
            )

            Glide.with(view.getContext())
                .load(finalURl.toString())
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .error(R.drawable.pented_circle)
                .placeholder(R.drawable.pented_circle)
                .into(view)
            //view.setImageResource(imageUrl)
        }

        @BindingAdapter("loadCircleImageApiUser")
        @JvmStatic
        fun loadCircleImageApiUser(view: CircleImageView, imageUrl: S3Bucket) {
            var finalURl = Utils.getUrlFromS3Details(
                BucketFolderPath = imageUrl.BucketFolderPath ?: "",
                FileName = imageUrl.FileName ?: ""
            )

            Glide.with(view.getContext())
                .load(finalURl.toString())
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .error(R.drawable.user)
                .placeholder(R.drawable.user)
                .into(view)
            //view.setImageResource(imageUrl)
        }

        @BindingAdapter("setLockFreeIconForSubjects")
        @JvmStatic
        fun setLockFreeIconForSubjects(view: ImageView, isLock: Boolean) {
            if(isLock)
            {
                view.setImageResource(R.drawable.ic_lock)
            }
            else view.setImageResource(R.drawable.free)
        }

        @BindingAdapter("setCircleProgress")
        @JvmStatic
        fun setCircleProgress(view: CircularProgressIndicator, progress: Double) {
            view.setCurrentProgress(progress)
            if(progress == 0.0)
            {
                view.visibility = View.GONE
            }
            if(progress == 100.00)
            {
                view.progressColor = ContextCompat.getColor(view.context, R.color.green)
                view.dotColor = ContextCompat.getColor(view.context, R.color.green)
            }
            else if(progress < 50.00)
            {
                view.progressColor = ContextCompat.getColor(view.context, R.color.red)
                view.dotColor = ContextCompat.getColor(view.context, R.color.red)
            }
            else if(progress > 50.00)
            {
                view.progressColor = ContextCompat.getColor(view.context, R.color.orange)
                view.dotColor = ContextCompat.getColor(view.context, R.color.orange)
            }
           // view.setImageResource(imageUrl)
        }
//
//        @BindingAdapter("setRating")
//        @JvmStatic
//        fun setRating(view: ScaleRatingBar, rating: String) {
//            view.rating = rating.toFloat()
//        }
//


    }
}