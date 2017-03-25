package dev.datvt.busfun.adapters;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.DecelerateInterpolator;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

import dev.datvt.busfun.R;
import dev.datvt.busfun.models.Place;

/**
 * Created by datvt on 5/10/2016.
 */
public class PlacesAdapter extends BaseAdapter {

    private Activity context;
    private List<Place> places;
    private int mLastPosition;

    public PlacesAdapter(Activity context, List<Place> busList) {
        this.context = context;
        this.places = busList;
    }

    @Override
    public int getCount() {
        if (places != null) {
            return places.size();
        }
        return 0;
    }

    @Override
    public Object getItem(int position) {
        if (places != null) {
            return places.get(position);
        }
        return null;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        final ViewHolder holder;
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        if (convertView == null) {
            convertView = inflater.inflate(R.layout.item_place, null);
            holder = createViewHolder(convertView);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        Place place = (Place) getItem(position);
        holder.name.setText(place.getName());
        holder.vicinity.setText(place.getVicinity());
        for (int i = 0; i < place.getType().size(); i++) {
            holder.type.setText(place.getType().get(i) + ", ");
        }
        holder.icon.setImageBitmap(place.getIcon());

        // Otherwise, we start above the ending point.
        float initialTranslation = (mLastPosition <= position ? 500f : -500f);

        convertView.setTranslationY(initialTranslation);
        convertView.animate()
                .setInterpolator(new DecelerateInterpolator(1.0f))
                .translationY(0f)
                .setDuration(300l)
                .setListener(null);

        // Keep track of the last position we loaded
        mLastPosition = position;

        return convertView;
    }

    private ViewHolder createViewHolder(View v) {
        ViewHolder holder = new ViewHolder();
        holder.icon = (ImageView) v.findViewById(R.id.imageView);
        holder.name = (TextView) v.findViewById(R.id.place_name);
        holder.vicinity = (TextView) v.findViewById(R.id.place_vicinity);
        holder.type = (TextView) v.findViewById(R.id.place_type);
        return holder;
    }

    private static class ViewHolder {
        public TextView name;
        public ImageView icon;
        public TextView vicinity;
        public TextView type;
    }
}
