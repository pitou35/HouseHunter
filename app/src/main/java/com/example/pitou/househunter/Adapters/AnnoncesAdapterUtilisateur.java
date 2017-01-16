package com.example.pitou.househunter.Adapters;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.TextView;

import com.example.pitou.househunter.ListeAnnonceFragmentUtilisateur;
import com.example.pitou.househunter.R;
import com.example.pitou.househunter.model.Annonce;

import java.util.ArrayList;

import static com.google.android.gms.internal.zzs.TAG;

/**
 * Created by kaoutar on 21/11/2016.
 */

public class AnnoncesAdapterUtilisateur extends ArrayAdapter<Annonce> {

    private ListeAnnonceFragmentUtilisateur fragmentListeAnnonces;
    public AnnoncesAdapterUtilisateur(Context context, ArrayList<Annonce> annonces, ListeAnnonceFragmentUtilisateur fragment) {
        super(context, 0, annonces);
        fragmentListeAnnonces = fragment;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        // Get the data item for this position
        Annonce annonce = getItem(position);
        // Check if an existing view is being reused, otherwise inflate the view
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.annonceutilisateur, parent, false);
        }
        // Lookup view for data population

        TextView TitreAnnonce = (TextView) convertView.findViewById(R.id.TitreAnnonce);
        TextView DescriptionAnnonce = (TextView) convertView.findViewById(R.id.DescrAnnonce);
        TextView AdresseAnnonce = (TextView) convertView.findViewById(R.id.Adresse);
        TextView telContact = (TextView) convertView.findViewById(R.id.Telephone);
        TextView prixLogment = (TextView) convertView.findViewById(R.id.PrixLogement);
        TextView datePublication = (TextView) convertView.findViewById(R.id.datePub);
        TextView Dispnibilite = (TextView) convertView.findViewById(R.id.disponibilite);
        TitreAnnonce.setText(annonce.getTitre());
        DescriptionAnnonce.setText(annonce.getDescription());
        AdresseAnnonce.setText(annonce.getAdresse());
        telContact.setText(annonce.getTelContact());
        prixLogment.setText(annonce.getPrixLogment());
        datePublication.setText(annonce.getDatePublication());
        Dispnibilite.setText(annonce.getDisponibiliteLogement());


        /**
         * necessaire pour afficher les details et supprimer l'annonce
         */
        final TextView idAnnonce = (TextView) convertView.findViewById(R.id.idAnnonce);

        Button details = (Button) convertView.findViewById(R.id.buttonDetails);
        idAnnonce.setText(annonce.getIdAnnonce());

        /**
         * Bouton pour afficher les details de l'annonce en question
         */
        details.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.i(TAG, "id annonce to details"+idAnnonce.getText());
                fragmentListeAnnonces.callDetailAnnonceFragment(idAnnonce.getText().toString());

            }
        });

        // Return the completed view to render on screen
        return convertView;
    }
}
