package com.vice.imtranslate;

import android.app.ActivityManager;
import android.app.Application;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.BitmapFactory;
import android.support.v4.util.ArrayMap;

import com.hyphenate.EMConnectionListener;
import com.hyphenate.EMError;
import com.hyphenate.EMMessageListener;
import com.hyphenate.chat.EMClient;
import com.hyphenate.chat.EMConversation;
import com.hyphenate.chat.EMMessage;
import com.hyphenate.chat.EMOptions;
import com.hyphenate.chat.EMTextMessageBody;
import com.hyphenate.util.NetUtils;
import com.vice.imtranslate.global.ActivityControler;
import com.vice.imtranslate.global.Constants;
import com.vice.imtranslate.global.ForegroundActivitiesHelper;
import com.vice.imtranslate.net.TranslateHelper;
import com.vice.imtranslate.ui.activity.MainActivity;
import com.vice.imtranslate.utils.MessageUtils;
import com.vice.imtranslate.utils.ShareprefencesUtils;

import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * Created by vice on 2016/9/8.
 */
public class App extends Application {
    private static Context mAppContext;
    private EMMessageListener messageListener;
    private TranslateHelper translateHelper;

    public static Map<String,Integer> expressionMap;//表情映射


    @Override
    public void onCreate() {
        super.onCreate();
        mAppContext = getApplicationContext();

        int pid = android.os.Process.myPid();
        String processAppName = getAppName(pid);
// 如果APP启用了远程的service，此application:onCreate会被调用2次
// 为了防止环信SDK被初始化2次，加此判断会保证SDK被初始化1次
// 默认的APP会在以包名为默认的process name下运行，如果查到的process name不是APP的process name就立即返回

        if (processAppName == null || !processAppName.equalsIgnoreCase(mAppContext.getPackageName())) {

            // 则此application::onCreate 是被service 调用的，直接返回
            return;
        }


        EMOptions options = new EMOptions();
        options.setAutoLogin(true);//是否自动登录
// 默认添加好友时，是不需要验证的，改成需要验证
        options.setAcceptInvitationAlways(false);
//初始化
        EMClient.getInstance().init(getApplicationContext(), options);
//在做打包混淆时，关闭debug模式，避免消耗不必要的资源
        EMClient.getInstance().setDebugMode(true);

        //接收消息监听
        messageListener = new EMMessageListener() {
            @Override
            public void onMessageReceived(List<EMMessage> list) {
                for (EMMessage emMessage : list) {
                    if (emMessage.getType() == EMMessage.Type.TXT) {
                        //根据本地存储的信息判断是否要翻译
                        int language = ShareprefencesUtils.getReceivedLanguage(getAppContext(), EMClient.getInstance().getCurrentUser());
                        if (language == 0) {
                            //0为不用翻译
                            sendNotification(emMessage);//发送Notification
                        } else {
                            translateMessage(emMessage);
                        }
                    } else {
                        //非文字消息不翻译
                        sendNotification(emMessage);
                    }
                }
            }

            @Override
            public void onCmdMessageReceived(List<EMMessage> list) {

            }

            @Override
            public void onMessageReadAckReceived(List<EMMessage> list) {

            }

            @Override
            public void onMessageDeliveryAckReceived(List<EMMessage> list) {

            }

            @Override
            public void onMessageChanged(EMMessage emMessage, Object o) {

            }
        };
        EMClient.getInstance().chatManager().addMessageListener(messageListener);


        //表情映射
        expressionMap=new ArrayMap<>();
        for (int i=0;i<Constants.EXPRESSION_KEY.length;i++){
            expressionMap.put(Constants.EXPRESSION_KEY[i],Constants.EXPRESSION_VALUE[i]);
        }

    }

    private void translateMessage(final EMMessage emMessage) {
        int currentLanguage = ShareprefencesUtils.getReceivedLanguage(getAppContext(), EMClient.getInstance().getCurrentUser());
        if (null == translateHelper) {
            translateHelper = new TranslateHelper(getAppContext());
        }
        String content = MessageUtils.getMessageDigest(emMessage, getAppContext());
        try {
            translateHelper.translate(content, "auto", Constants.language[currentLanguage], new TranslateHelper.TranslateCallBack() {
                @Override
                public void onSuccess(String result) {
                    EMConversation currentConversation = EMClient.getInstance().chatManager().getConversation(emMessage.getFrom());
                    EMMessage translatedMessage = EMMessage.createReceiveMessage(EMMessage.Type.TXT);
                    translatedMessage.addBody(new EMTextMessageBody(result));
                    translatedMessage.setFrom(emMessage.getFrom());
                    translatedMessage.setTo(emMessage.getTo());
                    translatedMessage.setMsgTime(emMessage.getMsgTime());
                    currentConversation.removeMessage(emMessage.getMsgId());
                    currentConversation.insertMessage(translatedMessage);
//                    EMClient.getInstance().chatManager().updateMessage(emMessage);
                    sendNotification(translatedMessage);//发送Notification
                }

                @Override
                public void onFailure(String exception) {
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void sendNotification(EMMessage translatedMessage) {
        if (ForegroundActivitiesHelper.getInstance().hasForegroundActivities()) {
            //在前台
        } else {
            //在后台，发Notification

            // 在Android进行通知处理，首先需要重系统哪里获得通知管理器NotificationManager，它是一个系统Service。
            NotificationManager manager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
            PendingIntent pendingIntent = PendingIntent.getActivity(this, 0,
                    new Intent(this, MainActivity.class), 0);
            // 通过Notification.Builder来创建通知，注意API Level
            // API11之后才支持
            Notification notify = new Notification.Builder(this)
                    .setSmallIcon(R.mipmap.ic_launcher) // 设置状态栏中的小图片，尺寸一般建议在24×24，这个图片同样也是在下拉状态栏中所显示，如果在那里需要更换更大的图片，可以使用setLargeIcon(Bitmap
                    // icon)
                    .setTicker("新消息提醒")// 设置在status
                    // bar上显示的提示文字
                    .setContentTitle("新消息")// 设置在下拉status
                    // bar后Activity，本例子中的NotififyMessage的TextView中显示的标题
                    .setContentText(translatedMessage.getFrom() + ":" + MessageUtils.getMessageDigest(translatedMessage, getAppContext()))// TextView中显示的详细内容
                    .setContentIntent(pendingIntent) // 关联PendingIntent
                    .setNumber(1) // 在TextView的右方显示的数字，可放大图片看，在最右侧。这个number同时也起到一个序列号的左右，如果多个触发多个通知（同一ID），可以指定显示哪一个。
                    .getNotification(); // 需要注意build()是在API level
            // 16及之后增加的，在API11中可以使用getNotificatin()来代替
            notify.flags = Notification.FLAG_AUTO_CANCEL;
            manager.notify(0, notify);
        }
    }

    public static Context getAppContext() {
        return mAppContext;
    }

    private String getAppName(int pID) {
        String processName = null;
        ActivityManager am = (ActivityManager) this.getSystemService(ACTIVITY_SERVICE);
        List l = am.getRunningAppProcesses();
        Iterator i = l.iterator();
        PackageManager pm = this.getPackageManager();
        while (i.hasNext()) {
            ActivityManager.RunningAppProcessInfo info = (ActivityManager.RunningAppProcessInfo) (i.next());
            try {
                if (info.pid == pID) {
                    processName = info.processName;
                    return processName;
                }
            } catch (Exception e) {
                // Log.d("Process", "Error>> :"+ e.toString());
            }
        }
        return processName;
    }

}
