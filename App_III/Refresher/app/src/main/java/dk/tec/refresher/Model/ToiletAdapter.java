package dk.tec.refresher.Model;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.List;

import dk.tec.refresher.R;

public class ToiletAdapter extends ArrayAdapter<ToiletLocation> {
    private Context context;
    private List<ToiletLocation> toilets;
    private OnDeleteListener onDeleteListener;

    public ToiletAdapter(Context context, List<ToiletLocation> toilets, OnDeleteListener onDeleteListener) {
        super(context, R.layout.list_item_toilet, toilets);
        this.context = context;
        this.toilets = toilets;
        this.onDeleteListener = onDeleteListener;
    }

    public interface OnDeleteListener {
        void onDelete(int position);
    }
    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View rowView = inflater.inflate(R.layout.list_item_toilet, parent, false);

        // Get references to the UI elements in the list item layout
        TextView txtToiletLongitude = rowView.findViewById(R.id.txt_toilet_longitude);
        TextView txtToiletLatitude = rowView.findViewById(R.id.txt_toilet_latitude);
        Button btnDelete = rowView.findViewById(R.id.btnDelete);

        // Set data to the UI elements
        ToiletLocation toilet = toilets.get(position);
        txtToiletLongitude.setText(String.valueOf(toilet.getLongitude()));
        txtToiletLatitude.setText(String.valueOf(toilet.getLatitude()));

        // Set OnClickListener for the delete button, so it calls the OnDeleteListener
        btnDelete.setOnClickListener(v -> {
            onDeleteListener.onDelete(position);
        });

        return rowView;
    }
}
