package com.kodeeo.simcardmgr;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Locale;

import com.kodeeo.simcardmgr.models.RowContentHolder;
import com.kodeeo.simcardmgr.models.RowViewHolder;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Filter;

public class ListAdapter extends ArrayAdapter<RowContentHolder> {
	ArrayList<RowContentHolder> mItems = new ArrayList<RowContentHolder>();
	private ArrayList<RowContentHolder> mOriginalItems;
	Context mContext;
	ArrayList<Integer> mSelectionList = new ArrayList<Integer>();
	MessageHandler handler;

	private boolean inSelectionMode = false;
	private final Object mLock = new Object();
	private CustomFilter mFilter;

	public ListAdapter(Context context, int textViewResourceId,
			ArrayList<RowContentHolder> objects) {
		super(context, textViewResourceId, objects);
		mContext = context;
		mItems = objects;
		// TODO Auto-generated constructor stub
	}

	public void setHandler(MessageHandler handler) {
		this.handler = handler;
	}

	public void setListData(ArrayList<RowContentHolder> objects) {
		mItems = objects;
		notifyDataSetChanged();
	}

	@Override
	public void add(RowContentHolder object) {
		// TODO Auto-generated method stub
		synchronized (mLock) {
			if (mOriginalItems != null) {
				mOriginalItems.add(object);
				Collections.sort(mOriginalItems, new ContactComparator());
			} else {
				mItems.add(object);
				Collections.sort(mItems, new ContactComparator());
			}
		}

		notifyDataSetChanged();
	}

	public void removeSelected() {
		for (int i = mSelectionList.size() - 1; i >= 0; i--) {
			mItems.remove((int) (mSelectionList.get(i)));
			mSelectionList.remove(i);
		}
		notifyDataSetChanged();
	}

	@Override
	public void clear() {
		// TODO Auto-generated method stub
		synchronized (mLock) {
			if (mOriginalItems != null) {
				mOriginalItems.clear();
			} else {
				mItems.clear();
			}
		}
		notifyDataSetChanged();
	}

	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return mItems.size();
	}

	public int getOriginalCount() {
		return mOriginalItems.size();
	}

	@Override
	public RowContentHolder getItem(int position) {
		// TODO Auto-generated method stub
		return mItems.get(position);
	}

	public void selectAll() {
		mSelectionList.clear();
		for (int i = 0; i < mItems.size(); i++) {
			mItems.get(i).setSelected(true);
			mSelectionList.add(i);
		}
		notifyDataSetChanged();
	}

	public void selectNone() {
		for (int i = 0; i < mItems.size(); i++) {
			mItems.get(i).setSelected(false);
		}
		mSelectionList.clear();
		notifyDataSetChanged();
	}

	public ArrayList<Integer> getSelectedList() {
		return mSelectionList;
	}

	/**
	 * After update contact, call this method to update contact's value in
	 * ListView.
	 */
	public void updateAfterEdit() {
		Collections.sort(mItems, new ContactComparator());
		notifyDataSetChanged();
	}

	@Override
	public View getView(final int position, View convertView, ViewGroup parent) {
		// TODO Auto-generated method stub
		View v = convertView;
		RowViewHolder viewHolder = null;

		if (v == null) {
			v = LayoutInflater.from(mContext).inflate(R.layout.contact_list_item,
					null);
			viewHolder = new RowViewHolder(v);
			v.setTag(viewHolder);
		} else {
			viewHolder = (RowViewHolder) v.getTag();
		}

		final RowContentHolder item = mItems.get(position);

		if (item != null) {
			viewHolder.getTxtName().setText(item.getName());
			viewHolder.getTxtNumber().setText(item.getNumber());
			viewHolder.getCbSelect().setChecked(item.isSelected());

			viewHolder.getBtnCall().setOnClickListener(
					new View.OnClickListener() {

						@Override
						public void onClick(View v) {
							// TODO Auto-generated method stub
							if (item.getNumber() != null && !item.getNumber().equals("")) {
								try {
									String phoneNum = item.getNumber();
									String uri = "tel: " + phoneNum.trim();
									Intent iDial = new Intent(Intent.ACTION_DIAL);
									iDial.setData(Uri.parse(uri));
									mContext.startActivity(iDial);
								} catch (Exception ex) {
									
								}
							}
						}
					});

			viewHolder.getBtnText().setOnClickListener(
					new View.OnClickListener() {

						@Override
						public void onClick(View v) {
							// TODO Auto-generated method stub
							if (item.getNumber() != null && !item.getNumber().equals("")) {
								try {
									Intent sendIntent = new Intent(Intent.ACTION_VIEW);
									sendIntent.setData(Uri.parse("sms:"
											+ item.getNumber().trim()));
									mContext.startActivity(sendIntent);
								} catch (Exception ex) {
									
								}
							}
							
						}
					});

			viewHolder.getCbSelect().setOnClickListener(
					new View.OnClickListener() {

						@Override
						public void onClick(View v) {
							// TODO Auto-generated method stub
							if (item.isSelected()) {
								item.setSelected(false);
								mSelectionList.remove(Integer.valueOf(position));
							} else {
								item.setSelected(true);
								mSelectionList.add(position);
							}
						}
					});

			if (inSelectionMode) {
				viewHolder.getRelActions().setVisibility(View.GONE);
				viewHolder.getCbSelect().setVisibility(View.VISIBLE);
			} else {
				viewHolder.getRelActions().setVisibility(View.VISIBLE);
				viewHolder.getCbSelect().setVisibility(View.GONE);
			}
		}

		return v;
	}

	@Override
	public void insert(RowContentHolder object, int index) {
		// TODO Auto-generated method stub
		synchronized (mLock) {
			if (mOriginalItems != null) {
				mOriginalItems.add(index, object);
			} else {
				mItems.add(index, object);
			}
		}
		notifyDataSetChanged();
	}

	@Override
	public void remove(RowContentHolder object) {
		// TODO Auto-generated method stub
		synchronized (mLock) {
			if (mOriginalItems != null) {
				mOriginalItems.remove(object);
			}

			mItems.remove(object);

		}
		notifyDataSetChanged();
	}

	public ArrayList<RowContentHolder> getListOfContents() {
		return mItems;
	}

	public boolean containsName(String name) {
		for (int i = 0; i < mItems.size(); i++) {
			if (mItems.get(i).getName().equalsIgnoreCase(name)) {
				return true;
			}
		}
		return false;
	}

	public void turnOnSelectionMode() {
		inSelectionMode = true;
		notifyDataSetChanged();
	}

	public void turnOffSelectionMode() {
		inSelectionMode = false;
		notifyDataSetChanged();
	}

	public boolean isInSelectionMode() {
		return inSelectionMode;
	}

	public Filter getFilter() {
		if (mFilter == null) {
			mFilter = new CustomFilter();
		}
		return mFilter;
	}

	private class CustomFilter extends Filter {

		@Override
		protected FilterResults performFiltering(CharSequence constraint) {
			// TODO Auto-generated method stub
			FilterResults results = new FilterResults();
			if (mOriginalItems == null) {
				synchronized (mLock) {
					mOriginalItems = new ArrayList<RowContentHolder>(mItems);
				}
			}

			if (constraint == null || constraint.length() == 0) {
				ArrayList<RowContentHolder> list;
				synchronized (mLock) {
					list = new ArrayList<RowContentHolder>(mOriginalItems);
					mOriginalItems = null;
				}
				results.values = list;
				results.count = list.size();
			} else {
				String constraintString = constraint.toString().toLowerCase(
						Locale.getDefault());

				ArrayList<RowContentHolder> values;
				synchronized (mLock) {
					values = new ArrayList<RowContentHolder>(mOriginalItems);
				}

				final int count = values.size();
				final ArrayList<RowContentHolder> newValues = new ArrayList<RowContentHolder>();

				for (int i = 0; i < count; i++) {
					final RowContentHolder value = values.get(i);
					final String valueText = value.toString().toLowerCase(
							Locale.getDefault());

					// First match against the whole, non-splitted value
					if (valueText.contains(constraintString)) {
						newValues.add(value);
					}
				}

				results.values = newValues;
				results.count = newValues.size();
			}

			return results;
		}

		@SuppressWarnings("unchecked")
		@Override
		protected void publishResults(CharSequence constraint,
				FilterResults results) {
			// TODO Auto-generated method stub
			mItems = (ArrayList<RowContentHolder>) results.values;
			handler.obtainMessage(MessageHandler.FILTER_COMPLETED,
					mItems.size(), 0).sendToTarget();
			if (results.count > 0) {
				notifyDataSetChanged();
			} else {
				notifyDataSetInvalidated();
			}
		}

	}

	private class ContactComparator implements Comparator<RowContentHolder> {

		@Override
		public int compare(RowContentHolder lhs, RowContentHolder rhs) {
			// TODO Auto-generated method stub
			return lhs.getName().compareToIgnoreCase(rhs.getName());
		}

	}
}
