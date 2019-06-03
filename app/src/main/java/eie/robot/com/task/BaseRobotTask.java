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
import eie.robot.com.common.mIncomeTask;
import eie.robot.com.common.mToast;

public abstract class BaseRobotTask {

    Boolean IsSign = false;        //是否已经签到
    String AppName = "";        //应用名称
    float TodayGold = 0;        //今日当前收益
    float TodayMaxIncome = 0;   //今日最大收益，用于控制是否继续刷金币
    boolean TodayIncomeIsFinsh = false;   //今日的收益是否已完成。
    private String PackageName = null; //APP包名

    public boolean StartTask(){
        //APP允许开始的标志，true可继续执行，false即表示准备结束
        mCommonTask.setAppTaskOpen();
        //设置APP的收益最新时间
        mIncomeTask.setLastIncomeTime();

        mToast.success("准备运行【"+AppName+"】");
        return true;
    }

    boolean CloseTask(){
        //设置APP的运行状态为停止
        mCommonTask.setAppTaskClose();

        mToast.success("【"+this.AppName+"】已停止");
        mFunction.sleep(2 * mConfig.clickSleepTime);
        return true;
    }

    BaseRobotTask(){
        PackageName = mFunction.GetAppPackageName(AppName);
    }

    boolean returnHome(String Nav1, String Nav2, Runnable runnable){

        if(mCommonTask.isCloseAppTask()){
            return false;
        }

        //循环打开APP
        if(!mFunction.loopOpenApp(AppName)){
            return false;
        }

        //确定已经打开应用之后，下面确定是否处于首页。
        //获取底部导航栏的图标来判断
        AccessibilityNodeInfo NodeInfo1 = AccessibilityHelper.findNodeInfosByText(Nav1);
        AccessibilityNodeInfo NodeInfo2 = AccessibilityHelper.findNodeInfosByText(Nav2);

        if ( NodeInfo1 != null || NodeInfo2 != null ) {
           //说明正处于首页
            return true;
        } else {
            //到此，虽然不是主界面，但却是处于打开状态，目前可能是处于内页，弹框中，至于是哪个，无法确定，
            //采取触发返回键的方式。
            int count = mConfig.loopCount*2;
            while (count > 0) {
                //清理可能出现的各种弹框
                runnable.run();//this.CloseDialog();

                //再次获取底部导航栏
                NodeInfo1 = AccessibilityHelper.findNodeInfosByText(Nav1);
                NodeInfo2 = AccessibilityHelper.findNodeInfosByText(Nav2);
                if ( NodeInfo1 != null || NodeInfo2 != null ) {
                    break;
                }
                //尝试点击一次返回键
                AccessibilityHelper.performBack();

                //停一下，等待反应
                mFunction.click_sleep();

                //如果超过了多次仍然没有成功，可能是无法获取到Window，尝试点击一下屏幕中间
                if(count < (count/2)){
                    mToast.error("多次没反应，尝试着点击一下屏幕中心");
                    mGestureUtil.clickInScreenCenter();
                }
                count--;
            }
            if (NodeInfo1 != null || NodeInfo2 != null) {
                return true;
            } else {
                //如果一直没反应，尝试着点击一下屏幕中心。
                mToast.error("一直没反应，尝试着点击一下屏幕中心");
                mGestureUtil.click(mGlobal.mScreenWidth/2,mGlobal.mScreenHeight/2);
                return false;
            }
        }
    }

    boolean returnHomeById(String Nav1, String Nav2, Runnable runnable){

        if(mCommonTask.isCloseAppTask()){
            return false;
        }

        //循环打开APP
        if(!mFunction.loopOpenApp(AppName)){
            return false;
        }

        //确定已经打开应用之后，下面确定是否处于首页。
        //获取底部导航栏的图标来判断
        AccessibilityNodeInfo NodeInfo1 = AccessibilityHelper.findNodeInfosById(Nav1);
        AccessibilityNodeInfo NodeInfo2 = AccessibilityHelper.findNodeInfosById(Nav2);

        if ( NodeInfo1 != null || NodeInfo2 != null ) {
            //说明正处于首页
            return true;
        } else {
            //到此，虽然不是主界面，但却是处于打开状态，目前可能是处于内页，弹框中，至于是哪个，无法确定，
            //采取触发返回键的方式。
            int count = mConfig.loopCount*2;
            while (count > 0) {
                //清理可能出现的各种弹框
                runnable.run();//this.CloseDialog();

                //再次获取底部导航栏
                NodeInfo1 = AccessibilityHelper.findNodeInfosById(Nav1);
                NodeInfo2 = AccessibilityHelper.findNodeInfosById(Nav2);
                if ( NodeInfo1 != null || NodeInfo2 != null ) {
                    break;
                }
                //尝试点击一次返回键
                AccessibilityHelper.performBack();

                //停一下，等待反应
                mFunction.click_sleep();

                //如果超过了多次仍然没有成功，可能是无法获取到Window，尝试点击一下屏幕中间
                if(count < (count/2)){
                    mToast.error("多次没反应，尝试着点击一下屏幕中心");
                    mGestureUtil.clickInScreenCenter();
                }
                count--;
            }
            if (NodeInfo1 != null || NodeInfo2 != null) {
                return true;
            } else {
                //如果一直没反应，尝试着点击一下屏幕中心。
                mToast.error("一直没反应，尝试着点击一下屏幕中心");
                mGestureUtil.click(mGlobal.mScreenWidth/2,mGlobal.mScreenHeight/2);
                return false;
            }
        }
    }


    public boolean isTodayIncomeNoFinsh(){
        return ! this.TodayIncomeIsFinsh;
    }

    //将今天的最大收益设置为未达到
    public void setTodayIncomeIsNoFinsh(){
        this.TodayIncomeIsFinsh = false;
    }

}
