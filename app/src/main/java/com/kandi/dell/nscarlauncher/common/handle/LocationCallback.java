package com.kandi.dell.nscarlauncher.common.handle;




public interface LocationCallback {
    void getLastKnownLocation(LocationModel var1);

    void onLocationChanged(LocationModel var1);

    boolean isLocationOne();
}
