package com.pented.learningapp.adapter

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.pented.learningapp.R
import com.pented.learningapp.homeScreen.home.subjectTopic.ChapterWithAnimation2Activity
import com.pented.learningapp.model.TopicsModel


class TopicsAdapter(val topicsModel : ArrayList<TopicsModel>,val context: Context) : RecyclerView.Adapter<TopicsAdapter.MyViewHolder>()  {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val v = LayoutInflater.from(parent.context).inflate(R.layout.row_topics, parent, false)
        return MyViewHolder(v)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        var model = topicsModel[position]

        holder.tvTopicName.text = model.topicName

        if (position == 0){
            holder.ivTopicImg.setOnClickListener {
                //val intent = Intent(context, WatchVideoActivity::class.java)
                val intent = Intent(context, ChapterWithAnimation2Activity::class.java)
                intent.putExtra("chapterName",model.topicName)
                intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
                context.startActivity(intent)
            }

        }

    }

    override fun getItemCount(): Int {
        return topicsModel.size
    }

    class MyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

            val tvTopicName = itemView.findViewById(R.id.tvTopicName) as TextView
            val ivTopicImg  = itemView.findViewById(R.id.ivTopicImg) as ImageView
    }
}