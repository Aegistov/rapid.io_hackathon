package com.rapidio.pp.android.starter

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.pm.PackageManager
import android.location.Location
import android.os.Bundle
import android.support.v4.app.ActivityCompat
import android.support.v4.content.ContextCompat
import android.support.v7.app.AppCompatActivity
import android.telephony.TelephonyManager
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
import io.reactivex.Observer
import io.reactivex.disposables.Disposable

class MainActivity : AppCompatActivity() {

    @BindView(R.id.pp_text_1) lateinit var textView1: TextView
    @BindView(R.id.pp_button_1) lateinit var button1: Button

    var rxLocation: RxLocation? = null
    var collectionName: String = "no_phone"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        Timber.plant(DebugTree())

        setContentView(R.layout.activity_main)
        ButterKnife.bind(this)
        requestPermissions()
    }

    override fun onStop() {
        super.onStop()
    }

    override fun onDestroy() {
        super.onDestroy()
    }

    fun addLocation(loc: Location) {
        val newDocument = Rapid.getInstance().collection(collectionName, LocationEntity::class.java).newDocument()
        newDocument.mutate(LocationEntity(newDocument.id, loc.longitude, loc.latitude))
    }

    fun subscribeToRapid(collection: String) {
        Rapid.getInstance().collection(collection)
                .subscribe({ test ->
                    Log.d("rapidio test", test.toString())
                })
    }

    fun addCollectionNameToRapid() {
        Rapid.getInstance().collection<UserEntity>("users", UserEntity::class.java).document(collectionName)
                .mutate(UserEntity(collectionName))
                .onSuccess { Log.d("rapidio mutate", "Success") }
                .onError { error -> error.printStackTrace() }
    }

    fun requestPermissions() {
        val neededPermissions = arrayOf(Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.READ_PHONE_STATE)
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
        rxLocation = RxLocation(this)
        @SuppressLint("MissingPermission", "HardwareIds")
        collectionName = (getSystemService(Context.TELEPHONY_SERVICE) as TelephonyManager).deviceId ?: "no_phone"
        addCollectionNameToRapid()
        subscribeToRapid(collectionName)
        val locationRequest = LocationRequest.create()
                .setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY)
                .setInterval(10000)

        rxLocation?.let {
            it.location().updates(locationRequest)
                    .subscribe(object : Observer<Location> {
                        override fun onError(p0: Throwable) {
                            Timber.d(p0)
                        }

                        override fun onComplete() {
                        }

                        override fun onSubscribe(p0: Disposable) {
                        }

                        override fun onNext(p0: Location) {
                            Log.d("locations", "ayyy")
                            addLocation(p0)
                        }

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
                    requestPermissions()
                }
                return
            }
        }
    }

    companion object {
        val MY_PERMISSIONS_REQUEST_CALLBACK = 1234
    }
}
