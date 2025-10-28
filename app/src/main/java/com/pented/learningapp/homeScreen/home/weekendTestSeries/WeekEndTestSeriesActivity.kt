
package com.pented.learningapp.homeScreen.home.weekendTestSeries

import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.View
import androidx.lifecycle.ViewModelProvider
import com.pented.learningapp.BR
import com.pented.learningapp.R
import com.pented.learningapp.base.BaseActivity
import com.pented.learningapp.base.BaseViewModel
import com.pented.learningapp.base.BindingAdapter
import com.pented.learningapp.databinding.ActivityWeekendTestSeriesBinding
import com.pented.learningapp.helper.Constants
import com.pented.learningapp.helper.JustCopyItVIewModel
import com.pented.learningapp.helper.Utils
import com.pented.learningapp.homeScreen.home.liveClasses.viewModel.LiveClassVM
import com.pented.learningapp.homeScreen.home.weekendTestSeries.adapter.WeekendSeriesContainerAdapter
import com.pented.learningapp.homeScreen.home.weekendTestSeries.model.WeekendSeriesChildModel
import com.pented.learningapp.homeScreen.home.weekendTestSeries.model.WeekendSeriesHeaderModel
import com.pented.learningapp.homeScreen.home.weekendTestSeries.model.WeekendSeriesRecyclerViewSection
import com.pented.learningapp.homeScreen.home.weekendTestSeries.model.WeekendTestSeriesResponseModel
import com.pented.learningapp.homeScreen.home.weekendTestSeries.viewModel.WeekendTestSeriesVM
import com.pented.learningapp.homeScreen.webView.WebviewActivity
import com.pented.learningapp.widget.pdfviewer.PdfViewerActivity
import kotlinx.android.synthetic.main.activity_weekend_test_series.*
import kotlinx.android.synthetic.main.activity_weekend_test_series.edtSearch
import kotlinx.android.synthetic.main.activity_weekend_test_series.icCross
import kotlinx.android.synthetic.main.activity_weekend_test_series.ivBack
import kotlinx.android.synthetic.main.activity_weekend_test_series.ivSearch
import kotlinx.android.synthetic.main.activity_weekend_test_series.lilSearch
import kotlinx.android.synthetic.main.activity_weekend_test_series.mainFrame
import kotlinx.android.synthetic.main.activity_weekend_test_series.recycler_view
import kotlinx.android.synthetic.main.transparent_progressbar_layout.*
import java.util.*
import kotlin.collections.ArrayList


class WeekEndTestSeriesActivity: BaseActivity<ActivityWeekendTestSeriesBinding>() {

    override fun layoutID() = R.layout.activity_weekend_test_series
    var sectionHeaders: ArrayList<WeekendSeriesRecyclerViewSection> = ArrayList<WeekendSeriesRecyclerViewSection>()
    var weekendTestSeriesList: ArrayList<WeekendTestSeriesResponseModel.Data> = ArrayList<WeekendTestSeriesResponseModel.Data>()
    var weekendTestSeriesListFilter: ArrayList<WeekendTestSeriesResponseModel.Data> = ArrayList<WeekendTestSeriesResponseModel.Data>()
    override fun viewModel(): BaseViewModel = ViewModelProvider(this).get(WeekendTestSeriesVM::class.java)
    lateinit var weekendTestSeriesVM: WeekendTestSeriesVM
    var timer = Timer()
    var DELAY: Long = 500
    var generalTextWatcher: TextWatcher? = null
    var searchValue: String? = null

    override fun initActivity() {

        init()
        observer()
        listeners()
    }

    private fun init() {
        weekendTestSeriesVM = (getViewModel() as WeekendTestSeriesVM)

        if(Constants.isApiCalling)
        {
            weekendTestSeriesVM.callWeekendTestSeries()
        }
        else{
            sectionHeaders.clear()
            //Create a List of Child DataModel
            var childList: ArrayList<WeekendSeriesChildModel> = ArrayList()
            childList.add(WeekendSeriesChildModel("Rational & Numbers","Part 1"))

            //Create a List of SectionHeader DataModel implements SectionHeader

            var header = WeekendSeriesHeaderModel("Maths","24 questions, 3 hours")
            var section1 = WeekendSeriesRecyclerViewSection(header,childList)
            sectionHeaders.add(section1)

            childList = ArrayList()
            childList.add(WeekendSeriesChildModel("Rational & Numbers","Part 2"))

            var header1 = WeekendSeriesHeaderModel("Maths - 2","24 questions, 3 hours")

            var section2 = WeekendSeriesRecyclerViewSection(header1,childList)
            sectionHeaders.add(section2)




            val adapter =
                WeekendSeriesContainerAdapter(
                    this@WeekEndTestSeriesActivity,
                    sectionHeaders
                )
            recycler_view.adapter = adapter
        }

    }
    private fun observer() {
        weekendTestSeriesVM.observedErrorMessageChanges().observe(this, { event ->
            event?.getContentIfNotHandled()?.let {
                showErrorMessage(it, this, mainFrame)
            }
        })
        weekendTestSeriesVM.observerWeekendTestSeriesData().observe(this, { event ->
            event?.getContentIfNotHandled()?.let {
                weekendTestSeriesList.clear()
                weekendTestSeriesList.addAll(it.data)
                setWeekendTestSeriesData()
            }
        })

        weekendTestSeriesVM.observedChanges().observe(this, { event ->
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

    private fun setWeekendTestSeriesData(searchValue: String = "") {
        if(!searchValue.isNullOrBlank())
        {
            weekendTestSeriesListFilter.clear()
            for (weekendTestSeries in weekendTestSeriesList)
            {
                if(weekendTestSeries.Title?.contains(searchValue) == true || weekendTestSeries.SubjectName?.contains(searchValue) == true)
                {
                    weekendTestSeriesListFilter.add(weekendTestSeries)
                }
            }


            recycler_view.adapter = BindingAdapter(
                layoutId = R.layout.row_weekend_test_series_api,
                br = BR.model,
                list = ArrayList(weekendTestSeriesListFilter),
                clickListener = { view, position ->
                    when (view.id) {
                        R.id.mainLayout -> {
//                            var pdf = Utils.getUrlFromS3Details(
//                                BucketFolderPath = weekendTestSeriesList[position].S3Bucket?.BucketFolderPath!!,
//                                FileName = weekendTestSeriesList[position].S3Bucket?.FileName!!
//                            ).toString()

                            weekendTestSeriesList[position].TestUrl?.let {
                                startActivityWithData(
                                    WebviewActivity::class.java,
                                    it
                                )
                            }
//                            startActivity(
//                                PdfViewerActivity.launchPdfFromUrl(
//                                    this, pdf,
//                                    weekendTestSeriesList[position]?.Title, "",false
//                                )
//                            )
                            //Log.e("PDF File","IS"+pdf)
                        }
                    }
                })
        }
        else
        {
            recycler_view.adapter = BindingAdapter(
                layoutId = R.layout.row_weekend_test_series_api,
                br = BR.model,
                list = ArrayList(weekendTestSeriesList),
                clickListener = { view, position ->
                    when (view.id) {
                        R.id.mainLayout -> {
//                            var pdf = Utils.getUrlFromS3Details(
//                                BucketFolderPath = weekendTestSeriesList[position].S3Bucket?.BucketFolderPath!!,
//                                FileName = weekendTestSeriesList[position].S3Bucket?.FileName!!
//                            ).toString()

                            weekendTestSeriesList[position].TestUrl?.let {
                                startActivityWithData(
                                    WebviewActivity::class.java,
                                    it
                                )
                            }
//                            startActivity(
//                                PdfViewerActivity.launchPdfFromUrl(
//                                    this, pdf,
//                                    weekendTestSeriesList[position]?.Title, "",false
//                                )
//                            )
                            //Log.e("PDF File","IS"+pdf)
                        }
                    }
                })
        }


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
    private fun listeners() {
        ivBack.setOnClickListener {
            onBackPressed()
        }

        ivSearch.setOnClickListener {
            lilSearch.visibility = View.VISIBLE
            Utils.showKeyboard(this@WeekEndTestSeriesActivity,edtSearch)
            ivSearch.visibility = View.GONE
        }
        icCross.setOnClickListener {
            edtSearch.setText("")
            Utils.hideKeyboard(this@WeekEndTestSeriesActivity)
            ivSearch.visibility = View.VISIBLE
            lilSearch.visibility = View.GONE
            setWeekendTestSeriesData()
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
                                setWeekendTestSeriesData(searchValue ?: "")
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
                        setWeekendTestSeriesData()
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
}