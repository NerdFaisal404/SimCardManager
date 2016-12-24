package com.kodeeo.simcardmgr.fragments;

import java.util.Locale;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.telephony.ServiceState;
import android.telephony.TelephonyManager;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.kodeeo.simcardmgr.AboutActivity;
import com.kodeeo.simcardmgr.R;

public class SimInfoFragment extends BaseFragment {
	TextView txtPhoneType;
	TextView txtPhoneNumber;
	TextView txtSerialNumber;
	TextView txtSIMIMSI;
	TextView txtCountryName;
	TextView txtOpName;
	TextView txtNetworkType;
	TextView txtNetworkRoaming;
	TextView txtNetworkOperator;
	TextView txtNetworkState;
	TextView txtNetowrkSelectionMode;
	TextView txtVoicemailNumber;
	TextView txtDeviceId;
	TextView txtDeviceSoftwareVer;
	AdView adView;

	private BroadcastReceiver receiver;

	private String[] mNetworkType = new String[] { "Unknown", "GPRS", "EDGE",
			"UMTS", "CDMA", "EVDO revision 0", "EVDO revision A", "1xRTT",
			"HSDPA", "HSUPA", "HSPA", "iDen", "EVDO revision B", "LTE",
			"eHRPD", "HSPA+" };
	private String[] mNetworkState = new String[] { "In service",
			"Out of services", "Emergency numbers only", "Power off" };

	@Override
	public void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setHasOptionsMenu(true);

		receiver = new BroadcastReceiver() {
			@Override
			public void onReceive(Context context, Intent intent) {
				int state = intent.getExtras().getInt("state");
				txtNetworkState.setText(mNetworkState[state]);
			}
		};
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		View root = inflater.inflate(R.layout.info_fragment, container, false);
		initViews(root);
		fillData();
		return root;
	}

	private void initViews(View root) {
		txtPhoneType = (TextView) root.findViewById(R.id.txtPhoneType);
		txtPhoneType.setOnClickListener(onResultClick);

		txtPhoneNumber = (TextView) root.findViewById(R.id.txtPhoneNum);
		txtPhoneNumber.setOnClickListener(onResultClick);

		txtSerialNumber = (TextView) root.findViewById(R.id.txtSimNum);
		txtSerialNumber.setOnClickListener(onResultClick);

		txtSIMIMSI = (TextView) root.findViewById(R.id.txtSimIMSI);
		txtSIMIMSI.setOnClickListener(onResultClick);

		txtCountryName = (TextView) root.findViewById(R.id.txtSimCountry);
		txtCountryName.setOnClickListener(onResultClick);

		txtOpName = (TextView) root.findViewById(R.id.txtOpName);
		txtOpName.setOnClickListener(onResultClick);

		txtNetworkType = (TextView) root.findViewById(R.id.txtNetworkType);
		txtNetworkType.setOnClickListener(onResultClick);

		txtNetworkRoaming = (TextView) root
				.findViewById(R.id.txtNetworkRoaming);
		txtNetworkRoaming.setOnClickListener(onResultClick);

		txtNetworkOperator = (TextView) root
				.findViewById(R.id.txtNetworkOperator);
		txtNetworkOperator.setOnClickListener(onResultClick);

		txtNetworkState = (TextView) root.findViewById(R.id.txtNetworkState);
		txtNetworkState.setOnClickListener(onResultClick);

		txtNetowrkSelectionMode = (TextView) root
				.findViewById(R.id.txtNetworkSelectionMode);
		txtNetowrkSelectionMode.setOnClickListener(onResultClick);

		txtVoicemailNumber = (TextView) root
				.findViewById(R.id.txtVoicemailNumber);
		txtVoicemailNumber.setOnClickListener(onResultClick);

		txtDeviceId = (TextView) root.findViewById(R.id.txtDeviceId);
		txtDeviceId.setOnClickListener(onResultClick);

		txtDeviceSoftwareVer = (TextView) root
				.findViewById(R.id.txtDeviceSoftwareVer);
		txtDeviceSoftwareVer.setOnClickListener(onResultClick);

		adView = (AdView) root.findViewById(R.id.adView);
		AdRequest adRequest = new AdRequest.Builder().build();
		adView.loadAd(adRequest);
	}

	private void fillData() {
		TelephonyManager telephonyManager = (TelephonyManager) getActivity()
				.getSystemService(Context.TELEPHONY_SERVICE);
		String temp = "";
		int itemp = 0;
		itemp = telephonyManager.getPhoneType();

		// Set phone type
		if (itemp == TelephonyManager.PHONE_TYPE_NONE) {
			temp = "Unknown";
		} else if (itemp == TelephonyManager.PHONE_TYPE_GSM) {
			temp = "GSM";
		} else if (itemp == TelephonyManager.PHONE_TYPE_CDMA) {
			temp = "CDMA";
		} else if (itemp == 3) {
			// TelephonyManager.PHONE_TYPE_CDMA in API11
			temp = "SIP";
		}
		txtPhoneType.setText(temp);

		// Set phone number
		temp = telephonyManager.getLine1Number();
		if (temp == null || temp.equals("")) {
			temp = "Unknown";
		}
		txtPhoneNumber.setText(temp);

		// Set SIM serial number
		temp = telephonyManager.getSimSerialNumber();
		if (temp == null) {
			temp = "Unavailable";
		}
		txtSerialNumber.setText(temp);

		// Set SIM Operator name
		temp = telephonyManager.getSimOperatorName();
		if (temp == null || temp.equals("")) {
			temp = telephonyManager.getNetworkOperatorName();
		}
		txtOpName.setText(temp);

		// Set Country name
		temp = telephonyManager.getSimCountryIso();
		Locale loc = new Locale("", temp);
		txtCountryName.setText(loc.getDisplayName());

		// Set SIM IMSI
		temp = telephonyManager.getSubscriberId();
		txtSIMIMSI.setText(temp);

		// Set network type
		itemp = telephonyManager.getNetworkType();
		txtNetworkType.setText(mNetworkType[itemp]);

		// Set network roaming state
		if (telephonyManager.isNetworkRoaming()) {
			txtNetworkRoaming.setText("Yes");
		} else {
			txtNetworkRoaming.setText("No");
		}

		// Set network operator name
		temp = telephonyManager.getNetworkOperatorName();
		txtNetworkOperator.setText(temp);

		// Set network selection mode
		boolean bool = new ServiceState().getIsManualSelection();
		if (bool) {
			txtNetowrkSelectionMode.setText("Manual");
		} else {
			txtNetowrkSelectionMode.setText("Automatic");
		}

		// Set voice mail number
		temp = telephonyManager.getVoiceMailNumber();
		if (temp == null) {
			temp = "Unavailable";
		}
		txtVoicemailNumber.setText(temp);

		// Set device id
		temp = telephonyManager.getDeviceId();
		if (temp == null) {
			temp = "Unavailable";
		}
		txtDeviceId.setText(temp);

		// Set device software version
		temp = telephonyManager.getDeviceSoftwareVersion();
		if (temp == null) {
			temp = "Unavailable";
		}
		txtDeviceSoftwareVer.setText(temp);

		IntentFilter filter = new IntentFilter();
		filter.addAction("android.intent.action.SERVICE_STATE");
		getActivity().registerReceiver(receiver, filter);
	}

	View.OnClickListener onResultClick = new View.OnClickListener() {

		@SuppressWarnings("deprecation")
		@Override
		public void onClick(View v) {
			// TODO Auto-generated method stub
			String extractedText = ((TextView) v).getText().toString();
			int sdk = android.os.Build.VERSION.SDK_INT;
			if (sdk < 11) {
				android.text.ClipboardManager clipboard = (android.text.ClipboardManager) getActivity()
						.getSystemService(Context.CLIPBOARD_SERVICE);
				clipboard.setText(extractedText);
			} else {
				android.content.ClipboardManager clipboard = (android.content.ClipboardManager) getActivity()
						.getSystemService(Context.CLIPBOARD_SERVICE);
				android.content.ClipData clip = android.content.ClipData
						.newPlainText("text label", extractedText);
				clipboard.setPrimaryClip(clip);
			}

			Toast.makeText(getActivity(),
					"'" + extractedText + "' copied to clipboard",
					Toast.LENGTH_SHORT).show();
		}
	};

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// TODO Auto-generated method stub
		switch (item.getItemId()) {
		case R.id.action_about:
			// Toast.makeText(this, R.string.strNotImplemented,
			// Toast.LENGTH_LONG).show();
			Intent iAbout = new Intent(getActivity(), AboutActivity.class);
			startActivity(iAbout);
			break;

		default:
			break;
		}
		return true;
	}

	@Override
	public void onDestroy() {
		// TODO Auto-generated method stub
		getActivity().unregisterReceiver(receiver);
		super.onDestroy();
	}

}
