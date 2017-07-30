package com.rapidio.pp.android.starter

import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.view.View

class SyncMain : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sync_main)
    }

    fun mapScreen(view: View) {
        val mapIntent = Intent(this, MainActivity::class.java)
        startActivity(mapIntent)
    }

}
