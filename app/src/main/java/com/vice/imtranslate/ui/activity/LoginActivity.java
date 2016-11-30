package com.vice.imtranslate.ui.activity;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.hyphenate.EMCallBack;
import com.hyphenate.chat.EMClient;
import com.vice.imtranslate.R;
import com.vice.imtranslate.runtimepermissions.PermissionsManager;
import com.vice.imtranslate.runtimepermissions.PermissionsResultAction;
import com.vice.imtranslate.utils.ShareprefencesUtils;

public class LoginActivity extends BaseActivity {

    private EditText etUsername;
    private EditText etPassword;
    private Button btnLogin;
    private ProgressDialog dialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        hideBack();
        setTitle("登录");

        etUsername = (EditText) findViewById(R.id.et_username);
        etPassword = (EditText) findViewById(R.id.et_password);
        btnLogin = (Button) findViewById(R.id.btn_login);

        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String username=etUsername.getText().toString();
                String password=etPassword.getText().toString();
                if (!TextUtils.isEmpty(username)&&!TextUtils.isEmpty(password)){
                    login( username,password);
                    showDialog(true);
                }
//                else{
//                    login("test1","666666");
//                }
            }
        });
    }

    @Override
    View addContentLayout() {
        View view = getLayoutInflater().inflate(R.layout.activity_login, contentLayout,false);
        return view;
    }

    private void login(String userName,String password) {
        EMClient.getInstance().login(userName,password,new EMCallBack() {//回调
            @Override
            public void onSuccess() {
//                EMClient.getInstance().groupManager().loadAllGroups();
//                EMClient.getInstance().chatManager().loadAllConversations();
                Log.d("loginvvv", "登录聊天服务器成功！");
                showDialog(false);
                ShareprefencesUtils.saveAutoLogin(LoginActivity.this,true);
                //跳转界面
                startActivity(new Intent(LoginActivity.this,MainActivity.class));
                finish();

            }

            @Override
            public void onProgress(int progress, String status) {

            }

            @Override
            public void onError(int code, String message) {
                Log.d("loginvvv", "登录聊天服务器失败！");
                showDialog(false);
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(LoginActivity.this,"登录失败",Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });
    }

    private void showDialog(final boolean show){
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (show){
                    if (dialog==null){
                        dialog = ProgressDialog.show(LoginActivity.this, null, "登录中...");
                    }else{
                        dialog.show();
                    }
                }else{
                    if (dialog!=null){
                        dialog.dismiss();
                    }
                }
            }
        });
    }
    private void requestpermissions(){
//        PermissionsManager.getInstance().requestAllManifestPermissionsIfNecessary(this, new PermissionsResultAction() {
//            @Override
//            public void onGranted() {
//
//            }
//
//            @Override
//            public void onDenied(String permission) {
//
//            }
//        });
    }
}
