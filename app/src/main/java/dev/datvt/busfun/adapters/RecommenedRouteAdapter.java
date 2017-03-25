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
import dev.datvt.busfun.models.Route;

/**
 * Created by datvt on 5/10/2016.
 */
public class RecommenedRouteAdapter extends BaseAdapter {

    private Activity context;
    private List<Route> busList;
    private int mLastPosition;

    public RecommenedRouteAdapter(Activity context, List<Route> busList) {
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
            convertView = inflater.inflate(R.layout.item_find_road, null);
            holder = createViewHolder(convertView);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        Route route = (Route) getItem(position);
        String[] id = route.getBusID().split(" ");
        String id_bus = "";
        for (int i = 0; i < id.length; i++) {
            id_bus += id[i];
            if (i < id.length - 1) {
                id_bus += " hoặc ";
            }
        }
        holder.busID.setText(id_bus);
        holder.busNumber.setText("Số tuyến cần đi: 1");
        holder.busTitle.setText("Đi tuyến");
        double start = Double.parseDouble(route.getPlaceStart().getDistance());
        double end = Double.parseDouble(route.getPlaceEnd().getDistance());
        int temp = (int)((start + end) * 1000);
        String distance = "";
        if (temp < 1000) {
            distance = temp + " m";
        } else {
            int temp2 = temp / 1000;
            temp = temp % 1000;
            distance = temp2 + "," + temp + " km";
        }
        holder.busDetail.setText("Đi bộ: " + distance);

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
        holder.busID = (TextView) v.findViewById(R.id.tvBusID);
        holder.busTitle = (TextView) v.findViewById(R.id.tvGoBus);
        holder.busNumber = (TextView) v.findViewById(R.id.tvBusNumber);
        holder.busDetail = (TextView) v.findViewById(R.id.tvInfo);
        return holder;
    }

    private static class ViewHolder {
        public TextView busTitle;
        public TextView busID;
        public TextView busNumber;
        public TextView busDetail;
    }
}
