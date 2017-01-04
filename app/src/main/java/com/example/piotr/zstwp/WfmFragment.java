package com.example.piotr.zstwp;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;

import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

public class WfmFragment extends Fragment implements View.OnClickListener {

    View view;
    private Button loginButton;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        view = inflater.inflate(R.layout.fragment_wfm, container, false);
        loginButton = (Button) view.findViewById(R.id.loginButton);

        return inflater.inflate(R.layout.fragment_wfm, container, false);
    }

    @Override
    public void onClick(View v) {
        //TODO
        //Trzeba zrobić przejście do innego fragmentu w ramach zakładki

    }
}
