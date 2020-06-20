package com.example.ai_smile.widget;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentStatePagerAdapter;
import com.example.ai_smile.activity.ImageFragment;
import java.util.List;

/**
 * ImageScrollActivity 图片引用 Adapter
 */
public class PageAdapter extends FragmentStatePagerAdapter {

    List<String> mDatas;

    public PageAdapter(FragmentManager fm, List data) {
        super(fm);
        mDatas = data;
    }

    @Override
    public Fragment getItem(int position) {
        String url = mDatas.get(position);
        Fragment fragment = ImageFragment.newInstance(url);
        return fragment;
    }

    @Override
    public int getCount() {
        return mDatas.size();
    }

}
