package com.tim.FloorSmart;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.*;
import com.tim.FloorSmart.Database.FSProduct;

import java.util.ArrayList;

public class ProductItemAdapter extends BaseAdapter {
	Context			mContext = null;
	ProductsActivity mActivity = null;
    private LayoutInflater mInflater;

    private ArrayList<FSProduct> m_Infos = new ArrayList<FSProduct>();
    EditText        currentFocus = null;
    int             curSelectedType = -1;
    int             nEditingIdx = -1;

    public ProductItemAdapter(ProductsActivity activity, Context context) {
    	mContext = context;
    	mActivity = activity;
        // Cache the LayoutInflate to avoid asking for a new one each time.
        mInflater = LayoutInflater.from(context);
    }

    public void setData(ArrayList<FSProduct> parkData)
    {
        m_Infos = parkData;
    }

    /**
     * The number of items in the list is determined by the number of speeches
     * in our array.
     *
     * @see android.widget.ListAdapter#getCount()
     */
    public int getCount() {
    	if (m_Infos == null)
    		return 0;

        return m_Infos.size();
    }

    /**
     * Since the data comes from an array, just returning the index is
     * sufficient to get at the data. If we were using a more complex data
     * structure, we would return whatever object represents one row in the
     * list.
     *
     * @see android.widget.ListAdapter#getItem(int)
     */
    public Object getItem(int position) {
        return position;
    }

    /**
     * Use the array index as a unique id.
     *
     * @see android.widget.ListAdapter#getItemId(int)
     */
    public long getItemId(int position) {
        return position;
    }

    private String[] state = { "finished", "subfloor"};

    /**
     * Make a view to hold each row.
     *
     * @see android.widget.ListAdapter#getView(int, android.view.View,
     *      android.view.ViewGroup)
     */
    public View getView(int position, View convertView, ViewGroup parent) {
        // When convertView is not null, we can reuse it directly, there is no need
        // to reinflate it. We only inflate a new View when the convertView supplied
        // by ListView is null.
        if (convertView == null) {
            convertView = mInflater.inflate(R.layout.products_item, null);
            ResolutionSet._instance.iterateChild(convertView.findViewById(R.id.RLProductsItemRoot));
        } else {
        }

        FSProduct _info = null;
        if (m_Infos != null)
        {
            _info = m_Infos.get(position);
        }


        Button btnProductEdit = (Button)convertView.findViewById(R.id.btnProductEdit);
        Button btnProductDelete = (Button)convertView.findViewById(R.id.btnProductDelete);
        Button btnEditDone = (Button)convertView.findViewById(R.id.btnEditDone);
        Button btnEditCancel = (Button)convertView.findViewById(R.id.btnCancel);

        Spinner spinner = (Spinner)convertView.findViewById(R.id.spinner);
        ArrayAdapter<CharSequence> adapter_state = ArrayAdapter.createFromResource(mContext, R.array.product_array, R.layout.custom_spiner);
        adapter_state.setDropDownViewResource(R.layout.custom_spiner);
        spinner.setAdapter(adapter_state);
        spinner.setSelection((int)_info.productType);
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                //To change body of implemented methods use File | Settings | File Templates.
            }
        });

        // Bind the data efficiently with the holder.
        if (_info != null)
        {
            ((TextView)convertView.findViewById(R.id.lblProductName)).setText(_info.productName);
            ((TextView)convertView.findViewById(R.id.lblType)).setText(FSProduct.getDisplayProductType((int)_info.productType));

            try {
                if (nEditingIdx == position)
                {
                    ((RelativeLayout) convertView.findViewById(R.id.RLShowBar)).setVisibility(View.GONE);
                    ((RelativeLayout) convertView.findViewById(R.id.RLEditBar)).setVisibility(View.VISIBLE);

                    ((EditText) convertView.findViewById(R.id.txtItemProductName)).setFocusable(true);
                    ((EditText) convertView.findViewById(R.id.txtItemProductName)).setFocusableInTouchMode(true);
                    ((EditText) convertView.findViewById(R.id.txtItemProductName)).requestFocus();

                    ((TextView) convertView.findViewById(R.id.txtItemCurrentType)).setText(FSProduct.getDisplayProductType((int)_info.productType));
                    ((EditText) convertView.findViewById(R.id.txtItemProductName)).setText(_info.productName);
                    ((EditText) convertView.findViewById(R.id.txtItemProductName)).setSelection(_info.productName.length());
                }

                //if (currentFocus != null)
                //    currentFocus.requestFocus();
            }
            catch (Exception e)
            {

            }
        }else
        {
            ((TextView)convertView.findViewById(R.id.lblProductName)).setText("");
            ((RelativeLayout)convertView.findViewById(R.id.RLEditBar)).setVisibility(View.GONE);
        }

        ProductItem itemInfo = new ProductItem();
        itemInfo.parentView = convertView;
        itemInfo.itemId = position;
        itemInfo.productName = (_info != null) ? _info.productName : "";
        itemInfo.productType = (_info != null) ? (int)_info.productType : FSProduct.FSPRODUCTTYPE_SUBFLOOR;

        btnProductEdit.setTag(itemInfo);
        btnProductEdit.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (currentFocus != null)
                    return;

                ProductItem itemInfo = (ProductItem) v.getTag();
                nEditingIdx = itemInfo.itemId;
                currentFocus = (EditText) itemInfo.parentView.findViewById(R.id.txtItemProductName);
                curSelectedType = itemInfo.productType;

                ((TextView) itemInfo.parentView.findViewById(R.id.txtItemCurrentType)).setText(FSProduct.getDisplayProductType((int)itemInfo.productType));

                ((RelativeLayout) itemInfo.parentView.findViewById(R.id.RLShowBar)).setVisibility(View.GONE);
                ((RelativeLayout) itemInfo.parentView.findViewById(R.id.RLEditBar)).setVisibility(View.VISIBLE);

                try
                {
                    currentFocus.setFocusable(true);
                    currentFocus.setFocusableInTouchMode(true);
                    currentFocus.setText(itemInfo.productName);
                    currentFocus.requestFocus();
                    currentFocus.setSelection(itemInfo.productName.length());
                }
                catch (Exception e)
                {
                    Log.e("error", e.toString());
                }

                InputMethodManager imm = (InputMethodManager) mContext.getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.showSoftInput(itemInfo.parentView.findViewById(R.id.txtItemProductName), InputMethodManager.SHOW_IMPLICIT);
            }
        });

        btnProductDelete.setTag(itemInfo);
        btnProductDelete.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (currentFocus != null)
                    return;

                ProductItem itemInfo = (ProductItem)v.getTag();
                mActivity.deleteProduct(itemInfo.itemId);
            }
        });

        btnEditDone.setTag(itemInfo);
        btnEditDone.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                currentFocus.setFocusable(false);
                currentFocus.setFocusableInTouchMode(false);
                currentFocus = null;
                nEditingIdx = -1;

                ProductItem itemInfo = (ProductItem)v.getTag();
                ((RelativeLayout)itemInfo.parentView.findViewById(R.id.RLShowBar)).setVisibility(View.VISIBLE);
                ((RelativeLayout)itemInfo.parentView.findViewById(R.id.RLEditBar)).setVisibility(View.GONE);

                curSelectedType = (int)((Spinner)itemInfo.parentView.findViewById(R.id.spinner)).getSelectedItemPosition();
                String strNewName = ((EditText)itemInfo.parentView.findViewById(R.id.txtItemProductName)).getText().toString();
                if (mActivity.changeProduct(itemInfo.itemId, strNewName, curSelectedType))
                {
                    ((TextView)itemInfo.parentView.findViewById(R.id.lblProductName)).setText(strNewName);
                    ((TextView)itemInfo.parentView.findViewById(R.id.txtItemCurrentType)).setText(FSProduct.getDisplayProductType(curSelectedType));
                }

                hideSoftKeyboard(itemInfo.parentView.findViewById(R.id.txtItemProductName));
                //hideSoftKeyboard(itemInfo.parentView);

                mActivity.hideSoftKeyboard();
            }
        });

        btnEditCancel.setTag(itemInfo);
        btnEditCancel.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                currentFocus.setFocusable(false);
                currentFocus.setFocusableInTouchMode(false);
                currentFocus = null;
                nEditingIdx = -1;

                ProductItem itemInfo = (ProductItem)v.getTag();
                ((RelativeLayout)itemInfo.parentView.findViewById(R.id.RLShowBar)).setVisibility(View.VISIBLE);
                ((RelativeLayout)itemInfo.parentView.findViewById(R.id.RLEditBar)).setVisibility(View.GONE);

                hideSoftKeyboard(itemInfo.parentView.findViewById(R.id.txtItemProductName));
                //hideSoftKeyboard(itemInfo.parentView);

                mActivity.hideSoftKeyboard();
            }
        });

        return convertView;
    }

    private void hideSoftKeyboard(View focusView)
    {
        InputMethodManager inputMethodManager = (InputMethodManager) mContext.getSystemService(Context.INPUT_METHOD_SERVICE);
        inputMethodManager.hideSoftInputFromWindow(focusView.getWindowToken(), InputMethodManager.HIDE_IMPLICIT_ONLY);
        inputMethodManager.hideSoftInputFromWindow(focusView.getWindowToken(), InputMethodManager.HIDE_IMPLICIT_ONLY);
        mActivity.getWindow().setSoftInputMode(
                WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
    }

    class ProductItem
    {
        public View parentView;
        public int itemId;
        public String productName;
        public int productType;
    }
}