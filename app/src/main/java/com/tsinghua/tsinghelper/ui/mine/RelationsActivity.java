package com.tsinghua.tsinghelper.ui.mine;

import android.content.Context;
import android.os.Bundle;

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

import java.util.ArrayList;
import java.util.HashMap;

import butterknife.BindView;
import butterknife.ButterKnife;

public class RelationsActivity extends AppCompatActivity {
    @BindView(R.id.toolbar)
    Toolbar mToolbar;
    @BindView(R.id.tab_layout)
    TabLayout mTabLayout;
    @BindView(R.id.view_pager)
    ViewPager mViewPager;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_relations);
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
        RelationsActivity.Adapter adapter =
                new RelationsActivity.Adapter(getSupportFragmentManager(), this);
        mViewPager.setAdapter(adapter);
        mTabLayout.setupWithViewPager(mViewPager);
    }

    public static class Adapter extends FragmentPagerAdapter {

        private final int TAB_CNT = 2;
        private final String[] TITLE = {"我关注的", "关注我的"};
        private final String[] TYPES = {"followings", "followers"};
        private final ArrayList<Fragment> mFragments = new ArrayList<>();
        private Context mContext;

        Adapter(FragmentManager fm, Context cxt) {
            super(fm, BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT);
            mContext = cxt;

            for (int i = 0; i < TAB_CNT; i++) {
                HashMap<String, String> params = new HashMap<>();
                params.put("relation", TYPES[i]);
                mFragments.add(new UserListFragment(params, mContext));
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
