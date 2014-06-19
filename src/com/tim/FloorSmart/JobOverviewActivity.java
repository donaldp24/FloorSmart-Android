package com.tim.FloorSmart;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Rect;
import android.os.Bundle;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import com.tim.FloorSmart.Global.CommonDefs;
import com.tim.FloorSmart.Global.GlobalData;
import com.tim.FloorSmart.Scan.ScanManager;

import java.text.SimpleDateFormat;
import java.util.Date;

public class JobOverviewActivity extends Activity{

    private ScanManager manager;

    RelativeLayout mainLayout;
    boolean bInitialized = false;
    /**
     * Called when the activity is first created.
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.reports);

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
    }

    @Override
    protected void onResume()
    {
        super.onResume();

        // Initialize Global
        if (GlobalData.sharedData() == null)
            GlobalData.initialize(this);
    }
}
