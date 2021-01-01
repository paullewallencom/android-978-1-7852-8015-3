package geniesoftstudios.com.sendreceivedata;

import android.app.Activity;
import android.os.Bundle;
import android.support.wearable.view.WatchViewStub;
import android.widget.TextView;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Handler;
import android.widget.ImageView;
import android.util.Log;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.wearable.Asset;
import com.google.android.gms.wearable.DataApi;
import com.google.android.gms.wearable.DataEvent;
import com.google.android.gms.wearable.DataEventBuffer;
import com.google.android.gms.wearable.DataMapItem;
import com.google.android.gms.wearable.MessageApi;
import com.google.android.gms.wearable.MessageEvent;
import com.google.android.gms.wearable.Wearable;
import java.io.InputStream;

public class WearActivity extends Activity {

    private TextView mTextView;
    private ImageView imageView;
    private GoogleApiClient mGoogleApiClient;
    private Bitmap imageBitmap;

    private final Handler imageHandler = new Handler();
    private static final String LOG_TAG = "WearActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_wear);
        final WatchViewStub stub = (WatchViewStub) findViewById(R.id.watch_view_stub);
        stub.setOnLayoutInflatedListener(new WatchViewStub.OnLayoutInflatedListener() {
            @Override
            public void onLayoutInflated(WatchViewStub stub) {
                mTextView = (TextView) stub.findViewById(R.id.received_message_input);
                imageView = (ImageView) stub.findViewById(R.id.received_image_input);
            }
        });

        // Establish our connection
        initGoogleApiClient();
    }

    // establishes a connection between the mobile and wearable
    private void initGoogleApiClient()
    {
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(new GoogleApiClient.ConnectionCallbacks() {
                    @Override
                    public void onConnected(Bundle connectionHint) {
                        Log.d(LOG_TAG, "onConnected: " + connectionHint);
                        Wearable.DataApi.addListener(mGoogleApiClient, onDataChangedListener);
                        Wearable.MessageApi.addListener( mGoogleApiClient, messageListener);
                    }
                    @Override
                    public void onConnectionSuspended(int cause) {
                        Log.d(LOG_TAG, "onConnectionSuspended: " + cause);
                    }
                })
                .addOnConnectionFailedListener(new GoogleApiClient.OnConnectionFailedListener() {
                    @Override
                    public void onConnectionFailed(ConnectionResult result) {
                        Log.d(LOG_TAG, "onConnectionFailed: " + result);
                    }
                })
                .addApi(Wearable.API)
                .build();

        mGoogleApiClient.connect();
    }

    public DataApi.DataListener onDataChangedListener = new DataApi.DataListener() {
        @Override

        public void onDataChanged(DataEventBuffer dataEvents) {
            for (DataEvent event : dataEvents) {
                if (event.getType() == DataEvent.TYPE_CHANGED &&
                        event.getDataItem().getUri().getPath().equals("/image")) {

                    DataMapItem dataMapItem = DataMapItem.fromDataItem(event.getDataItem());
                    Asset imageAsset = dataMapItem.getDataMap().getAsset("androidImage");
                    imageBitmap = loadBitmapFromAsset(imageAsset);

                    // Process our received image bitmap
                    imageHandler.post(new Runnable() {
                        @Override
                        public void run() {
                                Log.d(LOG_TAG, "Image received");
                                imageView.setImageBitmap(imageBitmap);
                            }
                    });
                }
            }
        }
    };

    public Bitmap loadBitmapFromAsset(Asset asset) {
        if (asset == null) {
            throw new IllegalArgumentException("Asset cannot be empty");
        }
        // Convert asset into a file descriptor and block until it's ready
        InputStream assetInputStream = Wearable.DataApi.getFdForAsset(mGoogleApiClient, asset).await().getInputStream();
        if (assetInputStream == null) {
            Log.w(LOG_TAG, "Requested an unknown Asset.");
            return null;
        }
        // Decode the stream into a bitmap
        return BitmapFactory.decodeStream(assetInputStream);
    }

    MessageApi.MessageListener messageListener = new MessageApi.MessageListener() {
        @Override
        public void onMessageReceived(final MessageEvent messageEvent) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    if (messageEvent.getPath().equalsIgnoreCase("/message")) {
                        Log.i(LOG_TAG, new String(messageEvent.getData()));
                        mTextView.setText(new String(messageEvent.getData()));
                    }
                }
            });
        }
    };

    @Override
    protected void onStop() {
        super.onStop();
        mGoogleApiClient.disconnect();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mGoogleApiClient.disconnect();
    }
}