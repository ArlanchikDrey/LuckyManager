package com.arlanov.taskplanner.RecyclerViewAdapters;


import android.animation.ObjectAnimator;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.util.SparseBooleanArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.arlanov.taskplanner.Database.MySQLiteHelper;
import com.arlanov.taskplanner.Database.TaskContract;
import com.arlanov.taskplanner.R;
import com.arlanov.taskplanner.Utils.Dialog.SingleTaskBottomSheet;
import com.arlanov.taskplanner.Utils.Listeners.ListenerUpdateAdaptersPosition;
import com.github.aakira.expandablelayout.ExpandableLayoutListenerAdapter;
import com.github.aakira.expandablelayout.Utils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class RecyclerAdapterMain extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private List<String> items;
    private SparseBooleanArray sparseBooleanArray = new SparseBooleanArray();
    private Context context;
    private MySQLiteHelper helper;
    private SQLiteDatabase database;
    private RecyclerAdapterChild adapter;
    boolean open = false;

    public RecyclerAdapterMain(List<String> items, Context context) {
        this.context = context;
        this.items = items;
        helper = new MySQLiteHelper(context);
        database = helper.getReadableDatabase();

        for (int i = 0; i < items.size(); i++) {
            sparseBooleanArray.append(i, false);
        }
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {

        LayoutInflater inflater = LayoutInflater.from(viewGroup.getContext());

        View view = inflater.inflate(R.layout.item_main, viewGroup, false);
        return new ViewHolderMain(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder viewHolder, final int i) {

        final ViewHolderMain viewHolderChild = (ViewHolderMain) viewHolder;

        viewHolderChild.setIsRecyclable(false);
        getCalendar(items.get(i), viewHolderChild);

        viewHolderChild.expandableLayout.setInRecyclerView(true);
        viewHolderChild.expandableLayout.setExpanded(sparseBooleanArray.get(i));

        // слушатель на событие открытия expandableLayout
        viewHolderChild.expandableLayout.setListener(new ExpandableLayoutListenerAdapter() {

            @Override
            public void onPreOpen() {
                changeRotate(viewHolderChild.button, 0f, 180f).start();
                sparseBooleanArray.put(i, true);
            }

            @Override
            public void onOpened() {
                open = true;

            }

            @Override
            public void onPreClose() {
                open = false;
                changeRotate(viewHolderChild.button, 180f, 0f).start();
                sparseBooleanArray.put(i, false);
                viewHolderChild.expandableLayout.initLayout();

                viewHolderChild.expandableLayout.toggle(500, input -> 500f);
            }

        });
        viewHolderChild.button.setRotation(sparseBooleanArray.get(i) ? 180f : 0f);
        viewHolderChild.button.setOnClickListener(v -> viewHolderChild.expandableLayout.toggle());
        viewHolderChild.textWeekday.setOnClickListener(v -> viewHolderChild.expandableLayout.toggle());


        createRecyclerViewChild(
                viewHolderChild.recyclerViewChild,
                viewHolderChild.itemView.getContext(),
                viewHolderChild,
                i
        );


    }

    private void getCalendar(String str, ViewHolderMain viewHolderMain) {

        Calendar c = Calendar.getInstance();

        try {
            Date date = new SimpleDateFormat("EEE d MMM").parse(str);
            c.setTime(date);
        } catch (ParseException e) {
            e.printStackTrace();
        }

        Calendar today = Calendar.getInstance();
        today.setTimeInMillis(Calendar.getInstance().getTimeInMillis());
        boolean isDayToday = c.get(Calendar.DAY_OF_MONTH) == today.get(Calendar.DAY_OF_MONTH);
        boolean isMonthToday = c.get(Calendar.MONTH) == today.get(Calendar.MONTH);


        Calendar tomorrow = Calendar.getInstance();
        tomorrow.setTimeInMillis(Calendar.getInstance().getTimeInMillis());
        tomorrow.add(Calendar.DATE, 1);
        boolean isDayTomorrow = c.get(Calendar.DAY_OF_MONTH) == tomorrow.get(Calendar.DAY_OF_MONTH);

        Calendar yesterday = Calendar.getInstance();
        yesterday.setTimeInMillis(Calendar.getInstance().getTimeInMillis());
        yesterday.add(Calendar.DATE, -1);
        boolean isDayYesterday = c.get(Calendar.DAY_OF_MONTH) == yesterday.get(Calendar.DAY_OF_MONTH);

        if (isDayToday && isMonthToday) {
            str = "Сегодня";
            viewHolderMain.textWeekday.setText(getColoredString(str));
        } else if (isDayTomorrow) {
            str = "Завтра";
            viewHolderMain.textWeekday.setText(getColoredString(str));
        } else if (isDayYesterday) {
            str = "Вчера";
            viewHolderMain.textWeekday.setText(getColoredString(str));

        } else {
            viewHolderMain.textWeekday.setText(str.substring(0, 1).toUpperCase() + str.substring(1, 2));
        }

    }


    private ObjectAnimator changeRotate(RelativeLayout relativeLayout, float v, float v1) {
        ObjectAnimator animator = ObjectAnimator.ofFloat(relativeLayout, "rotation", v, v1);
        animator.setDuration(300);
        animator.setInterpolator(Utils.createInterpolator(Utils.LINEAR_INTERPOLATOR));

        return animator;
    }

    private void createRecyclerViewChild(RecyclerView recyclerView,
                                         Context context,
                                         ViewHolderMain viewHolderChild,
                                         final int pos) {

        String text = items.get(pos);


        recyclerView.setLayoutManager(
                new LinearLayoutManager(
                        context,
                        LinearLayoutManager.VERTICAL,
                        false)
        );
        OnItemClicked itemClicked = new OnItemClicked(recyclerView, text, viewHolderChild);
        adapter = new RecyclerAdapterChild(getCursor(text), context, itemClicked);
        recyclerView.setAdapter(adapter);

        int numOfTask = adapter.getItemCount();
        setView(numOfTask, text, viewHolderChild);

        itemClicked.deleteTask(recyclerView, text, viewHolderChild);

        viewHolderChild.btn_addTask.setOnClickListener(v -> {

            if (itemClicked.getCount() >= 13 || numOfTask >= 13)
                Toast.makeText(context, "Извините, вы больше не можете ставить задачи на этот день", Toast.LENGTH_SHORT).show();
            else {
                FragmentManager manager = ((AppCompatActivity) context).getSupportFragmentManager();

                SingleTaskBottomSheet bottomSheet = new SingleTaskBottomSheet();
                Bundle bundle = new Bundle();
                bundle.putString(SingleTaskBottomSheet.TAG_TEXT, text);
                bottomSheet.setArguments(bundle);

                bottomSheet.setUpdateAdapters(() -> {
                    //((MainActivity)context).update();
                    itemClicked.changeAdapter();
                    setView(recyclerView.getAdapter().getItemCount(), text, viewHolderChild);

                    viewHolderChild.expandableLayout.initLayout();

                    viewHolderChild.expandableLayout.toggle(500, input -> 500f);
                });

                bottomSheet.show(manager, "TAGS");
            }
        });
    }

    private void setView(int numOfTask, String text, ViewHolderMain viewHolderChild) {
        viewHolderChild.progressBar.setMax(numOfTask);

        String s = viewHolderChild.textWeekday.getText().toString();
        boolean textDay = s.equals("Вчера") || s.equals("Сегодня") || s.equals("Завтра");

        int numOfprogress = 0;

        if (numOfTask == 0) {
            if (textDay) {
                viewHolderChild.textProgress.setText(text);
            } else {
                viewHolderChild.textProgress.setText(text.substring(2));
            }
            viewHolderChild.textProgress.append("       Задач нет");
            viewHolderChild.textRate.setText("0%");
            viewHolderChild.progressBar.setProgress(0);
        } else {
            numOfprogress = getProgress(getCursor(text));
            viewHolderChild.progressBar.setProgress(numOfprogress);
            if (textDay) {
                viewHolderChild.textProgress.setText(text);
            } else {
                viewHolderChild.textProgress.setText(text.substring(2));
            }
            viewHolderChild.textProgress.append("   Выполнено: ");
            viewHolderChild.textProgress.append(getColoredString((numOfprogress + "/" + numOfTask)));

            int res = (int) Math.round((double) numOfprogress / numOfTask * 100);
            viewHolderChild.textRate.setText(res + "%");

        }

        viewHolderChild.numOfTask.setText("Задач " + (numOfTask - numOfprogress));
    }

    private Cursor getCursor(String text) {

        Cursor cursor = database.query(
                TaskContract.TaskEntry.TABLE_NAME,
                null,
                "time = ?",
                new String[]{text}
                , null, null, "value");

        return cursor;
    }


    private Spannable getColoredString(CharSequence text) {
        Spannable spannable = new SpannableString(text);
        spannable.setSpan(new ForegroundColorSpan(Color.BLACK), 0, spannable.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        return spannable;
    }

    private int getProgress(Cursor cursor) {
        int num = 0;

        while (cursor.moveToNext()) {
            if (cursor.getString(
                    cursor.getColumnIndexOrThrow(TaskContract.TaskEntry.COLUMN_TASK_VALUE)).equals("true")) {
                num++;
            }
        }

        cursor.close();

        return num;

    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    private class OnItemClicked implements ListenerUpdateAdaptersPosition {
        private RecyclerView recyclerView;
        private String text;
        private ViewHolderMain viewHolder;
        private int count;

        public OnItemClicked(RecyclerView recyclerView, String text, ViewHolderMain viewHolder) {
            this.recyclerView = recyclerView;
            this.text = text;
            this.viewHolder = viewHolder;
        }

        private void deleteTask(RecyclerView recyclerView,
                                String text,
                                ViewHolderMain viewHolderChild) {
            new ItemTouchHelper(new ItemTouchHelper.SimpleCallback(0,
                    ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT) {
                @Override
                public boolean onMove(@NonNull RecyclerView recyclerView,
                                      @NonNull RecyclerView.ViewHolder viewHolder,
                                      @NonNull RecyclerView.ViewHolder viewHolder1) {
                    return false;
                }

                @Override
                public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int i) {
                    final ViewHolderInChild view = (ViewHolderInChild) viewHolder;

                    MySQLiteHelper helper = new MySQLiteHelper(context);
                    SQLiteDatabase data = helper.getWritableDatabase();


                    data.delete(TaskContract.TaskEntry.TABLE_NAME, "_id = ?",
                            new String[]{view.getId()});

                    data.close();

                    changeAdapter();

                    setView(view.getCount() - 1, text, viewHolderChild);

                }
            }).attachToRecyclerView(recyclerView);
        }


        @Override
        public void updateListener(int count) {

            changeAdapter();

            setView(count, text, viewHolder);
        }

        public void changeAdapter() {
            adapter = null;
            adapter = new RecyclerAdapterChild(getCursor(text), context, this);
            recyclerView.setAdapter(adapter);
            viewHolder.expandableLayout.initLayout();
            viewHolder.expandableLayout.toggle(500, input -> 500f);

            setCount(adapter.getItemCount());

        }

        public int getCount() {
            return count;
        }

        public void setCount(int count) {
            this.count = count;
        }
    }
}

