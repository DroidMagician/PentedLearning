package com.pented.learningapp.homeScreen.subjects.adapter

import android.content.Context
import android.content.Intent
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.pented.learningapp.R
import com.pented.learningapp.helper.Utils
import com.pented.learningapp.homeScreen.home.SubjectActivity
import com.pented.learningapp.homeScreen.subjects.model.SubjectListResponseModel
import com.pented.learningapp.homeScreen.subjects.model.SubjectRecyclerViewSection

class SubjectContainerAdapterAPI(private val context: Context, private val sections : ArrayList<SubjectListResponseModel.Subject>) : RecyclerView.Adapter<SubjectContainerAdapterAPI.ViewHolder>() {

    class ViewHolder(private val view : View) : RecyclerView.ViewHolder(view) {

        companion object {
            fun create(parent: ViewGroup) : ViewHolder {
                val view = LayoutInflater.from(parent.context).inflate(R.layout.subject_item_content_container, parent, false)
                return ViewHolder(
                    view
                )
            }
        }

        fun bind(context : Context, section : SubjectListResponseModel.Subject) {
            val tvName = view.findViewById(R.id.sectionHeader) as TextView
            val mainLayout = view.findViewById(R.id.mainLayout) as LinearLayout
            val txtPoints = view.findViewById(R.id.txtPoints) as TextView
            val txtChapters = view.findViewById(R.id.txtChapters) as TextView
            val txtMore = view.findViewById(R.id.txtMore) as TextView
            val recyclerView = view.findViewById(R.id.recyclerView) as RecyclerView
            val subjectIcon = view.findViewById(R.id.subjectIcon) as ImageView

            var finalURl = Utils.getUrlFromS3Details(BucketFolderPath = section.S3Bucket?.BucketFolderPath ?: "",FileName = section.S3Bucket?.FileName?: "")
            Log.e("Final URL is", "UTILS Here $finalURl")
            Glide.with(context)
                .load(finalURl.toString())
                .error(R.drawable.pented_circle)
                .placeholder(R.drawable.pented_circle)
                .into(subjectIcon)
            recyclerView.setOnClickListener {
                var intent = Intent(context,SubjectActivity::class.java)
                intent.putExtra("subjectId",section?.Id)
                context.startActivity(intent)
            }
            txtMore.setOnClickListener {
                var intent = Intent(context,SubjectActivity::class.java)
                intent.putExtra("subjectId",section?.Id)
                context.startActivity(intent)
            }
            mainLayout.setOnClickListener {
                var intent = Intent(context,SubjectActivity::class.java)
                intent.putExtra("subjectId",section?.Id)
                context.startActivity(intent)
//                section?.Id?.let {
//                    startActivityWithData(
//                        SubjectActivity::class.java,
//                        it
//                    )
//                }
            }

            tvName.text = section.Name
            txtPoints.text = "${section.EarnedPoints?.toInt()}/${section.TotalPoints?.toInt()} Watch Time"
//            recyclerView.setHasFixedSize(true)
            recyclerView.isNestedScrollingEnabled = false
            txtChapters.text = "${section.ViewedChapters} chapters / ${section.TotalChapters} chapters"
            Log.e("Chapter Size","Is "+section.Chapters?.size)
            if(section.Chapters?.size ?: 0 > 3)
            {
                var moreChapters = section?.Chapters?.size ?: 0 - 3
                txtMore.text = "+ ${section.TotalChapters?.minus(3)} more"
                txtMore.visibility = View.VISIBLE
            }
            else
            {
                txtMore.visibility = View.GONE
            }

            val layoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
            recyclerView.layoutManager = layoutManager

            for (chapter in section.Chapters)
            {
                chapter.SubjectId = section?.Id
            }
            val adapter =
                section.Chapters?.let {
                    SubjectItemAdapterAPI(
                        it
                    )
                }
            recyclerView.adapter = adapter

        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder.create(
            parent
        )
    }
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val section = sections[position]
        holder.bind(context, section)
    }

    override fun getItemCount() = sections.size


}