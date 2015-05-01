package me.ghoscher.nearly.storage;

import android.content.Context;

import com.j256.ormlite.dao.Dao;

import java.sql.SQLException;
import java.util.ArrayList;

import me.ghoscher.nearly.model.Place;

/**
 * Created by Hisham on 2/5/2015.
 */
public class FavoritesProvider {
    private Dao<Place, Integer> dao;
    private PlacesDatabaseHelper db;


    public FavoritesProvider(Context context) {
        db = new PlacesDatabaseHelper(context);

        try {
            dao = db.getPlacesDao();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public boolean checkExists(Place place) {
        try {
            Place existing = dao.queryForSameId(place);
            return existing != null;
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return false;
    }

    public boolean addPlace(Place place) {
        try {
            dao.createOrUpdate(place);
            return true;
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return false;
    }

    public boolean removePlace(Place place) {
        try {
            dao.delete(place);
            return true;
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return false;
    }

    public ArrayList<Place> getAll() {
        try {
            return new ArrayList<>(dao.queryForAll());
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return new ArrayList<>();
    }

    public void close() {
        db.close();
    }
}
