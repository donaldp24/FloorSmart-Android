package com.tim.FloorSmart;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Rect;
import android.os.Bundle;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import com.tim.FloorSmart.Database.*;
import com.tim.FloorSmart.Global.CommonDefs;
import com.tim.FloorSmart.Global.CommonMethods;
import com.tim.FloorSmart.Global.GlobalData;
import com.tim.FloorSmart.Scan.ScanManager;

import java.util.Date;

public class ReadingActivity extends Activity{

    private ScanManager manager;

    RelativeLayout mainLayout;
    boolean bInitialized = false;

    Date curDate;
    FSLocProduct _curLocProduct;

    /**
     * Called when the activity is first created.
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.readings);

        mainLayout = (RelativeLayout)findViewById(R.id.RLReadingRoot);
        mainLayout.getViewTreeObserver().addOnGlobalLayoutListener(
                new ViewTreeObserver.OnGlobalLayoutListener() {
                    public void onGlobalLayout() {
                        if (bInitialized == false)
                        {
                            Rect r = new Rect();
                            mainLayout.getLocalVisibleRect(r);
                            ResolutionSet._instance.setResolution(r.width(), r.height(), true);
                            ResolutionSet._instance.iterateChild(findViewById(R.id.RLReadingRoot));
                            bInitialized = true;
                        }
                    }
                }
        );

        ImageView imgJobs = (ImageView)findViewById(R.id.imgJobs);
        imgJobs.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(ReadingActivity.this, JobsActivity.class));
                overridePendingTransition(R.anim.right_in, R.anim.left_out);
                finish();
            }
        });

        ImageView imgProducts = (ImageView)findViewById(R.id.imgProducts);
        imgProducts.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(ReadingActivity.this, ProductsActivity.class));
                overridePendingTransition(R.anim.right_in, R.anim.left_out);
                finish();
            }
        });

        ImageView imgReport = (ImageView)findViewById(R.id.imgReports);
        imgReport.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent reportIntent = new Intent(ReadingActivity.this, JobsActivity.class);
                reportIntent.putExtra(CommonDefs.ACTIVITY_TAG_REPORT, CommonDefs.ACTIVITY_REPORTS);
                startActivity(reportIntent);

                //startActivity(new Intent(RecordActivity.this, ReportsActivity.class));
                overridePendingTransition(R.anim.right_in, R.anim.left_out);
                finish();
            }
        });

        ImageView imgSettings = (ImageView)findViewById(R.id.imgSettings);
        imgSettings.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(ReadingActivity.this, SettingsActivity.class));
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
    }

    @Override
    protected void onStart()
    {
        super.onStart();

        Date now = new Date();
        GlobalData globalData = GlobalData.sharedData();
        if (curDate == null || CommonMethods.compareOnlyDate(curDate, now) == 0) // check today
        {
            ((TextView)findViewById(R.id.lblTitle)).setText(getString(R.string.record_curreading_title));
        }
        else
        {
            ((TextView)findViewById(R.id.lblTitle)).setText(String.format("Readings (%s)", CommonMethods.date2str(curDate, globalData.settingDateFormat)));
        }

        float coverage = 0;
        if (_curLocProduct != null)
        {
            FSLocation loc = DataManager.sharedInstance(ReadingActivity.this).getLocationFromID(_curLocProduct.locProductLocID);
            if (loc == null)
                return;

            FSJob job = DataManager.sharedInstance(ReadingActivity.this).getJobFromID(loc.locJobID);
            if (job == null)
                return;

            String jobName = job.jobName;
            String locName = loc.locName;
            String procName = _curLocProduct.locProductName;

            ((TextView)findViewById(R.id.txtJobName)).setText(jobName);
            ((TextView)findViewById(R.id.txtLocation)).setText(getString(R.string.record_curreading_location) + locName);
            ((TextView)findViewById(R.id.txtProduct)).setText(R.string.record_curreading_product + String.format("%s (%s)", procName, FSProduct.getDisplayProductType((int)_curLocProduct.locProductType)));

            coverage = (float)_curLocProduct.locProductCoverage;
        }
        else
        {
            ((TextView)findViewById(R.id.txtJobName)).setText("");
            ((TextView)findViewById(R.id.txtLocation)).setText(getString(R.string.record_curreading_location));
            ((TextView)findViewById(R.id.txtProduct)).setText(R.string.record_curreading_product);

            coverage = 0;
        }

        float conCoverage = coverage;
        if (globalData.settingArea == GlobalData.AREA_FEET)
        {
            ((TextView)findViewById(R.id.txtUnitFT)).setVisibility(View.VISIBLE);
            ((TextView)findViewById(R.id.txtUnitM)).setVisibility(View.INVISIBLE);
        }
        else
        {
            ((TextView)findViewById(R.id.txtUnitFT)).setVisibility(View.INVISIBLE);
            ((TextView)findViewById(R.id.txtUnitM)).setVisibility(View.VISIBLE);

            conCoverage = globalData.sqft2sqm(coverage);
        }

        String strCoverage = String.format("%.2f", conCoverage);
        if (_curLocProduct != null)
        {

        }
        else
        {
            strCoverage = "";
            ((TextView)findViewById(R.id.txtUnitFT)).setVisibility(View.INVISIBLE);
            ((TextView)findViewById(R.id.txtUnitM)).setVisibility(View.INVISIBLE);
            ((TextView)findViewById(R.id.txtUnit2)).setVisibility(View.INVISIBLE);
        }

        ((TextView)findViewById(R.id.txtCoverage)).setText(strCoverage);

        //initDateTable();
    }
}
