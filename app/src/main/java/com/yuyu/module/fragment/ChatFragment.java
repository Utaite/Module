package com.yuyu.module.fragment;

import android.content.Context;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.trello.rxlifecycle.components.RxFragment;
import com.yuyu.module.R;
import com.yuyu.module.activity.MainActivity;
import com.yuyu.module.adapter.ChatAdapter;
import com.yuyu.module.utils.ChatVOK;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class ChatFragment extends RxFragment {

    private final String TAG = ChatFragment.class.getSimpleName();

    private Context context;
    private DatabaseReference databaseReference;

    @BindView(R.id.chat_list)
    RecyclerView chat_list;
    @BindView(R.id.chat_edit)
    EditText chat_edit;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_chat, container, false);
        ButterKnife.bind(this, view);
        context = getActivity();
        databaseReference = FirebaseDatabase.getInstance().getReference();
        initialize();
        return view;
    }

    @Override
    public void onStart() {
        super.onStart();
        ((MainActivity) context).getBottom_tab_bar().setVisibility(View.GONE);
    }

    @Override
    public void onStop() {
        super.onStop();
        ((MainActivity) context).getBottom_tab_bar().setVisibility(View.VISIBLE);
    }

    @Override
    public void onPause() {
        super.onPause();
        InputMethodManager imm = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(chat_edit.getWindowToken(), 0);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    @OnClick(R.id.chat_btn)
    public void onChatButtonClick() {
        String message = chat_edit.getText().toString().trim();
        databaseReference.child(getString(R.string.chat_message)).push().setValue(
                new ChatVOK(((MainActivity) context).getAuthVO().getEmail(), message,
                        ((MainActivity) context).getAuthVO().getPhotoUrl()));
        // TODO FCM
        chat_edit.getText().clear();
    }

    public void initialize() {
        ((MainActivity) context).getToast().setTextShow(getString(R.string.loading));
        LinearLayoutManager llm = new LinearLayoutManager(context);
        llm.setOrientation(LinearLayoutManager.VERTICAL);
        chat_list.setHasFixedSize(true);
        chat_list.setLayoutManager(llm);
        ChatAdapter adapter = new ChatAdapter(context);
        chat_list.setAdapter(adapter);

        databaseReference.child(getString(R.string.chat_message)).addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                ChatVOK vo = dataSnapshot.getValue(ChatVOK.class);
                adapter.add(new ChatVOK(vo.getEmail(), vo.getMessage(), vo.getPhotoUrl()));
                chat_list.smoothScrollToPosition(adapter.getItemCount());
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
