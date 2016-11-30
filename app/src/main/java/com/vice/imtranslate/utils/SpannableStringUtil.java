package com.vice.imtranslate.utils;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.style.ImageSpan;
import android.widget.EditText;
import android.widget.TextView;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by vice on 2016/10/31 0031.
 */
public class SpannableStringUtil {
    public static final String[] REGEX={
            "\\[\\):\\]","\\[:D\\]","\\[;\\)\\]","\\[:-o\\]","\\[:p\\]","\\[\\(H\\)\\]","\\[:@\\]",
            "\\[:s\\]","\\[:\\$\\]","\\[:\\(\\]","\\[:'\\(\\]","\\[:\\|\\]","\\[\\(a\\)\\]","\\[8o\\|\\]",
            "\\[8-\\|\\]","\\[\\+o\\(\\]","\\[<o\\)\\]","\\[\\|-\\)]","\\[\\*-\\)\\]","\\[:-#\\]", "\\[:-*\\]",

            "\\[\\^o\\)\\]","\\[8-\\)\\]","\\[\\(\\|\\)\\]","\\[\\(u\\)\\]","\\[\\(S\\)\\]","\\[\\(\\*\\)\\]", "\\[\\(#\\)\\]",
            "\\[\\(R\\)\\]","\\[\\(\\{\\)\\]","\\[\\(\\}\\)\\]","\\[\\(k\\)\\]","\\[\\(F\\)\\]","\\[\\(W\\)\\]", "\\[\\(D\\)\\]"
    };
    public static SpannableString getContent(final Context context, final TextView textView , String content){
        SpannableString spannableString=new SpannableString(content);
        for(String regex:REGEX){
            Pattern p=Pattern.compile(regex);
            Matcher m = p.matcher(spannableString);
            while (m.find()){
                String key=m.group();
                System.out.println("vvvkey"+key);
                int start=m.start();
                Integer imgRes=ExpressionUtil.getExpressionByName(key);
                if(imgRes!=-1){
                    int size= (int) (textView.getTextSize()*13/10);
                    Bitmap bitmap= BitmapFactory.decodeResource(context.getResources(),imgRes);
                    Bitmap scaleBitmap=Bitmap.createScaledBitmap(bitmap,size,size,true);
                    ImageSpan imageSpan=new ImageSpan(context,scaleBitmap);
                    spannableString.setSpan(imageSpan,start,start+key.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                }
            }
        }
        return spannableString;
    }

}
