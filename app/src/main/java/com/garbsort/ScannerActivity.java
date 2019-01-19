package com.garbsort;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.garbsort.garbsort.R;

import java.util.ArrayList;
import java.util.List;

public class ScannerActivity extends AppCompatActivity {
    private ViewPager viewPager;
    private FragmentPagerAdapter pagerAdapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scanner);

        viewPager = findViewById(R.id.vp_frag);
        pagerAdapter = new FragmentPagerAdapter(getSupportFragmentManager());
        viewPager.setAdapter(pagerAdapter);
    }

    /**
     * The class for fragment pager adapter
     */
    public class FragmentPagerAdapter extends FragmentStatePagerAdapter {
        /**
         * The list of fragments
         */
        private List<Fragment> fragments = new ArrayList<>();

        /**
         * The fragment pager adapter
         *
         * @param fm the fragment manager
         */
        public FragmentPagerAdapter(FragmentManager fm) {
            super(fm);
            fragments.add(ScannerFragment.newInstance());
        }

        @Override
        public Fragment getItem(int position) {
            return fragments.get(position);
        }

        @Override
        public int getCount() {
            return fragments.size();
        }
    }
}
