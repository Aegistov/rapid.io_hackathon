package com.rapidio.pp.android.starter

import android.databinding.BaseObservable
import com.google.gson.annotations.SerializedName


class UserEntity(
        @SerializedName("id") var id: String
) : BaseObservable()