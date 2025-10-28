package com.pented.learningapp.helper;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.pented.learningapp.R;
import com.pented.learningapp.widget.chipview.ChipViewAdapter;

/**
 * Created by Plumillon Forge on 09/10/15.
 */
public class MainChipViewAdapter extends ChipViewAdapter {
    public MainChipViewAdapter(Context context) {
        super(context);
    }

    @Override
    public int getLayoutRes(int position) {
        Tag tag = (Tag) getChip(position);

        switch (tag.getType()) {
            default:
            case 2:
            case 4:
                return 0;

            case 1:
                return R.layout.row_chip;
            case 5:
                return 0;
                //return R.layout.chip_double_close;

            case 3:
                return 0;
                //return R.layout.chip_close;
        }
    }

    @Override
    public int getBackgroundColor(int position) {
        Tag tag = (Tag) getChip(position);

//        switch (tag.getType()) {
//            default:
//                return 0;
//
//            case 1:
//            case 4:
//                return getColor(R.color.primary);
//
//            case 2:
//            case 5:
//                return getColor(R.color.primary);
//
//            case 3:
//                return getColor(R.color.primary);
//        }
        return getColor(R.color.white_60);
    }

    @Override
    public int getBackgroundColorSelected(int position) {
        return 0;
    }

    @Override
    public int getBackgroundRes(int position) {
        return 0;
    }

    @Override
    public void onLayout(View view, int position) {
        Tag tag = (Tag) getChip(position);

        if (tag.getType() == 1)
        {
            Log.e("Values","Are 1= "+tag.getSubject()+" "+tag.getTime()+" "+tag.getTeacherName());
            if(tag.getSubject() != null && (!tag.getSubject().equals("")))
            {
                ((TextView) view.findViewById(R.id.txtSubjectName)).setVisibility(View.VISIBLE);
                ((TextView) view.findViewById(R.id.txtSubjectName)).setText(tag.getSubject());
            }
            else
            {
                ((TextView) view.findViewById(R.id.txtSubjectName)).setVisibility(View.GONE);
            }

            if(tag.getTime() != null && (!tag.getTime().equals("")))
            {
                ((TextView) view.findViewById(R.id.txtTime)).setVisibility(View.VISIBLE);
                ((TextView) view.findViewById(R.id.txtTime)).setText(tag.getTime());
            }
            else
            {
                ((TextView) view.findViewById(R.id.txtTime)).setVisibility(View.GONE);
            }

            if(tag.getTeacherName() != null && (!tag.getTeacherName().equals("")))
            {
                ((TextView) view.findViewById(R.id.txtTeacherName)).setVisibility(View.VISIBLE);
                ((TextView) view.findViewById(R.id.txtTeacherName)).setText(tag.getTeacherName());
            }
            else
            {
                ((TextView) view.findViewById(R.id.txtTeacherName)).setVisibility(View.GONE);
            }

//            ((TextView) view.findViewById(R.id.txtTime)).setText(tag.getTime());
//            ((TextView) view.findViewById(R.id.txtTeacherName)).setText(tag.getTeacherName());
        }


    }
}
