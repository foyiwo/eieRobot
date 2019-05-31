package eie.robot.com.task;

import android.graphics.Rect;
import android.icu.lang.UCharacter;
import android.view.accessibility.AccessibilityNodeInfo;

import com.vondear.rxtool.view.RxToast;

import eie.robot.com.accessibilityservice.AccessibilityHelper;
import eie.robot.com.common.mCommonTask;
import eie.robot.com.common.mConfig;
import eie.robot.com.common.mFunction;
import eie.robot.com.common.mGestureUtil;
import eie.robot.com.common.mGlobal;
import eie.robot.com.common.mToast;

public class RobTaskJuKanDian extends BaseRobotTask {

    public int SizeOffset = 40;

    /**
     * 构造函数
     */
    public RobTaskJuKanDian() {
        super();
        this.AppName = "聚看点";
        this.TodayMaxIncome = 6000;
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
                //每次进行一项任务时，都先恢复到首页
                //如果APP未打开，则会自行打开,如果最后还是无法打开，则跳出这次循环，重新来。
                if(!returnHome()){
                    continue;
                }
                //领取时段奖励
                performTask_ShiDuanJiangLi();

                //判断收益是否封顶
                if(JudgeGoldIncomeIsMax()){
                    break;
                }

                //签到(聚看点的签到放到了【CloseDialog()】方法里)
                //SignIn();


                mFunction.openScreen();

                //阅读文章
                int RefreshCount =   mFunction.getRandom_10_20()+10;
                while (RefreshCount > 0){
                    if(!mCommonTask.AppTaskOpenStatus){ break;}
                    performTask_KanZiXun();
                    mFunction.click_sleep();
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
        //签到
        SignIn();


        AccessibilityNodeInfo node = AccessibilityHelper.findNodeInfosByText("我知道了");
        if(node != null){
            AccessibilityHelper.performClick(node);
        }

        node = AccessibilityHelper.findNodeInfosByText("分享立赚");
        if(node != null){
            AccessibilityHelper.performClick(node);
            mFunction.sleep(mConfig.clickSleepTime);
            AccessibilityHelper.performBack(mGlobal.mAccessibilityService);
            mFunction.sleep(mConfig.clickSleepTime);
        }

        node = AccessibilityHelper.findNodeInfosByText("忽略");
        if(node != null){
            AccessibilityHelper.performClick(node);
        }

        //判断是否点多了，触发了【退出APP确认框】
        AccessibilityNodeInfo NodeInfo3 = AccessibilityHelper.findNodeInfosByText("确认退出聚看点？");
        AccessibilityNodeInfo NodeInfo4 = AccessibilityHelper.findNodeInfosByText("继续赚钱");
        if ( NodeInfo3 != null && NodeInfo4 != null ) {
            AccessibilityHelper.performClick(NodeInfo4);
        }
        node = AccessibilityHelper.findNodeInfosByText("分享奖励领取成功");
        if(node != null){
            node = AccessibilityHelper.findNodeInfosById("com.xiangzi.jukandian:id/close_img_layout");
            if(node != null){
                mGestureUtil.click(node);
            }

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
        mToast.success("判断今日收益是否封顶");
        //再次恢复到首页
        if(!returnHome()){
            return false;
        }
        mGestureUtil.scroll_down_100();
        AccessibilityNodeInfo IncomeNode = null;
        try{
            //利用ID的方式
            AccessibilityNodeInfo nodeInfo = AccessibilityHelper.findNodeInfosById("com.xiangzi.jukandian:id/goldValue");
            if(nodeInfo != null && nodeInfo.getClassName().equals("android.widget.TextView")){
                IncomeNode = nodeInfo;
            }else {
                //利用文本的方式
                nodeInfo = AccessibilityHelper.findNodeInfosByText("今日金币");
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

            mToast.info("阅读完毕，首页滑动");
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

            //设置收益的最新时间
            mCommonTask.setLastIncomeTime();

            //等待反应
            mFunction.sleep(mConfig.clickSleepTime);

            //滑动次数(随机10到20)
            int SwiperCount = mFunction.getRandom_6_12();
            mToast.info("新闻任务:滑动"+SwiperCount+"次");

            //开始滑动文章
            while (true) {

                if(!mCommonTask.AppTaskOpenStatus){ break; }
                if (SwiperCount < 1) {
                    break;
                }
                //点开【查看全文，奖励更多】按钮，阅读全文
                AccessibilityNodeInfo info = AccessibilityHelper.findNodeInfosByClassName("android.support.v4.view.ViewPager");
                if(info != null){
                    Rect rect = new Rect();
                    info.getBoundsInScreen(rect);
                    if(rect.top > 100){
                        mGestureUtil.click(mGlobal.mScreenWidth/2,rect.top - 60);
                    }
                }

                //向上滑动
                mGestureUtil.scroll_up();

                //判断是否处于文章页，如果不是则退出
                AccessibilityNodeInfo XinWenNode = AccessibilityHelper.findNodeInfosByText("评论得金币");
                if(XinWenNode == null){
                    mGestureUtil.click(SizeOffset,SizeOffset);
                    break;
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
        AccessibilityNodeInfo nodeInfo = AccessibilityHelper.findNodeInfosByText("一键签到");
        if(nodeInfo != null){
            mGestureUtil.click(nodeInfo);
        }
    }


    /**
     * 回归到首页，如果APP未打开，则会自行打开
     * @return
     */
    private boolean returnHome(){
        return returnHome("任务中心","我的",new Runnable() {
            @Override
            public void run() {
                CloseDialog();
            }
        });
    }
}
