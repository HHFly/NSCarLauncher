package com.example.dell.nscarlauncher.common.util;

import java.lang.reflect.Array;

public class FilterAppUtils {
    private static AppPkgName pkgName = new AppPkgName();
    public static boolean filter(String name){
        for (String pkg:pkgName.data){
            if(pkg.equals(name)){
                return false;
            }
        }

        return true;
    }

}
//屏蔽包名
class AppPkgName{
String[] data =new String[]{
        "com.kandi.acarpower",
        "com.kandi.acarset",
        "com.kandi.aircontrol",
         "com.kandi.carcontrol",
        "com.kandi.powermanager",
        "com.kandi.nscarlauncher"
};
}
