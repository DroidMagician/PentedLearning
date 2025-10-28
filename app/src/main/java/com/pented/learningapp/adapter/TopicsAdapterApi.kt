package com.pented.learningapp.adapter

import android.content.Context
import android.content.Intent
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.google.gson.Gson
import com.pented.learningapp.R
import com.pented.learningapp.homeScreen.home.subjectTopic.ChapterWithAnimation2Activity
import com.pented.learningapp.helper.Constants
import com.pented.learningapp.helper.Utils
import com.pented.learningapp.homeScreen.home.model.Topic
import com.pented.learningapp.homeScreen.home.subjectTopic.TopicVideoListActivity


class TopicsAdapterApi(val topicsModel : ArrayList<Topic>, val context: Context) : RecyclerView.Adapter<TopicsAdapterApi.MyViewHolder>()  {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val v = LayoutInflater.from(parent.context).inflate(R.layout.row_topics, parent, false)
        return MyViewHolder(v)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        var model = topicsModel[position]

        holder.tvTopicName.text = model.Name
        var finalURl = Utils.getUrlFromS3Details(BucketFolderPath = model.S3Bucket?.BucketFolderPath ?: "",FileName = model.S3Bucket?.FileName?: "")
       // Log.e("Final URL is", "UTILS Here $finalURl")
        Glide.with(context)
            .load(finalURl.toString())
            .error(R.drawable.pented_circle)
            .placeholder(R.drawable.pented_circle)
            .into(holder.ivTopicImg)
        holder. ivTopicImg.setOnClickListener {
            Log.e("Selected Chapter","Is${Gson().toJson(topicsModel)}")
            Constants.selectedTopicsList = topicsModel
            //val intent = Intent(context, WatchVideoActivity::class.java)
//            val intent = Intent(context, ChapterWithAnimation2Activity::class.java)
           // startActivity(TopicVideoListActivity::class.java)
            val intent = Intent(context, TopicVideoListActivity::class.java)
            intent.putExtra("topicID",topicsModel[position].Id)
            intent.putExtra("chapterName",model.Name)
            Log.e("Inside Adapter","Chapter Name ${model.Name}")
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
            context.startActivity(intent)
        }

        if(model.IsCompleted == true)
        {
            holder.ivLockThumb.visibility = View.VISIBLE
        }
        else
        {
            holder.ivLockThumb.visibility = View.GONE
        }
//        if (position == 0){
//            Constants.selectedTopicsList.clear()
//
//            holder.ivTopicImg.setOnClickListener {
//
//            }
//
//        }

    }

    override fun getItemCount(): Int {
        return topicsModel.size
    }

    class MyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

            val tvTopicName = itemView.findViewById(R.id.tvTopicName) as TextView
            val ivTopicImg  = itemView.findViewById(R.id.ivTopicImg) as ImageView
            val ivLockThumb  = itemView.findViewById(R.id.ivLockThumb) as ImageView
    }
}