package com.example.pitou.househunter;

import android.app.FragmentTransaction;
import android.content.Context;
import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
/**
 * A placeholder fragment containing a simple view.
 */
public class CarteFragment extends Fragment implements OnMapReadyCallback{

    MapView mMapView;
    private GoogleMap googleMap;

    //On définit le listener de la région
    //private OnFragmentMapInteractionListener mListener;

    /*public CarteFragment(){

    }*/

    /*
     * Méthode pour créer ce fragment (pour rajouter des paramêtres si besoin)
     */
    /*public static  CarteFragment newInstance() {
        CarteFragment fragment = new  CarteFragment();
        Bundle args = new Bundle(); //on récupére les arguments (ici on n'en a pas)
        fragment.setArguments(args); //on valide la maj
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
        }
    }**/

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_map, container, false);

        //On récupére la map view
        mMapView = (MapView) rootView.findViewById(R.id.mapView);
        mMapView.onCreate(savedInstanceState);

        mMapView.onResume(); //On l'affiche
        try {
            MapsInitializer.initialize(getActivity().getApplicationContext());
        } catch (Exception e) {
            e.printStackTrace();
        }

        //Méthode pour récuperer la carte de la view
        mMapView.getMapAsync(new OnMapReadyCallback() {
            @Override
            //Lorsqu'on récupére la carte on execute cette méthode
            public void onMapReady(GoogleMap mMap) {
                googleMap = mMap;
                //TODO: c'est ici qu'on va gerer maqueur,barre de recherche et tout le reste
                // For dropping a marker at a point on the Map
                LatLng sydney = new LatLng(-34, 151);
                googleMap.addMarker(new MarkerOptions().position(sydney).title("Localisation"));

                // For zooming automatically to the location of the marker
                CameraPosition cameraPosition = new CameraPosition.Builder().target(sydney).zoom(8).build();
                googleMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
            }
        });

        Button button= (Button) rootView.findViewById(R.id.buttonConnection);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FragmentTransaction ft=getFragmentManager().beginTransaction();
                ft.replace(R.id.current_fragment, new ProfilFragment());
                ft.commit();
            }
        });
        return rootView;
    }

    /**Permet de rattacher les listeners utilisé par le fragment et implémenté par l'activité**/
    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        /*if (context instanceof OnFragmentMapInteractionListener) {
            mListener = (OnFragmentMapInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }*/
    }

    @Override
    public void onDetach() {
        super.onDetach();
       // mListener = null;
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {

    }


    /**
     * Interface qui liste les traitements appellées par les listeners qui seront à implémenter dans l'activité
     */
    /*public interface OnFragmentMapInteractionListener {
        void onClickBtnConnection(); //l'activité devra implémenter cette méthode avec:
    }*/
}
