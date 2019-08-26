package com.kandi.dell.nscarlauncher.common.handle;

import android.location.Location;

public class LocationModel {
    private String longitude;
    private String latitude;

    public LocationModel() {
    }

    public LocationModel(LocationModel data) {
        if (data != null) {
            this.longitude = data.getLongitude();
            this.latitude = data.getLatitude();
        }

    }

    public LocationModel(Location data) {
        if (data != null) {
            this.longitude = String.valueOf(data.getLongitude());
            this.latitude = String.valueOf(data.getLatitude());
        }

    }

    public LocationModel(String longitude, String latitude) {
        this.longitude = longitude;
        this.latitude = latitude;
    }

    public String toString() {
        return "[longitude]" + this.longitude + "---[latitude]" + this.latitude;
    }

    public String getLongitude() {
        return this.longitude;
    }

    public LocationModel setLongitude(String longitude) {
        this.longitude = longitude;
        return this;
    }

    public String getLatitude() {
        return this.latitude;
    }

    public LocationModel setLatitude(String latitude) {
        this.latitude = latitude;
        return this;
    }
}
