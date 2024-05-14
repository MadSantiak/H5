package com.example.gowork.Controllers;

import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.widget.TextView;

import com.example.gowork.Model.Workperiod.Workperiod;

import java.text.ParseException;
import java.util.Date;

import static com.example.gowork.MainActivity.sdf;
public class TimeElapsedUpdater implements Runnable {
    private static final Object lock = new Object();
    private Workperiod workPeriod;
    private TextView elapsedTimeTextView;
    private Handler handler;
    private boolean stopRequested;

    public TimeElapsedUpdater(Workperiod workPeriod, TextView elapsedTimeTextView) {
        this.workPeriod = workPeriod;
        this.elapsedTimeTextView = elapsedTimeTextView;
        handler = new Handler(Looper.getMainLooper());
    }

    public void requestStop() {
        stopRequested = true;
    }

    @Override
    public void run() {
        while (!stopRequested && !Thread.currentThread().isInterrupted()) {
            synchronized (lock) {
                // Check if stop time is present
                if (workPeriod.getStopTime() != null) {
                    calculateElapsedTimeWithStopTime();
                } else {
                    calculateElapsedTimeWithoutStopTime();
                }
            }

            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }
    }

    private void calculateElapsedTimeWithoutStopTime() {
        Date start;
        String time = workPeriod.getStartTime();
        Log.d("Time:", time);

        try {
            start = sdf.parse(workPeriod.getStartTime());
        } catch (ParseException e) {
            Log.e("Parsing Error", "Error parsing start time: " + e.getMessage());
            return;
        }

        long elapsedMillis = new Date().getTime() - start.getTime();
        updateElapsedTimeUI(elapsedMillis);
    }

    private void calculateElapsedTimeWithStopTime() {
        Date start, stop;
        try {
            start = sdf.parse(workPeriod.getStartTime());
            stop = sdf.parse(workPeriod.getStopTime());
            long elapsedMillis = stop.getTime() - start.getTime();
            updateElapsedTimeUI(elapsedMillis);
        } catch (ParseException e) {
            Log.e("Parsing Error", "Error parsing start or stop time: " + e.getMessage());
        } finally {
            requestStop();
        }
    }

    private void updateElapsedTimeUI(long elapsedMillis) {
        long elapsedSeconds = elapsedMillis / 1000;
        long hours = elapsedSeconds / 3600;
        long minutes = (elapsedSeconds % 3600) / 60;
        long seconds = elapsedSeconds % 60;

        String elapsedTime = String.format("%02d:%02d:%02d", hours, minutes, seconds);

        handler.post(() -> {
            elapsedTimeTextView.setText(elapsedTime);
        });
    }
}