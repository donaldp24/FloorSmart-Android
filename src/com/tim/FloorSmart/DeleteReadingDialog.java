package com.tim.FloorSmart;

import android.app.Dialog;
import android.content.Context;
import android.widget.TextView;

/**
 * Created by MaHanYong on 14-2-20.
 */
public class DeleteReadingDialog extends Dialog {
    Context mContext;
    String label = "";
    public DeleteReadingDialog(Context context, int layout, String label)
    {
        super(context, layout);
        mContext = context;
        this.label = label;
        SetView(R.layout.deletereading_confirm_dialog);
    }

    public void SetView(int layoutName)
    {
        super.setContentView(layoutName);
        ResolutionSet._instance.iterateChild(findViewById(R.id.RLConfirmRoot));

        ((TextView)findViewById(R.id.lblCurReading)).setText(label);
        /*
        Button btnOK = (Button)findViewById(R.id.btnOK);
        btnOK.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View v) {
                dismiss();
            }

        });

        Button btnCancel = (Button)findViewById(R.id.btnCancel);
        btnCancel.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View v) {
                dismiss();
            }

        });        */
    }
}
