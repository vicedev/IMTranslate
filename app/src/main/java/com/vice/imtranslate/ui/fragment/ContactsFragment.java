package com.vice.imtranslate.ui.fragment;


import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

import com.hyphenate.chat.EMClient;
import com.hyphenate.chat.EMConversation;
import com.hyphenate.chat.EMMessage;
import com.hyphenate.exceptions.HyphenateException;
import com.vice.imtranslate.R;
import com.vice.imtranslate.adapter.FragmentContactsListAdapter;
import com.vice.imtranslate.bean.User;
import com.vice.imtranslate.global.Constants;
import com.vice.imtranslate.ui.activity.BaseActivity;
import com.vice.imtranslate.ui.activity.ChatActivity;
import com.vice.imtranslate.ui.activity.MainActivity;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;

/**
 * A simple {@link Fragment} subclass.
 */
public class ContactsFragment extends Fragment {


    private ListView lvContacts;
    private List<User> userList;

    public ContactsFragment() {
        // Required empty public constructor
        userList=new ArrayList<>();
        String currentUser = EMClient.getInstance().getCurrentUser();
        // TODO 写死了联系人,需要从自己服务器获取
        if (currentUser.equals("test1")){
            userList.add(new User("test2","test2","http://a.hiphotos.baidu.com/zhidao/pic/item/91529822720e0cf3ef6c79630c46f21fbf09aa8a.jpg"));
            userList.add(new User("test3","test3","http://img4.imgtn.bdimg.com/it/u=3257207530,468293946&fm=206&gp=0.jpg"));
        }else if (currentUser.equals("test2")){
            userList.add(new User("test1","test1","http://img5.duitang.com/uploads/item/201508/26/20150826195235_xzB8K.thumb.700_0.jpeg"));
            userList.add(new User("test3","test3","http://img4.imgtn.bdimg.com/it/u=3257207530,468293946&fm=206&gp=0.jpg"));
        }else if (currentUser.equals("test3")){
            userList.add(new User("test1","test1","http://img5.duitang.com/uploads/item/201508/26/20150826195235_xzB8K.thumb.700_0.jpeg"));
            userList.add(new User("test2","test2","http://a.hiphotos.baidu.com/zhidao/pic/item/91529822720e0cf3ef6c79630c46f21fbf09aa8a.jpg"));
        }

    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View layout=inflater.inflate(R.layout.fragment_contacts, container, false);
        lvContacts = (ListView) layout.findViewById(R.id.lv_contacts);

        FragmentContactsListAdapter adapter=new FragmentContactsListAdapter(getActivity(),userList);
        lvContacts.setAdapter(adapter);
        lvContacts.setOnItemClickListener(new MyItemClickListener());

        return layout;
    }

    class MyItemClickListener implements AdapterView.OnItemClickListener{

        @Override
        public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
            if (userList!=null){
                User user=userList.get(i);
                Intent intent=new Intent(getActivity(), ChatActivity.class);
                if (TextUtils.isEmpty(user.getUserId())){
                    return;
                }
                intent.putExtra(Constants.USER_ID,user.getUserId());
                getActivity().startActivity(intent);
                getActivity().overridePendingTransition(R.anim.activity_in_from_right,R.anim.activity_out_to_left);
            }
        }
    }



}
