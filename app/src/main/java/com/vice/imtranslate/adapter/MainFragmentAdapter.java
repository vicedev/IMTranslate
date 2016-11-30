package com.vice.imtranslate.adapter;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import java.util.List;

/**
 * Created by vice on 2016/9/8.
 */
public class MainFragmentAdapter extends FragmentPagerAdapter {
    private List<Fragment> pageList;
    public MainFragmentAdapter(FragmentManager fm, List<Fragment> pageList) {
        super(fm);
        this.pageList=pageList;
    }

    @Override
    public Fragment getItem(int position) {
        return pageList.get(position);
    }

    @Override
    public int getCount() {
        return pageList.size();
    }
}
