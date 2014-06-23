package com.tim.FloorSmart;

import android.app.Activity;
import android.content.DialogInterface;
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
import com.tim.FloorSmart.Global.CommonDefs;
import com.tim.FloorSmart.Global.CommonMethods;
import com.tim.FloorSmart.Global.GlobalData;

import java.util.ArrayList;

public class JobsActivity extends BaseActivity{
    RelativeLayout mainLayout;
    boolean bInitialized = false;

    ListView listJobs;
    JobItemAdapter jobAdapter;
    ArrayList<FSJob> arrJobNames;

    boolean bFromRecord = false;
    boolean bReports = false;

    static final int RESULT_ACTIVITY_ARCHIVE = 0;

    /**
     * Called when the activity is first created.
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.jobs);

        bFromRecord = (getIntent().getIntExtra(CommonDefs.ACTIVITY_TAG_FRRECORD, CommonDefs.ACTIVITY_FROM_SPLASH) == CommonDefs.ACTIVITY_FROM_RECORD);
        bReports = (getIntent().getIntExtra(CommonDefs.ACTIVITY_TAG_REPORT, CommonDefs.ACTIVITY_JOB) == CommonDefs.ACTIVITY_REPORTS);

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

                            listJobs = (ListView)findViewById(R.id.listView);

                            listJobs.setDivider(new ColorDrawable(Color.TRANSPARENT));
                            listJobs.setCacheColorHint(Color.parseColor("#FFF1F1F1"));
                            listJobs.setDividerHeight(20);
                            jobAdapter = new JobItemAdapter(JobsActivity.this, getApplicationContext());

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
                    startActivity(new Intent(JobsActivity.this, RecordActivity.class));
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
                    startActivity(new Intent(JobsActivity.this, JobsActivity.class));
                    overridePendingTransition(R.anim.left_in, R.anim.right_out);
                    finish();
                }
            });

            ImageView imgProduct = (ImageView)findViewById(R.id.imgProducts);
            imgProduct.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    startActivity(new Intent(JobsActivity.this, ProductsActivity.class));
                    overridePendingTransition(R.anim.left_in, R.anim.right_out);
                    finish();
                }
            });

            ImageView imgSettings = (ImageView)findViewById(R.id.imgSettings);
            imgSettings.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    startActivity(new Intent(JobsActivity.this, SettingsActivity.class));
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
                    startActivity(new Intent(JobsActivity.this, RecordActivity.class));
                    overridePendingTransition(R.anim.left_in, R.anim.right_out);
                    finish();
                }
            });

            ImageView imgProduct = (ImageView)findViewById(R.id.imgProducts);
            imgProduct.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    startActivity(new Intent(JobsActivity.this, ProductsActivity.class));
                    overridePendingTransition(R.anim.right_in, R.anim.left_out);
                    finish();
                }
            });

            ImageView imgReport = (ImageView)findViewById(R.id.imgReports);
            imgReport.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent reportIntent = new Intent(JobsActivity.this, JobsActivity.class);
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
                    startActivity(new Intent(JobsActivity.this, SettingsActivity.class));
                    overridePendingTransition(R.anim.right_in, R.anim.left_out);
                    finish();
                }
            });
        }

        Button btnJobAdd = (Button)findViewById(R.id.btnAddJob);
        btnJobAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String jobName = ((EditText)findViewById(R.id.txtJobName)).getText().toString();

                if (jobName.equals(""))
                {
                    CommonMethods.showAlertMessage(JobsActivity.this, "Please input job name to add!");
                    return;
                }

                DataManager _instance = DataManager.sharedInstance(JobsActivity.this);
                if (_instance.isExistSameJob(jobName))
                {
                    CommonMethods.showAlertMessage(JobsActivity.this, "Job " + jobName + " is already exist");
                    return;
                }

                FSJob newJob = new FSJob();
                newJob.jobName = jobName;
                newJob.jobID = _instance.addJobToDatabase(newJob);

                ((EditText)findViewById(R.id.txtJobName)).setText("");
                initTableData();

                hideSoftKeyboard();
                scrolltoEndList();
            }
        });

        Button btnJobDel = (Button)findViewById(R.id.btnDelJob);
        btnJobDel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((EditText)findViewById(R.id.txtJobName)).setText("");
            }
        });

        RelativeLayout rlBack = (RelativeLayout)findViewById(R.id.RLBack);
        rlBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        RelativeLayout rlGoArchive = (RelativeLayout)findViewById(R.id.RLGoArchive);
        rlGoArchive.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent archiveIntent = new Intent(JobsActivity.this, JobsArchiveActivity.class);
                if (bReports)
                    archiveIntent.putExtra(CommonDefs.ACTIVITY_TAG_REPORT, CommonDefs.ACTIVITY_REPORTS);

                startActivityForResult(archiveIntent, RESULT_ACTIVITY_ARCHIVE);
                overridePendingTransition(R.anim.right_in, R.anim.left_out);
            }
        });

        if (bFromRecord == false)
        {
            rlBack.setVisibility(View.INVISIBLE);
        }
        else
        {
            rlGoArchive.setVisibility(View.INVISIBLE);
        }

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
        //listJobs.setAdapter(jobAdapter);
    }

    public void hideSoftKeyboard() {
        View viewText = findViewById(R.id.txtJobName);

        if(viewText!=null) {
            InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
            inputMethodManager.hideSoftInputFromWindow(viewText.getWindowToken(), InputMethodManager.HIDE_IMPLICIT_ONLY);
            inputMethodManager.hideSoftInputFromWindow(viewText.getWindowToken(), 0);
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

    public boolean changeJobName(int id, String newJobName)
    {
        if (arrJobNames == null || id >= arrJobNames.size())
            return false;

        FSJob originJob = arrJobNames.get(id);
        if (newJobName.equals("") || originJob.jobName.equals(newJobName))
            return false;

        DataManager _instance = DataManager.sharedInstance(JobsActivity.this);
        if (_instance.isExistSameJob(newJobName))
        {
            CommonMethods.showAlertMessage(this, "Job " + newJobName + " is already exist");
            return false;
        }

        originJob.jobName = newJobName;
        _instance.updateJobToDatabase(originJob);

        initTableData();

        return true;
    }

    public void clickItem(int position)
    {
        if (bFromRecord)
        {
            Intent data = new Intent();
            data.putExtra(CommonDefs.ACTIVITY_TAG_JOBID, arrJobNames.get(position).jobID);
            setResult(Activity.RESULT_OK, data);
            finish();
        }
        else
        {
            Intent overviewIntent = new Intent(JobsActivity.this, JobOverviewActivity.class);
            overviewIntent.putExtra(CommonDefs.ACTIVITY_TAG_JOBID, arrJobNames.get(position).jobID);
            startActivity(overviewIntent);
        }
    }

    public void goArchive(int itemid)
    {
        final ArchiveDialog confirmDialog = new ArchiveDialog(this, R.style.NoTitleDialog);
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
                    CommonMethods.showAlertMessage(JobsActivity.this, "Recording is for this job now.\n Please 'Cancel' recording first to archive this job.");
                    return;
                }

                curJob.jobArchived = 1;
                DataManager.sharedInstance(JobsActivity.this).updateJobToDatabase(curJob);

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

    private void initTableDataArray()
    {
        String searchTxt = ((EditText)findViewById(R.id.txtJobName)).getText().toString();
        DataManager _instance = DataManager.sharedInstance(JobsActivity.this);
        arrJobNames = _instance.getJobs(0, searchTxt);

        if (searchTxt.equals("") || arrJobNames.size() > 0)
            changeViewResult(true);
        else
            changeViewResult(false);
    }

    @Override
    protected void onResume()
    {
        super.onResume();
        hideSoftKeyboard();
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        super.onActivityResult(requestCode, resultCode, data);
        initTableData();
    }

    public void scrolltoEndList()
    {
        listJobs.post(new Runnable() {
            @Override
            public void run() {
                // Select the last row so it will scroll into view...
                listJobs.setSelection(jobAdapter.getCount() - 1);
            }
        });
    }

}
