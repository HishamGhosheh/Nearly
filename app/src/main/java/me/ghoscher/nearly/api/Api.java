package me.ghoscher.nearly.api;

/**
 * Created by Hisham on 30/4/2015.
 */

/**
 * Api links. comes really handy for large api collections
 */
class Api {

    private static final String BASE_URL = "https://maps.googleapis.com/maps/api/place";

    static class Places {
        public static final String GET_PLACES_SEARCH = BASE_URL +  "/nearbysearch/json";
        public static final String GET_PLACE_DETAILS = BASE_URL +  "/details/json";

        public static final String GET_PLACE_PHOTO = BASE_URL +  "/photo";
    }

}
