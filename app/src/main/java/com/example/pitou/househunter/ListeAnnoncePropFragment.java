package com.example.pitou.househunter;

import android.os.Bundle;
import android.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.pitou.househunter.Adapters.AnnoncesAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;


public class ListeAnnoncePropFragment extends Fragment {

    private FirebaseDatabase db;
    private FirebaseAuth auth;
    private DatabaseReference myRef;
    private AnnoncesAdapter adapter;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_liste_annonce_prop, container, false);
    }



}
