package dev.datvt.busfun.activities;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.sqlite.SQLiteDatabase;
import android.location.Address;
import android.location.Criteria;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.ContextMenu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.Toast;

import com.google.android.gms.maps.model.LatLng;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.ExecutionException;

import dev.datvt.busfun.R;
import dev.datvt.busfun.adapters.RecommenedRouteAdapter;
import dev.datvt.busfun.models.Place;
import dev.datvt.busfun.models.Route;
import dev.datvt.busfun.utils.Contants;
import dev.datvt.busfun.utils.DataHandler;
import dev.datvt.busfun.utils.PlaceJSONParser;
import dev.datvt.busfun.utils.Places;
import dev.datvt.busfun.utils.RequestHandler;
import dev.datvt.busfun.utils.TransparentProgressDialog;

/**
 * Created by datvt on 5/4/2016.
 */
public class FindRouteActivity extends Activity implements View.OnClickListener {

    private ArrayList<Place> placeStartArrayList = null;
    private ArrayList<Place> placeEndArrayList = null;
    private ArrayList<Route> routeArrayList;

    private AutoCompleteTextView edtStart, edtEnd;
    private Button btnFindRoad;
    private ImageView ivChange, ivLocationMe, ivLocationNew, ivExit;
    private ListView lvRoute;
    private RecommenedRouteAdapter recommenedRouteAdapter;
    private PlacesTask placesTask;
    private PlacesTaskEnd placesTaskEnd;
    private ParserTask parserTask;
    private ParserTaskEnd parserTaskEnd;

    private LatLng latLngStart, latLngEnd;
    private String address_start = "";
    private String address_end = "";

    private double mLatitude = 0;
    private double mLongitude = 0;
    private boolean checkMe = false;

    private DataHandler mDataHandler;
    private SQLiteDatabase database = null;

    private View toastView;
    private Toast toast;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.find_no_way_bus);

        gettForWidgets();
        addEvents();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mDataHandler.close();
    }

    private void gettForWidgets() {
        mDataHandler = new DataHandler(this);
        mDataHandler.open();
        database = mDataHandler.getMyDatabase();

        edtEnd = (AutoCompleteTextView) findViewById(R.id.edtFinish);
        edtStart = (AutoCompleteTextView) findViewById(R.id.edtBegin);

        btnFindRoad = (Button) findViewById(R.id.btnFindRoad);
        ivExit = (ImageView) findViewById(R.id.ivExit);
        ivChange = (ImageView) findViewById(R.id.ivChange);
        ivLocationMe = (ImageView) findViewById(R.id.ivLocationMe);
        ivLocationNew = (ImageView) findViewById(R.id.ivLocationNew);

        lvRoute = (ListView) findViewById(R.id.lvRoute);
        routeArrayList = new ArrayList<Route>();
        recommenedRouteAdapter = new RecommenedRouteAdapter(this, routeArrayList);
        lvRoute.setAdapter(recommenedRouteAdapter);
        registerForContextMenu(lvRoute);

        getLocationMe();

        edtStart.addTextChangedListener(new TextWatcher() {

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

        edtEnd.addTextChangedListener(new TextWatcher() {

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                placesTaskEnd = new PlacesTaskEnd();
                placesTaskEnd.execute(s.toString());
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count,
                                          int after) {
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });
    }

    private void addEvents() {
        ivExit.setOnClickListener(this);
        btnFindRoad.setOnClickListener(this);
        ivChange.setOnClickListener(this);
        ivLocationNew.setOnClickListener(this);
        ivLocationMe.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.ivExit:
                finish();
                break;
            case R.id.ivChange:
                String s = edtStart.getText().toString();
                edtStart.setText(edtEnd.getText().toString());
                edtEnd.setText(s);
                break;
            case R.id.ivLocationMe:
                if (!checkMe) {
                    ivLocationMe.setImageResource(R.drawable.bus_gps_blue);
                    ivLocationNew.setImageResource(R.drawable.bus_gps_gray);
                    checkMe = true;
                }
                if (mLatitude != 0 && mLongitude != 0) {
                    edtStart.setText("Vị trí của tôi");
                } else {
                    showToast("Không tìm thấy vị trí của bạn");
                }
                break;
            case R.id.ivLocationNew:
                if (checkMe) {
                    ivLocationNew.setImageResource(R.drawable.bus_gps_blue);
                    ivLocationMe.setImageResource(R.drawable.bus_gps_gray);
                    checkMe = false;
                }
                edtStart.setText("");
                break;
            case R.id.btnFindRoad:
                processRoute();
                if (checkMe) {
                    ivLocationNew.setImageResource(R.drawable.bus_gps_blue);
                    ivLocationMe.setImageResource(R.drawable.bus_gps_gray);
                    checkMe = false;
                } else {
                    ivLocationMe.setImageResource(R.drawable.bus_gps_blue);
                    ivLocationNew.setImageResource(R.drawable.bus_gps_gray);
                    checkMe = true;
                }
                break;
        }
    }

    private void processRoute() {
        TransparentProgressDialog progressDialog = new TransparentProgressDialog(this, R.drawable.ic_loading);
        progressDialog.show();
        String start = edtStart.getText().toString().trim();
        String end = edtEnd.getText().toString().trim();
        if (!start.isEmpty() && !end.isEmpty()) {
            if (mLatitude > 0 && mLongitude > 0) {
                if (start.equals("Vị trí của tôi")) {
                    latLngStart = new LatLng(mLatitude, mLongitude);
                    address_start = "Vị trí của tôi";
                } else {
                    if (getLocationNew(start) != null) {
                        Address address = getLocationNew(start);
                        latLngStart = new LatLng(address.getLatitude(), address.getLongitude());
                        address_start = address.getCountryName() + " - " + address.getFeatureName();
                    } else {
                        showToast("Không tìm thấy vị trí điểm bắt đầu");
                    }
                }
                if (end.equals("Vị trí của tôi")) {
                    latLngEnd = new LatLng(mLatitude, mLongitude);
                    address_end = "Vị trí của tôi";
                } else {
                    if (getLocationNew(end) != null) {
                        Address address = getLocationNew(end);
                        latLngEnd = new LatLng(address.getLatitude(), address.getLongitude());
                        address_end = address.getCountryName() + " - " + address.getFeatureName();
                    } else {
                        showToast("Không tìm thấy vị trí điểm kết thúc");
                    }
                }
                if (end.equals("Vị trí của tôi") && start.equals("Vị trí của tôi")) {
                    showToast("Không thể tìm thấy lộ trình tại hai điểm trùng nhau");
                }
            } else {
                if (getLocationNew(end) != null && getLocationNew(start) != null) {
                    Address address = getLocationNew(start);
                    latLngStart = new LatLng(address.getLatitude(), address.getLongitude());
                    address_start = address.getCountryName() + " - " + address.getFeatureName();
                    address = getLocationNew(end);
                    latLngEnd = new LatLng(address.getLatitude(), address.getLongitude());
                    address_end = address.getCountryName() + " - " + address.getFeatureName();
                } else {
                    showToast("Không tìm thấy vị trí hai điểm này");
                }
            }
            process(latLngStart.latitude, latLngStart.longitude, latLngEnd);
            edtStart.setText("");
            edtEnd.setText("");
        } else {
            showToast("Bạn chưa nhập đầy đủ thông tin lộ trình");
        }
        progressDialog.dismiss();
    }

    private void getLocationMe() {
        LocationManager locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
        Criteria criteria = new Criteria();

        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        Location location = locationManager.getLastKnownLocation(locationManager.getBestProvider(criteria, false));
        if (location != null) {
            mLatitude = location.getLatitude();
            mLongitude = location.getLongitude();
        }
    }

    private Address getLocationNew(String location) {
        List<Address> addressList = null;
        Geocoder geocoder = new Geocoder(this);

        try {
            addressList = geocoder.getFromLocationName(location, 1);
        } catch (IOException e) {
            showToast("Không tìm thấy địa điểm này");
            return null;
        }

        if (addressList.size() > 0) {
            Address address = addressList.get(0);
            return address;
        } else {
            showToast("Không tìm thấy địa điểm này");
            return null;
        }
    }

    private void process(double lat, double lng, LatLng latLng) {
        placeStartArrayList = searchBusStationNearMe(lat, lng);
        if (placeStartArrayList != null && placeStartArrayList.size() > 0) {
            for (int i = 0; i < placeStartArrayList.size(); i++) {
                double distance = calculationByDistance(new LatLng(lat, lng), new LatLng(placeStartArrayList.get(i).getLat(), placeStartArrayList.get(i).getLng()));
                placeStartArrayList.get(i).setDistance(distance + "");
                placeStartArrayList.get(i).setAddress_end(address_end);
                placeStartArrayList.get(i).setAddress_start(address_start);
            }
            Log.i("START", placeStartArrayList.toString());
        } else {
            showToast("Không tìm thấy điểm xe buýt nào cạnh bạn");
        }

        placeEndArrayList = searchBusStationNearMe(latLng.latitude, latLng.longitude);
        if (placeEndArrayList != null && placeEndArrayList.size() > 0) {
            for (int i = 0; i < placeEndArrayList.size(); i++) {
                double distance = calculationByDistance(new LatLng(latLng.latitude, latLng.longitude), new LatLng(placeEndArrayList.get(i).getLat(), placeEndArrayList.get(i).getLng()));
                placeEndArrayList.get(i).setDistance(distance + "");
                placeEndArrayList.get(i).setAddress_end(address_end);
                placeEndArrayList.get(i).setAddress_start(address_start);
            }
            Log.i("END", placeEndArrayList.toString());
        } else {
            showToast("Không tìm thấy điểm xe buýt nào cạnh bạn");
        }

        ArrayList<String> list = new ArrayList<String>();
        String array = "";
        for (int i = 0; i < placeStartArrayList.size(); i++) {
            for (int j = 0; j < placeEndArrayList.size(); j++) {
                array = processString(placeStartArrayList.get(i).getName(), placeEndArrayList.get(j).getName());
                if (!array.isEmpty()) {
                    if (list.indexOf(array) < 0) {
                        list.add(array);
                        Route route = new Route();
                        route.setPlaceStart(placeStartArrayList.get(i));
                        route.setPlaceEnd(placeEndArrayList.get(j));
                        route.setBusID(array);
                        routeArrayList.add(route);
                        Log.i("PLACE-BUS", placeStartArrayList.get(i).getName() + " - " + placeEndArrayList.get(j).getName());
                    }
                }
            }
        }
        recommenedRouteAdapter.notifyDataSetChanged();

    }

    private ArrayList<Place> searchBusStationNearMe(double lat, double lng) {
        StringBuilder googlePlacesUrl = new StringBuilder(Contants.PLACES_SEARCH_URL);
        googlePlacesUrl.append("location=" + lat + "," + lng);
        googlePlacesUrl.append("&radius=" + Contants.PROXIMITY_RADIUS);
        googlePlacesUrl.append("&types=bus_station");
        googlePlacesUrl.append("&sensor=false");
        googlePlacesUrl.append("&key=" + Contants.GOOGLE_API_KEY);

        try {
            LoadDataBusStation loadDataBusStation = new LoadDataBusStation();
            String station = loadDataBusStation.execute(googlePlacesUrl.toString()).get();
            JSONObject googlePlacesJson = new JSONObject(station);
            Places placeJsonParser = new Places();
            List<HashMap<String, String>> googlePlacesList = placeJsonParser.parse(googlePlacesJson);
            ArrayList<Place> placeArrayList = new ArrayList<Place>();
            for (int i = 0; i < googlePlacesList.size(); i++) {
                HashMap<String, String> googlePlace = googlePlacesList.get(i);
                Place place = new Place();
                double latute = Double.parseDouble(googlePlace.get("lat"));
                double lngute = Double.parseDouble(googlePlace.get("lng"));
                place.setLat(latute);
                place.setLng(lngute);
                place.setName(googlePlace.get("place_name"));
                place.setVicinity(googlePlace.get("vicinity"));
                placeArrayList.add(place);
            }
            return placeArrayList;
        } catch (InterruptedException e) {
            showToast("Không tìm thấy điểm xe buýt nào cạnh bạn");
            return null;
        } catch (ExecutionException e) {
            showToast("Không tìm thấy điểm xe buýt nào cạnh bạn");
            return null;
        } catch (JSONException e) {
            showToast("Không tìm thấy điểm xe buýt nào cạnh bạn");
            return null;
        }
    }

    private String processString(String s1, String s2) {
        String s = "";
        String[] temp = s1.split("[,]");
        for (int i = 0; i < temp.length; i++) {
            if (s2.indexOf(temp[i].trim()) > -1) {
                s += temp[i].trim() + " ";
            }
        }
        return s;
    }

    public double calculationByDistance(LatLng StartP, LatLng EndP) {
        int Radius = 6371;
        double lat1 = StartP.latitude;
        double lat2 = EndP.latitude;
        double lon1 = StartP.longitude;
        double lon2 = EndP.longitude;
        double dLat = Math.toRadians(lat2 - lat1);
        double dLon = Math.toRadians(lon2 - lon1);
        double a = Math.sin(dLat / 2) * Math.sin(dLat / 2)
                + Math.cos(Math.toRadians(lat1))
                * Math.cos(Math.toRadians(lat2)) * Math.sin(dLon / 2)
                * Math.sin(dLon / 2);
        double c = 2 * Math.asin(Math.sqrt(a));
        return Radius * c;
    }

    private void showToast(String msg) {
        toast = Toast.makeText(getApplicationContext(), msg, Toast.LENGTH_SHORT);
        toastView = toast.getView();
        toastView.setBackgroundResource(R.drawable.border_toast);
        toast.show();
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);
        menu.setHeaderTitle("Tùy chọn");
        menu.setHeaderIcon(R.drawable.ic_setting);
        getMenuInflater().inflate(R.menu.context_menu, menu);
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        AdapterView.AdapterContextMenuInfo menuInfo = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();
        int pos = menuInfo.position;
        Route route = (Route) recommenedRouteAdapter.getItem(pos);

        switch (item.getItemId()) {
            case R.id.item_view:
                Intent intent = new Intent(FindRouteActivity.this, RouteDetailActivity.class);
                Bundle bundle = new Bundle();
                bundle.putSerializable("route", route);
                intent.putExtras(bundle);
                startActivity(intent);
                break;
            case R.id.item_display:
                Intent intent2 = getIntent();
                Bundle bundle2 = new Bundle();
                bundle2.putDouble("latStart", latLngStart.latitude);
                bundle2.putDouble("latEnd", latLngEnd.latitude);
                bundle2.putDouble("lngStart", latLngStart.longitude);
                bundle2.putDouble("lngEnd", latLngEnd.longitude);
                bundle2.putString("address_start", address_start);
                bundle2.putString("address_end", address_end);
                bundle2.putSerializable("route_bus", route);
                intent2.putExtras(bundle2);
                setResult(103, intent2);
                finish();
                break;

        }
        return super.onContextItemSelected(item);
    }

    private class LoadDataBusStation extends AsyncTask<String, Void, String> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            Log.i("JSON-PLACE", s);
        }

        @Override
        protected String doInBackground(String... params) {
            String data = "";
            try {
                RequestHandler http = new RequestHandler();
                data = http.read(params[0]);
            } catch (Exception e) {
                Log.d("Background Task", e.toString());
            }
            return data;
        }
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
            edtStart.setAdapter(adapter);
        }
    }

    private class PlacesTaskEnd extends AsyncTask<String, Void, String> {

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
            parserTaskEnd = new ParserTaskEnd();
            parserTaskEnd.execute(result);
        }
    }

    private class ParserTaskEnd extends AsyncTask<String, Integer, List<HashMap<String, String>>> {
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
            edtEnd.setAdapter(adapter);
        }
    }
}
