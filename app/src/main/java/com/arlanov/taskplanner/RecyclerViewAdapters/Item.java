package com.arlanov.taskplanner.RecyclerViewAdapters;

import android.support.v7.widget.LinearLayoutManager;
import android.widget.LinearLayout;

public class Item {
    private String textWeekday, subText;
    private boolean isEx;

    public Item(String textWeekday, String subText, boolean isEx) {
        this.textWeekday = textWeekday;
        this.subText = subText;

        this.isEx = isEx;
    }

    public String getTextWeekday() {
        return textWeekday;
    }


    public String getSubText() {
        return subText;
    }



}
