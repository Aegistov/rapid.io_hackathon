package com.rapidio.pp.android.starter

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.util.Log
import android.widget.Button
import android.widget.TextView
import butterknife.BindView
import butterknife.ButterKnife
import io.reactivex.disposables.CompositeDisposable
import timber.log.Timber
import timber.log.Timber.DebugTree
import io.rapid.Rapid

class MainActivity : AppCompatActivity() {

    @BindView(R.id.pp_text_1) lateinit var textView1: TextView
    @BindView(R.id.pp_button_1) lateinit var button1: Button

    internal var cd = CompositeDisposable()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        Timber.plant(DebugTree())

        setContentView(R.layout.activity_main)
        ButterKnife.bind(this)
        subscribeToRapid()
        button1.setOnClickListener { testAddItem() }
    }

    override fun onDestroy() {
        super.onDestroy()
        cd.clear()
    }

    fun testAddItem() {
        val newDocument = Rapid.getInstance().collection("tests", TestEntity::class.java).newDocument()
        newDocument.mutate(TestEntity(newDocument.id, "hello_world_2", 1))
    }

    fun subscribeToRapid() {
        Rapid.getInstance().collection("tests")
                .subscribe({ test ->
                    Log.d("rapidio test", test.toString())
                })
    }

}
