package com.kodeeo.simcardmgr.workerthreads;

import java.io.FileOutputStream;
import java.io.OutputStreamWriter;
import java.util.ArrayList;

import a_vcard.android.provider.Contacts;
import a_vcard.android.syncml.pim.vcard.ContactStruct;
import a_vcard.android.syncml.pim.vcard.VCardComposer;
import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Message;

import com.kodeeo.simcardmgr.MessageHandler;
import com.kodeeo.simcardmgr.R;
import com.kodeeo.simcardmgr.models.RowContentHolder;

public class ExportAsyncTask extends
		AsyncTask<ArrayList<RowContentHolder>, String, Boolean> {

	Context mContext;
	ProgressDialog mDialog;
	MessageHandler mHandler;
	ArrayList<RowContentHolder> mList;
	String mFileAbsolutePath;

	public ExportAsyncTask(Context context, MessageHandler handler,
			ArrayList<RowContentHolder> list, String path) {
		mContext = context;
		mHandler = handler;
		mList = list;
		mFileAbsolutePath = path;
	}

	@Override
	protected void onPostExecute(Boolean result) {
		// TODO Auto-generated method stub
		super.onPostExecute(result);
		try {
			mDialog.dismiss();
		} catch (Exception ex) {
			
		}
	}

	@Override
	protected void onPreExecute() {
		// TODO Auto-generated method stub
		super.onPreExecute();
		mDialog = new ProgressDialog(mContext);
		mDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
		mDialog.setIndeterminate(false);
		mDialog.setCancelable(false);
		mDialog.setTitle(R.string.strExportWaiting);
		mDialog.setMax(mList.size());
		mDialog.setMessage("Processing...");
		mDialog.show();
	}

	@Override
	protected Boolean doInBackground(ArrayList<RowContentHolder>... params) {
		// TODO Auto-generated method stub

		OutputStreamWriter writer;
		try {
			writer = new OutputStreamWriter(new FileOutputStream(
					mFileAbsolutePath), "UTF-8");
			VCardComposer composer = new VCardComposer();

			int count = mList.size();
			for (int i = 0; i < count; i++) {

				ContactStruct contactStruct = new ContactStruct();
				RowContentHolder contact = mList.get(i);
				contactStruct.name = contact.getName();
				contactStruct.addPhone(Contacts.Phones.TYPE_MOBILE,
						contact.getNumber(), null, true);

				publishProgress(contactStruct.name);

				// create vCard representation
				String vcardString = composer.createVCard(contactStruct,
						VCardComposer.VERSION_VCARD30_INT);

				// write vCard to the output stream
				writer.write(vcardString);
				writer.write("\n"); // add empty lines between contacts
				Thread.sleep(50);
			}

			writer.close();
			Message msg = mHandler.obtainMessage(
					MessageHandler.EXPORT_COMPLETE, "Exported " + mList.size()
							+ " contact(s) to " + mFileAbsolutePath);
			msg.sendToTarget();
			return true;

		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			mHandler.sendEmptyMessage(MessageHandler.EXPORT_FAILED_UNKNOWN);
			return false;
		}

	}

	@Override
	protected void onProgressUpdate(String... values) {
		// TODO Auto-generated method stub
		super.onProgressUpdate(values);
		mDialog.setMessage(values[0]);
		// mDialog.setProgress(Integer.parseInt(values[1]));
		mDialog.incrementProgressBy(1);
		mDialog.show();
	}

}
