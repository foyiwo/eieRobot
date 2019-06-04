package eie.robot.com.common;

import android.widget.Toast;

import com.vondear.rxtool.view.RxToast;

public class mToast {

    public static void message(String message){
        Toast toast = Toast.makeText(mGlobal.mApplication,null,Toast.LENGTH_SHORT);
        toast.setText(message);
        toast.show();
    }
    public static void success(String message){
        RxToast.success(mGlobal.mAccessibilityService, message, Toast.LENGTH_SHORT, true).show();

    }
    public static void success_sleep(String message){
        RxToast.success(mGlobal.mAccessibilityService, message, Toast.LENGTH_SHORT, true).show();
        mFunction.click_sleep();
    }
    public static void info(String message){
        RxToast.info(mGlobal.mNavigationBarActivity, message, Toast.LENGTH_SHORT, true).show();
    }
    public static void error(String message){
        RxToast.error(mGlobal.mNavigationBarActivity, message, Toast.LENGTH_SHORT, true).show();
    }

}
