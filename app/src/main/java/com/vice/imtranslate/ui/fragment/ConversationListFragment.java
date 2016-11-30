package com.vice.imtranslate.ui.fragment;


import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

import com.hyphenate.EMMessageListener;
import com.hyphenate.chat.EMClient;
import com.hyphenate.chat.EMConversation;
import com.hyphenate.chat.EMMessage;
import com.hyphenate.chat.EMTextMessageBody;
import com.vice.imtranslate.App;
import com.vice.imtranslate.R;
import com.vice.imtranslate.adapter.FragmentConversationListAdapter;
import com.vice.imtranslate.global.Constants;
import com.vice.imtranslate.net.TranslateHelper;
import com.vice.imtranslate.ui.activity.ChatActivity;
import com.vice.imtranslate.ui.activity.MainActivity;
import com.vice.imtranslate.utils.MessageUtils;
import com.vice.imtranslate.utils.ShareprefencesUtils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;

/**
 * A simple {@link Fragment} subclass.
 */
public class ConversationListFragment extends Fragment {
    private ListView lvConversation;
    private List<EMConversation> list;
    private TranslateHelper translateHelper;
    private EMMessageListener messageListener;
    private ConversationListFragment.refreshInterface refreshInterface;
    private AlertDialog dialog;
    private FragmentConversationListAdapter adapter;

    public ConversationListFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View layout = inflater.inflate(R.layout.fragment_conversation, container, false);
        lvConversation = (ListView) layout.findViewById(R.id.lv_conversation);

        lvConversation.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                String userId = list.get(i).getUserName();
                Intent intent = new Intent(getActivity(), ChatActivity.class);
                intent.putExtra(Constants.USER_ID, userId);
                startActivity(intent);
                getActivity().overridePendingTransition(R.anim.activity_in_from_right, R.anim.activity_out_to_left);
            }
        });
        lvConversation.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> adapterView, View view, int i, long l) {
                //弹出对话处理对话框
                showHandleConversationDialog(i);
                return true;
            }
        });

        return layout;
    }

    private void showHandleConversationDialog(final int position) {
        dialog = new AlertDialog.Builder(getActivity())
                .setItems(new String[]{"删除会话（删除聊天记录）", "删除会话（不删除聊天记录）"}, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        handleConversation(position, i);
                    }
                }).create();
        dialog.show();
    }

    private void handleConversation(int position, int i) {
        if (i == 0) {
//删除和某个user会话，如果需要保留聊天记录，传false
            EMClient.getInstance().chatManager().deleteConversation(list.get(position).getUserName(), true);
        } else if (i == 1) {
            EMClient.getInstance().chatManager().deleteConversation(list.get(position).getUserName(), false);
        }
        loadLateConversations();
        int count = EMClient.getInstance().chatManager().getUnreadMsgsCount();
        refreshInterface.refreshBarUnreadCount(count);
        if (adapter != null) {
            adapter.notifyDataSetChanged();
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (getActivity() instanceof MainActivity) {
            refreshInterface = (ConversationListFragment.refreshInterface) getActivity();
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        loadLateConversations();

        if (null == messageListener) {
            messageListener = new EMMessageListener() {
                @Override
                public void onMessageReceived(List<EMMessage> list) {
                    for (EMMessage emMessage : list) {
                        if (emMessage.getType() == EMMessage.Type.TXT) {
                            int language = ShareprefencesUtils.getReceivedLanguage(App.getAppContext(), EMClient.getInstance().getCurrentUser());
                            if (language == 0) {
                                if (getActivity() != null) {
                                    getActivity().runOnUiThread(new Runnable() {
                                        @Override
                                        public void run() {
                                            loadLateConversations();
                                            int count = EMClient.getInstance().chatManager().getUnreadMsgsCount();
                                            refreshInterface.refreshBarUnreadCount(count);
                                        }
                                    });
                                }
                            } else {
                                translateMessage(emMessage);
                            }
                        }else if (emMessage.getType()== EMMessage.Type.IMAGE){
                            if (getActivity() != null) {
                                getActivity().runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        loadLateConversations();
                                        int count = EMClient.getInstance().chatManager().getUnreadMsgsCount();
                                        refreshInterface.refreshBarUnreadCount(count);
                                    }
                                });
                            }
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
        }
        EMClient.getInstance().chatManager().addMessageListener(messageListener);

    }

    public interface refreshInterface {
        void refreshBarUnreadCount(int count);
//        void setOnUnreadCountChanged();
    }

    private void translateMessage(final EMMessage emMessage) {
        int currentLanguage = ShareprefencesUtils.getReceivedLanguage(App.getAppContext(), EMClient.getInstance().getCurrentUser());
        if (null == translateHelper) {
            translateHelper = new TranslateHelper(getActivity());
        }
        String content = MessageUtils.getMessageDigest(emMessage, getActivity());
        try {
            translateHelper.translate(content, "auto", Constants.language[currentLanguage], new TranslateHelper.TranslateCallBack() {
                @Override
                public void onSuccess(String result) {
                    EMTextMessageBody body = new EMTextMessageBody(result);
                    emMessage.addBody(body);

                    loadLateConversations();

                    int count = EMClient.getInstance().chatManager().getUnreadMsgsCount();
                    refreshInterface.refreshBarUnreadCount(count);
                }

                @Override
                public void onFailure(String exception) {
                    System.out.println("vvv" + "faile");

                    loadLateConversations();

                    int count = EMClient.getInstance().chatManager().getUnreadMsgsCount();
                    refreshInterface.refreshBarUnreadCount(count);
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (messageListener != null) {
            EMClient.getInstance().chatManager().removeMessageListener(messageListener);
        }
    }

    /**
     * 获取最近聊天列表
     */

    private void loadLateConversations() {
        //按时间排序
        List<Pair<Long, EMConversation>> sortList = new ArrayList<Pair<Long, EMConversation>>();
        Map<String, EMConversation> allConversations = EMClient.getInstance().chatManager().getAllConversations();

        for (EMConversation conversation : allConversations.values()) {
            if (conversation.getAllMessages().size() != 0) {
                sortList.add(new Pair<Long, EMConversation>(conversation.getLastMessage().getMsgTime(), conversation));
            }
        }
        try {
            // Internal is TimSort algorithm, has bug
            sortConversationByLastChatTime(sortList);
        } catch (Exception e) {
            e.printStackTrace();
        }
        list = new ArrayList<EMConversation>();
        for (Pair<Long, EMConversation> sortItem : sortList) {
            list.add(sortItem.second);
        }
        if (list != null) {
            adapter = new FragmentConversationListAdapter(getActivity(), list);
            lvConversation.setAdapter(adapter);
            adapter.notifyDataSetChanged();
        }
    }

    private void sortConversationByLastChatTime(List<Pair<Long, EMConversation>> conversationList) {
        Collections.sort(conversationList, new Comparator<Pair<Long, EMConversation>>() {
            @Override
            public int compare(final Pair<Long, EMConversation> con1, final Pair<Long, EMConversation> con2) {

                if (con1.first == con2.first) {
                    return 0;
                } else if (con2.first > con1.first) {
                    return 1;
                } else {
                    return -1;
                }
            }

        });
    }

}
