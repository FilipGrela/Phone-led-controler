package pl.filipgrela.ledcontroller.comunication;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Build;
import android.util.Log;

import androidx.annotation.RequiresApi;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.Socket;
import java.nio.charset.StandardCharsets;

import pl.filipgrela.ledcontroller.Variables;

public class RaspberryClient {

    Variables variables = Variables.getInstance();

    private static final String TAG = "RaspberryClient";

    private final String DEFAULT_IP = "192.168.8.137";
    private final int DEFAULT_PORT = 6061;
    private final int HEADER = 8;

    private Socket clientSocket;
    private PrintWriter out;
    private BufferedReader in;

    private SharedPreferences.Editor editor;
    private SharedPreferences pref;

    private Context context;

    public PrintWriter startConnection(Context context) throws IOException {
        Log.d(TAG, "Starting connection default settings");

        return startConnection(context,
                getPrefServerIp(context),
                getPrefServerPort(context));
    }

    int hostConnectionsTries = 0;
    public PrintWriter startConnection(Context context, String ip , int port) {
        this.context = context;
        out = null;

        if (isHostIsReachable(context, ip)) {
            hostConnectionsTries = 0;
            pref = context.getSharedPreferences("MyPref", Context.MODE_PRIVATE);
            editor = pref.edit();

            Log.d(TAG, "Starting connection on : " + ip + ":" + port);

            try {
                clientSocket = new Socket(ip, port);
                Log.d(TAG, "Connected");
            } catch (IOException e) {
                e.printStackTrace();
            }
            try {
                if (clientSocket != null) {
                    out = new PrintWriter(clientSocket.getOutputStream(), true);
                    in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
                }else{
                    Log.d(TAG, "Unable to send data");
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }else{
            hostConnectionsTries++;
            if(hostConnectionsTries <5){
                startConnection(context, ip, port);
            }
            hostConnectionsTries = 0;
            Log.d(TAG, "Host unreachable");
        }
        return out;
    }
    public void startHSVConnection(Context context){
        Log.d(TAG, "Starting hsv connection");
        //Replace below IP with the IP of that device in which server socket open.
        //If you change port then change the port number in the server side code also.
        Thread threadConnection = new Thread(() -> {
            try {
                //Replace below IP with the IP of that device in which server socket open.
                //If you change port then change the port number in the server side code also.
                PrintWriter out;
                out = startConnection(context);
                String lastMsg = "";
                do {
                    if (!lastMsg.equals("H_VAL" + variables.hValue + "S_VAL" + variables.sValue + "V_VAL" + variables.vValue)) {
                        lastMsg = "H_VAL" + variables.hValue + "S_VAL" + variables.sValue + "V_VAL" + variables.vValue;
                        sendMessage(context, out, "H_VAL" + variables.hValue + "S_VAL" + variables.sValue + "V_VAL" + variables.vValue);
                    }
                } while (variables.isHSeekBarTouched ^ variables.isSSeekBarTouched ^ variables.isVSeekBarTouched);
                sendMessage(context, out, "!DISCONNECT");
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
        threadConnection.start();
    }

    public void updateLEDColor(Context context){
        Thread thread = new Thread(() -> {
            try {
                //Replace below IP with the IP of that device in which server socket open.
                //If you change port then change the port number in the server side code also.
                PrintWriter out;
                out = startConnection(context);
                sendMessage(context, out,"H_VAL" + variables.hValue + "S_VAL" + variables.sValue + "V_VAL" + variables.vValue);
                sendMessage(context, out,"!DISCONNECT");
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
        thread.start();
    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    public void sendMessage(Context context, PrintWriter out, String msg) {
        this.context = context;
        if (isHostIsReachable(context, getPrefServerIp(context)) && out != null) {
            Log.d(TAG, "Message send.");

            byte[] bytes = msg.getBytes(StandardCharsets.UTF_8);
            String utf8EncodedMessage = new String(bytes, StandardCharsets.UTF_8);
            int msgLength = utf8EncodedMessage.length();

            bytes = String.valueOf(msgLength).getBytes(StandardCharsets.UTF_8);
            String msgLengthToSend = new String(bytes, StandardCharsets.UTF_8);

            msgLengthToSend = String.format("%-" + HEADER + "s", msgLengthToSend);

            out.print(msgLengthToSend);
            Log.d(TAG, "Msg leinht: '" + msgLengthToSend + "'");
            out.flush();
            out.print(msg);
            Log.d(TAG, msg);
            out.flush();
            if (msg.equals("!DISCONNECT")) {
                try {
                    stopConnection(out);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public void stopConnection(PrintWriter out) throws IOException {
        in.close();
        out.close();
        clientSocket.close();
        Log.d(TAG, "Connection closed.");
    }


    public void updateIP(String ip, int port) throws IOException {
        Log.d(TAG, "Server ip updating to:" + ip + ":" + port);

        editor.putString("Server_IP", ip);
        editor.putInt("Server_port", port);
        editor.apply();
    }

    private boolean isHostIsReachable(Context context, String ip){
        boolean reachable = false;
        try {
            InetAddress address = InetAddress.getByName(ip);
            reachable = address.isReachable(175);
        } catch (IOException e){
            e.printStackTrace();
        }

        return reachable;
    }

    public String getPrefServerIp(Context context){
        this.context = context;
        pref = context.getSharedPreferences("MyPref", Context.MODE_PRIVATE);

        return pref.getString("Server_IP", DEFAULT_IP);
    }

    public int getPrefServerPort(Context context){
        this.context = context;
        pref = context.getSharedPreferences("MyPref", Context.MODE_PRIVATE);

        return pref.getInt("Server_port", DEFAULT_PORT);
    }

    //TODO Zrobić wysyłanie wiadomości wyłaczającej program na rpi oraz rpi (EXIT_MSG = "Az!DaKYJ,2LvN=]s{R@];4))#Aj-hub<tuP4D+^S8RN,Yb+r_+")
}
