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
import com.google.analytics.tracking.android.EasyTracker;
import com.tim.FloorSmart.Database.DataManager;
import com.tim.FloorSmart.Database.FSJob;
import com.tim.FloorSmart.Global.CommonDefs;
import com.tim.FloorSmart.Global.CommonMethods;
import com.tim.FloorSmart.Global.GlobalData;

import java.util.ArrayList;

public class JobsArchiveActivity extends BaseActivity{
    RelativeLayout mainLayout;
    boolean bInitialized = false;

    ListView listJobs;
    JobArchiveItemAdapter jobAdapter;
    ArrayList<FSJob> arrJobNames;
    boolean bReports = false;

    /**
     * Called when the activity is first created.
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.jobs_archive);

        bReports = (getIntent().getIntExtra(CommonDefs.ACTIVITY_TAG_REPORT, CommonDefs.ACTIVITY_JOB) == CommonDefs.ACTIVITY_REPORTS);

        mainLayout = (RelativeLayout)findViewById(R.id.RLJobsRoot);
        mainLayout.getViewTreeObserver().addOnGlobalLayoutListener(
                new ViewTreeObserver.OnGlobalLayoutListener() {
                    public void onGlobalLayout() {
                        if (bInitialized == false) {
                            Rect r = new Rect();
                            mainLayout.getLocalVisibleRect(r);
                            ResolutionSet._instance.setResolution(r.width(), r.height(), true);
                            ResolutionSet._instance.iterateChild(findViewById(R.id.RLJobsRoot));
                            bInitialized = true;

                            listJobs = (ListView) findViewById(R.id.listView);

                            listJobs.setDivider(new ColorDrawable(Color.TRANSPARENT));
                            listJobs.setCacheColorHint(Color.TRANSPARENT);
                            listJobs.setDividerHeight(20);
                            jobAdapter = new JobArchiveItemAdapter(JobsArchiveActivity.this, getApplicationContext());

                            initTableData();
                        }
                    }
                }
        );

        if (bReports)
        {
            ImageView imgRecord = (ImageView)findViewById(R.id.imgRecord);
            imgRecord.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    startActivity(new Intent(JobsArchiveActivity.this, RecordActivity.class));
                    overridePendingTransition(R.anim.left_in, R.anim.right_out);
                    finish();
                }
            });

            ImageView imgJob = (ImageView)findViewById(R.id.imgJobs);
            imgJob.setImageResource(R.drawable.bt_jobs_nl);
            imgJob.setPadding(0, 0, 0, 0);

            imgJob.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    startActivity(new Intent(JobsArchiveActivity.this, JobsActivity.class));
                    overridePendingTransition(R.anim.left_in, R.anim.right_out);
                    finish();
                }
            });

            ImageView imgProduct = (ImageView)findViewById(R.id.imgProducts);
            imgProduct.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    startActivity(new Intent(JobsArchiveActivity.this, ProductsActivity.class));
                    overridePendingTransition(R.anim.left_in, R.anim.right_out);
                    finish();
                }
            });

            ImageView imgSettings = (ImageView)findViewById(R.id.imgSettings);
            imgSettings.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    startActivity(new Intent(JobsArchiveActivity.this, SettingsActivity.class));
                    overridePendingTransition(R.anim.right_in, R.anim.left_out);
                    finish();
                }
            });

            ImageView imgReport = (ImageView)findViewById(R.id.imgReports);
            imgReport.setImageResource(R.drawable.bt_report_sl);
            imgReport.setPadding(2, 3, 2, 3);
            //imgReport.setPadding(0, (int)(3 * ResolutionSet.fPro), 0, (int)(3 * ResolutionSet.fPro));
        }
        else
        {
            ImageView imgRecord = (ImageView)findViewById(R.id.imgRecord);
            imgRecord.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    startActivity(new Intent(JobsArchiveActivity.this, RecordActivity.class));
                    overridePendingTransition(R.anim.left_in, R.anim.right_out);
                    finish();
                }
            });

            ImageView imgProduct = (ImageView)findViewById(R.id.imgProducts);
            imgProduct.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    startActivity(new Intent(JobsArchiveActivity.this, ProductsActivity.class));
                    overridePendingTransition(R.anim.right_in, R.anim.left_out);
                    finish();
                }
            });

            ImageView imgReport = (ImageView)findViewById(R.id.imgReports);
            imgReport.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent reportIntent = new Intent(JobsArchiveActivity.this, JobsActivity.class);
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
                    startActivity(new Intent(JobsArchiveActivity.this, SettingsActivity.class));
                    overridePendingTransition(R.anim.right_in, R.anim.left_out);
                    finish();
                }
            });
        }

        Button btnCancel = (Button)findViewById(R.id.btnCancel);
        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((EditText)findViewById(R.id.txtJobName)).setText("");
            }
        });

        /*
        Button btnSearch = (Button)findViewById(R.id.btnSearch);
        btnSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                initTableData();
            }
        });*/

        EditText txtSearchField = (EditText)findViewById(R.id.txtJobName);
        txtSearchField.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                initTableData();
            }
        });

        RelativeLayout rlBack = (RelativeLayout)findViewById(R.id.RLBack);
        rlBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                overridePendingTransition(R.anim.left_in, R.anim.right_out);
                finish();
            }
        });
    }
    /*
    public void clickItem(int position)
    {
        Intent overviewIntent = new Intent(JobsArchiveActivity.this, JobOverviewActivity.class);
        overviewIntent.putExtra(CommonDefs.ACTIVITY_TAG_JOBID, arrJobNames.get(position).jobID);
        startActivity(overviewIntent);
    }
    */
    public void hideSoftKeyboard() {
        View viewText = findViewById(R.id.txtJobName);

        if(viewText!=null) {
            InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
            inputMethodManager.hideSoftInputFromWindow(viewText.getWindowToken(), InputMethodManager.HIDE_IMPLICIT_ONLY);
            getWindow().setSoftInputMode(
                    WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
        }
    }

    private void changeViewResult(boolean bExist)
    {
        if (bExist == true)
        {
            ((TextView)findViewById(R.id.txtSearching)).setVisibility(View.INVISIBLE);
            listJobs.setVisibility(View.VISIBLE);
        }
        else
        {
            ((TextView)findViewById(R.id.txtSearching)).setVisibility(View.VISIBLE);
            listJobs.setVisibility(View.INVISIBLE);
        }
    }

    private void initTableData()
    {
        initTableDataArray();

        // Set Adapter for listview
        //tblJobs.reloadData();
        jobAdapter.setData(arrJobNames);
        listJobs.setAdapter(jobAdapter);
    }

    private void initTableDataArray()
    {
        String searchTxt = ((EditText)findViewById(R.id.txtJobName)).getText().toString();
        DataManager _instance = DataManager.sharedInstance(JobsArchiveActivity.this);
        arrJobNames = _instance.getJobs(1, searchTxt);

        if (searchTxt.equals("") || arrJobNames.size() > 0)
            changeViewResult(true);
        else
            changeViewResult(false);
    }

    public void resendJob(int itemid)
    {
        final UnArchiveDialog confirmDialog = new UnArchiveDialog(this, R.style.NoTitleDialog);
        Button btnOK = (Button)confirmDialog.findViewById(R.id.btnOK);
        btnOK.setTag(itemid);
        btnOK.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View v) {
                confirmDialog.dismiss();

                GlobalData _instance = GlobalData.sharedData();
                FSJob curJob = arrJobNames.get((Integer)v.getTag());
                if (_instance.isSaved && _instance.selectedJobID == curJob.jobID)
                {
                    CommonMethods.showAlertMessage(JobsArchiveActivity.this, "Recording is for this job now.\n Please 'Cancel' recording first to delete this job.");
                    return;
                }

                curJob.jobArchived = 0;
                DataManager.sharedInstance(JobsArchiveActivity.this).updateJobToDatabase(curJob);

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

    public void deleteJob(int itemid)
    {
        final DeleteJobDialog confirmDialog = new DeleteJobDialog(this, R.style.NoTitleDialog);
        Button btnOK = (Button)confirmDialog.findViewById(R.id.btnOK);
        btnOK.setTag(itemid);
        btnOK.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View v) {
                confirmDialog.dismiss();

                GlobalData _instance = GlobalData.sharedData();
                FSJob curJob = arrJobNames.get((Integer)v.getTag());
                if (_instance.isSaved && _instance.selectedJobID == curJob.jobID)
                {
                    CommonMethods.showAlertMessage(JobsArchiveActivity.this, "Recording is for this job now.\n Please 'Cancel' recording first to delete this job.");
                    return;
                }

                DataManager.sharedInstance(JobsArchiveActivity.this).deleteJobFromDatabase(curJob);
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

    @Override
    public void onStart() {
        super.onStart();
        EasyTracker.getInstance(JobsArchiveActivity.this).activityStart(this); // Add this method.
    }

    @Override
    public void onStop() {
        super.onStop();
        EasyTracker.getInstance(JobsArchiveActivity.this).activityStop(this); // Add this method.
    }
}
