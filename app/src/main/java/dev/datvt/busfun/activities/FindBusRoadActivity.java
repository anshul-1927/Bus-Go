package dev.datvt.busfun.activities;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.graphics.Typeface;
import android.location.Address;
import android.location.Criteria;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.app.ActivityCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.ContextMenu;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;

import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.ExecutionException;

import dev.datvt.busfun.R;
import dev.datvt.busfun.models.Bus;
import dev.datvt.busfun.models.BusStation;
import dev.datvt.busfun.models.Route;
import dev.datvt.busfun.utils.Contants;
import dev.datvt.busfun.utils.DataHandler;
import dev.datvt.busfun.utils.DirectionsJSONParser;
import dev.datvt.busfun.utils.GooglePlacesReadTask;
import dev.datvt.busfun.utils.RequestHandler;
import dev.datvt.busfun.utils.TransparentProgressDialog;

/**
 * Created by datvt on 5/5/2016.
 */
public class FindBusRoadActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener, View.OnClickListener, LocationListener {


    private DrawerLayout drawer;
    private NavigationView navigationView;

    private GoogleMap googleMap;
    private TransparentProgressDialog progressDialog;
    private double mLatitude = 0;
    private double mLongitude = 0;
    private int PROXIMITY_RADIUS = 500;

    private TextView tvNotify, tvDetail, tvSelect;
    private ImageView btnMode, btnDetail;
    private View toastView;
    private Toast toast;

    private DataHandler mDataHandler;
    private SQLiteDatabase database = null;

    private List<HashMap<String, String>> hashMapList;
    private ArrayList<BusStation> busStationsGo = null;
    private ArrayList<BusStation> busStationsBack = null;
    private ArrayList<LatLng> travelBusGo = null;
    private ArrayList<LatLng> travelBusBack = null;

    private Bus myBus;
    private BusStation myBusStation;
    private String[] locationGoRoute = null;
    private String[] locationReturnRoute = null;
    private String[] codeGoBusStop = null;
    private String[] codeReturnBusStop = null;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (!isGooglePlayServicesAvailable()) {
            finish();
        }

        setContentView(R.layout.find_road_bus_main);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);

        drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        getForWidgets();
        getData();
        displayMapGoogle();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mDataHandler.close();
    }

    private boolean isGooglePlayServicesAvailable() {
        int status = GooglePlayServicesUtil.isGooglePlayServicesAvailable(this);
        if (ConnectionResult.SUCCESS == status) {
            return true;
        } else {
            GooglePlayServicesUtil.getErrorDialog(status, this, 0).show();
            return false;
        }
    }

    private void getForWidgets() {
        mDataHandler = new DataHandler(this);
        mDataHandler.open();
        database = mDataHandler.getMyDatabase();

        tvNotify = (TextView) findViewById(R.id.tvNotify);
        tvNotify.setSelected(true);
        tvDetail = (TextView) findViewById(R.id.tvDetail);
        tvSelect = (TextView) findViewById(R.id.tvSelect);

        btnMode = (ImageView) findViewById(R.id.btnMode);
        btnDetail = (ImageView) findViewById(R.id.btnDetail);
        btnDetail.setOnClickListener(this);

        registerForContextMenu(btnMode);

        busStationsGo = new ArrayList<BusStation>();
        busStationsBack = new ArrayList<BusStation>();
        travelBusGo = new ArrayList<LatLng>();
        travelBusBack = new ArrayList<LatLng>();
    }

    private void displayMapGoogle() {
        if (hasConnection()) {
            progressDialog = new TransparentProgressDialog(this, R.drawable.ic_dialog);
            progressDialog.setCancelable(false);
            progressDialog.show();

            googleMap = ((MapFragment) getFragmentManager().findFragmentById(R.id.maps)).getMap();

            googleMap.setOnMapLoadedCallback(new GoogleMap.OnMapLoadedCallback() {
                @Override
                public void onMapLoaded() {
                    progressDialog.dismiss();
                }
            });

            googleMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
            googleMap.getUiSettings().setZoomControlsEnabled(true);
            googleMap.getUiSettings().setMapToolbarEnabled(false);
            googleMap.setMyLocationEnabled(true);

            if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                    && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                return;
            }

            googleMap.setOnMyLocationButtonClickListener(new GoogleMap.OnMyLocationButtonClickListener() {
                @Override
                public boolean onMyLocationButtonClick() {
                    googleMap.clear();
                    LocationOfMe();
                    return false;
                }
            });

            MarkerOptions option = new MarkerOptions();
            option.position(new LatLng(21.009447, 105.828663));
            option.title("Học viện ngân hàng").snippet("Nơi tình yêu bắt đầu");
            option.icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_location_girl_friend));
            Marker maker = googleMap.addMarker(option);
            maker.showInfoWindow();
            googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(maker.getPosition(), 15));

            googleMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
                @Override
                public boolean onMarkerClick(Marker marker) {
                    marker.showInfoWindow();
                    googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(marker.getPosition(), 15));
                    googleMap.animateCamera(CameraUpdateFactory.zoomIn());
                    return false;
                }
            });

            loadTravelBusGo();
            loadTravelBusReturn();
        } else {
            showToast("Vui lòng kiểm tra kết nối internet");
        }
    }

    private void getData() {
        Intent intent = getIntent();
        Bundle bundle = intent.getExtras();
        myBus = new Bus();
        myBus.setId(bundle.getString("ID"));
        loadGoRoutePathGeo(myBus.getId());
        Log.i("GO_ROUTE_PATH", myBus.getGoRoutePath());
        getRoutePath();
    }

    private void shareApp() {
        Intent shareIntent = new Intent(Intent.ACTION_SEND);
        shareIntent.setType("text/plain");
        shareIntent.putExtra(Intent.EXTRA_TEXT, "Ứng dụng có hiệu quả cao");
        shareIntent.putExtra(Intent.EXTRA_SUBJECT, "Phản hồi về Bus Funny");
        startActivity(Intent.createChooser(shareIntent, "Chia sẻ"));
    }

    private void openGooglePlay() {
        final String appPackageName = getPackageName();
        try {
            startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=" + appPackageName)));
        } catch (android.content.ActivityNotFoundException anfe) {
            startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id=" + appPackageName)));
        }
    }

    private void searchLocation(String location) {
        TransparentProgressDialog progressDialog = new TransparentProgressDialog(this, R.drawable.ic_loading_1);
        progressDialog.show();
        List<Address> addressList = null;
        if (location != null && !location.isEmpty()) {
            Geocoder geocoder = new Geocoder(this);
            try {
                addressList = geocoder.getFromLocationName(location, 1);
                Log.i("LOCATION", addressList.toString());
            } catch (Exception e) {
                showToast("Không tìm thấy địa điểm này");
            }
            if (addressList.size() > 0) {
                Address address = addressList.get(0);
                LatLng latLng = new LatLng(address.getLatitude(), address.getLongitude());
                Marker marker = googleMap.addMarker(new MarkerOptions()
                        .position(latLng).title(location).snippet(address.getCountryName() + " - " + address.getFeatureName())
                        .icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_map_park)));
                marker.showInfoWindow();
                googleMap.animateCamera(CameraUpdateFactory.newLatLng(latLng));
            } else {
                showToast("Không tìm thấy địa điểm này");
            }
        } else {
            showToast("Không tìm thấy địa điểm này");
        }
        progressDialog.dismiss();
    }

    private void searchRoute(List<HashMap<String, String>> hashMapList) {
        googleMap.clear();
        HashMap<String, String> hashMap = hashMapList.get(0);
        String[] pos = hashMap.get("location").split("[|]");
        String[] name = hashMap.get("name").split("[|]");
        hashMap = hashMapList.get(1);
        String[] pos1 = hashMap.get("location").split("[|]");
        String[] name1 = hashMap.get("name").split("[|]");
        hashMap = hashMapList.get(2);
        String[] pos2 = hashMap.get("location").split("[|]");
        String[] name2 = hashMap.get("name").split("[|]");
        hashMap = hashMapList.get(3);
        String[] pos3 = hashMap.get("location").split("[|]");
        String[] name3 = hashMap.get("name").split("[|]");

        double posX = Double.parseDouble(pos[0].trim());
        double posY = Double.parseDouble(pos[1].trim());
        LatLng latLng = new LatLng(posX, posY);

        double posX1 = Double.parseDouble(pos1[0].trim());
        double posY1 = Double.parseDouble(pos1[1].trim());
        LatLng latLng1 = new LatLng(posX1, posY1);

        double posX2 = Double.parseDouble(pos2[0].trim());
        double posY2 = Double.parseDouble(pos2[1].trim());
        LatLng latLng2 = new LatLng(posX2, posY2);

        double posX3 = Double.parseDouble(pos3[0].trim());
        double posY3 = Double.parseDouble(pos3[1].trim());
        LatLng latLng3 = new LatLng(posX3, posY3);

        MarkerOptions options = new MarkerOptions();
        options.position(latLng);
        options.title(name[0]);
        options.snippet(name[1]);
        options.icon(BitmapDescriptorFactory.fromResource(R.drawable.startpoint));
        Marker marker = googleMap.addMarker(options);
        marker.showInfoWindow();
        googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 15));
        String url = getDirectionsUrlWalk(latLng, latLng2);
        LoadRouteWalk loadRouteWalk = new LoadRouteWalk();
        loadRouteWalk.execute(url);

        options = new MarkerOptions();
        options.position(latLng2);
        options.title(name2[1]);
        options.snippet(name2[0]);
        options.icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_map_station));
        googleMap.addMarker(options);
        options = new MarkerOptions();
        options.position(latLng3);
        options.title(name3[1]);
        options.snippet(name3[0]);
        options.icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_map_station));
        googleMap.addMarker(options);
        url = getDirectionsUrl(latLng2, latLng3);
        LoadRoute loadRoute = new LoadRoute();
        loadRoute.execute(url);

        options = new MarkerOptions();
        options.position(latLng1);
        options.title(name1[0]);
        options.snippet(name1[1]);
        options.icon(BitmapDescriptorFactory.fromResource(R.drawable.location_end));
        googleMap.addMarker(options);
        url = getDirectionsUrlWalk(latLng3, latLng1);
        loadRouteWalk = new LoadRouteWalk();
        loadRouteWalk.execute(url);
    }

    private void searchBusStationNearMe() {
        if (mLatitude > 0 && mLongitude > 0) {
            TransparentProgressDialog progressDialog = new TransparentProgressDialog(this, R.drawable.ic_loading);
            progressDialog.show();
            StringBuilder googlePlacesUrl = new StringBuilder(Contants.PLACE_URL);
            googlePlacesUrl.append("location=" + mLatitude + "," + mLongitude);
            googlePlacesUrl.append("&radius=" + PROXIMITY_RADIUS);
            googlePlacesUrl.append("&types=bus_station");
            googlePlacesUrl.append("&sensor=true");
            googlePlacesUrl.append("&key=" + Contants.GOOGLE_API_KEY);

            GooglePlacesReadTask googlePlacesReadTask = new GooglePlacesReadTask();
            Object[] toPass = new Object[2];
            toPass[0] = googleMap;
            toPass[1] = googlePlacesUrl.toString();
            try {
                String s = googlePlacesReadTask.execute(toPass).get();
                if (s == null) {
                    showToast("Không tìm thấy điểm xe buýt nào cạnh bạn");
                }
            } catch (InterruptedException e) {
                showToast("Không thể tìm kiếm");
            } catch (ExecutionException e) {
                showToast("Không thể tìm kiếm");
            }
            progressDialog.dismiss();
        } else {
            showToast("Không tìm thấy vị trí của bạn");
        }
    }

    public boolean hasConnection() {
        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);

        NetworkInfo wifiNetwork = cm.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
        if (wifiNetwork != null && wifiNetwork.isConnected()) {
            return true;
        }

        NetworkInfo mobileNetwork = cm.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);
        if (mobileNetwork != null && mobileNetwork.isConnected()) {
            return true;
        }

        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        if (activeNetwork != null && activeNetwork.isConnected()) {
            return true;
        }

        return false;
    }

    private void displayLocationOnMap(BusStation busStation) {
        TransparentProgressDialog progressDialog = new TransparentProgressDialog(this, R.drawable.ic_loading);
        progressDialog.show();
        googleMap.clear();
        MarkerOptions options = new MarkerOptions();
        String[] s = busStation.getLocation().split("[|]");
        double lat = Double.parseDouble(s[1]);
        double lng = Double.parseDouble(s[0]);
        options.position(new LatLng(lat, lng));
        options.icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_map_station));
        options.title(busStation.getName());
        options.snippet(busStation.getBusPassList());
        Marker marker = googleMap.addMarker(options);
        marker.showInfoWindow();
        googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(marker.getPosition(), 15));
        progressDialog.dismiss();
    }

    private void showToast(String msg) {
        toast = Toast.makeText(getApplicationContext(), msg, Toast.LENGTH_SHORT);
        toastView = toast.getView();
        toastView.setBackgroundResource(R.drawable.border_toast);
        toast.show();
    }

    private void loadGoRoutePathGeo(String id) {
        String sql = "SELECT * FROM BusLine WHERE _id = " + id;
        Cursor c = mDataHandler.selectSQL(sql);
        c.moveToFirst();
        while (!c.isAfterLast()) {
            myBus.setGoRoutePath(c.getString(13));
            myBus.setReturnRoutePath(c.getString(14));
            myBus.setGoRouteThroughStop(c.getString(15));
            myBus.setReturnRouteThroughStop(c.getString(16));
            c.moveToNext();
        }
        c.close();
    }

    private void getRoutePath() {
        double posX;
        double posY;
        locationGoRoute = myBus.getGoRoutePath().trim().split(" ");
        locationReturnRoute = myBus.getReturnRoutePath().trim().split(" ");

        Log.i("LENGTH_GO", locationGoRoute.length + "");

        for (int i = 0; i < locationGoRoute.length; i++) {
            String[] temp = locationGoRoute[i].split("[|]");

            posY = Double.valueOf(temp[0]);
            posX = Double.valueOf(temp[1]);
            travelBusGo.add(new LatLng(posX, posY));
        }

        for (int i = 0; i < locationReturnRoute.length; i++) {
            String[] temp = locationReturnRoute[i].split("[|]");
            posY = Double.valueOf(temp[0]);
            posX = Double.valueOf(temp[1]);
            travelBusBack.add(new LatLng(posX, posY));
        }

        codeGoBusStop = myBus.getGoRouteThroughStop().trim().split(" ");
        codeReturnBusStop = myBus.getReturnRouteThroughStop().trim().split(" ");

        for (int i = 0; i < codeGoBusStop.length; i++) {
            loadDataBusStation(codeGoBusStop[i]);
            busStationsGo.add(myBusStation);
        }

        for (int i = 0; i < codeReturnBusStop.length; i++) {
            loadDataBusStation(codeReturnBusStop[i]);
            busStationsBack.add(myBusStation);
        }
    }

    private void loadDataBusStation(String code) {
        String sql = "SELECT DISTINCT * FROM BusStop WHERE code = '" + code + "'";
        Cursor c = mDataHandler.selectSQL(sql);
        c.moveToFirst();
        while (!c.isAfterLast()) {
            myBusStation = new BusStation(c.getString(0), c.getString(3), c.getString(2), c.getString(1), c.getString(4));
            c.moveToNext();
        }
        c.close();
    }

    public void LocationOfMe() {
        LocationManager locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
        Criteria criteria = new Criteria();

        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        Location location = locationManager.getLastKnownLocation(locationManager.getBestProvider(criteria, true));
        if (location != null) {
            onLocationChanged(location);
        }

        locationManager.requestLocationUpdates(locationManager.getBestProvider(criteria, true), 20000, 0, this);
    }

    private void loadTravelBusGo() {
        Log.i("SIZE-Go ", travelBusGo.size() + "");
        if (travelBusGo.size() > 0) {
            PolylineOptions polylineOptions = new PolylineOptions();
            polylineOptions.addAll(travelBusGo);

            for (int i = 0; i < codeGoBusStop.length; i++) {
                loadDataBusStation(codeGoBusStop[i]);
                String[] loca = myBusStation.getLocation().split("[|]");
                double positionY = Double.valueOf(loca[0]);
                double positionX = Double.valueOf(loca[1]);
                MarkerOptions markerOptions = new MarkerOptions();
                markerOptions.position(new LatLng(positionX, positionY))
                        .title(myBusStation.getName())
                        .snippet(myBusStation.getBusPassList());
                if (i == 0) {
                    markerOptions.icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_location_start));
                } else if (i == codeGoBusStop.length - 1) {
                    markerOptions.icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_location_end));
                } else {
                    markerOptions.icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_bus_station_go));
                }
                if (i < 1) {
                    Marker marker = googleMap.addMarker(markerOptions);
                    marker.showInfoWindow();
                    googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(marker.getPosition(), 15));
                } else {
                    googleMap.addMarker(markerOptions);
                }
            }

            polylineOptions.color(Color.GREEN);
            polylineOptions.width(10);
            googleMap.addPolyline(polylineOptions);
        } else {
            showToast("Không tìm thấy đường đi");
        }
    }

    private void loadTravelBusReturn() {
        Log.i("SIZE-Return ", travelBusBack.size() + "");
        if (travelBusBack.size() > 0) {
            PolylineOptions polylineOptions = new PolylineOptions();
            polylineOptions.addAll(travelBusBack);

            for (int i = 0; i < codeReturnBusStop.length; i++) {
                loadDataBusStation(codeReturnBusStop[i]);
                String[] loca = myBusStation.getLocation().split("[|]");
                double positionY = Double.valueOf(loca[0]);
                double positionX = Double.valueOf(loca[1]);
                MarkerOptions markerOptions = new MarkerOptions();
                markerOptions.position(new LatLng(positionX, positionY))
                        .title(myBusStation.getName())
                        .snippet(myBusStation.getBusPassList());
                if (i == 0) {
                    markerOptions.icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_location_start));
                } else if (i == codeReturnBusStop.length - 1) {
                    markerOptions.icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_location_end));
                } else {
                    markerOptions.icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_bus_station_return));
                }
                if (i < 1) {
                    Marker marker = googleMap.addMarker(markerOptions);
                    marker.showInfoWindow();
                    googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(marker.getPosition(), 15));
                } else {
                    googleMap.addMarker(markerOptions);
                }
            }

            polylineOptions.color(Color.RED);
            polylineOptions.width(10);
            googleMap.addPolyline(polylineOptions);
        } else {
            showToast("Không tìm thấy đường đi");
        }
    }

    private String getDirectionsUrl(LatLng origin, LatLng dest) {
        String str_origin = "origin=" + origin.latitude + "," + origin.longitude;
        String str_dest = "destination=" + dest.latitude + "," + dest.longitude;
        String sensor = "sensor=false";
        String mode = "units=metric&mode=google.maps.TravelMode.TRANSIT&vehicle.type=VehicleType.BUS";
        String parameters = str_origin + "&" + str_dest + "&" + sensor + "&" + mode;
        String output = "json";
        String url = Contants.DIRECTIONS_URL + output + "?" + parameters;
        return url;
    }

    private String getDirectionsUrlWalk(LatLng origin, LatLng dest) {
        String str_origin = "origin=" + origin.latitude + "," + origin.longitude;
        String str_dest = "destination=" + dest.latitude + "," + dest.longitude;
        String sensor = "sensor=false";
        String mode = "units=metric&mode=walking";
        String parameters = str_origin + "&" + str_dest + "&" + sensor + "&" + mode;
        String output = "json";
        String url = Contants.DIRECTIONS_URL + output + "?" + parameters;
        return url;
    }

    @Override
    public void onBackPressed() {
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (hasConnection()) {
            if (id == R.id.nav_map) {
                item.setChecked(true);
                finish();
            }  else if (id == R.id.nav_bus_system) {
                item.setChecked(true);
                Intent intent_station = new Intent(FindBusRoadActivity.this, BusSystemActivity.class);
                startActivityForResult(intent_station, Contants.REQUEST_BUS_STATION);
            }  else if (id == R.id.nav_search_bus_station_nearest_me) {
                googleMap.clear();
                LocationOfMe();
                searchBusStationNearMe();
            } else if (id == R.id.nav_search_location) {
                item.setChecked(true);
                Intent intent_location = new Intent(FindBusRoadActivity.this, FindLocation.class);
                startActivityForResult(intent_location, Contants.REQUEST_LOCATION);
            } else if (id == R.id.nav_find_road) {
                item.setChecked(true);
                Intent intent_route = new Intent(FindBusRoadActivity.this, FindRouteActivity.class);
                startActivityForResult(intent_route, Contants.REQUEST_ROUTE);
            } else if (id == R.id.nav_profile) {
                item.setChecked(true);
                startActivity(new Intent(FindBusRoadActivity.this, ProfileActivity.class));
            } else if (id == R.id.nav_share) {
                item.setChecked(true);
                shareApp();
            } else if (id == R.id.nav_vote) {
                item.setChecked(true);
                openGooglePlay();
            } else if (id == R.id.nav_read_new) {
                item.setChecked(true);
                startActivity(new Intent(FindBusRoadActivity.this, PaperActivity.class));
                finish();
            }
        } else {
            showToast("Vui lòng kiểm tra kết nối internet");
        }
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == Contants.REQUEST_LOCATION && resultCode == 104 && data != null) {
            Bundle bundle = data.getExtras();
            String s = bundle.getString("location");
            searchLocation(s);
        }

        if (requestCode == Contants.REQUEST_ROUTE && resultCode == 103 && data != null) {
            Bundle bundle_route = data.getExtras();
            hashMapList = new ArrayList<HashMap<String, String>>();
            HashMap<String, String> hashMap = new HashMap<String, String>();
            hashMap.put("name", bundle_route.getString("address_start") + "|" + " ");
            Log.i("start-address", bundle_route.getString("address_start"));
            hashMap.put("location", bundle_route.getDouble("latStart") + "|" + bundle_route.getDouble("lngStart"));
            hashMapList.add(hashMap);

            hashMap = new HashMap<String, String>();
            hashMap.put("name", bundle_route.getString("address_end") + "|" + " ");
            Log.i("end-address", bundle_route.getString("address_end"));
            hashMap.put("location", bundle_route.getDouble("latEnd") + "|" + bundle_route.getDouble("lngEnd"));
            hashMapList.add(hashMap);

            Route route = (Route) bundle_route.getSerializable("route_bus");
            hashMap = new HashMap<String, String>();
            hashMap.put("name", route.getPlaceStart().getName() + "|" + route.getPlaceStart().getVicinity());
            Log.i("address_1", route.getPlaceStart().getName() + "|" + route.getPlaceStart().getVicinity());
            hashMap.put("location", route.getPlaceStart().getLat() + "|" + route.getPlaceStart().getLng());
            hashMapList.add(hashMap);

            hashMap = new HashMap<String, String>();
            hashMap.put("name", route.getPlaceEnd().getName() + "|" + route.getPlaceEnd().getVicinity());
            Log.i("address_2", route.getPlaceEnd().getName() + "|" + route.getPlaceEnd().getVicinity());
            hashMap.put("location", route.getPlaceEnd().getLat() + "|" + route.getPlaceEnd().getLng());
            hashMapList.add(hashMap);

            searchRoute(hashMapList);
        }

        if (requestCode == Contants.REQUEST_BUS_STATION && resultCode == 108 && data != null) {
            Bundle bundle_station = data.getExtras();
            BusStation station = (BusStation) bundle_station.getSerializable("station");
            Log.i("STATION2", station.toString());
            displayLocationOnMap(station);
        }

        if (requestCode == Contants.REQUEST_BUS_SALE && resultCode == 106 && data != null) {
            Bundle bundle_station_sale = data.getExtras();
            BusStation station = (BusStation) bundle_station_sale.getSerializable("station_sale");
            displayLocationOnMap(station);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.type_maps_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (hasConnection()) {
            int type = GoogleMap.MAP_TYPE_NORMAL;
            switch (id) {
                case R.id.item_map_none:
                    type = GoogleMap.MAP_TYPE_NONE;
                    break;
                case R.id.item_map_hybrid:
                    type = GoogleMap.MAP_TYPE_HYBRID;
                    break;
                case R.id.item_map_normal:
                    type = GoogleMap.MAP_TYPE_NORMAL;
                    break;
                case R.id.item_map_satellite:
                    type = GoogleMap.MAP_TYPE_SATELLITE;
                    break;
                case R.id.item_map_terrain:
                    type = GoogleMap.MAP_TYPE_TERRAIN;
                    break;
            }
            googleMap.setMapType(type);
        } else {
            showToast("Vui lòng kiểm tra kết nối internet");
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);
        getMenuInflater().inflate(R.menu.main, menu);
        menu.setHeaderTitle("Chế độ hiển thị");
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        if (hasConnection()) {
            switch (item.getItemId()) {
                case R.id.item_go:
                    if (busStationsGo.size() > 0) {
                        googleMap.clear();
                        loadTravelBusGo();
                    }
                    break;
                case R.id.item_back:
                    googleMap.clear();
                    loadTravelBusReturn();
                    break;
                case R.id.item_all:
                    googleMap.clear();
                    loadTravelBusGo();
                    loadTravelBusReturn();
                    break;
            }
        } else {
            showToast("Vui lòng kiểm tra kết nối internet");
        }
        return super.onContextItemSelected(item);
    }

    @Override
    public void onClick(View v) {
        if (v == btnDetail) {
            Intent intent_detail = new Intent(FindBusRoadActivity.this, DetailBusActivity.class);
            Bundle bundle_detail = new Bundle();
            bundle_detail.putSerializable("bus", myBus);
            intent_detail.putExtras(bundle_detail);
            startActivity(intent_detail);
        }
    }

    @Override
    public void onLocationChanged(Location location) {
        mLatitude = location.getLatitude();
        mLongitude = location.getLongitude();
        LatLng latLng = new LatLng(mLatitude, mLongitude);
        MarkerOptions option = new MarkerOptions();
        option.title("Chỗ tôi đang ở đó");
        option.snippet("Nơi khởi nguồn của tài năng Việt");
        option.position(latLng);
        option.icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_map_person_walking));
        Marker currentMarker = googleMap.addMarker(option);
        currentMarker.showInfoWindow();
        googleMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));
        googleMap.animateCamera(CameraUpdateFactory.zoomTo(15));
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {

    }

    @Override
    public void onProviderEnabled(String provider) {

    }

    @Override
    public void onProviderDisabled(String provider) {

    }


    private class LoadRoute extends AsyncTask<String, Void, String> {

        TransparentProgressDialog progressDialog;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressDialog = new TransparentProgressDialog(FindBusRoadActivity.this, R.drawable.ic_loading_1);
            progressDialog.setCancelable(false);
            progressDialog.show();
        }

        @Override
        protected String doInBackground(String... url) {

            String data = "";

            try {
                RequestHandler http = new RequestHandler();
                data = http.read(url[0]);
            } catch (Exception e) {
                Log.d("Background Task", e.toString());
            }
            return data;
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            Parser parserTask = new Parser();
            parserTask.execute(result);
            progressDialog.dismiss();
        }
    }

    private class Parser extends AsyncTask<String, Integer, List<List<HashMap<String, String>>>> {

        @Override
        protected List<List<HashMap<String, String>>> doInBackground(String... jsonData) {
            JSONObject jObject;
            List<List<HashMap<String, String>>> routes = null;

            try {
                jObject = new JSONObject(jsonData[0]);
                DirectionsJSONParser parser = new DirectionsJSONParser();
                routes = parser.parse(jObject);
            } catch (Exception e) {
                e.printStackTrace();
            }
            return routes;
        }

        @Override
        protected void onPostExecute(List<List<HashMap<String, String>>> result) {
            ArrayList<LatLng> points = null;
            PolylineOptions lineOptions = null;

            for (int i = 0; i < result.size(); i++) {
                points = new ArrayList<LatLng>();
                lineOptions = new PolylineOptions();
                List<HashMap<String, String>> path = result.get(i);
                for (int j = 0; j < path.size(); j++) {
                    HashMap<String, String> point = path.get(j);
                    double lat = Double.parseDouble(point.get("lat"));
                    double lng = Double.parseDouble(point.get("lng"));
                    LatLng position = new LatLng(lat, lng);

                    points.add(position);
                }
                lineOptions.addAll(points);
                lineOptions.width(10);
                lineOptions.color(Color.BLUE);
            }
            googleMap.addPolyline(lineOptions);
        }
    }

    private class LoadRouteWalk extends AsyncTask<String, Void, String> {

        TransparentProgressDialog progressDialog;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressDialog = new TransparentProgressDialog(FindBusRoadActivity.this, R.drawable.ic_loading_1);
            progressDialog.setCancelable(false);
            progressDialog.show();
        }

        @Override
        protected String doInBackground(String... url) {
            String data = "";
            try {
                RequestHandler http = new RequestHandler();
                data = http.read(url[0]);
            } catch (Exception e) {
                Log.d("Background Task", e.toString());
            }
            return data;
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            ParserWalk parserTaskWalk = new ParserWalk();
            parserTaskWalk.execute(result);
            progressDialog.dismiss();
        }
    }

    private class ParserWalk extends AsyncTask<String, Integer, List<List<HashMap<String, String>>>> {

        @Override
        protected List<List<HashMap<String, String>>> doInBackground(String... jsonData) {
            JSONObject jObject;
            List<List<HashMap<String, String>>> routes = null;
            try {
                jObject = new JSONObject(jsonData[0]);
                DirectionsJSONParser parser = new DirectionsJSONParser();
                routes = parser.parse(jObject);
            } catch (Exception e) {
                e.printStackTrace();
            }
            return routes;
        }

        @Override
        protected void onPostExecute(List<List<HashMap<String, String>>> result) {
            ArrayList<LatLng> points = null;
            PolylineOptions lineOptions = null;
            for (int i = 0; i < result.size(); i++) {
                points = new ArrayList<LatLng>();
                lineOptions = new PolylineOptions();
                List<HashMap<String, String>> path = result.get(i);
                for (int j = 0; j < path.size(); j++) {
                    HashMap<String, String> point = path.get(j);
                    double lat = Double.parseDouble(point.get("lat"));
                    double lng = Double.parseDouble(point.get("lng"));
                    LatLng position = new LatLng(lat, lng);

                    points.add(position);
                }
                lineOptions.addAll(points);
                lineOptions.width(10);
                lineOptions.color(Color.GRAY);
            }
            googleMap.addPolyline(lineOptions);
        }
    }
}
