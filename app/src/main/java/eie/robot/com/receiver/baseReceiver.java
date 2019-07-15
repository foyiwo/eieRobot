package eie.robot.com.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;

import eie.robot.com.common.mGlobal;
import eie.robot.com.common.mUploadDataUtil;

public class baseReceiver {

    //重启手机的广播
    static final String ACTION_REBOOT = "android.intent.action.ACTION_REBOOT";
    //广播接收
    public static void register(Context context){

        //屏幕关闭广播
        BroadcastReceiver receiver = new SystemReceiver();
        IntentFilter filter = new IntentFilter();
        filter.addAction(Intent.ACTION_SCREEN_OFF);
        filter.addAction(Intent.ACTION_SCREEN_ON);
        filter.addAction(Intent.ACTION_TIME_TICK);
        filter.addAction(baseReceiver.ACTION_REBOOT);
        context.registerReceiver(receiver, filter);


    }

    //发送定时广播
    public static void  sendReBootBroadcast(){

        Intent intent = new Intent();
        intent.setAction(ACTION_REBOOT);
        Context context = null;

        if(mGlobal.mNavigationBarActivity != null){
            mGlobal.mNavigationBarActivity.sendBroadcast(intent,null);
            mUploadDataUtil.postLogs(intent.getAction());
            return;
        }
        if(mGlobal.mApplication != null){
            mGlobal.mApplication.sendBroadcast(intent,null);
            mUploadDataUtil.postLogs(intent.getAction());
        }
    }

}
