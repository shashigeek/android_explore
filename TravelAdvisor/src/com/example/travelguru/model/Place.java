package com.example.travelguru.model;

import android.os.Parcel;
import android.os.Parcelable;

public class Place implements Parcelable {
    public String vicinity;
    public float[] geometry;
    public String name;
    public String reference;

    public Place() {

    }

    public Place(Parcel in) {

        this.vicinity = in.readString();
        this.geometry = in.createFloatArray();
        this.name = in.readString();
        this.reference = in.readString();
    }

    public String getVicinity() {
        return vicinity;
    }

    public void setVicinity(String vicinity) {
        this.vicinity = vicinity;
    }

    public float[] getGeometry() {
        return geometry;
    }

    public void setGeometry(float[] geometry) {
        this.geometry = geometry;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getReference() {
        return reference;
    }

    public void setReference(String reference) {
        this.reference = reference;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {

        dest.writeString(vicinity);
        dest.writeFloatArray(geometry);
        dest.writeString(name);
        dest.writeString(reference);
    }

    public static final Parcelable.Creator<Place> CREATOR = new Parcelable.Creator<Place>() {
        public Place createFromParcel(Parcel in) {
            return new Place(in);
        }

        public Place[] newArray(int size) {
            return new Place[size];
        }
    };

}
