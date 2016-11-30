package com.vice.imtranslate.ui.fragment;


import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.drawable.GlideDrawable;
import com.bumptech.glide.request.animation.GlideAnimation;
import com.bumptech.glide.request.target.SimpleTarget;
import com.hyphenate.EMCallBack;
import com.hyphenate.chat.EMClient;
import com.hyphenate.util.PathUtil;
import com.vice.imtranslate.R;
import com.vice.imtranslate.global.ActivityControler;
import com.vice.imtranslate.ui.activity.LoginActivity;
import com.vice.imtranslate.utils.ShareprefencesUtils;
import com.vice.imtranslate.utils.VUtils;

import java.io.File;

/**
 * A simple {@link Fragment} subclass.
 */
public class MeFragment extends Fragment {


    private ImageView ivAvatar;
    private TextView tvNickname;
    private Button btnLogout;
    private ProgressDialog dialog;

    public MeFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View layout=inflater.inflate(R.layout.fragment_me, container, false);

        ivAvatar = (ImageView) layout.findViewById(R.id.iv_avatar);
        tvNickname = (TextView) layout.findViewById(R.id.tv_nickname);
        btnLogout = (Button) layout.findViewById(R.id.btn_logout);

        ivAvatar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });

        setUserInfo();
        setItemListener();

        return layout;
    }

    private void setItemListener() {
        btnLogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                logout();
            }
        });
    }

    private void logout() {
        showDialog(true);
        EMClient.getInstance().logout(true, new EMCallBack() {

            @Override
            public void onSuccess() {
                ShareprefencesUtils.saveAutoLogin(getActivity(),false);
                ActivityControler.finishAll();
                startActivity(new Intent(getActivity(), LoginActivity.class));
                showDialog(false);
            }

            @Override
            public void onProgress(int progress, String status) {

            }

            @Override
            public void onError(int code, String message) {
                Toast.makeText(getActivity(),"退出登录失败",Toast.LENGTH_SHORT).show();
                 showDialog(false);
            }
        });
    }

    private void showDialog(final boolean show){
        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (show){
                    if (dialog==null){
                        dialog = ProgressDialog.show(getActivity(), null, "正在退出登录...");
                    }else{
                        dialog.show();
                    }
                }else{
                    if (null!=dialog){
                        dialog.dismiss();
                    }
                }
            }
        });
    }

    private void setUserInfo() {

        String nickName;
        String avatar = null;

        String currentUser = EMClient.getInstance().getCurrentUser();
        nickName=currentUser;
        if (currentUser.equals("test1")){
            avatar="http://img5.duitang.com/uploads/item/201508/26/20150826195235_xzB8K.thumb.700_0.jpeg";
        }else if (currentUser.equals("test2")){
            avatar="http://a.hiphotos.baidu.com/zhidao/pic/item/91529822720e0cf3ef6c79630c46f21fbf09aa8a.jpg";
        }else if (currentUser.equals("test3")){
            avatar="http://img4.imgtn.bdimg.com/it/u=3257207530,468293946&fm=206&gp=0.jpg";
        }

        Glide.with(getActivity())
                .load(avatar)
                .centerCrop()
                .placeholder(R.mipmap.ic_launcher)
                .into(ivAvatar);

        tvNickname.setText(nickName);
    }

}
