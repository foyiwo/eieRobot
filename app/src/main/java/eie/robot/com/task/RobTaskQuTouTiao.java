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
    }


    /**
     * 执行刷单任务（领取时段奖励、定时刷新视频、查看文章）
     */
    @Override
    public boolean StartTask()  {
        super.StartTask();
        mCommonTask.AppTaskOpenStatus = true;
        while (mCommonTask.AppTaskOpenStatus){
            try {
                //每次进行一项任务时，都先恢复到首页
                //如果APP未打开，则会自行打开,如果最后还是无法打开，则跳出这次循环，重新来。
                if(!returnHome()){
                    continue;
                }

                //签到
                SignIn();
                //领取时段奖励
                performTask_ShiDuanJiangLi();

                mFunction.openScreen();

                //两种刷金币的模式：刷视频、刷新闻。

                //刷视频
                int RefreshCount =   mFunction.getRandom_10_20();
                while (RefreshCount > 0){
                    if(!mCommonTask.AppTaskOpenStatus){ break; }
                    performTask_ShuaXiaoShiPing();
                    RefreshCount -- ;
                }

                //阅读文章
                RefreshCount =   mFunction.getRandom_10_20()+10;
                while (RefreshCount > 0){
                    if(!mCommonTask.AppTaskOpenStatus){ break; }
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

        //在主界面的情况下，点击底部导航【小视频】按钮，刷新小视频
        boolean result = mGestureUtil.click(mGlobal.mScreenWidth/2,mGlobal.mScreenHeight-SizeOffset);
        if(result){
            mToast.info("视频任务:阅读"+VideoInterval+"秒");
            mFunction.sleep( VideoInterval * 1000);
            return true;
        }
        return false;
    }

    /**
     * 执行刷单任务（领取时段奖励）
     */
    private boolean performTask_ShiDuanJiangLi(){

        mToast.success("时段奖励任务");
        // 获取底部导航栏按钮（头条）
        //点击头条列表
        mGestureUtil.click(SizeOffset,mGlobal.mScreenHeight-SizeOffset);


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
        mToast.info("资讯任务");
        if(!returnHome()){
            return false;
        }
        //点击头条列表
        mGestureUtil.click(SizeOffset,mGlobal.mScreenHeight-SizeOffset);
        mGestureUtil.scroll_up();
        mFunction.sleep(mConfig.clickSleepTime);
        int NewsCount =   mFunction.getRandom_4_8();
        while (NewsCount > 0){
            if(!mCommonTask.AppTaskOpenStatus){break;}
            Task_KanZiXun();
            if(!returnHome()){
                continue;
            }
            mGestureUtil.scroll_up();
            NewsCount -- ;
        }
        //刷资讯
        return true;
    }


    private boolean Task_KanZiXun() {
        AccessibilityNodeInfo nodeInfo = AccessibilityHelper.findNodeInfosByClassName(
                mGlobal.mAccessibilityService.getRootInActiveWindow()
                ,"android.support.v7.widget.RecyclerView");
        if (nodeInfo == null) {
            return false;
        }
        int CountNews = (nodeInfo.getChildCount()/2);
        if(nodeInfo.getChild(CountNews).getClassName().equals("android.widget.RelativeLayout")){
            return false;
        }
        //点击新闻进行阅读。
        boolean clickResult = AccessibilityHelper.performClick(nodeInfo.getChild(CountNews));

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
                //过滤政治文章
                AccessibilityNodeInfo info = AccessibilityHelper.findNodeInfosByText(mGlobal.mAccessibilityService.getRootInActiveWindow(),"根据平台规则，阅读时政类资讯不可领取金币");
                if(info != null){
                    break;
                }
                //
                info = AccessibilityHelper.findNodeInfosByText(mGlobal.mAccessibilityService.getRootInActiveWindow(),"小视频");
                if(info != null){
                    break;
                }

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

    /**
     * 执行签到任务
     */
    private void SignIn(){
        mToast.success("签到任务");
        AccessibilityNodeInfo nodeInfo = AccessibilityHelper.findNodeInfosByText(
                mGlobal.mAccessibilityService.getRootInActiveWindow(),"去签到");
        if(nodeInfo != null){
            AccessibilityHelper.performClick(nodeInfo);
        }
        mFunction.sleep(mConfig.clickSleepTime);
    }

    /**
     * 回归到首页，如果APP未打开，则会自行打开
     * @return
     */
    @Override
    public boolean returnHome(){
        if(!super.returnHome()){
            return false;
        }
        if(!mFunction.loopOpenApp(AppName)){
            return false;
        }

        //确定已经打开应用之后，下面确定是否处于首页。

        //获取底部导航栏的图标
        AccessibilityNodeInfo NodeInfo1 = AccessibilityHelper.loopFindNodeInfoByText("小视频");
        AccessibilityNodeInfo NodeInfo2 = AccessibilityHelper.loopFindNodeInfoByText("我的");

        if ( NodeInfo1 != null || NodeInfo2 != null ) {
            return true;
        } else {
            //到此，虽然不是主界面，但却是处于打开状态，目前可能是处于，内页，至于哪个内页，无法确定，
            //采取触发返回键的方式。
            int count = mConfig.loopCount;
            while (true) {
                AccessibilityHelper.performBack(mGlobal.mAccessibilityService);
                //停一下，等待反应
                mFunction.sleep(mConfig.loopSleepTime);
                NodeInfo1 = AccessibilityHelper.loopFindNodeInfoByText("小视频");
                NodeInfo2 = AccessibilityHelper.loopFindNodeInfoByText("我的");
                if ( NodeInfo1 != null || NodeInfo2 != null ) {
                    break;
                }
                count--;
                if (count < 0) {
                    break;
                }
            }
            if (NodeInfo1 != null || NodeInfo2 != null) {
                return true;
            } else {
                return false;
            }
        }
    }
}
