package br.com.m3rcurio.livroemprestator;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;


public class UsuarioPreferenciasFragment extends Fragment {

    private static final String PARM_ID_USUARIO = "idUsuario";

    private String idUsuario;


    public UsuarioPreferenciasFragment() {

    }


    public static UsuarioPreferenciasFragment newInstance(String idUsuario) {
        UsuarioPreferenciasFragment fragment = new UsuarioPreferenciasFragment();
        Bundle args = new Bundle();
        args.putString(PARM_ID_USUARIO, idUsuario);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            idUsuario = getArguments().getString(PARM_ID_USUARIO);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_usuario_preferencias, container, false);
    }


}
