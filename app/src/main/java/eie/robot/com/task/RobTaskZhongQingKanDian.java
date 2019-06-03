package eie.robot.com.task;

import android.graphics.Rect;
import android.view.accessibility.AccessibilityNodeInfo;

import com.vondear.rxtool.view.RxToast;

import eie.robot.com.accessibilityservice.AccessibilityHelper;
import eie.robot.com.common.mCommonTask;
import eie.robot.com.common.mConfig;
import eie.robot.com.common.mDeviceUtil;
import eie.robot.com.common.mFunction;
import eie.robot.com.common.mGestureUtil;
import eie.robot.com.common.mGlobal;
import eie.robot.com.common.mIncomeTask;
import eie.robot.com.common.mToast;

public class RobTaskZhongQingKanDian extends BaseRobotTask {

    private int SizeOffset = 40;

    /**
     * 构造函数
     */
    public RobTaskZhongQingKanDian() {
        super();
        this.AppName = "中青看点";
        this.TodayMaxIncome = 10000;
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

                //签到(聚看点的签到放到了【CloseDialog()】方法里)
                SignIn();

                //看视频
                int RefreshCount =   mFunction.getRandom_1_3();
                while (RefreshCount > 0){
                    if(mCommonTask.isCloseAppTask()){ break;}
                    performTask_KanShiPing();
                    RefreshCount -- ;
                }

                //阅读文章
                RefreshCount =   mFunction.getRandom_4_8();
                while (RefreshCount > 0){
                    if(mCommonTask.isCloseAppTask()){ break;}
                    performTask_KanZiXun();
                    mToast.success("倒数第"+RefreshCount+"轮新闻任务");
                    mFunction.click_sleep();
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
    /**
     * 判断今日的收益是否已经达到最大值
     */
    private Boolean JudgeGoldIncomeIsMax(){

        if(!returnHome()){
            return false;
        }
        //点击【我的】列表
        mGestureUtil.click(mGlobal.mScreenWidth-SizeOffset,mGlobal.mScreenHeight-SizeOffset);

        //再次恢复到首页
        if(!returnHome()){
            return false;
        }

        //我的界面往上滑动了，先向下滑动一次
        mGestureUtil.scroll_down(mGlobal.mScreenHeight/2,1000);

        AccessibilityNodeInfo IncomeNode = null;

        try{
            //利用ID的方式
            AccessibilityNodeInfo nodeInfo = AccessibilityHelper.findNodeInfosById("cn.youth.news:id/tv_today_douzi");
            if(nodeInfo != null && nodeInfo.getClassName().equals("android.widget.TextView")){
                IncomeNode = nodeInfo;
            }else {
                //利用文本的方式
                nodeInfo = AccessibilityHelper.findNodeInfosByText("今日青豆");
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
    /**
     * 执行刷单任务（看资讯）
     */
    private boolean performTask_KanShiPing(){

        if(!returnHome()){
            return false;
        }
        mGestureUtil.click((mGlobal.mScreenWidth/4)*2-SizeOffset,mGlobal.mScreenHeight-SizeOffset);
        //点击第二个功能列表
        mGestureUtil.scroll_down(mGlobal.mScreenHeight/3,10);
        mFunction.click_sleep();
        mGestureUtil.scroll_down(mGlobal.mScreenHeight/2,2000);
        mFunction.click_sleep();
        int NewsCount =   mFunction.getRandom_1_3()+3;
        mToast.success("视频任务");
        while (NewsCount > 0){
            if(mCommonTask.isCloseAppTask()){ break;}
            //看视频
            Task_KanShiPing();
            if(!returnHome()){
                continue;
            }
            mToast.info("阅读完毕，首页滑动");
            NewsCount -- ;
            if(NewsCount > 0){
                mGestureUtil.scroll_up();
                mGestureUtil.scroll_up();
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


    private boolean Task_KanZiXun() {
        AccessibilityNodeInfo node = AccessibilityHelper.findNodeInfosById("cn.youth.news:id/pull_list_view");
        if(node == null || node.getChildCount() < 1){
            return false;
        }
        AccessibilityNodeInfo ArticleNode = null;
        for (int i = 0;i < node.getChildCount(); i++ ){
            AccessibilityNodeInfo childNode = node.getChild(i);
            if(childNode == null) continue;
            AccessibilityNodeInfo AirtNode = AccessibilityHelper.findChildNodeInfosByText(childNode,"广告");
            if(AirtNode == null){
                ArticleNode = childNode;
                break;
            }
        }
        if(ArticleNode == null){
            return false;
        }
        //点击新闻进行阅读。
        boolean clickResult = mGestureUtil.click(ArticleNode);

        if(!clickResult){
            mToast.error("点击失效，重新选择文章");
            return false;
        }
        //开始阅读新闻
        if (clickResult) {

            //设置收益的最新时间
            mIncomeTask.setLastIncomeTime();
            //等待反应
           // mFunction.click_sleep();
            mGestureUtil.click(mGlobal.mScreenWidth/2,SizeOffset*4);
            //滑动次数(随机10到20)
            int SwiperCount = mFunction.getRandom_6_12();
            mToast.info("新闻任务:滑动"+SwiperCount+"次");
            int SwiperCountCopy = SwiperCount;
            //开始滑动文章
            while (SwiperCount > 0) {
                if(mCommonTask.isCloseAppTask()){ break; }

                //判断是否处于文章页，如果不是则退出
                AccessibilityNodeInfo XinWenNode = AccessibilityHelper.findNodeInfosByText("写评论...");
                if(XinWenNode == null){
                    AccessibilityHelper.performBack();
                    mFunction.click_sleep();
                    XinWenNode = AccessibilityHelper.findNodeInfosByText("写评论...");
                    if(XinWenNode == null){
                        mGestureUtil.click(SizeOffset,mDeviceUtil.getStatusBarHeight()+SizeOffset);
                        break;
                    }
                }
                Rect rect = new Rect();
                XinWenNode.getBoundsInScreen(rect);
                //第一次进来的时候，尝试点击【展开查看全文】，因为这个是webView,目前我没找到办法可以获取到该Node
                if(SwiperCountCopy == SwiperCount){
                    int TestCount = 3;
                    while (TestCount > 0){
                        mGestureUtil.scroll_up(300,1000);
                        mGestureUtil.click(mGlobal.mScreenWidth/2,rect.top - 55);
                        AccessibilityNodeInfo info = AccessibilityHelper.findNodeInfosById("cn.youth.news:id/tv_down");
                        if(info != null){
                            AccessibilityHelper.performBack();
                            mFunction.click_sleep();
                        }
                        TestCount--;
                    }
                }

                //点开【查看全文，奖励更多】按钮，阅读全文
                ///AccessibilityNodeInfo info = AccessibilityHelper.findNodeInfosByClassName("android.support.v4.view.ViewPager");
                AccessibilityNodeInfo info = AccessibilityHelper.findNodeInfosByText("查看全文，奖励更多");
                if(info != null){
                    mGestureUtil.click(info);
                }

                //向上滑动
                mGestureUtil.scroll_up();


                //停止进行阅读
                int sleepTime = mFunction.getRandom_4_8();
                mFunction.sleep(sleepTime * 1000);
                SwiperCount--;
            }
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
        //点击【我的】列表
        mGestureUtil.click(mGlobal.mScreenWidth-SizeOffset,mGlobal.mScreenHeight-SizeOffset);

        //再次恢复到首页
        if(!returnHome()){
            return false;
        }

        //我的界面往上滑动了，先向下滑动一次
        mGestureUtil.scroll_down_half_screen();
        AccessibilityNodeInfo nodeInfos = AccessibilityHelper.findNodeInfosByText("明日+");
        if(nodeInfos != null){
            mToast.success("今天已签到");
            return false;
        }
        AccessibilityNodeInfo SignNode = null;
        try{
            //利用ID的方式
            AccessibilityNodeInfo nodeInfo = AccessibilityHelper.findNodeInfosById("cn.youth.news:id/tv_sign");
            if(nodeInfo != null && nodeInfo.getClassName().equals("android.widget.TextView")){
                SignNode = nodeInfo;
            }else {
                //利用文本的方式
                SignNode = AccessibilityHelper.findNodeInfosByText("签到");
            }
        }catch (Exception ex){
            mToast.error(this.AppName+"收益检测错误:"+ex.getMessage());
        }

        if(SignNode != null){
            mGestureUtil.click(SignNode);
            this.CloseDialog();
            mFunction.click_sleep();
            mGestureUtil.click(mGlobal.mScreenWidth-2*SizeOffset,(float) (mGlobal.mScreenHeight*0.3));
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
        return returnHomeById("cn.youth.news:id/tv_user_tab","cn.youth.news:id/tv_find_tab",new Runnable() {
            @Override
            public void run() {
                CloseDialog();
            }
        });
    }
}
