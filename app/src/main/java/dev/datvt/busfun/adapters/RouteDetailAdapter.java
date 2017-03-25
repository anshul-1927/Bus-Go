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
import dev.datvt.busfun.models.RouteDetail;

/**
 * Created by datvt on 5/12/2016.
 */
public class RouteDetailAdapter extends BaseAdapter {

    private Activity context;
    private List<RouteDetail> busList;
    private int mLastPosition;

    public RouteDetailAdapter(Activity context, List<RouteDetail> busList) {
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
            convertView = inflater.inflate(R.layout.item_detail_find_road, null);
            holder = createViewHolder(convertView);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        RouteDetail routeDetail = (RouteDetail) getItem(position);

        if (routeDetail.isIcon()) {
            holder.ivIcon.setImageResource(R.drawable.walk);
        } else {
            holder.ivIcon.setImageResource(R.drawable.ic_bus_stop);
        }
        holder.busID.setText(routeDetail.getId());
        holder.busTitle.setText(routeDetail.getType() + " " + routeDetail.getDistance());
        holder.busDetail.setText(routeDetail.getInfo());

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
        holder.busDetail = (TextView) v.findViewById(R.id.tvInfo);
        holder.ivIcon = (ImageView) v.findViewById(R.id.ivIcon);
        return holder;
    }

    private static class ViewHolder {
        public TextView busTitle;
        public TextView busID;
        public TextView busDetail;
        public ImageView ivIcon;
    }
}