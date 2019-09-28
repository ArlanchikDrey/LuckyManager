package com.arlanov.taskplanner.Utils.Dialog;

import android.app.Activity;
import android.app.Dialog;
import android.content.ContentValues;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.BottomSheetDialog;
import android.support.design.widget.BottomSheetDialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.Toast;

import com.arlanov.taskplanner.Database.MySQLiteHelper;
import com.arlanov.taskplanner.Database.TaskContract;
import com.arlanov.taskplanner.R;
import com.arlanov.taskplanner.Utils.Listeners.ListenerUpdateAdapters;

public class SingleTaskBottomSheet extends BottomSheetDialogFragment {

    private View mView;
    private String textDate, textTask;
    public static String TAG_TEXT;
    private ListenerUpdateAdapters updateAdapters;



    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        assert getArguments() != null;
        textDate = getArguments().getString(TAG_TEXT);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        mView = inflater.inflate(R.layout.single_bottom_sheet_dialog, container, false);
        return mView;
    }

    @Override
    public void onStart() {
        super.onStart();
        final View view = getView();

        if (view != null)
            initViews(view);
    }

    private void initViews(final View view) {
        final EditText editText = view.findViewById(R.id.editTextWhatTODo);
        View viewButtonSend = view.findViewById(R.id.viewSendButtonTask);


        viewButtonSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                textTask = editText.getText().toString();
                if (textTask.length() > 0) {
                    new MyAsynckTask().execute();
                    dismiss();
                }
                else
                    Toast.makeText(getActivity(), "Введите текст задачи", Toast.LENGTH_SHORT).show();
            }
        });
    }

    public void setUpdateAdapters(ListenerUpdateAdapters updateAdapters) {
        this.updateAdapters = updateAdapters;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        Dialog dialog = new BottomSheetDialog(requireContext(),R.style.DialogStyle);
        Activity activity = getActivity();

        if (activity != null) {
            dialog.setOwnerActivity(activity);
            dialog.getWindow().setDimAmount(0);
        }

        return dialog;
    }

    private class MyAsynckTask extends AsyncTask<Void,Void,Boolean>{
        ContentValues contentValues;

        @Override
        protected void onPreExecute() {
            contentValues = new ContentValues();

            contentValues.put(TaskContract.TaskEntry.COLUMN_TIME, textDate);
            contentValues.put(TaskContract.TaskEntry.COLUMN_TASK_TEXT,textTask);
            contentValues.put(TaskContract.TaskEntry.COLUMN_TASK_VALUE,"false");
        }

        @Override
        protected Boolean doInBackground(Void... voids) {
            MySQLiteHelper helper = new
                    MySQLiteHelper(getActivity());
            try {
                SQLiteDatabase database = helper.getWritableDatabase();
                database.insert(TaskContract.TaskEntry.TABLE_NAME, null, contentValues);
                database.close();

                return true;
            }catch (SQLiteException e){
                return false;
            }
        }

        @Override
        protected void onPostExecute(Boolean aBoolean) {
            if (!aBoolean)
                Toast.makeText(getActivity(), "Ошибка", Toast.LENGTH_SHORT).show();
            else{
                updateAdapters.updateListener();
            }
        }
    }
}
