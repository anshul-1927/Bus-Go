package dev.datvt.busfun.activities;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;

import java.util.ArrayList;

import dev.datvt.busfun.R;
import dev.datvt.busfun.adapters.BusSaleTicketAdapter;
import dev.datvt.busfun.models.BusStation;
import dev.datvt.busfun.utils.DataHandler;

/**
 * Created by datvt on 5/5/2016.
 */
public class BusSaleTicketActivity extends Activity implements View.OnClickListener {

    private ListView lvBusSaleTick;
    private ArrayList<BusStation> stationArrayList;
    private BusSaleTicketAdapter busSaleTicketAdapter;
    private ImageView ivSearch;
    private EditText edtSearch;

    private DataHandler mDataHandler;
    private SQLiteDatabase database = null;

    private Button exit;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.bus_sale_ticket);

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

        lvBusSaleTick = (ListView) findViewById(R.id.lvBusSaleTicket);

        stationArrayList = new ArrayList<BusStation>();
        busSaleTicketAdapter = new BusSaleTicketAdapter(this, stationArrayList);
        lvBusSaleTick.setAdapter(busSaleTicketAdapter);

        mDataHandler = new DataHandler(this);
        mDataHandler.open();
        database = mDataHandler.getMyDatabase();

        loadListBusSaleTicket();

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

        lvBusSaleTick.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = getIntent();
                Bundle bundle  = new Bundle();
                BusStation busStation = (BusStation) busSaleTicketAdapter.getItem(position);
                bundle.putSerializable("station_sale", busStation);
                intent.putExtras(bundle);
                setResult(106, intent);
                finish();
            }
        });
    }

    private void loadListBusSaleTicket() {
        Cursor c = database.query("BusStop", null, null, null, null, null, null);
        c.moveToFirst();
        while (!c.isAfterLast()) {
            BusStation busStation = new BusStation(c.getString(0), c.getString(3), c.getString(2), c.getString(1), c.getString(4));
            if (busStation.getName().charAt(0) == '(') {
                busStation.setName(busStation.getName().trim().substring(3));
                stationArrayList.add(busStation);
            }
            c.moveToNext();
        }
        busSaleTicketAdapter.notifyDataSetChanged();
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
            busSaleTicketAdapter = new BusSaleTicketAdapter(this, busList);
            lvBusSaleTick.setAdapter(busSaleTicketAdapter);

        } else {
            busSaleTicketAdapter = new BusSaleTicketAdapter(this, stationArrayList);
            lvBusSaleTick.setAdapter(busSaleTicketAdapter);
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
