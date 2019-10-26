package com.example.alert360;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.telephony.SmsManager;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class MainActivity extends AppCompatActivity implements LocationListener {

    Button getLocationBtn;
    String msg="";
    DatabaseReference myRef;
    User user;
    LocationManager locationManager;
    String loc,currentdat;
    EditText e;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        e=findViewById(R.id.edit);

        SimpleDateFormat sdf = new SimpleDateFormat("dd.MM.yyyy 'at' hh:mm:ss a");
        final String currentDateandTime = sdf.format(new Date());

        user =new User();
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        myRef = database.getReference().child("info");

        getLocationBtn = (Button) findViewById(R.id.sos);

        if (ContextCompat.checkSelfPermission(getApplicationContext(), android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getApplicationContext(), android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION, android.Manifest.permission.ACCESS_COARSE_LOCATION}, 101);

        }
        if(ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.SEND_SMS)!= PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.SEND_SMS},123);
        }

        getLocationBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String l=e.getText().toString();

                getLocation();

                try {
                    SmsManager smgr = SmsManager.getDefault();
                    smgr.sendTextMessage("9246539107", null,msg, null, null);
                    smgr.sendTextMessage("9246539107", null,"help", null, null);
                    Toast.makeText(MainActivity.this, "Alerted Successfully", Toast.LENGTH_SHORT).show();
                    user.setLocation(loc);
                    user.setDat(currentDateandTime);
                    int c=Integer.parseInt(l.trim());
                    user.setCasu(c);
                    myRef.push().setValue(user);
                } catch (Exception e) {

                }

            }
        });
    }

    void getLocation() {
        try {
            locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
            locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 5000, 5, this);
        } catch (SecurityException e) {
            e.printStackTrace();
        }
    }

    public void onLocationChanged(Location location) {
        double s = location.getLatitude();
        double s1 = location.getLongitude();
        //locationText.setText("Latitude: " + location.getLatitude() + "\n Longitude: " + location.getLongitude());

        try {
            Geocoder geocoder = new Geocoder(this, Locale.getDefault());
            List<Address> addresses = geocoder.getFromLocation(location.getLatitude(), location.getLongitude(), 1);

            String add=addresses.get(0).getAddressLine(0);
            msg=msg+"Latitude:";
            msg=msg+s;
            msg=msg+" Longitude:";
            msg=msg+s1;
            loc=msg;
            msg=msg+" ";
            msg=msg+add;

        }catch(Exception e)
        {

        }
    }
    @Override
    public void onProviderDisabled(String provider) {

    }
    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {

    }

    @Override
    public void onProviderEnabled(String provider) {

    }
}