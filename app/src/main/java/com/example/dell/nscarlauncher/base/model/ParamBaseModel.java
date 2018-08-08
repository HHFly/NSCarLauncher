package com.example.dell.nscarlauncher.base.model;





import com.example.dell.nscarlauncher.common.util.MapUtils;

import java.util.Map;

/**
 * 功能：请求参数封装父类
 */

public class ParamBaseModel extends BaseModel {
    private Map<String, String> mMapData;

    public Map<String, String> getMap() {
        Map<String, String> map = MapUtils.getMap(this);
        if (mMapData == null) {
            mMapData = map;
        } else {
            mMapData.putAll(map);
        }
        return mMapData;
    }

    public void addMap(ParamBaseModel data) {
        if (data != null) {
            Map<String, String> map = data.getMap();
            if (mMapData == null) {
                mMapData = map;
            } else {
                mMapData.putAll(map);
            }
        }
    }
}
