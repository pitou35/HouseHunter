package com.example.pitou.househunter;

import android.*;
import android.Manifest;
import android.app.FragmentTransaction;
import android.content.Context;
import android.app.Fragment;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.UiSettings;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.auth.FirebaseAuth;

import java.util.ArrayList;

/**
 * A placeholder fragment containing a simple view.
 */
public class CarteFragment extends Fragment {

    MapView mMapView;
    private GoogleMap googleMap;
    //Indique si on a fait une recherche (pour le cas ou on a fait une recherche
    private Boolean search= false;
    //Marker de l'utilisateur
    private Marker userMark;
    //Marker ds annnonces
    private ArrayList<Marker> annoncesMarkers;

    private FirebaseAuth auth;


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
    }*/

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        final View rootView = inflater.inflate(R.layout.fragment_map, container, false);
        auth=FirebaseAuth.getInstance();
        /**On verifie si les permissions sont valides**/
        if (ActivityCompat.checkSelfPermission(this.getActivity(), android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(this.getActivity(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this.getActivity(), new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION},
                    MainActivity.ACCESS_FINE_LOCATION);
            ActivityCompat.requestPermissions(this.getActivity(), new String[]{Manifest.permission.ACCESS_COARSE_LOCATION},
                    MainActivity.ACCESS_FINE_LOCATION);
            this.getActivity().recreate();
        } else {
            /**On fait la localisation**/
            // Acquire a reference to the system Location Manager
            LocationManager locationManager = (LocationManager) getActivity().getSystemService(Context.LOCATION_SERVICE);

            // Define a listener that responds to location updates
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 5000, 150, new LocationListener() {
                public void onLocationChanged(Location location) {
                    // Called when a new location is found by the network location provider.
                    LatLng userPos = new LatLng(location.getLongitude(),location.getLatitude());
                    if(googleMap!= null && !search) {
                        //On supprime l'ancien marker si il existe
                        if(userMark!= null){
                            userMark.remove();
                        }
                        userMark = googleMap.addMarker(new MarkerOptions().position(userPos).title("Vous êtes ici"));
                        // For zooming automatically to the location of the marker
                        CameraPosition cameraPosition = new CameraPosition.Builder().target(userPos).zoom(8).build();
                        googleMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
                    }
                }

                public void onStatusChanged(String provider, int status, Bundle extras) {
                }

                public void onProviderEnabled(String provider) {
                    System.out.println("INFO:ACTIVATE");
                }

                public void onProviderDisabled(String provider) {
                }
            });

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
                    //On rajoute le zoom
                    googleMap.getUiSettings().setZoomControlsEnabled(true);
                }
            });

            Button button = (Button) rootView.findViewById(R.id.buttonConnection);
            button.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    //mListener.onClickBtnConnection();
                    if (auth!=null){
                        FragmentTransaction ft=getFragmentManager().beginTransaction();
                        ft.replace(R.id.current_fragment, new ProfilFragment());
                        ft.commit();
                    }else{
                        FragmentTransaction ft=getFragmentManager().beginTransaction();
                        ft.replace(R.id.current_fragment, new ConnectionFragment());
                        ft.commit();
                    }

                }
            });
        }
        return rootView;
    }

    /**Permet de rattacher les listeners utilisé par le fragment et implémenté par l'activité**/
    /*@Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentMapInteractionListener) {
            mListener = (OnFragmentMapInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }*/

    /*@Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }*/

    /**
     * Interface qui liste les traitements appellées par les listeners qui seront à implémenter dans l'activité
     */
    /*public interface OnFragmentMapInteractionListener {
        void onClickBtnConnection(); //l'activité devra implémenter cette méthode avec:
    }*/
}
