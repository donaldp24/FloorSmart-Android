package com.tim.FloorSmart;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.*;
import com.tim.FloorSmart.Database.FSJob;
import com.tim.FloorSmart.JobsActivity;

import java.util.ArrayList;

public class JobItemAdapter extends BaseAdapter {
	Context			mContext = null;
	JobsActivity mActivity = null;
    private LayoutInflater mInflater;

    private ArrayList<FSJob> m_Infos = new ArrayList<FSJob>();
    EditText        currentFocus = null;
    int             nEditingIdx = -1;

    public JobItemAdapter(JobsActivity activity, Context context) {
    	mContext = context;
    	mActivity = activity;
        // Cache the LayoutInflate to avoid asking for a new one each time.
        mInflater = LayoutInflater.from(context);
    }

    public void setData(ArrayList<FSJob> parkData)
    {
        m_Infos = parkData;
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
            convertView = mInflater.inflate(R.layout.jobs_item, null);
            ResolutionSet._instance.iterateChild(convertView.findViewById(R.id.RLJobsItemRoot));
        } else {
        }

        FSJob _info = null;
        if (m_Infos != null)
        {
            _info = m_Infos.get(position);
        }

        JobItem itemInfo = new JobItem();
        itemInfo.parentView = convertView;
        itemInfo.itemId = position;
        itemInfo.jobName = (_info != null) ? _info.jobName : "";

        // Bind the data efficiently with the holder.
        if (_info != null)
        {
            ((TextView)convertView.findViewById(R.id.lblJobName)).setText(_info.jobName);

            if (nEditingIdx == position)
            {
                ((RelativeLayout) convertView.findViewById(R.id.RLShowBar)).setVisibility(View.GONE);
                ((RelativeLayout) convertView.findViewById(R.id.RLEditBar)).setVisibility(View.VISIBLE);

                ((EditText) convertView.findViewById(R.id.txtItemJobName)).setFocusable(true);
                ((EditText) convertView.findViewById(R.id.txtItemJobName)).setFocusableInTouchMode(true);
                ((EditText) convertView.findViewById(R.id.txtItemJobName)).requestFocus();

                ((EditText) convertView.findViewById(R.id.txtItemJobName)).setText(_info.jobName);
                ((EditText) convertView.findViewById(R.id.txtItemJobName)).setSelection(_info.jobName.length());
            }

            /*if (currentFocus != null)
            {
                mActivity.removeMainFocus();
                currentFocus.requestFocus();
            }*/
        }else
        {
            ((TextView)convertView.findViewById(R.id.lblJobName)).setText("");

            ((RelativeLayout)convertView.findViewById(R.id.RLEditBar)).setVisibility(View.GONE);
        }

        RelativeLayout rootItem = (RelativeLayout)convertView.findViewById(R.id.RLJobsItemRoot);
        rootItem.setTag(position);
        rootItem.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (currentFocus != null)
                    return;

                mActivity.clickItem((Integer)v.getTag());
            }
        });

        Button btnJobEdit = (Button)convertView.findViewById(R.id.btnJobEdit);
        Button btnJobArchive = (Button)convertView.findViewById(R.id.btnJobDelete);
        Button btnEditDone = (Button)convertView.findViewById(R.id.btnEditDone);
        Button btnEditCancel = (Button)convertView.findViewById(R.id.btnCancel);


        btnJobEdit.setTag(itemInfo);
        btnJobEdit.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (currentFocus != null)
                    return;

                JobItem itemInfo = (JobItem) v.getTag();
                nEditingIdx = itemInfo.itemId;
                currentFocus = (EditText) itemInfo.parentView.findViewById(R.id.txtItemJobName);
                ((RelativeLayout) itemInfo.parentView.findViewById(R.id.RLShowBar)).setVisibility(View.GONE);
                ((RelativeLayout) itemInfo.parentView.findViewById(R.id.RLEditBar)).setVisibility(View.VISIBLE);
                currentFocus.setFocusable(true);
                currentFocus.setFocusableInTouchMode(true);
                currentFocus.requestFocus();
                currentFocus.setText(itemInfo.jobName);
                currentFocus.setSelection(itemInfo.jobName.length());

                InputMethodManager imm = (InputMethodManager) mContext.getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.showSoftInput(itemInfo.parentView.findViewById(R.id.txtItemJobName), InputMethodManager.SHOW_IMPLICIT);
            }
        });

        btnJobArchive.setTag(itemInfo);
        btnJobArchive.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (currentFocus != null)
                    return;

                JobItem itemInfo = (JobItem)v.getTag();
                mActivity.goArchive(itemInfo.itemId);
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

                JobItem itemInfo = (JobItem)v.getTag();
                ((RelativeLayout)itemInfo.parentView.findViewById(R.id.RLShowBar)).setVisibility(View.VISIBLE);
                ((RelativeLayout)itemInfo.parentView.findViewById(R.id.RLEditBar)).setVisibility(View.GONE);

                String strNewName = ((EditText)itemInfo.parentView.findViewById(R.id.txtItemJobName)).getText().toString();
                if (mActivity.changeJobName(itemInfo.itemId, strNewName))
                {
                    ((TextView)itemInfo.parentView.findViewById(R.id.lblJobName)).setText(strNewName);
                }

                hideSoftKeyboard(itemInfo.parentView.findViewById(R.id.txtItemJobName));
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

                JobItem itemInfo = (JobItem)v.getTag();
                ((RelativeLayout)itemInfo.parentView.findViewById(R.id.RLShowBar)).setVisibility(View.VISIBLE);
                ((RelativeLayout)itemInfo.parentView.findViewById(R.id.RLEditBar)).setVisibility(View.GONE);

                hideSoftKeyboard(itemInfo.parentView.findViewById(R.id.txtItemJobName));
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

    class JobItem
    {
        public View parentView;
        public int itemId;
        public String jobName;
    }
}