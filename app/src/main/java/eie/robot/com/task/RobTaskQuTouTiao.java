package eie.robot.com.task;

import android.accessibilityservice.AccessibilityService;
import android.accessibilityservice.GestureDescription;
import android.graphics.Path;
import android.graphics.Rect;
import android.icu.lang.UCharacter;
import android.os.Message;
import android.view.accessibility.AccessibilityNodeInfo;

import com.vondear.rxtool.RxDeviceTool;
import com.vondear.rxtool.view.RxToast;

import java.util.List;

import eie.robot.com.accessibilityservice.AccessibilityHelper;
import eie.robot.com.appconfig.IDQuTouTiao;
import eie.robot.com.common.mCommonTask;
import eie.robot.com.common.mConfig;
import eie.robot.com.common.mFunction;
import eie.robot.com.common.mGestureUtil;
import eie.robot.com.common.mGlobal;
import eie.robot.com.common.mIncomeTask;
import eie.robot.com.common.mToast;

import static eie.robot.com.common.mFunction.getRandom_1_20;

public class RobTaskQuTouTiao extends BaseRobotTask {

    public int SizeOffset = 40;

    /**
     * 构造函数
     */
    public RobTaskQuTouTiao() {
        super();
        this.AppName = "趣头条";
        this.TodayMaxIncome = 4200;
        this.TodayIncomeIsFinsh = false;
    }


    /**
     * 执行刷单任务（领取时段奖励、定时刷新视频、查看文章）
     */
    @Override
    public boolean StartTask()  {
        super.StartTask();
        while (mCommonTask.isOpenAppTask()){
            try {
                if(!returnHome()){
                    continue;
                }
                //领取时段奖励
                performTask_ShiDuanJiangLi();

                //判断今日金币是否已经到达
                if(JudgeGoldIncomeIsMax()){
                    break;
                }
                //每次进行一项任务时，都先恢复到首页
                //如果APP未打开，则会自行打开,如果最后还是无法打开，则跳出这次循环，重新来。
                if(!returnHome()){
                    continue;
                }

                //签到
                SignIn();

                mFunction.openScreen();

                //两种刷金币的模式：刷视频、刷新闻。

                //刷视频
                int RefreshCount =   mFunction.getRandom_10_20();

                while (RefreshCount > 0){
                    AccessibilityNodeInfo nodeInfo = AccessibilityHelper.findNodeInfosByText("赚钱玩");
                    if(nodeInfo != null){
                        break;
                    }
                    if(mCommonTask.isCloseAppTask()){ break; }
                    performTask_ShuaXiaoShiPing();
                    RefreshCount -- ;
                }

                //阅读文章
                RefreshCount =   mFunction.getRandom_10_20();
                while (RefreshCount > 0){
                    if(mCommonTask.isCloseAppTask()){ break; }
                    performTask_KanZiXun();
                    RefreshCount -- ;
                }
            }catch (Exception ex){
                RxToast.error(AppName+"出错:"+ex.getMessage());
            }
        }
        super.CloseTask();
        return false;
    }


    /**
     * 执行刷单任务（定时刷小视频）
     */
    private boolean performTask_ShuaXiaoShiPing(){
        //点击视频的间隔
        int VideoInterval = 6+ mFunction.getRandom_6_12();//3;

        if(!returnHome()){
            return false;
        }

        //在主界面的情况下，点击底部导航【小视频】按钮，刷新小视频
        boolean result = mGestureUtil.click(mGlobal.mScreenWidth/2,mGlobal.mScreenHeight-SizeOffset);
        if(result){
            mToast.info("视频任务:阅读"+VideoInterval+"秒");
            //设置收益的最新时间
            mIncomeTask.setLastIncomeTime();

            if(VideoInterval == 18){
                mGestureUtil.doubleClickInScreenCenter();
            }
            mFunction.sleep( VideoInterval * 1000);
            return true;
        }
        return false;
    }

    /**
     * 执行刷单任务（领取时段奖励）
     */
    private boolean performTask_ShiDuanJiangLi(){
        if(!returnHome()){
            return false;
        }
        if(!returnHome()){
            return false;
        }
        // 获取底部导航栏按钮（头条）
        //点击头条列表
        mGestureUtil.click(SizeOffset,mGlobal.mScreenHeight-SizeOffset);

        mToast.success("时段奖励任务");

        /**
         * 第二步：点击【时段奖励】按钮。
         */
        AccessibilityNodeInfo NodeInfo = AccessibilityHelper.findNodeInfosByText("领取");
        if (NodeInfo == null) return false;

        mFunction.sleep(mConfig.clickSleepTime);

        //防止其他地方也有【领取这两个字】
        Rect rect = new Rect();
        NodeInfo.getBoundsInScreen(rect);
        if(rect.top < 200){
            AccessibilityHelper.performClick(NodeInfo);
        }
        mFunction.sleep(mConfig.clickSleepTime);
        return true;
    }

    /**
     * 执行刷单任务（看资讯）
     */
    private boolean performTask_KanZiXun(){
        mToast.info("新闻任务");
        if(!returnHome()){
            return false;
        }
        //点击头条列表
        mGestureUtil.click(SizeOffset,mGlobal.mScreenHeight-SizeOffset);
        mFunction.sleep(mConfig.clickSleepTime);
        int NewsCount =   mFunction.getRandom_4_8();

        while (NewsCount > 0){
            mGestureUtil.scroll_up();
            if(mCommonTask.isCloseAppTask()){ break; }
            Task_KanZiXun();
            if(!returnHome()){
                continue;
            }

            mToast.info("阅读完毕，首页滑动");
            NewsCount -- ;
        }
        //刷资讯
        return true;
    }


    private boolean Task_KanZiXun() {
        AccessibilityNodeInfo nodeInfo = AccessibilityHelper.findNodeInfosByClassName(
                AccessibilityHelper.getRootInActiveWindow()
                ,"android.support.v7.widget.RecyclerView");
        if (nodeInfo == null) {
            return false;
        }
        int CountNews = (nodeInfo.getChildCount()/2);
        mToast.success("阅读当页第"+CountNews+"条新闻");
        if(nodeInfo.getChild(CountNews).getClassName().equals("android.widget.RelativeLayout")){
            return false;
        }
        //点击新闻进行阅读。
        boolean clickResult = mGestureUtil.click(nodeInfo.getChild(CountNews));

        //开始阅读新闻
        if (clickResult) {
            //等待反应
            mFunction.sleep(mConfig.clickSleepTime);
            //过滤广告
            if(filterAdvertisement()){
                return false;
            }
            //滑动次数(随机10到20)
            int SwiperCount = mFunction.getRandom_6_12();
            RxToast.warning(mGlobal.mNavigationBarActivity,SwiperCount+"次").show();
            //开始滑动文章
            while (true) {
                if (SwiperCount < 1) {
                    break;
                }
                if(mCommonTask.isCloseAppTask()){ break; }
                //过滤政治文章
                AccessibilityNodeInfo info = AccessibilityHelper.findNodeInfosByText(AccessibilityHelper.getRootInActiveWindow(),"根据平台规则，阅读时政类资讯不可领取金币");
                if(info != null){
                    break;
                }

                //
                info = AccessibilityHelper.findNodeInfosByText(AccessibilityHelper.getRootInActiveWindow(),"小视频");
                if(info != null){
                    break;
                }
                //判断是否处于文章页，如果不是则退出
                AccessibilityNodeInfo XinWenNode = AccessibilityHelper.findNodeInfosByText("我来说两句...");
                if(XinWenNode == null){
                    break;
                }
                //设置收益的最新时间
                mIncomeTask.setLastIncomeTime();
                //向上滑动
                mGestureUtil.scroll_up();

                //停止进行阅读
                int sleepTime = mFunction.getRandom_4_8();
                mFunction.sleep(sleepTime * 1000);
                SwiperCount--;
            }
        }

        //阅读完返回
        AccessibilityHelper.performBack(mGlobal.mAccessibilityService);

        mFunction.sleep(mConfig.clickSleepTime);

        return true;
    }

    //过来广告
    private boolean filterAdvertisement(){
        AccessibilityNodeInfo nodeInfo = AccessibilityHelper.findNodeInfosByText("关闭");
        if(nodeInfo == null){
            return false;
        }
        RxToast.warning(mGlobal.mNavigationBarActivity,"过滤广告").show();
        return AccessibilityHelper.performClick(nodeInfo);
    }

    //关闭APP弹出的所有可能弹框
    private void CloseDialog(){

        //开始通知的弹框
        AccessibilityNodeInfo node = AccessibilityHelper.findNodeInfosByText("每天签到不错过，连续签到，送 7500+ 金币");
        if(node != null){
            if(node.getClassName().equals("android.widget.TextView")){
                node = node.getParent();
                if(node != null){
                    Rect rect = new Rect();
                    node.getBoundsInScreen(rect);
                    float x = rect.left + rect.width() - SizeOffset;
                    float y = rect.top + SizeOffset;
                    mGestureUtil.click(x,y);
                }
            }
            AccessibilityHelper.performClick(node);
        }
        //判断是否处于弹框，但是却无法利用【返回键】取消的状态
        node = AccessibilityHelper.getRootInActiveWindow();
        if(node != null){
            Rect rect = new Rect();
            node.getBoundsInScreen(rect);
            int viewHeight = rect.height();
            if(viewHeight < mGlobal.mScreenHeight-100){
                mGestureUtil.click(mGlobal.mScreenWidth/2,mGlobal.mScreenHeight/2);
            }
        }


    }

    /**
     * 执行签到任务
     */
    private void SignIn(){
        mToast.success("签到任务");
        AccessibilityNodeInfo nodeInfo = AccessibilityHelper.findNodeInfosByText(
                AccessibilityHelper.getRootInActiveWindow(),"去签到");
        if(nodeInfo != null){
            AccessibilityHelper.performClick(nodeInfo);
        }
        mFunction.sleep(mConfig.clickSleepTime);
    }


    /**
     * 判断今日的收益是否已经达到最大值
     */
    private Boolean JudgeGoldIncomeIsMax(){
        if(!returnHome()){
            return false;
        }
        //点击【我的】列表
        mGestureUtil.click(mGlobal.mScreenWidth-SizeOffset,mGlobal.mScreenHeight-SizeOffset);
        mToast.info("判断今日收益是否封顶");
        mFunction.click_sleep();
        //再次恢复到首页
        if(!returnHome()){
            return false;
        }

        try{
            AccessibilityNodeInfo nodeInfo = AccessibilityHelper.findNodeInfosByText("今日金币");
            if(nodeInfo != null){
                String incomeText = nodeInfo.getText().toString().trim();
                incomeText = incomeText.replaceAll("今日金币","").trim();
                if(Integer.valueOf(incomeText) > this.TodayMaxIncome){
                    this.TodayIncomeIsFinsh = true;
                    mToast.success("今日收益("+incomeText+")已封顶("+this.TodayMaxIncome+")");
                    mFunction.sleep(mConfig.clickSleepTime);
                    return true;
                }else {
                    mToast.success("今日收益("+incomeText+")未封顶("+this.TodayMaxIncome+")，继续工作");
                    mFunction.sleep(mConfig.clickSleepTime);
                    return false;
                }
            }
        }catch (Exception ex){

        }
        return false;
    }


    /**
     * 回归到首页，如果APP未打开，则会自行打开
     * @return
     */
    private boolean returnHome(){
       return super.returnHome("我的","任务",new Runnable() {
            @Override
            public void run() {
                CloseDialog();
            }
        });

    }
}
