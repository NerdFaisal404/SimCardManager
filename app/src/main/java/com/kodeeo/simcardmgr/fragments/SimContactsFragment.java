package com.kodeeo.simcardmgr.fragments;

import java.io.File;
import java.io.FilenameFilter;
import java.text.DecimalFormat;
import java.util.ArrayList;

import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.AdapterView.AdapterContextMenuInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.kodeeo.simcardmgr.AboutActivity;
import com.kodeeo.simcardmgr.ListAdapter;
import com.kodeeo.simcardmgr.MessageHandler;
import com.kodeeo.simcardmgr.R;
import com.kodeeo.simcardmgr.SimUtil;
import com.kodeeo.simcardmgr.models.RowContentHolder;
import com.kodeeo.simcardmgr.models.SimContact;
import com.kodeeo.simcardmgr.workerthreads.DeleteAllAsyncTask;
import com.kodeeo.simcardmgr.workerthreads.ExportAsyncTask;
import com.kodeeo.simcardmgr.workerthreads.ImportAsyncTask;
import com.kodeeo.simcardmgr.workerthreads.LoadContactsAsyncTask;

public class SimContactsFragment extends BaseFragment {
	ListView mListContact;
	ListAdapter mListAdapter;
	RelativeLayout mRelSelectionMenu;
	Button mBtnSelectAll;
	Button mBtnSelectNone;
	Button mBtnCancel;
	Button mBtnDeleteSelected;
	MessageHandler mHandler;
	Button mBtnCloseSearch;
	TextView mTxtTotal;
	EditText mTxtQuery;
	TextView mTxtNoContacts;
	AdView adView;
	ProgressDialog progressDialog;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setHasOptionsMenu(true);
		mHandler = new MessageHandler(this);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		View root = inflater.inflate(R.layout.contacts_list_fragment,
				container, false);
		initViews(root);
		mListAdapter = new ListAdapter(getActivity(),
				R.layout.contact_list_item, new ArrayList<RowContentHolder>());
		mListAdapter.setHandler(mHandler);
		mListContact.setAdapter(mListAdapter);
		new LoadContactsAsyncTask(getActivity(), mHandler).execute();
		setTitle(getActivity().getResources().getString(
				R.string.tabContact_title));
		registerForContextMenu(mListContact);
		mListContact.setOnItemClickListener(onListItemClick);

		mTxtTotal.setText("Total " + mListAdapter.getCount() + " contacts.");

		return root;
	}

	@Override
	public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
		// TODO Auto-generated method stub
		super.onCreateOptionsMenu(menu, inflater);
		inflater.inflate(R.menu.contact_menu, menu);
	}

	@Override
	public void onCreateContextMenu(ContextMenu menu, View v,
			ContextMenuInfo menuInfo) {
		// TODO Auto-generated method stub
		super.onCreateContextMenu(menu, v, menuInfo);
		RowContentHolder contact = mListAdapter
				.getItem(((AdapterContextMenuInfo) menuInfo).position);
		menu.setHeaderTitle(contact.getName());
		MenuInflater inflater = new MenuInflater(getActivity());
		inflater.inflate(R.menu.context_menu, menu);

	}

	public AdapterView.OnItemClickListener onListItemClick = new AdapterView.OnItemClickListener() {

		@Override
		public void onItemClick(AdapterView<?> parent, View view, int position,
				long id) {
			// TODO Auto-generated method stub
			mListContact.showContextMenuForChild(view);
		}

	};

	@Override
	public boolean onContextItemSelected(MenuItem item) {
		// TODO Auto-generated method stub
		AdapterContextMenuInfo info = (AdapterContextMenuInfo) item
				.getMenuInfo();
		RowContentHolder contact = mListAdapter.getItem(info.position);
		switch (item.getItemId()) {
		case R.id.ctxDelete:
			doDeleteContact(contact);
			break;
		case R.id.ctxModify:
			doEditContact(contact);
			break;
		default:
			break;
		}
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// TODO Auto-generated method stub
		switch (item.getItemId()) {
		case R.id.opt_add:
			doAddContact();
			break;

		case R.id.opt_imex:

			doShowImportExportOption();
			break;

		case R.id.opt_search:
			if (mTxtTotal.getVisibility() == View.VISIBLE) {
				showSearchBar();
			} else {
				hideSearchBar();
			}
			break;

		case R.id.opt_select:
			if (mListAdapter.isInSelectionMode()) {
				mListAdapter.turnOffSelectionMode();
				mRelSelectionMenu.setVisibility(View.GONE);
			} else {
				mListAdapter.turnOnSelectionMode();
				mRelSelectionMenu.setVisibility(View.VISIBLE);
			}

			break;

		case R.id.opt_delAll:
			doDelAll();
			break;

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

	private void initViews(View root) {
		mListContact = (ListView) root.findViewById(R.id.listContacts);
		mRelSelectionMenu = (RelativeLayout) root
				.findViewById(R.id.relSelectionMenu);
		mBtnSelectAll = (Button) root.findViewById(R.id.btnSelectAll);
		mBtnSelectNone = (Button) root.findViewById(R.id.btnSelectNone);
		mBtnCancel = (Button) root.findViewById(R.id.btnCancel);
		mBtnDeleteSelected = (Button) root.findViewById(R.id.btnDelSelected);
		mTxtTotal = (TextView) root.findViewById(R.id.txtTotal);

		mBtnCloseSearch = (Button) root.findViewById(R.id.btnCloseSearch);
		mTxtQuery = (EditText) root.findViewById(R.id.txtSearchQuery);

		mBtnSelectAll.setOnClickListener(onBtnSelectAllClick);
		mBtnSelectNone.setOnClickListener(onBtnSelectNoneClick);
		mBtnDeleteSelected.setOnClickListener(onBtnDeleteSelectedClick);
		mBtnCancel.setOnClickListener(onBtnCancelClick);
		mBtnCloseSearch.setOnClickListener(onBtnCloseSearchClick);
		mTxtQuery.addTextChangedListener(txtQueryTextWatcher);
		mTxtNoContacts = (TextView) root.findViewById(android.R.id.empty);

		adView = (AdView) root.findViewById(R.id.adView);
		AdRequest adRequest = new AdRequest.Builder().build();
		adView.loadAd(adRequest);
	}

	private TextWatcher txtQueryTextWatcher = new TextWatcher() {

		@Override
		public void onTextChanged(CharSequence s, int start, int before,
				int count) {
			// TODO Auto-generated method stub
			mListAdapter.getFilter().filter(s);
		}

		@Override
		public void beforeTextChanged(CharSequence s, int start, int count,
				int after) {
			// TODO Auto-generated method stub

		}

		@Override
		public void afterTextChanged(Editable s) {
			// TODO Auto-generated method stub

		}
	};

	private View.OnClickListener onBtnCloseSearchClick = new View.OnClickListener() {

		@Override
		public void onClick(View v) {
			// TODO Auto-generated method stub
			hideSearchBar();
		}
	};

	private void showSearchBar() {
		mTxtTotal.setVisibility(View.GONE);
		mTxtQuery.setVisibility(View.VISIBLE);
		mBtnCloseSearch.setVisibility(View.VISIBLE);
	}

	private void hideSearchBar() {
		mTxtTotal.setVisibility(View.VISIBLE);
		mTxtQuery.setVisibility(View.INVISIBLE);
		mBtnCloseSearch.setVisibility(View.INVISIBLE);
		mTxtQuery.setText("");
		mTxtTotal.setText("Total " + mListAdapter.getCount() + " contacts");
	}

	private View.OnClickListener onBtnSelectAllClick = new View.OnClickListener() {

		@Override
		public void onClick(View v) {
			// TODO Auto-generated method stub
			mListAdapter.selectAll();
		}
	};

	private View.OnClickListener onBtnSelectNoneClick = new View.OnClickListener() {

		@Override
		public void onClick(View v) {
			// TODO Auto-generated method stub
			mListAdapter.selectNone();
		}
	};

	private View.OnClickListener onBtnCancelClick = new View.OnClickListener() {

		@Override
		public void onClick(View v) {
			// TODO Auto-generated method stub
			mRelSelectionMenu.setVisibility(View.GONE);
			mListAdapter.turnOffSelectionMode();
		}
	};

	private View.OnClickListener onBtnDeleteSelectedClick = new View.OnClickListener() {

		@Override
		public void onClick(View v) {
			// TODO Auto-generated method stub
			int count = 0;
			SimUtil simUtil = new SimUtil(getActivity().getContentResolver());
			try {
				for (int i = 0; i < mListAdapter.getSelectedList().size(); i++) {
					SimContact contact = mListAdapter.getItem(mListAdapter
							.getSelectedList().get(i));
					simUtil.deleteContact(contact);
					count++;
				}
				mListAdapter.removeSelected();
				mTxtTotal.setText("Total " + mListAdapter.getCount()
						+ " contacts");

			} catch (Exception ex) {

			}
			Toast.makeText(
					getActivity(),
					count
							+ " "
							+ getActivity().getResources().getString(
									R.string.deleteAllContact_success),
					Toast.LENGTH_SHORT).show();
		}
	};

	public void reloadContactListView() {
		new LoadContactsAsyncTask(getActivity(), mHandler).execute();
	}

	public void reloadContactListView(ArrayList<RowContentHolder> contacts) {
		mListAdapter.setListData(contacts);
		updateFragmentContent();
	}

	public void doAfterDeleteAll(int totalDeleted) {
		reloadContactListView();
		Toast.makeText(
				getActivity(),
				totalDeleted
						+ " "
						+ getActivity().getResources().getString(
								R.string.deleteAllContact_success),
				Toast.LENGTH_LONG).show();
	}

	private void doDelAll() {
		AlertDialog.Builder alert = new AlertDialog.Builder(getActivity());
		alert.setTitle(R.string.deleteAllContact_title)
				.setMessage(R.string.deleteAllContact_confirm)
				.setPositiveButton(R.string.strOk, null)
				.setNegativeButton(R.string.strCancel, null);

		final AlertDialog dialog = alert.create();
		dialog.show();

		Button btnOk = dialog.getButton(AlertDialog.BUTTON_POSITIVE);
		btnOk.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				new DeleteAllAsyncTask(getActivity(), mHandler, mListAdapter
						.getCount()).execute();
				dialog.dismiss();
			}
		});
	}

	/**
	 * Call to edit an existing contact in ListView
	 * 
	 * @param contact
	 *            the contact to be edit
	 */
	private void doEditContact(final SimContact contact) {

		View v = getActivity().getLayoutInflater().inflate(
				R.layout.edit_contact_dialog, null);
		final EditText txtName = (EditText) v.findViewById(R.id.txtAddName);
		final EditText txtNumber = (EditText) v.findViewById(R.id.txtAddNumber);

		final AlertDialog dialog = new Builder(getActivity()).setView(v)
				.setTitle(R.string.updateContact_title)
				.setPositiveButton(R.string.edit_dlg_btn_OK, null)
				.setNegativeButton(R.string.strCancel, null).create();
		txtName.setText(contact.getName());
		txtNumber.setText(contact.getNumber());

		dialog.setOnShowListener(new DialogInterface.OnShowListener() {

			@Override
			public void onShow(DialogInterface idialog) {
				// TODO Auto-generated method stub
				Button b = dialog.getButton(AlertDialog.BUTTON_POSITIVE);

				b.setOnClickListener(new View.OnClickListener() {

					@Override
					public void onClick(View view) {
						// TODO Do something
						String name = txtName.getText().toString().trim();
						String number = txtNumber.getText().toString();

						if (name.equals("")) {
							Animation shake = AnimationUtils.loadAnimation(
									getActivity(), R.anim.shake);
							txtName.startAnimation(shake);
							return;
						}

						if (number.equals("")) {
							Animation shake = AnimationUtils.loadAnimation(
									getActivity(), R.anim.shake);
							txtNumber.startAnimation(shake);
							return;
						}

						if (!name.equalsIgnoreCase(contact.getName())) {
							if (mListAdapter.containsName(name)) {
								Toast.makeText(getActivity(),
										R.string.strContactExisted,
										Toast.LENGTH_SHORT).show();
								return;
							}
						}
						number = number.replaceAll("\\s+", "");
						int result = new SimUtil(getActivity()
								.getContentResolver()).updateContact(contact,
								name, number);
						if (result <= 0) {
							Toast.makeText(getActivity(),
									R.string.updateContact_failed,
									Toast.LENGTH_SHORT).show();
						} else {
							contact.setName(name);
							contact.setNumber(number);
							updateFragmentContent();
							mListAdapter.updateAfterEdit();

							Toast.makeText(getActivity(),
									R.string.updateContact_success,
									Toast.LENGTH_SHORT).show();
							dialog.dismiss();
						}
					}
				});
			}
		});
		dialog.show();
	}

	/**
	 * Call to add a contact to ListView and also add to SIM card
	 */
	private void doAddContact() {
		View v = getActivity().getLayoutInflater().inflate(
				R.layout.add_contact_dialog, null);
		final TextView txtName = (TextView) v.findViewById(R.id.txtAddName);
		final TextView txtNumber = (TextView) v.findViewById(R.id.txtAddNumber);

		final AlertDialog dialog = new Builder(getActivity()).setView(v)
				.setPositiveButton(R.string.add_dlg_btn_OK, null)
				.setNegativeButton(R.string.strCancel, null)
				.setTitle(R.string.addContact_title).create();

		dialog.setOnShowListener(new DialogInterface.OnShowListener() {

			@Override
			public void onShow(DialogInterface idialog) {
				// TODO Auto-generated method stub
				Button b = dialog.getButton(AlertDialog.BUTTON_POSITIVE);

				b.setOnClickListener(new View.OnClickListener() {

					@Override
					public void onClick(View view) {
						// TODO Do something
						String name = txtName.getText().toString().trim();
						String number = txtNumber.getText().toString();
						if (name.equals("")) {
							Animation shake = AnimationUtils.loadAnimation(
									getActivity(), R.anim.shake);
							txtName.startAnimation(shake);
							return;
						}

						if (number.equals("")) {
							Animation shake = AnimationUtils.loadAnimation(
									getActivity(), R.anim.shake);
							txtNumber.startAnimation(shake);
							return;
						}

						if (mListAdapter.containsName(name)) {
							Toast.makeText(getActivity(),
									R.string.strContactExisted,
									Toast.LENGTH_SHORT).show();
							return;
						}
						number = number.replaceAll("\\s+", "");
						RowContentHolder contact = new RowContentHolder("",
								name, number);

						Uri result = new SimUtil(getActivity()
								.getContentResolver()).createContact(contact);
						if (result == null) {
							Toast.makeText(getActivity(),
									R.string.addContact_failed,
									Toast.LENGTH_SHORT).show();
						} else {
							mListAdapter.add(contact);
							updateFragmentContent();
							Toast.makeText(getActivity(),
									R.string.addContact_success,
									Toast.LENGTH_SHORT).show();
							dialog.dismiss();
						}
					}
				});
			}
		});
		dialog.show();
	}

	private void doDeleteContact(final RowContentHolder contact) {
		AlertDialog.Builder alert = new AlertDialog.Builder(getActivity());
		alert.setTitle(R.string.deleteContact_title)
				.setMessage(
						getActivity().getResources().getString(
								R.string.deleteContact_confirm)
								+ " \"" + contact.getName() + "\"")
				.setPositiveButton(R.string.strOk, null)
				.setNegativeButton(R.string.strCancel, null);

		final AlertDialog dialog = alert.create();
		dialog.show();

		Button btnOk = dialog.getButton(AlertDialog.BUTTON_POSITIVE);
		btnOk.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				int result = new SimUtil(getActivity().getContentResolver())
						.deleteContact(contact);
				if (result <= 0) {
					Toast.makeText(getActivity(),
							R.string.deleteContact_failed, Toast.LENGTH_LONG)
							.show();
				} else {
					mListAdapter.remove(contact);
					updateFragmentContent();
					Toast.makeText(getActivity(),
							R.string.deleteContact_completed, Toast.LENGTH_LONG)
							.show();
				}
				dialog.dismiss();
			}
		});
	}

	private void updateFragmentContent() {
		mTxtTotal.setText("Total " + mListAdapter.getCount() + " contacts");
		if (mListAdapter.getCount() == 0) {
			mTxtNoContacts.setVisibility(View.VISIBLE);
			mListContact.setVisibility(View.GONE);
		} else {
			mTxtNoContacts.setVisibility(View.GONE);
			mListContact.setVisibility(View.VISIBLE);
		}
	}

	@Override
	public void onDestroy() {
		mTxtQuery.removeTextChangedListener(txtQueryTextWatcher);
		super.onDestroy();
	}

	public void setTotalView(int count) {
		mTxtTotal.setText("Total " + count + " contacts");
	}

	public MessageHandler getHandler() {
		return mHandler;
	}

	public void doShowImportExportOption() {
		AlertDialog.Builder alert = new Builder(getActivity());
		alert.setTitle("Import/Export Contacts");
		alert.setItems(R.array.im_ex_arr,
				new DialogInterface.OnClickListener() {

					@Override
					public void onClick(DialogInterface dialog, int which) {
						// TODO Auto-generated method stub
						if (which == 0) {
							// Import
							doImport();
						} else if (which == 1) {
							doExport();
						}
					}
				});

		alert.setNegativeButton(R.string.strCancel, null);
		alert.show();
	}

	private String checkAndGenExportFileName() {
		String externalState = Environment.getExternalStorageState();
		if (externalState.equals(Environment.MEDIA_MOUNTED)) {
			DecimalFormat myFormatter = new DecimalFormat("0000");
			File storeDirectory = Environment.getExternalStorageDirectory();

			int k = 0;
			String fileName = "SimContacts" + myFormatter.format(k) + ".vcf";
			String filePath = storeDirectory.getAbsolutePath() + File.separator;
			File outputFile = new File(filePath + fileName);

			while (outputFile.exists()) {
				fileName = "SimContacts" + myFormatter.format(++k) + ".vcf";
				outputFile = new File(filePath + fileName);
			}
			return outputFile.getAbsolutePath();
		}
		return null;
	}

	private void doExport() {

		// Export
		final String fileName = checkAndGenExportFileName();
		if (fileName != null) {
			AlertDialog.Builder alert = new Builder(getActivity())
					.setTitle("Export to SD Card")
					.setMessage(
							"Your SIM contacts will be exported to '"
									+ fileName
									+ "'. \n\nDo you want to process? ")
					.setPositiveButton(R.string.strOk,
							new DialogInterface.OnClickListener() {

								@SuppressWarnings("unchecked")
								@Override
								public void onClick(DialogInterface dialog,
										int which) {
									// TODO Auto-generated
									// method stub
									new ExportAsyncTask(getActivity(),
											mHandler, mListAdapter
													.getListOfContents(),
											fileName).execute();
								}
							}).setNegativeButton(R.string.strCancel, null);
			alert.show();
		} else {
			Toast.makeText(getActivity(), R.string.strExportFailed_locked,
					Toast.LENGTH_LONG).show();
		}

	}

	private void doImport() {
		final String[] listOfFiles = checkAndGetImportFiles();
		if (listOfFiles == null) {
			Toast.makeText(getActivity(), R.string.strNothingToImport,
					Toast.LENGTH_LONG).show();
		} else {
			AlertDialog.Builder alert = new AlertDialog.Builder(getActivity());
			alert.setTitle("Import from file:").setItems(listOfFiles,
					new DialogInterface.OnClickListener() {

						@Override
						public void onClick(DialogInterface dialog, int which) {
							// TODO Auto-generated method stub
							String fileName = listOfFiles[which];
							final String fileFullName = Environment
									.getExternalStorageDirectory()
									.getAbsolutePath()
									+ File.separator + fileName;

							AlertDialog.Builder alert = new Builder(
									getActivity())
									.setTitle("Import from file")
									.setMessage(
											"This will APPEND contacts in file to list of contacts in SIM Card. Do you want to continue? ")
									.setPositiveButton(
											R.string.strOk,
											new DialogInterface.OnClickListener() {

												@Override
												public void onClick(
														DialogInterface dialog,
														int which) {
													// TODO Auto-generated
													// method stub
													new ImportAsyncTask(
															getActivity(),
															mHandler)
															.execute(fileFullName);
												}
											})
									.setNegativeButton(R.string.strCancel, null);
							alert.show();
						}
					});
			alert.setPositiveButton(R.string.strCancel, null);
			alert.show();
		}
	}

	private String[] checkAndGetImportFiles() {
		String externalState = Environment.getExternalStorageState();
		if (externalState.equals(Environment.MEDIA_MOUNTED)) {
			File[] vCardFiles = Environment.getExternalStorageDirectory()
					.listFiles(new FilenameFilter() {

						@Override
						public boolean accept(File dir, String filename) {
							// TODO Auto-generated method stub
							if (filename.endsWith(".vcf")) {
								return true;
							}
							return false;
						}
					});
			ArrayList<String> result = new ArrayList<String>();
			if (vCardFiles != null) {
				for (File f : vCardFiles) {
					result.add(f.getName());
				}
				return result.toArray(new String[0]);
			}
		}

		return null;

	}
}
