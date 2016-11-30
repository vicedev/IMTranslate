package com.vice.imtranslate.adapter;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.hyphenate.chat.EMConversation;
import com.hyphenate.chat.EMMessage;
import com.vice.imtranslate.R;
import com.vice.imtranslate.utils.MessageUtils;
import com.vice.imtranslate.utils.SpannableStringUtil;
import com.vice.imtranslate.utils.TimeFormatUtils;

import java.util.List;

/**
 * Created by vice on 2016/9/8.
 */
public class FragmentConversationListAdapter extends BaseAdapter {
    private List<EMConversation> conversationList;
    private Context mContext;
    public FragmentConversationListAdapter(Context context, List<EMConversation> conversationList) {
        this.conversationList=conversationList;
        mContext=context;
    }

    @Override
    public int getCount() {
        return conversationList.size();
    }

    @Override
    public Object getItem(int i) {
        return conversationList.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        ViewHolder holder;
        if (null==view){
            holder=new ViewHolder();
            view=View.inflate(mContext, R.layout.fragment_conversation_list_item,null);
            holder.ivAvatar= (ImageView) view.findViewById(R.id.iv_avatar);
            holder.tvNickname= (TextView) view.findViewById(R.id.tv_nickname);
            holder.tvTime= (TextView) view.findViewById(R.id.tv_time);
            holder.tvContent= (TextView) view.findViewById(R.id.tv_content);
            holder.tvUnread= (TextView) view.findViewById(R.id.tv_unread);
            view.setTag(holder);
        }else{
            holder= (ViewHolder) view.getTag();
        }

        EMMessage lastMessage=conversationList.get(i).getLastMessage();
        EMConversation emConversation = conversationList.get(i);

        int unreadMsg=emConversation.getUnreadMsgCount();
        long time=lastMessage.getMsgTime();
        String strTime = TimeFormatUtils.getStrTime(String.valueOf(time));
        String replaceAll = strTime.replaceAll("年", "/").replaceAll("月", "/").replaceAll("日", "");
        String url = null;
        //TODO 写死头像
        if (emConversation.getUserName().equals("test1")){
            url="http://img5.duitang.com/uploads/item/201508/26/20150826195235_xzB8K.thumb.700_0.jpeg";
        }else if (emConversation.getUserName().equals("test2")){
            url="http://a.hiphotos.baidu.com/zhidao/pic/item/91529822720e0cf3ef6c79630c46f21fbf09aa8a.jpg";
        }else if (emConversation.getUserName().equals("test3")){
            url="http://img4.imgtn.bdimg.com/it/u=3257207530,468293946&fm=206&gp=0.jpg";
        }
        Glide.with(mContext)
                .load(url)
                .placeholder(R.mipmap.ic_launcher)
                .centerCrop()
                .into(holder.ivAvatar);

        holder.tvNickname.setText(emConversation.getUserName());
        holder.tvTime.setText(replaceAll);
        String content=MessageUtils.getMessageDigest(lastMessage,mContext);
        holder.tvContent.setText(SpannableStringUtil.getContent(mContext,holder.tvContent,content));

        if (unreadMsg!=0){
            holder.tvUnread.setText(unreadMsg+"");
            holder.tvUnread.setVisibility(View.VISIBLE);
        }else{
            holder.tvUnread.setVisibility(View.INVISIBLE);
        }

        return view;
    }

    class ViewHolder{
        ImageView ivAvatar;
        TextView tvNickname;
        TextView tvTime;
        TextView tvContent;
        TextView tvUnread;
    }
}
