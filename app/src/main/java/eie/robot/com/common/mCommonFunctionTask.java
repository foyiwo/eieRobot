package eie.robot.com.common;

import android.accessibilityservice.AccessibilityService;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.graphics.Rect;
import android.os.Looper;
import android.support.v7.widget.WithHint;
import android.view.accessibility.AccessibilityNodeInfo;

import eie.robot.com.accessibilityservice.AccessibilityHelper;
import eie.robot.com.receiver.baseReceiver;
import retrofit2.http.PUT;
import rx.functions.Func0;

public class mCommonFunctionTask {

    //根据文字判断是否处于文章页
    public static Boolean judgeIsNoWenZhangPageByText(String desc){

        //判断是否处于文章页，如果不是则退出
        AccessibilityNodeInfo XinWenNode = AccessibilityHelper.findNodeInfosByText(desc);
        if(XinWenNode == null){
            AccessibilityHelper.performBack();
            mFunction.click_sleep();
            XinWenNode = AccessibilityHelper.findNodeInfosByText("写评论得红钻");
            if(XinWenNode == null){
                mGestureUtil.click(40,mDeviceUtil.getStatusBarHeight()+40);
                return true;
            }
        }
        return false;

    }

    //根据文字判断是否处于文章页
    public static Boolean judgeIsNoShiPingPageByResId(String ResId){
        return judgeNodeIsHavingByResId(ResId);
    }

    //根据文字判断是否处于文章页
    public static Boolean judgeNodeIsHavingByText(String desc){

        //判断是否处于文章页，如果不是则退出
        AccessibilityNodeInfo XinWenNode = AccessibilityHelper.findNodeInfosByText(desc);
        if(XinWenNode == null ) return false;
        Rect rect = new Rect();
        XinWenNode.getBoundsInScreen(rect);
        if(rect.top < mGlobal.mScreenHeight && rect.top > 0)  return true;
        return false;
    }

    public static boolean judgeNodeIsHavingByResId(String ResId){
        //判断是否处于文章页，如果不是则退出
        AccessibilityNodeInfo XinWenNode = AccessibilityHelper.findNodeInfosById(ResId);
        return XinWenNode != null;
    }

    /**
     * 粘贴数据进对应的Node位置
     * @param node
     * @param content
     * @return
     * @throws InterruptedException
     */
    public static boolean pasteTextToNode(AccessibilityNodeInfo node, String content){
        //让搜索框获取到焦点，这点很重要，否则无法复制
        node.performAction(AccessibilityNodeInfo.ACTION_FOCUS);

        mFunction.click_sleep();

        ClipData clip = ClipData.newPlainText("label", content);
        if(Looper.myLooper() == null){
            Looper.prepare();
        }
        ClipboardManager clipboardManager = (ClipboardManager) mGlobal.mAccessibilityService.getSystemService(Context.CLIPBOARD_SERVICE);

        assert clipboardManager != null;
        clipboardManager.setPrimaryClip(clip);

        return node.performAction(AccessibilityNodeInfo.ACTION_PASTE);
    }


    public static boolean loopJudgeNodeIsHavingByResId(String ResId){
        int TestCount = 10;
        while (TestCount > 0){
            AccessibilityNodeInfo XinWenNode = AccessibilityHelper.findNodeInfosById(ResId);
            if(XinWenNode != null){
                return true;
            }
            mFunction.sleep(1000);
            TestCount --;
        }
        return false;
    }

    public static boolean loopJudgeNodeIsHavingByText(int TestCount,String Text){
        while (TestCount > 0){
            AccessibilityNodeInfo XinWenNode = AccessibilityHelper.findNodeInfosByText(Text);
            if(XinWenNode != null){
                return true;
            }
            mFunction.sleep(1000);
            TestCount --;
        }
        return false;
    }

    public static boolean loopJudgeNodeIsHavingByText(String Text){
        int TestCount = 10;
        return loopJudgeNodeIsHavingByText(TestCount,Text);
    }

    //清理系统的各种弹框
    public static void CloseSystemBulletBox(){

        //调用微信
        AccessibilityNodeInfo rootWindow = AccessibilityHelper.getRootInActiveWindow();
        if (rootWindow != null && rootWindow.getPackageName().equals("com.miui.securitycenter")){
            mGestureUtil.clickByText("拒绝");
        }
        //取消软件安装
        if (rootWindow != null && rootWindow.getPackageName().equals("com.miui.packageinstaller")){
            mGestureUtil.clickByResourceId("com.miui.packageinstaller:id/cancel_button");
            mGestureUtil.clickByResourceId("android:id/button2");
            mGestureUtil.clickByText("取消");
        }

        if(mCommonFunctionTask.judgeNodeIsHavingByText("没有响应")){
            mGestureUtil.clickByText("确定");
            mGestureUtil.clickByText("关闭应用");
        }
        if(mCommonFunctionTask.judgeNodeIsHavingByText("无响应。要将其关闭吗")){
            mGestureUtil.clickByText("确定");
            mGestureUtil.clickByText("关闭应用");
        }

        if(mCommonFunctionTask.judgeNodeIsHavingByText("通知消息")){
            mGestureUtil.clickByText("拒绝");
        }

        mGestureUtil.clickByResourceId("android:id/aerr_close");
        mGestureUtil.clickByText("关闭应用");



        mGestureUtil.clickByText("跳过");
    }

    public static void ControlWifi(){
        if(mFunction.getRandomBooleanOffsetFalse()){
            CloseWIFI();
        }else {
            OpenWIFI();
        }
    }

    public static void CloseWIFI(){
        if(!mFunction.judgeAndroidVersionIsGreater7()){
            //mAdbShell.CloseWIFI();
        }
    }

    public static void OpenWIFI(){
        if(!mFunction.judgeAndroidVersionIsGreater7()){
            //mAdbShell.OpenWIFI();
        }
    }

    public static void loopJudgeIsReboot(){

        mFunction.runInChildThread(new Runnable() {
            @Override
            public void run() {
                while (true){
                    try {
                        baseReceiver.sendReBootBroadcast();
                        mFunction.sleep(15*1000);
                    }catch (Exception ex){

                    }
                }
            }
        });


    }
}
