package com.vice.imtranslate.ui.fragment;


import android.app.Activity;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.SystemClock;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.hyphenate.EMCallBack;
import com.hyphenate.EMMessageListener;
import com.hyphenate.chat.EMClient;
import com.hyphenate.chat.EMConversation;
import com.hyphenate.chat.EMImageMessageBody;
import com.hyphenate.chat.EMMessage;
import com.hyphenate.chat.EMMessageBody;
import com.hyphenate.chat.EMTextMessageBody;
import com.hyphenate.util.PathUtil;
import com.vice.imtranslate.App;
import com.vice.imtranslate.R;
import com.vice.imtranslate.adapter.FragmentChatListAdapter;
import com.vice.imtranslate.global.Constants;
import com.vice.imtranslate.net.TranslateHelper;
import com.vice.imtranslate.ui.activity.BaseActivity;
import com.vice.imtranslate.ui.activity.ChatActivity;
import com.vice.imtranslate.ui.activity.ImageShowActivity;
import com.vice.imtranslate.ui.custom.CustomExpressionMenu;
import com.vice.imtranslate.ui.custom.CustomExtendMenu;
import com.vice.imtranslate.utils.MessageUtils;
import com.vice.imtranslate.utils.ShareprefencesUtils;
import com.vice.imtranslate.utils.SpannableStringUtil;
import com.vice.imtranslate.utils.VUtils;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 */
public class ChatFragment extends Fragment {
    private TransferInterface transferInterface;
    private String userId;//对方的userId
    private ListView lvChat;
    private EditText etInput;
    private Button btnSend;

    private EMMessageListener msgListener;
    private List<EMMessage> messageList;
    private FragmentChatListAdapter adapter;
    private TranslateHelper translateHelper;
    private EMConversation currentConversation;

    private int currentLanguage;
    private boolean isLoadMore;
    private boolean hasMore = true;
    private ImageButton ibMore;
    private FrameLayout flMenuContainer;
    private CustomExtendMenu extendMenu;
    private File cameraFile;
    private ImageButton ibExpression;
    private CustomExpressionMenu expressionMenu;

    public ChatFragment() {

    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        transferInterface = (TransferInterface) getActivity();
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        userId = transferInterface.getUserId();

        currentLanguage = transferInterface.getCurrentLanguage();

        if (msgListener == null) {
            msgListener = new EMMessageListener() {

                @Override
                public void onMessageReceived(List<EMMessage> messages) {
                    //收到消息
                    for (EMMessage emMessage : messages) {
                        //防止显示非当前对话用户显示
                        if (emMessage.getFrom().equals(userId)) {
                            if (emMessage.getType() == EMMessage.Type.TXT) {
                                if (ShareprefencesUtils.getReceivedLanguage(App.getAppContext(), EMClient.getInstance().getCurrentUser()) == 0) {
                                    //不翻译
                                    messageList.add(emMessage);
                                    runUiRefresh();
                                } else {
                                    translateMessage(emMessage);
                                }
//                            System.out.println("vvv线程"+Thread.currentThread().getName());

                            } else if (emMessage.getType() == EMMessage.Type.IMAGE) {
                                messageList.add(emMessage);
                                runUiRefresh();
                            }
                        }
                    }
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
            EMClient.getInstance().chatManager().addMessageListener(msgListener);
        }
        messageList = getHistoryMessages();
        adapter = new FragmentChatListAdapter(getActivity(), messageList, new FragmentChatListAdapter.HandleMessageInterface() {
            @Override
            public void onLongClick(int position) {
                showHandleMessageDialog(position);
            }

            @Override
            public void onClick(int position) {
                EMMessage emMessage = messageList.get(position);
                if (emMessage.getType() == EMMessage.Type.IMAGE) {
                    EMImageMessageBody body = (EMImageMessageBody) emMessage.getBody();
                    Intent intent = new Intent(getActivity(), ImageShowActivity.class);
                    intent.putExtra(ImageShowActivity.IMAGE_PATH,emMessage.getFrom().equals(EMClient.getInstance().getCurrentUser())?body.getLocalUrl(): body.getRemoteUrl());
                    startActivity(intent);
                }
            }
        });
        lvChat.setAdapter(adapter);
        refreshToLast();

        //把所有消息标记为已经读
        if (null == currentConversation) {
            currentConversation = EMClient.getInstance().chatManager().getConversation(userId);
        }
        if (null != currentConversation) {
            currentConversation.markAllMessagesAsRead();
        }
    }

    private void translateMessage(final EMMessage emMessage) {
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
                    EMClient.getInstance().chatManager().updateMessage(emMessage);
                    messageList.add(emMessage);
                    runUiRefresh();
                }

                @Override
                public void onFailure(String exception) {
                    messageList.add(emMessage);
                    runUiRefresh();
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void loadMoreHistoryMessages() {
        if (currentConversation != null) {
            currentConversation.loadMoreMsgFromDB(messageList.get(0).getMsgId(), 20);
            List<EMMessage> allMessages = currentConversation.getAllMessages();
            if (allMessages.size() != 0) {
                if (allMessages.get(0).getMsgId().equals(messageList.get(0).getMsgId())) {
                    hasMore = false;
                    return;
                }
            }
            messageList.clear();
            messageList.addAll(allMessages);
        }

    }

    private List<EMMessage> getHistoryMessages() {
        EMConversation conversation = EMClient.getInstance().chatManager().getConversation(userId);
        //获取此会话的所有消息
        if (conversation != null) {
            conversation.loadMoreMsgFromDB(null, 20);
            List<EMMessage> messages = conversation.getAllMessages();
            return messages;
        }
        return new ArrayList<>();
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View layout = inflater.inflate(R.layout.fragment_chat, container, false);
        lvChat = (ListView) layout.findViewById(R.id.lv_chat);
        etInput = (EditText) layout.findViewById(R.id.et_input);
        btnSend = (Button) layout.findViewById(R.id.btn_send);
        ibMore = (ImageButton) layout.findViewById(R.id.ib_more);
        ibExpression = (ImageButton) layout.findViewById(R.id.ib_expression);

        //底部菜单容器
        flMenuContainer = (FrameLayout) layout.findViewById(R.id.fl_menu_container);
        //底部功能菜单
        extendMenu = (CustomExtendMenu) layout.findViewById(R.id.extend_menu);
        //表情菜单
        expressionMenu = (CustomExpressionMenu) layout.findViewById(R.id.expression_menu);

        //ListView设置
        //设置item按下颜色
        lvChat.setSelector(new ColorDrawable());//默认透明色
        //设置分割线
        lvChat.setDivider(null); //取消掉分隔线
        //解决有时滑动ListView背景会变为黑色
        lvChat.setCacheColorHint(Color.TRANSPARENT);//设置背景为透明

        etInput.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                if (motionEvent.getAction() == MotionEvent.ACTION_UP) {
                    flMenuContainer.setVisibility(View.GONE);
                    refreshToLast();
                }
                return false;
            }
        });

        btnSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String content = etInput.getText().toString();
                //创建一条文本消息，content为消息文字内容，toChatUsername为对方用户或者群聊的id，后文皆是如此
                EMMessage message = EMMessage.createTxtSendMessage(content, userId);
                sendMessage(message);
                etInput.setText("");
                refreshToLast();
            }
        });

        etInput.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                String content = etInput.getText().toString();
                if (TextUtils.isEmpty(content)) {
                    btnSend.setVisibility(View.GONE);
                    ibMore.setVisibility(View.VISIBLE);
                } else {
                    btnSend.setVisibility(View.VISIBLE);
                    ibMore.setVisibility(View.GONE);
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

//        userId=getArguments().getString("userId");
//        messageList = getHistoryMessages();
//        if (messageList!=null){
//            adapter = new FragmentChatListAdapter(getActivity(),messageList);
//            lvChat.setAdapter(adapter);
//            refreshToLast();
//        }
        lvChat.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                hideKeyboard();
                flMenuContainer.setVisibility(View.GONE);
                return false;
            }
        });

        //下拉加载更多历史消息
        lvChat.setOnScrollListener(new AbsListView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView absListView, int i) {

            }

            @Override
            public void onScroll(AbsListView absListView, int i, int i1, int i2) {
                System.out.println("vvvposition" + lvChat.getFirstVisiblePosition());
                if (hasMore && !isLoadMore && lvChat.getFirstVisiblePosition() == 0 && messageList != null && messageList.size() != 0) {
                    isLoadMore = true;
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            final int size = messageList.size();
                            loadMoreHistoryMessages();
                            if (getActivity() != null) {
                                getActivity().runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        refreshToPosition(messageList.size() - size);
                                        isLoadMore = false;
                                    }
                                });
                            }
                        }
                    }).start();
                }
            }
        });

        //从Activity获取选择的语言
        transferInterface.setOnLanguageChangeListener(new BaseActivity.onLanguageChangedListener() {
            @Override
            public void onChange(int language) {
                currentLanguage = language;
            }
        });

        extendMenu.setOnMenuItemClickListener(new CustomExtendMenu.MenuItemClickListener() {
            @Override
            public void onMenuItemlick(int type) {
                if (type == CustomExtendMenu.PICTURE) {
                    selectPicFromLocal();
                } else if (type == CustomExtendMenu.TAKE_PHOTO) {
                    selectPicFromCamera();
                }
            }
        });

        ibMore.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                refreshToLast();
                hideKeyboard();
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        if (getActivity() != null) {
                            if (flMenuContainer.getVisibility()==View.GONE){
                                flMenuContainer.setVisibility(View.VISIBLE);
                                extendMenu.setVisibility(View.VISIBLE);
                                expressionMenu.setVisibility(View.INVISIBLE);
                            }else{
                                if (extendMenu.getVisibility()==View.VISIBLE){
                                    flMenuContainer.setVisibility(View.GONE);
                                }else{
                                    extendMenu.setVisibility(View.VISIBLE);
                                    expressionMenu.setVisibility(View.INVISIBLE);
                                }
                            }
                        }
                    }
                }, 150);
            }
        });

        ibExpression.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                refreshToLast();
                hideKeyboard();
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        if (getActivity() != null) {
                            if (flMenuContainer.getVisibility()==View.GONE){
                                flMenuContainer.setVisibility(View.VISIBLE);
                                expressionMenu.setVisibility(View.VISIBLE);
                                extendMenu.setVisibility(View.INVISIBLE);
                            }else{
                                if (expressionMenu.getVisibility()==View.VISIBLE){
                                    flMenuContainer.setVisibility(View.GONE);
                                }else{
                                    expressionMenu.setVisibility(View.VISIBLE);
                                    extendMenu.setVisibility(View.INVISIBLE);
                                }
                            }
                        }
                    }
                }, 150);
            }
        });

        expressionMenu.setOnExpressionClickListener(new CustomExpressionMenu.OnExpressionClickListener() {
            @Override
            public void onClick(int expressionType, int position) {
                if (expressionType==Constants.CLASSICAL_EXPRESSION){
                    //经典表情
                    if (position==-1){
                        //删除按钮
                        etInput.dispatchKeyEvent(new KeyEvent(KeyEvent.ACTION_DOWN,KeyEvent.KEYCODE_DEL));
                    }else{
                        int currentPosition=etInput.getSelectionStart();
                        StringBuilder sb=new StringBuilder(etInput.getText().toString());
                        sb.insert(currentPosition,Constants.EXPRESSION_KEY[position]);
                        etInput.setText(SpannableStringUtil.getContent(getActivity(),etInput,sb.toString()));
                        etInput.setSelection(currentPosition+Constants.EXPRESSION_KEY[position].length());
                    }
                }
            }
        });

        return layout;
    }

    private static final int REQUEST_CODE_LOCAL = 3;
    private static final int REQUEST_CODE_CAMERA = 2;

    private void selectPicFromLocal() {
        Intent intent;
        if (Build.VERSION.SDK_INT < 19) {
            intent = new Intent(Intent.ACTION_GET_CONTENT);
            intent.setType("image/*");

        } else {
            intent = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        }
        startActivityForResult(intent, REQUEST_CODE_LOCAL);
    }

    private void selectPicFromCamera() {
        if (!VUtils.isSdcardExist()) {
            Toast.makeText(getActivity(), R.string.sd_card_does_not_exist, Toast.LENGTH_SHORT).show();
            return;
        }

        cameraFile = new File(PathUtil.getInstance().getImagePath(), EMClient.getInstance().getCurrentUser()
                + System.currentTimeMillis() + ".jpg");
        cameraFile.getParentFile().mkdirs();
        startActivityForResult(
                new Intent(MediaStore.ACTION_IMAGE_CAPTURE).putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(cameraFile)),
                REQUEST_CODE_CAMERA);
    }


    private final String COPY_MESSAGE = "复制消息";
    private final String DELETE_MESSAGE = "删除消息";

    private void showHandleMessageDialog(final int position) {
        EMMessage emMessage = messageList.get(position);
        String[] items = new String[]{DELETE_MESSAGE};
        if (emMessage.getType() == EMMessage.Type.TXT) {
            items = new String[]{COPY_MESSAGE, DELETE_MESSAGE};
        } else if (emMessage.getType() == EMMessage.Type.IMAGE) {
            items = new String[]{DELETE_MESSAGE};
        }
        final String[] finalItems = items;
        AlertDialog dialog = new AlertDialog.Builder(getActivity())
                .setItems(items, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        handleMessage(position, finalItems[i]);
                    }
                }).create();
        dialog.show();
    }

    private void handleMessage(int position, String item) {
        if (item == COPY_MESSAGE) {
            //复制到剪贴板
            ClipboardManager cm = (ClipboardManager) getActivity().getSystemService(Context.CLIPBOARD_SERVICE);
            ClipData myClip;
            myClip = ClipData.newPlainText("text", MessageUtils.getMessageDigest(messageList.get(position), getActivity()));
            cm.setPrimaryClip(myClip);
        } else if (item == DELETE_MESSAGE) {
//删除当前会话的某条聊天记录
            EMConversation conversation = EMClient.getInstance().chatManager().getConversation(userId);
            conversation.removeMessage(messageList.get(position).getMsgId());
            messageList.remove(position);
            refresh();
        }
    }

    private void sendMessage(final EMMessage message) {

        //发送消息
        EMClient.getInstance().chatManager().sendMessage(message);
        message.setMessageStatusCallback(new EMCallBack() {

            @Override
            public void onSuccess() {
                message.setStatus(EMMessage.Status.SUCCESS);
                refreshOnUi();
            }

            @Override
            public void onError(int i, String s) {
                message.setStatus(EMMessage.Status.FAIL);
                refreshOnUi();
            }

            @Override
            public void onProgress(int i, String s) {
                message.setStatus(EMMessage.Status.INPROGRESS);
                refreshOnUi();
            }
        });
        messageList.add(message);

    }

    private void refreshOnUi() {
        if (getActivity() != null) {
            getActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    refresh();
                }
            });
        }
    }

    private void runUiRefresh() {
        currentConversation.markAllMessagesAsRead();
        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (lvChat.getFirstVisiblePosition() >= messageList.size() - 12) {
                    refreshSmoothToLast();
                } else {
                    refresh();
                }
            }
        });
    }

    private void refreshToPosition(int position) {
        if (adapter != null) {
            adapter.notifyDataSetChanged();
            lvChat.setSelection(position);
        }
    }

    private void refresh() {
        if (adapter != null) {
            adapter.notifyDataSetChanged();
        }
    }

    private void refreshSmoothToLast() {
        if (adapter != null) {
            adapter.notifyDataSetChanged();
            lvChat.smoothScrollToPosition(messageList.size() - 1);
        }
    }

    private void refreshToLast() {
        if (adapter != null) {
            adapter.notifyDataSetChanged();
            lvChat.setSelection(messageList.size() - 1);
        }
    }

    public interface TransferInterface {
        String getUserId();

        void setOnLanguageChangeListener(BaseActivity.onLanguageChangedListener listener);

        int getCurrentLanguage();
    }


    @Override
    public void onDestroy() {
        super.onDestroy();
        if (msgListener != null) {
            EMClient.getInstance().chatManager().removeMessageListener(msgListener);
        }
    }

    private void hideKeyboard() {
        //1.得到InputMethodManager对象
        InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
//2.调用hideSoftInputFromWindow方法隐藏软键盘
        imm.hideSoftInputFromWindow(etInput.getWindowToken(), 0); //强制隐藏键盘

    }

    private void showKeyboard() {
        //1.得到InputMethodManager对象
        InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
//2.调用showSoftInput方法显示软键盘，其中view为聚焦的view组件
        imm.showSoftInput(etInput, InputMethodManager.SHOW_FORCED);

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == REQUEST_CODE_CAMERA) { // capture new image
                if (cameraFile != null && cameraFile.exists()){
                    sendImageMessage(cameraFile.getAbsolutePath());
                    refreshToLast();
                }
            } else if (requestCode == REQUEST_CODE_LOCAL) { // send local image
                if (data != null) {
                    Uri selectedImage = data.getData();
                    if (selectedImage != null) {
                        sendPicByUri(selectedImage);
                        refreshToLast();
                    }
                }
            }
        }
    }

    protected void sendImageMessage(String imagePath) {
        EMMessage message = EMMessage.createImageSendMessage(imagePath, false, userId);
        sendMessage(message);
    }

    protected void sendPicByUri(Uri selectedImage) {
        String[] filePathColumn = {MediaStore.Images.Media.DATA};
        Cursor cursor = getActivity().getContentResolver().query(selectedImage, filePathColumn, null, null, null);
        if (cursor != null) {
            cursor.moveToFirst();
            int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
            String picturePath = cursor.getString(columnIndex);
            cursor.close();
            cursor = null;

            if (picturePath == null || picturePath.equals("null")) {
                Toast toast = Toast.makeText(getActivity(), R.string.cant_find_pictures, Toast.LENGTH_SHORT);
                toast.setGravity(Gravity.CENTER, 0, 0);
                toast.show();
                return;
            }
            sendImageMessage(picturePath);
        } else {
            File file = new File(selectedImage.getPath());
            if (!file.exists()) {
                Toast toast = Toast.makeText(getActivity(), R.string.cant_find_pictures, Toast.LENGTH_SHORT);
                toast.setGravity(Gravity.CENTER, 0, 0);
                toast.show();
                return;

            }
            sendImageMessage(file.getAbsolutePath());
        }

    }
}
