package me.ghoscher.nearly.fragments;

import android.app.Activity;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;

import com.google.android.gms.common.GooglePlayServicesUtil;

/**
 * Created by Hisham on 29/4/2015.
 */

/**
 * DialogFragment wrapper for Google Play Services error dialog, avoiding potential dialog leaking
 */
public class GooglePlayErrorDialog extends DialogFragment {

    public interface GooglePlayErrorDialogHost {
        /**
         * Called when the user cancels the error dialog, no fix has been applied
         * @param dlg The wrapping dialog fragment instance
         * @param errorCode The original error code this dialog was created for
         */
        void onErrorDialogCancel(GooglePlayErrorDialog dlg, int errorCode);
    }

    public GooglePlayErrorDialog() {
    }

    public static GooglePlayErrorDialog newInstance(int errorCode, int reqCode) {
        GooglePlayErrorDialog dlg = new GooglePlayErrorDialog();

        Bundle args = new Bundle();
        args.putInt("error", errorCode);
        args.putInt("req_code", reqCode);
        dlg.setArguments(args);

        return dlg;
    }

    private int errorCode;
    private int reqCode;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        errorCode = getArguments().getInt("error");
        reqCode = getArguments().getInt("req_code");
    }

    @Override
    public Dialog getDialog() {
        // Create the wrapped dialog
        return GooglePlayServicesUtil.getErrorDialog(errorCode, getActivity(), reqCode, internalCancelListener);
    }

    private DialogInterface.OnCancelListener internalCancelListener = new DialogInterface.OnCancelListener() {
        @Override
        public void onCancel(DialogInterface dialog) {
            GooglePlayErrorDialogHost host = getHost();
            if (host != null)
                host.onErrorDialogCancel(GooglePlayErrorDialog.this, errorCode);
        }
    };

    private GooglePlayErrorDialogHost getHost() {
        // Has parent fragment?
        Fragment target = getTargetFragment();
        if (target instanceof GooglePlayErrorDialogHost)
            return (GooglePlayErrorDialogHost) target;

        // Is attached?
        Activity activity = getActivity();
        if (activity instanceof GooglePlayErrorDialogHost)
            return (GooglePlayErrorDialogHost) activity;

        return null;
    }
}
