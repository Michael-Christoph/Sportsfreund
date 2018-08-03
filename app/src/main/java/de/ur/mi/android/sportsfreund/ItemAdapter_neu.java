package de.ur.mi.android.sportsfreund;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.ArrayList;

public class ItemAdapter_neu extends ArrayAdapter<Game> {

    public ItemAdapter_neu(Context context, ArrayList<Game> items) {
        //super(context, R.layout.list_item,items);
        super(context,0,items);
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {

        LayoutInflater layoutInflater = LayoutInflater.from(getContext());
        View view = layoutInflater.inflate(R.layout.list_item,parent,false);

        String title = getItem(position).getGameName();
        String body = "Ort: " + getItem(position).getGameLocation() + "; Zeit: " + getItem(position).getGameTime();

        TextView titleText = (TextView) view.findViewById(R.id.TitleText);
        TextView bodyText = (TextView)  view.findViewById(R.id.BodyText);

        titleText.setText(title);
        bodyText.setText(body);




        return view;
    }
}
