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

import butterknife.internal.ListenerClass;
import eie.robot.com.accessibilityservice.AccessibilityHelper;
import eie.robot.com.appconfig.IDQuTouTiao;
import eie.robot.com.common.mCommonFunctionTask;
import eie.robot.com.common.mCommonTask;
import eie.robot.com.common.mConfig;
import eie.robot.com.common.mFunction;
import eie.robot.com.common.mGestureUtil;
import eie.robot.com.common.mGlobal;
import eie.robot.com.common.mIncomeTask;
import eie.robot.com.common.mToast;

import static eie.robot.com.common.mFunction.getRandom_1_20;

public class RobTaskQuTouTiao extends BaseRobotTask {



    //构造函数
    public RobTaskQuTouTiao() {
        super();
        this.AppName = "趣头条";
        this.TodayMaxIncome = 3999;
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

                //领取时段奖励
                performTask_TimeSlotReward();

                //判断今日金币是否已经到达
                if(JudgeGoldIncomeIsMax()){
                    break;
                }

                //签到
                SignIn();

                if(mFunction.getRandomBooleanOffsetTrue()){
                    //看新闻
                    performTask_LookNews();
                }else {
                    if(mFunction.getRandomBoolean()){
                        //刷列表视频
                        performTask_WatchVideo_list();
                    }else {
                        //刷小视频
                        performTask_WatchVideo();
                    }

                }

            }catch (Exception ex){
                RxToast.error(AppName+"出错:"+ex.getMessage());
            }
        }
        super.CloseTask();
        return false;
    }

    //-----------------------------------------------------------

    //看新闻总任务
    @Override
    Boolean performTask_LookNews(){
        //阅读文章
        int RefreshCount =   mFunction.getRandom_2_4();
        while (RefreshCount > 0){
            if(mCommonTask.isCloseAppTask()){ break; }
            performTask_LookNews_1();
            RefreshCount -- ;
        }
        return false;
    }

    //看新闻子任务一
    private boolean performTask_LookNews_1(){
        if(!returnHome()){
            return false;
        }

        mToast.success("新闻任务");
        //点击头条列表
        mGestureUtil.clickTab(5,1);

        this.CloseDialog();

        int NewsCount =   mFunction.getRandom_4_8();

        while (NewsCount > 0){
            mGestureUtil.scroll_up();
            if(mCommonTask.isCloseAppTask()){ break; }
            performTask_LookNews_2();
            if(!returnHome()){
                continue;
            }
            mToast.success("文章阅读完毕，首页滑动");
            NewsCount -- ;
        }
        return true;
    }

    //看新闻子任务二
    private boolean performTask_LookNews_2()  {
        try{
            AccessibilityNodeInfo nodeInfo = AccessibilityHelper.findNodeInfosByClassName("android.support.v7.widget.RecyclerView");
            if (nodeInfo == null || nodeInfo.getChildCount() < 1) {
                return false;
            }

            int loopCounter = nodeInfo.getChildCount();
            while (loopCounter > 0){
                loopCounter --;
                AccessibilityNodeInfo node = nodeInfo.getChild(loopCounter);
                if(node == null) continue;

                //过滤掉看视频
                if(node.getChildCount()>1 && node.getChild(1).getClassName().equals("android.widget.RelativeLayout")){
                    continue;
                }

                //过滤广告
                if(filterAdvertisement(node)){
                    continue;
                }

                Boolean clickResult = mGestureUtil.performClick(node);
                //开始阅读新闻
                if (clickResult){
                    //等待反应
                    mFunction.sleep(mConfig.clickSleepTime);
                    //滑动次数(随机10到20)
                    int SwiperCount = mFunction.getRandom_6_12();
                    mToast.success("阅读文章：滑动"+SwiperCount+"次");

                    while (SwiperCount > 0){
                        if(mCommonTask.isCloseAppTask()){ break; }
                        if(mCommonFunctionTask.judgeNodeIsHavingByText("小视频")){ break; }

                        if(!mCommonFunctionTask.judgeNodeIsHavingByText("评论得赏金")
                        && !mCommonFunctionTask.judgeNodeIsHavingByText("我来说两句...")) break;

                        //设置收益的最新时间
                        mIncomeTask.setLastIncomeTime();
                        //向上滑动
                        mGestureUtil.scroll_up();

                        //停止进行阅读
                        int sleepTime = mFunction.getRandom_4_8()-2;
                        mFunction.sleep(sleepTime * 1000);

                        SwiperCount--;
                    }

                    if(mFunction.getRandomBoolean()){
                        LookNewsSendingComment();
                    }

                    //阅读完返回
                    AccessibilityHelper.performBack();
                    mFunction.click_sleep();
                }
            }
        }catch (Exception x){

        }


        return true;
    }

    //看新闻输入评论
    private boolean LookNewsSendingComment(){
        try{
            AccessibilityNodeInfo nodeInfo = AccessibilityHelper.findNodeInfosByClassName("android.support.v7.widget.RecyclerView");
            if(nodeInfo == null) return false;

            String CommentText = nodeInfo.getChild(1).getChild(0).getChild(1).getChild(0).getText().toString();
            if(CommentText.isEmpty())return false;

            mGestureUtil.clickByText("1金币");

            mGestureUtil.clickByText("评论得赏金");

            nodeInfo = AccessibilityHelper.findNodeInfosByClassName("android.widget.EditText");
            if(nodeInfo == null) return false;

            mCommonFunctionTask.pasteTextToNode(nodeInfo,CommentText);

            mGestureUtil.clickByText("立即评论");

        }catch (Exception ex){

        }
        return false;
    }

    //-----------------------------------------------------------

    //看列表视频总任务
    Boolean performTask_WatchVideo_list(){

        mGestureUtil.clickTab(5,2);
        int RefreshCount =   mFunction.getRandom_4_8();
        while (RefreshCount > 0){
            if(mCommonTask.isCloseAppTask()){ break; }
            mGestureUtil.clickTab(5,2);

            performTask_WatchVideo_list_1();

            mGestureUtil.scroll_up();
            mGestureUtil.scroll_up();
            RefreshCount -- ;
        }
        return false;
    }

    //看列表视频子任务一
    private boolean performTask_WatchVideo_list_1(){

        List<AccessibilityNodeInfo> node = AccessibilityHelper.findNodeInfoByText(":");
        if(node == null) return false;

        int loopCounter = node.size();
        while (loopCounter >0 ){
            loopCounter--;
            if(filterAdvertisement(node.get(loopCounter))){
                continue;
            }
            if(mGestureUtil.clickByCoordinate(node.get(loopCounter))){
                //设置收益的最新时间
                mIncomeTask.setLastIncomeTime();
                mFunction.sleep(mFunction.getRandom_6_12()*3000);
            }
        }
        return true;
    }

    //-----------------------------------------------------------

    //看视频总任务
    @Override
    Boolean performTask_WatchVideo(){
        int RefreshCount =   mFunction.getRandom_4_8();
        while (RefreshCount > 0){
            AccessibilityNodeInfo nodeInfo = AccessibilityHelper.findNodeInfosByText("赚钱玩");
            if(nodeInfo != null){
                break;
            }
            if(mCommonTask.isCloseAppTask()){ break; }
            performTask_WatchVideo_1();
            RefreshCount -- ;
        }
        return false;
    }

    //看视频子任务一
    private boolean performTask_WatchVideo_1(){
        //点击视频的间隔
        int VideoInterval = 6+ mFunction.getRandom_6_12();//3;

        if(!returnHome()){
            return false;
        }

        //在主界面的情况下，点击底部导航【小视频】按钮，刷新小视频
        mGestureUtil.clickTab(5,3);

        mToast.info("视频任务:阅读"+VideoInterval+"秒");

        //设置收益的最新时间
        mIncomeTask.setLastIncomeTime();
        if(VideoInterval == 18){
            mGestureUtil.doubleClickInScreenCenter();
        }
        mFunction.sleep( VideoInterval * 1000);

        if(mFunction.getRandomBoolean()){
            VideoSendingComment();
        }
        return true;
    }

    //小视频输入评论
    private boolean VideoSendingComment(){
        try{

            AccessibilityNodeInfo nodeInfo = AccessibilityHelper.findNodeInfosByClassName("android.support.v7.widget.RecyclerView");
            if(nodeInfo == null) return false;

            nodeInfo = nodeInfo.getChild(0).getChild(4).getChild(0).getChild(1);
            if(nodeInfo == null) return false;

            mGestureUtil.click(nodeInfo);

            if(!mCommonFunctionTask.judgeNodeIsHavingByText("条评论")) return false;

            mGestureUtil.clickByText("1金币");

            nodeInfo = AccessibilityHelper.findNodeInfosByClassName("android.support.v7.widget.RecyclerView");
            if(nodeInfo == null) return false;

            String CommentText = nodeInfo.getChild(1).getChild(0).getChild(1).getChild(0).getText().toString();
            if(CommentText.isEmpty())return false;

            mGestureUtil.clickByText("评论得赏金");

            nodeInfo = AccessibilityHelper.findNodeInfosByClassName("android.widget.EditText");
            if(nodeInfo == null) return false;

            mCommonFunctionTask.pasteTextToNode(nodeInfo,CommentText);

            mGestureUtil.clickByText("立即评论");

            AccessibilityHelper.performBack();
        }catch (Exception ex){

        }
        return false;
    }


    //-----------------------------------------------------------


    //执行刷单任务（领取时段奖励）
    private boolean performTask_TimeSlotReward(){
        if(!returnHome()){
            return false;
        }

        if(!returnHome()){
            return false;
        }
        // 获取底部导航栏按钮（头条）
        //点击头条列表
        mGestureUtil.clickTab(5,1);
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
            mGestureUtil.performClick(NodeInfo);
        }
        mFunction.sleep(mConfig.clickSleepTime);
        return true;
    }

    //过滤广告
    private boolean filterAdvertisement(AccessibilityNodeInfo nodeInfo){

        AccessibilityNodeInfo node = AccessibilityHelper.findChildNodeInfosByText(nodeInfo,"广告");
        if(node != null){
            return true;
        }

        return false;
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

    //执行签到任务
    private void SignIn(){
        mToast.success("签到任务");
        mGestureUtil.clickTab(5,4);
    }

    //判断今日的收益是否已经达到最大值
    private Boolean JudgeGoldIncomeIsMax(){

        if(!returnHome()){
            return false;
        }
        //点击【我的】列表
        mGestureUtil.clickTab(5,5);

        //再次恢复到首页
        if(!returnHome()){
            return false;
        }
        try{

            AccessibilityNodeInfo nodes = AccessibilityHelper.findNodeInfosByText("今日阅读");
            if(nodes != null && nodes.getParent() != null && nodes.getParent().getChildCount() > 1){
                String ReadTime = nodes.getParent().getChild(0).getText().toString().trim();
                if(Float.valueOf(ReadTime) > 60 ){
                    this.TodayIncomeIsFinsh = true;
                    mToast.success("今日阅读时间过长("+ReadTime+")，结束工作");
                    mFunction.sleep(mConfig.clickSleepTime);
                    return true;
                }
            }

            AccessibilityNodeInfo nodeInfo = AccessibilityHelper.findNodeInfosByText("今日金币");
            if(nodeInfo != null){
                String incomeText = nodeInfo.getText().toString().trim();
                incomeText = incomeText.replaceAll("今日金币","").trim();
                if(Integer.valueOf(incomeText) > this.TodayMaxIncome){
                    AccessibilityNodeInfo node = AccessibilityHelper.findNodeInfosByEqualText("今日阅读");
                    if(node != null && node.getParent() != null && node.getParent().getChildCount() > 1){
                        String ReadTime = node.getParent().getChild(0).getText().toString().trim();
                        if(Float.valueOf(ReadTime) < 60 ){
                            this.TodayIncomeIsFinsh = false;
                            mToast.success("今日收益("+incomeText+")已封顶("+this.TodayMaxIncome+"),但阅读时间未封顶("+ReadTime+")，继续工作");
                            mFunction.sleep(mConfig.clickSleepTime);
                            return false;
                        }
                    }

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

    //回归到首页，如果APP未打开，则会自行打开
    private boolean returnHome(){
       return super.returnHome("我的","任务",new Runnable() {
            @Override
            public void run() {
                CloseDialog();
            }
        });

    }



}
