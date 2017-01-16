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
import android.widget.ListView;


import com.example.pitou.househunter.Adapters.AnnoncesAdapterUtilisateur;
import com.example.pitou.househunter.model.Annonce;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

import static com.google.android.gms.internal.zzs.TAG;


public class ListeAnnonceFragmentUtilisateur extends Fragment {

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
    /*
    La variable qui représente l'adapter de la liste des annonces avec le modèle de l'annonce.
     */
    private AnnoncesAdapterUtilisateur adapter;

    //On déclare le nom du paramêtres reçu qui recevra la liste des id des annonces à afficher
    private final static String ARG_ID= "annonces";
    //On déclare un tableau qui va contenir les annonces à afficher
    private ArrayList<String> idAnn = new ArrayList<>();


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        final View view= inflater.inflate(R.layout.fragment_liste_annonce_utilisateur, container, false);
        /*
        Récupération des données
         */
        db = FirebaseDatabase.getInstance();
        myRef = db.getReference("Annonces");


        //Si on a bien reçu les id des annonces à afficher: on va les chercher et completer l'adapteur
        if (idAnn != null && !idAnn.isEmpty()){
            ArrayList<Annonce> arrayOfAnnonces = new ArrayList<Annonce>();
            for(final String id: idAnn){
                System.out.println("Annonce à afficher: "+ id);
                myRef.child(id).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        //Log.i(TAG, "Value is: " + child.getValue(Annonce.class).getTitre());
                        Annonce value = dataSnapshot.getValue(Annonce.class);
                        adapter.add(value);
                        value.setIdAnnonce(myRef.child(id).getKey());
                        ListView laliste = (ListView) view.findViewById(R.id.ListeAnnonces);
                        laliste.setAdapter(adapter);
                    }

                    @Override
                    public void onCancelled(DatabaseError error) {
                        // Failed to read value
                        Log.w(TAG, "Failed to read value.", error.toException());
                    }
                });
                // Create the adapter to convert the array to views
                adapter = new AnnoncesAdapterUtilisateur(getContext(), arrayOfAnnonces, ListeAnnonceFragmentUtilisateur.this);
            }
        }



        /*myRef.addValueEventListener(new ValueEventListener() {
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
        });*/

        return view;

    }

    /**Méthode appelle lors de la créatino du fragement**/
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            idAnn = getArguments().getStringArrayList(ARG_ID);//On va stocker le paramêtre reçu dans une variable
        }
    }

    /**Méthode pour appeller ce fragement en lui fournissant une liste d'annonce (de paramêtre)**/
    public static ListeAnnonceFragmentUtilisateur newInstance(ArrayList<String> listeIdAnnonce) {
        ListeAnnonceFragmentUtilisateur fragment = new ListeAnnonceFragmentUtilisateur(); //On crée notre fragement
        Bundle args = new Bundle(); //On creer un bundle de paramêtres
        args.putStringArrayList(ARG_ID, listeIdAnnonce); //On rajoute dans nos paramêtres
        fragment.setArguments(args); //On rattache le bundle de paramêtres aux fragement
        return fragment;
    }

    /**
     * appel le fragment qui affiche les details d'un annonce,
     * @param idAnnonce : cet paramettre sert pour afficher les informations de l'annonce demandé
     */
    public void callDetailAnnonceFragment(String idAnnonce){
        DetailAnnonceFragment details = new DetailAnnonceFragment();
        FragmentTransaction ft= getFragmentManager().beginTransaction();
        details.setIdAnnonce(idAnnonce);
        ft.replace(R.id.current_fragment, details);
        ft.commit();
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
                //mettre a jour le listview des annonces (mettre a jour le fragment)
                FragmentTransaction ft= getFragmentManager().beginTransaction();
                ft.replace(R.id.current_fragment, new ListeAnnonceFragmentUtilisateur());
                ft.commit();
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
