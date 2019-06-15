package eie.robot.com.task;
import android.view.accessibility.AccessibilityNodeInfo;
import java.util.ArrayList;
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

public class RobTaskWeiQuKan extends BaseRobotTask {

    private List<String> HotWords = new ArrayList<>();

    /**
     * 构造函数
     */
    public RobTaskWeiQuKan() {
        super();
        this.AppName = "微趣看";
        this.TodayMaxIncome = 4500;
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
                //每次进行一项任务时，都先恢复到首页
                //如果APP未打开，则会自行打开,如果最后还是无法打开，则跳出这次循环，重新来。
                if(!returnHome()){
                    continue;
                }
                //领取时段奖励
                performTask_TimeSlotReward();

                //判断收益是否封顶
                if(JudgeGoldIncomeIsMax()){
                    break;
                }

                //签到
                SignIn();

                //阅读文章
                performTask_LookNews();


            }
            catch (Exception ex){
                mToast.error(ex.getMessage());
            }
        }
        super.CloseTask();
        return false;
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

            performTask_LookNews_1();
            mToast.success("倒数第"+RefreshCount+"轮新闻任务");
            mFunction.click_sleep();
            RefreshCount -- ;
        }
        return true;
    }

    //看新闻子任务一
    Boolean performTask_LookNews_1() {
        if(!returnHome()){
            return false;
        }

        //点击第一个功能列表
        mGestureUtil.click(SizeOffset,mGlobal.mScreenHeight-SizeOffset);

        //随机获取在本首页的滑动的次数
        int NewsCount =   mFunction.getRandom_4_8();
        mGestureUtil.scroll_up();

        while (NewsCount > 0){
            if(mCommonTask.isCloseAppTask()){ break; }

            //打开官方阅读奖励
            openRedPacketsEnergy();

            //进入文章页看新闻
            performTask_LookNews_2();
            if(!returnHome()){
                continue;
            }

            mToast.info("阅读完毕，首页滑动");
            mGestureUtil.scroll_up();
            NewsCount -- ;
        }
        return true;
    }

    //看新闻子任务二
    Boolean performTask_LookNews_2() {
        AccessibilityNodeInfo nodes = AccessibilityHelper.findNodeInfosById("com.qudu.weiqukan:id/recyclerView");
        if(nodes == null || nodes.getChildCount() < 1){
            return false;
        }
        int nodeCounter = nodes.getChildCount()-1;
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

                super.mCloseSystem();

                if(mCommonFunctionTask.judgeNodeIsHavingByText("正在尝试开启")){
                    mGestureUtil.clickByText("拒绝");
                    continue;
                }
                //判断是否处于页，如果不是则退出
                if(mCommonFunctionTask.judgeIsNoWenZhangPageByText("评论送金币...")){
                    break;
                }
                //判断计时器是否存在
                if(!mCommonFunctionTask.judgeNodeIsHavingByResId("com.qudu.weiqukan:id/circleProgressbar")){
                    break;
                }

                //点击全文阅读(点击推荐阅读的上一点位置)
                if(!mCommonFunctionTask.judgeNodeIsHavingByText("分享文章被阅读奖励 ¥100金币/位")){
                    if(mCommonFunctionTask.judgeNodeIsHavingByResId("com.qudu.weiqukan:id/container_ad")){
                        mGestureUtil.clickNodeOffsizeTopById("com.qudu.weiqukan:id/container_ad");
                    }else {
                        mGestureUtil.clickNodeOffsizeTopById("com.qudu.weiqukan:id/recyclerView_recommend");
                    }

                }

                //向上滑动
                mGestureUtil.scroll_up();

                //设置最新收益时间
                mIncomeTask.setLastIncomeTime();

                //停止进行阅读
                int sleepTime = mFunction.getRandom_2_4();
                mFunction.sleep(sleepTime * 1000);
                SwiperCount--;
            }
            if(!returnHome()){
                return false;
            }
        }

        return true;
    }

    //-----------------------------------------------------------


    //领取时段奖励
    private boolean performTask_TimeSlotReward(){
        if(!returnHome()){
            return false;
        }

        //点击第一个功能列表
        mGestureUtil.click(SizeOffset,mGlobal.mScreenHeight-SizeOffset);
        mToast.success("时段奖励任务");
        mGestureUtil.clickByResourceId("com.qudu.weiqukan:id/iv_countDown_finish");

        this.CloseDialog();
        return true;
    }

    //关闭APP弹出的所有可能弹框
    private void CloseDialog(){

        //关闭按钮
        mGestureUtil.clickByResourceId("com.qudu.weiqukan:id/iv_close");

        AccessibilityNodeInfo node = AccessibilityHelper.findNodeInfosByText("我知道了");
        if(node != null){
            mGestureUtil.click(node);
        }

        node = AccessibilityHelper.findNodeInfosByText("分享立赚");
        if(node != null){
            mGestureUtil.click(node);
            mFunction.sleep(mConfig.clickSleepTime);
            AccessibilityHelper.performBack();
            mFunction.sleep(mConfig.clickSleepTime);
        }

        node = AccessibilityHelper.findNodeInfosByText("忽略");
        if(node != null){
            mGestureUtil.click(node);
        }

        //判断是否点多了，触发了【退出APP确认框】
        AccessibilityNodeInfo NodeInfo3 = AccessibilityHelper.findNodeInfosByText("确认退出聚看点？");
        AccessibilityNodeInfo NodeInfo4 = AccessibilityHelper.findNodeInfosByText("继续赚钱");
        if ( NodeInfo3 != null && NodeInfo4 != null ) {
            mGestureUtil.click(NodeInfo4);
        }
        node = AccessibilityHelper.findNodeInfosById("com.xiangzi.jukandian:id/close_img_layout");
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
        mGestureUtil.clickTab(4,4);

        //再次恢复到首页
        if(!returnHome()){
            return false;
        }

        //我的界面往上滑动了，先向下滑动一次
        mGestureUtil.scroll_down_half_screen();

        AccessibilityNodeInfo IncomeNode = null;

        try{
            //利用ID的方式
            AccessibilityNodeInfo nodeInfo = AccessibilityHelper.findNodeInfosById("com.qudu.weiqukan:id/tv_goldCount");
            if(nodeInfo != null && nodeInfo.getClassName().equals("android.view.View")){
                IncomeNode = nodeInfo;
            }else {
                //利用文本的方式
                nodeInfo = AccessibilityHelper.findNodeInfosByText("金币");
                if(nodeInfo != null){
                    nodeInfo = nodeInfo.getParent();
                    if(nodeInfo != null && nodeInfo.getClassName().equals("android.widget.LinearLayout")){
                        if(nodeInfo.getChildCount()>0 && nodeInfo.getChild(0).getClassName().equals("android.view.View")){
                            IncomeNode = nodeInfo.getChild(0);
                        }
                    }
                }
            }
        }catch (Exception ex){
            mToast.error(this.AppName+"收益检测错误:"+ex.getMessage());
        }

        if(IncomeNode != null){
            String incomeText = IncomeNode.getContentDescription().toString().trim();
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
    private boolean HotWordSearchIncome(){
        if(this.isHotWordFinish){
            return true;
        }
        Boolean res = false;
        try{
            if(!returnHome()){
                return false;
            }

            //点击第一个功能列表
            mGestureUtil.clickTab(5,1);

            //点击【搜索】按钮
            if(!mGestureUtil.clickByResourceId("com.qudu.weiqukan:id/iv_search")){
                return false;
            }

            int HotCount = 13;
            while (HotCount > 0){
                HotCount --;
                if(!mCommonFunctionTask.judgeNodeIsHavingByText("任务说明")){
                    return false;
                }

                mGestureUtil.clickByText("可领取100");
                mGestureUtil.clickByText("可领取300");
                mGestureUtil.clickByText("可领取400");

                if(!mCommonFunctionTask.judgeNodeIsHavingByText("去搜索400")){
                    this.isHotWordFinish = true;
                    return false;
                }
                AccessibilityNodeInfo clickNode = null;
                List<AccessibilityNodeInfo> nodes = AccessibilityHelper.findNodeInfosByIds("com.qudu.weiqukan:id/tv_word");
                if(nodes != null && nodes.size() > 0){
                    for( AccessibilityNodeInfo node : nodes ){
                        if(node == null || node.getText().toString().isEmpty()){ continue; }
                        if(!HotWords.contains(node.getText().toString())){
                            String NodeText = node.getText().toString();
                            if(NodeText.isEmpty()){
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
                                HotWords.add(node.getText().toString());
                                if(nodes.size() >= 3 ){
                                    clickNode = nodes.get(2);
                                }
                            }
                        }
                    }
                }
                if(HotWords.size() < 1){
                    return false;
                }
                String word = HotWords.get(0);
                if(word.isEmpty()){
                    return false;
                }

                if(clickNode == null){
                    AccessibilityNodeInfo nodeInfo = AccessibilityHelper.findNodeInfosById("com.qudu.weiqukan:id/et_input");
                    if(nodeInfo == null){
                        return false;
                    }

                    if(!mCommonFunctionTask.pasteTextToNode(nodeInfo,word.substring(0,word.length()/mFunction.getRandom_1_3()))){
                        return false;
                    }
                    HotWords.remove(word);

                    if(!mGestureUtil.clickByResourceId("com.qudu.weiqukan:id/iv_search")){
                        return false;
                    }
                }else {
                    mGestureUtil.click(clickNode);
                }

                if(!mCommonFunctionTask.loopJudgeNodeIsHavingByText("点击任意一条阅读10秒即可完成任务")){
                    return false;
                }
                mGestureUtil.click(mGlobal.mScreenWidth/2,mGlobal.mScreenHeight/3);

                if(!mCommonFunctionTask.loopJudgeNodeIsHavingByText("滑动并阅读10秒即可完成任务")){
                    return false;
                }

                mGestureUtil.scroll_up();

                if(mCommonFunctionTask.loopJudgeNodeIsHavingByText(15,"任务完成")){
                    res = true;
                }
                mGestureUtil.clickByResourceId("com.qudu.weiqukan:id/iv_close");
                mIncomeTask.setLastIncomeTime();

            }
        }catch (Exception ex){
            mToast.error("HotWordSearchIncome:"+ex.getMessage());
        }

        return res;
    }

    //过滤广告
    private boolean filterAdvertisement( AccessibilityNodeInfo nodeInfo ){
        AccessibilityNodeInfo node = AccessibilityHelper.findChildNodeInfosByText("广告");
        if(node != null){
            return true;
        }
        if(nodeInfo.getClassName() != null && nodeInfo.getClassName().equals("android.widget.RelativeLayout")){
            return true;
        }
        return false;
    }

    //执行签到任务
    private boolean SignIn(){

        if(this.IsSign){
            return true;
        }

        if(!returnHome()){
            return false;
        }
        //点击【任务】列表
        mGestureUtil.click((mGlobal.mScreenWidth/4)*3-SizeOffset,mGlobal.mScreenHeight-SizeOffset);

        //再次恢复到首页
        if(!returnHome()){
            return false;
        }

        //我的界面往上滑动了，先向下滑动一次
        mGestureUtil.scroll_down_half_screen();

        mGestureUtil.click((mGlobal.mScreenWidth/2),(float) (mGlobal.mScreenHeight*0.261));

        mGestureUtil.click((mGlobal.mScreenWidth/4),(float) (mGlobal.mScreenHeight*0.829));

        AccessibilityHelper.performBack();

        this.CloseDialog();
        this.IsSign = true;
        return false;
    }

    //打开能量红包
    private boolean openRedPacketsEnergy(){
        mGestureUtil.clickByText("领金币");
        return true;
    }

    //回归到首页，如果APP未打开，则会自行打开
    private boolean returnHome(){
        return returnHome("首页","我的",new Runnable() {
            @Override
            public void run() {
                CloseDialog();
            }
        });
    }
}
