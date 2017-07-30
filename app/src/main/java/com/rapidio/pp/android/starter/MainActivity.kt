package com.rapidio.pp.android.starter

import android.Manifest
import android.annotation.SuppressLint
import android.content.pm.PackageManager
import android.os.Bundle
import android.support.v4.app.ActivityCompat
import android.support.v4.content.ContextCompat
import android.support.v7.app.AppCompatActivity
import android.util.Log
import android.widget.Button
import android.widget.TextView
import butterknife.BindView
import butterknife.ButterKnife
import com.google.android.gms.location.LocationRequest
import com.patloew.rxlocation.RxLocation
import timber.log.Timber
import timber.log.Timber.DebugTree
import io.rapid.Rapid

class MainActivity : AppCompatActivity() {

    @BindView(R.id.pp_text_1) lateinit var textView1: TextView
    @BindView(R.id.pp_button_1) lateinit var button1: Button

    var rxLocation: RxLocation? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        Timber.plant(DebugTree())

        setContentView(R.layout.activity_main)
        ButterKnife.bind(this)
        subscribeToRapid()
        button1.setOnClickListener { testAddItem() }
        rxLocation = RxLocation(this)
        requestLocationPermissions()
    }

    override fun onStop() {
        super.onStop()
    }

    override fun onDestroy() {
        super.onDestroy()
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

    fun requestLocationPermissions() {
        val neededPermissions = arrayOf(Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION)
        var hasAllPermissions = true
        neededPermissions.forEach { permission ->
            if (ContextCompat.checkSelfPermission(this, permission) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this,
                        neededPermissions,
                        MY_PERMISSIONS_REQUEST_CALLBACK)
                hasAllPermissions = false
            }
        }
        if (hasAllPermissions) {
            onLocationPermissionGranted()
        }
    }

    @SuppressLint("MissingPermission")
    private fun onLocationPermissionGranted() {
        val locationRequest = LocationRequest.create()
                .setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY)
                .setInterval(1000)

        rxLocation?.let {
            it.location().updates(locationRequest)
                    .flatMap { location -> it.geocoding().fromLocation(location).toObservable() }
                    .subscribe({
                        Log.d("locations", "ayyy")
                    })
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int,
                                            permissions: Array<String>, grantResults: IntArray) {
        when (requestCode) {
            MY_PERMISSIONS_REQUEST_CALLBACK -> {
                if (grantResults.isNotEmpty()) {
                    onLocationPermissionGranted()
                } else {
                    requestLocationPermissions()
                }
                return
            }
        }
    }

    companion object {
        val MY_PERMISSIONS_REQUEST_CALLBACK = 1234
    }
}
