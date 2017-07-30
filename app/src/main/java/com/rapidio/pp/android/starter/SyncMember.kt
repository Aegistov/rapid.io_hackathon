package com.rapidio.pp.android.starter

import android.content.Intent
import android.os.Bundle
import android.support.design.widget.BottomNavigationView
import android.support.v7.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_sync_member.*

class SyncMember : AppCompatActivity() {

    private val mOnNavigationItemSelectedListener = BottomNavigationView.OnNavigationItemSelectedListener { item ->
        when (item.itemId) {
            R.id.navigation_create -> {
//                message.setText(R.string.create_party)
                transitionToCreate()
                return@OnNavigationItemSelectedListener true
            }
            R.id.navigation_ready -> {
//                message.setText(R.string.ready_up)
                transitionToMain()
                return@OnNavigationItemSelectedListener true
            }
            R.id.navigation_join -> {
//                message.setText(R.string.join_party)
                transitionToJoin()
                return@OnNavigationItemSelectedListener true
            }
        }
        false
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sync_member)

        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener)
    }

    fun transitionToMain() {
        val startIntent = Intent(this, MainActivity::class.java)
        startActivity(startIntent)
    }

    fun transitionToJoin(){
        val startIntent = Intent(this, JoinParty::class.java)
        startActivity(startIntent)
    }

    fun transitionToCreate(){
        val startIntent = Intent(this, CreateParty::class.java)
        startActivity(startIntent)
    }
}
