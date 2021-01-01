package geniesoftstudios.com.hellogoogleglass;

/**
 * MapLocationActivity.java
 * Created by Steven Daniel on 17/06/2015
 */
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.location.Address;
import android.location.Criteria;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.app.Activity;
import android.content.Context;
import android.view.View;
import android.view.WindowManager;
import android.util.Log;
import com.google.android.glass.widget.CardBuilder;

public class MapLocationActivity extends Activity implements LocationListener{
    public LocationManager mLocationManager;
    private CardBuilder card;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Add the text to the view so the user knows we retrieved it correctly
        card = new CardBuilder(this, CardBuilder.Layout.TEXT);
        card.setText("Getting your location...");
        View cardView = card.getView();
        setContentView(cardView);

        // Request a static location from the location manager
        mLocationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

        // Set up a criteria object to get the location data, using the GPS provider
        // on the handheld device.
        Criteria criteria = new Criteria();
        criteria.setAccuracy(Criteria.ACCURACY_FINE);
        criteria.setAltitudeRequired(true);

        List<String> providers = mLocationManager.getProviders(criteria, true);

        // Asks the provider to send a location update every 10 seconds.
        for (String provider : providers) {
            mLocationManager.requestLocationUpdates(provider, 10000, 0, this);
        }
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
    }

    // Method to show the current Latitude and Longitude to the user
    @Override
    public void onLocationChanged(Location location) {

        // Get the Latitude and Longitude information
        String mLatitude = String.valueOf(location.getLatitude());
        String mLongitude = String.valueOf(location.getLongitude());

        // Attempt to get address information from the static location object
        Geocoder geocoder = new Geocoder(this);

        try {
            List<Address> addresses = geocoder.getFromLocation(location.getLatitude(),
                    location.getLongitude(), 1);

            // Check to see if we have successfully returned the address information
            if (addresses.size() > 0)  {
                Address mAddress = addresses.get(0);
                String mAddressInfo = "";
                for(int i = 0; i < mAddress.getMaxAddressLineIndex(); i++) {
                    mAddressInfo += mAddress.getAddressLine(i) + " ";
                }
                // Display the address information within our Card
                card.setFootnote(mAddressInfo);
            }
        } catch (IOException e) {
            Log.e("LocationActivity", "Geocoder error", e);
        }

        // Then, call our google maps URL to return the map for the latitude
        // and longitude coordinates
        new LocationMapImageTask().execute(
                "http://maps.googleapis.com/maps/api/staticmap?" +
                "zoom=10&size=640x360&markers=color:green|" + mLatitude + "," + mLongitude);

        View cardView = card.getView();
        setContentView(cardView);
    }

    // Private class to handle loading the Map and returning back an Bitmap image object
    private class LocationMapImageTask extends AsyncTask<String, Void, Bitmap> {

        @Override
        protected Bitmap doInBackground(String... stringURL) {
            Bitmap bmp = null;
            try {
                URL url = new URL(stringURL[0]);
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setDoInput(true);
                conn.connect();
                InputStream inputStream = conn.getInputStream();
                BitmapFactory.Options options = new BitmapFactory.Options();
                bmp = BitmapFactory.decodeStream(inputStream, null, options);
            }
            catch (Exception e) {
                Log.e("LocationActivity", "LocationMapImageTask", e);
            }
            // Return the map as a bitmap image
            return  bmp;
        }

        // After we have successfully executed our doInBackground method
        // we need to display our map image to the card.
        @Override
        protected void onPostExecute (Bitmap result) {
            // Add the map image to our Google Glass card
            card.addImage(result);

            View cardView = card.getView();
            setContentView(cardView);
            super.onPostExecute(result);
        }
    }

    @Override
    public void onProviderDisabled(String arg0) {
        // Called when the provider is disabled by the user.
    }

    @Override
    public void onProviderEnabled(String arg0) {
        // Called when the provider is enabled by the user.
    }

    @Override
    public void onStatusChanged(String arg0, int arg1, Bundle arg2) {
        // Called when the provider status changes.
    }
}