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
import eie.robot.com.task.RobTaskShanDianHeZi;
import eie.robot.com.task.RobTaskShuaBao;
import eie.robot.com.task.RobTaskSouHuZiXun;
import eie.robot.com.task.RobTaskTouTiaoJingXuan;
import eie.robot.com.task.RobTaskWeiLiKanKan;
import eie.robot.com.task.RobTaskWeiQuKan;
import eie.robot.com.task.RobTaskYouKanDian;
import eie.robot.com.task.RobTaskZhongQingKanDian;

public class mCommonTask {

    //总线程任务状态
    public static boolean ThreadTaskOpenStatus = false;

    //APP允许开始的标志，true可继续执行，false即表示准备结束
    private static boolean AppTaskOpenStatus = false;

    //APP完成了所有的任务，处于休眠状态
    private static boolean AppTaskSleepStatus = false;

    public static ArrayList<BaseRobotTask> mTasks = null;

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
                //在夜间重置
                mTaskTimer.ResetingAppFinishStatus();


                //组装任务列表，通过策略
                mTasks = new ArrayList<>();


                //头条精选
                mTasks.add(new RobTaskTouTiaoJingXuan());//已重构
                //搜狐资讯
                mTasks.add(new RobTaskSouHuZiXun());     //已重构
                //微鲤看看
                mTasks.add(new RobTaskWeiLiKanKan());    //已重构

                //优看点
                mTasks.add(new RobTaskYouKanDian());     //已重构
                //微趣看
                mTasks.add(new RobTaskWeiQuKan());       //已重构

                //聚看点
                mTasks.add(new RobTaskJuKanDian());
                //闪电盒子
                mTasks.add(new RobTaskShanDianHeZi());
                //趣看天下
                mTasks.add(new RobTaskQuKanTianXia());
                //中青看点
                mTasks.add(new RobTaskZhongQingKanDian());
                //趣头条
                mTasks.add(new RobTaskQuTouTiao());
                //刷宝
                mTasks.add(new RobTaskShuaBao());



                int i = 0;

                while (ThreadTaskOpenStatus){
                    //清楚手机内存
                    if(i >= mTasks.size()){
                        i = 0;
                    }
                    if( i == 3 || i == 7 || i == 11){
                        mCacheTask.ClearPhoneCacheTask();
                    }

                    //判断该APP是否已经到达最大收益。
                    if(mTasks.get(i).isTodayIncomeNoFinsh()){
                        //开启任务，死循环，如果全局定时器，将一直执行
                        mTasks.get(i).StartTask();
                    }

                    if(!ThreadTaskOpenStatus){ break; }

                    //清楚手机内存
                    mCacheTask.ClearPhoneROMTask();

                    i++;
                }
                //AccessibilityHelper.performHome();
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


