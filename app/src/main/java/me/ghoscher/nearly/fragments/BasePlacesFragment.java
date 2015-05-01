package me.ghoscher.nearly.fragments;


import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.Fragment;

import java.util.ArrayList;

import me.ghoscher.nearly.model.Place;

/**
 * Base fragment for fragments that display places list in some way
 */
public abstract class BasePlacesFragment extends Fragment {

    /**
     * Implement this to receive an event when a place is selected
     */
    public interface PlacesFragmentHost {
        void onPlaceSelected(BasePlacesFragment frg, Place place);

        void onPlaceOptions(BasePlacesFragment frg, Place place);
    }

    protected ArrayList<Place> places = new ArrayList<>();

    public BasePlacesFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);
    }

    /**
     * Removes the provided place from the fragment if exists
     *
     * @param place Place to remove
     */
    public void removePlace(Place place) {
        places.remove(place);
    }

    /**
     * Replaces the current places and invalidates the displayed places
     *
     * @param places Places to display
     */
    public void setPlaces(ArrayList<Place> places) {
        this.places = new ArrayList<>(places);
    }

    /**
     * Gets the wrapper of this fragment
     *
     * @return An implementation of {@link me.ghoscher.nearly.fragments.BasePlacesFragment.PlacesFragmentHost}
     */
    protected PlacesFragmentHost getHost() {
        Fragment target = getTargetFragment();
        if (target instanceof PlacesFragmentHost)
            return (PlacesFragmentHost) target;

        Activity activity = getActivity();

        if (activity instanceof PlacesFragmentHost)
            return (PlacesFragmentHost) activity;

        return null;
    }
}
