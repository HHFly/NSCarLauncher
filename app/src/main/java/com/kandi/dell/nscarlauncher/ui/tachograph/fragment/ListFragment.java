package com.kandi.dell.nscarlauncher.ui.tachograph.fragment;

import android.app.ListActivity;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Handler;
import android.support.v7.app.AlertDialog;
import android.text.TextUtils;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.GridView;
import android.widget.Toast;

import com.hikvision.dashcamsdkpre.BaseBO;
import com.hikvision.dashcamsdkpre.DeleteFileListDTO;
import com.hikvision.dashcamsdkpre.FileInfoBO;
import com.hikvision.dashcamsdkpre.GetFileListBO;
import com.hikvision.dashcamsdkpre.GetFileListDTO;
import com.hikvision.dashcamsdkpre.MoveFileListDTO;
import com.hikvision.dashcamsdkpre.api.GettingApi;
import com.hikvision.dashcamsdkpre.enums.MediaType;
import com.hikvision.dashcamsdkpre.listener.DashcamResponseListener;
import com.kandi.dell.nscarlauncher.R;
import com.kandi.dell.nscarlauncher.base.fragment.BaseFragment;
import com.kandi.dell.nscarlauncher.ui.bluetooth.FlagProperty;
import com.kandi.dell.nscarlauncher.ui.home.HomePagerActivity;
import com.kandi.dell.nscarlauncher.ui.tachograph.adapter.AlbumAdapter;
import com.kandi.dell.nscarlauncher.ui.tachograph.dialog.DialogPhoto;
import com.kandi.dell.nscarlauncher.ui.tachograph.dialog.DialogReview;
import com.kandi.dell.nscarlauncher.ui.tachograph.member.AlbumItem;
import com.kandi.dell.nscarlauncher.ui.tachograph.member.GlobalInfo;
import com.scwang.smartrefresh.layout.SmartRefreshLayout;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.listener.OnRefreshLoadmoreListener;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ListFragment extends BaseFragment {

    //缓冲线程池
    private ExecutorService mCacheThreadPool = Executors.newCachedThreadPool();
    //智能刷新加载布局
    private SmartRefreshLayout srlRefresh;
    //网格试图
    private GridView gvList;
    //适配器
    private AlbumAdapter albumAdapter;
    //意图
    private Intent intent;
    //路径
    private String path;
    //列表项集合
    private List<AlbumItem> list = new ArrayList<>();
    //索引
    private int index = 0;
    //选择按钮
    private Button btChoose;
    //完成按钮
    private Button btComplete;
    //删除按钮
    private Button btDel;
    //编辑模式
    private boolean EditMode;

    private MediaType mMediaType;
    private MediaType mDstType;
    private String mLastFileName;

    private DialogPhoto dialogPhoto;
    private DialogReview dialogReview;

    @Override
    public int getContentResId() {
        return R.layout.activity_list;
    }

    @Override
    public void setListener() {
        setClickListener(R.id.bt_move);
        setClickListener(R.id.ib_back);
    }

    @Override
    public void initView() {
        albumAdapter = new AlbumAdapter(getContext());
        //网格视图加入适配器
        gvList.setAdapter(albumAdapter);
        //网格视图设置列表项点击监听
        gvList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                //获取列表项
                AlbumItem albumItem = list.get(position);
                //编辑模式点击选择列表项，再次点击取消选择，非编辑模式点击图片进入图片加载，点击视频进入视频播放
                if (EditMode) {
                    albumItem.setmChecked(!albumItem.ismChecked());
                    albumAdapter.notifyDataSetChanged();
                } else {
                    if (albumItem.getmFileName().endsWith(".jpg")) {
//                        Intent intentPhoto = new Intent(getContext(), PhotoActivity.class);
//                        intentPhoto.putExtra("url", albumItem.getmThumbnailUrl());
//                        startActivity(intentPhoto);
                        getDialogPhoto().show(albumItem.getmThumbnailUrl());
                    } else {
//                        Intent intentReview = new Intent(getContext(), ReviewActivity.class);
//                        intentReview.putExtra("extra", GlobalInfo.sDownloadPath + albumItem.getmFileName());
//                        startActivity(intentReview);
                        getDialogReview().show(GlobalInfo.sDownloadPath + albumItem.getmFileName());
                    }
                }
            }
        });
        srlRefresh.setEnableLoadmore(true);
        srlRefresh.setEnableAutoLoadmore(false);
        //下拉刷新上拉加载监听器
        srlRefresh.setOnRefreshLoadmoreListener(new OnRefreshLoadmoreListener() {

            @Override
            public void onRefresh(RefreshLayout refreshLayout) {
                refresh();
            }

            @Override
            public void onLoadmore(RefreshLayout refreshlayout) {
                loadMore();
            }
        });
        changeListView();
    }

    @Override
    public void Resume() {
        if(isSecondResume){
            changeListView();
        }
    }

    @Override
    public void findView() {
        //选择按钮响应事件，点击进入编辑模式，显示完成按钮
        btChoose = getView(R.id.bt_choose);
        //完成按钮响应事件，点击退出编辑模式，显示选择按钮
        btComplete = getView(R.id.bt_complete);
        //删除按钮响应事件，点击删除文件
        btDel = getView(R.id.bt_del);
        gvList = getView(R.id.gv_list);
        srlRefresh = getView(R.id.srl_refresh);

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.bt_move:
                moveFileList();
                break;
            case R.id.ib_back:
                HomePagerActivity.homePagerActivity.getDvrFragment().hideFragmentNonstatic();
                break;
            case R.id.bt_choose:
                EditMode = true;
                btChoose.setVisibility(View.GONE);
                btComplete.setVisibility(View.VISIBLE);
                break;
            case R.id.bt_complete:
                EditMode = false;
                btChoose.setVisibility(View.VISIBLE);
                btComplete.setVisibility(View.GONE);
                AlbumItem albumItem;
                for (int i = 0; i < list.size(); i++) {
                    albumItem = list.get(i);
                    if (albumItem.ismChecked()) {
                        albumItem.setmChecked(false);
                    }
                }
                albumAdapter.notifyDataSetChanged();
                break;
            case R.id.bt_del:
                //弹出对话框确认是否删除
                AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                builder.setTitle(R.string.cerdel);
                builder.setPositiveButton(R.string.dele, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if (list.size() == 0) {
                            Toast.makeText(getContext(), R.string.select_file_first, Toast.LENGTH_SHORT).show();
                            return;
                        }
                        Runnable runnable4 = new Runnable() {
                            @Override
                            public void run() {
                                DeleteFileListDTO deleteFileListDTO = new DeleteFileListDTO();
                                List<String> fileList = new ArrayList<>();
                                AlbumItem albumItem;
                                for (int i = 0; i < list.size(); i++) {
                                    albumItem = list.get(i);
                                    if (albumItem.ismChecked()) {
                                        fileList.add(albumItem.getmFileName());
                                    }
                                }
                                deleteFileListDTO.setFileList(fileList);
                                GettingApi.deleteFileList(deleteFileListDTO, new DashcamResponseListener<BaseBO>() {
                                    @Override
                                    public void onDashcamResponseSuccess(BaseBO baseBO) {
                                        getActivity().runOnUiThread(new Runnable() {
                                            @Override
                                            public void run() {
                                                Toast.makeText(getContext(), R.string.sucdel, Toast.LENGTH_SHORT).show();
                                                refresh();
                                            }
                                        });

                                    }

                                    @Override
                                    public void onDashcamResponseFailure(BaseBO baseBO) {
                                        Toast.makeText(getContext(), baseBO.getErrorMsg(), Toast.LENGTH_SHORT).show();
                                    }
                                });

                            }
                        };
                        mCacheThreadPool.execute(runnable4);
                    }
                });
                builder.setNegativeButton(R.string.cac, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                });
                builder.create().show();
                break;
        }
    }

    public DialogPhoto getDialogPhoto() {
        if(dialogPhoto==null){
            dialogPhoto =new DialogPhoto(getContext());
        }
        return dialogPhoto;
    }

    public DialogReview getDialogReview() {
        if(dialogReview==null){
            dialogReview =new DialogReview(getContext());
        }
        return dialogReview;
    }

    //上拉加载
    private void loadMore() {
        Runnable runnable2 = new Runnable() {
            @Override
            public void run() {
                GetFileListDTO getFileListDTO = new GetFileListDTO();
                getFileListDTO.setLastFileName(mLastFileName);
                getFileListDTO.setDriver(1);
                getFileListDTO.setPageNumber(20);
                getFileListDTO.setMediaType(mMediaType);
                GettingApi.getFileList(getFileListDTO, new DashcamResponseListener<GetFileListBO>() {
                    @Override
                    public void onDashcamResponseSuccess(final GetFileListBO getFileListBO) {
                        getActivity().runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                List<FileInfoBO> fileList = getFileListBO.getFileList();
                                if (fileList != null && fileList.size() > 0) {
                                    mLastFileName = fileList.get(fileList.size() - 1).getFileName();
                                    for (FileInfoBO fileInfoBO : fileList) {
                                        String fileName = fileInfoBO.getFileName();
                                        if (TextUtils.isEmpty(fileName)) {
                                            continue;
                                        }
                                        AlbumItem albumItem = new AlbumItem();
                                        albumItem.setmThumbnailUrl(GlobalInfo.sDownloadPath + fileInfoBO.getFileThumbnail());
                                        albumItem.setmFileName(fileInfoBO.getFileName());
                                        list.add(albumItem);
                                    }
                                    albumAdapter.setmItems(list);
                                    albumAdapter.notifyDataSetChanged();
                                }
                                //结束加载
                                srlRefresh.finishLoadmore();
                            }
                        });
                    }

                    @Override
                    public void onDashcamResponseFailure(BaseBO baseBO) {
                        Toast.makeText(getContext(), baseBO.getErrorMsg(), Toast.LENGTH_SHORT).show();
                    }
                });
            }
        };
        mCacheThreadPool.execute(runnable2);
    }

    //下拉刷新
    private void refresh() {
        Runnable runnable3 = new Runnable() {
            @Override
            public void run() {
                //清空列表
                list.clear();
                index = 0;
                GetFileListDTO getFileListDTO = new GetFileListDTO();
                getFileListDTO.setLastFileName("");
                getFileListDTO.setDriver(1);
                getFileListDTO.setPageNumber(20);
                getFileListDTO.setMediaType(mMediaType);
                GettingApi.getFileList(getFileListDTO, new DashcamResponseListener<GetFileListBO>() {
                    @Override
                    public void onDashcamResponseSuccess(final GetFileListBO getFileListBO) {
                        getActivity().runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                List<FileInfoBO> fileList = getFileListBO.getFileList();
                                if (fileList != null && fileList.size() > 0) {
                                    mLastFileName = fileList.get(fileList.size() - 1).getFileName();
                                    for (FileInfoBO fileInfoBO : fileList) {
                                        String fileName = fileInfoBO.getFileName();
                                        if (TextUtils.isEmpty(fileName)) {
                                            continue;
                                        }
                                        AlbumItem albumItem = new AlbumItem();
                                        albumItem.setmThumbnailUrl(GlobalInfo.sDownloadPath + fileInfoBO.getFileThumbnail());
                                        albumItem.setmFileName(fileInfoBO.getFileName());
                                        list.add(albumItem);
                                    }
                                    albumAdapter.setmItems(list);
                                    albumAdapter.notifyDataSetChanged();
                                }
                                //结束刷新
                                srlRefresh.finishRefresh();
                            }
                        });
                    }

                    @Override
                    public void onDashcamResponseFailure(BaseBO baseBO) {
                        Toast.makeText(getContext(), baseBO.getErrorMsg(), Toast.LENGTH_SHORT).show();
                    }
                });

            }
        };
        mCacheThreadPool.execute(runnable3);
    }

    /**
     * 文件移动
     */
    private void moveFileList() {
        if (MediaType.EVENT_VIDEO == mMediaType) {
            mDstType = MediaType.NORMAL_VIDEO;
        } else if (MediaType.NORMAL_VIDEO == mMediaType) {
            mDstType = MediaType.EVENT_VIDEO;
        } else {
            Toast.makeText(getContext(), R.string.can_not_move, Toast.LENGTH_SHORT).show();
            return;
        }
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setTitle(R.string.cermove);
        builder.setPositiveButton(R.string.move, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if (list.size() == 0) {
                    Toast.makeText(getContext(), R.string.select_file_first, Toast.LENGTH_SHORT).show();
                    return;
                }
                Runnable runnable4 = new Runnable() {
                    @Override
                    public void run() {
                        List<String> fileList = new ArrayList<>();
                        AlbumItem albumItem;
                        for (int i = 0; i < list.size(); i++) {
                            albumItem = list.get(i);
                            if (albumItem.ismChecked()) {
                                fileList.add(albumItem.getmFileName());
                            }
                        }
                        MoveFileListDTO moveFileListDTO = new MoveFileListDTO();
                        moveFileListDTO.setDstDriver(1);
                        moveFileListDTO.setDstType(mDstType);
                        moveFileListDTO.setFileList(fileList);

                        GettingApi.moveFileList(moveFileListDTO, new DashcamResponseListener<BaseBO>() {
                            @Override
                            public void onDashcamResponseSuccess(BaseBO bo) {
                                getActivity().runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        Toast.makeText(getContext(), R.string.sucmove, Toast.LENGTH_SHORT).show();
                                        refresh();
                                    }
                                });
                            }

                            @Override
                            public void onDashcamResponseFailure(BaseBO bo) {
                                Toast.makeText(getContext(), bo.getErrorMsg(), Toast.LENGTH_SHORT).show();
                            }
                        });
                    }
                };
                mCacheThreadPool.execute(runnable4);
            }
        });
        builder.setNegativeButton(R.string.cac, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

            }
        });
        builder.create().show();
    }

    public void changeListView(){
        Runnable runnable1 = new Runnable() {
            @Override
            public void run() {
                GetFileListDTO dto = new GetFileListDTO();
                dto.setDriver(1);
                dto.setLastFileName("");
                dto.setPageNumber(20);
                //根据传过来的字符串类型和文件类型来获取对应的路径
                switch (FlagProperty.extra) {
                    case "event":
                        mMediaType = MediaType.EVENT_VIDEO;
                        break;
                    case "cycle":
                        mMediaType = MediaType.NORMAL_VIDEO;
                        break;
                    case "user":
                        mMediaType = MediaType.USER_DATA;
                        break;
                }
                dto.setMediaType(mMediaType);

                GettingApi.getFileList(dto, new DashcamResponseListener<GetFileListBO>() {
                    @Override
                    public void onDashcamResponseSuccess(final GetFileListBO getFileListBO) {
                        getActivity().runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                //文件列表
                                List<FileInfoBO> fileList = getFileListBO.getFileList();
                                if (fileList == null || fileList.size() == 0) {
                                    Toast.makeText(getContext(), getString(R.string.file_empty), Toast.LENGTH_SHORT).show();
                                    return;
                                }
                                mLastFileName = fileList.get(fileList.size() - 1).getFileName();
                                //foreach循环
                                for (FileInfoBO fileInfoBO : fileList) {
                                    String fileName = fileInfoBO.getFileName();
                                    //文件路径为空也跳过去
                                    if (TextUtils.isEmpty(fileName)) {
                                        continue;
                                    }
                                    AlbumItem albumItem = new AlbumItem();
                                    albumItem.setmThumbnailUrl(GlobalInfo.sDownloadPath + fileInfoBO.getFileThumbnail());
                                    albumItem.setmFileName(fileInfoBO.getFileName());
                                    list.add(albumItem);
                                }
                                //列表加入到适配器里面
                                albumAdapter.setmItems(list);
                                //通知适配器数据改变
                                albumAdapter.notifyDataSetChanged();
                            }
                        });
                    }

                    @Override
                    public void onDashcamResponseFailure(BaseBO baseBO) {
                        Toast.makeText(getContext(), baseBO.getErrorMsg(), Toast.LENGTH_SHORT).show();
                    }
                });
            }
        };

        mCacheThreadPool.execute(runnable1);
    }
}
