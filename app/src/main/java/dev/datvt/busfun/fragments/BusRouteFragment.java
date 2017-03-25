package dev.datvt.busfun.fragments;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;

import java.util.ArrayList;

import dev.datvt.busfun.R;
import dev.datvt.busfun.activities.BusDetailActivity;
import dev.datvt.busfun.adapters.BusListAdapter;
import dev.datvt.busfun.models.Bus;
import dev.datvt.busfun.utils.DataHandler;

/**
 * Created by datvt on 8/12/2016.
 */
public class BusRouteFragment extends Fragment implements View.OnClickListener {

    private View viewFramgent;

    private ListView lv;
    private ImageView ivSearch;
    private EditText edtSearch;
    private ArrayList<Bus> arrayList;
    private BusListAdapter adapterListBus;


    private DataHandler mDataHandler;
    private SQLiteDatabase database = null;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        viewFramgent = inflater.inflate(R.layout.bus_list, container, false);

        getForWidgets(viewFramgent);
        addEvents();

        return viewFramgent;
    }

    private void getForWidgets(View view) {
        lv = (ListView) view.findViewById(R.id.lvBus);
        edtSearch = (EditText) view.findViewById(R.id.edtSearchBus);
        ivSearch = (ImageView) view.findViewById(R.id.ivSearchBus);

        arrayList = new ArrayList<>();
        adapterListBus = new BusListAdapter(getActivity(), arrayList);
        lv.setAdapter(adapterListBus);

        mDataHandler = new DataHandler(getActivity());
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
        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Bus bus = (Bus) adapterListBus.getItem(position);
                Intent intent = new Intent(getActivity(), BusDetailActivity.class);
                Bundle bundle = new Bundle();
                bundle.putSerializable("bus_info", bus);
                intent.putExtras(bundle);
                startActivity(intent);
                getActivity().finish();
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
            adapterListBus = new BusListAdapter(getActivity(), busList);
            lv.setAdapter(adapterListBus);
        } else {
            adapterListBus = new BusListAdapter(getActivity(), arrayList);
            lv.setAdapter(adapterListBus);
        }
    }

    @Override
    public void onClick(View v) {
        if (v == ivSearch) {
            if (!edtSearch.getText().toString().trim().isEmpty()) {
                searchBus(edtSearch.getText().toString().trim());
            }
        }
    }
}
