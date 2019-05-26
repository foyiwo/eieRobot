package eie.robot.com.activity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.animation.AccelerateInterpolator;
import android.widget.Toast;

import com.qmuiteam.qmui.arch.QMUIFragmentActivity;
import com.vondear.rxtool.RxDeviceTool;
import com.vondear.rxtool.view.RxToast;
import com.yhao.floatwindow.FloatWindow;
import com.yhao.floatwindow.MoveType;
import com.yhao.floatwindow.Screen;

import eie.robot.com.R;
import eie.robot.com.accessibilityservice.mAccessibilityService;
import eie.robot.com.common.mCommonTask;
import eie.robot.com.common.mDeviceUtil;
import eie.robot.com.common.mFloatWindow;
import eie.robot.com.common.mFunction;
import eie.robot.com.common.mGlobal;
import eie.robot.com.fragment.BaseFragment;
import eie.robot.com.fragment.HomeFragment;

public class NavigationBarActivity extends QMUIFragmentActivity {
    @Override
    protected int getContextViewId() { return R.id.eieRobot; }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (savedInstanceState == null) {
            BaseFragment fragment = new HomeFragment();
            getSupportFragmentManager()
                    .beginTransaction()
                    .add(getContextViewId(), fragment, fragment.getClass().getSimpleName())
                    .addToBackStack(fragment.getClass().getSimpleName())

                    .commit();
        }

        //保存导航栏页的Activity到全局类，以备后用
        mGlobal.mNavigationBarActivity = NavigationBarActivity.this;

        //动态获取权限
        mFunction.setPermissions(NavigationBarActivity.this);

        //初始化屏幕的宽高
        mGlobal.mScreenWidth = RxDeviceTool.getScreenWidth(mGlobal.mNavigationBarActivity);
        mGlobal.mScreenHeight = mDeviceUtil.getFullActivityHeight(mGlobal.mNavigationBarActivity);



        //打开无障碍服务
        mFunction.openAccessibilityService();

        mFloatWindow.showFloatWindow();
    }






}
