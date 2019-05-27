package eie.robot.com.common;

import android.content.ComponentName;
import android.content.Intent;
import android.view.accessibility.AccessibilityNodeInfo;

import com.vondear.rxtool.RxDeviceTool;
import com.vondear.rxtool.view.RxToast;

import java.util.ArrayList;

import eie.robot.com.accessibilityservice.AccessibilityHelper;
import eie.robot.com.task.BaseRobotTask;
import eie.robot.com.task.RobTaskQiMaoXiaoShuo;
import eie.robot.com.task.RobTaskJuKanDian;
import eie.robot.com.task.RobTaskQuTouTiao;
import eie.robot.com.task.RobTaskShuaBao;

public class mCommonTask {

    //总线程任务状态
    public static boolean ThreadTaskOpenStatus = false;

    //APP允许开始的标志，true可继续执行，false即表示准备结束
    public static boolean AppTaskOpenStatus = false;

    //这个状态表示APP任务结束成功(之所有有这个，是因为结束任务有时间差)
    public static boolean AppTaskCloseSuccessStatus = false;

    //这个状态表示总任务结束成功(之所有有这个，是因为结束任务有时间差)
    public static boolean ThreadTaskCloseSuccessStatus = false;

    public static String StartTask(){
        //打开无障碍服务
        if(!mFunction.openAccessibilityService()){
            return "总任务启动失败，无障碍服务未开启";
        }

        //初始化设备信息
        mDeviceUtil.initDeviceInfo();

        mThread.mTaskThread = new Thread(new Runnable() {
            @Override
            public void run() {
                mCommonTask.ThreadTaskCloseSuccessStatus = false;
                mCommonTask.ThreadTaskOpenStatus = true;
                //组装任务列表，通过策略
                ArrayList<BaseRobotTask> tasks = new ArrayList<BaseRobotTask>();
                //刷宝
                tasks.add(new RobTaskShuaBao());
                //聚看点
                tasks.add(new RobTaskJuKanDian());
                //趣头条
                tasks.add(new RobTaskQuTouTiao());
                //tasks.get(2).StartTask();
                int i = 0;
                while (ThreadTaskOpenStatus){
                    if(i >= tasks.size()){
                        mCommonTask.ClearPhoneCacheTask();
                        i = 0;
                    }
                    //判断该APP是否已经到达最大收益。
                    if(!tasks.get(i).TodayIncomeIsFinsh){
                        //定时器，到时间后，设置【退出】标志，让任务退出
                        mCommonTask.AppTaskTimer();

                        //开启任务，死循环，如果没有上面的定时器，将一直执行
                        tasks.get(i).StartTask();
                    }
                    if(!ThreadTaskOpenStatus){ break; }
                    i++;
                }
                mCommonTask.ThreadTaskCloseSuccessStatus = true;
                mToast.success("总任务停止");
            }
        });

        //启动
        mThread.mTaskThread.start();
        mToast.message("总任务开始");
        return "true";

    }

    public static void AppTaskTimer(){
        //定时任务
        mFunction.runInChildThread(new Runnable() {
            @Override
            public void run() {
                int TaskMin = mFunction.getRandom_10_20();
                while (TaskMin > 0){
                    mFloatWindow.EditRobTaskTimerText(TaskMin+"m");
                    //休眠一分钟
                    mFunction.sleep(60*1000);
                    TaskMin--;
                }
                mCommonTask.AppTaskOpenStatus = false;
            }
        });
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
    //清理手机缓存
    public static void ClearPhoneCacheTask(){
        try{
            if(!mCommonTask.ThreadTaskOpenStatus){
                return;
            }
            while (true){
                if(mCommonTask.AppTaskCloseSuccessStatus){
                    break;
                }
            }
            mFunction.sleep(mConfig.clickSleepTime);
            String UniqueSerialNumber = RxDeviceTool.getUniqueSerialNumber().toLowerCase();
            int index = -1;
            index = UniqueSerialNumber.toLowerCase().indexOf("xiaomi");
            //小米手机版清理数据
            if(index > -1){
                ClearXiaoMiPhoneCache();
            }
            index = UniqueSerialNumber.toLowerCase().indexOf("lenovo");
            index = UniqueSerialNumber.toLowerCase().indexOf("huawei");
        }catch (Exception ex){
            RxToast.success("清理内存报错:"+ex.getMessage());
        }


    }

    private static void ClearXiaoMiPhoneCache(){
        RxToast.success("开始清理手机内存");
        ComponentName componetName = new ComponentName(
                "com.miui.cleanmaster",//主包名
                "com.miui.optimizecenter.MainActivity");//小米安全中心首页Activity包名
        try {
            Intent intent = new Intent();
            intent.setComponent(componetName);
            mGlobal.mApplication.startActivity(intent);
        } catch (Exception e) {
            mToast.message("错误："+e.getMessage());
        }
        AccessibilityNodeInfo nodeInfo = null;
        int count = 3 * mConfig.loopCount;
        while (true) {
            nodeInfo = AccessibilityHelper.findNodeInfosByText("清理选中垃圾");
            if ( nodeInfo != null ) {
                break;
            }
            count--;
            if (count < 0) {
                break;
            }
            mFunction.sleep(mConfig.clickSleepTime);
        }
        if(nodeInfo != null){
            AccessibilityHelper.performClick(nodeInfo);
            mFunction.sleep(15*1000);
        }
        RxToast.success("清理手机内存完成");
    }




}


