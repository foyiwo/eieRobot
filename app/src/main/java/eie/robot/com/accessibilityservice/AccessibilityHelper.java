package eie.robot.com.accessibilityservice;

import android.accessibilityservice.AccessibilityService;
import android.annotation.TargetApi;
import android.os.Build;
import android.text.TextUtils;
import android.view.accessibility.AccessibilityNodeInfo;

import java.lang.reflect.Field;
import java.util.List;

import eie.robot.com.common.mConfig;
import eie.robot.com.common.mFunction;
import eie.robot.com.common.mGlobal;

/**
 * <p>Created 16/2/4 上午9:49.</p>
 * <p><a href="mailto:730395591@qq.com">Email:730395591@qq.com</a></p>
 * <p><a href="http://www.happycodeboy.com">LeonLee Blog</a></p>
 *
 * @author LeonLee
 */
public final class AccessibilityHelper {


    private AccessibilityHelper() {

    }

    /**
     * 通过id查找
     */
    public static AccessibilityNodeInfo findNodeInfosById(AccessibilityNodeInfo nodeInfo, String resId) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR2) {
            List<AccessibilityNodeInfo> list = nodeInfo.findAccessibilityNodeInfosByViewId(resId);
            if (list != null && !list.isEmpty()) {
                return list.get(0);
            }
        }
        return null;
    }
    /**
     * 通过id查找
     */
    public static AccessibilityNodeInfo findNodeInfosById( String resId) {
        return findNodeInfosById(mGlobal.mAccessibilityService.getRootInActiveWindow(),resId);
    }
    /**
     * 通过id查找
     */
    public static AccessibilityNodeInfo findNodeInfosByIdDesc(AccessibilityNodeInfo nodeInfo, String resId) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR2) {
            List<AccessibilityNodeInfo> list = nodeInfo.findAccessibilityNodeInfosByViewId(resId);
            if (list != null && !list.isEmpty()) {
                return list.get(list.size() - 1);
            }
        }
        return null;
    }

    public static List<AccessibilityNodeInfo> findNodeInfosByIds(AccessibilityNodeInfo nodeInfo, String resId) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR2) {
            List<AccessibilityNodeInfo> list = nodeInfo.findAccessibilityNodeInfosByViewId(resId);
            if (list != null && !list.isEmpty()) {
                return list;
            }
        }
        return null;
    }

    /**
     * 通过文本查找
     */
    public static AccessibilityNodeInfo findNodeInfosByText(AccessibilityNodeInfo nodeInfo, String text) {
        List<AccessibilityNodeInfo> list = nodeInfo.findAccessibilityNodeInfosByText(text);
        if (list == null || list.isEmpty()) {
            return null;
        }
        return list.get(0);
    }


    /**
     * 通过文本查找
     */
    public static AccessibilityNodeInfo findNodeInfosByText( String text) {
        if(mGlobal.mAccessibilityService.getRootInActiveWindow() == null){
            return null;
        }
        return findNodeInfosByText(mGlobal.mAccessibilityService.getRootInActiveWindow(),text);
    }

    /**
     * 通过文本查找
     */
    public static List<AccessibilityNodeInfo> findNodeInfoByText(String text) {
        List<AccessibilityNodeInfo> list = mGlobal.mAccessibilityService.getRootInActiveWindow().findAccessibilityNodeInfosByText(text);
        if (list == null || list.isEmpty()) {
            return null;
        }
        return list;
    }

    /**
     * 通过关键字查找
     */
    public static AccessibilityNodeInfo findNodeInfosByTexts(AccessibilityNodeInfo nodeInfo, String... texts) {
        for (String key : texts) {
            AccessibilityNodeInfo info = findNodeInfosByText(nodeInfo, key);
            if (info != null) {
                return info;
            }
        }
        return null;
    }

    /**
     * 通过组件名字查找
     */
    public static AccessibilityNodeInfo findNodeInfosByClassName(AccessibilityNodeInfo nodeInfo, String className) {
        if (TextUtils.isEmpty(className)) {
            return null;
        }
        AccessibilityNodeInfo Info = null;
        for (int i = 0; i < nodeInfo.getChildCount(); i++) {
            AccessibilityNodeInfo node = nodeInfo.getChild(i);
            if (className.contentEquals(node.getClassName())) {
                return node;
            }
            else if(node.getChildCount() > 0) {
                Info = findNodeInfosByClassName(node,className);
            }
            if(Info != null && Info.getClassName().equals(className)){
                break;
            }
        }
        return Info;
    }

    /**
     * 通过组件名字查找
     */
    public static AccessibilityNodeInfo findNodeInfosByContentDescription(AccessibilityNodeInfo nodeInfo, String ContentDescription) {
        if (TextUtils.isEmpty(ContentDescription)) {
            return null;
        }

        AccessibilityNodeInfo NodeInfo = null;

        for (int i = 0; i < nodeInfo.getChildCount(); i++) {
            AccessibilityNodeInfo node = nodeInfo.getChild(i);
            if (ContentDescription.equals(node.getContentDescription())) {
                NodeInfo = node;
            }
            if(node.getChildCount() > 0){
                NodeInfo = findNodeInfosByContentDescription(node,ContentDescription);
            }
            if(NodeInfo != null){
                return NodeInfo;
            }
        }
        return null;
    }
    /**
     * 找父组件
     */
    public static AccessibilityNodeInfo findParentNodeInfosByClassName(AccessibilityNodeInfo nodeInfo, String className) {
        if (nodeInfo == null) {
            return null;
        }
        if (TextUtils.isEmpty(className)) {
            return null;
        }
        if (className.equals(nodeInfo.getClassName())) {
            return nodeInfo;
        }
        return findParentNodeInfosByClassName(nodeInfo.getParent(), className);
    }

    private static final Field sSourceNodeField;

    static {
        Field field = null;
        try {
            field = AccessibilityNodeInfo.class.getDeclaredField("mSourceNodeId");
            field.setAccessible(true);
        } catch (Exception e) {
            e.printStackTrace();
        }
        sSourceNodeField = field;
    }

    public static long getSourceNodeId(AccessibilityNodeInfo nodeInfo) {
        if (sSourceNodeField == null) {
            return -1;
        }
        try {
            return sSourceNodeField.getLong(nodeInfo);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return -1;
    }

    public static String getViewIdResourceName(AccessibilityNodeInfo nodeInfo) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
            return nodeInfo.getViewIdResourceName();
        }
        return null;
    }

    /**
     * 返回主界面事件
     */
    @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
    public static void performHome(AccessibilityService service) {
        if (service == null) {
            return;
        }
        service.performGlobalAction(AccessibilityService.GLOBAL_ACTION_HOME);
    }

    /**
     * 返回事件
     */
    @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
    public static void performBack(AccessibilityService service) {
        if (service == null) {
            return;
        }
        service.performGlobalAction(AccessibilityService.GLOBAL_ACTION_BACK);
    }

    /**
     * 点击事件
     */
    public static boolean performClick(AccessibilityNodeInfo nodeInfo) {
        if (nodeInfo == null) {
            return false;
        }

        if (nodeInfo.isClickable()) {
            return nodeInfo.performAction(AccessibilityNodeInfo.ACTION_CLICK);
        } else {
            return performClick(nodeInfo.getParent());
        }
    }

    /**
     * scroll事件
     */
    public static boolean scroll(AccessibilityNodeInfo nodeInfo) {
        if (nodeInfo == null) {
            return false;
        }
        if (nodeInfo.isScrollable()) {
            return nodeInfo.performAction(AccessibilityNodeInfo.ACTION_SCROLL_FORWARD);
        } else {
            return performClick(nodeInfo.getParent());
        }
    }

    /**
     * 由于手机存在卡顿的可能，这个方法，会尝试多次获取对应的Node
     */
    public static AccessibilityNodeInfo loopFindNodeInfoByID(AccessibilityService iABService, String NodeId)  {

        //尝试多次获取
        int count = mConfig.loopCount;
        AccessibilityNodeInfo rootWindow = null;
        AccessibilityNodeInfo node = null;
        while (true) {
            rootWindow = iABService.getRootInActiveWindow();
            node = AccessibilityHelper.findNodeInfosById(rootWindow, NodeId);
            if (node != null) {
                break;
            }
            count--;
            if (count < 0) {
                break;
            }
            mFunction.sleep(mConfig.loopSleepTime);
        }
        return node;
    }

    /**
     * 由于手机存在卡顿的可能，这个方法，会尝试多次获取对应的Node
     */
    public static AccessibilityNodeInfo loopFindNodeInfoByTexts(String[] Texts)  {

        //尝试多次获取
        int count = mConfig.loopCount;
        AccessibilityNodeInfo rootWindow = null;
        AccessibilityNodeInfo node = null;
        while (true) {
            rootWindow = mGlobal.mAccessibilityService.getRootInActiveWindow();
            for (String text : Texts) {
                node = AccessibilityHelper.findNodeInfosByText(rootWindow, text);
                if (node != null) {
                    break;
                }
            }
            if (node != null) {
                break;
            }
            count--;
            if (count < 0) {
                break;
            }
            mFunction.sleep(mConfig.loopSleepTime);
        }
        return node;
    }

    /**
     * 由于手机存在卡顿的可能，这个方法，会尝试多次获取对应的Node
     */
    public static AccessibilityNodeInfo loopFindNodeInfoByText(String text)  {

        //尝试多次获取
        int count = 2;
        AccessibilityNodeInfo rootWindow = null;
        AccessibilityNodeInfo node = null;
        while (true) {
            rootWindow = mGlobal.mAccessibilityService.getRootInActiveWindow();
            node = AccessibilityHelper.findNodeInfosByText(rootWindow, text);
            if (node != null) {
                break;
            }
            count--;
            if (count < 0) {
                break;
            }
            mFunction.sleep(mConfig.loopSleepTime);
        }
        return node;
    }

    public static List<AccessibilityNodeInfo> findNodeInfosByIds( String resId) {
        if(mGlobal.mAccessibilityService == null){
            return null;
        }
        return AccessibilityHelper.findNodeInfosByIds(mGlobal.mAccessibilityService.getRootInActiveWindow(),resId);
    }
}
