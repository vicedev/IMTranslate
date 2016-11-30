package com.vice.imtranslate.ui.activity;

import android.annotation.TargetApi;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.TextView;
import android.widget.Toast;

import com.hyphenate.EMConnectionListener;
import com.hyphenate.EMError;
import com.hyphenate.EMMessageListener;
import com.hyphenate.chat.EMClient;
import com.hyphenate.chat.EMMessage;
import com.hyphenate.util.NetUtils;
import com.vice.imtranslate.App;
import com.vice.imtranslate.R;
import com.vice.imtranslate.adapter.MainFragmentAdapter;
import com.vice.imtranslate.global.ActivityControler;
import com.vice.imtranslate.runtimepermissions.PermissionsManager;
import com.vice.imtranslate.runtimepermissions.PermissionsResultAction;
import com.vice.imtranslate.ui.fragment.ContactsFragment;
import com.vice.imtranslate.ui.fragment.ConversationListFragment;
import com.vice.imtranslate.ui.fragment.MeFragment;
import com.vice.imtranslate.utils.ShareprefencesUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.TreeMap;

public class MainActivity extends BaseActivity implements ConversationListFragment.refreshInterface {

    private View layout;
    private ViewPager mViewPager;
    private List<Fragment> pageList;
    private TextView tvChat;
    private TextView tvContacts;
    private TextView tvMe;
    private List<TextView> tabList;
    private EMMessageListener messageListener;
    private TextView tvUnread;
    private AlertDialog.Builder builder;
    private MyConnectionListener listener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestPermissions();

        hideBack();
        setTitle("ImTranslate");
        tvChat = (TextView) findViewById(R.id.tv_chat);
        tvContacts = (TextView) findViewById(R.id.tv_contacts);
        tvMe = (TextView) findViewById(R.id.tv_me);
        mViewPager = (ViewPager) findViewById(R.id.view_pager);

        tvUnread = (TextView) findViewById(R.id.tv_unread);

        //底部tab相关
        tabList = new ArrayList<>();
        tabList.add(tvChat);
        tabList.add(tvContacts);
        tabList.add(tvMe);
        for (int i = 0; i < tabList.size(); i++) {
            final int finalI = i;
            tabList.get(i).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    mViewPager.setCurrentItem(finalI, false);
                }
            });
        }

        //添加Fragment
        pageList = new ArrayList<>();
        pageList.add(new ConversationListFragment());
        pageList.add(new ContactsFragment());
        pageList.add(new MeFragment());

        //ViewPager相关
        mViewPager.setAdapter(new MainFragmentAdapter(getSupportFragmentManager(), pageList));
        MyPageChangeLintener myPageChangeLintener = new MyPageChangeLintener();
        mViewPager.addOnPageChangeListener(myPageChangeLintener);
        //初始化底部bar选中和标题显示
        tabList.get(0).setSelected(true);
        setTitle("会话");

        setConnectListener();

    }

    private void setConnectListener() {
        //注册一个监听连接状态的listener
        if (listener == null) {
            listener = new MyConnectionListener();
        }
        EMClient.getInstance().addConnectionListener(listener);
    }

    //连接状态监听
    class MyConnectionListener implements EMConnectionListener {
        @Override
        public void onConnected() {
        }

        @Override
        public void onDisconnected(final int error) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    if (error == EMError.USER_REMOVED) {
                        // 显示帐号已经被移除
                    } else if (error == EMError.USER_LOGIN_ANOTHER_DEVICE) {
                        // 显示帐号在其他设备登录
                        System.out.println("vvv"+"别处登录");
                        if (builder == null) {
                            builder = new AlertDialog.Builder(MainActivity.this);
                            builder.setMessage("您的账号已经在别处登录，请重新登录");
                            builder.setCancelable(false);
                            builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    ActivityControler.finishAll();
                                    startActivity(new Intent(App.getAppContext(), LoginActivity.class));
                                }
                            });
                        }
                        AlertDialog dialog = builder.create();
                        if (android.os.Build.VERSION.SDK_INT<23){
                            dialog.getWindow().setType(WindowManager.LayoutParams.TYPE_SYSTEM_ALERT);//指定会全局,可以在后台弹出
                        }
                        dialog.show();
                        ShareprefencesUtils.saveAutoLogin(MainActivity.this,false);
                    } else {
                        if (NetUtils.hasNetwork(MainActivity.this)) {
                            //连接不到聊天服务器
                            Toast.makeText(MainActivity.this, "服务器连接失败", Toast.LENGTH_SHORT).show();
                        } else {
                            //当前网络不可用，请检查网络设置
                            Toast.makeText(MainActivity.this, "当前网络不可用，请检查网咯", Toast.LENGTH_SHORT).show();
                        }
                    }
                }
            });
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        int unreadMsgsCount = EMClient.getInstance().chatManager().getUnreadMsgsCount();
        refreshBarUnreadCount(unreadMsgsCount);
//        EMMessageListener messageListener=new EMMessageListener() {
//            @Override
//            public void onMessageReceived(List<EMMessage> list) {
//                final int unreadMsgsCount1 = EMClient.getInstance().chatManager().getUnreadMsgsCount();
//                runOnUiThread(new Runnable() {
//                    @Override
//                    public void run() {
//                        tvUnread.setVisibility(View.VISIBLE);
//                        tvUnread.setText(unreadMsgsCount1+"");
//                    }
//                });
//            }
//
//            @Override
//            public void onCmdMessageReceived(List<EMMessage> list) {
//
//            }
//
//            @Override
//            public void onMessageReadAckReceived(List<EMMessage> list) {
//
//            }
//
//            @Override
//            public void onMessageDeliveryAckReceived(List<EMMessage> list) {
//
//            }
//
//            @Override
//            public void onMessageChanged(EMMessage emMessage, Object o) {
//
//            }
//        };
//        EMClient.getInstance().chatManager().addMessageListener(messageListener);
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (listener != null) {
            EMClient.getInstance().removeConnectionListener(listener);
        }
    }

    @Override
    View addContentLayout() {
        layout = getLayoutInflater().inflate(R.layout.activity_main, contentLayout, false);
        return layout;
    }

    @Override
    public void refreshBarUnreadCount(final int count) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (count == 0) {
                    tvUnread.setVisibility(View.INVISIBLE);
                } else {
                    tvUnread.setVisibility(View.VISIBLE);
                    tvUnread.setText(count + "");
                }
            }
        });
    }

    class MyPageChangeLintener implements ViewPager.OnPageChangeListener {

        @Override
        public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

        }

        @Override
        public void onPageSelected(int position) {
            for (int i = 0; i < tabList.size(); i++) {
                tabList.get(i).setSelected(i == position ? true : false);
            }
            if (position == 0) {
                setTitle("会话");
            } else if (position == 1) {
                setTitle("通信录");
            } else if (position == 2) {
                setTitle("我");
            }
        }

        @Override
        public void onPageScrollStateChanged(int state) {

        }
    }

    @TargetApi(23)
    private void requestPermissions() {
        PermissionsManager.getInstance().requestAllManifestPermissionsIfNecessary(this, new PermissionsResultAction() {
            @Override
            public void onGranted() {
//				Toast.makeText(MainActivity.this, "All permissions have been granted", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onDenied(String permission) {
                //Toast.makeText(MainActivity.this, "Permission " + permission + " has been denied", Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        PermissionsManager.getInstance().notifyPermissionsChange(permissions, grantResults);
    }
}
