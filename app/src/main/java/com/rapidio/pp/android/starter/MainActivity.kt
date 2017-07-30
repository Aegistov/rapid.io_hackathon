package com.rapidio.pp.android.starter

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.pm.PackageManager
import android.location.Location
import android.os.Bundle
import android.support.v4.app.ActivityCompat
import android.support.v4.app.FragmentActivity
import android.support.v4.content.ContextCompat
import android.telephony.TelephonyManager
import android.util.Log
import butterknife.ButterKnife
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.MapFragment
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.patloew.rxlocation.RxLocation
import timber.log.Timber
import timber.log.Timber.DebugTree
import io.rapid.Rapid
import io.reactivex.Observer
import io.reactivex.disposables.Disposable

class MainActivity : FragmentActivity(), OnMapReadyCallback {

    var rxLocation: RxLocation? = null
    var documentId: String = "no_phone"
    lateinit var map: GoogleMap

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        Timber.plant(DebugTree())

        setContentView(R.layout.activity_main_test)
        ButterKnife.bind(this)
        requestPermissions()
        val mapFragment = fragmentManager
                .findFragmentById(R.id.map) as MapFragment
        mapFragment.getMapAsync(this)
    }

    override fun onStop() {
        super.onStop()
    }

    override fun onDestroy() {
        super.onDestroy()
    }

    fun addLocation(loc: Location) {
        Rapid.getInstance().collection<LocationEntity>("userLocations", LocationEntity::class.java).document(documentId)
                .mutate(LocationEntity(documentId, loc.longitude, loc.latitude))
                .onSuccess { Log.d("rapidio mutate location", "Success") }
                .onError { error -> error.printStackTrace() }
    }

    fun subscribeToRapid() {
        Rapid.getInstance().collection("userLocations")
                .subscribe({ test ->
                    Log.d("rapid subscribe", "response gotten")
                    test.forEach { location ->
                        Log.d("rapid subscribe", "testing location object")
                        Log.d("rapid subscribe", location.body.toString())
                        if (location.body.containsKey("latitude") && location.body.containsKey("longitude")) {
                            Log.d("rapid subscribe", "object is good")
                            map.addMarker(MarkerOptions()
                                    .position(LatLng(
                                            location.body["latitude"] as Double,
                                            location.body["longitude"] as Double))
                                    .title(location.id)
                                    .icon(BitmapDescriptorFactory.fromResource(R.drawable.check)))
                        }
                    }
                })
    }

    fun addCollectionNameToRapid() {
        Rapid.getInstance().collection<UserEntity>("users", UserEntity::class.java).document(documentId)
                .mutate(UserEntity(documentId))
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
        @SuppressLint("HardwareIds")
        documentId = (getSystemService(Context.TELEPHONY_SERVICE) as TelephonyManager).deviceId ?: "no_phone"
        addCollectionNameToRapid()
        subscribeToRapid()
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

    override fun onMapReady(map: GoogleMap) {
        this.map = map
    }

    companion object {
        val MY_PERMISSIONS_REQUEST_CALLBACK = 1234
    }
}
