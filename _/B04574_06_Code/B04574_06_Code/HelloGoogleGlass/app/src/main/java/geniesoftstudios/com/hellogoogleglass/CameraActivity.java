package geniesoftstudios.com.hellogoogleglass;

/**
 * CameraActivity.java
 * Created by Steven Daniel on 31/05/2015
 */
import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.PixelFormat;
import android.hardware.Camera;
import android.media.AudioManager;
import com.google.android.glass.media.Sounds;
import android.os.Bundle;
import android.os.Environment;
import android.view.KeyEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.util.Log;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class CameraActivity extends Activity {
    private SurfaceHolder _surfaceHolder;
    private Camera _camera;
    private boolean _previewOn;

    Context _context = this;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.camera_preview);

        // Set up the camera preview UI
        getWindow().setFormat(PixelFormat.UNKNOWN);
        SurfaceView surfaceView = (SurfaceView) findViewById(R.id.camerapreview);
        _surfaceHolder = surfaceView.getHolder();
        _surfaceHolder.addCallback(new SurfaceHolderCallback());
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        switch (keyCode) {
            // Google Glass Touchpad tap
            case KeyEvent.KEYCODE_DPAD_CENTER:  // Alternative way to take a picture
            case KeyEvent.KEYCODE_ENTER: {
                Log.d("CameraActivity", "Touchpad Tap.");
                AudioManager audio = (AudioManager)getSystemService(Context.AUDIO_SERVICE);
                audio.playSoundEffect(Sounds.SUCCESS);

                // Take the picture
                _camera.takePicture(null, null, new SavePicture());
                return true;
            }
            default: {
                return super.onKeyDown(keyCode, event);
            }
        }
    }

    // Create the image filename with the current timestamp
    private String getFilename(boolean isThumbnail) {
        Log.d("CameraActivity", "Saving picture...");
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd_HHmmss_SSS");

        // Build the image filename
        StringBuilder imageFilename = new StringBuilder();
        imageFilename.append(sdf.format(new Date()));
        if (isThumbnail) imageFilename.append("_tn");
        imageFilename.append(".jpg");

        // Return the full path to the image
        return Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM) + File.separator + "Camera" + File.separator + imageFilename;
    }

    // Write the image to local storage
    public void savePicture(Bitmap image, String filename) throws IOException {
        FileOutputStream fos = null;
        try {
            fos = new FileOutputStream(filename);
            image.compress(Bitmap.CompressFormat.JPEG, 100, fos);
            Log.d("CameraActivity", "Picture saved.");
        }
        catch (IOException e) {
            e.printStackTrace();
            throw(e);
        }
        finally {
            fos.close();
        }
    }

    // Handling of the camera preview
    private class SurfaceHolderCallback implements SurfaceHolder.Callback {

        @Override
        public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
            if (null != _camera) {

                try {
                    Camera.Parameters params = _camera.getParameters();
                    params.setPreviewFpsRange(5000, 5000);
                    _camera.setParameters(params);

                    // Start the preview
                    _camera.setPreviewDisplay(_surfaceHolder);
                    _camera.startPreview();
                    _previewOn = true;
                }
                catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

        @Override
        public void surfaceCreated(SurfaceHolder holder) {
            _camera = Camera.open();
        }

        @Override
        public void surfaceDestroyed(SurfaceHolder holder) {
            if (_previewOn) {
                // Stop the preview and release the camera
                _camera.stopPreview();
                _camera.release();
            }
        }
    }

    // Callback that is called when the picture is taken
    class SavePicture implements Camera.PictureCallback {
        @Override
        public void onPictureTaken(byte[] bytes, Camera camera) {
            Log.d("CameraActivity", "Picture taken.");
            Bitmap image = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);

            try {
                // Save the image
                String imageFilename = getFilename(false);
                savePicture(image, imageFilename);
            }
            catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}