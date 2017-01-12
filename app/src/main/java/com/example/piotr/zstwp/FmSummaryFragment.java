package com.example.piotr.zstwp;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.CardView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.concurrent.TimeUnit;

/**
 * Created by Maciek on 2017-01-10.
 */

public class FmSummaryFragment extends Fragment {
    View view;
    CardView cardClock;
    CardView cardSuccess;
    CardView cardFail;
    Button exitButton;
    Button callButton;
    Context thiscontext;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        view = inflater.inflate(R.layout.fragment_fm_summary, container, false);

        cardClock = (CardView) view.findViewById(R.id.cardClock);
        cardSuccess = (CardView) view.findViewById(R.id.cardSuccess);
        cardFail = (CardView) view.findViewById(R.id.cardFail);

        exitButton = (Button) view.findViewById(R.id.exitButton);
        callButton = (Button) view.findViewById(R.id.callButton);

        exitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                exit();
            }
        });

        callButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                exit();
                callNoc();
            }
        });

        final TextView clock = (TextView) view.findViewById(R.id.clock);
        new CountDownTimer(10000, 1000) {

            public void onTick(long milliseconds) {
                int seconds = (int) (milliseconds / 1000) % 60 ;
                int minutes = (int) ((milliseconds / (1000*60)) % 60);

                String s = Integer.toString(seconds);
                String m = Integer.toString(minutes);
                if (s.length() == 1) s = "0" + s;
                if (m.length() == 1) m = "0" + m;
                String displayedTime = m + ":" + s;
                clock.setText(displayedTime);
            }

            public void onFinish() {
                clock.setText("00:00");
                new CheckOtherDevices().execute();
            }
        }.start();

        return view;

    }

    public void exit() {
        //TODO: exit from FM tab
        /*Fragment fragmentFm = new FmFragment();
        FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.fmSummary, fragmentFm);
        fragmentTransaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
        fragmentTransaction.addToBackStack(null);
        fragmentTransaction.commit();

        ViewGroup mContainer = (ViewGroup) getActivity().findViewById(R.id.fmSummary);
        mContainer.removeAllViews();*/
    }

    public void callNoc() {
        Intent callIntent = new Intent(Intent.ACTION_DIAL);
        callIntent.setData(Uri.parse("tel:123456789"));
        startActivity(callIntent);
    }

    public void switchCards(boolean success) {
        cardClock.setVisibility(View.GONE);
        if (success) {
            cardSuccess.setVisibility(View.VISIBLE);
            exitButton.setVisibility(View.VISIBLE);
        } else {
            cardFail.setVisibility(View.VISIBLE);
            callButton.setVisibility(View.VISIBLE);
        }
    }

    private class CheckOtherDevices extends AsyncTask<Void, Void, String> {
        @Override
        protected String doInBackground(Void... params) {
            HttpURLConnection urlConnection = null;
            BufferedReader reader = null;
            String result = null;
            String ticketId = "T-ID-1427";

            try {
                URL url = new URL("http://zstwp.esy.es/zstwpApi/otherAlerts?TicketNumber=" + ticketId);
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
                    String success = jObject.getString("success");
                    switchCards(success.equals("true"));
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            } else {
                Toast.makeText(thiscontext, "There was an error, please try again.", Toast.LENGTH_LONG).show();
                switchCards(false);
            }
        }
    }

}
