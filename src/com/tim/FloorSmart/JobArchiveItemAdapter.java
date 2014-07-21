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

import java.util.ArrayList;

public class JobArchiveItemAdapter extends BaseAdapter {
	Context			mContext = null;
	JobsArchiveActivity mActivity = null;
    private LayoutInflater mInflater;

    private ArrayList<FSJob> m_Infos = new ArrayList<FSJob>();

    public JobArchiveItemAdapter(JobsArchiveActivity activity, Context context) {
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
            convertView = mInflater.inflate(R.layout.jobs_archive_item, null);
            ResolutionSet._instance.iterateChild(convertView.findViewById(R.id.RLJobsItemRoot));
        } else {
        }

        FSJob _info = null;
        if (m_Infos != null)
        {
            _info = m_Infos.get(position);
        }

        // Bind the data efficiently with the holder.
        if (_info != null)
        {
            ((TextView)convertView.findViewById(R.id.lblJobName)).setText(_info.jobName);
        }else
        {
            ((TextView)convertView.findViewById(R.id.lblJobName)).setText("");
        }
        /*
        RelativeLayout rootItem = (RelativeLayout)convertView.findViewById(R.id.RLJobsItemRoot);
        rootItem.setTag(position);
        rootItem.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                mActivity.clickItem((Integer)v.getTag());
            }
        });
        */
        Button btnDelete = (Button)convertView.findViewById(R.id.btnJobDelete);
        Button btnResend = (Button)convertView.findViewById(R.id.btnJobResend);

        btnDelete.setTag(position);
        btnDelete.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                mActivity.deleteJob((Integer)v.getTag());
            }
        });

        btnResend.setTag(position);
        btnResend.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                mActivity.resendJob((Integer)v.getTag());
            }
        });

        return convertView;
    }
}