package br.com.m3rcurio.livroemprestator;

import android.content.Intent;
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

public class CriarUsuarioActivity extends AppCompatActivity {
    private String TAG = "CRIA USUARIO";
    private FirebaseAuth mAuth;
    private EditText mCampoNome;
    private EditText mCampoEmail;
    private EditText mCampoSenha;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_criar_usuario);
        mAuth = FirebaseAuth.getInstance();
        mCampoNome = (EditText) findViewById(R.id.editCadastroUsuarioNome);
        mCampoEmail = (EditText) findViewById(R.id.editCadastroUsuarioEmail);
        mCampoSenha =  (EditText) findViewById(R.id.editCadastroUsuarioSenha);
    }

    public void criarUsuario(View v){
        criarConta(mCampoEmail.getText().toString(), mCampoSenha.getText().toString());
    }

    private void criarConta(String email, String password) {
        Log.d(TAG, "Criar Usuario:" + email);
        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d(TAG, "Criar usuario com email");
                            FirebaseUser user = mAuth.getCurrentUser();
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
