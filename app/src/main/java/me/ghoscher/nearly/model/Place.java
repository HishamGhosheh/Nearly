package me.ghoscher.nearly.model;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.android.gms.maps.model.LatLng;
import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Created by Hisham on 30/4/2015.
 */

@DatabaseTable(tableName = "places")
public class Place implements Parcelable {

    /**
     * Parses a JSONArray into Place array
     *
     * @param arr JSONArray to parse
     * @return Place array
     */
    public static ArrayList<Place> parseArray(JSONArray arr) {
        ArrayList<Place> result = new ArrayList<>();

        if (arr != null) {
            for (int i = 0; i < arr.length(); i++) {
                JSONObject obj = arr.optJSONObject(i);
                Place place = parsePlace(obj);

                if (place != null)
                    result.add(place);
            }
        }

        return result;
    }

    /**
     * Parses a JSONObject to a Place
     *
     * @param json JSONObject to parse
     * @return Place or null
     */
    public static Place parsePlace(JSONObject json) {
        if (json == null) return null;

        Place place = new Place();

        place.id = json.optString("place_id");
        place.icon = json.optString("icon");
        place.name = json.optString("name");

        JSONObject geometry = json.optJSONObject("geometry");
        if (geometry != null) {
            JSONObject location = geometry.optJSONObject("location");

            double lat = location.optDouble("lat");
            double lng = location.optDouble("lng");

            place.location = new LatLng(lat, lng);
        }

        JSONArray photos = json.optJSONArray("photos");
        place.photos = Photo.parseArray(photos);

        return place;
    }

    @DatabaseField
    private String id;

    @DatabaseField
    private String name;

    @DatabaseField
    private String icon;

    // Not saving location
    private LatLng location;

    // Not saving photos
    private ArrayList<Photo> photos = new ArrayList<>();

    public Place() {

    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public LatLng getLocation() {
        return location;
    }

    public String getIcon() {
        return icon;
    }

    public ArrayList<Photo> getPhotos() {
        return photos;
    }

    public Place setId(String id) {
        this.id = id;
        return this;
    }

    public Place setName(String name) {
        this.name = name;
        return this;
    }

    public Place setLocation(LatLng location) {
        this.location = location;
        return this;
    }

    @Override
    public boolean equals(Object o) {
        return (o instanceof Place && id!=null && id.equals(((Place) o).id));
    }

    //region Parcelable implementation
    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(id);
        dest.writeString(name);
        dest.writeString(icon);
        dest.writeParcelable(location, 0);
        dest.writeTypedList(photos);
    }

    public static final Creator<Place> CREATOR = new Creator<Place>() {

        public Place createFromParcel(Parcel in) {
            Place place = new Place();

            place.id = in.readString();
            place.name = in.readString();
            place.icon = in.readString();
            place.location = in.readParcelable(LatLng.class.getClassLoader());
            in.readTypedList(place.photos, Photo.CREATOR);

            return place;
        }

        public Place[] newArray(int size) {
            return new Place[size];
        }

    };
    //endregion
}
