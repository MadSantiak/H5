package com.example.gowork.Model.Period;

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

public class PeriodAdapter extends ArrayAdapter<Period> {
    private Context context;
    private List<Period> periods;

    private OnClickListener onClickListener;

    public interface OnClickListener {
        void onClick();
    }

    public PeriodAdapter(Context context, List<Period> periods, OnClickListener onDeleteListener) {
        super(context, R.layout.list_item_period, periods);
        this.context = context;
        this.periods = periods;
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
        Period period = periods.get(position);
        txtToiletLongitude.setText(String.valueOf(period.getLongitude()));
        txtToiletLatitude.setText(String.valueOf(period.getLatitude()));

        // Set OnClickListener for the delete button, so it calls the OnDeleteListener
        btnDelete.setOnClickListener(v -> {
            onClickListener.onClick();
        });

        return rowView;
    }
}
