package com.example.piotr.zstwp;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import java.io.File;


public class WfmNotificationFragment extends Fragment {
    View view;

    private Button navigateButton;

    Context thiscontext;

    private double destinationLatitude;
    private double destinationLongitude;

    private GPSTracker gps;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        view = inflater.inflate(R.layout.fragment_wfm_notification, container, false);
        thiscontext = container.getContext();


        gps = new GPSTracker(thiscontext);


        if(gps.canGetLocation()){

        } else {
            // can't get location
            // GPS or Network is not enabled
            // Ask user to enable GPS/network in settings
            gps.showSettingsAlert();
        }

        // na razie jakies wspolrzedne na sztywno ;)
        destinationLatitude = 50.037067;
        destinationLongitude = 19.870662;

        navigateButton = (Button) view.findViewById(R.id.navigateButton);

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
    }


    public void onBackPressed(){
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


}
