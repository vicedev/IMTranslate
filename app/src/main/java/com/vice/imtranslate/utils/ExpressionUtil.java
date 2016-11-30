package com.vice.imtranslate.utils;

import android.support.v4.util.ArrayMap;

import com.vice.imtranslate.App;
import com.vice.imtranslate.R;

import java.util.Map;

/**
 * Created by vice on 2016/10/31 0031.
 */
public class ExpressionUtil {
    public static int getExpressionByName(String name){
        System.out.println("vvvname"+App.expressionMap.get(name));
        Integer imgRes=App.expressionMap.get(name);
        if (imgRes!=null){
            return imgRes;
        }else{
         return -1;
        }
    }
}
