package br.com.m3rcurio.livroemprestator;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
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
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import br.com.m3rcurio.livroemprestator.model.Interacoes;
import br.com.m3rcurio.livroemprestator.model.Livros;


public class ListaLivrosFragment extends Fragment {
    private Interacoes interacao;
    private static final String ARG_INTERACOES = "interacao";

    private List<Livros> listaLivros = new ArrayList<>();
    private String TAG = ListaLivrosFragment.class.getSimpleName();

    //dados da rota
    private static final String ARG_ROTA = "";
    public String rota;

    private ListaLivrosRecyclerViewAdapter recycleViewAdapter;


    public static ListaLivrosFragment newInstance(String rota, Interacoes interacao) {
        ListaLivrosFragment fragment = new ListaLivrosFragment();
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
        View view = inflater.inflate(R.layout.fragment_lista_livros, container, false);


        if (getArguments() != null) {
            rota = getArguments().getString(ARG_ROTA);
            interacao = (Interacoes)getArguments().getSerializable(ARG_INTERACOES);
            //Log.e(TAG,"Usuario "+interacao.getUsuario().getEmail());
        }

        recycleViewAdapter = new ListaLivrosRecyclerViewAdapter(this.getActivity(), listaLivros);
        RecyclerView recyclerView = (RecyclerView) view.findViewById(R.id.listaLivrosRecyclerView);
        recyclerView.setAdapter(recycleViewAdapter);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setLayoutManager(new LinearLayoutManager(this.getActivity()));

        if(rota.equals("rotaLivro")){
            new getDataLivros().execute();
        }else{
            Log.e(TAG, "rotaUsuario");
            listaLivrosDoUsuario(interacao.getUsuarioEmprestador());
        }


        return view;
    }

    public void listaLivrosDoUsuario(String idUsuario){

        DatabaseReference livrosUsuarioFireBase = FirebaseDatabase.getInstance().getReference().child("listaLivrosPorUsuario").child(idUsuario);

        livrosUsuarioFireBase.addChildEventListener(new ChildEventListener() {

            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                Livros livroItem = dataSnapshot.getValue(Livros.class);
                Log.e(TAG, "livro"+livroItem.getTitulo());
                listaLivros.add(livroItem);
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

    public class getDataLivros extends AsyncTask<String, String, String> {

        HttpURLConnection urlConnection;

        @Override
        protected String doInBackground(String... args) {

            StringBuilder result = new StringBuilder();

            try {
                URL url = new URL("https://www.googleapis.com/books/v1/volumes?q=game%20of%20thrones");
                urlConnection = (HttpURLConnection) url.openConnection();
                InputStream in = new BufferedInputStream(urlConnection.getInputStream());

                BufferedReader reader = new BufferedReader(new InputStreamReader(in));

                String line;
                while ((line = reader.readLine()) != null) {
                    result.append(line);
                }

                JSONObject jsonObj = new JSONObject(result.toString());
                JSONArray livros = jsonObj.getJSONArray("items");

                // looping through All Contacts
                for (int i = 0; i < livros.length(); i++) {
                    JSONObject c = livros.getJSONObject(i);
                    JSONObject volumeInfo = c.getJSONObject("volumeInfo");
                    JSONObject imageLinks = volumeInfo.getJSONObject("imageLinks");
                    //JSONArray jsonArrayAutores = volumeInfo.getJSONArray("authors");
                    String name = volumeInfo.getString("title");
                    String imagem = imageLinks.getString("smallThumbnail");

                    //Log.e(TAG, "Titulo do livro: " + name);
                    Livros livro = new Livros();
                    List<String> autores = new ArrayList<>();
                   /* for (int a = 0; a < jsonArrayAutores.length(); a++) {
                        //Log.e(TAG, "Autor: " + jsonArrayAutores.getString(a));
                        autores.add(jsonArrayAutores.getString(a));
                    }*/

                    for (int a = 0; a < 2; a++) {
                        //Log.e(TAG, "Autor: " + jsonArrayAutores.getString(a));
                        autores.add("aaa"+a);
                    }
                    livro.setAutores(autores);
                    livro.setId(c.getString("id"));
                    livro.setTitulo(name);
                    try{
                        livro.setSubTitulo(volumeInfo.getString("subtitle"));
                    }catch(Exception e){
                        livro.setSubTitulo("");
                    }
                    livro.setUrlImagem(imagem);
                    livro.setResumo(volumeInfo.getString("description"));
                    listaLivros.add(livro);
                }
            }catch( Exception e) {
                e.printStackTrace();
                Log.e(TAG, "Erro ao buscar livros: " + e);
            }
            finally {
                urlConnection.disconnect();
                // Log.e(TAG, "Json Livros " + result.toString());
            }
            return result.toString();
        }
        @Override
        protected void onPostExecute(String result) {
            recycleViewAdapter.notifyDataSetChanged();
        }
    }


    public class ListaLivrosViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        public TextView livroTitulo;
        public TextView livroSubTitulo;
        public TextView livroAutor;
        public ImageView livroImagem;
        public Livros livro;

        public ListaLivrosViewHolder(View itemView) {
            super(itemView);
            this.livroTitulo = (TextView) itemView.findViewById(R.id.listaLivroTitulo);
            this.livroSubTitulo = (TextView) itemView.findViewById(R.id.listaLivroSubTitulo);
            this.livroAutor = (TextView) itemView.findViewById(R.id.listaLivroAutor);
            this.livroImagem = (ImageView) itemView.findViewById(R.id.listaLivroImage);

            itemView.setOnClickListener(this);
            RecyclerView.LayoutParams params = (RecyclerView.LayoutParams) itemView.getLayoutParams();
            params.setMargins(35, 0, 0, 0);
            itemView.setLayoutParams(params);
        }

        @Override
        public void onClick(View v) {
            Intent intent = new Intent(ListaLivrosFragment.this.getActivity(), DetalheLivroActivity.class);
            // Log.e(TAG,"Livro "+this.livro.getTitulo() +" / Usuario "+interacao.getUsuario().getNome());
            interacao.setLivro(livro.getId());
            intent.putExtra("interacao", interacao);
            intent.putExtra("rota",rota);
            intent.putExtra("livro",this.livro);
            startActivity(intent);
        }
    }

    public class ListaLivrosRecyclerViewAdapter extends RecyclerView.Adapter<ListaLivrosViewHolder> {
        private Context context;
        private List<Livros> listaLivros;

        public ListaLivrosRecyclerViewAdapter(Context context, List<Livros> livros) {
            this.context = context;
            this.listaLivros = livros;
        }

        @Override
        public ListaLivrosViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(context).inflate(R.layout.item_lista_livro, parent, false);
            return new ListaLivrosViewHolder(view);
        }

        @Override
        public void onBindViewHolder(final ListaLivrosViewHolder holder, int position) {
            Livros livro = listaLivros.get(position);
            holder.livroTitulo.setText(livro.getTitulo());
            holder.livroSubTitulo.setText(livro.getSubTitulo());
            //  holder.livroAutor.setText(livro.getExbicaoAutores());
            Glide.with(context)
                    .load(livro.getUrlImagem())
                    .asBitmap()
                    .diskCacheStrategy(DiskCacheStrategy.NONE)
                    .centerCrop()
                    .into(holder.livroImagem);
            holder.livro = livro;
        }

        @Override
        public int getItemCount() {
            return listaLivros.size();
        }
    }
}
