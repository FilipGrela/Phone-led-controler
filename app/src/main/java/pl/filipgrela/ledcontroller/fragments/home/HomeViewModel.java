package pl.filipgrela.ledcontroller.fragments.home;

import android.content.SharedPreferences;
import android.os.Vibrator;

import androidx.lifecycle.ViewModel;

import java.text.DecimalFormat;

import pl.filipgrela.ledcontroller.Variables;
import pl.filipgrela.ledcontroller.comunication.RaspberryClient;

public class HomeViewModel extends ViewModel {
    private final String TAG = "HomeViewModel";

    SharedPreferences sharedPref;
    Variables variables = Variables.getInstance();

    private Vibrator vibrator;

    private RaspberryClient raspberryClient;

    private final DecimalFormat df = new DecimalFormat("#.#");

}