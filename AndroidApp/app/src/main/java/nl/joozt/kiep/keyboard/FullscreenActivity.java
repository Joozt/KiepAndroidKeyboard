package nl.joozt.kiep.keyboard;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ProgressBar;

import androidx.appcompat.app.AppCompatActivity;
import androidx.preference.PreferenceManager;

public class FullscreenActivity extends AppCompatActivity implements SharedPreferences.OnSharedPreferenceChangeListener {
    public static final String KIEP_KEYBOARD = "KiepKeyboard";
    private EditText editText;
    private TTS tts;
    private UpdateCheck updateCheck;
    private FontSize fontSize;

    @Override
    @SuppressLint("SourceLockedOrientationActivity")
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        PreferenceManager.getDefaultSharedPreferences(this).registerOnSharedPreferenceChangeListener(this);

        // Set the content full screen & landscape
        setContentView(R.layout.activity_fullscreen);
        this.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);

        // Get the information of the editor where text is located
        editText = findViewById(R.id.editText);
        editText.setShowSoftInputOnFocus(false);

        Analytics analytics = new Analytics(this);
        GlobalKeyPressListener keyPressListener = new GlobalKeyPressListener(editText);

        fontSize = new FontSize(this, keyPressListener, editText);

        // Define instances of the yes/no functionality
        YesNo yesNo = new YesNo(this, keyPressListener, editText);
        yesNo.setAnalytics(analytics);

        // Define instances of the TTS functionality
        tts = new TTS(this, keyPressListener, editText);
        tts.setAnalytics(analytics);

        FrameLayout frameLayout = findViewById(R.id.content);
        ProgressBar progressBar = findViewById(R.id.progressBar);
        new BatteryStatus(this, progressBar, frameLayout);

        updateCheck = new UpdateCheck(this);
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
        this.recreate(); // Reload everything when one of the settings has changed
    }

    @Override
    protected void onResume() {
        super.onResume();

        // Get preferences with previously stored information
        SharedPreferences settings = getSharedPreferences(KIEP_KEYBOARD, Context.MODE_PRIVATE);
        String text = settings.getString("Text", KIEP_KEYBOARD);

        // Set the text back in the editor
        editText.setText(text);

        if (text.equals(KIEP_KEYBOARD)) {

            // If default text, select all and overwrite when start typing
            editText.selectAll();
        } else {

            // Set the cursor to the end
            editText.setSelection(editText.getText().length());
        }
        editText.requestFocus();

        updateCheck.checkForUpdate();
    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
                | View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                | View.SYSTEM_UI_FLAG_FULLSCREEN);
    }

    @Override
    @SuppressLint("ApplySharedPref")
    protected void onPause() {
        super.onPause();

        fontSize.saveCurrentFontSize();

        // Get settings and define preferences to store the text when minimizing / onPause
        SharedPreferences settings = getSharedPreferences(KIEP_KEYBOARD, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = settings.edit();

        // Store the result in the preferences
        editor.putString("Text", editText.getText().toString());
        editor.commit();
    }

    @Override
    protected void onDestroy() {
        tts.destroy();
        super.onDestroy();
    }
}
