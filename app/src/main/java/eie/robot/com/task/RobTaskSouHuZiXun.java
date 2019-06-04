package eie.robot.com.task;

import android.graphics.Rect;
import android.view.accessibility.AccessibilityNodeInfo;

import com.vondear.rxtool.view.RxToast;

import java.util.List;

import eie.robot.com.accessibilityservice.AccessibilityHelper;
import eie.robot.com.common.mCommonTask;
import eie.robot.com.common.mConfig;
import eie.robot.com.common.mDeviceUtil;
import eie.robot.com.common.mFunction;
import eie.robot.com.common.mGestureUtil;
import eie.robot.com.common.mGlobal;
import eie.robot.com.common.mIncomeTask;
import eie.robot.com.common.mToast;

public class RobTaskSouHuZiXun extends BaseRobotTask {

    private int SizeOffset = 40;

    /**
     * 构造函数
     */
    public RobTaskSouHuZiXun() {
        super();
        this.AppName = "搜狐资讯";
        this.TodayMaxIncome = 3500;
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

                //领取时段奖励，搜狐资讯没有这个
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


    //关闭APP弹出的所有可能弹框
    private void CloseDialog(){

        AccessibilityNodeInfo node = AccessibilityHelper.findNodeInfosByText("放弃提现");
        if(node != null){
            mGestureUtil.click(node);
        }

        node = AccessibilityHelper.findNodeInfosByText("继续阅读");
        if(node != null){
            mGestureUtil.click(node);
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
        //我的界面往上滑动了，先向下滑动一次
        mGestureUtil.scroll_down_half_screen();
        int ScrollCounter = 10;
        while (ScrollCounter > 0){
            //再次恢复到首页
            if(!returnHome()){
                return false;
            }

            AccessibilityNodeInfo node = AccessibilityHelper.findNodeInfosByText("今日阅读");

            if(node != null){
                Rect rect = new Rect();
                node.getBoundsInScreen(rect);
                if(rect.top < 200){
                    continue;
                }
                AccessibilityNodeInfo nodeInfo = AccessibilityHelper.findNodeInfosByText("立即领取");
                if(nodeInfo != null){
                    mGestureUtil.click(nodeInfo);
                }
                node = node.getParent();
                if(node != null && node.getClassName().equals("android.widget.LinearLayout")){
                    node = node.getParent();
                    if(node != null && node.getClassName().equals("android.widget.RelativeLayout")){
                        node = AccessibilityHelper.findChildNodeInfosByText(node,"%");
                        if(node != null){
                            String progressBar = node.getText().toString();
                            if(progressBar.contains("%")){
                                progressBar = progressBar.replace("%","");
                                if(Integer.valueOf(progressBar) >= 100){
                                    this.TodayIncomeIsFinsh = true;
                                    mToast.success("今日收益("+progressBar+"%)已封顶。");

                                    return true;
                                }else {
                                    mToast.success("今日收益("+progressBar+"%)未封顶，继续工作");
                                    return false;
                                }
                            }
                        }
                    }
                }
            }
            //界面往上滑动
            mGestureUtil.scroll_up(mGlobal.mScreenHeight/2,1000);
            ScrollCounter -- ;
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
        int NewsCount =   mFunction.getRandom_1_3()+3;
        mToast.success("视频任务");
        while (NewsCount > 0){
            if(mCommonTask.isCloseAppTask()){ break;}
            //看视频
            Task_KanShiPing();
            if(!returnHome()){
                continue;
            }
            mToast.success("阅读完毕，首页滑动");
            NewsCount -- ;
            if(NewsCount > 0){
                mGestureUtil.scroll_up_500();
            }
        }
        //刷资讯
        return true;
    }

    private boolean Task_KanShiPing() {
        List<AccessibilityNodeInfo> nodes = AccessibilityHelper.findNodeInfosByIds("com.sohu.infonews:id/textComment");
        if(nodes == null || nodes.size() < 1){
            return false;
        }

        //点击新闻进行阅读。
        boolean clickResult = mGestureUtil.click(nodes.get(0));
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
                AccessibilityNodeInfo XinWenNode = AccessibilityHelper.findNodeInfosByText("写优质评论，获狐币大礼");
                if(XinWenNode == null){
                    mGestureUtil.click(SizeOffset,mDeviceUtil.getStatusBarHeight()+SizeOffset);
                    break;
                }
                XinWenNode = AccessibilityHelper.findNodeInfosByText("重新播放");
                if(XinWenNode != null){
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

            mGestureUtil.scroll_up_500();
            //打开能量红包
            openRedPacketsEnergy();

            NewsCount -- ;
        }
        //刷资讯
        return true;
    }


    //打开能量红包
    private boolean openRedPacketsEnergy(){
        AccessibilityNodeInfo node = AccessibilityHelper.findNodeInfosByText("能量红包");
        if(node != null){
            Rect rect = new Rect();
            node.getBoundsInScreen(rect);
            if(rect.top > 10){
                mGestureUtil.click(rect.top+rect.height()/2,(float) (mGlobal.mScreenWidth*0.75));
            }
        }
        return true;
    }

    private boolean Task_KanZiXun() {
        AccessibilityNodeInfo node = AccessibilityHelper.findNodeInfosByClassName("android.support.v7.widget.RecyclerView");
        if(node == null || node.getChildCount() < 1){
            return false;
        }
        AccessibilityNodeInfo ArticleNode = null;
        for (int i = node.getChildCount()-1;i >= 0; i-- ){
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

            mFunction.click_sleep();
            //滑动次数(随机10到20)
            int SwiperCount = mFunction.getRandom_6_12();

            mToast.info("新闻任务:滑动"+SwiperCount+"次");

            //开始滑动文章
            while (SwiperCount > 0) {
                if(mCommonTask.isCloseAppTask()){ break; }

                //判断是否处于文章页，如果不是则退出
                AccessibilityNodeInfo XinWenNode = AccessibilityHelper.findNodeInfosByText("写优质评论，获狐币大礼");
                if(XinWenNode == null){
                    AccessibilityHelper.performBack();
                    mFunction.click_sleep();
                    XinWenNode = AccessibilityHelper.findNodeInfosByText("写优质评论，获狐币大礼");
                    if(XinWenNode == null){
                        mGestureUtil.click(SizeOffset,mDeviceUtil.getStatusBarHeight()+SizeOffset);
                        break;
                    }
                }

                //向上滑动
                mGestureUtil.scroll_up();
                //设置收益的最新时间
                mIncomeTask.setLastIncomeTime();

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

        AccessibilityNodeInfo SignNode = null;
        try{
            SignNode = AccessibilityHelper.findNodeInfosByText("签到");
        }catch (Exception ex){
            mToast.error(this.AppName+"收益检测错误:"+ex.getMessage());
        }
        if(SignNode != null){
            if(SignNode.getText().toString().equals("已签到")){
                mToast.success("今天已签到");
                return false;
            }
            mGestureUtil.click(SignNode);
            mFunction.click_sleep();
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
        return returnHome("首页","任务",new Runnable() {
            @Override
            public void run() {
                CloseDialog();
            }
        });
    }
}
