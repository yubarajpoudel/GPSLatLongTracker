package servicetutorial.service;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.util.Log;

import com.google.android.gms.maps.model.LatLng;
import com.mantraideas.simplehttp.datamanager.DataRequestManager;
import com.mantraideas.simplehttp.datamanager.OnDataRecievedListener;
import com.mantraideas.simplehttp.datamanager.dmmodel.DataRequest;
import com.mantraideas.simplehttp.datamanager.dmmodel.DataRequestPair;
import com.mantraideas.simplehttp.datamanager.dmmodel.Method;
import com.mantraideas.simplehttp.datamanager.dmmodel.Response;
import com.mantraideas.simplehttp.datamanager.util.DmUtilities;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by Yubaraj on 24/11/16.
 */

public class GoogleService extends Service implements LocationListener {

    boolean isGPSEnable = false;
    boolean isNetworkEnable = false;
    double latitude, longitude;
    LocationManager locationManager;
    Location location;
    private Handler mHandler = new Handler();
    private Timer mTimer = null;
    long notify_interval = 5000;
    public static String str_receiver = "servicetutorial.service.receiver";
    Intent intent;
    DbHandler handler;


    public GoogleService() {

    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        handler = new DbHandler(this);
        mTimer = new Timer();
        mTimer.schedule(new TimerTaskToGetLocation(), 5, notify_interval);
        intent = new Intent(str_receiver);
//        fn_getlocation();
    }

    @Override
    public void onLocationChanged(Location location) {

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

    private void fn_getlocation() {
        locationManager = (LocationManager) getApplicationContext().getSystemService(LOCATION_SERVICE);
        isGPSEnable = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
        isNetworkEnable = locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
        Log.d("GoogleService", "GpsEnabled = " + isGPSEnable + " isNetworkAvaialble = " + isNetworkEnable);
        if (!isGPSEnable && !isNetworkEnable) {
           Log.d("Googleservice", "location off");
        } else {

            if (isNetworkEnable) {
                location = null;
                locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 1, 0, this);
                if (locationManager != null) {
                    location = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
                    if (location != null) {

                        Log.e("latitude", location.getLatitude() + "");
                        Log.e("longitude", location.getLongitude() + "");

                        latitude = location.getLatitude();
                        longitude = location.getLongitude();
                        fn_update(location);
                    }
                }

            }


            if (isGPSEnable) {
                location = null;
                locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 1, 0, this);
                if (locationManager != null) {
                    location = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
                    if (location != null) {
                        Log.e("latitude", location.getLatitude() + "");
                        Log.e("longitude", location.getLongitude() + "");
                        latitude = location.getLatitude();
                        longitude = location.getLongitude();
                        fn_update(location);
                    }
                }
            }


        }

    }

    private class TimerTaskToGetLocation extends TimerTask {
        @Override
        public void run() {

            mHandler.post(new Runnable() {
                @Override
                public void run() {
                    fn_getlocation();
                }
            });

        }
    }

    private void fn_update(Location location) {
        boolean hasData = handler.isDataExists();
        Log.d("MainActivity", "isOfflineDataExists = " + hasData);

        if (hasData && DmUtilities.isNetworkConnected(getApplicationContext())) {
            List<LatLng> latLngslist = new ArrayList<>();
            latLngslist.addAll(handler.getData());
            Log.d("MainActivity", "latlongListSize = " + latLngslist.size());
            handler.deleteAllData();
        }

        DataRequestPair requestPair = DataRequestPair.create();
        requestPair.put("u_id", "5a05904593ec6");
        requestPair.put("lat", latitude + "");
        requestPair.put("lng", longitude + "");
        requestPair.put("battery", Utilities.getBatteryPercentage(getApplicationContext()) + "");

        DataRequest request = DataRequest.getInstance();
        request.addUrl("http://tracker.mantraideas.com/api/gps-logs");
        request.addMethod(Method.POST);
//            request.addMinimumServerCallTimeDifference(5000);
        request.addDataRequestPair(requestPair);

        DataRequestManager<String> requestManager = DataRequestManager.getInstance(getApplicationContext(), String.class);
        requestManager.addRequestBody(request);
        requestManager.addOnDataRecieveListner(new OnDataRecievedListener() {
            @Override
            public void onDataRecieved(Response response, Object object) {
                if (response == Response.OK) {
                    Log.d("Main", "data = " + object.toString());
                } else {
                    boolean success = handler.saveData(latitude, longitude);
                    Log.d("MainActivity", "success = " + success);
                }
            }
        });
        requestManager.sync();
        intent.putExtra("latutide", location.getLatitude() + "");
        intent.putExtra("longitude", location.getLongitude() + "");
        sendBroadcast(intent);
    }


}
