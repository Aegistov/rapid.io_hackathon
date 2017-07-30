package com.rapidio.pp.android.starter

import android.databinding.BaseObservable
import android.databinding.Bindable
import com.google.gson.annotations.SerializedName


class TestEntity(
        @SerializedName("id") var id: String,
        @SerializedName("name") var name: String,
        clicked: Int) : BaseObservable() {


    @SerializedName("clicked")
    private var mClicked: Int = clicked


    var clicked: Int
        @Bindable
        get() = mClicked
        set(newValue) {
            mClicked = newValue
            notifyPropertyChanged(clicked)
        }
}