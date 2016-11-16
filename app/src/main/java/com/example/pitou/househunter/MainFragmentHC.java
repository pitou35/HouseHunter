package com.example.pitou.househunter;

import android.app.Activity;
import android.app.Fragment;
import android.util.Log;

/**
 * Created by kaoutar on 16/11/2016.
 */

public class MainFragmentHC extends Fragment {

    private MainFragmentCallBack parent;


    @Override
    public void onAttach(Activity activity) {
        Log.w("MainFragmentHC", "onAttach called");
        super.onAttach(activity);
        // Utiliser cette méthode pour lier votre fragment avec son callback
        parent = (MainFragmentCallBack) activity;
    }

    /**
     * Un item a été sélectionné, notifier le changement
     * @param position of the item
     */
    public void onItemSelected(int position) {
        //Notifiez le parent qu'un item a été sélectionné
        parent.onItemSelected(position);
        //Faîtes d'autres traitements ici au besoin
    }
}
