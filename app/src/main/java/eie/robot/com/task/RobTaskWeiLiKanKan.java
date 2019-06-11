package eie.robot.com.task;

import android.view.accessibility.AccessibilityNodeInfo;
import java.util.ArrayList;
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

public class RobTaskWeiLiKanKan extends BaseRobotTask {

    private boolean isHotWordFinish = false;
    private List<String> HotWords = new ArrayList<>();

    /**
     * 构造函数
     */
    public RobTaskWeiLiKanKan() {
        super();
        this.AppName = "微鲤看看";
        this.TodayMaxIncome = 16000;
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
                if(mCommonTask.isCloseAppTask()){ break; }

                //每次进行一项任务时，都先恢复到首页
                //如果APP未打开，则会自行打开,如果最后还是无法打开，则跳出这次循环，重新来。
                if(!returnHome()){ continue; }

                //热词搜索
                HotWordSearchIncome();

                //领取时段奖励
                performTask_TimeSlotReward();

                //判断收益是否封顶
                if(JudgeGoldIncomeIsMax()){ break; }

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
        super.CloseTask();
        return false;
    }

    //-----------------------------------------------------------

    //看视频总任务
    @Override
    Boolean performTask_WatchVideo() {
        super.performTask_WatchVideo();

        int LoopCount =   1;    //mFunction.getRandom_1_3();
        while (LoopCount > 0){
            if(mCommonTask.isCloseAppTask()){ break;}

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

        //点击第三个功能列表
        mGestureUtil.clickTab(5,3);

        int NewsCount =   mFunction.getRandom_2_5();
        while (NewsCount > 0){
            if(mCommonTask.isCloseAppTask()){ break;}

            //看视频
            performTask_WatchVideo_2();

            if(!returnHome()){
                continue;
            }
            mToast.info("阅读完毕，首页滑动");
            mGestureUtil.scroll_up();
            NewsCount -- ;
        }
    }

    //看视频子任务二
    private void performTask_WatchVideo_2(){
        try{
            List<AccessibilityNodeInfo> nodes= AccessibilityHelper.findNodeInfosByIds("cn.weli.story:id/video_desc");
            if(nodes == null || nodes.size() < 1){
                return;
            }
            int loopCounter = nodes.size()-1;
            while (loopCounter > 0){
                loopCounter --;
                if(mCommonTask.isCloseAppTask()){ break; }

                //点击视频Node
                mGestureUtil.click(nodes.get(loopCounter).getParent().getParent());

                //滑动次数(随机)
                int SlideCounter = mFunction.getRandom_4_8();

                //开始滑动文章
                while (SlideCounter > 0) {
                    if(mCommonTask.isCloseAppTask()){ break; }

                    this.mCloseSystem();

                    if(!mCommonFunctionTask.judgeNodeIsHavingByText("写评论...")){
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
        }
    }

    //-----------------------------------------------------------

    //看新闻总任务
    @Override
    Boolean performTask_LookNews() {
        super.performTask_LookNews();
        int RefreshCount =   mFunction.getRandom_4_8();
        while (RefreshCount > 0){
            if(mCommonTask.isCloseAppTask()){ break;}

            //热词搜索
            HotWordSearchIncome();

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
            if(mCommonTask.isCloseAppTask()){ break; }

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
                if(mCommonTask.isCloseAppTask()){ break; }

                //准备阅读的Node
                AccessibilityNodeInfo XinWenNode = nodes.getChild(nodeCounter);

                if(filterAdvertisement(XinWenNode)){
                    continue;
                }

                mGestureUtil.click(XinWenNode);

                //滑动次数(随机10到20)
                int SwiperCount = mFunction.getRandom_8_14();
                //开始滑动文章
                while (SwiperCount > 0) {
                    if(mCommonTask.isCloseAppTask()){ break; }

                    this.mCloseSystem();

                    //判断是否处于页，如果不是则退出
                    if(mCommonFunctionTask.judgeIsNoWenZhangPageByText("语音说说你的想法")){
                        break;
                    }
                    //判断计时器是否存在
                    if(!mCommonFunctionTask.judgeNodeIsHavingByResId("cn.weli.story:id/rl_read_coin")){
                        break;
                    }
                    //点击全文阅读
                    mGestureUtil.clickByText("展开查看全文");

                    mIncomeTask.setLastIncomeTime();

                    //向上滑动
                    mGestureUtil.scroll_up();

                    //停止进行阅读
                    int sleepTime = mFunction.getRandom_2_4();
                    mFunction.sleep(sleepTime * 1000);
                    SwiperCount--;
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
        mGestureUtil.clickByResourceId("cn.weli.story:id/button1");
        //清理系统级的弹框
        super.mCloseSystem();

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
        //也许界面往上滑动了，尝试向下滑动一次
        mGestureUtil.scroll_down_half_screen();
        AccessibilityNodeInfo IncomeNode = null;
        try{
            //利用ID的方式
            AccessibilityNodeInfo nodeInfo = AccessibilityHelper.findNodeInfosById("cn.weli.story:id/text_today_coin");
            if(nodeInfo != null && nodeInfo.getClassName().equals("android.widget.TextView")){
                IncomeNode = nodeInfo;
            }else {
                //利用文本的方式
                nodeInfo = AccessibilityHelper.findNodeInfosByText("今日金币");
                if(nodeInfo != null){
                    nodeInfo = nodeInfo.getParent();
                    if(nodeInfo != null && nodeInfo.getClassName().equals("android.widget.LinearLayout")){
                        if(nodeInfo.getChildCount()>0 && nodeInfo.getChild(0).getClassName().equals("android.widget.TextView")){
                            IncomeNode = nodeInfo.getChild(0);
                        }
                    }
                }
            }
        }catch (Exception ex){
            mToast.error(this.AppName+"收益检测错误:"+ex.getMessage());
        }
        if(mCommonFunctionTask.judgeNodeIsHavingByText("今日已签到，")){
            this.IsSign = true;
        }
        if(IncomeNode != null){
            String incomeText = IncomeNode.getText().toString().trim();
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

            List<AccessibilityNodeInfo> nodesList = AccessibilityHelper.findNodeInfosByIds("cn.weli.story:id/tv_title");
            if(nodesList != null) {
                for (AccessibilityNodeInfo node : nodesList){
                    String Text = node.getText().toString();
                    char[] array = Text.toCharArray();
                    for (char text : array){
                        String Word = String.valueOf(text);
                        if(Word.isEmpty()){ continue; }
                        String IngoreString = ",，!！。.?？:：\"”“";
                        if(IngoreString.contains(Word)){
                            continue;
                        }
                        HotWords.add(String.valueOf(text));
                    }
                }
            }



            if(!mGestureUtil.clickByResourceId("cn.weli.story:id/et_search")){
                return;
            }

            int LoopCount = 12;
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
                        this.isHotWordFinish = true;
                        return;
                    }

                    AccessibilityNodeInfo clickNode = null;
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
                                    if(nodes.getChildCount() >= 3 ){
                                        clickNode = nodes.getChild(2);
                                    }
                                }
                            }
                        }
                    }
                    if(HotWords.size() < 1){
                        return;
                    }
                    String word = HotWords.get(0);
                    if(word.isEmpty()){
                        HotWords.remove(word);
                        continue;
                    }

                    if(clickNode == null){
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

                        HotWords.remove(word);

                        if(!mGestureUtil.clickByResourceId("cn.weli.story:id/view_search")){
                            continue;
                        }
                    }else {
                        mGestureUtil.click(clickNode);
                    }


                    if(!mCommonFunctionTask.loopJudgeNodeIsHavingByResId("cn.weli.story:id/view_close")){
                        continue;
                    }

                    mGestureUtil.scroll_up();

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

        //点击【头条】列表
        mGestureUtil.clickTab(5,2);

        //再次恢复到首页
        if(!returnHome()){
            return;
        }


        //点击签到，进入签到界面
        mGestureUtil.click(SizeOffset, mDeviceUtil.getStatusBarHeight()+SizeOffset);

        if(mCommonFunctionTask.judgeNodeIsHavingByText("去阅读")){
            this.CloseDialog();
            return;
        }

        this.CloseDialog();

        if(!mCommonFunctionTask.loopJudgeNodeIsHavingByResId("cn.weli.story:id/webView1")){
            return;
        }
        //点击签到按钮，因为界面是webView，用比例的方式
        mGestureUtil.click(mGlobal.mScreenWidth/2,(float) (mGlobal.mScreenHeight*0.148));

        AccessibilityHelper.performBack();

        this.CloseDialog();
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
