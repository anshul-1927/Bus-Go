package dev.datvt.busfun.adapters;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.DecelerateInterpolator;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.List;

import dev.datvt.busfun.R;
import dev.datvt.busfun.models.Bus;


/**
 * Created by datvt on 5/2/2016.
 */
public class BusListAdapter extends BaseAdapter {

    private Activity context;
    private List<Bus> busList;
    private int mLastPosition;

    public BusListAdapter(Activity context, List<Bus> busList) {
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
            convertView = inflater.inflate(R.layout.item_bus_list, null);
            holder = createViewHolder(convertView);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        Bus bus = (Bus) getItem(position);
        holder.bus_code.setText(bus.getCode());
        holder.bus_name.setText(bus.getName());

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
        holder.bus_code = (TextView) v.findViewById(R.id.tvBusID);
        holder.bus_name = (TextView) v.findViewById(R.id.tvBusName);
        return holder;
    }

    private static class ViewHolder {
        public TextView bus_code;
        public TextView bus_name;
    }
}
