package me.ghoscher.nearly;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

import java.util.ArrayList;

import me.ghoscher.nearly.api.GoogleApiHelper;
import me.ghoscher.nearly.core.BaseActivity;
import me.ghoscher.nearly.fragments.GalleryFragment;
import me.ghoscher.nearly.model.Photo;
import me.ghoscher.nearly.model.Place;
import me.ghoscher.nearly.storage.FavoritesProvider;

/**
 * Receives a place and loads the photos for that place
 */
public class GalleryActivity extends BaseActivity
        implements
        GalleryFragment.GalleryFragmentHost,
        GoogleApiHelper.PlaceDetailsCallback {

    public static final String EXTRA_PLACE = "place";

    // State keys
    public static final String KEY_PLACE = "place";
    public static final String KEY_PHOTOS = "photos";

    private static final String TAG_GALLERY = "frg_gallery";

    /**
     * Starts the activity for this place
     *
     * @param context Context to use
     * @param place   Place to get photos for
     */
    public static void launch(Context context, Place place) {
        Intent intent = new Intent(context, GalleryActivity.class);
        intent.putExtra(EXTRA_PLACE, place);
        context.startActivity(intent);
    }

    // Api wrapper
    private GoogleApiHelper apiHelper;

    private Place place = null;
    private ArrayList<Photo> photos = null;

    private GalleryFragment frgGallery;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gallery);

        // Init api wrapper
        apiHelper = new GoogleApiHelper(this, getString(R.string.google_browser_key));

        if (savedInstanceState == null) {
            // Create fragment even if no photos available
            frgGallery = GalleryFragment.newInstance(new ArrayList<Photo>());

            getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.relContent, frgGallery, TAG_GALLERY)
                    .commit();
        } else {
            frgGallery = (GalleryFragment) getSupportFragmentManager()
                    .findFragmentByTag(TAG_GALLERY);
        }

        // Second if block. Better code readability
        if (savedInstanceState == null) {
            place = getIntent().getParcelableExtra("place");
        } else {
            place = savedInstanceState.getParcelable(KEY_PLACE);
            photos = savedInstanceState.getParcelableArrayList(KEY_PHOTOS);
        }

        // Only load photos if not loaded before
        if (photos == null)
            apiHelper.getPlaceDetails(place, this);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        outState.putParcelable(KEY_PLACE, place);
        outState.putParcelableArrayList(KEY_PHOTOS, photos);
        super.onSaveInstanceState(outState);
    }

    @Override
    public void onPhotoSelected(GalleryFragment frg, Photo photo) {
        ImageViewerActivity.launch(this, photo);
    }

    @Override
    public void onPlaceDetailsLoaded(Place place, Exception e) {
        if (e == null) {
            this.place = place;
            this.photos = place.getPhotos();

            frgGallery.setPhotos(photos);

            if (photos.size() == 0)
                makeToast(R.string.no_photos);
        } else {
            // TODO Show error dialog and consider retrying
            makeToast(R.string.get_place_details_failed);
            finish();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_gallery, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.action_favorite) {
            boolean success = new FavoritesProvider(this).addPlace(place);
            makeToast(success ? R.string.added_successfully : R.string.add_failed);

            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
