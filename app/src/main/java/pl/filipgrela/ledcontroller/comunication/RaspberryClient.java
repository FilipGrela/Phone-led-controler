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
import java.net.Socket;
import java.nio.charset.StandardCharsets;

public class RaspberryClient {

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

    public void startConnection(Context context) throws IOException {
        Log.d(TAG, "Starting connection dealt settings");
        pref = context.getSharedPreferences("MyPref", Context.MODE_PRIVATE);

        startConnection(context,
                pref.getString("Server_IP", DEFAULT_IP),
                pref.getInt("Server_port", DEFAULT_PORT));
    }

    public void startConnection(Context context, String ip , int port) {
        this.context = context;

        pref = context.getSharedPreferences("MyPref", Context.MODE_PRIVATE);
        editor = pref.edit();

        Log.d(TAG, "Starting connection on : " + ip + ":" + port);

        try {
            clientSocket = new Socket(ip, port);
        } catch (IOException e) {
            e.printStackTrace();
        }
        Log.d(TAG, "Connected");
        try {
            out = new PrintWriter(clientSocket.getOutputStream(), true);
            in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    public void sendMessage(String msg) {
        Log.d(TAG, "Message send.");

        byte[] bytes = msg.getBytes(StandardCharsets.UTF_8);
        String utf8EncodedMessage = new String(bytes, StandardCharsets.UTF_8);
        int msgLength = utf8EncodedMessage.length();

        bytes = String.valueOf(msgLength).getBytes(StandardCharsets.UTF_8);
        String utf8EncodedMessageLength = new String(bytes, StandardCharsets.UTF_8);
        String msgLengthToSend = utf8EncodedMessageLength;

        msgLengthToSend = String.format("%-" + HEADER + "s", msgLengthToSend);

        out.print(msgLengthToSend);
        Log.d(TAG, "Msg leinht: '" + msgLengthToSend + "'");
        out.flush();
        out.print(msg);
        Log.d(TAG, msg);
        out.flush();
        if(!msg.equals("!DISCONNECT")){
            sendMessage("!DISCONNECT");
        }else if (msg.equals("!DISCONNECT")){
            try {
                stopConnection();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public void stopConnection() throws IOException {
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

    //TODO Zrobić wysyłanie wiadomości wyłaczającej program na rpi oraz rpi (EXIT_MSG = "Az!DaKYJ,2LvN=]s{R@];4))#Aj-hub<tuP4D+^S8RN,Yb+r_+")
}
