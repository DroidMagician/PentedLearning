package com.pented.learningapp.authScreens.fragment

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.app.Activity.RESULT_OK
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.location.*
import android.net.Uri
import android.os.Build
import android.os.Handler
import android.os.Looper
import android.provider.MediaStore
import android.provider.Settings
import android.util.Log
import android.view.View
import android.widget.*
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatActivity.RECEIVER_EXPORTED
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import androidx.lifecycle.ViewModelProvider
import com.devstune.searchablemultiselectspinner.SearchableItem
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
import com.pented.learningapp.MainActivity
import com.pented.learningapp.MyApplication
import com.pented.learningapp.R
import com.pented.learningapp.authScreens.model.GetDropdownResponseModel
import com.pented.learningapp.authScreens.model.GetLanguagesResponseModel
import com.pented.learningapp.authScreens.model.GetSchoolNameResponseModel
import com.pented.learningapp.authScreens.viewModel.RegisterVM
import com.pented.learningapp.base.BaseFragment
import com.pented.learningapp.base.BaseViewModel
import com.pented.learningapp.base.BindingAdapter
import com.pented.learningapp.databinding.FragmentRegisterYourselfOneBinding
import com.pented.learningapp.helper.Constants
import com.pented.learningapp.helper.SharedPrefs
import com.pented.learningapp.helper.Utils
import com.pented.learningapp.helper.cropper.CropImageContract
import com.pented.learningapp.helper.cropper.CropImageContractOptions
import com.pented.learningapp.helper.cropper.CropImageOptions
import com.pented.learningapp.helper.cropper.CropImageView
import com.pented.learningapp.multiLanguageSupport.LocaleManager
import com.pented.learningapp.myUtils.FileUtil
//import com.theartofdev.edmodo.cropper.CropImage
import kotlinx.android.synthetic.main.activity_edit_profile_details.*
import kotlinx.android.synthetic.main.fragment_register_yourself_one.*
import kotlinx.android.synthetic.main.fragment_register_yourself_one.btnRegisterMySelf
import kotlinx.android.synthetic.main.fragment_register_yourself_one.edtSchoolName
import kotlinx.android.synthetic.main.fragment_register_yourself_one.imgView
import kotlinx.android.synthetic.main.fragment_register_yourself_one.linearSearch
import kotlinx.android.synthetic.main.fragment_register_yourself_one.mainFrame
import kotlinx.android.synthetic.main.fragment_register_yourself_one.recycler_view
import kotlinx.android.synthetic.main.fragment_register_yourself_one.spClass
import java.io.File
import java.io.IOException
import java.util.*
import kotlin.collections.ArrayList


class FragmentRegisterYourselfOne : BaseFragment<FragmentRegisterYourselfOneBinding>(),
    LocationListener {
    private var imageUri: Uri? = null
    val REQUEST_PERMISSION_SETTING = 101
    val CAMERA_REQUEST = 102
    lateinit var mBottomDialogNChoose: BottomSheetDialog
    var outputFileUri: Uri? = null
    val REQUEST_CODE_PICK = 103
    var requestCode1: Int = 0
    var file1: File? = null
    private val AUTOCOMPLETE_REQUEST_CODE = 1
    private var items: MutableList<SearchableItem> = ArrayList()
    var uuid: String? = null
    var standards = ArrayList<String>()
    override fun layoutID() = R.layout.fragment_register_yourself_one
    override fun viewModel(): BaseViewModel = ViewModelProvider(this).get(RegisterVM::class.java)
    lateinit var registerViewModel: RegisterVM
    lateinit var receiver: MyReceiver
    private lateinit var autocompleteFragment: AutocompleteSupportFragment
    private lateinit var locationManager: LocationManager
    private lateinit var tvGpsLocation: TextView
    var schoolNameTextList = ArrayList<String>()
    private val locationPermissionCode = 2
    private val readPhoneStatePermissionCode = 3
    var dropdownList = ArrayList<GetDropdownResponseModel.Data>()
    var languagesList = ArrayList<GetLanguagesResponseModel.Data>()
    var schoolNameList = ArrayList<GetSchoolNameResponseModel.Data>()

    companion object {
        fun newInstance(): FragmentRegisterYourselfOne {
            return FragmentRegisterYourselfOne()
        }
    }


    override fun initFragment() {
        //getting recyclerview from xml

        init()
        listener()
        observer()


    }

    private fun observer() {
        registerViewModel.observedErrorMessageChanges().observe(this, { event ->
            event?.getContentIfNotHandled()?.let {
                showErrorMessage(it, requireActivity(), mainFrame)
            }
        })

        registerViewModel.observerLanguageChange().observe(this, { event ->
            event?.getContentIfNotHandled()?.let {
                languagesList.clear()
                languagesList.addAll(it.data)
                hideDialog()
                if (!languagesList.isNullOrEmpty()) {
                    Log.e("Data is", "Here ${it.data[0].LanguageName}")
                }

                for (language in languagesList) {
                    Log.e("language", "is ${language.LanguageName}")
                }


                recycler_view.adapter = BindingAdapter(
                    layoutId = R.layout.row_language_list,
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
                                Log.e(
                                    "Selected Language",
                                    "Is ${languagesList[position]}"
                                )
                                dropdownList.clear()
                                standards.clear()
                                registerViewModel.callGetDropdownList()
                            }

                            R.id.radioButton -> {
                                for (langauges in languagesList) {
                                    langauges.isSelected = false
                                }
                                languagesList[position].isSelected = true
                                recycler_view.adapter?.notifyDataSetChanged()
                                Constants.headerlanguageid =
                                    languagesList[position].LanguageId?.toString()
                                Log.e(
                                    "Selected Language",
                                    "Is ${languagesList[position]}"
                                )
                                dropdownList.clear()
                                standards.clear()
                                registerViewModel.callGetDropdownList()
                            }
                        }
                    })


            }
        })

        registerViewModel.observerSchoolNameChange().observe(this, { event ->
            event?.getContentIfNotHandled()?.let {
                schoolNameList.clear()
                schoolNameList.addAll(it.data)
                hideDialog()
                if (!schoolNameList.isNullOrEmpty()) {
                    Log.e("Data is", "Here ${schoolNameList[0].Value}")
                }
                for (schoolName in schoolNameList) {
                    schoolName.Value?.let { it1 -> schoolNameTextList.add(it1) }
                }
                val adapter: ArrayAdapter<String> =
                    ArrayAdapter<String>(
                        requireActivity(), android.R.layout.simple_list_item_1,
                        schoolNameTextList
                    )

                edtSchoolName.setAdapter(adapter)
                edtSchoolName.setThreshold(1)

            }
        })
        registerViewModel.observerDropdownChange().observe(this, { event ->
            event?.getContentIfNotHandled()?.let {
                dropdownList.clear()
                dropdownList.addAll(it.data)


                for (standard in dropdownList) {
                    standard.Value?.let { standards.add(it) }
                }

                Log.e("standards", "is ${standards.size}")

                val aa = activity?.let {
                    ArrayAdapter(
                        it,
                        R.layout.row_spinner_class,
                        standards
                    )
                }

                Constants.headerstandardid = dropdownList[0].Id
                // Set layout to use when the list of choices appear
                aa?.setDropDownViewResource(R.layout.row_dropdown_class)
                // Set Adapter to Spinner
                spClass?.adapter = aa

                spClass?.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
                    override fun onNothingSelected(parent: AdapterView<*>?) {

                    }

                    override fun onItemSelected(
                        parent: AdapterView<*>?,
                        view: View?,
                        position: Int,
                        id: Long
                    ) {
                        Log.e("On Item Selection", "Value is ${position}")
                        dropdownList[position].LanguageId?.let {
                            SharedPrefs.setSelectedLanguage(
                                requireActivity(),
                                it
                            )
                        }

                        Constants.headerstandardid = dropdownList[position].Id
                        if (dropdownList[position].LanguageId == 1) {
                            LocaleManager.setNewLocale(
                                requireContext(),
                                LocaleManager.GUJARATI
                            );
                            // setNewLocale(requireActivity(), LocaleManager.GUJARATI)
                        } else if (dropdownList[position].LanguageId == 2) {
                            LocaleManager.setNewLocale(
                                requireContext(),
                                LocaleManager.HINDI
                            );
                            // setNewLocale(requireActivity(), LocaleManager.HINDI)
                        } else if (dropdownList[position].LanguageId == 3) {
                            LocaleManager.setNewLocale(
                                requireContext(),
                                LocaleManager.ENGLISH
                            );
                            //  setNewLocale(requireActivity(), LocaleManager.ENGLISH)
                        }
                    }

                }


            }
        })

        registerViewModel.observedChanges().observe(this, { event ->
            event?.getContentIfNotHandled()?.let {
                Log.e("String is", "Here ${it}")
                when (it) {
                    Constants.VISIBLE -> {
                        showDialog()
                    }

                    Constants.HIDE -> {
                        hideDialog()
                    }

                    Constants.NAVIGATE -> {
                        hideDialog()
                    }

                    Constants.SUCCESS -> {
                        Log.e("Tokenis====", "======" + SharedPrefs.getToken(requireActivity()))
                        startActivity(Intent(requireActivity(), MainActivity::class.java))
                        requireActivity().finishAffinity()
                    }

                    else -> {
                        showMessage(it, requireActivity(), mainFrame)
                    }
                }
            }
        })
    }

    override fun onLocationChanged(location: Location) {

        Log.e("Location", "Latitude: " + location.latitude + " , Longitude: " + location.longitude)
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        if (requestCode == locationPermissionCode) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                locationManager =
                    requireActivity().getSystemService(Context.LOCATION_SERVICE) as LocationManager
                if ((ContextCompat.checkSelfPermission(
                        requireActivity(),
                        Manifest.permission.ACCESS_FINE_LOCATION
                    ) != PackageManager.PERMISSION_GRANTED)
                ) {
                    ActivityCompat.requestPermissions(
                        requireActivity(),
                        arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                        locationPermissionCode
                    )
                }
                locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 5000, 5f, this)
            } else {

            }
        }
    }

    override fun onProviderDisabled(provider: String) {
        //super.onProviderDisabled(provider)
    }

    override fun onProviderEnabled(provider: String) {
        //    super.onProviderEnabled(provider)
    }

    fun init() {
        Utils.hideKeyboard(requireActivity())
        Constants.registerPoints = null
        registerViewModel = (getViewModel() as RegisterVM)
        uuid = getDeviceId(requireActivity())
        locationManager =
            requireActivity().getSystemService(Context.LOCATION_SERVICE) as LocationManager
        if ((ContextCompat.checkSelfPermission(
                requireActivity(),
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED)
        ) {
            ActivityCompat.requestPermissions(
                requireActivity(),
                arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                locationPermissionCode
            )
        } else {
            locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 5000, 5f, this)
        }
        if ((ContextCompat.checkSelfPermission(
                requireActivity(),
                Manifest.permission.READ_PHONE_STATE
            ) != PackageManager.PERMISSION_GRANTED)
        ) {
            ActivityCompat.requestPermissions(
                requireActivity(),
                arrayOf(Manifest.permission.READ_PHONE_STATE),
                readPhoneStatePermissionCode
            )
        }

        //Check for Notification Permission
        if ((ContextCompat.checkSelfPermission(
                requireActivity(),
                Manifest.permission.POST_NOTIFICATIONS
            ) != PackageManager.PERMISSION_GRANTED)
        ) {
            ActivityCompat.requestPermissions(
                requireActivity(),
                arrayOf(Manifest.permission.POST_NOTIFICATIONS),
                readPhoneStatePermissionCode
            )
        }


//        edtAddres.inputType = InputType.TYPE_NULL
//        edtAddres.isCursorVisible = false
//        edtAddres.isFocusable = false
        registerViewModel.callLanguageList()
//        registerViewModel.callGetDropdownList()

        receiver = MyReceiver(Handler(Looper.myLooper()!!))
        val intentfilter = IntentFilter()
        intentfilter.addAction("StandardFound")
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            requireActivity()?.registerReceiver(receiver, intentfilter, RECEIVER_EXPORTED)
        } else {
            @Suppress("UnspecifiedRegisterReceiverFlag")
            requireActivity()?.registerReceiver(receiver, intentfilter)
        }
//        requireActivity()?.registerReceiver(receiver, intentfilter)

    }

    fun getDeviceId(context: Context): String? {
        val deviceId: String
        deviceId = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            Settings.Secure.getString(
                context.contentResolver,
                Settings.Secure.ANDROID_ID
            )
        } else {
            "123"
            // val mTelephony = context.getSystemService(AppCompatActivity.TELEPHONY_SERVICE) as TelephonyManager
//            if (mTelephony.deviceId != null) {
//                mTelephony.deviceId
//            } else {
//                Settings.Secure.getString(
//                    context.contentResolver,
//                    Settings.Secure.ANDROID_ID
//                )
//            }
        }
        return deviceId
    }

    private val cropImage = registerForActivityResult(CropImageContract()) { result ->
        if (result.isSuccessful) {
            // Use the cropped image URI.
            val croppedImageUri = result.uriContent
            val croppedImageFilePath = result.getUriFilePath(requireActivity()) // optional usage
            Log.e("Success is", "Here ${croppedImageUri} Path${croppedImageFilePath}")
//            var result = CropImage.getActivityResult(data);
            var finalUri: Uri? = null
            var file: File? = null
            if (croppedImageUri != null) {
                var resultUri = croppedImageUri;
                try {
                    file = FileUtil.from(requireActivity(), resultUri)
//                    MediaScannerConnection.scanFile(this@EditProfileDetailsActivity, arrayOf(file.absolutePath), null,
//                        OnScanCompletedListener { path, uri ->
//                            resultUri = uri
//                            Log.i(
//                                "onScanCompleted",
//                                uri.path!!
//                            )
//                        })
                    finalUri = FileProvider.getUriForFile(
                        requireActivity().getApplicationContext(),
                        requireActivity().getPackageName() + ".fileprovider",
                        file
                    )
                    Log.e(
                        "file",
                        "File...:::: uti - " + file.path + " file -" + file + " : " + file.exists()
                    )
                } catch (e: IOException) {
                    e.printStackTrace()
                }
                registerViewModel.registerRequestModel.mSelectedMediaPath = finalUri?.path
                registerViewModel.registerRequestModel.ImageName = finalUri?.lastPathSegment
                registerViewModel.registerRequestModel.profile_picture = finalUri?.lastPathSegment
                registerViewModel.uploadProfileImage(
                    file?.path!!, finalUri?.lastPathSegment!!,
                    requireActivity()
                )
                imgView.setImageURI(finalUri)
                Log.e(
                    "Cropped Photo",
                    "path=== ${finalUri?.path} lastPathSegment==${finalUri?.lastPathSegment} filePathLastSegment===${file?.path!!}"
                )
//                editProfileDetailsVM.registerRequestModel.mSelectedMediaPath = finalUri?.path
//                editProfileDetailsVM.registerRequestModel.ImageName = finalUri?.lastPathSegment
//                editProfileDetailsVM.registerRequestModel.profile_picture =
//                    finalUri?.lastPathSegment
//                editProfileDetailsVM.uploadProfileImage(
//                    file?.path!!, finalUri?.lastPathSegment!!, this@EditProfileDetailsActivity
//                )
                imgViewSelect.visibility = View.GONE
                imgView.visibility = View.VISIBLE
                // imgView.setImageURI(outputFileUri)
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

    public fun showDialog() {
        Utils.hideKeyboard(requireActivity())
        progressRegisterStep1.visibility = View.VISIBLE
        Utils.getNonWindowTouchable(requireActivity())
    }

    public fun hideDialog() {
        progressRegisterStep1.visibility = View.GONE
        Utils.getWindowTouchable(requireActivity())
    }

    @SuppressLint("SuspiciousIndentation")
    private fun listener() {

        for (i in 0..20) {
            items.add(SearchableItem("Item $i", "$i"))
        }
        imgView.setOnClickListener {
            Dexter.withContext(requireActivity()).withPermissions(
                Manifest.permission.CAMERA,
                (if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) Manifest.permission.READ_MEDIA_IMAGES else Manifest.permission.READ_EXTERNAL_STORAGE)
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
                                requireActivity().packageName,
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
        imgViewSelect.setOnClickListener {
            Dexter.withContext(requireActivity()).withPermissions(
                Manifest.permission.CAMERA,
                (if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) Manifest.permission.READ_MEDIA_IMAGES else Manifest.permission.READ_EXTERNAL_STORAGE)
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
                                requireActivity()?.packageName,
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
        btnRegisterMySelf.setOnClickListener {
            //  registerViewModel.registerRequestModel.AppVersion = "1.0"
            var currentLatitude: Double = -1.0
            var currentLongitude: Double = -1.0
            if ((ContextCompat.checkSelfPermission(
                    requireActivity(),
                    Manifest.permission.ACCESS_FINE_LOCATION
                ) == PackageManager.PERMISSION_GRANTED)
            ) {
                val myLocation =
                    locationManager.getLastKnownLocation(LocationManager.PASSIVE_PROVIDER)

                myLocation?.latitude.let {
                    if (it != null) {
                        currentLatitude = it
                    }
                }
                myLocation?.longitude.let {
                    if (it != null) {
                        currentLongitude = it
                    }
                }
            }

            Log.e("Location OnClick", "Is Here" + currentLatitude + "Longitude" + currentLongitude)
            registerViewModel.studentLocationRequestModel.Latitude =
                currentLatitude?.toString() ?: ""
            registerViewModel.studentLocationRequestModel.Longitude =
                currentLongitude?.toString() ?: ""
//            val myLocation = locationManager.getLastKnownLocation(LocationManager.PASSIVE_PROVIDER)
//
//            currentLatitude = myLocation?.latitude
//            currentLongitude = myLocation?.longitude
//
            registerViewModel.registerRequestModel.MobileNumber = Constants.phoneNumber
            registerViewModel.registerRequestModel.OTP = Constants.OTP
            // registerViewModel.registerRequestModel.DeviceModel = android.os.Build.MODEL
            //  registerViewModel.registerRequestModel.UUID = uuid
            registerViewModel.callRegisterUser()

            //startActivity(Intent(requireActivity(), MainActivity::class.java))
        }


//        edtAddres.setOnClickListener {
//            val fields = listOf(
//                Place.Field.LAT_LNG,
//                Place.Field.ID,
//                Place.Field.NAME,
//                Place.Field.ADDRESS_COMPONENTS,
//                Place.Field.NAME,
//                Place.Field.TYPES,
//                Place.Field.ADDRESS
//            )
//            val apiKey = getString(R.string.places_api_key)
//            if (!Places.isInitialized()) {
//                Places.initialize(requireActivity(), apiKey)
//            }
//            val placesClient = Places.createClient(requireActivity())
//
//            autocompleteFragment =
//                (childFragmentManager.findFragmentById(R.id.autocomplete_fragment) as AutocompleteSupportFragment?)!!
//            linearSearch.visibility = View.GONE
//
//            autocompleteFragment?.setPlaceFields(
//                listOf(
//                    Place.Field.LAT_LNG,
//                    Place.Field.ID,
//                    Place.Field.ADDRESS_COMPONENTS,
//                    Place.Field.NAME, Place.Field.TYPES, Place.Field.ADDRESS
//                )
//            );
//            autocompleteFragment?.setHint("Search Here")
//            // Start the autocomplete intent.
//            val intent = Autocomplete.IntentBuilder(AutocompleteActivityMode.FULLSCREEN, fields)
//                .build(requireActivity())
//            startActivityForResult(intent, AUTOCOMPLETE_REQUEST_CODE)
//        }
    }

    inner class MyReceiver(handler: Handler) : BroadcastReceiver() {
        var handler: Handler = handler // Handler used to execute code on the UI thread
        override fun onReceive(context: Context?, intent: Intent?) {
            Log.e("Intent", "Action ${intent?.action}")
            handler.post {
                run {
                    if (intent?.action.equals("StandardFound")) {


                    }
                }

            }
        }
    }

    private fun showChooseDialog() {
        try {
            val sheetView: View = layoutInflater
                .inflate(R.layout.dialog_bottom_choose, null)
            mBottomDialogNChoose = BottomSheetDialog(requireActivity())
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
                Dexter.withContext(requireActivity())
                    .withPermissions(
                        Manifest.permission.CAMERA
                    )
                    .withListener(object : MultiplePermissionsListener {
                        override fun onPermissionsChecked(report: MultiplePermissionsReport?) {
                            if (report != null) {
                                if (report.areAllPermissionsGranted()) {
                                    val cameraIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
                                    outputFileUri = FileProvider.getUriForFile(
                                        requireActivity(),
                                        context?.getApplicationContext()
                                            ?.getPackageName() + ".fileprovider",
                                        File(
                                            context?.externalCacheDir!!.path,
                                            "pickImageResult.jpeg"
                                        )
                                    )
                                    if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.LOLLIPOP) {
                                        cameraIntent.addFlags(Intent.FLAG_GRANT_WRITE_URI_PERMISSION)
                                    }
                                    cameraIntent.putExtra(
                                        MediaStore.EXTRA_OUTPUT,
                                        outputFileUri
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
                                        requireActivity()?.packageName,
                                        null
                                    )
                                    intent.data = uri
                                    startActivityForResult(intent, REQUEST_PERMISSION_SETTING)
                                }
                            }
                        }

                        override fun onPermissionRationaleShouldBeShown(
                            p0: MutableList<PermissionRequest>?,
                            token: PermissionToken?
                        ) {
                            token?.continuePermissionRequest();
                        }
                    }).check()
            }
            lilChoosePhoto.setOnClickListener {
                Dexter.withContext(requireActivity())
                    .withPermission(if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) Manifest.permission.READ_MEDIA_IMAGES else Manifest.permission.READ_EXTERNAL_STORAGE)
                    .withListener(object : PermissionListener {
                        @RequiresApi(Build.VERSION_CODES.KITKAT)
                        override fun onPermissionGranted(response: PermissionGrantedResponse) {

                            // permission is granted, open the gallery
                            val mimeTypes = arrayOf("image/jpeg", "image/jpg", "image/png")
                            val gallery = Intent(
                                Intent.ACTION_PICK,
                                MediaStore.Images.Media.INTERNAL_CONTENT_URI
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
                                val intent =
                                    Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
                                val uri = Uri.fromParts(
                                    "package",
                                    requireActivity().packageName,
                                    null
                                )
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
            Log.e("Data photo", "Is ${data?.extras?.get("data")}")
            /*  val out = ByteArrayOutputStream()
              photoOriginal?.compress(Bitmap.CompressFormat.JPEG, 90, out)
              val photo = BitmapFactory.decodeStream(ByteArrayInputStream(out.toByteArray()))*/
//            if (photo?.let { Utils.getImageUri(requireActivity(), it) } != null) {
//                file1 = File(
//                    Utils.getRealPathFromURI(
//                        outputFileUri,
//                        requireActivity()
//                    )
//                )
//            }
            if (outputFileUri != null) {
//                file1 = File(
//                    Utils.getRealPathFromURI(
//                        outputFileUri,
//                        requireActivity()
//                    )
//                )
                if (outputFileUri != null) {
                    imgViewSelect.visibility = View.GONE
                    imgView.visibility = View.VISIBLE
                    // imgView.setImageURI(outputFileUri)
//                    CropImage.activity(outputFileUri)
//                        .start(requireActivity(), this);

                }
            } else {
                showMessage(
                    getString(R.string.something_went_wrong),
                    requireActivity(),
                    mainFrame
                )
            }

        } else if (requestCode == AUTOCOMPLETE_REQUEST_CODE) {
            when (resultCode) {
                Activity.RESULT_OK -> {
                    try {

                        val place = Autocomplete.getPlaceFromIntent(data!!)
                        val placesClient = Places.createClient(requireActivity())
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
                        val geocoder = Geocoder(requireActivity(), Locale.getDefault())
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
        } else if (resultCode == RESULT_OK && requestCode == REQUEST_CODE_PICK) {
            mBottomDialogNChoose.dismiss()
            requestCode1 = REQUEST_CODE_PICK
            if (Utils.getRealPathFromURI(
                    data?.data,
                    requireActivity()
                ) != null && Utils.getRealPathFromURI(data?.data, requireActivity()) != ""
            ) {
                file1 = File(Utils.getRealPathFromURI(data?.data, requireActivity()))

            }

            if (file1 != null) {
                imgViewSelect.visibility = View.GONE
                imgView.visibility = View.VISIBLE
//                CropImage.activity(data?.data)
//                    .setGuidelines(CropImageView.Guidelines.ON)
//                    .setActivityMenuIconColor(Color.WHITE)
//                    .setActivityTitle("")
//                    .setAutoZoomEnabled(true)
//                    .setAllowRotation(false)
//                    .setCropShape(CropImageView.CropShape.RECTANGLE)
//                    .setAspectRatio(1, 1)
//                    .setOutputCompressFormat(Bitmap.CompressFormat.PNG)
//                    .start(requireActivity(), this);

            } else {
                showMessage(
                    getString(R.string.something_went_wrong),
                    requireActivity(),
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
//                    val file = FileUtil.from(requireActivity(), resultUri)
////                    MediaScannerConnection.scanFile(requireActivity(), arrayOf(file.absolutePath), null,
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
//               registerViewModel.registerRequestModel.mSelectedMediaPath = finalUri?.path
//                registerViewModel.registerRequestModel.ImageName = finalUri?.lastPathSegment
//                registerViewModel.registerRequestModel.profile_picture = finalUri?.lastPathSegment
//                registerViewModel.uploadProfileImage(
//                    finalUri?.path!!,
//                    finalUri?.lastPathSegment!!,
//                    requireActivity()
//                )
//                imgView.setImageURI(finalUri)
//            } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
//                var error = result.getError();
//                Log.e("Error is", "Here ${error}")
//            }
//        }
    }

    override fun onDestroy() {
        super.onDestroy()
        requireActivity().unregisterReceiver(receiver)
    }
//    public fun showDialog() {
//        Utils.hideKeyboard(requireActivity())
//        lilProgressBar.visibility = View.VISIBLE
//        animationView.visibility = View.VISIBLE
//        Utils.getNonWindowTouchable(requireActivity())
//    }
//
//    public fun hideDialog() {
//        lilProgressBar.visibility = View.GONE
//        animationView.visibility = View.GONE
//        Utils.getWindowTouchable(requireActivity())
//    }
}