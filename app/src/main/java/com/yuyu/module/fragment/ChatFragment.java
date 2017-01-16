package com.yuyu.module.fragment;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.trello.rxlifecycle.components.RxFragment;
import com.yuyu.module.R;
import com.yuyu.module.activity.MainActivity;
import com.yuyu.module.utils.ChatVO;

import java.util.Random;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;


public class ChatFragment extends RxFragment {

    private final String TAG = ChatFragment.class.getSimpleName();

    private Context context;
    private String userName;

    private FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
    private DatabaseReference databaseReference = firebaseDatabase.getReference();

    @BindView(R.id.chat_list)
    ListView chat_list;
    @BindView(R.id.chat_edit)
    EditText chat_edit;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_chat, container, false);
        ButterKnife.bind(this, view);
        context = getActivity();
        initialize();
        return view;
    }

    @OnClick(R.id.chat_btn)
    public void onChatButtonClick() {
        ChatVO vo = new ChatVO(userName, chat_edit.getText().toString().trim());
        databaseReference.child("message").push().setValue(vo);
        chat_edit.setText("");
    }

    @Override
    public void onResume() {
        super.onResume();
        ((MainActivity) context).getBottom_tab_bar().setVisibility(View.GONE);
    }

    @Override
    public void onPause() {
        super.onPause();
        ((MainActivity) context).getBottom_tab_bar().setVisibility(View.VISIBLE);
        InputMethodManager imm = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(chat_edit.getWindowToken(), 0);
    }

    public void initialize() {
        userName = "user" + new Random().nextInt(10000);
        ArrayAdapter adapter = new ArrayAdapter<>(context, android.R.layout.simple_list_item_1, android.R.id.text1);
        chat_list.setAdapter(adapter);

        databaseReference.child("message").addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                ChatVO vo = dataSnapshot.getValue(ChatVO.class);
                adapter.add(vo.getUserName() + ": " + vo.getMessage());
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

}
