package com.vice.imtranslate.utils;

import android.content.Context;
import android.content.SharedPreferences;

import com.vice.imtranslate.global.Constants;

/**
 * Created by Administrator on 2016/9/14 0014.
 */
public class ShareprefencesUtils {
    public static void saveReceivedLanguage(Context context,String userId,int language ){
        SharedPreferences sp=context.getSharedPreferences("config",Context.MODE_PRIVATE);
        sp.edit().putInt(Constants.RECEIVED_LANGUAGE+userId,language).commit();
    }
    public static int getReceivedLanguage(Context context,String userId){
        SharedPreferences sp=context.getSharedPreferences("config",Context.MODE_PRIVATE);
        return sp.getInt(Constants.RECEIVED_LANGUAGE+userId,0);
    }
    public static void saveAutoLogin(Context context,boolean autologin ){
        SharedPreferences sp=context.getSharedPreferences("config",Context.MODE_PRIVATE);
        sp.edit().putBoolean(Constants.AUTO_LOGIN,autologin).commit();
    }
    public static boolean getAutoLogin(Context context){
        SharedPreferences sp=context.getSharedPreferences("config",Context.MODE_PRIVATE);
        return sp.getBoolean(Constants.AUTO_LOGIN,false);
    }
}
