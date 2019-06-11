package eie.robot.com.task;

import android.graphics.Path;
import android.graphics.Rect;
import android.view.accessibility.AccessibilityNodeInfo;

import com.vondear.rxtool.view.RxToast;

import eie.robot.com.accessibilityservice.AccessibilityHelper;
import eie.robot.com.common.mCommonTask;
import eie.robot.com.common.mConfig;
import eie.robot.com.common.mFunction;
import eie.robot.com.common.mGestureUtil;
import eie.robot.com.common.mGlobal;
import eie.robot.com.common.mIncomeTask;
import eie.robot.com.common.mToast;

public class RobTaskQuKanTianXia extends BaseRobotTask {

    public int SizeOffset = 40;

    /**
     * 构造函数
     */
    public RobTaskQuKanTianXia() {
        super();
        this.AppName = "趣看天下";
        this.TodayMaxIncome = 6000;
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
                if(!returnHome()){
                    continue;
                }
                //领取时段奖励
                performTask_ShiDuanJiangLi();

                //判断收益是否封顶
                if(JudgeGoldIncomeIsMax()){
                    break;
                }

                //每次进行一项任务时，都先恢复到首页
                //如果APP未打开，则会自行打开,如果最后还是无法打开，则跳出这次循环，重新来。
                if(!returnHome()){
                    continue;
                }

                //签到(聚看点的签到放到了【CloseDialog()】方法里)
                SignIn();


                mFunction.openScreen();

                //看视频
                int RefreshCount =   1;//mFunction.getRandom_1_3();
                while (RefreshCount > 0){
                    if(mCommonTask.isCloseAppTask()){ break;}
                    performTask_KanShiPing();
                    RefreshCount -- ;
                }

                //看新闻
                RefreshCount =   mFunction.getRandom_4_8();
                while (RefreshCount > 0){
                    if(mCommonTask.isCloseAppTask()){ break;}
                    performTask_KanZiXun();
                    RefreshCount -- ;
                }
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
                mGlobal.mAccessibilityService.getRootInActiveWindow()
                ,"android.widget.HorizontalScrollView");
        if(ScrollViewNodeInfo == null){
            return false;
        }
        mToast.success("时段奖励任务");
        Rect rect = new Rect();
        ScrollViewNodeInfo.getBoundsInScreen(rect);
        if(rect.width() < 1 || rect.top < 1){
            return false;
        }

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
        mGestureUtil.scroll_down_100();
        AccessibilityNodeInfo IncomeNode = null;
        try{
            //利用ID的方式
            AccessibilityNodeInfo nodeInfo = AccessibilityHelper.findNodeInfosById("com.yanhui.qktx:id/tv_money_count");
            if(nodeInfo != null && nodeInfo.getClassName().equals("android.widget.TextView")){
                IncomeNode = nodeInfo;
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
        //点击第二个功能列表
        mGestureUtil.click((mGlobal.mScreenWidth/5)*2-SizeOffset,mGlobal.mScreenHeight-SizeOffset);
        mToast.success("新闻任务");
        int NewsCount =   mFunction.getRandom_1_3();
        while (NewsCount > 0){
            if(mCommonTask.isCloseAppTask()){ break;}
            //看视频
            Task_KanShiPing();
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
            if(mCommonTask.isCloseAppTask()){ break; }
            Task_KanZiXun();
            if(!returnHome()){
                continue;
            }

            mToast.info("新闻看完，首页滑动");
            mGestureUtil.scroll_up();
            NewsCount -- ;
        }
        //刷资讯
        return true;
    }


    private boolean Task_KanShiPing() {
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

    private boolean Task_KanZiXun() {

        //点击新闻进行阅读。
        boolean clickResult = mGestureUtil.clickInScreenCenter();
        //开始阅读新闻
        if (clickResult) {
            //等待反应
            mFunction.sleep(mConfig.clickSleepTime);

            //滑动次数(随机10到20)
            int SwiperCount = mFunction.getRandom_6_12();
            mToast.info("新闻任务:滑动"+SwiperCount+"次");
            int SwiperCountCopy = SwiperCount;
            //开始滑动文章
            while (true) {

                if(mCommonTask.isCloseAppTask()){ break; }

                if (SwiperCount < 1) {
                    break;
                }
                AccessibilityNodeInfo info = AccessibilityHelper.findNodeInfosByText("评论得金币");
                //判断是否处于文章页或者视频页，如果不是则退出
                if(info == null){
                    break;
                }
                //设置收益的最新时间
                mIncomeTask.setLastIncomeTime();
                //判断是处于视频页还是文章页，info为空，说明是文章页，不为空，则为视频页，不进行滑动
                AccessibilityNodeInfo VideoInfos = AccessibilityHelper.findNodeInfosById("com.yanhui.qktx:id/surface_container");
                Rect rect = new Rect();
                info.getBoundsInScreen(rect);
                //第一次进来的时候，尝试点击【展开查看全文】，因为这个是webView,目前我没找到办法可以获取到该Node
                if(SwiperCountCopy == SwiperCount && VideoInfos == null){
                    int TestCount = 3;
                    while (TestCount > 0){
                        mGestureUtil.scroll_up(100,1000);
                        mGestureUtil.click(mGlobal.mScreenWidth/2,rect.top - 55);
                        info = AccessibilityHelper.findNodeInfosByText("保存");
                        if(info != null){
                            AccessibilityHelper.performBack();
                            mFunction.click_sleep();
                        }
                        TestCount--;
                    }
                }
                if(VideoInfos == null){
                    mGestureUtil.scroll_up();
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

        return true;
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

    /**
     * 执行签到任务
     */
    private void SignIn(){
        mToast.success("签到");
        mGestureUtil.click((mGlobal.mScreenWidth/5)*4-SizeOffset,mGlobal.mScreenHeight-SizeOffset);
    }

    /**
     * 回归到首页，如果APP未打开，则会自行打开
     * @return
     */
    private boolean returnHome(){
        return returnHome("每日金币","我的",new Runnable() {
            @Override
            public void run() {
                CloseDialog();
            }
        });
    }
}
