package com.vice.imtranslate.global;

import android.app.Activity;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 2016/9/17 0017.
 */
public class ForegroundActivitiesHelper {
    private List<Activity> activities;
    private static ForegroundActivitiesHelper helper;

    public synchronized static ForegroundActivitiesHelper getInstance(){
        if (null==helper){
            helper=new ForegroundActivitiesHelper();
        }
        return helper;
    }

    private ForegroundActivitiesHelper() {
        activities=new ArrayList<>();
    }
    public void pushActivity(Activity activity){
        if (!activities.contains(activity)){
            activities.add(activity);
        }
    }

    public void popActivity(Activity activity){
        activities.remove(activity);
    }

    public boolean hasForegroundActivities(){
        return activities.size()==0?false:true;
    }
}
