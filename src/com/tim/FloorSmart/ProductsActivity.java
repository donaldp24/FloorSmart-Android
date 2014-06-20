package com.tim.FloorSmart;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Point;
import android.graphics.Rect;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.*;
import android.view.inputmethod.InputMethodManager;
import android.widget.*;
import com.tim.FloorSmart.Database.DataManager;
import com.tim.FloorSmart.Database.FSProduct;
import com.tim.FloorSmart.Global.CommonDefs;
import com.tim.FloorSmart.Scan.ScanManager;

import java.util.ArrayList;

public class ProductsActivity extends BaseActivity{

    private ScanManager manager;

    RelativeLayout mainLayout;
    boolean bInitialized = false;

    boolean bShowDropMenu = false;
    boolean bShowListDrop = false;

    int finishNum = -1;

    ListView listProducts;
    ProductItemAdapter productAdapter;
    ArrayList<FSProduct> arrProducts;

    static final int RESULT_ACTIVITY_ARCHIVE = 0;

    /**
     * Called when the activity is first created.
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.products);

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

                            listProducts = (ListView)findViewById(R.id.listView);

                            listProducts.setDivider(new ColorDrawable(Color.TRANSPARENT));
                            listProducts.setCacheColorHint(Color.parseColor("#FFF1F1F1"));
                            listProducts.setDividerHeight(20);
                            productAdapter = new ProductItemAdapter(ProductsActivity.this, getApplicationContext());

                            initTableData();
                        }
                    }
                }
        );

        ImageView imgRecord = (ImageView)findViewById(R.id.imgRecord);
        imgRecord.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(ProductsActivity.this, RecordActivity.class));
                overridePendingTransition(R.anim.left_in, R.anim.right_out);
                finish();
            }
        });

        ImageView imgJobs = (ImageView)findViewById(R.id.imgJobs);
        imgJobs.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(ProductsActivity.this, JobsActivity.class));
                overridePendingTransition(R.anim.left_in, R.anim.right_out);
                finish();
            }
        });

        ImageView imgReport = (ImageView)findViewById(R.id.imgReports);
        imgReport.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent reportIntent = new Intent(ProductsActivity.this, JobsActivity.class);
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
                startActivity(new Intent(ProductsActivity.this, SettingsActivity.class));
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
                    Toast.makeText(ProductsActivity.this, "Please input product name to add!", Toast.LENGTH_SHORT).show();
                    return;
                }

                DataManager _instance = DataManager.sharedInstance(ProductsActivity.this);
                if (_instance.isExistSameProduct(productName, finishNum))
                {
                    Toast.makeText(ProductsActivity.this, "Product " + productName + "(" + FSProduct.getDisplayProductType(finishNum) + ") is already exist", Toast.LENGTH_SHORT).show();
                    return;
                }

                FSProduct newProduct = new FSProduct();
                newProduct.productName = productName;
                newProduct.productType = finishNum;
                newProduct.productID = DataManager.sharedInstance(ProductsActivity.this).addProductToDatabase(newProduct);

                ((EditText)findViewById(R.id.txtProductName)).setText("");
                initTableData();
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
            getWindow().setSoftInputMode(
                    WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
        }
    }

    private void initTableData()
    {
        initTableDataArray();

        // Set Adapter for listview
        //tblJobs.reloadData();
        productAdapter.setData(arrProducts);
        listProducts.setAdapter(productAdapter);
    }

    private void initTableDataArray()
    {
        String searchTxt = ((EditText)findViewById(R.id.txtProductName)).getText().toString();
        DataManager _instance = DataManager.sharedInstance(ProductsActivity.this);
        arrProducts = _instance.getProducts(searchTxt);
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
        if (arrProducts == null || productid >= arrProducts.size())
            return;

        final DeleteProductDialog confirmDialog = new DeleteProductDialog(this, R.style.NoTitleDialog);
        Button btnOK = (Button)confirmDialog.findViewById(R.id.btnOK);
        btnOK.setTag(productid);
        btnOK.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View v) {
                confirmDialog.dismiss();

                DataManager.sharedInstance(ProductsActivity.this).deleteProductFromDatabase(arrProducts.get((Integer)v.getTag()));

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
        if (arrProducts == null || productid >= arrProducts.size())
            return false;

        DataManager _instance = DataManager.sharedInstance(ProductsActivity.this);
        if (_instance.isExistSameProduct(productName, producttype))
        {
            Toast.makeText(ProductsActivity.this, "Product " + productName + "(" + FSProduct.getDisplayProductType(producttype) + ") is already exist", Toast.LENGTH_SHORT).show();
            return false;
        }

        FSProduct _curProduct = arrProducts.get(productid);
        _curProduct.productName = productName;
        _curProduct.productType = producttype;

        _instance.updateProductToDatabase(_curProduct);

        return true;
    }
}
