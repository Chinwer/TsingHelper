package com.tsinghua.tsinghelper.ui.mine;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.viewpager.widget.ViewPager;

import com.google.android.material.tabs.TabLayout;
import com.tsinghua.tsinghelper.R;
import com.tsinghua.tsinghelper.ui.task.NewTaskTypeActivity;
import com.tsinghua.tsinghelper.ui.task.TaskListFragment;
import com.tsinghua.tsinghelper.util.HttpUtil;

import java.util.ArrayList;
import java.util.HashMap;

import butterknife.BindView;
import butterknife.ButterKnife;

public class PublishedTasksActivity extends AppCompatActivity {

    @BindView(R.id.toolbar)
    Toolbar mToolbar;
    @BindView(R.id.tab_layout)
    TabLayout mTabLayout;
    @BindView(R.id.pager_tasks)
    ViewPager mViewPager;
    @BindView(R.id.btn_publish)
    TextView mPublish;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_published_tasks);
        ButterKnife.bind(this);

        initToolbar();
        initTabLayout();
    }

    public void initToolbar() {
        setSupportActionBar(mToolbar);
        ActionBar actionBar = getSupportActionBar();
        assert actionBar != null;
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setDisplayShowTitleEnabled(false);
    }

    public void initTabLayout() {
        Adapter adapter = new Adapter(getSupportFragmentManager(), this);
        mViewPager.setAdapter(adapter);
        mTabLayout.setupWithViewPager(mViewPager);

        Intent it = getIntent();
        mTabLayout.getTabAt(it.getIntExtra("pos", 0)).select();
    }

    public void publish(View view) {
        Intent it = new Intent(PublishedTasksActivity.this, NewTaskTypeActivity.class);
        startActivity(it);
        finish();
    }

    public static class Adapter extends FragmentPagerAdapter {

        private final int TAB_CNT = 3;
        private final String[] TYPES = {
                "", "doing", "done"
        };
        private final String[] TITLE = {
                "全部任务", "进行中", "已完成"
        };
        private final ArrayList<Fragment> mFragments = new ArrayList<>();
        private Context mContext;

        Adapter(FragmentManager fm, Context cxt) {
            super(fm, BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT);
            mContext = cxt;


            for (int i = 0; i < TAB_CNT; i++) {
                HashMap<String, String> params = new HashMap<>();
                params.put("type", TYPES[i]);
                mFragments.add(new TaskListFragment(params, HttpUtil.TASK_GET_MINE, mContext));
            }
        }

        @Override
        public int getCount() {
            return TAB_CNT;
        }

        @Nullable
        @Override
        public CharSequence getPageTitle(int position) {
            return TITLE[position];
        }

        @NonNull
        @Override
        public Fragment getItem(int position) {
            return mFragments.get(position);
        }
    }
}
