package com.example.pitou.househunter;

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
import java.util.Map;

/**
 * A placeholder fragment containing a simple view.
 */
public class CarteFragment extends Fragment {

    MapView mMapView;
    private GoogleMap googleMap;
    //Indique si on a fait une recherche d'adresse (pour le cas ou on a fait une recherche)
    private Boolean search = false;
    //Marker de l'utilisateur
    private Marker userMark;
    //Marker des annnonces
    private HashMap<String,Marker> annoncesMarkers;
    //Path des coordonnées des annonces
    private String geoPath = "AnnoncesPos/";
    //GeoFire qu'on va utiliser pour les Annonces
    private GeoFire geoFire;
    //Niveau zoom carte par défault
    private static int ZOOM_MAP=12;
    //
    private FirebaseAuth auth;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        final View rootView = inflater.inflate(R.layout.fragment_map, container, false);
        auth=FirebaseAuth.getInstance();
        /**On verifie si les permissions sont valides**/
        //Si c'est pas le cas on demande les permissions
        if (ActivityCompat.checkSelfPermission(this.getActivity(), android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(this.getActivity(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this.getActivity(), new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION},
                    MainActivity.ACCESS_FINE_LOCATION);
            ActivityCompat.requestPermissions(this.getActivity(), new String[]{Manifest.permission.ACCESS_COARSE_LOCATION},
                    MainActivity.ACCESS_FINE_LOCATION);
            restartActivity();
        //Sinon on passe à la suite
        } else {
            /**On fait la localisation**/
            // On récupére une réference du location Manager
            final LocationManager locationManager = (LocationManager) getActivity().getSystemService(Context.LOCATION_SERVICE);

            // On définit un listener pour gere les changements de coordonnées
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 5000, 0, new LocationListener() {
                public void onLocationChanged(Location location) {
                    // Called when a new location is found by the network location provider.
                    LatLng userPos = new LatLng(location.getLatitude(), location.getLongitude());
                    //Si on n'a pas fait une recherche d'adresse pour placer le marker (!search)
                    if (googleMap != null && !search) {
                        //On supprime l'ancien marker utilisateur si il existe
                        if (userMark != null) {
                            userMark.remove();
                        }
                        //On place un nouveau marker et on zoom dessus
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
                    search = false; //On indique qu'on ne se place plus en fonction d'une recherche mais avec la position actuelle de l'utilisateur
                }

                public void onProviderDisabled(String provider) {
                }
            });

            /**On genere et gére la carte google**/
            //On récupére la map view
            mMapView = (MapView) rootView.findViewById(R.id.mapView);
            mMapView.onCreate(savedInstanceState);
            //On initialise la hashmap des markers d'annonces
            annoncesMarkers = new HashMap<>();
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

                    //On définit un listener pour réagir au click sur un marker
                    googleMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener(){
                        /**Méthode pour réagir au interaction sur les markers**/
                        @Override
                        public boolean onMarkerClick(final Marker marker) {
                        //Si c'est le marker de l'utilisateur: on fait rien de particulier
                        if(marker.equals(userMark)) {
                            return false; //false = on conserve l'interaction de base
                            // Sinon c'est une annonce:
                           /*Comme plusieurs annonces peuvent avoir les mêmes adresses/coordonnees alors on va appeller un
                           fragment pour afficher une liste de ces annonces: on va donc récuperer tout les markers
                           qui ont les mêmes coordonnées que celui qu'on vient de cliquer et on va récuperer leurs titres(=id des annonces)
                           qu'on envoie à cette liste.
                            */
                        }else{
                            ArrayList<String> idAnn = new ArrayList<>();
                            for (Map.Entry<String, Marker> entry : annoncesMarkers.entrySet()){
                                Marker m = entry.getValue();
                                if (m.getPosition().equals(marker.getPosition())){
                                    idAnn.add(m.getTitle());
                                }
                            }
                            /**Initialisation du fragment à charger**/
                            ListeAnnonceFragmentUtilisateur lstAnnonce =  ListeAnnonceFragmentUtilisateur.newInstance(idAnn); //on utilise l'instance et on fournit la valeur attendu dans l'instance
                            /**Ajout d'un fragement**/
                            //Début transaction avec une classe précise
                            /*FragmentTransaction ft=getFragmentManager().beginTransaction();
                            ft.replace(R.id.current_fragment, lstAnnonce);
                            ft.commit();*/
                            ((MainActivity)getActivity()).showFragment(lstAnnonce);
                            return true; //true = on supprime l'interaction de base
                        }
                        }
                    });
                }
            });

            /**Gestion de geofire: pour afficher les annonces autour de notre positiion**/
            DatabaseReference ref = FirebaseDatabase.getInstance().getReference(geoPath);
            geoFire = new GeoFire(ref);

            /**Listeners**/
            /*Listener bouton connection*/
            Button button = (Button) rootView.findViewById(R.id.buttonConnection);
            button.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    //mListener.onClickBtnConnection();
                    if (auth!=null){
                        /*FragmentTransaction ft=getFragmentManager().beginTransaction();
                        ft.replace(R.id.current_fragment, new ProfilFragment());
                        ft.commit();*/
                        ((MainActivity)getActivity()).showFragment(new ProfilFragment());

                    }else{
                        /*FragmentTransaction ft=getFragmentManager().beginTransaction();
                        ft.replace(R.id.current_fragment, new ConnectionFragment());
                        ft.commit();*/
                        ((MainActivity)getActivity()).showFragment(new ConnectionFragment());
                    }

                }
            });

            /*Listener bouton réactiver le positionnement du marker user en fonction de la localisation*/
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
                                //String locality = add.getAddressLine(2); // 2 = Pays
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

    /**Méthode pour redémarrer l'activité**/
    private void restartActivity() {
        Intent intent = getActivity().getIntent();
        getActivity().finish();
        startActivity(intent);
    }

    /**Méthode pour activer un geoquerie: c'est a dire utiliser geoFire pour repérer autour du markeruser les annonces**/
    private void launchQuerie(double lat,double longit) {
    GeoQuery geoQuery = geoFire.queryAtLocation(new GeoLocation(lat, longit), 10.0); //Rayon de 10.0 km
    geoQuery.addGeoQueryEventListener(new GeoQueryEventListener() {
          //Cas un element est entrée dans le rayon (ou trouvé)
          @Override
          public void onKeyEntered(String key, GeoLocation location) {
              //Si jamais il reste une trace pour cette annonce on la supprime
              if (annoncesMarkers.get(key) != null) {
                  annoncesMarkers.get(key).remove(); //on supprime le marker
                  annoncesMarkers.remove(key);
              }
              //Maj carte (ajout du marqueur)
              LatLng loc = new LatLng(location.latitude, location.longitude);
              annoncesMarkers.put(key, googleMap.addMarker(new MarkerOptions()
                      .position(loc)
                      .title(key)
                      .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_ORANGE))
                      .snippet(key)
              ));
          }
          //Cas un element sort
          @Override
          public void onKeyExited(String key) {
              //Recuperer dans la liste des markers, le marker qui a la clé et le supprimer
              if (annoncesMarkers.get(key)!=null) {
                  annoncesMarkers.get(key).remove();
                  annoncesMarkers.remove(key);
              }
          }

          //Cas un element bouge
          @Override
          public void onKeyMoved(String key, GeoLocation location) {
              //Si jamais il reste une trace pour cette annonce on la supprime
              if (annoncesMarkers.get(key) != null) {
                  annoncesMarkers.get(key).remove(); //on supprime le marker
                  annoncesMarkers.remove(key);
              }
              //Maj carte (ajout du marqueur)
              LatLng loc = new LatLng(location.latitude, location.longitude);
              annoncesMarkers.put(key, googleMap.addMarker(new MarkerOptions()
                      .position(loc)
                      .title(key)
                      .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_ORANGE))
                      .snippet(key)
              ));
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

    /**Méthode pour positionner le marker du user à l'adresse (dont on a les coordonnées) entrée par le client**/
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
