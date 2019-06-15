package eie.robot.com.task;

import android.graphics.Rect;
import android.view.accessibility.AccessibilityNodeInfo;

import com.vondear.rxtool.view.RxToast;

import eie.robot.com.accessibilityservice.AccessibilityHelper;
import eie.robot.com.common.mCommonTask;
import eie.robot.com.common.mConfig;
import eie.robot.com.common.mFunction;
import eie.robot.com.common.mGestureUtil;
import eie.robot.com.common.mGlobal;
import eie.robot.com.common.mIncomeTask;
import eie.robot.com.common.mToast;

public class RobTaskShuaBao extends BaseRobotTask {

    public int SizeOffset = 40;

    /**
     * 构造函数
     */
    public RobTaskShuaBao() {
        super();
        this.AppName = "刷宝短视频";
        String packname = mFunction.GetAppPackageName(this.AppName);
        if(packname.isEmpty()){
            this.AppName = "刷宝";
        }

        this.TodayMaxIncome = 13888;
        this.TodayIncomeIsFinsh = false;
    }


    //执行刷单任务（领取时段奖励、定时刷新视频、查看文章）
    @Override
    public boolean StartTask()  {
        super.StartTask();
        while (mCommonTask.isOpenAppTask()){
            try {

                if(!returnHome()){
                    continue;
                }

                //判断收益是否封顶
                if(JudgeGoldIncomeIsMax()){
                    break;
                }

                //签到
                SignIn();

                //看视频
                this.performTask_WatchVideo();

                if(mCommonTask.isCloseAppTask()){ break; }
            }catch (Exception ex){
                RxToast.error(ex.getMessage());
            }
        }
        super.CloseTask();
        return false;
    }

    //-----------------------------------------------------------

    //看视频总任务
    @Override
    Boolean performTask_WatchVideo(){
        int RefreshCount =   mFunction.getRandom_10_20()+20;
        while (RefreshCount > 0){
            if(mCommonTask.isCloseAppTask()){ break; }
            performTask_WatchVideo_1();
            RefreshCount -- ;
        }
        return false;
    }

    //执行刷单任务（定时刷小视频）
    private boolean performTask_WatchVideo_1(){
        if(!returnHome()){
            return false;
        }

        //在主界面的情况下，点击底部导航【小视频】按钮，刷新小视频
        mGestureUtil.clickTab(5,1);
        int VideoCount = mFunction.getRandom_10_20();

        while (VideoCount > 0){

            //点击视频的间隔
            int VideoInterval = 6+ mFunction.getRandom_6_12();//3;
            if(mCommonTask.isCloseAppTask()){ break; }

            mGestureUtil.scroll_up_30();

            mToast.success("视频任务:浏览"+VideoInterval+"秒");

            //设置收益的最新时间
            mIncomeTask.setLastIncomeTime();

            if(VideoInterval == 18){
                AccessibilityNodeInfo nodeInfo = AccessibilityHelper.findNodeInfosByText("关注");
                if(nodeInfo != null){
                    mGestureUtil.click(nodeInfo);
                }else {
                    mGestureUtil.doubleClickInScreenCenter();
                }
                mGestureUtil.doubleClickInScreenCenter();
            }
            mFunction.sleep( VideoInterval * 1000);
            VideoCount--;
        }
        return true;
    }

    //-----------------------------------------------------------


    //关闭APP弹出的所有可能弹框
    private void CloseDialog(){
        mGestureUtil.clickByResourceId("com.jm.video:id/imgClose");
        mGestureUtil.clickByText("邀请好友最高可得");
    }

    //过滤广告
    private boolean filterAdvertisement( AccessibilityNodeInfo nodeInfo ){
        if(nodeInfo.getClassName().equals("android.widget.RelativeLayout")){
            return true;
        }

        //资源ID目前测试每个版本都是一样的，暂且先这样
        AccessibilityNodeInfo node = AccessibilityHelper.findNodeInfosById(nodeInfo,"com.xiangzi.jukandian:id/item_artical_ad_three_bd_flag");
        if(node != null){
            RxToast.warning(mGlobal.mNavigationBarActivity,"过滤广告").show();
            return true;
        }
        return false;
    }

    //执行签到任务
    private void SignIn(){
        if(this.IsSign){
            mToast.success("今天已签到");
            mFunction.click_sleep();
            return;
        }
       if(!returnHome()){
           return;
       }
       mGestureUtil.clickTab(5,4);
       this.CloseDialog();

       if(mGestureUtil.clickWebNodeByText("立即签到")){
           this.IsSign = true;
           this.CloseDialog();
           mToast.success("签到成功");
           mFunction.click_sleep();
       }
       AccessibilityNodeInfo nodeInfo = AccessibilityHelper.findWebViewNodeInfosByText("继续赚元宝");
       if(nodeInfo != null){
           this.IsSign = true;
           this.CloseDialog();
           mToast.success("今天已签到");
           mFunction.click_sleep();
       }



    }

    //判断今日的收益是否已经达到最大值
    private Boolean JudgeGoldIncomeIsMax(){
        try{
            if(!returnHome()){
                return false;
            }

            //点击【我的】列表
            mGestureUtil.clickTab(5,5);

            //再次恢复到首页
            if(!returnHome()){
                return false;
            }
            AccessibilityNodeInfo nodeInfo = AccessibilityHelper.findNodeInfosById("com.jm.video:id/tv_gold_num");
            if(nodeInfo != null){
                String incomeText = nodeInfo.getText().toString().trim();
                if(Integer.valueOf(incomeText) > this.TodayMaxIncome){
                    this.TodayIncomeIsFinsh = true;
                    mToast.info("当前收益("+incomeText+")已封顶("+this.TodayMaxIncome+")");
                    mFunction.sleep(mConfig.clickSleepTime);
                    return true;
                }else {
                    mToast.info("当前收益("+incomeText+")未封顶("+this.TodayMaxIncome+")，继续工作");
                    mFunction.sleep(mConfig.clickSleepTime);
                    return false;
                }
            }

        }catch (Exception ex){

        }
        return false;
    }

    //回归到首页，如果APP未打开，则会自行打开
    private boolean returnHome(){
        return returnHome("任务","首页",new Runnable() {
            @Override
            public void run() {
                CloseDialog();
            }
        });
    }
}
