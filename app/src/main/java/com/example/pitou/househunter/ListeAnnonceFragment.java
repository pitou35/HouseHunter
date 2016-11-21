package com.example.pitou.househunter;


import android.os.Bundle;
import android.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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


    private FirebaseDatabase db;
    private FirebaseAuth auth;
    private DatabaseReference myRef;
    private AnnoncesAdapter adapter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        db = FirebaseDatabase.getInstance();
        auth=FirebaseAuth.getInstance();
        auth.getCurrentUser();
        myRef = db.getReference("Annonces");
        ArrayList<Annonce> arrayOfAnnonces = new ArrayList<Annonce>();
        // Create the adapter to convert the array to views
        adapter = new AnnoncesAdapter(getContext(), arrayOfAnnonces);
        final View view= inflater.inflate(R.layout.fragment_liste_annonce, container, false);
        myRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                // This method is called once with the initial value and again
                // whenever data at this location is updated.
                for (DataSnapshot child : dataSnapshot.getChildren()){
                        Log.i(TAG, "Value is: " + child.getValue(Annonce.class).getTitre());
                    Annonce value = child.getValue(Annonce.class);
                    adapter.add(value);
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


        return view;

    }



}
