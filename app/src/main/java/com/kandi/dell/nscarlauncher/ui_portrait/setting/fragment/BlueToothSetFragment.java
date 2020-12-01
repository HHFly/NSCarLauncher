package com.kandi.dell.nscarlauncher.ui_portrait.setting.fragment;

import android.app.Dialog;
import android.os.Handler;
import android.os.IKdBtService;
import android.os.Message;
import android.os.RemoteException;
import android.os.ServiceManager;
import android.os.SystemProperties;
import android.support.v7.widget.SwitchCompat;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.TextView;

import com.kandi.dell.nscarlauncher.R;
import com.kandi.dell.nscarlauncher.app.App;
import com.kandi.dell.nscarlauncher.base.fragment.BaseFragment;
import com.kandi.dell.nscarlauncher.ui.bluetooth.FlagProperty;
import com.kandi.dell.nscarlauncher.ui_portrait.home.HomePagerActivity;
import com.kandi.dell.nscarlauncher.ui.home.androideunm.FragmentType;
import com.kandi.dell.nscarlauncher.widget.CustomDialog;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;

public class BlueToothSetFragment extends BaseFragment{
    private HomePagerActivity homePagerActivity;
    private final int BLUETOOTH_INFO_NAME = 1;
    private final int BLUETOOTH_CHANGE_NAME = 2;
    private final int BLUETOOTH_CHANGE_PASSWORD = 3;
    private final int BLUETOOTH_ATTACHED = 4;
    File file = new File("/sdcard/kandi/bluetoothPassword.txt");
    String[] info = new String[1]; // 获取蓝牙名字传入参数
    String newName = "";
    String newPassword = "";
    IKdBtService btservice = IKdBtService.Stub.asInterface(ServiceManager.getService("bt"));

    TextView tv_set_bluetooth_name;
    TextView tv_set_bluetooth_password;
    public int originId = 0;//来源与那个fragment用于隐藏蓝牙设置界面
    public void setOriginId(int originId){
        this.originId = originId;
    }

    public int getOriginId(){
        return originId;
    }

    public void setHomePagerActivity(HomePagerActivity homePagerActivity) {
        this.homePagerActivity = homePagerActivity;
    }

    @Override
    public void setmType(int mType) {
        super.setmType(FragmentType.BTSET);
    }

    @Override
    public void initView() {
        setmType(FragmentType.BTSET);
        myHandler.sendMessage(myHandler.obtainMessage(BLUETOOTH_INFO_NAME));

    }

    @Override
    public int getContentResId() {

            return R.layout.activity_btset_portrait;
    }

    @Override
    public void findView() {
        tv_set_bluetooth_name=getView(R.id.bt_name);
        tv_set_bluetooth_password=getView(R.id.bt_pin);
    }

    @Override
    public void setListener() {
        setClickListener(R.id.bt_back);
        setClickListener(R.id.ll_name);
        setClickListener(R.id.ll_pin);

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.bt_back:
                if(getOriginId() == 0){
                    homePagerActivity.getSetFragment().hideFragment();
                }else if(getOriginId() == 1){
//                    homePagerActivity.isShowPhoneAnim=false;
                    homePagerActivity.jumpFragment(FragmentType.BTMUSIC);

                }else{
//                    homePagerActivity.isShowPhoneAnim=false;
                    homePagerActivity.jumpFragment(FragmentType.PHONE);

                }
                break;
            case R.id.ll_name:
                ChangeName();
                break;
            case R.id.ll_pin:
                ChangePass();
                break;
        }
    }
    public void hideDialog(){
        if(dialogName!=null){
            dialogName.dismiss();
            dialogName=null;

        }if(dialogPass!=null){
            dialogPass.dismiss();
            dialogPass=null;
        }

    }
/*蓝牙名称*/
  private    Dialog dialogName;
    private void ChangeName() {
        dialogName = new CustomDialog(getContext(), R.style.nodarken_style);

        Window window = dialogName.getWindow();
        window.setContentView(R.layout.dialog_bluetooth_change_name);
        TextView bluetooth_previous_name = (TextView) window.findViewById(R.id.dialog_bluetooth_previous_name);
        final EditText bluetooth_new_name = (EditText) window.findViewById(R.id.bluetooth_input_new_name);
        bluetooth_previous_name.setText(info[0]);
        TextView bt_confirm = (TextView) window.findViewById(R.id.bluetooth_input_confirm_name);
        TextView bt_cancel = (TextView) window.findViewById(R.id.bluetooth_input_cancel_name);

        bt_confirm.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                newName = bluetooth_new_name.getText().toString().trim();
                if (newName.compareTo("") != 0) {
//                    System.out.println("新名称： " + newName);
                    new Thread() {
                        public void run() {
                            int result = -1;
                            try {
                                result = btservice.btSetLocalName(newName);
//                                System.out.println("改名称结果:" + result);
                            } catch (RemoteException e) {
                                e.printStackTrace();
                            }
                            if (result == 0) {
                                myHandler.sendMessage(myHandler.obtainMessage(BLUETOOTH_CHANGE_NAME));
                            }
                        };
                    }.start();
                    dialogName.dismiss();
                    dialogName=null;
                    App.get().getCurActivity().initImmersionBar();
                }
            }
        });

        bt_cancel.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                dialogName.dismiss();
                dialogName=null;
                App.get().getCurActivity().initImmersionBar();
            }
        });
        dialogName.show();
    }
    private Dialog dialogPass;

        /*更改密码*/
    private void ChangePass() {
        dialogPass = new CustomDialog(getContext(), R.style.nodarken_style);

        Window window = dialogPass.getWindow();
        window.setContentView(R.layout.dialog_bluetooth_change_password);
        TextView bluetooth_previous_password = (TextView) window
                .findViewById(R.id.dialog_bluetooth_previous_password);
        final EditText bluetooth_new_password = (EditText) window
                .findViewById(R.id.bluetooth_input_new_password);
        bluetooth_previous_password.setText(FlagProperty.BtCode);
        TextView bt_confirm = (TextView) window.findViewById(R.id.bluetooth_input_confirm_password);
        TextView bt_cancel = (TextView) window.findViewById(R.id.bluetooth_input_cancel_password);

        bt_confirm.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                newPassword = bluetooth_new_password.getText().toString().trim();
                if (newPassword.compareTo("") != 0) {
//                    System.out.println("新密码： " + newPassword);
                    new Thread() {
                        public void run() {
                            FlagProperty.BtCode = newPassword;
                            writePasswordToFile(FlagProperty.BtCode);
                            myHandler.sendMessage(myHandler.obtainMessage(BLUETOOTH_CHANGE_PASSWORD));
                        };
                    }.start();
                    dialogPass.dismiss();
                    dialogPass=null;
                    App.get().getCurActivity().initImmersionBar();
                }
            }
        });

        bt_cancel.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                dialogPass.dismiss();
                dialogPass=null;
                App.get().getCurActivity().initImmersionBar();
            }
        });
        dialogPass.show();
    }
    public void BluetoothInit() {
        bluetoothPasswordInit();
        new MyBluetoothStateThread().start();
    }
    // 配置蓝牙密码初始化
    public void bluetoothPasswordInit() {
        if (file.exists()) {
            String res = "";
            try {
                FileInputStream fin = new FileInputStream(file);
                int length = fin.available();
                byte[] buffer = new byte[length];
                fin.read(buffer);
                res =   new String(buffer, "utf-8");
                fin.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
            FlagProperty.BtCode = res;
        } else {
            File file_dir = new File("/sdcard/kandi/");
            if (!file_dir.exists()) {
                file_dir.mkdirs();
            }
            try {
                file.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
            writePasswordToFile(FlagProperty.BtCode);
        }
    }

    //蓝牙密码写入文件中
    public void writePasswordToFile(String word){
        try {
            FileOutputStream fout = new FileOutputStream(file);
            byte[] bytes = word.getBytes();
            fout.write(bytes);
            fout.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    public class MyBluetoothStateThread extends Thread{
        @Override
        public void run() {
            while(!FlagProperty.flag_bluetooth){
                Log.d("testbt", "sys.kd.btconnected" + SystemProperties.get("sys.kd.btconnected"));
                if (SystemProperties.get("sys.kd.btconnected").compareTo("yes") == 0) {
                    FlagProperty.flag_bluetooth = true;
                    myHandler.sendEmptyMessage(BLUETOOTH_ATTACHED);
                }

                try {
                    sleep(3000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    }
    public Handler myHandler = new Handler() {
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case BLUETOOTH_INFO_NAME:
                    try {
                        tv_set_bluetooth_name.setText(info[btservice.btGetLocalName(info)]);
                        tv_set_bluetooth_password.setText(FlagProperty.BtCode);
                    } catch (RemoteException e) {
                        e.printStackTrace();
                    }
                    break;
                case BLUETOOTH_CHANGE_NAME:
                    tv_set_bluetooth_name.setText( newName);
                    info[0] = newName;
                    break;
                case BLUETOOTH_CHANGE_PASSWORD:
                    tv_set_bluetooth_password.setText(FlagProperty.BtCode);
                    break;
                case BLUETOOTH_ATTACHED:
//                    HomeTitleLayout.setBluetooth(1);
                    //状态栏
                    break;

                default:
                    break;
            }
        };
    };



}
