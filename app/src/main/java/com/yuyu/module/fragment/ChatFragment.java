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
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.OptionalPendingResult;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.trello.rxlifecycle.components.RxFragment;
import com.yuyu.module.R;
import com.yuyu.module.activity.MainActivity;
import com.yuyu.module.utils.ChatVO;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;


public class ChatFragment extends RxFragment {

    private final String TAG = ChatFragment.class.getSimpleName();

    private final int AUTH_REQUEST_CODE = 9999;

    private Context context;
    private FirebaseAuth firebaseAuth;
    private FirebaseAuth.AuthStateListener authStateListener;
    private GoogleApiClient googleApiClient;
    private DatabaseReference databaseReference;

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
            if (auth.getCurrentUser() == null) {
                Intent intent = Auth.GoogleSignInApi.getSignInIntent(googleApiClient);
                startActivityForResult(intent, AUTH_REQUEST_CODE);
            }
        };
        databaseReference = FirebaseDatabase.getInstance().getReference();
        googleApiClient = buildGoogleApiClient();
        initialize();
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
        ChatVO vo = new ChatVO(((MainActivity) context).getMainParcel().getId(), chat_edit.getText().toString().trim());
        chat_edit.getText().clear();
        databaseReference.child(getString(R.string.chat_message)).push().setValue(vo);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == AUTH_REQUEST_CODE) {
            GoogleSignInResult result = Auth.GoogleSignInApi.getSignInResultFromIntent(data);
            handleSignInResult(result);
        }
    }

    public void handleSignInResult(GoogleSignInResult result) {
        if (result.isSuccess()) {
            GoogleSignInAccount account = result.getSignInAccount();
            AuthCredential credential = GoogleAuthProvider.getCredential(account.getIdToken(), null);
            firebaseAuth.signInWithCredential(credential)
                    .addOnCompleteListener((MainActivity) context, task -> {
                        ((MainActivity) context).getToast().setTextShow(getString(R.string.chat_auth_success));
                        Log.e("getDisplayName", account.getDisplayName());
                        Log.e("getEmail", account.getEmail());
                        Log.e("getPhotoUrl", String.valueOf(account.getPhotoUrl()));
                        Log.e("getId", account.getId());
                        Log.e("getIdToken", account.getIdToken());
                        Log.e("getFamilyName", account.getFamilyName());
                        Log.e("getGivenName", account.getGivenName());
                    });
        }
    }

    public void initialize() {
        ArrayAdapter adapter = new ArrayAdapter<>(context, android.R.layout.simple_list_item_1, android.R.id.text1);
        chat_list.setAdapter(adapter);

        databaseReference.child(getString(R.string.chat_message)).addChildEventListener(new ChildEventListener() {
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
