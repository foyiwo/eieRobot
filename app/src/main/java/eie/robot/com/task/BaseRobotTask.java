package eie.robot.com.task;

import android.view.accessibility.AccessibilityNodeInfo;

import com.vondear.rxtool.RxDeviceTool;
import com.vondear.rxtool.view.RxToast;

import eie.robot.com.accessibilityservice.AccessibilityHelper;
import eie.robot.com.common.mCommonTask;
import eie.robot.com.common.mConfig;
import eie.robot.com.common.mFunction;
import eie.robot.com.common.mGlobal;
import eie.robot.com.common.mToast;

public abstract class BaseRobotTask {

    Boolean IsSign = false;        //是否已经签到
    String AppName = "";        //应用名称
    float TodayGold = 0;        //今日当前收益
    float TodayMaxIncome = 0;   //今日最大收益，用于控制是否继续刷金币
    public boolean TodayIncomeIsFinsh = false;   //今日的收益是否已完成。


    public boolean StartTask(){
        //APP允许开始的标志，true可继续执行，false即表示准备结束
        mCommonTask.AppTaskOpenStatus = true;
        //这个状态表示APP任务结束成功(之所有有这个，是因为结束任务有时间差)
        mCommonTask.AppTaskCloseSuccessStatus = false;
        RxToast.success(mGlobal.mNavigationBarActivity,"准备运行"+AppName).show();
        return true;
    }

    public  boolean CloseTask(){
        mCommonTask.AppTaskOpenStatus = false;
        mCommonTask.AppTaskCloseSuccessStatus = true;
        mToast.success(this.AppName+"停止成功");

        mFunction.sleep(2 * mConfig.clickSleepTime);
        AccessibilityHelper.performHome(mGlobal.mAccessibilityService);
        return true;
    }

    private String PackageName = null;



    BaseRobotTask(){
        PackageName = mFunction.GetAppPackageName(AppName);
    }



    public boolean returnHome(){
        if(mCommonTask.AppTaskOpenStatus && mCommonTask.ThreadTaskOpenStatus){
            return true;
        }
        return false;
    }



}
