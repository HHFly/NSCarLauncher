package com.kandi.dell.nscarlauncher.ui_portrait.setting.fragment;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.AsyncTask;
import android.os.Handler;
import android.os.Message;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SwitchCompat;
import android.text.InputType;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.kandi.dell.nscarlauncher.R;
import com.kandi.dell.nscarlauncher.app.App;
import com.kandi.dell.nscarlauncher.base.fragment.BaseFragment;
import com.kandi.dell.nscarlauncher.common.util.EditTextUtils;
import com.kandi.dell.nscarlauncher.common.util.ToastUtils;
import com.kandi.dell.nscarlauncher.common.util.WifiUtil;
import com.kandi.dell.nscarlauncher.ui_portrait.home.HomePagerActivity;
import com.kandi.dell.nscarlauncher.ui_portrait.setting.adapter.WifiAdpter;
import com.kandi.dell.nscarlauncher.ui_portrait.setting.model.Wifiinfo;
import com.kandi.dell.nscarlauncher.widget.AddOneEtParamDialog;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;

public class WifiFragment extends BaseFragment implements CompoundButton.OnCheckedChangeListener {
    private HomePagerActivity homePagerActivity;
    private final int SCAN_WIFI = 1;
    private final int CURRENT_WIFI_ISNULL = 2;
    private final int CURRENT_WIFI_NOTNULL = 3;
    private WifiAdpter mAdapter;
    private WifiInfo currentWifiInfo;// 当前所连接的wifi
    List<Wifiinfo> datas = new ArrayList<Wifiinfo>();
    private WifiManager wifiManager;
    private List<ScanResult> wifiList;// wifi列表
    private boolean isScan;
    private SwitchCompat aSwitchCompat;

    public void setHomePagerActivity(HomePagerActivity homePagerActivity) {
        this.homePagerActivity = homePagerActivity;
    }

    @Override
    public int getContentResId() {
        return R.layout.activity_wifiset_portrait;
    }

    @Override
    public void findView() {

    }

    @Override
    public void setListener() {
        setClickListener(R.id.bt_back);
        setClickListener(R.id.ll_wifi_current);
    }

    @Override
    public void onClick(View v) {
      switch (v.getId()){
          case R.id.bt_back:
              homePagerActivity.getSetFragment().hideFragment();
              break;
          case R.id.ll_wifi_current:
              showCurrentWifiInfo(getContext());
              break;
      }
    }

    @Override

    public void initView() {
        aSwitchCompat= getView(R.id.isopen);
        aSwitchCompat.setOnCheckedChangeListener(this);
        wifiManager = (WifiManager) getContext().getApplicationContext().getSystemService(Context.WIFI_SERVICE);
        aSwitchCompat.setChecked(wifiManager.isWifiEnabled());

    }

    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        switch (buttonView.getId()){
            case R.id.isopen:
                if(isChecked){
                    wifiManager.setWifiEnabled(true);
                    setViewVisibility(R.id.ll_wifi,true);
                    isScan=true;
                    new ScanWifiThread().start();
                    new RefreshSsidThread().start();
                }else {
                    wifiManager.setWifiEnabled(false);
                    setViewVisibility(R.id.ll_wifi,false);
                    isScan =false;
                }
                break;

            default:
                break;
        }
    }
    // 显示现在连接的wifi的数据信息
    public void showCurrentWifiInfo(Context context){
        final Dialog dialog = new Dialog(context, R.style.nodarken_style);
        dialog.show();
        Window window = dialog.getWindow();
        window.setContentView(R.layout.dialog_wifi_info_show);
        window.setWindowAnimations(R.style.mystyle);
        TextView wifi_name = (TextView) window.findViewById(R.id.current_wifi_name);
        TextView wifi_ipAddress = (TextView) window.findViewById(R.id.current_wifi_ip);
        TextView wifi_netmask = (TextView) window.findViewById(R.id.current_wifi_submask);
        TextView wifi_gateway = (TextView) window.findViewById(R.id.current_wifi_gateway);
        if(currentWifiInfo!=null&&currentWifiInfo.getSSID()!=null) {
            wifi_name.setText(currentWifiInfo.getSSID().substring(1, currentWifiInfo.getSSID().length() - 1));
        }
        wifi_ipAddress.setText(WifiUtil.intToIp(wifiManager.getDhcpInfo().ipAddress));
        wifi_netmask.setText(WifiUtil.intToIp(wifiManager.getDhcpInfo().netmask));
        wifi_gateway.setText(WifiUtil.intToIp(wifiManager.getDhcpInfo().gateway));

        Button left_button = (Button) window.findViewById(R.id.current_wifi_left);
        Button right_button = (Button) window.findViewById(R.id.current_wifi_right);

        //忽略此网络操作
        left_button.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                wifiManager.removeNetwork(currentWifiInfo.getNetworkId());       //断开网络并忘记密码
                //wifiManager.disableNetwork(currentWifiInfo.getNetworkId());       //断开网络但不忘记密码
                wifiManager.disconnect();
                setViewVisibility(R.id.ll_wifi_current,false);
                dialog.dismiss();
                App.get().getCurActivity().initImmersionBar();
            }
        });
        //返回操作
        right_button.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                dialog.dismiss();
                App.get().getCurActivity().initImmersionBar();
            }
        });
    }

    // 显示现在连接的wifi的数据信息
    public void showWifiPassword(Context context,final Wifiinfo text){
        final Dialog dialog = new Dialog(context, R.style.nodarken_style);
        dialog.show();
        Window window = dialog.getWindow();
        window.setContentView(R.layout.dialog_wifi_input);
        window.setWindowAnimations(R.style.mystyle);
        TextView wifi_input_name = (TextView) window.findViewById(R.id.wifi_input_name);
        EditText wifi_input_password = (EditText) window.findViewById(R.id.wifi_input_password);
        ImageView iv_input_see = (ImageView) window.findViewById(R.id.iv_input_see);
        TextView wifi_input_cancel = (TextView) window.findViewById(R.id.wifi_input_cancel);
        TextView wifi_input_confirm = (TextView) window.findViewById(R.id.wifi_input_confirm);
        wifi_input_name.setText(text.name);
        EditTextUtils.openKeyBroad(wifi_input_password);
        iv_input_see.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(iv_input_see.isSelected()){
                    wifi_input_password.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
                    iv_input_see.setSelected(false);
                }else {
                    wifi_input_password.setInputType(InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);
                    iv_input_see.setSelected(true);
                }
            }
        });
        wifi_input_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EditTextUtils.hideKeyBroad(context,wifi_input_password);
                dialog.dismiss();
                App.get().getCurActivity().initImmersionBar();
            }
        });
        wifi_input_confirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String data = wifi_input_password.getText().toString();
                if (TextUtils.isEmpty(data)) {
                    return;
                }
                new ConnectWifiThread().execute(text.name, data, String.valueOf(text.security_type));
                EditTextUtils.hideKeyBroad(context,wifi_input_password);
                dialog.dismiss();
                App.get().getCurActivity().initImmersionBar();
            }
        });
        dialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
            @Override
            public void onCancel(DialogInterface dialog) {
                hintKeyBoard();
                dialog.dismiss();
                App.get().getCurActivity().initImmersionBar();
            }
        });
    }

    public void hintKeyBoard() {
        //拿到InputMethodManager
        InputMethodManager imm = (InputMethodManager) getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.toggleSoftInput(InputMethodManager.HIDE_IMPLICIT_ONLY, 0);
    }

    /**
     * 扫描wifi线程
     *
     * @author passing
     *
     */
    class ScanWifiThread extends Thread {

        @Override
        public void run() {
            while (isScan) {
                currentWifiInfo = wifiManager.getConnectionInfo();
                myHandler.sendMessage(myHandler.obtainMessage(SCAN_WIFI));
                try {
                    Thread.sleep(2000);
                } catch (InterruptedException e) {
                    break;
                }
            }
        }
    }

    /**
     * 获取网络ip地址
     *
     * @author passing
     *
     */
    class RefreshSsidThread extends Thread {

        @Override
        public void run() {
            while (isScan) {
                currentWifiInfo = wifiManager.getConnectionInfo();
                if (null != currentWifiInfo.getSSID() && 0 != currentWifiInfo.getIpAddress()) {
                    myHandler.sendEmptyMessage(CURRENT_WIFI_NOTNULL);
                } else {
                    myHandler.sendEmptyMessage(CURRENT_WIFI_ISNULL);
                }
                try {
                    sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    }
    /**
     * 扫描wifi
     */
    public void startScan() {
        datas.clear();
        wifiManager.startScan();
        HashSet<String> scanHisResult = new HashSet<String>();
        // 获取扫描结果
        wifiList = wifiManager.getScanResults();
        //Log.d("Wifi-Kangdi", "wifiList [ " + wifiList.toString() + "]");
        //Log.d("Wifi-Kangdi", "wifiList [ " + currentWifiInfo.getSSID() + "]");

        for (int i = 0; i < wifiList.size(); i++) {
            String tempStr = wifiList.get(i).SSID;
            if (isWifiConnected(getContext())== true){
                if(tempStr.compareTo(currentWifiInfo.getSSID().substring(1, currentWifiInfo.getSSID().length() - 1)) != 0) {
                    if(scanHisResult.contains(tempStr) == true){
                        continue;
                    }
                    else{
                        scanHisResult.add(tempStr);
                    }
                    Wifiinfo info = new Wifiinfo();
                    info.name = tempStr;
                    info.signal_intensity = getSignalIntensity(WifiManager.calculateSignalLevel(wifiList.get(i).level, 4));
                    info.security_type = getSecurity(wifiList.get(i));
                    //Log.d("Wifi-Kangdi", "[security_type] " + info.security_type);
                    info.state = getString(R.string.断开);
                    datas.add(info);
                }
            }else{
                if(scanHisResult.contains(tempStr) == true){
                    continue;
                }
                else{
                    scanHisResult.add(tempStr);
                }
                Wifiinfo info = new Wifiinfo();
                info.name = tempStr;
                info.signal_intensity = getSignalIntensity(WifiManager.calculateSignalLevel(wifiList.get(i).level, 4));
                info.security_type = getSecurity(wifiList.get(i));
                //Log.d("Wifi-Kangdi", "[security_type] " + info.security_type);
                info.state = getString(R.string.断开);
                datas.add(info);

            }
        }

        Collections.sort(datas, new ComparatorValues()); // 对获得的文件进行排序
    }
    public boolean isWifiConnected(Context context)
    {   try {
        ConnectivityManager connectivityManager = (ConnectivityManager)context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo wifiNetworkInfo = connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
        if(wifiNetworkInfo.isConnected())
        {
            return true ;
        }

        return false ;
    }catch (Exception e){
        return false ;
    }

    }
    public final class ComparatorValues implements Comparator<Wifiinfo> {

        @Override
        public int compare(Wifiinfo lhs, Wifiinfo rhs) {
            if (lhs.signal_intensity < rhs.signal_intensity) {
                return 1;
            } else {
                return -1;
            }
        }

    }
    /**
     * These values are matched in string arrays -- changes must be kept in sync
     */
    public final int SECURITY_NONE = 0;
    public final int SECURITY_WEP = 1;
    public final int SECURITY_PSK = 2;
    public final int SECURITY_EAP = 3;

    private int getSecurity(ScanResult result) {
        if (result.capabilities.contains("WEP")) {
            return SECURITY_WEP;
        } else if (result.capabilities.contains("PSK")) {
            return SECURITY_PSK;
        } else if (result.capabilities.contains("EAP")) {
            return SECURITY_EAP;
        }
        return SECURITY_NONE;
    }
    // 获取不同信号强度wifi图片
    public int getSignalIntensity(int num) {
        if (num == 4) {
            return R.mipmap.home_top_btn5_05;
        } else if (num == 3) {
            return R.mipmap.home_top_btn5_04;
        } else if (num == 2) {
            return R.mipmap.home_top_btn5_03;
        }else if (num == 1) {
            return R.mipmap.home_top_btn5_02;
        }else if (num == 0) {
            return R.mipmap.home_top_btn5_01;
        }
        else {
            return R.mipmap.home_top_btn5_01;
        }
    }
    public Handler myHandler = new Handler() {
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case SCAN_WIFI:
                    startScan();
                   initRvAdapter(datas);
                    break;
                case CURRENT_WIFI_NOTNULL:
                    if(currentWifiInfo!=null&&currentWifiInfo.getSSID().length()>2) {
                        setTvText(R.id.wifi_current_name, currentWifiInfo.getSSID().substring(1, currentWifiInfo.getSSID().length() - 1));

//                        homePagerActivity.setWifiLevel();
                       setViewVisibility(R.id.ll_wifi_current,true);
                    }
                    break;
                case CURRENT_WIFI_ISNULL:
                    setViewVisibility(R.id.ll_wifi_current,false);
//                    wifi_current_name.setText("");
//                    wifi_current_state.setVisibility(View.INVISIBLE);
//                    wifi_current_info.setVisibility(View.INVISIBLE);
//                    homePagerActivity.setWifiLevel();

                    break;

                default:
                    break;
            }
        };
    };
    /**
     * 初始化adapter
     *
     *
     */
    private void initRvAdapter( List<Wifiinfo> data) {
        if (mAdapter == null) {
            RecyclerView rv = getView(R.id.recyclerView_wifi);
            mAdapter =new WifiAdpter(data);

            if (rv != null) {
                LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
                rv.setLayoutManager(linearLayoutManager);
                rv.setAdapter(mAdapter);
            }
            mAdapter.setOnItemClickListener(new WifiAdpter.OnItemClickListener() {


                @Override
                public void onClick(Wifiinfo data) {
                    showWifiPassword(getContext(),data);
                }
            });

        }else {
            mAdapter.notifyData(data,true);
        }

    }
    //    填写信息dialog
    private  void  ShowDialog(final Wifiinfo text){
        AddOneEtParamDialog mAddOneEtParamDialog = AddOneEtParamDialog.getInstance(false,text.name,1);

        mAddOneEtParamDialog.setOnDialogClickListener(new AddOneEtParamDialog.DefOnDialogClickListener() {
            @Override
            public void onClickCommit(AddOneEtParamDialog dialog, String data) {

                new ConnectWifiThread().execute(text.name, data, String.valueOf(text.security_type));
                dialog.dismiss();
                App.get().getCurActivity().initImmersionBar();

            }

            @Override
            public void onClickCancel(AddOneEtParamDialog dialog) {
                App.get().getCurActivity().initImmersionBar();
                dialog.dismiss();
            }
        });

        mAddOneEtParamDialog.show(this.getFragmentManager());
    }

/**
 * 连接wifi
 *
 * @author passing
 *
 */
class ConnectWifiThread extends AsyncTask<String, Integer, String> {

    @Override
    protected String doInBackground(String... params) {
        int security_type = Integer.parseInt(params[2]);
        // 连接配置好指定ID的网络
        //Log.d("Wifi-Kangdi", "[doInBackground security_type] " + security_type);
        WifiConfiguration config = WifiUtil.createWifiInfo(params[0], params[1], security_type, wifiManager);
        int networkId = wifiManager.addNetwork(config);
        if (null != config) {
            wifiManager.enableNetwork(networkId, true);
            wifiManager.saveConfiguration();
            return params[0];
        }
        return null;
    }

    @Override
    protected void onPostExecute(String result) {
        if (null != result) {
            System.out.println("连接成功!");
        } else {
            System.out.println("连接失败!");
        }
        super.onPostExecute(result);
    }

}
}
