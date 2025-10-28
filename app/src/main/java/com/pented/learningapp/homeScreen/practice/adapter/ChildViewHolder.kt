package com.pented.learningapp.homeScreen.practice.adapter

import android.view.View
import android.widget.LinearLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.pented.learningapp.R

/**
 * Created by apple on 11/7/16.
 */
class ChildViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
    @JvmField
    var name: TextView
    @JvmField
    var txtQuestion: TextView
    @JvmField
    var txtDuration: TextView
    @JvmField
    var mainLayout: LinearLayout

    init {
        name = itemView.findViewById<View>(R.id.txtName) as TextView
        txtQuestion = itemView.findViewById<View>(R.id.txtQuestion) as TextView
        txtDuration = itemView.findViewById<View>(R.id.txtDuration) as TextView
        mainLayout = itemView.findViewById<View>(R.id.mainLayout) as LinearLayout
    }
}