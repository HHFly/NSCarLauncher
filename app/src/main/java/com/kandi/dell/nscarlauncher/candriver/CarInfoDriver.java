package com.kandi.dell.nscarlauncher.candriver;

import android.os.RemoteException;

import com.driverlayer.os_driverServer.IECarDriver;

public class CarInfoDriver {

    private IECarDriver R_service;

    public CarInfoDriver(IECarDriver R_service) {
        this.R_service = R_service;
    }

    private int[] param = new int[2];
    public void getCarInfo() throws RemoteException {
        R_service.getCarInfo(param);
    }

    public int getCarSpeed(){
        return param[0];
    }

    public int getRemaingMileage(){
        return param[1];
    }

    private int[] cse_param = new int[2];
    public void getCar_SportEnergy() throws RemoteException {
        R_service.getCar_SportEnergy(cse_param);
    }

    public int getCarMode(){
        return cse_param[0];
    }

    public int getEnergyLevel(){
        return cse_param[1];
    }

    public void setCar_SportEnergy(int[] scs_param) throws RemoteException {
        R_service.setCar_SportEnergy(scs_param);
    }
}
