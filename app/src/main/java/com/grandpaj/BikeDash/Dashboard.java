package com.grandpaj.BikeDash;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.location.Location;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.AbsoluteSizeSpan;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.google.android.gms.appindexing.Action;
import com.google.android.gms.appindexing.AppIndex;
import com.google.android.gms.appindexing.Thing;
import com.google.android.gms.common.api.GoogleApiClient;

import DataDisplay.DataText;

public class Dashboard extends AppCompatActivity implements GPSCallback {
    private GPSManager gpsManager = null;
    private double speed = 0.0;
    private double calculatedSpeed = 0.0;
    private double tripDistance = 0.0;
    private long locationTime;
    private long locationTimeDifference;
    private Location lastLocation;
    private LocationHistory locationHistory = new LocationHistory();
    private int measurement_index = Constants.INDEX_KM;
    private AbsoluteSizeSpan sizeSpanLarge = null;
    private AbsoluteSizeSpan sizeSpanSmall = null;
    private Calendar calendar = new GregorianCalendar();
    private SharedPreferences mPrefs;
    private Typeface face;
    /**
     * ATTENTION: This was auto-generated to implement the App Indexing API.
     * See https://g.co/AppIndexing/AndroidStudio for more information.
     */
    private GoogleApiClient client;

    /**
     * ATTENTION: This was auto-generated to implement the App Indexing API.
     * See https://g.co/AppIndexing/AndroidStudio for more information.
     */
    public Action getIndexApiAction() {
        Thing object = new Thing.Builder()
                .setName("DeviceSpeedDemo Page") // TODO: Define a title for the content shown.
                // TODO: Make sure this auto-generated URL is correct.
                .setUrl(Uri.parse("http://[ENTER-YOUR-URL-HERE]"))
                .build();
        return new Action.Builder(Action.TYPE_VIEW)
                .setObject(object)
                .setActionStatus(Action.STATUS_TYPE_COMPLETED)
                .build();
    }

    @Override
    public void onStart() {
        super.onStart();

        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        client.connect();
        AppIndex.AppIndexApi.start(client, getIndexApiAction());
    }

    @Override
    public void onStop() {
        super.onStop();

        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        AppIndex.AppIndexApi.end(client, getIndexApiAction());
        client.disconnect();
    }

    private class locationChange {
        float distanceChange;
        long timeDifference;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mPrefs = getPreferences(MODE_PRIVATE);
        tripDistance = mPrefs.getFloat("trip_distance", (float) 0.0);
        locationHistory.setTripDistance(tripDistance);


        setContentView(R.layout.main);

        gpsManager = new GPSManager();

        gpsManager.startListening(getApplicationContext());
        gpsManager.setGPSCallback(this);

        face = Typeface.createFromAsset(getAssets(), "fonts/LED.Font.ttf");
        TextView tvSpeed = ((TextView) findViewById(R.id.info_message));

        //((TextView)findViewById(R.id.info_message)).setText(getString(R.string.info));
        tvSpeed.setText(getString(R.string.info));

        measurement_index = AppSettings.getMeasureUnit(this);
        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        client = new GoogleApiClient.Builder(this).addApi(AppIndex.API).build();
    }

    protected void onPause() {
        super.onPause();

        SharedPreferences.Editor ed = mPrefs.edit();
        //save the total trip distance
        ed.putFloat("trip_distance", (float) tripDistance);
        ed.commit();
    }

    /**
     * @param location gps reported current location
     */
    @Override
    public void onGPSUpdate(Location location) {
        locationChange lastChange = new locationChange();

        location.getLatitude();
        location.getLongitude();
        speed = convertSpeed(location.getSpeed());
        //locationTime = location.getElapsedRealtimeNanos();
        locationTime = location.getTime();  //milliseconds

        long roundedSpeed = Math.round(10.0 * speed);
        int iSpeed = (int) speed;
        int fracSpeed = (int) roundedSpeed - 10 * iSpeed;
        //String speedString = "" + roundDecimal(convertSpeed(speed),2)+"";
        String speedString = "" + iSpeed;
        String unitString = measurementUnitString(measurement_index);

        //setSpeedText(R.id.info_message,speedString);
        DataText speedView = (DataText) findViewById(R.id.info_message);
        //speedView.setTypeface(face,Typeface.BOLD);
        //speedView.setTextSize((float)168.0);
        speedView.setData(speedString);

        TextView speedUnits = (TextView) findViewById(R.id.speed_units);
        speedUnits.setText("" + unitString + "/hr");

        TextView fracSpeedView = (TextView) findViewById(R.id.speed_fraction);
        fracSpeedView.setTypeface(face, Typeface.BOLD);
        fracSpeedView.setText("" + fracSpeed + "");

        locationHistory.append(location);
        calculatedSpeed = locationHistory.getCalculatedSpeed();

        /*
        if(lastLocation != null) {
            tripDistance += lastLocation.distanceTo(location);

            lastChange.distanceChange = lastLocation.distanceTo(location);
            lastChange.timeDifference = location.getTime()-lastLocation.getTime();
        }
        lastLocation = location;
    */
        tripDistance = locationHistory.getTripDistance();
        String tripDistanceString = "" + roundDecimal(convertDistance(tripDistance), 3);

        TextView distanceMessage = (TextView) findViewById(R.id.distance_message);
        distanceMessage.setTypeface(face, Typeface.BOLD);
        distanceMessage.setText("" + tripDistanceString);
        TextView distanceUnits = (TextView) findViewById(R.id.distance_units);
        distanceUnits.setText("" + unitString);
        //setSpeedText(R.id.distance_message,tripDistanceString + "");
        //setSpeedText(R.id.distance_units,"" + unitString);

        //get the current time
        //String timeString = calendar.
        SimpleDateFormat timeFormat = new SimpleDateFormat("h:mm:ss");
        String cDateTime = timeFormat.format(new Date());
        //setSpeedText(R.id.current_time,cDateTime);
        TextView timeView = (TextView) findViewById(R.id.current_time);
        timeView.setText(cDateTime);
    }

    @Override
    protected void onDestroy() {
        gpsManager.stopListening();
        gpsManager.setGPSCallback(null);

        gpsManager = null;

        super.onDestroy();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu, menu);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        boolean result = true;

        switch (item.getItemId()) {
            case R.id.menu_about: {
                displayAboutDialog();

                break;
            }
            case R.id.unit_km: {
                measurement_index = 0;

                AppSettings.setMeasureUnit(this, 0);

                break;
            }
            case R.id.unit_miles: {
                measurement_index = 1;

                AppSettings.setMeasureUnit(this, 1);

                break;
            }
            default: {
                result = super.onOptionsItemSelected(item);

                break;
            }
        }

        return result;
    }

    public void resetDistance(View view) {
        //tripDistance = 0.0;
        locationHistory.setTripDistance(0.0);
    }

    private double convertSpeed(double speed) {
        return ((speed * Constants.HOUR_MULTIPLIER) * Constants.UNIT_MULTIPLIERS[measurement_index]);
    }

    private double convertDistance(double distance) {
        return (distance * Constants.UNIT_MULTIPLIERS[measurement_index]);
    }

    private String measurementUnitString(int unitIndex) {
        String string = "";

        switch (unitIndex) {
            case Constants.INDEX_KM:
                string = "km";
                break;
            case Constants.INDEX_MILES:
                string = "mi";
                break;
        }

        return string;
    }

    private double roundDecimal(double value, final int decimalPlace) {
        BigDecimal bd = new BigDecimal(value);

        bd = bd.setScale(decimalPlace, RoundingMode.HALF_UP);
        value = bd.doubleValue();

        return value;
    }

    private void setSpeedText(int textid, String text) {
        Spannable span = new SpannableString(text);
        int firstPos = text.indexOf(32);

        span.setSpan(sizeSpanLarge, 0, firstPos, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        span.setSpan(sizeSpanSmall, firstPos + 1, text.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);

        TextView tv = ((TextView) findViewById(textid));

        tv.setText(span);
    }

    private void setSpeedText(int textid, double speed) {
        //String speedText = "not yet";
        String speedText = String.valueOf(speed);
        TextView tv = ((TextView) findViewById(textid));

        tv.setText(speedText);
    }

    private void displayAboutDialog() {
        final LayoutInflater inflator = LayoutInflater.from(this);
        final View settingsview = inflator.inflate(R.layout.about, null);
        final AlertDialog.Builder builder = new AlertDialog.Builder(this);

        builder.setTitle(getString(R.string.app_name));
        builder.setView(settingsview);

        builder.setPositiveButton(android.R.string.ok, new OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

            }
        });

        builder.create().show();
    }
}