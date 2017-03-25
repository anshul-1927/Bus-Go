package dev.datvt.busfun.activities;

import android.app.Activity;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.AutoCompleteTextView;
import android.widget.ImageView;
import android.widget.SimpleAdapter;
import android.widget.Toast;

import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.List;

import dev.datvt.busfun.R;
import dev.datvt.busfun.utils.Contants;
import dev.datvt.busfun.utils.DataHandler;
import dev.datvt.busfun.utils.PlaceJSONParser;
import dev.datvt.busfun.utils.RequestHandler;

/**
 * Created by datvt on 5/5/2016.
 */
public class FindLocation extends Activity {


    private AutoCompleteTextView edtLocation;
    private PlacesTask placesTask;
    private ParserTask parserTask;
    private DataHandler mDataHandler;
    private SQLiteDatabase database = null;
    private ImageView ivSearch;
    private View toastView;
    private Toast toast;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.search_location);

        mDataHandler = new DataHandler(this);
        mDataHandler.open();
        database = mDataHandler.getMyDatabase();

        ivSearch = (ImageView) findViewById(R.id.ivSearchLocation);

        edtLocation = (AutoCompleteTextView) findViewById(R.id.edtLocation);
        edtLocation.addTextChangedListener(new TextWatcher() {

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                placesTask = new PlacesTask();
                placesTask.execute(s.toString());
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count,
                                          int after) {
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });

        ivSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                searchLocation();
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mDataHandler.close();
    }

    private void searchLocation() {
        String s = edtLocation.getText().toString().trim();
        if (!s.isEmpty()) {
            Intent intent = getIntent();
            Bundle bundle = new Bundle();
            bundle.putString("location", s);
            intent.putExtras(bundle);
            setResult(104, intent);
            finish();
        } else {
            showToast("Bạn chưa nhập địa điểm cần tìm");
            edtLocation.requestFocus();
        }
    }

    @Override
    public boolean dispatchKeyEvent(KeyEvent e) {
        if (e.getKeyCode() == KeyEvent.KEYCODE_ENTER) {
            searchLocation();
            return true;
        }
        return super.dispatchKeyEvent(e);
    }

    private void showToast(String msg) {
        toast = Toast.makeText(getApplicationContext(), msg, Toast.LENGTH_SHORT);
        toastView = toast.getView();
        toastView.setBackgroundResource(R.drawable.border_toast);
        toast.show();
    }

    private class PlacesTask extends AsyncTask<String, Void, String> {

        @Override
        protected String doInBackground(String... place) {
            String data = "";
            String key = "key=" + Contants.GOOGLE_API_KEY;
            String input = "";
            RequestHandler requestHandler = new RequestHandler();
            try {
                input = "input=" + URLEncoder.encode(place[0], "utf-8");
            } catch (UnsupportedEncodingException e1) {
                e1.printStackTrace();
            }
            String types = "types=geocode";
            String sensor = "sensor=false";
            String parameters = input + "&" + types + "&" + sensor + "&" + key;
            String output = "json";
            String url = Contants.PLACE_AUTOCOMPLETE_URL + output + "?" + parameters;
            try {
                data = requestHandler.read(url);
            } catch (Exception e) {
                Log.d("Background Task", e.toString());
            }
            return data;
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            Log.i("RES_PLACE", result);
            parserTask = new ParserTask();
            parserTask.execute(result);
        }
    }

    private class ParserTask extends AsyncTask<String, Integer, List<HashMap<String, String>>> {
        JSONObject jObject;

        @Override
        protected List<HashMap<String, String>> doInBackground(String... jsonData) {
            List<HashMap<String, String>> places = null;
            PlaceJSONParser placeJsonParser = new PlaceJSONParser();
            try {
                jObject = new JSONObject(jsonData[0]);
                places = placeJsonParser.parse(jObject);
            } catch (Exception e) {
                Log.d("Exception", e.toString());
            }
            return places;
        }

        @Override
        protected void onPostExecute(List<HashMap<String, String>> result) {
            String[] from = new String[]{"description"};
            int[] to = new int[]{android.R.id.text1};
            SimpleAdapter adapter = new SimpleAdapter(getBaseContext(), result, android.R.layout.simple_list_item_1, from, to);
            edtLocation.setAdapter(adapter);
        }
    }

}
