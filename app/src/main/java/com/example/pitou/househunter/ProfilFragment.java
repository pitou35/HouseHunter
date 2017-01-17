package com.example.pitou.househunter;

import android.app.Fragment;
import android.app.FragmentTransaction;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;
import android.widget.Toolbar;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

/**
 * Created by pitou on 20/11/2016.
 */

public class ProfilFragment extends Fragment {

    private Button  btnChangePassword, btnRemoveUser,
             changePassword, remove, signOut, BLesAnnones;

    private EditText password, newPassword;
    private ProgressBar progressBar;
    private FirebaseAuth.AuthStateListener authListener;
    private FirebaseAuth auth;
    private Button btnBack;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup parent, Bundle savedInstanceState) {

        View view= inflater.inflate(R.layout.fragment_profil, parent, false);


        auth = FirebaseAuth.getInstance();

        final FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

        authListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();
                if (user == null) {
                    /*FragmentTransaction ft=getFragmentManager().beginTransaction();
                    ft.replace(R.id.current_fragment, new ConnectionFragment());
                    ft.commit();*/
                    ((MainActivity)getActivity()).showFragment(new ConnectionFragment());

                }
            }
        };

        btnBack = (Button) view.findViewById(R.id.btn_back);
        btnChangePassword = (Button) view.findViewById(R.id.change_password_button);
        btnRemoveUser = (Button) view.findViewById(R.id.remove_user_button);

        changePassword = (Button) view.findViewById(R.id.changePass);

        remove = (Button) view.findViewById(R.id.remove);
        signOut = (Button) view.findViewById(R.id.sign_out);
        BLesAnnones = (Button) view.findViewById(R.id.BLesAnnones);

        password = (EditText) view.findViewById(R.id.password);
        newPassword = (EditText) view.findViewById(R.id.newPassword);


        password.setVisibility(View.GONE);
        newPassword.setVisibility(View.GONE);
        changePassword.setVisibility(View.GONE);
        remove.setVisibility(View.GONE);

        progressBar = (ProgressBar) view.findViewById(R.id.progressBar);

        if (progressBar != null) {
            progressBar.setVisibility(View.GONE);
        }

        btnChangePassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                password.setVisibility(View.GONE);
                newPassword.setVisibility(View.VISIBLE);
                changePassword.setVisibility(View.VISIBLE);
                remove.setVisibility(View.GONE);
            }
        });


        BLesAnnones.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                /*FragmentTransaction ft=getFragmentManager().beginTransaction();
                ft.replace(R.id.current_fragment, new ListeAnnonceFragment());
                ft.commit();*/
                ((MainActivity)getActivity()).showFragment(new ListeAnnonceFragment());

            }
        });

        changePassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                progressBar.setVisibility(View.VISIBLE);
                if (user != null && !newPassword.getText().toString().trim().equals("")) {
                    if (newPassword.getText().toString().trim().length() < 6) {
                        newPassword.setError("Mot de passe trop court. MInimum 6 caractères");
                        progressBar.setVisibility(View.GONE);
                    } else {
                        user.updatePassword(newPassword.getText().toString().trim())
                                .addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        if (task.isSuccessful()) {
                                            Toast.makeText(getActivity(), "LE mot de passe est mis à jour, connectez-vous avec le nouveau mot de passe!", Toast.LENGTH_SHORT).show();
                                            signOut();
                                            progressBar.setVisibility(View.GONE);
                                        } else {
                                            Toast.makeText(getActivity(), "Impossible de mettre à jour le mot de passe!", Toast.LENGTH_SHORT).show();
                                            progressBar.setVisibility(View.GONE);
                                        }
                                    }
                                });
                    }
                } else if (newPassword.getText().toString().trim().equals("")) {
                    newPassword.setError("Saisir mot de passe");
                    progressBar.setVisibility(View.GONE);
                }
            }
        });

        btnRemoveUser.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                progressBar.setVisibility(View.VISIBLE);
                if (user != null) {
                    user.delete()
                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if (task.isSuccessful()) {
                                        Toast.makeText(getActivity(), "Votre profil est supprimé:( Créez un compte maintenant!", Toast.LENGTH_SHORT).show();
                                        /*FragmentTransaction ft=getFragmentManager().beginTransaction();
                                        ft.replace(R.id.current_fragment, new CreateAccFragment());
                                        ft.commit();*/
                                        progressBar.setVisibility(View.GONE);
                                        ((MainActivity)getActivity()).showFragment(new CreateAccFragment());
                                    } else {
                                        Toast.makeText(getActivity(), "Impossible de supprimer le compte!", Toast.LENGTH_SHORT).show();
                                        progressBar.setVisibility(View.GONE);
                                    }
                                }
                            });
                }
            }
        });

        signOut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                signOut();
            }
        });
        return view;
    }

    public void signOut() {
        auth.signOut();
    }

    @Override
    public void onResume() {
        super.onResume();
        progressBar.setVisibility(View.GONE);
    }

    @Override
    public void onStart() {
        super.onStart();
        auth.addAuthStateListener(authListener);
    }

    @Override
    public void onStop() {
        super.onStop();
        if (authListener != null) {
            auth.removeAuthStateListener(authListener);
        }
    }
}
