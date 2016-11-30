package com.example.pitou.househunter;

import android.app.FragmentTransaction;
import android.os.Bundle;
import android.app.Fragment;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.example.pitou.househunter.model.Annonce;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;



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
            //auth.getCurrentUser().getUid();
        myRef = db.getReference("Annonces");


        final TextView titre = (TextView) view.findViewById(R.id.ETitreAnnonce);
        final TextView description = (TextView) view.findViewById(R.id.EDescriAnnonce);
        final TextView adresse = (TextView) view.findViewById(R.id.EAdressAnnonce);
        final TextView telephone = (TextView) view.findViewById(R.id.ETelContact);
        final TextView prixLog = (TextView) view.findViewById(R.id.EprixLog);
        final TextView datePub = (TextView) view.findViewById(R.id.EdatePublication);
        final TextView dateDispo = (TextView) view.findViewById(R.id.EdisponibiliteLoge);
        Button ajouter = (Button) view.findViewById(R.id.Ajouter);


        ajouter.setOnClickListener(new View.OnClickListener() {
                                       @Override
                                       public void onClick(View v) {

                                           String titreA = titre.getText().toString();
                                           String descriptionA = description.getText().toString();
                                           String adresseA = adresse.getText().toString();
                                           String telContact = telephone.getText().toString();
                                           String prixLogement = prixLog.getText().toString();
                                           String datePublication = datePub.getText().toString();
                                           String dateDisponibilite = dateDispo.getText().toString();
                                           Annonce annonce = new Annonce(titreA, descriptionA, adresseA, telContact, prixLogement, datePublication, dateDisponibilite);
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
                                           FragmentTransaction ft=getFragmentManager().beginTransaction();
                                           ft.replace(R.id.current_fragment, new ListeAnnonceFragment());
                                           ft.commit();
                                       }


        });


        return view;
    }



}
