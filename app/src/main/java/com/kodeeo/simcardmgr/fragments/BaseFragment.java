package com.kodeeo.simcardmgr.fragments;

import android.support.v4.app.Fragment;

public class BaseFragment extends Fragment {
	protected String mTitle;

	public String getTitle() {
		return mTitle;
	}

	public void setTitle(String mTitle) {
		this.mTitle = mTitle;
	}

}
