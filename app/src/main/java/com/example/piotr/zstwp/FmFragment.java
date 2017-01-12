package com.example.piotr.zstwp;

import android.content.Context;
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
import android.widget.ProgressBar;
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

public class FmFragment extends Fragment {
    View view;
    private TextView instructionsView;
    private TextView deviceName;
    private TextView deviceType;
    private TextView deviceIp;
    private TextView ticketNumber;
    private TextView rackslot;
    private TextView shelf;
    private ProgressBar progressBar;
    private Button confirmButton;
    private Button cancelButton;

    private AlertDialog commentDialog;
    private ProgressBar dialogProgress;

    private String repairComment;

    Context thiscontext;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        view = inflater.inflate(R.layout.fragment_fm, container, false);
        thiscontext = container.getContext();

        getAlert();

        instructionsView = (TextView) view.findViewById(R.id.instructions_field);
        deviceName = (TextView) view.findViewById(R.id.device_name);
        deviceType = (TextView) view.findViewById(R.id.device_type);
        deviceIp = (TextView) view.findViewById(R.id.device_ip);
        ticketNumber = (TextView) view.findViewById(R.id.ticket_number);
        rackslot = (TextView) view.findViewById(R.id.rackslot);
        shelf = (TextView) view.findViewById(R.id.shelf);
        progressBar = (ProgressBar) view.findViewById(R.id.progressBar);

        confirmButton = (Button) view.findViewById(R.id.confirmButton);
        confirmButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                confirmAlert(v, true);
            }
        });

        cancelButton = (Button) view.findViewById(R.id.cancelButton);
        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                confirmAlert(v, false);
            }
        });

        confirmButton.setEnabled(false);
        cancelButton.setEnabled(false);

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
                .setPositiveButton("OK", null)
                .setNegativeButton("Cancel", null);

        commentDialog = builder.create();
        commentDialog.show();
        commentDialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                EditText commentText = (EditText) commentDialog.findViewById(R.id.commentField);
                repairComment = commentText.getText().toString();

                dialogProgress = (ProgressBar) commentDialog.findViewById(R.id.dialogProgressBar);
                dialogProgress.setVisibility(View.VISIBLE);

                //Disable canceling dialog
                v.setEnabled(false);
                commentText.setEnabled(false);
                commentDialog.getButton(AlertDialog.BUTTON_NEGATIVE).setEnabled(false);
                commentDialog.setCanceledOnTouchOutside(false);

                Log.d("Dialog", repairComment);
                new ConfirmRepair().execute();
            }
        });

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

    public void goToSummary() {
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
            String ticketId = "101";

            try {
                URL url = new URL("http://zstwp.esy.es/zstwpApi/detailsById?TicketId=" + ticketId);
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
                    instructionsView.setText(jObject.getString("message"));
                    deviceName.setText(jObject.getString("name"));
                    deviceType.setText(jObject.getString("type"));
                    deviceIp.setText(jObject.getString("ip"));
                    ticketNumber.setText(jObject.getString("number"));
                    rackslot.setText(jObject.getString("rackslot"));
                    shelf.setText(jObject.getString("shelf"));
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            } else {
                String errorMessage = "Błąd przy połączeniu z bazą. Odśwież";
                instructionsView.setText(errorMessage);
            }
            progressBar.setVisibility(View.GONE);
            confirmButton.setEnabled(true);
            cancelButton.setEnabled(true);
        }
    }

    private class ConfirmRepair extends AsyncTask<Void, Void, String> {
        @Override
        protected String doInBackground(Void... params) {
            HttpURLConnection urlConnection = null;
            BufferedReader reader = null;
            String result = null;
            String ticketId = "101";
            String personId = "2001";

            try {
                URL url = new URL("http://zstwp.esy.es/zstwpApi/updateById?TicketId=" + ticketId +
                        "&PersonId=" + personId + "&message=" + repairComment);
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
                    if (success.equals("true")) {
                        Log.d("Udało się", success);
                        goToSummary();
                    } else {
                        Toast.makeText(thiscontext, "There was an error, please try again.", Toast.LENGTH_LONG).show();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            } else {
                Toast.makeText(thiscontext, "There was an error, please try again.", Toast.LENGTH_LONG).show();
            }
            dialogProgress.setVisibility(View.GONE);
            commentDialog.dismiss();
        }
    }
}


