package com.vice.imtranslate.global;

import com.hyphenate.EMMessageListener;
import com.hyphenate.chat.EMClient;
import com.hyphenate.chat.EMMessage;

import java.util.List;

/**
 * Created by vice on 2016/9/9.
 */
public class ImHelper {
    private static ImHelper imHelper;


    public ImHelper() {
    }

    public static ImHelper getInstance(){
        if (imHelper==null){
            imHelper=new ImHelper();
        }
        return imHelper;
    }

    public void init(){


        initListener();
    }

    private void initListener() {
        //消息监听
        EMClient.getInstance().chatManager().addMessageListener(msgListener);

    }


    EMMessageListener msgListener = new EMMessageListener() {

        @Override
        public void onMessageReceived(List<EMMessage> messages) {
            //收到消息
        }

        @Override
        public void onCmdMessageReceived(List<EMMessage> messages) {
            //收到透传消息
        }

        @Override
        public void onMessageReadAckReceived(List<EMMessage> messages) {
            //收到已读回执
        }

        @Override
        public void onMessageDeliveryAckReceived(List<EMMessage> message) {
            //收到已送达回执
        }

        @Override
        public void onMessageChanged(EMMessage message, Object change) {
            //消息状态变动
        }
    };

}
