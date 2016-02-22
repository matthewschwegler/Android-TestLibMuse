package com.interaxon.test.libmuse;

import android.app.Activity;
import android.util.Log;
import android.widget.TextView;

import com.interaxon.libmuse.Accelerometer;
import com.interaxon.libmuse.Eeg;
import com.interaxon.libmuse.MuseArtifactPacket;
import com.interaxon.libmuse.MuseDataListener;
import com.interaxon.libmuse.MuseDataPacket;
import com.interaxon.libmuse.MuseFileWriter;

import java.lang.ref.WeakReference;
import java.util.ArrayList;

/**
 * Created by elblo on 2/22/2016.
 */
class DataListener extends MuseDataListener {

    final WeakReference<Activity> activityRef;
    private MuseFileWriter fileWriter;

    DataListener(final WeakReference<Activity> activityRef) {
        this.activityRef = activityRef;
    }

    @Override
    public void receiveMuseDataPacket(MuseDataPacket p) {
        switch (p.getPacketType()) {
            case EEG:
                updateEeg(p.getValues());
                break;
            case ACCELEROMETER:
                updateAccelerometer(p.getValues());
                break;
            case ALPHA_RELATIVE:
                updateAlphaRelative(p.getValues());
                break;
            case BATTERY:
                fileWriter.addDataPacket(1, p);
                // It's library client responsibility to flush the buffer,
                // otherwise you may get memory overflow.
                if (fileWriter.getBufferedMessagesSize() > 8096)
                    fileWriter.flush();
                break;
            default:
                break;
        }
    }

    @Override
    public void receiveMuseArtifactPacket(MuseArtifactPacket p) {
        if (p.getHeadbandOn() && p.getBlink()) {
            Log.i("Artifacts", "blink");
        }
    }

    private void updateAccelerometer(final ArrayList<Double> data) {
        final Activity activity = activityRef.get();
        if (activity != null) {
            activity.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    TextView acc_x = (TextView) activity.findViewById(R.id.acc_x);
                    TextView acc_y = (TextView) activity.findViewById(R.id.acc_y);
                    TextView acc_z = (TextView) activity.findViewById(R.id.acc_z);
                    acc_x.setText(String.format(
                            "%6.2f", data.get(Accelerometer.FORWARD_BACKWARD.ordinal())));
                    acc_y.setText(String.format(
                            "%6.2f", data.get(Accelerometer.UP_DOWN.ordinal())));
                    acc_z.setText(String.format(
                            "%6.2f", data.get(Accelerometer.LEFT_RIGHT.ordinal())));
                }
            });
        }
    }

    private void updateEeg(final ArrayList<Double> data) {
        final Activity activity = activityRef.get();
        if (activity != null) {
            activity.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    TextView tp9 = (TextView) activity.findViewById(R.id.eeg_tp9);
                    TextView fp1 = (TextView) activity.findViewById(R.id.eeg_fp1);
                    TextView fp2 = (TextView) activity.findViewById(R.id.eeg_fp2);
                    TextView tp10 = (TextView) activity.findViewById(R.id.eeg_tp10);
                    tp9.setText(String.format(
                            "%6.2f", data.get(Eeg.TP9.ordinal())));
                    fp1.setText(String.format(
                            "%6.2f", data.get(Eeg.FP1.ordinal())));
                    fp2.setText(String.format(
                            "%6.2f", data.get(Eeg.FP2.ordinal())));
                    tp10.setText(String.format(
                            "%6.2f", data.get(Eeg.TP10.ordinal())));
                }
            });
        }
    }

    private void updateAlphaRelative(final ArrayList<Double> data) {
        final Activity activity = activityRef.get();
        if (activity != null) {
            activity.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    TextView elem1 = (TextView) activity.findViewById(R.id.elem1);
                    TextView elem2 = (TextView) activity.findViewById(R.id.elem2);
                    TextView elem3 = (TextView) activity.findViewById(R.id.elem3);
                    TextView elem4 = (TextView) activity.findViewById(R.id.elem4);
                    elem1.setText(String.format(
                            "%6.2f", data.get(Eeg.TP9.ordinal())));
                    elem2.setText(String.format(
                            "%6.2f", data.get(Eeg.FP1.ordinal())));
                    elem3.setText(String.format(
                            "%6.2f", data.get(Eeg.FP2.ordinal())));
                    elem4.setText(String.format(
                            "%6.2f", data.get(Eeg.TP10.ordinal())));
                }
            });
        }
    }

    public void setFileWriter(MuseFileWriter fileWriter) {
        this.fileWriter  = fileWriter;
    }
}