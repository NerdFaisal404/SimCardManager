package com.kodeeo.simcardmgr;

import java.util.ArrayList;

import com.kodeeo.simcardmgr.fragments.SimContactsFragment;
import com.kodeeo.simcardmgr.models.RowContentHolder;

import android.os.Handler;
import android.os.Message;
import android.widget.Toast;

public class MessageHandler extends Handler {
	public static final int DELETE_ALL_COMPLETED = 1;
	public static final int FILTER_COMPLETED = 2;
	public static final int EXPORT_FAILED_UNKNOWN = 3;
	public static final int LOAD_CONTACT_DONE = 4;
	public static final int EXPORT_COMPLETE = 5;
	public static final int IMPORT_FULL_OR_READONLY = 6;
	public static final int IMPORT_COMPLETE = 7;
	public static final int IMPORT_FAILED = 8;

	SimContactsFragment fragment;

	public MessageHandler(SimContactsFragment frg) {
		fragment = frg;
	}

	@SuppressWarnings("unchecked")
	@Override
	public void handleMessage(Message msg) {
		// TODO Auto-generated method stub
		super.handleMessage(msg);
		switch (msg.what) {
		case DELETE_ALL_COMPLETED:
			int totalDeleted = msg.arg1;
			fragment.doAfterDeleteAll(totalDeleted);
			break;
		case FILTER_COMPLETED:
			fragment.setTotalView(msg.arg1);
			break;

		case EXPORT_COMPLETE:
			Toast.makeText(fragment.getActivity(), (String) msg.obj,
					Toast.LENGTH_LONG).show();

			break;
		case EXPORT_FAILED_UNKNOWN:
			Toast.makeText(fragment.getActivity(),
					"Export failed, unknown reason.", Toast.LENGTH_LONG).show();
			break;

		case IMPORT_FULL_OR_READONLY:
			Toast.makeText(
					fragment.getActivity(),
					"SIM memory is full or readonly. " + msg.arg1 + "/"
							+ msg.arg2 + " contacts imported.",
					Toast.LENGTH_LONG).show();
			break;
		case IMPORT_COMPLETE:
			fragment.reloadContactListView();
			Toast.makeText(
					fragment.getActivity(),
					"Import contacts complete. " + msg.arg1 + "/" + msg.arg2
							+ " contacts imported.", Toast.LENGTH_LONG).show();
			break;
		case IMPORT_FAILED:
			Toast.makeText(fragment.getActivity(),
					"Import contacts failed: vCard file corrupted",
					Toast.LENGTH_LONG).show();
			break;

		case LOAD_CONTACT_DONE:
			fragment.reloadContactListView((ArrayList<RowContentHolder>) msg.obj);
			break;
		default:
			break;
		}
	}
}
