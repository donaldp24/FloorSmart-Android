package com.tim.FloorSmart;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Rect;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.ViewTreeObserver;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.*;
import com.tim.FloorSmart.Database.DataManager;
import com.tim.FloorSmart.Database.FSJob;
import com.tim.FloorSmart.Database.FSLocation;
import com.tim.FloorSmart.Global.CommonDefs;
import com.tim.FloorSmart.Global.GlobalData;

import java.util.ArrayList;

public class LocationsActivity extends BaseActivity{
    RelativeLayout mainLayout;
    boolean bInitialized = false;

    ListView listLocations;
    LocationItemAdapter locationAdapter;
    ArrayList<FSLocation> arrLocationNames;

    boolean bFromRecord = false;

    FSJob _curJob;
    FSLocation _curLoc;

    /**
     * Called when the activity is first created.
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.locations);

        Intent curIntent = getIntent();
        int jobid = -1;
        jobid = curIntent.getIntExtra(CommonDefs.ACTIVITY_TAG_JOBID, -1);
        bFromRecord = (getIntent().getIntExtra(CommonDefs.ACTIVITY_TAG_FRRECORD, CommonDefs.ACTIVITY_FROM_SPLASH) == CommonDefs.ACTIVITY_FROM_RECORD);

        if (jobid != -1)
        {
            _curJob = DataManager.sharedInstance(LocationsActivity.this).getJobFromID(jobid);
        }

        FSLocation defaultLoc = new FSLocation();
        defaultLoc.locID = 0;
        defaultLoc.locJobID = _curJob.jobID;
        defaultLoc.locName = "Default";
        _curLoc = defaultLoc;

        ((TextView)findViewById(R.id.txtCurJobName)).setText(_curJob.jobName);

        mainLayout = (RelativeLayout)findViewById(R.id.RLLocationsRoot);
        mainLayout.getViewTreeObserver().addOnGlobalLayoutListener(
                new ViewTreeObserver.OnGlobalLayoutListener() {
                    public void onGlobalLayout() {
                        if (bInitialized == false)
                        {
                            Rect r = new Rect();
                            mainLayout.getLocalVisibleRect(r);
                            ResolutionSet._instance.setResolution(r.width(), r.height(), true);
                            ResolutionSet._instance.iterateChild(findViewById(R.id.RLLocationsRoot));
                            bInitialized = true;

                            listLocations = (ListView)findViewById(R.id.listView);

                            listLocations.setDivider(new ColorDrawable(Color.TRANSPARENT));
                            listLocations.setCacheColorHint(Color.parseColor("#FFF1F1F1"));
                            listLocations.setDividerHeight(20);
                            locationAdapter = new LocationItemAdapter(LocationsActivity.this, getApplicationContext());

                            initTableData();
                        }
                    }
                }
        );

        ImageView imgJobs = (ImageView)findViewById(R.id.imgJobs);
        imgJobs.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(LocationsActivity.this, JobsActivity.class));
                overridePendingTransition(R.anim.right_in, R.anim.left_out);
                finish();
            }
        });

        ImageView imgProduct = (ImageView)findViewById(R.id.imgProducts);
        imgProduct.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(LocationsActivity.this, ProductsActivity.class));
                overridePendingTransition(R.anim.right_in, R.anim.left_out);
                finish();
            }
        });

        ImageView imgReport = (ImageView)findViewById(R.id.imgReports);
        imgReport.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent reportIntent = new Intent(LocationsActivity.this, LocationsActivity.class);
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
                startActivity(new Intent(LocationsActivity.this, SettingsActivity.class));
                overridePendingTransition(R.anim.right_in, R.anim.left_out);
                finish();
            }
        });

        Button btnLocAdd = (Button)findViewById(R.id.btnAddLocation);
        btnLocAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String locName = ((EditText)findViewById(R.id.txtLocationName)).getText().toString();

                if (locName.equals(""))
                {
                    Toast.makeText(LocationsActivity.this, "Please input location name to add!", Toast.LENGTH_SHORT).show();
                    return;
                }

                DataManager _instance = DataManager.sharedInstance(LocationsActivity.this);
                if (_instance.isExistSameLocation(_curJob.jobID, locName))
                {
                    Toast.makeText(LocationsActivity.this, "Location " + locName + " is already exist in this job", Toast.LENGTH_SHORT).show();
                    return;
                }

                FSLocation newLocation = new FSLocation();
                newLocation.locName = locName;
                newLocation.locID = 0;
                newLocation.locJobID = _curJob.jobID;
                newLocation.locID = _instance.addLocationToDatabase(newLocation);

                ((EditText)findViewById(R.id.txtLocationName)).setText("");
                initTableData();
            }
        });

        Button btnJobDel = (Button)findViewById(R.id.btnDelLocation);
        btnJobDel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((EditText)findViewById(R.id.txtLocationName)).setText("");
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

    public void hideSoftKeyboard() {
        View viewText = findViewById(R.id.txtLocationName);

        if(viewText!=null) {
            InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
            inputMethodManager.hideSoftInputFromWindow(viewText.getWindowToken(), InputMethodManager.HIDE_IMPLICIT_ONLY);
            getWindow().setSoftInputMode(
                    WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
        }
    }

    private void initTableData()
    {
        initTableDataArray();

        // Set Adapter for listview
        //tblJobs.reloadData();
        locationAdapter.setData(arrLocationNames);
        listLocations.setAdapter(locationAdapter);
    }

    private void initTableDataArray()
    {
        DataManager _instance = DataManager.sharedInstance(LocationsActivity.this);
        arrLocationNames = _instance.getLocations(_curJob.jobID);
    }

    public void clickItem(int position)
    {
        Intent data = new Intent();
        data.putExtra(CommonDefs.ACTIVITY_TAG_LOC_ID, arrLocationNames.get(position).locID);
        setResult(Activity.RESULT_OK, data);
        finish();
    }

    public boolean changeLocation(int locationid, String newname)
    {
        if (locationid >= arrLocationNames.size())
            return false;

        FSLocation curLoc = arrLocationNames.get(locationid);
        if (curLoc.locName.equalsIgnoreCase(newname))
        {

        }
        else
        {
            if (DataManager.sharedInstance(LocationsActivity.this).isExistSameLocation(_curJob.jobID, newname))
            {
                Toast.makeText(LocationsActivity.this, "Location " + newname + " is already exist in this job", Toast.LENGTH_SHORT).show();
                return false;
            }

            curLoc.locName = newname;
            DataManager.sharedInstance(LocationsActivity.this).updateLocToDatabase(curLoc);
        }

        initTableData();

        return true;
    }

    public void deleteLocation(int locationid)
    {
        final DeleteLocDialog confirmDialog = new DeleteLocDialog(this, R.style.NoTitleDialog);
        Button btnOK = (Button)confirmDialog.findViewById(R.id.btnOK);
        btnOK.setTag(locationid);
        btnOK.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View v) {
                confirmDialog.dismiss();

                FSLocation loc = arrLocationNames.get((Integer)v.getTag());
                GlobalData _instance = GlobalData.sharedData();
                if (_instance.isSaved && _instance.selectedLocID == loc.locID)
                {
                    Toast.makeText(LocationsActivity.this, "Recording is for this Location\nPlease 'Cancel' recording first to delete this location.", Toast.LENGTH_SHORT).show();
                    return;
                }

                DataManager.sharedInstance(LocationsActivity.this).deleteLocFromDatabase(loc);
                initTableData();
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

    @Override
    protected void onResume()
    {
        super.onResume();
        hideSoftKeyboard();
    }
}
