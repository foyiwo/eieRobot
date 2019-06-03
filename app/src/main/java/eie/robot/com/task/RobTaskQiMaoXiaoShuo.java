package eie.robot.com.task;

import android.view.accessibility.AccessibilityNodeInfo;

import com.vondear.rxtool.view.RxToast;

import eie.robot.com.accessibilityservice.AccessibilityHelper;
import eie.robot.com.appconfig.IDQuTouTiao;
import eie.robot.com.common.mCommonTask;
import eie.robot.com.common.mConfig;
import eie.robot.com.common.mFunction;
import eie.robot.com.common.mGestureUtil;
import eie.robot.com.common.mGlobal;

public class RobTaskQiMaoXiaoShuo extends BaseRobotTask {

    public int SizeOffset = 40;

    /**
     * 构造函数
     */
    public RobTaskQiMaoXiaoShuo() {
        super();
        this.AppName = "七猫免费小说";
    }


    /**
     * 执行刷单任务（领取时段奖励、定时刷新视频、查看文章）
     */
    @Override
    public boolean StartTask()  {
        super.StartTask();
        mCommonTask.setAppTaskOpen();
        try {
            //每次进行一项任务时，都先恢复到首页
            //如果APP未打开，则会自行打开,如果最后还是无法打开，则跳出这次循环，重新来。
            if(!returnHome()){
                return false;
            }
            //签到
            SignIn();
            mFunction.openScreen();
            //开始读3个小时小说
            Task_KanXiaoShuo();

        }catch (Exception ex){
            RxToast.error(ex.getMessage());
        }
        CloseTask();
        return false;
    }
    //开始看三个小时小说
    private boolean Task_KanXiaoShuo() {
        AccessibilityNodeInfo nodeInfo = AccessibilityHelper.loopFindNodeInfoByText("继续阅读>");
        if (nodeInfo == null) {
            return false;
        }
        //点击新闻进行阅读。
        boolean clickResult = AccessibilityHelper.performClick(nodeInfo);

        //开始阅读新闻
        if (clickResult) {
            int AmountSleepTime = 0;
            while (true){
                if(mCommonTask.isCloseAppTask()){ break; }
                mGestureUtil.scroll_left();
                int SleepTime = mFunction.getRandom_10_20();
                mFunction.sleep(SleepTime*1000);
                AmountSleepTime += SleepTime;
                if(AmountSleepTime > 60*60*3){
                    break;
                }
            }
        }
        return true;
    }


    /**
     * 执行签到任务
     */
    private void SignIn(){

    }

    /**
     * 回归到首页，如果APP未打开，则会自行打开
     * @return
     */
    private boolean returnHome(){
        return returnHome("任务中心","我的",new Runnable() {
            @Override
            public void run() {
            }
        });
    }
}
