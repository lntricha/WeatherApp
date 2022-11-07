package com.absa.weatherapp.model

import android.os.Parcel
import android.os.Parcelable
import com.google.gson.annotations.SerializedName

data class Temp(
    @SerializedName("temp")
    val temp: Float? = null,

    @SerializedName("feels_like")
    val feels_like: Float? = null,

    @SerializedName("temp_min")
    val temp_min: Float? = null,

    @SerializedName("temp_max")
    val temp_max: Float? = null,

    @SerializedName("humidity")
    val humidity: Int? = null,
) : Parcelable {
    constructor(parcel: Parcel) : this(
        parcel.readValue(Float::class.java.classLoader) as? Float,
        parcel.readValue(Float::class.java.classLoader) as? Float,
        parcel.readValue(Float::class.java.classLoader) as? Float,
        parcel.readValue(Float::class.java.classLoader) as? Float,
        parcel.readValue(Int::class.java.classLoader) as? Int
    )

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeValue(temp)
        parcel.writeValue(feels_like)
        parcel.writeValue(temp_min)
        parcel.writeValue(temp_max)
        parcel.writeValue(humidity)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<Temp> {
        override fun createFromParcel(parcel: Parcel): Temp {
            return Temp(parcel)
        }

        override fun newArray(size: Int): Array<Temp?> {
            return arrayOfNulls(size)
        }
    }
}
