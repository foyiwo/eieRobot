package eie.robot.com.common;

import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.animation.AccelerateInterpolator;
import android.widget.TextView;

import com.vondear.rxtool.view.RxToast;
import com.yhao.floatwindow.FloatWindow;
import com.yhao.floatwindow.MoveType;
import com.yhao.floatwindow.PermissionListener;
import com.yhao.floatwindow.Screen;
import com.yhao.floatwindow.ViewStateListener;

import eie.robot.com.R;

public class mFloatWindow {

    public static void showFloatWindow(
             @Nullable View.OnClickListener StartRobTask
            ,@Nullable View.OnClickListener CloseFloat
            ,@Nullable View.OnClickListener StopRobTask
    ){
        //监听悬浮控件状态改变
        ViewStateListener viewStateListener = new ViewStateListener() {
            @Override
            public void onPositionUpdate(int i, int i1) {

            }

            @Override
            public void onShow() {

            }

            @Override
            public void onHide() {

            }

            @Override
            public void onDismiss() {

            }

            @Override
            public void onMoveAnimStart() {
                RxToast.success("1");
            }

            @Override
            public void onMoveAnimEnd() {
                RxToast.success("2");
            }

            @Override
            public void onBackToDesktop() {

            }
        };
        //监听权限申请结果
        PermissionListener permissionListener = new PermissionListener() {
            @Override
            public void onSuccess() {

            }

            @Override
            public void onFail() {

            }
        };

        if(mGlobal.mNavigationBarActivity == null){
            RxToast.info("Context为空,无法打开悬浮宽");
        }
        LayoutInflater mInflater = LayoutInflater.from(mGlobal.mNavigationBarActivity);
        View contentView  = mInflater.inflate(R.layout.float_button,null);

        //开始任务
        contentView.findViewById(R.id.btnStartRobTaskButton).setOnClickListener(StartRobTask);
//        //关闭悬浮宽
        contentView.findViewById(R.id.btnCloseFloatButton).setOnClickListener(CloseFloat);
//        //停止任务
        contentView.findViewById(R.id.btnStopRobTaskButton).setOnClickListener(StopRobTask);

        mGlobal.viewFloatButton = contentView;

        mGlobal.mViewRobTaskTimer = (TextView)mGlobal.viewFloatButton.findViewById(R.id.btnRobTaskTimer);

        FloatWindow
                .with(mGlobal.mNavigationBarActivity.getApplicationContext())
                .setView(contentView)
                .setWidth(80)                               //设置控件宽高
                //.setHeight(Screen.width,0.25f)              //自动高度
                .setX(0)                                   //设置控件初始位置
                .setY(Screen.height,0.3f)
                .setDesktopShow(true)                        //桌面显示
                .setMoveType(MoveType.slide)
                .setViewStateListener(viewStateListener)    //监听悬浮控件状态改变
                .setPermissionListener(permissionListener)  //监听权限申请结果
                .setMoveStyle(500, new AccelerateInterpolator())  //贴边动画时长为500ms，加速插值器
                .build();
    }

    public static void showFloatWindow(){
        //开启任务
        View.OnClickListener StartRobTask = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String taskRes = mCommonTask.StartTask();
                if(taskRes.equals("true")){
                    mGlobal.viewFloatButton.findViewById(R.id.btnStartTaskAndCloseFloat).setVisibility(View.GONE);
                    mGlobal.viewFloatButton.findViewById(R.id.btnStopTask).setVisibility(View.VISIBLE);
                }else {
                    mToast.message(taskRes);
                }


            }
        };
        //关闭悬浮窗
        View.OnClickListener CloseFloat = new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        };
        //停止任务
        View.OnClickListener StopRobTask = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mCommonTask.StopTask();
                mToast.message("总任务停止中...");
                mGlobal.viewFloatButton.findViewById(R.id.btnStartTaskAndCloseFloat).setVisibility(View.VISIBLE);
                mGlobal.viewFloatButton.findViewById(R.id.btnStopTask).setVisibility(View.GONE);
            }
        };

        mFloatWindow.showFloatWindow(StartRobTask,CloseFloat,StopRobTask);
    }

    public static void closeFloatWindow(){
        //监听悬浮控件状态改变
        ViewStateListener viewStateListener = new ViewStateListener() {
            @Override
            public void onPositionUpdate(int i, int i1) {

            }

            @Override
            public void onShow() {

            }

            @Override
            public void onHide() {

            }

            @Override
            public void onDismiss() {

            }

            @Override
            public void onMoveAnimStart() {

            }

            @Override
            public void onMoveAnimEnd() {

            }

            @Override
            public void onBackToDesktop() {

            }
        };
        //监听权限申请结果
        PermissionListener permissionListener = new PermissionListener() {
            @Override
            public void onSuccess() {

            }

            @Override
            public void onFail() {

            }
        };

        FloatWindow
                .with(mGlobal.mNavigationBarActivity.getApplicationContext())
                .setView(R.layout.float_button)
                .setWidth(100)                               //设置控件宽高
                //.setHeight(Screen.width,0.25f)              //自动高度
                .setX(100)                                   //设置控件初始位置
                .setY(Screen.height,0.3f)
                .setDesktopShow(true)                        //桌面显示
                .setMoveType(MoveType.slide)
                .setViewStateListener(viewStateListener)    //监听悬浮控件状态改变
                .setPermissionListener(permissionListener)  //监听权限申请结果
                .setMoveStyle(500, new AccelerateInterpolator())  //贴边动画时长为500ms，加速插值器
                .build();


    }


    //修改APP的剩余时间
    public static void EditRobTaskTimerText(final String message){
        mGlobal.mViewRobTaskTimer.post(new Runnable() {
            @Override
            public void run() {
                mGlobal.mViewRobTaskTimer.setText(message);
            }
        });
    }
}
