package com.pented.learningapp.adapter

import android.content.Context
import android.util.Log
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.pented.learningapp.R
import com.pented.learningapp.model.PlanetsModel


class PlanetAdapter(val planetsModel: ArrayList<PlanetsModel>, val context: Context) :
    RecyclerView.Adapter<PlanetAdapter.MyViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val v = LayoutInflater.from(parent.context).inflate(
            R.layout.row_planet_chapter,
            parent,
            false
        )
        return MyViewHolder(v)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        var model = planetsModel[position]

        holder.tvTopicName.text = model.topicName
        holder.ivPlanet.setImageResource(model.planet_img)

        if (position == 0){
            holder.llPlanet.gravity = Gravity.RIGHT or Gravity.CENTER
        }else{
            if (position % 2 == 0) {
                holder.llPlanet.gravity = Gravity.RIGHT
            } else {
                holder.llPlanet.gravity = Gravity.LEFT
            }
        }

        holder.llPlanet.setOnClickListener {
            val originalPos = IntArray(2)
           var location =  holder.llPlanet.getLocationInWindow(originalPos)
            val x = originalPos[0]
            val y = originalPos[1]
            Log.e("Final Location", "Is ${x} y is $y")
        }

    }

    override fun getItemCount(): Int {
        return planetsModel.size
    }

    class MyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        val llPlanet = itemView.findViewById(R.id.llPlanet) as LinearLayout
        val tvTopicName = itemView.findViewById(R.id.tvTopicName) as TextView
        val ivPlanet = itemView.findViewById(R.id.ivPlanet) as ImageView
    }
}