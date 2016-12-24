package com.kodeeo.simcardmgr;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

import com.kodeeo.simcardmgr.models.RowContentHolder;
import com.kodeeo.simcardmgr.models.SimContact;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.database.Cursor;
import android.net.Uri;
import android.util.Log;

public class SimUtil {
	private static final String TAG = SimUtil.class.getSimpleName();

	private ContentResolver resolver;
	private Uri simUri;

	public Integer maxContactNameLength; // Maximum length of contact names may
											// differ from SIM to SIM, will be
											// detected upon load of class

	public SimUtil(ContentResolver resolver) {
		this.resolver = resolver;

		// URI for SIM card is different on Android 1.5 and 1.6
		simUri = Uri.parse("content://icc/adn");
	}

	/**
	 * Detects the maximum length of a contacts name which is accepted by the
	 * SIM card.
	 * 
	 * @return Length of the longest contact name the SIM card accepts
	 */
	public Integer detectMaxContactNameLength() {
		if (Log.isLoggable(TAG, Log.DEBUG))
			Log.d(TAG, "detectMaxContactNameLength()");

		String nameString = "sImSaLabiMXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXX"; // 51
																					// chars
		Integer currentMax = nameString.length();

		// used for test-creation
		Uri createdUri = null;
		SimContact testContact = null;

		// loop from longest to shortest contact name length until a contact was
		// stored successfully
		for (currentMax = nameString.length(); ((createdUri == null) && currentMax > 0); currentMax--) {
			testContact = new SimContact(null, nameString.substring(0,
					currentMax), "74672522246");
			createdUri = createContact(testContact);
		}

		// if not stored successfully
		if ((null == createdUri) || (!createdUri.toString().contains("/adn/0"))) {
			return null;
		}

		// if stored successfully, remove contact again
		deleteContact(testContact);

		return currentMax;
	}

	/**
	 * Retrieves all contacts from the SIM card.
	 * 
	 * @return List containing Contact objects from the stored SIM information
	 */

	public ArrayList<RowContentHolder> loadContacts() {
		ArrayList<RowContentHolder> result = new ArrayList<RowContentHolder>();
		try {
			String m_simPhonename = null;
			String m_simphoneNo = null;

			Uri simUri = Uri.parse("content://icc/adn");
			Cursor cursorSim = resolver.query(simUri, null, null, null, null);

			if (cursorSim.moveToFirst()) {
				do {
					m_simPhonename = cursorSim.getString(cursorSim
							.getColumnIndex("name"));
					m_simphoneNo = cursorSim.getString(cursorSim
							.getColumnIndex("number"));
					String id = cursorSim.getString(cursorSim
							.getColumnIndex("_id"));
					if (m_simPhonename == null) {
						m_simPhonename = "";
					}
					if (m_simphoneNo == null) {
						m_simphoneNo = "";
					}
//					m_simphoneNo.replaceAll("\\D", "");
//					m_simphoneNo.replaceAll("&", "");
//					Log.v("TienNT", m_simPhonename == null ? "NULL" : "NOT NULL");
					m_simPhonename = m_simPhonename.replace("|", "");

					result.add(new RowContentHolder(id, m_simPhonename,
							m_simphoneNo));
					// Log.i("PhoneContact", "name: " + m_simPhonename
					// + " phone: " + m_simphoneNo);
				} while (cursorSim.moveToNext());
			}

			Collections.sort(result, new Comparator<SimContact>() {

				@Override
				public int compare(SimContact lhs, SimContact rhs) {
					// TODO Auto-generated method stub
					return lhs.getName().compareToIgnoreCase(rhs.getName());
				}
			});
		} catch (Exception e) {
			e.printStackTrace();
		}

		return result;
	}

	public Uri createContact(SimContact contact) {
		Uri result = null;
		try {
			Uri simUri = Uri.parse("content://icc/adn");
			ContentValues values = new ContentValues();
			values.put("tag", contact.getName());
			values.put("number", contact.getNumber());
			result = resolver.insert(simUri, values);

			if (result == null) {
				Log.v("TienNT", "Uri result null");
			} else {
				Log.v("TienNT", result.toString());
			}
		} catch (Exception ex) {
			result = null;
		}

		return result;
	}

	public int updateContact(SimContact oldContact, String newName,
			String newNumber) {
		try {
			Uri uri = Uri.parse("content://icc/adn");
			ContentValues values = new ContentValues();
			values.put("tag", oldContact.getName());
			values.put("number", oldContact.getNumber());
			values.put("newTag", newName);
			values.put("newNumber", newNumber);
			return resolver.update(uri, values, null, null);
		} catch (Exception ex) {
			return -1;
		}
	}

	public int deleteContact(SimContact contact) {
		
		try {
			return resolver.delete(simUri, "tag='" + contact.getName()
					+ "' AND number='" + contact.getNumber() + "'", null);
		} catch (Exception ex) {
			return -1;
		}

	}

	public int deleteAll() {
		Uri uri = Uri.parse("content://icc/adn");
		Cursor cursor = resolver.query(uri, null, null, null, null);

		int totalDeleted = 0;
		Log.d("1023", ">>>>>> " + cursor.getCount());
		try {
			while (cursor.moveToNext()) {
				String name = cursor.getString(cursor.getColumnIndex("name"));
				String phoneNumber = cursor.getString(cursor
						.getColumnIndex("number"));
				String where = "tag='" + name + "'";
				where += " AND number='" + phoneNumber + "'";
				totalDeleted += resolver.delete(uri, where, null);
			}
		} catch (Exception ex) {
			
		}

		return totalDeleted;
	}

}