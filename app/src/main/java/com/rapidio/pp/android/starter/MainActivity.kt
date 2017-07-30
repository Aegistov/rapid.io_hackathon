package com.rapidio.pp.android.starter

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.widget.TextView
import butterknife.BindView
import butterknife.ButterKnife
import com.rapidio.pp.android.starter.network.NetworkService
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import timber.log.Timber
import timber.log.Timber.DebugTree

class MainActivity : AppCompatActivity() {

    @BindView(R.id.pp_text_1) internal var textView1: TextView? = null

    internal var cd = CompositeDisposable()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        Timber.plant(DebugTree())

        setContentView(R.layout.activity_main)
        ButterKnife.bind(this)

        loadData()
    }

    override fun onDestroy() {
        super.onDestroy()
        cd.clear()
    }

    private fun loadData() {
        textView1?.text = "Hello World (local)"

        val disposable = NetworkService().api.hello()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({ response -> Timber.d("result %s", response) }) { throwable -> Timber.e(throwable, "ruh roh, something went wrong!") }

        cd.add(disposable)
    }
}
