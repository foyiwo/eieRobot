package eie.robot.com.common;

import android.accessibilityservice.AccessibilityService;
import android.accessibilityservice.GestureDescription;
import android.graphics.Path;
import android.graphics.Rect;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.view.accessibility.AccessibilityNodeInfo;

import com.vondear.rxtool.RxDeviceTool;

import eie.robot.com.accessibilityservice.AccessibilityHelper;

public class mGestureUtil {
    /**
     * 触发一个触摸手势
     * @param sx 起点 x 轴
     * @param sy 起点 y 轴
     * @param dx 终点 x 轴
     * @param dy 终点 x 轴
     */

    private static Boolean dispatchGesture(float sx, float sy, float dx, float dy, long duration){

        //如果Android SDK低于24，也就是Android7.0,则
        if(!mFunction.judgeAndroidVersionIsGreater7()){
            mAdbShell.dispatchGesture(sx,sy,dx,dy,duration);
            return true;
        }else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            return dispatchGesture_N(sx, sy, dx, dy, duration);
        }
        return false;
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    private static Boolean dispatchGesture_N(float sx, float sy, float dx, float dy, long duration){
        GestureDescription.Builder builder = new GestureDescription.Builder();
        Path path = new Path();
        path.moveTo(sx,sy);
        path.lineTo(dx,dy);
        GestureDescription.StrokeDescription Gesture = new GestureDescription.StrokeDescription(path, 0, duration);
        GestureDescription gestureDescription = builder.addStroke(Gesture).build();

        AccessibilityService.GestureResultCallback callback = new AccessibilityService.GestureResultCallback() {
            @Override
            public void onCompleted(GestureDescription gestureDescription) {
                super.onCompleted(gestureDescription);
            }

            @Override
            public void onCancelled(GestureDescription gestureDescription) {
                super.onCancelled(gestureDescription);
            }
        };
        return mGlobal.mAccessibilityService.dispatchGesture(gestureDescription,callback,null);
    }

    /**
     * 触发一个触摸手势
     */
    public static Boolean dispatchGesture(Path path, long duration){

        GestureDescription.Builder builder = null;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N) {
            builder = new GestureDescription.Builder();
            GestureDescription.StrokeDescription Gesture = new GestureDescription.StrokeDescription(path, 100, duration);
            GestureDescription gestureDescription = builder.addStroke(Gesture).build();

            AccessibilityService.GestureResultCallback callback = new AccessibilityService.GestureResultCallback() {
                @Override
                public void onCompleted(GestureDescription gestureDescription) {
                    super.onCompleted(gestureDescription);
                }

                @Override
                public void onCancelled(GestureDescription gestureDescription) {
                    super.onCancelled(gestureDescription);
                }
            };
            return mGlobal.mAccessibilityService.dispatchGesture(gestureDescription,callback,null);
        }
        return false;

    }

    public static Boolean click(AccessibilityNodeInfo nodeInfo){
        if(nodeInfo == null){
            return false;
        }
        if(AccessibilityHelper.performClick(nodeInfo)){
            mFunction.click_sleep();
            return true;
        }
        if(nodeInfo == null){
            return false;
        }

        float x = 0;
        float y = 0;

        Rect rect = new Rect();
        nodeInfo.getBoundsInScreen(rect);

        x = rect.left+rect.width()/2;
        y = rect.top+rect.height()/2;

        return click(x,y);
    }
    public static Boolean clickTop(AccessibilityNodeInfo nodeInfo){
        if(nodeInfo == null){
            return false;
        }

        float x = 0;
        float y = 0;

        Rect rect = new Rect();
        nodeInfo.getBoundsInScreen(rect);

        x = rect.left+rect.width()/2;
        y = rect.top+10;

        return click(x,y);
    }

    public static boolean doubleClickInScreenCenter(){
        float x = mGlobal.mScreenWidth/2;
        float y = mGlobal.mScreenHeight/2;
        mGestureUtil.click(x,y,100);
        mGestureUtil.click(x,y,0);
        return true;
    }
    public static boolean clickInScreenCenter(){
        float x = mGlobal.mScreenWidth/2;
        float y = mGlobal.mScreenHeight/2;
        mGestureUtil.click(x,y,mConfig.clickSleepTime);
        return true;
    }
    public static boolean click(float x,float y){
        return mGestureUtil.click(x,y,mConfig.clickSleepTime);
    }
    public static void clickTab(int TabCount,int Number){
        mGestureUtil.click((mGlobal.mScreenWidth/TabCount)*Number-40,mGlobal.mScreenHeight-40);
    }
    //点击某个点手势
    public static Boolean click(float x,float y,long clicktime){

        if(!mFunction.judgeAndroidVersionIsGreater7()){
            mAdbShell.click(x,y);
            return true;
        }

        GestureDescription.Builder builder = null;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N) {
            builder = new GestureDescription.Builder();
            Path path = new Path();
            path.moveTo(x,y);
            GestureDescription.StrokeDescription Gesture = new GestureDescription.StrokeDescription(path, 0, 10);
            GestureDescription gestureDescription = builder.addStroke(Gesture).build();

            AccessibilityService.GestureResultCallback callback = new AccessibilityService.GestureResultCallback() {
                @Override
                public void onCompleted(GestureDescription gestureDescription) {
                    super.onCompleted(gestureDescription);
                }

                @Override
                public void onCancelled(GestureDescription gestureDescription) {
                    super.onCancelled(gestureDescription);
                }
            };

            boolean res = mGlobal.mAccessibilityService.dispatchGesture(gestureDescription,callback,null);
            mFunction.sleep(clicktime);
            return res;
        }
        return false;
    }

    //点击某个点手势
    public static Boolean clickByText(String text){
        if(text == null || text.isEmpty()){
            return false;
        }
        AccessibilityNodeInfo nodeInfo = AccessibilityHelper.findNodeInfosByText(text);
        if(nodeInfo == null || !nodeInfo.getText().toString().equals(text)){
            return false;
        }
        if(AccessibilityHelper.performClick(nodeInfo)){
            mFunction.click_sleep();
            return true;
        }
        if(nodeInfo != null){
            Rect rect = new Rect();
            nodeInfo.getBoundsInScreen(rect);
            if(rect.top < mGlobal.mScreenHeight-88 && rect.top > 88){
                return mGestureUtil.click(nodeInfo);
            }
        }
        return false;
    }

    //点击某个元素的上面一点
    public static Boolean clickNodeOffsizeTopById(String resid){
        AccessibilityNodeInfo nodeInfo = AccessibilityHelper.findNodeInfosById(resid);
        if(AccessibilityHelper.performClick(nodeInfo)){
            mFunction.click_sleep();
            return true;
        }
        if(nodeInfo != null){
            Rect rect = new Rect();
            nodeInfo.getBoundsInScreen(rect);
            if(rect.top < mGlobal.mScreenHeight-100 && rect.top > 100){
                return mGestureUtil.click(mGlobal.mScreenWidth/2,rect.top-100);
            }
        }
        return false;
    }
    //点击某个点手势
    public static Boolean clickByResourceId(String id){
        AccessibilityNodeInfo nodeInfo = AccessibilityHelper.findNodeInfosById(id);
        if(AccessibilityHelper.performClick(nodeInfo)){
            mFunction.click_sleep();
            return true;
        }
        if(nodeInfo != null){
            Rect rect = new Rect();
            nodeInfo.getBoundsInScreen(rect);
            if(rect.top < mGlobal.mScreenHeight-10 && rect.top > 10){
                return mGestureUtil.click(nodeInfo);
            }
        }
        return false;
    }
    //点击Web里的某个Node
    public static Boolean clickWebNodeByText(String text){
        if(text == null || text.isEmpty()){
            return false;
        }
        AccessibilityNodeInfo nodeInfo = AccessibilityHelper.findWebViewNodeInfosByText(text);

        if(nodeInfo == null){
            return false;
        }
        try{
            String NodeText = nodeInfo.getText() == null ? nodeInfo.getContentDescription().toString() : nodeInfo.getText().toString();
            if(!NodeText.equals(text)){
                return false;
            }
        }catch (Exception ex){
            return false;
        }

        if(mGestureUtil.performClick(nodeInfo)){
            mFunction.click_sleep();
            return true;
        }
        if(nodeInfo != null){
            Rect rect = new Rect();
            nodeInfo.getBoundsInScreen(rect);
            if(rect.top < mGlobal.mScreenHeight-88 && rect.top > 88){
                return mGestureUtil.click(nodeInfo);
            }
        }
        return false;
    }

    //向上滑动
    public static void scroll_up(){
//        float sx = (float) (mGlobal.mScreenWidth/2) + mFunction.getRandom_0_20();
//        float sy = (float) (mGlobal.mScreenHeight/1.3) + mFunction.getRandom_0_50();
//        float dx = (float) (mGlobal.mScreenWidth/2) - mFunction.getRandom_0_20();
//        float dy = (float) (mGlobal.mScreenHeight/3) + mFunction.getRandom_50_100();
//        mGestureUtil.dispatchGesture(sx,sy,dx,dy,duration);
        long duration = 1000;
        mGestureUtil.scroll_up(duration);
    }
    //向上滑动
    public static void scroll_up_30(){
        long duration = 30;
        scroll_up(duration);
    }
    //向上滑动
    public static void scroll_up_500(){
        long duration = 500;
        scroll_up(duration);
    }
    //向上滑动
    public static void scroll_up(long duration){

        try{
            Path path = new Path();

            float dx = (mGlobal.mScreenWidth/3) + mFunction.getRandom_0_50();
            float dy = (float) (mGlobal.mScreenHeight/1.35) - mFunction.getRandom_0_50();
            path.moveTo(dx,dy);

            float sxss = dx;
            float syss = dy;
            float dxss = 0;
            float dyss= 0;


            int count = mFunction.getRandom_6_12();
            int Factor = 1;
            for (int i=0; i < count; i++){
                float dxx = dx - Factor*mFunction.getRandom_0_50();
                dy = dy - mFunction.getRandom_50_100();
                if(dxx <= 0){
                    dxx = 50;
                }
                if(dy <= 0){
                    dy = 50;
                }
                path.lineTo(dxx,dy);
                Factor = Factor * -1;

                dxss = dxx;
                dyss = dy;
            }

            if(mFunction.judgeAndroidVersionIsGreater7()){
                dispatchGesture(path,duration);
            }else {
                mAdbShell.dispatchGesture(sxss,syss,dxss,dyss,duration);
            }
            mFunction.sleep(mConfig.clickSleepTime);
        }catch (Exception ex){

        }
    }
    public static void scroll_up(long distance ,long duration){
        mGestureUtil.dispatchGesture(mGlobal.mScreenWidth/2,(float) (mGlobal.mScreenHeight/2),mGlobal.mScreenWidth/2,mGlobal.mScreenHeight/2-distance,duration);
        mFunction.sleep(mConfig.clickSleepTime/2);

    }
    public static void scroll_down(long distance ,long duration){
        mGestureUtil.dispatchGesture(mGlobal.mScreenWidth/2,mGlobal.mScreenHeight/3,mGlobal.mScreenWidth/2,mGlobal.mScreenHeight/3+distance,duration);
        mFunction.sleep(mConfig.clickSleepTime/2);

    }
    public static void scroll_down_half_screen(){

        mGestureUtil.scroll_down(mGlobal.mScreenHeight/2,1000);

    }

    //向左滑动
    public static void scroll_left(){
        float sx = (float) (mGlobal.mScreenWidth/1.3) + mFunction.getRandom_0_50();
        float sy = (float) (mGlobal.mScreenHeight/1.5) + mFunction.getRandom_50_100();
        float dx = (float) (mGlobal.mScreenWidth/5) + mFunction.getRandom_0_20();
        float dy = (float) (mGlobal.mScreenHeight/1.5) + mFunction.getRandom_0_50();
        long duration = 30;
        mGestureUtil.dispatchGesture(sx,sy,dx,dy,duration);
    }
    //向左滑动
    public static void scroll_right(){
        float sx = (float) (mGlobal.mScreenWidth/1.3) + mFunction.getRandom_0_50();
        float sy = (float) (mGlobal.mScreenHeight/1.5) + mFunction.getRandom_50_100();
        float dx = (float) (mGlobal.mScreenWidth/5) + mFunction.getRandom_0_20();
        float dy = (float) (mGlobal.mScreenHeight/1.5) + mFunction.getRandom_0_50();
        long duration = 30;
        mGestureUtil.dispatchGesture(dx,dy,sx,sy,duration);
    }
    //向上滑动
    public static Boolean scroll_up_quick(){
        mGestureUtil.dispatchGesture(mGlobal.mScreenWidth/6,(float) (1100),mGlobal.mScreenWidth/6,950,20);
        //mGestureUtil.dispatchGesture(mGlobal.mScreenWidth/6,(float) (mGlobal.mScreenHeight/1.5),mGlobal.mScreenWidth/6,mGlobal.mScreenHeight/3,20);
        return true;
    }
    //向下滑动
    public static Boolean scroll_down(){
        return mGestureUtil.dispatchGesture(mGlobal.mScreenWidth/2,mGlobal.mScreenHeight/2,mGlobal.mScreenWidth/2,mGlobal.mScreenHeight,1500);
    }
    //向下滑动
    public static Boolean scroll_down_100(){
        mGestureUtil.dispatchGesture(mGlobal.mScreenWidth/2,mGlobal.mScreenHeight/2,mGlobal.mScreenWidth/2,mGlobal.mScreenHeight,100);
        mFunction.click_sleep();
        return true;
    }

    public static boolean scroll_down_screen(){
        mGestureUtil.dispatchGesture(mGlobal.mScreenWidth/2,0,mGlobal.mScreenWidth/2,mGlobal.mScreenHeight,1000);
        mFunction.click_sleep();
        return true;
    }
    public static boolean scroll_up_screen(){
        mGestureUtil.dispatchGesture(mGlobal.mScreenWidth/2,mGlobal.mScreenHeight,mGlobal.mScreenWidth/2,0,1000);
        mFunction.click_sleep();
        return true;
    }
    public static void MultipleScrollUp(int count){
        int i=0;
        while (i < count) {
            mFunction.sleep(mConfig.clickSleepTime);
            mGestureUtil.scroll_up_quick();
            i++;
        }
    }

    public static Boolean performClick(AccessibilityNodeInfo nodeInfo){
        if(nodeInfo == null) return false;
        if(AccessibilityHelper.performClick(nodeInfo)){
            mFunction.click_sleep();
            return true;
        }
        if(nodeInfo != null){
            Rect rect = new Rect();
            nodeInfo.getBoundsInScreen(rect);
            if(rect.top < mGlobal.mScreenHeight-88 && rect.top > 88){
                return mGestureUtil.click(nodeInfo);
            }
        }
        return false;
    }


}
