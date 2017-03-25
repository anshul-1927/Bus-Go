package dev.datvt.busfun.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import dev.datvt.busfun.R;

public class CategoryAdapter extends ArrayAdapter<String> {

    private Context context;
    private String[] categoryies;
    private int ivIcon;

    public CategoryAdapter(Context context, int ivIcon, String[] categories) {
        super(context, ivIcon, categories);
        this.categoryies = categories;
        this.ivIcon = ivIcon;
        this.context = context;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        LayoutInflater inflater = (LayoutInflater) context
                .getSystemService(context.LAYOUT_INFLATER_SERVICE);
        View rowView = inflater.inflate(R.layout.category, parent, false);
        ImageView iv = (ImageView) rowView.findViewById(R.id.ivIcon);
        TextView tv = (TextView) rowView.findViewById(R.id.tvCategory);
        iv.setImageResource(ivIcon);
        tv.setText(categoryies[position]);
        return rowView;
    }

}
