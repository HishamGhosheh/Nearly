package me.ghoscher.nearly;

import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import java.util.ArrayList;
import java.util.concurrent.Future;

import me.ghoscher.nearly.api.GoogleApiHelper;
import me.ghoscher.nearly.core.LocationAwareActivity;
import me.ghoscher.nearly.fragments.BasePlacesFragment;
import me.ghoscher.nearly.fragments.PlacesListFragment;
import me.ghoscher.nearly.fragments.PlacesMapFragment;
import me.ghoscher.nearly.model.Place;


public class MainActivity extends LocationAwareActivity
        implements
        View.OnClickListener,
        GoogleApiHelper.PlacesSearchCallback,
        BasePlacesFragment.PlacesFragmentHost {

    public static void launch(Context context) {
        Intent intent = new Intent(context, MainActivity.class);
        context.startActivity(intent);
    }

    // Fragments index in the ViewPager
    private static final int INDEX_LISTING = 0;
    private static final int INDEX_MAP = 1;

    // TODO move to a settings screen
    private static final int SEARCH_RADIUS = 5000;

    private ArrayList<Place> places = new ArrayList<>();

    private ViewPager vPager;
    private PlacesPagerAdapter pagerAdapter;

    // Current places search request if any
    Future<String> searchRequest = null;

    // Apis wrapper
    GoogleApiHelper apiHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        apiHelper = new GoogleApiHelper(this, getString(R.string.google_browser_key));

        vPager = (ViewPager) findViewById(R.id.vPager);
        findViewById(R.id.vShowListing).setOnClickListener(this);
        findViewById(R.id.vShowMap).setOnClickListener(this);

        pagerAdapter = new PlacesPagerAdapter(getSupportFragmentManager());
        vPager.setAdapter(pagerAdapter);

        // Disable UI while not ready
        lockUI(getString(R.string.waiting_location_update)); // Waiting location fix then places loading
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.action_favorites) {
            FavoritesActivity.launch(this);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.vShowListing:
                // Passing false since ViewPager and MapView didn't mix well
                vPager.setCurrentItem(INDEX_LISTING, false);
                break;
            case R.id.vShowMap:
                // Passing false since ViewPager and MapView didn't mix well
                vPager.setCurrentItem(INDEX_MAP, false);
                break;
        }
    }

    @Override
    public void onPlacesLoaded(ArrayList<Place> places, Exception e) {
        // Got location fix and loaded places (hopefully), allow the user interact
        unlockUI();

        if (e == null) {
            // No longer interested in location updates
            stopLocationUpdates();

            // Send data to fragments
            this.places = places;
            pagerAdapter.notifyDataSetChanged();
        } else {
            makeToast(R.string.get_places_failed);
            // TODO show error dialog and consider retrying
        }
    }

    @Override
    public void onPlaceSelected(BasePlacesFragment frg, Place place) {
        GalleryActivity.launch(this, place);
    }

    @Override
    public void onPlaceOptions(BasePlacesFragment frg, Place place) {

    }

    private class PlacesPagerAdapter extends FragmentPagerAdapter {

        public PlacesPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            if (position == INDEX_LISTING)
                return new PlacesListFragment();
            else if (position == INDEX_MAP)
                return new PlacesMapFragment();
            else
                return new PlacesListFragment();
        }

        @Override
        public int getItemPosition(Object object) {
            // An invalidate will case the existing fragments to be checked
            if (object != null) {
                BasePlacesFragment frg = (BasePlacesFragment) object;
                frg.setPlaces(places);
            }

            return super.getItemPosition(object);
        }

        @Override
        public int getCount() {
            return 2;
        }
    }

    @Override
    protected void onLocationUpdate(Location location) {
        if (this.places.size() > 0)
            return;

        // Cancel any pending search request
        if (searchRequest != null) searchRequest.cancel(true);

        setLockMessage(getString(R.string.loading_data));
        searchRequest = apiHelper.findPlaces(location, SEARCH_RADIUS, this);
    }

    @Override
    public void onConnectionSuspended(int i) {
        // TODO show message
    }
}
