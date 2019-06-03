package eie.robot.com.common;

import java.util.Date;
import java.util.List;

import eie.robot.com.task.BaseRobotTask;

public class mIncomeTask {

    //最后收益的时间，如果超过五分钟没有更新，则在定时器里结束该应用
    private static Date LastIncomeTime = new Date();

    //全部任务完成定时器，达到多少之后，重新唤醒
    private static int AppTaskAllFinishCounter = 0;

    private static String WorkStartTime  = "08:00:00";
    private static String WorkEndTime    = "00:30:00";


    //判断停止收益的时候是否已经超时
    public static boolean isTimeOutForIncome(){
        return !mIncomeTask.isNormalForIncome();
    }

    //判断收益是否在正常运行
    private static Boolean isNormalForIncome(){
        try {
            int StopTime = 5;
            //获取当前时间
            String currentTime = mDateUtil.formatDate(new Date(),"datetime");

            //获取最近的收益时间
            String lastTime = mDateUtil.formatDate(mIncomeTask.LastIncomeTime,"datetime");
            lastTime = mDateUtil.dateAdd(lastTime,"mm",StopTime,"datetime");

            //比较时间，如果当前时间已经超过了收益时差最大值。
            int res = mDateUtil.compareDate(lastTime,currentTime);
            if(res > 0){
                return true;
            }
            mToast.error("超过"+StopTime+"分钟没有手机，取消该APP资格");
            mIncomeTask.setLastIncomeTime();
            mFunction.click_sleep();
        }catch (Exception ex){

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
        mIncomeTask.LastIncomeTime = new Date();
    }


    public static Boolean isAppTaskAllFinish(List<BaseRobotTask> tasks){
        int i = 0;
        for (BaseRobotTask task : tasks){
            if(!task.isTodayIncomeNoFinsh()){
                i++;
            }
        }
        if(i >= tasks.size()){
            if(mIncomeTask.AppTaskAllFinishCounter > 60){
                for (BaseRobotTask task : tasks){
                    task.setTodayIncomeIsNoFinsh();
                }
                return false;
            }
            mIncomeTask.AppTaskAllFinishCounter ++;
            return true;
        }
        return false;
    }
}
