package dev.datvt.busfun.activities;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import dev.datvt.busfun.R;
import dev.datvt.busfun.models.Bus;
import dev.datvt.busfun.utils.DataHandler;

/**
 * Created by datvt on 5/13/2016.
 */
public class DetailBusActivity extends Activity implements View.OnClickListener {

    private TextView bus_name, bus_cost, bus_time, bus_frequence, direction_go, direction_back;
    private Button btnExit;

    private DataHandler mDataHandler;
    private SQLiteDatabase database = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.detail_bus_route);

        getForWidgets();
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
        btnExit.setOnClickListener(this);

        mDataHandler = new DataHandler(this);
        mDataHandler.open();
        database = mDataHandler.getMyDatabase();

        getDataBusInfo();
    }

    private void getDataBusInfo() {
        Intent intent = getIntent();
        Bundle bundle = intent.getExtras();
        Bus bus = (Bus) bundle.getSerializable("bus");
        loadBusDetail(bus.getId());
    }

    private void loadBusDetail(String id) {
        String sql = "SELECT * FROM BusLine WHERE _id = " + id;
        Cursor c = mDataHandler.selectSQL(sql);
        c.moveToFirst();
        while (!c.isAfterLast()) {
            bus_name.setText(c.getString(2) + " " + c.getString(1));
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
    }
}

