package me.ghoscher.nearly.fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import me.ghoscher.nearly.R;
import me.ghoscher.nearly.model.Place;

/**
 * Created by Hisham on 30/4/2015.
 */

/**
 * Displays places as clickable pins on a map
 */
public class PlacesMapFragment extends BasePlacesFragment implements
        OnMapReadyCallback,
        GoogleMap.OnMarkerClickListener {

    private MapView vMap;
    private GoogleMap map;

    // Linking markers to places for quick lookup
    private HashMap<Marker, Place> markersPlacesMap = new HashMap<>();

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.frg_places_map, container, false);

        vMap = (MapView) view.findViewById(R.id.vMap);
        vMap.getMapAsync(this);

        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        vMap.onCreate(savedInstanceState);
    }

    @Override
    public void setPlaces(ArrayList<Place> places) {
        super.setPlaces(places);

        // Map might not be ready yet. When it's ready it will automatically refresh markers
        if (map != null)
            refreshMarkers();
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        map = googleMap;
        map.setMyLocationEnabled(true);
        map.getUiSettings().setMyLocationButtonEnabled(true);

        map.setOnMarkerClickListener(this);

        MapsInitializer.initialize(getActivity());

        // Display places if setPlaces was called before
        refreshMarkers();
    }

    private void refreshMarkers() {
        // Clear everything
        map.clear();
        markersPlacesMap.clear();

        // Zoom map to include all the places
        if (places.size() > 0) {
            LatLngBounds.Builder boundsBuilder = LatLngBounds.builder();
            for (Place p : places)
                boundsBuilder.include(p.getLocation());

            map.animateCamera(CameraUpdateFactory.newLatLngBounds(boundsBuilder.build(), 50));
        }

        // Add all markers
        for (Place place : places) {
            Marker marker = map.addMarker(new MarkerOptions()
                            .position(place.getLocation())
                            .draggable(false)
                            .title(String.valueOf(place.getName()))
                            .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED))
            );

            markersPlacesMap.put(marker, place);
        }
    }

    @Override
    public void removePlace(Place place) {
        for (Map.Entry<Marker, Place> entry : markersPlacesMap.entrySet()) {
            if (entry.getValue().equals(place)) {
                entry.getKey().remove();
                break;
            }
        }

        super.removePlace(place);
    }

    @Override
    public boolean onMarkerClick(Marker marker) {
        // Get wrapper and inform of place select
        PlacesFragmentHost host = getHost();
        if (host != null) {
            Place place = markersPlacesMap.get(marker);
            host.onPlaceSelected(this, place);
        }

        return true;
    }

    //region MapView lifecycle management
    @Override
    public void onResume() {
        super.onResume();
        if (vMap != null) vMap.onResume();
    }

    @Override
    public void onPause() {
        if (vMap != null) vMap.onPause();
        super.onPause();
    }

    @Override
    public void onDestroy() {
        if (vMap != null) vMap.onDestroy();
        super.onDestroy();
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        if (vMap != null) vMap.onSaveInstanceState(outState);
        super.onSaveInstanceState(outState);
    }

    @Override
    public void onLowMemory() {
        if (vMap != null) vMap.onLowMemory();
        super.onLowMemory();
    }
    //endregion
}
