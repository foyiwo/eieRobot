package eie.robot.com.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.widget.Toast;

import com.vondear.rxtool.view.RxToast;

import eie.robot.com.accessibilityservice.AccessibilityHelper;
import eie.robot.com.accessibilityservice.mAccessibilityService;
import eie.robot.com.common.mAdbShell;
import eie.robot.com.common.mFunction;
import eie.robot.com.common.mGlobal;
import eie.robot.com.common.mUploadDataUtil;

public class BootBroadcastReceiver extends BroadcastReceiver {
    static final String ACTION = "android.intent.action.ACTION_BOOT_COMPLETED";
    //static final String ACTION = "android.intent.action.ACTION_DATE_CHANGED";

    @Override
    public void onReceive(Context context, Intent intent) {
        try{
            mUploadDataUtil.postLogs(intent.getAction());
            //开机打开屏幕
            mFunction.openPhoneLock();

            mGlobal.mNavigationBarActivity = context;
            mFunction.OpenAppByPackage(mGlobal.AppName);

        }catch (Exception ex){
            mUploadDataUtil.postLogs(intent.getAction()+":"+ex.getMessage());
        }
    }


}
