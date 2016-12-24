package com.kodeeo.simcardmgr.workerthreads;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import com.kodeeo.simcardmgr.MessageHandler;
import com.kodeeo.simcardmgr.SimUtil;
import com.kodeeo.simcardmgr.models.SimContact;

import a_vcard.android.syncml.pim.PropertyNode;
import a_vcard.android.syncml.pim.VDataBuilder;
import a_vcard.android.syncml.pim.VNode;
import a_vcard.android.syncml.pim.vcard.VCardException;
import a_vcard.android.syncml.pim.vcard.VCardParser;
import android.app.ProgressDialog;
import android.content.Context;
import android.net.Uri;
import android.os.AsyncTask;

public class ImportAsyncTask extends AsyncTask<String, String, Boolean> {

	Context mContext;
	ProgressDialog mDialog;
	MessageHandler mHandler;

	public ImportAsyncTask(Context context, MessageHandler handler) {
		mContext = context;
		mHandler = handler;
	}

	@Override
	protected void onPreExecute() {
		// TODO Auto-generated method stub
		super.onPreExecute();
		mDialog = new ProgressDialog(mContext);
		mDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
		mDialog.setIndeterminate(false);
		mDialog.setCancelable(false);
		mDialog.setTitle("Importing...");
		mDialog.setMessage("Processing...");
		mDialog.show();
	}

	@Override
	protected Boolean doInBackground(String... params) {
		// TODO Auto-generated method stub
		VCardParser parser = new VCardParser();
		VDataBuilder builder = new VDataBuilder();
		SimUtil util = new SimUtil(mContext.getContentResolver());

		String file = params[0];

		// read whole file to string
		BufferedReader reader;
		try {
			reader = new BufferedReader(new InputStreamReader(
					new FileInputStream(file), "UTF-8"));
			String vcardString = "";
			String line;
			while ((line = reader.readLine()) != null) {
				vcardString += line + "\n";
			}
			reader.close();

			// parse the string
			boolean parsed = parser.parse(vcardString, "UTF-8", builder);
			if (!parsed) {
				throw new VCardException("Could not parse vCard file: " + file);
			}

			// get all parsed contacts
			List<VNode> pimContacts = builder.vNodeList;
			mDialog.setMax(pimContacts.size());

			// do something for all the contacts
			int count = 0;
			for (VNode contact : pimContacts) {

				ArrayList<PropertyNode> props = contact.propList;

				// contact name - FN property
				SimContact simContact = new SimContact();
				for (PropertyNode prop : props) {
					if ("FN".equals(prop.propName)) {
						simContact.setName(prop.propValue);
					}
					if ("TEL".equals(prop.propName)) {
						simContact.setNumber(prop.propValue);
						break;
					}
				}
				Uri newContactInserted = util.createContact(simContact);
				publishProgress(simContact.getName());
				if (newContactInserted == null) {
					mHandler.obtainMessage(
							MessageHandler.IMPORT_FULL_OR_READONLY, count,
							pimContacts.size()).sendToTarget();
					break;
				} else {
					count++;
				}

				Thread.sleep(50);
			}
			mHandler.obtainMessage(MessageHandler.IMPORT_COMPLETE, count,
					pimContacts.size()).sendToTarget();
			return true;
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			mHandler.sendEmptyMessage(MessageHandler.IMPORT_FAILED);
		}

		return false;
	}

	@Override
	protected void onProgressUpdate(String... values) {
		// TODO Auto-generated method stub
		super.onProgressUpdate(values);
		mDialog.setMessage(values[0]);
		mDialog.incrementProgressBy(1);
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

}
