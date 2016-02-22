package com.interaxon.test.libmuse;

import android.app.Activity;
import android.util.Log;
import android.widget.TextView;

import com.interaxon.libmuse.ConnectionState;
import com.interaxon.libmuse.MuseConnectionListener;
import com.interaxon.libmuse.MuseConnectionPacket;
import com.interaxon.libmuse.MuseVersion;

import java.lang.ref.WeakReference;

/**
 * Created by elblo on 2/22/2016.
 */
class ConnectionListener extends MuseConnectionListener {

    final WeakReference<Activity> activityRef;

    ConnectionListener(final WeakReference<Activity> activityRef) {
        this.activityRef = activityRef;
    }

    @Override
    public void receiveMuseConnectionPacket(MuseConnectionPacket p) {
        final ConnectionState current = p.getCurrentConnectionState();
        final String status = p.getPreviousConnectionState().toString() +
                " -> " + current;
        final String full = "Muse " + p.getSource().getMacAddress() +
                " " + status;
        Log.i("Muse Headband", full);
         final Activity activity = activityRef.get();
        // UI thread is used here only because we need to update
        // TextView values. You don't have to use another thread, unless
        // you want to run disconnect() or connect() from connection packet
        // handler. In this case creating another thread is required.
        if (activity != null) {
            activity.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    TextView statusText =
                            (TextView) activity.findViewById(R.id.con_status);
                    statusText.setText(status);
                    TextView museVersionText =
                            (TextView) activity.findViewById(R.id.version);
                    if (current == ConnectionState.CONNECTED) {
                        MuseVersion museVersion = MainActivity.muse.getMuseVersion();
                        String version = museVersion.getFirmwareType() +
                                " - " + museVersion.getFirmwareVersion() +
                                " - " + Integer.toString(
                                museVersion.getProtocolVersion());
                        museVersionText.setText(version);
                    } else {
                        museVersionText.setText(R.string.undefined);
                    }
                }
            });
        }
    }
}
