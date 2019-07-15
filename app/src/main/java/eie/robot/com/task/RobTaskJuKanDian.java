package eie.robot.com.task;

import android.graphics.Rect;
import android.view.accessibility.AccessibilityNodeInfo;

import java.util.List;

import eie.robot.com.accessibilityservice.AccessibilityHelper;
import eie.robot.com.common.mCommonFunctionTask;
import eie.robot.com.common.mCommonTask;
import eie.robot.com.common.mConfig;
import eie.robot.com.common.mUploadDataUtil;
import eie.robot.com.common.mDeviceUtil;
import eie.robot.com.common.mFunction;
import eie.robot.com.common.mGestureUtil;
import eie.robot.com.common.mGlobal;
import eie.robot.com.common.mIncomeTask;
import eie.robot.com.common.mToast;

public class RobTaskJuKanDian extends BaseRobotTask {

    //构造函数
    public RobTaskJuKanDian() {
        super();
        this.AppName = "聚看点";
        this.TodayMaxIncome = 6000;
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

                    //领取圣诞树奖励
                    performTask_TimeSlotReward_Tree();

                    //领取时段奖励
                    performTask_TimeSlotReward();

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
        int RefreshCount =   mFunction.getRandom_1_3();
        while (RefreshCount > 0){
            //判断收益
            if(JudgeGoldIncomeIsMax()){
                break;
            }
            if(mCommonTask.isCloseAppTask() || this.ArticleIsFinish){ break;}
            performTask_WatchVideo_1();
            RefreshCount -- ;
        }
        return false;
    }

    //看视频子任务一
    private boolean performTask_WatchVideo_1(){
        UploadIncome();

        if(!returnHome()){
            return false;
        }
        mToast.success("视频任务");
        //点击第二个功能列表
        mGestureUtil.clickTab(5,2);

        int NewsCount =   mFunction.getRandom_1_3();
        while (NewsCount > 0){
            if(mCommonTask.isCloseAppTask()){ break;}
            //判断收益
            if(this.JudgeGoldIncomeIsMax()){
                break;
            }
            //看视频
            performTask_WatchVideo_2();

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

    //看视频子任务二
    private boolean performTask_WatchVideo_2() {



        List<AccessibilityNodeInfo> nodes = AccessibilityHelper.findNodeInfosByIds("com.xiangzi.jukandian:id/item_video_parent");

        if(nodes == null || nodes.size() < 1){
            return false;
        }
        int NodeCounter = nodes.size()-1;
        while (NodeCounter >= 0){
            try{
                //点击新闻进行阅读。
                if(AccessibilityHelper.performClick(nodes.get(NodeCounter))){

                    mFunction.click_sleep();

                    //滑动次数(随机10到20)
                    int SwiperCount = mFunction.getRandom_6_12();

                    mToast.info("开始看视频");
                    //开始看视频
                    while (SwiperCount > 0) {
                        if(mCommonTask.isCloseAppTask() || this.VideoIsFinish){ break; }

                        this.CloseDialog();

                        //判断是否处于视频播放页，如果不是则退出
                        //判断是否处于文章页，如果不是则退出
                        AccessibilityNodeInfo XinWenNode = AccessibilityHelper.findNodeInfosByText("评论得金币");
                        if(XinWenNode == null){
                            mGestureUtil.click(SizeOffset,mDeviceUtil.getStatusBarHeight()+SizeOffset);
                            AccessibilityHelper.performBack();
                            break;
                        }

                        if(mCommonFunctionTask.judgeNodeIsHavingByText("重播")){
                            break;
                        }

                        //设置收益的最新时间
                        mIncomeTask.setLastIncomeTime();

                        mFunction.sleep(mConfig.WaitLauncherlTime);
                        SwiperCount--;
                    }
                }
                NodeCounter--;
                AccessibilityHelper.performBack();
                mFunction.click_sleep();
            }catch (Exception ex){
                mToast.error_sleep("performTask_WatchVideo_2:"+ex.getMessage());
            }

        }

        return true;
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
        UploadIncome();

        if(!returnHome()){
            return false;
        }
        //点击第一个功能列表
        mGestureUtil.clickTab(5,1);

        //随机获取在本首页的滑动的次数
        int NewsCount = mFunction.getRandom_4_8();

        while (NewsCount > 0){

            if(mCommonTask.isCloseAppTask()){ break; }

            //判断收益
            if(this.JudgeGoldIncomeIsMax()){
                break;
            }

            //领取时段奖励
            performTask_TimeSlotReward();

            //进入文章页看新闻
            performTask_LookNews_2();
            if(!returnHome()){
                continue;
            }
            mToast.info("阅读完毕，首页滑动");
            mGestureUtil.scroll_up();
            NewsCount -- ;
        }
        //刷资讯
        return true;
    }

    //看新闻子任务二
    private boolean performTask_LookNews_2() {


        List<AccessibilityNodeInfo> nodes = AccessibilityHelper.findNodeInfosByIds("com.xiangzi.jukandian:id/item_artical_three_read_num");

        if(nodes == null || nodes.size() < 1){
            return false;
        }
        int NodeCounter = nodes.size()-1;
        while (NodeCounter >= 0){
            if(AccessibilityHelper.performClick(nodes.get(NodeCounter))){
                //等待反应
                mFunction.click_sleep();

                //滑动次数(随机10到20)
                int SwiperCount = mFunction.getRandom_6_12();

                mToast.info("新闻任务:滑动"+SwiperCount+"次");

                int loopXinWenCounter = 3;
                //开始滑动文章
                while (SwiperCount > 0) {

                    try{
                        if(mCommonTask.isCloseAppTask() || this.ArticleIsFinish){ break; }

                        this.CloseDialog();

                        //判断是否处于文章页，如果不是则退出
                        AccessibilityNodeInfo XinWenNode = AccessibilityHelper.findNodeInfosByText("评论得金币");
                        if(XinWenNode == null){
                            AccessibilityHelper.performBack();
                            mFunction.click_sleep();
                            XinWenNode = AccessibilityHelper.findNodeInfosByText("评论得金币");
                            if(XinWenNode == null){
                                mGestureUtil.click(SizeOffset,mDeviceUtil.getStatusBarHeight()+SizeOffset);
                                break;
                            }
                        }

                        while (loopXinWenCounter > 0){
                            mGestureUtil.scroll_up(150,1000);
                            mGestureUtil.click(mGlobal.mScreenWidth/2,mGlobal.mScreenHeight - 150);
                            loopXinWenCounter--;
                        }


                        //向上滑动
                        mGestureUtil.scroll_up();

                        //点开【查看全文，奖励更多】按钮，阅读全文
                        if(mGestureUtil.clickByText("查看全文，奖励更多") || mGestureUtil.clickWebNodeByText("查看全文，奖励更多")){
                            mToast.success("查看全文，奖励更多");
                        }



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

                LookNewsSendingComment();
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
        if(!mCommonFunctionTask.judgeNodeIsHavingByText("搜索你感兴趣的内容")){
            mGestureUtil.clickTab(5,1);
        }
        mGestureUtil.clickByResourceId("com.xiangzi.jukandian:id/icon_home_left_timer_lq");
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
        //签到
        SignIn();

        AccessibilityNodeInfo nodeInfo = AccessibilityHelper.findWebViewNodeInfosByText("阅读福袋");
        if(nodeInfo != null){
            Rect rect = new Rect();
            nodeInfo.getBoundsInScreen(rect);
            if(rect.top < mGlobal.mScreenHeight-100 && rect.top > 100){
                mGestureUtil.click((float) (mGlobal.mScreenWidth*0.67),rect.top+rect.height()-50);
            }

        }

        mGestureUtil.clickByText("不再提醒");
        mGestureUtil.clickByText("确认关闭");

        mGestureUtil.clickByText("我知道了");

        AccessibilityNodeInfo node = AccessibilityHelper.findNodeInfosByText("分享立赚");
        if(node != null){
            mGestureUtil.click(node);
            AccessibilityHelper.performBack();
        }
        mGestureUtil.clickByText("忽略");

        mGestureUtil.clickByText("继续赚钱");

        mGestureUtil.clickByResourceId("com.xiangzi.jukandian:id/close_img_layout");


    }

    //判断今日的收益是否已经达到最大值
    private Boolean JudgeGoldIncomeIsMax(){
        try{
            this.TaskCounter ++;
            if(TaskCounter < this.TaskCounterDefaultValue){
                return false;
            }
            if(!returnHome()){
                return false;
            }

            //回复到首页
            if(!mCommonFunctionTask.judgeNodeIsHavingByText("搜索你感兴趣的内容")){
                mGestureUtil.clickTab(5,1);
            }

            String JinBiNum = AccessibilityHelper.getNodeInfosTextByResourceId("com.xiangzi.jukandian:id/pager_jinbi_num");
            if(JinBiNum.isEmpty() || Integer.valueOf(JinBiNum) <=0){
                return false;
            }
            mGestureUtil.clickByResourceId("com.xiangzi.jukandian:id/pager_jinbi_num");
            mFunction.click_sleep();
            if(!mCommonFunctionTask.judgeNodeIsHavingByText("阅读收益")){
                return false;
            }
            //文章【阅读收益】的次数，最大150
            AccessibilityNodeInfo node = AccessibilityHelper.findWebViewNodeInfosByText("今日奖励次数");
            if(node == null ) return false;
            String ArticleIncomeCounter = node.getText() == null ? node.getContentDescription().toString() : node.getText().toString();
            ArticleIncomeCounter = ArticleIncomeCounter.replace("今日奖励次数(","");
            ArticleIncomeCounter = ArticleIncomeCounter.replace("次)","");
            if(Integer.valueOf(ArticleIncomeCounter) >= 48){
                this.ArticleIsFinish = true;
            }


            node = AccessibilityHelper.findWebViewNodeInfosByText("视频收益");
            if(node == null) return false;
            node = node.getParent();
            if(node == null || node.getChildCount() < 3) return false;
            String VideoIncomeCounter =  node.getChild(1).getText() == null ? node.getChild(1).getContentDescription().toString() :  node.getChild(1).getText().toString();
            VideoIncomeCounter = VideoIncomeCounter.replace("今日奖励次数(","");
            VideoIncomeCounter = VideoIncomeCounter.replace("次)","");
            if(Integer.valueOf(VideoIncomeCounter) >= 23){
                this.VideoIsFinish = true;
            }

            if(this.ArticleIsFinish && this.VideoIsFinish){
                this.TodayIncomeIsFinsh = true;
                mToast.success("今日收益已封顶");
                mFunction.sleep(mConfig.clickSleepTime);
                this.TaskCounter = 0;
                AccessibilityHelper.performBack();
                return true;
            }else {
                mToast.success("今日收益未封顶(文章:"+ArticleIncomeCounter+"，视频:"+VideoIncomeCounter+"),继续工作");
                mFunction.click_sleep();
                AccessibilityHelper.performBack();
                this.TaskCounter = 0;
                return false;
            }

        }catch (Exception ignored){
            this.TaskCounter = 0;
            return false;
        }
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
            String curValue = AccessibilityHelper.getNodeInfosTextByResourceId("com.xiangzi.jukandian:id/curValue");
            String goldValue = AccessibilityHelper.getNodeInfosTextByResourceId("com.xiangzi.jukandian:id/goldValue");
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
        return returnHome("任务中心","我的",new Runnable() {
            @Override
            public void run() {
                CloseDialog();
            }
        });
    }
}
