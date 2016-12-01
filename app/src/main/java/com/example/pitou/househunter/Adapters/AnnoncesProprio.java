package com.example.pitou.househunter.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.example.pitou.househunter.R;
import com.example.pitou.househunter.model.Annonce;

import java.util.ArrayList;

/**
 * Created by kaoutar on 01/12/2016.
 */

public class AnnoncesProprio extends ArrayAdapter<Annonce> {

    public AnnoncesProprio(Context context, ArrayList<Annonce> annonces) {
        super(context, 0, annonces);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        // Get the data item for this position
        Annonce annonce = getItem(position);
        // Check if an existing view is being reused, otherwise inflate the view
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.annonce, parent, false);
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


        // Return the completed view to render on screen
        return convertView;
    }
}
