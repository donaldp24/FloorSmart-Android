package com.tim.FloorSmart;

import android.app.Dialog;
import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.widget.TextView;


/**
 * Created by MaHanYong on 14-2-20.
 */
public class WarningAlertDialog extends Dialog {
    Context mContext;
    Handler handler;
    public WarningAlertDialog(Context context, int layout, String title, String body)
    {
        super(context, layout);
        mContext = context;
        SetView(R.layout.message_alert_dialog);

        handler = new Handler()
        {
            @Override
            public void handleMessage(Message msg)
            {
                if (msg.what == 0)
                {
                    dismiss();
                }
            }
        };

        handler.sendEmptyMessageDelayed(0, 5000);

        ((TextView)findViewById(R.id.txtTitle)).setText(title);
        ((TextView)findViewById(R.id.txtBody)).setText(body);
    }

    public void SetView(int layoutName)
    {
        super.setContentView(layoutName);
        ResolutionSet._instance.iterateChild(findViewById(R.id.RLConfirmRoot));

    }
}
