package com.pented.learningapp.homeScreen.practice.adapter

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import com.bumptech.glide.Glide
import com.pented.learningapp.R
import com.pented.learningapp.helper.Utils
import com.pented.learningapp.homeScreen.practice.activity.QuestionPaperActivity
import com.pented.learningapp.homeScreen.practice.model.GetQuestionPaperResponseModel
import com.pented.learningapp.widget.sectionedrecyclerview.SectionRecyclerViewAdapter


/**
 * Created by apple on 11/7/16.
 */
class AdapterSectionRecycler(var context: Context, sectionHeaderItemList: List<SectionHeader?>?) : SectionRecyclerViewAdapter<SectionHeader?, GetQuestionPaperResponseModel.QuestionPaper?, SectionViewHolder?, ChildViewHolder?>(
    context,
    sectionHeaderItemList
) {
    override fun onCreateSectionViewHolder(sectionViewGroup: ViewGroup, viewType: Int): SectionViewHolder {
        val view = LayoutInflater.from(context).inflate(
            R.layout.section_item,
            sectionViewGroup,
            false
        )
        return SectionViewHolder(view)
    }

    override fun onCreateChildViewHolder(childViewGroup: ViewGroup, viewType: Int): ChildViewHolder {
        val view = LayoutInflater.from(context).inflate(R.layout.row_tests, childViewGroup, false)
        return ChildViewHolder(view)
    }



    override fun onBindSectionViewHolder(
        sectionViewHolder: SectionViewHolder?,
        sectionPosition: Int,
        section: SectionHeader?
    ) {
        sectionViewHolder?.name?.text = section?.sectionText?.name
        sectionViewHolder?.txtPart?.text = "${section?.sectionText?.points} Points"
        sectionViewHolder?.totalQuestions?.text = "${section?.sectionText?.totalQuestionPapers} QuestionPapers"
        var finalURl = Utils.getUrlFromS3Details(BucketFolderPath = section?.subjectIcon?.BucketFolderPath ?: "",FileName = section?.subjectIcon?.FileName?: "")
        Log.e("Final URL is", "UTILS Here $finalURl")
        sectionViewHolder?.icSubjectIcon?.let {
            Glide.with(context)
                .load(finalURl.toString())
                .error(R.drawable.pented_circle)
                .placeholder(R.drawable.pented_circle)
                .into(it)
        }

        sectionViewHolder?.mainSubjectLayout?.setOnClickListener {
            var intent = Intent(context, QuestionPaperActivity::class.java)
            val extras = Bundle()
            extras.putString("subjectID", section?.sectionText?.subjectId)
            extras.putString("subjectName", section?.sectionText?.name)
            intent.putExtras(extras)
            //intent.putExtra("subjectID", section?.sectionText?.subjectId)
            context.startActivity(intent)
        }
    }

    override fun onBindChildViewHolder(
        childViewHolder: ChildViewHolder?,
        sectionPosition: Int,
        childPosition: Int,
        child: GetQuestionPaperResponseModel.QuestionPaper?
    ) {
        childViewHolder?.name?.text = child?.Title
        childViewHolder?.txtQuestion?.text = "${child?.TotalQuestions} Questions"
        childViewHolder?.txtDuration?.text = "${child?.DurationInMinutes} Min."
        childViewHolder?.mainLayout?.setOnClickListener {
            var intent = Intent(context, QuestionPaperActivity::class.java)
            val extras = Bundle()
            extras.putString("subjectID", child?.SubjectId.toString())
            extras.putString("subjectName", child?.SubjectTitle)
            intent.putExtras(extras)
            context.startActivity(intent)
        }
    }
}