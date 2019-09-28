package com.arlanov.taskplanner.Database;

import android.provider.BaseColumns;

public class TaskContract {

    private TaskContract(){}

    public static final class TaskEntry implements BaseColumns {
        public static final String TABLE_NAME = "tableTask";
        public static final String COLUMN_TIME = "time";
        public static final String COLUMN_TASK_TEXT = "taskText";
        public static final String COLUMN_TASK_VALUE = "value";
    }
}
