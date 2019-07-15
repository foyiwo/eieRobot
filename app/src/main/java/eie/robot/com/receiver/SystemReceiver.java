package eie.robot.com.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import eie.robot.com.common.mAdbShell;
import eie.robot.com.common.mFunction;
import eie.robot.com.common.mGlobal;
import eie.robot.com.common.mUploadDataUtil;

public class SystemReceiver extends BroadcastReceiver {
    static final String BOOT_COMPLETED = Intent.ACTION_BOOT_COMPLETED;
    static final String SCREEN_OFF_ACTION = Intent.ACTION_SCREEN_OFF;
    static final String TIME_TICK_ACTION = Intent.ACTION_TIME_TICK;

    @Override
    public void onReceive(Context context, Intent intent) {

        try{

            //mUploadDataUtil.postLogs(intent.getAction());
            if(intent.getAction() != null
                    && intent.getAction().equals(SCREEN_OFF_ACTION)){
                mAdbShell.reboot();
            }

            if(intent.getAction() != null
                    && intent.getAction().equals(baseReceiver.ACTION_REBOOT)){
                //开机打开屏幕
                mUploadDataUtil.getIsReBoot();
            }



        }catch (Exception ex){
            mUploadDataUtil.postLogs(intent.getAction()+":"+ex.getMessage());
        }
    }
}
