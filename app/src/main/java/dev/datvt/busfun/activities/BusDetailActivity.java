package dev.datvt.busfun.activities;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import java.util.ArrayList;

import dev.datvt.busfun.R;
import dev.datvt.busfun.models.Bus;
import dev.datvt.busfun.models.BusStation;
import dev.datvt.busfun.utils.DataHandler;

/**
 * Created by datvt on 5/5/2016.
 */
public class BusDetailActivity extends Activity implements View.OnClickListener {

    private TextView bus_name, bus_cost, bus_time, bus_frequence, direction_go, direction_back;
    private Button btnExit, btnFindRoad;

    private ArrayList<BusStation> stationArrayListGo = null;
    private ArrayList<BusStation> stationArrayListBack = null;
    private String busID;

    private DataHandler mDataHandler;
    private SQLiteDatabase database = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.detail_bus);

        getForWidgets();
        addEvents();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mDataHandler.close();
    }

    private void getForWidgets() {
        bus_name = (TextView) findViewById(R.id.bus_name);
        bus_cost = (TextView) findViewById(R.id.bus_cost);
        bus_time = (TextView) findViewById(R.id.bus_time);
        bus_frequence = (TextView) findViewById(R.id.bus_frequence);
        direction_go = (TextView) findViewById(R.id.direction_go);
        direction_back = (TextView) findViewById(R.id.direction_back);

        btnExit = (Button) findViewById(R.id.exit);
        btnFindRoad = (Button) findViewById(R.id.findRoad);

        mDataHandler = new DataHandler(this);
        mDataHandler.open();
        database = mDataHandler.getMyDatabase();

        getDataBusInfo();
    }

    private void addEvents() {
        btnExit.setOnClickListener(this);
        btnFindRoad.setOnClickListener(this);
    }


    private void getDataBusInfo() {
        Intent intent = getIntent();
        Bundle bundle = intent.getExtras();
        Bus bus = (Bus) bundle.getSerializable("bus_info");
        bus_name.setText(bus.getCode() + " " + bus.getName());
        loadBusDetail(bus.getId());
    }

    private void loadBusDetail(String id) {
        String sql = "SELECT * FROM BusLine WHERE _id = " + id;
        busID = id;
        Cursor c = mDataHandler.selectSQL(sql);
        c.moveToFirst();
        while (!c.isAfterLast()) {
            bus_cost.setText(c.getString(7));
            bus_time.setText(c.getString(6));
            bus_frequence.setText(c.getString(9));
            direction_go.setText(c.getString(11));
            direction_back.setText(c.getString(12));
            c.moveToNext();
        }
        c.close();
    }

    @Override
    public void onClick(View v) {
        if (v == btnExit) {
            finish();
        }
        if (v == btnFindRoad) {
            Intent intent_road = new Intent(BusDetailActivity.this, FindBusRoadActivity.class);
            Bundle bundle_road = new Bundle();
            bundle_road.putString("ID", busID);
            intent_road.putExtras(bundle_road);
            startActivity(intent_road);
            finish();
        }
    }
}