package com.rapidio.pp.android.starter

import android.content.Intent
import android.os.Bundle
import android.support.design.widget.BottomNavigationView
import android.support.v7.app.AppCompatActivity
import android.widget.Toast
import kotlinx.android.synthetic.main.activity_sync_member.*

class SyncMember : AppCompatActivity() {

    private val mOnNavigationItemSelectedListener = BottomNavigationView.OnNavigationItemSelectedListener { item ->
        when (item.itemId) {
            R.id.navigation_create -> {
                val myToast = Toast.makeText(this, "Party Created!", Toast.LENGTH_SHORT)
                myToast.show()
                return@OnNavigationItemSelectedListener true
            }
            R.id.navigation_ready -> {
//                message.setText(R.string.ready_up)
                transitionToMain()
                return@OnNavigationItemSelectedListener true
            }
            R.id.navigation_join -> {
//                message.setText(R.string.join_party)
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
}
