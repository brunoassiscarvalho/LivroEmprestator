package br.com.m3rcurio.livroemprestator;


import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
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
import br.com.m3rcurio.livroemprestator.model.Usuarios;


public class ListaUsuariosFragment extends Fragment {
    private static String TAG = "LISTA USUARIO";

    public Interacoes interacao;
    private static final String ARG_INTERACOES = "interacao";

    private static final String ARG_ROTA = "rota";
    public String rota;

    private DatabaseReference bancoDados;
    private List<Usuarios> listaUsuarios = new ArrayList<>();
    private ListaUsuariosRecyclerViewAdapter recycleViewAdapter;

    public static ListaUsuariosFragment newInstance(String rota, Interacoes interacao) {
        ListaUsuariosFragment fragment = new ListaUsuariosFragment();
        Bundle args = new Bundle();
        args.putString(ARG_ROTA, rota);
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
        View view = inflater.inflate(R.layout.fragment_lista_usuarios, container, false);
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

        if (getArguments() != null) {
            rota = getArguments().getString(ARG_ROTA);
            interacao = (Interacoes)getArguments().getSerializable(ARG_INTERACOES);
        }

        recycleViewAdapter = new ListaUsuariosRecyclerViewAdapter(this.getActivity(), listaUsuarios);
        RecyclerView recyclerView = (RecyclerView) view.findViewById(R.id.listaUsuariosRecyclerView);
        recyclerView.setAdapter(recycleViewAdapter);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setLayoutManager( new LinearLayoutManager(this.getActivity(), LinearLayoutManager.VERTICAL, false));


        if(rota.equals("rotaLivro")){
            listaUsuariosPorLivro(interacao.getLivro(),user);
        }else{
            Log.e(TAG, "rotaUsuario");
            listaUsuarios(user);
        }

        return view;
    }

    private void listaUsuarios(final FirebaseUser user){
        bancoDados = FirebaseDatabase.getInstance().getReference().child("listaUsuarios");
        bancoDados.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                Usuarios usuario = dataSnapshot.getValue(Usuarios.class);
                if(!usuario.getId().equals(user.getUid())){
                    listaUsuarios.add(usuario);
                }

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
    }

    private void listaUsuariosPorLivro(String idLivro ,final FirebaseUser user ){
        DatabaseReference livrosUsuarioFireBase = FirebaseDatabase.getInstance().getReference().child("listaUsuariosPorLivro").child(idLivro);

        livrosUsuarioFireBase.addChildEventListener(new ChildEventListener() {

            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                Usuarios usuarioItem = dataSnapshot.getValue(Usuarios.class);
                Log.e(TAG, "usuario"+usuarioItem.getEmail());
                if(!usuarioItem.getId().equals(user.getUid())){
                    listaUsuarios.add(usuarioItem);
                }

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
    }

    public class ListaUsuariosViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        public TextView apelido;
        public ImageView usuarioImagem;
        public Usuarios usuario;

        public ListaUsuariosViewHolder(View itemView) {
            super(itemView);
            this.apelido = (TextView) itemView.findViewById(R.id.listaUsuarioApelido);
            this.usuarioImagem = (ImageView) itemView.findViewById(R.id.listaUsuariooImage);

            itemView.setOnClickListener(this);
            RecyclerView.LayoutParams params = (RecyclerView.LayoutParams) itemView.getLayoutParams();
            params.setMargins(35, 0, 0, 0);
            itemView.setLayoutParams(params);
        }

        @Override
        public void onClick(View v) {
            Intent intent = new Intent(ListaUsuariosFragment.this.getActivity(), BasicaActivity.class);
            if (rota.equals("rotaLivro")) {
                intent.putExtra("tipoTela","iteracoes");
            }else if (rota.equals("rotaUsuario")){
                intent.putExtra("tipoTela","listaLivros");
            }
            interacao.setUsuarioEmprestador(usuario.getId());
            intent.putExtra("rota",rota);
            intent.putExtra("interacao",interacao);
            startActivity(intent);
        }
    }

    public class ListaUsuariosRecyclerViewAdapter extends RecyclerView.Adapter<ListaUsuariosFragment.ListaUsuariosViewHolder> {
        private Context context;
        private List<Usuarios> listaUsuarios;

        public ListaUsuariosRecyclerViewAdapter(Context context, List<Usuarios> livros) {
            this.context = context;
            this.listaUsuarios = livros;
        }

        @Override
        public ListaUsuariosFragment.ListaUsuariosViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(context).inflate(R.layout.item_lista_usuarios, parent, false);
            return new ListaUsuariosFragment.ListaUsuariosViewHolder(view);
        }

        @Override
        public void onBindViewHolder(final ListaUsuariosFragment.ListaUsuariosViewHolder holder, int position) {
            Usuarios usuario = listaUsuarios.get(position);
            holder.apelido.setText(usuario.getApelido());
            // holder.livroSubTitulo.setText(usuario.getSubTitulo());
            // holder.livroAutor.setText(usuario.getExbicaoAutores());
            Glide.with(context)
                    .load(usuario.getUrlImagem())
                    .asBitmap()
                    .diskCacheStrategy(DiskCacheStrategy.NONE)
                    .centerCrop()
                    .into(holder.usuarioImagem);
            holder.usuario = usuario;
        }

        @Override
        public int getItemCount() {
            return listaUsuarios.size();
        }
    }
}