package com.rapidio.pp.android.starter

import android.databinding.BaseObservable
import com.google.gson.annotations.SerializedName

class LocationEntity(
        @SerializedName("id") var id: String,
        @SerializedName("latitude") var latitude: Double,
        @SerializedName("longitude") var longitude: Double
) : BaseObservable()