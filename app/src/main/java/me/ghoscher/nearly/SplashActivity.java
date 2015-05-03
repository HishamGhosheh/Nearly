package me.ghoscher.nearly;

import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.FragmentActivity;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;

import me.ghoscher.nearly.fragments.GooglePlayErrorDialog;

public class SplashActivity extends FragmentActivity
        implements GooglePlayErrorDialog.GooglePlayErrorDialogHost {

    private static final int REQ_GET_PLAY_SERVICES = 1;

    private static final long SPLASH_DURATION = 3000;

    private static final String FRG_ERROR_DIALOG = "play_error_dialog";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        View vLogo = findViewById(R.id.vLogo);
        Animation anim = AnimationUtils.loadAnimation(this, R.anim.logo_appear);
        vLogo.startAnimation(anim);
    }

    @Override
    protected void onResume() {
        super.onResume();

        // Remove any pending next activity runnables
        handler.removeCallbacks(runnable);

        //region Checking Play Services
        int playServicesError = GooglePlayServicesUtil.isGooglePlayServicesAvailable(this);

        if (playServicesError == ConnectionResult.SUCCESS) {
            // All Ok, schedule running next activity
//            handler.postDelayed(runnable, BuildConfig.DEBUG ? 0 : SPLASH_DURATION);
            handler.postDelayed(runnable, SPLASH_DURATION);
        } else {
            boolean recoverable = GooglePlayServicesUtil.isUserRecoverableError(playServicesError);

            if (recoverable) {
                GooglePlayErrorDialog dlg = (GooglePlayErrorDialog) getSupportFragmentManager().findFragmentByTag(FRG_ERROR_DIALOG);

                // Avoid displaying multiple dialogs
                if (dlg == null) {
                    dlg = GooglePlayErrorDialog.newInstance(playServicesError, REQ_GET_PLAY_SERVICES);
                    dlg.show(getSupportFragmentManager(), FRG_ERROR_DIALOG);
                }
            } else {
                // TODO show dialog error
                Toast.makeText(this, R.string.play_services_not_supported, Toast.LENGTH_LONG).show();
                finish();
            }
        }
        //endregion
    }

    @Override
    protected void onPause() {
        // Avoid the app popping up if the user presses home while in splash screen
        handler.removeCallbacks(runnable);
        super.onPause();
    }

    @Override
    public void onErrorDialogCancel(GooglePlayErrorDialog dlg, int errorCode) {
        // User doesn't want to fix Play Services error
        finish();
    }

    // Handler to schedule opening the next activity
    private Handler handler = new Handler();

    // Runnable to open the next activity
    private Runnable runnable = new Runnable() {
        @Override
        public void run() {
            MainActivity.launch(SplashActivity.this);
            finish();
        }
    };
}
