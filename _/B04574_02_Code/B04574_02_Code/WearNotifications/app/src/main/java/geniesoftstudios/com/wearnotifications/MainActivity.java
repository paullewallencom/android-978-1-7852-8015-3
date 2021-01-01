package geniesoftstudios.com.wearnotifications;

import android.support.v7.app.ActionBarActivity;
import android.app.Notification;
import android.app.PendingIntent;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationManagerCompat;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

// Handling Custom Notifications
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

// Handling Voice Notifications
import android.support.v4.app.RemoteInput;
import android.util.Log;

public class MainActivity extends ActionBarActivity {

    // Set up our Notification message ID
    int NOTIFICATION_ID = 001;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Clear all previous notifications and
        // generate new unique ids
        NotificationManagerCompat.from(this).cancelAll();

        Intent intent = new Intent(this, MainActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);

        // Method to display our custom notification
        displayCustomNotification(pendingIntent);

        // Method to handle voice notifications
        //voiceNotifications(pendingIntent);

        // Method to display our Page-Stacking notifications
        //displayPageStackNotifications();
    }

    // Method for displaying our basic notification message
    public void displayBasicNotification(PendingIntent pendingIntent)
    {
        // Set up our Notification Action class method
        NotificationCompat.Action action = new NotificationCompat.Action.Builder(
                R.mipmap.ic_launcher, getString(R.string.notification_title), pendingIntent)
                .build();

        Notification notification = new NotificationCompat.Builder(MainActivity.this)
                .setContentText(getString(R.string.basic_notify_msg))
                .setContentTitle(getText(R.string.notification_title))
                .setSmallIcon(R.mipmap.ic_launcher)
                .extend(new NotificationCompat.WearableExtender().addAction(action))
                .build();

        NotificationManagerCompat notificationManagerCompat = NotificationManagerCompat.from(MainActivity.this);
        notificationManagerCompat.notify(NOTIFICATION_ID, notification);
    }

    // Method for displaying our custom notification message
    public void displayCustomNotification(final PendingIntent pendingIntent) {

        // Get a reference to our Send Notifications Button
        final Button mSendNotificationButton = (Button) findViewById(R.id.sendNotificationButton);
        final EditText mSendNotificationInput = (EditText) findViewById(R.id.customNotificationInput);

        // Set our notification input hint message and update the text for our button
        mSendNotificationInput.setHint(R.string.notification_message);
        mSendNotificationButton.setText(R.string.notification_button);

        // Set up our Send Notifications Button OnClick method
        mSendNotificationButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                // Get a pointer to our entered in message textBox
                String message = mSendNotificationInput.getText().toString();

                // Set up our Notification Action class method
                NotificationCompat.Action action = new NotificationCompat.Action.Builder(
                        R.mipmap.ic_launcher, getString(R.string.notification_title), pendingIntent)
                        .build();

                Notification notification = new NotificationCompat.Builder(MainActivity.this)
                        .setContentText(message)
                        .setContentTitle(getText(R.string.notification_title))
                        .setSmallIcon(R.mipmap.ic_launcher)
                        .extend(new NotificationCompat.WearableExtender().addAction(action))
                        .build();

                NotificationManagerCompat notificationManagerCompat = NotificationManagerCompat.from(MainActivity.this);
                notificationManagerCompat.notify(NOTIFICATION_ID, notification);
            }
        });
    }

    // Method for handling our Voice Notifications
    public void voiceNotifications(PendingIntent pendingIntent)
    {
        // Key for the string that's delivered in the action's intent
        final String EXTRA_VOICE_REPLY = "extra_voice_reply";
        final String voiceOptions = "Choose one of these options";
        String[] voiceChoices = getResources().getStringArray(R.array.voice_choices);

        final RemoteInput remoteInput = new RemoteInput.Builder(EXTRA_VOICE_REPLY)
                .setLabel(voiceOptions)
                .setChoices(voiceChoices)
                .build();

        // Call our voice notification
        handleVoiceNotifications(remoteInput, pendingIntent);

        // Get the users spoken voice message and display it
        CharSequence replyText = getMessageText(getIntent(), EXTRA_VOICE_REPLY);
        if(replyText != null) {
            Log.d("VoiceNotifications", "You replied: " + replyText);
        }
    }

    // Method for responding to Voice Notification messages
    public void handleVoiceNotifications(RemoteInput remoteInput, PendingIntent pendingIntent)
    {
        // Get a reference to our entered in message textBox
        String message = "Please respond to this message";

        // Set up our Notification Action class method
        NotificationCompat.Action action = new NotificationCompat.Action.Builder(
                R.mipmap.ic_launcher, getString(R.string.notification_title), pendingIntent)
                .addRemoteInput(remoteInput)
                .build();

        Notification notification = new NotificationCompat.Builder(MainActivity.this)
                .setContentText(message)
                .setContentTitle(getText(R.string.notification_title))
                .setSmallIcon(R.mipmap.ic_launcher)
                .extend(new NotificationCompat.WearableExtender().addAction(action))
                .build();

        NotificationManagerCompat notificationManagerCompat = NotificationManagerCompat.from(MainActivity.this);
        notificationManagerCompat.notify(NOTIFICATION_ID, notification);
    }

    // Method for displaying our page stacking notification messages
    public void displayPageStackNotifications() {

        int stackNotificationId = 0;
        int MAX_NOTIFICATIONS = 2;

        // String to represent the group all the notifications will be a part of
        final String GROUP_NOTIFICATIONS = "group_notifications";

        // Group notification that will be visible on the phone
        Notification summaryNotification = new NotificationCompat.Builder(this)
                .setContentTitle(MAX_NOTIFICATIONS + " Notifications Received")
                .setContentText("You have received " + MAX_NOTIFICATIONS + " messages")
                .setSmallIcon(R.mipmap.ic_launcher)
                .setGroup(GROUP_NOTIFICATIONS)
                .setGroupSummary(true)
                .build();

        // Create our first view Intent
        Intent viewIntent1 = new Intent(this, MainActivity.class);
        PendingIntent viewPendingIntent1 =
                PendingIntent.getActivity(this, (stackNotificationId + 1), viewIntent1, 0);
        Notification notification1 = new NotificationCompat.Builder(this)
                .addAction(R.mipmap.ic_launcher, "Sounds Great", viewPendingIntent1)
                .setContentTitle("Movie Message")
                .setContentText("Do you want to go to the movies?")
                .setSmallIcon(R.mipmap.ic_launcher)
                .setGroup(GROUP_NOTIFICATIONS)
                .build();

        // Create our second view intent
        Intent viewIntent2 = new Intent(this, MainActivity.class);
        PendingIntent viewPendingIntent2 =
                PendingIntent.getActivity(this, (stackNotificationId + 2), viewIntent2, 0);
        Notification notification2 = new NotificationCompat.Builder(this)
                .addAction(R.mipmap.ic_launcher, "Why not", viewPendingIntent2)
                .setContentTitle("Red Wine Message")
                .setContentText("Another glass of Red Wine?")
                .setSmallIcon(R.mipmap.ic_launcher)
                .setGroup(GROUP_NOTIFICATIONS)
                .build();

        // Issue our group notification message
        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(this);
        notificationManager.notify(stackNotificationId, summaryNotification);

        // Then, issue each of our separate wearable notifications
        notificationManager.notify((stackNotificationId + 1), notification1);
        notificationManager.notify((stackNotificationId + 2), notification2);
    }

        @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
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

    // Method that accepts an intent and returns the voice response,
    // which is referenced by the EXTRA_VOICE_REPLY key.
    private CharSequence getMessageText(Intent intent, String EXTRA_VOICE_REPLY) {
        Bundle remoteInput = RemoteInput.getResultsFromIntent(intent);
        if (remoteInput != null) {
            return remoteInput.getCharSequence(EXTRA_VOICE_REPLY);
        }
        return null;
    }
}