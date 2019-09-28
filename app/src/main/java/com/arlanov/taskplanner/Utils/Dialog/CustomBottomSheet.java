package com.arlanov.taskplanner.Utils.Dialog;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.ContentValues;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
import android.support.design.widget.BottomSheetDialog;
import android.support.design.widget.BottomSheetDialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Toast;

import com.arlanov.taskplanner.Database.MySQLiteHelper;
import com.arlanov.taskplanner.Database.TaskContract;
import com.arlanov.taskplanner.R;
import com.arlanov.taskplanner.Utils.Listeners.ListenerUpdateAdapters;

import java.util.Calendar;


public class CustomBottomSheet extends BottomSheetDialogFragment
        implements View.OnClickListener {

    private View layout, butSend;
    private Button butToday, butTommorow, butWeek;
    private EditText editText;
    private int choiseDay; // 1 - today, 2 - tomorrow, 3 - choiseUser
    private boolean isClicked = false;
    private String time = "";
    String textInput ;
    private ListenerUpdateAdapters listenerUpdateAdapters;

    public void setListenerUpdateAdapters(ListenerUpdateAdapters listenerUpdateAdapters) {
        this.listenerUpdateAdapters = listenerUpdateAdapters;
    }

    @Override
    public void onStart() {
        super.onStart();
        final View view = getView();

        if (view != null){
            initViews(view);
        }
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container,
                             Bundle savedInstanceState) {
        layout = inflater.inflate(R.layout.bottom_sheet_dialog,container,false);
        return layout;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        Dialog dialog = new BottomSheetDialog(requireContext(),R.style.DialogStyle);
        Activity activity = getActivity();

        if (activity != null){
            dialog.setOwnerActivity(activity);
            dialog.getWindow().setDimAmount(0);
        }

        return dialog;
    }

    private void initViews(View view){
        butToday = view.findViewById(R.id.butToday);
        butTommorow = view.findViewById(R.id.butTommorow);
        butWeek = view.findViewById(R.id.butWeek);
        butSend = view.findViewById(R.id.viewSendButton);
        editText = view.findViewById(R.id.editTextWhatDo);

        butToday.setOnClickListener(this);
        butTommorow.setOnClickListener(this);
        butWeek.setOnClickListener(this);
        butSend.setOnClickListener(this);

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.butToday:
                onClickToday();
                break;
            case R.id.butTommorow:
                onClickTomorrow();
                break;
            case R.id.butWeek:
                onClickWeek();

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                    createDialog();
                }

                break;
            case R.id.viewSendButton:
                sendQuery();
                listenerUpdateAdapters.updateListener();
                break;
        }
    }

    private void onClickToday(){
        if (isClicked == false){
            choiseDay = 1;
            butToday.setBackground(getResources().getDrawable(R.drawable.button_style_isclicked));
            butTommorow.setBackground(getResources().getDrawable(R.drawable.button_style));
            butWeek.setBackground(getResources().getDrawable(R.drawable.button_style));
            time = getTimeToday();
            isClicked = true;
        }else{
            isClicked = false;
            time = "";
            choiseDay = 0;
            butToday.setBackground(getResources().getDrawable(R.drawable.button_style));

        }

    }

    private void onClickTomorrow(){
        if (isClicked == false){
            choiseDay = 2;
            butTommorow.setBackground(getResources().getDrawable(R.drawable.button_style_isclicked));
            butWeek.setBackground(getResources().getDrawable(R.drawable.button_style));
            butToday.setBackground(getResources().getDrawable(R.drawable.button_style));
            time = getTimePlusDay();
            isClicked = true;
        }else{
            isClicked = false;
            time = "";
            choiseDay = 0;
            butTommorow.setBackground(getResources().getDrawable(R.drawable.button_style));

        }
    }

    private String getTimePlusDay(){
        Calendar c = Calendar.getInstance();
        c.setTimeInMillis(Calendar.getInstance().getTimeInMillis());
        c.set(Calendar.YEAR, c.get(Calendar.YEAR));
        c.set(Calendar.MONTH, c.get(Calendar.MONTH));
        c.set(Calendar.DAY_OF_MONTH, c.get(Calendar.DAY_OF_MONTH) + 1);

        return String.format("%1$ta %1$te %1$tb", c);
    }

    private String getTimeToday(){
        Calendar c = Calendar.getInstance();
        c.setTimeInMillis(System.currentTimeMillis());

        return String.format("%1$ta %1$te %1$tb", c);
    }

    private void onClickWeek(){
        if (isClicked == false){
            choiseDay = 3;
            butWeek.setBackground(getResources().getDrawable(R.drawable.button_style_isclicked));
            butToday.setBackground(getResources().getDrawable(R.drawable.button_style));
            butTommorow.setBackground(getResources().getDrawable(R.drawable.button_style));
            time = "";
            isClicked = true;
        }else{
            isClicked = false;
            time = "";
            choiseDay = 0;
            butWeek.setBackground(getResources().getDrawable(R.drawable.button_style));
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    private void createDialog(){
        DatePickerDialog datePickerDialog =
                new DatePickerDialog(getContext()) ;
        datePickerDialog.show();
        datePickerDialog.setOnDateSetListener(new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                Calendar c = Calendar.getInstance();
                c.set(Calendar.YEAR, year);
                c.set(Calendar.MONTH, month);
                c.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                time = String.format("%1$ta %1$te %1$tb", c);

            }
        });
    }

    private void sendQuery(){
        textInput = editText.getText().toString().trim();
        MyAsyncTaskSET taskSET = new MyAsyncTaskSET();

        if (choiseDay == 1 && !textInput.equals("")){
            taskSET.execute();
            dismiss();
        }
        else if (choiseDay == 2 && !textInput.equals("")){
            taskSET.execute();
            dismiss();
        }
        else if (choiseDay == 3 && !textInput.equals("")){
            taskSET.execute();
            dismiss();
        }
        else if (choiseDay == 0 || textInput.equals("")){
            Toast.makeText(getActivity(), "Должен быть выбран день и введен текст задачи" , Toast.LENGTH_SHORT).show();
        }
    }

    private class MyAsyncTaskSET extends AsyncTask<Void,Void,Boolean>{

        ContentValues contentValues;


        @Override
        protected void onPreExecute() {
            contentValues = new ContentValues();

            contentValues.put(TaskContract.TaskEntry.COLUMN_TIME, time);
            contentValues.put(TaskContract.TaskEntry.COLUMN_TASK_TEXT,textInput);
            contentValues.put(TaskContract.TaskEntry.COLUMN_TASK_VALUE,"false");

        }

        @Override
        protected Boolean doInBackground(Void... voids) {
            MySQLiteHelper helper = new
                    MySQLiteHelper(getActivity());

            try{
                SQLiteDatabase database = helper.getWritableDatabase();
                database.insert(TaskContract.TaskEntry.TABLE_NAME,
                        null,
                        contentValues);
                database.close();
                return true;
            }catch (SQLiteException e){
                return false;
            }

        }

        @Override
        protected void onPostExecute(Boolean aBoolean) {
            if (!aBoolean){
                Toast.makeText(getActivity(), "Error", Toast.LENGTH_SHORT).show();
            }
        }
    }

}



