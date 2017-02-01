package com.yuyu.module.fragment;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

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
import com.trello.rxlifecycle.components.RxFragment;
import com.yuyu.module.R;
import com.yuyu.module.activity.MainActivity;
import com.yuyu.module.utils.Constant;

import butterknife.ButterKnife;

import static android.app.Activity.RESULT_OK;

public class AuthFragment extends RxFragment {

    private final String TAG = AuthFragment.class.getSimpleName();

    private Context context;
    private FirebaseAuth firebaseAuth;
    private FirebaseAuth.AuthStateListener authStateListener;
    private GoogleApiClient googleApiClient;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_main, container, false);
        ButterKnife.bind(this, view);
        context = getActivity();
        initialize();
        return view;
    }

    public void initialize() {
        googleApiClient = buildGoogleApiClient();
        firebaseAuth = FirebaseAuth.getInstance();
        authStateListener = auth -> {
            FirebaseUser user = auth.getCurrentUser();
            if (user == null) {
                Intent intent = Auth.GoogleSignInApi.getSignInIntent(googleApiClient);
                startActivityForResult(intent, Constant.AUTH_REQUEST_CODE);
            } else {
                ((MainActivity) context).getAuthVO()
                        .setDisplayName(user.getDisplayName());
                ((MainActivity) context).getAuthVO()
                        .setEmail(user.getEmail());
                ((MainActivity) context).getAuthVO()
                        .setUid(user.getUid());
                ((MainActivity) context).getAuthVO()
                        .setPhotoUrl(String.valueOf(user.getPhotoUrl()));
            }
        };
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
        if (authStateListener != null) {
            firebaseAuth.removeAuthStateListener(authStateListener);
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        googleApiClient.stopAutoManage((MainActivity) context);
        googleApiClient.disconnect();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == Constant.AUTH_REQUEST_CODE) {
            if (resultCode != RESULT_OK) {
                ((MainActivity) context).getToast().setTextShow(getString(R.string.auth_err));
//                ((MainActivity) context).finish();
            } else {
                handleSignInResult(Auth.GoogleSignInApi.getSignInResultFromIntent(data));
            }
        }
    }

    public void handleSignInResult(GoogleSignInResult result) {
        if (result.isSuccess()) {
            GoogleSignInAccount account = result.getSignInAccount();
            AuthCredential credential = GoogleAuthProvider.getCredential(account.getIdToken(), null);
            firebaseAuth.signInWithCredential(credential)
                    .addOnCompleteListener((MainActivity) context, task -> {
                        ((MainActivity) context).getAuthVO()
                                .setId(account.getId());
                        ((MainActivity) context).getAuthVO()
                                .setIdToken(account.getIdToken());
                    });
        }
    }

}
