package eie.robot.com.task;

import android.graphics.Rect;
import android.view.accessibility.AccessibilityNodeInfo;

import java.util.List;

import eie.robot.com.accessibilityservice.AccessibilityHelper;
import eie.robot.com.common.mCommonFunctionTask;
import eie.robot.com.common.mCommonTask;
import eie.robot.com.common.mConfig;
import eie.robot.com.common.mDeviceUtil;
import eie.robot.com.common.mFunction;
import eie.robot.com.common.mGestureUtil;
import eie.robot.com.common.mGlobal;
import eie.robot.com.common.mIncomeTask;
import eie.robot.com.common.mToast;
import eie.robot.com.common.mUploadDataUtil;

public class RobTaskTaoTouTiao extends BaseRobotTask {

    //构造函数
    public RobTaskTaoTouTiao() {
        super();
        this.AppName = "淘头条";
        this.TodayMaxIncome = 4000;
        this.TodayIncomeIsFinsh = false;
    }

    //执行刷单任务（领取时段奖励、定时刷新视频、查看文章）
    @Override
    public boolean StartTask()  {
        try{
            super.StartTask();

            while (mCommonTask.isOpenAppTask()){
                try {
                    //每次进行一项任务时，都先恢复到首页
                    //如果APP未打开，则会自行打开,如果最后还是无法打开，则跳出这次循环，重新来。
                    if(!returnHome()){
                        continue;
                    }
                    //上传收益数据
                    UploadIncome();

                    //领取时段奖励
                    performTask_TimeSlotReward();

                    //判断收益是否封顶
                    if(JudgeGoldIncomeIsMax()){
                        break;
                    }

                    //判断收益是否封顶（每次重启的时候查一次）
                    this.TaskCounter = this.TaskCounterDefaultValue;


                    if(mFunction.getRandomBooleanOffsetTrue() || this.VideoIsFinish){
                        //阅读文章
                        performTask_LookNews();
                    }else {
                        //看视频
                        performTask_WatchVideo();
                    }

                }
                catch (Exception ex){
                    mToast.error(ex.getMessage());
                }
            }
            //上传数据
            UploadIncome();
            super.CloseTask();

        }catch (Exception ex){

        }
        return false;
    }

    //-----------------------------------------------------------

    //看视频总任务
    @Override
    Boolean performTask_WatchVideo(){
        super.performTask_WatchVideo();

        int RefreshCount =  mFunction.getRandom_2_4();
        while (RefreshCount > 0){
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
        mGestureUtil.clickTab(5,2);

        mToast.info("视频任务:阅读"+VideoInterval+"秒");

        //设置收益的最新时间
        mIncomeTask.setLastIncomeTime();

        if(VideoInterval >= 17){
            mGestureUtil.clickByResourceId("com.ly.taotoutiao:id/like_iv");
        }
        mFunction.sleep( VideoInterval * 1000);

        if(true){
            VideoWxChatShare();
        }
        return true;
    }

    //小视频输入评论
    private boolean VideoWxChatShare(){
        try{
            if(mGestureUtil.clickByResourceId("com.ly.taotoutiao:id/share_ll")){
                mFunction.click_sleep();
                if(mGestureUtil.clickByResourceId("com.ly.taotoutiao:id/ll_wchat")){
                    mFunction.click_sleep();
                    AccessibilityHelper.performBack();
                    mFunction.sleep(4*1000);
                    AccessibilityHelper.performBack();
                    mFunction.sleep(4*1000);
                }
            }
        }catch (Exception ex){

        }
        return false;
    }

    //-----------------------------------------------------------

    //看新闻总任务
    @Override
    Boolean performTask_LookNews(){
        int RefreshCount =   mFunction.getRandom_2_4();
        while (RefreshCount > 0){
            //判断收益
            if(mCommonTask.isCloseAppTask() || this.ArticleIsFinish){ break;}
            performTask_LookNews_1();
            mToast.success("倒数第"+RefreshCount+"轮新闻任务");
            mFunction.click_sleep();
            RefreshCount -- ;
        }
        return false;
    }

    //看新闻子任务一
    private boolean performTask_LookNews_1(){
        if(!returnHome()){
            return false;
        }

        //点击第一个功能列表
        mGestureUtil.clickTab(5,1);

        //随机获取在本首页的滑动的次数
        int NewsCount = mFunction.getRandom_4_8();

        while (NewsCount > 0){

            if(mCommonTask.isCloseAppTask()){ break; }

            //领取时段奖励
            performTask_TimeSlotReward();

            //进入文章页看新闻
            performTask_LookNews_2();

            if(!returnHome()){
                continue;
            }
            mToast.info("阅读完毕，首页滑动");
            mGestureUtil.scroll_up();
            mGestureUtil.scroll_up();
            NewsCount -- ;
        }
        //刷资讯
        return true;
    }

    //看新闻子任务二
    private boolean performTask_LookNews_2() {

        AccessibilityNodeInfo nodes = AccessibilityHelper.findNodeInfosById("com.ly.taotoutiao:id/mRecyclerView");

        if(nodes == null || nodes.getChildCount() < 1){
            return false;
        }
        int NodeCounter = nodes.getChildCount()-1;
        while (NodeCounter >= 0){

            AccessibilityNodeInfo AirtNode = AccessibilityHelper.findChildNodeInfosByText(nodes.getChild(NodeCounter),"广告");
            if(AirtNode != null){
                break;
            }

            if(mGestureUtil.click(nodes.getChild(NodeCounter))){
                //等待反应
                mFunction.click_sleep();

                //滑动次数(随机10到20)
                int SwiperCount = mFunction.getRandom_6_12()-3;

                mToast.info("新闻任务:滑动"+SwiperCount+"次");

                int loopXinWenCounter = 3;
                //开始滑动文章
                while (SwiperCount > 0) {

                    try{
                        if(mCommonTask.isCloseAppTask() || this.ArticleIsFinish){ break; }

                        this.CloseDialog();

                        //判断是否处于文章页，如果不是则退出
                        AccessibilityNodeInfo XinWenNode = AccessibilityHelper.findNodeInfosByText("新闻详情");
                        if(XinWenNode == null){
                            AccessibilityHelper.performBack();
                            break;
                        }

                        //点开【查看全文，奖励更多】按钮，阅读全文
                        if(mGestureUtil.clickWebNodeByText("展开阅读全文") || mGestureUtil.clickByText("展开阅读全文")){
                            mToast.success("展开阅读全文");
                        }

//                        while (loopXinWenCounter > 0){
//                            mGestureUtil.scroll_up(150,1000);
//                            mGestureUtil.click(mGlobal.mScreenWidth/2,mGlobal.mScreenHeight - 150);
//                            loopXinWenCounter--;
//                        }

                        //向上滑动
                        mGestureUtil.scroll_up();


                        //设置收益的最新时间
                        mIncomeTask.setLastIncomeTime();

                        //停止进行阅读
                        int sleepTime = mFunction.getRandom_2_5();
                        mFunction.sleep(sleepTime * 1000);
                        SwiperCount--;
                    }catch (Exception ex){
                        mToast.error_sleep(ex.getMessage());
                    }
                }

                //LookNewsSendingComment();
            }
            NodeCounter--;
        }
        return true;
    }

    //看新闻输入评论
    private boolean LookNewsSendingComment(){
        try{
            if(!mCommonFunctionTask.judgeNodeIsHavingByText("评论得金币")) return false;

            if(!mGestureUtil.clickByResourceId("com.xiangzi.jukandian:id/fl_comment_layout")) return false;

            if(!mCommonFunctionTask.judgeNodeIsHavingByText("点赞是一种态度")) return false;

            List<AccessibilityNodeInfo> nodes = AccessibilityHelper.findNodeInfosByIds("com.xiangzi.jukandian:id/comment_content");
            if(nodes == null || nodes.size() <= 0) return false;

            String CommentText = nodes.get(nodes.size()-1).getText().toString();

            mGestureUtil.clickByText("评论得金币");

            AccessibilityNodeInfo nodeInfo = AccessibilityHelper.findNodeInfosByClassName("android.widget.EditText");
            if(nodeInfo == null) return false;
            mCommonFunctionTask.pasteTextToNode(nodeInfo, CommentText);

            mFunction.click_sleep();

            mGestureUtil.clickByText("发送");

        }catch (Exception ex){

        }
        return false;
    }

    //-----------------------------------------------------------


    //领取时段奖励
    private boolean performTask_TimeSlotReward(){
        if(!returnHome()){
            return false;
        }
        if(!mCommonFunctionTask.judgeNodeIsHavingByResId("com.ly.taotoutiao:id/mSearchView")){
            mGestureUtil.clickTab(5,1);
        }
        mGestureUtil.clickByText("领取");

        this.CloseDialog();
        return true;
    }

    //领取圣诞树奖励
    private boolean performTask_TimeSlotReward_Tree(){
        mGestureUtil.clickTab(5,4);
        mGestureUtil.click(mGlobal.mScreenWidth-SizeOffset,(float) (mGlobal.mScreenHeight*0.79));
        return true;
    }


    //关闭APP弹出的所有可能弹框
    private void CloseDialog(){
        mGestureUtil.clickByText("开始阅读");

        mGestureUtil.clickByResourceId("com.ly.taotoutiao:id/btn_close");


    }

    //判断今日的收益是否已经达到最大值
    private Boolean JudgeGoldIncomeIsMax(){

        try{
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
            String goldValue = AccessibilityHelper.getNodeInfosTextByResourceId("com.ly.taotoutiao:id/tv_scroll_coin");
            if(!goldValue.isEmpty()){
                if(Float.valueOf(goldValue) > this.TodayMaxIncome){
                    this.TodayIncomeIsFinsh = true;
                    mToast.success("今日收益已封顶");
                    return true;
                }else {
                    mToast.success("今日收益("+goldValue+")未封顶("+this.TodayMaxIncome+")，继续工作");
                    return false;
                }
            }
        }catch (Exception ex){

        }
        return false;
    }

    //过滤广告
    private boolean filterAdvertisement( AccessibilityNodeInfo nodeInfo ){

        return false;
    }

    //执行签到任务
    private void SignIn(){
        mGestureUtil.clickByText("一键签到");
        mGestureUtil.clickByResourceId("com.xiangzi.jukandian:id/v2_sign_sign_button");
        mGestureUtil.clickByResourceId("com.xiangzi.jukandian:id/v2_sign_close_button");
    }

    //上传APP的最新收益情况
    @Override
    Boolean UploadIncome(){
        try{
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
            String curValue = AccessibilityHelper.getNodeInfosTextByResourceId("com.ly.taotoutiao:id/tv_scroll_cash");
            String goldValue = AccessibilityHelper.getNodeInfosTextByResourceId("com.ly.taotoutiao:id/tv_scroll_coin");
            if(!curValue.isEmpty() && !goldValue.isEmpty()){
                float rmd = Float.valueOf(curValue) + Float.valueOf(goldValue)/10000;
                mUploadDataUtil.postIncomeRecord(this.AppName,rmd);
                mToast.success("收益上传:"+rmd);
            }
        }catch (Exception ex){

        }

        return true;
    }

    //回归到首页，如果APP未打开，则会自行打开
    private boolean returnHome(){
        return returnHome("任务","我的",new Runnable() {
            @Override
            public void run() {
                CloseDialog();
            }
        });
    }
}
