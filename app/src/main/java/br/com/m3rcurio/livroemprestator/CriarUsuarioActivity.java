package br.com.m3rcurio.livroemprestator;

import android.content.Intent;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import br.com.m3rcurio.livroemprestator.model.Usuarios;

public class CriarUsuarioActivity extends AppCompatActivity {
    private String TAG = "CRIAR USUARIO";
    private FirebaseAuth mAuth;
    private EditText mCampoNome;
    private EditText mCampoEmail;
    private EditText mCampoSenha;
    private EditText mCampoApelido;
    private DatabaseReference bancoDados;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_criar_usuario);
        mAuth = FirebaseAuth.getInstance();
        mCampoNome = (EditText) findViewById(R.id.editCadastroUsuarioNome);
        mCampoApelido = (EditText) findViewById(R.id.editCadastroUsuarioApelido);
        mCampoEmail = (EditText) findViewById(R.id.editCadastroUsuarioEmail);
        mCampoSenha =  (EditText) findViewById(R.id.editCadastroUsuarioSenha);
        bancoDados = FirebaseDatabase.getInstance().getReference();

    }

    public void criarUsuario(View v){
        criarContaFirebase(mCampoEmail.getText().toString(), mCampoSenha.getText().toString() ,  mCampoApelido.getText().toString() );
       // Usuarios usuario = new Usuarios();

    }

    private void criarContaFirebase(final String email, String password, final String apelido) {
        Log.d(TAG, "Criar Usuario:" + email);
        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d(TAG, "Criar usuario com email");
                            FirebaseUser user = mAuth.getCurrentUser();
                            UserProfileChangeRequest profileUpdates = new UserProfileChangeRequest.Builder()
                                    .setDisplayName(apelido)
                                    .setPhotoUri(Uri.parse("http://www.sitedecuriosidades.com/im/g/CEA60.jpg"))
                                    .build();

                            user.updateProfile(profileUpdates)
                                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            if (task.isSuccessful()) {
                                                Log.d(TAG, "User profile updated.");
                                            }
                                        }
                                    });
                            //criaLIsta De usuários do firebase
                            Usuarios usuario = new Usuarios(user.getUid(), apelido, apelido, email);
                            DatabaseReference usuarioFireBase = FirebaseDatabase.getInstance().getReference().child("listaUsuarios").child(usuario.getId());
                            usuarioFireBase.setValue(usuario);
                            Intent intent = new Intent(getBaseContext(), PerfilUsuarioActivity.class);
                            startActivity(intent);
                        } else {
                            // If sign in fails, display a message to the user.
                            Log.w(TAG, "Não foi possível criar usuário com email", task.getException());
                            Toast.makeText(CriarUsuarioActivity.this, "A autenticação falhou.",
                                    Toast.LENGTH_SHORT).show();

                        }

                    }
                });
    }
}
