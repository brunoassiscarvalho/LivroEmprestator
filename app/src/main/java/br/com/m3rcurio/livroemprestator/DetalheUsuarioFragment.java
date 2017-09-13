package br.com.m3rcurio.livroemprestator;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.facebook.AccessToken;
import com.facebook.login.LoginManager;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class DetalheUsuarioFragment extends Fragment implements GoogleApiClient.OnConnectionFailedListener{
    private GoogleApiClient mGoogleApiClient;

    public DetalheUsuarioFragment() {
        // Required empty public constructor
    }

    public static DetalheUsuarioFragment newInstance() {
        DetalheUsuarioFragment fragment = new DetalheUsuarioFragment();
      //  Bundle args = new Bundle();
       // fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .build();
        mGoogleApiClient = new GoogleApiClient.Builder(this.getContext())
                .enableAutoManage(this.getActivity(), this)
                .addApi(Auth.GOOGLE_SIGN_IN_API, gso)
                .build();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_detalhe_usuario, container, false);
        TextView txtPerfilApelido = (TextView)v.findViewById(R.id.txtPerfilApelido);
        TextView txtPerfilEmail = (TextView)v.findViewById(R.id.txtPerfilEmail);
        FirebaseUser usuarioAtual = FirebaseAuth.getInstance().getCurrentUser();
        txtPerfilApelido.setText(usuarioAtual.getDisplayName());
        txtPerfilEmail.setText(usuarioAtual.getEmail());

        Button botao = (Button) v.findViewById(R.id.sair);

         botao.setOnClickListener(new View.OnClickListener(){
             @Override
             public void onClick(View v){
               // try{
                    saiGoogle();
                    sairFacebook();
                    FirebaseAuth.getInstance().signOut();
              //  }catch(Exception e){

              //  }
            }
        });
        return v;
    }
    private void sairFacebook(){
        if (AccessToken.getCurrentAccessToken() != null && com.facebook.Profile.getCurrentProfile() != null){
            LoginManager.getInstance().logOut();
        }
    }

    private void saiGoogle() {
        if (mGoogleApiClient!= null) {
            Auth.GoogleSignInApi.signOut(mGoogleApiClient).setResultCallback(
                    new ResultCallback<Status>() {
                        @Override
                        public void onResult(Status status) {

                        }
                    });
        }
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

    public void btTagsUsuario(View v){
       // startActivity(new Intent(getBaseContext(), UsuarioTagsActivity.class));
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mGoogleApiClient.stopAutoManage(getActivity());
        mGoogleApiClient.disconnect();
    }


}
