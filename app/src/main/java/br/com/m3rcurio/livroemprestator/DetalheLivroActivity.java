package br.com.m3rcurio.livroemprestator;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import br.com.m3rcurio.livroemprestator.model.Interacoes;
import br.com.m3rcurio.livroemprestator.model.Livros;
import br.com.m3rcurio.livroemprestator.model.LivrosBasica;
import br.com.m3rcurio.livroemprestator.model.UsuariosBasica;

public class DetalheLivroActivity extends AppCompatActivity {
    private Interacoes interacao;
    private Livros livro;
    private String rota;
    private String TAG = "DETALHE DO LIVRO";
    private LinearLayout barraBotoes;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detalhe_livro);

        Toolbar myToolbar = (Toolbar) findViewById(R.id.my_toolbar);
        setSupportActionBar(myToolbar);
        myToolbar.setNavigationIcon(R.drawable.ic_arrow_back_black_24dp);
        myToolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        barraBotoes = (LinearLayout)findViewById(R.id.detalheLivroBarraBotoes);

        Intent it = getIntent();
        interacao = (Interacoes) it.getSerializableExtra("interacao");
        rota = (String) it.getSerializableExtra("rota");
        livro= (Livros)it.getSerializableExtra("livro");
        viewHolderDetalheLivro();
    }

    public void btPegarEmprestado(View v){
        Intent intent = new Intent(DetalheLivroActivity.this, BasicaActivity.class);

        if(rota.equals("rotaLivro")){
            intent.putExtra("tipoTela","listaUsuarios");

        }else if(rota.equals("rotaUsuario")) {
            intent.putExtra("tipoTela","iteracoes");
        }
        intent.putExtra("interacao",interacao);
        intent.putExtra("rota",rota);
        startActivity(intent);
    }

    public void btEuTenho(View v){
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

        LivrosBasica livroItem = new LivrosBasica();
        livroItem.setId(livro.getId());
        livroItem.setTitulo(livro.getTitulo());
        livroItem.setSubTitulo(livro.getSubTitulo());
        livroItem.setUrlImagem(livro.getUrlImagem());
        livroItem.setQuantidade(1);

        UsuariosBasica usuario = new UsuariosBasica();
        usuario.setId(user.getUid());
        usuario.setApelido(user.getDisplayName());

        DatabaseReference dadosFirebase = FirebaseDatabase.getInstance().getReference();
        dadosFirebase.child("listaLivrosPorUsuario").child(user.getUid()).child(livro.getId()).setValue(livroItem);
        dadosFirebase.child("listaUsuariosPorLivro").child(livro.getId()).child(user.getUid()).setValue(usuario);
        barraBotoes.setVisibility(v.GONE);
        Log.e(TAG, "livro adcionado a estante"+interacao.getLivro());
    }

    public void viewHolderDetalheLivro(){
        TextView detalheLivroTitulo = (TextView)findViewById(R.id.detalheLivroTitulo);
        TextView detalheLivroSubTitulo = (TextView) findViewById(R.id.detalheLivroSubTitulo);
        TextView detalheLivroAutor = (TextView) findViewById(R.id.detalheLivroAutor);
        TextView detalheLivroResumo =(TextView) findViewById(R.id.detalheLivroResumo);
        detalheLivroTitulo.setText(livro.getTitulo());
        detalheLivroSubTitulo.setText(livro.getSubTitulo());
        detalheLivroResumo.setText(livro.getResumo());

    }
}
