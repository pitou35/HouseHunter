package com.example.pitou.househunter;

import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

/**
 * Created by pitou on 18/11/2016.
 */

public class ResetPasswordFragment extends Fragment {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup parent, Bundle savedInstanceState) {

        View view= inflater.inflate(R.layout.fragment_create_acc, parent, false);
        return view;
    }
}
