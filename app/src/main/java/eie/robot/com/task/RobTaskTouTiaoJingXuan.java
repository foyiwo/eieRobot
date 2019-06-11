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

    private int SizeOffset = 40;

    private boolean YueDuWenZhangIsFinish   = false;
    private boolean WenZhangTouPiaoIsFinish = false;
    private boolean ShiPingTouPiaoIsFinish  = false;
    /**
     * 构造函数
     */
    public RobTaskTouTiaoJingXuan() {
        super();
        this.AppName = "头条精选";
        this.TodayMaxIncome = 150000;
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

                //领取时段奖励，中青看点没有这个
                //performTask_ShiDuanJiangLi();

                //判断收益是否封顶
                if(JudgeGoldIncomeIsMax()){
                    break;
                }

                //签到
                //SignIn();
                //看视频
                int RefreshCount =  3;//mFunction.getRandom_6_12();
                while (RefreshCount > 0){
                    if(mCommonTask.isCloseAppTask() || this.ShiPingTouPiaoIsFinish){ break;}
                    performTask_KanShiPing();
                    RefreshCount -- ;
                }
                //阅读文章
                RefreshCount =  mFunction.getRandom_4_8();
                while (RefreshCount > 0){
                    if(mCommonTask.isCloseAppTask() || (this.YueDuWenZhangIsFinish && this.WenZhangTouPiaoIsFinish)){ break;}
                    this.CollectIncome();
                    performTask_KanZiXun();
                    mToast.success("倒数第"+RefreshCount+"轮新闻任务");
                    mFunction.sleep(1500);
                    RefreshCount -- ;
                }


            }
            catch (Exception ex){
                mToast.error(ex.getMessage());
            }
        }
        super.CloseTask();
        return false;
    }


    /**
     * 执行刷单任务（定时刷小视频）
     */
    private boolean performTask_ShuaXiaoShiPing(){
        //点击视频的间隔
        int VideoInterval = 6+ mFunction.getRandom_6_12();//3;

        //在主界面的情况下，点击底部导航【小视频】按钮，刷新小视频
        boolean result = mGestureUtil.click(mGlobal.mScreenWidth/2,mGlobal.mScreenHeight-SizeOffset);
        if(result){
            RxToast.normal(VideoInterval+"秒");
            mFunction.sleep( VideoInterval * 1000);
            return true;
        }
        return false;
    }

    /**
     * 执行刷单任务（领取时段奖励）
     */
    private boolean performTask_ShiDuanJiangLi(){
        if(!returnHome()){
            return false;
        }

        //点击第一个功能列表
        mGestureUtil.click(SizeOffset,mGlobal.mScreenHeight-SizeOffset);

        AccessibilityNodeInfo ScrollViewNodeInfo = AccessibilityHelper.findNodeInfosByClassName(
                AccessibilityHelper.getRootInActiveWindow()
                ,"android.widget.HorizontalScrollView");

        if(ScrollViewNodeInfo == null){
            return false;
        }
        mToast.success("时段奖励任务");

        Rect rect = new Rect();
        ScrollViewNodeInfo.getBoundsInScreen(rect);

        if(rect.left < 1 || rect.top < 1){
            return false;
        }
        //点击时段按钮
        mGestureUtil.click(rect.left + SizeOffset,rect.top - SizeOffset);
        this.CloseDialog();
        return true;
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

    private Boolean CollectIncome(){

        if(!returnHome()){
            return false;
        }
        //点击【赚钱】列表
        mGestureUtil.click((mGlobal.mScreenWidth/4)*3-SizeOffset,mGlobal.mScreenHeight-SizeOffset);
        mToast.success("收取血汗钱");

        AccessibilityNodeInfo node = AccessibilityHelper.findNodeInfosByText("红钻余额");
        if(node == null ){
            return false;
        }
        node = AccessibilityHelper.findNodeInfosById("com.deshang.ttjx:id/container");
        if(node != null){
            if(node.getChildCount() > 0){
                mGestureUtil.click(node.getChild(0));
                return true;
            }
        }
        return false;
    }

    /**
     * 判断今日的收益是否已经达到最大值
     */
    private Boolean JudgeGoldIncomeIsMax(){
        this.CollectIncome();

        if(!returnHome()){
            return false;
        }
        //点击【赚钱】列表
        mGestureUtil.click((mGlobal.mScreenWidth/4)*3-SizeOffset,mGlobal.mScreenHeight-SizeOffset);

        AccessibilityNodeInfo node = AccessibilityHelper.findNodeInfosByText("红钻余额");
        if(node == null ){
            return false;
        }
        int TestCounter = 10;
        while (TestCounter > 0){
            TestCounter--;
            node = AccessibilityHelper.findNodeInfosById("com.deshang.ttjx:id/recycler_view");
            if(node != null){
                if(node.getChildCount() > 0){
                    mGestureUtil.click(node.getChild(1));
                }
            }
            mToast.success("判断今日收益");

            //清除可能的弹框
            this.CloseDialog();

            node = AccessibilityHelper.findNodeInfosByText("任务列表");
            if(node != null){
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
                            }
                        }
                        //跳出循环
                        break;
                    }
                    mGestureUtil.scroll_up_screen();
                    SlideCount--;
                }
                if(YueDuWenZhangIsFinish && ShiPingTouPiaoIsFinish && WenZhangTouPiaoIsFinish){
                    this.TodayIncomeIsFinsh = true;
                    return true;
                }

                //跳出循环
                break;
            }
        }
        return false;
    }

    public boolean judgeTaskIsOrNotFinish(String text){
        AccessibilityNodeInfo node = AccessibilityHelper.findNodeInfosByText("视频投票");
        if(node != null){
            String[] XinWenCount = node.getText().toString().replace("视频投票","").split("/");
            if( XinWenCount.length ==2){
                if(Integer.valueOf(XinWenCount[0]) >= Integer.valueOf(XinWenCount[1])){
                    Rect rect = new Rect();
                    node.getParent().getBoundsInScreen(rect);
                    mGestureUtil.click(mGlobal.mScreenWidth-SizeOffset*2,rect.top+mGlobal.mScreenHeight/2);
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * 执行刷单任务（看资讯）
     */
    private boolean performTask_KanShiPing(){

        if(!returnHome()){
            return false;
        }
        mGestureUtil.click((mGlobal.mScreenWidth/4)*2-SizeOffset,mGlobal.mScreenHeight-SizeOffset);

        //判断是否属于视频列表页
        AccessibilityNodeInfo nodeInfo = AccessibilityHelper.findNodeInfosById("com.deshang.ttjx:id/recycler_view");
        if(nodeInfo == null){
            return false;
        }
        //向下滑动刷新视频
        mGestureUtil.scroll_down(mGlobal.mScreenHeight/2,1000);
        mFunction.click_sleep();
        //随便点击一个视频，进入看视频界面
        mGestureUtil.click(SizeOffset*2,mGlobal.mScreenHeight/3);

        //判断是否属于看视频页
        nodeInfo = AccessibilityHelper.findNodeInfosById("com.deshang.ttjx:id/like");
        if(nodeInfo == null){
            return false;
        }
        int NewsCount =   3;
        mToast.success("视频任务");
        while (NewsCount > 0){
            if(mCommonTask.isCloseAppTask()){ break;}

            //规避广告页
            if(mCommonFunctionTask.judgeIsNoShiPingPageByResId("com.deshang.ttjx:id/like")){
                mGestureUtil.scroll_up_30();
                continue;
            }
            //设置收益的最新时间
            mIncomeTask.setLastIncomeTime();
            //点击喜欢
            mGestureUtil.click((float) (mGlobal.mScreenWidth-SizeOffset*1.5),(float)(mGlobal.mScreenHeight*0.634));

            //看视频时间
            int VideoInterval = 6+ mFunction.getRandom_6_12();
            mToast.success("视频任务："+VideoInterval+"秒");
            mFunction.sleep( VideoInterval * 1000);
            NewsCount--;
            if(NewsCount>0){
                mGestureUtil.scroll_up_30();
            }
        }
        //刷资讯
        return true;
    }

    private boolean Task_KanShiPing() {
        AccessibilityNodeInfo node = AccessibilityHelper.findNodeInfosById("cn.youth.news:id/pull_list_view");
        if(node == null || node.getChildCount() < 1){
            return false;
        }
        AccessibilityNodeInfo VideoNode = null;
        for (int i = 0;i < node.getChildCount(); i++ ){
            AccessibilityNodeInfo childNode = node.getChild(i);
            if(childNode == null) continue;
            AccessibilityNodeInfo AirtNode = AccessibilityHelper.findChildNodeInfosByText(childNode,"广告");
            if(AirtNode == null){
                VideoNode = childNode;
                break;
            }
        }
        if(VideoNode == null){
            return false;
        }
        //点击新闻进行阅读。
        boolean clickResult = mGestureUtil.click(VideoNode);
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
            mGestureUtil.click(mGlobal.mScreenWidth/2,SizeOffset*4);
            //开始滑动文章
            while (SwiperCount > 0) {
                if(mCommonTask.isCloseAppTask()){ break; }

                //判断是否处于视频播放页，如果不是则退出
                //判断是否处于文章页，如果不是则退出
                AccessibilityNodeInfo XinWenNode = AccessibilityHelper.findNodeInfosByText("写评论");
                if(XinWenNode == null){
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

    /**
     * 执行刷单任务（看资讯）
     */
    private boolean performTask_KanZiXun(){
        if(!returnHome()){
            return false;
        }
        //点击第一个功能列表
        mGestureUtil.click(SizeOffset,mGlobal.mScreenHeight-SizeOffset);

        //随机获取在本首页的滑动的次数
        int NewsCount =   mFunction.getRandom_4_8();
        while (NewsCount > 0){
            if(mCommonTask.isCloseAppTask()){ break; }
            //进入文章页看新闻
            Task_KanZiXun();
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

    //判断新闻是否已经阅读完毕
    private boolean judgeXinWenIsFinish(){
        AccessibilityNodeInfo node = AccessibilityHelper.findNodeInfosById("c.l.a:id/reward_text");
        if(node != null && node.getText().toString().contains("x")){
            String Counter = node.getText().toString().replace("x","").trim();
            return Integer.valueOf(Counter) < 1;
        }
        return false;
    }


    private boolean Task_KanZiXun() {
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

    /**
     * 执行签到任务
     */
    private Boolean SignIn(){
        if(!returnHome()){
            return false;
        }
        //点击【任务】列表
        mGestureUtil.click((mGlobal.mScreenWidth/5)*4-SizeOffset,mGlobal.mScreenHeight-SizeOffset);

        //再次恢复到首页
        if(!returnHome()){
            return false;
        }

        //我的界面往上滑动了，先向下滑动一次
        mGestureUtil.scroll_down_half_screen();
        AccessibilityNodeInfo nodeInfos = AccessibilityHelper.findNodeInfosByText("已领取");
        if(nodeInfos != null){
            mToast.success("今天已签到");
            return false;
        }
        AccessibilityNodeInfo SignNode = null;
        try{
            //利用ID的方式
            AccessibilityNodeInfo nodeInfo = AccessibilityHelper.findNodeInfosById("c.l.a:id/red_pack_signed_btn");
            if(nodeInfo != null && nodeInfo.getClassName().equals("android.widget.TextView")){
                SignNode = nodeInfo;
            }else {
                //利用文本的方式
                SignNode = AccessibilityHelper.findNodeInfosByText("立即签到");
            }
        }catch (Exception ex){
            mToast.error(this.AppName+"收益检测错误:"+ex.getMessage());
        }

        if(SignNode != null){
            mGestureUtil.click(SignNode);
            AccessibilityHelper.performBack();
            returnHome();
            return true;
        }
        return false;
    }


    /**
     * 回归到首页，如果APP未打开，则会自行打开
     * @return
     */
    private boolean returnHome(){
        return returnHome("首页","我的",new Runnable() {
            @Override
            public void run() {
                CloseDialog();
            }
        });
    }
}
