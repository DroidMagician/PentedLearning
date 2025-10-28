package com.pented.learningapp.helper;

import com.pented.learningapp.widget.chipview.Chip;

public class Tag implements Chip {
    private String subjectName;
    private String time;
    private String teacherName;
    private int mType = 0;

    public Tag(String subjectName,String time,String teacherName, int type) {
        this(subjectName,time,teacherName);
        mType = type;
    }

    public Tag(String name,String mtime,String mteacherName) {
        subjectName = name;
        time = mtime;
        teacherName = mteacherName;
    }

    public int getType() {
        return mType;
    }

    @Override
    public String getTime() {
        return time;
    }

    @Override
    public String getSubject() {
        return subjectName;
    }

    @Override
    public String getTeacherName() {
        return teacherName;
    }
}
