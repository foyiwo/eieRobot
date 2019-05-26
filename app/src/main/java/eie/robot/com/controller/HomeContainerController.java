package eie.robot.com.controller;

import android.content.Context;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import com.vondear.rxfeature.activity.ActivityCodeTool;
import com.vondear.rxtool.RxImageTool;
import com.vondear.rxtool.RxRecyclerViewDividerTool;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import eie.robot.com.R;
import eie.robot.com.adapter.AdapterRecyclerViewMain;
import eie.robot.com.model.ModelMainItem;

public class HomeContainerController extends BaseController {

    private Context mContext;
    @BindView(R.id.recyclerview)
    RecyclerView recyclerview;

    private List<ModelMainItem> mData;
    private int mColumnCount = 3;
    private long mBackPressed;
    public HomeContainerController(Context context, int ViewId) {
        super(context, ViewId);
        mContext = context;
        initData();
        initView();
    }

    @Override
    protected String getTitle() {
        return "集装箱";
    }

    private void initData() {
        mData = new ArrayList<>();
        mData.add(new ModelMainItem("二维码与条形码的扫描与生成", R.drawable.circle_dynamic_generation_code, ActivityCodeTool.class));

    }

    private void initView() {
        if (mColumnCount <= 1) {
            recyclerview.setLayoutManager(new LinearLayoutManager(mContext));
        } else {
            recyclerview.setLayoutManager(new GridLayoutManager(mContext, mColumnCount));
        }

        recyclerview.addItemDecoration(new RxRecyclerViewDividerTool(RxImageTool.dp2px(5f)));
        AdapterRecyclerViewMain recyclerViewMain = new AdapterRecyclerViewMain(mData);

        recyclerview.setAdapter(recyclerViewMain);
    }


}
