package com.pented.learningapp.homeScreen.practice.adapter

import com.pented.learningapp.helper.model.S3Bucket
import com.pented.learningapp.homeScreen.practice.model.GetQuestionPaperResponseModel
import com.pented.learningapp.widget.sectionedrecyclerview.Section

/**
 * Created by apple on 11/7/16.
 */
class SectionHeader(var childList: ArrayList<GetQuestionPaperResponseModel.QuestionPaper>, var sectionText: Header, var subjectIcon: S3Bucket) : Section<GetQuestionPaperResponseModel.QuestionPaper?> {
    override fun getChildItems(): MutableList<GetQuestionPaperResponseModel.QuestionPaper>? {
        return ArrayList<GetQuestionPaperResponseModel.QuestionPaper>()
    }
}