package dev.datvt.busfun.activities;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;

import java.util.ArrayList;

import dev.datvt.busfun.R;
import dev.datvt.busfun.adapters.BusListAdapter;
import dev.datvt.busfun.adapters.BusStationListAdapter;
import dev.datvt.busfun.models.Bus;
import dev.datvt.busfun.models.BusStation;
import dev.datvt.busfun.utils.DataHandler;

/**
 * Created by datvt on 5/5/2016.
 */
public class BusStationActivity extends Activity implements View.OnClickListener {

    private ListView lvBusStation;
    private ArrayList<BusStation> stationArrayList;
    private BusStationListAdapter busStationListAdapter;
    private ImageView ivSearch;
    private EditText edtSearch;

    private DataHandler mDataHandler;
    private SQLiteDatabase database = null;

    private Button exit;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.bus_station);

        getForWidget();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mDataHandler.close();
    }

    private void getForWidget() {
        edtSearch = (EditText) findViewById(R.id.edtSearchBus);
        ivSearch = (ImageView) findViewById(R.id.ivSearchBus);

        lvBusStation = (ListView) findViewById(R.id.lvBusStation);

        stationArrayList = new ArrayList<BusStation>();
        busStationListAdapter = new BusStationListAdapter(this, stationArrayList);
        lvBusStation.setAdapter(busStationListAdapter);

        mDataHandler = new DataHandler(this);
        mDataHandler.open();
        database = mDataHandler.getMyDatabase();

        loadListBusStation();


        edtSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                searchBus(edtSearch.getText().toString().trim());
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        lvBusStation.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = getIntent();
                Bundle bundle  = new Bundle();
                BusStation busStation = (BusStation) busStationListAdapter.getItem(position);
                bundle.putSerializable("station", busStation);
                Log.i("STATION", busStation.toString());
                intent.putExtras(bundle);
                setResult(108, intent);
                finish();
            }
        });
    }

    private void loadListBusStation() {
        Cursor c = database.query("BusStop", null, null, null, null, null, null);
        c.moveToFirst();
        while (!c.isAfterLast()) {
            BusStation busStation = new BusStation(c.getString(0), c.getString(3), c.getString(2), c.getString(1), c.getString(4));
            if (busStation.getName().charAt(0) == '(') {
                busStation.setName(busStation.getName().trim().substring(3));
            }
            stationArrayList.add(busStation);
            c.moveToNext();
        }
        busStationListAdapter.notifyDataSetChanged();
        c.close();
    }

    private void searchBus(String bus) {
        if (!bus.isEmpty()) {
            ArrayList<BusStation> busList = new ArrayList<BusStation>();
            for (int i = 0; i < stationArrayList.size(); i++) {
                if (stationArrayList.get(i).getName().startsWith(bus)) {
                    busList.add(stationArrayList.get(i));
                }
            }
            busStationListAdapter = new BusStationListAdapter(this, busList);
            lvBusStation.setAdapter(busStationListAdapter);

        } else {
            busStationListAdapter = new BusStationListAdapter(this, stationArrayList);
            lvBusStation.setAdapter(busStationListAdapter);
        }
    }

    @Override
    public void onClick(View v) {
        if (v == exit) {
            finish();
        } else if (v == ivSearch) {
            if (!edtSearch.getText().toString().trim().isEmpty()) {
                searchBus(edtSearch.getText().toString().trim());
            }
        }
    }
}
