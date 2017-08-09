package com.journeytech.mark.mark;

/**
 * Created by rtyJa on 09/08/2017.
 */

import android.os.Parcel;
import android.os.Parcelable;

public class Marker implements Parcelable {

    private String name;
    private String color;

    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }
    public String getColor() {
        return color;
    }
    public void setColor(String color) {
        this.color = color;
    }

    public static final Parcelable.Creator<Marker> CREATOR = new Creator<Marker>() {
        public Marker createFromParcel(Parcel source) {
            Marker mMarker = new Marker();
            mMarker.name = source.readString();
            mMarker.color = source.readString();
            return mMarker;
        }

        public Marker[] newArray(int size) {
            return new Marker[size];
        }
    };

    public int describeContents() {
        return 0;
    }
    public void writeToParcel(Parcel parcel, int flags) {
        parcel.writeString(name);
        parcel.writeString(color);
    }
}
