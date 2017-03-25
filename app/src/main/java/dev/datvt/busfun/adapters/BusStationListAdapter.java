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
import dev.datvt.busfun.models.BusStation;

/**
 * Created by datvt on 5/4/2016.
 */
public class BusStationListAdapter extends BaseAdapter {

    private Activity context;
    private List<BusStation> busList;
    private int mLastPosition;

    public BusStationListAdapter(Activity context, List<BusStation> busList) {
        this.context = context;
        this.busList = busList;
    }

    @Override
    public int getCount() {
        if (busList != null) {
            return busList.size();
        }
        return 0;
    }

    @Override
    public Object getItem(int position) {
        if (busList != null) {
            return busList.get(position);
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
            convertView = inflater.inflate(R.layout.item_bus, null);
            holder = createViewHolder(convertView);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        BusStation busStation = (BusStation) getItem(position);
        holder.bus_name.setText(busStation.getName());
        holder.imageView.setImageResource(R.drawable.icon_bus_stop_1);

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
        holder.imageView = (ImageView) v.findViewById(R.id.imageView);
        holder.bus_name = (TextView) v.findViewById(R.id.tvBusName);
        return holder;
    }

    private static class ViewHolder {
        public TextView bus_name;
        public ImageView imageView;
    }
}
