package me.ghoscher.nearly.fragments;


import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.koushikdutta.ion.Ion;

import java.util.ArrayList;

import me.ghoscher.nearly.R;
import me.ghoscher.nearly.model.Place;

/**
 * Simple fragment that displays places as a vertical list
 */
public class PlacesListFragment extends BasePlacesFragment
        implements
        AdapterView.OnItemClickListener,
        AdapterView.OnItemLongClickListener {

    public static PlacesListFragment newInstance(ArrayList<Place> places) {
        PlacesListFragment frg = new PlacesListFragment();

        Bundle args = new Bundle();
        args.putParcelableArrayList("places", places);
        frg.setArguments(args);

        return frg;
    }

    ListView lstPlaces;
    PlacesAdapter adapter;

    public PlacesListFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Bundle args = getArguments();
        if (args != null) {
            ArrayList<Place> places = args.getParcelableArrayList("places");
            setPlaces(places);
        }
    }

    @Override
    public void setPlaces(ArrayList<Place> places) {
        super.setPlaces(places);

        if (adapter != null) {
            adapter.clear();
            adapter.notifyDataSetChanged();
        }

        adapter = new PlacesAdapter(getActivity(), places);

        if (lstPlaces != null)
            lstPlaces.setAdapter(adapter);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.frg_places_list, container, false);
        lstPlaces = (ListView) view.findViewById(R.id.lstPlaces);

        lstPlaces.setOnItemClickListener(this);
        lstPlaces.setOnItemLongClickListener(this);

        if (adapter != null)
            lstPlaces.setAdapter(adapter);

        return view;
    }


    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        PlacesFragmentHost host = getHost();
        if (host != null) {
            Place place = (Place) parent.getItemAtPosition(position);
            host.onPlaceSelected(this, place);
        }
    }

    @Override
    public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
        PlacesFragmentHost host = getHost();
        if (host != null) {
            Place place = (Place) parent.getItemAtPosition(position);
            host.onPlaceOptions(this, place);
        }

        return true;
    }

    @Override
    public void removePlace(Place place) {
        adapter.remove(place);
        adapter.notifyDataSetChanged();

        super.removePlace(place);
    }

    // Adapter class, a subclass because it's only used by this fragment
    private class PlacesAdapter extends ArrayAdapter<Place> {
        public PlacesAdapter(Context context, ArrayList<Place> places) {
            super(context, 0, places);
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            Place place = getItem(position);
            View view = convertView;

            if (view == null) {
                view = LayoutInflater.from(getContext()).inflate(R.layout.item_place, parent, false);
                ViewHolder holder = new ViewHolder(view);
                view.setTag(holder);
            }

            ViewHolder holder = (ViewHolder) view.getTag();

            holder.tvPlaceName.setText(place.getName());
            holder.tvPlacePhotos.setText(getContext().getString(R.string.photos_count_fmt, place.getPhotos().size()));

            Ion.with(getContext())
                    .load(place.getIcon())
                    .intoImageView(holder.imgPlaceIcon);

            return view;
        }

        private class ViewHolder {
            public ImageView imgPlaceIcon;
            public TextView tvPlaceName;
            public TextView tvPlacePhotos;

            public ViewHolder(View view) {
                imgPlaceIcon = (ImageView) view.findViewById(R.id.imgPlaceIcon);
                tvPlaceName = (TextView) view.findViewById(R.id.tvPlaceName);
                tvPlacePhotos = (TextView) view.findViewById(R.id.tvPlacePhotos);

                view.setTag(this);
            }
        }
    }
}
