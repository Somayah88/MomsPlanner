package com.example.somayahalharbi.momsplanner.models;

import android.os.Parcel;
import android.os.Parcelable;


public class Appointment implements Parcelable {
    public static final Parcelable.Creator<Appointment> CREATOR
            = new Parcelable.Creator<Appointment>() {
        public Appointment createFromParcel(Parcel in) {
            return new Appointment(in);
        }

        public Appointment[] newArray(int size) {
            return new Appointment[size];
        }
    };
    private String apptTitle;
    private String apptDate;
    private String apptTime;
    private String apptLocation;
    private String apptOwner;

    public Appointment(Parcel in) {
        this.apptTitle = in.readString();
        this.apptDate = in.readString();
        this.apptTime = in.readString();
        this.apptLocation = in.readString();
        this.apptOwner = in.readString();

    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int i) {
        dest.writeString(apptTitle);
        dest.writeString(apptDate);
        dest.writeString(apptTime);
        dest.writeString(apptLocation);
        dest.writeString(apptOwner);


    }

    // Getters $ Setters

    public String getApptTitle() {
        return apptTitle;
    }

    public void setApptTitle(String apptTitle) {
        this.apptTitle = apptTitle;
    }

    public String getApptDate() {
        return apptDate;
    }

    public void setApptDate(String apptDate) {
        this.apptDate = apptDate;
    }

    public String getApptTime() {
        return apptTime;
    }

    public void setApptTime(String apptTime) {
        this.apptTime = apptTime;
    }

    public String getApptLocation() {
        return apptLocation;
    }

    public void setApptLocation(String apptLocation) {
        this.apptLocation = apptLocation;
    }

    public String getApptOwner() {
        return apptOwner;
    }

    public void setApptOwner(String apptOwner) {
        this.apptOwner = apptOwner;
    }
}
