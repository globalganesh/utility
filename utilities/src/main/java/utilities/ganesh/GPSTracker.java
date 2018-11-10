package utilities.ganesh;

import android.Manifest;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.provider.Settings;

public class GPSTracker implements LocationListener {

    private static final long MIN_DISTANCE_CHANGE_FOR_UPDATES = 10; // 10 meters
    private static final long MIN_TIME_BW_UPDATES = 1000 * 60 * 1; // 1 minute
    private final Context mContext;
    public Location location;
    protected LocationManager locationManager;
    boolean isGPSEnabled = false;
    boolean isNetworkEnabled = false;
    boolean canGetLocation = false;

    public GPSTracker(Context context) {
        this.mContext = context;
        locationManager = (LocationManager) mContext.getSystemService(mContext.LOCATION_SERVICE);
        if (getLocationGps() == null)
            getLocationNetwork();
    }

    public Double getLatitude() {
        if (location != null)
            return location.getLatitude();
        return 0.0;
    }

    public Double getLongitude() {
        if (location != null)
            return location.getLongitude();
        return 0.0;
    }

    public Location getLocationNetwork() {
        try {
            if (Build.VERSION.SDK_INT >= 23 && mContext.checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && mContext.checkSelfPermission(Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED)
                return null;

            isNetworkEnabled = locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
            if (isNetworkEnabled) {
                locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, MIN_TIME_BW_UPDATES, MIN_DISTANCE_CHANGE_FOR_UPDATES, this);
                if (locationManager != null) {
                    this.canGetLocation = true;
                    location = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
                    return location;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
        return null;
    }

    public Location getLocationGps() {
        try {
            if (Build.VERSION.SDK_INT >= 23 && mContext.checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && mContext.checkSelfPermission(Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED)
                return null;

            isGPSEnabled = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
            if (isGPSEnabled) {
                locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, MIN_TIME_BW_UPDATES, MIN_DISTANCE_CHANGE_FOR_UPDATES, this);
                if (locationManager != null) {
                    this.canGetLocation = true;
                    location = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
                    return location;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
        return null;
    }

    public void stopUsingGPS() {
        if (locationManager != null) {
            //locationManager.removeUpdates(GPSTracker.this);
        }
    }

    public boolean canGetLocation() {
        return this.canGetLocation;
    }

    public void showSettingsAlert() {
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(mContext);
        alertDialog.setTitle("GPS Connection");
        alertDialog.setMessage("GPS is not enabled.Go to settings to turn on");
        alertDialog.setCancelable(false);
        alertDialog.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
                Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                mContext.startActivity(intent);
            }
        });
        alertDialog.show();
    }

    @Override
    public void onLocationChanged(Location location) {
        if (location == null)
            return;
        this.location = location;
        updateLocation();
    }

    @Override
    public void onProviderDisabled(String provider) {
    }

    @Override
    public void onProviderEnabled(String provider) {
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {
    }

    private void updateLocation() {
        final Handler ThreadCallback = new Handler();
        final Runnable runInCityThread = new Runnable() {
            @Override
            public void run() {
                //Toast.makeText(mContext, "GPS Location Changed \n" + message, Toast.LENGTH_SHORT).show();
            }
        };

        new Thread() {
            public void run() {
                //message = WebService.updateLocation(PreferenceConnector.readString(mContext, PreferenceConnector.USERID, ""), location.getLatitude(), location.getLongitude());
                ThreadCallback.post(runInCityThread);
            }
        }.start();

    }
}
