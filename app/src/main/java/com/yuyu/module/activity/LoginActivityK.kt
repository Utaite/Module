package com.yuyu.module.activity

import android.content.Context
import android.widget.Toast
import com.trello.rxlifecycle.components.support.RxAppCompatActivity
import com.yuyu.module.chain.ChainedToast

class LoginActivityK : RxAppCompatActivity() {

    private val TAG = LoginActivityK::class.java.simpleName
    private val context: Context = this
    private val toast: ChainedToast = ChainedToast(this).makeTextTo(this, "", Toast.LENGTH_SHORT)

}
