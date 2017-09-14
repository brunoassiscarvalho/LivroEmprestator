package br.com.m3rcurio.livroemprestator;

import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.List;

import br.com.m3rcurio.livroemprestator.model.Interacoes;

public class ListaIteracoesFragment extends Fragment {

    private List<Interacoes> listaIteracoes = new ArrayList<>();
    private ListaInteracoesRecyclerViewAdapter recycleViewAdapter;
    private DatabaseReference bancoDados;
    private String[] tipoIteracoes;


    public ListaIteracoesFragment() {
        // Required empty public constructor
    }

    public static ListaIteracoesFragment newInstance() {
        ListaIteracoesFragment fragment = new ListaIteracoesFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {  super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        Resources res = getResources();
        tipoIteracoes = res.getStringArray(R.array.tipoIteracoes);

        View view = inflater.inflate(R.layout.fragment_lista_iteracoes, container, false);
        recycleViewAdapter = new ListaInteracoesRecyclerViewAdapter(this.getActivity(), listaIteracoes);
        RecyclerView recyclerView = (RecyclerView) view.findViewById(R.id.listaInteracoesRecyclerView);
        recyclerView.setAdapter(recycleViewAdapter);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setLayoutManager(new LinearLayoutManager(this.getActivity()));

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

        bancoDados = FirebaseDatabase.getInstance().getReference().child("interacaoPorUsuario").child(user.getUid());
        bancoDados.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                Interacoes interacoes = dataSnapshot.getValue(Interacoes.class);
                listaIteracoes.add(interacoes);
                recycleViewAdapter.notifyDataSetChanged();
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }

        });
        return view;
    }

    public class ListaInteracoesViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        public ImageView usuarioImagem;
        public TextView descricaoTipoInteracao;
        public ImageView livroImagem;
        public Interacoes interacao;

        public ListaInteracoesViewHolder(View itemView) {
            super(itemView);
            this.usuarioImagem = (ImageView) itemView.findViewById(R.id.listaIteracoesUsuarioImage);
            this.descricaoTipoInteracao = (TextView) itemView.findViewById(R.id.listaIteracoesTipo);
            this.livroImagem = (ImageView) itemView.findViewById(R.id.listaIteracoesLivroImage);

            itemView.setOnClickListener(this);
            RecyclerView.LayoutParams params = (RecyclerView.LayoutParams) itemView.getLayoutParams();
            params.setMargins(35, 0, 0, 0);
            itemView.setLayoutParams(params);
        }

        @Override
        public void onClick(View v) {
            Intent intent = new Intent(ListaIteracoesFragment.this.getActivity(), BasicaActivity.class);
            intent.putExtra("interacao",interacao);
            intent.putExtra("tipoTela","iteracoes");
            startActivity(intent);
        }
    }

    public class ListaInteracoesRecyclerViewAdapter extends RecyclerView.Adapter<ListaIteracoesFragment.ListaInteracoesViewHolder> {
        private Context context;
        private List<Interacoes> listaIteracoes;

        public ListaInteracoesRecyclerViewAdapter(Context context, List<Interacoes> listInteracoes) {
            this.context = context;
            this.listaIteracoes = listInteracoes;
        }

        @Override
        public ListaIteracoesFragment.ListaInteracoesViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(context).inflate(R.layout.item_lista_interacoes, parent, false);
            return new ListaIteracoesFragment.ListaInteracoesViewHolder(view);
        }

        @Override
        public void onBindViewHolder(final ListaIteracoesFragment.ListaInteracoesViewHolder holder, int position) {
            Interacoes interacoes = listaIteracoes.get(position);
            holder.descricaoTipoInteracao.setText(tipoIteracoes[interacoes.getStatus()]);
            holder.interacao = interacoes;
        }

        @Override
        public int getItemCount() {
            return listaIteracoes.size();
        }
    }

}
