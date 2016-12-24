package com.kodeeo.simcardmgr;

import android.os.Bundle;
import android.support.v4.app.FragmentActivity;

import com.kodeeo.simcardmgr.fragments.AboutFragment;

public class AboutActivity extends FragmentActivity {
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_about);

		getSupportFragmentManager().beginTransaction()
				.add(R.id.container, new AboutFragment()).commit();
	}

}
