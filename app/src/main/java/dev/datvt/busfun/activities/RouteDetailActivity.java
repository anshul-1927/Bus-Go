package dev.datvt.busfun.activities;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.ListView;

import java.util.ArrayList;

import dev.datvt.busfun.R;
import dev.datvt.busfun.adapters.RouteDetailAdapter;
import dev.datvt.busfun.models.Route;
import dev.datvt.busfun.models.RouteDetail;

/**
 * Created by datvt on 5/7/2016.
 */
public class RouteDetailActivity extends Activity implements View.OnClickListener {

    private ImageView ivBack;
    private ListView listView;
    private RouteDetailAdapter routeDetailAdapter;
    private ArrayList<RouteDetail> routeDetailArrayList;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.detail_list_find_road);

        getForWidgets();
    }

    private void getForWidgets() {
        ivBack = (ImageView) findViewById(R.id.ivBack);
        ivBack.setOnClickListener(this);
        listView = (ListView) findViewById(R.id.lvDetailRoad);
        routeDetailArrayList = new ArrayList<RouteDetail>();
        routeDetailAdapter = new RouteDetailAdapter(this, routeDetailArrayList);
        listView.setAdapter(routeDetailAdapter);

        getDataBusInfo();
    }

    private void getDataBusInfo() {
        Intent intent = getIntent();
        Bundle bundle = intent.getExtras();
        Route route = (Route) bundle.getSerializable("route");

        String[] id = route.getBusID().split(" ");
        String id_bus = "";
        String t = " hoặc ";
        for (int i = 0; i < id.length; i++) {
            id_bus += id[i];
            if (i < id.length - 1) {
                id_bus += " " + t;
            }
        }
        Log.i("ID", id_bus);

        double start = Double.parseDouble(route.getPlaceStart().getDistance());
        double end = Double.parseDouble(route.getPlaceEnd().getDistance());
        Log.i("DISTANCE_START", start + "");
        Log.i("DISTANCE_END", end + "");

        int temp = (int) (start * 1000);
        int temp1 = (int) (end * 1000);
        String distance_start = "";
        String distance_end = "";
        if (temp < 1000) {
            distance_start = temp + " m";
        } else {
            int temp2 = temp / 1000;
            temp = temp % 1000;
            distance_start = temp2 + "," + temp + " km";
        }
        if (temp1 < 1000) {
            distance_end = temp1 + " m";
        } else {
            int temp3 = temp1 / 1000;
            temp1 = temp1 % 1000;
            distance_end = temp3 + "," + temp1 + " km";
        }


        RouteDetail routeDetail = new RouteDetail();
        routeDetail.setId("");
        routeDetail.setIcon(true);
        routeDetail.setType("Đi bộ");
        routeDetail.setDistance(distance_start);
        if (route.getPlaceStart().getVicinity() == null || route.getPlaceStart().getVicinity().equals("Vietnam")) {
            routeDetail.setInfo(route.getPlaceStart().getAddress_start() + " -> Bến " + route.getPlaceStart().getName());
        } else {
            routeDetail.setInfo(route.getPlaceStart().getAddress_start() + " -> Bến " + route.getPlaceStart().getVicinity());
        }
        routeDetailArrayList.add(routeDetail);

        routeDetail = new RouteDetail();
        routeDetail.setId(id_bus);
        routeDetail.setIcon(false);
        routeDetail.setType("Đi tuyến");
        routeDetail.setDistance("");
        if ((route.getPlaceStart().getVicinity() == null ||
                route.getPlaceStart().getVicinity().equals("Vietnam")) &&
                route.getPlaceEnd().getVicinity() != null &&
                !route.getPlaceEnd().getVicinity().equals("Vietnam")) {
            routeDetail.setInfo("Đi từ bến " + route.getPlaceStart().getName() + " -> Bến " +
                    route.getPlaceEnd().getVicinity());
        } else if (route.getPlaceStart().getVicinity() != null &&
                !route.getPlaceStart().getVicinity().equals("Vietnam") &&
                (route.getPlaceEnd().getVicinity() == null ||
                        route.getPlaceEnd().getVicinity().equals("Vietnam"))) {
            routeDetail.setInfo("Đi từ bến " + route.getPlaceStart().getVicinity() + " -> Bến " +
                    route.getPlaceEnd().getName());
        } else if ((route.getPlaceStart().getVicinity() == null ||
                route.getPlaceStart().getVicinity().equals("Vietnam")) &&
                (route.getPlaceEnd().getVicinity() == null ||
                        route.getPlaceEnd().getVicinity().equals("Vietnam"))) {
            routeDetail.setInfo("Đi từ bến " + route.getPlaceStart().getName() + " -> Bến " +
                    route.getPlaceEnd().getName());
        } else {
            routeDetail.setInfo("Đi từ bến " + route.getPlaceStart().getVicinity() + " -> Bến " +
                    route.getPlaceEnd().getVicinity());
        }
        routeDetailArrayList.add(routeDetail);

        routeDetail = new RouteDetail();
        routeDetail.setId("");
        routeDetail.setIcon(true);
        routeDetail.setType("Đi bộ");
        routeDetail.setDistance(distance_end);
        if (route.getPlaceEnd().getVicinity() == null || route.getPlaceEnd().getVicinity().equals("Vietnam")) {
            routeDetail.setInfo("Từ bến " + route.getPlaceEnd().getName() + " -> " + route.getPlaceEnd().getAddress_end());
        } else {
            routeDetail.setInfo("Từ bến " + route.getPlaceEnd().getVicinity() + " -> " + route.getPlaceEnd().getAddress_end());
        }

        routeDetailArrayList.add(routeDetail);
        routeDetailAdapter.notifyDataSetChanged();
    }


    @Override
    public void onClick(View v) {
        if (v == ivBack) {
            finish();
        }
    }
}
