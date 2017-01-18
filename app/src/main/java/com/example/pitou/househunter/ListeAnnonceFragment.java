package com.example.pitou.househunter;


import android.app.AlertDialog;
import android.app.FragmentTransaction;
import android.content.DialogInterface;
import android.os.Bundle;
import android.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;


import com.example.pitou.househunter.Adapters.AnnoncesAdapter;
import com.example.pitou.househunter.model.Annonce;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

import static com.google.android.gms.internal.zzs.TAG;


public class ListeAnnonceFragment extends Fragment {

    /**NOTE IMPORTANTE: J'ai fait passer les ids des annonces à lister en paramêtre de ce fragement:
     * les ids des annonces à afficher sont stocké dans idAnn **/
    /**NOTE 2: IL SEMBLE QUE LE BOUTON AJOUTER (PRESENT ICI) DOIT ETRE dans ListeAnnoncePropFragement
     * (ici c'est la liste d'affichage pas la liste pour les propriétaires)*/
    /*
    les variables qui représentes les tables de la base de données
     */
    private FirebaseDatabase db;
    private FirebaseAuth auth;
    private DatabaseReference myRef;
    private DatabaseReference myRefPos;
    private ListView listAnnonces;
    /*
    La variable qui représente l'adapter de la liste des annonces avec le modèle de l'annonce.
     */
    private AnnoncesAdapter adapter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view= inflater.inflate(R.layout.fragment_liste_annonce, container, false);
        listAnnonces=(ListView) view.findViewById(R.id.ListeAnnonces);
        List<Annonce> Annonces=new ArrayList<>();
        adapter=new AnnoncesAdapter(getContext(), Annonces, ListeAnnonceFragment.this);
        listAnnonces.setAdapter(adapter);
        /*
        Récupération des données
         */


        //ArrayList<Annonce> arrayOfAnnonces = new ArrayList<Annonce>();
        // Create the adapter to convert the array to views

        //adapter = new AnnoncesAdapter(getContext(), arrayOfAnnonces, ListeAnnonceFragment.this);

        Button NewAnnonce = (Button) view.findViewById(R.id.BNewAnnonce);


       /* myRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                // This method is called once with the initial value and again
                // whenever data at this location is updated.
                String currentU =  auth.getCurrentUser().getUid();
                for (DataSnapshot child : dataSnapshot.getChildren()){
                        //Log.i(TAG, "Value is: " + child.getValue(Annonce.class).getTitre());
                    Annonce value = child.getValue(Annonce.class);
                    if( value.getIdUser().equals(auth.getCurrentUser().getUid())) {
                        adapter.add(value);
                    }
                    value.setIdAnnonce(child.getKey());
                    //adapter.add(value);
                    ListView laliste = (ListView) view.findViewById(R.id.ListeAnnonces);
                    laliste.setAdapter(adapter);
                }
            }
            @Override
            public void onCancelled(DatabaseError error) {
                // Failed to read value
                Log.w(TAG, "Failed to read value.", error.toException());
            }
        });
        */


        NewAnnonce.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                /*FragmentTransaction ft=getFragmentManager().beginTransaction();
                ft.replace(R.id.current_fragment, new CreateAnnoncePropFragment());
                ft.commit();*/
                ((MainActivity)getActivity()).showFragment(new CreateAnnoncePropFragment());
            }
        });
        return view;

    }

    public void onViewCreated(View view, Bundle savedInstanceState){
        super.onViewCreated(view, savedInstanceState);
        db = FirebaseDatabase.getInstance();
        auth=FirebaseAuth.getInstance();
        auth.getCurrentUser();
        myRef = db.getReference("Annonces");
        myRefPos = db.getReference("AnnoncesPos");
        myRef.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                String currentU =  auth.getCurrentUser().getUid();
                Annonce value = dataSnapshot.getValue(Annonce.class);
                if( value.getIdUser().equals(auth.getCurrentUser().getUid())) {

                    adapter.add(value);
                }
                value.setIdAnnonce(dataSnapshot.getKey());
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {
                Annonce value = dataSnapshot.getValue(Annonce.class);
                adapter.remove(value);
            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

    }

    /**
     * appel le fragment qui affiche les details d'un annonce,
     * @param idAnnonce : cet paramettre sert pour afficher les informations de l'annonce demandé
     */
    public void callDetailAnnonceFragment(String idAnnonce){
        System.out.println(idAnnonce);
        DetailAnnonceFragment details = new DetailAnnonceFragment();
        //FragmentTransaction ft= getFragmentManager().beginTransaction();
        details.setIdAnnonce(idAnnonce);
        /*ft.replace(R.id.current_fragment, details);
        ft.commit();*/
        ((MainActivity)getActivity()).showFragment(details);
    }

    /**
     * montre message de confirmation si l'utilisateur clique sur
     *oui: l'application efface de la base de donnés l'annonce selectionné, on met a jour le fragment qui contient
     * la liste d'annonces apres l'effacement d'un annonce
     * sinon le message de confirmation est fermé
     * @param idAnnonce: permet de savoir quel annonce il faut effacer
     */
    public void createAndShowAlertDialog(String idAnnonce) {
        final String idAnnonceEffacer = idAnnonce;
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle("Voulez vous effacer l'annonce?");
        builder.setPositiveButton("Oui", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                //supprimer l'annonce de la BD
                myRef.child(idAnnonceEffacer).removeValue();
                myRefPos.child(idAnnonceEffacer).removeValue();
                //mettre a jour le listview des annonces (mettre a jour le fragment)
                /*FragmentTransaction ft= getFragmentManager().beginTransaction();
                ft.replace(R.id.current_fragment, new ListeAnnonceFragment());
                ft.commit();*/
                ((MainActivity)getActivity()).showFragment(new ListeAnnonceFragment());
            }
        });
        builder.setNegativeButton("Non", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                dialog.dismiss();
            }
        });
        AlertDialog dialog = builder.create();
        dialog.show();
    }

}
