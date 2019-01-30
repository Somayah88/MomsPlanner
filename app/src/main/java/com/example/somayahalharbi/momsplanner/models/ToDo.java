package com.example.somayahalharbi.momsplanner.models;

import android.os.Parcel;
import android.os.Parcelable;

public class ToDo implements Parcelable {
    public static final Parcelable.Creator<ToDo> CREATOR = new Parcelable.Creator<ToDo>() {

        @Override
        public ToDo createFromParcel(Parcel in) {
            return new ToDo(in);
        }

        @Override
        public ToDo[] newArray(int size) {
            return new ToDo[size];
        }
    };
    private String toDo;
    private String owner;
    private int priority; // should be 1, 2, 3: 3 is highest
    private boolean checked;
    private String dueBy;
    private String ownerId;
    private String taskId;

    public ToDo() {

    }

    private ToDo(Parcel in) {
        this.toDo = in.readString();
        this.owner = in.readString();
        this.priority = in.readInt();
        this.checked = in.readByte() != 0;
        this.dueBy = in.readString();
        this.ownerId = in.readString();
        this.taskId = in.readString();

    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int i) {
        dest.writeString(toDo);
        dest.writeString(owner);
        dest.writeInt(priority);
        dest.writeByte((byte) (checked ? 1 : 0));
        dest.writeString(dueBy);
        dest.writeString(ownerId);
        dest.writeString(taskId);


    }

    //Getters & Setters

    public String getToDo() {
        return toDo;
    }

    public void setToDo(String toDo) {
        this.toDo = toDo;
    }

    public String getOwner() {
        return owner;
    }

    public void setOwner(String owner) {
        this.owner = owner;
    }

    public int getPriority() {
        return priority;
    }

    public void setPriority(int priority) {
        this.priority = priority;
    }

    public boolean isChecked() {
        return checked;
    }

    public void setChecked(boolean checked) {
        this.checked = checked;
    }

    public String getDueBy() {
        return dueBy;
    }

    public void setDueBy(String dueBy) {
        this.dueBy = dueBy;
    }

    public String getOwnerId() {
        return ownerId;
    }

    public void setOwnerId(String ownerId) {
        this.ownerId = ownerId;
    }

    public String getTaskId() {
        return taskId;
    }

    public void setTaskId(String taskId) {
        this.taskId = taskId;
    }



}
