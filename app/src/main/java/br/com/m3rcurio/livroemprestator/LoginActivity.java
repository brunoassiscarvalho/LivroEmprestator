package br.com.m3rcurio.livroemprestator;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;

import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FacebookAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import br.com.m3rcurio.livroemprestator.model.Usuarios;

public class LoginActivity extends AppCompatActivity implements  GoogleApiClient.OnConnectionFailedListener{
    private String TAG = "LOGIN";
    private FirebaseAuth mAuth;
    private EditText mCampoEmail;
    private EditText mCampoSenha;
    private FirebaseAuth.AuthStateListener mAuthListener;
    private GoogleApiClient mGoogleApiClient;
    private static final int RC_SIGN_IN = 9001;
    CallbackManager mCallbackManager;
    private Usuarios usuarioLogado;
    private String teste;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mAuth = FirebaseAuth.getInstance();
        setContentView(R.layout.activity_login);

        //google
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();

        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .enableAutoManage(this, this)
                .addApi(Auth.GOOGLE_SIGN_IN_API, gso)
                .build();

        //firebase - verifica se o usuario está logado
        mAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();
                if (user != null) {

                    Log.d(TAG, "Usuario Logado:" + user.getUid());
                    Usuarios usuario = new Usuarios(user.getUid(), user.getDisplayName(),user.getDisplayName(),user.getEmail());

                    DatabaseReference usuarioFireBase = FirebaseDatabase.getInstance().getReference().child("listaUsuarios").child(usuario.getId());
                    usuarioFireBase.setValue(usuario);
                    //recuperaDadosUsuario(usuario.getId(), usuarioFireBase);
                    Intent intent = new Intent(getBaseContext(), MainActivity.class);
                    intent.putExtra("usuarioLogado", usuario);
                    intent.putExtra("itemSelecionado", 1);
                    startActivity(intent);

                } else {
                    Log.d(TAG, "Usuario Não Logado");

                    mCampoEmail = (EditText) findViewById(R.id.editLoginEmail);
                    mCampoSenha =  (EditText) findViewById(R.id.editLoginSenha);
                }
                // ...
            }
        };

        //facebook
        mCallbackManager = CallbackManager.Factory.create();
        LoginManager.getInstance().registerCallback(mCallbackManager,
                new FacebookCallback<LoginResult>() {
                    @Override
                    public void onSuccess(LoginResult loginResult) {
                        Log.d(TAG, "Logado no facebook" + loginResult);
                        handleFacebookAccessToken(loginResult.getAccessToken());
                    }

                    @Override
                    public void onCancel() {
                        Log.d(TAG, "Deslogado do facebook");
                        // ...
                    }

                    @Override
                    public void onError(FacebookException error) {
                        Log.d(TAG, "Erro ao logar no facebook", error);
                        // ...
                    }
                });
    }

    @Override
    public void onStart() {
        super.onStart();
        mAuth.addAuthStateListener(mAuthListener);
    }

    @Override
    public void onStop() {
        super.onStop();
        if (mAuthListener != null) {
            mAuth.removeAuthStateListener(mAuthListener);
        }
    }

    //login com email
    public void fazerLogin (View v){
        logar(mCampoEmail.getText().toString(), mCampoSenha.getText().toString());
    }

    public void vaiParaCriarUsuario (View v){
        Intent intent = new Intent(getBaseContext(), CriarUsuarioActivity.class);
        startActivity(intent);
    }

    private void logar(final String email, String password) {
        Log.d(TAG, "logar com email: "+ email );
        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete( Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            Log.d(TAG, "Usuario logado com email: "+ email);
                            //salva usuario na lista
                            FirebaseUser user = mAuth.getCurrentUser();
                            Usuarios usuario = new Usuarios(user.getUid(), user.getDisplayName(),user.getDisplayName(),user.getEmail());
                            DatabaseReference usuarioFireBase = FirebaseDatabase.getInstance().getReference().child("listaUsuarios").child(usuario.getId());
                            usuarioFireBase.setValue(usuario);
                            // vai para a main
                            Intent intent = new Intent(getBaseContext(), MainActivity.class);
                            intent.putExtra("usuarioLogado", usuario);
                            intent.putExtra("itemSelecionado", 1);
                            startActivity(intent);
                        } else {
                            Log.w(TAG, "Falha no login com email", task.getException());
                            Toast.makeText(LoginActivity.this, "Falhou na autenticação",
                                    Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }
    //login google
    public void loginGoogle(View v){
        Intent signInIntent = Auth.GoogleSignInApi.getSignInIntent(mGoogleApiClient);
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }

    private void firebaseAuthWithGoogle(GoogleSignInAccount acct) {
        Log.d(TAG, "Firebase com google");
        AuthCredential credential = GoogleAuthProvider.getCredential(acct.getIdToken(), null);
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete( Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            Log.d(TAG, "Sucesso Firebase com google:" + task.isSuccessful());
                            FirebaseUser user = mAuth.getCurrentUser();
                            Usuarios usuario = new Usuarios(user.getUid(), user.getDisplayName(),user.getDisplayName(),user.getEmail());
                            DatabaseReference usuarioFireBase = FirebaseDatabase.getInstance().getReference().child("listaUsuarios").child(usuario.getId());
                            usuarioFireBase.setValue(usuario);
                        }else{
                            Log.w(TAG, "Insucesso Firebase com google", task.getException());
                            Toast.makeText(LoginActivity.this, "Falhou na autenticação", Toast.LENGTH_SHORT).show();

                        }
                    }
                });
    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {

    }

    //login facebook
    private void handleFacebookAccessToken(AccessToken token) {
        Log.d(TAG, "handleFacebookAccessToken:" + token);

        AuthCredential credential = FacebookAuthProvider.getCredential(token.getToken());
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            Log.d(TAG, "Logado no Facebook");
                            FirebaseUser user = mAuth.getCurrentUser();
                            Usuarios usuario = new Usuarios(user.getUid(), user.getDisplayName(),user.getDisplayName(),user.getEmail());
                            DatabaseReference usuarioFireBase = FirebaseDatabase.getInstance().getReference().child("listaUsuarios").child(usuario.getId());
                            usuarioFireBase.setValue(usuario);
                        } else {
                            // If sign in fails, display a message to the user.
                            Log.w(TAG, "Deslogado do Facebook", task.getException());
                            Toast.makeText(LoginActivity.this, "Falhou na autenticação",
                                    Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }
    //resultado da activity de login
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        //retorno google
        if (requestCode == RC_SIGN_IN) {
            GoogleSignInResult result = Auth.GoogleSignInApi.getSignInResultFromIntent(data);
            if (result.isSuccess()) {
                GoogleSignInAccount account = result.getSignInAccount();
                firebaseAuthWithGoogle(account);
            } else {

            }
        }
        mCallbackManager.onActivityResult(requestCode, resultCode, data);
    }

    public void recuperaDadosUsuario(String idUsuario, DatabaseReference usuarioFireBase){

        final Usuarios[] usuario = new Usuarios[1];
        usuarioFireBase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                teste = dataSnapshot.getValue().toString();
                Log.e(TAG, "usuario recupera: "+teste);


                //notifyDataSetChanged();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
        // usuarioLogado = usuario[0];
        //Log.e(TAG, "usuario 2: "+usuarioLogado.getEmail());
    }

}
