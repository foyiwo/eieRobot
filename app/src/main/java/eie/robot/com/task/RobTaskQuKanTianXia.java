package eie.robot.com.task;

import android.graphics.Path;
import android.graphics.Rect;
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

public class RobTaskQuKanTianXia extends BaseRobotTask {

    public int SizeOffset = 40;

    //构造函数
    public RobTaskQuKanTianXia() {
        super();
        this.AppName = "趣看天下";
        this.TodayMaxIncome = 6000;
        this.TodayIncomeIsFinsh = false;
    }

    //执行刷单任务（领取时段奖励、定时刷新视频、查看文章）
    @Override
    public boolean StartTask()  {
        super.StartTask();
        try {
            while (mCommonTask.isOpenAppTask()){

                //回到首页
                if(!returnHome()){
                    continue;
                }

                //领取时段奖励
                performTask_TimeSlotReward();

                //判断收益是否封顶
                if(JudgeGoldIncomeIsMax()){
                    break;
                }

                //回到首页
                if(!returnHome()){
                    continue;
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
            }

            JudgeGoldIncomeIsMax();
            super.CloseTask();
        }catch (Exception ex){
            RxToast.error(ex.getMessage());
        }
        return false;
    }

    //-----------------------------------------------------------

    //看新闻总任务
    @Override
    Boolean performTask_LookNews(){
        //看新闻
        Integer RefreshCount =   mFunction.getRandom_4_8();
        while (RefreshCount > 0){
            if(mCommonTask.isCloseAppTask()){ break;}
            performTask_LookNews_1();
            RefreshCount -- ;
        }
        return true;
    }

    //看新闻子任务一
    private boolean performTask_LookNews_1(){
        if(!returnHome()){
            return false;
        }

        //点击第一个功能列表
        mGestureUtil.clickTab(5,1);

        mToast.success("新闻阅读任务");

        mGestureUtil.scroll_up();

        int NewsCount =   mFunction.getRandom_4_8();

        while (NewsCount > 0){
            if(mCommonTask.isCloseAppTask()){ break; }

            performTask_LookNews_2();

            if(!returnHome()){
                continue;
            }

            mToast.info("新闻看完，首页滑动");
            mGestureUtil.scroll_up();
            mGestureUtil.scroll_up();
            NewsCount -- ;
        }
        return true;
    }

    //看新闻子任务二
    private boolean performTask_LookNews_2() {

        List<AccessibilityNodeInfo> node = AccessibilityHelper.findNodeInfosByIds("com.yanhui.qktx:id/iv_img2");
        if(node == null) return false;

        int loopCounter = node.size();

        while (loopCounter > 0){
            try{
                loopCounter--;

                //点击新闻进行阅读。
                if (mGestureUtil.click(node.get(loopCounter))) {
                    //等待反应
                    mFunction.sleep(mConfig.clickSleepTime);

                    //滑动次数(随机10到20)
                    int SwiperCount = mFunction.getRandom_6_12();

                    mToast.info("新闻任务:滑动"+SwiperCount+"次");


                    int loopXinWenCounter = 3;
                    //开始滑动文章
                    while (SwiperCount > 0) {

                        if(mCommonTask.isCloseAppTask()){ break; }


                        //判断是否处于文章页或者视频页，如果不是则退出
                        if(!mCommonFunctionTask.judgeNodeIsHavingByText("评论得金币")){
                            break;
                        }

                        while (loopXinWenCounter > 0){
                            mGestureUtil.scroll_up(100,1000);
                            mGestureUtil.click(mGlobal.mScreenWidth/2,mGlobal.mScreenHeight-150);
                            if(mCommonFunctionTask.judgeNodeIsHavingByText("保存")){
                                AccessibilityHelper.performBack();
                                mFunction.click_sleep();
                            }
                            loopXinWenCounter--;
                        }


                        //设置收益的最新时间
                        mIncomeTask.setLastIncomeTime();


                        if(mGestureUtil.clickWebNodeByText("展开查看全文")){
                            mToast.success("展开查看全文");
                        }

                        //判断是处于视频页还是文章页，info为空，说明是文章页，不为空，则为视频页，不进行滑动
                        if(!mCommonFunctionTask.judgeNodeIsHavingByResId("com.yanhui.qktx:id/surface_container")){
                            mGestureUtil.scroll_up();
                        }

                        //停止进行阅读
                        int sleepTime = mFunction.getRandom_2_4();
                        mFunction.sleep(sleepTime * 1000);
                        SwiperCount--;

                    }

                    if(mFunction.getRandomBooleanOffsetFalse()){
                        LookNewsSendingComment();
                    }

                    returnHome();
                }
            }catch (Exception ex){
                mToast.error_sleep(ex.getMessage());
            }
        }

        return true;
    }

    //看新闻输入评论
    private boolean LookNewsSendingComment(){
        try{

            if(!mCommonFunctionTask.judgeNodeIsHavingByText("评论得金币")) return false;

            if(!mGestureUtil.clickByResourceId("com.yanhui.qktx:id/img_news_bottom_right_img_comment")) return false;

            if(!mCommonFunctionTask.judgeNodeIsHavingByText("点赞是一种态度")) return false;

            mGestureUtil.scroll_up();

            mGestureUtil.scroll_up();

            mGestureUtil.clickByResourceId("com.yanhui.qktx:id/view_click_like_number");

            String CommentText = AccessibilityHelper.getNodeInfosTextByResourceId("com.yanhui.qktx:id/expand_comment_content");
            if(CommentText.isEmpty()) return false;

            mGestureUtil.clickByCoordinate(AccessibilityHelper.findNodeInfosByEqualText("评论得金币"),0);

            AccessibilityNodeInfo nodeInfo = AccessibilityHelper.findNodeInfosByClassName("android.widget.EditText");
            if(nodeInfo == null) return false;

            mCommonFunctionTask.pasteTextToNode(nodeInfo,CommentText);

            mGestureUtil.clickByText("发送");

            mFunction.click_sleep();
            AccessibilityHelper.performBack();

        }catch (Exception ex){

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

            returnHome();

            //点击第一个功能列表
            mGestureUtil.clickTab(5,3);

            performTask_WatchVideo_1();

            RefreshCount -- ;
        }
        return false;
    }

    //看视频子任务一
    private boolean performTask_WatchVideo_1(){

        //进入视频
        mGestureUtil.click(SizeOffset*3,SizeOffset*3);

        if(!mCommonFunctionTask.judgeNodeIsHavingByText("分享")) return false;

        //点击视频的间隔
        int VideoInterval = 6+ mFunction.getRandom_6_12();

        mToast.info("视频任务:阅读"+VideoInterval+"秒");

        //设置收益的最新时间
        mIncomeTask.setLastIncomeTime();

        if(VideoInterval >= 17){
            mGestureUtil.clickByResourceId("com.yanhui.qktx:id/collect");
        }
        mFunction.sleep( VideoInterval * 1000);

        if(mFunction.getRandomBooleanOffsetFalse()){
            VideoSendingComment();
        }
        return true;
    }

    //小视频输入评论
    private boolean VideoSendingComment(){
        try{

            mGestureUtil.clickByResourceId("com.yanhui.qktx:id/comment");

            if(!mCommonFunctionTask.judgeNodeIsHavingByText("条评论")) return false;

            mGestureUtil.scroll_up();

            mGestureUtil.scroll_up_30();

            mGestureUtil.clickByResourceId("com.yanhui.qktx:id/view_click_like_number");

            String CommentText = AccessibilityHelper.getNodeInfosTextByResourceId("com.yanhui.qktx:id/expand_comment_content");
            if(CommentText.isEmpty()) return false;

            mGestureUtil.clickByCoordinate(AccessibilityHelper.findNodeInfosByEqualText("评论得金币"),0);

            AccessibilityNodeInfo nodeInfo = AccessibilityHelper.findNodeInfosByClassName("android.widget.EditText");
            if(nodeInfo == null) return false;

            mCommonFunctionTask.pasteTextToNode(nodeInfo,CommentText);

            mGestureUtil.clickByText("发送");

            mFunction.click_sleep();

            AccessibilityHelper.performBack();
        }catch (Exception ex){

        }
        return false;
    }


    //-----------------------------------------------------------

    //看列表视频总任务
    Boolean performTask_WatchVideo_list(){
        //看视频
        int RefreshCount =  mFunction.getRandom_1_3();
        while (RefreshCount > 0){
            if(mCommonTask.isCloseAppTask()){ break;}
            returnHome();

            //点击第一个功能列表
            mGestureUtil.clickTab(5,2);

            performTask_WatchVideo_list_1();
            RefreshCount -- ;
        }
        return true;
    }

    //看列表视频总任务一
    private boolean performTask_WatchVideo_list_1(){

        mToast.success("视频任务");
        int NewsCount =   mFunction.getRandom_1_3();

        while (NewsCount > 0){
            if(mCommonTask.isCloseAppTask()){ break;}
            //看视频
            performTask_WatchVideo_list_2();

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

    //看列表视频总任务二
    private boolean performTask_WatchVideo_list_2(){
        //点击新闻进行阅读。
        boolean clickResult = mGestureUtil.clickInScreenCenter();
        if(!clickResult){
            mToast.error("点击失效，重新选择视频");
            return false;
        }
        //开始阅读新闻
        if (clickResult) {
            //等待反应
            mFunction.click_sleep();

            //滑动次数(随机10到20)
            int SwiperCount = mFunction.getRandom_6_12();
            mToast.info("开始看视频");
            //开始滑动文章
            while (SwiperCount > 0) {
                if(mCommonTask.isCloseAppTask()){ break; }

                //判断是否处于视频播放页，如果不是则退出
                //判断是否处于文章页，如果不是则退出
                AccessibilityNodeInfo XinWenNode = AccessibilityHelper.findNodeInfosByText("评论得金币");
                if(XinWenNode == null){
                    break;
                }

                //设置收益的最新时间
                mIncomeTask.setLastIncomeTime();

                XinWenNode = AccessibilityHelper.findNodeInfosByText("转发给好友观看每次奖励500金币");
                if(XinWenNode != null){
                    break;
                }
                mFunction.sleep(mConfig.WaitLauncherlTime);
                SwiperCount--;
            }
        }

        return true;
    }

    //-----------------------------------------------------------


    //执行刷单任务（领取时段奖励）
    private boolean performTask_TimeSlotReward(){

        if(!returnHome()){
            return false;
        }

        //点击第一个功能列表
        mGestureUtil.clickTab(5,1);

        if(mCommonFunctionTask.judgeNodeIsHavingByResId("com.yanhui.qktx:id/tv_time")){
            mToast.success("时段奖励已领取");
            return true;
        }

        AccessibilityNodeInfo ScrollViewNodeInfo = AccessibilityHelper.findNodeInfosByClassName(
                mGlobal.mAccessibilityService.getRootInActiveWindow()
                ,"android.widget.HorizontalScrollView");
        if(ScrollViewNodeInfo == null){
            return false;
        }

        Rect rect = new Rect();
        ScrollViewNodeInfo.getBoundsInScreen(rect);
        if(rect.width() < 1 || rect.top < 1){
            return false;
        }

        mToast.success("领取时段奖励");
        //点击时段按钮
        mGestureUtil.click(rect.width() - SizeOffset,rect.top - SizeOffset);

        this.CloseDialog();

        return true;
    }

    //关闭APP弹出的所有可能弹框
    private void CloseDialog(){
        AccessibilityNodeInfo node = AccessibilityHelper.findNodeInfosById("com.yanhui.qktx:id/img_close");
        if(node != null){
            mGestureUtil.click(node);
        }
        node = AccessibilityHelper.findNodeInfosByText("继续阅读");
        if(node != null){
            mGestureUtil.click(node);
        }
        node = AccessibilityHelper.findNodeInfosByText("我知道了");
        if(node != null){
            mGestureUtil.click(node);
        }
        //签到
        //判断是否处于弹框，但是却无法利用【返回键】取消的状态
        node = mGlobal.mAccessibilityService.getRootInActiveWindow();
        if(node != null){
            Rect rect = new Rect();
            node.getBoundsInScreen(rect);
            int viewHeight = rect.height();
            if(viewHeight < mGlobal.mScreenHeight-100){
                mGestureUtil.click(mGlobal.mScreenWidth/2,mGlobal.mScreenHeight/2);
            }
        }
        //提示开启通知框的弹窗
        node = AccessibilityHelper.findNodeInfosByText("开启推送可获得大量金币");
        if(node != null){
            node = AccessibilityHelper.findNodeInfosByText("取消");
            if(node != null){
                mGestureUtil.click(node);
            }

        }
        node = AccessibilityHelper.findNodeInfosByText("去阅读");
        if(node != null){
            mGestureUtil.click(node);
        }


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
        mGestureUtil.scroll_down_100();

        UploadIncome();

        AccessibilityNodeInfo IncomeNode = null;

        try{

            //利用ID的方式
            AccessibilityNodeInfo nodeInfo = AccessibilityHelper.findNodeInfosById("com.yanhui.qktx:id/tv_money_count");
            if(nodeInfo != null && nodeInfo.getClassName().equals("android.widget.TextView")){
                IncomeNode = nodeInfo;
            }

            if(IncomeNode != null){
                String incomeText = IncomeNode.getText().toString().trim();
                if(Integer.valueOf(incomeText) > this.TodayMaxIncome){
                    AccessibilityNodeInfo nodes = AccessibilityHelper.findNodeInfosById("com.yanhui.qktx:id/tv_income_count");
                    if(nodes != null && nodes.getParent() != null && nodes.getParent().getChildCount() > 1){
                        String ReadTime = nodes.getText().toString().trim();
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
                    AccessibilityNodeInfo nodes = AccessibilityHelper.findNodeInfosById("com.yanhui.qktx:id/tv_income_count");
                    if(nodes != null && nodes.getParent() != null && nodes.getParent().getChildCount() > 1){
                        String ReadTime = nodes.getText().toString().trim();
                        if(Float.valueOf(ReadTime) > 60 ){
                            this.TodayIncomeIsFinsh = true;
                            mToast.success("今日阅读时间过长("+ReadTime+")，结束工作");
                            mFunction.sleep(mConfig.clickSleepTime);
                            return true;
                        }
                    }
                    mToast.success("今日收益("+incomeText+")未封顶("+this.TodayMaxIncome+")，继续工作");
                    mFunction.sleep(mConfig.clickSleepTime);
                    return false;
                }
            }
        }catch (Exception ex){
            mToast.error(this.AppName+"收益检测错误:"+ex.getMessage());
        }


        return false;
    }

    //过来广告
    private boolean filterAdvertisement( AccessibilityNodeInfo nodeInfo ){
        if(nodeInfo.getClassName().equals("android.widget.RelativeLayout")){
            return true;
        }

        //资源ID目前测试每个版本都是一样的，暂且先这样

        AccessibilityNodeInfo node = AccessibilityHelper.findChildNodeInfosById(nodeInfo,"com.xiangzi.jukandian:id/item_artical_ad_three_bd_flag");
        if(node != null){
            RxToast.warning(mGlobal.mNavigationBarActivity,"过滤广告").show();
            return true;
        }
        return false;
    }

    //执行签到任务
    private void SignIn(){
        if(this.IsSign ){
            return;
        }
        mToast.success("签到");
        mGestureUtil.clickTab(5,4);
        this.IsSign = true;
    }

    //上传APP的最新收益情况
    @Override
    Boolean UploadIncome(){

        if(mCommonFunctionTask.judgeNodeIsHavingByResId("com.yanhui.qktx:id/tv_coin_count")){
            String curValue = AccessibilityHelper.getNodeInfosTextByResourceId("com.yanhui.qktx:id/tv_coin_count");
            if(!curValue.isEmpty()){
                float rmd = Float.valueOf(curValue)/10000;
                mUploadDataUtil.postIncomeRecord(this.AppName,rmd);
            }
            return true;
        }

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

        String curValue = AccessibilityHelper.getNodeInfosTextByResourceId("com.yanhui.qktx:id/tv_coin_count");
        if(!curValue.isEmpty()){
            float rmd = Float.valueOf(curValue)/10000;
            mUploadDataUtil.postIncomeRecord(this.AppName,rmd);
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
