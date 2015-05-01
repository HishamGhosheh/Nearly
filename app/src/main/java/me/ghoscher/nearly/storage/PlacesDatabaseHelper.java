package me.ghoscher.nearly.storage;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;

import com.j256.ormlite.android.apptools.OrmLiteSqliteOpenHelper;
import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.support.ConnectionSource;
import com.j256.ormlite.table.TableUtils;

import java.sql.SQLException;

import me.ghoscher.nearly.R;
import me.ghoscher.nearly.model.Place;

public class PlacesDatabaseHelper extends OrmLiteSqliteOpenHelper {

    private final static String DATABASE_NAME = "storage.db";
    private final static int DATABASE_VERSION = 1;

    private Dao<Place, Integer> placesDao = null;

    public PlacesDatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION, R.raw.ormlite_config);
    }

    @Override
    public void onCreate(SQLiteDatabase db, ConnectionSource connectionSource) {
        try {
            TableUtils.createTable(connectionSource, Place.class);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }


    @Override
    public void onUpgrade(SQLiteDatabase db, ConnectionSource connectionSource, int oldVersion, int newVersion) {
        try {
            TableUtils.dropTable(connectionSource, Place.class, true);
            // after we drop the old databases, we create the new ones
            onCreate(db, connectionSource);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public Dao<Place, Integer> getPlacesDao() throws SQLException {
        if (placesDao == null) {
            placesDao = getDao(Place.class);
        }
        return placesDao;
    }

    @Override
    public void close() {
        super.close();
        placesDao = null;
    }
}