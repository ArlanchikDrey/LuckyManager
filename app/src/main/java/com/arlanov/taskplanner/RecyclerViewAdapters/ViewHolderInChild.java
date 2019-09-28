package com.arlanov.taskplanner.RecyclerViewAdapters;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.RadioButton;
import android.widget.TextView;

import com.arlanov.taskplanner.R;

public class ViewHolderInChild extends RecyclerView.ViewHolder {

    TextView textView;
    RadioButton radioButton;


    String id;
    int count;

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public ViewHolderInChild(@NonNull View itemView) {
        super(itemView);
        textView = itemView.findViewById(R.id.textTask);
        radioButton = itemView.findViewById(R.id.buttonCheck);

    }
}
