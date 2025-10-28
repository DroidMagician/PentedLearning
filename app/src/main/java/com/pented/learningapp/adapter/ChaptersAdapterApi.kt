package com.pented.learningapp.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.pented.learningapp.R
import com.pented.learningapp.homeScreen.home.model.Chapter
import com.pented.learningapp.model.ChaptersModel
import com.pented.learningapp.model.TopicsModel


class ChaptersAdapterApi(val chaptersModel : ArrayList<Chapter>, val context: Context) : RecyclerView.Adapter<ChaptersAdapterApi.MyViewHolder>()  {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val v = LayoutInflater.from(parent.context).inflate(R.layout.row_chapters, parent, false)
        return MyViewHolder(v)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        var model = chaptersModel[position]

        holder.tvChapterName.text = model?.Name

        holder.rvTopics.layoutManager = LinearLayoutManager(context,LinearLayoutManager.HORIZONTAL,false)

        val adapter = model?.Topics?.let { TopicsAdapterApi(it,context) }

        holder.rvTopics.adapter = adapter

    }

    override fun getItemCount(): Int {
        return chaptersModel.size
    }

    class MyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

            val tvChapterName = itemView.findViewById(R.id.tvChapterName) as TextView
            val rvTopics  = itemView.findViewById(R.id.rvTopics) as RecyclerView
    }
}