package com.pented.learningapp.helper

import android.animation.ValueAnimator
import android.annotation.SuppressLint
import android.app.*
import android.content.ContentValues
import android.content.Context
import android.content.Intent
import android.database.Cursor
import android.graphics.Bitmap
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.provider.MediaStore
import android.util.Log
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.view.WindowManager
import android.view.animation.DecelerateInterpolator
import android.view.inputmethod.InputMethodManager
import android.webkit.MimeTypeMap
import android.webkit.URLUtil
import android.widget.*
import androidx.annotation.RequiresApi
import com.amazonaws.services.s3.model.GeneratePresignedUrlRequest
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.google.firebase.analytics.FirebaseAnalytics
import com.google.firebase.analytics.ktx.analytics
import com.google.firebase.analytics.ktx.logEvent
import com.google.firebase.ktx.Firebase
import com.pented.learningapp.MyApplication
import com.pented.learningapp.R
import com.pented.learningapp.amazonS3.S3Util
import com.pented.learningapp.authScreens.GetStartedActivity
import de.hdodenhof.circleimageview.CircleImageView
import java.io.*
import java.net.URL
import java.text.SimpleDateFormat
import java.util.*

object Utils {
    @RequiresApi(Build.VERSION_CODES.M)
    @SuppressLint("MissingPermission")
    fun isConnected(context: Context): Boolean {
//        val connectivityManager =
//            context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
//        val info = connectivityManager.activeNetworkInfo
//        return info != null && info.isConnected
        val connectivityManager =
            context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        if (connectivityManager != null) {
            val capabilities =
                connectivityManager.getNetworkCapabilities(connectivityManager.activeNetwork)
            if (capabilities != null) {
                if (capabilities.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR)) {
                    Log.i("Internet", "NetworkCapabilities.TRANSPORT_CELLULAR")
                    return true
                } else if (capabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI)) {
                    Log.i("Internet", "NetworkCapabilities.TRANSPORT_WIFI")
                    return true
                } else if (capabilities.hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET)) {
                    Log.i("Internet", "NetworkCapabilities.TRANSPORT_ETHERNET")
                    return true
                }
            }
        }
        return false
    }


    fun showReminderDialog(context: Context) {
        AlertDialog.Builder(context).setMessage("test").show()
//        val dialog = Dialog(context)
//        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
//        dialog.setCancelable(false)
//        dialog.setContentView(R.layout.dialog_login_again)
//        dialog.getWindow()?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
//
//        var lilRemindMeLater = dialog.findViewById<LinearLayout>(R.id.lilRemindMeLater)
//        var llSubscribe = dialog.findViewById<LinearLayout>(R.id.llSubscribe)
//        var txtTitle = dialog.findViewById<TextView>(R.id.txtTitle)
//        var btnContinue = dialog.findViewById<Button>(R.id.btnContinue)
//        var txtDescription = dialog.findViewById<TextView>(R.id.txtDescription)
//        //   txtTitle.text = title
//        // txtDescription.text = description
//
//        lilRemindMeLater.setOnClickListener {
//            dialog.dismiss()
//        }
//
//        btnContinue.setOnClickListener {
//            dialog.dismiss()
//            val loginIntent = Intent(context, GetStartedActivity::class.java)
//            loginIntent.putExtra("TIMEOUT", true)
//            val preferences = SharedPrefs.getSharedPreference(context)
//            val editor = preferences?.edit()
//            editor?.clear()
//            editor?.apply()
//            TaskStackBuilder.create(context)
//                .addNextIntentWithParentStack(loginIntent)
//                .startActivities();
//            //startActivity(ChooseYourSubscriptionActivity::class.java)
//        }
//
//        dialog.show()
    }
    fun showKeyboard(activity: Activity, editText: EditText) {
        val inputMethodManager: InputMethodManager =
            activity.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        inputMethodManager.toggleSoftInputFromWindow(
            editText.applicationWindowToken,
            InputMethodManager.SHOW_IMPLICIT, 0
        )
        editText.requestFocus()
       // editText.setSelection(editText.text.length)
    }
    fun downloadFiles(url: String, activity: Activity) {

        val fileName = URLUtil.guessFileName(url, null, MimeTypeMap.getFileExtensionFromUrl(url))

        val uri = Uri.parse(url)
        val request = DownloadManager.Request(uri)
        request.setAllowedNetworkTypes(DownloadManager.Request.NETWORK_WIFI or DownloadManager.Request.NETWORK_MOBILE)
        request.setTitle(fileName)
        request.setDescription("file is downloading ......")

        request.allowScanningByMediaScanner()
        request.setMimeType(Utils.getMimeType(uri.toString()));

        request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED)

        request.setDestinationInExternalPublicDir(
            Environment.DIRECTORY_DOWNLOADS,
            uri.lastPathSegment
        )

        val manager = activity.getSystemService(Context.DOWNLOAD_SERVICE) as DownloadManager
/*
        manager.getMimeTypeForDownloadedFile(manager.enqueue(request))
*/
        manager.enqueue(request)

    }
    fun getMimeType(url: String): String? {
        var type: String? = null
        val extension = MimeTypeMap.getFileExtensionFromUrl(url)
        if (extension != null) {
            val mime = MimeTypeMap.getSingleton()
            type = mime.getMimeTypeFromExtension(extension)
        }
        return type
    }

    fun hideKeyboard(activity: Activity) {
        val imm = activity.getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager
        //Find the currently focused view, so we can grab the correct window token from it.
        var view = activity.currentFocus
        //If no_data_layout view currently has focus, create a new one, just so we can grab a window token from it
        if (view == null) {
            view = View(activity)
        }
        imm.hideSoftInputFromWindow(view.windowToken, 0)
    }
    fun expandOrCollapseViewGloble(v: View, expand: Boolean) {
        if (expand) {
            v.measure(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT)
            val targetHeight = v.measuredHeight
            //v.layoutParams.height = 0
            v.visibility = View.VISIBLE
            val valueAnimator = ValueAnimator.ofInt(targetHeight)
            valueAnimator.addUpdateListener { animation ->
                //    v.layoutParams.height = animation.animatedValue as Int
                v.requestLayout()
            }
            valueAnimator.interpolator = DecelerateInterpolator()
            valueAnimator.duration = 500
            valueAnimator.start()
        } else {
            val initialHeight = v.measuredHeight
            val valueAnimator = ValueAnimator.ofInt(initialHeight, 0)
            valueAnimator.interpolator = DecelerateInterpolator()
            valueAnimator.addUpdateListener { animation ->
                //      v.layoutParams.height = animation.animatedValue as Int
                v.requestLayout()
                if (animation.animatedValue as Int == 0) v.visibility = View.GONE
            }
            valueAnimator.interpolator = DecelerateInterpolator()
            valueAnimator.duration = 500
            valueAnimator.start()
        }
    }
    fun getFormatedDate(date: String, format: String)
    {
        val inputFormat = SimpleDateFormat("yyyy-MM-dd'T'h:mm a")
        val outputFormat = SimpleDateFormat("dd-MM-yyyy hh:mm a")
        val date: Date = inputFormat.parse(date)
        val formattedDate: String = outputFormat.format(date)
        Log.e("formattedDate","is"+formattedDate) // prints 10-04-2018


    }
    fun getFormatedDateLiveLature(date: String, format: String): String {
        val inputFormat = SimpleDateFormat("yyyy-MM-dd'T'h:mm a")
        val outputFormat = SimpleDateFormat("EEE, dd MMMM")
        val date: Date = inputFormat.parse(date)
        val formattedDate: String = outputFormat.format(date)
        Log.e("formattedDate","is"+formattedDate) // prints 10-04-2018
       return formattedDate

    }
    fun loadCircleImageView(view: CircleImageView, imageUrl: Int) {

            Glide.with(view.getContext())
                .load(imageUrl).apply(RequestOptions().circleCrop())
                .error(R.drawable.pented_circle)
                .placeholder(R.drawable.pented_circle)
                .into(view)
    }
    fun getUrlFromS3Details(
        bucketName: String = "pentedapp",
        BucketFolderPath: String,
        FileName: String
    ): URL {
        var s3Client = S3Util.getS3Client()
        var cal = GregorianCalendar.getInstance();
        cal.setTime(Date());
        cal.add(Calendar.DAY_OF_YEAR, +7);
        var daysBeforeDate = cal.getTime();


        val request = GeneratePresignedUrlRequest(bucketName, "$BucketFolderPath$FileName")
        request.expiration = daysBeforeDate
        val objectURL: URL = s3Client.generatePresignedUrl(request)
        //Log.e("Final Data is", "UTILS Here $BucketFolderPath  $FileName")

        return objectURL
    }

    fun loadImage(view: ImageView, url: String)
    {
        Glide.with(view.getContext())
            .load(url).apply(RequestOptions().centerCrop())
            .error(R.drawable.pented_circle)
            .placeholder(R.drawable.pented_circle)
            .into(view)
    }

    fun loadCircleImage(view: CircleImageView, url: String)
    {
        Glide.with(view.getContext())
            .load(url.toString()).apply(RequestOptions().centerCrop())
            .error(R.drawable.pented_circle)
            .placeholder(R.drawable.pented_circle)
            .into(view)
    }
    fun loadCircleImageUser(view: CircleImageView, url: String)
    {
        Glide.with(view.getContext())
            .load(url.toString()).apply(RequestOptions().centerCrop())
            .error(R.drawable.user)
            .placeholder(R.drawable.user)
            .into(view)
    }

    fun isNullorBlank(value: String): Boolean {
        if(value == null || (value.equals("")))
        {
            return true
        }
        else{
            return false
        }
    }

    private lateinit var firebaseAnalytics: FirebaseAnalytics
    fun setScreenViewAnalytics(screenName: String, screenClass: String) {
        firebaseAnalytics = Firebase.analytics
        firebaseAnalytics.logEvent(FirebaseAnalytics.Event.SCREEN_VIEW) {
            param(FirebaseAnalytics.Param.SCREEN_NAME, screenName)
            param(FirebaseAnalytics.Param.SCREEN_CLASS, screenClass)
        }
    }
    fun getNonWindowTouchable(context: Activity) {
        context.window.setFlags(
            WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE,
            WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE
        );
    }
    fun getWindowTouchable(context: Activity) {
        context.window.clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
    }
    fun getImageUri(inContext: Context, inImage: Bitmap): Uri? {
        var uri: Uri? = null
        var fos: OutputStream? = null
        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                val contentValues = ContentValues().apply {
                    put(MediaStore.MediaColumns.DISPLAY_NAME, System.currentTimeMillis().toString())
                    put(MediaStore.MediaColumns.MIME_TYPE, "image/jpeg")
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) { //this one
                        put(MediaStore.MediaColumns.RELATIVE_PATH, Environment.DIRECTORY_PICTURES)
                        put(MediaStore.MediaColumns.IS_PENDING, 1)
                    }
                }
                val contentResolver = inContext.contentResolver

                contentResolver.also { resolver ->
                    uri =
                        resolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, contentValues)
                    fos = uri?.let { resolver.openOutputStream(it) }
                }

                fos?.use { inImage.compress(Bitmap.CompressFormat.JPEG, 70, it) }

                contentValues.clear()
                contentValues.put(MediaStore.Video.Media.IS_PENDING, 0)
                uri?.let { contentResolver.update(it, contentValues, null, null) }

                return uri
            } else {
                val bytes = ByteArrayOutputStream()
                inImage.compress(Bitmap.CompressFormat.JPEG, 100, bytes)
                val path =
                    MediaStore.Images.Media.insertImage(
                        inContext.contentResolver,
                        inImage,
                        "Title",
                        null
                    )
                return Uri.parse(path)
            }
        } catch (e: java.lang.Exception) {
            return uri
            e.printStackTrace()
        }
    }

    fun getRealPathFromURI(uri: Uri?, context: Context): String {
        var path = ""
        try {
            if (context.contentResolver != null) {
                val cursor: Cursor? =
                    uri?.let { context.contentResolver.query(it, null, null, null, null) }
                if (cursor != null) {
                    cursor.moveToFirst()
                    val idx = cursor.getColumnIndex(MediaStore.Images.ImageColumns.DATA)
                    path = cursor.getString(idx)
                    cursor.close()
                }
            }
            return path
        } catch (e: java.lang.Exception) {
            var res: String? = null
            val proj = arrayOf(MediaStore.Images.Media.DATA)
            val cursor: Cursor? =
                uri?.let { context.contentResolver.query(it, proj, null, null, null) }

            if (cursor != null) {
                cursor.moveToFirst()
                res = cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA))
                cursor.close()
            }


            //return res!!
            return res!!
        }

    }

    fun getFileName(uri: Uri?): String? {
        if (uri == null) return null
        var fileName: String? = null
        val path = uri.path
        val cut = path?.lastIndexOf('/')
        if (cut != -1) {
            fileName = cut?.plus(1)?.let { path?.substring(it) }
        }
        return fileName
    }

    fun copy(context: Context, srcUri: Uri?, dstFile: File?) {
        try {
            val inputStream = srcUri?.let { context.contentResolver.openInputStream(it) }
                ?: return
            val outputStream: OutputStream = FileOutputStream(dstFile)
            copystream(inputStream, outputStream)
            inputStream.close()
            outputStream.close()
        } catch (e: IOException) {
            e.printStackTrace()
        } catch (e: java.lang.Exception) {
            e.printStackTrace()
        }
    }
    @Throws(java.lang.Exception::class, IOException::class)
    fun copystream(input: InputStream?, output: OutputStream?): Int {
        val buffer = ByteArray(1024 * 2)
        val `in` = BufferedInputStream(input, 1024 * 2)
        val out = BufferedOutputStream(output, 1024 * 2)
        var count = 0
        var n = 0
        try {
            while (`in`.read(buffer, 0, 1024 * 2).also { n = it } != -1) {
                out.write(buffer, 0, n)
                count += n
            }
            out.flush()
        } finally {
            try {
                out.close()
            } catch (e: IOException) {
                Log.e(e.message, java.lang.String.valueOf(e))
            }
            try {
                `in`.close()
            } catch (e: IOException) {
                Log.e(e.message, java.lang.String.valueOf(e))
            }
        }
        return count
    }
}