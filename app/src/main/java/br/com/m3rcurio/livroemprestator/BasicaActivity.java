package br.com.m3rcurio.livroemprestator;

import android.content.Intent;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;

import br.com.m3rcurio.livroemprestator.model.Interacoes;
import br.com.m3rcurio.livroemprestator.model.Usuarios;

public class BasicaActivity extends AppCompatActivity {
    private String tipoTela;
    private String rota;
    private Interacoes interacao;
    private Usuarios usuario;
    private String TAG = "BASICA";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_basica);

        Toolbar myToolbar = (Toolbar) findViewById(R.id.toolbarBasica);
        setSupportActionBar(myToolbar);
        myToolbar.setNavigationIcon(R.drawable.ic_arrow_back_black_24dp);
        myToolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        Fragment selectedFragment = null;

        Intent it = getIntent();
        interacao = (Interacoes) it.getSerializableExtra("interacao");
       // Log.e(TAG,"Interacao id: "+ interacao.getId());
        tipoTela = (String) it.getSerializableExtra("tipoTela");
        rota = (String) it.getSerializableExtra("rota");
       // usuario = (Usuarios)it.getSerializableExtra("usuario");
       // Log.e(TAG,"Interacao usuario: "+ usuario.getEmail());

        Bundle bundle = it.getExtras();
        if (bundle != null) {
            for (String key : bundle.keySet()) {
                Object value = bundle.get(key);
                Log.d(TAG, String.format("%s %s (%s)", key,
                        value.toString(), value.getClass().getName()));
            }
        }

        if(tipoTela.equals("listaLivros")){
            selectedFragment = ListaLivrosFragment.newInstance(rota, interacao);
        }else if(tipoTela.equals("listaUsuarios")){
            selectedFragment = ListaUsuariosFragment.newInstance(rota, interacao);
        }
        else if(tipoTela.equals("iteracoes")){
            selectedFragment = IteracoesFragment.newInstance(interacao);
        }

        transaction.replace(R.id.frameBasica, selectedFragment);
        transaction.commit();
    }
}
