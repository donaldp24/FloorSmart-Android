package com.tim.FloorSmart;

import android.app.Dialog;
import android.content.Context;

/**
 * Created by MaHanYong on 14-2-20.
 */
public class SelectViewDialog extends Dialog {
	Context mContext;
    public Object parentNode = null;
    public int mode = -1;
    public String retStr = "";

    public SelectViewDialog(Context context, int layout, int mode, Object parentNode)
    {
        super(context, layout);
    	mContext = context;
        this.mode = mode;
        this.retStr = "";
        this.parentNode = parentNode;
        SetView(R.layout.selectview);
    }

    public void SetView(int layoutName)
    {
        super.setContentView(layoutName);
        ResolutionSet._instance.iterateChild(findViewById(R.id.RLSelectViewRoot));
    }
}
