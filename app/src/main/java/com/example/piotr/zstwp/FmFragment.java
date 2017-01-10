package com.example.piotr.zstwp;

import android.content.DialogInterface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AlertDialog;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
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
    private TextView deviceName;
    private TextView deviceType;
    private TextView deviceIp;
    private TextView ticketNumber;
    private TextView rackslot;
    private TextView shelf;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        view = inflater.inflate(R.layout.fragment_fm, container, false);

        getAlert();

        instructionsView = (TextView) view.findViewById(R.id.instructions_field);
        deviceName = (TextView) view.findViewById(R.id.device_name);
        deviceType = (TextView) view.findViewById(R.id.device_type);
        deviceIp = (TextView) view.findViewById(R.id.device_ip);
        ticketNumber = (TextView) view.findViewById(R.id.ticket_number);
        rackslot = (TextView) view.findViewById(R.id.rackslot);
        shelf = (TextView) view.findViewById(R.id.shelf);

        final Button confirmButton = (Button) view.findViewById(R.id.confirmButton);
        confirmButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                confirmAlert(v, true);
            }
        });

        final Button cancelButton = (Button) view.findViewById(R.id.cancelButton);
        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                confirmAlert(v, false);
            }
        });

        return view;
    }

    public void getAlert() {
        Log.d("Start", "pobierz alarm");
        new GetAlertsData().execute();
    }

    public void confirmAlert(View view, final Boolean status) {
        Log.d("Button", "Confirm");
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        final LayoutInflater inflater = getActivity().getLayoutInflater();
        final View inf = inflater.inflate(R.layout.dialog_fm_comment, null);
        final EditText commentField;
        TextView statusText;
        String statusInfo = "Fail";

        if (status) {
            statusInfo = "Success";
        }


        builder.setView(inf)
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {
                        EditText commentField = (EditText) inf.findViewById(R.id.commentField);
                        String comment = commentField.getText().toString();
                        Log.d("Czy się udało?", status.toString());
                        Log.d("Comment", comment);
                        goToSummary();
                    }
                })
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        Log.d("cancel", "cancel");
                    }
                });

        final AlertDialog commentDialog = builder.create();
        commentDialog.show();
        commentDialog.getButton(AlertDialog.BUTTON_POSITIVE).setEnabled(false);
        statusText = (TextView) commentDialog.findViewById(R.id.statusInfo);
        statusText.setText(statusInfo);
        commentField = (EditText) commentDialog.findViewById(R.id.commentField);
        commentField.addTextChangedListener(new TextWatcher() {

            @Override
            public void afterTextChanged(Editable s) {
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start,
                                          int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start,
                                      int before, int count) {
                commentDialog.getButton(AlertDialog.BUTTON_POSITIVE).setEnabled(s.length() != 0);
            }
        });
    }
    
    public void goToSummary(){
        Fragment fragmentSummary = new FmSummaryFragment();
        FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.fmStart, fragmentSummary);
        fragmentTransaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
        fragmentTransaction.addToBackStack(null);
        fragmentTransaction.commit();

        ViewGroup mContainer = (ViewGroup) getActivity().findViewById(R.id.fmStart);
        mContainer.removeAllViews();
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


