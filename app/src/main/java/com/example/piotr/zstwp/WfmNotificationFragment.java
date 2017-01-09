package com.example.piotr.zstwp;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

/**
 * Created by Jakub on 2017-01-05.
 */

public class WfmNotificationFragment extends Fragment {
    View view;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        //view = inflater.inflate(R.layout.fragment_wfm_main, container, false);

        return inflater.inflate(R.layout.fragment_wfm_notification, container, false);

    }




}
