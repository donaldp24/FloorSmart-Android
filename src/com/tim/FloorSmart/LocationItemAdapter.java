package com.tim.FloorSmart;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.*;
import com.tim.FloorSmart.Database.FSLocation;

import java.util.ArrayList;

public class LocationItemAdapter extends BaseAdapter {
	Context			mContext = null;
	LocationsActivity mActivity = null;
    private LayoutInflater mInflater;

    private ArrayList<FSLocation> m_Infos = new ArrayList<FSLocation>();
    EditText        currentFocus = null;
    int             nEditingIdx = -1;

    public LocationItemAdapter(LocationsActivity activity, Context context) {
    	mContext = context;
    	mActivity = activity;
        // Cache the LayoutInflate to avoid asking for a new one each time.
        mInflater = LayoutInflater.from(context);
    }

    public void setData(ArrayList<FSLocation> locationData)
    {
        m_Infos = locationData;
    }

    /**
     * The number of items in the list is determined by the number of speeches
     * in our array.
     *
     * @see android.widget.ListAdapter#getCount()
     */
    public int getCount() {
    	if (m_Infos == null)
    		return 0;

        return m_Infos.size();
    }

    /**
     * Since the data comes from an array, just returning the index is
     * sufficient to get at the data. If we were using a more complex data
     * structure, we would return whatever object represents one row in the
     * list.
     *
     * @see android.widget.ListAdapter#getItem(int)
     */
    public Object getItem(int position) {
        return position;
    }

    /**
     * Use the array index as a unique id.
     *
     * @see android.widget.ListAdapter#getItemId(int)
     */
    public long getItemId(int position) {
        return position;
    }

    /**
     * Make a view to hold each row.
     *
     * @see android.widget.ListAdapter#getView(int, android.view.View,
     *      android.view.ViewGroup)
     */
    public View getView(int position, View convertView, ViewGroup parent) {
        // When convertView is not null, we can reuse it directly, there is no need
        // to reinflate it. We only inflate a new View when the convertView supplied
        // by ListView is null.
        if (convertView == null) {
            convertView = mInflater.inflate(R.layout.locations_item, null);
            ResolutionSet._instance.iterateChild(convertView.findViewById(R.id.RLLocationsItemRoot));
        } else {
        }

        FSLocation _info = null;
        if (m_Infos != null)
        {
            _info = m_Infos.get(position);
        }

        LocationItem itemInfo = new LocationItem();
        itemInfo.parentView = convertView;
        itemInfo.itemId = position;
        itemInfo.locationName = (_info != null) ? _info.locName : "";

        // Bind the data efficiently with the holder.
        if (_info != null)
        {
            ((TextView)convertView.findViewById(R.id.lblLocationName)).setText(_info.locName);

            if (nEditingIdx == position)
            {
                ((RelativeLayout) convertView.findViewById(R.id.RLShowBar)).setVisibility(View.GONE);
                ((RelativeLayout) convertView.findViewById(R.id.RLEditBar)).setVisibility(View.VISIBLE);

                ((EditText) convertView.findViewById(R.id.txtItemLocName)).setFocusable(true);
                ((EditText) convertView.findViewById(R.id.txtItemLocName)).setFocusableInTouchMode(true);
                ((EditText) convertView.findViewById(R.id.txtItemLocName)).requestFocus();

                ((EditText) convertView.findViewById(R.id.txtItemLocName)).setText(_info.locName);
                ((EditText) convertView.findViewById(R.id.txtItemLocName)).setSelection(_info.locName.length());
            }

            /*if (currentFocus != null)
            {
                mActivity.removeMainFocus();
                currentFocus.requestFocus();
            }*/
        }else
        {
            ((TextView)convertView.findViewById(R.id.lblLocationName)).setText("");

            ((RelativeLayout)convertView.findViewById(R.id.RLEditBar)).setVisibility(View.GONE);
        }

        RelativeLayout rlRootItem = (RelativeLayout)convertView.findViewById(R.id.RLLocationsItemRoot);
        rlRootItem.setTag(position);
        rlRootItem.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (currentFocus != null)
                    return;

                mActivity.clickItem((Integer)v.getTag());
            }
        });

        Button btnLocEdit = (Button)convertView.findViewById(R.id.btnLocEdit);
        Button btnLocDelete = (Button)convertView.findViewById(R.id.btnLocDelete);
        Button btnEditDone = (Button)convertView.findViewById(R.id.btnEditDone);
        Button btnEditCancel = (Button)convertView.findViewById(R.id.btnCancel);

        btnLocEdit.setTag(itemInfo);
        btnLocEdit.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (currentFocus != null)
                    return;

                LocationItem itemInfo = (LocationItem) v.getTag();
                nEditingIdx = itemInfo.itemId;
                currentFocus = (EditText) itemInfo.parentView.findViewById(R.id.txtItemLocName);
                ((RelativeLayout) itemInfo.parentView.findViewById(R.id.RLShowBar)).setVisibility(View.GONE);
                ((RelativeLayout) itemInfo.parentView.findViewById(R.id.RLEditBar)).setVisibility(View.VISIBLE);
                currentFocus.setFocusable(true);
                currentFocus.setFocusableInTouchMode(true);
                currentFocus.requestFocus();
                currentFocus.setText(itemInfo.locationName);
                currentFocus.setSelection(itemInfo.locationName.length());

                InputMethodManager imm = (InputMethodManager) mContext.getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.showSoftInput(itemInfo.parentView.findViewById(R.id.txtItemLocName), InputMethodManager.SHOW_IMPLICIT);
            }
        });

        btnLocDelete.setTag(itemInfo);
        btnLocDelete.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (currentFocus != null)
                    return;

                LocationItem itemInfo = (LocationItem) v.getTag();
                mActivity.deleteLocation(itemInfo.itemId);
            }
        });

        btnEditDone.setTag(itemInfo);
        btnEditDone.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                currentFocus.setFocusable(false);
                currentFocus.setFocusableInTouchMode(false);
                currentFocus = null;
                nEditingIdx = -1;

                LocationItem itemInfo = (LocationItem)v.getTag();
                ((RelativeLayout)itemInfo.parentView.findViewById(R.id.RLShowBar)).setVisibility(View.VISIBLE);
                ((RelativeLayout)itemInfo.parentView.findViewById(R.id.RLEditBar)).setVisibility(View.GONE);

                String strNewName = ((EditText)itemInfo.parentView.findViewById(R.id.txtItemLocName)).getText().toString();
                if (mActivity.changeLocation(itemInfo.itemId, strNewName))
                {
                    ((TextView)itemInfo.parentView.findViewById(R.id.lblLocationName)).setText(strNewName);
                }

                hideSoftKeyboard(itemInfo.parentView.findViewById(R.id.txtItemLocName));
                //hideSoftKeyboard(itemInfo.parentView);

                mActivity.hideSoftKeyboard();
            }
        });

        btnEditCancel.setTag(itemInfo);
        btnEditCancel.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                currentFocus.setFocusable(false);
                currentFocus.setFocusableInTouchMode(false);
                currentFocus = null;
                nEditingIdx = -1;

                LocationItem itemInfo = (LocationItem)v.getTag();
                ((RelativeLayout)itemInfo.parentView.findViewById(R.id.RLShowBar)).setVisibility(View.VISIBLE);
                ((RelativeLayout)itemInfo.parentView.findViewById(R.id.RLEditBar)).setVisibility(View.GONE);

                hideSoftKeyboard(itemInfo.parentView.findViewById(R.id.txtItemLocName));
                //hideSoftKeyboard(itemInfo.parentView);

                mActivity.hideSoftKeyboard();
            }
        });

        return convertView;
    }

    private void hideSoftKeyboard(View focusView)
    {
        InputMethodManager inputMethodManager = (InputMethodManager) mContext.getSystemService(Context.INPUT_METHOD_SERVICE);
        inputMethodManager.hideSoftInputFromWindow(focusView.getWindowToken(), InputMethodManager.HIDE_IMPLICIT_ONLY);
        inputMethodManager.hideSoftInputFromWindow(focusView.getWindowToken(), InputMethodManager.HIDE_IMPLICIT_ONLY);
        mActivity.getWindow().setSoftInputMode(
                WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
    }

    class LocationItem
    {
        public View parentView;
        public int itemId;
        public String locationName;
    }
}