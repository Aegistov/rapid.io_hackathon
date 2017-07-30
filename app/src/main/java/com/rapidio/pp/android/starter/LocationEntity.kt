package com.rapidio.pp.android.starter

import android.databinding.BaseObservable
import com.google.gson.annotations.SerializedName

class LocationEntity(
        @SerializedName("id") var id: String,
        @SerializedName("longitute") var Longitute: Double,
        @SerializedName("latitude") var Latitude: Double
) : BaseObservable()