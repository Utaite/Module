package com.yuyu.module.fragment;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;

import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.OptionalPendingResult;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.trello.rxlifecycle.components.RxFragment;
import com.yuyu.module.R;
import com.yuyu.module.activity.MainActivity;
import com.yuyu.module.utils.ChatVO;
import com.yuyu.module.utils.Constant;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

import static android.app.Activity.RESULT_OK;


public class ChatFragment extends RxFragment {

    private final String TAG = ChatFragment.class.getSimpleName();

    private Context context;
    private FirebaseAuth firebaseAuth;
    private FirebaseAuth.AuthStateListener authStateListener;
    private GoogleApiClient googleApiClient;

    @BindView(R.id.chat_list)
    ListView chat_list;
    @BindView(R.id.chat_edit)
    EditText chat_edit;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_chat, container, false);
        ButterKnife.bind(this, view);
        context = getActivity();
        firebaseAuth = FirebaseAuth.getInstance();
        authStateListener = auth -> {
            FirebaseUser user = auth.getCurrentUser();
            if (user == null) {
                Intent intent = Auth.GoogleSignInApi.getSignInIntent(googleApiClient);
                startActivityForResult(intent, Constant.AUTH_REQUEST_CODE);
            } else {
                Log.e("DisplayName", user.getDisplayName());
                Log.e("Email", user.getEmail());
                Log.e("Uid", user.getUid());
            }
        };
        googleApiClient = buildGoogleApiClient();
        return view;
    }

    public GoogleApiClient buildGoogleApiClient() {
        return new GoogleApiClient.Builder(context)
                .enableAutoManage((MainActivity) context, connectionResult -> {
                })
                .addApi(Auth.GOOGLE_SIGN_IN_API, new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                        .requestIdToken(getString(R.string.default_web_client_id))
                        .requestEmail()
                        .build())
                .build();
    }

    @Override
    public void onStart() {
        super.onStart();
        ((MainActivity) context).getBottom_tab_bar().setVisibility(View.GONE);
        firebaseAuth.addAuthStateListener(authStateListener);
        OptionalPendingResult<GoogleSignInResult> opr = Auth.GoogleSignInApi.silentSignIn(googleApiClient);
        if (opr.isDone()) {
            handleSignInResult(opr.get());
        } else {
            opr.setResultCallback(this::handleSignInResult);
        }
    }

    @Override
    public void onStop() {
        super.onStop();
        ((MainActivity) context).getBottom_tab_bar().setVisibility(View.VISIBLE);
        if (authStateListener != null) {
            firebaseAuth.removeAuthStateListener(authStateListener);
        }
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
        googleApiClient.stopAutoManage((MainActivity) context);
        googleApiClient.disconnect();
    }

    @OnClick(R.id.chat_btn)
    public void onChatButtonClick() {
        FirebaseDatabase.getInstance().getReference().child(getString(R.string.chat_message)).push().setValue(
                new ChatVO(((MainActivity) context).getMainParcel().getId(), chat_edit.getText().toString().trim()));
        chat_edit.getText().clear();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == Constant.AUTH_REQUEST_CODE) {
            if (resultCode != RESULT_OK) {
                ((MainActivity) context).getToast().setTextShow(getString(R.string.chat_auth_err));
            } else {
                GoogleSignInResult result = Auth.GoogleSignInApi.getSignInResultFromIntent(data);
                handleSignInResult(result);
            }
        }
    }

    public void handleSignInResult(GoogleSignInResult result) {
        if (result.isSuccess()) {
            ((MainActivity) context).getToast().setTextShow(getString(R.string.loading));
            GoogleSignInAccount account = result.getSignInAccount();
            AuthCredential credential = GoogleAuthProvider.getCredential(account.getIdToken(), null);
            firebaseAuth.signInWithCredential(credential)
                    .addOnCompleteListener((MainActivity) context, task -> {
                        initialize();
                        ((MainActivity) context).getToast().setTextShow(getString(R.string.chat_auth_load));
                        Log.e("Id", account.getId());
                        Log.e("IdToken", account.getIdToken());
                    });
        }
    }

    public void initialize() {
        ArrayAdapter adapter = new ArrayAdapter<>(context, android.R.layout.simple_list_item_1, android.R.id.text1);
        chat_list.setAdapter(adapter);

        FirebaseDatabase.getInstance().getReference().child(getString(R.string.chat_message)).addChildEventListener(new ChildEventListener() {
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
