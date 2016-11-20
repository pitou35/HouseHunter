package com.example.pitou.househunter;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.app.Fragment;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.pitou.househunter.model.Annonce;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.iid.FirebaseInstanceId;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;


public class CreateAnnoncePropFragment extends Fragment {

    private FirebaseDatabase db;
    private FirebaseAuth auth;
    private DatabaseReference myRef;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view= inflater.inflate(R.layout.fragment_create_annonce_prop, container, false);

        db = FirebaseDatabase.getInstance();
        auth=FirebaseAuth.getInstance();
            auth.getCurrentUser();
        myRef = db.getReference("Annonces");


        final EditText titre = (EditText) view.findViewById(R.id.TitreAnnonce);
        final EditText description = (EditText) view.findViewById(R.id.DescriAnnonce);
        final EditText adresse = (EditText) view.findViewById(R.id.AdressAnnonce);
        Button ajouter = (Button) view.findViewById(R.id.Ajouter);

        ajouter.setOnClickListener(new View.OnClickListener() {
                                       @Override
                                       public void onClick(View v) {

                                           String titreA = titre.getText().toString();
                                           String descriptionA = description.getText().toString();
                                           String adresseA = adresse.getText().toString();
                                           Annonce annonce = new Annonce(titreA, descriptionA, adresseA);
                                           myRef.push().setValue(annonce);

                                           if (TextUtils.isEmpty(titreA)) {
                                               Toast.makeText(getContext(), "Enter a titlle!", Toast.LENGTH_SHORT).show();
                                               return;
                                           }

                                           if (TextUtils.isEmpty(descriptionA)) {
                                               Toast.makeText(getContext(), "Enter a description!", Toast.LENGTH_SHORT).show();
                                               return;
                                           }
                                           if (TextUtils.isEmpty(adresseA)) {
                                               Toast.makeText(getContext(), "Enter an adress!", Toast.LENGTH_SHORT).show();
                                               return;
                                           }
                                       }


        });


        return view;
    }



}
