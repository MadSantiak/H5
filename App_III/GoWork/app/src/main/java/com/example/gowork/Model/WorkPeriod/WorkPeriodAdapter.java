package com.example.gowork.Model.WorkPeriod;

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

import java.util.List;

public class WorkPeriodAdapter extends ArrayAdapter<WorkPeriod> {
    private Context context;
    private List<WorkPeriod> workPeriods;

    private OnClickListener onClickListener;

    public interface OnClickListener {
        void onClick();
    }

    public WorkPeriodAdapter(Context context, List<WorkPeriod> workPeriods, OnClickListener onDeleteListener) {
        super(context, R.layout.list_item_period, workPeriods);
        this.context = context;
        this.workPeriods = workPeriods;
        this.onClickListener = onClickListener;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View rowView = inflater.inflate(R.layout.list_item_period, parent, false);

        // Get references to the UI elements in the list item layout
        TextView txtToiletLongitude = rowView.findViewById(R.id.txt_period_longitude);
        TextView txtToiletLatitude = rowView.findViewById(R.id.txt_period_latitude);
        Button btnDelete = rowView.findViewById(R.id.btnStop);

        // Set data to the UI elements
        WorkPeriod workPeriod = workPeriods.get(position);
        txtToiletLongitude.setText(String.valueOf(workPeriod.getLongitude()));
        txtToiletLatitude.setText(String.valueOf(workPeriod.getLatitude()));

        // Set OnClickListener for the delete button, so it calls the OnDeleteListener
        btnDelete.setOnClickListener(v -> {
            onClickListener.onClick();
        });

        return rowView;
    }
}
