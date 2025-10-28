package com.pented.learningapp.homeScreen.subscription

import android.app.Activity
import android.app.Dialog
import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.net.Uri
import android.util.Log
import android.view.View
import android.view.Window
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModelProvider
import com.pented.learningapp.BR
import com.pented.learningapp.R
import com.pented.learningapp.base.BaseActivity
import com.pented.learningapp.base.BaseViewModel
import com.pented.learningapp.base.BindingAdapter
import com.pented.learningapp.databinding.ActivitySubscriptionBinding
import com.pented.learningapp.helper.Constants
import com.pented.learningapp.helper.SharedPrefs
import com.pented.learningapp.helper.Utils
import com.pented.learningapp.homeScreen.home.model.GetSubscriptionDataResponseModel
import com.pented.learningapp.homeScreen.subscription.model.ApplyCouponResponseModel
import com.pented.learningapp.homeScreen.subscription.model.SelectSubjectModel
import com.pented.learningapp.homeScreen.subscription.viewModel.SubscriptionVM
import com.razorpay.Checkout
import com.razorpay.PaymentResultListener
import kotlinx.android.synthetic.main.activity_subscription.*
import kotlinx.android.synthetic.main.transparent_progressbar_layout.*
import org.json.JSONObject
import java.text.DecimalFormat


class ChooseYourSubscriptionActivity : BaseActivity<ActivitySubscriptionBinding>(),
    PaymentResultListener {

    override fun layoutID() = R.layout.activity_subscription
    var applyCouponReponseModel = ApplyCouponResponseModel()
    var seelctSubjectList = ArrayList<SelectSubjectModel>()
    var subscriptionIds = ArrayList<Int>()
    var orderId:String ? = null
    var seelctSubjectListAPI = ArrayList<GetSubscriptionDataResponseModel.Subjects>()
    var totalAmount:Int ? = 0
    var finalAmount:Int ? = 0
    var contactNumber:String ? = ""

    override fun viewModel(): BaseViewModel = ViewModelProvider(this).get(SubscriptionVM::class.java)
    lateinit var subscriptionVM: SubscriptionVM
    override fun initActivity() {
        init()
        observer()
        listner()
    }

    private fun observer() {
        subscriptionVM.observedErrorMessageChanges().observe(this, { event ->
            event?.getContentIfNotHandled()?.let {
                showErrorMessage(it, this, mainFrame)
            }
        })


        subscriptionVM.observedHomeChanges().observe(this, { event ->
            event?.getContentIfNotHandled()?.let {
                Log.e("SubscriptionResponse", "Is here ${it.data}")
                contactNumber = it.data.ContactNumber
                seelctSubjectListAPI.clear()
                it.data?.Subjects.let { it1 ->
                    if (it1 != null) {
                        seelctSubjectListAPI.addAll(it1)
                    }
                }
                for (subject in seelctSubjectListAPI) {
                    if (subject.IsSubscribed) {
                        subject.isSelected = true
                    }
                }
                setSubjectListAdapter()
            }
        })

         subscriptionVM.observedinitPaymentChanges().observe(this, { event ->
             event?.getContentIfNotHandled()?.let {
                 orderId = it?.data?.OrderId
                 startPayment()
             }
         })

         subscriptionVM.observedcapturePaymentChanges().observe(this, { event ->
             event?.getContentIfNotHandled()?.let {
                 Log.e("CapturePayment", "Is == ${it?.data?.Amount}")
                 Log.e("CapturePayment", "Is == ${it?.data?.Status}")
                 showCongratulationsDialog()

             }
         })

        subscriptionVM.observedApplyCouponCodeChanges().observe(this, { event ->
            event?.getContentIfNotHandled()?.let {
                Log.e("ApplyCouponCode", "Is here ${it.data}")
                applyCouponReponseModel = it
                enterCodeLayout.visibility = View.GONE
                greenDot.visibility = View.VISIBLE
                var discountAmount = totalAmount!! * it.data.OfferValue?.toInt()!! / 100
                var finalDiscountAmount = 0
                if(it?.data?.MaxDiscount == null || (it?.data?.MaxDiscount == 0))
                {
                    finalDiscountAmount = discountAmount
                }
                else{
                    if (discountAmount > it?.data?.MaxDiscount!!) {
                        finalDiscountAmount = it?.data?.MaxDiscount!!
                    } else {
                        finalDiscountAmount = discountAmount
                    }
                }

                finalAmount = totalAmount?.minus(finalDiscountAmount)
                // txtPrice.setText("₹ ${totalAmount?.minus(finalDiscountAmount)} / Month")
                txtPrice.setText("₹ ${getFormatedAmount(totalAmount?.minus(finalDiscountAmount ?: 0) ?: 0)}")
                txtApplyCouponCode.setText("${it.data.OfferValue} % Discount applied")
                txtApplyCouponCode.setTextColor(
                    ContextCompat.getColor(
                        this@ChooseYourSubscriptionActivity,
                        R.color.green
                    )
                )

            }
        })

        subscriptionVM.observedChanges().observe(this, { event ->
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
    }
    fun showCongratulationsDialog() {
        val dialog = Dialog(this@ChooseYourSubscriptionActivity)
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.setCancelable(false)
        dialog.setContentView(R.layout.dialog_success_payment)
        dialog.getWindow()?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

        var btnThanks = dialog.findViewById<Button>(R.id.btnThanks)
        var txtPoints = dialog.findViewById<TextView>(R.id.txtPoints)
       // txtPoints.text = "$quotient Points"
        btnThanks.setOnClickListener {
            dialog.dismiss()
            onBackPressed()
        }
        dialog.show()
    }
    private fun setSubjectListAdapter() {
        rvSubjects.adapter = BindingAdapter(
            layoutId = R.layout.row_select_subjects_new,
            br = BR.model,
            list = ArrayList(seelctSubjectListAPI),
            clickListener = { view, position ->
                when (view.id) {
                    R.id.rlMainLayout -> {
                        if (!seelctSubjectListAPI[position].IsSubscribed) {
                            seelctSubjectListAPI[position].isSelected =
                                !seelctSubjectListAPI[position].isSelected
                            rvSubjects.adapter?.notifyItemChanged(position)

                            if (seelctSubjectListAPI.any { it.isSelected && (!it.IsSubscribed) }) {
                                below_layout.visibility = View.VISIBLE
                            } else {
                                below_layout.visibility = View.GONE
                            }

                            totalAmount = 0
                            subscriptionIds.clear()
                            for (selectedSubject in seelctSubjectListAPI) {
                                if (selectedSubject.isSelected && (!selectedSubject.IsSubscribed)) {
                                    totalAmount = selectedSubject.SubscriptionFee?.let {
                                        totalAmount?.plus(
                                            it
                                        )
                                    }
                                    selectedSubject.SubjectId?.let { subscriptionIds.add(it) }
                                }
                            }
                            // txtPrice.text = "$totalAmount / Month"
                            txtPrice.text = "₹${getFormatedAmount(totalAmount ?: 0)}"
                        }
                    }
                    R.id.ivChecked -> {
                        if (!seelctSubjectListAPI[position].IsSubscribed) {
                            seelctSubjectListAPI[position].isSelected =
                                !seelctSubjectListAPI[position].isSelected
                            rvSubjects.adapter?.notifyItemChanged(position)

                            if (seelctSubjectListAPI.any { it.isSelected && (!it.IsSubscribed) }) {
                                below_layout.visibility = View.VISIBLE
                            } else {
                                below_layout.visibility = View.GONE
                            }

                            totalAmount = 0
                            subscriptionIds.clear()
                            for (selectedSubject in seelctSubjectListAPI) {
                                if (selectedSubject.isSelected && (!selectedSubject.IsSubscribed)) {
                                    totalAmount = selectedSubject.SubscriptionFee?.let {
                                        totalAmount?.plus(
                                            it
                                        )
                                    }
                                    selectedSubject.SubjectId?.let { subscriptionIds.add(it) }
                                }
                            }
                            // txtPrice.text = "$totalAmount / Month"
                            txtPrice.text = "₹${getFormatedAmount(totalAmount ?: 0)}"
                        }
                    }
                }
            })
    }

    fun getFormatedAmount(totalAmount: Int): String {
        val decimalFormat = DecimalFormat("#.##")
        val twoDigitsF: Float = java.lang.Float.valueOf(decimalFormat.format(totalAmount))
        return twoDigitsF.toString()
    }
    fun copyToClipboard(copyText: String?) {
        val clipboard: ClipboardManager =
            getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
        val clip = ClipData.newPlainText("url", copyText)
        clipboard.setPrimaryClip(clip)
        val toast = Toast.makeText(
            this@ChooseYourSubscriptionActivity,
            "Link is copied",
            Toast.LENGTH_SHORT
        )
        toast.show()
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
    private fun listner() {
        ivBack.setOnClickListener {
            onBackPressed()
        }


        txtCopyLink.setOnClickListener {
            copyToClipboard(txtLinkURL.text.toString())
        }

        lilContactNumber.setOnClickListener {
            val callIntent = Intent(Intent.ACTION_DIAL, Uri.parse("tel:" + contactNumber))
            startActivity(callIntent)
        }
        btnGetCall.setOnClickListener {
            val callIntent = Intent(Intent.ACTION_DIAL, Uri.parse("tel:" + contactNumber))
            startActivity(callIntent)
        }

        btnPurchaseNow.setOnClickListener {
          //  subscriptionVM.initPaymentResponseModel.CouponId = applyCouponReponseModel.data?.Id?.toString()
//            subscriptionVM.initPaymentResponseModel.DiscountAmount = applyCouponReponseModel.data?.MaxDiscount?.toString()
            Log.e("Total Amount is", "Here $totalAmount")
           if(finalAmount == null || finalAmount == 0)
           {
               finalAmount = totalAmount
           }
            subscriptionVM.initPaymentResponseModel.DiscountAmount = "0"
            subscriptionVM.initPaymentResponseModel.PayableAmount = finalAmount?.toString()
            subscriptionVM.initPaymentResponseModel.SubjectIds = subscriptionIds
            subscriptionVM.initPayment()
         //   startPayment()
        }
        txtSubmit.setOnClickListener {
            if(edtCouponCode.text.toString().isNullOrBlank())
            {
                Toast.makeText(this, "Please enter coupon code", Toast.LENGTH_SHORT).show()
            }
            else
            {
                subscriptionVM.applyCouponCode(edtCouponCode.text.toString(), subscriptionIds)
            }

        }

    }

    private fun startPayment() {

        var loginUser = SharedPrefs.getLoginDetail(this@ChooseYourSubscriptionActivity)
        /*
      *  You need to pass current activity in order to let Razorpay create CheckoutActivity
      * */
        val activity: Activity = this
        val co = Checkout()
        co.setKeyID("rzp_live_fP057IKvvMGFW3")
        try {
            val options = JSONObject()
            options.put("name", "Razorpay Corp")
            options.put("description", "Demoing Charges")
            //You can omit the image option to fetch the image from dashboard
           // options.put("image","https://s3.amazonaws.com/rzp-mobile/images/rzp.png")
            options.put("currency", "INR")
            options.put("order_id", orderId);
            options.put("amount", "100")
            options.put("send_sms_hash", true);

            val prefill = JSONObject()
            prefill.put("email", loginUser?.Email)
            prefill.put("contact", loginUser?.MobileNumber)

            options.put("prefill", prefill)
            co.open(activity, options)
        }catch (e: Exception){
            Toast.makeText(activity, "Error in payment: " + e.message, Toast.LENGTH_LONG).show()
            e.printStackTrace()
        }
    }
    override fun onPaymentError(errorCode: Int, response: String?) {
        try{
            Toast.makeText(this, "Payment failed $errorCode \n $response", Toast.LENGTH_LONG).show()
        }catch (e: Exception){
            Log.e("TAG", "Exception in onPaymentSuccess", e)
        }
    }

    override fun onPaymentSuccess(razorpayPaymentId: String?) {
        try{
            razorpayPaymentId?.let { subscriptionVM.capturePayment(it) }

            Toast.makeText(this, "Payment Successful $razorpayPaymentId", Toast.LENGTH_LONG).show()
        }catch (e: Exception){
            Log.e("TAG", "Exception in onPaymentSuccess", e)
        }
    }

    private fun init() {
        subscriptionVM = (getViewModel() as SubscriptionVM)
        subscriptionVM.callGetSubscriptionData()
        Checkout.preload(applicationContext)

        txtApplyCouponCode.setOnClickListener {
            enterCodeLayout.visibility = View.VISIBLE
        }


//        rvSubjects.layoutManager = GridLayoutManager(
//            this@ChooseYourSubscriptionActivity.applicationContext,
//            3
//        )

//
//        //crating an arraylist to store users using the data class user
//        val users = ArrayList<SubjectModel>()
//
//        //adding some dummy data to the list
//        users.add(SubjectModel(R.drawable.ic_maths, "Maths"))
//        users.add(SubjectModel(R.drawable.ic_science, "Science"))
//        users.add(SubjectModel(R.drawable.ic_social_science, "Social\nscience"))
//        users.add(SubjectModel(R.drawable.ic_english, "English"))
//        users.add(SubjectModel(R.drawable.ic_hindi, "Hindi"))
//
//        //creating our adapter
//        val adapter = let { SubjectAdapter(users, it) }
//
//        //now adding the adapter to recyclerview
//        rvSubjects.adapter = adapter
    }
}