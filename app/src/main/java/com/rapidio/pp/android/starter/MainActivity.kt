package com.rapidio.pp.android.starter

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Location
import android.os.Bundle
import android.support.v4.app.ActivityCompat
import android.support.v4.app.FragmentActivity
import android.support.v4.content.ContextCompat
import android.telephony.TelephonyManager
import android.transition.Visibility
import android.util.Log
import android.widget.Button
import butterknife.BindView
import android.view.View
import android.widget.TextView
import android.widget.Toast
import butterknife.ButterKnife
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.maps.CameraUpdateFactory
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

    @BindView(R.id.button6) lateinit var rallyPointButton: Button
    @BindView(R.id.setting_rally_point) lateinit var rallyPointText: TextView
    @BindView(R.id.button5) lateinit var joinParty: Button

    var rxLocation: RxLocation? = null
    var documentId: String = "no_phone"
    var settingRallyPoint = false
    var locationPoints: MutableList<LocationEntity> = mutableListOf()
    var rallyPoints: MutableList<LocationEntity> = mutableListOf()
    var map: GoogleMap? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        Timber.plant(DebugTree())

        setContentView(R.layout.activity_main_test)
        ButterKnife.bind(this)
        requestPermissions()
        val mapFragment = fragmentManager
                .findFragmentById(R.id.map) as MapFragment
        mapFragment.getMapAsync(this)
        joinParty.setOnClickListener { goToPartyActivity() }
        rallyPointButton.setOnClickListener {
            settingRallyPoint = !settingRallyPoint
            rallyPointText.visibility = when (settingRallyPoint) {
                true -> View.VISIBLE
                false -> View.GONE
            }

        }

    }

    override fun onStop() {
        super.onStop()
    }

    override fun onDestroy() {
        super.onDestroy()
    }

    fun addLocation(loc: Location) {
        Rapid.getInstance().collection<LocationEntity>("userLocations", LocationEntity::class.java).document(documentId)
                .mutate(LocationEntity(documentId, loc.latitude, loc.longitude))
                .onSuccess { Log.d("rapidio mutate location", "Success") }
                .onError { error -> error.printStackTrace() }
    }


    fun addRallyPoint(loc: LatLng) {
        if (settingRallyPoint) {
            Rapid.getInstance().collection<LocationEntity>("rallyPoints", LocationEntity::class.java).document(documentId)
                    .mutate(LocationEntity(documentId, loc.latitude, loc.longitude))
                    .onSuccess { Log.d("rapidio rallyPoint", "Success") }
                    .onError { error -> error.printStackTrace() }
            settingRallyPoint = false
            rallyPointText.visibility = View.GONE
        }
    }

    fun subscribeToLocations() {
        Rapid.getInstance().collection("userLocations")
                .subscribe({ test ->
                    Log.d("rapid subscribe", "response gotten")
                    locationPoints.removeAll { true }
                    test.forEach { location ->
                        Log.d("rapid subscribe", "testing location object")
                        Log.d("rapid subscribe", location.body.toString())
                        if (location.body.containsKey("latitude") && location.body.containsKey("longitude")) {
                            Log.d("rapid subscribe", "object is good")
                            locationPoints.add(LocationEntity(location.id,
                                    location.body["latitude"] as Double,
                                    location.body["longitude"] as Double))
                        }
                    }
                    redraw()
                })
    }

    fun subscribeToRallyPoints() {
        Rapid.getInstance().collection("rallyPoints")
                .subscribe({ test ->
                    Log.d("rapid subscribe", "response gotten")
                    rallyPoints.removeAll { true }
                    test.forEach { location ->
                        Log.d("rapid subscribe", "testing location object")
                        Log.d("rapid subscribe", location.body.toString())
                        if (location.body.containsKey("latitude") && location.body.containsKey("longitude")) {
                            Log.d("rapid subscribe", "object is good")
                            rallyPoints.add(LocationEntity(location.id,
                                    location.body["latitude"] as Double,
                                    location.body["longitude"] as Double))
                        }
                    }
                    redraw()
                })
    }

    fun redraw() {
        map?.clear()
        rallyPoints.forEach { location ->
            map?.addMarker(MarkerOptions()
                    .position(LatLng(
                            location.latitude,
                            location.longitude))
                    .title(location.id)
                    .icon(BitmapDescriptorFactory.fromResource(R.drawable.marker)))
        }

        locationPoints.forEach { location ->
            map?.addMarker(MarkerOptions()
                    .position(LatLng(
                            location.latitude,
                            location.longitude))
                    .title(location.id)
                    .icon(BitmapDescriptorFactory.fromResource(R.drawable.check)))
        }
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
        subscribeToLocations()
        subscribeToRallyPoints()
        val locationRequest = LocationRequest.create()
                .setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY)
                .setInterval(10000)

        rxLocation?.let {
            it.location().updates(locationRequest)
                    .subscribe(object : Observer<Location> {
                        override fun onError(error: Throwable) {
                            Timber.d(error)
                        }

                        override fun onComplete() {
                        }

                        override fun onSubscribe(p0: Disposable) {
                        }

                        override fun onNext(loc: Location) {
                            Log.d("locations", "ayyy")
                            addLocation(loc)
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
        map.setOnMapClickListener { addRallyPoint(it) }
        map.moveCamera(CameraUpdateFactory.newLatLngZoom(LatLng(37.7737387,-122.4197341) , 14.0f) )
    }

    fun goToPartyActivity() {
        val startIntent = Intent(this, SyncMember::class.java)
        startActivity(startIntent)
    }

    companion object {
        val MY_PERMISSIONS_REQUEST_CALLBACK = 1234
    }

    fun toastMe(view: View) {
        val myToast = Toast.makeText(this, "Location pushed to party members!", Toast.LENGTH_SHORT)
        myToast.show()
    }
}
