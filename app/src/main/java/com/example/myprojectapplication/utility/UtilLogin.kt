package com.example.myprojectapplication.utility

import android.content.Context
import android.content.Intent
import com.example.myprojectapplication.LogActivity

object utilLogin {

    fun forceLogin(context: Context){
        val intent = Intent(context, LogActivity::class.java)
        context.startActivity(intent)
    }

}