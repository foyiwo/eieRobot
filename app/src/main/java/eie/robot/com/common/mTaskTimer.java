package eie.robot.com.common;

import java.util.Date;

import eie.robot.com.accessibilityservice.AccessibilityHelper;
import eie.robot.com.task.BaseRobotTask;

public class mTaskTimer {

    //任务的时间
    private static int TaskMin = 0;

    //任务计时器的数量
    private static int AppTaskCounter = 0;

    private static Date LastTimerTaskTime = new Date();


    //定时器的监控器
    public static void TimerTaskTimer(){
        //定时任务
        mFunction.runInChildThread(new Runnable() {
            @Override
            public void run() {
                try{
                    while (true){
                        try{
                            if(!isNormalForAppTimer()){
                                try{
                                    //mThread.mTaskThread.interrupt();
                                    mThread.mTaskThread = null;
                                }catch (Exception ex){
                                     mToast.error("TimerTaskTimer:"+ex.getMessage());
                                }
                                mCommonTask.StartTask();
                                mTaskTimer.AppTaskTimer();

                                mTaskTimer.LastTimerTaskTime = new Date();
                                mFunction.click_sleep();
                            }
                            //两分钟后起来看看
                            mFunction.sleep(10*1000);
                        }catch (Exception x){
                            mTaskTimer.AppTaskCounter = 0;
                            mToast.error("定时器出错："+x.getMessage());
                        }
                    }
                }catch (Exception ex){
                    mCommonTask.setAppTaskClose();
                    mTaskTimer.AppTaskCounter = 0;
                }
            }
        });
    }

    //任务的定时器
    public static void AppTaskTimer(){
        //定时任务
        mFunction.runInChildThread(new Runnable() {
            @Override
            public void run() {
                try{
                    while (true){
                        try{
                            if(mTaskTimer.AppTaskCounter >= 1){
                                mToast.success("超过一个定时器在运行，本次启动停止");
                                return;
                            }
                            if(mCommonTask.isCloseThreadTask()){
                                mFloatWindow.EditRobTaskTimerMinText("-1m");
                                mFunction.sleep(30*1000);
                                continue;
                            }
                            if(mCommonTask.isCloseAppTask()){
                                //mToast.error("APP任务处于关闭状态，定时器未启动");
                                mFunction.sleep(10*1000);
                                continue;
                            }
                            mTaskTimer.TaskMin =  mFunction.getRandom_10_20()+15;
                            mTaskTimer.AppTaskCounter ++;
                            while (mTaskTimer.TaskMin >= 0){
                                mFloatWindow.EditRobTaskTimerMinText(mTaskTimer.TaskMin+"m");
                                if(mIncomeTask.isTimeOutForIncome()){
                                    //APP收益停止已超时，停止当前应用，跳往下一个
                                    break;
                                }
                                int SecondCount = 60;
                                int SleepSecondTimer = 60/SecondCount;
                                while (SecondCount >= 0){
                                    if(mCommonTask.isCloseThreadTask()){ break; }
                                    mFloatWindow.EditRobTaskTimerSecondText(SecondCount*SleepSecondTimer+"s");
                                    mFunction.sleep(SleepSecondTimer*1000);
                                    mTaskTimer.LastTimerTaskTime = new Date();
                                    SecondCount -- ;
                                }
                                mTaskTimer.TaskMin--;
                                if(mCommonTask.isCloseThreadTask()){ break; }
                            }

                            mCommonTask.setAppTaskClose();
                            mTaskTimer.AppTaskCounter = 0;
                        }catch (Exception x){
                            mTaskTimer.AppTaskCounter = 0;
                            mToast.error("定时器出错："+x.getMessage());
                        }
                    }
                }catch (Exception ex){
                    mCommonTask.setAppTaskClose();
                    mTaskTimer.AppTaskCounter = 0;
                }
            }
        });
    }

    //定时打开手机屏幕和在每天晚上六点后，重置
    public static void AppTaskOpenScreenTimer(){
        //定时任务
        mFunction.runInChildThread(new Runnable() {
            @Override
            public void run() {
                try{
                    while (true){
                        try {
                            mFunction.openScreen();
                            mFunction.sleep(4*60*1000);
                        }catch (Exception ex){

                        }

                    }
                }catch (Exception ex){

                }
            }
        });
    }

    //凌晨停止刷钱
    public static void StopAppInNight(){
        try {
            if(judgeTimeIsInNight()){
                while (true){
                    if(judgeTimeIsInNight()){
                        AccessibilityHelper.performHome();
                        mToast.success("深夜休息中.....");
                        int StopTime = 1;
                        mFunction.sleep(StopTime*60*1000);
                        mFunction.openScreen();
                    }else {
                        for (BaseRobotTask task : mCommonTask.mTasks){
                            task.setTodayIncomeIsNoFinsh();
                        }
                        mToast.success("天亮了，开始搬砖....");
                        break;
                    }
                }
            }
        }catch (Exception ignored){
            mToast.error_sleep(ignored.getMessage());
        }

    }

    public static Boolean judgeTimeIsInNight(){

        //获取当前时间
        String currentTime = mDateUtil.formatDate(new Date(),"datetime");

        String currentDate = mDateUtil.formatDate(new Date(),"date");

        int StartRamdomNumber = mFunction.getRandom_4_8();
        int EndRamdomNumber = mFunction.getRandom_4_8();
        String startTime = currentDate+" 23:00:00";
        String endTime = currentDate+" 08:1"+EndRamdomNumber+":00";

        //比较时间
        int start   = mDateUtil.compareDate(currentTime,startTime);
        int end     = mDateUtil.compareDate(endTime,currentTime);

        //属于休眠时间
        if(start > 0 || end > 0){
            return true;
        }
        return false;
    }

    public static void ResetingAppFinishStatus(){
        //定时任务
        mFunction.runInChildThread(new Runnable() {
            @Override
            public void run() {
                try{
                    while (true){
                        try {
                            int StopTime = 1;
                            mFunction.sleep(StopTime*60*1000);
                            //获取当前时间
                            String currentTime = mDateUtil.formatDate(new Date(),"datetime");

                            String currentDate = mDateUtil.formatDate(new Date(),"date");
                            String startTime = currentDate+" 02:30:00";
                            String endTime = currentDate+" 06:00:00";

                            //比较时间，如果当前时间已经超过了收益时差最大值。
                            int start   = mDateUtil.compareDate(currentTime,startTime);
                            int end     = mDateUtil.compareDate(endTime,currentTime);
                            if(start > 0 && end > 0){
                                for (BaseRobotTask task : mCommonTask.mTasks){
                                    task.setTodayIncomeIsNoFinsh();
                                }
                            }

                        }catch (Exception ex){

                        }

                    }
                }catch (Exception ex){

                }
            }
        });
    }

    //判断定时器是否在正常运行
    private static Boolean isNormalForAppTimer(){
        try {
            //int StopTime = 2;
            int StopTime = 2;
            //获取当前时间
            String currentTime = mDateUtil.formatDate(new Date(),"datetime");

            //获取最近的收益时间
            String lastTime = mDateUtil.formatDate(mTaskTimer.LastTimerTaskTime,"datetime");
            lastTime = mDateUtil.dateAdd(lastTime,"mm",StopTime,"datetime");

            //比较时间，如果当前时间已经超过了收益时差最大值。
            int res = mDateUtil.compareDate(lastTime,currentTime);
            if(res > 0){
                return true;
            }
            mToast.error("超过"+StopTime+"分钟定时器没有运行，重新启动任务");
        }catch (Exception ex){

        }
        return false;
    }

}
