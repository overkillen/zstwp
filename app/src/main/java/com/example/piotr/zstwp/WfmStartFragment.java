package com.example.piotr.zstwp;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;

import android.view.ViewGroup;
import android.widget.Button;

import static android.content.ContentValues.TAG;

public class WfmStartFragment extends Fragment implements View.OnClickListener {

    View view;
    private Button loginButton;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        view = inflater.inflate(R.layout.fragment_wfm_start, container, false);
        loginButton = (Button) view.findViewById(R.id.loginButton);
        loginButton.setOnClickListener(this);

        //return inflater.inflate(R.layout.fragment_wfm_start, container, false);
        return view;
    }
    @Override
    public void onClick(View v) {
        Fragment fragmentNotification = new WfmNotificationFragment();
        FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.start, fragmentNotification);
        fragmentTransaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
        fragmentTransaction.addToBackStack(null);
        fragmentTransaction.commit();

        ViewGroup mContainer = (ViewGroup) getActivity().findViewById(R.id.start);
        mContainer.removeAllViews();

    }
}
