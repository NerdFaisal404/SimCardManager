package com.kodeeo.simcardmgr.workerthreads;

import com.kodeeo.simcardmgr.MessageHandler;

import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Message;

public class DeleteAllAsyncTask extends AsyncTask<Void, Integer, Integer> {
	Context mContext;
	ProgressDialog dialog;
	MessageHandler handler;
	int mTotal = 0;

	public DeleteAllAsyncTask(Context context, MessageHandler handler,
			int contactCount) {
		mContext = context;
		this.handler = handler;
		mTotal = contactCount;
	}

	@Override
	protected void onPostExecute(Integer result) {
		// TODO Auto-generated method stub
		super.onPostExecute(result);
		try {
			dialog.dismiss();
		} catch (Exception ex) {
			
		}
		Message msg = handler.obtainMessage(
				MessageHandler.DELETE_ALL_COMPLETED, result, 0);
		msg.sendToTarget();
	}

	@Override
	protected void onPreExecute() {
		// TODO Auto-generated method stub
		super.onPreExecute();
		dialog = new ProgressDialog(mContext);
		dialog.setIndeterminate(true);
		dialog.setCancelable(false);
		dialog.show();
	}

	@Override
	protected void onProgressUpdate(Integer... values) {
		// TODO Auto-generated method stub
		super.onProgressUpdate(values);
		dialog.setMessage("Deleting " + values[0] + "/" + mTotal
				+ " contacts...");
	}

	@Override
	protected Integer doInBackground(Void... params) {
		// TODO Auto-generated method stub
		Uri uri = Uri.parse("content://icc/adn");
		ContentResolver resolver = mContext.getContentResolver();

		Cursor cursor = resolver.query(uri, null, null, null, null);

		int totalDeleted = 0;

		while (cursor.moveToNext()) {
			String name = cursor.getString(cursor.getColumnIndex("name"));
			String phoneNumber = cursor.getString(cursor
					.getColumnIndex("number"));
			String where = "tag='" + name + "'";
			where += " AND number='" + phoneNumber + "'";
			totalDeleted += resolver.delete(uri, where, null);
			publishProgress(totalDeleted);
		}

		return totalDeleted;
	}

}
