package eie.robot.com.task;

import android.view.accessibility.AccessibilityNodeInfo;
import java.util.ArrayList;
import java.util.List;
import eie.robot.com.accessibilityservice.AccessibilityHelper;
import eie.robot.com.common.mCommonFunctionTask;
import eie.robot.com.common.mCommonTask;
import eie.robot.com.common.mConfig;
import eie.robot.com.common.mData;
import eie.robot.com.common.mDeviceUtil;
import eie.robot.com.common.mFunction;
import eie.robot.com.common.mGestureUtil;
import eie.robot.com.common.mGlobal;
import eie.robot.com.common.mIncomeTask;
import eie.robot.com.common.mToast;

public class RobTaskWeiLiKanKan extends BaseRobotTask {

    private List<String> HotWords = new ArrayList<>();

    //构造函数
    public RobTaskWeiLiKanKan() {
        super();
        this.AppName = "微鲤看看";
        this.TodayMaxIncome = 7000;
        this.TodayIncomeIsFinsh = false;
    }

    //执行刷单任务（领取时段奖励、定时刷新视频、查看文章）
    @Override
    public boolean StartTask()  {
        super.StartTask();
        while (mCommonTask.isOpenAppTask()){
            try {
                if(mCommonTask.isCloseAppTask() || this.TodayIncomeIsFinsh){ break; }

                //每次进行一项任务时，都先恢复到首页
                //如果APP未打开，则会自行打开,如果最后还是无法打开，则跳出这次循环，重新来。
                if(!returnHome()){ continue; }

                //领取时段奖励
                performTask_TimeSlotReward();

                //判断收益是否封顶（每次重启的时候查一次）
                this.TaskCounter = this.TaskCounterDefaultValue;
                //if(JudgeGoldIncomeIsMax()){ break; }

                //签到
                SignIn();

                if(mFunction.getRandomBooleanOffsetTrue()){
                    //阅读文章
                    performTask_LookNews();
                }else {
                    //阅读视频
                    performTask_WatchVideo();
                }
            }
            catch (Exception ex){
                mToast.error(ex.getMessage());
            }
        }
        super.CloseTask();
        return false;
    }

    //-----------------------------------------------------------

    //看视频总任务
    @Override
    Boolean performTask_WatchVideo() {
        super.performTask_WatchVideo();
        if(!returnHome()){ return false; }

        int LoopCount =   mFunction.getRandom_1_3();
        while (LoopCount > 0){
            if(mCommonTask.isCloseAppTask() || this.VideoIsFinish){ break;}

            //点击第三个功能列表
            mGestureUtil.clickTab(5,3);

            //执行看视频
            performTask_WatchVideo_1();

            LoopCount -- ;
        }
        return true;
    }

    //看视频子任务一
    private void performTask_WatchVideo_1(){
        if(!returnHome()){
            return;
        }
        mToast.success("开启视频任务");

        int NewsCount =   mFunction.getRandom_2_4();
        while (NewsCount > 0){
            if(mCommonTask.isCloseAppTask() || this.VideoIsFinish){ break;}



            //看视频
            performTask_WatchVideo_2();

            if(!returnHome()){
                continue;
            }
            mToast.info("阅读完毕，视频首页刷新");
            //点击第三个功能列表
            mGestureUtil.clickTab(5,3);
            mGestureUtil.clickTab(5,3);
            NewsCount -- ;
        }
    }

    //看视频子任务二
    private void performTask_WatchVideo_2(){
        try{
            List<AccessibilityNodeInfo> nodes= AccessibilityHelper.findNodeInfosByIds("cn.weli.story:id/tv_play_count");
            if(nodes == null || nodes.size() < 1){
                return;
            }
            int loopCounter = nodes.size();
            while (loopCounter > 0){
                loopCounter --;
                if(mCommonTask.isCloseAppTask() || this.VideoIsFinish){ break; }

                //点击视频Node
                mGestureUtil.performClick(nodes.get(loopCounter));


                //滑动次数(随机)
                int SlideCounter = mFunction.getRandom_4_8();

                //开始滑动文章
                while (SlideCounter > 0) {
                    if(mCommonTask.isCloseAppTask() || this.VideoIsFinish){ break; }

                    this.mCloseSystem();

                    if(!mCommonFunctionTask.judgeNodeIsHavingByText("说说你的想法")){
                        AccessibilityHelper.performBack();
                        if(!mCommonFunctionTask.judgeNodeIsHavingByText("说说你的想法")){
                            break;
                        }
                    }
                    if(mCommonFunctionTask.judgeNodeIsHavingByText("重播")){
                        break;
                    }
                    mIncomeTask.setLastIncomeTime();

                    mFunction.sleep(mConfig.WaitLauncherlTime);
                    SlideCounter--;
                }

                AccessibilityHelper.performBack();
            }
        }catch (Exception ex){
            mToast.error("performTask_WatchVideo_2:"+ex.getMessage());
            mFunction.click_sleep();
        }
    }

    //-----------------------------------------------------------

    //看新闻总任务
    @Override
    Boolean performTask_LookNews() {
        super.performTask_LookNews();
        int RefreshCount =   mFunction.getRandom_1_3();
        while (RefreshCount > 0){
            if(mCommonTask.isCloseAppTask() || this.ArticleIsFinish ){ break; }

            //热词搜索
            HotWordSearchIncome();

            //时段奖励
            performTask_TimeSlotReward();

            //子任务
            performTask_LookNews_1();

            mToast.success("倒数第"+RefreshCount+"轮新闻任务");
            mFunction.click_sleep();
            RefreshCount -- ;
        }
        return true;
    }

    //看新闻子任务一
    private void performTask_LookNews_1(){
        if(!returnHome()){
            return;
        }
        //点击第二个功能列表
        mGestureUtil.clickTab(5,2);

        //随机获取在本首页的滑动的次数
        int LoopCount =   mFunction.getRandom_4_8();
        while (LoopCount > 0){
            if(mCommonTask.isCloseAppTask() || this.ArticleIsFinish){ break; }

            //首页领金币
            openRedPacketsEnergy();

            //进入文章页看新闻
            performTask_LookNews_2();

            if(!returnHome()){
                continue;
            }

            mToast.info("阅读完毕，首页滑动");
            mGestureUtil.scroll_up();
            LoopCount -- ;
        }
    }

    //看新闻子任务二
    private void performTask_LookNews_2() {
        try{
            AccessibilityNodeInfo nodes = AccessibilityHelper.findNodeInfosById("cn.weli.story:id/recyclerView");
            if(nodes == null || nodes.getChildCount() < 1){
                return;
            }
            int nodeCounter = nodes.getChildCount();
            while (nodeCounter > 0){
                nodeCounter --;
                if(mCommonTask.isCloseAppTask() || this.ArticleIsFinish){ break; }

                //准备阅读的Node
                AccessibilityNodeInfo XinWenNode = nodes.getChild(nodeCounter);

                if(filterAdvertisement(XinWenNode)){
                    continue;
                }

                mGestureUtil.click(XinWenNode);

                //判断金币收益
                if(JudgeGoldIncomeIsMax()){ return; }

                //滑动次数(随机10到20)
                int SwiperCount = mFunction.getRandom_6_12();
                //开始滑动文章
                while (SwiperCount > 0) {
                    if(mCommonTask.isCloseAppTask() || this.ArticleIsFinish){ break; }

                    this.mCloseSystem();

                    //判断是否处于页，如果不是则退出
                    if(!mCommonFunctionTask.judgeNodeIsHavingByText("语音说说你的想法")){
                        AccessibilityHelper.performBack();
                        if(!mCommonFunctionTask.judgeNodeIsHavingByText("语音说说你的想法")){
                            break;
                        }
                    }
                    //判断计时器是否存在
                    if(!mCommonFunctionTask.judgeNodeIsHavingByResId("cn.weli.story:id/rl_read_coin")){
                        break;
                    }
                    //点击全文阅读
                    if(mGestureUtil.clickByText("展开查看全文")){
                        mToast.success("点击展开查看全文");
                    }
                    mIncomeTask.setLastIncomeTime();

                    //向上滑动
                    mGestureUtil.scroll_up();

                    //停止进行阅读
                    int sleepTime = mFunction.getRandom_2_4();
                    mFunction.sleep(sleepTime * 1000);
                    SwiperCount--;

                }

                if(mFunction.getRandomBoolean()){
                    SendingComment();
                }
                AccessibilityHelper.performBack();
            }
        }catch (Exception ex){
            mToast.error_sleep("performTask_LookNews_2:"+ex.getMessage());
        }

    }

    //过滤广告
    private boolean filterAdvertisement( AccessibilityNodeInfo nodeInfo ){
        if(nodeInfo == null){
            return true;
        }
        if(nodeInfo.getClassName() != null && nodeInfo.getClassName().equals("android.widget.RelativeLayout")){
            return true;
        }
        AccessibilityNodeInfo node = AccessibilityHelper.findChildNodeInfosByText(nodeInfo,"广告");
        if(node != null){
            return true;
        }
        return false;
    }

    //-----------------------------------------------------------


    //输入评论
    private boolean SendingComment(){

        if(this.PingLunIsFinish){
            return true;
        }

        mGestureUtil.scroll_left();


        AccessibilityNodeInfo nodeInfo = AccessibilityHelper.findNodeInfosByEqualText("说说你的想法");
        if(nodeInfo != null){
            mGestureUtil.clickByCoordinate(nodeInfo);
        }else {
            nodeInfo = AccessibilityHelper.findNodeInfosByEqualText("请输入...");
            if(nodeInfo != null){
                mGestureUtil.clickByCoordinate(nodeInfo);
            }
        }

        nodeInfo = AccessibilityHelper.findNodeInfosByClassName("android.widget.EditText");
        if(nodeInfo == null) return false;

        mCommonFunctionTask.pasteTextToNode(nodeInfo, mData.getRandomSChatSpeaking());

        mFunction.click_sleep();

        mGestureUtil.clickByText("发送");

        nodeInfo = AccessibilityHelper.findNodeInfosById("cn.weli.story:id/iv_button");
        if(nodeInfo != null){
            //连击
            mGestureUtil.continuedClick(nodeInfo,mFunction.getRandom_50_100()-20);
        }

        return false;
    }


    //领取时段奖励
    private void performTask_TimeSlotReward(){
        if(mCommonTask.isCloseAppTask()){ return; }

        if(!returnHome()){
            return;
        }

        //点击第二个Tab
        mGestureUtil.clickTab(5,2);
        AccessibilityNodeInfo nodeInfo = AccessibilityHelper.findNodeInfosById("cn.weli.story:id/tv_treasure_box");

        if(nodeInfo != null && nodeInfo.getText().toString().contains("+")){
            mGestureUtil.click(nodeInfo);
            mToast.success("时段奖励任务获取成功");
        }else {
            mToast.success("时段奖励任务已获取过");
        }
        this.CloseDialog();
    }

    //关闭APP弹出的所有可能弹框
    private void CloseDialog(){
        //关闭按钮
        mGestureUtil.clickByResourceId("cn.weli.story:id/image_close");
        mGestureUtil.clickByResourceId("cn.weli.story:id/ic_close");
        mGestureUtil.clickByResourceId("cn.weli.story:id/iv_close");
        mGestureUtil.clickByResourceId("cn.weli.story:id/button1");


        //点击收红包微鲤福利
        mGestureUtil.clickByResourceId("cn.weli.story:id/iv_take");

        //点击收到微鲤看看福利之后的【知道了】
        mGestureUtil.clickByResourceId("cn.weli.story:id/tv_ok");
        mGestureUtil.clickByText("知道了");

        //清理系统级的弹框
        super.mCloseSystem();

    }

    //判断今日的收益是否已经达到最大值
    private Boolean JudgeGoldIncomeIsMax(){
        this.TaskCounter ++;
        if(TaskCounter < this.TaskCounterDefaultValue){
            return false;
        }
        try{
            //判断计时圈是否存在
            if(!mCommonFunctionTask.judgeNodeIsHavingByResId("cn.weli.story:id/iv_coin")){
                mFunction.click_sleep();
                if(!mCommonFunctionTask.judgeNodeIsHavingByResId("cn.weli.story:id/iv_coin")){
                    return false;
                }
            }
            //点击计时圈
            if(!mGestureUtil.clickByResourceId("cn.weli.story:id/iv_coin")){
                return false;
            }

            mFunction.click_sleep();

            AccessibilityNodeInfo ArticleIncomeNode = AccessibilityHelper.findWebViewNodeInfosByText("今日已阅读");
            if(ArticleIncomeNode != null && ArticleIncomeNode.getParent() != null && ArticleIncomeNode.getParent().getClassName().equals("android.webkit.WebView")){
                AccessibilityNodeInfo nodeInfos = ArticleIncomeNode.getParent();
                if(nodeInfos.getChildCount() > 1 && nodeInfos.getChild(1).getChildCount()>1){
                    nodeInfos = nodeInfos.getChild(1);
                    String  ArticleIncome = AccessibilityHelper.getNodeInfosTextByNode(nodeInfos.getChild(0));
                    if(Float.valueOf(ArticleIncome) >= 50){
                        this.TodayIncomeIsFinsh = true;
                        mToast.success("今日收益已封顶");
                        mFunction.sleep(mConfig.clickSleepTime);
                        this.TaskCounter = 0;
                        return true;
                    }else {
                        mToast.success("今日收益未封顶(文章:"+ArticleIncome+"分钟),继续工作");
                        mFunction.click_sleep();
                        this.TaskCounter = 0;
                        return false;
                    }
                }
            }


            //判断是否已经属于受益页
            AccessibilityNodeInfo node = AccessibilityHelper.findWebViewNodeInfosByText("阅读60分钟");
            AccessibilityNodeInfo nodeInfo = AccessibilityHelper.findNodeInfosByText("查看金币明细");
            if(node == null && nodeInfo == null){
                return false;
            }

            int IncomeLocation = 1;//1：表示处于文章界面，2：表示处于视频界面
            if(node != null) IncomeLocation = 1;    //文章界面，webview
            if(nodeInfo != null) IncomeLocation = 2;//视频界面, 原生

            String ArticleIncome = "";
            String ArticleIncomeCopy = "";
            if(IncomeLocation == 1){
                ArticleIncome = AccessibilityHelper.getWebNodeInfosTextByText("阅读文章");
            }
            if(IncomeLocation == 2){
                ArticleIncome = AccessibilityHelper.getNodeInfosTextByText("阅读文章");
                ArticleIncomeCopy = ArticleIncome;
            }
            if(ArticleIncome==null) return false;

            String[] articleIncomeArrays = ArticleIncome.split("分钟");
            if(articleIncomeArrays.length < 2) return false;
            ArticleIncome = articleIncomeArrays[0].replace("阅读文章","");
            if(Float.valueOf(ArticleIncome) >= 45){
                this.ArticleIsFinish = true;
            }

            String VideoIncome = "";
            if(IncomeLocation == 1){
                VideoIncome = AccessibilityHelper.getWebNodeInfosTextByText("观看视频");
            }
            if(IncomeLocation == 2){
                String[] VideoIncomeA = ArticleIncomeCopy.split("金币");
                if(VideoIncomeA.length < 2) return false;
                VideoIncome = VideoIncomeA[1];
            }
            if(VideoIncome == null) return false;

            String[] VideoIncomeArrays = VideoIncome.split("分钟");
            if(VideoIncomeArrays.length < 2) return false;
            VideoIncome = VideoIncomeArrays[0].replace("观看视频","");
            VideoIncome = VideoIncome.replace("\n","");
            if(Float.valueOf(VideoIncome) >= 40){
                this.VideoIsFinish = true;
            }

            if(this.ArticleIsFinish && this.VideoIsFinish){
                this.TodayIncomeIsFinsh = true;
                mToast.success("今日收益已封顶");
                mFunction.sleep(mConfig.clickSleepTime);
                this.TaskCounter = 0;

                return true;
            }else {
                mToast.success("今日收益未封顶(文章:"+ArticleIncome+"分钟，视频:"+VideoIncome+"分钟),继续工作");
                mFunction.click_sleep();

                this.TaskCounter = 0;
                return false;
            }
        }catch (Exception ignored){
            mToast.error_sleep(ignored.getMessage());
        }
        return false;
    }

    //热词搜索获取奖励
    private void HotWordSearchIncome(){
        if(this.isHotWordFinish){
            return;
        }
        try{
            if(!returnHome()){
                return;
            }
            //点击第二个功能列表
            mGestureUtil.clickTab(5,2);

            mGestureUtil.scroll_up();

            List<AccessibilityNodeInfo> nodesList = AccessibilityHelper.findNodeInfosByIds("cn.weli.story:id/tv_title");
            if(nodesList != null && nodesList.size() > 1) {
                nodesList.remove(0);
                for (AccessibilityNodeInfo node : nodesList){
                    String Text = node.getText().toString();
                    char[] array = Text.toCharArray();
                    for (char text : array){
                        String Word = String.valueOf(text);
                        if(Word.isEmpty()){ continue; }
                        String IngoreString = "1234567890【】,，!！。.?？:：\"”“";
                        if(IngoreString.contains(Word)){
                            continue;
                        }
                        if(HotWords.size() > 30) break;
                        HotWords.add(HotWords.size(),String.valueOf(text));
                    }
                }
            }
            if(!mGestureUtil.clickByResourceId("cn.weli.story:id/et_search")){
                return;
            }

            AccessibilityNodeInfo nodes = AccessibilityHelper.findNodeInfosById("cn.weli.story:id/tabFlowLayout");
            if(nodes != null && nodes.getChildCount() > 0){
                for( int i=0; i < nodes.getChildCount();i++ ){
                    if(nodes.getChild(i) == null || nodes.getChild(i).getText().toString().isEmpty()){ continue; }
                    if(!HotWords.contains(nodes.getChild(i).getText().toString())){
                        String NodeText = nodes.getChild(i).getText().toString();
                        if(NodeText.isEmpty()){
                            continue;
                        }
                        if(NodeText.equals("领今日福利")){
                            continue;
                        }
                        if(NodeText.equals("抽现金红包")){
                            continue;
                        }
                        boolean isAdd = true;
                        for (String hotWords : HotWords){
                            if(hotWords.startsWith(NodeText.substring(0,1))){
                                isAdd = false;
                                break;
                            }
                        }
                        if(isAdd){
                            HotWords.add(nodes.getChild(i).getText().toString());
                        }
                    }
                }
            }


            int LoopCount = mFunction.getRandom_6_12();
            while (LoopCount > 0){
                try{
                    if(mCommonTask.isCloseAppTask()){ break; }
                    LoopCount --;
                    if(!mCommonFunctionTask.loopJudgeNodeIsHavingByText("任务说明")){
                        return;
                    }
                    mGestureUtil.clickByText("领取+400");
                    mGestureUtil.clickByText("领取+500");
                    mGestureUtil.clickByText("领取+600");

                    if(!mCommonFunctionTask.judgeNodeIsHavingByText("去搜索+600")){
                        AccessibilityHelper.performBack();
                        mToast.success("热词搜索获取奖励完成");
                        this.isHotWordFinish = true;
                        return;
                    }

                    AccessibilityNodeInfo clickNode = null;

                    if(HotWords.size() < 1){
                        return;
                    }
                    String word = HotWords.get(0);
                    for (String hotWords : HotWords){
                        char[] st = hotWords.toCharArray();
                        if(st.length>1){
                            word = hotWords;
                        }
                    }

                    if(word.isEmpty()){
                        HotWords.remove(word);
                        continue;
                    }

                    if(clickNode == null){
                        HotWords.remove(word);
                        AccessibilityNodeInfo nodeInfo = AccessibilityHelper.findNodeInfosById("cn.weli.story:id/edt_tool_search");
                        if(nodeInfo == null){
                            return;
                        }
                        int WordLength = 0;
                        if(word.length() <= 3){
                            WordLength = word.length();
                        }else {
                            WordLength = word.length()/mFunction.getRandom_1_3();
                        }
                        String subWord = word.substring(0,WordLength);
                        if(!mCommonFunctionTask.pasteTextToNode(nodeInfo,subWord)){
                            return;
                        }
                        if(!mGestureUtil.clickByResourceId("cn.weli.story:id/view_search")){
                            continue;
                        }
                    }else {
                        mGestureUtil.click(clickNode);
                    }


                    if(!mCommonFunctionTask.loopJudgeNodeIsHavingByResId("cn.weli.story:id/view_close")){
                        continue;
                    }

                    mFunction.sleep(mConfig.WaitLauncherlTime);

                    mGestureUtil.scroll_up_screen();

                    mGestureUtil.clickInScreenCenter();

                    float x = mGlobal.mScreenWidth/2;
                    float y = mGlobal.mScreenHeight/2-100;
                    mGestureUtil.click(x,y,mConfig.clickSleepTime);

                    int Counter = 5;
                    while (Counter >0 ){
                        Counter --;
                        mGestureUtil.scroll_up();
                        mFunction.click_sleep();
                    }
                    mGestureUtil.clickByResourceId("cn.weli.story:id/view_close");
                    mIncomeTask.setLastIncomeTime();

                }catch (Exception ex){
                    mToast.success_sleep(ex.getMessage());
                }
            }
        }catch (Exception ex){
            mToast.error("HotWordSearchIncome:"+ex.getMessage());
        }
    }

    //执行签到任务
    private void SignIn(){
        if(mCommonTask.isCloseAppTask()){ return; }

        if(this.IsSign){
            mToast.success("今日已签到");
            return;
        }
        if(!returnHome()){
            return;
        }
        if(!mCommonFunctionTask.judgeNodeIsHavingByText("今日金币")){
            //点击【头条】列表
            mGestureUtil.clickTab(5,5);
        }

        //再次恢复到首页
        if(!returnHome()){
            return;
        }

        if(!mCommonFunctionTask.judgeNodeIsHavingByText("今日金币")){
            return;
        }
        if(mCommonFunctionTask.judgeNodeIsHavingByText("今日已签到")){
            this.IsSign = true;
            mToast.success("今天已签到");
            mFunction.click_sleep();
            return;
        }
        //点击签到，进入签到界面
        if(!mGestureUtil.clickByResourceId("cn.weli.story:id/ll_not_sign")){
            return;
        }

        mFunction.click_sleep();
        this.CloseDialog();

        if(!mCommonFunctionTask.loopJudgeNodeIsHavingByResId("cn.weli.story:id/webView1")){
            return;
        }
        if(!mGestureUtil.clickWebNodeByText("立即签到")){
            return;
        }
        this.CloseDialog();
        AccessibilityHelper.performBack();

        this.IsSign = true;
    }

    //打开能量红包
    private void openRedPacketsEnergy(){
        mGestureUtil.clickByText("领金币");
    }

    //回归到首页，如果APP未打开，则会自行打开
    private boolean returnHome(){
        return returnHomeById("cn.weli.story:id/iv_tab_0","cn.weli.story:id/iv_tab_4",new Runnable() {
            @Override
            public void run() {
                CloseDialog();
            }
        });
    }
}
