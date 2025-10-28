package com.pented.learningapp.homeScreen.subjects.adapter

import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.pented.learningapp.R
import com.pented.learningapp.homeScreen.home.SubjectActivity
import com.pented.learningapp.homeScreen.subjects.model.SubjectListResponseModel
import com.skydoves.progressview.ProgressView
import kotlinx.android.synthetic.main.fragment_home.*

class SubjectItemAdapterAPI(private val items: List<SubjectListResponseModel.Chapter>) : RecyclerView.Adapter<SubjectItemAdapterAPI.ViewHolder>() {
    private val limit = 3
    class ViewHolder(private val view: View) : RecyclerView.ViewHolder(view) {
        lateinit var tvItemName: TextView
        lateinit  var txtDescription: TextView
        lateinit  var txtPoints: TextView
        lateinit  var progressView1: ProgressView
        lateinit  var saperater: View
        lateinit  var mainSubject: View

        init {
             tvItemName= view.findViewById(R.id.txtName) as TextView
            txtDescription = view.findViewById(R.id.txtDescription) as TextView
            txtPoints = view.findViewById(R.id.txtPoints) as TextView
            progressView1 = view.findViewById(R.id.progressView1) as ProgressView
             saperater = view.findViewById(R.id.saperater) as View
             mainSubject = view.findViewById(R.id.mainSubject) as View

        }
        companion object {
            fun create(parent: ViewGroup) : ViewHolder {
                val view = LayoutInflater.from(parent.context).inflate(
                    R.layout.row_subjects,
                    parent,
                    false
                )
                return ViewHolder(
                    view
                )
            }
        }

//        fun bind(itemName : SubjectChildModel) {
//            val tvItemName= view.findViewById(R.id.txtName) as TextView
//            val txtDescription = view.findViewById(R.id.txtDescription) as TextView
//            val saperater = view.findViewById(R.id.saperater) as View
//
//            tvItemName.text = itemName.name
//            txtDescription.text = itemName.question
//        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder.create(
            parent
        )
    }
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.tvItemName.text =items[position].Name
        var remainingPoints = items[position].TotalPoints?.toInt() ?: 0 - items[position]?.EarnedPoints!!
        ?: 0
        holder.txtPoints.text = "${items[position].EarnedPoints?.toInt()} / ${items[position].TotalPoints?.toInt()} Minutes"
        holder.progressView1.max = items[position].TotalPoints?.toFloat() ?: 0.0f
        items[position].EarnedPoints?.toFloat().let {
            if (it != null) {
                holder.progressView1.progress = it
            }
        }
        holder.mainSubject.setOnClickListener {
            var intent = Intent(holder.mainSubject.context, SubjectActivity::class.java)
            intent.putExtra("subjectId",items[position]?.SubjectId)
            holder.mainSubject.context.startActivity(intent)
        }
      //  holder.txtDescription.text =items[position].question
        if(items.size-1 == position)
        {
            holder.saperater.visibility = View.GONE
        }


    }

    override fun getItemCount(): Int {
        if(items.size > limit){
            return limit;
        }
        else
        {
            return items.size
        }
    }

    class SubjectSectionViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

    }
}