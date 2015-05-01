package me.ghoscher.nearly.core;

import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;


/**
 * Base activity class to provide common functionality
 */
public abstract class BaseActivity extends AppCompatActivity {

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        return false;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        return super.onOptionsItemSelected(item);
    }

    /**
     * Utility method to create toasts
     * @param message Message to be displayed as a toast
     */
    protected void makeToast(int message) {
        Toast.makeText(this, message, Toast.LENGTH_LONG).show();
    }

    /**
     * Utility method to create toasts
     * @param message Message to be displayed as a toast
     * @param length Toast duration
     */
    protected void makeToast(int message, int length) {
        Toast.makeText(this, message, length).show();
    }

    //region UI locking mechanism
    private static final String TAG_LOCK_UI = "base_activity_lock_ui";

    /**
     * Sets the message displayed in the lock dialog if exists
     * @param message Message to be displayed
     */
    protected void setLockMessage(String message) {
        LockUIDialog dlg = (LockUIDialog) getSupportFragmentManager().findFragmentByTag(TAG_LOCK_UI);
        if (dlg != null)
            dlg.setMessage(message);
    }

    /**
     * Adds one level to the UI lock
     */
    protected void lockUI() {
        lockUI(null);
    }

    /**
     * Adds one level to the UI lock and updates the dialog message
     * @param message Message resource id to be displayed
     */
    protected void lockUI(int message) {
        lockUI(getString(message));
    }

    /**
     * Adds one level to the UI lock and updates the dialog message
     * @param message Message to be displayed
     */
    protected void lockUI(String message) {
        LockUIDialog dlg = (LockUIDialog) getSupportFragmentManager().findFragmentByTag(TAG_LOCK_UI);
        if (dlg == null) {
            dlg = new LockUIDialog();
            dlg.setMessage(message);
            dlg.show(getSupportFragmentManager(), TAG_LOCK_UI);
        } else {
            dlg.lockUI();
        }
    }

    /**
     * Subtracts one level from the UI lock. If the level reaches zero the the lock will be removed
     */
    protected void unlockUI() {
        LockUIDialog dlg = (LockUIDialog) getSupportFragmentManager().findFragmentByTag(TAG_LOCK_UI);
        if (dlg != null)
            dlg.unlockUI();
    }

    /**
     * Called when the user presses back while the UI is locked. The default implementation finishes the activity.
     */
    protected void cancelWork() {
        finish();
    }
    //endregion
}
