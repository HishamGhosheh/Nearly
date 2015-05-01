package me.ghoscher.nearly.model;

import android.os.Parcel;
import android.os.Parcelable;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Created by Hisham on 30/4/2015.
 */
public class Photo implements Parcelable {

    /**
     * Parses a JSONArray into Photo array
     * @param arr JSONArray to parse
     * @return Photo array
     */
    public static ArrayList<Photo> parseArray(JSONArray arr) {
        ArrayList<Photo> result = new ArrayList<>();

        if (arr != null) {
            for (int i = 0; i < arr.length(); i++) {
                JSONObject obj = arr.optJSONObject(i);
                Photo photo = parsePhoto(obj);

                if (photo != null)
                    result.add(photo);
            }
        }

        return result;
    }

    /**
     * Parses a JSONObject to a Photo
     * @param json JSONObject to parse
     * @return Photo or null
     */
    public static Photo parsePhoto(JSONObject json) {
        if (json == null) return null;

        Photo photo = new Photo();

        photo.reference = json.optString("photo_reference");
        photo.width = json.optInt("width");
        photo.height = json.optInt("height");

        JSONArray attributes = json.optJSONArray("html_attributions");
        if (attributes != null) {
            for (int i = 0; i < attributes.length(); i++) {
                photo.attributes.add(attributes.optString(i));
            }
        }

        return photo;
    }

    private String reference;
    private ArrayList<String> attributes = new ArrayList<>();
    private int width;
    private int height;

    public Photo() {
    }

    public String getReference() {
        return reference;
    }

    public Photo setReference(String reference) {
        this.reference = reference;
        return this;
    }

    public ArrayList<String> getAttributes() {
        return attributes;
    }

    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
    }


    @Override
    public boolean equals(Object o) {
        return (o instanceof Photo && reference!=null && reference.equals(((Photo) o).reference));
    }

    //region Parcelable implementation
    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(reference);
        dest.writeInt(width);
        dest.writeInt(height);
        dest.writeStringList(attributes);
    }

    public static final Creator<Photo> CREATOR = new Creator<Photo>() {

        public Photo createFromParcel(Parcel in) {
            Photo photo = new Photo();

            photo.reference = in.readString();
            photo.width = in.readInt();
            photo.height = in.readInt();
            in.readStringList(photo.getAttributes());

            return photo;
        }

        public Photo[] newArray(int size) {
            return new Photo[size];
        }

    };
    //endregion
}
