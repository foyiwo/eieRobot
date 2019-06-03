package eie.robot.com.common;

import android.content.ComponentName;
import android.content.Intent;
import android.view.accessibility.AccessibilityNodeInfo;

import com.vondear.rxtool.RxDeviceTool;
import com.vondear.rxtool.view.RxToast;

import java.util.ArrayList;
import java.util.Date;

import eie.robot.com.accessibilityservice.AccessibilityHelper;
import eie.robot.com.task.BaseRobotTask;
import eie.robot.com.task.RobTaskQiMaoXiaoShuo;
import eie.robot.com.task.RobTaskJuKanDian;
import eie.robot.com.task.RobTaskQuKanTianXia;
import eie.robot.com.task.RobTaskQuTouTiao;
import eie.robot.com.task.RobTaskShuaBao;
import eie.robot.com.task.RobTaskZhongQingKanDian;

public class mCommonTask {

    //总线程任务状态
    public static boolean ThreadTaskOpenStatus = false;

    //APP允许开始的标志，true可继续执行，false即表示准备结束
    private static boolean AppTaskOpenStatus = false;

    //APP完成了所有的任务，处于休眠状态
    private static boolean AppTaskSleepStatus = false;

    public static String StartTask(){
        //打开无障碍服务
        if(!mFunction.openAccessibilityService()){
            return "总任务启动失败，无障碍服务未开启";
        }

        //初始化设备信息
        mDeviceUtil.initDeviceInfo();
        mCommonTask.ThreadTaskOpenStatus = true;
        mThread.mTaskThread = new Thread(new Runnable() {
            @Override
            public void run() {

                //定时器，到时间后，设置【退出】标志，让任务退出
                mTaskTimer.AppTaskTimer();
                //定时器，不断点亮屏幕
                mTaskTimer.AppTaskOpenScreenTimer();

                //组装任务列表，通过策略
                ArrayList<BaseRobotTask> tasks = new ArrayList<>();

                //趣看天下
                tasks.add(new RobTaskZhongQingKanDian());
                //趣看天下
                tasks.add(new RobTaskQuKanTianXia());
                //刷宝
                tasks.add(new RobTaskShuaBao());
                //趣头条
                tasks.add(new RobTaskQuTouTiao());
                //聚看点
                tasks.add(new RobTaskJuKanDian());

                int i = 0;

                while (ThreadTaskOpenStatus){
                    if(mIncomeTask.isAppTaskAllFinish(tasks)){
                        mToast.success("所有任务已完成，休眠一分钟再说");
                        mFunction.sleep(60*1000);
                        continue;
                    }

                    if(i >= tasks.size()){
                        i = 0;
                    }
                    if( i == 3){
                        mCacheTask.ClearPhoneCacheTask();
                    }

                    //判断该APP是否已经到达最大收益。
                    if(tasks.get(i).isTodayIncomeNoFinsh()){
                        //开启任务，死循环，如果全局定时器，将一直执行
                        tasks.get(i).StartTask();
                    }
                    if(!ThreadTaskOpenStatus){ break; }
                    i++;
                }
                AccessibilityHelper.performHome();
                mToast.success("总任务停止");
            }
        });

        //启动
        mThread.mTaskThread.start();
        mToast.message("总任务开始");
        return "true";

    }

    //停止任务
    public static void StopTask(){
        try {
            mCommonTask.AppTaskOpenStatus = false;
            mCommonTask.ThreadTaskOpenStatus = false;
        }catch (Exception ex){
            mToast.success("总任务停止"+ex.getMessage());
        }

    }

    public static void setAppTaskClose(){
        mCommonTask.AppTaskOpenStatus = false;
    }
    public static void setAppTaskOpen(){
        mCommonTask.AppTaskOpenStatus = true;
    }
    public static boolean isCloseAppTask(){
        return !mCommonTask.AppTaskOpenStatus ||  !mCommonTask.ThreadTaskOpenStatus;
    }
    public static boolean isCloseThreadTask(){
        return !mCommonTask.ThreadTaskOpenStatus;
    }
    public static boolean isOpenAppTask(){
        return mCommonTask.AppTaskOpenStatus;
    }
    public static boolean isSleepApp(){
        return mCommonTask.AppTaskSleepStatus;
    }
    public static void setAppSleep(){
        mCommonTask.AppTaskSleepStatus = true;
    }
    public static void setAppNoSleep(){
        mCommonTask.AppTaskSleepStatus = false;
    }
}


