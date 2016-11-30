package com.vice.imtranslate.ui.custom;

import android.app.Activity;
import android.content.Context;
import android.content.res.TypedArray;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import com.rd.PageIndicatorView;
import com.vice.imtranslate.R;
import com.vice.imtranslate.global.Constants;
import com.vice.imtranslate.ui.fragment.ClassicalExpressionFragment;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by vice on 2016/11/2 0002.
 */
public class CustomExpressionMenu extends FrameLayout {
    private int columns;
    private int rows;
    private FragmentActivity activity;
    private ViewPager vpExpression;
    private List<Fragment> pageList;

    public CustomExpressionMenu(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.activity=(FragmentActivity)context;

        TypedArray ta=context.obtainStyledAttributes(attrs,R.styleable.CustomExpressionMenu);
        rows=ta.getInteger(R.styleable.CustomExpressionMenu_rows,3);
        columns=ta.getInteger(R.styleable.CustomExpressionMenu_columns,7);
        ta.recycle();

        LayoutInflater.from(context).inflate(R.layout.custom_expression_menu,this);

        //计算表情页面数量和排列
        pageList=new ArrayList<>();
        int onePageSize=rows*columns;

        int pageNum=Constants.EXPRESSION_VALUE.length/onePageSize;//页面数量

        if (pageNum!=0){
            //多页
            for (int i=0;i<pageNum;i++){
                pageList.add(ClassicalExpressionFragment.newInstance((onePageSize-1)*i,onePageSize,columns));
            }
            int leaveNum=Constants.EXPRESSION_VALUE.length-(onePageSize-1)*pageNum;
            pageList.add(ClassicalExpressionFragment.newInstance(Constants.EXPRESSION_VALUE.length+1-leaveNum,leaveNum,columns));

        }else{
            //一页
            pageList.add(ClassicalExpressionFragment.newInstance(0,Constants.EXPRESSION_VALUE.length+1,columns));
        }

        for (int i=0;i<pageList.size();i++){
            ClassicalExpressionFragment fragment= (ClassicalExpressionFragment) pageList.get(i);
            fragment.setOnExpressionClickListener(new ClassicalExpressionFragment.OnExpressionClickListener() {
                @Override
                public void onClick(int expressionType, int position) {
                    if (onExpressionClickListener!=null){
                        onExpressionClickListener.onClick(expressionType,position);
                    }
                }
            });
        }
    }

    //    public CustomExpressionMenu(FragmentActivity activity, int rows, int columns) {
//        super(activity);
//        this.rows=rows;
//        this.columns=columns;
//        this.activity=activity;
//
//    }



    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        vpExpression = (ViewPager) findViewById(R.id.vp_expression);
        vpExpression.setAdapter(new MyPagerAdapter(activity.getSupportFragmentManager()));



    }

    class MyPagerAdapter extends FragmentPagerAdapter{

        public MyPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            return pageList.get(position);
        }

        @Override
        public int getCount() {
            return pageList.size();
        }
    }
    private OnExpressionClickListener onExpressionClickListener;

    public interface OnExpressionClickListener{
        void onClick(int expressionType,int position);
    }

    public void setOnExpressionClickListener(OnExpressionClickListener onExpressionClickListener){
        this.onExpressionClickListener=onExpressionClickListener;
    }


}
