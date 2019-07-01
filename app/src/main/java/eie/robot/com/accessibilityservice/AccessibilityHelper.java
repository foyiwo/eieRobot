package eie.robot.com.accessibilityservice;

import android.accessibilityservice.AccessibilityService;
import android.annotation.TargetApi;
import android.os.Build;
import android.text.TextUtils;
import android.view.accessibility.AccessibilityNodeInfo;
import android.view.accessibility.AccessibilityWindowInfo;

import java.lang.reflect.Field;
import java.util.List;

import eie.robot.com.common.mConfig;
import eie.robot.com.common.mFunction;
import eie.robot.com.common.mGlobal;
import eie.robot.com.common.mToast;

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
        return findNodeInfosById(AccessibilityHelper.getRootInActiveWindow(),resId);
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
        if(AccessibilityHelper.getRootInActiveWindow() == null){
            mFunction.recoverRootWindow();

        }
        if(AccessibilityHelper.getRootInActiveWindow() == null){
            return null;
        }
        return findNodeInfosByText(AccessibilityHelper.getRootInActiveWindow(),text);
    }


    /**
     * 通过包含文本查找
     */
    public static AccessibilityNodeInfo findChildNodeInfosByText(String text) {
        if(AccessibilityHelper.getRootInActiveWindow() == null){
            mFunction.recoverRootWindow();
            return null;
        }
        if(AccessibilityHelper.getRootInActiveWindow() == null){
            return null;
        }
        return findChildNodeInfosByText(AccessibilityHelper.getRootInActiveWindow(),text);

    }

    /**
     * 通过文本查找
     */
    public static AccessibilityNodeInfo findChildNodeInfosByText( AccessibilityNodeInfo nodeInfo, String text) {
        if (TextUtils.isEmpty(text) || nodeInfo == null) {
            return null;
        }
        AccessibilityNodeInfo Info = null;
        int childCount = nodeInfo.getChildCount();
        for (int i = 0; i < childCount; i++) {
            AccessibilityNodeInfo node = nodeInfo.getChild(i);
            String NodeStrigs = null;
            if (node.getText() != null ) {
                NodeStrigs = node.getText().toString();
            }
            if (node.getText() != null && node.getText().toString().contains(text)) {
                return node;
            }
            String NodeStrig = null;
            if (node.getContentDescription() != null ) {
                NodeStrig  = node.getContentDescription().toString();
            }
            if (node.getContentDescription() != null && node.getContentDescription().toString().contains(text)) {
                return node;
            }
            else if(node.getChildCount() > 0) {
                int count = node.getChildCount();
                Info = findChildNodeInfosByText(node,text);
            }
            if(Info != null && Info.getText() != null && Info.getText().toString().contains(text)){
                break;
            }
            if(Info != null && Info.getContentDescription() != null && Info.getContentDescription().toString().contains(text)){
                break;
            }
        }
        return Info;
    }

    /**
     * 通过相等文本相同查找
     */
    public static AccessibilityNodeInfo findNodeInfosByEqualText(String text) {
        if(AccessibilityHelper.getRootInActiveWindow() == null){
            mFunction.recoverRootWindow();
            return null;
        }
        if(AccessibilityHelper.getRootInActiveWindow() == null){
            return null;
        }
        return findNodeInfosByEqualText(AccessibilityHelper.getRootInActiveWindow(),text);

    }
    /**
     * 通过文本查找
     */
    public static AccessibilityNodeInfo findNodeInfosByEqualText( AccessibilityNodeInfo nodeInfo, String text) {
        if (TextUtils.isEmpty(text) || nodeInfo == null) {
            return null;
        }
        AccessibilityNodeInfo Info = null;
        int childCount = nodeInfo.getChildCount();
        for (int i = 0; i < childCount; i++) {
            AccessibilityNodeInfo node = nodeInfo.getChild(i);
            String NodeStrigs = null;
            if (node.getText() != null ) {
                NodeStrigs = node.getText().toString();
            }
            if (node.getText() != null && node.getText().toString().equals(text)) {
                return node;
            }
            String NodeStrig = null;
            if (node.getContentDescription() != null ) {
                NodeStrig  = node.getContentDescription().toString();
            }
            if (node.getContentDescription() != null && node.getContentDescription().toString().equals(text)) {
                return node;
            }
            else if(node.getChildCount() > 0) {
                int count = node.getChildCount();
                Info = findNodeInfosByEqualText(node,text);
            }
            if(Info != null && Info.getText() != null && Info.getText().toString().equals(text)){
                break;
            }
            if(Info != null && Info.getContentDescription() != null && Info.getContentDescription().toString().equals(text)){
                break;
            }
        }
        return Info;
    }


    /**
     * 通过文本查找
     */
    public static List<AccessibilityNodeInfo> findNodeInfoByText(String text) {
        List<AccessibilityNodeInfo> list = AccessibilityHelper.getRootInActiveWindow().findAccessibilityNodeInfosByText(text);
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
    public static AccessibilityNodeInfo findNodeInfosByClassName(String className) {
        if(AccessibilityHelper.getRootInActiveWindow() == null){
            mFunction.recoverRootWindow();
            return null;
        }
        if(AccessibilityHelper.getRootInActiveWindow() == null){
            return null;
        }
        AccessibilityNodeInfo node = AccessibilityHelper.getRootInActiveWindow();
        if(node == null){
            return null;
        }
        return findNodeInfosByClassName(node ,className);
    }

    /**
     * 通过组件名字查找
     */
    public static AccessibilityNodeInfo findChildNodeInfosById(AccessibilityNodeInfo nodeInfo, String resId) {
        if(nodeInfo == null){ return null; }

        if (TextUtils.isEmpty(resId)) {
            return null;
        }
        AccessibilityNodeInfo Info = null;
        for (int i = 0; i < nodeInfo.getChildCount(); i++) {
            AccessibilityNodeInfo node = nodeInfo.getChild(i);
            if (node.getViewIdResourceName() != null && resId.contentEquals(node.getViewIdResourceName())) {
                return node;
            }
            else if(node.getChildCount() > 0) {
                Info = findChildNodeInfosById(node,resId);
            }
            if(Info != null && Info.getViewIdResourceName().equals(resId)){
                break;
            }
        }
        return Info;
    }
    /**
     * 通过组件名字查找
     */
    public static AccessibilityNodeInfo findNodeInfosByClassName(AccessibilityNodeInfo nodeInfo, String className) {
        if (TextUtils.isEmpty(className)) {
            return null;
        }
        AccessibilityNodeInfo Info = null;
        try{
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
        }catch (Exception ex){
            Info = null;
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
    public static void performHome() {
        if (mGlobal.mAccessibilityService == null) {
            return;
        }
        mGlobal.mAccessibilityService.performGlobalAction(AccessibilityService.GLOBAL_ACTION_HOME);
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
    @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
    public static void performBack() {
        if (mGlobal.mAccessibilityService == null) {
            return;
        }
        mGlobal.mAccessibilityService.performGlobalAction(AccessibilityService.GLOBAL_ACTION_BACK);
    }
    public static void performRecents() {
        if (mGlobal.mAccessibilityService == null) {
            return;
        }
        mGlobal.mAccessibilityService.performGlobalAction(AccessibilityService.GLOBAL_ACTION_RECENTS);
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
            rootWindow = AccessibilityHelper.getRootInActiveWindow();
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
        if(AccessibilityHelper.getRootInActiveWindow() == null){
            mFunction.recoverRootWindow();
            return null;
        }
        if(AccessibilityHelper.getRootInActiveWindow() == null){
            return null;
        }
        //尝试多次获取
        int count = 2;
        AccessibilityNodeInfo rootWindow = null;
        AccessibilityNodeInfo node = null;
        while (true) {
            rootWindow = AccessibilityHelper.getRootInActiveWindow();
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
        if(AccessibilityHelper.getRootInActiveWindow() == null){
            mFunction.recoverRootWindow();
            return null;
        }
        if(AccessibilityHelper.getRootInActiveWindow() == null){
            return null;
        }
        if(mGlobal.mAccessibilityService == null){
            return null;
        }
        return AccessibilityHelper.findNodeInfosByIds(AccessibilityHelper.getRootInActiveWindow(),resId);
    }

    public static AccessibilityNodeInfo getRootInActiveWindow(){
        AccessibilityNodeInfo node = mGlobal.mAccessibilityService.getRootInActiveWindow();
        if(node == null){
            mFunction.recoverRootWindow();
        }
        node = mGlobal.mAccessibilityService.getRootInActiveWindow();
        if(node != null){
            return node;
        }
        List<AccessibilityWindowInfo> windows = mGlobal.mAccessibilityService.getWindows();
        for (AccessibilityWindowInfo info : windows){
            if(info.getType() == AccessibilityWindowInfo.TYPE_APPLICATION){
                return info.getRoot();
            }
        }
        return mGlobal.mAccessibilityService.getRootInActiveWindow();
    }

    /**
     * 通过文本查找
     */
    public static AccessibilityNodeInfo findWebViewNodeInfosByText(String text) {

        AccessibilityNodeInfo webView = AccessibilityHelper.findNodeInfosByClassName("android.webkit.WebView");
        if(webView == null){
            return null;
        }
        return findChildNodeInfosByText(webView,text);
    }

    public static String getNodeInfosTextByText(String text){
        AccessibilityNodeInfo nodeInfo = AccessibilityHelper.findNodeInfosByText(text);
        if(nodeInfo == null) return "";
        if(nodeInfo.getText() == null) return "";
        return nodeInfo.getText().toString().trim();
    }
    public static String getWebNodeInfosTextByText(String text){
        AccessibilityNodeInfo nodeInfo = AccessibilityHelper.findWebViewNodeInfosByText(text);
        if(nodeInfo == null) return null;
        CharSequence NodeText = nodeInfo.getText() == null ? nodeInfo.getContentDescription() : nodeInfo.getText();
        if(NodeText == null) return null;
        return NodeText.toString();
    }
    public static String getNodeInfosTextByNode(AccessibilityNodeInfo nodeInfo){
        if(nodeInfo == null) return null;
        CharSequence NodeText = nodeInfo.getText() == null ? nodeInfo.getContentDescription() : nodeInfo.getText();
        if(NodeText == null) return null;
        return NodeText.toString();
    }
    public static String getNodeInfosTextByResourceId(String ResourceId){
        AccessibilityNodeInfo nodeInfo = AccessibilityHelper.findNodeInfosById(ResourceId);
        if(nodeInfo == null) return "";

        CharSequence charSequence = nodeInfo.getText() == null ? nodeInfo.getContentDescription() : nodeInfo.getText();
        if(charSequence == null) return "";
        return charSequence.toString().trim();
    }
}
