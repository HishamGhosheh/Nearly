package me.ghoscher.nearly.fragments;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.GridView;
import android.widget.ImageView;

import com.koushikdutta.ion.Ion;

import java.util.ArrayList;

import me.ghoscher.nearly.R;
import me.ghoscher.nearly.api.GoogleApiHelper;
import me.ghoscher.nearly.model.Photo;

/**
 * Created by Hisham on 1/5/2015.
 */
public class GalleryFragment extends Fragment
        implements AdapterView.OnItemClickListener {

    public interface GalleryFragmentHost {
        void onPhotoSelected(GalleryFragment frg, Photo photo);
    }

    public static GalleryFragment newInstance(ArrayList<Photo> photos) {
        GalleryFragment frg = new GalleryFragment();

        Bundle args = new Bundle();
        args.putParcelableArrayList("photos", photos);
        frg.setArguments(args);

        return frg;
    }

    private ArrayList<Photo> photos = new ArrayList<>();

    private GoogleApiHelper apiHelper;

    private GridView gridView;
    private PhotosAdapter adapter;

    public GalleryFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        photos = getArguments().getParcelableArrayList("photos");

        apiHelper = new GoogleApiHelper(getActivity(), getString(R.string.google_browser_key));

        setRetainInstance(true);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.frg_gallery, container, false);

        gridView = (GridView) view.findViewById(R.id.grdPhotos);
        adapter = new PhotosAdapter(getActivity(), photos);
        gridView.setAdapter(adapter);

        gridView.setOnItemClickListener(this);

        return view;
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        GalleryFragmentHost host = getHost();
        if (host != null) {
            Photo photo = (Photo) parent.getItemAtPosition(position);
            host.onPhotoSelected(this, photo);
        }
    }

    public void setPhotos(ArrayList<Photo> photos) {
        this.photos = photos;
        adapter = new PhotosAdapter(getActivity(), photos);
        gridView.setAdapter(adapter);
    }

    protected GalleryFragmentHost getHost() {
        Fragment target = getTargetFragment();
        if (target instanceof GalleryFragmentHost)
            return (GalleryFragmentHost) target;

        Activity activity = getActivity();

        if (activity instanceof GalleryFragmentHost)
            return (GalleryFragmentHost) activity;

        return null;
    }

    private class PhotosAdapter extends ArrayAdapter<Photo> {

        public PhotosAdapter(Context context, ArrayList<Photo> photos) {
            super(context, 0, photos);
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            View view = convertView;
            if (view == null) {
                view = LayoutInflater.from(getContext()).inflate(R.layout.item_photo, parent, false);
                ViewHolder holder = new ViewHolder(view);
                view.setTag(holder);
            }

            Photo item = getItem(position);

            ViewHolder holder = (ViewHolder) view.getTag();

            Ion.with(getContext())
                    .load(apiHelper.buildPhotoUrl(item, 300)) // TODO calculate width
                    .intoImageView(holder.imgPhoto);

            return view;
        }

        private class ViewHolder {
            public ImageView imgPhoto;

            public ViewHolder(View view) {
                imgPhoto = (ImageView) view.findViewById(R.id.imgPlacePhoto);
            }
        }
    }
}
