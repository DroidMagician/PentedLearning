package com.pented.learningapp.adapter

import android.content.Context
import android.content.Intent
import android.content.Intent.FLAG_ACTIVITY_NEW_TASK
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.pented.learningapp.R
import com.pented.learningapp.homeScreen.home.SubjectActivity
import com.pented.learningapp.model.SubjectModel

class SubjectAdapter(val subjectModels : ArrayList<SubjectModel>,val context: Context) : RecyclerView.Adapter<SubjectAdapter.MyViewHolder>()  {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val v = LayoutInflater.from(parent.context).inflate(R.layout.row_home_subjects, parent, false)
        return MyViewHolder(v)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        var model = subjectModels[position]

        holder.tvSubjectName.text = model.subjectName
        holder.ivSubjectIcon.setImageResource(model.subjectIcon)

        if (position==0){
            holder.rlProgress.setBackgroundResource(R.drawable.ic_red_circle_progress)
            holder.ivLockThumb.visibility = View.GONE

            holder.rlProgress.setOnClickListener {
                val intent = Intent(context, SubjectActivity::class.java)
                intent.flags = FLAG_ACTIVITY_NEW_TASK
                context.startActivity(intent)
            }

        }else if (position == 2){
            holder.rlProgress.setBackgroundResource(R.drawable.ic_orange_circle_progress)
            holder.ivLockThumb.visibility = View.GONE
        }else if (position == 3){
            holder.rlProgress.setBackgroundResource(R.drawable.ic_green_circle_completed_subject)
            holder.ivLockThumb.setImageResource(R.drawable.ic_thumb_new)
        }
    }

    override fun getItemCount(): Int {
        return subjectModels.size
    }

    class MyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {


            val tvSubjectName = itemView.findViewById(R.id.tvSubjectName) as TextView
            val ivSubjectIcon  = itemView.findViewById(R.id.ivSubjectIcon) as ImageView
            val rlProgress  = itemView.findViewById(R.id.rlProgress) as RelativeLayout
            val ivLockThumb  = itemView.findViewById(R.id.ivLockThumb) as ImageView

    }
}