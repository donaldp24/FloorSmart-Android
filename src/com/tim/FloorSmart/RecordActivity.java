package com.tim.FloorSmart;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Rect;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.*;
import android.widget.*;
import com.tim.FloorSmart.Database.*;
import com.tim.FloorSmart.Global.CommonDefs;
import com.tim.FloorSmart.Global.CommonMethods;
import com.tim.FloorSmart.Global.GlobalData;
import com.tim.FloorSmart.Scan.ScanManager;

import java.util.ArrayList;

public class RecordActivity extends Activity{
    private static int ACTIVITY_RESULT_JOBS = 0;
    private static int ACTIVITY_RESULT_LOCATION = 1;
    private static int ACTIVITY_RESULT_PRODUCT = 2;

    private ScanManager manager;

    RelativeLayout mainLayout;
    boolean bInitialized = false;

    FSJob selectedJob = null;
    FSLocation selectedLocation = null;
    FSProduct selectedProduct = null;
    FSLocProduct selectedLocProduct = null;

    FSLocation defaultLocation = null;
    FSProduct defaultProduct = null;

    boolean isPrevSquareFoot = false;

    RelativeLayout rlPause;
    RelativeLayout rlRecording;

    ListView listSelectView;
    SelectViewItemAdapter adapter;
    /**
     * Called when the activity is first created.
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.record);

        mainLayout = (RelativeLayout)findViewById(R.id.RLRecordRoot);
        mainLayout.getViewTreeObserver().addOnGlobalLayoutListener(
                new ViewTreeObserver.OnGlobalLayoutListener() {
                    public void onGlobalLayout() {
                        if (bInitialized == false)
                        {
                            Rect r = new Rect();
                            mainLayout.getLocalVisibleRect(r);
                            ResolutionSet._instance.setResolution(r.width(), r.height(), true);
                            ResolutionSet._instance.iterateChild(findViewById(R.id.RLRecordRoot));
                            bInitialized = true;
                        }
                    }
                }
        );

        ImageView imgJobs = (ImageView)findViewById(R.id.imgJobs);
        imgJobs.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(RecordActivity.this, JobsActivity.class));
                overridePendingTransition(R.anim.right_in, R.anim.left_out);
                finish();
            }
        });

        ImageView imgProducts = (ImageView)findViewById(R.id.imgProducts);
        imgProducts.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(RecordActivity.this, ProductsActivity.class));
                overridePendingTransition(R.anim.right_in, R.anim.left_out);
                finish();
            }
        });

        ImageView imgReport = (ImageView)findViewById(R.id.imgReports);
        imgReport.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent reportIntent = new Intent(RecordActivity.this, JobsActivity.class);
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
                startActivity(new Intent(RecordActivity.this, SettingsActivity.class));
                overridePendingTransition(R.anim.right_in, R.anim.left_out);
                finish();
            }
        });

        Button btnAddJob = (Button)findViewById(R.id.btnAddJob);
        btnAddJob.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onSelJob();
            }
        });

        Button btnAddLoc = (Button)findViewById(R.id.btnAddLoc);
        btnAddLoc.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onSelLocation();
            }
        });

        Button btnAddProduct = (Button)findViewById(R.id.btnAddProduct);
        btnAddProduct.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onSelProduct();
            }
        });

        RelativeLayout rlSelectJob = (RelativeLayout)findViewById(R.id.RLSelectJob);
        rlSelectJob.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onJobClick();
            }
        });

        RelativeLayout rlSelectLocation = (RelativeLayout)findViewById(R.id.RLSelectLocation);
        rlSelectLocation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onLocationClick();
            }
        });

        RelativeLayout rlSelectProduct = (RelativeLayout)findViewById(R.id.RLSelectProduuct);
        rlSelectProduct.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onProductClick();
            }
        });

        Button btnSummary = (Button)findViewById(R.id.btnSummary);
        btnSummary.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent readingIntent = new Intent(RecordActivity.this, ReadingActivity.class);
                readingIntent.putExtra(CommonDefs.ACTIVITY_TAG_LOC_PRODUCT_ID, selectedLocProduct.locProductID);
                startActivity(readingIntent);
                overridePendingTransition(R.anim.right_in, R.anim.left_out);
            }
        });

        rlRecording = (RelativeLayout)findViewById(R.id.RLRecord);
        rlPause = (RelativeLayout)findViewById(R.id.RLPause);

        rlRecording.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onSaveClicked();
            }
        });

        rlPause.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onCancelClicked();
            }
        });

        listSelectView = ((ListView)findViewById(R.id.listSelectView));
        listSelectView.setDivider(new ColorDrawable(Color.BLACK));
        listSelectView.setCacheColorHint(Color.TRANSPARENT);
        listSelectView.setDividerHeight(5);

        adapter = new SelectViewItemAdapter(RecordActivity.this, RecordActivity.this);

        initMember();

        GlobalData _instance = GlobalData.sharedData();
        DataManager _dataInstance = DataManager.sharedInstance(RecordActivity.this);
        selectedJob = _dataInstance.getJobFromID(_instance.selectedJobID);
        selectedLocation = _dataInstance.getLocationFromID(_instance.selectedLocID);
        selectedProduct = null;
        selectedLocProduct = _dataInstance.getLocProductWithID(_instance.selectedLocProductID);

        if (selectedJob == null || selectedLocation == null || selectedLocProduct == null)
        {

        }
        else
        {

        }

        if (selectedJob != null)
        {
            ((TextView)findViewById(R.id.txtJobName)).setText(selectedJob.jobName);
        }
        else
        {
            ((TextView)findViewById(R.id.txtJobName)).setText(getString(R.string.record_reading_notselected));
        }

        if (selectedLocation != null)
        {
            ((TextView)findViewById(R.id.txtLocationName)).setText(selectedLocation.locName);
        }
        else
        {
            ((TextView)findViewById(R.id.txtLocationName)).setText(getString(R.string.record_reading_notselected));
        }

        if (selectedLocProduct != null)
        {
            ((TextView)findViewById(R.id.txtProductName)).setText(selectedLocProduct.locProductName);

            if (_instance.settingArea == GlobalData.AREA_FEET)
            {
                ((TextView)findViewById(R.id.txtCoverage)).setText(String.format("%.2f", selectedLocProduct.locProductCoverage));
            }
            else
            {
                ((TextView)findViewById(R.id.txtCoverage)).setText(String.format("%.2f", _instance.sqft2sqm((float)selectedLocProduct.locProductCoverage)));
            }

            if (selectedLocation.locName.equalsIgnoreCase(DataManager.FMD_DEFAULT_LOCATIONNAME))
            {
                ((TextView)findViewById(R.id.txtLocationName)).setText(getString(R.string.record_reading_notselected));
            }
            if (selectedLocProduct.locProductName.equalsIgnoreCase(DataManager.FMD_DEFAULT_PRODUCTNAME))
            {
                ((TextView)findViewById(R.id.txtProductName)).setText(getString(R.string.record_reading_notselected));
            }
        }
        else
        {
            ((TextView)findViewById(R.id.txtProductName)).setText(getString(R.string.record_reading_notselected));
            ((TextView)findViewById(R.id.txtCoverage)).setText("");
        }

        if (_instance.isSaved == true &&
                (selectedJob != null && selectedLocation != null && selectedLocProduct != null))
        {
            // Change Record State
        }
        else
        {

        }

        if (_instance.settingArea == GlobalData.AREA_FEET)
        {
            ((TextView)findViewById(R.id.txtCoverFT)).setVisibility(View.VISIBLE);
            ((TextView)findViewById(R.id.txtCoverM)).setVisibility(View.INVISIBLE);
            isPrevSquareFoot = true;
        }
        else
        {
            ((TextView)findViewById(R.id.txtCoverFT)).setVisibility(View.INVISIBLE);
            ((TextView)findViewById(R.id.txtCoverM)).setVisibility(View.VISIBLE);
            isPrevSquareFoot = false;
        }
    }

    private void initMember()
    {
        defaultLocation = new FSLocation();
        defaultLocation.locID = 0;
        defaultLocation.locJobID = 0;
        defaultLocation.locName = DataManager.FMD_DEFAULT_LOCATIONNAME;

        defaultProduct = new FSProduct();
        defaultProduct.productID = 0;
        defaultProduct.productName = DataManager.FMD_DEFAULT_PRODUCTNAME;
        defaultProduct.productDeleted = 0;
    }

    @Override
    protected void onStart()
    {
        super.onStart();

        GlobalData globalData = GlobalData.sharedData();

        boolean isKeeped = true;
        if (selectedJob != null)
        {
            FSJob orgJob = selectedJob;
            selectedJob = DataManager.sharedInstance(RecordActivity.this).getJobFromID(selectedJob.jobID);
            if (selectedJob == null || selectedJob.jobArchived == 1)
            {
                selectedJob = null;
                isKeeped = false;
                selectedLocation = null;
                selectedLocProduct = null;
                selectedProduct = null;
            }
            else
            {
                ((TextView)findViewById(R.id.txtJobName)).setText(selectedJob.jobName);
            }
        }

        if (selectedLocation != null)
        {
            FSLocation orgLoc = selectedLocation;
            selectedLocation = DataManager.sharedInstance(RecordActivity.this).getLocationFromID(selectedLocation.locID);
            if (selectedLocation == null)
            {
                isKeeped = false;
                selectedProduct = null;
                selectedLocProduct = null;
            }
            else
            {
                ((TextView)findViewById(R.id.txtLocationName)).setText(selectedLocation.locName);
            }
        }

        if (selectedProduct != null)
        {
            FSProduct orgProduct = selectedProduct;
            selectedProduct = DataManager.sharedInstance(RecordActivity.this).getProductFromID(selectedProduct.productID);
            if (selectedProduct == null) {
                isKeeped = false;
            }
            else
            {
                ((TextView)findViewById(R.id.txtProductName)).setText(selectedProduct.productName);
            }
        }

        if (selectedLocProduct != null)
        {
            FSLocProduct orgLocProduct = selectedLocProduct;
            selectedLocProduct = DataManager.sharedInstance(RecordActivity.this).getLocProductWithID(selectedLocProduct.locProductID);
            if (selectedLocProduct == null) {
                isKeeped = false;
            }
            else
            {
                ((TextView)findViewById(R.id.txtProductName)).setText(selectedLocProduct.locProductName);
            }
        }

        if (isKeeped == false)
        {
            //if (globalData.isSaved)
            //{
            globalData.resetSavedData();
            //self.btnSummary.enabled = NO;
            //}

            if (selectedJob == null) {
                ((TextView)findViewById(R.id.txtJobName)).setText(getString(R.string.record_reading_notselected));
                ((TextView)findViewById(R.id.txtLocationName)).setText(getString(R.string.record_reading_notselected));
                ((TextView)findViewById(R.id.txtProductName)).setText(getString(R.string.record_reading_notselected));

                ((TextView)findViewById(R.id.txtCoverage)).setText("");
                selectedLocation = null;
                selectedLocProduct = null;
                selectedProduct = null;
            }
            else
            {
                ((TextView)findViewById(R.id.txtLocationName)).setText(getString(R.string.record_reading_notselected));
                ((TextView)findViewById(R.id.txtProductName)).setText(getString(R.string.record_reading_notselected));

                if (selectedLocation == null)
                {
                    selectedLocation = DataManager.sharedInstance(RecordActivity.this).getDefaultLocationOfJob(selectedJob.jobID);
                    selectedLocProduct = null;
                    selectedProduct = null;
                }

                if (selectedLocation != null)
                {
                    if (selectedLocProduct == null && selectedProduct == null)
                        selectedLocProduct = DataManager.sharedInstance(RecordActivity.this).getDefaultLocProductOfLocation(selectedLocation);

                    if (selectedLocation.locName.equalsIgnoreCase(DataManager.FMD_DEFAULT_LOCATIONNAME))
                        ((TextView)findViewById(R.id.txtLocationName)).setText(getString(R.string.record_reading_notselected));
                    else
                        ((TextView)findViewById(R.id.txtLocationName)).setText(selectedLocation.locName);
                }

                if (selectedLocProduct != null)
                {
                    if (selectedLocProduct.locProductName.equalsIgnoreCase(DataManager.FMD_DEFAULT_PRODUCTNAME))
                        ((TextView)findViewById(R.id.txtProductName)).setText(getString(R.string.record_reading_notselected));
                    else
                        ((TextView)findViewById(R.id.txtProductName)).setText(selectedLocProduct.locProductName);
                }
            }
        }

        // set txtcoverage
        float coverage = 0;
        String coverageStr = ((EditText)findViewById(R.id.txtCoverage)).getText().toString();
        if (coverageStr.equals("") == false)
            coverage = Float.parseFloat(coverageStr);

        // have to convert unit
        if (isPrevSquareFoot == (globalData.settingArea == GlobalData.AREA_FEET))
            coverage = coverage;
        else
        {
            if (globalData.settingArea == GlobalData.AREA_FEET)
            {
                coverage = globalData.sqm2sqft(coverage);
                isPrevSquareFoot = true;
            }
            else
            {
                coverage = globalData.sqft2sqm(coverage);
                isPrevSquareFoot = true;
            }
        }

        if (globalData.settingArea == GlobalData.AREA_FEET)
        {
            ((TextView)findViewById(R.id.txtCoverFT)).setVisibility(View.VISIBLE);
            ((TextView)findViewById(R.id.txtCoverM)).setVisibility(View.INVISIBLE);
        }
        else
        {
            ((TextView)findViewById(R.id.txtCoverFT)).setVisibility(View.INVISIBLE);
            ((TextView)findViewById(R.id.txtCoverM)).setVisibility(View.VISIBLE);
        }

        ((TextView)findViewById(R.id.txtCoverage)).setText(String.format("%.2f", coverage));
    }

    private boolean isSelectable()
    {
        return GlobalData.sharedData().isSaved;
    }

    private void onJobClick()
    {
        ArrayList<FSJob> arrayData = DataManager.sharedInstance(RecordActivity.this).getJobs(0, "");

        if (arrayData.size() <= 0)
        {
            onSelJob();
            return;
        }

        if (isSelectable() == false)
            pauseRecording();

        ShowSelectDialog(GlobalData.MODE_SELECT_JOB, null);
    }

    private void onLocationClick()
    {
        if (selectedJob == null)
        {
            onSelLocation();
            return;
        }

        ArrayList<FSLocation> arrayData = DataManager.sharedInstance(RecordActivity.this).getAllDistinctLocations();

        if (arrayData.size() <= 0)
        {
            onSelLocation();
            return;
        }

        if (isSelectable() == false)
            pauseRecording();

        if (selectedJob == null)
        {
            Toast.makeText(RecordActivity.this, "Please select a job", Toast.LENGTH_SHORT).show();
            return;
        }

        ShowSelectDialog(GlobalData.MODE_SELECT_LOCATION, selectedJob);
    }

    private void onProductClick()
    {
        FSLocation loc = null;
        if (selectedLocation == null)
            loc = defaultLocation;
        else
            loc = selectedLocation;

        if (loc == null)
        {
            onSelProduct();
            return;
        }

        ArrayList<FSProduct> arrayData = DataManager.sharedInstance(RecordActivity.this).getProducts("");

        if (arrayData.size() <= 0)
        {
            onSelProduct();
            return;
        }

        if (isSelectable() == false)
            pauseRecording();

        if (selectedJob == null)
        {
            Toast.makeText(RecordActivity.this, "Please select a job", Toast.LENGTH_SHORT).show();
            return;
        }

        ShowSelectDialog(GlobalData.MODE_SELECT_PRODUCT, loc);
    }

    private void onSelJob()
    {
        if (isSelectable() == false)
            pauseRecording();

        Intent jobSelectIntent = new Intent(RecordActivity.this, JobsActivity.class);
        jobSelectIntent.putExtra(CommonDefs.ACTIVITY_TAG_FRRECORD, CommonDefs.ACTIVITY_FROM_RECORD);

        startActivityForResult(jobSelectIntent, ACTIVITY_RESULT_JOBS);
    }

    private void onSelLocation()
    {
        if (isSelectable() == false)
            pauseRecording();

        if (selectedJob == null)
        {
            Toast.makeText(RecordActivity.this, "Please select a job", Toast.LENGTH_SHORT).show();
            return;
        }

        Intent locationSelectIntent = new Intent(RecordActivity.this, LocationsActivity.class);
        locationSelectIntent.putExtra(CommonDefs.ACTIVITY_TAG_FRRECORD, CommonDefs.ACTIVITY_FROM_RECORD);
        locationSelectIntent.putExtra(CommonDefs.ACTIVITY_TAG_JOBID, (int)selectedJob.jobID);

        startActivityForResult(locationSelectIntent, ACTIVITY_RESULT_LOCATION);
    }

    private void onSelProduct()
    {
        if (isSelectable() == false)
            pauseRecording();

        if (selectedJob == null)
        {
            Toast.makeText(RecordActivity.this, "Please select a job", Toast.LENGTH_SHORT).show();
            return;
        }

        FSLocation curLocation = null;
        if (selectedLocation == null)
            curLocation = defaultLocation;
        else
            curLocation = selectedLocation;

        Intent locationSelectIntent = new Intent(RecordActivity.this, LocProductsActivity.class);
        locationSelectIntent.putExtra(CommonDefs.ACTIVITY_TAG_FRRECORD, CommonDefs.ACTIVITY_FROM_RECORD);
        locationSelectIntent.putExtra(CommonDefs.ACTIVITY_TAG_LOC_ID, curLocation.locID);
        locationSelectIntent.putExtra(CommonDefs.ACTIVITY_TAG_LOC_NAME, curLocation.locName);
        locationSelectIntent.putExtra(CommonDefs.ACTIVITY_TAG_LOC_JOBID, curLocation.locJobID);

        startActivityForResult(locationSelectIntent, ACTIVITY_RESULT_PRODUCT);
    }

    private void ShowSelectDialog(int mode, Object parentNode)
    {
        TextView txtTitle = (TextView)findViewById(R.id.txtSelectTitle);

        if (mode == GlobalData.MODE_SELECT_JOB)
        {
            txtTitle.setText("Select a Job");

            ArrayList<FSJob> jobs = DataManager.sharedInstance(RecordActivity.this).getJobs(0, "");
            adapter.setMode(mode);
            adapter.setJobData(jobs);

            listSelectView.setAdapter(adapter);
        }
        else if (mode == GlobalData.MODE_SELECT_LOCATION)
        {
            txtTitle.setText("Select a Location");

            ArrayList<FSLocation> locations = DataManager.sharedInstance(RecordActivity.this).getAllDistinctLocations();
            adapter.setMode(mode);
            adapter.setLocData(locations);

            listSelectView.setAdapter(adapter);
        }
        else if (mode == GlobalData.MODE_SELECT_PRODUCT)
        {
            txtTitle.setText("Select a Product");

            ArrayList<FSProduct> products = DataManager.sharedInstance(RecordActivity.this).getProducts("");
            adapter.setMode(mode);
            adapter.setProductData(products);

            listSelectView.setAdapter(adapter);
        }
        else
            return;

        ((RelativeLayout)findViewById(R.id.RLSelectView)).setVisibility(View.VISIBLE);
        ((RelativeLayout)findViewById(R.id.RLSelectView)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((RelativeLayout)findViewById(R.id.RLSelectView)).setVisibility(View.INVISIBLE);
            }
        });
    }

    public void selectItem(Object itemData, int mode)
    {
        ((RelativeLayout)findViewById(R.id.RLSelectView)).setVisibility(View.INVISIBLE);

        if (mode == GlobalData.MODE_SELECT_JOB)
        {
            FSJob jobSelected = (FSJob)itemData;
            if (jobSelected != null)
            {
                jobSelected(jobSelected);
            }
        }
        else if (mode == GlobalData.MODE_SELECT_LOCATION)
        {
            FSLocation locSelected = (FSLocation)itemData;
            locSelected.locJobID = selectedJob.jobID;
            FSLocation existLoc = DataManager.sharedInstance(RecordActivity.this).getLocation(selectedJob.jobID, locSelected.locName);

            if (existLoc != null)
            {
                locationSelected(existLoc);
            }
            else
            {
                locSelected.locID = DataManager.sharedInstance(RecordActivity.this).addLocationToDatabase(locSelected);
                locationSelected(locSelected);
            }
        }
        else if (mode == GlobalData.MODE_SELECT_PRODUCT)
        {
            FSProduct productSelected = (FSProduct)itemData;
            if (productSelected != null)
            {
                productSelected(productSelected);
            }
        }
    }

    private void jobSelected(FSJob job)
    {
        if (selectedJob != null && selectedJob.jobID == job.jobID)
            return;

        selectedJob = job;
        selectedLocation = null;
        selectedProduct = null;
        selectedLocProduct = null;

        ((TextView)findViewById(R.id.txtJobName)).setText(job.jobName);
        ((TextView)findViewById(R.id.txtLocationName)).setText(getString(R.string.record_reading_notselected));
        ((TextView)findViewById(R.id.txtProductName)).setText(getString(R.string.record_reading_notselected));

        FSLocation location = DataManager.sharedInstance(RecordActivity.this).getDefaultLocationOfJob(job.jobID);
        if (location != null)
        {
            locationSelected(location);
        }
        else
        {
            defaultLocation.locID = 0;
            defaultLocation.locName = DataManager.FMD_DEFAULT_LOCATIONNAME;
            defaultLocation.locJobID = selectedJob.jobID;

            long retId = DataManager.sharedInstance(RecordActivity.this).addLocationToDatabase(defaultLocation);
            defaultLocation = DataManager.sharedInstance(RecordActivity.this).getLocationFromID(retId);
            locationSelected(defaultLocation);
            //selectedLocation = null;
        }

        defaultLocation.locJobID = job.jobID;
    }

    private void locationSelected(FSLocation loc)
    {
        if (selectedLocation != null && selectedLocation.locID == loc.locID)
            return;

        selectedLocation = loc;

        ((TextView)findViewById(R.id.txtLocationName)).setText(loc.locName);
        ((TextView)findViewById(R.id.txtProductName)).setText(getString(R.string.record_reading_notselected));

        selectedProduct = null;

        FSLocProduct locProduct = DataManager.sharedInstance(RecordActivity.this).getDefaultLocProductOfLocation(selectedLocation);
        if (locProduct != null)
        {
            locProductSelected(locProduct);
        }
        else
            selectedLocProduct = null;
    }

    private void locProductSelected(FSLocProduct locProduct)
    {
        if (selectedLocProduct != null && selectedLocProduct.locProductID == locProduct.locProductID)
            return;

        selectedLocProduct = locProduct;
        selectedProduct = null;

        ((TextView)findViewById(R.id.txtProductName)).setText(selectedLocProduct.locProductName);
        float coverage = (float)selectedLocProduct.locProductCoverage;

        GlobalData _instance = GlobalData.sharedData();
        if (_instance.settingArea == GlobalData.AREA_FEET)
            coverage = coverage;
        else
            coverage = _instance.sqft2sqm(coverage);

        ((TextView)findViewById(R.id.txtCoverage)).setText(String.format("%.2f", coverage));
    }

    private void productSelected(FSProduct product)
    {
        if (selectedProduct != null && selectedProduct.productID == product.productID)
            return;

        FSLocProduct locProduct = DataManager.sharedInstance(RecordActivity.this).getLocProductWithProduct(product, selectedLocation.locID);
        if (locProduct != null)
        {
            selectedProduct = null;
            locProductSelected(locProduct);
        }
        else
        {
            selectedProduct = product;
            selectedLocProduct = null;

            ((TextView)findViewById(R.id.txtProductName)).setText(selectedProduct.productName);
        }
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == ACTIVITY_RESULT_JOBS)
        {
            if (data != null)
            {
                long jobId = data.getLongExtra(CommonDefs.ACTIVITY_TAG_JOBID, 0);
                if (jobId != 0)
                {
                    FSJob selectedJob = DataManager.sharedInstance(RecordActivity.this).getJobFromID(jobId);
                    jobSelected(selectedJob);
                }
            }
        }
        else if (requestCode == ACTIVITY_RESULT_LOCATION)
        {
            if (data != null)
            {
                long locId = data.getLongExtra(CommonDefs.ACTIVITY_TAG_LOC_ID, 0);
                if (locId != 0)
                {
                    FSLocation selectedLoc = DataManager.sharedInstance(RecordActivity.this).getLocationFromID(locId);
                    locationSelected(selectedLoc);
                }
            }
        }
        else if (requestCode == ACTIVITY_RESULT_PRODUCT)
        {
            if (data != null)
            {
                long locId = data.getLongExtra(CommonDefs.ACTIVITY_TAG_LOC_ID, 0);
                if (locId != 0)
                {
                    FSLocation selectedLoc = DataManager.sharedInstance(RecordActivity.this).getLocationFromID(locId);
                    locationSelected(selectedLoc);
                }

                long locProductId = data.getLongExtra(CommonDefs.ACTIVITY_TAG_LOC_PRODUCT_ID, 0);
                if (locProductId != 0)
                {
                    FSLocProduct selectedLocProduct = DataManager.sharedInstance(RecordActivity.this).getLocProductWithID(locProductId);
                    locProductSelected(selectedLocProduct);
                }

                long productId = data.getLongExtra(CommonDefs.ACTIVITY_TAG_PRODUCT_ID, 0);
                if (productId != 0)
                {
                    FSProduct selectedProduct = DataManager.sharedInstance(RecordActivity.this).getProductFromID(productId);
                    productSelected(selectedProduct);
                }
            }
        }
    }

    private void onSaveClicked()
    {
        if (selectedJob == null)
        {
            Toast.makeText(RecordActivity.this, "Please select a job", Toast.LENGTH_SHORT).show();
            return;
        }

        GlobalData globalData = GlobalData.sharedData();
        float coverage = 0;
        String strCoverage = ((EditText)findViewById(R.id.txtCoverage)).getText().toString();
        coverage = Float.parseFloat(strCoverage);

        if (globalData.settingArea == GlobalData.AREA_FEET)
            coverage = coverage;
        else
            coverage = globalData.sqm2sqft(coverage);

        if (selectedLocation == null)
        {
            defaultLocation.locJobID = selectedJob.jobID;
            defaultLocation.locName = DataManager.FMD_DEFAULT_LOCATIONNAME;
            long loc_id = DataManager.sharedInstance(RecordActivity.this).addLocationToDatabase(defaultLocation);
            selectedLocation = DataManager.sharedInstance(RecordActivity.this).getLocationFromID(loc_id);
        }

        if (selectedLocation == null)
        {
            Toast.makeText(RecordActivity.this, "Please select a location", Toast.LENGTH_SHORT).show();
            return;
        }

        if (selectedLocProduct != null)
        {
            selectedLocProduct.locProductCoverage = coverage;
            DataManager.sharedInstance(RecordActivity.this).updateLocProductToDatabase(selectedLocProduct);
        }
        else if (selectedProduct != null)
        {
            FSLocProduct locProduct = DataManager.sharedInstance(RecordActivity.this).getLocProductWithProduct(selectedProduct, selectedLocation.locID);
            if (locProduct != null)
            {
                selectedLocProduct = locProduct;
                selectedLocProduct.locProductCoverage = coverage;
                DataManager.sharedInstance(RecordActivity.this).updateLocProductToDatabase(selectedLocProduct);
            }
            else
            {
                long locproduct_id = DataManager.sharedInstance(RecordActivity.this).addLocProductToDatabaseWithProduct(selectedProduct, (long)selectedLocation.locID, coverage);
                selectedLocProduct = DataManager.sharedInstance(RecordActivity.this).getLocProductWithID(locproduct_id);
            }
        }
        else
        {
            FSLocProduct locProduct = new FSLocProduct();
            locProduct.locProductLocID = selectedLocation.locID;
            locProduct.locProductName = DataManager.FMD_DEFAULT_PRODUCTNAME;
            locProduct.locProductType = FSProduct.FSPRODUCTTYPE_FINISHED;
            locProduct.locProductCoverage = coverage;
            locProduct.locProductID = DataManager.sharedInstance(RecordActivity.this).addLocProductToDatabase(locProduct);

            selectedLocProduct = DataManager.sharedInstance(RecordActivity.this).getLocProductWithID(locProduct.locProductID);
        }

        if (selectedLocProduct == null)
        {
            Toast.makeText(RecordActivity.this, "Please select a product", Toast.LENGTH_SHORT).show();
            return;
        }

        GlobalData.sharedData().saveSelection(selectedJob.jobID, selectedLocation.locID, selectedLocProduct.locProductID);
        GlobalData.sharedData().startRecording();

        rlPause.setBackgroundResource(R.drawable.bt_title_bar_cancel);
        rlRecording.setBackgroundResource(R.drawable.bt_title_bar_save_disabled);

        ScanManager manager = ScanManager.managerWithListner(this, ScanManagerListenerInstance.sharedInstance());
        manager.stopScan();
        manager.startScan();
    }

    private void onCancelClicked()
    {
        pauseRecording();
    }

    private void pauseRecording()
    {
        GlobalData.sharedData().pauseRecording();

        rlPause.setBackgroundResource(R.drawable.bt_title_bar_cancel_disabled);
        rlRecording.setBackgroundResource(R.drawable.bt_title_bar_save);

        ScanManager manager = ScanManager.managerWithListner(this, ScanManagerListenerInstance.sharedInstance());
        manager.stopScan();
    }
}
