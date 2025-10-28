package com.pented.learningapp.homeScreen.subjects.model

import com.pented.learningapp.homeScreen.subjects.adapter.SubjectChildModel
import com.pented.learningapp.homeScreen.subjects.adapter.SubjectHeaderModel

data class SubjectRecyclerViewSection(val label : SubjectHeaderModel, val items : List<SubjectChildModel>)