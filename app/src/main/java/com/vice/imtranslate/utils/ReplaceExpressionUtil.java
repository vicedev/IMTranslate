package com.vice.imtranslate.utils;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by vice on 2016/11/3 0003.
 */
public class ReplaceExpressionUtil {
    public static final String[] EXPRESSIONS={
            "\\[\\):\\]","\\[:D\\]","\\[;\\)\\]","\\[:-o\\]","\\[:p\\]","\\[\\(H\\)\\]","\\[:@\\]",
            "\\[:s\\]","\\[:\\$\\]","\\[:\\(\\]","\\[:'\\(\\]","\\[:\\|\\]","\\[\\(a\\)\\]","\\[8o\\|\\]",
            "\\[8-\\|\\]","\\[\\+o\\(\\]","\\[<o\\)\\]","\\[\\|-\\)]","\\[\\*-\\)\\]","\\[:-#\\]", "\\[:-*\\]",

            "\\[\\^o\\)\\]","\\[8-\\)\\]","\\[\\(\\|\\)\\]","\\[\\(u\\)\\]","\\[\\(S\\)\\]","\\[\\(\\*\\)\\]", "\\[\\(#\\)\\]",
            "\\[\\(R\\)\\]","\\[\\(\\{\\)\\]","\\[\\(\\}\\)\\]","\\[\\(k\\)\\]","\\[\\(F\\)\\]","\\[\\(W\\)\\]", "\\[\\(D\\)\\]"
    };

    public static List<String> replaceAll(String content){
        String q=content;
        List<String> expressions=new ArrayList<>();
        for(String expression:EXPRESSIONS){

            Pattern p=Pattern.compile(expression);
            Matcher m = p.matcher(q);

            while (m.find()){
                String key =m.group();
                expressions.add(key);
            }
            q=q.replaceAll(expression,"\n");
        }
        expressions.add(0,q);

        return expressions;
    }
}
