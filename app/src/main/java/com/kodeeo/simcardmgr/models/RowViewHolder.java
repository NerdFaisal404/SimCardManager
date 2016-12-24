package com.kodeeo.simcardmgr.models;

import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.kodeeo.simcardmgr.R;

public class RowViewHolder {
	private View root;
	private TextView txtName;
	private TextView txtNumber;
	private RelativeLayout relActions;
	private Button btnCall;
	private Button btnText;
	private CheckBox cbSelect;

	public RowViewHolder(View root) {
		this.root = root;
	}

	public TextView getTxtName() {
		if (txtName == null) {
			txtName = (TextView) root.findViewById(R.id.txtName);
		}
		return txtName;
	}

	public TextView getTxtNumber() {
		if (txtNumber == null) {
			txtNumber = (TextView) root.findViewById(R.id.txtNum);
		}
		return txtNumber;
	}

	public Button getBtnCall() {
		if (btnCall == null) {
			btnCall = (Button) root.findViewById(R.id.btnCall);
		}
		return btnCall;
	}

	public Button getBtnText() {
		if (btnText == null) {
			btnText = (Button) root.findViewById(R.id.btnText);
		}
		return btnText;
	}

	public CheckBox getCbSelect() {
		if (cbSelect == null) {
			cbSelect = (CheckBox) root.findViewById(R.id.cbSelect);
		}
		return cbSelect;
	}

	public RelativeLayout getRelActions() {
		if (relActions == null) {
			relActions = (RelativeLayout) root.findViewById(R.id.relAction);
		}
		return relActions;
	}
}
