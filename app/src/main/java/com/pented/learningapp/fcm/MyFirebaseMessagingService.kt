package com.pented.learningapp.fcm

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Intent
import android.media.RingtoneManager
import android.os.Build
import android.os.Bundle
import android.util.Log
import androidx.core.app.NotificationCompat
import androidx.work.OneTimeWorkRequest
import androidx.work.WorkManager
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import com.google.gson.Gson
import com.pented.learningapp.R
import com.pented.learningapp.authScreens.GetStartedActivity
import com.pented.learningapp.enum.notificationTypes
import com.pented.learningapp.helper.Constants
import com.pented.learningapp.helper.SharedPrefs
import org.json.JSONObject

class MyFirebaseMessagingService : FirebaseMessagingService() {
    lateinit var dataObject: JSONObject
    var notiData = FCMNotificationData()
    //var notiData = FCMNotificationData()
    /**
     * Called when message is received.
     *
     * @param remoteMessage Object representing the message received from Firebase Cloud Messaging.
     */
    // [START receive_message]
    lateinit var type: String

    override fun onMessageReceived(remoteMessage: RemoteMessage) {
        Log.e(TAG, "From: ${remoteMessage.from}")
        Log.e("Remote Message is", "Notification ${Gson().toJson(remoteMessage)}")
        Log.e(TAG, "Remote Message data is: " + remoteMessage.data.toString())
        Log.e(TAG, "Remote Message Type: " + remoteMessage.data["notificationType"])

        val from = remoteMessage.from
        val map = remoteMessage.data

        if (map.contains("response")) {
            dataObject = JSONObject(map.get("response"))
        }

   //     Log.e("Noti", "Count ${SharedPrefs.getCount(MyApplication.getInstance())}")
    //    sendBroadcast(Intent("notificationReceived"))
    //    sendBroadcast(Intent("homeBadgeCount"))
        // Check if message contains a data payload.
        remoteMessage.data.isNotEmpty().let {
         //   Log.e(TAG, "Message data payload: " + remoteMessage.data.toString())
          //  Log.e(TAG, "Message is ${remoteMessage.data}")
            if (/* Check if data needs to be processed by long running job */ true) {
                // For long-running tasks (10 seconds or more) use WorkManager.
                scheduleJob()
            } else {
                // Handle message within 10 seconds
                handleNow()
            }
        }
        var notiType = notificationTypes.General.notificationType

        remoteMessage.data.get("notificationType")?.let {
            notiData.request_status = it
            Constants.notificationType = it
        }
//        if(remoteMessage.data.containsKey("notificationType"))
//        {
//            Constants.notificationType = remoteMessage.data["notificationType"] ?: "500"
//        }
        Log.e("notificationType==1","=========${Constants.notificationType}")
//        remoteMessage.data.get("type")?.let { it1 ->
//           // notiData.request_status = it1
//
//            if(it1.equals("Admin_Notification"))
//            {
//                sendNotification(dataObject.getString("notificationMessage")!!, dataObject.getString("description")!!)
//                return
//            }
//        }
        remoteMessage.data?.let {
       //     Log.e(TAG, "Message Notification Body: ${remoteMessage.notification?.body}")
            remoteMessage.data.get("notificationType")?.let { it1 ->
                notiData.type = it1
                Constants.notificationType = it1
            }
            Log.e("notificationType==10","=========${Constants.notificationType}")
            remoteMessage.data["notificationType"]?.let {
                sendNotification(remoteMessage.notification?.title ?: "",remoteMessage.notification?.body ?: "",it)
            }

        }

        // Check if message contains a notification payload.
//        remoteMessage.data?.let {
//            Log.e(TAG, "Message Notification Body: ${remoteMessage.notification?.body}")
//
//            remoteMessage.data.get("type")?.let { it1 ->
//                notiData.type = it1
//                if (it1.equals("Accepted") || (it1.equals("Rejected"))
//                    || (it1.equals("ReturnAccepted")) || (it1.equals("ReturnRejected"))
//                ) {
//                    //refresh rentals
//                    sendBroadcast(Intent("RefreshRentals"))
//                    dataObject.getInt("senderId")?.let { notiData.receiver_id = it }
//                    dataObject.getInt("receiverId")?.let { notiData.sender_id = it }
//                } else if(it1.equals(Constants.CANCELLED,true)|| (it1.equals(Constants.PRODUCT_REVIEW))||
//                    it1.equals(Constants.RENTED,true)|| (it1.equals("Requested"))||
//                    it1.equals(Constants.RETURN_REQUESTED,true)){
//                    //refresh items
//                    sendBroadcast(Intent("RefreshItems"))
//                    dataObject.getInt("senderId")?.let { notiData.sender_id = it }
//                    dataObject.getInt("receiverId")?.let { notiData.receiver_id = it }
//                }
////                else if(it1.equals("Chat"))
////                {
////
////                }
//                notiData.request_status = it1
//            }
//            if(dataObject.has("productId"))
//            {
//                dataObject.getInt("productId")?.let { notiData.product_id = it }
//            }
//
//
//            if (dataObject.has("id")) {
//                dataObject.getInt("id").let { notiData.request_id = it }
//            }
//            remoteMessage.data.get("body")?.let { it1 ->
//                Log.e("Type is", "Value ${notiData.type}")
//                if (notiData.type.equals("Chat")) {
//                    sendBroadcast(Intent("chat_items_clear"))
//                    sendBroadcast(Intent("chat_rental_clear"))
//                    sendBroadcast(Intent("chat_search_clear"))
//                    dataObject.getString("dateTime")?.let { notiData.dateTime = it }
//                    dataObject.getString("messageType")?.let { notiData.messageType = it }
//                    dataObject.getString("message")?.let { notiData.message = it }
//                    dataObject.getString("userName")?.let { notiData.userName = it }
//                    dataObject.getString("productName")?.let { notiData.productName = it }
//                    //Check here java.lang.Error: org.json.JSONException: No value for profilePicture
//                    if(dataObject.has("profilePicture"))
//                    {
//                        dataObject.getString("profilePicture")?.let { notiData.profilePicture = it }
//                    }
//                    else
//                    {
//                        notiData.profilePicture = ""
//                    }
//                    dataObject.getString("receiverId")?.let { notiData.receiver_id = it.toInt() }
//                    dataObject.getString("productId")?.let { notiData.product_id = it.toInt() }
//                    dataObject.getString("senderId")?.let { notiData.sender_id = it.toInt() }
//
//                    sendNotification(
//                        it1,
//                        if (notiData?.message.toString().contains("http")) "Image" else notiData?.message.toString()
//                    )
//
//                    var intent = Intent()
//                    intent.setAction(Constants.CHAT_MESSAGE_RECEIVED)
//                    val gson = Gson()
//                    val myJson = gson.toJson(notiData)
//                    intent.putExtra("ChatData", myJson)
//                    sendBroadcast(intent)
//                }
//                else if(notiData.type.equals("PickUpReminder") ||notiData.type.equals("DropOffReminder") )
//                {
////                    SharedPrefs.setCount(
////                        MyApplication.getInstance(),
////                        SharedPrefs.getCount(MyApplication.getInstance()) + 1
////                    )
//                    sendNotification(it1, remoteMessage.data.get("description")!!)
//                }
//                else {
//                    // increment noti count
//                    SharedPrefs.setCount(
//                        MyApplication.getInstance(),
//                        SharedPrefs.getCount(MyApplication.getInstance()) + 1
//                    )
//                    sendNotification(it1, remoteMessage.data.get("description")!!)
//                }
//            }
////            remoteMessage.data.get("description")?.let { it1 ->
////                sendNotification(it1)
////            }
//        }

        // Also if you intend on generating your own notifications as a result of a received FCM
        // message, here is where that should be initiated. See sendNotification method below.
    }
    // [END receive_message]

    // [START on_new_token]
    /**
     * Called if InstanceID token is updated. This may occur if the security of
     * the previous token had been compromised. Note that this is called when the InstanceID token
     * is initially generated so this is where you would retrieve the token.
     */
    override fun onNewToken(token: String) {
        Log.d(TAG, "Refreshed token: $token")

        // If you want to send messages to this application instance or
        // manage this apps subscriptions on the server side, send the
        // Instance ID token to your app server.
        SharedPrefs.storeFcmToken(baseContext, token)
        sendRegistrationToServer(token)
    }
    // [END on_new_token]

    /**
     * Schedule async work using WorkManager.
     */
    private fun scheduleJob() {
        // [START dispatch_job]
        val work = OneTimeWorkRequest.Builder(MyWorker::class.java).build()
        WorkManager.getInstance().beginWith(work).enqueue()
        // [END dispatch_job]
    }

    /**
     * Handle time allotted to BroadcastReceivers.
     */
    private fun handleNow() {
        Log.d(TAG, "Short lived task is done.")
    }

    /**
     * Persist token to third-party servers.
     *
     * Modify this method to associate the user's FCM InstanceID token with any server-side account
     * maintained by your application.
     *
     * @param token The new token.
     */
    private fun sendRegistrationToServer(token: String?) {

    }

    /**
     * Create and show a simple notification containing the received FCM message.
     *
     * @param messageBody FCM message body received.
     */
    private fun sendNotification(messageBody: String, get: String, s: String) {
        val intent = Intent(this, GetStartedActivity::class.java)

        val extras = Bundle()
        val gson = Gson()
        val myJson = gson.toJson(notiData)
       // Log.e("Object Set", "is $notiData")
        extras.putString("Type",  notiData.request_status)
        extras.putString("myJson", myJson)
//        if (notiData.request_status.equals(Constants.PRODUCT_REVIEW)) {
//            extras.putString("myNotificationJson", myJson)
//        } else {
//            extras.putString("myJson", myJson)
//        }

        intent.putExtras(extras)
        intent.action = Intent.ACTION_MAIN
        intent.addCategory(Intent.CATEGORY_LAUNCHER)
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP)
       // Constants.Noti_Type = "Rental"
        val pendingIntent = PendingIntent.getActivity(
            this, 0 /* Request code */, intent,
            PendingIntent.FLAG_UPDATE_CURRENT or  PendingIntent.FLAG_IMMUTABLE,

        )

        // receiverId == userid then display own item detail else display renter item details
        val channelId = getString(R.string.default_notification_channel_id)
        val defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)
        val notificationBuilder = NotificationCompat.Builder(this, channelId)
            .setSmallIcon(R.drawable.ic_notification_icon_main)
            .setContentTitle(messageBody)
            .setStyle(NotificationCompat.BigTextStyle().bigText(get))
            .setPriority(2) // max priority
            .setContentText(get)
            .setAutoCancel(true)
            .setSound(defaultSoundUri)
            .setContentIntent(pendingIntent)

        val notificationManager =
            getSystemService(NOTIFICATION_SERVICE) as NotificationManager

        // Since android Oreo notification channel is needed.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                channelId,
                "Channel human readable title",
                NotificationManager.IMPORTANCE_DEFAULT
            )
            notificationManager.createNotificationChannel(channel)
        }

        notificationManager.notify(
            notificationId++ /* ID of notification */,
            notificationBuilder.build()
        )
    }

    companion object {
        @JvmStatic
        var notificationId = 0
        private const val TAG = "MyFirebaseMsgService"
    }
}
