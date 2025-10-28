package com.pented.learningapp.homeScreen.practice.adapter

import android.media.Image
import android.view.View
import android.widget.ImageView
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.pented.learningapp.R

/**
 * Created by shanky on 11/12/2016.
 */
class SectionViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
    var name: TextView
    var txtPart: TextView
    var totalQuestions: TextView
    var icSubjectIcon: ImageView
    var mainSubjectLayout: RelativeLayout

    init {
        name = itemView.findViewById<View>(R.id.sectionHeader) as TextView
        txtPart = itemView.findViewById<View>(R.id.txtPart) as TextView
        totalQuestions = itemView.findViewById<View>(R.id.totalQuestions) as TextView
        icSubjectIcon = itemView.findViewById<ImageView>(R.id.icSubjectIcon) as ImageView
        mainSubjectLayout = itemView.findViewById<RelativeLayout>(R.id.mainSubjectLayout) as RelativeLayout
    }
}