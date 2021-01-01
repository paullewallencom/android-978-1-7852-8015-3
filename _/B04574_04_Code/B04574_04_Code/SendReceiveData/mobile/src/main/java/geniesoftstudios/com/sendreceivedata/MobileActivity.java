package geniesoftstudios.com.sendreceivedata;

import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.util.Log;
import java.io.ByteArrayOutputStream;
import java.net.URL;
import java.util.Random;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.wearable.Node;
import com.google.android.gms.wearable.NodeApi;
import com.google.android.gms.wearable.PutDataMapRequest;
import com.google.android.gms.wearable.Wearable;
import com.google.android.gms.common.api.GoogleApiClient.ConnectionCallbacks;
import com.google.android.gms.common.api.GoogleApiClient.OnConnectionFailedListener;
import com.google.android.gms.wearable.Asset;
import com.google.android.gms.wearable.DataMap;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.widget.Button;
import android.widget.EditText;

public class MobileActivity extends ActionBarActivity {

    private GoogleApiClient mGoogleApiClient;
    private static final String LOG_TAG = "MobileActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mobile);

        // Get a pointer to our buttons and textField
        final Button mSendMessageButton = (Button) findViewById(R.id.send_message_button);
        final Button mSendImageButton = (Button) findViewById(R.id.send_image_button);
        final EditText mSendMessageInput = (EditText) findViewById(R.id.send_message_input);

        // Set up our hint message for our Text Field
        mSendMessageInput.setHint(R.string.send_message_text);

        // Set up our send message button onClick method handler
        mSendMessageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Create a new thread to send the entered message
                Thread thread = new Thread(new Runnable()
                {
                    @Override
                    public void run()
                    {
                        try {
                            String messageText = mSendMessageInput.getText().toString();
                            NodeApi.GetConnectedNodesResult nodes = Wearable.NodeApi.getConnectedNodes(mGoogleApiClient).await();
                            for (Node node : nodes.getNodes()) {
                                Wearable.MessageApi.sendMessage(mGoogleApiClient,
                                        node.getId(), "/message", messageText.getBytes() ).await();
                            }
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    mSendMessageInput.getText().clear();
                                }
                            });
                        }
                        catch (Exception e) {
                            Log.e(LOG_TAG, e.getMessage());
                        }
                    }
                });
                // Starts our Thread
                thread.start();
                Log.d(LOG_TAG, "Message has been sent");
            }
        });

        // Set up our send image button onClick method handler
        mSendImageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Create a new thread to send the downloaded image
                Thread thread = new Thread(new Runnable()
                {
                    @Override
                    public void run()
                    {
                        // Declare our image variable to hold the URL
                        String imageName = "http://www.androidcentral.com/sites/" +
                                "androidcentral.com/files/styles/w550h500/public/" +
                                "wallpapers/batdroid-blj.jpg";
                        try {
                            PutDataMapRequest request = PutDataMapRequest.create("/image");
                            DataMap map = request.getDataMap();
                            URL url = new URL(imageName);
                            Bitmap bmp = BitmapFactory.decodeStream(url.openConnection().getInputStream());
                            final ByteArrayOutputStream byteStream = new ByteArrayOutputStream();
                            bmp.compress(Bitmap.CompressFormat.PNG, 100, byteStream);

                            // Creates an image asset from the chosen image
                            Asset asset = Asset.createFromBytes(byteStream.toByteArray());
                            Random randomGenerator = new Random();
                            int randomInt = randomGenerator.nextInt(1000);
                            map.putInt("Integer", randomInt);
                            map.putAsset("androidImage", asset);
                            Wearable.DataApi.putDataItem(mGoogleApiClient, request.asPutDataRequest());
                        }
                        catch (Exception e) {
                            Log.e(LOG_TAG, e.getMessage());
                        }
                    }
                });
                // Starts our Thread
                thread.start();
                Log.d(LOG_TAG, "Image has been sent");
            }
        });
    }

    // establishes a connection between the mobile and wearable
    private void initGoogleApiClient() {
        if (mGoogleApiClient != null && mGoogleApiClient.isConnected()) {
            Log.d(LOG_TAG, "Connected");
        }
        else
        {
            // Creates a new GoogleApiClient object with all connection callbacks
            mGoogleApiClient = new GoogleApiClient.Builder(this)
                    .addConnectionCallbacks(new ConnectionCallbacks() {
                        @Override
                        public void onConnected(Bundle connectionHint) {
                            Log.d(LOG_TAG, "onConnected: " + connectionHint);
                        }
                        @Override
                        public void onConnectionSuspended(int cause) {
                            Log.d(LOG_TAG, "onConnectionSuspended: " + cause);
                        }
                    })
                    .addOnConnectionFailedListener(new OnConnectionFailedListener() {
                        @Override
                        public void onConnectionFailed(ConnectionResult result) {
                            Log.d(LOG_TAG, "onConnectionFailed: " + result);
                        }
                    })
                    .addApi(Wearable.API)
                    .build();

            // Make the connection
            mGoogleApiClient.connect();
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        initGoogleApiClient();
    }

    @Override
    protected void onResume() {
        super.onResume();
        initGoogleApiClient();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mGoogleApiClient.disconnect();
    }

    @Override
    protected void onStop() {
        super.onStop();
        mGoogleApiClient.disconnect();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_mobile, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}