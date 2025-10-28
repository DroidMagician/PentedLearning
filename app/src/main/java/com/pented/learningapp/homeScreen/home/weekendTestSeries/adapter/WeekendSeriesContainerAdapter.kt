package com.pented.learningapp.homeScreen.home.weekendTestSeries.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.pented.learningapp.R
import com.pented.learningapp.homeScreen.home.weekendTestSeries.model.WeekendSeriesRecyclerViewSection

class WeekendSeriesContainerAdapter(private val context: Context, private val sections : ArrayList<WeekendSeriesRecyclerViewSection>) : RecyclerView.Adapter<WeekendSeriesContainerAdapter.ViewHolder>() {

    class ViewHolder(private val view : View) : RecyclerView.ViewHolder(view) {

        companion object {
            fun create(parent: ViewGroup) : ViewHolder {
                val view = LayoutInflater.from(parent.context).inflate(R.layout.weekend_test_series_item_content_container, parent, false)
                return ViewHolder(
                    view
                )
            }
        }

        fun bind(context: Context, section: WeekendSeriesRecyclerViewSection) {
            val tvName = view.findViewById(R.id.sectionHeader) as TextView
            val txtPoints = view.findViewById(R.id.txtPoints) as TextView
            //val txtChapters = view.findViewById(R.id.txtChapters) as TextView
            val recyclerView = view.findViewById(R.id.recyclerView) as RecyclerView

            tvName.text = section.label.name
            txtPoints.text = section.label.part
//            recyclerView.setHasFixedSize(true)
            recyclerView.isNestedScrollingEnabled = false

            val layoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
            recyclerView.layoutManager = layoutManager

            val adapter =
                WeekendSeriesItemAdapter(
                    section.items
                )
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