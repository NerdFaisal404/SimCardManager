package com.kodeeo.simcardmgr.fragments;

import android.app.AlertDialog;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager.NameNotFoundException;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.kodeeo.simcardmgr.R;

public class AboutFragment extends Fragment {
	private TextView txtAppVersion;
	private Button btnGoBack;
	private Button btnEmail;
	private Button btnRate;
	private Button btnShare;
	private AdView adView;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View root = inflater.inflate(R.layout.fragment_about, container, false);
		initViews(root);
		fillInfo();
		return root;
	}

	private void initViews(View root) {
		txtAppVersion = (TextView) root.findViewById(R.id.txtVersion);

		// Setup button goback
		btnGoBack = (Button) root.findViewById(R.id.btnGoBack);
		btnGoBack.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				getActivity().finish();
			}
		});

		btnRate = (Button) root.findViewById(R.id.btnRate);
		btnRate.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub

				AlertDialog.Builder alert = new AlertDialog.Builder(
						getActivity());
				alert.setTitle(R.string.strOpenPSConfirm_title)
						.setMessage(R.string.strOpenPSConfirm)
						.setPositiveButton(R.string.strOk, null)
						.setNegativeButton(R.string.strCancel, null);

				final AlertDialog dialog = alert.create();
				dialog.show();

				Button btnOk = dialog.getButton(AlertDialog.BUTTON_POSITIVE);
				btnOk.setOnClickListener(new View.OnClickListener() {

					@Override
					public void onClick(View v) {
						String packageName = "";
						PackageInfo pInfo;
						try {
							pInfo = getActivity().getPackageManager()
									.getPackageInfo(
											getActivity().getPackageName(), 0);
							packageName = pInfo.packageName;
						} catch (NameNotFoundException e) {
							e.printStackTrace();
						}

						if (packageName != null && !packageName.equals("")) {
							Intent intent = new Intent(Intent.ACTION_VIEW);
							intent.setData(Uri.parse("market://details?id="
									+ packageName));
							startActivity(intent);
						}
						dialog.dismiss();
					}
				});
			}
		});

		btnShare = (Button) root.findViewById(R.id.btnShare);
		btnShare.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				String packageName = "";
				PackageInfo pInfo;
				try {
					pInfo = getActivity().getPackageManager().getPackageInfo(
							getActivity().getPackageName(), 0);
					packageName = pInfo.packageName;
				} catch (NameNotFoundException e) {
					e.printStackTrace();
				}

				if (packageName != null && !packageName.equals("")) {
					Intent intent = new Intent(
							android.content.Intent.ACTION_SEND);
					intent.setType("text/plain");
					intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_WHEN_TASK_RESET);

					// Add data to the intent, the receiving app will decide
					// what to
					// do with it.
					intent.putExtra(Intent.EXTRA_SUBJECT, "SIM card manager");
					intent.putExtra(
							Intent.EXTRA_TEXT,
							"https://play.google.com/store/apps/details?id="
									+ packageName
									+ "\n");

					startActivity(Intent.createChooser(intent,
							"How do you want to share?"));
				}
			}
		});

		adView = (AdView) root.findViewById(R.id.adView);
		AdRequest adRequest = new AdRequest.Builder().build();
		adView.loadAd(adRequest);

	}

	private void fillInfo() {
		String versionName = "";
		try {
			PackageInfo pInfo = getActivity().getPackageManager()
					.getPackageInfo(
							getActivity().getPackageName(), 0);
			versionName = pInfo.versionName;
		} catch (NameNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		txtAppVersion.setText("Version " + versionName);
	}
}
