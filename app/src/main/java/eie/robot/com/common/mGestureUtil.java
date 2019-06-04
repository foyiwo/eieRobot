package eie.robot.com.common;

import android.accessibilityservice.AccessibilityService;
import android.accessibilityservice.GestureDescription;
import android.graphics.Path;
import android.graphics.Rect;
import android.view.accessibility.AccessibilityNodeInfo;

public class mGestureUtil {
    /**
     * 触发一个触摸手势
     * @param sx 起点 x 轴
     * @param sy 起点 y 轴
     * @param dx 终点 x 轴
     * @param dy 终点 x 轴
     */
    private static Boolean dispatchGesture(float sx,float sy,float dx,float dy,long duration){
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
    public static Boolean dispatchGesture(Path path,long duration){
        GestureDescription.Builder builder = new GestureDescription.Builder();
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

    public static Boolean click(AccessibilityNodeInfo nodeInfo){
        float x = 0;
        float y = 0;

        Rect rect = new Rect();
        nodeInfo.getBoundsInScreen(rect);

        x = rect.left+rect.width()/2;
        y = rect.top+rect.height()/2;

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
    //点击某个点手势
    public static Boolean click(float x,float y,long clicktime){
        GestureDescription.Builder builder = new GestureDescription.Builder();
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

        Path path = new Path();

        float dx = (mGlobal.mScreenWidth/3) + mFunction.getRandom_0_50();
        float dy = (float) (mGlobal.mScreenHeight/1.3) - mFunction.getRandom_0_50();
        path.moveTo(dx,dy);

        int count = mFunction.getRandom_6_12();
        int Factor = 1;
        for (int i=0; i < count; i++){
            float dxx = dx - Factor*mFunction.getRandom_0_50();
            dy = dy - mFunction.getRandom_50_100();
            path.lineTo(dxx,dy);

            Factor = Factor * -1;
        }
        dispatchGesture(path,duration);
        mFunction.sleep(mConfig.clickSleepTime);

    }
    public static void scroll_up(long distance ,long duration){

        Path path = new Path();
        path.moveTo(mGlobal.mScreenWidth/2,(float) (mGlobal.mScreenHeight/2));
        path.lineTo(mGlobal.mScreenWidth/2,mGlobal.mScreenHeight/2-distance);
        mGestureUtil.dispatchGesture(path,duration);
        mFunction.sleep(mConfig.clickSleepTime/2);

    }
    public static void scroll_down(long distance ,long duration){

        Path path = new Path();
        path.moveTo(mGlobal.mScreenWidth/2,mGlobal.mScreenHeight/3);
        path.lineTo(mGlobal.mScreenWidth/2,mGlobal.mScreenHeight/3+distance);
        mGestureUtil.dispatchGesture(path,duration);
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


}
