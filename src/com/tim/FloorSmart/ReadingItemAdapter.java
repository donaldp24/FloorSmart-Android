package com.tim.FloorSmart;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.RelativeLayout;
import android.widget.TextView;
import com.tim.FloorSmart.Database.FSJob;
import com.tim.FloorSmart.Database.FSLocation;
import com.tim.FloorSmart.Database.FSProduct;
import com.tim.FloorSmart.Database.FSReading;
import com.tim.FloorSmart.Global.CommonMethods;
import com.tim.FloorSmart.Global.GlobalData;

import java.util.ArrayList;

public class ReadingItemAdapter extends BaseAdapter {
	Context			mContext = null;
	ReadingActivity mActivity = null;
    private LayoutInflater mInflater;

    private ArrayList<FSReading> m_readingInfos = new ArrayList<FSReading>();

    public ReadingItemAdapter(ReadingActivity activity, Context context) {
    	mContext = context;
    	mActivity = activity;
        // Cache the LayoutInflate to avoid asking for a new one each time.
        mInflater = LayoutInflater.from(context);
    }

    public void setData(ArrayList<FSReading> readingData)
    {
        m_readingInfos = readingData;
    }

    /**
     * The number of items in the list is determined by the number of speeches
     * in our array.
     *
     * @see android.widget.ListAdapter#getCount()
     */
    public int getCount() {
        if (m_readingInfos == null)
            return 0;

        return m_readingInfos.size();
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
            convertView = mInflater.inflate(R.layout.measure_item, null);
            ResolutionSet._instance.iterateChild(convertView.findViewById(R.id.RLMeasureItemRoot));
        } else {
        }

        FSReading item = null;
        if (m_readingInfos != null)
        {
            item = m_readingInfos.get(position);
        }

        if (item != null)
        {
            ((TextView)convertView.findViewById(R.id.lblTime)).setText(String.format("%s hrs", CommonMethods.date2str(item.readTimestamp, "HH:mm")));
            //((TextView)convertView.findViewById(R.id.lblTime)).setText(String.format("%s hrs", CommonMethods.date2str(item.readTimestamp, "mm:ss")));

            ((TextView)convertView.findViewById(R.id.lblMC)).setText(String.format("%s", item.getDisplayRealMCValue()));
            ((TextView)convertView.findViewById(R.id.lblEMC)).setText(String.format("%.1f", item.getEmcValue()));
            ((TextView)convertView.findViewById(R.id.lblRH)).setText(String.format("%d", Math.round(item.readConvRH)));

            float temp = (float)item.readConvTemp;
            if (GlobalData.sharedData().settingTemp == GlobalData.TEMP_FAHRENHEIT)
                temp = FSReading.getFTemperature(temp);

            ((TextView)convertView.findViewById(R.id.lblTemperature)).setText(String.format("%d", Math.round(temp)));
        }
        else
        {
            ((TextView)convertView.findViewById(R.id.lblTime)).setText("");
            ((TextView)convertView.findViewById(R.id.lblMC)).setText("");
            ((TextView)convertView.findViewById(R.id.lblEMC)).setText("");
            ((TextView)convertView.findViewById(R.id.lblRH)).setText("");
            ((TextView)convertView.findViewById(R.id.lblTemperature)).setText("");
        }

        RelativeLayout rlMainRoot = (RelativeLayout)convertView.findViewById(R.id.RLMeasureItemRoot);
        if (position % 2 == 0)
            rlMainRoot.setBackgroundColor(Color.argb(255, 193, 182, 169));
        else
            rlMainRoot.setBackgroundColor(Color.argb(255, 164, 149, 130));

        // Bind the data efficiently with the holder.
        RelativeLayout rlDelete = ((RelativeLayout)convertView.findViewById(R.id.RLDelete));
        rlDelete.setTag(position);
        rlDelete.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                mActivity.deleteItem((Integer)v.getTag());
            }
        });

        return convertView;
    }
}