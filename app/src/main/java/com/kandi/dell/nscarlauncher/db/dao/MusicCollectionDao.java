package com.kandi.dell.nscarlauncher.db.dao;

import android.content.Context;

import com.kandi.dell.nscarlauncher.db.FinalDb;
import com.kandi.dell.nscarlauncher.ui.music.model.Mp3Info;

import java.util.List;

public class MusicCollectionDao {
    public static List<Mp3Info> getAllFav(Context context, String orderBy){
        FinalDb finalDb = FinalDb.create(context);
        return finalDb.findAll(Mp3Info.class, orderBy);
    }
    public static List<Mp3Info> getAllFav(Context context){
        FinalDb finalDb = FinalDb.create(context);
        return finalDb.findAll(Mp3Info.class);
    }
    public static void addFav(Context context,Mp3Info mcm){
        FinalDb finalDb = FinalDb.create(context);
        finalDb.save(mcm);
    }
    public static List<Mp3Info> findFavByUrl(Context context,String url){
        FinalDb finalDb = FinalDb.create(context);
        return finalDb.findAllByWhere(Mp3Info.class, "url = \""+url+"\"");
    }
    public static Mp3Info findFavById(Context context,int id ){
        FinalDb finalDb = FinalDb.create(context);
        return finalDb.findById(id, Mp3Info.class);
    }
    public static void deleteFavById(Context context,int id){
        FinalDb finalDb = FinalDb.create(context);
        finalDb.deleteById(Mp3Info.class, id);
    }
    public static void deleteFavByUrl(Context context,String url){
        FinalDb finalDb = FinalDb.create(context);
        finalDb.deleteByWhere(Mp3Info.class, "url = \""+url+"\"");
    }
    public static void deleteFavByUsbOut(Context context,String usb){
        FinalDb finalDb = FinalDb.create(context);
        finalDb.deleteByWhere(Mp3Info.class, "url like '%"+usb+"%'");
    }
}
