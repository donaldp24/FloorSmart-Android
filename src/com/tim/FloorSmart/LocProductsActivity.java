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
import com.tim.FloorSmart.Database.*;
import com.tim.FloorSmart.Global.CommonDefs;
import com.tim.FloorSmart.Global.CommonMethods;
import com.tim.FloorSmart.Global.GlobalData;
import com.tim.FloorSmart.Scan.ScanManager;

import java.util.ArrayList;

public class LocProductsActivity extends BaseActivity{

    private ScanManager manager;

    RelativeLayout mainLayout;
    boolean bInitialized = false;

    boolean bShowDropMenu = false;
    boolean bFlagAddedLoc = false;
    long addedLocId = 0;

    int finishNum = -1;

    boolean bSwitchOn = true;

    ListView listLocProducts;
    LocProductItemAdapter locproductAdapter;
    ArrayList<FSLocProduct> arrLocProducts;
    ArrayList<FSProduct> arrProducts;

    FSLocation curLoc;

    /**
     * Called when the activity is first created.
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.products_location);

        curLoc = new FSLocation();
        curLoc.locJobID = getIntent().getLongExtra(CommonDefs.ACTIVITY_TAG_LOC_JOBID, 0);
        curLoc.locName = getIntent().getStringExtra(CommonDefs.ACTIVITY_TAG_LOC_NAME);
        curLoc.locID = getIntent().getLongExtra(CommonDefs.ACTIVITY_TAG_LOC_ID, 0);

        mainLayout = (RelativeLayout)findViewById(R.id.RLProductsRoot);
        mainLayout.getViewTreeObserver().addOnGlobalLayoutListener(
                new ViewTreeObserver.OnGlobalLayoutListener() {
                    public void onGlobalLayout() {
                        if (bInitialized == false)
                        {
                            Rect r = new Rect();
                            mainLayout.getLocalVisibleRect(r);
                            ResolutionSet._instance.setResolution(r.width(), r.height(), true);
                            ResolutionSet._instance.iterateChild(findViewById(R.id.RLProductsRoot));
                            bInitialized = true;

                            listLocProducts = (ListView)findViewById(R.id.listView);

                            listLocProducts.setDivider(new ColorDrawable(Color.TRANSPARENT));
                            listLocProducts.setCacheColorHint(Color.parseColor("#FFF1F1F1"));
                            listLocProducts.setDividerHeight(20);
                            locproductAdapter = new LocProductItemAdapter(LocProductsActivity.this, getApplicationContext());

                            initTableData();
                        }
                    }
                }
        );

        ImageView imgRecord = (ImageView)findViewById(R.id.imgRecord);
        imgRecord.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(LocProductsActivity.this, RecordActivity.class));
                overridePendingTransition(R.anim.left_in, R.anim.right_out);
                finish();
            }
        });

        ImageView imgJobs = (ImageView)findViewById(R.id.imgJobs);
        imgJobs.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(LocProductsActivity.this, JobsActivity.class));
                overridePendingTransition(R.anim.left_in, R.anim.right_out);
                finish();
            }
        });

        ImageView imgReport = (ImageView)findViewById(R.id.imgReports);
        imgReport.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent reportIntent = new Intent(LocProductsActivity.this, JobsActivity.class);
                reportIntent.putExtra(CommonDefs.ACTIVITY_TAG_REPORT, CommonDefs.ACTIVITY_REPORTS);
                startActivity(reportIntent);

                //startActivity(new Intent(ProductsActivity.this, ReportsActivity.class));
                overridePendingTransition(R.anim.right_in, R.anim.left_out);
                finish();
            }
        });

        ImageView imgSettings = (ImageView)findViewById(R.id.imgSettings);
        imgSettings.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(LocProductsActivity.this, SettingsActivity.class));
                overridePendingTransition(R.anim.right_in, R.anim.left_out);
                finish();
            }
        });

        ImageView imgDrpDown = (ImageView)findViewById(R.id.imgDropButton);
        imgDrpDown.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                changeDropList(-1);
            }
        });

        RelativeLayout rlSelectTypeRegion = (RelativeLayout)findViewById(R.id.RLSelectTypeRegion);
        rlSelectTypeRegion.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                changeDropList(-1);
            }
        });

        RelativeLayout rlItemFinished = (RelativeLayout)findViewById(R.id.RLItemFinished);
        rlItemFinished.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                changeDropList(FSProduct.FSPRODUCTTYPE_FINISHED);
            }
        });

        RelativeLayout rlItemSubFloor = (RelativeLayout)findViewById(R.id.RLItemSubFloor);
        rlItemSubFloor.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                changeDropList(FSProduct.FSPRODUCTTYPE_SUBFLOOR);
            }
        });

        Button btnAdd = (Button)findViewById(R.id.btnAddProduct);
        btnAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String productName = ((EditText)findViewById(R.id.txtProductName)).getText().toString();

                if (productName.equals(""))
                {
                    CommonMethods.showAlertMessage(LocProductsActivity.this, "Please input product name to add!");
                    return;
                }

                DataManager _instance = DataManager.sharedInstance(LocProductsActivity.this);

                if (bSwitchOn)
                {
                    if (_instance.isExistSameProduct(productName, finishNum))
                    {
                        CommonMethods.showAlertMessage(LocProductsActivity.this, "Product " + productName + "(" + FSProduct.getDisplayProductType(finishNum) + ") is already exist");
                        return;
                    }

                    FSProduct newProduct = new FSProduct();
                    newProduct.productName = productName;
                    newProduct.productType = finishNum;
                    newProduct.productID = DataManager.sharedInstance(LocProductsActivity.this).addProductToDatabase(newProduct);
                }
                else
                {
                    FSLocProduct locProduct = new FSLocProduct();
                    locProduct.locProductID = 0;
                    locProduct.locProductLocID = 0;
                    locProduct.locProductName = productName;
                    locProduct.locProductType = finishNum;

                    if (curLoc.locID == 0)
                    {
                        long loc_id = DataManager.sharedInstance(LocProductsActivity.this).addLocationToDatabase(curLoc);
                        curLoc = DataManager.sharedInstance(LocProductsActivity.this).getLocationFromID(loc_id);

                        // Set Flag added location
                        bFlagAddedLoc = true;
                        addedLocId = loc_id;
                    }

                    if (curLoc != null)
                    {
                        if (DataManager.sharedInstance(LocProductsActivity.this).isExistSameLocProduct(curLoc.locID, productName, finishNum))
                        {
                            CommonMethods.showAlertMessage(LocProductsActivity.this, "Product '" + productName + "(" + FSProduct.getDisplayProductType(finishNum) + ")' is already exist in this Location");
                            return;
                        }

                        locProduct.locProductLocID = curLoc.locID;
                        DataManager.sharedInstance(LocProductsActivity.this).addLocProductToDatabase(locProduct);

                        FSProduct sameProduct = DataManager.sharedInstance(LocProductsActivity.this).getProductWithLocProduct(locProduct);
                        if (sameProduct == null)
                        {
                            sameProduct = new FSProduct();
                            sameProduct.productID = 0;
                            sameProduct.productName = locProduct.locProductName;
                            sameProduct.productType = locProduct.locProductType;

                            DataManager.sharedInstance(LocProductsActivity.this).addProductToDatabase(sameProduct);
                        }
                    }
                }

                ((EditText)findViewById(R.id.txtProductName)).setText("");
                initTableData();

                hideSoftKeyboard();
                scrolltoEndList();
            }
        });

        Button btnDel = (Button)findViewById(R.id.btnDelProduct);
        btnDel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((EditText)findViewById(R.id.txtProductName)).setText("");
            }
        });

        finishNum = FSProduct.FSPRODUCTTYPE_SUBFLOOR;
        ((TextView)findViewById(R.id.txtCurrentType)).setText(FSProduct.getDisplayProductType(finishNum));

        EditText txtSearchField = (EditText)findViewById(R.id.txtProductName);
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

        TextView txtSwitchOn = (TextView)findViewById(R.id.txtSwitchOn);
        txtSwitchOn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((ImageView)findViewById(R.id.imgSwitch)).setImageResource(R.drawable.setting_option);
                bSwitchOn = true;

                initTableData();
            }
        });

        TextView txtSwitchOff = (TextView)findViewById(R.id.txtSwitchOff);
        txtSwitchOff.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((ImageView)findViewById(R.id.imgSwitch)).setImageResource(R.drawable.setting_option_nonactive);
                bSwitchOn = false;

                initTableData();
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

        String jobName = "";
        String locName = "";

        if (curLoc != null)
        {
            if (curLoc.locJobID != 0)
            {
                FSJob job = DataManager.sharedInstance(LocProductsActivity.this).getJobFromID(curLoc.locJobID);
                if (job != null)
                    jobName = job.jobName;
            }

            locName = curLoc.locName;
        }

        ((TextView)findViewById(R.id.lblLocation)).setText(getString(R.string.product_loc_location) + " " + jobName + "." + locName);
        EasyTracker.getInstance(LocProductsActivity.this).activityStart(this); // Add this method.
    }

    @Override
    public void onStop() {
        super.onStop();
        EasyTracker.getInstance(LocProductsActivity.this).activityStop(this); // Add this method.
    }

    public void clickItem(int position)
    {
        Intent data = new Intent();
        if (bSwitchOn)
            data.putExtra(CommonDefs.ACTIVITY_TAG_PRODUCT_ID, arrProducts.get(position).productID);
        else
            data.putExtra(CommonDefs.ACTIVITY_TAG_LOC_PRODUCT_ID, arrLocProducts.get(position).locProductID);

        if (bFlagAddedLoc)
        {
            data.putExtra(CommonDefs.ACTIVITY_TAG_LOC_ID, addedLocId);
        }

        setResult(Activity.RESULT_OK, data);
        finish();
    }

    private void changeDropList(int type)
    {
        if (type != -1)
        {
            ((TextView)findViewById(R.id.txtCurrentType)).setText(FSProduct.getDisplayProductType(type));
            finishNum = type;
        }

        bShowDropMenu = !bShowDropMenu;
        ((RelativeLayout)findViewById(R.id.RLDropList)).setVisibility((bShowDropMenu) ? View.VISIBLE : View.INVISIBLE);
    }

    public void hideSoftKeyboard() {
        View viewText = findViewById(R.id.txtProductName);

        if(viewText!=null) {
            InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
            inputMethodManager.hideSoftInputFromWindow(viewText.getWindowToken(), InputMethodManager.HIDE_IMPLICIT_ONLY);
            inputMethodManager.hideSoftInputFromWindow(viewText.getWindowToken(), 0);
            getWindow().setSoftInputMode(
                    WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
        }
    }

    private void initTableData()
    {
        initTableDataArray();

        // Set Adapter for listview
        //tblJobs.reloadData();
        locproductAdapter.setData(arrLocProducts);
        locproductAdapter.setProductData(arrProducts);
        locproductAdapter.setSwitchMode(bSwitchOn);
        listLocProducts.setAdapter(locproductAdapter);
    }

    private void changeViewResult(boolean bExist)
    {
        if (bExist == true)
        {
            ((TextView)findViewById(R.id.txtSearching)).setVisibility(View.INVISIBLE);
            listLocProducts.setVisibility(View.VISIBLE);
        }
        else
        {
            ((TextView)findViewById(R.id.txtSearching)).setVisibility(View.VISIBLE);
            listLocProducts.setVisibility(View.INVISIBLE);
        }
    }

    private void initTableDataArray()
    {
        boolean bExistData = false;

        String searchTxt = ((EditText)findViewById(R.id.txtProductName)).getText().toString();
        DataManager _instance = DataManager.sharedInstance(LocProductsActivity.this);
        if (bSwitchOn == true)
        {
            arrProducts = _instance.getProducts(searchTxt);
            bExistData = arrProducts.size() > 0;
        }
        else
        {
            arrLocProducts = _instance.getLocProducts(curLoc, searchTxt);
            bExistData = arrLocProducts.size() > 0;
        }

        if (searchTxt.equals("") || bExistData == true)
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

    public void deleteProduct(int productid)
    {
        final DeleteProductDialog confirmDialog = new DeleteProductDialog(this, R.style.NoTitleDialog);
        Button btnOK = (Button)confirmDialog.findViewById(R.id.btnOK);
        btnOK.setTag(productid);
        btnOK.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View v) {
                confirmDialog.dismiss();

                if (bSwitchOn)
                    DataManager.sharedInstance(LocProductsActivity.this).deleteProductFromDatabase(arrProducts.get((Integer) v.getTag()));
                else
                {
                    //if (arrLocProducts == null || productid >= arrLocProducts.size())
                    //    return;

                    GlobalData _instance = GlobalData.sharedData();
                    if (_instance.isSaved && _instance.selectedLocProductID == arrLocProducts.get((Integer) v.getTag()).locProductID)
                    {
                        CommonMethods.showAlertMessage(LocProductsActivity.this, "Recording is for this Product.\nPlease 'Cancel' recording first to delete this product.");
                        return;
                    }

                    DataManager.sharedInstance(LocProductsActivity.this).deleteLocProductFromDatabase(arrLocProducts.get((Integer) v.getTag()));
                }

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

    public boolean changeProduct(int productid, String productName, int producttype)
    {
        if (bSwitchOn)
        {
            if (arrProducts == null || productid >= arrProducts.size())
                return false;

            DataManager _instance = DataManager.sharedInstance(LocProductsActivity.this);
            if (_instance.isExistSameProduct(productName, producttype))
            {
                CommonMethods.showAlertMessage(LocProductsActivity.this, "Product " + productName + "(" + FSProduct.getDisplayProductType(producttype) + ") is already exist");
                return false;
            }

            FSProduct _curProduct = arrProducts.get(productid);
            _curProduct.productName = productName;
            _curProduct.productType = producttype;

            _instance.updateProductToDatabase(_curProduct);

            return true;
        }
        else
        {
            if (arrLocProducts == null || productid >= arrLocProducts.size())
                return false;

            DataManager _instance = DataManager.sharedInstance(LocProductsActivity.this);
            if (_instance.isExistSameLocProduct(curLoc.locID, productName, producttype))
            {
                CommonMethods.showAlertMessage(LocProductsActivity.this, "Product " + productName + "(" + FSProduct.getDisplayProductType(producttype) + ") is already exist");
                return false;
            }

            FSLocProduct _curProduct = arrLocProducts.get(productid);
            _curProduct.locProductName = productName;
            _curProduct.locProductType = producttype;

            _instance.updateLocProductToDatabase(_curProduct);

            FSProduct sameProduct = DataManager.sharedInstance(LocProductsActivity.this).getProductWithLocProduct(_curProduct);
            if (sameProduct == null)
            {
                sameProduct = new FSProduct();
                sameProduct.productID = 0;
                sameProduct.productName = _curProduct.locProductName;
                sameProduct.productType = _curProduct.locProductType;

                DataManager.sharedInstance(LocProductsActivity.this).addProductToDatabase(sameProduct);
            }

            return true;
        }
    }

    public void scrolltoEndList()
    {
        listLocProducts.post(new Runnable() {
            @Override
            public void run() {
                // Select the last row so it will scroll into view...
                listLocProducts.setSelection(locproductAdapter.getCount() - 1);
            }
        });
    }
}
