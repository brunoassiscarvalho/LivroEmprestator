package br.com.m3rcurio.livroemprestator;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.SystemClock;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
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
import com.google.firebase.database.ValueEventListener;

import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import br.com.m3rcurio.livroemprestator.model.Interacoes;
import br.com.m3rcurio.livroemprestator.model.Livros;
import br.com.m3rcurio.livroemprestator.model.Usuarios;

public class IteracoesFragment extends Fragment {

    private LinearLayout barraBotoes;
    private LinearLayout barraResposta;
    private DatabaseReference bancoDados;
    private Interacoes interacao;
    private String codLivro;
    private Livros livro;

    private List<Livros> listaLivros = new ArrayList<>();
    private ListaLivrosRecyclerViewAdapter recycleViewAdapter;

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
        livro = new Livros();
        View v = inflater.inflate(R.layout.fragment_iteracoes, container, false);
        final FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

        String[] tipoIteracoes = getResources().getStringArray(R.array.tipoIteracoes);


        if (getArguments() != null) {
            interacao = (Interacoes)getArguments().getSerializable(ARG_INTERACOES);

        }

        barraBotoes = (LinearLayout)v.findViewById(R.id.iteracoesBarraBotoes);
        barraResposta = (LinearLayout)v.findViewById(R.id.iteracoesBarraResposta);

        View includeUsuarioEmprestador = v.findViewById(R.id.interacaoUsuarioEmprestador);
        View includeUsuarioLeitor = v.findViewById(R.id.interacaoUsuarioLeitor);

        buscarUsuario(interacao.getUsuarioEmprestador(),includeUsuarioEmprestador);
        buscarUsuario(interacao.getUsuarioLeitor(),includeUsuarioLeitor);


        TextView iteracaoDescricao = (TextView) v.findViewById(R.id.iteracaoDescricao);
        iteracaoDescricao.setText(tipoIteracoes[interacao.getStatus()]);

        Button botaoCancelar = (Button) v.findViewById(R.id.btIteracoesCancelar);

        botaoCancelar.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                Intent intent = new Intent(getActivity(), MainActivity.class);
                startActivity(intent);
                getActivity().finishAffinity();
            }
        });
        barraResposta.setVisibility(View.GONE);
        Log.e(TAG, "Status Interacao: "+interacao.getStatus() );
        if(interacao.getStatus()==0){
            barraBotoes.setVisibility(View.VISIBLE);
        }else if(interacao.getStatus()==1 ){
            if(interacao.getUsuarioLeitor().equals(user.getUid())){
                barraBotoes.setVisibility(View.GONE);
            }
        }else if(interacao.getStatus()==2 ){
            if(interacao.getUsuarioEmprestador().equals(user.getUid())){
                barraBotoes.setVisibility(View.GONE);
            }
        }else if(interacao.getStatus()==3 ){
            if(interacao.getUsuarioEmprestador().equals(user.getUid())){
                barraBotoes.setVisibility(View.GONE);
            }
        }else if(interacao.getStatus()==4 ){
            if(interacao.getUsuarioLeitor().equals(user.getUid())){
                barraBotoes.setVisibility(View.GONE);
            }
        }else if(interacao.getStatus()>4){
            barraBotoes.setVisibility(View.GONE);
        }

        Button botaoConfirmar = (Button) v.findViewById(R.id.btIteracoesConfirma);

        botaoConfirmar.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){

                if(interacao.getStatus()==0){
                    Timestamp timestamp = new Timestamp(System.currentTimeMillis());
                    interacao.setId(interacao.getUsuarioLeitor()+interacao.getUsuarioEmprestador()+timestamp.getTime());
                }
                interacao.setStatus(interacao.getStatus()+1);
                DatabaseReference usuarioFireBase = FirebaseDatabase.getInstance().getReference();
                usuarioFireBase.child("interacaoPorUsuario").child(interacao.getUsuarioLeitor()).child(interacao.getId()).setValue(interacao);
                usuarioFireBase.child("interacaoPorUsuario").child(interacao.getUsuarioEmprestador()).child(interacao.getId()).setValue(interacao);
                Log.e(TAG,"iteração id"+interacao.getId());

                if(interacao.getStatus()==3){

                    AlarmManager alarmMgr = (AlarmManager) getActivity().getSystemService(Context.ALARM_SERVICE);

                    Intent intent = new Intent(getActivity(), AlarmReceiver.class);
                    intent.putExtra("tituloLivro",livro.getTitulo());
                    PendingIntent alarmIntent = PendingIntent.getBroadcast(getActivity().getBaseContext(), 0, intent, 0);

                    Calendar calendar = Calendar.getInstance();
                    // calendar.setTimeInMillis(System.currentTimeMillis());
                    calendar.set(Calendar.HOUR_OF_DAY, 14);
                    calendar.add( Calendar.DAY_OF_MONTH , 5 );
                    long inicio = calendar.getTimeInMillis();
                    alarmMgr.set(AlarmManager.RTC ,  inicio, alarmIntent);

                   // alarmMgr.set(AlarmManager.ELAPSED_REALTIME_WAKEUP,  SystemClock.elapsedRealtime() + 10 * 1000, alarmIntent);
                    Log.e(TAG, "Criado o alarme" );

                }


                barraBotoes.setVisibility(View.GONE);
                barraResposta.setVisibility(View.VISIBLE);
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
        recycleViewAdapter = new ListaLivrosRecyclerViewAdapter(this.getActivity(), listaLivros);
        RecyclerView recyclerView = (RecyclerView) v.findViewById(R.id.iteracaoLivrosRecyclerView);
        recyclerView.setAdapter(recycleViewAdapter);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setLayoutManager(new LinearLayoutManager(this.getActivity()));

        codLivro = interacao.getLivro();
        new getDataLivros().execute();

        return v;

    }



    private void buscarUsuario(String idUsuario, View v){
        final View view = v;
        bancoDados = FirebaseDatabase.getInstance().getReference().child("listaUsuarios").child(idUsuario);
        bancoDados.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Usuarios usuarioRetorno = dataSnapshot.getValue(Usuarios.class);
                Log.e(TAG, "usuario recupera: "+usuarioRetorno.getEmail());

                TextView apelido = (TextView) view.findViewById(R.id.listaUsuarioApelido);
                ImageView usuarioImagem = (ImageView) view.findViewById(R.id.listaLivroImage);
                apelido.setText(usuarioRetorno.getApelido());
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
                URL url = new URL("https://www.googleapis.com/books/v1/volumes/"+codLivro);
                urlConnection = (HttpURLConnection) url.openConnection();
                InputStream in = new BufferedInputStream(urlConnection.getInputStream());

                BufferedReader reader = new BufferedReader(new InputStreamReader(in));

                String line;
                while ((line = reader.readLine()) != null) {
                    result.append(line);
                }

                JSONObject jsonObj = new JSONObject(result.toString());
                Log.e(TAG, "doInBackground: "+result.toString() );
                JSONObject volumeInfo = jsonObj.getJSONObject("volumeInfo");
                JSONObject imageLinks = volumeInfo.getJSONObject("imageLinks");
                //JSONArray jsonArrayAutores = volumeInfo.getJSONArray("authors");
                String name = volumeInfo.getString("title");
                String imagem = imageLinks.getString("smallThumbnail");
                List<String> autores = new ArrayList<>();
                for (int a = 0; a < 2; a++) {
                    //Log.e(TAG, "Autor: " + jsonArrayAutores.getString(a));
                    autores.add("aaa"+a);
                }

                livro.setAutores(autores);
                livro.setId(jsonObj.getString("id"));
                livro.setTitulo(name);
                try{
                    livro.setSubTitulo(volumeInfo.getString("subtitle"));
                }catch(Exception e){
                    livro.setSubTitulo("");
                }
                livro.setUrlImagem(imagem);
                livro.setResumo(volumeInfo.getString("description"));
                listaLivros.add(livro);

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
            return new IteracoesFragment.ListaLivrosViewHolder(view);
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

    private void buscarInteracao(final String idInteracao, String idUsuario){

        bancoDados = FirebaseDatabase.getInstance().getReference().child("interacaoPorUsuario").child(idUsuario);
        bancoDados.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                Interacoes interacaoRetorno = dataSnapshot.getValue(Interacoes.class);
                if(interacaoRetorno.getId().equals(idInteracao)){
                    interacao = interacaoRetorno;
                }
                Log.e(TAG, "usuario recupera: "+interacaoRetorno.getStatus());


            }
            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });
    }
}
