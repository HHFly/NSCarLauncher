package com.kandi.dell.nscarlauncher.ui.setting.model;

public class EqData  {
   private short position;
    private String  preset;
    private Boolean isSelect=false;

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
}
