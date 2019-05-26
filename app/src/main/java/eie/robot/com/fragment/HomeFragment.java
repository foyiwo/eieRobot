/*
 * Tencent is pleased to support the open source community by making QMUI_Android available.
 *
 * Copyright (C) 2017-2018 THL A29 Limited, a Tencent company. All rights reserved.
 *
 * Licensed under the MIT License (the "License"); you may not use this file except in
 * compliance with the License. You may obtain a copy of the License at
 *
 * http://opensource.org/licenses/MIT
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License is
 * distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND,
 * either express or implied. See the License for the specific language governing permissions and
 * limitations under the License.
 */

package eie.robot.com.fragment;

import android.graphics.drawable.Drawable;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.PagerAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.qmuiteam.qmui.util.QMUIDisplayHelper;
import com.qmuiteam.qmui.widget.QMUITabSegment;
import com.qmuiteam.qmui.widget.QMUIViewPager;

import java.util.HashMap;

import butterknife.BindView;
import butterknife.ButterKnife;
import eie.robot.com.R;
import eie.robot.com.common.mGlobal;
import eie.robot.com.controller.BaseController;
import eie.robot.com.controller.HomeContainerController;
import eie.robot.com.controller.HomeMineController;
import eie.robot.com.controller.HomeRobotController;


/**
 * @author cginechen
 * @date 2016-10-19
 */
public class HomeFragment extends BaseFragment {
    private final static String TAG = HomeFragment.class.getSimpleName();

    @BindView(R.id.mControllerViewPager)
    QMUIViewPager mControllerViewPager;
    @BindView(R.id.mNavigationBar)
    QMUITabSegment mNavigationBars;

    //测量作用，隐藏状态
    @BindView(R.id.viewMeasureScreenHeight)
    TextView viewMeasureScreenHeight;

    private HashMap<Pager, BaseController> mPages;
    private PagerAdapter mPagerAdapter = new PagerAdapter() {

        private int mChildCount = 0;
        @Override
        public boolean isViewFromObject(View view, Object object) {
            return view == object;
        }

        @Override
        public int getCount() {
            return mPages.size();
        }

        @Override
        public Object instantiateItem(final ViewGroup container, int position) {
            BaseController page = mPages.get(Pager.getPagerFromPositon(position));
            ViewGroup.LayoutParams params = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
            container.addView(page, params);
            return page;
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            container.removeView((View) object);
        }

        @Override
        public int getItemPosition(Object object) {
            if (mChildCount == 0) {
                return POSITION_NONE;
            }
            return super.getItemPosition(object);
        }

        @Override
        public void notifyDataSetChanged() {
            mChildCount = getCount();
            super.notifyDataSetChanged();
        }
    };

    @Override
    protected View onCreateView() {
        FrameLayout layout = (FrameLayout) LayoutInflater.from(getActivity()).inflate(R.layout.fragment_home, null);
        ButterKnife.bind(this, layout);
        initTabs();
        initPagers();
        mGlobal.mNavigationBars = mNavigationBars;
        mGlobal.viewMeasureScreenHeight = viewMeasureScreenHeight;
        mGlobal.mNavigationBars.selectTab(1);

        return layout;
    }

    //初始化底部列表
    private void initTabs(){
        //地图界面
        QMUITabSegment.Tab robot = new QMUITabSegment.Tab(
                getTabIconDrawable(R.mipmap.icon_navigationbar_robot),
                getTabIconDrawable(R.mipmap.icon_navigationbar_robot_selected),
                "机器人", false
        );

        //集装箱
        QMUITabSegment.Tab container = new QMUITabSegment.Tab(
                getTabIconDrawable(R.mipmap.icon_navigationbar_list),
                getTabIconDrawable(R.mipmap.icon_navigationbar_list_selected),
                "集装箱", false
        );

        QMUITabSegment.Tab me = new QMUITabSegment.Tab(
                getTabIconDrawable(R.mipmap.icon_tabbar_me),
                getTabIconDrawable(R.mipmap.icon_tabbar_me_select),
                "本体", false
        );
        mNavigationBars.addTab(container);
        mNavigationBars.addTab(robot);
        mNavigationBars.addTab(me);

        mNavigationBars.notifyDataChanged();//刷新

    }
    private Drawable getTabIconDrawable(int id){
        int iconShowSize = QMUIDisplayHelper.dp2px(getContext(), 10);
        Drawable normalDrawable = ContextCompat.getDrawable(getContext(), id);
        normalDrawable.setBounds(0, 0, iconShowSize, iconShowSize);
        return normalDrawable;
    }

    //初始化界面
    private void initPagers() {

        BaseController.BaseControlListener listener = new BaseController.BaseControlListener() {
            @Override
            public void startFragment(BaseFragment fragment) {
                HomeFragment.this.startFragment(fragment);
            }
        };

        mPages = new HashMap<>();

        BaseController homeContainerController = new HomeContainerController(getActivity(),R.layout.controller_container);
        homeContainerController.setControlListener(listener);
        mPages.put(Pager.container, homeContainerController);

        BaseController homeRobotController = new HomeRobotController(getActivity(),R.layout.controller_robot);
        homeRobotController.setControlListener(listener);
        mPages.put(Pager.robot, homeRobotController);


        BaseController homeMineController = new HomeMineController(getActivity(),R.layout.controller_mine);
        homeMineController.setControlListener(listener);
        mPages.put(Pager.mine, homeMineController);

        mControllerViewPager.setAdapter(mPagerAdapter);
        mControllerViewPager.setOffscreenPageLimit(10);
        mControllerViewPager.setSwipeable(false);
        mNavigationBars.setupWithViewPager(mControllerViewPager, false);
    }

    @Override
    protected boolean canDragBack() {
        return true;
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    enum Pager {
        robot,mine,container;
        public static Pager getPagerFromPositon(int position) {
            switch (position) {
                case 0:
                    return container;
                case 1:
                    return robot;
                case 2:
                    return mine;
                default:
                    return robot;
            }
        }
    }


}