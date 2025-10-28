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
import android.widget.Toast
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
import com.pented.learningapp.databinding.ActivityPaperListingBinding
import com.pented.learningapp.helper.Constants
import com.pented.learningapp.helper.Utils
import com.pented.learningapp.homeScreen.practice.model.GetQuestionPaperBySubjectResponseModel
import com.pented.learningapp.homeScreen.practice.model.QuestionPaperModel
import com.pented.learningapp.homeScreen.practice.viewModel.QuestionPaperListVM
import com.pented.learningapp.widget.pdfviewer.PdfViewerActivity
import kotlinx.android.synthetic.main.activity_paper_listing.*
import kotlinx.android.synthetic.main.fragment_test.*
import kotlinx.android.synthetic.main.fragment_test.ivBack
import kotlinx.android.synthetic.main.fragment_test.mainFrame
import kotlinx.android.synthetic.main.fragment_test.recycler_view
import kotlinx.android.synthetic.main.transparent_progressbar_layout.*
import java.io.UnsupportedEncodingException
import java.net.URL
import java.net.URLEncoder
import java.util.*
import kotlin.collections.ArrayList


class QuestionPaperActivity : BaseActivity<ActivityPaperListingBinding>() {

    override fun layoutID() = R.layout.activity_paper_listing
    val REQUEST_PERMISSION_SETTING = 101
    var examBlueprintList: ArrayList<QuestionPaperModel> = ArrayList<QuestionPaperModel>()
    override fun viewModel(): BaseViewModel = ViewModelProvider(this).get(QuestionPaperListVM::class.java)
    lateinit var questionPaperListVM: QuestionPaperListVM

    var questionListApi: ArrayList<GetQuestionPaperBySubjectResponseModel.Data> = ArrayList<GetQuestionPaperBySubjectResponseModel.Data>()
    // val subjectListApiBackup: ArrayList<SubjectListResponseModel.Subject> = ArrayList<SubjectListResponseModel.Subject>()
    var questionListApiFilter: ArrayList<GetQuestionPaperBySubjectResponseModel.Data> = ArrayList<GetQuestionPaperBySubjectResponseModel.Data>()


    override fun initActivity() {
        init()
        listner()
        observer()
    }

    private fun listner() {
        ivBack.setOnClickListener {
            onBackPressed()
        }
    }
    private fun observer() {
        questionPaperListVM.observedErrorMessageChanges().observe(this, { event ->
            event?.getContentIfNotHandled()?.let {
                showErrorMessage(it, this@QuestionPaperActivity, mainFrame)
            }
        })


        questionPaperListVM.observedQuestionPaperListData().observe(this, { event ->
            event?.getContentIfNotHandled()?.let {
                Log.e("Home Response Model", "Is here ${it.data}")
                questionListApi.clear()
                questionListApiFilter.clear()

                it.data?.let { it1 -> questionListApi.addAll(it1) }
                //Create a List of SectionHeader DataModel implements SectionHeader

                setQuestionPaperAPI()
            }
        })

        questionPaperListVM.observedChanges().observe(this, { event ->
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
                        showMessage(it, this@QuestionPaperActivity, mainFrame)
                    }
                }
            }
        })
    }
    public fun showDialog() {
        Utils.hideKeyboard(this@QuestionPaperActivity)
        lilProgressBar.visibility = View.VISIBLE
        animationView.visibility = View.VISIBLE
        Utils.getNonWindowTouchable(this@QuestionPaperActivity)
    }

    public fun hideDialog() {
        lilProgressBar.visibility = View.GONE
        animationView.visibility = View.GONE
        Utils.getWindowTouchable(this@QuestionPaperActivity)
    }
    private fun init() {
        questionPaperListVM = (getViewModel() as QuestionPaperListVM)
        val intent = intent
        val extras = intent.extras
        val subjectID = extras?.getString("subjectID")
        val subjectName = extras?.getString("subjectName")
        Log.e("Subject Id", "Is here ${subjectID}")
        Constants.subjectIdFromQuestion = subjectID
        subjectID?.toInt()?.let { questionPaperListVM.callGetQuestionPaperList(it) }
        txtTitle.text= "$subjectName"
//        var examBlueprintModel = QuestionPaperModel("Question Paper - 1","Maths blueprint","3 parts")
//        var examBlueprintModel1 = QuestionPaperModel("Question Paper - 2","Maths blueprint","2 parts")
//        examBlueprintList.add(examBlueprintModel)
//        examBlueprintList.add(examBlueprintModel1)
//        setExamBluePrintAdapter()
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
    private fun setQuestionPaperAPI() {
        recycler_view.adapter = BindingAdapter(
            layoutId = R.layout.row_question_paper,
            br = BR.model,
            list = ArrayList(questionListApi),
            clickListener = { view, position ->
                when (view.id) {
                    R.id.btnGoToSolution -> {
                        var dataString: String = Gson().toJson(questionListApi[position])
                        if((questionListApi[position].SolutionVideos?.isEmpty() == true || (questionListApi[position].SolutionVideos?.get(0)?.SolutionVideoS3Bucket == null ||  (questionListApi[position].SolutionVideos?.get(0)?.SolutionVideoS3Bucket?.FileName == null)) && (questionListApi[position].SolutionVideos?.get(0)?.Youtubelink == null))
                            && (questionListApi[position].AnswerSPDF3Bucket == null || (questionListApi[position].AnswerSPDF3Bucket?.BucketFolderPath == null) || (questionListApi[position].AnswerSPDF3Bucket?.FileName == null)))
                        {
                            Toast.makeText(this@QuestionPaperActivity,"No solution found",Toast.LENGTH_SHORT).show()
                            return@BindingAdapter
                        }
                        else{
                            Constants.questionPaperId = questionListApi[position].QuestionPaperId.toString()
                            Constants.QuestionPaperVideoPoints = questionListApi[position].Points.toString()
                            startActivityWithObjectData(SolutionActivity::class.java,questionListApi[position])
                        }
                    }
                    R.id.btnDownloadPdf -> {
                        if(questionListApi[position].QuestionPDFS3Bucket == null || (questionListApi[position].QuestionPDFS3Bucket?.BucketFolderPath == null) || (questionListApi[position].QuestionPDFS3Bucket?.FileName == null))
                        {
                            Toast.makeText(this@QuestionPaperActivity,"File not found",Toast.LENGTH_SHORT).show()
                            return@BindingAdapter
                        }
                        var pdf = Utils.getUrlFromS3Details(
                            BucketFolderPath = questionListApi[position].QuestionPDFS3Bucket?.BucketFolderPath!!,
                            FileName = questionListApi[position].QuestionPDFS3Bucket?.FileName!!
                        ).toString()
                        Log.e("PDF File", "IS" + pdf)
                        var url = ""
                        try {
                            url = URLEncoder.encode(pdf, "UTF-8")
                        } catch (e: UnsupportedEncodingException) {
                            e.printStackTrace()
                        }
                        // pdf = pdf.replace(" ", "%20")
                        //  val sourceUrl = URL(pdf)
                        Log.e("PDf url", "Is Here $url")
                        startActivity(
                            PdfViewerActivity.launchPdfFromUrl(
                                this, pdf,
                                questionListApi[position]?.Title, "", false
                            )
                        )
                    // DownloadPdf(questionListApi[position].QuestionPDFS3Bucket)
                    }
                }
            })
    }

    private fun DownloadPdf(questionPDFS3Bucket: GetQuestionPaperBySubjectResponseModel.QuestionPDFS3Bucket?) {

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
        Dexter.withContext(this)
            .withPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE)
            .withListener(object : PermissionListener {
                override fun onPermissionGranted(response: PermissionGrantedResponse) {
                    // permission is granted
                    Utils.getNonWindowTouchable(this@QuestionPaperActivity)
                    showDialog()
                    Utils.downloadFiles(
                        fixedUrl, this@QuestionPaperActivity
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

    private var downloadComplete: BroadcastReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            try {
                Utils.getWindowTouchable(this@QuestionPaperActivity)
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