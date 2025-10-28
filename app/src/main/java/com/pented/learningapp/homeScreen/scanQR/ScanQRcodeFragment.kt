package com.pented.learningapp.homeScreen.scanQR

import android.content.Intent
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.lifecycle.ViewModelProvider
import com.budiyev.android.codescanner.CodeScanner
import com.budiyev.android.codescanner.DecodeCallback
import com.google.gson.Gson
import com.pented.learningapp.R
import com.pented.learningapp.base.BaseFragment
import com.pented.learningapp.base.BaseViewModel
import com.pented.learningapp.databinding.FragmentScanqrBinding
import com.pented.learningapp.helper.Constants
import com.pented.learningapp.helper.Utils
import com.pented.learningapp.homeScreen.scanQR.activity.WatchQRVideoActivity
import com.pented.learningapp.homeScreen.scanQR.viewModel.ScanQRVM
import kotlinx.android.synthetic.main.fragment_scanqr.*
import kotlinx.android.synthetic.main.transparent_progressbar_layout.*


class ScanQRcodeFragment: BaseFragment<FragmentScanqrBinding>() {
    private lateinit var codeScanner: CodeScanner
    override fun layoutID() = R.layout.fragment_scanqr
    override fun viewModel(): BaseViewModel = ViewModelProvider(this).get(ScanQRVM::class.java)
    lateinit var scanQRVM: ScanQRVM
    override fun initFragment() {
        scanQRVM = (getViewModel() as ScanQRVM)
        init()
        observer()
        listeners()
    }

    private fun init() {
        codeScanner = CodeScanner(requireActivity(), scanner_view)
        codeScanner.decodeCallback = DecodeCallback {
            activity?.runOnUiThread {
                //scanQRVM.callscanQRData(it.text)
                scanQRVM.callscanQRData(it.text)
               // Toast.makeText(activity, it.text, Toast.LENGTH_LONG).show()
            }
        }
        scanner_view.setOnClickListener {
            codeScanner.startPreview()
        }
    }
    private fun observer() {
        scanQRVM.observedErrorMessageChanges().observe(this, { event ->
            event?.getContentIfNotHandled()?.let {
                showErrorMessage(it, requireActivity(), mainFrame)
            }
        })


        scanQRVM.observedscanQRChanges().observe(this, { event ->
            event?.getContentIfNotHandled()?.let {
                Log.e("TAG", "observer: Data is ${it.data.TopicVideoTitle}" )
                Constants.subjectIdForScanQR = it.data.SubjectId?.toString()
                val gson = Gson()
                var intent = Intent(requireActivity(), WatchQRVideoActivity::class.java)
                //  intent.putExtra("topicVideoId",topicDataList[current].TopicVideoId)
                intent.putExtra("topicVideo",gson.toJson(it))
                Log.e("topicVideoId===",gson.toJson(it))
                startActivity(intent)
            }
        })

        scanQRVM.observedChanges().observe(this, { event ->
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
                        codeScanner.startPreview()
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
    override fun onResume() {
        super.onResume()
        codeScanner.startPreview()
    }

    override fun onPause() {
        codeScanner.releaseResources()
        super.onPause()
    }
    private fun listeners() {

    }
}