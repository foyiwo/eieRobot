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

public class RobTaskTouTiaoJingXuan extends BaseRobotTask {


    //构造函数
    public RobTaskTouTiaoJingXuan() {
        super();
        this.AppName = "头条精选";
        this.TodayMaxIncome = 5000;
        this.TodayIncomeIsFinsh = false;
    }

    //总任务
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
        int RefreshCount =  3;
        while (RefreshCount > 0){
            if(mCommonTask.isCloseAppTask() || this.ShiPingTouPiaoIsFinish){ break;}

            performTask_WatchVideo_1();

            RefreshCount -- ;
        }
        return true;
    }

    //看视频子任务一
    private boolean performTask_WatchVideo_1(){

        if(!returnHome()){
            return false;
        }

        mGestureUtil.clickTab(4,2);

        //判断是否属于视频列表页
        AccessibilityNodeInfo nodeInfo = AccessibilityHelper.findNodeInfosById("com.deshang.ttjx:id/recycler_view");
        if(nodeInfo == null){
            return false;
        }

        //向下滑动刷新视频
        mGestureUtil.scroll_down(mGlobal.mScreenHeight,500);

        mFunction.click_sleep();

        //随便点击一个视频，进入看视频界面
        mGestureUtil.click(SizeOffset*2,mGlobal.mScreenHeight/3);

        //判断是否属于看视频页
        nodeInfo = AccessibilityHelper.findNodeInfosById("com.deshang.ttjx:id/like");
        if(nodeInfo == null){
            return false;
        }
        mToast.success("视频任务");

        int NewsCount =   3;
        while (NewsCount > 0){

            if(mCommonTask.isCloseAppTask()){ break;}

            //规避广告页
            if(!mCommonFunctionTask.judgeNodeIsHavingByResId("com.deshang.ttjx:id/like")){
                mGestureUtil.scroll_up_30();
                continue;
            }
            //设置收益的最新时间
            mIncomeTask.setLastIncomeTime();

            //点击喜欢
            //mGestureUtil.click((float) (mGlobal.mScreenWidth-SizeOffset*1.5),(float)(mGlobal.mScreenHeight*0.634));
            mGestureUtil.clickByResourceId("com.deshang.ttjx:id/like");

            //看视频时间
            int VideoInterval = mFunction.getRandom_6_12();
            mToast.success("视频任务："+VideoInterval+"秒");
            mFunction.sleep( VideoInterval * 1000);
            NewsCount--;
            if(NewsCount>0){
                mGestureUtil.scroll_up_30();
            }
        }
        return true;
    }

    //-----------------------------------------------------------

    //看新闻总任务
    @Override
    Boolean performTask_LookNews(){
        int RefreshCount =  mFunction.getRandom_4_8();
        while (RefreshCount > 0){
            if(mCommonTask.isCloseAppTask() || (this.YueDuWenZhangIsFinish && this.WenZhangTouPiaoIsFinish)){ break;}
            this.CollectIncome();
            performTask_LookNews_1();
            mToast.success("倒数第"+RefreshCount+"轮新闻任务");
            mFunction.sleep(1500);
            RefreshCount -- ;
        }
        return true;
    }

    //看新闻子任务一
    private Boolean performTask_LookNews_1(){
        if(!returnHome()){
            return false;
        }

        //点击第一个功能列表
        mGestureUtil.clickTab(4,1);

        mGestureUtil.scroll_up();
        //随机获取在本首页的滑动的次数
        int NewsCount =   mFunction.getRandom_4_8();
        while (NewsCount > 0){
            if(mCommonTask.isCloseAppTask()){ break; }
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
    private boolean performTask_LookNews_2() {
        List<AccessibilityNodeInfo> nodes = AccessibilityHelper.findNodeInfosByIds("com.deshang.ttjx:id/read_number");
        if(nodes == null || nodes.size() < 1){
            return false;
        }

        int nodeCounter = nodes.size()-1;
        while (nodeCounter >= 0){
            try{
                nodeCounter --;
                if(mCommonTask.isCloseAppTask()){ break; }

                //准备阅读的Node
                AccessibilityNodeInfo XinWenNode = nodes.get(nodeCounter);

                mGestureUtil.click(XinWenNode.getParent());

                //滑动次数(随机10到20)
                int SwiperCount = mFunction.getRandom_6_12()+10;
                mToast.info("新闻任务:滑动"+SwiperCount+"次");
                //开始滑动文章
                while (SwiperCount > 0) {
                    if(mCommonTask.isCloseAppTask()){ break; }

                    //判断是否处于文章页，如果不是则退出
                    if(mCommonFunctionTask.judgeIsNoWenZhangPageByText("写评论得红钻")){
                        break;
                    }

                    //点击值得看投票
                    mGestureUtil.clickByResourceId("com.deshang.ttjx:id/like");

                    //向上滑动
                    mGestureUtil.scroll_up();

                    //设置收益的最新时间
                    mIncomeTask.setLastIncomeTime();
                    //停止进行阅读
                    int sleepTime = 2;
                    mFunction.sleep(sleepTime * 1000);
                    SwiperCount--;
                }
                AccessibilityHelper.performBack();
                mFunction.click_sleep();
            }catch (Exception ex){
                mToast.error("Task_KanZiXun:"+ex.getMessage());
            }
            returnHome();
        }
        //阅读完返回
        return true;
    }

    //点击矿，收集
    private Boolean CollectIncome(){

        if(!returnHome()){
            return false;
        }
        //点击【赚钱】列表

        mGestureUtil.clickTab(4,3);

        mToast.success("收取血汗钱");

        if(!mCommonFunctionTask.judgeNodeIsHavingByText("红钻余额")){
            return false;
        }
        AccessibilityNodeInfo node = AccessibilityHelper.findNodeInfosById("com.deshang.ttjx:id/container");
        if(node != null){
            if(node.getChildCount() > 0){
                mGestureUtil.click(node.getChild(0));
                return true;
            }
        }
        return false;
    }

    //-----------------------------------------------------------

    //领取红票分红
    private boolean getShareIncome(){
        if(!returnHome()){
            return false;
        }

        //点击【赚钱】列表
        mGestureUtil.clickTab(4,3);

        AccessibilityNodeInfo node = AccessibilityHelper.findNodeInfosById("com.deshang.ttjx:id/hp_num");
        if(node != null){
            if(Integer.valueOf(node.getContentDescription().toString()) < 1){
                mToast.success("没有分红领取");
                return false;
            }
        }

        mGestureUtil.clickByResourceId("com.deshang.ttjx:id/receive_bonus");

        return false;
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

    //判断今日的收益是否已经达到最大值
    private Boolean JudgeGoldIncomeIsMax(){

        this.CollectIncome();

        if(!returnHome()){
            return false;
        }
        //点击【赚钱】列表
        mGestureUtil.clickTab(4,3);

        if(!mCommonFunctionTask.judgeNodeIsHavingByText("红钻余额")){
            return false;
        }

        int TestCounter = 10;
        while (TestCounter > 0){

            TestCounter--;
            AccessibilityNodeInfo node = AccessibilityHelper.findNodeInfosById("com.deshang.ttjx:id/recycler_view");
            if(node != null){
                if(node.getChildCount() > 1){
                    mGestureUtil.click(node.getChild(1));
                }
            }
            mToast.success("判断今日收益");

            //清除可能的弹框
            this.CloseDialog();

            if(mCommonFunctionTask.judgeNodeIsHavingByText("任务列表")){
                int SlideCount = 10;
                while (SlideCount > 0){
                    //清除可能的弹框
                    this.CloseDialog();
                    //判断阅读文章是否已经完成
                    node = AccessibilityHelper.findNodeInfosByText("阅读文章");
                    if(node != null){
                        Rect rect = new Rect();
                        node.getParent().getBoundsInScreen(rect);
                        if(rect.top > mGlobal.mScreenHeight){
                            mGestureUtil.scroll_up_screen();
                            continue;
                        }
                        String[] XinWenCount = node.getText().toString().replace("阅读文章","").split("/");
                        if( XinWenCount.length ==2){
                            if(Integer.valueOf(XinWenCount[0]) >= Integer.valueOf(XinWenCount[1])){
                                mToast.success("阅读文章已完成");
                                rect = new Rect();
                                node.getParent().getBoundsInScreen(rect);
                                mGestureUtil.click(mGlobal.mScreenWidth-SizeOffset*2,rect.top+rect.height()/2);
                                YueDuWenZhangIsFinish = true;

                            }else {
                                mToast.error("阅读文章未完成");
                                mFunction.click_sleep();
                            }
                        }
                    }
                    //判断阅读文章是否已经完成
                    node = AccessibilityHelper.findNodeInfosByText("文章投票");
                    if(node != null){
                        Rect rect = new Rect();
                        node.getParent().getBoundsInScreen(rect);
                        if(rect.top > mGlobal.mScreenHeight){
                            mGestureUtil.scroll_up_screen();
                            continue;
                        }
                        String[] XinWenCount = node.getText().toString().replace("文章投票","").split("/");
                        if( XinWenCount.length ==2){
                            if(Integer.valueOf(XinWenCount[0]) >= Integer.valueOf(XinWenCount[1])){
                                mToast.success("文章投票已完成");
                                rect = new Rect();
                                node.getParent().getBoundsInScreen(rect);
                                mGestureUtil.click(mGlobal.mScreenWidth-SizeOffset*2,rect.top+rect.height()/2);
                                WenZhangTouPiaoIsFinish = true;
                            }else {
                                mToast.error("文章投票未完成");
                                mFunction.click_sleep();
                            }
                        }
                    }
                    //判断阅读文章是否已经完成
                    node = AccessibilityHelper.findNodeInfosByText("视频投票");
                    if(node != null){
                        Rect rect = new Rect();
                        node.getParent().getBoundsInScreen(rect);
                        if(rect.top > mGlobal.mScreenHeight){
                            mGestureUtil.scroll_up_screen();
                            continue;
                        }
                        String[] XinWenCount = node.getText().toString().replace("视频投票","").split("/");
                        if( XinWenCount.length == 2){
                            if(Integer.valueOf(XinWenCount[0]) >= Integer.valueOf(XinWenCount[1])){
                                mToast.success("视频投票已完成");
                                rect = new Rect();
                                node.getParent().getBoundsInScreen(rect);
                                mGestureUtil.click(mGlobal.mScreenWidth-SizeOffset*2,rect.top+rect.height()/2);
                                ShiPingTouPiaoIsFinish = true;
                            }else {
                                mToast.error("视频投票未完成");
                                mFunction.click_sleep();
                            }
                        }
                        //跳出循环
                        break;
                    }
                    mGestureUtil.scroll_up_screen();
                    SlideCount--;
                }
                if(YueDuWenZhangIsFinish && ShiPingTouPiaoIsFinish && WenZhangTouPiaoIsFinish){
                    mToast.success("今日收益已封顶");
                    this.TodayIncomeIsFinsh = true;
                    mFunction.click_sleep();
                    return true;
                }else {
                    mToast.success("今日收益未封顶，继续工作");
                    mFunction.click_sleep();
                }

                //跳出循环
                break;
            }
        }
        return false;
    }

    //关闭APP弹出的所有可能弹框
    private void CloseDialog(){
        mGestureUtil.clickByResourceId("com.deshang.ttjx:id/close");
        AccessibilityNodeInfo node = AccessibilityHelper.findNodeInfosByText("放弃提现");
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
