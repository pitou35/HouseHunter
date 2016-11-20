package com.example.pitou.househunter;


import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;

public class MainActivity extends AppCompatActivity implements CarteFragment.OnFragmentMapInteractionListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        FragmentTransaction ft=getFragmentManager().beginTransaction();

        ft.replace(R.id.current_fragment, new CreateAnnoncePropFragment());

        ft.commit();
    }

    /**Implémentaiton des listenrs du fragements carte**/
    @Override
    public void onClickBtnConnection() {
        FragmentTransaction ft=getFragmentManager().beginTransaction();
        ft.replace(R.id.current_fragment, new ConnectionFragment());
        ft.commit();
    }
}
