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

public class mCommonTask {

    //总线程任务状态
    public static boolean ThreadTaskOpenStatus = false;

    //APP允许开始的标志，true可继续执行，false即表示准备结束
    public static boolean AppTaskOpenStatus = false;

    //这个状态表示APP任务结束成功(之所有有这个，是因为结束任务有时间差)
    public static boolean AppTaskCloseSuccessStatus = false;

    //这个状态表示总任务结束成功(之所有有这个，是因为结束任务有时间差)
    public static boolean ThreadTaskCloseSuccessStatus = false;

    //APP任务定时器的计数器，同一时间，只允许一个计数器存在
    public static int AppTaskCounter = 0;

    //最后收益的时间，如果超过五分钟没有更新，则在定时器里结束该应用
    public static Date LastIncomeTime = new Date();


    public static String WorkStartTime  = "08:00:00";
    public static String WorkEndTime    = "00:30:00";


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

                //趣头条
                tasks.add(new RobTaskQuTouTiao());
                //刷宝
                tasks.add(new RobTaskShuaBao());
                //趣看天下
                tasks.add(new RobTaskQuKanTianXia());
                //聚看点
                tasks.add(new RobTaskJuKanDian());
                //tasks.get(2).StartTask();

                int TaskSize = tasks.size();
                int i = 0;
                while (ThreadTaskOpenStatus){
                    if(i >= tasks.size()){
                        mCommonTask.ClearPhoneCacheTask();
                        i = 0;
                    }
                    //判断该APP是否已经到达最大收益。
                    if(tasks.get(i).TodayIncomeIsFinsh){
                        TaskSize = TaskSize - 1;
                        if(TaskSize == 0){
                            mFunction.sleep(60*60*1000);
                            for (BaseRobotTask task : tasks){
                                task.TodayIncomeIsFinsh = false;
                            }
                            TaskSize = tasks.size();
                        }
                    }else {
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
                try{
                    int TaskMin =  mFunction.getRandom_10_20()+10;
                    if(mCommonTask.AppTaskCounter >= 1){
                        return;
                    }
                    mCommonTask.AppTaskCounter ++;
                    while (TaskMin >= 0){
                        if(mCommonTask.AppTaskCounter > 1){
                            return;
                        }
                        mFloatWindow.EditRobTaskTimerText(TaskMin+"m");

                        if(!isNormalForIncome()){
                            break;
                        }

                        //休眠一分钟
                        mFunction.sleep(60*1000);
                        TaskMin--;
                    }
                    mCommonTask.AppTaskOpenStatus = false;
                    mCommonTask.AppTaskCounter = 0;

                }catch (Exception ex){
                    mCommonTask.AppTaskOpenStatus = false;
                    mCommonTask.AppTaskCounter = 0;
                }


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
//            while (true){
//                if(mCommonTask.AppTaskCloseSuccessStatus){
//                    break;
//                }
//            }
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
    //清楚小米手机的内存
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
    //判断收益是否在正常运行
    public static Boolean isNormalForIncome(){

        String currentTime = mDateUtil.formatDate(new Date(),"datetime");

        String lastTime = mDateUtil.formatDate(mCommonTask.LastIncomeTime,"datetime");
        lastTime = mDateUtil.dateAdd(lastTime,"mm",3,"datetime");

        int res = mDateUtil.compareDate(lastTime,currentTime);
        if(res > 0){
            return true;
        }
        return false;
    }

    public static Boolean isAppStopingTime(){

        String currentTime = mDateUtil.formatDate(new Date(),"datetime");
        String currentTimeDay = mDateUtil.formatDate(new Date(),"date");
        String EndTime = currentTimeDay+" "  + WorkEndTime;
        String StartTime = currentTimeDay+" "+ WorkStartTime;

        int res1 = mDateUtil.compareDate(currentTime,EndTime);
        int res2 = mDateUtil.compareDate(StartTime,currentTime);
        if(res1 > 0 && res2 >0){
            return true;
        }
        return false;
    }
    //将收益的时间更新到最近
    public static void setLastIncomeTime(){
        mCommonTask.LastIncomeTime = new Date();
    }






}


