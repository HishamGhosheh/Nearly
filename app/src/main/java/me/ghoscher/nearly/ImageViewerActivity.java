package me.ghoscher.nearly;

import android.app.WallpaperManager;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.text.Html;
import android.text.TextUtils;
import android.text.method.LinkMovementMethod;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.koushikdutta.async.future.FutureCallback;
import com.koushikdutta.ion.Ion;
import com.koushikdutta.ion.ProgressCallback;

import java.io.Closeable;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;

import me.ghoscher.nearly.api.GoogleApiHelper;
import me.ghoscher.nearly.core.BaseActivity;
import me.ghoscher.nearly.model.Photo;


public class ImageViewerActivity extends BaseActivity {

    public static final String EXTRA_PHOTO = "photo";

    /**
     * Starts the activity for a specific photo
     *
     * @param context Context to use
     * @param photo   Photo to display
     */
    public static void launch(Context context, Photo photo) {
        Intent intent = new Intent(context, ImageViewerActivity.class);
        intent.putExtra(EXTRA_PHOTO, photo);
        context.startActivity(intent);
    }

    private Photo photo;
    private String photoUrl;

    // Apis wrapper
    private GoogleApiHelper apiHelper;

    private ImageView imgPhoto;
    private TextView tvAttributes;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image_viewer);

        // Views references
        imgPhoto = (ImageView) findViewById(R.id.imgPhoto);
        tvAttributes = (TextView) findViewById(R.id.tvAttributes);

        apiHelper = new GoogleApiHelper(this, getString(R.string.google_browser_key));

        photo = getIntent().getParcelableExtra(EXTRA_PHOTO);
        photoUrl = apiHelper.buildPhotoUrl(photo);

        // TODO extract image loading to a utils class for easy library changing
        Ion.with(this)
                .load(photoUrl)
                .intoImageView(imgPhoto);

        //region Prepare attributes to be displayed in a line each
        ArrayList<String> attributes = photo.getAttributes();
        StringBuilder attrsText = new StringBuilder();

        for (int i = 0; i < attributes.size(); i++) {
            if (i != 0)
                attrsText.append(Character.LINE_SEPARATOR);

            attrsText.append(attributes.get(i));
        }

        //endregion

        tvAttributes.setText(Html.fromHtml(attrsText.toString()));
        tvAttributes.setMovementMethod(new LinkMovementMethod());
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_image_viewer, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.action_save)
            savePhoto();
        else if (id == R.id.action_share)
            sharePhoto();
        else if (id == R.id.action_wallpaper)
            setWallpaper();

        return true;
    }

    private void sharePhoto() {
        downloadImage(null, new FutureCallback<File>() {
            @Override
            public void onCompleted(Exception e, File result) {
                Intent intent = new Intent(Intent.ACTION_SEND);
                intent.setType("image/*");
                intent.putExtra(Intent.EXTRA_STREAM, Uri.fromFile(result));
                intent = Intent.createChooser(intent, getString(R.string.share_via));
                startActivity(intent);
            }
        });
    }

    private void savePhoto() {
        downloadImage(String.valueOf(System.currentTimeMillis()), new FutureCallback<File>() {
            @Override
            public void onCompleted(Exception e, File result) {
                makeToast(R.string.photo_saved);
                // Nothing else to do, photo already saved
            }
        });
    }

    private void setWallpaper() {
        downloadImage(null, new FutureCallback<File>() {
            @Override
            public void onCompleted(Exception e, File result) {
                // TODO: should be either PNG or JPEG

                FileInputStream in = null;
                try {
                    in = new FileInputStream(result);
                    WallpaperManager wm = WallpaperManager.getInstance(ImageViewerActivity.this);
                    wm.setStream(in);

                    makeToast(R.string.download_started);

                } catch (IOException e1) {
                    e1.printStackTrace();
                    makeToast(R.string.wallpaper_change_failed);
                } finally {
                    silentClose(in);
                }
            }
        });
    }

    private void downloadImage(String filename, final FutureCallback<File> callback) {

        // TODO: check storage state and better manage photo naming
        // TODO: image extension
        if (TextUtils.isEmpty(filename))
            filename = "nearly_temp.jpeg";

        String photoPath = getExternalFilesDir(Environment.DIRECTORY_PICTURES) + File.separator + filename;

        makeToast(R.string.download_started, Toast.LENGTH_SHORT);

        Ion.with(this)
                .load(photoUrl)
                .progress(new ProgressCallback() {
                    @Override
                    public void onProgress(long downloaded, long total) {
                        // TODO: show progressbar
                    }
                })
                .write(new File(photoPath))
                .setCallback(new FutureCallback<File>() {
                    @Override
                    public void onCompleted(Exception e, File result) {
                        if (e == null && result != null) {
                            callback.onCompleted(null, result);
                        } else {
                            makeToast(R.string.photo_download_failed);
                        }
                    }
                });
    }

    /**
     * Silently close a closable. Supresses all exceptions
     *
     * @param closeable Closeable to close
     */
    private void silentClose(Closeable closeable) {
        try {
            if (closeable != null)
                closeable.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
