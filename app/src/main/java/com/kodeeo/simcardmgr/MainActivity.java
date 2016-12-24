package com.kodeeo.simcardmgr;

import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.ViewPager;
import android.view.Menu;

import com.kodeeo.simcardmgr.fragments.SimContactsFragment;
import com.kodeeo.simcardmgr.fragments.SimInfoFragment;
import com.viewpagerindicator.PageIndicator;
import com.viewpagerindicator.TitlePageIndicator;

public class MainActivity extends FragmentActivity {
	public static String KEY_FIRST_OPEN = "first";
	PagerAdapter mAdapter;
	ViewPager mPager;
	PageIndicator mIndicator;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
		super.onCreate(savedInstanceState);

		setContentView(R.layout.activity_main);

		mAdapter = new PagerAdapter(getSupportFragmentManager());

		SimContactsFragment contactsFragment = new SimContactsFragment();
		contactsFragment.setTitle("Contacts");
		mAdapter.add(contactsFragment);

		SimInfoFragment infoFragment = new SimInfoFragment();
		infoFragment.setTitle("SIM Info");
		mAdapter.add(infoFragment);

		mPager = (ViewPager) findViewById(R.id.pager);
		mPager.setAdapter(mAdapter);

		mIndicator = (TitlePageIndicator) findViewById(R.id.indicator);
		mIndicator.setViewPager(mPager);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}
}
