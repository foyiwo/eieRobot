package eie.robot.com.task;

import android.graphics.Rect;
import android.view.accessibility.AccessibilityNodeInfo;

import com.vondear.rxtool.view.RxToast;

import java.nio.channels.GatheringByteChannel;
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

public class RobTaskYouKanDian extends BaseRobotTask {

    private int SizeOffset = 40;

    private String HomeNavTitle1 = "com.memory.online:id/tab_textview";
    private String HomeNavTitle2 = "com.memory.online:id/tab_textview";
    /**
     * 构造函数
     */
    public RobTaskYouKanDian() {
        super();
        this.AppName = "优看点";
        this.TodayMaxIncome = 5000;
        this.TodayIncomeIsFinsh = false;
    }


    //执行刷单任务（领取时段奖励、定时刷新视频、查看文章）
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

                //判断收益是否封顶
                if(JudgeGoldIncomeIsMax()){
                    break;
                }

                //签到
                SignIn();

                //看视频
                performTask_WatchVideo();

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

    //看视频总任务
    @Override
    Boolean performTask_WatchVideo(){
        int LoopCount =  mFunction.getRandom_1_3();
        while (LoopCount > 0){
            if(mCommonTask.isCloseAppTask()){ break;}

            performTask_WatchVideo_1();

            LoopCount -- ;
        }
        return true;
    }

    //看视频子任务一
    private boolean performTask_WatchVideo_1(){

        if(!returnHome()){
            return false;
        }

        if(mCommonTask.isCloseAppTask()){ return false; }

        mToast.success("开启视频任务");

        //点击第二个功能列表
        mGestureUtil.clickTab(4,2);

        //向下滑动
        mGestureUtil.scroll_down(mGlobal.mScreenHeight/2,500);

        //看视频
        performTask_WatchVideo_2();

        mToast.info("阅读完毕，首页刷新");

        return true;
    }

    //看视频子任务二
    private boolean performTask_WatchVideo_2() {

        List<AccessibilityNodeInfo> nodes= AccessibilityHelper.findNodeInfosByIds("com.memory.online:id/tv_duration");

        if(nodes == null || nodes.size() < 1){
            return false;
        }

        int nodeCounter = nodes.size();
        while (nodeCounter > 0){
            nodeCounter --;
            if(mCommonTask.isCloseAppTask()){ break; }

            //准备阅读的Node
            AccessibilityNodeInfo VideoNode = nodes.get(nodeCounter);

            try{
                //判断时间是否够刷出奖励
                String[] durationTime = VideoNode.getText().toString().trim().split(":");
                if(durationTime.length < 2){ continue; }
                if(Integer.valueOf(durationTime[0]) < 1){ continue; }
            }catch (Exception ex){
                mToast.error("Task_KanShiPing:"+ex.getMessage());
                continue;
            }

            mGestureUtil.click(VideoNode.getParent());

            mFunction.click_sleep();
            mGestureUtil.click(mGlobal.mScreenWidth/2,SizeOffset*6);

            //滑动次数(随机10到20)
            int SwiperCount = mFunction.getRandom_6_12();
            //开始滑动文章
            while (SwiperCount > 0) {
                if(mCommonTask.isCloseAppTask()){ break; }
                super.mCloseSystem();
                //判断是否处于视频播放页，如果不是则退出
                if(mFunction.judgeAppIsHomeById(HomeNavTitle1,HomeNavTitle1)){
                    break;
                }

                if(IncomeSure()){ break; }

                mFunction.sleep(mConfig.WaitLauncherlTime);
                mGestureUtil.click(mGlobal.mScreenWidth/2,SizeOffset*6);
                SwiperCount--;
            }

            if(!returnHome()){
                return false;
            }

        }
        return true;
    }

    //-----------------------------------------------------------

    //看新闻总任务
    @Override
    Boolean performTask_LookNews() {
        super.performTask_LookNews();
        int RefreshCount =   mFunction.getRandom_4_8();
        while (RefreshCount > 0){
            if(mCommonTask.isCloseAppTask()){ break;}
            performTask_LookNews_1();
            mToast.success("倒数第"+RefreshCount+"轮新闻任务");
            mFunction.click_sleep();
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
        mGestureUtil.clickTab(4,1);
        mGestureUtil.scroll_up();

        //随机获取在本首页的滑动的次数
        int NewsCount =   mFunction.getRandom_8_14();
        while (NewsCount > 0){
            if(mCommonTask.isCloseAppTask()){ return false; }
            //进入文章页看新闻
            performTask_LookNews_2();
            if(!returnHome()){
                return false;
            }
            mToast.info("阅读完毕，首页刷新");
            mGestureUtil.scroll_up();
            mGestureUtil.scroll_up();
            NewsCount -- ;
        }

        return true;
    }

    //看新闻子任务二
    private boolean performTask_LookNews_2() {

        List<AccessibilityNodeInfo> nodes = AccessibilityHelper.findNodeInfosByIds("com.memory.online:id/tv_readmoney");
        if(nodes == null || nodes.size() <  1){
            return false;
        }
        int nodeCounter = nodes.size()-1;
        while (nodeCounter > 0){
            nodeCounter --;
            if(mCommonTask.isCloseAppTask()){ break; }
            //准备阅读的Node
            AccessibilityNodeInfo XinWenNode = nodes.get(nodeCounter);

            mGestureUtil.click(XinWenNode.getParent().getParent());


            //展开全文
            mGestureUtil.click(mGlobal.mScreenWidth/2,(float)(mGlobal.mScreenHeight*0.88),1000);
            //展开全文
            mGestureUtil.click(mGlobal.mScreenWidth/2,(float)(mGlobal.mScreenHeight*0.83),1000);
            //展开全文
            mGestureUtil.click(mGlobal.mScreenWidth/2,(float)(mGlobal.mScreenHeight*0.73),1000);
            //展开全文
            mGestureUtil.click(mGlobal.mScreenWidth/2,(float)(mGlobal.mScreenHeight*0.75),1000);
            //展开全文
            mGestureUtil.click(mGlobal.mScreenWidth/2,(float)(mGlobal.mScreenHeight*0.72),1000);
            //点开视频
            mGestureUtil.click(mGlobal.mScreenWidth/2,SizeOffset*6,1000);


            //滑动次数
            int SwiperCount = mFunction.getRandom_8_14();

            //开始滑动文章
            while (SwiperCount > 0) {
                if(mCommonTask.isCloseAppTask()){ break; }
                super.mCloseSystem();
                //判断是否处于视频播放页，如果不是则退出
                if(mFunction.judgeAppIsHomeById(HomeNavTitle1,HomeNavTitle1)){
                    break;
                }

                if(IncomeSure()){ break; }

                //向上滑动
                mGestureUtil.scroll_up();

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

    private boolean IncomeSure(){
        if(mCommonFunctionTask.judgeNodeIsHavingByText("双倍奖励阅读")){
            mFunction.sleep(2 * mConfig.clickSleepTime);
            mGestureUtil.clickInScreenCenter();
            int LoopCounter = 15;
            while (LoopCounter > 0){
                super.mCloseSystem();
                if(mCommonFunctionTask.judgeNodeIsHavingByText("朕知道了")){
                    mGestureUtil.clickByText("朕知道了");
                    AccessibilityHelper.performBack();
                    return true;
                }
                LoopCounter --;
                mFunction.click_sleep();
            }
        }

        AccessibilityNodeInfo node = AccessibilityHelper.findNodeInfosByText("朕知道了");
        if(node != null){
            Rect rect = new Rect();
            node.getBoundsInScreen(rect);
            if(rect.top < mGlobal.mScreenHeight-100){
                AccessibilityHelper.performClick(node);
                mFunction.sleep(1000);
                mIncomeTask.setLastIncomeTime();
                return true;
            }
        }

        if(mCommonFunctionTask.judgeNodeIsHavingByText("请按照下方提示步骤，逐一完成之后,即可领取奖励红包")){
            mGestureUtil.clickByResourceId("com.memory.online:id/x_recycler_view");
            int SlideCount = 5;
            while (SlideCount > 0){
                mGestureUtil.scroll_up();
                mFunction.sleep(3000);
                SlideCount--;
            }
            return true;
        }
        return false;
    }


    //-----------------------------------------------------------

    //关闭APP弹出的所有可能弹框
    private void CloseDialog(){
        mGestureUtil.clickByText("我知道了");
        mGestureUtil.clickByText("继续赚钱");
        mGestureUtil.clickByResourceId("com.memory.online:id/iv_close");
        super.mCloseSystem();
    }

    //判断今日的收益是否已经达到最大值
    private Boolean JudgeGoldIncomeIsMax(){

        if(!returnHome()){
            return false;
        }

        //点击【我的】列表
        mGestureUtil.click(4,2);

        //再次恢复到首页
        if(!returnHome()){
            return false;
        }

        //我的界面往上滑动了，先向下滑动一次
        mGestureUtil.scroll_down_100();

        mGestureUtil.clickByText("收益明细");

        AccessibilityNodeInfo IncomeNode = null;

        try{
            //利用ID的方式
            AccessibilityNodeInfo nodeInfo = AccessibilityHelper.findNodeInfosById("com.memory.online:id/tv_all_money_value");
            if(nodeInfo != null && nodeInfo.getClassName().equals("android.widget.TextView")){
                IncomeNode = nodeInfo;
            }else {
                //利用文本的方式
                nodeInfo = AccessibilityHelper.findNodeInfosByText("合计:");
                if(nodeInfo != null){
                    nodeInfo = nodeInfo.getParent();
                    if(nodeInfo != null && nodeInfo.getClassName().equals("android.widget.LinearLayout")){
                        nodeInfo = nodeInfo.getParent();
                        if(nodeInfo != null && nodeInfo.getClassName().equals("android.widget.LinearLayout")){
                            if(nodeInfo.getChildCount()>0 && nodeInfo.getChild(0).getClassName().equals("android.widget.TextView")){
                                IncomeNode = nodeInfo.getChild(0);
                            }
                        }
                    }
                }
            }
        }catch (Exception ex){
            mToast.error(this.AppName+"收益检测错误:"+ex.getMessage());
        }

        if(IncomeNode != null){
            String incomeText = IncomeNode.getText().toString().trim();
            incomeText = incomeText.trim().replace("金币","");
            String[] IncomeArray = incomeText.split(",");
            incomeText = "";
            for (String income : IncomeArray){
                incomeText = incomeText.concat(income);
            }

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

    //过滤广告
    private boolean filterAdvertisement( AccessibilityNodeInfo nodeInfo ){

        return false;
    }

    //执行签到任务
    private void SignIn(){
        if(!returnHome()){
            return;
        }
        mGestureUtil.click(mGlobal.mScreenWidth-SizeOffset,mGlobal.mScreenHeight-SizeOffset);
        //我的界面往上滑动了，先向下滑动一次
        mGestureUtil.scroll_down_half_screen();
        AccessibilityNodeInfo node = AccessibilityHelper.findNodeInfosById("com.memory.online:id/ll_feature2");
        if(node != null){
            mGestureUtil.click(node);
            node = AccessibilityHelper.findNodeInfosById("com.memory.online:id/tv_sign_btn");
            if(node != null && node.getText().toString().equals("已签到")){
                mToast.success("今日已签到");
            }if(node != null && node.getText().toString().equals("签到")){
                mGestureUtil.click(node);
                mToast.success("签到成功");
                AccessibilityHelper.performBack();
            }
        }
    }

    //回归到首页，如果APP未打开，则会自行打开
    private boolean returnHome(){
        return returnHomeById(HomeNavTitle1,HomeNavTitle2,new Runnable() {
            @Override
            public void run() {
                CloseDialog();
            }
        });
    }
}
