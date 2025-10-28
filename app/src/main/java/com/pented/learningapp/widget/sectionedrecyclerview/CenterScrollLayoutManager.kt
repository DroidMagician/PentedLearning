package com.pented.learningapp.widget.sectionedrecyclerview

import android.content.Context
import androidx.recyclerview.widget.LinearLayoutManager
import com.pented.learningapp.MyApplication

class CenterScrollLayoutManager(context: Context, orientation: Int, reverseLayout: Boolean): LinearLayoutManager(context, orientation, reverseLayout) {

    override fun scrollToPosition(position: Int) {

        //this will place the top of the item at the center of the screen
        val height = MyApplication.app.resources.displayMetrics.heightPixels
        val offset = height/2

        //if you know the item height, you can place the center of the item at the center of the screen
        //  by subtracting half the height of that item from the offset:
//        val height = getApplicationContext().resources.displayMetrics.heightPixels
//        //(say item is 40dp tall)
//        val itemHeight = 40F * getApplicationContext().resources.displayMetrics.scaledDensity
//        val offset = height/2 - itemHeight/2

        //depending on if you have a toolbar or other headers above the RecyclerView, 
        //  you may want to subtract their height as well:
//        val height = getApplicationContext().resources.displayMetrics.heightPixels
//        //(say item is 40dp tall):
//        val itemHeight = 40F * getApplicationContext().resources.displayMetrics.scaledDensity
//        //(say toolbar is 56dp tall, which is the default action bar height for portrait mode)
//        val toolbarHeight = 56F * getApplicationContext().resources.displayMetrics.scaledDensity
//        val offset = height/2 - itemHeight/2 - toolbarHeight

        //call scrollToPositionWithOffset with the desired offset
        super.scrollToPositionWithOffset(position, offset)
    }
}