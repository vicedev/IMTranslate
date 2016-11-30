package com.vice.imtranslate.ui.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.hyphenate.EMCallBack;
import com.hyphenate.chat.EMClient;
import com.vice.imtranslate.R;
import com.vice.imtranslate.utils.ShareprefencesUtils;

public class SplashActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        hideTitleBar();
//        login("test1","666666");
        final boolean autoLogin = ShareprefencesUtils.getAutoLogin(this);
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                finish();
                if (autoLogin){
                    startActivity(new Intent(SplashActivity.this,MainActivity.class));
                }else{
                    startActivity(new Intent(SplashActivity.this,LoginActivity.class));
                }
            }
        }, 2000);
    }

    @Override
    View addContentLayout() {
        View layout=getLayoutInflater().inflate(R.layout.activity_splash,contentLayout,false);
        return layout;
    }

    private void login(String userName,String password) {
        EMClient.getInstance().login(userName,password,new EMCallBack() {//回调
            @Override
            public void onSuccess() {
//                EMClient.getInstance().groupManager().loadAllGroups();
//                EMClient.getInstance().chatManager().loadAllConversations();
                Log.d("loginvvv", "登录聊天服务器成功！");
                //跳转界面
                startActivity(new Intent(SplashActivity.this,MainActivity.class));
                finish();
            }

            @Override
            public void onProgress(int progress, String status) {

            }

            @Override
            public void onError(int code, String message) {
                Log.d("loginvvv", "登录聊天服务器失败！");
                startActivity(new Intent(SplashActivity.this,LoginActivity.class));
            }
        });
    }
}
