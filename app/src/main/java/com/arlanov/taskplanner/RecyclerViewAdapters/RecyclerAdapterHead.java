package com.arlanov.taskplanner.RecyclerViewAdapters;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.arlanov.taskplanner.R;
import com.arlanov.taskplanner.Utils.Listeners.ListenerPositionToday;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;

public class RecyclerAdapterHead extends RecyclerView.Adapter<RecyclerAdapterHead.ViewHolder> {

    private HashMap<Integer, List<String>> listHashMap = new LinkedHashMap<>();
    private Context context;
    private ListenerPositionToday listenerPositionToday;

    public RecyclerAdapterHead(Context context,
                               ArrayList<String> lists,
                               String dateToday,
                               ListenerPositionToday positionToday) {
        this.context = context;
        this.listenerPositionToday = positionToday;

        int x = 0;

        for (int k = 0; k < 52; k++) {
            if (k == 0)
                x = k;
            else
                x = x+7;
            List<String> list =  lists.subList(x,x+7);
            if (list.contains(dateToday))
                listenerPositionToday.position(k);
            listHashMap.put(k, list);
        }


    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        LayoutInflater inflater = LayoutInflater.from(context);

        View view = inflater.inflate(R.layout.item_main_recyclerview, viewGroup, false);

        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder viewHolder, int i) {
        viewHolder.recyclerView.setHasFixedSize(true);
        viewHolder.recyclerView.setLayoutManager(new LinearLayoutManager(context));
        viewHolder.recyclerView.setAdapter(new RecyclerAdapterMain(listHashMap.get(i),context));
    }

    @Override
    public int getItemCount() {
        return listHashMap.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        RecyclerView recyclerView;

        public ViewHolder(View itemView) {
            super(itemView);
            recyclerView = itemView.findViewById(R.id.recyclerViewMain);
        }
    }
}
