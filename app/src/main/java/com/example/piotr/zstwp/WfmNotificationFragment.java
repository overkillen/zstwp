package com.example.piotr.zstwp;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import android.util.Pair;
import android.os.Handler;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.DataOutput;
import java.io.DataOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import java.util.logging.LogRecord;

import static android.content.ContentValues.TAG;


/**
 * Created by Jakub on 2017-01-05.
 */


public class WfmNotificationFragment extends Fragment implements OnBackPressedListener {
    View view;


    private Button navigateButton;
    private Button endWorkButton;
    private TextView alertInfo;

    Context thiscontext;

    private double destinationLatitude;
    private double destinationLongitude;
    private int localServicemanID;

    private GPSTracker gps;
    private boolean sendData = false;
    private boolean sendDataFunctionIsActive = false;
    private boolean getNotificationData = false;
    private boolean taskForServiceman = false;
    private boolean getNotificationDataFunctionIsActive = false;
    private String nowWorking = "no";


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        view = inflater.inflate(R.layout.fragment_wfm_notification, container, false);
        thiscontext = container.getContext();


        gps = new GPSTracker(thiscontext);


        if (gps.canGetLocation()) {

        } else {
            // can't get location
            // GPS or Network is not enabled
            // Ask user to enable GPS/network in settings
            gps.showSettingsAlert();
        }

        // na razie jakies wspolrzedne na sztywno ;)
        destinationLatitude = 50.037067;
        destinationLongitude = 19.870662;

        SharedPreferences myPrefs = thiscontext.getSharedPreferences("myPrefs", Context.MODE_WORLD_READABLE);
        setLocalServicemanID(Integer.parseInt(myPrefs.getString("MEM1", "0000")));

        navigateButton = (Button) view.findViewById(R.id.navigateButton);
        endWorkButton = (Button) view.findViewById(R.id.endWorkButton);
        alertInfo = (TextView) view.findViewById(R.id.last_alert_time_text);




        // Set a click listener for Fragment button
        navigateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //TODO
                //Tu pod zmiennymi destinationLatitude i destinationLongitude powinny byc
                // wspolrzedne miejsca awarii pobrane z bazy
                Intent intent = new Intent(android.content.Intent.ACTION_VIEW, Uri.parse("google.navigation:q=" + destinationLatitude + "," + destinationLongitude));
                startActivity(intent);

            }
        });

        endWorkButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        navigateButton.setEnabled(false);
        endWorkButton.setEnabled(true);


        if (!sendDataFunctionIsActive) {
            callAsynchronousTaskPostGPSCords();
        }

        if(!getNotificationData){
            callAsynchronousTaskGetNotificationData();
        }
        //return inflater.inflate(R.layout.fragment_wfm_notification, container, false);
        return view;

    }

    @Override
    public void onPause() {
        super.onPause();

        if (gps != null) {
            gps.stopUsingGPS();
        }
        // Remove object
        gps = null;
        sendData = false;
        getNotificationData = false;
    }

    public void onResume() {
        super.onResume();

        sendData = true; //po powrocie z  nawigacji sprawdzamy czy coś jest dla nasdo zrobienia, jeśli stare zgłoszenie nie jest naprawione to zablokujemy ponownie funkcję wysyłania

        if (!sendDataFunctionIsActive) {
            callAsynchronousTaskPostGPSCords();
        }
        if(!getNotificationData){
            callAsynchronousTaskGetNotificationData();
        }
    }


    public void onBackPressed() {
        if(!taskForServiceman) {
            Fragment fragmentStart = new WfmStartFragment();
            FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
            fragmentTransaction.replace(R.id.notification, fragmentStart);
            fragmentTransaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
            fragmentTransaction.addToBackStack(null);
            fragmentTransaction.commit();

            ViewGroup mContainer = (ViewGroup) getActivity().findViewById(R.id.notification);
            mContainer.removeAllViews();
        }else{

        }
    }

    private class PostGPSCords extends AsyncTask<Void, Void, String> {
        @Override

        protected String doInBackground(Void... params) {
            HttpURLConnection urlConnection = null;
            BufferedReader reader = null;
            String result = null;
            Double temporaryServicemanLatitude = null;
            Double temporaryServicemanLongitude = null;

            if (!(gps == null)) {
                temporaryServicemanLatitude = gps.getServisantLatitude();
                temporaryServicemanLongitude = gps.getServisantLongitude();
            } else {
                temporaryServicemanLatitude = 0.0;
                temporaryServicemanLongitude = 0.0;
            }

            try {
                URL url = new URL("http://zstwp.esy.es/zstwpWFM/test.php?temporaryServicemanLatitude=" + temporaryServicemanLatitude + "&temporaryServicemanLongitude=" + temporaryServicemanLongitude + "&servicemanID=" + getLocalServicemanID() + "&nowWorking=" + nowWorking);
                urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.setRequestMethod("GET");
                urlConnection.connect();

                InputStream inputStream = urlConnection.getInputStream();

                Log.d(TAG, "doInBackground: Została wywołana strona testowa");
            } catch (IOException e) {
                Log.e("PlaceholderFragment", "Error ", e);
                return null;
            } finally {
                if (urlConnection != null) {
                    urlConnection.disconnect();
                }
            }
            return null;
        }
       /* protected String doInBackground(Void... params) {
            HttpURLConnection urlConnection = null;
            String result = null;

            try {
                URL url = new URL("http://zstwp.esy.es/zstwpWFM/test.php");
                urlConnection = (HttpURLConnection) url.openConnection();
                //urlConnection.setReadTimeout(10000);
                //urlConnection.setConnectTimeout(15000);
                urlConnection.setDoOutput(true);
                //urlConnection.setDoInput(true);
                urlConnection.setUseCaches(false);
                urlConnection.setInstanceFollowRedirects(false);
                urlConnection.setRequestMethod("POST");
                //urlConnection.setRequestProperty("Content-Type", "application/json");
                urlConnection.setRequestProperty( "Content-Type", "application/x-www-form-urlencoded");
                urlConnection.setRequestProperty( "charset", "utf-8");
                //urlConnection.setRequestProperty("Host", "android.piotr.zstwp");
                //urlConnection.setRequestProperty("Accept", "application/json");


                urlConnection.connect();
                Log.d(TAG, "doInBackground: Mamy połączenie");

                JSONObject temporaryData = new JSONObject();

                try {
                    if(!(gps == null)) {
                        temporaryData.put("temporaryServicemanLatitude", gps.getServisantLatitude());
                        temporaryData.put("temporaryServicemanLongitude", gps.getServisantLongitude());
                    }else{
                        temporaryData.put("temporaryServicemanLatitude", 0);
                        temporaryData.put("temporaryServicemanLongitude", 0);
                    }
                    temporaryData.put("servicemanID", getLocalServicemanID());
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                DataOutputStream wr = new DataOutputStream(urlConnection.getOutputStream());

                wr.write(temporaryData.toString().getBytes("utf-8"));
                wr.flush();
                wr.close();

                Log.d(TAG, "doInBackground: Dane zostały wysłane");
            } catch (IOException e) {
                Log.e(TAG, "doInBackground: Nie można wysłać danych, może nie ma sieci", e);
                return null;
            } finally {
                if (urlConnection != null) {
                    urlConnection.disconnect();
                }
            }
            return result;
        }*/

        @Override
        protected void onPostExecute(String s) {
        }
    }

    private class GetNotificationData extends AsyncTask<Void, Void, String> {
        @Override
        protected String doInBackground(Void... params) {
            HttpURLConnection urlConnection = null;
            BufferedReader reader = null;
            String result = null;

            try {
                URL url = new URL("http://zstwp.esy.es/zstwpWFM/test2.php?ServicemanId=" + getLocalServicemanID());
                urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.setRequestMethod("GET");
                urlConnection.connect();

                InputStream inputStream = urlConnection.getInputStream();
                StringBuffer buffer = new StringBuffer();

                if (inputStream == null) {
                    return null;
                }

                reader = new BufferedReader(new InputStreamReader(inputStream));

                String line = "";
                while ((line = reader.readLine()) != null) {
                    buffer.append(line);
                }

                if (buffer.length() == 0) {
                    return null;
                }

                result = buffer.toString();
                return result;
            } catch (IOException e) {
                Log.e("PlaceholderFragment", "Error ", e);
                return null;
            } finally {
                if (urlConnection != null) {
                    urlConnection.disconnect();
                }
                if (reader != null) {
                    try {
                        reader.close();
                    } catch (final IOException e) {
                        Log.e("PlaceholderFragment", "Error closing stream", e);
                    }
                }
            }
        }

        @Override
        protected void onPostExecute(String s) {
            if (s != null && !s.equals("null")) {
                Log.i("Odpowiedz ", s);
                JSONObject jObject = null;
                try {
                    jObject = new JSONObject(s);
                    Log.i("message", jObject.getString("message"));
                    alertInfo.setText(jObject.getString("message"));
                    alertInfo.append(" serID: " + jObject.getString("whoSend"));
                    if(jObject.getString("toME").contains("Yes")){
                        Log.d(TAG, "onPostExecute: mam zadanie do wykonania");
                        nowWorking = "yes";
                        setDestinationLatitude(Double.parseDouble(jObject.getString("latitude")));
                        setDestinationLongitude(Double.parseDouble(jObject.getString("longitude")));
                        taskForServiceman = true;  //blokada wyjścia z aplikacji gdy serwisant ma zgłoszenie
                        sendData = false; //zatrzymujemy wysyłanie danych aktualizujących położenie serwisanta do centrali
                        //getNotificationData = false;
                        if(jObject.getString("isRepair").contains("Yes")) {
                            endWorkButton.setEnabled(true);
                            navigateButton.setEnabled(false);
                            nowWorking = "no";

                        }else{
                            endWorkButton.setEnabled(false);
                            navigateButton.setEnabled(true);
                        }

                    }else{
                        Log.d(TAG, "onPostExecute: nie mam zadania do wykonania");
                        nowWorking = "no";
                        endWorkButton.setEnabled(true);
                        navigateButton.setEnabled(false);
                    }
                    /*if(jObject.getString("isRepair").contains("Yes")) {
                        Log.d(TAG, "onPostExecute: naporawiono usterkę");
                        if (!sendDataFunctionIsActive) {
                            callAsynchronousTaskPostGPSCords();
                        }
                        endWorkButton.setEnabled(true);
                        navigateButton.setEnabled(false);
                        taskForServiceman = false;

                    }else{
                        Log.d(TAG, "onPostExecute: Jeszcze nie naprawiono usterki");
                    } */
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            } else {
                String errorMessage = "Błąd przy połączeniu z bazą. Poczekaj na aktualizację";
                alertInfo.setText(errorMessage);
            }
        }
    }

    public void callAsynchronousTaskPostGPSCords() {
        sendDataFunctionIsActive = true;
        sendData = true;
        final Handler handler = new Handler();
        final Timer timer = new Timer();
        TimerTask doAsynchronousTask = new TimerTask() {
            @Override
            public void run() {
                handler.post(new Runnable() {
                    public void run() {
                        if (sendData) {
                            try {
                                PostGPSCords postGPSCords = new PostGPSCords();
                                postGPSCords.execute();
                            } catch (Exception e) {

                            }
                        } else {
                            Log.d(TAG, "run: Przerywamy wysyłanie danych");
                            timer.cancel();
                            timer.purge();
                            sendDataFunctionIsActive = false;

                        }
                    }

                });
            }
        };
        timer.schedule(doAsynchronousTask, 0, 7000);
    }

    public void callAsynchronousTaskGetNotificationData()
    {
        getNotificationDataFunctionIsActive = true;
        getNotificationData = true;
        final Handler handler = new Handler();
        final Timer timer = new Timer();
        TimerTask doAsynchronousTask = new TimerTask() {
            @Override
            public void run() {
                handler.post(new Runnable() {
                    public void run() {
                        if (sendData) {
                            try {
                                GetNotificationData getNotificationData = new GetNotificationData();
                                getNotificationData.execute();
                            } catch (Exception e) {

                            }
                        } else {
                            Log.d(TAG, "run: Przerywamy odbieranie danych");
                            timer.cancel();
                            timer.purge();
                            getNotificationDataFunctionIsActive = false;

                        }
                    }

                });
            }
        };
        timer.schedule(doAsynchronousTask, 0, 7000);
    }

    private synchronized void setDestinationLatitude(double d){
        destinationLatitude = d;
    };
    private synchronized void setDestinationLongitude(double d){
        destinationLongitude = d;
    };
    private synchronized void setLocalServicemanID(int d){
        localServicemanID = d;
    };
    private synchronized double getDestinationLatitude(){
        return destinationLatitude;
    };
    private synchronized double getDestinationLongitude(){
        return destinationLongitude;
    };
    private synchronized int getLocalServicemanID() {
        return localServicemanID;
    };

}