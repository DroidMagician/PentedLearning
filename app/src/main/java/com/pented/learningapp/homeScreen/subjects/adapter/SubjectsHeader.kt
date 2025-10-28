package com.pented.learningapp.homeScreen.subjects.adapter

import com.pented.learningapp.widget.sectionedrecyclerview.Section

/**
 * Created by apple on 11/7/16.
 */
class SubjectsHeader(var childList: ArrayList<SubjectChildModel>, var sectionText: SubjectHeaderModel) : Section<SubjectChildModel?> {
    override fun getChildItems(): ArrayList<SubjectChildModel> {
        return childList
    }
}