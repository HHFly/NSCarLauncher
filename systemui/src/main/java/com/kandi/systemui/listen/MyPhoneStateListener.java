package com.kandi.systemui.listen;

import android.content.Context;
import android.telephony.PhoneStateListener;
import android.telephony.SignalStrength;
import android.widget.Toast;

public class MyPhoneStateListener extends PhoneStateListener {
    /* 从得到的信号强度,每个tiome供应商有更新 */
    private Context context;

    public void setContext(Context context) {
        this.context = context;
    }

    @Override
    public void onSignalStrengthsChanged(SignalStrength signalStrength) {
        super.onSignalStrengthsChanged(signalStrength);
//        Toast.makeText(context,
//                "Go to Firstdroid!!! GSM Cinr = " + String.valueOf(signalStrength.getGsmSignalStrength()),
//                Toast.LENGTH_SHORT).show();
//        System.out.println(String.valueOf(signalStrength.getGsmSignalStrength()) + "~~!~~");
    }
}
