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

package eie.robot.com.controller;

import android.content.Context;
import android.os.Parcelable;
import android.util.SparseArray;
import android.view.LayoutInflater;

import com.qmuiteam.qmui.util.QMUIViewHelper;
import com.qmuiteam.qmui.widget.QMUITopBarLayout;
import com.qmuiteam.qmui.widget.QMUIWindowInsetLayout;

import butterknife.BindView;
import butterknife.ButterKnife;
import eie.robot.com.R;
import eie.robot.com.fragment.BaseFragment;

/**
 * @author cginechen
 * @date 2016-10-20
 */

public abstract class BaseController extends QMUIWindowInsetLayout {

    @BindView(R.id.TopBar) QMUITopBarLayout mTopBar;

    private BaseControlListener mBaseControlListener;
    private int mDiffRecyclerViewSaveStateId = QMUIViewHelper.generateViewId();

    public BaseController(Context context, int ViewId) {
        super(context);
        LayoutInflater.from(context).inflate(ViewId, this);
        ButterKnife.bind(this);
        initTopBar();
    }

    protected void startFragment(BaseFragment fragment) {
        if (mBaseControlListener != null) {
            mBaseControlListener.startFragment(fragment);
        }
    }

    public void setControlListener(BaseControlListener baseControlListener) {
        mBaseControlListener = baseControlListener;
    }

    protected abstract String getTitle();

    private void initTopBar() {
        mTopBar.setTitle(getTitle());
    }


    public interface BaseControlListener {
        void startFragment(BaseFragment fragment);
    }

    @Override
    protected void dispatchSaveInstanceState(SparseArray<Parcelable> container) {
        super.dispatchSaveInstanceState(container);
    }

    @Override
    protected void dispatchRestoreInstanceState(SparseArray<Parcelable> container) {
        super.dispatchRestoreInstanceState(container);

    }


}
