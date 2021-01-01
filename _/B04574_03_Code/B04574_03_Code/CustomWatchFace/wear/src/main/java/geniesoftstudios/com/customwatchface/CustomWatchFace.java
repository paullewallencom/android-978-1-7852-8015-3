package geniesoftstudios.com.customwatchface;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;

import java.util.TimeZone;
import java.text.SimpleDateFormat;
import java.util.Calendar;

public class CustomWatchFace {
    private  final Paint   timeObject;
    private  final Paint   dateObject;
    private  final Paint   batteryObject;
    private        String  dateText;
    private        String  timeText;
    public         String  batteryText;

    private static final String TIME_FORMAT = "kk:mm:ss a";
    private static final String DATE_FORMAT = "EEE, dd MMM yyyy";

    // Declare our class constructor
    public static CustomWatchFace newInstance(Context context) {
        Paint batteryObject = new Paint();
        batteryObject.setColor(Color.RED);
        batteryObject.setTextSize(25);

        Paint timeObject = new Paint();
        timeObject.setColor(Color.GREEN);
        timeObject.setTextSize(35);

        Paint dateObject = new Paint();
        dateObject.setColor(Color.WHITE);
        dateObject.setTextSize(35);

        return new CustomWatchFace(timeObject, dateObject, batteryObject);
    }

    CustomWatchFace(Paint objTime, Paint objDate, Paint objBattery) {
        this.timeObject = objTime;
        this.dateObject = objDate;
        this.batteryObject = objBattery;

        // Initialise our Battery Information
        batteryText = "Level: 0%";
    }

    // Method to update the watch face each time an update has occurred
    public void draw(Canvas canvas, Rect bounds) {
        canvas.drawColor(Color.BLACK);

        timeText = new SimpleDateFormat(TIME_FORMAT).format(Calendar.getInstance().getTime());
        dateText = new SimpleDateFormat(DATE_FORMAT).format(Calendar.getInstance().getTime());

        float timeXOffset = calculateXOffset(timeText, timeObject, bounds);
        float timeYOffset = calculateTimeYOffset(timeText, timeObject, bounds);
        canvas.drawText(timeText, timeXOffset, timeYOffset, timeObject);

        float dateXOffset = calculateXOffset(dateText, dateObject, bounds);
        float dateYOffset = calculateDateYOffset(dateText, dateObject);
        canvas.drawText(dateText, dateXOffset, timeYOffset + dateYOffset, dateObject);

        float batteryXOffset = calculateXOffset(batteryText, batteryObject, bounds);
        float batteryYOffset = calculateBatteryYOffset(batteryText, batteryObject);
        canvas.drawText(batteryText, batteryXOffset, dateYOffset + batteryYOffset, batteryObject);
    }

    // Calculate our X-Offset using our Time Label as the offset
    private float calculateXOffset(String text, Paint paint, Rect watchBounds) {
        float centerX = watchBounds.exactCenterX();
        float timeLength = paint.measureText(text);
        return centerX - (timeLength / 2.0f);
    }

    // Calculate our Time Y-Offset
    private float calculateTimeYOffset(String timeText, Paint timePaint, Rect watchBounds) {
        float centerY = watchBounds.exactCenterY();
        Rect textBounds = new Rect();
        timePaint.getTextBounds(timeText, 0, timeText.length(), textBounds);
        int textHeight = textBounds.height();
        return centerY + (textHeight / 2.0f);
    }

    // calculate our Date Label Y-Offset
    private float calculateDateYOffset(String dateText, Paint datePaint) {
        Rect textBounds = new Rect();
        datePaint.getTextBounds(dateText, 0, dateText.length(), textBounds);
        return textBounds.height() + 10.0f;
    }

    // Calculate our Battery Label Y-Offset
    private float calculateBatteryYOffset(String batteryText, Paint batteryPaint) {
        Rect textBounds = new Rect();
        batteryPaint.getTextBounds(batteryText, 0, batteryText.length(), textBounds);
        return textBounds.height() + 40.0f;
    }

    public void setAntiAlias(boolean antiAlias) {
        batteryObject.setAntiAlias(antiAlias);
        timeObject.setAntiAlias(antiAlias);
        dateObject.setAntiAlias(antiAlias);
    }

    // Set each of our objects colours
    public void setColor(int red, int green, int white) {
        batteryObject.setColor(red);
        timeObject.setColor(green);
        dateObject.setColor(white);
    }

    // method to get our current timezone
    public void updateTimeZoneWith(String timeZone) {
        // Set our default time zone
        TimeZone.setDefault(TimeZone.getTimeZone(timeZone));

        // Get the current time for our current timezone
        timeText = new SimpleDateFormat(TIME_FORMAT).format(Calendar.getInstance().getTime());
    }
}