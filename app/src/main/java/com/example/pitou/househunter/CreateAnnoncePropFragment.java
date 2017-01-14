package com.example.pitou.househunter;

import android.app.FragmentTransaction;
import android.location.Address;
import android.location.Geocoder;
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
import com.firebase.geofire.GeoFire;
import com.firebase.geofire.GeoLocation;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.io.IOException;
import java.math.BigInteger;
import java.security.SecureRandom;
import java.util.List;


public class CreateAnnoncePropFragment extends Fragment {

    private FirebaseDatabase db;
    private FirebaseAuth auth;
    private DatabaseReference myRef;
    private GeoFire geoFire;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view= inflater.inflate(R.layout.fragment_create_annonce_prop, container, false);

        db = FirebaseDatabase.getInstance();
        auth=FirebaseAuth.getInstance();
            //auth.getCurrentUser().getUid();
        myRef = db.getReference("Annonces");
        geoFire = new GeoFire(db.getReference("AnnoncesPos")); //On implément GeoFire qui va nous permettre de stocker les coordonnées de la nouvelle annonce


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
                                           final Annonce annonce = new Annonce(titreA, descriptionA, adresseA, telContact, prixLogement, datePublication, dateDisponibilite, auth.getCurrentUser().getUid());
                                           //Generation d'un id aléatoire (car on doit connaitre l'id aléatoire pour des traitements plus tard)
                                           final SecureRandom random = new SecureRandom();
                                           final String idAnnonce=  new BigInteger(130, random).toString(32);
                                           //On verifie si l'id existe déja
                                           //Si l'id existe déja: on arrete l'opération et on demande de la refaire
                                           myRef.child(idAnnonce).addListenerForSingleValueEvent(new ValueEventListener() {
                                               @Override
                                               public void onDataChange(DataSnapshot snapshot) {
                                                   if (snapshot.exists()) {
                                                       Toast.makeText(getContext(), "Erreur generation id automatique: recommencez svp", Toast.LENGTH_SHORT).show();
                                                   }else{
                                                       myRef.child(idAnnonce).setValue(annonce);
                                                   }
                                               }

                                               @Override
                                               public void onCancelled(DatabaseError databaseError) {

                                               }
                                           });

                                           if (TextUtils.isEmpty(titreA)) {
                                               Toast.makeText(getContext(), "Enter a title!", Toast.LENGTH_SHORT).show();
                                               return;
                                           }

                                           if (TextUtils.isEmpty(descriptionA)) {
                                               Toast.makeText(getContext(), "Enter a description!", Toast.LENGTH_SHORT).show();
                                               return;
                                           }
                                           if (TextUtils.isEmpty(adresseA)) {
                                               Toast.makeText(getContext(), "Enter an adress!", Toast.LENGTH_SHORT).show();
                                               return;
                                           }else{
                                               //Si l'adresse n'est pas vide alors on va convertir l'adresse en coordonées et le sauver sur firebase
                                               Geocoder gc = new Geocoder(v.getContext());
                                               try {
                                               //Liste d'adresse trouvé à partir du texte entré
                                               List<Address> liste = gc.getFromLocationName(adresseA, 5);

                                               // On choisit la première adresse
                                               if (liste != null && !liste.isEmpty()) {
                                                   Address add = liste.get(0);
                                                   double lat = add.getLatitude();
                                                   double lgt = add.getLongitude();
                                                   geoFire.setLocation(idAnnonce,new GeoLocation(lat,lgt)); //On récupére et enregistre ses coordonnées dans firebase
                                               }
                                               } catch (IOException e) {
                                                   e.printStackTrace();
                                               }
                                           }

                                           FragmentTransaction ft=getFragmentManager().beginTransaction();
                                           ft.replace(R.id.current_fragment, new ListeAnnonceFragment());
                                           ft.commit();
                                       }


        });


        return view;
    }

}
