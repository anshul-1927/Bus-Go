package dev.datvt.busfun.activities;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;

import java.util.ArrayList;

import dev.datvt.busfun.R;
import dev.datvt.busfun.adapters.BusListAdapter;
import dev.datvt.busfun.models.Bus;
import dev.datvt.busfun.utils.DataHandler;

/**
 * Created by datvt on 5/5/2016.
 */
public class BusListActivity extends Activity implements View.OnClickListener {

    private Button exit;
    private ListView lv;
    private ImageView ivSearch;
    private EditText edtSearch;
    private ArrayList<Bus> arrayList;
    private BusListAdapter adapterListBus;


    private DataHandler mDataHandler;
    private SQLiteDatabase database = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.bus_list);

        getForWidgets();
        addEvents();
    }

    private void getForWidgets() {
        lv = (ListView) findViewById(R.id.lvBus);
        edtSearch = (EditText) findViewById(R.id.edtSearchBus);
        ivSearch = (ImageView) findViewById(R.id.ivSearchBus);

        arrayList = new ArrayList<>();
        adapterListBus = new BusListAdapter(this, arrayList);
        lv.setAdapter(adapterListBus);

        mDataHandler = new DataHandler(this);
        mDataHandler.open();
        database = mDataHandler.getMyDatabase();

        loadListBus();

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
    }

    private void addEvents() {
        exit.setOnClickListener(this);
        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Bus bus = (Bus) adapterListBus.getItem(position);
                Intent intent = new Intent(BusListActivity.this, BusDetailActivity.class);
                Bundle bundle = new Bundle();
                bundle.putSerializable("bus_info", bus);
                intent.putExtras(bundle);
                startActivity(intent);
                finish();
            }
        });
    }

    private void loadListBus() {
        Cursor c = database.query("BusLine", null, null, null, null, null, null);
        c.moveToFirst();
        while (!c.isAfterLast()) {
            Bus bus = new Bus(c.getString(0), c.getString(1), c.getString(2));
            arrayList.add(bus);
            c.moveToNext();
        }
        adapterListBus.notifyDataSetChanged();
        c.close();
    }


    private void searchBus(String bus) {
        if (!bus.isEmpty()) {
            ArrayList<Bus> busList = new ArrayList<Bus>();
            for (int i = 0; i < arrayList.size(); i++) {
                if (arrayList.get(i).getCode().indexOf(bus) > -1) {
                    busList.add(arrayList.get(i));
                }
            }
            adapterListBus = new BusListAdapter(this, busList);
            lv.setAdapter(adapterListBus);
        } else {
            adapterListBus = new BusListAdapter(this, arrayList);
            lv.setAdapter(adapterListBus);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mDataHandler.close();
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

    @Override
    public boolean dispatchKeyEvent(KeyEvent e) {
        if (e.getKeyCode() == KeyEvent.KEYCODE_ENTER) {
            searchBus(edtSearch.getText().toString().trim());
            return true;
        }
        return super.dispatchKeyEvent(e);
    }
}
