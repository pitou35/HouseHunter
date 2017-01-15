package com.example.pitou.househunter;


import android.app.FragmentTransaction;
import android.os.Bundle;
import android.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ListView;


import com.example.pitou.househunter.Adapters.AnnoncesAdapter;
import com.example.pitou.househunter.model.Annonce;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

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
    /*
    La variable qui représente l'adapter de la liste des annonces avec le modèle de l'annonce.
     */
    private AnnoncesAdapter adapter;

    //On déclare le nom du paramêtres reçu qui recevra la liste des id des annonces à afficher
    private final static String ARG_ID= "annonces";
    //On déclare un tableau qui va contenir les annonces à afficher
    private ArrayList<String> idAnn = new ArrayList<>();


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        final View view= inflater.inflate(R.layout.fragment_liste_annonce, container, false);
        /*
        Récupération des données
         */
        db = FirebaseDatabase.getInstance();
        auth=FirebaseAuth.getInstance();
        auth.getCurrentUser();
        myRef = db.getReference("Annonces");


        //Test pour voir si on reçoit bien les annonces à afficher
        if (idAnn != null && !idAnn.isEmpty()){
            for(String id: idAnn){
                System.out.println("Annonce à afficher: "+ id);
            }
        }
        ArrayList<Annonce> arrayOfAnnonces = new ArrayList<Annonce>();
        // Create the adapter to convert the array to views

        adapter = new AnnoncesAdapter(getContext(), arrayOfAnnonces);

        Button NewAnnonce = (Button) view.findViewById(R.id.BNewAnnonce);

        myRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                // This method is called once with the initial value and again
                // whenever data at this location is updated.
                String currentU =  auth.getCurrentUser().getUid();
                for (DataSnapshot child : dataSnapshot.getChildren()){
                        //Log.i(TAG, "Value is: " + child.getValue(Annonce.class).getTitre());
                    Annonce value = child.getValue(Annonce.class);
                    System.out.println("**************");
                    System.out.println(value.getIdUser());
                    System.out.println(auth.getCurrentUser().getUid());
                    System.out.println(value.getAdresse());
                    if( value.getIdUser().equals(auth.getCurrentUser().getUid())) {
                        adapter.add(value);
                    }
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
        NewAnnonce.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FragmentTransaction ft=getFragmentManager().beginTransaction();
                ft.replace(R.id.current_fragment, new CreateAnnoncePropFragment());
                ft.commit();

            }
        });

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
    public static  ListeAnnonceFragment newInstance(ArrayList<String> listeIdAnnonce) {
        ListeAnnonceFragment fragment = new  ListeAnnonceFragment(); //On crée notre fragement
        Bundle args = new Bundle(); //On creer un bundle de paramêtres
        args.putStringArrayList(ARG_ID, listeIdAnnonce); //On rajoute dans nos paramêtres
        fragment.setArguments(args); //On rattache le bundle de paramêtres aux fragement
        return fragment;
    }

}
