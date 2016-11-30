package com.vice.imtranslate.utils;

import android.content.Context;
import android.text.TextUtils;

import com.hyphenate.chat.EMMessage;
import com.hyphenate.chat.EMTextMessageBody;
import com.hyphenate.util.EMLog;
import com.vice.imtranslate.R;
import com.vice.imtranslate.global.EaseConstant;

/**
 * Created by vice on 2016/9/8.
 */
public class MessageUtils {
    /**
     * Get digest according message type and content
     *
     * @param message
     * @param context
     * @return
     */
    public static String getMessageDigest(EMMessage message, Context context) {
        String digest = "";
        switch (message.getType()) {
            case LOCATION:
                if (message.direct() == EMMessage.Direct.RECEIVE) {
                    digest = getString(context, R.string.location_recv);
                    digest = String.format(digest, message.getFrom());
                    return digest;
                } else {
                    digest = getString(context, R.string.location_prefix);
                }
                break;
            case IMAGE:
                digest = getString(context, R.string.picture);
                break;
            case VOICE:
                digest = getString(context, R.string.voice_prefix);
                break;
            case VIDEO:
                digest = getString(context, R.string.video);
                break;
            case TXT:
                EMTextMessageBody txtBody = (EMTextMessageBody) message.getBody();
                if(message.getBooleanAttribute(EaseConstant.MESSAGE_ATTR_IS_VOICE_CALL, false)){
                    digest = getString(context, R.string.voice_call) + txtBody.getMessage();
                }else if(message.getBooleanAttribute(EaseConstant.MESSAGE_ATTR_IS_VIDEO_CALL, false)){
                    digest = getString(context, R.string.video_call) + txtBody.getMessage();
                }else if(message.getBooleanAttribute(EaseConstant.MESSAGE_ATTR_IS_BIG_EXPRESSION, false)){
                    if(!TextUtils.isEmpty(txtBody.getMessage())){
                        digest = txtBody.getMessage();
                    }else{
                        digest = getString(context, R.string.dynamic_expression);
                    }
                }else{
                    digest = txtBody.getMessage();
                }
                break;
            case FILE:
                digest = getString(context, R.string.file);
                break;
            default:
                return "";
        }

        return digest;
    }
    static String getString(Context context, int resId){
        return context.getResources().getString(resId);
    }

}
