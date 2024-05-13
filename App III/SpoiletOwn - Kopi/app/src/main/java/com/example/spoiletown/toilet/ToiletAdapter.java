package com.example.spoiletown.toilet;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.example.spoiletown.MainActivity;
import com.example.spoiletown.R;

import java.util.List;

public class ToiletAdapter extends BaseAdapter {
    int number;
    private final List<Toilet> listToilet;
    private MainActivity main;

    TextView txtName,txtLongitude,txtAltitude,txtLatitude,txtBearing;

    public ToiletAdapter(List<Toilet> listToilet, MainActivity main) {
        this.listToilet = listToilet;
        this.main = main;
    }


    @Override
    public int getCount() {
        return listToilet.size();
    }

    @Override
    public Object getItem(int position) {
        return null;
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater = LayoutInflater.from(main);
        View v = inflater.inflate(R.layout.custom_toilet,null);

        /**
         * Just some fluff to give some visual flair to the list; alternates backgroud color every other entry.
         */
        if (position % 2 == 0)
        {
            v.setBackgroundResource(R.color.light_grey);
        }
        else {
            v.setBackgroundResource(R.color.dark_grey);
        }

        Toilet toilet = listToilet.get(position);

        txtName = v.findViewById(R.id.txtName);
        txtName.setText(toilet.getId());
        txtAltitude = v.findViewById(R.id.txtAltitude);
        txtAltitude.setText(String.valueOf(toilet.getAltitidue()));
        txtLatitude = v.findViewById(R.id.txtLatitude);
        txtLatitude.setText(String.valueOf(toilet.getLatitude()));
        txtLongitude = v.findViewById(R.id.txtLongitude);
        txtLongitude.setText(String.valueOf(toilet.getLongitude()));
        txtBearing = v.findViewById(R.id.txtBearing);
        txtBearing.setText(String.valueOf(toilet.getBearing()));

        return v;
    }
}
