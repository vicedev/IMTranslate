package com.vice.imtranslate.ui.fragment;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.vice.imtranslate.R;
import com.vice.imtranslate.global.Constants;
import com.vice.imtranslate.ui.activity.ChatActivity;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link ClassicalExpressionFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ClassicalExpressionFragment extends Fragment {

    private static final String ARG_PARAM1 = "startPosition";
    private static final String ARG_PARAM2 = "size";
    private static final String ARG_PARAM3 = "columns";
    private int startPosition;
    private int size;
    private int columns;
    private GridView gvExpression;

    public ClassicalExpressionFragment() {
        // Required empty public constructor
    }

    public static ClassicalExpressionFragment newInstance(int startPosition, int size,int columns) {
        ClassicalExpressionFragment fragment = new ClassicalExpressionFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_PARAM1, startPosition);
        args.putInt(ARG_PARAM2, size);
        args.putInt(ARG_PARAM3, columns);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            startPosition = getArguments().getInt(ARG_PARAM1);
            size = getArguments().getInt(ARG_PARAM2);
            columns = getArguments().getInt(ARG_PARAM3);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View layout=inflater.inflate(R.layout.fragment_classical_expression, container, false);

        gvExpression = (GridView) layout.findViewById(R.id.gv_expression);

        gvExpression.setNumColumns(columns);
        gvExpression.setAdapter(new BaseAdapter() {
            @Override
            public int getCount() {
                return size;
            }

            @Override
            public Object getItem(int position) {
                return Constants.EXPRESSION_VALUE[position+startPosition];
            }

            @Override
            public long getItemId(int position) {
                return position;
            }

            @Override
            public View getView(int position, View convertView, ViewGroup parent) {
                ImageView ivExpression=new ImageView(getActivity());
                if (position==(size-1)){
                    //删除按钮
                    ivExpression.setImageResource(R.drawable.delete_expression);
                }else{
                    ivExpression.setImageResource(Constants.EXPRESSION_VALUE[startPosition+position]);
                }
                return ivExpression;
            }
        });
        gvExpression.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (position==(size-1)){
                    onExpressionClickListener.onClick(Constants.CLASSICAL_EXPRESSION,-1);
                }else{
                    onExpressionClickListener.onClick(Constants.CLASSICAL_EXPRESSION,(startPosition+position));
                }
            }
        });

        return layout;
    }

    private OnExpressionClickListener onExpressionClickListener;

    public interface OnExpressionClickListener{
        void onClick(int expressionType,int position);
    }

    public void setOnExpressionClickListener(OnExpressionClickListener onExpressionClickListener){
        this.onExpressionClickListener=onExpressionClickListener;
    }


}
