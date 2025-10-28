package com.pented.learningapp.homeScreen.home.editProfile

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.IntentFilter
import android.graphics.Bitmap
import android.location.Address
import android.location.Geocoder
import android.net.ConnectivityManager
import android.net.Uri
import android.os.Build
import android.provider.MediaStore
import android.provider.Settings
import android.util.Log
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.FrameLayout
import android.widget.LinearLayout
import androidx.annotation.RequiresApi
import androidx.core.content.FileProvider
import androidx.lifecycle.ViewModelProvider
import com.amazonaws.mobileconnectors.s3.transferutility.TransferNetworkLossHandler
//import com.pented.learningapp.helper.cropper.CropImage
//import com.pented.learningapp.helper.cropper.CropImageContract
//import com.pented.learningapp.helper.cropper.CropImageContractOptions
//import com.pented.learningapp.helper.cropper.CropImageOptions
//import com.pented.learningapp.helper.cropper.CropImageView
import com.google.android.libraries.places.api.Places
import com.google.android.libraries.places.api.model.AutocompleteSessionToken
import com.google.android.libraries.places.widget.Autocomplete
import com.google.android.libraries.places.widget.AutocompleteSupportFragment
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.gson.Gson
import com.karumi.dexter.Dexter
import com.karumi.dexter.MultiplePermissionsReport
import com.karumi.dexter.PermissionToken
import com.karumi.dexter.listener.PermissionDeniedResponse
import com.karumi.dexter.listener.PermissionGrantedResponse
import com.karumi.dexter.listener.PermissionRequest
import com.karumi.dexter.listener.multi.MultiplePermissionsListener
import com.karumi.dexter.listener.single.PermissionListener
import com.pented.learningapp.BR
import com.pented.learningapp.R
import com.pented.learningapp.authScreens.fragment.FragmentRegisterYourselfOne
import com.pented.learningapp.authScreens.model.GetDropdownResponseModel
import com.pented.learningapp.authScreens.model.GetLanguagesResponseModel
import com.pented.learningapp.authScreens.model.GetSchoolNameResponseModel
import com.pented.learningapp.base.BaseActivity
import com.pented.learningapp.base.BaseViewModel
import com.pented.learningapp.base.BindingAdapter
import com.pented.learningapp.databinding.ActivityEditProfileBinding
import com.pented.learningapp.helper.Constants
import com.pented.learningapp.helper.SharedPrefs
import com.pented.learningapp.helper.Utils
import com.pented.learningapp.helper.cropper.CropImageContract
import com.pented.learningapp.helper.cropper.CropImageContractOptions
import com.pented.learningapp.helper.cropper.CropImageOptions
import com.pented.learningapp.helper.cropper.CropImageView
import com.pented.learningapp.homeScreen.home.editProfile.viewModel.EditProfileDetailsVM
import com.pented.learningapp.multiLanguageSupport.LocaleManager
import com.pented.learningapp.myUtils.FileUtil
//import com.theartofdev.edmodo.cropper.CropImage
import kotlinx.android.synthetic.main.activity_edit_profile_details.*
import kotlinx.android.synthetic.main.transparent_progressbar_layout.*
import java.io.File
import java.io.IOException
import java.util.*
import kotlin.collections.ArrayList


class EditProfileDetailsActivity : BaseActivity<ActivityEditProfileBinding>() {

    override fun layoutID() = R.layout.activity_edit_profile_details
    private var imageUri: Uri? = null
    val REQUEST_PERMISSION_SETTING = 101
    val CAMERA_REQUEST = 102
    lateinit var mBottomDialogNChoose: BottomSheetDialog
    var outputFileUri: Uri? = null
    val REQUEST_CODE_PICK = 103
    var requestCode1: Int = 0
    var file1: File? = null
    private val AUTOCOMPLETE_REQUEST_CODE = 1
    lateinit var receiver: FragmentRegisterYourselfOne.MyReceiver
    private lateinit var autocompleteFragment: AutocompleteSupportFragment
    var dropdownList = ArrayList<GetDropdownResponseModel.Data>()
    var languagesList = ArrayList<GetLanguagesResponseModel.Data>()
    var schoolNameList = ArrayList<GetSchoolNameResponseModel.Data>()
    var schoolNameTextList = ArrayList<String>()
    var uuid: String? = null
    var standards = ArrayList<String>()
    lateinit var editProfileDetailsVM: EditProfileDetailsVM
    var isProfileDetailsExpand = false
    override fun viewModel(): BaseViewModel =
        ViewModelProvider(this).get(EditProfileDetailsVM::class.java)

    override fun initActivity() {
        init()
        observer()
        listner()
    }

    private fun listner() {
        imgView.setOnClickListener {
            Dexter.withContext(this@EditProfileDetailsActivity).withPermissions(
                Manifest.permission.CAMERA,(if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) Manifest.permission.READ_MEDIA_IMAGES else Manifest.permission.READ_EXTERNAL_STORAGE)
            ).withListener(object : MultiplePermissionsListener {
                override fun onPermissionsChecked(report: MultiplePermissionsReport?) {
                    if (report != null) {
                        if (report.areAllPermissionsGranted()) {
                            cropImage.launch(
                                CropImageContractOptions(
                                    uri = imageUri, cropImageOptions = CropImageOptions(
                                        guidelines = CropImageView.Guidelines.ON,
                                        outputCompressFormat = Bitmap.CompressFormat.PNG
                                    )
                                )
                            )
                        }
                    }
                    if (report != null) {
                        if (report.isAnyPermissionPermanentlyDenied()) {
                            // show alert dialog navigating to Settings
                            val intent =
                                Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
                            val uri = Uri.fromParts(
                                "package",
                                this@EditProfileDetailsActivity?.packageName,
                                null
                            )
                            intent.data = uri
                            startActivityForResult(intent, REQUEST_PERMISSION_SETTING)
                        }
                    }
                }

                override fun onPermissionRationaleShouldBeShown(
                    p0: MutableList<PermissionRequest>?, token: PermissionToken?
                ) {
                    token?.continuePermissionRequest();
                }
            }).check()

        //showChooseDialog()
        }
        lilEditProfileText.setOnClickListener {
//            cropImage.launch(
//                CropImageContractOptions(
//                    uri = imageUri, cropImageOptions = CropImageOptions(
//                        guidelines = CropImageView.Guidelines.ON,
//                        outputCompressFormat = Bitmap.CompressFormat.PNG
//                    )
//                )
//            )
            Dexter.withContext(this@EditProfileDetailsActivity).withPermissions(
                Manifest.permission.CAMERA,(if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) Manifest.permission.READ_MEDIA_IMAGES else Manifest.permission.READ_EXTERNAL_STORAGE)
            ).withListener(object : MultiplePermissionsListener {
                override fun onPermissionsChecked(report: MultiplePermissionsReport?) {
                    if (report != null) {
                        if (report.areAllPermissionsGranted()) {
                            cropImage.launch(
                                CropImageContractOptions(
                                    uri = imageUri, cropImageOptions = CropImageOptions(
                                        guidelines = CropImageView.Guidelines.ON,
                                        outputCompressFormat = Bitmap.CompressFormat.PNG
                                    )
                                )
                            )
                        }
                    }
                    if (report != null) {
                        if (report.isAnyPermissionPermanentlyDenied()) {
                            // show alert dialog navigating to Settings
                            val intent =
                                Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
                            val uri = Uri.fromParts(
                                "package",
                                this@EditProfileDetailsActivity?.packageName,
                                null
                            )
                            intent.data = uri
                            startActivityForResult(intent, REQUEST_PERMISSION_SETTING)
                        }
                    }
                }

                override fun onPermissionRationaleShouldBeShown(
                    p0: MutableList<PermissionRequest>?, token: PermissionToken?
                ) {
                    token?.continuePermissionRequest();
                }
            }).check()
            //showChooseDialog()
        }
        ivBack.setOnClickListener {
            onBackPressed()
        }

        btnRegisterMySelf.setOnClickListener {
//            editProfileDetailsVM.registerRequestModel.StandardId = Constants.headerstandardid
//            editProfileDetailsVM.registerRequestModel.LanguageId = Constants.headerlanguageid
            editProfileDetailsVM.callRegisterUser()

            //startActivity(Intent(this@EditProfileDetailsActivity, MainActivity::class.java))
        }
    }

    //sample usage
    private fun observer() {
        editProfileDetailsVM.observedErrorMessageChanges().observe(this, { event ->
            event?.getContentIfNotHandled()?.let {
                showErrorMessage(it, this, mainFrame)
            }
        })
        editProfileDetailsVM.observerDropdownChange().observe(this, { event ->
            event?.getContentIfNotHandled()?.let {
                dropdownList.clear()
                standards.clear()
                if (!it.data.isNullOrEmpty()) {
                    dropdownList.addAll(it.data)
                }
                for (standard in dropdownList) {
                    standard.Value?.let { standards.add(it) }
                }

                Log.e("standards", "is ${standards.size}")

                val aa = ArrayAdapter(
                    this, R.layout.row_spinner_class, standards
                )

                // Constants.headerlanguageid = dropdownList[0].LanguageId?.toString()
                // Constants.headerstandardid = dropdownList[0].Id
                // Set layout to use when the list of choices appear
                aa?.setDropDownViewResource(R.layout.row_dropdown_class)
                // Set Adapter to Spinner
                spClass?.adapter = aa

                spClass?.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
                    override fun onNothingSelected(parent: AdapterView<*>?) {

                    }

                    override fun onItemSelected(
                        parent: AdapterView<*>?, view: View?, position: Int, id: Long
                    ) {
                        Log.e("On Item Selection", "Value is ${position}")
                        dropdownList[position].LanguageId?.let {
                            SharedPrefs.setSelectedLanguage(
                                this@EditProfileDetailsActivity, it
                            )
                        }

                        Constants.headerstandardid = dropdownList[position].Id
//                        if (dropdownList[position].LanguageId == 1) {
//                            LocaleManager.setNewLocale(
//                                this@EditProfileDetailsActivity,
//                                LocaleManager.GUJARATI
//                            );
//                            // setNewLocale(requireActivity(), LocaleManager.GUJARATI)
//                        } else if (dropdownList[position].LanguageId == 2) {
//                            LocaleManager.setNewLocale(
//                                this@EditProfileDetailsActivity,
//                                LocaleManager.HINDI
//                            );
//                            // setNewLocale(requireActivity(), LocaleManager.HINDI)
//                        } else if (dropdownList[position].LanguageId == 3) {
//                            LocaleManager.setNewLocale(
//                                this@EditProfileDetailsActivity,
//                                LocaleManager.ENGLISH
//                            );
//                            //  setNewLocale(requireActivity(), LocaleManager.ENGLISH)
//                        }
                    }
                }
                var selectedPostion = -1
                for (i in 0 until dropdownList.size) {
                    if (dropdownList[i].Id?.toInt() == EditProfileActivity.studentProfile?.StandardId) {
                        selectedPostion = i
                        break
                    }
                }
                spClass.setSelection(selectedPostion)
                hideDialog()
                //  Log.e("Data is", "Here ${it.data[0].Value}")
            }
        })
        editProfileDetailsVM.observerSchoolNameChange().observe(this, { event ->
            event?.getContentIfNotHandled()?.let {
                schoolNameList.clear()
                schoolNameList.addAll(it.data)
                hideDialog()
                for (schoolName in schoolNameList) {
                    schoolName.Value?.let { it1 -> schoolNameTextList.add(it1) }
                }
                val adapter: ArrayAdapter<String> = ArrayAdapter<String>(
                    this, android.R.layout.simple_list_item_1, schoolNameTextList
                )
                edtSchoolName.setAdapter(adapter)
                edtSchoolName.setThreshold(1)
                if (!schoolNameList.isNullOrEmpty()) {
                    Log.e("Data is", "Here ${schoolNameList[0].Value}")
                }
            }
        })
        editProfileDetailsVM.observerLanguageChange().observe(this, { event ->
            event?.getContentIfNotHandled()?.let {
                languagesList.clear()
                languagesList.addAll(it.data)
                hideDialog()
                Log.e("Language ID", "Is ${EditProfileActivity.studentProfile?.LanguageId}")
                for (langauges in languagesList) {
                    if (langauges.LanguageId == EditProfileActivity.studentProfile?.LanguageId) {
                        langauges.isSelected = true
                    }
                }
                recycler_view.adapter = BindingAdapter(layoutId = R.layout.row_language_list,
                    br = BR.model,
                    list = ArrayList(languagesList),
                    clickListener = { view, position ->
                        when (view.id) {
                            R.id.mainLayout -> {
                                for (langauges in languagesList) {
                                    langauges.isSelected = false
                                }
                                languagesList[position].isSelected = true
                                recycler_view.adapter?.notifyDataSetChanged()
                                Constants.headerlanguageid =
                                    languagesList[position].LanguageId?.toString()
                                dropdownList.clear()
                                standards.clear()
                                editProfileDetailsVM.callGetDropdownList()
                                Log.e("Selected Language", "Is ${languagesList[position]}")
                            }

                            R.id.radioButton -> {
                                for (langauges in languagesList) {
                                    langauges.isSelected = false
                                }
                                languagesList[position].isSelected = true
                                recycler_view.adapter?.notifyDataSetChanged()
                                Constants.headerlanguageid =
                                    languagesList[position].LanguageId?.toString()
                                dropdownList.clear()
                                standards.clear()
                                editProfileDetailsVM.callGetDropdownList()
                                Log.e("Selected Language", "Is ${languagesList[position]}")

                            }
                        }
                    })

                standards.clear()
                for (standard in dropdownList) {
                    standard.Value?.let { standards.add(it) }
                }

                Log.e("standards", "is ${standards.size}")

                val aa = ArrayAdapter(
                    this, R.layout.row_spinner_class, standards
                )

                // Constants.headerlanguageid = dropdownList[0].LanguageId?.toString()
                // Constants.headerstandardid = dropdownList[0].Id
                // Set layout to use when the list of choices appear
                aa?.setDropDownViewResource(R.layout.row_dropdown_class)
                // Set Adapter to Spinner
                spClass?.adapter = aa

                spClass?.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
                    override fun onNothingSelected(parent: AdapterView<*>?) {

                    }

                    override fun onItemSelected(
                        parent: AdapterView<*>?, view: View?, position: Int, id: Long
                    ) {
                        Log.e("On Item Selection", "Value is ${position}")
                        dropdownList[position].LanguageId?.let {
                            SharedPrefs.setSelectedLanguage(
                                this@EditProfileDetailsActivity, it
                            )
                        }

                        Constants.headerstandardid = dropdownList[position].Id
//                        if (dropdownList[position].LanguageId == 1) {
//                            LocaleManager.setNewLocale(
//                                this@EditProfileDetailsActivity,
//                                LocaleManager.GUJARATI
//                            );
//                            // setNewLocale(requireActivity(), LocaleManager.GUJARATI)
//                        } else if (dropdownList[position].LanguageId == 2) {
//                            LocaleManager.setNewLocale(
//                                this@EditProfileDetailsActivity,
//                                LocaleManager.HINDI
//                            );
//                            // setNewLocale(requireActivity(), LocaleManager.HINDI)
//                        } else if (dropdownList[position].LanguageId == 3) {
//                            LocaleManager.setNewLocale(
//                                this@EditProfileDetailsActivity,
//                                LocaleManager.ENGLISH
//                            );
//                            //  setNewLocale(requireActivity(), LocaleManager.ENGLISH)
//                        }
                    }

                }

                var selectedPostion = -1
                for (i in 0 until dropdownList.size) {
                    if (dropdownList[i].Id?.toInt() == EditProfileActivity.studentProfile?.StandardId) {
                        selectedPostion = i
                        break
                    }
                }
                spClass.setSelection(selectedPostion)
//                mySpinner.setSelection(
//                    (mySpinner.getAdapter() as ArrayAdapter<String?>).getPosition(
//                        myString
//                    )
//                )

            }
        })

        editProfileDetailsVM.observedChanges().observe(this, { event ->
            event?.getContentIfNotHandled()?.let {
                when (it) {
                    Constants.VISIBLE -> {
                        showDialog()
                    }

                    Constants.HIDE -> {
                        hideDialog()
                    }

                    Constants.NAVIGATE -> {
                        hideDialog()
                        sendBroadcast(Intent("StandardFound"))
                    }

                    Constants.SUCCESS -> {
                        if (Constants.headerlanguageid?.toInt() == 1) {
                            LocaleManager.setNewLocale(
                                this@EditProfileDetailsActivity, LocaleManager.GUJARATI
                            );
                            // setNewLocale(requireActivity(), LocaleManager.GUJARATI)
                        } else if (Constants.headerlanguageid?.toInt() == 2) {
                            LocaleManager.setNewLocale(
                                this@EditProfileDetailsActivity, LocaleManager.HINDI
                            );
                            // setNewLocale(requireActivity(), LocaleManager.HINDI)
                        } else if (Constants.headerlanguageid?.toInt() == 3) {
                            LocaleManager.setNewLocale(
                                this@EditProfileDetailsActivity, LocaleManager.ENGLISH
                            );
                            //  setNewLocale(requireActivity(), LocaleManager.ENGLISH)
                        }
                        onBackPressed()
                        Constants.isProfileUpdated = true
                    }

                    else -> {
                        hideDialog()
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

    private val cropImage = registerForActivityResult(CropImageContract()) { result ->
        if (result.isSuccessful) {
            // Use the cropped image URI.
            val croppedImageUri = result.uriContent
            val croppedImageFilePath = result.getUriFilePath(this) // optional usage
            Log.e("Success is", "Here ${croppedImageUri} Path${croppedImageFilePath}")
//            var result = CropImage.getActivityResult(data);
            var finalUri: Uri? = null
            var file: File? = null
            if (croppedImageUri != null) {
                var resultUri = croppedImageUri;
                try {
                     file = FileUtil.from(this@EditProfileDetailsActivity, resultUri)
//                    MediaScannerConnection.scanFile(this@EditProfileDetailsActivity, arrayOf(file.absolutePath), null,
//                        OnScanCompletedListener { path, uri ->
//                            resultUri = uri
//                            Log.i(
//                                "onScanCompleted",
//                                uri.path!!
//                            )
//                        })
                    finalUri = FileProvider.getUriForFile(getApplicationContext(), getPackageName()+".fileprovider", file)
                    Log.e(
                        "file",
                        "File...:::: uti - " + file.path + " file -" + file + " : " + file.exists()
                    )
                } catch (e: IOException) {
                    e.printStackTrace()
                }
                Log.e("Cropped Photo", "path=== ${finalUri?.path} lastPathSegment==${finalUri?.lastPathSegment} filePathLastSegment===${file?.path!!}")
                editProfileDetailsVM.registerRequestModel.mSelectedMediaPath = finalUri?.path
                editProfileDetailsVM.registerRequestModel.ImageName = finalUri?.lastPathSegment
                editProfileDetailsVM.registerRequestModel.profile_picture =
                    finalUri?.lastPathSegment
                editProfileDetailsVM.uploadProfileImage(
                    file?.path!!, finalUri?.lastPathSegment!!, this@EditProfileDetailsActivity
                )
                imgView.setImageURI(finalUri)
            } else {
                Log.e("Error is", "Here Error")

            }
            // Process the cropped image URI as needed.
        } else {
            // An error occurred.
            val exception = result.error
            // Handle the error.
        }
    }

    private fun init() {
        editProfileDetailsVM = (getViewModel() as EditProfileDetailsVM)

        // registerReceiver(TransferNetworkLossHandler.getInstance(getApplicationContext()),  IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION));

        if (EditProfileActivity.studentProfile != null) {
            var userImage = EditProfileActivity.studentProfile?.S3Bucket?.FileName?.let { it1 ->
                EditProfileActivity.studentProfile?.S3Bucket?.BucketFolderPath?.let { it2 ->
                    Utils.getUrlFromS3Details(
                        BucketFolderPath = it2, FileName = it1
                    )
                }
            }
            Log.e("Upload","User Image From Backend ${userImage}")
            if (EditProfileActivity.studentProfile?.S3Bucket?.FileName?.isNullOrBlank() == false && EditProfileActivity.studentProfile?.S3Bucket?.BucketFolderPath?.isNullOrBlank() == false) {
                Utils.loadCircleImageUser(imgView, userImage.toString())
            }
            editProfileDetailsVM.registerRequestModel.Name =
                EditProfileActivity.studentProfile?.Name
            editProfileDetailsVM.registerRequestModel.EmailId =
                EditProfileActivity.studentProfile?.Email
            editProfileDetailsVM.registerRequestModel.Address =
                EditProfileActivity.studentProfile?.Adress
            editProfileDetailsVM.registerRequestModel.SchoolName =
                EditProfileActivity.studentProfile?.SchoolName
            etPhone.text = EditProfileActivity?.studentProfile?.MobileNumber
            binding.invalidateAll()
            Constants.headerlanguageid = EditProfileActivity.studentProfile?.LanguageId.toString()
        }
        if (Constants.isApiCalling) {
            dropdownList.clear()
            editProfileDetailsVM.callLanguageList()
            editProfileDetailsVM.callGetDropdownList()
            editProfileDetailsVM.callSchoolNameList()
        }
    }

    private fun showChooseDialog() {
        try {
            val sheetView: View = layoutInflater.inflate(R.layout.dialog_bottom_choose, null)
            mBottomDialogNChoose = BottomSheetDialog(this@EditProfileDetailsActivity)
            mBottomDialogNChoose.setContentView(sheetView)
            mBottomDialogNChoose.show()
            mBottomDialogNChoose.setCancelable(true)
            // Remove default white color background
            val bottomSheet =
                mBottomDialogNChoose.findViewById<View>(R.id.design_bottom_sheet) as FrameLayout?
            val lilChoosePhoto = sheetView.findViewById(R.id.lilChoosePhoto) as LinearLayout
            val lilTakePhoto = sheetView.findViewById(R.id.lilTakePhoto) as LinearLayout
            val lilCancel = sheetView.findViewById(R.id.lilCancel) as LinearLayout
            lilCancel.setOnClickListener { mBottomDialogNChoose.dismiss() }
            lilTakePhoto.setOnClickListener {
                Dexter.withContext(this@EditProfileDetailsActivity).withPermissions(
                        Manifest.permission.CAMERA,
                    ).withListener(object : MultiplePermissionsListener {
                        override fun onPermissionsChecked(report: MultiplePermissionsReport?) {
                            if (report != null) {
                                if (report.areAllPermissionsGranted()) {
                                    val cameraIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
                                    outputFileUri = FileProvider.getUriForFile(
                                        this@EditProfileDetailsActivity,
                                        getApplicationContext()?.getPackageName() + ".fileprovider",
                                        File(
                                            externalCacheDir!!.path, "pickImageResult.jpeg"
                                        )
                                    )
                                    if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.LOLLIPOP) {
                                        cameraIntent.addFlags(Intent.FLAG_GRANT_WRITE_URI_PERMISSION)
                                    }
                                    cameraIntent.putExtra(
                                        MediaStore.EXTRA_OUTPUT, outputFileUri
                                    )
                                    startActivityForResult(cameraIntent, CAMERA_REQUEST)
                                }
                            }
                            if (report != null) {
                                if (report.isAnyPermissionPermanentlyDenied()) {
                                    // show alert dialog navigating to Settings
                                    val intent =
                                        Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
                                    val uri = Uri.fromParts(
                                        "package",
                                        this@EditProfileDetailsActivity?.packageName,
                                        null
                                    )
                                    intent.data = uri
                                    startActivityForResult(intent, REQUEST_PERMISSION_SETTING)
                                }
                            }
                        }

                        override fun onPermissionRationaleShouldBeShown(
                            p0: MutableList<PermissionRequest>?, token: PermissionToken?
                        ) {
                            token?.continuePermissionRequest();
                        }
                    }).check()
            }
            lilChoosePhoto.setOnClickListener {
                Dexter.withContext(this@EditProfileDetailsActivity)
                    .withPermission(if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) Manifest.permission.READ_MEDIA_IMAGES else Manifest.permission.READ_EXTERNAL_STORAGE)
                    .withListener(object : PermissionListener {
                        @RequiresApi(Build.VERSION_CODES.KITKAT)
                        override fun onPermissionGranted(response: PermissionGrantedResponse) {

                            // permission is granted, open the gallery
                            val mimeTypes = arrayOf("image/jpeg", "image/jpg", "image/png")
                            val gallery = Intent(
                                Intent.ACTION_PICK, MediaStore.Images.Media.INTERNAL_CONTENT_URI
                            )
                            gallery.type = "image/*"
                            gallery.putExtra(Intent.EXTRA_MIME_TYPES, mimeTypes)
                            startActivityForResult(gallery, REQUEST_CODE_PICK)


//                            val intent: Intent
//                            intent =
//                                if (Environment.getExternalStorageState() == Environment.MEDIA_MOUNTED) {
//                                    Intent(
//                                        Intent.ACTION_PICK,
//                                        MediaStore.Video.Media.EXTERNAL_CONTENT_URI
//                                    )
//                                } else {
//                                    Intent(
//                                        Intent.ACTION_PICK,
//                                        MediaStore.Video.Media.INTERNAL_CONTENT_URI
//                                    )
//                                }
//                            //  In this example we will set the type to video
//                            //  In this example we will set the type to video
//                            intent.type = "image/*"
//                            intent.action = Intent.ACTION_GET_CONTENT
//                            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
//                            intent.putExtra(Intent.EXTRA_MIME_TYPES, mimeTypes)
//                            startActivityForResult(
//                                intent,
//                                REQUEST_CODE_PICK
//                            )
                        }

                        override fun onPermissionDenied(response: PermissionDeniedResponse) {
                            // check for permanent denial of permission
                            if (response.isPermanentlyDenied) {
                                // navigate user to app settings
                                val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
                                val uri = Uri.fromParts(
                                    "package", this@EditProfileDetailsActivity.packageName, null
                                )
                                intent.data = uri
                                startActivityForResult(intent, REQUEST_PERMISSION_SETTING)
                            }
                        }

                        override fun onPermissionRationaleShouldBeShown(
                            permission: PermissionRequest?, token: PermissionToken
                        ) {
                            token.continuePermissionRequest()
                        }
                    }).check()

            }

            bottomSheet?.background = null
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    @RequiresApi(Build.VERSION_CODES.KITKAT)
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        linearSearch.visibility = View.GONE
        Log.e("requestCode", "= $requestCode resultCode=$resultCode data=$data ${outputFileUri}")
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == CAMERA_REQUEST && resultCode == RESULT_OK) {
            mBottomDialogNChoose.dismiss()
            val photo/*Original*/ = data?.extras?.get("data") as Bitmap?
            Log.e("photo", "Is $photo")
            Log.e("Data photo", "Is ${data?.extras?.get("data")}")/*  val out = ByteArrayOutputStream()
              photoOriginal?.compress(Bitmap.CompressFormat.JPEG, 90, out)
              val photo = BitmapFactory.decodeStream(ByteArrayInputStream(out.toByteArray()))*/
//            if (photo?.let { Utils.getImageUri(this@EditProfileDetailsActivity, it) } != null) {
//                file1 = File(
//                    Utils.getRealPathFromURI(
//                        outputFileUri,
//                        this@EditProfileDetailsActivity
//                    )
//                )
//            }
            if (outputFileUri != null) {
//                file1 = File(
//                    Utils.getRealPathFromURI(
//                        outputFileUri,
//                        this@EditProfileDetailsActivity
//                    )
//                )
                if (outputFileUri != null) {

                    imgView.visibility = View.VISIBLE
                    // imgView.setImageURI(outputFileUri)
//                    val resInfoList = packageManager.queryIntentActivities(
//                        mIntent,
//                        PackageManager.MATCH_DEFAULT_ONLY
//                    )
//                    for (resolveInfo in resInfoList) {
//                        val packageName = resolveInfo.activityInfo.packageName
//                        grantUriPermission(
//                            packageName,
//                            photoURI,
//                            Intent.FLAG_GRANT_WRITE_URI_PERMISSION or Intent.FLAG_GRANT_READ_URI_PERMISSION
//                        )
//                    }
//                    cropImage.launch(
//                        CropImageContractOptions(
//                            uri = imageUri, cropImageOptions = CropImageOptions(
//                                guidelines = CropImageView.Guidelines.ON,
//                                outputCompressFormat = Bitmap.CompressFormat.PNG
//                            )
//                        )
//                    )
//                    CropImage.activity(outputFileUri).start(this@EditProfileDetailsActivity);

                }
            } else {
                showMessage(
                    getString(R.string.something_went_wrong),
                    this@EditProfileDetailsActivity,
                    mainFrame
                )
            }

        }
        else
            if (requestCode == AUTOCOMPLETE_REQUEST_CODE) {
            when (resultCode) {
                Activity.RESULT_OK -> {
                    try {

                        val place = Autocomplete.getPlaceFromIntent(data!!)
                        val placesClient = Places.createClient(this@EditProfileDetailsActivity)
                        val token = AutocompleteSessionToken.newInstance()


//                        try {
//                            if (place.addressComponents != null && place.addressComponents?.asList()
//                                    ?.isNotEmpty() == true
//                            ) {
//                                for (i in 0 until place.addressComponents?.asList()?.size!!) {
//                                    for (j in 0 until place.addressComponents?.asList()!![i].types?.size!!) {
//                                        if (place.addressComponents?.asList()!![i].types[j].equals(
//                                                "administrative_area_level_2",
//                                                true
//                                            )
//                                        ) {
//                                            addressVM.saveAddressRequestModel.county =
//                                                place.addressComponents?.asList()!![i].name
//                                            edtCounty.setText(place.addressComponents?.asList()!![i].name)
//                                        }
//                                    }
//                                }
//                            }
//                        } catch (e: Exception) {
//                            e.printStackTrace()
//                        }
//


                        val latLng = place.latLng
                        val myLat = latLng!!.latitude
                        val myLong = latLng!!.longitude
                        val geocoder = Geocoder(
                            this@EditProfileDetailsActivity, Locale.getDefault()
                        )
                        val addresses: MutableList<Address>? =
                            geocoder.getFromLocation(myLat, myLong, 1)
                        Log.e("LAt", "Long is $myLat and $myLong")
                        Log.e("addresses", " is ${Gson().toJson(addresses)}")

//                        for (i in 0 until countryList.size) {
//                            if (countryList[i].alpha2Code == addresses[0].countryCode) {
//                                addressVM.stateReqModel.countryId =
//                                    countryList[i].numericCode.toString()
//                                fromOnActivty = true
//                                addressVM.getStateList(false)
//                                addressVM.saveAddressRequestModel.countryShortName =
//                                    countryList[i].alpha2Code
//                                addressVM.saveAddressRequestModel.countryFullName =
//                                    countryList[i].name
//                                edtCountry.setText(countryList[i].name)
//                                break
//                            }
//                        }

                    } catch (e: Exception) {
                        //hideProgressBar()
                        e.printStackTrace()
                    }
                }

                2 -> {
                    // hideProgressBar()

                    data?.let {
                        val status = Autocomplete.getStatusFromIntent(data)
                        Log.e("status", "Is ${status.statusMessage}")
                    }
                }

                0 -> {
                    //hideProgressBar()
                }
            }
            return
        }
            else if (resultCode == RESULT_OK && requestCode == REQUEST_CODE_PICK) {
            mBottomDialogNChoose.dismiss()
            requestCode1 = REQUEST_CODE_PICK
            if (Utils.getRealPathFromURI(
                    data?.data, this@EditProfileDetailsActivity
                ) != null && Utils.getRealPathFromURI(
                    data?.data, this@EditProfileDetailsActivity
                ) != ""
            ) {
                file1 = File(Utils.getRealPathFromURI(data?.data, this@EditProfileDetailsActivity))

            }

            if (file1 != null) {

                imgView.visibility = View.VISIBLE
//                cropImage.launch(
//                    CropImageContractOptions(
//                        uri = imageUri, cropImageOptions = CropImageOptions(
//                            guidelines = CropImageView.Guidelines.ON,
//                            outputCompressFormat = Bitmap.CompressFormat.PNG
//                        )
//                    )
//                )
//                CropImage.activity(data?.data)
//                    .setGuidelines(CropImageView.Guidelines.ON)
//                    .setActivityMenuIconColor(Color.WHITE)
//                    .setActivityTitle("")
//                    .setAutoZoomEnabled(true)
//                    .setAllowRotation(false)
//                    .setCropShape(CropImageView.CropShape.RECTANGLE)
//                    .setAspectRatio(1, 1)
//                    .setOutputCompressFormat(Bitmap.CompressFormat.PNG)
//                    .start(this@EditProfileDetailsActivity);

            } else {
                showMessage(
                    getString(R.string.something_went_wrong),
                    this@EditProfileDetailsActivity,
                    mainFrame
                )
            }
        }
//        else  if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
//            var result = CropImage.getActivityResult(data);
//            var finalUri:Uri ? = null
//            if (resultCode == RESULT_OK) {
//                var resultUri = result.getUri();
//                try {
//                    val file = FileUtil.from(this@EditProfileDetailsActivity, resultUri)
////                    MediaScannerConnection.scanFile(this@EditProfileDetailsActivity, arrayOf(file.absolutePath), null,
////                        OnScanCompletedListener { path, uri ->
////                            resultUri = uri
////                            Log.i(
////                                "onScanCompleted",
////                                uri.path!!
////                            )
////                        })
//                    finalUri =Uri.fromFile(file)
//                    Log.e(
//                        "file",
//                        "File...:::: uti - " + file.path + " file -" + file + " : " + file.exists()
//                    )
//                } catch (e: IOException) {
//                    e.printStackTrace()
//                }
//
//                editProfileDetailsVM.registerRequestModel.mSelectedMediaPath = finalUri?.path
//                editProfileDetailsVM.registerRequestModel.ImageName = finalUri?.lastPathSegment
//                editProfileDetailsVM.registerRequestModel.profile_picture = finalUri?.lastPathSegment
//                editProfileDetailsVM.uploadProfileImage(
//                    finalUri?.path!!,
//                    finalUri?.lastPathSegment!!,
//                    this@EditProfileDetailsActivity
//                )
//                imgView.setImageURI(finalUri)
//            } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
//                var error = result.getError();
//                Log.e("Error is", "Here ${error}")
//            }
//        }
    }
}