package com.absa.weatherapp.model

import android.os.Parcel
import android.os.Parcelable
import com.google.gson.annotations.SerializedName

data class Coordinate(
    @SerializedName("lon")
    val lon: Float? = null,

    @SerializedName("lat")
    val lat: Float? = null

) : Parcelable {
    constructor(parcel: Parcel) : this(
        parcel.readValue(Float::class.java.classLoader) as? Float,
        parcel.readValue(Float::class.java.classLoader) as? Float
    )

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeValue(lon)
        parcel.writeValue(lat)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<Coordinate> {
        override fun createFromParcel(parcel: Parcel): Coordinate {
            return Coordinate(parcel)
        }

        override fun newArray(size: Int): Array<Coordinate?> {
            return arrayOfNulls(size)
        }
    }
}
