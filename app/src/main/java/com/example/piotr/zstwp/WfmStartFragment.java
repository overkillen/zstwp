package com.example.piotr.zstwp;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;

import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import static android.content.Context.MODE_PRIVATE;


public class WfmStartFragment extends Fragment {


    Context thiscontext;
    View view;
    private Button loginButton;
    private EditText etDriverId;

    //private TextView textViewTest;

    private String DriverIdString;
    private boolean driverIdCorrect;


    @Override
    public View onCreateView(LayoutInflater inflater, final ViewGroup container, Bundle savedInstanceState) {


        view = inflater.inflate(R.layout.fragment_wfm_start, container, false);


        // DriverIds in database
        final int[] driver_id_tab = {2001, 2002, 2003, 2004};

        // Get last entered driverId
        SharedPreferences myPrefs = this.getActivity().getSharedPreferences("myPrefs", Context.MODE_WORLD_READABLE);
        String driverId = myPrefs.getString("MEM1", "");

        etDriverId = (EditText) view.findViewById(R.id.driverId_et);
        etDriverId.setText("" + driverId);


        loginButton = (Button) view.findViewById(R.id.loginButton);
        
        thiscontext = container.getContext();

        // Set a click listener for Fragment button
        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Create a Toast notification/message
                //Toast.makeText(thiscontext, "this is my Toast message!!! =)", Toast.LENGTH_LONG).show();


                DriverIdString = etDriverId.getText().toString();

                // Check if editText is not empty
                if (DriverIdString.matches("")) {
                    Toast.makeText(thiscontext, "Enter your ID", Toast.LENGTH_SHORT).show();
                } else {
                    // Check if driverId is correct (existing in database)
                    int driverIdInt = Integer.parseInt(DriverIdString);

                    for (int i=0; i < driver_id_tab.length; i++ ){
                        if ( driverIdInt == driver_id_tab[i]){
                            driverIdCorrect = true;
                        }
                    }

                    if(driverIdCorrect) {
                        // Save driverId in editText
                        SharedPreferences myPrefs = thiscontext.getSharedPreferences("myPrefs", Context.MODE_WORLD_READABLE);
                        SharedPreferences.Editor editor = myPrefs.edit();
                        editor.putString("MEM1", etDriverId.getText().toString());
                        editor.commit();

                        //TODO
                        //tu bedzie kod odpowiedzialny za przejscie do kolejnego fragmentu
                        Fragment fragmentNotification = new WfmNotificationFragment();
                        FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
                        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                        fragmentTransaction.replace(R.id.start, fragmentNotification);
                        fragmentTransaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
                        fragmentTransaction.addToBackStack("fragmentStart");
                        fragmentTransaction.commit();

                        ViewGroup mContainer = (ViewGroup) getActivity().findViewById(R.id.start);
                        mContainer.removeAllViews();


                        //Toast.makeText(thiscontext, "Teraz powinien otworzyc sie kolejny fragment", Toast.LENGTH_LONG).show();


                       // Toast.makeText(thiscontext, "Teraz powinien otworzyc sie kolejny fragment", Toast.LENGTH_LONG).show();


                    } else{
                        Toast.makeText(thiscontext, "Enter the correct ID", Toast.LENGTH_SHORT).show();
                    }

                }


            }
        });

        return view;
        //return inflater.inflate(R.layout.fragment_wfm_start, container, false);
    }


}
