package com.kandi.dell.nscarlauncher.ui.tachograph;

import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.SurfaceTexture;
import android.os.Handler;
import android.text.TextUtils;
import android.view.TextureView;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.Toast;

import com.hikvision.dashcamsdkpre.BaseBO;
import com.hikvision.dashcamsdkpre.CorrTimeDTO;
import com.hikvision.dashcamsdkpre.DashcamApi;
import com.hikvision.dashcamsdkpre.EventNotificationBO;
import com.hikvision.dashcamsdkpre.GetBasicCapabilitiesBO;
import com.hikvision.dashcamsdkpre.GetCapabilitiesBO;
import com.hikvision.dashcamsdkpre.GetImageCapabilitiesBO;
import com.hikvision.dashcamsdkpre.GetIntelligentCapabilitiesBO;
import com.hikvision.dashcamsdkpre.GetNetworkCapabilitiesBO;
import com.hikvision.dashcamsdkpre.GetStorageCapabilitiesBO;
import com.hikvision.dashcamsdkpre.NormalNotificationBO;
import com.hikvision.dashcamsdkpre.StartRecordBO;
import com.hikvision.dashcamsdkpre.StartRecordDTO;
import com.hikvision.dashcamsdkpre.StartSessionBO;
import com.hikvision.dashcamsdkpre.StartSessionDTO;
import com.hikvision.dashcamsdkpre.StopRecordBO;
import com.hikvision.dashcamsdkpre.StopRecordDTO;
import com.hikvision.dashcamsdkpre.TakePhotoBO;
import com.hikvision.dashcamsdkpre.TakePhotoDTO;
import com.hikvision.dashcamsdkpre.api.CapabilityApi;
import com.hikvision.dashcamsdkpre.api.ControlApi;
import com.hikvision.dashcamsdkpre.api.SessionApi;
import com.hikvision.dashcamsdkpre.enums.ChannelType;
import com.hikvision.dashcamsdkpre.enums.ClientType;
import com.hikvision.dashcamsdkpre.enums.RecordType;
import com.hikvision.dashcamsdkpre.listener.ConnectListener;
import com.hikvision.dashcamsdkpre.listener.DashcamResponseListener;
import com.hikvision.dashcamsdkpre.listener.NotificationListener;
import com.hikvision.dashcamsdkpre.util.DashcamLog;
import com.hikvision.playerlibrary.HikPreviewPlayer;
import com.hikvision.playerlibrary.HikVideoModel;
import com.kandi.dell.nscarlauncher.R;
import com.kandi.dell.nscarlauncher.base.fragment.BaseFragment;
import com.kandi.dell.nscarlauncher.common.util.FragmentUtils;
import com.kandi.dell.nscarlauncher.ui.bluetooth.FlagProperty;
import com.kandi.dell.nscarlauncher.ui.home.HomePagerActivity;
import com.kandi.dell.nscarlauncher.ui.home.androideunm.FragmentType;
import com.kandi.dell.nscarlauncher.ui.tachograph.fragment.ListFragment;
import com.kandi.dell.nscarlauncher.ui.tachograph.fragment.SettingsFragment;
import com.kandi.dell.nscarlauncher.ui.tachograph.member.GlobalInfo;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class DvrFragment extends BaseFragment {

    //预览播放器
    private HikPreviewPlayer hikPreviewPlayer;
    //预览视频加载控件
    private TextureView tvRadio;
    //路径
    private String address;
    //行车记录仪应用程序接口
    private DashcamApi mDashcamApi;
    //通知数目
    private int mNotificationNumber;
    //计数器
    private int count;
    //手动录像按钮
    private Button btRec;
    //抓拍按钮
    private Button btCatch;
    //五连拍按钮
    private Button btFive;
    //缩时录像按钮
    private Button btShort;

    private Button btnPreview;

    private boolean isRecording;
    private boolean isRecording2;
    //缓冲线程池
    private ExecutorService mCacheThreadPool = Executors.newCachedThreadPool();

    public BaseFragment mCurFragment;//当前页
    public ListFragment listFragment;
    public SettingsFragment settingsFragment;

    @Override
    public void setmType(int mType) {
        super.setmType(FragmentType.DVR);
    }

    @Override
    public int getContentResId() {
        return R.layout.fragment_dvr_main;
    }

    @Override
    public void setListener() {
        setClickListener(R.id.bt_rec);
        setClickListener(R.id.bt_catch);
        setClickListener(R.id.bt_five);
        setClickListener(R.id.bt_short);
        setClickListener(R.id.ib_photo);
        setClickListener(R.id.ib_set);
        setClickListener(R.id.bt_event);
        setClickListener(R.id.bt_cycle);
        setClickListener(R.id.bt_user);
        setClickListener(R.id.btn_correct);
        setClickListener(R.id.btn_get_capabilities);
        setClickListener(R.id.btn_disconnect);
        setClickListener(R.id.btn_preview);
        tvRadio.setSurfaceTextureListener(new TextureView.SurfaceTextureListener() {
            @Override
            public void onSurfaceTextureAvailable(SurfaceTexture surfaceTexture, int i, int i1) {
//                startPreview();
            }

            @Override
            public void onSurfaceTextureSizeChanged(SurfaceTexture surfaceTexture, int i, int i1) {
            }

            @Override
            public boolean onSurfaceTextureDestroyed(SurfaceTexture surfaceTexture) {
                return false;
            }

            @Override
            public void onSurfaceTextureUpdated(SurfaceTexture surfaceTexture) {
            }
        });
    }

    @Override
    public void initView() {
        setmType(FragmentType.DVR);
        initConnect();
        mNotificationNumber = mDashcamApi.addNotificationListener(mNotificationListener);
        listFragment = new ListFragment();
        settingsFragment = new SettingsFragment();
    }

    @Override
    public void Resume() {
        super.Resume();
        if(mDashcamApi != null){
            if(!mDashcamApi.isConnected()){
                initConnect();
            }
            mNotificationNumber = mDashcamApi.addNotificationListener(mNotificationListener);
        }
    }

    /*需要在关闭的时候调用*/
    public void Pause() {
        if(mDashcamApi != null){
            mDashcamApi.removeNotificationListener(mNotificationNumber);
        }
    }

    @Override
    public void findView() {
        btRec = getView(R.id.bt_rec);
        btCatch = getView(R.id.bt_catch);
        btFive = getView(R.id.bt_five);
        btShort = getView(R.id.bt_short);
        btnPreview = getView(R.id.btn_preview);
        tvRadio = getView(R.id.tv_radio);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.btn_correct:
                correctTime();
                break;
            case R.id.btn_get_capabilities:
                getCapabilities();
                break;
            case R.id.btn_disconnect:
                stopSession();
                break;
            case R.id.btn_preview:
                if (!isPreview) {
                    btnPreview.setText(getString(R.string.stop));
                    startPreview();
                } else {
                    btnPreview.setText(getString(R.string.preview));
                    stopPreview();
                }
                break;
        }
        Intent intent;
        //如果没有连接弹出提示
        if (!mDashcamApi.isConnected()) {
            Toast.makeText(getContext(), "no link!", Toast.LENGTH_SHORT).show();
            return;
        }
        switch (v.getId()) {
            case R.id.bt_rec:
                //手动录像按钮

                Runnable runnable1 = new Runnable() {
                    @Override
                    public void run() {
                        StartRecordDTO startRecordDTO = new StartRecordDTO();
                        startRecordDTO.setChanNo(1);
                        startRecordDTO.setRecordType(RecordType.MANUAL_RECORD);
                        //控制类指令
                        ControlApi.startRecord(startRecordDTO, new DashcamResponseListener<StartRecordBO>() {
                            @Override
                            public void onDashcamResponseSuccess(StartRecordBO startRecordBO) {
                                getActivity().runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        Toast.makeText(getContext(), R.string.handsuc, Toast.LENGTH_SHORT).show();
                                    }
                                });

                            }

                            @Override
                            public void onDashcamResponseFailure(final BaseBO baseBO) {
                                getActivity().runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        Toast.makeText(getContext(), getErrorMsg(baseBO.getResult(), getContext()), Toast.LENGTH_SHORT).show();
                                    }
                                });
                            }
                        });
                    }
                };
                mCacheThreadPool.execute(runnable1);
                break;
            case R.id.bt_catch:
                //抓拍按钮
                Runnable runnable2 = new Runnable() {
                    @Override
                    public void run() {
                        TakePhotoDTO takePhotoDTO = new TakePhotoDTO();
                        takePhotoDTO.setChannelType(ChannelType.FRONT);
                        takePhotoDTO.setInterval(3);
                        takePhotoDTO.setNumber(1);
                        //控制类指令
                        ControlApi.takePhoto(takePhotoDTO, new DashcamResponseListener<TakePhotoBO>() {
                            @Override
                            public void onDashcamResponseSuccess(final TakePhotoBO takePhotoBO) {
                                getActivity().runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        String filePath = takePhotoBO.getFilePath();
                                        if (!TextUtils.isEmpty(filePath)) {
                                            Toast.makeText(getContext(), filePath, Toast.LENGTH_SHORT).show();
                                        }
                                    }
                                });
                            }

                            @Override
                            public void onDashcamResponseFailure(final BaseBO baseBO) {
                                getActivity().runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        Toast.makeText(getContext(), getErrorMsg(baseBO.getResult(), getContext()), Toast.LENGTH_SHORT).show();
                                    }
                                });
                            }
                        });
                    }
                };
                mCacheThreadPool.execute(runnable2);
                break;
            case R.id.bt_five:
                //五连拍按钮
                Runnable runnable3 = new Runnable() {
                    @Override
                    public void run() {
                        TakePhotoDTO takePhotoDTO = new TakePhotoDTO();
                        takePhotoDTO.setChannelType(ChannelType.FRONT);
                        takePhotoDTO.setInterval(3);
                        takePhotoDTO.setNumber(5);
                        //控制类指令
                        ControlApi.takePhoto(takePhotoDTO, new DashcamResponseListener<TakePhotoBO>() {
                            @Override
                            public void onDashcamResponseSuccess(final TakePhotoBO takePhotoBO) {
                                getActivity().runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        String filePath = takePhotoBO.getFilePath();
                                        if (!TextUtils.isEmpty(filePath)) {
                                            Toast.makeText(getContext(), filePath, Toast.LENGTH_SHORT).show();
                                        }
                                    }
                                });
                            }

                            @Override
                            public void onDashcamResponseFailure(final BaseBO baseBO) {
                                getActivity().runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        Toast.makeText(getContext(), getErrorMsg(baseBO.getResult(), getContext()), Toast.LENGTH_SHORT).show();
                                    }
                                });

                            }
                        });
                    }
                };
                mCacheThreadPool.execute(runnable3);
                break;
            case R.id.bt_short:
                //缩时录像按钮
                if (!isRecording2) {
                    isRecording2 = true;
                    btShort.setText(getString(R.string.stop));

                    Runnable runnable4 = new Runnable() {
                        @Override
                        public void run() {
                            StartRecordDTO startRecordDTO = new StartRecordDTO();
                            startRecordDTO.setChanNo(1);
                            startRecordDTO.setRecordType(RecordType.TIMELAPSE_RECORD);
                            //控制类指令
                            ControlApi.startRecord(startRecordDTO, new DashcamResponseListener<StartRecordBO>() {
                                @Override
                                public void onDashcamResponseSuccess(StartRecordBO startRecordBO) {
                                    getActivity().runOnUiThread(new Runnable() {
                                        @Override
                                        public void run() {
                                            Toast.makeText(getContext(), R.string.cutsuc, Toast.LENGTH_SHORT).show();
                                        }
                                    });
                                }

                                @Override
                                public void onDashcamResponseFailure(final BaseBO baseBO) {
                                    getActivity().runOnUiThread(new Runnable() {
                                        @Override
                                        public void run() {
                                            Toast.makeText(getContext(), getErrorMsg(baseBO.getResult(), getContext()), Toast.LENGTH_SHORT).show();
                                        }
                                    });
                                }
                            });

                        }
                    };
                    mCacheThreadPool.execute(runnable4);
                } else {
                    isRecording2 = false;
                    btShort.setText(getString(R.string.sslx));

                    Runnable runnable4 = new Runnable() {
                        @Override
                        public void run() {
                            StopRecordDTO stopRecordDTO = new StopRecordDTO();
                            stopRecordDTO.setChanNo(1);
                            stopRecordDTO.setRecordType(RecordType.TIMELAPSE_RECORD);
                            //控制类指令
                            ControlApi.stopRecord(stopRecordDTO, new DashcamResponseListener<StopRecordBO>() {
                                @Override
                                public void onDashcamResponseSuccess(StopRecordBO startRecordBO) {
                                    getActivity().runOnUiThread(new Runnable() {
                                        @Override
                                        public void run() {
                                            Toast.makeText(getContext(), R.string.stopsuc, Toast.LENGTH_SHORT).show();
                                        }
                                    });

                                }

                                @Override
                                public void onDashcamResponseFailure(final BaseBO baseBO) {
                                    getActivity().runOnUiThread(new Runnable() {
                                        @Override
                                        public void run() {
                                            Toast.makeText(getContext(), getErrorMsg(baseBO.getResult(), getContext()), Toast.LENGTH_SHORT).show();
                                        }
                                    });
                                }
                            });
                        }
                    };
                    mCacheThreadPool.execute(runnable4);
                }
                break;
            case R.id.ib_photo:
                //switchfragment
                FlagProperty.extra = null;
                switchFragment(listFragment);
                break;
            case R.id.ib_set:
                //设置按钮
                switchFragment(settingsFragment);
                break;
            case R.id.bt_event:
                //switchfragment
                FlagProperty.extra = "event";
                switchFragment(listFragment);
                break;
            case R.id.bt_cycle:
                //switchfragment
                FlagProperty.extra = "cycle";
                switchFragment(listFragment);
                break;
            case R.id.bt_user:
                //switchfragment
                FlagProperty.extra = "user";
                switchFragment(listFragment);
                break;
        }
    }

    /**
     * 选择Fragment
     *
     * @param fragment
     */

    public void switchFragment(BaseFragment fragment) {
        getView(R.id.frame_dvr_main).setVisibility(View.VISIBLE);
        mCurFragment = FragmentUtils.selectFragment(getActivity(), mCurFragment, fragment, R.id.frame_dvr_main);
//        mCurFragment.setmType(fragment.getmType());
        mCurFragment.Resume();
    }

    public void hideFragmentNonstatic(){
        getView(R.id.frame_dvr_main).setVisibility(View.GONE);
    }

    public void initConnect(){
        mDashcamApi = DashcamApi.getInstance();
        DashcamLog.setDebug(true);
        //AP模式
        mDashcamApi.setDeviceAddress("192.168.42.1");
        mDashcamApi.setPort(7878);
        mDashcamApi.connect(new ConnectListener() {
            @Override
            public void onConnectSuccess() {
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(getContext(), "设备连接成功", Toast.LENGTH_SHORT).show();
                        GlobalInfo.sDeviceIp = mDashcamApi.getDeviceAddress();
                        StartSessionDTO dto = new StartSessionDTO();
                        dto.setClientType(ClientType.PHONE_APP);
                        SessionApi.startSession(dto, new DashcamResponseListener<StartSessionBO>() {
                            @Override
                            public void onDashcamResponseSuccess(StartSessionBO startSessionBO) {
                                getActivity().runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        Toast.makeText(getContext(), "session成功", Toast.LENGTH_SHORT).show();
                                    }
                                });
                            }

                            @Override
                            public void onDashcamResponseFailure(BaseBO baseBO) {

                            }
                        });
                    }
                });
            }

            @Override
            public void onConnectFailed() {
                Toast.makeText(getContext(), "session失败", Toast.LENGTH_SHORT).show();
            }
        });
    }

    /**
     * 断开连接
     */
    private void stopSession() {
        SessionApi.stopSession(new DashcamResponseListener<BaseBO>() {
            @Override
            public void onDashcamResponseSuccess(BaseBO bo) {
                disconnect();
//                finish();
                HomePagerActivity.homePagerActivity.hideFragment();
            }

            @Override
            public void onDashcamResponseFailure(BaseBO bo) {
                disconnect();
//                finish();
                HomePagerActivity.homePagerActivity.hideFragment();
            }
        });
    }

    /**
     * 时间校时
     */
    private void correctTime() {
        CorrTimeDTO dto = new CorrTimeDTO();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
        dto.setTime(sdf.format(new Date()));
        ControlApi.corrTime(dto, new DashcamResponseListener<BaseBO>() {
            @Override
            public void onDashcamResponseSuccess(BaseBO bo) {
                Toast.makeText(getContext(), getString(R.string.corr_time_success), Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onDashcamResponseFailure(final BaseBO bo) {
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(getContext(), bo.getErrorMsg(), Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });
    }

    private GetCapabilitiesBO mCapabilitiesBO;

    /**
     * 获取设备顶层能力集
     */
    private void getCapabilities() {
        CapabilityApi.getCapabilities(new DashcamResponseListener<GetCapabilitiesBO>() {
            @Override
            public void onDashcamResponseSuccess(GetCapabilitiesBO bo) {
                mCapabilitiesBO = bo;
                getBasicCapabilities();
            }

            @Override
            public void onDashcamResponseFailure(BaseBO bo) {
                getBasicCapabilities();
            }
        });
    }

    /**
     * 获取基础能力集
     */
    private void getBasicCapabilities() {
        if (!mCapabilitiesBO.hasBasicCapability()) {
            getImageCapabilities();
            return;
        }
        CapabilityApi.getBasicCapabilities(new DashcamResponseListener<GetBasicCapabilitiesBO>() {
            @Override
            public void onDashcamResponseSuccess(GetBasicCapabilitiesBO bo) {
                GlobalInfo.sDownloadPath = bo.getDownloadPath();
                getImageCapabilities();
            }

            @Override
            public void onDashcamResponseFailure(BaseBO bo) {
                getImageCapabilities();
            }
        });
    }

    /**
     * 获取图像能力集
     */
    private void getImageCapabilities() {
        if (!mCapabilitiesBO.hasImageCapability()) {
            getNetworkCapabilities();
            return;
        }
        CapabilityApi.getImageCapabilities(new DashcamResponseListener<GetImageCapabilitiesBO>() {
            @Override
            public void onDashcamResponseSuccess(GetImageCapabilitiesBO bo) {
                getNetworkCapabilities();
            }

            @Override
            public void onDashcamResponseFailure(BaseBO bo) {
                getNetworkCapabilities();
            }
        });
    }

    /**
     * 获取网络能力集
     */
    private void getNetworkCapabilities() {
        if (!mCapabilitiesBO.hasNetworkCapability()) {
            getStorageCapabilities();
            return;
        }
        CapabilityApi.getNetworkCapabilities(new DashcamResponseListener<GetNetworkCapabilitiesBO>() {
            @Override
            public void onDashcamResponseSuccess(GetNetworkCapabilitiesBO bo) {
                getStorageCapabilities();
            }

            @Override
            public void onDashcamResponseFailure(BaseBO bo) {
                getStorageCapabilities();
            }
        });
    }

    /**
     * 获取存储能力集
     */
    private void getStorageCapabilities() {
        if (!mCapabilitiesBO.hasStorageCapability()) {
            getIntelligentCapabilities();
            return;
        }
        CapabilityApi.getStorageCapabilities(new DashcamResponseListener<GetStorageCapabilitiesBO>() {
            @Override
            public void onDashcamResponseSuccess(GetStorageCapabilitiesBO bo) {
                getIntelligentCapabilities();
            }

            @Override
            public void onDashcamResponseFailure(BaseBO bo) {
                getIntelligentCapabilities();
            }
        });
    }

    /**
     * 获取智能能力集
     */
    private void getIntelligentCapabilities() {
        if (!mCapabilitiesBO.hasIntelliDriveCapability()) {
            return;
        }
        CapabilityApi.getIntelligentCapabilities(new DashcamResponseListener<GetIntelligentCapabilitiesBO>() {
            @Override
            public void onDashcamResponseSuccess(GetIntelligentCapabilitiesBO bo) {

            }

            @Override
            public void onDashcamResponseFailure(BaseBO bo) {

            }
        });
    }

    private boolean isPreview;

    //开始预览
    private void startPreview() {
        //能力集指令
        CapabilityApi.getBasicCapabilities(new DashcamResponseListener<GetBasicCapabilitiesBO>() {
            @Override
            public void onDashcamResponseSuccess(GetBasicCapabilitiesBO getBasicCapabilitiesBO) {
                address = getBasicCapabilitiesBO.getRTSPServerList().get(0).getUrl();
                hikPreviewPlayer = new HikPreviewPlayer();
                HikVideoModel videoModel = new HikVideoModel();
                videoModel.setUrl(address);
                videoModel.setTextureView(tvRadio);
                hikPreviewPlayer.startPreview(videoModel);

                isPreview = true;
            }

            @Override
            public void onDashcamResponseFailure(BaseBO baseBO) {
            }
        });
    }

    //停止预览
    private void stopPreview() {
        //如果播放器不为空就停止预览释放资源并且赋空
        if (hikPreviewPlayer != null) {
            hikPreviewPlayer.stopPreview();
            hikPreviewPlayer.release();
            hikPreviewPlayer = null;

            isPreview = false;
        }
    }

    //断开连接
    private void disconnect() {
        mDashcamApi.disconnect(null);
    }

    //通知监听器
    private NotificationListener mNotificationListener = new NotificationListener() {
        @Override
        public void onNormalNotificationArrive(final NormalNotificationBO normalNotificationBO) {
            getActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {
//                    if (normalNotificationBO.getNormalNotificationType() == NormalNotificationType.START_RECORD) {
//                        Toast.makeText(MainActivity.this, R.string.startrec, Toast.LENGTH_SHORT).show();
//                    } else if (normalNotificationBO.getNormalNotificationType() == NormalNotificationType.STOP_RECORD) {
//                        Toast.makeText(MainActivity.this, R.string.stoprec, Toast.LENGTH_SHORT).show();
//                    }
                    Toast.makeText(getContext(), normalNotificationBO.getNormalNotificationType().toString(), Toast.LENGTH_LONG).show();
                }
            });
        }

        @Override
        public void onEventNotificationArrive(final EventNotificationBO eventNotificationBO) {
            getActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {
//                    List<EventInfoBO> eventInfoBOList = eventNotificationBO.getEventInfoList();
//                    for (EventInfoBO eventInfoBO : eventInfoBOList) {
//                        if (eventInfoBO.getEventNotificationType() == EventNotificationType.MANUAL_RECORD) {
//                            Toast.makeText(MainActivity.this, R.string.handrec, Toast.LENGTH_SHORT).show();
//                        } else if (eventInfoBO.getEventNotificationType() == EventNotificationType.CAPTURE) {
//                            Toast.makeText(MainActivity.this, R.string.zp, Toast.LENGTH_SHORT).show();
//                        }
//                    }
                    Toast.makeText(getContext(), "收到通知 " + eventNotificationBO.getEventInfoList().size(), Toast.LENGTH_LONG).show();
                }
            });
        }
    };

    //获取错误信息
    public static String getErrorMsg(int errorCode, Context context) {
        String errorMsg = null;
        Resources resources = context.getResources();
        int[] ids = resources.getIntArray(R.array.error_code_list);
        String[] msgs = resources.getStringArray(R.array.error_reason_list);
        int length = ids.length;
        for (int i = 0; i < length; i++) {
            if (ids[i] == errorCode) {
                errorMsg = msgs[i];
                break;
            }
        }
        if (errorMsg == null) {
            return msgs[0];
        } else {
            return errorMsg;
        }
    }
}
