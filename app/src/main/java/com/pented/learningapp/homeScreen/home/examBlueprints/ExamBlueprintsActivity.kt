package com.pented.learningapp.homeScreen.home.examBlueprints

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModelProvider
import com.pented.learningapp.BR
import com.pented.learningapp.R
import com.pented.learningapp.base.BaseActivity
import com.pented.learningapp.base.BaseViewModel
import com.pented.learningapp.base.BindingAdapter
import com.pented.learningapp.databinding.ActivityExamBlueprintsBinding
import com.pented.learningapp.helper.Constants
import com.pented.learningapp.helper.Utils
import com.pented.learningapp.homeScreen.home.examBlueprints.model.ExamBluePrintResponseModel
import com.pented.learningapp.homeScreen.home.examBlueprints.model.ExamBlueprintModel
import com.pented.learningapp.homeScreen.home.examBlueprints.viewModel.ExamBlueprintsVM
import com.pented.learningapp.widget.pdfviewer.PdfViewerActivity
import kotlinx.android.synthetic.main.activity_exam_blueprints.*
import kotlinx.android.synthetic.main.transparent_progressbar_layout.*
import java.io.UnsupportedEncodingException
import java.net.URLEncoder
import java.util.*
import kotlin.collections.ArrayList


class ExamBlueprintsActivity : BaseActivity<ActivityExamBlueprintsBinding>() {

    override fun layoutID() = R.layout.activity_exam_blueprints
    var timer = Timer()
    var DELAY: Long = 500
    var generalTextWatcher: TextWatcher? = null
    var searchValue: String? = null

    var examBlueprintList: ArrayList<ExamBlueprintModel> = ArrayList<ExamBlueprintModel>()
    var examBlueprintListAPI: ArrayList<ExamBluePrintResponseModel.Data> = ArrayList<ExamBluePrintResponseModel.Data>()
    var examBlueprintListAPIFilter: ArrayList<ExamBluePrintResponseModel.Data> = ArrayList<ExamBluePrintResponseModel.Data>()
    override fun viewModel(): BaseViewModel = ViewModelProvider(this).get(ExamBlueprintsVM::class.java)
    lateinit var examBlueprintsVM: ExamBlueprintsVM
    private val requiredPermissionList = arrayOf(
        Manifest.permission.WRITE_EXTERNAL_STORAGE,
        Manifest.permission.READ_EXTERNAL_STORAGE
    )
    private var download_file_url = "https://github.com/afreakyelf/Pdf-Viewer/files/5856345/AA.pdf"
    var per = 0f
    private val PERMISSION_CODE = 4040

    override fun initActivity() {
        init()
        observer()
        listner()
    }

    private fun observer() {
        examBlueprintsVM.observedErrorMessageChanges().observe(this, { event ->
            event?.getContentIfNotHandled()?.let {
                showErrorMessage(it, this, mainFrame)
            }
        })
        examBlueprintsVM.observerExamBlueprintData().observe(this, { event ->
            event?.getContentIfNotHandled()?.let {
                examBlueprintListAPI.clear()
                examBlueprintListAPI.addAll(it.data)
                setExamBluePrintAdapterAPI()
            }
        })

        examBlueprintsVM.observedChanges().observe(this, { event ->
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

        ivSearch.setOnClickListener {
            lilSearch.visibility = View.VISIBLE
            Utils.showKeyboard(this@ExamBlueprintsActivity, edtSearch)
            ivSearch.visibility = View.GONE
        }
        icCross.setOnClickListener {
            edtSearch.setText("")
            Utils.hideKeyboard(this@ExamBlueprintsActivity)
            ivSearch.visibility = View.VISIBLE
            lilSearch.visibility = View.GONE
            setExamBluePrintAdapterAPI()
        }

        generalTextWatcher = object : TextWatcher {
            override fun afterTextChanged(s: Editable) {
                timer = Timer()
                timer.schedule(object : TimerTask() {
                    override fun run() {
                        Log.e("Search Text", "Is == ${edtSearch.text.toString()}")
                        if (s.isNotEmpty()) {
                            searchValue = edtSearch.text.toString()
                            runOnUiThread(Runnable {
                                setExamBluePrintAdapterAPI(searchValue ?: "")
                            })

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
                    runOnUiThread(Runnable {
                        setExamBluePrintAdapterAPI()
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

    private fun init() {
        examBlueprintsVM = (getViewModel() as ExamBlueprintsVM)

        if(Constants.isApiCalling)
        {
            examBlueprintsVM.callGetExamBluePrints()
        }
        else
        {
            var examBlueprintModel = ExamBlueprintModel(
                "CBSE, 2018 - Gujarat",
                "Maths blueprint",
                "3 parts"
            )
            var examBlueprintModel1 = ExamBlueprintModel(
                "CBSE, 2020 - Gujarat",
                "Maths blueprint",
                "2 parts"
            )
            examBlueprintList.add(examBlueprintModel)
            examBlueprintList.add(examBlueprintModel1)
            setExamBluePrintAdapter()
        }

    }

    private fun setExamBluePrintAdapter() {
        recycler_view.adapter = BindingAdapter(
            layoutId = R.layout.row_exam_blueprints,
            br = BR.model,
            list = ArrayList(examBlueprintList),
            clickListener = { view, position ->
                when (view.id) {
                    R.id.lilShare -> {

                    }
                }
            })
    }

    private fun setExamBluePrintAdapterAPI(searchValue: String = "") {

        if(!searchValue.isNullOrBlank())
        {
            examBlueprintListAPIFilter.clear()
            for (examBluePrint in examBlueprintListAPI)
            {
                if(examBluePrint.Title?.contains(searchValue)==true || examBluePrint?.SubjectName?.contains(
                        searchValue
                    ) == true)
                {
                    examBlueprintListAPIFilter.add(examBluePrint)
                }
            }
            recycler_view.adapter = BindingAdapter(
                layoutId = R.layout.row_exam_blueprints_api,
                br = BR.model,
                list = ArrayList(examBlueprintListAPIFilter),
                clickListener = { view, position ->
                    when (view.id) {
                        R.id.mainLayout -> {
                            if(examBlueprintListAPIFilter[position].S3Bucket == null || (examBlueprintListAPIFilter[position].S3Bucket?.BucketFolderPath == null) || (examBlueprintListAPIFilter[position].S3Bucket?.FileName == null))
                            {
                                Toast.makeText(this@ExamBlueprintsActivity,"File not found",Toast.LENGTH_SHORT).show()
                                return@BindingAdapter
                            }

                            var pdf = Utils.getUrlFromS3Details(
                                BucketFolderPath = examBlueprintListAPIFilter[position].S3Bucket?.BucketFolderPath!!,
                                FileName = examBlueprintListAPIFilter[position].S3Bucket?.FileName!!
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
                                    examBlueprintListAPIFilter[position]?.Title, "", false
                                )
                            )
//                        Log.e("Clicked File Name:","Is ${examBlueprintListAPI[position].S3Bucket?.FileName}")
//                        examBlueprintListAPI[position].S3Bucket?.let {
//                            startActivityWithData(WebviewActivity::class.java,
//                                it
//                            )
//                        }
                        }
                        R.id.lilShare -> {
                            val share = Intent(Intent.ACTION_SEND)
                            share.type = "text/plain"
                            share.putExtra(Intent.EXTRA_TEXT, "${examBlueprintListAPIFilter[position].Title} for more details download https://play.google.com/store/apps/details?id=com.pented.learningapp")
                            startActivity(Intent.createChooser(share, "Share Text"))
                        }
                    }
                })
        }
        else
        {
            recycler_view.adapter = BindingAdapter(
                layoutId = R.layout.row_exam_blueprints_api,
                br = BR.model,
                list = ArrayList(examBlueprintListAPI),
                clickListener = { view, position ->
                    when (view.id) {
                        R.id.mainLayout -> {
                            if(examBlueprintListAPI[position].S3Bucket == null || (examBlueprintListAPI[position].S3Bucket?.BucketFolderPath == null) || (examBlueprintListAPI[position].S3Bucket?.FileName == null))
                            {
                                Toast.makeText(this@ExamBlueprintsActivity,"File not found",Toast.LENGTH_SHORT).show()
                                return@BindingAdapter
                            }

                            var pdf = Utils.getUrlFromS3Details(
                                BucketFolderPath = examBlueprintListAPI[position].S3Bucket?.BucketFolderPath!!,
                                FileName = examBlueprintListAPI[position].S3Bucket?.FileName!!
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
                                    examBlueprintListAPI[position]?.Title, "", false
                                )
                            )
//                        Log.e("Clicked File Name:","Is ${examBlueprintListAPI[position].S3Bucket?.FileName}")
//                        examBlueprintListAPI[position].S3Bucket?.let {
//                            startActivityWithData(WebviewActivity::class.java,
//                                it
//                            )
//                        }
                        }
                        R.id.lilShare -> {
                            val share = Intent(Intent.ACTION_SEND)
                            share.type = "text/plain"
                            share.putExtra(Intent.EXTRA_TEXT, "${examBlueprintListAPI[position].Title} for more details download https://play.google.com/store/apps/details?id=com.pented.learningapp")
                            startActivity(Intent.createChooser(share, "Share Text"))
                        }
                    }
                })
        }

    }
    private fun launchPdf() {

    }
    private fun checkAndRequestPermission(): Boolean {
        val permissionsNeeded = ArrayList<String>()

        for (permission in requiredPermissionList) {
            if (ContextCompat.checkSelfPermission(this, permission) !=
                PackageManager.PERMISSION_GRANTED
            ) {
                permissionsNeeded.add(permission)
            }
        }

        if (permissionsNeeded.isNotEmpty()) {
            ActivityCompat.requestPermissions(
                this,
                permissionsNeeded.toTypedArray(),
                PERMISSION_CODE
            )
            return false
        }

        return true
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when (requestCode) {
            PERMISSION_CODE -> if (grantResults.isNotEmpty()) {
                val readPermission = grantResults[0] == PackageManager.PERMISSION_GRANTED
                val writePermission = grantResults[1] == PackageManager.PERMISSION_GRANTED
                if (readPermission && writePermission)
                    launchPdf()
                else {
                    Toast.makeText(this, " Permission Denied", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }
}