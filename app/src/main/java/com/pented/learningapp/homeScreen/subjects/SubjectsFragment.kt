package com.pented.learningapp.homeScreen.subjects

import android.content.Intent
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.View
import androidx.lifecycle.ViewModelProvider
import com.google.gson.Gson
import com.pented.learningapp.R
import com.pented.learningapp.base.BaseFragment
import com.pented.learningapp.base.BaseViewModel
import com.pented.learningapp.databinding.FragmentSubjectsBinding
import com.pented.learningapp.databinding.FragmentTestBinding
import com.pented.learningapp.helper.Constants
import com.pented.learningapp.helper.Constants.subjectListApiBackup
import com.pented.learningapp.helper.JustCopyItVIewModel
import com.pented.learningapp.helper.Utils
import com.pented.learningapp.homeScreen.home.HomeFragment
import com.pented.learningapp.homeScreen.home.viewModel.HomeVM
import com.pented.learningapp.homeScreen.subjects.adapter.*
import com.pented.learningapp.homeScreen.subjects.model.SubjectListResponseModel
import com.pented.learningapp.homeScreen.subjects.model.SubjectRecyclerViewSection
import com.pented.learningapp.homeScreen.subjects.viewModel.SubjectListVM
import kotlinx.android.synthetic.main.fragment_subjects.*
import kotlinx.android.synthetic.main.transparent_progressbar_layout.*
import java.util.*
import kotlin.collections.ArrayList


class SubjectsFragment: BaseFragment<FragmentSubjectsBinding>() {
    var timer = Timer()
    var DELAY: Long = 500
    var generalTextWatcher: TextWatcher? = null
    var searchValue: String? = null

    override fun layoutID() = R.layout.fragment_subjects
    lateinit var adapterRecycler: AdapterSubjectsRecycler
    var sectionHeaders: ArrayList<SubjectRecyclerViewSection> = ArrayList<SubjectRecyclerViewSection>()
    var subjectListApi: ArrayList<SubjectListResponseModel.Subject> = ArrayList<SubjectListResponseModel.Subject>()
    // val subjectListApiBackup: ArrayList<SubjectListResponseModel.Subject> = ArrayList<SubjectListResponseModel.Subject>()
    var subjectListApiFilter: ArrayList<SubjectListResponseModel.Subject> = ArrayList<SubjectListResponseModel.Subject>()
    lateinit var subjectListVM: SubjectListVM
    override fun viewModel(): BaseViewModel = ViewModelProvider(this).get(SubjectListVM::class.java)
    override fun initFragment() {
        init()
        observer()
        listner()
    }

    private fun listner() {
        ivSearch.setOnClickListener {
            lilSearch.visibility = View.VISIBLE
            Utils.showKeyboard(requireActivity(),edtSearch)
            ivSearch.visibility = View.GONE
        }

        ivBack.setOnClickListener {
            requireActivity().sendBroadcast(Intent(Constants.BACKPRESSED))
        }

        icCross.setOnClickListener {
            edtSearch.setText("")
            Utils.hideKeyboard(requireActivity())
            ivSearch.visibility = View.VISIBLE
            lilSearch.visibility = View.GONE
            subjectListApi.clear()
            //subjectListApi = ArrayList<SubjectListResponseModel.Subject>(subjectListApiBackup)
           // setSubjectAdapterAPI("")
           // subjectListApi = subjectListApiBackup
           subjectListVM.callSubjectData()
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
                                setSubjectAdapterAPI(searchValue ?: "")
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
                        subjectListVM.callSubjectData()
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

    private fun setSubjectAdapterAPI(searchValue: String = "") {
        Log.e("Subject List","Is ${Gson().toJson(subjectListApi)} searchValue $searchValue")
        Log.e("Subject List","subjectListApiBackup Is ${Gson().toJson(subjectListApiBackup)} searchValue $searchValue")
        if(!searchValue.isNullOrBlank())
        {
            subjectListApiFilter.clear()
            for (subject in subjectListApi)
            {

                if(subject.Chapters?.size ?: 0 > 0)
                {
                    var isChapterFound = false
                    var chapterList = ArrayList<SubjectListResponseModel.Chapter>()
                    for(chapter in subject.Chapters!!)
                    {
                        if(chapter.Name?.contains(searchValue,true) == true)
                        {
                            isChapterFound = true
                            chapterList.add(chapter)
                        }
                    }
                    if(isChapterFound)
                    {
                        subject.Chapters = chapterList
                        subjectListApiFilter.add(subject)
                    }
                    else{
                        if(subject?.Name?.contains(searchValue,true) == true)
                        {
                            subjectListApiFilter.add(subject)
                        }
                    }

                }
                else
                {
                    if(subject?.Name?.contains(searchValue,true) == true)
                    {
                        subjectListApiFilter.add(subject)
                    }
                }
            }
            val adapter =
                SubjectContainerAdapterAPI(
                    requireActivity(),
                    subjectListApiFilter
                )
            recycler_view.adapter = adapter
        }
        else
        {
            val adapter =
                SubjectContainerAdapterAPI(
                    requireActivity(),
                    subjectListApi
                )
            recycler_view.adapter = adapter
        }

    }

    private fun observer() {
        subjectListVM.observedErrorMessageChanges().observe(this, { event ->
            event?.getContentIfNotHandled()?.let {
                showErrorMessage(it, requireActivity(), mainFrame)
            }
        })


        subjectListVM.observedSubjectData().observe(this, { event ->
            event?.getContentIfNotHandled()?.let {
                Log.e("Home Response Model", "Is here ${it.data}")
                subjectListApi.clear()
                subjectListApiBackup.clear()
                it.data.Subjects?.let { it1 -> subjectListApi.addAll(it1) }

                it.data.Subjects?.let { it1 -> subjectListApiBackup.addAll(it1) }
                setSubjectAdapterAPI()
//                val adapter =
//                    SubjectContainerAdapterAPI(
//                        requireActivity(),
//                        subjectListApi
//                    )
//                recycler_view.adapter = adapter
            }
        })

        subjectListVM.observedChanges().observe(this, { event ->
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

        subjectListVM = (getViewModel() as SubjectListVM)

        if(Constants.isApiCalling)
        {
            subjectListVM.callSubjectData()
        }
        else
        {
            sectionHeaders.clear()
            //Create a List of Child DataModel
            var childList: ArrayList<SubjectChildModel> = ArrayList()
            childList.add(SubjectChildModel("Rational & Numbers","Description of the chapter comes here."))
            childList.add(SubjectChildModel("Linear progressions","Description of the chapter comes here."))
            childList.add(SubjectChildModel("Probability","Description of the chapter comes here."))

            //Create a List of SectionHeader DataModel implements SectionHeader

            var header = SubjectHeaderModel("Maths","24/ 100 Points earned")
            var section1 = SubjectRecyclerViewSection(header,childList)
            sectionHeaders.add(section1)

            childList = ArrayList()
            childList.add(SubjectChildModel("Rational & Numbers","Description of the chapter comes here."))
            childList.add(SubjectChildModel("Linear progressions","Description of the chapter comes here."))
            childList.add(SubjectChildModel("Probability","Description of the chapter comes here."))
            var header1 = SubjectHeaderModel("Science","24/ 100 Points earned")

            var section2 = SubjectRecyclerViewSection(header1,childList)
            sectionHeaders.add(section2)




            val adapter =
                SubjectContainerAdapter(
                    requireActivity(),
                    sectionHeaders
                )
            recycler_view.adapter = adapter
        }


    }

}