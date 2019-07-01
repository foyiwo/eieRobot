package eie.robot.com.controller;

import android.content.Context;
import android.view.View;

import com.github.clans.fab.FloatingActionButton;
import com.github.clans.fab.FloatingActionMenu;

import butterknife.BindView;
import eie.robot.com.R;
import eie.robot.com.common.mCommonTask;

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




    @Override
    protected String getTitle() {
        return "网赚机器人";
    }




}
