package eie.robot.com.task;

import android.graphics.Rect;
import android.view.accessibility.AccessibilityNodeInfo;

import com.vondear.rxtool.view.RxToast;

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

public class RobTaskSouHuZiXun extends BaseRobotTask {

    //构造函数
    public RobTaskSouHuZiXun() {
        super();
        this.AppName = "搜狐资讯";
        this.TodayMaxIncome = 3500;
        this.TodayIncomeIsFinsh = false;
        this.TaskCounterDefaultValue = 3;
    }

    //执行刷单任务（领取时段奖励、定时刷新视频、查看文章）
    @Override
    public boolean StartTask()  {
        try{
            super.StartTask();
            while (mCommonTask.isOpenAppTask()){
                try {
                    if(this.TodayIncomeIsFinsh){
                        break;
                    }

                    //每次进行一项任务时，都先恢复到首页
                    //如果APP未打开，则会自行打开,如果最后还是无法打开，则跳出这次循环，重新来。
                    if(!returnHome()){
                        continue;
                    }

                    UploadIncome();

                    //判断收益是否封顶（每次重启的时候查一次）
                    this.TaskCounter = this.TaskCounterDefaultValue;

                    //签到
                    SignIn();

                    //阅读文章
                    performTask_LookNews();

                    //看视频
                    performTask_WatchVideo();
                }
                catch (Exception ex){
                    mToast.error(ex.getMessage());
                }
            }
            UploadIncome();
            super.CloseTask();
        }catch (Exception ex){

        }

        return false;
    }

    //-----------------------------------------------------------

    //执行刷单任务（看视频）
    //看新闻总任务
    @Override
    Boolean performTask_LookNews(){
        int RefreshCount =   mFunction.getRandom_4_8();
        while (RefreshCount > 0){
            if(mCommonTask.isCloseAppTask()){ break;}
            if(this.ArticleIsFinish || this.TodayIncomeIsFinsh){ break;}

            //阅读文章
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
        mGestureUtil.clickTab(4,1);

        //随机获取在本首页的滑动的次数
        int NewsCount =   mFunction.getRandom_4_8();
        while (NewsCount > 0){
            if(mCommonTask.isCloseAppTask() || this.ArticleIsFinish){ break; }

            //打开能量红包
            openRedPacketsEnergy();

            //进入文章页看新闻
            performTask_LookNews_2();

            if(mCommonTask.isCloseAppTask() || this.ArticleIsFinish){ break; }

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
        try{
            List<AccessibilityNodeInfo> node = AccessibilityHelper.findNodeInfosByIds("com.sohu.infonews:id/pic_container");
            if(node == null || node.size() < 1){
                return false;
            }

            int LoopCounter = node.size()-1;
            while (LoopCounter >= 0){
                Boolean result = mGestureUtil.performClick(node.get(LoopCounter));

                //开始阅读新闻
                if (result) {
                    mFunction.click_sleep();

                    //判断收益
                    if(JudgeGoldIncomeIsMax()){
                        return true;
                    }

                    //滑动次数(随机10到20)
                    int SwiperCount = mFunction.getRandom_4_8();

                    mToast.info("新闻任务:滑动"+SwiperCount+"次");

                    //开始滑动文章
                    while (SwiperCount > 0) {
                        if(mCommonTask.isCloseAppTask() || this.ArticleIsFinish){ break; }

                        //判断是否处于文章页，如果不是则退出
                        AccessibilityNodeInfo XinWenNode = AccessibilityHelper.findNodeInfosByText("写优质评论，获狐币大礼");
                        if(XinWenNode == null){
                            AccessibilityHelper.performBack();
                            mFunction.click_sleep();
                            XinWenNode = AccessibilityHelper.findNodeInfosByText("写优质评论，获狐币大礼");
                            if(XinWenNode == null){
                                mGestureUtil.click(SizeOffset,mDeviceUtil.getStatusBarHeight()+SizeOffset);
                                break;
                            }
                        }
                        if(!mCommonFunctionTask.judgeNodeIsHavingByResId("com.sohu.infonews:id/counting_img")){
                            break;
                        }

                        //向上滑动
                        mGestureUtil.scroll_up();
                        //设置收益的最新时间
                        mIncomeTask.setLastIncomeTime();

                        //停止进行阅读
                        int sleepTime = mFunction.getRandom_4_8();
                        mFunction.sleep(sleepTime * 1000);
                        SwiperCount--;

                    }
                }
                returnHome();
                LoopCounter--;
            }
        }catch (Exception ignored){

        }
        return true;
    }


    //-----------------------------------------------------------

    //看视频总任务
    @Override
    Boolean performTask_WatchVideo(){
        int RefreshCount =   mFunction.getRandom_1_3();
        while (RefreshCount > 0){
            if(mCommonTask.isCloseAppTask()){ break;}
            if(this.VideoIsFinish || this.TodayIncomeIsFinsh){ break;}
            performTask_WatchVideo_1();
            RefreshCount -- ;
        }
        return false;
    }

    //看视频子任务一
    private boolean performTask_WatchVideo_1(){
        if(!returnHome()){
            return false;
        }
        mGestureUtil.clickTab(4,2);
        //点击第二个功能列表
        int NewsCount =   mFunction.getRandom_1_3()+3;
        mToast.success("视频任务");
        while (NewsCount > 0){
            if(mCommonTask.isCloseAppTask() || this.VideoIsFinish){ break;}
            //看视频
            performTask_WatchVideo_2();

            if(!returnHome()){
                continue;
            }

            if(mCommonTask.isCloseAppTask() || this.VideoIsFinish){ break;}

            mToast.success("阅读完毕，首页滑动");
            NewsCount -- ;
            if(NewsCount > 0){
                mGestureUtil.scroll_up_500();
            }
        }
        //刷资讯
        return true;
    }

    //看视频子任务二
    private boolean performTask_WatchVideo_2() {
        List<AccessibilityNodeInfo> nodes = AccessibilityHelper.findNodeInfosByIds("com.sohu.infonews:id/textComment");
        if(nodes == null || nodes.size() < 1){
            return false;
        }
        //点击新闻进行阅读。
        boolean clickResult = mGestureUtil.click(nodes.get(0));
        //开始阅读新闻
        if (clickResult) {
            //等待反应
            mFunction.click_sleep();

            //判断是否达到今日收益最大值
            if(JudgeGoldIncomeIsMax()){
                return true;
            }

            //滑动次数(随机10到20)
            int SwiperCount = mFunction.getRandom_6_12();
            mToast.info("开始看视频");
            //开始滑动文章
            while (SwiperCount > 0) {
                if(mCommonTask.isCloseAppTask() || this.VideoIsFinish){ break; }

                //判断是否处于视频播放页，如果不是则退出
                //判断是否处于文章页，如果不是则退出
                AccessibilityNodeInfo XinWenNode = AccessibilityHelper.findNodeInfosByText("写优质评论，获狐币大礼");
                if(XinWenNode == null){
                    mGestureUtil.click(SizeOffset,mDeviceUtil.getStatusBarHeight()+SizeOffset);
                    break;
                }
                XinWenNode = AccessibilityHelper.findNodeInfosByText("重新播放");
                if(XinWenNode != null){
                    mGestureUtil.click(SizeOffset,mDeviceUtil.getStatusBarHeight()+SizeOffset);
                    break;
                }
                //设置收益的最新时间
                mIncomeTask.setLastIncomeTime();
                mFunction.sleep(mConfig.WaitLauncherlTime);
                SwiperCount--;

            }
        }

        return true;
    }



    //---------------------------------------------------

    //打开能量红包
    private boolean openRedPacketsEnergy(){
        AccessibilityNodeInfo node = AccessibilityHelper.findNodeInfosById("com.sohu.infonews:id/energy_open");
        if(node != null){
            mGestureUtil.click(node);
            AccessibilityHelper.performBack();
        }
        this.CloseDialog();
        return true;
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
    private Boolean SignIn(){
        if(this.IsSign){
            return true;
        }
        if(!returnHome()){
            return false;
        }
        //点击【我的】列表
        mGestureUtil.clickTab(4,4);

        //再次恢复到首页
        if(!returnHome()){
            return false;
        }

        //我的界面往上滑动了，先向下滑动一次
        mGestureUtil.scroll_down_half_screen();

        AccessibilityNodeInfo SignNode = null;
        try{
            SignNode = AccessibilityHelper.findNodeInfosByText("签到");
        }catch (Exception ex){
            mToast.error(this.AppName+"收益检测错误:"+ex.getMessage());
        }
        if(SignNode != null){
            if(SignNode.getText().toString().equals("已签到")){
                mToast.success("今天已签到");
                this.IsSign = true;
                return false;
            }
            mGestureUtil.click(SignNode);
            mFunction.click_sleep();
            returnHome();
            return true;
        }
        return false;
    }

    //关闭APP弹出的所有可能弹框
    private void CloseDialog(){

        mGestureUtil.clickByResourceId("com.sohu.infonews:id/act_close_image");


        AccessibilityNodeInfo node = AccessibilityHelper.findNodeInfosByText("放弃提现");
        if(node != null){
            mGestureUtil.click(node);
        }

        node = AccessibilityHelper.findNodeInfosByText("继续阅读");
        if(node != null){
            mGestureUtil.click(node);
        }

        node = AccessibilityHelper.findNodeInfosByText("忽略");
        if(node != null){
            mGestureUtil.click(node);
        }

        mGestureUtil.clickByText("笑纳了");

        node = AccessibilityHelper.findNodeInfosById("com.xiangzi.jukandian:id/close_img_layout");
        if(node != null){
            mGestureUtil.click(node);
        }
    }

    //判断今日的收益是否已经达到最大值
    private Boolean JudgeGoldIncomeIsMax(){
        this.TaskCounter ++;
        if(TaskCounter < this.TaskCounterDefaultValue){
            return false;
        }
        if(! mGestureUtil.clickByResourceId("com.sohu.infonews:id/counting_img")){
            return false;
        }
        mFunction.click_sleep();
        if(! mCommonFunctionTask.judgeNodeIsHavingByText("今日阅读任务")){
            return false;
        }


        //判断文章数量
        float ArticleIncomeCounter = 0;
        AccessibilityNodeInfo readCountArticle = AccessibilityHelper.findNodeInfosById("com.sohu.infonews:id/read_count_article");
        if(readCountArticle != null){
            String ArticleText = readCountArticle.getText().toString().replace("阅读文章","").trim();
            String[] ArticleArray = ArticleText.split("/");
            if(ArticleArray.length > 1){
                if(Float.valueOf(ArticleArray[0]) >= Integer.valueOf(ArticleArray[1])){
                    this.ArticleIsFinish = true;
                }
                ArticleIncomeCounter = Float.valueOf(ArticleArray[0]);
            }
        }

        float VideoIncomeCounter = 0;
        //判断视频数量
        AccessibilityNodeInfo readCountVideo = AccessibilityHelper.findNodeInfosById("com.sohu.infonews:id/read_count_video");
        if(readCountVideo != null){
            String ArticleText = readCountVideo.getText().toString().replace("观看视频","").trim();
            String[] ArticleArray = ArticleText.split("/");
            if(ArticleArray.length > 1){
                if(Float.valueOf(ArticleArray[0]) >= Integer.valueOf(ArticleArray[1])){
                    this.VideoIsFinish = true;
                }
                VideoIncomeCounter = Float.valueOf(ArticleArray[0]);
            }
        }

        mGestureUtil.clickByText("立即领取");

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

    }

    //上传APP的最新收益情况
    @Override
    Boolean UploadIncome(){

        try{

            if(!mCommonFunctionTask.judgeNodeIsHavingByText("任务中心")){
                if(!returnHome()){
                    return false;
                }

                mGestureUtil.clickTab(4,4);

                if(!returnHome()){
                    return false;
                }

                mGestureUtil.scroll_down_half_screen();

                if(!returnHome()){
                    return false;
                }
            }

            float cash = 0;
            AccessibilityNodeInfo cash_item = AccessibilityHelper.findNodeInfosById("com.sohu.infonews:id/cash_item");
            if(cash_item != null){
                cash_item = cash_item.getChild(0).getChild(1);
                if(cash_item != null){
                    cash = Float.valueOf(cash_item.getText().toString());
                }
            }

            float coin = 0;
            AccessibilityNodeInfo money_item = AccessibilityHelper.findNodeInfosById("com.sohu.infonews:id/money_item");
            if(money_item != null){
                money_item = money_item.getChild(0).getChild(1);
                if(money_item != null){
                    String coinString = money_item.getText().toString().replace(",","");
                    coin = Float.valueOf(coinString)/10000;
                }
            }

            float rmd = cash + coin ;
            mUploadDataUtil.postIncomeRecord(this.AppName,rmd);


        }catch (Exception ex){

        }


        return true;
    }


    //回归到首页，如果APP未打开，则会自行打开
    private boolean returnHome(){
        return returnHome("追踪","任务",new Runnable() {
            @Override
            public void run() {
                CloseDialog();
            }
        });
    }
}
