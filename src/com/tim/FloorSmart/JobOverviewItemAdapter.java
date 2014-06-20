package com.tim.FloorSmart;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.*;
import com.tim.FloorSmart.Database.FSLocProduct;
import com.tim.FloorSmart.Database.FSProduct;
import com.tim.FloorSmart.Global.CommonMethods;
import com.tim.FloorSmart.Global.GlobalData;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;

public class JobOverviewItemAdapter extends BaseExpandableListAdapter {
	Context			mContext = null;
	JobOverviewActivity mActivity = null;
    private LayoutInflater mInflater;

    private ArrayList<JobOverviewActivity.FSReportListNodeObject> m_HeaderInfos = new ArrayList<JobOverviewActivity.FSReportListNodeObject>();
    private HashMap<JobOverviewActivity.FSReportListNodeObject, ArrayList<Date>> m_ChildInfos;

    public JobOverviewItemAdapter(JobOverviewActivity activity, Context context) {
    	mContext = context;
    	mActivity = activity;
        // Cache the LayoutInflate to avoid asking for a new one each time.
        mInflater = LayoutInflater.from(context);
    }

    public void setData(ArrayList<JobOverviewActivity.FSReportListNodeObject> headerinfo, HashMap<JobOverviewActivity.FSReportListNodeObject, ArrayList<Date>> childinfo)
    {
        m_HeaderInfos = headerinfo;
        m_ChildInfos = childinfo;
    }

    @Override
    public int getGroupCount() {
        return m_HeaderInfos.size();
    }

    @Override
    public int getChildrenCount(int groupPosition) {
        return m_ChildInfos.get(this.m_HeaderInfos.get(groupPosition)).size();  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public Object getGroup(int groupPosition) {
        return m_HeaderInfos.get(groupPosition);
    }

    @Override
    public Object getChild(int groupPosition, int childPosition) {
        return this.m_ChildInfos.get(this.m_HeaderInfos.get(groupPosition))
                .get(childPosition);
    }

    @Override
    public long getGroupId(int groupPosition) {
        return groupPosition;
    }

    @Override
    public long getChildId(int groupPosition, int childPosition) {
        return childPosition;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public boolean hasStableIds() {
        return false;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public View getGroupView(int groupPosition, boolean isExpanded, View convertView, ViewGroup parent) {
        JobOverviewActivity.FSReportListNodeObject headerdata = (JobOverviewActivity.FSReportListNodeObject)getGroup(groupPosition);
        if (convertView == null)
        {
            LayoutInflater inflater = (LayoutInflater)this.mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.reportheader_item, null);
            ResolutionSet._instance.iterateChild(convertView.findViewById(R.id.RLReportHeaderItemRoot));
        }

        ((TextView)convertView.findViewById(R.id.txtLocation)).setText(headerdata.loc.locName);
        ((TextView)convertView.findViewById(R.id.txtProduct)).setText(headerdata.locProduct.locProductName);
        ((TextView)convertView.findViewById(R.id.txtProductType)).setText(FSProduct.getDisplayProductType((int)headerdata.locProduct.locProductType));

        float convCoverage = (float)headerdata.locProduct.locProductCoverage;
        if (GlobalData.sharedData().settingArea == GlobalData.AREA_FEET)
        {
            convertView.findViewById(R.id.txtUnitM).setVisibility(View.INVISIBLE);
            convertView.findViewById(R.id.txtUnitFT).setVisibility(View.VISIBLE);
        }
        else
        {
            convertView.findViewById(R.id.txtUnitM).setVisibility(View.VISIBLE);
            convertView.findViewById(R.id.txtUnitFT).setVisibility(View.INVISIBLE);
            convCoverage = GlobalData.sharedData().sqft2sqm((float)headerdata.locProduct.locProductCoverage);
        }

        String strCoverage = String.format("%.2f", convCoverage);
        ((TextView)convertView.findViewById(R.id.txtCoverage)).setText(strCoverage);

        return convertView;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public View getChildView(int groupPosition, int childPosition, boolean isLastChild, View convertView, ViewGroup parent) {
        Date date = (Date)getChild(groupPosition, childPosition);

        if (convertView == null){
            LayoutInflater inflater = (LayoutInflater)this.mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.report_item, null);
            ResolutionSet._instance.iterateChild(convertView.findViewById(R.id.RLReportItemRoot));
        }

        ((TextView)convertView.findViewById(R.id.lblReadingDate)).setText(String.format("Date: %s", CommonMethods.date2str(date, GlobalData.sharedData().settingDateFormat)));

        return convertView;
    }

    @Override
    public boolean isChildSelectable(int groupPosition, int childPosition) {
        return true;  //To change body of implemented methods use File | Settings | File Templates.
    }
}