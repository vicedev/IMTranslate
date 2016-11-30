package com.vice.imtranslate.ui.activity;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;

import com.vice.imtranslate.R;
import com.vice.imtranslate.global.Constants;
import com.vice.imtranslate.ui.fragment.ChatFragment;

public class ChatActivity extends BaseActivity implements ChatFragment.TransferInterface{

    private String userId;
    private ViewGroup layout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        userId = getIntent().getStringExtra(Constants.USER_ID);
        setTitle(userId);
        showLanguage();//显示选择接收语言按钮

//        ChatFragment chatFragment=new ChatFragment();
//        Bundle bundle=new Bundle();
//        bundle.putString("userId",userId);
//        chatFragment.setArguments(bundle);
//        getSupportFragmentManager().beginTransaction().replace(R.id.container,chatFragment).commit();
    }

    @Override
    View addContentLayout() {
        layout = (RelativeLayout) getLayoutInflater().inflate(R.layout.activity_chat, contentLayout,false);
        return layout;
    }

    @Override
    protected void onFinish() {
        super.onFinish();
        overridePendingTransition(R.anim.activity_in_from_left,R.anim.activity_out_to_right);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(R.anim.activity_in_from_left,R.anim.activity_out_to_right);
    }

    @Override
    public String getUserId() {
        return userId;
    }

    @Override
    public void setOnLanguageChangeListener(onLanguageChangedListener listener) {
        super.listener=listener;
    }

    @Override
    public int getCurrentLanguage() {
        return super.currentLanguage;
    }


}
