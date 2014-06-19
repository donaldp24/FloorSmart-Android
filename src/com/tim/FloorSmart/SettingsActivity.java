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

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

public class SettingsActivity extends Activity{

    private ScanManager manager;

    RelativeLayout mainLayout;
    boolean bInitialized = false;
    /**
     * Called when the activity is first created.
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.settings);

        mainLayout = (RelativeLayout)findViewById(R.id.RLSettingsRoot);
        mainLayout.getViewTreeObserver().addOnGlobalLayoutListener(
                new ViewTreeObserver.OnGlobalLayoutListener() {
                    public void onGlobalLayout() {
                        if (bInitialized == false)
                        {
                            Rect r = new Rect();
                            mainLayout.getLocalVisibleRect(r);
                            ResolutionSet._instance.setResolution(r.width(), r.height(), true);
                            ResolutionSet._instance.iterateChild(findViewById(R.id.RLSettingsRoot));
                            bInitialized = true;
                        }
                    }
                }
        );

        ImageView imgRecord = (ImageView)findViewById(R.id.imgRecord);
        imgRecord.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(SettingsActivity.this, RecordActivity.class));
                overridePendingTransition(R.anim.left_in, R.anim.right_out);
                finish();
            }
        });

        ImageView imgJob = (ImageView)findViewById(R.id.imgJobs);
        imgJob.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(SettingsActivity.this, JobsActivity.class));
                overridePendingTransition(R.anim.left_in, R.anim.right_out);
                finish();
            }
        });

        ImageView imgProduct = (ImageView)findViewById(R.id.imgProducts);
        imgProduct.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(SettingsActivity.this, ProductsActivity.class));
                overridePendingTransition(R.anim.left_in, R.anim.right_out);
                finish();
            }
        });

        ImageView imgReports = (ImageView)findViewById(R.id.imgReports);
        imgReports.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent reportIntent = new Intent(SettingsActivity.this, JobsActivity.class);
                reportIntent.putExtra(CommonDefs.ACTIVITY_TAG_REPORT, CommonDefs.ACTIVITY_REPORTS);
                startActivity(reportIntent);

                //startActivity(new Intent(SettingsActivity.this, ReportsActivity.class));
                overridePendingTransition(R.anim.left_in, R.anim.right_out);
                finish();
            }
        });

        // Option Initialize
        Button btnTempF = (Button)findViewById(R.id.btnTempF);
        Button btnTempC = (Button)findViewById(R.id.btnTempC);

        Button btnAreaFt = (Button)findViewById(R.id.btnAreaFT);
        Button btnAreaM = (Button)findViewById(R.id.btnAreaM);

        RelativeLayout rlTimeUS = (RelativeLayout)findViewById(R.id.RLUSStandard);
        RelativeLayout rlEuropean = (RelativeLayout)findViewById(R.id.RLEuropean);

        GlobalData _sharedData = GlobalData.sharedData();
        if (_sharedData.settingTemp == GlobalData.TEMP_FAHRENHEIT)
        {
            btnTempF.setBackgroundResource(R.drawable.bg_f_sl);
            btnTempC.setBackgroundResource(R.drawable.bg_c_nl);
        }
        else
        {
            btnTempF.setBackgroundResource(R.drawable.bg_f_nl);
            btnTempC.setBackgroundResource(R.drawable.bg_c_sl);
        }

        if (_sharedData.settingArea == GlobalData.AREA_FEET)
        {
            btnAreaFt.setBackgroundResource(R.drawable.bg_ft_sl);
            btnAreaM.setBackgroundResource(R.drawable.bg_m_nl);
        }
        else
        {
            btnAreaFt.setBackgroundResource(R.drawable.bg_ft_nl);
            btnAreaM.setBackgroundResource(R.drawable.bg_m_sl);
        }

        if (_sharedData.settingDateFormat.equals(GlobalData.DATEFORMAT_EU))
        {
            rlTimeUS.setBackgroundResource(R.drawable.bt_ustime);
            rlEuropean.setBackgroundResource(R.drawable.bt_eurotime_selected);
        }
        else
        {
            rlTimeUS.setBackgroundResource(R.drawable.bt_ustime_selected);
            rlEuropean.setBackgroundResource(R.drawable.bt_eurotime);
        }

        SimpleDateFormat formatUs = new SimpleDateFormat(GlobalData.DATEFORMAT_US);
        SimpleDateFormat formatEu = new SimpleDateFormat(GlobalData.DATEFORMAT_EU);

        Date date = new Date();
        ((TextView)findViewById(R.id.txtUSDateTime)).setText(formatUs.format(date));
        ((TextView)findViewById(R.id.txtEuDateTime)).setText(formatEu.format(date));

        // Option Events
        btnTempF.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                changeTemperature(GlobalData.TEMP_FAHRENHEIT);
            }
        });

        btnTempC.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                changeTemperature(GlobalData.TEMP_CELSIUS);
            }
        });

        btnAreaFt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                changeArea(GlobalData.AREA_FEET);
            }
        });

        btnAreaM.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                changeArea(GlobalData.AREA_METER);
            }
        });

        rlTimeUS.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                changeDateTimeFormat(GlobalData.DATEFORMAT_US);
            }
        });

        rlEuropean.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                changeDateTimeFormat(GlobalData.DATEFORMAT_EU);
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

    private void changeTemperature(int value)
    {
        Button btnTempF = (Button)findViewById(R.id.btnTempF);
        Button btnTempC = (Button)findViewById(R.id.btnTempC);

        GlobalData.sharedData().setSettingTemp(value);
        if (value == GlobalData.TEMP_FAHRENHEIT)
        {
            btnTempF.setBackgroundResource(R.drawable.bg_f_sl);
            btnTempC.setBackgroundResource(R.drawable.bg_c_nl);
        }
        else
        {
            btnTempF.setBackgroundResource(R.drawable.bg_f_nl);
            btnTempC.setBackgroundResource(R.drawable.bg_c_sl);
        }
    }

    private void changeArea(int value)
    {
        Button btnAreaFt = (Button)findViewById(R.id.btnAreaFT);
        Button btnAreaM = (Button)findViewById(R.id.btnAreaM);

        GlobalData.sharedData().setSettingArea(value);
        if (value == GlobalData.AREA_FEET)
        {
            btnAreaFt.setBackgroundResource(R.drawable.bg_ft_sl);
            btnAreaM.setBackgroundResource(R.drawable.bg_m_nl);
        }
        else
        {
            btnAreaFt.setBackgroundResource(R.drawable.bg_ft_nl);
            btnAreaM.setBackgroundResource(R.drawable.bg_m_sl);
        }
    }

    private void changeDateTimeFormat(String dateFormat)
    {
        RelativeLayout rlTimeUS = (RelativeLayout)findViewById(R.id.RLUSStandard);
        RelativeLayout rlEuropean = (RelativeLayout)findViewById(R.id.RLEuropean);

        GlobalData.sharedData().setSettingDateFormat(dateFormat);
        if (dateFormat.equals(GlobalData.DATEFORMAT_US))
        {
            rlTimeUS.setBackgroundResource(R.drawable.bt_ustime_selected);
            rlEuropean.setBackgroundResource(R.drawable.bt_eurotime);
        }
        else
        {
            rlTimeUS.setBackgroundResource(R.drawable.bt_ustime);
            rlEuropean.setBackgroundResource(R.drawable.bt_eurotime_selected);
        }
    }
}
