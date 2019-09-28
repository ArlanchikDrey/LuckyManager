package com.arlanov.taskplanner.RecyclerViewAdapters;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.arlanov.taskplanner.R;
import com.github.aakira.expandablelayout.ExpandableLinearLayout;

public class ViewHolderMain extends RecyclerView.ViewHolder {

    TextView textWeekday, numOfTask, textProgress, textRate;
    RelativeLayout button;
    ExpandableLinearLayout expandableLayout;
    RecyclerView recyclerViewChild;
    ProgressBar progressBar;
    View btn_addTask;

    public ViewHolderMain(@NonNull View itemView) {
        super(itemView);
        numOfTask = itemView.findViewById(R.id.numOfTask);
        textWeekday = itemView.findViewById(R.id.weekday);
        textProgress = itemView.findViewById(R.id.textProgress);
        textRate = itemView.findViewById(R.id.textRate);
        button = itemView.findViewById(R.id.buttonsItemChild);
        expandableLayout = itemView.findViewById(R.id.expandablelayout);
        recyclerViewChild = itemView.findViewById(R.id.recyclerViewChild);
        progressBar = itemView.findViewById(R.id.progressBar);
        btn_addTask = itemView.findViewById(R.id.newAddTaskInDay);
    }
}
