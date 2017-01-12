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
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
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
    private boolean getNotificationDataFunctionIsActive = false;


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
        localServicemanID = 2001;

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
        if (!sendDataFunctionIsActive) {
            callAsynchronousTaskPostGPSCords();
        }
        if(!getNotificationData){
            callAsynchronousTaskGetNotificationData();
        }
    }


    public void onBackPressed() {
        Fragment fragmentStart = new WfmStartFragment();
        FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.notification, fragmentStart);
        fragmentTransaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
        fragmentTransaction.addToBackStack(null);
        fragmentTransaction.commit();

        ViewGroup mContainer = (ViewGroup) getActivity().findViewById(R.id.notification);
        mContainer.removeAllViews();
    }

    private class PostGPSCords extends AsyncTask<Void, Void, String> {
        @Override
        protected String doInBackground(Void... params) {
            HttpURLConnection urlConnection = null;
            String result = null;

            try {
                URL url = new URL("http://zstwp.esy.es/zstwpWFM/test.php");
                urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.setReadTimeout(10000);
                urlConnection.setConnectTimeout(15000);
                urlConnection.setDoOutput(true);
                urlConnection.setDoInput(true);
                urlConnection.setRequestProperty("Content-Type", "application/json");
                urlConnection.setRequestProperty("Accept", "application/json");
                urlConnection.setRequestMethod("POST");

                urlConnection.connect();
                Log.d(TAG, "doInBackground: Mamy połączenie");

                JSONObject temporaryData = new JSONObject();

                try {
                    temporaryData.put("temporaryServicemanLatitude", getDestinationLatitude());
                    temporaryData.put("temporaryServicemanLongitude", getDestinationLongitude());
                    temporaryData.put("servicemanID", getLocalServicemanID());
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                OutputStreamWriter wr = new OutputStreamWriter(urlConnection.getOutputStream());
                wr.write(temporaryData.toString());
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
        }

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
                URL url = new URL("http://zstwp.esy.es/zstwpApi/test2.php");
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
        timer.schedule(doAsynchronousTask, 0, 5000);
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
        timer.schedule(doAsynchronousTask, 0, 5000);
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