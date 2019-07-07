package eie.robot.com;

import android.app.Application;
import com.qmuiteam.qmui.arch.QMUISwipeBackActivityManager;
import com.tencent.bugly.Bugly;
import com.tencent.tinker.loader.app.TinkerApplication;
import com.tencent.tinker.loader.shareutil.ShareConstants;
import com.vondear.rxtool.RxTool;

import eie.robot.com.common.mGlobal;

public class eieRobotApplication extends TinkerApplication {

    public eieRobotApplication() {
        super(ShareConstants.TINKER_ENABLE_ALL, "eie.robot.com.eieRobotApplicationLike",
                "com.tencent.tinker.loader.TinkerLoader", false);
    }

    @Override
    public void onCreate() {
        super.onCreate();

        RxTool.init(this);
        QMUISwipeBackActivityManager.init(this);

        mGlobal.mApplication = this;

    }
}
