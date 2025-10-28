package com.pented.learningapp.adapter

import android.content.Context
import android.content.Intent
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView
import com.airbnb.lottie.LottieAnimationView
import com.pented.learningapp.R
import com.pented.learningapp.helper.Constants
import com.pented.learningapp.homeScreen.home.model.TopicVideoResponseModel
import com.pented.learningapp.homeScreen.home.watchVideo.WatchVideoActivity
import com.pented.learningapp.model.PlanetsModel


class PlanetAdapterNew(val planetsModel: ArrayList<TopicVideoResponseModel.Video>, val context: Context) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    private val TYPE_LEFT = 1
    private val TYPE_RIGHT = 2
    companion object{
        var Selectedposition = 0
    }
    lateinit var v:View
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {

        when(viewType)
        {
            TYPE_LEFT -> {
                v = LayoutInflater.from(parent.context).inflate(
                    R.layout.row_planet_chapter_left,
                    parent,
                    false
                )
                return ViewHolderLeft(v)
            }
            TYPE_RIGHT -> {
                v = LayoutInflater.from(parent.context).inflate(
                    R.layout.row_planet_chapter_right,
                    parent,
                    false
                )
                return ViewHolderRight(v)
            }
            else -> {
                return  ViewHolderLeft(v)
            }
        }
    }



    override fun getItemViewType(position: Int): Int {

        if (position == 0) {
          return  TYPE_RIGHT
        } else {
            if (position % 2 == 0) {
                return  TYPE_RIGHT
            } else {
                return  TYPE_LEFT
            }
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        var model = planetsModel[position]
        var  itemType = getItemViewType(position);
        if (itemType == TYPE_LEFT) {
            val viewHolderLeft: ViewHolderLeft = holder as ViewHolderLeft
            viewHolderLeft.tvTopicNameLeft.text = model.TopicVideoShortTitle
            viewHolderLeft.txtChapterNameLeft.text = model.TopicVideoTitle
            viewHolderLeft.txtChapterDescriptionLeft.text = model.TopicVideoDescription

            viewHolderLeft.ivPlanetLeft.setImageResource(model.planet_img)
            if(model.isRocketVisible)
            {
                viewHolderLeft.animationViewLeft.visibility = View.VISIBLE
                viewHolderLeft.lilStartNowLeft.visibility = View.VISIBLE
            }
            else{
                viewHolderLeft.animationViewLeft.visibility = View.GONE
                viewHolderLeft.lilStartNowLeft.visibility = View.GONE
            }
            if (position == planetsModel.lastIndex) {
                Log.e("Postion is","Here left"+position)
            }
            viewHolderLeft.txtStartNowLeft.setOnClickListener {
                context.sendBroadcast(Intent("StartNowTopicVideo"))
            }
            if(viewHolderLeft.animationViewLeft.visibility == View.VISIBLE)
            {
                viewHolderLeft.animationViewLeft.setOnClickListener {
                    context.sendBroadcast(Intent("StartNowTopicVideo"))
                }
            }
//            if (position == planetsModel.lastIndex){
//                Log.e("Postion is","Here left"+position)
//                val params = holder.linearLeft.layoutParams as LinearLayout.LayoutParams
//                params.topMargin = 170
//                holder.itemView.layoutParams = params
//            }


            viewHolderLeft.llPlanetLeft.setOnClickListener {
                val originalPos = IntArray(2)
//                for(planet in planetsModel)
//                {
//                    planet.isRocketVisible = false
//                }
                //model.isRocketVisible = true
               // notifyDataSetChanged()

                //context.startActivity(Intent(context,WatchVideoActivity::class.java))
                Selectedposition = position
                  //context.sendBroadcast(Intent("DoAnimation"))
                var location =  viewHolderLeft.llPlanetLeft.getLocationInWindow(originalPos)
                val x = originalPos[0]
                val y = originalPos[1]
                Log.e("Final Location", "Is ${x} y is $y")
            }

        }
        else if (itemType == TYPE_RIGHT) {
            val viewHolderright: ViewHolderRight = holder as ViewHolderRight
            viewHolderright.tvTopicNameRight.text = model.TopicVideoShortTitle
            viewHolderright.txtChapterNameRight.text = model.TopicVideoTitle
            viewHolderright.txtChapterDescriptionRight.text = model.TopicVideoDescription
            viewHolderright.ivPlanetRight.setImageResource(model.planet_img)
            if(model.isRocketVisible)
            {
                viewHolderright.animationViewRight.visibility = View.VISIBLE
                viewHolderright.lilStartNowRight.visibility = View.VISIBLE
            }
            else{
                viewHolderright.animationViewRight.visibility = View.GONE
                viewHolderright.lilStartNowRight.visibility = View.GONE
            }
            if (position == planetsModel.lastIndex) {
                Log.e("Postion is","Here right"+position)
            }
            if(viewHolderright.animationViewRight.visibility == View.VISIBLE)
            {
                viewHolderright.animationViewRight.setOnClickListener {
                    context.sendBroadcast(Intent("StartNowTopicVideo"))
                }
            }
//            if (position == planetsModel.lastIndex){
//                Log.e("Postion is","Here right"+position)
//                val params = holder.linearRight.layoutParams as LinearLayout.LayoutParams
//                params.topMargin = 170
//                holder.itemView.layoutParams = params
//            }

            viewHolderright.txtStartNowRight.setOnClickListener {
                context.sendBroadcast(Intent("StartNowTopicVideo"))
            }

            viewHolderright.llPlanetRight.setOnClickListener {
                val originalPos = IntArray(2)
//                for(planet in planetsModel)
//                {
//                    planet.isRocketVisible = false
//                }
//                model.isRocketVisible = true
//                notifyDataSetChanged()

                Selectedposition = position
                  //context.sendBroadcast(Intent("DoAnimation"))
                var location =  viewHolderright.llPlanetRight.getLocationInWindow(originalPos)
                val x = originalPos[0]
                val y = originalPos[1]
                Log.e("Final Location", "Is ${x} y is $y")
            }

        }

    }

    override fun getItemCount(): Int {
        return planetsModel.size
    }
    class ViewHolderLeft(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val llPlanetLeft = itemView.findViewById(R.id.llPlanetLeft) as LinearLayout
        val linearLeft = itemView.findViewById(R.id.linearLeft) as LinearLayout
        val lilStartNowLeft = itemView.findViewById(R.id.lilStartNowLeft) as CardView
        val animationViewLeft = itemView.findViewById(R.id.animationViewLeft) as ImageView
        val tvTopicNameLeft = itemView.findViewById(R.id.tvTopicNameLeft) as TextView
        val txtChapterNameLeft = itemView.findViewById(R.id.txtChapterNameLeft) as TextView
        val txtChapterDescriptionLeft = itemView.findViewById(R.id.txtChapterDescriptionLeft) as TextView
        val txtStartNowLeft = itemView.findViewById(R.id.txtStartNowLeft) as TextView
        val ivPlanetLeft = itemView.findViewById(R.id.ivPlanetLeft) as ImageView
    }
    class ViewHolderRight(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val llPlanetRight = itemView.findViewById(R.id.llPlanetRight) as LinearLayout
        val linearRight = itemView.findViewById(R.id.linearRight) as LinearLayout
        val lilStartNowRight = itemView.findViewById(R.id.lilStartNowRight) as CardView
        val animationViewRight = itemView.findViewById(R.id.animationViewRight) as ImageView
        val tvTopicNameRight = itemView.findViewById(R.id.tvTopicNameRight) as TextView
        val txtChapterNameRight = itemView.findViewById(R.id.txtChapterNameRight) as TextView
        val txtChapterDescriptionRight = itemView.findViewById(R.id.txtChapterDescriptionRight) as TextView
        val txtStartNowRight = itemView.findViewById(R.id.txtStartNowRight) as TextView
        val ivPlanetRight = itemView.findViewById(R.id.ivPlanetRight) as ImageView
    }

}