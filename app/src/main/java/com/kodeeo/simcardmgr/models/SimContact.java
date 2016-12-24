package com.kodeeo.simcardmgr.models;

import android.util.Log;

public class SimContact {
	private static final String TAG = SimContact.class.getSimpleName();

	protected String name;
	protected String number;
	protected String id;

	public SimContact() {
		// TODO Auto-generated constructor stub
	}

	public SimContact(String id, String name, String number) {
		if (Log.isLoggable(TAG, Log.DEBUG))
			Log.d(TAG, "Contact()[" + id + "] '" + name + "': " + number);
		this.id = id;
		this.name = name;
		this.number = number;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getNumber() {
		return number;
	}

	public void setNumber(String number) {
		this.number = number;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	/**
	 * @return the string for the Listview
	 */
	@Override
	public String toString() {
		return name;
	}

	// null-safe string compare
	public boolean compareStrings(final String one, final String two) {
		if (one == null ^ two == null) {
			return false;
		}
		if (one == null && two == null) {
			return true;
		}
		return one.compareTo(two) == 0;
	}

	@Override
	public boolean equals(Object o) {
		SimContact c = (SimContact) o;
		if (c == null) {
			return false;
		}
		if (this.name == null) {
			if (c.name != null)
				return false;
		}

		return this.name.equalsIgnoreCase(c.name);
	}

	public int compareTo(SimContact c) {
		return this.name.compareToIgnoreCase(c.getName());
	}

}