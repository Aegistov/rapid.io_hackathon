package com.rapidio.pp.android.starter

import android.support.v7.app.AppCompatActivity
import android.os.Bundle

class JoinParty : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_join_party)
    }

    fun updatePartyCollection() {
        //function that mutates document in collection.
        // Matches ID in document then adds individual's USERNAME to document array
    }
}
