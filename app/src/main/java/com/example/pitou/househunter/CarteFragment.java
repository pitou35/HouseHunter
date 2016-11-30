package com.example.pitou.househunter;

import android.*;
import android.Manifest;
import android.app.FragmentTransaction;
import android.content.Context;
import android.app.Fragment;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
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
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdate;
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

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * A placeholder fragment containing a simple view.
 */
public class CarteFragment extends Fragment {

    MapView mMapView;
    private GoogleMap googleMap;
    //Indique si on a fait une recherche (pour le cas ou on a fait une recherche
    private Boolean search = false;
    //Marker de l'utilisateur
    private Marker userMark;
    //Marker ds annnonces
    private ArrayList<Marker> annoncesMarkers;

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

        /**On verifie si les permissions sont valides**/
        if (ActivityCompat.checkSelfPermission(this.getActivity(), android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(this.getActivity(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this.getActivity(), new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION},
                    MainActivity.ACCESS_FINE_LOCATION);
            ActivityCompat.requestPermissions(this.getActivity(), new String[]{Manifest.permission.ACCESS_COARSE_LOCATION},
                    MainActivity.ACCESS_FINE_LOCATION);
            restartActivity();
        } else {
            /**On fait la localisation**/
            // Acquire a reference to the system Location Manager
            final LocationManager locationManager = (LocationManager) getActivity().getSystemService(Context.LOCATION_SERVICE);

            // Define a listener that responds to location updates
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 5000, 0, new LocationListener() {
                public void onLocationChanged(Location location) {
                    // Called when a new location is found by the network location provider.
                    LatLng userPos = new LatLng(location.getLatitude(), location.getLongitude());
                    if (googleMap != null && !search) {
                        //On supprime l'ancien marker si il existe
                        if (userMark != null) {
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
                    search = false; //on indique qu'on ne se place plus en fonction d'une recherche mais de sa position
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

            /*Listener bouton connection*/
            Button button = (Button) rootView.findViewById(R.id.buttonConnection);
            button.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    //mListener.onClickBtnConnection();
                    FragmentTransaction ft=getFragmentManager().beginTransaction();
                    ft.replace(R.id.current_fragment, new ConnectionFragment());
                    ft.commit();
                }
            });

            /*Listener bouton réactiver loc*/
            Button buttonLoc = (Button) rootView.findViewById(R.id.buttonLoc);
            buttonLoc.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    search = false; //on indique qu'on ne se place plus en fonction d'une recherche mais de sa position
                }
            });

            /*Listener pour le bouton de recherche*/
            Button buttonSearch = (Button)  rootView.findViewById(R.id.buttonRecherche);
            buttonSearch.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    // Perform action on click
                    EditText editText = (EditText) rootView.findViewById(R.id.editTextRecherche);
                    String message = editText.getText().toString();
                    if (message.isEmpty()) {
                        Toast.makeText(v.getContext(), "Entrez une adresse !", Toast.LENGTH_LONG).show();
                    } else {
                        Geocoder gc = new Geocoder(v.getContext());
                        try {
                            //Liste d'adresse trouvé à partir du texte entré
                            List<Address> liste = gc.getFromLocationName(message, 5);

                            // On choisit la première adresse
                            if (liste != null && !liste.isEmpty()) {
                                Address add = liste.get(0);
                                String locality = add.getAddressLine(2); // 2 = Pays
                                String ville = add.getAddressLine(1); // 1 = Code postale + Ville
                                String adr = add.getAddressLine(0); // 0 = Adresse
                                double lat = add.getLatitude();
                                double lgt = add.getLongitude();
                                gotoLocation(lat, lgt, ville, adr);
                            } else {
                                Toast.makeText(v.getContext(), "Adresse inconnue !", Toast.LENGTH_LONG).show();
                            }
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
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

    /**Méthode pour redémarrer l'activité**/
    private void restartActivity() {
        Intent intent = getActivity().getIntent();
        getActivity().finish();
        startActivity(intent);
    }

    //Méthode pour positionner le marker d'adresse à l'adresse entrée par le client
    private void gotoLocation (double lat, double lgt, String add1, String add2){
        LatLng ll = new LatLng(lat, lgt);
        CameraUpdate update = CameraUpdateFactory.newLatLngZoom(ll, 12);
        if (userMark != null){
            userMark.remove();
        }
        userMark =googleMap.addMarker(new MarkerOptions()
                .position(ll)
                .title("Lieu")
                .snippet(add2 + " " + add1));
        search = true; //On indique qu'on a fait une recherche donc on cherche plus la position de l'utilisateur
        googleMap.moveCamera(update);
    }
}
