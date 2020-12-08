package nl.joozt.kiep.keyboard;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.TypedValue;
import android.widget.EditText;

import androidx.preference.PreferenceManager;

public class FontSize {
    private final Context context;
    private final SharedPreferences preferences;
    private final EditText editText;

    public FontSize(Context context, GlobalKeyPressListener keyPressListener, final EditText editText) {
        this.context = context;
        this.preferences = PreferenceManager.getDefaultSharedPreferences(context);
        this.editText = editText;

        float fontSizeSp = preferences.getInt(SettingsActivity.FONT_SIZE, SettingsActivity.FONT_SIZE_DEFAULT);
        editText.setTextSize(TypedValue.COMPLEX_UNIT_SP, fontSizeSp);

        String decrementKey = preferences.getString(SettingsActivity.FONT_SIZE_DECREMENT_KEY, SettingsActivity.FONT_SIZE_DECREMENT_KEY_DEFAULT);
        keyPressListener.addListener(decrementKey, () -> {
            float fontSize = (editText.getTextSize() / context.getResources().getDisplayMetrics().scaledDensity) - 10;
            if (fontSize >= 10) {
                editText.setTextSize(TypedValue.COMPLEX_UNIT_SP, fontSize);
            }
            return true;
        });

        String incrementKey = preferences.getString(SettingsActivity.FONT_SIZE_INCREMENT_KEY, SettingsActivity.FONT_SIZE_INCREMENT_KEY_DEFAULT);
        keyPressListener.addListener(incrementKey, () -> {
            float fontSize = (editText.getTextSize() / context.getResources().getDisplayMetrics().scaledDensity) + 10;
            if (fontSize <= 400) {
                editText.setTextSize(TypedValue.COMPLEX_UNIT_SP, fontSize);
            }
            return true;
        });
    }

    public void saveCurrentFontSize() {
        float fontSizePx = editText.getTextSize();
        int fontSizeSp = (int) (fontSizePx / context.getResources().getDisplayMetrics().scaledDensity);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putInt(SettingsActivity.FONT_SIZE, fontSizeSp);
        editor.apply();
    }
}
