package com.pented.learningapp.homeScreen.subjects.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.pented.learningapp.R
import com.pented.learningapp.widget.sectionedrecyclerview.SectionRecyclerViewAdapter

/**
 * Created by apple on 11/7/16.
 */
class AdapterSubjectsRecycler(var context: Context, sectionHeaderItemList: ArrayList<SubjectsHeader?>?) : SectionRecyclerViewAdapter<SubjectsHeader?, SubjectChildModel?, AdapterSubjectsRecycler.SubjectSectionViewHolder?, AdapterSubjectsRecycler.SubjectChildViewHolder?>(context, sectionHeaderItemList) {
    override fun onCreateSectionViewHolder(sectionViewGroup: ViewGroup, viewType: Int): SubjectSectionViewHolder {
        val view = LayoutInflater.from(context).inflate(R.layout.section_item, sectionViewGroup, false)
        return SubjectSectionViewHolder(view)
    }

    override fun onCreateChildViewHolder(childViewGroup: ViewGroup, viewType: Int): SubjectChildViewHolder {
        val view = LayoutInflater.from(context).inflate(R.layout.row_subjects, childViewGroup, false)
        return SubjectChildViewHolder(view)
    }



    override fun onBindSectionViewHolder(sectionViewHolder: SubjectSectionViewHolder?, sectionPosition: Int, section: SubjectsHeader?) {
        sectionViewHolder?.name?.text = section?.sectionText?.name
        sectionViewHolder?.txtPart?.text = section?.sectionText?.part
    }

    override fun onBindChildViewHolder(childViewHolder: SubjectChildViewHolder?, sectionPosition: Int, childPosition: Int, child: SubjectChildModel?) {
        childViewHolder?.name?.text = child?.name
        childViewHolder?.txtQuestion?.text = child?.question
    }

    class SubjectChildViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        @JvmField
        var name: TextView
        @JvmField
        var txtQuestion: TextView

        init {
            name = itemView.findViewById<View>(R.id.txtName) as TextView
            txtQuestion = itemView.findViewById<View>(R.id.txtQuestion) as TextView
        }
    }

    class SubjectSectionViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var name: TextView
        var txtPart: TextView

        init {
            name = itemView.findViewById<View>(R.id.sectionHeader) as TextView
            txtPart = itemView.findViewById<View>(R.id.txtPart) as TextView
        }
    }
}