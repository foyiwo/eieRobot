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
    static final String BOOT_COMPLETED = "android.intent.action.BOOT_COMPLETED";
    static final String SCREEN_OFF_ACTION = "android.intent.action.SCREEN_OFF_ACTION";
    static final String TIME_TICK_ACTION = "android.intent.action.TIME_TICK_ACTION";

    @Override
    public void onReceive(Context context, Intent intent) {

        try{

            //mUploadDataUtil.postLogs(intent.getAction());
            if(intent.getAction() != null
                    && (intent.getAction().equals(BOOT_COMPLETED) || intent.getAction().equals(SCREEN_OFF_ACTION))){
                //开机打开屏幕
                mFunction.openPhoneLock();
                if(mGlobal.mNavigationBarActivity == null){
                    mGlobal.mNavigationBarActivity = context;
                }
                mFunction.OpenAppByPackage(mGlobal.AppName);
            }

            if(intent.getAction() != null
                    && intent.getAction().equals(TIME_TICK_ACTION)){
                //开机打开屏幕
                mUploadDataUtil.getIsReBoot();
            }



        }catch (Exception ex){
            mUploadDataUtil.postLogs(intent.getAction()+":"+ex.getMessage());
        }
    }


}
