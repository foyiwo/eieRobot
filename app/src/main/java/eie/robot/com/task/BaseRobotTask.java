package eie.robot.com.task;

import android.view.accessibility.AccessibilityNodeInfo;

import com.vondear.rxtool.RxDeviceTool;
import com.vondear.rxtool.view.RxToast;

import eie.robot.com.accessibilityservice.AccessibilityHelper;
import eie.robot.com.common.mCommonTask;
import eie.robot.com.common.mConfig;
import eie.robot.com.common.mFunction;
import eie.robot.com.common.mGestureUtil;
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


    public boolean returnHome(String Nav1,String Nav2 ,Runnable runnable){
        if(!mCommonTask.AppTaskOpenStatus && !mCommonTask.ThreadTaskOpenStatus){
            return false;
        }
        if(!mFunction.loopOpenApp(AppName)){
            return false;
        }

        //确定已经打开应用之后，下面确定是否处于首页。

        //获取底部导航栏的图标
        AccessibilityNodeInfo NodeInfo1 = AccessibilityHelper.findNodeInfosByText(Nav1);
        AccessibilityNodeInfo NodeInfo2 = AccessibilityHelper.findNodeInfosByText(Nav2);

        if ( NodeInfo1 != null || NodeInfo2 != null ) {
            //设置收益的最新时间
            //mCommonTask.setLastIncomeTime();
            return true;
        } else {
            //到此，虽然不是主界面，但却是处于打开状态，目前可能是处于，内页，至于哪个内页，无法确定，
            //采取触发返回键的方式。
            int count = mConfig.loopCount+5;
            while (count > 0) {

                runnable.run();//this.CloseDialog();

                NodeInfo1 = AccessibilityHelper.findNodeInfosByText(Nav1);
                NodeInfo2 = AccessibilityHelper.findNodeInfosByText(Nav2);
                if ( NodeInfo1 != null || NodeInfo2 != null ) {
                    break;
                }
                count--;
                AccessibilityHelper.performBack(mGlobal.mAccessibilityService);
                //停一下，等待反应
                mFunction.click_sleep();
            }
            if (NodeInfo1 != null || NodeInfo2 != null) {
                return true;
            } else {
                //如果一直没反应，尝试着点击一下屏幕中心。
                mGestureUtil.click(mGlobal.mScreenWidth/2,mGlobal.mScreenHeight/2);
                return false;
            }
        }
    }



}
