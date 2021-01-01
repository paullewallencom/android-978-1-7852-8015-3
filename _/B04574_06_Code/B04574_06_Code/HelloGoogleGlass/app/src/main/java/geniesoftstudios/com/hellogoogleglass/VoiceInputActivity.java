package geniesoftstudios.com.hellogoogleglass;

/**
 * VoiceInputActivity.java
 * Created by Steven Daniel on 31/05/2015
 */
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.speech.RecognizerIntent;
import android.view.View;

import com.google.android.glass.widget.CardBuilder;
import java.util.List;
import android.util.Log;

public class VoiceInputActivity extends Activity {
    private final static int SPEECH_REQUEST = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        receiveVoiceInput();
    }

    public void receiveVoiceInput() {
        // Start the intent to ask the user for voice input
        Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        intent.putExtra(RecognizerIntent.EXTRA_PROMPT, "Say something");
        startActivityForResult(intent, SPEECH_REQUEST);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode,
                                    Intent data) {
        // When the voice input intent returns and is ok
        if (requestCode == SPEECH_REQUEST && resultCode == RESULT_OK) {
            // Get the text spoken
            List<String> results = data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
            String spokenText = results.get(0);
            Log.d("VoiceInputActivity", "customization text: " + spokenText);

            // Add the text to the view so the user knows we retrieved it correctly
            CardBuilder card = new CardBuilder(this, CardBuilder.Layout.TEXT);
            card.setText(spokenText);
            View cardView = card.getView();
            setContentView(cardView);
        }
        super.onActivityResult(requestCode, resultCode, data);
    }
}