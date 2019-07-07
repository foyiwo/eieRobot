package eie.robot.com.accessibilityservice;

import android.accessibilityservice.AccessibilityService;
import android.accessibilityservice.AccessibilityServiceInfo;
import android.annotation.TargetApi;
import android.content.Context;
import android.os.Build;
import android.view.accessibility.AccessibilityEvent;
import android.view.accessibility.AccessibilityManager;
import android.view.accessibility.AccessibilityNodeInfo;
import android.widget.Toast;

import java.util.Iterator;
import java.util.List;

import eie.robot.com.common.mGlobal;
import eie.robot.com.common.mToast;


/**
 * AccessibilityService服务，在本服务中，会绑定
 */
public class mAccessibilityService extends AccessibilityService {
    private static final String TAG = "mAccessibilityService";

    //  AccessibilityService    事件回调
    @Override
    public void onAccessibilityEvent(AccessibilityEvent event) {
        mGlobal.mAccessibilityService = this;
    }

    @Override
    public void onInterrupt() {
        mToast.message("无障碍服务连接中断!");
    }

    /**
     * AccessibilityService服务启动成功回调
     */
    @Override
    protected void onServiceConnected() {
        super.onServiceConnected();

        mGlobal.mAccessibilityService = this;
        mToast.message("无障碍服务连接服务成功!");
    }

    @Override
    public void onCreate() {
        super.onCreate();

    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    /**
     * 判断当前服务是否正在运行
     */
    @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
    public static boolean isRunning() {

        if (mGlobal.mAccessibilityService == null) {
           return false;
        }
        AccessibilityManager accessibilityManager = (AccessibilityManager) mGlobal.mAccessibilityService.getSystemService(Context.ACCESSIBILITY_SERVICE);
        AccessibilityServiceInfo info = mGlobal.mAccessibilityService.getServiceInfo();

        if (info == null) {
            return false;
        }
        assert accessibilityManager != null;
        List<AccessibilityServiceInfo> list = accessibilityManager.getEnabledAccessibilityServiceList(
                AccessibilityServiceInfo.FEEDBACK_GENERIC);
        Iterator<AccessibilityServiceInfo> iterator = list.iterator();

        boolean isConnect = false;
        while (iterator.hasNext()) {
            AccessibilityServiceInfo i = iterator.next();
            if (i.getId().equals(info.getId())) {
                isConnect = true;
                break;
            }
        }
        if (!isConnect) {
            return false;
        }
        return true;
    }

}
