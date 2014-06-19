package com.tim.FloorSmart;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Rect;
import android.os.Bundle;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.*;
import com.tim.FloorSmart.Database.*;
import com.tim.FloorSmart.Global.CommonDefs;
import com.tim.FloorSmart.Global.GlobalData;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import android.widget.ExpandableListView.OnChildClickListener;
import android.widget.ExpandableListView.OnGroupClickListener;
import android.widget.ExpandableListView.OnGroupCollapseListener;
import android.widget.ExpandableListView.OnGroupExpandListener;

public class JobOverviewActivity extends Activity{

    RelativeLayout mainLayout;
    boolean bInitialized = false;

    ArrayList<FSReportListNodeObject> arrayJob;
    HashMap<JobOverviewActivity.FSReportListNodeObject, ArrayList<Date>> childdata;
    FSJob curJob;

    ExpandableListView listView;
    JobOverviewItemAdapter adapter;

    /**
     * Called when the activity is first created.
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.reports);

        long job_id = getIntent().getLongExtra(CommonDefs.ACTIVITY_TAG_JOBID, 0);
        if (job_id != 0)
            curJob = DataManager.sharedInstance(JobOverviewActivity.this).getJobFromID(job_id);

        mainLayout = (RelativeLayout)findViewById(R.id.RLJobsRoot);
        mainLayout.getViewTreeObserver().addOnGlobalLayoutListener(
                new ViewTreeObserver.OnGlobalLayoutListener() {
                    public void onGlobalLayout() {
                        if (bInitialized == false)
                        {
                            Rect r = new Rect();
                            mainLayout.getLocalVisibleRect(r);
                            ResolutionSet._instance.setResolution(r.width(), r.height(), true);
                            ResolutionSet._instance.iterateChild(findViewById(R.id.RLJobsRoot));
                            bInitialized = true;

                            initListView();
                        }
                    }
                }
        );

        ImageView imgRecord = (ImageView)findViewById(R.id.imgRecord);
        imgRecord.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(JobOverviewActivity.this, RecordActivity.class));
                overridePendingTransition(R.anim.left_in, R.anim.right_out);
                finish();
            }
        });

        ImageView imgProduct = (ImageView)findViewById(R.id.imgProducts);
        imgProduct.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(JobOverviewActivity.this, ProductsActivity.class));
                overridePendingTransition(R.anim.right_in, R.anim.left_out);
                finish();
            }
        });

        ImageView imgReport = (ImageView)findViewById(R.id.imgReports);
        imgReport.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent reportIntent = new Intent(JobOverviewActivity.this, JobsActivity.class);
                reportIntent.putExtra(CommonDefs.ACTIVITY_TAG_REPORT, CommonDefs.ACTIVITY_REPORTS);
                startActivity(reportIntent);

                overridePendingTransition(R.anim.right_in, R.anim.left_out);
                finish();
            }
        });

        ImageView imgSettings = (ImageView)findViewById(R.id.imgSettings);
        imgSettings.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(JobOverviewActivity.this, SettingsActivity.class));
                overridePendingTransition(R.anim.right_in, R.anim.left_out);
                finish();
            }
        });

        RelativeLayout rlBack = (RelativeLayout)findViewById(R.id.RLBack);
        rlBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        RelativeLayout rlPrint = ((RelativeLayout)findViewById(R.id.RLPrint));
        rlPrint.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Do Print
            }
        });
    }

    private void initListView()
    {
        arrayJob = new ArrayList<FSReportListNodeObject>();
        childdata = new HashMap<FSReportListNodeObject, ArrayList<Date>>();

        ((TextView)findViewById(R.id.lblJobName)).setText("");

        if (curJob != null)
        {
            ((TextView)findViewById(R.id.lblJobName)).setText(curJob.jobName);
            ArrayList<FSLocation> arrayLocation = DataManager.sharedInstance(JobOverviewActivity.this).getLocations(curJob.jobID, true);
            for (int i = 0; i < arrayLocation.size(); i++)
            {
                FSLocation loc = arrayLocation.get(i);
                ArrayList<FSLocProduct> arrayLocProduct = DataManager.sharedInstance(JobOverviewActivity.this).getLocProducts(loc, "", true);

                for (int j = 0; j < arrayLocProduct.size(); j++)
                {
                    FSLocProduct locProduct = arrayLocProduct.get(j);
                    ArrayList<Date> arrayReadingDates = DataManager.sharedInstance(JobOverviewActivity.this).getAllReadingDates(locProduct.locProductID);
                    ArrayList<Date> arrayReadingDatesHasData = new ArrayList<Date>();

                    for (int k = 0; k < arrayReadingDates.size(); k++)
                    {
                        Date date = arrayReadingDates.get(k);
                        if (DataManager.sharedInstance(JobOverviewActivity.this).getReadingsCount(locProduct.locProductID, date) > 0)
                        {
                            arrayReadingDatesHasData.add(date);
                        }
                    }

                    if (arrayReadingDatesHasData.size() > 0)
                    {
                        FSReportListNodeObject node = new FSReportListNodeObject();
                        node.loc = loc;
                        node.locProduct = locProduct;
                        node.arrayDates = arrayReadingDates;
                        arrayJob.add(node);

                        childdata.put(node, arrayReadingDates);
                    }
                }
            }
        }

        if (arrayJob.size() == 0)
        {
            ((RelativeLayout)findViewById(R.id.RLPrint)).setVisibility(View.INVISIBLE);
        }
        else
        {
            ((RelativeLayout)findViewById(R.id.RLPrint)).setVisibility(View.VISIBLE);
        }

        listView = (ExpandableListView)findViewById(R.id.listView);
        adapter = new JobOverviewItemAdapter(this, this);
        adapter.setData(arrayJob, childdata);

        listView.setOnChildClickListener(new ExpandableListView.OnChildClickListener(){
            @Override
            public boolean onChildClick(ExpandableListView parent, View v, int groupPosition, int childPosition, long id){
                FSReportListNodeObject node = arrayJob.get(groupPosition);
                Intent readingIntent = new Intent(JobOverviewActivity.this, ReadingActivity.class);
                readingIntent.putExtra(CommonDefs.ACTIVITY_TAG_LOC_PRODUCT_ID, node.locProduct.locProductID);
                readingIntent.putExtra(CommonDefs.ACTIVITY_TAG_DATE, node.arrayDates.get(childPosition).getTime());
                startActivity(readingIntent);
                return false;
            }
        });

        listView.setAdapter(adapter);
    }

    public class FSReportListNodeObject
    {
        public FSLocation loc;
        public FSLocProduct locProduct;
        public ArrayList<Date> arrayDates;
    }
}
