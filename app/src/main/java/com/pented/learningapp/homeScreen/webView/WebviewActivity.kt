package com.pented.learningapp.homeScreen.webView

import android.graphics.Bitmap
import android.util.Log
import android.view.View
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.lifecycle.ViewModelProvider
import com.pented.learningapp.R
import com.pented.learningapp.base.BaseActivity
import com.pented.learningapp.base.BaseViewModel
import com.pented.learningapp.databinding.ActivityWebViewBinding
import com.pented.learningapp.helper.Constants
import com.pented.learningapp.helper.JustCopyItVIewModel
import com.pented.learningapp.helper.Utils
import com.pented.learningapp.helper.model.S3Bucket
import kotlinx.android.synthetic.main.activity_exam_blueprints.ivBack
import kotlinx.android.synthetic.main.activity_web_view.*
import kotlinx.android.synthetic.main.transparent_progressbar_layout.*
import java.io.UnsupportedEncodingException
import java.net.URL
import java.net.URLEncoder


class WebviewActivity : BaseActivity<ActivityWebViewBinding>() {

    override fun layoutID() = R.layout.activity_web_view

      override fun viewModel(): BaseViewModel = ViewModelProvider(this).get(JustCopyItVIewModel::class.java)
      override fun initActivity() {
        init()
        listner()
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
    }

    private fun init() {
        val s3Bucket = intent.getSerializableExtra(Constants.EXTRA) as String?
        webView.webViewClient = WebViewClient()
        webView.settings.setSupportZoom(true)
        webView.settings.javaScriptEnabled = true
        webView.settings.domStorageEnabled = true
        webView.webViewClient = object : WebViewClient() {
            override fun onPageStarted(view: WebView, url: String, favicon: Bitmap?) {
                super.onPageStarted(view, url, favicon)
                showDialog()
            }

            override fun onPageFinished(view: WebView, url: String) {
                super.onPageFinished(view, url)
               // webView.loadUrl("javascript:(function() {document.querySelector('[class=\"ndfHFb-c4YZDc-Wrql6b\"]').remove();})()")
                hideDialog()
            }
        }
        Log.e("s3Bucket", "URL" + s3Bucket)
        webView.loadUrl(s3Bucket ?: "")
//        if(s3Bucket != null)
//        {
//            var pdf = Utils.getUrlFromS3Details(
//                BucketFolderPath = s3Bucket?.BucketFolderPath!!,
//                FileName = s3Bucket.FileName!!
//            ).toString()
//            Log.e("PDF File","IS"+pdf)
//            var url = ""
//            try {
//                url = URLEncoder.encode(pdf, "UTF-8")
//            } catch (e: UnsupportedEncodingException) {
//                e.printStackTrace()
//            }
//           // pdf = pdf.replace(" ", "%20")
//          //  val sourceUrl = URL(pdf)
//            Log.e("PDf url", "Is Here $url")
//            webView.loadUrl("https://docs.google.com/gview?embedded=true&url=$url")
//        }

    }


}