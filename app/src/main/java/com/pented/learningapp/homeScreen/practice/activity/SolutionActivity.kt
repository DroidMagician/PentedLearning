package com.pented.learningapp.homeScreen.practice.activity

import android.Manifest
import android.app.DownloadManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.net.Uri
import android.os.Build
import android.provider.Settings
import android.util.Log
import android.view.View
import androidx.lifecycle.ViewModelProvider
import com.amazonaws.services.s3.model.GeneratePresignedUrlRequest
import com.google.gson.Gson
import com.karumi.dexter.Dexter
import com.karumi.dexter.PermissionToken
import com.karumi.dexter.listener.PermissionDeniedResponse
import com.karumi.dexter.listener.PermissionGrantedResponse
import com.karumi.dexter.listener.PermissionRequest
import com.karumi.dexter.listener.single.PermissionListener
import com.pented.learningapp.BR
import com.pented.learningapp.R
import com.pented.learningapp.amazonS3.S3Util
import com.pented.learningapp.base.BaseActivity
import com.pented.learningapp.base.BaseViewModel
import com.pented.learningapp.base.BindingAdapter
import com.pented.learningapp.databinding.ActivitySolutionBinding
import com.pented.learningapp.helper.Constants
import com.pented.learningapp.helper.JustCopyItVIewModel
import com.pented.learningapp.helper.Utils
import com.pented.learningapp.homeScreen.home.impQuestions.model.GetImpQuestionResponseModel
import com.pented.learningapp.homeScreen.practice.model.GetQuestionPaperBySubjectResponseModel
import com.pented.learningapp.homeScreen.practice.model.SolutionModel
import kotlinx.android.synthetic.main.activity_solution.*
import kotlinx.android.synthetic.main.transparent_progressbar_layout.*
import java.net.URL
import java.util.*
import kotlin.collections.ArrayList

class SolutionActivity : BaseActivity<ActivitySolutionBinding>() {
    val REQUEST_PERMISSION_SETTING = 101
    override fun layoutID() = R.layout.activity_solution

    var examBlueprintList: ArrayList<SolutionModel> = ArrayList<SolutionModel>()
    var videoSolutionList: ArrayList<GetQuestionPaperBySubjectResponseModel.SolutionVideo> = ArrayList<GetQuestionPaperBySubjectResponseModel.SolutionVideo>()
    var pdfSolutiontList: ArrayList<GetQuestionPaperBySubjectResponseModel.AnswerSPDF3Bucket> = ArrayList<GetQuestionPaperBySubjectResponseModel.AnswerSPDF3Bucket>()
    override fun viewModel(): BaseViewModel = ViewModelProvider(this).get(JustCopyItVIewModel::class.java)
    override fun initActivity() {
        init()
        listner()
    }

    private fun listner() {
        ivBack.setOnClickListener {
            onBackPressed()
        }
    }
    override fun onStart() {
        super.onStart()
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            registerReceiver(
                downloadComplete,
                IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE),RECEIVER_EXPORTED
            )
//                            registerReceiver(receiver, intentfilter, RECEIVER_EXPORTED)
        } else {
            @Suppress("UnspecifiedRegisterReceiverFlag")
            registerReceiver(
                downloadComplete,
                IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE)
            )
//                            registerReceiver(receiver, intentfilter)
        }
//        registerReceiver(downloadComplete, IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE))
    }
    override fun onStop() {
        super.onStop()
        unregisterReceiver(downloadComplete)
    }
    private fun init() {
        val gson = Gson()
        val yourObject = gson.fromJson<GetQuestionPaperBySubjectResponseModel.Data>(intent.getStringExtra(
            Constants.EXTRA), GetQuestionPaperBySubjectResponseModel.Data::class.java)
        Log.e("Solution","Video is"+Gson().toJson(yourObject))
        txtHeading.text = yourObject?.Title

        yourObject.SolutionVideos?.let { videoSolutionList.addAll(it) }

        if(videoSolutionList.size > 0)
        {
            recycler_view_videos.visibility = View.VISIBLE
            setVideoSolutionAdapter()
            imgSaperator.visibility = View.VISIBLE
        }
        else
        {
            recycler_view_videos.visibility = View.GONE
            imgSaperator.visibility = View.GONE
        }

        if(yourObject.AnswerSPDF3Bucket == null || (yourObject.AnswerSPDF3Bucket?.BucketFolderPath == null) || yourObject.AnswerSPDF3Bucket?.FileName == null)
        {
            //No Pdf added
        }
        else{
            yourObject.AnswerSPDF3Bucket?.let { pdfSolutiontList.add(it) }
        }

        setPdfSolutionAdapter()
//        var examBlueprintModel = SolutionModel("Solution for question 1","Maths blueprint",true)
//        var examBlueprintModel1 = SolutionModel("Solution for question 2","Maths blueprint",false)
//        examBlueprintList.add(examBlueprintModel)
//        examBlueprintList.add(examBlueprintModel1)

    }
    public fun showDialog() {
        Utils.hideKeyboard(this@SolutionActivity)
        lilProgressBar.visibility = View.VISIBLE
        animationView.visibility = View.VISIBLE
        Utils.getNonWindowTouchable(this@SolutionActivity)
    }

    public fun hideDialog() {
        lilProgressBar.visibility = View.GONE
        animationView.visibility = View.GONE
        Utils.getWindowTouchable(this@SolutionActivity)
    }
    private fun setVideoSolutionAdapter() {
        recycler_view_videos.adapter = BindingAdapter(
                layoutId = R.layout.row_solution_video,
                br = BR.model,
                list = ArrayList(videoSolutionList),
                clickListener = { view, position ->
                    when (view.id) {
                        R.id.lilMain -> {
                            startActivityWithObjectData(SolutionVideoActivity::class.java,videoSolutionList[position])
                        }
                    }
                })
    }
    private fun setPdfSolutionAdapter() {
        recycler_view_pdfs.adapter = BindingAdapter(
            layoutId = R.layout.row_solution,
            br = BR.model,
            list = ArrayList(pdfSolutiontList),
            clickListener = { view, position ->
                when (view.id) {
                    R.id.lilMain -> {
                        DownloadPdf(pdfSolutiontList[position])
                    }
                }
            })
    }
    private fun DownloadPdf(questionPDFS3Bucket: GetQuestionPaperBySubjectResponseModel.AnswerSPDF3Bucket?) {

        var s3Client = S3Util.getS3Client()
        var cal = GregorianCalendar.getInstance();
        cal.setTime(Date());
        cal.add(Calendar.DAY_OF_YEAR, +7);
        var daysBeforeDate = cal.getTime();


        val request = GeneratePresignedUrlRequest(
            "pentedapp",
            "${questionPDFS3Bucket?.BucketFolderPath}${questionPDFS3Bucket?.FileName}"
        )
        request.expiration = daysBeforeDate
        val objectURL: URL = s3Client.generatePresignedUrl(request)
        val fixedUrl: String = objectURL?.toString()?.replace(" ", "%20") ?: ""
        Log.e("Pdf Document","URL is"+objectURL)
        if(Build.VERSION.SDK_INT <= Build.VERSION_CODES.P && Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            Dexter.withContext(this)
                .withPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE)
                .withListener(object : PermissionListener {
                    override fun onPermissionGranted(response: PermissionGrantedResponse) {
                        // permission is granted
                        Utils.getNonWindowTouchable(this@SolutionActivity)
                        showDialog()
                        Utils.downloadFiles(
                            fixedUrl, this@SolutionActivity
                        )
//                    transactionDetailsVM.receiptResponseModel.data?.get(0)?.receipt?.let { it1 ->
//
//                    }

                    }

                    override fun onPermissionDenied(response: PermissionDeniedResponse) {
                        // check for permanent denial of permission
                        if (response.isPermanentlyDenied) {
                            // navigate user to app settings

                            val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
                            val uri = Uri.fromParts("package", packageName, null)
                            intent.data = uri
                            startActivityForResult(intent, REQUEST_PERMISSION_SETTING)
                        }
                    }

                    override fun onPermissionRationaleShouldBeShown(
                        permission: PermissionRequest?,
                        token: PermissionToken
                    ) {
                        token.continuePermissionRequest()
                    }
                }).check()
        }
        else{
            Utils.getNonWindowTouchable(this@SolutionActivity)
            showDialog()
            Utils.downloadFiles(
                fixedUrl, this@SolutionActivity
            )
        }

    }

    private var downloadComplete: BroadcastReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            try {
                Utils.getWindowTouchable(this@SolutionActivity)
                hideDialog()
                intent.extras?.let {
                    val downloadField = intent.getLongExtra(
                        DownloadManager.EXTRA_DOWNLOAD_ID, 0
                    )
                    val downloadManager =
                        getSystemService(Context.DOWNLOAD_SERVICE) as DownloadManager
                    val uri: Uri = downloadManager.getUriForDownloadedFile(downloadField)
                    val browserIntent =
                        Intent(Intent.ACTION_VIEW, uri)
                    browserIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
                    browserIntent.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY)
                    startActivity(browserIntent)
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }

        }
    }
}