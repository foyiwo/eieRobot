package eie.robot.com.task;

import android.graphics.Rect;
import android.view.View;
import android.view.accessibility.AccessibilityNodeInfo;

import com.vondear.rxtool.view.RxToast;

import java.util.List;

import eie.robot.com.accessibilityservice.AccessibilityHelper;
import eie.robot.com.common.mCommonFunctionTask;
import eie.robot.com.common.mCommonTask;
import eie.robot.com.common.mConfig;
import eie.robot.com.common.mFunction;
import eie.robot.com.common.mGestureUtil;
import eie.robot.com.common.mGlobal;
import eie.robot.com.common.mIncomeTask;
import eie.robot.com.common.mToast;
import eie.robot.com.common.mUploadDataUtil;

public class RobTaskShuaBao extends BaseRobotTask {

    public int SizeOffset = 40;

    //构造函数
    public RobTaskShuaBao() {
        super();
        this.AppName = "刷宝短视频";
        String packname = mFunction.GetAppPackageName(this.AppName);
        if(packname.isEmpty()){
            this.AppName = "刷宝";
        }

        this.TodayMaxIncome = 9999;
        this.TodayIncomeIsFinsh = false;
    }


    //执行刷单任务（领取时段奖励、定时刷新视频、查看文章）
    @Override
    public boolean StartTask()  {
        super.StartTask();
        try {
            while (mCommonTask.isOpenAppTask()){

                mCommonFunctionTask.OpenWIFI();

                if(!returnHome()){
                    continue;
                }

                //签到
                SignIn();

                //判断收益是否封顶
                if(JudgeGoldIncomeIsMax()){
                    break;
                }

                this.TodayIncomeIsFinsh = true;
                if(this.TodayIncomeIsFinsh){
                    break;
                }

                if(!JudgeGoldIncomeIsLegal()){
                    break;
                }




                //看视频
                this.performTask_WatchVideo();

                if(mCommonTask.isCloseAppTask()){ break; }

            }
            JudgeGoldIncomeIsMax();
            super.CloseTask();
        }catch (Exception ex){
            RxToast.error(ex.getMessage());
        }

        return false;
    }

    //-----------------------------------------------------------

    //看视频总任务
    @Override
    Boolean performTask_WatchVideo(){
        int RefreshCount =   mFunction.getRandom_4_8();
        while (RefreshCount > 0){
            if(mCommonTask.isCloseAppTask()){ break; }

            //在主界面的情况下，点击底部导航【小视频】按钮，刷新小视频
            mGestureUtil.clickTab(5,1);

            if(!returnHome()){
                return false;
            }

            performTask_WatchVideo_1();
            RefreshCount -- ;
        }
        return false;
    }

    //执行刷单任务（定时刷小视频）
    private boolean performTask_WatchVideo_1(){

        int VideoCount = mFunction.getRandom_2_4();

        while (VideoCount > 0){

            //点击视频的间隔
            int VideoInterval = 6+ mFunction.getRandom_6_12();

            if(mCommonTask.isCloseAppTask()){ break; }

            mGestureUtil.scroll_up_30();

            mToast.success("视频任务:浏览"+VideoInterval+"秒");

            //设置收益的最新时间
            mIncomeTask.setLastIncomeTime();

            if(VideoInterval >= 17){
                AccessibilityNodeInfo nodeInfo = AccessibilityHelper.findNodeInfosByText("关注");
                if(nodeInfo != null){
                    mGestureUtil.click(nodeInfo);
                }else {
                    mGestureUtil.clickByResourceId("com.jm.video:id/praise");
                }
                mGestureUtil.clickByResourceId("com.jm.video:id/praise");

                LookOverVideoAuthorHome();
            }
            if(mCommonFunctionTask.judgeNodeIsHavingByText("衣服")){
                mGestureUtil.clickByText("关注");
                mGestureUtil.doubleClickInScreenCenter();
            }

            if(mFunction.getRandomBooleanOffsetFalse()){
                VideoSendingComment();
            }

            if(mFunction.getRandomBooleanOffsetFalse()){
                WeChatShare();
            }

            LookOverIncome();

            mFunction.sleep( VideoInterval * 1000);
            VideoCount--;
        }
        LookOverIncome();
        returnHome();
        return true;
    }

    //小视频输入评论
    private boolean VideoSendingComment(){
        try{

            mGestureUtil.clickByResourceId("com.jm.video:id/comment");

            if(!mCommonFunctionTask.judgeNodeIsHavingByText("条评论")) return false;

            mGestureUtil.scroll_up();

            mGestureUtil.scroll_up();

            mGestureUtil.clickByResourceId("com.jm.video:id/iv_prise_btn");

            AccessibilityNodeInfo nodeInfo = AccessibilityHelper.findNodeInfosByClassName("android.support.v7.widget.RecyclerView");
            if(nodeInfo == null) return false;

            String CommentText = nodeInfo.getChild(1).getChild(0).getChild(2).getText().toString();
            if(CommentText.isEmpty()) return false;

            mGestureUtil.clickByText("我也说几句");

            nodeInfo = AccessibilityHelper.findNodeInfosByClassName("android.widget.EditText");
            if(nodeInfo == null) return false;

            mCommonFunctionTask.pasteTextToNode(nodeInfo,CommentText);

            mGestureUtil.click(mGlobal.mScreenWidth-SizeOffset,mGlobal.mScreenHeight-SizeOffset);

            AccessibilityHelper.performBack();

            returnHome();
        }catch (Exception ex){

        }
        return false;
    }

    //-----------------------------------------------------------


    //微信分享
    private boolean WeChatShare(){
        try{
            if(!returnHome()){
                return false;
            }
            mGestureUtil.clickByResourceId("com.jm.video:id/share");

            if(!mCommonFunctionTask.judgeNodeIsHavingByText("分享到")) return false;

            mGestureUtil.clickByText("微信好友");

            mFunction.click_sleep();

            AccessibilityHelper.performBack();

            returnHome();
        }catch (Exception ex){

        }
        return true;
    }

    //看作者的主页
    private boolean LookOverVideoAuthorHome(){

        try{
            if(!returnHome()){
                return false;
            }

            mGestureUtil.clickByResourceId("com.jm.video:id/name");

            String nodeInfoText = AccessibilityHelper.getNodeInfosTextByText("作品");

            nodeInfoText = nodeInfoText.replace("作品","").trim();

            if(Integer.valueOf(nodeInfoText) > 0){
                mGestureUtil.clickByResourceId("com.jm.video:id/iv_cover");

                int VideoCount = mFunction.getRandom_6_12();
                if(VideoCount >= Integer.valueOf(nodeInfoText)){
                    VideoCount = Integer.valueOf(nodeInfoText);
                }

                while (VideoCount > 0){

                    //点击视频的间隔
                    int VideoInterval = 6+ mFunction.getRandom_6_12();

                    if(mCommonTask.isCloseAppTask()){ break; }

                    mGestureUtil.scroll_up_30();

                    mToast.success("视频任务:浏览"+VideoInterval+"秒");

                    //设置收益的最新时间
                    mIncomeTask.setLastIncomeTime();

                    if(VideoInterval >= 17){
                        AccessibilityNodeInfo nodeInfo = AccessibilityHelper.findNodeInfosByText("关注");
                        if(nodeInfo != null){
                            mGestureUtil.click(nodeInfo);
                        }else {
                            mGestureUtil.clickByResourceId("com.jm.video:id/praise");
                        }
                        mGestureUtil.clickByResourceId("com.jm.video:id/praise");
                    }

                    LookOverIncome();

                    mFunction.sleep( VideoInterval * 1000);
                    VideoCount--;
                }

                returnHome();
            }
        }catch (Exception ex){

        }
        return true;
    }

    private void LookOverIncome(){
        try{
            if(!returnHome()){
                return;
            }

            if(mCommonFunctionTask.judgeNodeIsHavingByResId("com.jm.video:id/frame_box_open_new")){
                mGestureUtil.clickByResourceId("com.jm.video:id/frame_box_open_new");
                mFunction.click_sleep();
                mGestureUtil.clickByResourceId("com.jm.video:id/view_gold");

                int LoopCounter = 15;
                while (LoopCounter > 0){
                    if(mCommonFunctionTask.judgeNodeIsHavingByResId("com.jm.video:id/tt_video_ad_close")){
                        mGestureUtil.clickByResourceId("com.jm.video:id/tt_video_ad_close");
                        returnHome();
                    }
                    LoopCounter--;
                    mFunction.click_sleep();
                }
            }
        }catch (Exception ex){

        }
    }



    //关闭APP弹出的所有可能弹框
    private void CloseDialog(){
        AccessibilityNodeInfo node = AccessibilityHelper.findNodeInfosByText("首页");
        if(node != null){
            Rect rect = new Rect();
            node.getBoundsInScreen(rect);
            if(rect.right < 0 || rect.right > mGlobal.mScreenWidth){
                AccessibilityHelper.performBack();
            }
        }


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

            UploadIncome();

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

    private Boolean JudgeGoldIncomeIsLegal(){
        try{
            if(!returnHome()){ return false; }

            //在主界面的情况下，点击底部导航【小视频】按钮，刷新小视频
            mGestureUtil.clickTab(5,1);

            if(!returnHome()){
                return false;
            }

            //在主界面的情况下，点击底部导航【小视频】按钮，刷新小视频
            mGestureUtil.clickTab(5,1);

            if(!returnHome()){
                return false;
            }

            performTask_WatchVideo_1();

            AccessibilityNodeInfo currentNodeInfo = AccessibilityHelper.findNodeInfosByText("当前元宝(个)");
            if(currentNodeInfo != null){
                mGestureUtil.click(currentNodeInfo);
                mFunction.click_sleep();
                mFunction.click_sleep();

                AccessibilityNodeInfo node = AccessibilityHelper.findWebViewNodeInfosByText("+1");
                if(node != null){
                    this.TodayIncomeIsFinsh = true;
                    mToast.success("账号被封，停止工作");
                    return false;
                }
            }
        }catch (Exception ex){

        }

        return true;
    }

    //上传APP的最新收益情况
    @Override
    Boolean UploadIncome(){

        try{
            if(!mCommonFunctionTask.judgeNodeIsHavingByText("当前余额(元)")){
                if(!returnHome()){
                    return false;
                }

                mGestureUtil.clickTab(5,5);

                if(!returnHome()){
                    return false;
                }

                mGestureUtil.scroll_down_half_screen();

                if(!returnHome()){
                    return false;
                }
            }

            float cash = 0;
            AccessibilityNodeInfo cash_item = AccessibilityHelper.findNodeInfosById("com.jm.video:id/tv_mine_money");
            if(cash_item != null){
                String coinString = cash_item.getText().toString();
                cash = Float.valueOf(coinString);
            }

            float coin = 0;
            AccessibilityNodeInfo money_item = AccessibilityHelper.findNodeInfosById("com.jm.video:id/tv_gold_num");
            if(money_item != null){
                String coinString = money_item.getText().toString();
                coin = Float.valueOf(coinString)/10000;
            }

            float rmd = cash + coin ;
            mUploadDataUtil.postIncomeRecord(this.AppName,rmd);
            mToast.success("收益上传:"+rmd);
        }catch (Exception ex){

        }


        return true;
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
