package pl.filipgrela.ledcontroller;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentTransaction;
import androidx.preference.PreferenceFragmentCompat;
import androidx.preference.PreferenceScreen;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.VibrationEffect;
import android.os.Vibrator;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.Toast;

import java.io.IOException;
import java.io.PrintWriter;
import java.text.DecimalFormat;

import pl.filipgrela.ledcontroller.comunication.RaspberryClient;
import pl.filipgrela.ledcontroller.settings.SettingsActivity;
import pl.filipgrela.ledcontroller.settings.SettingsFragment;

import static android.widget.Toast.LENGTH_LONG;

public class MainActivity extends AppCompatActivity implements
        PreferenceFragmentCompat.OnPreferenceStartScreenCallback {
    private final String TAG = "MainActivity";

    SettingsActivity settingsActivity;

    private Vibrator vibrator;

    private RaspberryClient raspberryClient;

    private final DecimalFormat df = new DecimalFormat("#.#");

    private LinearLayout linearLayout;

    private EditText hSeekBarValue;
    private EditText sSeekBarValue;
    private EditText vSeekBarValue;

    private SeekBar hSeekBar;
    private SeekBar sSeekBar;
    private SeekBar vSeekBar;

    private boolean isHSeekBarTouched = false;
    private boolean isSSeekBarTouched = false;
    private boolean isVSeekBarTouched = false;

    private double hValue = 180;
    private double sValue = 100;
    private double vValue = 30;

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        settingsActivity = new SettingsActivity();

        vibrator = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);

        raspberryClient = new RaspberryClient();

        linearLayout = findViewById(R.id.activity_main_linear_layout);

        hSeekBar = findViewById(R.id.HSeekbar);
        sSeekBar = findViewById(R.id.SSeekbar);
        vSeekBar = findViewById(R.id.VSeekbar);

        hSeekBarValue = findViewById(R.id.HSeekbarValue);
        sSeekBarValue = findViewById(R.id.SSeekbarValue);
        vSeekBarValue = findViewById(R.id.VSeekbarValue);


        hSeekBarValue.setText(String.valueOf(hValue));
        sSeekBarValue.setText(String.valueOf(sValue));
        vSeekBarValue.setText(String.valueOf(vValue));

        hSeekBar.setProgress((int) (hValue/360*100));
        sSeekBar.setProgress((int) (sValue));
        vSeekBar.setProgress((int) (vValue));

        //TODO Zrobić zapamiętywanie ostatnio wybarnego koloru, oraz ładowanie przys starcie.
        setupKeyboardHide();
        setupSeekBars();
        setupEditText();
    }

    private void setupKeyboardHide(){
        linearLayout.setOnTouchListener((v, event) -> {
            InputMethodManager imm = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(linearLayout.getWindowToken(), InputMethodManager.RESULT_UNCHANGED_SHOWN);

            hSeekBarValue.clearFocus();
            sSeekBarValue.clearFocus();
            vSeekBarValue.clearFocus();
            return true;
        });
    }

    private void setupSeekBars(){
        hSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if (fromUser){
                    hValue = 360.0*((double) progress /100.0);
                    hSeekBarValue.setText(df.format(hValue));
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                vibrate(50);
                isHSeekBarTouched = true;
                startConnection();
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                isHSeekBarTouched = false;
            }
        });

        sSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if (fromUser){
                    sValue = progress;
                    sSeekBarValue.setText(String.valueOf(sValue));
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                vibrate(50);
                isSSeekBarTouched = true;
                startConnection();
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                isSSeekBarTouched = false;
            }
        });

        vSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if (fromUser){
                    vValue = progress;
                    vSeekBarValue.setText(String.valueOf(vValue));
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                vibrate(50);
                isVSeekBarTouched = true;
                startConnection();
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                isVSeekBarTouched = false;
            }
        });
    }

    private void setupEditText(){

        hSeekBarValue.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if(s != null || !s.toString().isEmpty()) {
                    if(!isHSeekBarTouched){
                        double value = Double.parseDouble(String.valueOf(s));
                        if(value > 360){
                            makeToast("Max value for H is 360");
                            hSeekBarValue.setText("360");
                        }else if(value < 0){
                            makeToast("Min value for H is 0");
                            hSeekBarValue.setText("0");
                        }else{
                            hValue = value;
                            hSeekBar.setProgress((int)((int) 100d / 360d *hValue));
                            updateLEDColor();
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

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

                if(s != null || !s.toString().isEmpty()) {
                    if(!isSSeekBarTouched){
                        double value = Double.parseDouble(String.valueOf(s));
                        if(value > 100){
                            makeToast("Max value for S is 100");
                            sSeekBarValue.setText("100");
                        }else if(value < 0){
                            makeToast("Min value for H is 0");
                            sSeekBarValue.setText("0");
                        }else{
                            sValue = value;
                            sSeekBar.setProgress((int) sValue);
                            updateLEDColor();
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

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

                if(s != null || !s.toString().isEmpty() ^ !isVSeekBarTouched) {
                    if(!isVSeekBarTouched){
                        double value = Double.parseDouble(String.valueOf(s));
                        if(value > 100){
                            makeToast("Max value for H is 100");
                            vSeekBarValue.setText("100");
                        }else if(value < 0){
                            makeToast("Min value for H is 0");
                            vSeekBarValue.setText("0");
                        }else{
                            vValue = value;
                            vSeekBar.setProgress((int) vValue);
                            updateLEDColor();
                        }
                    }
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });
    }

    //TODO przerzucić wątek do RaspberryClient
    private void startConnection(){
        //Replace below IP with the IP of that device in which server socket open.
        //If you change port then change the port number in the server side code also.
        Thread threadConnection = new Thread(() -> {
            try {
                //Replace below IP with the IP of that device in which server socket open.
                //If you change port then change the port number in the server side code also.
                PrintWriter out;
                out = raspberryClient.startConnection(getApplicationContext());
                String lastMsg = "";
                do {
                    if (!lastMsg.equals("H_VAL" + hValue + "S_VAL" + sValue + "V_VAL" + vValue)) {
                        lastMsg = "H_VAL" + hValue + "S_VAL" + sValue + "V_VAL" + vValue;
                        raspberryClient.sendMessage(getApplicationContext(), out, "H_VAL" + hValue + "S_VAL" + sValue + "V_VAL" + vValue);
                    }
                } while (isHSeekBarTouched ^ isSSeekBarTouched ^ isVSeekBarTouched);
                raspberryClient.sendMessage(getApplicationContext(), out, "!DISCONNECT");
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
        threadConnection.start();
    }

    public void vibrate(int timeImMs){
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            vibrator.vibrate(VibrationEffect.createOneShot(timeImMs, VibrationEffect.DEFAULT_AMPLITUDE));
        } else {
            //deprecated in API 26
            vibrator.vibrate(timeImMs);
        }
    }

    //TODO przerzucić wątek do RaspberryClient
    private void updateLEDColor(){
        Thread thread = new Thread(() -> {
            try {
                //Replace below IP with the IP of that device in which server socket open.
                //If you change port then change the port number in the server side code also.
                PrintWriter out;
                out = raspberryClient.startConnection(getApplicationContext());
                raspberryClient.sendMessage(getApplicationContext(), out,"H_VAL" + hValue + "S_VAL" + sValue + "V_VAL" + vValue);
                raspberryClient.sendMessage(getApplicationContext(), out,"!DISCONNECT");
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
        thread.start();
    }

    @SuppressLint("ShowToast")
    private void makeToast(String msg){
        Toast.makeText(getApplicationContext(), msg, LENGTH_LONG).show();
    }

    @Override
    public boolean onPreferenceStartScreen(PreferenceFragmentCompat preferenceFragmentCompat,
                                           PreferenceScreen preferenceScreen) {
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        SettingsFragment fragment = new SettingsFragment();
        Bundle args = new Bundle();
        args.putString(PreferenceFragmentCompat.ARG_PREFERENCE_ROOT, preferenceScreen.getKey());
        fragment.setArguments(args);
        ft.add(R.id.fragment_container, fragment, preferenceScreen.getKey());
        ft.addToBackStack(preferenceScreen.getKey());
        ft.commit();
        return true;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @SuppressLint("NonConstantResourceId")
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        switch (id) {
            case R.id.action_menu:
                startActivity(new Intent(this, SettingsActivity.class));
                return true;
        }

        return super.onOptionsItemSelected(item);
    }
}