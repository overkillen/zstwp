package com.example.piotr.zstwp;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class FmFragment extends Fragment {
    View view;
    private TextView instructionsView;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        view = inflater.inflate(R.layout.fragment_fm, container, false);

        getAlert();

        instructionsView = (TextView) view.findViewById(R.id.instructions_field);
        Button confirmButton = (Button) view.findViewById(R.id.confirmButton);
        confirmButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                confirmAlert(v);
            }
        });

        return view;
    }

    public void getAlert() {
        Log.d("Start", "pobierz alarm");
        new GetAlertsData().execute();
    }

    public void confirmAlert(View view) {
        Log.d("Button", "Confirm");
        getAlert();
    }

    private class GetAlertsData extends AsyncTask<Void, Void, String> {
        @Override
        protected String doInBackground(Void... params) {
            HttpURLConnection urlConnection = null;
            BufferedReader reader = null;
            String result = null;

            try {
                URL url = new URL("http://api.icndb.com/jokes/random?firstName=John&amp;lastName=Doe");
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
            if (s != null) {
                Log.i("json", s);
                JSONObject jObject = null;
                try {
                    jObject = new JSONObject(s);
                    String instructions = jObject.getJSONObject("value").getString("joke");
                    instructionsView.setText(instructions);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            } else {
                String errorMessage = "Błąd przy połączeniu z bazą. Odśwież";
                instructionsView.setText(errorMessage);
            }
        }
    }
}


