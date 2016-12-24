package com.kodeeo.simcardmgr.workerthreads;

import java.util.ArrayList;

import com.kodeeo.simcardmgr.MessageHandler;
import com.kodeeo.simcardmgr.SimUtil;
import com.kodeeo.simcardmgr.models.RowContentHolder;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;

public class LoadContactsAsyncTask extends AsyncTask<Void, Void, Boolean> {
	Context mContext;
	ProgressDialog mDialog;
	MessageHandler mHandler;
	ArrayList<RowContentHolder> contacts;

	public LoadContactsAsyncTask(Context context, MessageHandler handler) {
		mContext = context;
		mHandler = handler;
	}

	@Override
	protected void onPreExecute() {
		// TODO Auto-generated method stub
		super.onPreExecute();
		mDialog = new ProgressDialog(mContext);
		mDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
		mDialog.setIndeterminate(true);
		mDialog.setCancelable(false);
		mDialog.setMessage("Loading contacts...");
		mDialog.show();
	}

	@Override
	protected Boolean doInBackground(Void... params) {
		// TODO Auto-generated method stub
		contacts = new ArrayList<RowContentHolder>();
		SimUtil util = new SimUtil(mContext.getContentResolver());

		contacts = util.loadContacts();
		return true;
	}

	@Override
	protected void onPostExecute(Boolean result) {
		// TODO Auto-generated method stub
		super.onPostExecute(result);

		mHandler.obtainMessage(MessageHandler.LOAD_CONTACT_DONE, contacts)
				.sendToTarget();

		try {
			mDialog.dismiss();
		} catch (Exception ex) {
			
		}
	}
}
