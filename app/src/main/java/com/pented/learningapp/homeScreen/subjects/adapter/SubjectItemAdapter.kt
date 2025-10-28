package com.pented.learningapp.homeScreen.subjects.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.pented.learningapp.R

class SubjectItemAdapter(private val items : List<SubjectChildModel>) : RecyclerView.Adapter<SubjectItemAdapter.ViewHolder>() {

    class ViewHolder(private val view : View) : RecyclerView.ViewHolder(view) {
        lateinit var tvItemName: TextView
        lateinit  var txtQuestion: TextView
        lateinit  var saperater: View

        init {
             tvItemName= view.findViewById(R.id.txtName) as TextView
             txtQuestion = view.findViewById(R.id.txtQuestion) as TextView
             saperater = view.findViewById(R.id.saperater) as View

        }
        companion object {
            fun create(parent: ViewGroup) : ViewHolder {
                val view = LayoutInflater.from(parent.context).inflate(R.layout.row_subjects, parent, false)
                return ViewHolder(
                    view
                )
            }
        }

        fun bind(itemName : SubjectChildModel) {
            val tvItemName= view.findViewById(R.id.txtName) as TextView
            val txtQuestion = view.findViewById(R.id.txtQuestion) as TextView
            val saperater = view.findViewById(R.id.saperater) as View

            tvItemName.text = itemName.name
            txtQuestion.text = itemName.question
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder.create(
            parent
        )
    }
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.tvItemName.text =items[position].name
        holder.txtQuestion.text =items[position].question
        if(items.size-1 == position)
        {
            holder.saperater.visibility = View.GONE
        }


    }

    override fun getItemCount() = items.size

    class SubjectSectionViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

    }
}