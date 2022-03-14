package pl.filipgrela.ledcontroller.settings;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;

import androidx.fragment.app.DialogFragment;

import pl.filipgrela.ledcontroller.R;
import pl.filipgrela.ledcontroller.comunication.RaspberryClient;

public class ShutdownDialog extends DialogFragment {

    Context context;

    public ShutdownDialog(Context context){
        this.context = context;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        // Use the Builder class for convenient dialog construction
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setIcon(R.drawable.shut_down);
        builder.setTitle(R.string.attention);
        builder.setMessage(R.string.alertShutdown)
                .setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        new RaspberryClient().sendCustomMessage(context, "g3VH$wPq~2?(;MhF#5vaG{/\"eUAf]+`ynCd8z!DJxcXsSj=r)%");
                    }
                })
                .setNeutralButton(R.string.alertOffProgram, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        new RaspberryClient().sendCustomMessage(context, "fW;X#.`V:BhGg&-UT*?'+}FxY]c9<%S3w![Kk\"/E2rHNzD)QqM");
                    }
                })
                .setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        // User cancelled the dialog
                    }
                });
        // Create the AlertDialog object and return it
        return builder.create();
    }
}
