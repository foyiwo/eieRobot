package eie.robot.com.controller;

import android.content.Context;

import butterknife.BindView;

public class HomeMineController extends BaseController {

    private Context mContext;
    public HomeMineController(Context context, int ViewId) {
        super(context, ViewId);
        mContext = context;
    }


    @Override
    protected String getTitle() {
        return "我的信息";
    }
}
