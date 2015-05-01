package me.ghoscher.nearly.core;

import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.TextView;

import me.ghoscher.nearly.R;

/**
 * Created by Hisham on 1/5/2015.
 */

/**
 * A dialog that locks the acivity hosting it. This can be used for long running loading tasks.
 * This dialog supports multiple lock levels. If it's locked 3 times then it has to be unlocked 3 times to get dismissed.
 * Only use with {@link me.ghoscher.nearly.core.BaseActivity}
 */
public class LockUIDialog extends DialogFragment {

    private TextView tvMessage;

    private String message = null; // TODO convert to messages stack

    // Current lock level
    private int lockLevel = 1;

    public LockUIDialog() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);

        // Only cancelable with BACK button
        setCancelable(false);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.dlg_lock_ui, container, false);

        tvMessage = (TextView) view.findViewById(R.id.tvMessage);

        if (message != null)
            tvMessage.setText(message);

        return view;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        Dialog dlg = super.onCreateDialog(savedInstanceState);
        dlg.requestWindowFeature(Window.FEATURE_NO_TITLE);

        // Manually hanlde the BACK button
        dlg.setOnKeyListener(new DialogInterface.OnKeyListener() {
            @Override
            public boolean onKey(DialogInterface dialog, int keyCode, KeyEvent event) {
                if (keyCode == KeyEvent.KEYCODE_BACK)
                {
                    ((BaseActivity) getActivity()).cancelWork();
                    dismiss();
                    return true;
                }

                return false;
            }
        });

        return dlg;
    }

    public void setMessage(String message) {
        this.message = message;

        if (tvMessage != null)
            tvMessage.setText(message);
    }

    public int lockUI() {
        lockLevel++;
        return lockLevel;
    }

    public int unlockUI() {
        lockLevel--;
        if (lockLevel < 0) lockLevel = 0;

        if (lockLevel == 0)
            dismiss();

        return lockLevel;
    }
}
