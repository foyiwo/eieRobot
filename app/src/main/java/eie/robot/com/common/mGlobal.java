package eie.robot.com.common;

import android.accessibilityservice.AccessibilityService;
import android.annotation.SuppressLint;
import android.app.Application;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.qmuiteam.qmui.arch.QMUIFragmentActivity;
import com.qmuiteam.qmui.widget.QMUITabSegment;
import com.vondear.rxtool.RxDeviceTool;

public class mGlobal {

    //主Activity
    public  static Application mApplication = null;

    //服务器域名
    public  static String baseUrl = "http://www.foyiwo.com:8085";

    //主Activity
    public  static QMUIFragmentActivity mNavigationBarActivity = null;

    //导航栏控件
    public  static QMUITabSegment mNavigationBars = null;

    //测量作用，隐藏状态
    @SuppressLint("StaticFieldLeak")
    public  static TextView viewMeasureScreenHeight = null;

    //辅助服务【Accessibility服务】
    @SuppressLint("StaticFieldLeak")
    public static AccessibilityService mAccessibilityService = null;

    public static int mScreenWidth = 0;//RxDeviceTool.getScreenWidth(mGlobal.mAccessibilityService);

    public static int mScreenHeight = 0;//RxDeviceTool.getScreenHeight(mGlobal.mAccessibilityService);


    //任务状态：0：表示当前任务结束，1：代表当前正在执行任务
    public static int TaskStatus = 0;

    @SuppressLint("StaticFieldLeak")
    public static View viewFloatButton = null;

    //每个APP的任务时间（分）
    @SuppressLint("StaticFieldLeak")
    public static TextView mViewRobTaskTimerMin = null;

    //每个APP里的子任务的任务时间（秒）
    @SuppressLint("StaticFieldLeak")
    public static TextView mViewRobTaskTimerSecond = null;




}
