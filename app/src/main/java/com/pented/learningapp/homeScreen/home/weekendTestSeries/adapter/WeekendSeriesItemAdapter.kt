package com.pented.learningapp.homeScreen.home.weekendTestSeries.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.pented.learningapp.R
import com.pented.learningapp.homeScreen.home.weekendTestSeries.model.WeekendSeriesChildModel

class WeekendSeriesItemAdapter(private val items : List<WeekendSeriesChildModel>) : RecyclerView.Adapter<WeekendSeriesItemAdapter.ViewHolder>() {

    class ViewHolder(private val view : View) : RecyclerView.ViewHolder(view) {
        lateinit var tvItemName: TextView
        lateinit  var txtPart: TextView
        init {
             tvItemName= view.findViewById(R.id.txtName) as TextView
            txtPart = view.findViewById(R.id.txtPart) as TextView
//             saperater = view.findViewById(R.id.saperater) as View

        }
        companion object {
            fun create(parent: ViewGroup) : ViewHolder {
                val view = LayoutInflater.from(parent.context).inflate(R.layout.row_weekend_test_series, parent, false)
                return ViewHolder(
                    view
                )
            }
        }

        fun bind(itemName : WeekendSeriesChildModel) {
            val tvItemName= view.findViewById(R.id.txtName) as TextView
            val txtPart = view.findViewById(R.id.txtPart) as TextView

            tvItemName.text = itemName.name
            txtPart.text = itemName.question
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder.create(
            parent
        )
    }
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.tvItemName.text =items[position].name
        holder.txtPart.text =items[position].question


    }

    override fun getItemCount() = items.size

    class SubjectSectionViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

    }
}