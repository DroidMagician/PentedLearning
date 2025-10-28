package com.pented.learningapp.activity

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.pented.learningapp.R
import com.pented.learningapp.adapter.EarlierNotificationsAdapter
import com.pented.learningapp.adapter.NewNotificationsAdapter
import com.pented.learningapp.adapter.SubjectAdapter
import com.pented.learningapp.model.NotificationsModel
import com.pented.learningapp.model.SubjectModel
import kotlinx.android.synthetic.main.activity_notification.*

class NotificationActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_notification)

        //getting recyclerview from xml
        val recyclerView = findViewById(R.id.rvNewNotification) as RecyclerView

        //adding a layoutmanager
        recyclerView.layoutManager = LinearLayoutManager(applicationContext)


        //crating an arraylist to store users using the data class user
        val notificationsModel = ArrayList<NotificationsModel>()

        //adding some dummy data to the list
        notificationsModel.add(NotificationsModel(R.drawable.ic_user, "Live classes","Checkout upcoming live class by satendra sir on sunday 4 pm at 26th March 2020","Just now"))
        notificationsModel.add(NotificationsModel(R.drawable.ic_thumb_new, "Doubt is resolved","Your submitted doubt on 26/ 11 about Who all are the main contributors in pythogras theorem and how did upcoming so they gets to know about the main facts","8 hours ago"))

        //creating our adapter
        val adapter = NewNotificationsAdapter(notificationsModel)

        //now adding the adapter to recyclerview
        recyclerView.adapter = adapter



        ivBack.setOnClickListener {
            finish()
        }



        val rvEarlierNotification = findViewById(R.id.rvEarlierNotification) as RecyclerView

        //adding a layoutmanager
        rvEarlierNotification.layoutManager = LinearLayoutManager(applicationContext)


        //crating an arraylist to store users using the data class user
        val earlierNotificationsModel = ArrayList<NotificationsModel>()

        //adding some dummy data to the list
        earlierNotificationsModel.add(NotificationsModel(R.drawable.ic_noti_one, "Weekend test is coming","You have an upcoming weekly test of Maths on Sunday at 4 pm","3 days ago"))
        earlierNotificationsModel.add(NotificationsModel(R.drawable.ic_noti_two, "Daily Important question is live","Daily important questions for maths is solved and updated on the platform","9 days ago"))
        earlierNotificationsModel.add(NotificationsModel(R.drawable.ic_noti_three, "30 % Discount now","We have a 40 % discount on pented subscription for 4 months, Buy now","10 days ago"))

        //creating our adapter
        val adapter2 = EarlierNotificationsAdapter(earlierNotificationsModel)

        //now adding the adapter to recyclerview
        rvEarlierNotification.adapter = adapter2

    }
}