package pl.filipgrela.ledcontroller;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.VibrationEffect;
import android.os.Vibrator;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.widget.EditText;
import android.widget.SeekBar;
import android.widget.Toast;

import java.io.IOException;
import java.text.DecimalFormat;

import pl.filipgrela.ledcontroller.comunication.RaspberryClient;

import static android.widget.Toast.LENGTH_LONG;

public class MainActivity extends AppCompatActivity {
    private String TAG = "MainActivity";

    private Vibrator vibrator;

    private RaspberryClient raspberryClient;

    private DecimalFormat df = new DecimalFormat("#.#");

    private EditText hSeekBarValue;
    private EditText sSeekBarValue;
    private EditText vSeekBarValue;

    private SeekBar hSeekBar;
    private SeekBar sSeekBar;
    private SeekBar vSeekBar;

    private double hValue = 180;
    private double sValue = 100;
    private double vValue = 30;

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        vibrator = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);

        raspberryClient = new RaspberryClient();

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

        updadeLEDColor();
        setupSeekBars();
        setupEditText();
    }

    private void setupSeekBars(){
        hSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                hValue = 360.0*(Double.valueOf(progress)/100.0);

                hSeekBarValue.setText(String.valueOf(df.format(hValue)));
                updadeLEDColor();
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                vibrate(50);
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });

        sSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                sValue = progress;

                sSeekBarValue.setText(String.valueOf(sValue));
                updadeLEDColor();
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                vibrate(50);
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });

        vSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                vValue = progress;

                vSeekBarValue.setText(String.valueOf(vValue));
                updadeLEDColor();
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                vibrate(50);
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

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

                if(s == null || !s.toString().isEmpty()) {
                    double value = Double.parseDouble(String.valueOf(s));
                    if(value > 360){
                        makeToast("Max value for H is 360");
                        hSeekBarValue.setText("360");
                    }else if(value < 0){
                        makeToast("Min value for H is 0");
                        hSeekBarValue.setText("0");
                    }else{
                        hValue = value;
                        updadeLEDColor();
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

                if(s == null || !s.toString().isEmpty()) {
                    double value = Double.parseDouble(String.valueOf(s));
                    if(value > 100){
                        makeToast("Max value for S is 100");
                        sSeekBarValue.setText("100");
                    }else if(value < 0){
                        makeToast("Min value for H is 0");
                        sSeekBarValue.setText("0");
                    }else{
                        sValue = value;
                        updadeLEDColor();
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

                if(s == null || !s.toString().isEmpty()) {
                    double value = Double.parseDouble(String.valueOf(s));
                    if(value > 100){
                        makeToast("Max value for H is 100");
                        vSeekBarValue.setText("100");
                    }else if(value < 0){
                        makeToast("Min value for H is 0");
                        vSeekBarValue.setText("0");
                    }else{
                        vValue = value;
                        updadeLEDColor();
                    }
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });
    }

    public void vibrate(int timeImMs){
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            vibrator.vibrate(VibrationEffect.createOneShot(timeImMs, VibrationEffect.DEFAULT_AMPLITUDE));
        } else {
            //deprecated in API 26
            vibrator.vibrate(timeImMs);
        }
    }

    private void updadeLEDColor(){
        final Handler handler = new Handler();
        Thread thread = new Thread(new Runnable() {
            @RequiresApi(api = Build.VERSION_CODES.KITKAT)
            @Override
            public void run() {
                try {
                    //Replace below IP with the IP of that device in which server socket open.
                    //If you change port then change the port number in the server side code also.
                    raspberryClient.startConnection(getApplicationContext());
                    raspberryClient.sendMessage("H_VAL" + hValue + "S_VAL" + sValue + "V_VAL" + vValue);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });
        thread.start();
    }

    @SuppressLint("ShowToast")
    private void makeToast(String msg){
        Toast.makeText(getApplicationContext(), msg, LENGTH_LONG).show();
    }

}