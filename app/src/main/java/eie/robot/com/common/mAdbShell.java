package eie.robot.com.common;

import android.annotation.SuppressLint;
import android.graphics.Path;
import android.util.Log;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

public class mAdbShell {

    private static final String TAG = "RootCmd";
    private static boolean mHaveRoot = false;

    // 判断机器Android是否已经root，即是否获取root权限
    public static boolean haveRoot() {
        if (!mHaveRoot) {
            int ret = execRootCmdSilent("echo test"); // 通过执行测试命令来检测
            if (ret != -1) {
                //Log.i(TAG, "have root!");
                mHaveRoot = true;
            }
        } else {
            //Log.i(TAG, "mHaveRoot = true, have root!");
        }
        return mHaveRoot;
    }

    //执行命令并且输出结果
    public static String execRootCmd(String cmd) {
        String result = "";
        DataOutputStream dos = null;
        DataInputStream dis = null;
        try {
            Process p = Runtime.getRuntime().exec("su");// 经过Root处理的android系统即有su命令
            dos = new DataOutputStream(p.getOutputStream());
            dis = new DataInputStream(p.getInputStream());

            Log.i(TAG, cmd);
            dos.writeBytes(cmd + "\n");
            dos.flush();
            dos.writeBytes("exit\n");
            dos.flush();
            String line = null;
            while ((line = dis.readLine()) != null) {
                Log.d("result", line);
                result += line;
            }
            p.waitFor();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (dos != null) {
                try {
                    dos.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if (dis != null) {
                try {
                    dis.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return result;
    }

    //执行命令但不关注结果输出
    public static int execRootCmdSilent(String cmd) {
        int result = -1;
        DataOutputStream dos = null;

        try {
            Process p = Runtime.getRuntime().exec("su");
            dos = new DataOutputStream(p.getOutputStream());


            dos.writeBytes(cmd + "\n");
            dos.flush();
            dos.writeBytes("exit\n");
            dos.flush();
            p.waitFor();
            result = p.exitValue();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (dos != null) {
                try {
                    dos.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return result;
    }

    public static String GetPackagesList(){
        return execRootCmd("pm list packages");
    }

    public static boolean click(float x,float y,long clicktime){
        String shells = "input tap "+x+" "+y;
        Integer result = execRootCmdSilent(shells);
        mFunction.sleep(clicktime);
        return true;
    }

    public static boolean dispatchGesture(float sx,float sy,float dx,float dy,long duration){

        String cmd = String.format("input swipe %f %f %f %f ",sx,sy,dx,dy);
        Integer result = execRootCmdSilent(cmd);
        mFunction.sleep(1000);
        return true;
    }

    public static boolean CloseWIFI(){
        execRootCmdSilent(" svc wifi disable ");
        mFunction.sleep(1000);
        return true;
    }

    public static boolean OpenWIFI(){
        execRootCmdSilent(" svc wifi enable ");
        mFunction.sleep(1000);
        return true;
    }

    public static boolean stopApp(String AppName){
        try{
            if(!mFunction.judgeAndroidVersionIsGreater7() && mAdbShell.haveRoot()){
                String PackageName = mFunction.GetAppPackageName(AppName);
                if(PackageName == null || PackageName.isEmpty() || PackageName.equals("")){
                    return false;
                }
                String shells = "am force-stop "+PackageName;
                Integer result = execRootCmdSilent(shells);
                mToast.success("强行关闭:"+AppName);
                mFunction.click_sleep();
            }
        }catch (Exception ex){
            return false;
        }
        return true;
    }

    public static boolean clearApp(String AppName){
        try{
            if(!mFunction.judgeAndroidVersionIsGreater7() && mAdbShell.haveRoot()){
                String PackageName = mFunction.GetAppPackageName(AppName);
                if(PackageName == null || PackageName.isEmpty() || PackageName.equals("")){
                    return false;
                }
//                String shells = "pm clear "+PackageName;
//                Integer result = execRootCmdSilent(shells);
                mToast.success("清理手机数据:"+AppName);
                mFunction.click_sleep();
            }
        }catch (Exception ex){
            return false;
        }
        return true;
    }

}
