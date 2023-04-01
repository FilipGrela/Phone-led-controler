package pl.filipgrela.ledcontroller.fragments.hsvcontroller;

import static android.widget.Toast.LENGTH_LONG;

import androidx.appcompat.app.AppCompatDelegate;
import androidx.lifecycle.ViewModelProvider;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.drawable.ShapeDrawable;
import android.graphics.drawable.shapes.RoundRectShape;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.os.VibrationEffect;
import android.os.Vibrator;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.SeekBar;
import android.widget.Toast;

import java.text.DecimalFormat;
import java.util.Objects;

import pl.filipgrela.ledcontroller.R;
import pl.filipgrela.ledcontroller.Variables;
import pl.filipgrela.ledcontroller.comunication.RaspberryClient;
import pl.filipgrela.ledcontroller.settings.SettingsActivity;

public class HsvControllerFragment extends Fragment {

    private HsvControllerViewModel mViewModel;
    private final String TAG = "HsvControllerFragment";

    SharedPreferences sharedPref;
    Variables variables = Variables.getInstance();

    SettingsActivity settingsActivity;

    private Vibrator vibrator;

    private RaspberryClient raspberryClient;

    private final DecimalFormat df = new DecimalFormat("#.#");

    private FrameLayout relativeLayout;

    private EditText hSeekBarValue;
    private EditText sSeekBarValue;
    private EditText vSeekBarValue;

    private SeekBar hSeekBar;
    private SeekBar sSeekBar;
    private SeekBar vSeekBar;

    private Button[] buttons = new Button[6];


    private double hValue;
    private double sValue;
    private double vValue;

    boolean isDarkModeDisabled = Boolean.parseBoolean(String.valueOf(AppCompatDelegate.getDefaultNightMode() == AppCompatDelegate.MODE_NIGHT_NO));

    public static HsvControllerFragment newInstance() {
        return new HsvControllerFragment();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_hsv_controller, container, false);

        AppCompatDelegate.setDefaultNightMode(isDarkModeDisabled ? AppCompatDelegate.MODE_NIGHT_NO : AppCompatDelegate.MODE_NIGHT_YES);

        sharedPref = requireContext().getSharedPreferences("hssssss", Context.MODE_PRIVATE);
        loadPreferencesToVariables();
        hValue = variables.hValue;
        sValue = variables.sValue;
        vValue = variables.vValue;

        settingsActivity = new SettingsActivity();
        raspberryClient = new RaspberryClient();

        relativeLayout = view.findViewById(R.id.fragment_hsv_controller);

        hSeekBar = view.findViewById(R.id.HSeekbar);
        sSeekBar = view.findViewById(R.id.SSeekbar);
        vSeekBar = view.findViewById(R.id.VSeekbar);

        hSeekBarValue = view.findViewById(R.id.HSeekbarValue);
        sSeekBarValue = view.findViewById(R.id.SSeekbarValue);
        vSeekBarValue = view.findViewById(R.id.VSeekbarValue);

        buttons[0] = view.findViewById(R.id.favourite_0);
        buttons[1] = view.findViewById(R.id.favourite_1);
        buttons[2] = view.findViewById(R.id.favourite_2);
        buttons[3] = view.findViewById(R.id.favourite_3);
        buttons[4] = view.findViewById(R.id.favourite_4);
        buttons[5] = view.findViewById(R.id.favourite_5);


        vibrator = (Vibrator) requireActivity().getSystemService(Context.VIBRATOR_SERVICE);

        updateUI();
        setupKeyboardHide();
        setupSeekBars();
        setupEditText();
        setupFavouriteColors();

        raspberryClient.updateLEDColor(getContext());

        return view;
    }
    private void updateUI() {
        hValue = variables.hValue;
        sValue = variables.sValue;
        vValue = variables.vValue;

        hSeekBarValue.setText(String.valueOf(hValue));
        sSeekBarValue.setText(String.valueOf(sValue));
        vSeekBarValue.setText(String.valueOf(vValue));

        hSeekBar.setProgress((int) (hValue/360*100));
        sSeekBar.setProgress((int) (sValue));
        vSeekBar.setProgress((int) (vValue));
    }

    @SuppressLint("ClickableViewAccessibility")
    private void setupKeyboardHide(){
        relativeLayout.setOnTouchListener((v, event) -> {
            InputMethodManager imm = (InputMethodManager) requireActivity().getSystemService(
                    Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(relativeLayout.getWindowToken(), InputMethodManager.RESULT_UNCHANGED_SHOWN);

            hSeekBarValue.clearFocus();
            sSeekBarValue.clearFocus();
            vSeekBarValue.clearFocus();
            return true;
        });
    }

    private void setupSeekBars(){
        hSeekBar.setMax(360);
        hSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if (fromUser){
                    hValue = progress;
                    variables.sethValue(hValue);
                    hSeekBarValue.setText(df.format(hValue));
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                vibrate(50);
                variables.isHSeekBarTouched = true;
                raspberryClient.startHSVConnection(getContext());
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                variables.isHSeekBarTouched = false;
                updateHSVPreferences();
            }
        });

        sSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if (fromUser){
                    sValue = progress;
                    variables.setsValue(sValue);
                    sSeekBarValue.setText(String.valueOf(sValue));
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                vibrate(50);
                variables.isSSeekBarTouched = true;
                raspberryClient.startHSVConnection(getContext());
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                variables.isSSeekBarTouched = false;
                updateHSVPreferences();
            }
        });

        vSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if (fromUser){
                    vValue = progress;
                    variables.setvValue(vValue);
                    vSeekBarValue.setText(String.valueOf(vValue));
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                vibrate(50);
                variables.isVSeekBarTouched = true;
                raspberryClient.startHSVConnection(getContext());
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                variables.isVSeekBarTouched = false;
                updateHSVPreferences();
            }
        });
    }

    private void setupEditText(){

        hSeekBarValue.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @SuppressLint("SetTextI18n")
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if(s != null || !Objects.requireNonNull(s).toString().isEmpty()) {
                    if(!variables.isHSeekBarTouched){
                        double value = ((!s.toString().equals("") && !s.toString().equals("."))? Double.parseDouble(String.valueOf(s)) : 0);
                        if(value > 360){
                            makeToast(getResources().getString(R.string.toast_h_over_range) + "!");
                        }else if(value < 0){
                            makeToast(getResources().getString(R.string.toast_h_under_range) + "!");
                        }else{
                            hValue = value;
                            variables.setvValue(hValue);
                            updateHSVPreferences();
                            hSeekBar.setProgress((int)((int) 100d / 360d *hValue));
                            raspberryClient.updateLEDColor(getContext());
                        }
                    }
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });

        sSeekBarValue.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @SuppressLint("SetTextI18n")
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

                if(s != null || !Objects.requireNonNull(s).toString().isEmpty()) {
                    if(!variables.isSSeekBarTouched){
                        Log.d(TAG, "string empty\"" + s + "\"");
                        double value = ((!s.toString().equals("") && !s.toString().equals("."))? Double.parseDouble(String.valueOf(s)) : 0);
                        if(value > 100){
                            makeToast(getResources().getString(R.string.toast_s_over_range) + "!");
                        }else if(value < 0){
                            makeToast(getResources().getString(R.string.toast_s_under_range) + "!");
                        }else{
                            sValue = value;
                            variables.setsValue(sValue);
                            updateHSVPreferences();
                            sSeekBar.setProgress((int) sValue);
                            raspberryClient.updateLEDColor(getContext());
                        }
                    }
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });

        vSeekBarValue.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @SuppressLint("SetTextI18n")
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

                if(s != null || !Objects.requireNonNull(s).toString().isEmpty() ^ !variables.isVSeekBarTouched) {
                    if(!variables.isVSeekBarTouched){
                        double value = ((!s.toString().equals("") && !s.toString().equals("."))? Double.parseDouble(String.valueOf(s)) : 0);
                        if(value > 100){
                            makeToast(getResources().getString(R.string.toast_v_over_range) + "!");
                        }else if(value < 0){
                            makeToast(getResources().getString(R.string.toast_v_under_range) + "!");
                        }else{
                            vValue = value;
                            variables.setvValue(vValue);
                            updateHSVPreferences();
                            vSeekBar.setProgress((int) vValue);
                            raspberryClient.updateLEDColor(getContext());
                        }
                    }
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
    }

    private void setupFavouriteColors() {
        for (int j = 0; j <= buttons.length - 1; j++){
            final int i = j;

            setButtonColor(buttons[i],
                    sharedPref.getFloat("favorites_button_h" + buttons[i].getId(), 180),
                    sharedPref.getFloat("favorites_button_s" + buttons[i].getId(), 100),
                    sharedPref.getFloat("favorites_button_v" + buttons[i].getId(), 0));

            buttons[i].setOnClickListener(v -> {
                variables.sethValue(sharedPref.getFloat("favorites_button_h" + buttons[i].getId(), 180));
                variables.setsValue(sharedPref.getFloat("favorites_button_s" + buttons[i].getId(), 100));
                variables.setvValue(sharedPref.getFloat("favorites_button_v" + buttons[i].getId(), 0));
                updateUI();
                vibrate(50);

                raspberryClient.updateLEDColor(getContext());

            });

            buttons[i].setOnLongClickListener(v -> {
                vibrate(150);

                saveColorToFavourites(buttons[i]);
                setButtonColor(buttons[i], (float) hValue, (float) sValue, (float) vValue);
                return false;
            });
        }
    }

    private void setButtonColor(Button button, float h, float s, float v){
        RoundRectShape rectShape = new RoundRectShape(new float[]{
                50, 50, 50, 50,
                50, 50, 50, 50
        }, null, null);
        ShapeDrawable shapeDrawable = new ShapeDrawable(rectShape);
        shapeDrawable.getPaint().setColor(Color.parseColor(hsvToRgb((float) h, (float) s, (float) v)));
        shapeDrawable.getPaint().setStyle(Paint.Style.FILL);
        shapeDrawable.getPaint().setAntiAlias(true);
        shapeDrawable.getPaint().setFlags(Paint.ANTI_ALIAS_FLAG);

        button.setForeground(shapeDrawable);
    }

    public static String hsvToRgb(float H, float S, float V) {
        float R, G, B;

        H /= 360f;
        S /= 100f;
        V /= 100f;

        if (S == 0) {
            R = V * 255;
            G = V * 255;
            B = V * 255;
        } else {
            float var_h = H * 6;
            if (var_h == 6)
                var_h = 0; // H must be < 1
            int var_i = (int) Math.floor((double) var_h); // Or ... var_i =
            // floor( var_h )
            float var_1 = V * (1 - S);
            float var_2 = V * (1 - S * (var_h - var_i));
            float var_3 = V * (1 - S * (1 - (var_h - var_i)));

            float var_r;
            float var_g;
            float var_b;
            if (var_i == 0) {
                var_r = V;
                var_g = var_3;
                var_b = var_1;
            } else if (var_i == 1) {
                var_r = var_2;
                var_g = V;
                var_b = var_1;
            } else if (var_i == 2) {
                var_r = var_1;
                var_g = V;
                var_b = var_3;
            } else if (var_i == 3) {
                var_r = var_1;
                var_g = var_2;
                var_b = V;
            } else if (var_i == 4) {
                var_r = var_3;
                var_g = var_1;
                var_b = V;
            } else {
                var_r = V;
                var_g = var_1;
                var_b = var_2;
            }

            R = var_r * 255; // RGB results from 0 to 255
            G = var_g * 255;
            B = var_b * 255;
        }

        String rs = Integer.toHexString((int) (R));
        String gs = Integer.toHexString((int) (G));
        String bs = Integer.toHexString((int) (B));

        if (rs.length() == 1)
            rs = "0" + rs;
        if (gs.length() == 1)
            gs = "0" + gs;
        if (bs.length() == 1)
            bs = "0" + bs;
        return "#" + rs + gs + bs;
    }

    public void loadPreferencesToVariables(){
        variables.sethValue(sharedPref.getFloat("hValue", 280));
        variables.setsValue(sharedPref.getFloat("sValue", 100));
        variables.setvValue(sharedPref.getFloat("vValue", 35));

        Log.d(TAG, "Reading HSV preferences  h:" + variables.hValue + " s:"+ variables.sValue + " v:" + variables.vValue);
    }

    public void updatePreferences(String key, String value){
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putString(key, value);
        editor.apply();
        Log.d(TAG, "Updated \"" + key + "\" preferences with:\"" + value + "\"");
    }

    public void updatePreferences(String key, Float value){
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putFloat(key, value);
        editor.apply();
        Log.d(TAG, "Updated \"" + key + "\" preferences with:\"" + value + "\"");
    }

    private void saveColorToFavourites(Button button) {
        updatePreferences("favorites_button_h" + button.getId(), (float) hValue);
        updatePreferences("favorites_button_s" + button.getId(), (float) sValue);
        updatePreferences("favorites_button_v" + button.getId(), (float) vValue);
    }

    public void updateHSVPreferences(){
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putFloat("hValue", (float) variables.hValue);
        editor.putFloat("sValue", (float) variables.sValue);
        editor.putFloat("vValue", (float) variables.vValue);
        editor.apply();
        Log.d(TAG, "Updated HSV preferences  h:" + variables.hValue + " s:"+ variables.sValue + " v:" + variables.vValue);
    }

    public void vibrate(int timeImMs){
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            vibrator.vibrate(VibrationEffect.createOneShot(timeImMs, VibrationEffect.DEFAULT_AMPLITUDE));
        } else {
            //deprecated in API 26
            vibrator.vibrate(timeImMs);
        }
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mViewModel = new ViewModelProvider(this).get(HsvControllerViewModel.class);


    }

    @SuppressLint("ShowToast")
    private void makeToast(String msg){
        Toast.makeText(getContext(), msg, LENGTH_LONG).show();
    }
}