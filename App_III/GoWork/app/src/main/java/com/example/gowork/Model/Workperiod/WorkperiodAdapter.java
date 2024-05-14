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

import com.example.gowork.R;

import java.text.SimpleDateFormat;
import java.util.List;

public class WorkperiodAdapter extends ArrayAdapter<Workperiod> {
    private Context context;
    private List<Workperiod> workPeriods;

    TextView txtPeriodLongitude, txtPeriodLatitude, txtStartTime, txtStopTime;
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

        // Set data to the UI elements
        Workperiod workPeriod = workPeriods.get(position);
        txtPeriodLongitude.setText(String.valueOf(workPeriod.getLongitude()));
        txtPeriodLatitude.setText(String.valueOf(workPeriod.getLatitude()));

        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss"); // Example pattern

        if (workPeriod.getStartTime() != null) {
            String formattedStartDate = sdf.format(workPeriod.getStartTime());
            txtStartTime.setText(formattedStartDate);
        }
        if (workPeriod.getStopTime() != null) {
            String formattedStopDate = sdf.format(workPeriod.getStopTime());
            txtStopTime.setText(formattedStopDate);
        }
        // Set OnClickListener for the delete button, so it calls the OnDeleteListener
        btnStop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Call the onStopClickListener to notify MainActivity of the stop action
                if (onStopClickListener != null) {
                    onStopClickListener.onStopClick(position);
                }
            }
        });
        btnDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Call the onStopClickListener to notify MainActivity of the stop action
                if (onDeleteClickListener != null) {
                    onDeleteClickListener.onDeleteClick(position);
                }
            }
        });

        return rowView;
    }
}
