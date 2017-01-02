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

import com.firebase.geofire.GeoFire;
import com.firebase.geofire.GeoLocation;
import com.firebase.geofire.GeoQuery;
import com.firebase.geofire.GeoQueryEventListener;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.UiSettings;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * A placeholder fragment containing a simple view.
 */
public class CarteFragment extends Fragment {

    /**
     * Comment afficher les annonces
     * 1)Création d'annonce: quand on fournit une adresse => on reprends L'objet Adresse et on retrouve les coordonnées qu'on sauvegarde
     * dans Firebase (avec geofire)
     * 2)Ici on utilise geofire qui va autour du user marker récuperer les annonces dans un rayon de 10 km
     * 3)Pour chaque annonce récuperer on conserve son id et on affiche un marker avec les infos
     * 4)Quand on clique sur ce marker on appelle le fragement pour ouvrir le détail
     */
    /**
     * DONE: ajout marker (carte) quand geoquerie repére un element dans le rayon (mdf sur firebase)
     * DONE: suppr marker (carte) quand geoquerie repére un element qui disparait dans le rayon (mdf sur firebase)
     * DONE: mdf marker (carte) quand geoquerie repére un element qui bouge dans le rayon (mdf sur firebase)
     * TODO: barre pour regler le rayon
     * TODO: liens entre les coordonnées avec les vrais id et afficher de vrai info
     * TODO: completer formulaire ajout/mdf pour à partir de l'adresse determiner puis stocker les coordonnées avec geoqueries
     * TODO: mdf listener marker pour changer de fragement quand on clique sur un marker
     */
    MapView mMapView;
    private GoogleMap googleMap;
    //Indique si on a fait une recherche (pour le cas ou on a fait une recherche
    private Boolean search = false;
    //Marker de l'utilisateur
    private Marker userMark;
    //Marker ds annnonces
    private HashMap<String,Marker> annoncesMarkers;
    //Path des coordonnées des annonces
    private String geoPath = "AnnoncesPos/";
    GeoFire geoFire;
    //Niveau zoom carte
    private static int ZOOM_MAP=8;


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
                        CameraPosition cameraPosition = new CameraPosition.Builder().target(userPos).zoom(ZOOM_MAP).build();
                        googleMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
                        launchQuerie(location.getLatitude(), location.getLongitude());
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

            /**On genere et gere la carte google**/
            //On récupére la map view
            mMapView = (MapView) rootView.findViewById(R.id.mapView);
            mMapView.onCreate(savedInstanceState);
            //On initialise la hashmpa des markers d'annonces
            annoncesMarkers = new HashMap<String,Marker>();
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

            /**Gestion de geofire: pour afficher les annonces autours de notre positiion**/
            DatabaseReference ref = FirebaseDatabase.getInstance().getReference(geoPath);
            geoFire = new GeoFire(ref);

            /*TEMPO: données de test**/
            geoFire.setLocation("id1", new GeoLocation(48.083328, -1.68333));
            geoFire.setLocation("id2", new GeoLocation(48.083028, -1.67333));
            geoFire.setLocation("id3", new GeoLocation(48.082728, -1.66333));
            geoFire.setLocation("id4", new GeoLocation(48.084428, -1.68333));


            /**Listeners**/
            /*Listener bouton connection*/
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

    /**Méthode pour activer un geoquerie: c'est a dire utiliser geoFire pour reperer autour du markeruser les annonces**/
    private void launchQuerie(double lat,double longit) {
    GeoQuery geoQuery = geoFire.queryAtLocation(new GeoLocation(lat, longit), 10.0);
    geoQuery.addGeoQueryEventListener(new GeoQueryEventListener() {
          //Cas element entrée dans le radius (ou trouvé)
          @Override
          public void onKeyEntered(String key, GeoLocation location) {
              //Si jamais il reste une trace pour cette annonce on la supprime
              if (annoncesMarkers.get(key) != null) {
                  annoncesMarkers.get(key).remove(); //on supprime le marker
                  annoncesMarkers.remove(key);
              }
              //Maj carte (ajout marqueur)
              LatLng loc = new LatLng(location.latitude, location.longitude);
              annoncesMarkers.put(key, googleMap.addMarker(new MarkerOptions()
                      .position(loc)
                      .title(key)
                      .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_ORANGE))
                      //.icon(BitmapDescriptorFactory.fromResource(R.drawable.taxi))
                      .snippet(key)
              )); //Ajout du nouveau marker
          }
          //Cas element sort
          @Override
          public void onKeyExited(String key) {
              //Recuperer dans la liste des markers, le marker qui a la clé et le supprimer
              if (annoncesMarkers.get(key)!=null) {
                  annoncesMarkers.get(key).remove();
                  annoncesMarkers.remove(key);
              }
          }

          //Cas element bouge
          @Override
          public void onKeyMoved(String key, GeoLocation location) {
              //Si jamais il reste une trace pour cette annonce on la supprime
              if (annoncesMarkers.get(key) != null) {
                  annoncesMarkers.get(key).remove(); //on supprime le marker
                  annoncesMarkers.remove(key);
              }
              //Maj carte (ajout marqueur)
              LatLng loc = new LatLng(location.latitude, location.longitude);
              annoncesMarkers.put(key, googleMap.addMarker(new MarkerOptions()
                      .position(loc)
                      .title(key)
                      .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_ORANGE))
                      //.icon(BitmapDescriptorFactory.fromResource(R.drawable.taxi))
                      .snippet(key)
              )); //Ajout du nouveau marker
          }

          @Override
          public void onGeoQueryReady() {
              System.out.println("INFO: REQUETE PRETE");
          }

          @Override
          public void onGeoQueryError(DatabaseError error) {
          }
      });
    }
    //Méthode pour positionner le marker d'adresse à l'adresse entrée par le client
    private void gotoLocation (double lat, double lgt, String add1, String add2){
        LatLng ll = new LatLng(lat, lgt);
        CameraUpdate update = CameraUpdateFactory.newLatLngZoom(ll, ZOOM_MAP);
        if (userMark != null){
            userMark.remove();
        }
        userMark =googleMap.addMarker(new MarkerOptions()
                .position(ll)
                .title("Lieu")
                .snippet(add2 + " " + add1));
        search = true; //On indique qu'on a fait une recherche donc on cherche plus la position de l'utilisateur
        googleMap.moveCamera(update);
        launchQuerie(lat,lgt);
    }
}
