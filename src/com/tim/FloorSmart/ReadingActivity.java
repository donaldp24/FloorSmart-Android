package com.tim.FloorSmart;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Rect;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.*;
import com.google.analytics.tracking.android.EasyTracker;
import com.tim.FloorSmart.Database.*;
import com.tim.FloorSmart.Global.CommonDefs;
import com.tim.FloorSmart.Global.CommonMethods;
import com.tim.FloorSmart.Global.GlobalData;
import com.tim.FloorSmart.Scan.ScanManager;

import java.util.ArrayList;
import java.util.Date;

public class ReadingActivity extends BaseActivity {

    private ScanManager manager;

    RelativeLayout mainLayout;
    boolean bInitialized = false;

    Date curDate;
    FSLocProduct _curLocProduct = null;
    boolean isFromRecorded = false;

    ArrayList<FSReading> arrOverallreadings;

    ListView listView;
    ReadingItemAdapter adapter;

    //boolean bDeleteFlag = false;
    /**
     * Called when the activity is first created.
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.readings);

        long loc_id = getIntent().getLongExtra(CommonDefs.ACTIVITY_TAG_LOC_PRODUCT_ID, 0);
        if (loc_id != 0)
            _curLocProduct = DataManager.sharedInstance().getLocProductWithID(loc_id);
        isFromRecorded = getIntent().getBooleanExtra(CommonDefs.ACTIVITY_TAG_FRRECORD, false);

        long dateValue = getIntent().getLongExtra(CommonDefs.ACTIVITY_TAG_DATE, 0);
        if (dateValue != 0)
            curDate = new Date(dateValue);

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

                            listView = ((ListView)findViewById(R.id.listView));
                            listView.setDivider(new ColorDrawable(Color.TRANSPARENT));
                            listView.setCacheColorHint(Color.TRANSPARENT);
                            listView.setDividerHeight(5);

                            adapter = new ReadingItemAdapter(ReadingActivity.this, ReadingActivity.this);
                            initDateTable();

                            if (isFromRecorded)
                            {
                                scrolltoEndList();
                                isFromRecorded = false;
                                showWarning();
                            }
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

            ((TextView)findViewById(R.id.txtName)).setText(jobName);
            ((TextView)findViewById(R.id.txtLocation)).setText(getString(R.string.record_curreading_location) + " " + locName);
            ((TextView)findViewById(R.id.txtProduct)).setText(getString(R.string.record_curreading_product) + String.format(" %s (%s)", procName, FSProduct.getDisplayProductType((int)_curLocProduct.locProductType)));

            coverage = (float)_curLocProduct.locProductCoverage;
        }
        else
        {
            ((TextView)findViewById(R.id.txtName)).setText("");
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
        EasyTracker.getInstance(ReadingActivity.this).activityStart(this); // Add this method.
    }

    @Override
    public void onStop() {
        super.onStop();
        EasyTracker.getInstance(ReadingActivity.this).activityStop(this); // Add this method.
    }

    @Override
    protected void onDestroy()
    {
        super.onDestroy();
    }

    public void initDateTable()
    {
        if (curDate == null)
            curDate = new Date();

        if (_curLocProduct == null)
        {
            arrOverallreadings = new ArrayList<FSReading>();
            setCurData(null);
            setOverallData();
        }
        else
        {
            arrOverallreadings = DataManager.sharedInstance(ReadingActivity.this).getReadings(_curLocProduct.locProductID, curDate);
            if (arrOverallreadings.size() > 0 )
            {
                setCurData(arrOverallreadings.get(arrOverallreadings.size() - 1));
            }

            setOverallData();
        }

        adapter.setData(arrOverallreadings);
        listView.setAdapter(adapter);
    }

    public void scrolltoEndList()
    {
        listView.post(new Runnable() {
            @Override
            public void run() {
                // Select the last row so it will scroll into view...
                listView.setSelection(adapter.getCount() - 1);
            }
        });
    }

    private void setCurData(FSReading data)
    {
        GlobalData globalData = GlobalData.sharedData();
        if (data != null)
        {
            ((TextView)findViewById(R.id.txtlastReading)).setText(String.format("Last Reading : %s %s", CommonMethods.date2str(data.readTimestamp, globalData.settingDateFormat), CommonMethods.date2str(data.readTimestamp, "HH:mm")));
            ((TextView)findViewById(R.id.txtMainRH)).setText(String.format("RH : %d%%", Math.round(data.readConvRH)));
            String tempUnit = globalData.getTempUnit();
            float temp = (float)data.readConvTemp;
            if (globalData.settingTemp == GlobalData.TEMP_FAHRENHEIT)
                temp = FSReading.getFTemperature(temp);

            ((TextView)findViewById(R.id.txtMainTemp)).setText(String.format("Temp : %d%s", Math.round(temp), tempUnit));
            ((TextView)findViewById(R.id.txtMainBattery)).setText(String.format("Battery : %d%%", data.readBattery));
            ((TextView)findViewById(R.id.txtMainDepth)).setText(String.format("Depth : %s", FSReading.getDisplayDepth(data.readDepth)));
            ((TextView)findViewById(R.id.txtMainMaterial)).setText(String.format("Material : %s", FSReading.getDisplayMaterial(data.readMaterial)));
            ((TextView)findViewById(R.id.txtMainSG)).setText(String.format("s.g. : %d", data.readGravity));
            ((TextView)findViewById(R.id.txtMainMC)).setText(String.format("MC : %s%%", data.getDisplayRealMCValue()));
            ((TextView)findViewById(R.id.txtMainEMC)).setText(String.format("EMC : %.1f%%", data.getEmcValue()));
        }
        else
        {
            ((TextView)findViewById(R.id.txtlastReading)).setText(String.format("Last Reading : %s", CommonMethods.date2str(curDate, globalData.settingDateFormat)));
            ((TextView)findViewById(R.id.txtMainRH)).setText("RH :");
            ((TextView)findViewById(R.id.txtMainTemp)).setText("Temp :");
            ((TextView)findViewById(R.id.txtMainBattery)).setText("Battery :");
            ((TextView)findViewById(R.id.txtMainDepth)).setText("Depth :");
            ((TextView)findViewById(R.id.txtMainMaterial)).setText("Material :");
            ((TextView)findViewById(R.id.txtMainSG)).setText("s.g. :");
            ((TextView)findViewById(R.id.txtMainMC)).setText("MC :");
            ((TextView)findViewById(R.id.txtMainEMC)).setText("EMC :");
        }
    }

    private void setOverallData()
    {
        if (arrOverallreadings == null || arrOverallreadings.size() == 0)
        {
            float mcValue = 0;
            int rh = 0;
            int temp = 0;

            ((TextView)findViewById(R.id.txtMCAVG)).setText(String.format("MC Avg: %.1f%s", mcValue, "%"));
            ((TextView)findViewById(R.id.txtMCHigh)).setText(String.format("MC High: %.1f%s", mcValue, "%"));
            ((TextView)findViewById(R.id.txtMCLow)).setText(String.format("MC Low: %.1f%s", mcValue, "%"));
            ((TextView)findViewById(R.id.txtEMCAVG)).setText(String.format("EMC Avg: 0%s", "%"));
            ((TextView)findViewById(R.id.txtRHAVG)).setText(String.format("RH Avg: %d%s", (int)rh, "%"));
            ((TextView)findViewById(R.id.txtTempAVG)).setText(String.format("Temp Avg: %d", (int)temp));
        }
        else
        {
            float mcavg = FSReading.getMCAvg(arrOverallreadings);
            float mchigh = FSReading.getMCMax(arrOverallreadings);
            float mclow = FSReading.getMCMin(arrOverallreadings);
            float rhavg = FSReading.getRHAvg(arrOverallreadings);
            float tempavg = FSReading.getTempAvg(arrOverallreadings);
            float emcavg = FSReading.getEmcAvg(arrOverallreadings);

            ((TextView)findViewById(R.id.txtMCAVG)).setText(String.format("MC Avg: %.1f%s", mcavg, "%"));
            ((TextView)findViewById(R.id.txtMCHigh)).setText(String.format("MC High: %.1f%s", mchigh, "%"));
            ((TextView)findViewById(R.id.txtMCLow)).setText(String.format("MC Low: %.1f%s", mclow, "%"));
            ((TextView)findViewById(R.id.txtEMCAVG)).setText(String.format("EMC Avg: %.1f%s", emcavg, "%"));
            ((TextView)findViewById(R.id.txtRHAVG)).setText(String.format("RH Avg: %d%s", Math.round(rhavg), "%"));

            String tempUnit = GlobalData.sharedData().getTempUnit();
            if (GlobalData.sharedData().settingTemp == GlobalData.TEMP_FAHRENHEIT)
                tempavg = FSReading.getFTemperature(tempavg);

            ((TextView)findViewById(R.id.txtTempAVG)).setText(String.format("Temp Avg: %d%s", Math.round(tempavg), tempUnit));
        }
    }

    public void deleteItem(int position)
    {
        FSReading item = arrOverallreadings.get(position);
        String label = String.format("MC:%s%%(%s hrs)", item.getDisplayRealMCValue(), CommonMethods.date2str(item.readTimestamp, "HH:mm"));

        final DeleteReadingDialog confirmDialog = new DeleteReadingDialog(this, R.style.NoTitleDialog, label);
        Button btnOK = (Button)confirmDialog.findViewById(R.id.btnOK);
        btnOK.setTag(position);
        btnOK.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View v) {
                confirmDialog.dismiss();

                FSReading item = arrOverallreadings.get((Integer)v.getTag());
                if (item != null)
                    DataManager.sharedInstance(ReadingActivity.this).deleteReadingFromDatabase(item);

                initDateTable();
            }

        });

        Button btnCancel = (Button)confirmDialog.findViewById(R.id.btnCancel);
        btnCancel.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                confirmDialog.dismiss();
            }

        });

        confirmDialog.show();
    }

    public void showWarning()
    {
        if (arrOverallreadings.size() >= 2)
        {
            FSReading prevReading = arrOverallreadings.get(arrOverallreadings.size() - 2);
            FSReading lastReading = arrOverallreadings.get(arrOverallreadings.size() - 1);

            boolean isShowMsg = false;
            String titleText = "";
            String bodyText = "";
            if (prevReading.readGravity != lastReading.readGravity && prevReading.readDepth != lastReading.readDepth)
            {
                titleText = "s.g. and Depth are changed\n";
                bodyText = String.format(" s.g. : %d => %d \n depth : %s => %s", prevReading.readGravity, lastReading.readGravity, FSReading.getDisplayDepth(prevReading.readDepth), FSReading.getDisplayDepth(lastReading.readDepth));
                isShowMsg = true;
            }
            else if (prevReading.readGravity != lastReading.readGravity)
            {
                titleText = "s.g. is changed\n";
                bodyText = String.format("\n s.g. : %d => %d", prevReading.readGravity, lastReading.readGravity);
                isShowMsg = true;
            }
            else if (prevReading.readDepth != lastReading.readDepth)
            {
                titleText = "Depth is changed\n";
                bodyText = String.format("\n depth : %s => %s", FSReading.getDisplayDepth(prevReading.readDepth), FSReading.getDisplayDepth(lastReading.readDepth));
                isShowMsg = true;
            }

            if (isShowMsg)
            {
                // Show Message Box
                WarningAlertDialog alertDlg = new WarningAlertDialog(ReadingActivity.this, R.style.NoTitleDialog, titleText, bodyText);
                alertDlg.show();
            }
        }
        else
        {
            int count = DataManager.sharedInstance(ReadingActivity.this).getReadingsCount(_curLocProduct.locProductID);
            if (count == 1)
            {
                String titleText = "First reading for this Product!\n";
                String bodyText = "";
                if (_curLocProduct.locProductType == FSProduct.FSPRODUCTTYPE_FINISHED)
                {
                    bodyText = String.format(" NWFA Guidelines require 40 readings \n per 1000sqft for finished material");
                }
                else
                {
                    bodyText = String.format(" NWFA Guidelines require 20 readings \n per 1000sqft for subfloor material");
                }

                // Show Message Box
                WarningAlertDialog alertDlg = new WarningAlertDialog(ReadingActivity.this, R.style.NoTitleDialog, titleText, bodyText);
                alertDlg.show();
            }
        }
    }

    @Override
    protected void onResume()
    {
        super.onResume();

        if (GlobalData.bFromRecord == true)
        {
            if (bInitialized == true)
            {
                initDateTable();
                scrolltoEndList();
                showWarning();
            }
        }

        GlobalData.bFromRecord = false;
    }
}
