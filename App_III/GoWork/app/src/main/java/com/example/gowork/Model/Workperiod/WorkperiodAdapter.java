package com.example.gowork.Model.Workperiod;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.gowork.Controllers.TimeElapsedUpdater;
import com.example.gowork.R;

import java.text.SimpleDateFormat;
import java.util.List;

public class WorkperiodAdapter extends ArrayAdapter<Workperiod> {
    private Context context;
    private List<Workperiod> workPeriods;
    TimeElapsedUpdater timeElapsedUpdater;

    TextView txtPeriodLongitude, txtPeriodLatitude, txtStartTime, txtStopTime, txtElapsed, txtId;
    Button btnStop, btnDelete;

    public WorkperiodAdapter(Context context, List<Workperiod> workPeriods) {
        super(context, R.layout.list_item_period, workPeriods);
        this.context = context;
        this.workPeriods = workPeriods;
    }

    public interface OnStopClickListener {
        void onStopClick(int position);
    }
    private OnStopClickListener onStopClickListener;

    public void setOnStopClickListener(OnStopClickListener listener) {
        this.onStopClickListener = listener;
    }

    public interface OnDeleteClickListener {
        void onDeleteClick(int position);
    }
    private OnDeleteClickListener onDeleteClickListener;

    public void setOnDeleteClickListener(OnDeleteClickListener listener) {
        this.onDeleteClickListener = listener;
    }


    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View rowView = inflater.inflate(R.layout.list_item_period, parent, false);

        // Get references to the UI elements in the list item layout
        txtPeriodLongitude = rowView.findViewById(R.id.txt_period_longitude);
        txtPeriodLatitude = rowView.findViewById(R.id.txt_period_latitude);
        txtStartTime = rowView.findViewById(R.id.txt_period_start);
        txtStopTime = rowView.findViewById(R.id.txt_period_stop);
        btnStop = rowView.findViewById(R.id.btnStop);
        btnDelete = rowView.findViewById(R.id.btnDelete);
        txtId = rowView.findViewById(R.id.txt_id);
        txtElapsed = rowView.findViewById(R.id.txt_elapsed);

        // Set data to the UI elements
        Workperiod workPeriod = workPeriods.get(position);

        txtPeriodLongitude.setText(String.valueOf(workPeriod.getLongitude()));
        txtPeriodLatitude.setText(String.valueOf(workPeriod.getLatitude()));
        txtId.setText(String.valueOf(workPeriod.getId()));
        txtStartTime.setText(workPeriod.getStartTime());
        txtStopTime.setText(workPeriod.getStopTime());


        startElapsedTimeUpdaterThread(workPeriod, txtElapsed);



        // Set OnClickListener for the delete button, so it calls the OnDeleteListener
        btnStop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Call the onStopClickListener to notify MainActivity of the stop action
                if (onStopClickListener != null) {
                    onStopClickListener.onStopClick(position);
                }
                timeElapsedUpdater.requestStop();
            }
        });
        btnDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Call the onStopClickListener to notify MainActivity of the stop action
                if (onDeleteClickListener != null) {
                    onDeleteClickListener.onDeleteClick(position);
                }
                timeElapsedUpdater.requestStop();
            }
        });

        return rowView;
    }

    private void startElapsedTimeUpdaterThread(Workperiod workPeriod, TextView elapsedTimeTextView) {
        timeElapsedUpdater = new TimeElapsedUpdater(workPeriod, elapsedTimeTextView);
        Thread thread = new Thread(timeElapsedUpdater);
        thread.start();
    }
}
