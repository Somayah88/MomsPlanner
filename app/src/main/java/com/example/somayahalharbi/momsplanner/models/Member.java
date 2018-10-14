package com.example.somayahalharbi.momsplanner.models;

import android.os.Parcel;
import android.os.Parcelable;

public class Member implements Parcelable {
    private String name;
    //private String relationship;
    private String DOB;
    private String id;

    public static final Parcelable.Creator<Member> CREATOR = new Parcelable.Creator<Member>() {
        @Override
        public Member createFromParcel(Parcel in) {
            return new Member(in);
        }

        @Override
        public Member[] newArray(int size) {
            return new Member[size];
        }
    };

    public Member() {

    }

    private Member(Parcel in) {
        this.name = in.readString();
        // this.relationship = in.readString();
        this.DOB = in.readString();
        this.id = in.readString();
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int i) {
        dest.writeString(name);
        // dest.writeString(relationship);
        dest.writeString(DOB);
        dest.writeString(id);
    }
    // Getters & Setters

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    // public String getRelationship() {return relationship;}

    //public void setRelationship(String relationship) {this.relationship = relationship;}

    public String getDOB() {
        return DOB;
    }

    public void setDOB(String DOB) {
        this.DOB = DOB;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }


}
