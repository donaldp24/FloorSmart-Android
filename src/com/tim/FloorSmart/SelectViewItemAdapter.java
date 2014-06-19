package com.tim.FloorSmart;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;
import com.tim.FloorSmart.Database.FSJob;
import com.tim.FloorSmart.Database.FSLocation;
import com.tim.FloorSmart.Database.FSProduct;
import com.tim.FloorSmart.Global.GlobalData;

import java.util.ArrayList;

public class SelectViewItemAdapter extends BaseAdapter {
	Context			mContext = null;
	RecordActivity mActivity = null;
    private LayoutInflater mInflater;

    int mode = -1;
    private ArrayList<FSJob> m_jobInfos = new ArrayList<FSJob>();
    private ArrayList<FSLocation> m_locationInfos = new ArrayList<FSLocation>();
    private ArrayList<FSProduct> m_productInfos = new ArrayList<FSProduct>();

    public SelectViewItemAdapter(RecordActivity activity, Context context) {
    	mContext = context;
    	mActivity = activity;
        // Cache the LayoutInflate to avoid asking for a new one each time.
        mInflater = LayoutInflater.from(context);
    }

    public void setJobData(ArrayList<FSJob> jobData)
    {
        m_jobInfos = jobData;
    }

    public void setLocData(ArrayList<FSLocation> locData)
    {
        m_locationInfos = locData;
    }

    public void setProductData(ArrayList<FSProduct> productData)
    {
        m_productInfos = productData;
    }

    public void setMode(int mode)
    {
        this.mode = mode;
    }

    /**
     * The number of items in the list is determined by the number of speeches
     * in our array.
     *
     * @see android.widget.ListAdapter#getCount()
     */
    public int getCount() {
    	if (mode == -1)
    		return 0;

        if (mode == GlobalData.MODE_SELECT_JOB)
        {
            if (m_jobInfos == null)
                return 0;
            else
                return m_jobInfos.size();
        }

        if (mode == GlobalData.MODE_SELECT_PRODUCT)
        {
            if (m_productInfos == null)
                return 0;
            else
                return m_productInfos.size();
        }

        if (mode == GlobalData.MODE_SELECT_LOCATION)
        {
            if (m_locationInfos == null)
                return 0;
            else
                return m_locationInfos.size();
        }

        return 0;
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
            convertView = mInflater.inflate(R.layout.selectview_item, null);
            ResolutionSet._instance.iterateChild(convertView.findViewById(R.id.RLSelectViewItemRoot));
        } else {
        }

        String itemTitle = "";
        Object itemData = null;
        if (mode == GlobalData.MODE_SELECT_JOB)
        {
            if (m_jobInfos != null)
            {
                itemTitle = m_jobInfos.get(position).jobName;
                itemData = m_jobInfos.get(position);
            }
        }
        else if (mode == GlobalData.MODE_SELECT_LOCATION)
        {
            if (m_locationInfos != null)
            {
                itemTitle = m_locationInfos.get(position).locName;
                itemData = m_locationInfos.get(position);
            }
        }
        else if (mode == GlobalData.MODE_SELECT_PRODUCT)
        {
            if (m_productInfos != null)
            {
                itemTitle = m_productInfos.get(position).productName + "(" + FSProduct.getDisplayProductType((int)m_productInfos.get(position).productType) + ")";
                itemData = m_productInfos.get(position);
            }
        }

        // Bind the data efficiently with the holder.
        ((TextView)convertView.findViewById(R.id.txtItemName)).setText(itemTitle);
        RelativeLayout rlRoot = ((RelativeLayout)convertView.findViewById(R.id.RLSelectViewItemRoot));
        rlRoot.setTag(itemData);
        rlRoot.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                mActivity.selectItem(v.getTag(), mode);
            }
        });

        return convertView;
    }
}