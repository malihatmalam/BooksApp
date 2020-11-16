package com.example.booksapp;

import android.os.Parcel;
import android.os.Parcelable;

public class itemData implements Parcelable {
    public String itemTitle;
    public String itemAuthor;
    public String itemDescription;
    public String itemImage;

    protected itemData(Parcel in){
        itemTitle = in.readString();
        itemAuthor = in.readString();
        itemImage = in.readString();
        itemDescription = in.readString();
    }

    public static final Creator<itemData> CREATOR = new Creator<itemData>() {
        @Override
        public itemData createFromParcel(Parcel in) {
            return new itemData(in);
        }

        @Override
        public itemData[] newArray(int size) {
            return new itemData[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(itemTitle);
        dest.writeString(itemAuthor);
        dest.writeString(itemDescription);
        dest.writeString(itemImage);
    }
}
