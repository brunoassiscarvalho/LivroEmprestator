package br.com.m3rcurio.livroemprestator;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;



public class EstanteUsuarioFragment extends Fragment {

    public EstanteUsuarioFragment() {
        // Required empty public constructor
    }


    public static EstanteUsuarioFragment newInstance() {
        EstanteUsuarioFragment fragment = new EstanteUsuarioFragment();

        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_estante_usuario, container, false);
    }


}
