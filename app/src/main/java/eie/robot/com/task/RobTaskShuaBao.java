package eie.robot.com.task;

import android.graphics.Rect;
import android.view.accessibility.AccessibilityNodeInfo;

import com.vondear.rxtool.view.RxToast;

import eie.robot.com.accessibilityservice.AccessibilityHelper;
import eie.robot.com.common.mCommonTask;
import eie.robot.com.common.mConfig;
import eie.robot.com.common.mFunction;
import eie.robot.com.common.mGestureUtil;
import eie.robot.com.common.mGlobal;
import eie.robot.com.common.mToast;

public class RobTaskShuaBao extends BaseRobotTask {

    public int SizeOffset = 40;

    /**
     * 构造函数
     */
    public RobTaskShuaBao() {
        super();
        this.AppName = "刷宝短视频";
        String packname = mFunction.GetAppPackageName(this.AppName);
        if(packname.isEmpty()){
            this.AppName = "刷宝";
        }
        this.TodayMaxIncome = 11888;
        this.TodayIncomeIsFinsh = false;
    }


    /**
     * 执行刷单任务（领取时段奖励、定时刷新视频、查看文章）
     */
    @Override
    public boolean StartTask()  {
        super.StartTask();
        while (mCommonTask.AppTaskOpenStatus){
            try {
                //判断收益是否封顶
                if(JudgeGoldIncomeIsMax()){
                    break;
                }

                //每次进行一项任务时，都先恢复到首页
                //如果APP未打开，则会自行打开,如果最后还是无法打开，则跳出这次循环，重新来。
                if(!returnHome()){
                    continue;
                }

                //签到
                SignIn();
                //领取时段奖励
                //performTask_ShiDuanJiangLi();

                mFunction.openScreen();

                //阅读文章
                int RefreshCount =   mFunction.getRandom_10_20()+30;
                while (RefreshCount > 0){
                    if(!mCommonTask.AppTaskOpenStatus){ break;}
                    performTask_ShuaXiaoShiPing();
                    RefreshCount -- ;

                }
                if(!mCommonTask.AppTaskOpenStatus){ break;}
            }catch (Exception ex){
                RxToast.error(ex.getMessage());
            }
        }
        super.CloseTask();
        return false;
    }


    /**
     * 执行刷单任务（定时刷小视频）
     */
    private boolean performTask_ShuaXiaoShiPing(){

        if(!returnHome()){
            return false;
        }

        //在主界面的情况下，点击底部导航【小视频】按钮，刷新小视频
        mGestureUtil.click(SizeOffset,mGlobal.mScreenHeight-SizeOffset);
        int VideoCount = mFunction.getRandom_10_20();
        while (VideoCount > 0){
            //点击视频的间隔
            int VideoInterval = 6+ mFunction.getRandom_6_12();//3;

            if(!mCommonTask.AppTaskOpenStatus){ break; }

            mGestureUtil.scroll_up_30();
            mToast.success("视频任务:浏览"+VideoInterval+"秒");

            //设置收益的最新时间
            mCommonTask.setLastIncomeTime();

            if(VideoInterval == 18){
                AccessibilityNodeInfo nodeInfo = AccessibilityHelper.findNodeInfosByText("关注");
                if(nodeInfo != null){
                    mGestureUtil.click(nodeInfo);
                }else {
                    mGestureUtil.doubleClickInScreenCenter();
                }
                mGestureUtil.doubleClickInScreenCenter();
            }
            mFunction.sleep( VideoInterval * 1000);
            VideoCount--;
        }
        return true;
    }

    /**
     * 执行刷单任务（领取时段奖励）
     */
    private boolean performTask_ShiDuanJiangLi(){
        mToast.success("时段奖励任务");
        if(!returnHome()){
            return false;
        }
        //点击第一个功能列表
        mGestureUtil.click(SizeOffset,mGlobal.mScreenHeight-SizeOffset);


        AccessibilityNodeInfo ScrollViewNodeInfo = AccessibilityHelper.findNodeInfosByClassName(
                mGlobal.mAccessibilityService.getRootInActiveWindow()
                ,"android.widget.HorizontalScrollView");
        if(ScrollViewNodeInfo == null){
            return false;
        }
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
        AccessibilityNodeInfo node = AccessibilityHelper.findNodeInfosById("com.jm.video:id/imgClose");
        if(node != null){
            mGestureUtil.click(node);
        }

    }


    /**
     * 执行刷单任务（看资讯）
     */
    private boolean performTask_KanZiXun(){
        mToast.success("新闻任务");
        if(!returnHome()){
            return false;
        }
        //点击第一个功能列表
        mGestureUtil.click(SizeOffset,mGlobal.mScreenHeight-SizeOffset);

        mGestureUtil.scroll_up();
        int NewsCount =   mFunction.getRandom_4_8();
        while (NewsCount > 0){
            if(!mCommonTask.AppTaskOpenStatus){break;}
            Task_KanZiXun();
            if(!returnHome()){
                continue;
            }
            mToast.info("首页滑动");
            mGestureUtil.scroll_up();
            NewsCount -- ;

        }
        //刷资讯
        return true;
    }


    private boolean Task_KanZiXun() {
        AccessibilityNodeInfo nodeInfo = AccessibilityHelper.findNodeInfosByClassName(
                mGlobal.mAccessibilityService.getRootInActiveWindow()
                ,"android.support.v7.widget.RecyclerView");
        if (nodeInfo == null) {
            return false;
        }
        int CountNews = (nodeInfo.getChildCount()/2);

        AccessibilityNodeInfo childNodeInfo = nodeInfo.getChild(CountNews);
        if(childNodeInfo == null){
            mToast.error("新闻为空，重新选择文章");
            return false;
        }

        mToast.success("阅读当页第"+CountNews+"条新闻");
        //过滤广告
        if(filterAdvertisement(childNodeInfo)){
            return false;
        }

        //点击新闻进行阅读。
        boolean clickResult = mGestureUtil.click(childNodeInfo);
        if(!clickResult){
            mToast.error("点击失效，重新选择文章");
            return false;
        }
        //开始阅读新闻
        if (clickResult) {
            //等待反应
            mFunction.sleep(mConfig.clickSleepTime);

            //滑动次数(随机10到20)
            int SwiperCount = mFunction.getRandom_6_12();
            mToast.info("资讯任务:滑动"+SwiperCount+"次");

            //开始滑动文章
            while (true) {
                if (SwiperCount < 1) {
                    break;
                }
                //向上滑动
                mGestureUtil.scroll_up();

                //点开【查看全文，奖励更多】按钮，阅读全文
                AccessibilityNodeInfo info = AccessibilityHelper.findNodeInfosByText("查看全文，奖励更多");
                if(info != null){
                    AccessibilityHelper.performClick(info);
                }

                //判断是否已经下载了某个APP
                AccessibilityNodeInfo node = AccessibilityHelper.findNodeInfosByText("出于安全考虑，已禁止您的手机安装来自此来源的未知应用。");
                if(node != null){
                    break;
                }

                //停止进行阅读
                int sleepTime = mFunction.getRandom_4_8();
                mFunction.sleep(sleepTime * 1000);
                SwiperCount--;
            }
        }
        //阅读完返回
        mToast.success("阅读完毕");
        return true;
    }

    //过来广告
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
    private void SignIn(){
        if(this.IsSign){
            return;
        }
        mToast.info("开始签到");
       if(!returnHome()){
           return;
       }
        AccessibilityNodeInfo nodeInfo = AccessibilityHelper.findNodeInfosByText("任务");
        if(nodeInfo != null){
            mGestureUtil.click(nodeInfo);
            if(!returnHome()){
                return;
            }
            mGestureUtil.click(mGlobal.mScreenWidth-2*SizeOffset,(float)(mGlobal.mScreenHeight*0.28));
            mFunction.sleep(2*mConfig.clickSleepTime);
        }
    }


    /**
     * 判断今日的收益是否已经达到最大值
     */
    private Boolean JudgeGoldIncomeIsMax(){
        mToast.info("判断今日收益是否封顶");
        if(!returnHome()){
            return false;
        }
        //点击【我的】列表
        mGestureUtil.click(mGlobal.mScreenWidth-SizeOffset,mGlobal.mScreenHeight-SizeOffset);

        //再次恢复到首页
        if(!returnHome()){
            return false;
        }

        try{
            AccessibilityNodeInfo nodeInfo = AccessibilityHelper.findNodeInfosById("com.jm.video:id/tv_gold_num");
            if(nodeInfo != null){
                String incomeText = nodeInfo.getText().toString().trim();
                if(Integer.valueOf(incomeText) > this.TodayMaxIncome){
                    this.TodayIncomeIsFinsh = true;
                    mToast.info("当前收益("+incomeText+")已封顶("+this.TodayMaxIncome+")");
                    mFunction.sleep(mConfig.clickSleepTime);
                    return true;
                }else {
                    mToast.info("当前收益("+incomeText+")未封顶("+this.TodayMaxIncome+")，继续工作");
                    mFunction.sleep(mConfig.clickSleepTime);
                    return false;
                }
            }

        }catch (Exception ex){

        }
        return false;
    }


    /**
     * 回归到首页，如果APP未打开，则会自行打开
     * @return
     */
    private boolean returnHome(){
        return returnHome("任务","首页",new Runnable() {
            @Override
            public void run() {
                CloseDialog();
            }
        });
    }
}
