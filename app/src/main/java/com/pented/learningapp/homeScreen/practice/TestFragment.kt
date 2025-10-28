package com.pented.learningapp.homeScreen.practice

import android.content.Intent
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.View
import androidx.lifecycle.ViewModelProvider
import com.pented.learningapp.R
import com.pented.learningapp.base.BaseFragment
import com.pented.learningapp.base.BaseViewModel
import com.pented.learningapp.databinding.FragmentTestBinding
import com.pented.learningapp.helper.Constants
import com.pented.learningapp.helper.JustCopyItVIewModel
import com.pented.learningapp.helper.Utils
import com.pented.learningapp.homeScreen.practice.adapter.AdapterSectionRecycler
import com.pented.learningapp.homeScreen.practice.adapter.Child
import com.pented.learningapp.homeScreen.practice.adapter.Header
import com.pented.learningapp.homeScreen.practice.adapter.SectionHeader
import com.pented.learningapp.homeScreen.practice.model.GetQuestionPaperResponseModel
import com.pented.learningapp.homeScreen.practice.viewModel.PracticeListVM
import com.pented.learningapp.homeScreen.subjects.adapter.SubjectContainerAdapterAPI
import com.pented.learningapp.homeScreen.subjects.model.SubjectListResponseModel
import com.pented.learningapp.homeScreen.subjects.viewModel.SubjectListVM
import kotlinx.android.synthetic.main.fragment_subjects.*
import kotlinx.android.synthetic.main.fragment_test.*
import kotlinx.android.synthetic.main.fragment_test.edtSearch
import kotlinx.android.synthetic.main.fragment_test.icCross
import kotlinx.android.synthetic.main.fragment_test.ivBack
import kotlinx.android.synthetic.main.fragment_test.ivSearch
import kotlinx.android.synthetic.main.fragment_test.lilSearch
import kotlinx.android.synthetic.main.fragment_test.mainFrame
import kotlinx.android.synthetic.main.fragment_test.recycler_view
import kotlinx.android.synthetic.main.transparent_progressbar_layout.*
import java.util.*
import kotlin.collections.ArrayList


class TestFragment: BaseFragment<FragmentTestBinding>() {

    override fun layoutID() = R.layout.fragment_test
    lateinit var adapterRecycler: AdapterSectionRecycler
    var sectionHeaders: ArrayList<SectionHeader> = ArrayList<SectionHeader>()
    var timer = Timer()
    var DELAY: Long = 500
    var generalTextWatcher: TextWatcher? = null
    var searchValue: String? = null
    override fun viewModel(): BaseViewModel = ViewModelProvider(this).get(PracticeListVM::class.java)
    lateinit var practiceListVM: PracticeListVM

    var practiceListApi: ArrayList<GetQuestionPaperResponseModel.Data> = ArrayList<GetQuestionPaperResponseModel.Data>()
    // val subjectListApiBackup: ArrayList<SubjectListResponseModel.Subject> = ArrayList<SubjectListResponseModel.Subject>()
    var practiceListApiFilter: ArrayList<GetQuestionPaperResponseModel.Data> = ArrayList<GetQuestionPaperResponseModel.Data>()


    override fun initFragment() {

        //Create a List of Child DataModel

        init()
        listeners()
        observer()
    }

    private fun observer() {
        practiceListVM.observedErrorMessageChanges().observe(this, { event ->
            event?.getContentIfNotHandled()?.let {
                showErrorMessage(it, requireActivity(), mainFrame)
            }
        })


        practiceListVM.observedQuestionPaperListData().observe(this, { event ->
            event?.getContentIfNotHandled()?.let {
                Log.e("Home Response Model", "Is here ${it.data}")
                practiceListApi.clear()
                practiceListApiFilter.clear()

                it.data?.let { it1 -> practiceListApi.addAll(it1) }
                //Create a List of SectionHeader DataModel implements SectionHeader
                sectionHeaders = ArrayList()

                setQuestionPaperAPI()
//                val adapter =
//                    SubjectContainerAdapterAPI(
//                        requireActivity(),
//                        subjectListApi
//                    )
//                recycler_view.adapter = adapter
            }
        })

        practiceListVM.observedChanges().observe(this, { event ->
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
                        showMessage(it, requireActivity(), mainFrame)
                    }
                }
            }
        })
    }

    private fun setQuestionPaperAPI(searchValue: String = "") {
//        for (mainPaper in practiceListApi)
//        {
//            var childList: ArrayList<GetQuestionPaperResponseModel.QuestionPaper> = ArrayList()
//            var header = Header("${mainPaper.SubjectTitle}","${mainPaper.SubjectId}")
//            for (questionPaper in mainPaper.QuestionPapers!!)
//            {
//                questionPaper?.SubjectId = mainPaper.SubjectId
//                questionPaper?.SubjectTitle = mainPaper.SubjectTitle
//                childList.add(questionPaper)
//            }
//            //mainPaper.QuestionPapers?.let { it1 -> childList.addAll(it1) }
//            mainPaper.S3Bucket?.let { SectionHeader(childList, header, it) }?.let {
//                sectionHeaders.add(
//                    it
//                )
//            }
//
//        }

        if(!searchValue.isNullOrBlank())
        {
            practiceListApiFilter.clear()
            sectionHeaders = ArrayList()
            for (subject in practiceListApi)
            {

                if(subject.QuestionPapers?.size ?: 0 > 0)
                {
                    var isChapterFound = false
                    var chapterList = ArrayList<GetQuestionPaperResponseModel.QuestionPaper>()
//                    for(chapter in subject.QuestionPapers!!)
//                    {
//                        if(chapter.Title?.contains(searchValue,true) == true)
//                        {
//                            isChapterFound = true
//                            chapterList.add(chapter)
//                        }
//                    }
                    if(isChapterFound)
                    {
                        subject.QuestionPapers = chapterList
                        practiceListApiFilter.add(subject)
                    }
                    else{
                        if(subject?.SubjectTitle?.contains(searchValue,true) == true)
                        {
                            practiceListApiFilter.add(subject)
                        }
                    }

                }
                else
                {
                    if(subject?.SubjectTitle?.contains(searchValue,true) == true)
                    {
                        practiceListApiFilter.add(subject)
                    }
                }
            }
            for (mainPaper in practiceListApiFilter)
            {
                var childList: ArrayList<GetQuestionPaperResponseModel.QuestionPaper> = ArrayList()
                var header = Header("${mainPaper.SubjectTitle}","${mainPaper.SubjectId}","${mainPaper.Points}","${mainPaper.TotalQuestionPapers}")
//                for (questionPaper in mainPaper.QuestionPapers!!)
//                {
//                    questionPaper?.SubjectId = mainPaper.SubjectId
//                    questionPaper?.SubjectTitle = mainPaper.SubjectTitle
//                    childList.add(questionPaper)
//                }
                childList.clear()

                //mainPaper.QuestionPapers?.let { it1 -> childList.addAll(it1) }
                mainPaper.S3Bucket?.let { SectionHeader(childList, header, it) }?.let {
                    sectionHeaders.add(
                        it
                    )
                }

            }

            adapterRecycler = AdapterSectionRecycler(requireActivity(), sectionHeaders)
            recycler_view.setAdapter(adapterRecycler)
//            val adapter =
//                SubjectContainerAdapterAPI(
//                    requireActivity(),
//                    subjectListApiFilter
//                )
//            recycler_view.adapter = adapter
        }
        else
        {
            sectionHeaders = ArrayList()
            for (mainPaper in practiceListApi)
            {
                var childList: ArrayList<GetQuestionPaperResponseModel.QuestionPaper> = ArrayList()
                var header = Header("${mainPaper.SubjectTitle}","${mainPaper.SubjectId}","${mainPaper.Points}","${mainPaper.TotalQuestionPapers}")
//                for (questionPaper in mainPaper.QuestionPapers!!)
//                {
//                    questionPaper?.SubjectId = mainPaper.SubjectId
//                    questionPaper?.SubjectTitle = mainPaper.SubjectTitle
//                    childList.add(questionPaper)
//                }

                //mainPaper.QuestionPapers?.let { it1 -> childList.addAll(it1) }
                mainPaper.S3Bucket?.let { SectionHeader(childList, header, it) }?.let {
                    sectionHeaders.add(
                        it
                    )
                }

            }

            adapterRecycler = AdapterSectionRecycler(requireActivity(), sectionHeaders)
            recycler_view.setAdapter(adapterRecycler)

//            val adapter =
//                SubjectContainerAdapterAPI(
//                    requireActivity(),
//                    subjectListApi
//                )
//            recycler_view.adapter = adapter
        }





//                childList.add(Child("Rational & Numbers","24 questions"))
//                childList.add(Child("Linear progressions","16 questions"))
//                childList.add(Child("Probability","16 questions"))



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
        practiceListVM = (getViewModel() as PracticeListVM)
        practiceListVM.callGetQuestionPaperList()
    }

    private fun listeners() {
        ivBack.setOnClickListener {
            requireActivity().sendBroadcast(Intent(Constants.BACKPRESSED))
        }

        ivSearch.setOnClickListener {
            lilSearch.visibility = View.VISIBLE
            Utils.showKeyboard(requireActivity(),edtSearch)
            ivSearch.visibility = View.GONE
        }


        icCross.setOnClickListener {
            edtSearch.setText("")
            Utils.hideKeyboard(requireActivity())
            ivSearch.visibility = View.VISIBLE
            lilSearch.visibility = View.GONE
            practiceListApi.clear()
            //subjectListApi = ArrayList<SubjectListResponseModel.Subject>(subjectListApiBackup)
            // setSubjectAdapterAPI("")
            // subjectListApi = subjectListApiBackup
            practiceListVM.callGetQuestionPaperList()
        }

        generalTextWatcher = object : TextWatcher {
            override fun afterTextChanged(s: Editable) {
                timer = Timer()
                timer.schedule(object : TimerTask() {
                    override fun run() {
                        Log.e("Search Text", "Is == ${edtSearch.text.toString()}")
                        if (s.isNotEmpty()) {
                            searchValue = edtSearch.text.toString()
                            requireActivity()?.runOnUiThread(Runnable {
                                setQuestionPaperAPI(searchValue ?: "")
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
                    requireActivity()?.runOnUiThread(Runnable {
                        // subjectListApi = subjectListApiBackup
                        // subjectListApi.clear()
                        // subjectListApi= ArrayList<SubjectListResponseModel.Subject>(subjectListApiBackup)
                        // setSubjectAdapterAPI("")
                        practiceListVM.callGetQuestionPaperList()
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
