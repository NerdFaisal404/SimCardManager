package com.kodeeo.simcardmgr.models;


public class RowContentHolder extends SimContact {

	protected boolean selected;

	public RowContentHolder(String id, String name, String number) {
		super(id, name, number);
		// TODO Auto-generated constructor stub
		selected = false;
	}

	public boolean isSelected() {
		return selected;
	}

	public void setSelected(boolean selected) {
		this.selected = selected;
	}

}
