package com.googlemaps.template.myapplication;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.googlemaps.template.myapplication.model.Locality;

import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.BasicResponseHandler;
import org.apache.http.impl.client.DefaultHttpClient;

import java.io.IOException;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity implements OnMapReadyCallback {
    GoogleMap mMap;
    GPSTracker gps;

    private double lat=0;
    private double lng=0;

    private String httpAnswer="nothing";

    private ArrayList<Locality> mLocalities = new ArrayList<>();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        takeGPSLocation();

        makeGetRequestOnNewThread();



    }

    @Override
    public void onMapReady(GoogleMap map) {
        mMap=map;
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(
                new LatLng(lat, lng), 8));
        mMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
        for (int i=0;i<mLocalities.size();i++)
        mMap.addMarker(new MarkerOptions()
                .position(new LatLng(mLocalities.get(i).getLat(),mLocalities.get(i).getLng()))
                .title(mLocalities.get(i).getLocName()));
    }

    public void takeGPSLocation(){
        gps = new GPSTracker(MainActivity.this);
        if(gps.canGetLocation){
            lat=gps.getLatitude();
            lng=gps.getLongitude();
        }
    }
    private void makeGetRequest() {
        String address="https://geocode-maps.yandex.ru/1.x/?geocode="+lat+","+lng+"&spn=0.7,0.7&kind=locality&format=json";
        HttpClient client = new DefaultHttpClient();
        ResponseHandler res = new BasicResponseHandler();
        HttpGet request = new HttpGet(address);
        try {
            httpAnswer=client.execute(request, res).toString();
            Log.d("Response of GET request", httpAnswer);
        } catch (ClientProtocolException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        takeLocalities();
    }
    public void makeGetRequestOnNewThread() {
        Thread t = new Thread(new Runnable() {
            @Override
            public void run() {
                makeGetRequest();
            }
        });
        t.start();
        try {
            t.join();
        }catch (Exception e){
            Log.d("JOIN", "failed");
        }
        MapFragment mapFragment = (MapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
    }

    public void takeLocalities(){
        String json=httpAnswer;
        Log.d("JSON", json);
        try {
            for (int i = 0; i < 10; i++) {
                int index = json.indexOf("LocalityName");
                json = json.substring(index + 15);
                index = json.indexOf("\"");
                String locName = json.substring(0, index);
                index = json.indexOf("pos\":\"");
                json = json.substring(index + 6);
                index = json.indexOf(" ");
                String coordinate1 = json.substring(0, index);
                json = json.substring(index + 1);
                index = json.indexOf("\"");
                String coordinate2 = json.substring(0, index);
                double locLat = Double.parseDouble(coordinate1);
                double locLng = Double.parseDouble(coordinate2);
                mLocalities.add(new Locality(locName, locLat, locLng));
            }

        }catch(Exception e){
            Log.d("count of localities", mLocalities.size()+"");
        }
    }


}