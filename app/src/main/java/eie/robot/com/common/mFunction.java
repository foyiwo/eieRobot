package eie.robot.com.common;

import android.Manifest;
import android.accessibilityservice.AccessibilityService;
import android.accessibilityservice.GestureDescription;
import android.app.Activity;
import android.app.ActivityManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.graphics.Path;
import android.graphics.Rect;
import android.net.Uri;
import android.os.Build;
import android.os.PowerManager;
import android.provider.Settings;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.accessibility.AccessibilityNodeInfo;
import android.widget.Toast;

import com.vondear.rxtool.RxTool;
import com.vondear.rxtool.view.RxToast;

import java.util.List;

import eie.robot.com.accessibilityservice.AccessibilityHelper;
import eie.robot.com.accessibilityservice.mAccessibilityService;
import eie.robot.com.activity.NavigationBarActivity;

public class mFunction {
    /**
     * 设置Android6.0的权限申请
     */
    public static void setPermissions(Activity mContext) {
        final String[] PERMISSION = new String[]{
                Manifest.permission.READ_CONTACTS,          // 读取联系人
                Manifest.permission.ACCESS_COARSE_LOCATION, //用于进行网络定位
                Manifest.permission.ACCESS_FINE_LOCATION,   //用于访问GPS定位
                Manifest.permission.WRITE_EXTERNAL_STORAGE, //写入外部存储
                Manifest.permission.READ_EXTERNAL_STORAGE,  //读取外部存储
                Manifest.permission.READ_PHONE_STATE,        //读取电话状态
                Manifest.permission.CAMERA                    //读取电话状态
        };
        if (ContextCompat.checkSelfPermission(mContext, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED
                ||ContextCompat.checkSelfPermission(mContext, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                ||ContextCompat.checkSelfPermission(mContext, Manifest.permission.READ_CONTACTS) != PackageManager.PERMISSION_GRANTED
                ||ContextCompat.checkSelfPermission(mContext, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            //Android 6.0申请权限
            ActivityCompat.requestPermissions(mContext,PERMISSION,1);
        }else{
            Log.i("申请","权限申请ok");
        }
    }

    /**
     * 打开辅助服务的设置
     */
    public static void openAccessibilityServiceSettings() {
        try {
            Intent intent = new Intent(Settings.ACTION_ACCESSIBILITY_SETTINGS);
            mGlobal.mNavigationBarActivity.startActivity(intent);
        } catch (Exception e) {
            RxToast.error(mGlobal.mNavigationBarActivity, "辅助服务打开失败:"+e.getMessage(), Toast.LENGTH_SHORT, true).show();
            e.printStackTrace();
        }
    }


    /**
     *打开趣头条
     */
    public  static void  openQuKanTianXia(){
        mFunction.openLocalAPP(mGlobal.mNavigationBarActivity,"com.yanhui.qktx","com.yanhui.qktx.activity.MainActivity");
    }


    //根据APP名字获取包名
    public static String GetAppPackageName(String AppName){
        Intent intent = new Intent(Intent.ACTION_MAIN, null);
        intent.addCategory(Intent.CATEGORY_LAUNCHER);
        List<ResolveInfo> apps = mGlobal.mNavigationBarActivity.getPackageManager().queryIntentActivities(intent, 0);

        String packageName = "";
        //for循环遍历ResolveInfo对象获取包名和类名
        for (int i = 0; i < apps.size(); i++) {
            ResolveInfo info = apps.get(i);
            CharSequence cls = info.activityInfo.name;
            CharSequence name = info.activityInfo.loadLabel(mGlobal.mNavigationBarActivity.getPackageManager());
            if(name.equals(AppName)){
                packageName = info.activityInfo.packageName;
                break;
            }
        }

        return packageName;
    }

    //完全模拟人工，在桌面打开APP
    private static boolean OpenAppInDesktop(String AppName){
        //回到桌面
        AccessibilityHelper.performHome();
        mFunction.click_sleep();

        if(AccessibilityHelper.getRootInActiveWindow() == null){
            mFunction.recoverRootWindow();
            mToast.error("OpenAppInDesktop报错：RootInActiveWindow为空");
            return false;
        }
        AccessibilityNodeInfo nodeInfo = AccessibilityHelper.findNodeInfosByText(AppName);
        if(nodeInfo != null){
            mGestureUtil.click(nodeInfo);
            return true;
        }

        //获取应用的包名
        String packageName = mFunction.GetAppPackageName(AppName);

        //获取当前界面的包名
        String CurrentPackageName = AccessibilityHelper.getRootInActiveWindow().getPackageName().toString();

        //该列表是华为、小米、联想手机的桌面的包名
        String PhoneDeskPackageName = "com.miui.home,com.huawei.android.launcher,com.zui.launcher,eie.robot.com";

        //开始左右滑动来查找应用进行点击
        //左滑五次
        int i = 5;
        while (i > 0){
            if(!PhoneDeskPackageName.contains(CurrentPackageName)){
                //不在桌面，直接返回
                return false;
            }
            if(AccessibilityHelper.getRootInActiveWindow().getPackageName().equals(packageName)){
                //当前界面包名和应用包名一直，说明正处于该APP内
                return true;
            }
            mGestureUtil.scroll_left();
            mFunction.click_sleep();
            nodeInfo = AccessibilityHelper.findNodeInfosByText(AppName);
            if(nodeInfo != null){
                mGestureUtil.click(nodeInfo);
                return true;
            }
            i--;
        }

        //右滑五次
        i = 5;
        while (i > 0){
            if(!PhoneDeskPackageName.contains(CurrentPackageName)){
                //不在桌面，直接返回
                return true;
            }
            if(AccessibilityHelper.getRootInActiveWindow().getPackageName().equals(packageName)){
                //当前界面包名和应用包名一直，说明正处于该APP内
                return true;
            }
            mGestureUtil.scroll_right();
            mFunction.click_sleep();
            nodeInfo = AccessibilityHelper.findNodeInfosByText(AppName);
            if(nodeInfo != null){;
                return mGestureUtil.click(nodeInfo);
            }
            i--;
        }
        return false;
    }

    //根据APP的包名打开APP
    private static void OpenAppByPackage(String AppName){
        String packageName = mFunction.GetAppPackageName(AppName);
        Intent intent = mGlobal.mNavigationBarActivity
                        .getPackageManager().getLaunchIntentForPackage(packageName);

        if(intent == null){
            RxToast.error(mGlobal.mNavigationBarActivity, "未安装", Toast.LENGTH_LONG).show();
        }else{
//            int includeBackground = Reflect.on(Intent.class).field("FLAG_RECEIVER_INCLUDE_BACKGROUND").get();
//            intent.setFlags(intent.getFlags()| includeBackground);com.hsh.wyguaji
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_NO_HISTORY);
            mGlobal.mNavigationBarActivity.startActivity(intent);
        }
    }

    //根据APP名字打开应用
    private static boolean OpenApp(String AppName){
        return OpenAppInDesktop(AppName);
    }

    //根据APP名字关闭应用
    public  static void CloseApp(String AppName){
        AccessibilityHelper.performHome(mGlobal.mAccessibilityService);
        mFunction.sleep(mConfig.clickSleepTime);

        String packageName = mFunction.GetAppPackageName(AppName);
        Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
        Uri uri = Uri.fromParts("package", packageName, null);
        intent.setData(uri);
        //mGlobal.mNavigationBarActivity.startActivity(intent);
        ActivityManager am = (ActivityManager) mGlobal.mNavigationBarActivity.getSystemService(Context.ACTIVITY_SERVICE);
        assert am != null;
        am.killBackgroundProcesses(packageName);
    }


    //循环时间打开APP，以保证正确打开了。
    public static Boolean loopOpenApp(String AppName){
        if (mGlobal.mAccessibilityService == null) {
            mFunction.recoverRootWindow();
            RxToast.error(mConfig.AccessibilityServiceIsNoOpen+",无法回到首页");
            return false;
        }

        AccessibilityNodeInfo rootWindow = AccessibilityHelper.getRootInActiveWindow();
        if (rootWindow == null){
            mFunction.recoverRootWindow();
            mToast.error("loopOpenApp出错：rootWindow为空");
            return false;
        }

        String PackageName = mFunction.GetAppPackageName(AppName);
        if(PackageName == null || PackageName.isEmpty() || PackageName.equals("")){
            mToast.error("找不到"+AppName+"，或许未安装");
            return false;
        }
        //如果抓取到的界面的包名不是【当前运行APP】的包名，则尝试打开【当前运行APP】
        if (!rootWindow.getPackageName().equals(PackageName)) {
            if(!mFunction.OpenApp(AppName)){
                //打开APP失败
                return false;
            }
            //休眠等待
            mFunction.sleep(mConfig.WaitLauncherlTime);

            //打开后，尝试多次获取
            int count = mConfig.loopCount*2;
            while (count > 0) {
                mCommonFunctionTask.CloseSystemBulletBox();
                rootWindow = AccessibilityHelper.getRootInActiveWindow();
                if(rootWindow == null){
                    mFunction.click_sleep();
                    continue;
                }
                if (rootWindow.getPackageName().equals(PackageName)) {
                    break;
                }
                AccessibilityHelper.performBack();
                mFunction.click_sleep();
                count--;
            }
        }
        //尝试过后，还不是，则结束
        return rootWindow.getPackageName().equals(PackageName);
    }


    public static boolean recoverRootWindow(){
        if(mCommonTask.isCloseAppTask()){
            return true;
        }
        mGestureUtil.scroll_down_screen();
        mGestureUtil.scroll_up_screen();
        return true;
    }

    //根据APP名字获取

    /**
    / *打开趣头条
    */
    public static void openLocalAPP(Context context,String pkg,String cls){
        try{
            Intent intent = new Intent();
            ComponentName cmp = new ComponentName(pkg, cls);
            intent.setAction(Intent.ACTION_VIEW);
            intent.addCategory(Intent.CATEGORY_LAUNCHER);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            intent.setComponent(cmp);
            context.startActivity(intent);
        }catch (Exception ex){
            RxToast.error(mGlobal.mNavigationBarActivity, "APP打开失败:"+ex.getMessage(), Toast.LENGTH_SHORT, true).show();
        }
    }

    /**
     * 在子线程里执行方法
     * @param runnable
     */
    public static void runInChildThread(Runnable runnable){
        new Thread(runnable).start();
    }

    /**
     * 所在线程休眠所给的时间
     * @param time
     */
    public static void sleep(long time)  {
        if (time > 0) {
            try {
                Thread.sleep(time);
            } catch (InterruptedException e) {
                try {
                    Thread.interrupted();
                }catch (Exception ex){

                }
                e.printStackTrace();
            }
        }
    }

    /**
     * 所在线程休眠所给的时间
     */
    public static void click_sleep()  {
        mFunction.sleep(mConfig.clickSleepTime);
    }
    /**
     * 点亮手机屏幕
     */
    public static void openScreen(){
        PowerManager powerManager = (PowerManager)mGlobal.mAccessibilityService.getSystemService(Context.POWER_SERVICE);
        //true为打开，false为关闭
        boolean ifOpen = powerManager.isScreenOn();
        if (!ifOpen) {
            // 获取PowerManager.WakeLock对象,后面的参数|表示同时传入两个值,最后的是LogCat里用的Tag
            PowerManager.WakeLock wl = powerManager.newWakeLock(
                    PowerManager.ACQUIRE_CAUSES_WAKEUP | PowerManager.FULL_WAKE_LOCK | PowerManager.ON_AFTER_RELEASE, "BOARD");
            wl.acquire(10000); // 点亮屏幕
            wl.release(); // 释放
        }
    }


    public static Boolean getRandomBooleanOffsetFalse(){
        double res = Math.random()*20;
        if(res > 15){
            return true;
        }
        return false;
    }
    public static Boolean getRandomBooleanOffsetTrue(){
        double res = Math.random()*20;
        if(res > 9){
            return true;
        }
        return false;
    }
    public static Boolean getRandomBoolean(){
        double res = Math.random()*20;
        if(res > 10){
            return true;
        }
        return false;
    }
    public static float getRandom_0_20(){
        return (float) (Math.random()*20);
    }
    public static float getRandom_0_50(){
        return (float) (Math.random()*50);
    }
    public static float getRandom_0_200(){
        return (float) Math.random()*200;
    }
    public static int getRandom_1_20(){
        return (int) (Math.random()*20)+1;
    }
    public static int getRandom_50_100(){
        return (int) ((Math.random()+1)*50);
    }
    public static int getRandom_10_20(){
        return (int) ((Math.random()+1)*10);
    }
    public static int getRandom_10_15(){
        return ((int) Math.random()/2+1)*10;
    }
    public static int getRandom_4_8(){
        return (int) ((Math.random()+1)*4);
    }
    public static int getRandom_3_6(){
       int count = (int) ((Math.random()+1)*3);
       return count;
    }
    public static int getRandom_2_5(){
        int count = (int) ((Math.random()*2)+2);
        if(count > 4){
            count = 4;
        }
        return count;
    }
    public static double getRandom_06_08(){
        double count = Math.random();
        if(count > 0.8){
            count = 0.8;
        }
        if(count < 0.6){
            count = 0.6;
        }
        return count;
    }
    public static int getRandom_2_4(){
        int count = (int) ((Math.random()*2)+2);
        if(count > 4){
            count = 4;
        }
        return count;
    }
    public static int getRandom_6_12(){
        int count =(int)  ((Math.random()+1)*6);//6 ~ 12

        return count;
    }
    public static int getRandom_8_14(){
        int count =(int)  ((Math.random()+1)*6)+2;//6 ~ 12

        return count;
    }
    public static int getRandom_1_3(){
        return (int) ( Math.random()*2)+1;
    }

    public static boolean openAccessibilityService(){
        //先判断【辅助服务】是否启动,否则前往开始界面
        if(!mAccessibilityService.isRunning()){
            RxToast.info(mGlobal.mNavigationBarActivity, "Accessibility服务未启动，请选启动！", Toast.LENGTH_SHORT, true).show();
            mFunction.openAccessibilityServiceSettings();
            return false;
        }
        return true;
    }

    public static boolean judgeAppIsHome(String nav1,String nav2){
        //判断是否处于文章页，如果不是则退出
        AccessibilityNodeInfo XinWenNode = AccessibilityHelper.findNodeInfosByText(nav1);
        AccessibilityNodeInfo VideoNode = AccessibilityHelper.findNodeInfosByText(nav2);
        if((XinWenNode != null && XinWenNode.getText().toString().equals(nav1))
                && (VideoNode != null && VideoNode.getText().toString().equals(nav2))){
           return true;
        }
        return false;
    }
    public static boolean judgeAppIsHomeById(String nav1,String nav2){
        //判断是否处于文章页，如果不是则退出
        AccessibilityNodeInfo XinWenNode = AccessibilityHelper.findNodeInfosById(nav1);
        AccessibilityNodeInfo VideoNode = AccessibilityHelper.findNodeInfosById(nav2);
        return XinWenNode != null && VideoNode != null;
    }


    public static boolean judgeAndroidVersionIsGreater7(){
        Boolean result = Build.VERSION.SDK_INT >= 24;
        return result;

    }

    public static int getNodeHeight(AccessibilityNodeInfo node){
        if(node == null ){
            return 0;
        }
        Rect rect = new Rect();
        node.getBoundsInScreen(rect);
        int height = rect.height();
                return height;
    }
    public static int getNodeWidth(AccessibilityNodeInfo node){
        if(node == null ){
            return 0;
        }
        Rect rect = new Rect();
        node.getBoundsInScreen(rect);

        return rect.width();
    }
}
