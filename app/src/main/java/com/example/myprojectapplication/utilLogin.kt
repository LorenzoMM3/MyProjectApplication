package com.example.myprojectapplication

import android.content.Context
import android.content.Intent

object utilLogin {

    fun forceLogin(context: Context){
        val intent = Intent(context, LogActivity::class.java)
        context.startActivity(intent)
    }

}