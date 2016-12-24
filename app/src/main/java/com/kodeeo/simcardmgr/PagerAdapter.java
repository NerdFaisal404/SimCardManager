package com.kodeeo.simcardmgr;

import java.util.ArrayList;

import com.kodeeo.simcardmgr.fragments.BaseFragment;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

public class PagerAdapter extends FragmentPagerAdapter {

	ArrayList<Fragment> mItems = new ArrayList<Fragment>();

	public PagerAdapter(FragmentManager fm) {
		super(fm);
		// TODO Auto-generated constructor stub
	}

	@Override
	public Fragment getItem(int position) {
		// TODO Auto-generated method stub
		return mItems.get(position);
	}

	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return mItems.size();
	}

	@Override
	public CharSequence getPageTitle(int position) {
		// TODO Auto-generated method stub
		return ((BaseFragment) mItems.get(position)).getTitle();
	}

	public void add(Fragment fragment){
		mItems.add(fragment);
		notifyDataSetChanged();
	}
}
