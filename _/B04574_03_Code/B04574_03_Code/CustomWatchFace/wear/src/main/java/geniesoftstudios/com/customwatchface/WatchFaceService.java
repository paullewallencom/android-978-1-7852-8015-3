package geniesoftstudios.com.customwatchface;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Rect;
import android.os.BatteryManager;
import android.os.Handler;
import android.os.Looper;
import android.support.wearable.watchface.CanvasWatchFaceService;
import android.support.wearable.watchface.WatchFaceStyle;
import android.view.SurfaceHolder;

import java.util.Calendar;

public class WatchFaceService extends CanvasWatchFaceService {

    @Override
    public Engine onCreateEngine() {
        return new WatchFaceEngine();
    }

    private class WatchFaceEngine extends CanvasWatchFaceService.Engine {

        private CustomWatchFace watchFace;
        private Handler clockTick;

        @Override
        public void onCreate(SurfaceHolder holder) {
            super.onCreate(holder);

            setWatchFaceStyle(new WatchFaceStyle.Builder(WatchFaceService.this)
                    .setCardPeekMode(WatchFaceStyle.PEEK_MODE_SHORT)
                    .setAmbientPeekMode(WatchFaceStyle.AMBIENT_PEEK_MODE_HIDDEN)
                    .setBackgroundVisibility(WatchFaceStyle.BACKGROUND_VISIBILITY_INTERRUPTIVE)
                    .setShowSystemUiTime(false)
                    .build());

            clockTick = new Handler(Looper.myLooper());
            registerBatteryInfoReceiver();
            startTimerIfNecessary();

            watchFace = CustomWatchFace.newInstance(WatchFaceService.this);
        }

        private void startTimerIfNecessary() {
            clockTick.removeCallbacks(timeRunnable);
            if (isVisible() && !isInAmbientMode()) {
                clockTick.post(timeRunnable);
            }
        }

        private final Runnable timeRunnable = new Runnable() {
            @Override
            public void run() {
                onSecondTick();

                if (isVisible() && !isInAmbientMode()) {
                    long TICK_PERIOD_MILLIS = Calendar.getInstance().get(Calendar.MILLISECOND);
                    clockTick.postDelayed(this, TICK_PERIOD_MILLIS);
                }
            }
        };
        // Method to handle when the seconds update
        private void onSecondTick() {
            invalidateIfNecessary();
        }

        private void invalidateIfNecessary() {
            if (isVisible() && !isInAmbientMode()) {
                invalidate();
            }
        }

        @Override
        public void onVisibilityChanged(boolean visible) {
            super.onVisibilityChanged(visible);
            if (visible) {
                registerTimeZoneReceiver();
                registerBatteryInfoReceiver();
            }
            else {
                unregisterTimeZoneReceiver();
                unregisterBatteryInfoReceiver();
            }

            startTimerIfNecessary();
        }

        private void registerTimeZoneReceiver() {
            IntentFilter timeZoneFilter = new IntentFilter(Intent.ACTION_TIMEZONE_CHANGED);
            registerReceiver(timeZoneChangedReceiver, timeZoneFilter);
        }

        private BroadcastReceiver timeZoneChangedReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                if (Intent.ACTION_TIMEZONE_CHANGED.equals(intent.getAction())) {
                    watchFace.updateTimeZoneWith(intent.getStringExtra("time-zone"));
                }
            }
        };

        // method to unregister our detected timezone receiver
        private void unregisterTimeZoneReceiver() {
            unregisterReceiver(timeZoneChangedReceiver);
        }

        // Register a broadcast message to get the battery level
        private void registerBatteryInfoReceiver() {
            IntentFilter batteryInfoFilter = new IntentFilter(Intent.ACTION_BATTERY_CHANGED);
            registerReceiver(batteryInfoChangedReceiver, batteryInfoFilter);
        }

        // Method to receive the message sent by the battery info receiver class
        private BroadcastReceiver batteryInfoChangedReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                if (Intent.ACTION_BATTERY_CHANGED.equals(intent.getAction())) {
                    watchFace.batteryText = String.valueOf("Battery: " +
                            intent.getIntExtra(BatteryManager.EXTRA_LEVEL, 0) + "%");
                }
            }
        };

        // method to unregister our battery information receiver
        private void unregisterBatteryInfoReceiver() {
            unregisterReceiver(batteryInfoChangedReceiver);
        }

        @Override
        public void onDraw(Canvas canvas, Rect bounds) {
            super.onDraw(canvas, bounds);
            watchFace.draw(canvas, bounds);
        }

        @Override
        public void onTimeTick() {
            super.onTimeTick();
            invalidate();
        }

        @Override
        public void onAmbientModeChanged(boolean inAmbientMode) {
            super.onAmbientModeChanged(inAmbientMode);
            watchFace.setAntiAlias(!inAmbientMode);

            if (!inAmbientMode) {
                 watchFace.setColor(Color.RED, Color.GREEN, Color.WHITE);
            }
            else watchFace.setColor(Color.GRAY, Color.GRAY, Color.GRAY);
            invalidate();

            startTimerIfNecessary();
        }

        @Override
        public void onDestroy() {
            clockTick.removeCallbacks(timeRunnable);
            super.onDestroy();
        }
    }
}