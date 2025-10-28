package com.pented.learningapp.adapter

import android.R.attr.button
import android.content.Context
import android.graphics.drawable.Drawable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.pented.learningapp.R
import com.pented.learningapp.model.NotificationsModel


class EarlierNotificationsAdapter(val notificationsModel : ArrayList<NotificationsModel>) : RecyclerView.Adapter<EarlierNotificationsAdapter.MyViewHolder>()  {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val v = LayoutInflater.from(parent.context).inflate(R.layout.row_notifications, parent, false)
        return MyViewHolder(v)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        var model = notificationsModel[position]

        holder.tvNotiTitle.text = model.notiTitle
        holder.tvNotiDescription.text = model.notiDescription
        holder.tvTime.text = model.time
        holder.ivNotiIcon.setImageResource(model.notiIcon)

        if (position == 2){
            val img: Drawable = holder.tvNotiTitle.context.resources.getDrawable(R.drawable.ic_star)
            holder.tvNotiTitle.setCompoundDrawablesWithIntrinsicBounds( img, null, null, null)
            holder.tvNotiTitle.compoundDrawablePadding = 5
        }

    }

    override fun getItemCount(): Int {
        return notificationsModel.size
    }

    class MyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

            val tvNotiTitle = itemView.findViewById(R.id.tvNotiTitle) as TextView
            val tvNotiDescription = itemView.findViewById(R.id.tvNotiDescription) as TextView
            val tvTime = itemView.findViewById(R.id.tvTime) as TextView
            val ivNotiIcon  = itemView.findViewById(R.id.ivNotiIcon) as ImageView
            val llMainNoti  = itemView.findViewById(R.id.llMainNoti) as LinearLayout

    }
}