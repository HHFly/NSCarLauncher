package com.kandi.dell.nscarlauncher.ui_portrait.setting.model;

import android.media.audiofx.Equalizer;

public class EqData  {
   private short position;
    private String  preset;
    private Boolean isSelect=false;
    private Equalizer.Settings settings;
    public Boolean getSelect() {
        return isSelect;
    }

    public void setSelect(Boolean select) {
        isSelect = select;
    }

    public short getPosition() {
        return position;
    }

    public void setPosition(short position) {
        this.position = position;
    }

    public String getPreset() {
        return preset;
    }

    public void setPreset(String preset) {
        this.preset = preset;
    }

    public Equalizer.Settings getSettings() {
        return settings;
    }

    public void setSettings(Equalizer.Settings settings) {
        this.settings = settings;
    }
}