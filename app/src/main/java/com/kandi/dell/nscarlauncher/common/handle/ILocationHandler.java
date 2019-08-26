package com.kandi.dell.nscarlauncher.common.handle;

import android.app.Activity;

import java.util.List;

public interface ILocationHandler {
    void register(Activity var1, LocationCallback var2);

    void unRegister(LocationCallback var1);

    void unRegister(List<LocationCallback> var1);

    void unRegisterAll();
}
