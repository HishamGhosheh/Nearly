package me.ghoscher.nearly.api;

import android.content.Context;
import android.location.Location;

import com.koushikdutta.async.future.Future;
import com.koushikdutta.async.future.FutureCallback;
import com.koushikdutta.ion.Ion;
import com.koushikdutta.ion.builder.Builders;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import me.ghoscher.nearly.model.Photo;
import me.ghoscher.nearly.model.Place;

/**
 * Created by Hisham on 30/4/2015.
 */

// TODO move parsing off the main thread

/**
 * Wrapper for some Google Places API.
 * Note that the new Google Play Services offers a Place class and Places
 */
public class GoogleApiHelper {

    private static final String GET = "GET";
    private static final String POST = "POST";

    public interface PlacesSearchCallback {
        void onPlacesLoaded(ArrayList<Place> places, Exception e);
    }

    public interface PlaceDetailsCallback {
        void onPlaceDetailsLoaded(Place place, Exception e);
    }

    private Context context;
    private final String apiKey;

    public GoogleApiHelper(Context context, String apiKey) {
        this.context = context;
        this.apiKey = apiKey;
    }

    private Builders.Any.B makeRequest(String method, String url) {
        return Ion.with(context)
                .load(method, url)
                .addQuery("key", apiKey);
    }

    public Future<String> findPlaces(Location location, int radius, final PlacesSearchCallback callback) {
        Builders.Any.B req = makeRequest(GET, Api.Places.GET_PLACES_SEARCH);

        req.addQuery("radius", String.valueOf(radius))
                .addQuery("location", location.getLatitude() + "," + location.getLongitude());

        return req.asString().setCallback(new FutureCallback<String>() {
            @Override
            public void onCompleted(Exception e, String result) {
                ArrayList<Place> places = new ArrayList<Place>();

                if (e == null) {
                    try {
                        JSONObject obj = new JSONObject(result);
                        JSONArray results = obj.optJSONArray("results");

                        if (results != null)
                            places = Place.parseArray(results);

                    } catch (JSONException e1) {
                        e1.printStackTrace();
                        e = e1;
                    }
                }

                callback.onPlacesLoaded(places, e);
            }
        });
    }

    public Future<String> getPlaceDetails(Place place, final PlaceDetailsCallback callback) {
        Builders.Any.B req = makeRequest(GET, Api.Places.GET_PLACE_DETAILS);

        req.addQuery("placeid", place.getId());

        return req.asString().setCallback(new FutureCallback<String>() {
            @Override
            public void onCompleted(Exception e, String result) {
                Place place = null;

                if (e == null) {
                    try {
                        JSONObject obj = new JSONObject(result);
                        obj = obj.optJSONObject("result");

                        if (obj != null)
                            place = Place.parsePlace(obj);

                    } catch (JSONException e1) {
                        e1.printStackTrace();
                        e = e1;
                    }
                }

                callback.onPlaceDetailsLoaded(place, e);
            }
        });
    }

    public String buildPhotoUrl(Photo photo) {
        return buildPhotoUrl(photo, 0);
    }

    public String buildPhotoUrl(Photo photo, int maxWidth) {
        StringBuilder url = new StringBuilder(Api.Places.GET_PLACE_PHOTO);
        url.append("?key=")
                .append(apiKey)

                .append("&photoreference=")
                .append(photo.getReference());

        if (maxWidth != 0) {
            url.append("&maxwidth=")
                    .append(Math.min(maxWidth, 800));
        } else if (photo.getWidth() != 0) {
            url.append("&maxwidth=")
                    .append(photo.getWidth());
        } else if (photo.getHeight() != 0) {
            url.append("&maxheight=")
                    .append(photo.getHeight());
        }

        return url.toString();
    }
}
