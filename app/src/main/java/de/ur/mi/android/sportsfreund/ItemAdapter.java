package de.ur.mi.android.sportsfreund;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CursorAdapter;
import android.widget.TextView;

class ItemAdapter extends ArrayAdapter<ListObject> {

    public ItemAdapter(Context context, ListObject[] item) {
        super(context, R.layout.list_item,item);
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {

        LayoutInflater layoutInflater = LayoutInflater.from(getContext());
        View view = layoutInflater.inflate(R.layout.list_item,parent,false);

        String title = getItem(position).getTitle();
        String body = getItem(position).getBody();

        TextView titleText = (TextView) view.findViewById(R.id.TitleText);
        TextView bodyText = (TextView)  view.findViewById(R.id.BodyText);

        titleText.setText(title);
        bodyText.setText(body);




        return view;
    }
}
