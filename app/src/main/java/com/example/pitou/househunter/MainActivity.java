package com.example.pitou.househunter;


import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {

    public static int ACCESS_FINE_LOCATION = 1;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        showFragment(new CarteFragment());
    }

    public void showFragment(Fragment fragment) {
        getFragmentManager().beginTransaction()
                .replace(R.id.current_fragment, fragment, fragment.getTag())
                .addToBackStack(fragment.getTag())
                .commit();
    }

    @Override
    public void onBackPressed(){
        FragmentManager FM=getFragmentManager();
        if (FM != null && FM.getBackStackEntryCount() > 1)
        {
            FM.popBackStack();
        }else {
            finish();
        }
    }
}
