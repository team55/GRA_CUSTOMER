package ru.team55.network;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;

import static java.lang.Thread.sleep;


public class NetHelper {

    public static final String TAG = NetHelper.class.getSimpleName();

    /* Таймауты */
    public static final int TIME_5S = 5000;
    public static final int TIME_10S = 10000;


    /* Коды ошибок */
    public static final int CONNECTION_FAILED = 100;
    public static final int FAILED_TIMEOUT = 101;
    public static final int INTERRUPTED = 102;



    public static void networkEnableIfDisabled(Context context){
        context.startActivity(new Intent("android.settings.AIRPLANE_MODE_SETTINGS"));
    }

    public static boolean networkIsEnabled(Context context){

        return true;
    }

    public static boolean gpsIsEnabled(Context context){

        return true;
    }

    public static boolean networkIsConnected(Context context){

        return true;
    }





    public static void readyNetwork(NetworkReadyCallback callback, final String url, final int timeout){

        final boolean[] responded = {false};

        new Thread("CheckNetworkChild") {

            @Override
            public void run() {
                Log.d(TAG, "Старт проверки доступности хоста");
                HttpGet requestForTest = new HttpGet(url);
                try {
                    new DefaultHttpClient().execute(requestForTest); // can last...
                    responded[0] = true;
                } catch (Exception e) {
                    //тут ничего не делаем - в цикле повторит вызов
                    Log.e(TAG, "Старт проверки доступности хоста - ошибка:"+e.getLocalizedMessage());
                }

            }

        }.start();


        try {

            //Ждем таймаута
            int waited = 0;
            while(!responded[0] && (waited < timeout)) {
                sleep(500);
                if(!responded[0]) {
                    waited += 500;
                }
                Log.d(TAG, "Цикл ожидания доступности хоста");
            }


        }
        catch(InterruptedException e) {
            callback.OnError(102,"Thread interrupted");
        }
        finally {
            if (!responded[0]){
                Log.w(TAG, "Превышено время ожидания хоста");
                callback.OnError(101,"Превышено врямя ожидания");
            }
            else{
                Log.d(TAG, "Хост получен");
                callback.Ready();
            }

        }

    }


}
