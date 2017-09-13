package br.com.m3rcurio.livroemprestator;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.sql.Timestamp;

import br.com.m3rcurio.livroemprestator.model.Interacoes;

public class IteracoesFragment extends Fragment {
    private TextView iteracaoResposta;
    private LinearLayout barraBotoes;
    private LinearLayout barraResposta;
    private DatabaseReference bancoDados;
    private Interacoes interacao;
    private static final String ARG_INTERACOES = "interacao";
    private String TAG = "INTERAÇÃO";

    public IteracoesFragment() {
        // Required empty public constructor
    }

    public static IteracoesFragment newInstance(Interacoes interacao) {
        IteracoesFragment fragment = new IteracoesFragment();
        Bundle args = new Bundle();
        args.putSerializable(ARG_INTERACOES,interacao);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_iteracoes, container, false);
        final FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();


        if (getArguments() != null) {
            interacao = (Interacoes)getArguments().getSerializable(ARG_INTERACOES);

        }

        barraBotoes = (LinearLayout)v.findViewById(R.id.iteracoesBarraBotoes);
        barraResposta = (LinearLayout)v.findViewById(R.id.iteracoesBarraResposta);

        Button botaoCancelar = (Button) v.findViewById(R.id.btIteracoesCancelar);

        botaoCancelar.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                Intent intent = new Intent(getActivity(), MainActivity.class);
                startActivity(intent);
                getActivity().finishAffinity();
            }
        });

        Button botaoConfirmar = (Button) v.findViewById(R.id.btIteracoesConfirma);

        botaoConfirmar.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                int status = interacao.getStatus()+1;
                if(status==1){
                    Timestamp timestamp = new Timestamp(System.currentTimeMillis());
                    interacao.setId(interacao.getUsuarioLeitor()+interacao.getUsuarioEmprestador()+timestamp.getTime());
                }
                DatabaseReference usuarioFireBase = FirebaseDatabase.getInstance().getReference();
                interacao.setStatus(status);
                //usuarioFireBase.child("listaInteracoes").child(interacao.getId()).setValue(interacao);
                usuarioFireBase.child("interacaoPorUsuario").child(user.getUid()).setValue(interacao);
                usuarioFireBase.child("interacaoPorUsuario").child(interacao.getUsuarioEmprestador()).setValue(interacao);

                Log.e(TAG,"iteração id"+interacao.getId());

                barraBotoes.setVisibility(View.GONE);
            }
        });

        Button btIteracoesOk = (Button) v.findViewById(R.id.btIteracoesOk);

        btIteracoesOk.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                Intent intent = new Intent(getActivity(), MainActivity.class);
                intent.putExtra("itemSelecionado", 3);
                startActivity(intent);
                getActivity().finishAffinity();
            }
        });
        return v;

    }
}
