package com.absa.weatherapp.model

import android.os.Parcel
import android.os.Parcelable
import com.google.gson.annotations.SerializedName

data class LocationData(
    @SerializedName("lon")
    val lon: Float? = null,

    @SerializedName("lat")
    val lat: Float? = null,

    @SerializedName("name")
    val name: String? = null,

    @SerializedName("country")
    val country: String? = null,

    @SerializedName("state")
    val state: String? = null

) : Parcelable {
    constructor(parcel: Parcel) : this(
        parcel.readValue(Float::class.java.classLoader) as? Float,
        parcel.readValue(Float::class.java.classLoader) as? Float,
        parcel.readString(),
        parcel.readString(),
        parcel.readString()
    )

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeValue(lon)
        parcel.writeValue(lat)
        parcel.writeString(name)
        parcel.writeString(country)
        parcel.writeString(state)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<LocationData> {
        override fun createFromParcel(parcel: Parcel): LocationData {
            return LocationData(parcel)
        }

        override fun newArray(size: Int): Array<LocationData?> {
            return arrayOfNulls(size)
        }
    }
}
