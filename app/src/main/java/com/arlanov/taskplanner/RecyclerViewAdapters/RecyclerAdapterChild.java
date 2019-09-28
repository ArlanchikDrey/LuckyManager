package com.arlanov.taskplanner.RecyclerViewAdapters;

import android.app.Dialog;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.graphics.Paint;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;

import com.arlanov.taskplanner.Database.MySQLiteHelper;
import com.arlanov.taskplanner.Database.TaskContract;
import com.arlanov.taskplanner.R;
import com.arlanov.taskplanner.Utils.Listeners.ListenerUpdateAdaptersPosition;

public class RecyclerAdapterChild extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private Cursor cursor;
    private SQLiteDatabase database;
    private MySQLiteHelper helper;
    private Context context;
    private ListenerUpdateAdaptersPosition updateAdapters;
    private Dialog dialog;


    public RecyclerAdapterChild(Cursor cursor,Context context,ListenerUpdateAdaptersPosition updateAdapters) {
        this.context = context;
        this.cursor = cursor;
        this.updateAdapters = updateAdapters;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {

        LayoutInflater inflater = LayoutInflater.from(viewGroup.getContext());

        View view = inflater.inflate(R.layout.item_child, viewGroup, false);

        return new ViewHolderInChild(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder viewHolder, final int i) {
        final ViewHolderInChild viewHolderInChild = (ViewHolderInChild) viewHolder;
        final RadioButton radioButton = viewHolderInChild.radioButton;
        final TextView textView = viewHolderInChild.textView;

        if (!cursor.moveToPosition((i))) {
            return;
        }

        String itemTask = cursor.getString(
                cursor.getColumnIndexOrThrow(TaskContract.TaskEntry.COLUMN_TASK_TEXT));
        final String itemValue = cursor.getString(
                cursor.getColumnIndexOrThrow(TaskContract.TaskEntry.COLUMN_TASK_VALUE));
        final String id = cursor.getString(
                cursor.getColumnIndexOrThrow(TaskContract.TaskEntry._ID));

        viewHolderInChild.setId(id);
        viewHolderInChild.setCount(getItemCount());

        textView.setText(itemTask);

        if (Boolean.parseBoolean(itemValue)) {
            textView.setPaintFlags(Paint.STRIKE_THRU_TEXT_FLAG);
            textView.setTextColor(Color.GRAY);
        }
        else{
            textView.setPaintFlags(0);
            textView.setTextColor(Color.BLACK);
        }

        radioButton.setChecked(Boolean.parseBoolean(itemValue));
        radioButton.setOnClickListener(v -> {

            if (Boolean.parseBoolean(itemValue))
                updateValueTask(false, id);
            else
                updateValueTask(true, id);
        });


        textView.setOnClickListener(v -> showDialog(textView.getText().toString(),id));
    }

    private void updateValueTask(boolean isValue, String id) {

        helper = new MySQLiteHelper(context);
        database = helper.getWritableDatabase();

        ContentValues updatedValues = new ContentValues();
        updatedValues.put(TaskContract.TaskEntry.COLUMN_TASK_VALUE, String.valueOf(isValue));

        database.update(TaskContract.TaskEntry.TABLE_NAME, updatedValues, "_id = ?",
                new String[]{id});

        database.close();

        if (updateAdapters != null)
            updateAdapters.updateListener(getItemCount());
    }

    private void updateTextTask(String text, String id) {

        helper = new MySQLiteHelper(context);
        database = helper.getWritableDatabase();

        ContentValues updatedValues = new ContentValues();
        updatedValues.put(TaskContract.TaskEntry.COLUMN_TASK_TEXT, text);

        database.update(TaskContract.TaskEntry.TABLE_NAME, updatedValues, "_id = ?",
                new String[]{id});

        database.close();

        if (updateAdapters != null)
            updateAdapters.updateListener(getItemCount());
    }

    private void showDialog(String text,String id){
        dialog=new Dialog(context);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.edit_text_dialog);
        //отображаем фото и обрабатываем нажатия
        EditText im = dialog.findViewById(R.id.editTextTask);
        im.setText(text);

        Button button = dialog.findViewById(R.id.saveTaskText);

        button.setOnClickListener(v -> {
            String str = im.getText().toString();

            if (!str.isEmpty()) {
                updateTextTask(str, id);
                dialog.dismiss();
            }else{
                Toast.makeText(context, "Введите текст задачи", Toast.LENGTH_SHORT).show();
            }
        });

        dialog.show();
    }
    @Override
    public int getItemCount() {
        return cursor.getCount();
    }

}
