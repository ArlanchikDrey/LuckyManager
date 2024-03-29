package com.arlanov.taskplanner.Utils;

import android.app.Activity;
import android.view.Window;
import android.view.WindowManager;

public class MyStyles {
    /**
     * стиль для actionBar*/
    public void setTranslucentStatus(Activity activity, boolean on) {
        Window win = activity.getWindow();
        WindowManager.LayoutParams winParams = win.getAttributes();
        final int bits = WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS;
        if (on) {
            winParams.flags |= bits;
        } else {
            winParams.flags &= ~bits;
        }
        win.setAttributes(winParams);
    }
}
