package com.example.mymessenger.views

import android.app.AlertDialog
import android.app.Dialog
import android.content.Context
import android.os.Bundle
import android.support.v4.app.DialogFragment
import com.example.mymessenger.R
import kotlinx.android.synthetic.main.dialog_password.view.*

class PasswordDialog : DialogFragment(){
    private lateinit var mListiner: Listener
    interface Listener{
        fun onPasswordConfirm(password: String)
    }

    override fun onAttach(context: Context?) {
        super.onAttach(context)
        mListiner = context as Listener
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val view = activity!!.layoutInflater.inflate(R.layout.dialog_password, null)
        return AlertDialog.Builder(context!!)
            .setView(view)
            .setPositiveButton(android.R.string.ok,{_,_->
                mListiner.onPasswordConfirm(view.password_input.text.toString())//передаём пароль активности
            })
            .setNegativeButton(android.R.string.cancel, {_,_->

            })
            .setTitle(R.string.please_enter_password).create()
    }
}