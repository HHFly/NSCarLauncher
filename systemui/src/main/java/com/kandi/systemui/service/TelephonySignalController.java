package com.kandi.systemui.service;

import android.content.Context;
import android.telephony.PhoneStateListener;
import android.telephony.SignalStrength;
import android.telephony.TelephonyManager;
import android.util.Log;

public class TelephonySignalController {


    KandiSystemUiService mService;
    Context mContext;
    private TelephonyManager mPhone;
    
    public TelephonySignalController(KandiSystemUiService service){
        mService = service;
        mContext = service;
        String TAG = "TelephonySignalController";
        PhoneStateListener mPhoneStateListener = new PhoneStateListener() {
            @Override
            public void onSignalStrengthsChanged(SignalStrength signalStrength) {
            	int level = signalStrength.getGsmSignalStrength();
//            	Log.d("huachao", "telephone level:" + level);
            	mService.TopRefreshNetworkEvent(level,mPhone.getNetworkType());
            }
        };
        if (mPhone == null) {
            try {
                mPhone = (TelephonyManager) mContext.getSystemService(Context.TELEPHONY_SERVICE);;
            } catch (Exception ex) {
                Log.e(TAG, ex.toString());
                return;
            }
            mPhone.listen(mPhoneStateListener,
                    PhoneStateListener.LISTEN_SERVICE_STATE
                  | PhoneStateListener.LISTEN_SIGNAL_STRENGTHS
                  | PhoneStateListener.LISTEN_CALL_STATE
                  | PhoneStateListener.LISTEN_DATA_CONNECTION_STATE
                  | PhoneStateListener.LISTEN_DATA_ACTIVITY);
        }
        
    }
}
