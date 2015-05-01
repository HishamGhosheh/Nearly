package me.ghoscher.nearly;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import java.util.ArrayList;

import me.ghoscher.nearly.core.BaseActivity;
import me.ghoscher.nearly.fragments.BasePlacesFragment;
import me.ghoscher.nearly.fragments.PlacesListFragment;
import me.ghoscher.nearly.model.Place;
import me.ghoscher.nearly.storage.FavoritesProvider;


public class FavoritesActivity extends BaseActivity
        implements BasePlacesFragment.PlacesFragmentHost {

    public static void launch(Context context) {
        Intent intent = new Intent(context, FavoritesActivity.class);
        context.startActivity(intent);
    }

    public static final String TAG_PLACES_LIST = "frg_places";

    private PlacesListFragment frg;

    FavoritesProvider favoritesProvider;

    @Override

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_favorites);

        favoritesProvider = new FavoritesProvider(this);

        if (savedInstanceState == null) {
            ArrayList<Place> favorites = favoritesProvider.getAll();

            frg = PlacesListFragment.newInstance(favorites);

            getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.relContent, frg, TAG_PLACES_LIST)
                    .commit();

            makeToast(R.string.long_press_remove);
        } else {
            frg = (PlacesListFragment) getSupportFragmentManager().findFragmentByTag(TAG_PLACES_LIST);
        }
    }

    @Override
    public void onPlaceSelected(BasePlacesFragment frg, Place place) {
        GalleryActivity.launch(this, place);
    }

    @Override
    public void onPlaceOptions(BasePlacesFragment frg, Place place) {
        boolean removed = favoritesProvider.removePlace(place);
        if (removed) {
            frg.removePlace(place);
        }
    }

    @Override
    public void finish() {
        favoritesProvider.close();
        super.finish();
    }
}
