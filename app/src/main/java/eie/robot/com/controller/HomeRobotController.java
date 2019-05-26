package eie.robot.com.controller;

import android.content.Context;
import android.view.View;
import android.widget.Toast;

import com.github.clans.fab.FloatingActionButton;
import com.github.clans.fab.FloatingActionMenu;
import com.tamic.novate.Throwable;
import com.tamic.novate.callback.ResponseCallback;
import com.vondear.rxtool.view.RxToast;

import butterknife.BindView;
import eie.robot.com.R;
import eie.robot.com.common.mCommonTask;
import eie.robot.com.common.mDataUtil;
import eie.robot.com.common.mDeviceUtil;
import eie.robot.com.common.mGlobal;
import okhttp3.Call;
import okhttp3.ResponseBody;

public class HomeRobotController extends BaseController {

    private Context mContext;

    //机器人界面，悬浮菜单按钮
    @BindView(R.id.menus_robot)
    FloatingActionMenu btnRobotFloatingActionMenu;

    //机器人界面，悬浮菜单里的【开始赚钱】按钮
    @BindView(R.id.btn_start_money)
    FloatingActionButton btnStartMoney;



    public HomeRobotController(Context context, int ViewId) {
        super(context, ViewId);
        mContext = context;

        //绑定事件
        initEvent();
    }
    //绑定事件
    private void initEvent(){

        //点击【开始赚钱】按钮
        this.btnStartMoney.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                //关闭悬浮按钮
                btnRobotFloatingActionMenu.close(true);

                //开启任务
                mCommonTask.StartTask();
            }
        });
    }


    private void Test(){
        mDataUtil.postIncomeRecord(null, new ResponseCallback<Object, ResponseBody>() {
            @Override
            public Object onHandleResponse(ResponseBody response) throws Exception {
                return null;
            }

            @Override
            public void onError(Object tag, Throwable e) {

            }

            @Override
            public void onCancel(Object tag, Throwable e) {

            }

            @Override
            public void onNext(Object tag, Call call, Object response) {

            }
        });
    }



    @Override
    protected String getTitle() {
        return "网赚机器人";
    }




}
