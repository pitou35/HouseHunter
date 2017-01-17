package com.example.pitou.househunter;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import com.example.pitou.househunter.Adapters.AnnoncesAdapter;
import com.example.pitou.househunter.model.Annonce;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import static com.google.android.gms.internal.zzs.TAG;


public class DetailAnnonceFragment extends Fragment {

   //elements de l'interface
    private TextView titreAnnonce;
    private TextView descriptionAnnonce;
    private TextView adresse;
    private TextView prixLogement;
    private TextView telContact;
    private TextView disponibilite;


    //conexion bd
    private FirebaseDatabase db;
    private FirebaseAuth auth;
    private DatabaseReference myRef;
    private String idAnnonce;

    public DetailAnnonceFragment() {
        // Required empty public constructor
    }

    public String getIdAnnonce(){return idAnnonce;}
    public void setIdAnnonce(String idAnnonce){this.idAnnonce = idAnnonce;}

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        db = FirebaseDatabase.getInstance();
        auth = FirebaseAuth.getInstance();
        auth.getCurrentUser();
        myRef = db.getReference("Annonces");


        View view = inflater.inflate(R.layout.fragment_detail_annonce, container, false);

        titreAnnonce = (TextView) view.findViewById(R.id.textTitre);
        descriptionAnnonce = (TextView) view.findViewById(R.id.textDescription);
        adresse = (TextView) view.findViewById(R.id.textAdresse);
        prixLogement = (TextView) view.findViewById(R.id.textPrix);
        telContact = (TextView) view.findViewById(R.id.textContact);
        disponibilite = (TextView) view.findViewById(R.id.textDisponibilite);



        myRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                // This method is called once with the initial value and again
                // whenever data at this location is updated.
                for (DataSnapshot child : dataSnapshot.getChildren()){
                    //Log.i(TAG, "Value is: " + child.getValue(Annonce.class).getTitre());
                    Annonce value = child.getValue(Annonce.class);
                    //afficher details de l'annonce
                    if(child.getKey().equals(idAnnonce)){
                        titreAnnonce.setText(value.getTitre());
                        descriptionAnnonce.setText(value.getDescription());
                        prixLogement.setText(value.getPrixLogment());
                        telContact.setText(value.getTelContact());
                        disponibilite.setText(value.getDisponibiliteLogement());
                        adresse.setText(value.getAdresse());
                    }


                }


            }
            @Override
            public void onCancelled(DatabaseError error) {
                // Failed to read value
                Log.w(TAG, "Failed to read value.", error.toException());
            }
        });

        return view;
    }

}
