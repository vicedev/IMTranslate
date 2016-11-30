package com.vice.imtranslate.ui.activity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.view.View;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.hyphenate.chat.EMClient;
import com.vice.imtranslate.App;
import com.vice.imtranslate.R;
import com.vice.imtranslate.global.ActivityControler;
import com.vice.imtranslate.global.Constants;
import com.vice.imtranslate.global.ForegroundActivitiesHelper;
import com.vice.imtranslate.utils.ShareprefencesUtils;
import com.vice.imtranslate.utils.StatusBarUtils;

/**
 * Created by vice on 2016/9/8.
 */
public abstract class BaseActivity extends FragmentActivity {

    protected Button btnBack;
    protected TextView tvTitle;
    protected RelativeLayout contentLayout;
    private Button btnLanguage;
//    protected int currentLanguage;

    private AlertDialog.Builder builder;
    protected int currentLanguage;
    private RelativeLayout titleBar;

    /**
     * <item>自动检测</item>
     * <item>中文 </item>
     * <item>英语</item>
     * <item>粤语</item>
     * <item>文言文</item>
     * <item>日语</item>
     * <item>韩语 </item>
     * <item>法语</item>
     * <item>西班牙语</item>
     * <item>泰语</item>
     * <item>阿拉伯语</item>
     * <item>俄语 </item>
     * <item>葡萄牙语</item>
     * <item>德语</item>
     * <item>意大利语</item>
     * <item>希腊语</item>
     * <item>荷兰语 </item>
     * <item>波兰语</item>
     * <item>保加利亚语</item>
     * <item>爱沙尼亚语</item>
     * <item>丹麦语</item>
     * <item>芬兰语 </item>
     * <item>捷克语</item>
     * <item>罗马尼亚语</item>
     * <item>斯洛文尼亚语</item>
     * <item>瑞典语</item>
     * <item>匈牙利语 </item>
     * <item>繁体中文</item>
     *
     * @param savedInstanceState
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ActivityControler.addActivity(this);

        setContentView(R.layout.activity_base);

        StatusBarUtils.setWindowStatusBarColor(this,R.color.statusbar_color);//设置状态栏颜色

        titleBar = (RelativeLayout) findViewById(R.id.rl_titlebar);
        contentLayout = (RelativeLayout) findViewById(R.id.content_layout);
        contentLayout.addView(addContentLayout());

        tvTitle = (TextView) findViewById(R.id.tv_title);
        btnBack = (Button) findViewById(R.id.btn_back);
        btnLanguage = (Button) findViewById(R.id.btn_language);

        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
                onFinish();
            }
        });

        currentLanguage = ShareprefencesUtils.getReceivedLanguage(App.getAppContext(), EMClient.getInstance().getCurrentUser());
        btnLanguage.setText(Constants.languageName[currentLanguage]);

        btnLanguage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (builder == null) {
                    builder = new AlertDialog.Builder(BaseActivity.this);
                    builder.setItems(Constants.languageName, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            if (listener != null) {
                                listener.onChange(i);
                            }
                            btnLanguage.setText(Constants.languageName[i]);
                            ShareprefencesUtils.saveReceivedLanguage(App.getAppContext(), EMClient.getInstance().getCurrentUser(),i);
                        }
                    });
                }
                builder.show();
            }
        });
    }

    abstract View addContentLayout();

    public void hideBack() {
        btnBack.setVisibility(View.GONE);
    }

    public void setTitle(String title) {
        tvTitle.setText(title);
    }

    public void showLanguage() {
        btnLanguage.setVisibility(View.VISIBLE);
    }

    public void hideTitleBar(){
        titleBar.setVisibility(View.GONE);
    }

    public interface onLanguageChangedListener {
        void onChange(int language);
    }

    protected void onFinish(){

    }

    protected onLanguageChangedListener listener;

    @Override
    protected void onDestroy() {
        super.onDestroy();
        ActivityControler.removeActivity(this);
    }

    @Override
    protected void onResume() {
        super.onResume();
        ForegroundActivitiesHelper.getInstance().pushActivity(this);
    }

    @Override
    protected void onStop() {
        super.onStop();
        ForegroundActivitiesHelper.getInstance().popActivity(this);
    }
}
