package br.com.m3rcurio.livroemprestator;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.design.internal.BottomNavigationItemView;
import android.support.design.internal.BottomNavigationMenuView;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import com.google.firebase.auth.FirebaseAuth;

import java.lang.reflect.Field;
import com.google.gson.Gson;

import br.com.m3rcurio.livroemprestator.model.Interacoes;
import br.com.m3rcurio.livroemprestator.model.Usuarios;

public class MainActivity extends AppCompatActivity {

    private int itemSelecionado;
    private Interacoes interacao;
    private Usuarios usuarioLogado;

    private String TAG = "MAIN";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        interacao=new Interacoes();
        interacao.setUsuarioLeitor(FirebaseAuth.getInstance().getCurrentUser().getUid());
        interacao.setStatus(0);


        Toolbar myToolbar = (Toolbar) findViewById(R.id.my_toolbar);
        setSupportActionBar(myToolbar);

        BottomNavigationView bottomNavigationView = (BottomNavigationView)
                findViewById(R.id.navigation);

        bottomNavigationView.setOnNavigationItemSelectedListener
                (new BottomNavigationView.OnNavigationItemSelectedListener() {
                    @Override
                    public boolean onNavigationItemSelected(MenuItem itemMenu) {
                        int item =1;
                        switch (itemMenu.getItemId()) {
                            case R.id.action_item1:
                                item = 1;
                                break;
                            case R.id.action_item2:
                                item = 2;
                                break;
                            case R.id.action_item3:
                                item = 3;
                                break;
                            case R.id.action_item4:
                                item = 4;
                                break;
                        }
                        selecionarItem(item);
                        return true;
                    }
                });

        Intent it = getIntent();
        it.putExtra("interacao", interacao);
        itemSelecionado = (int) it.getSerializableExtra("itemSelecionado");

        selecionarItem(itemSelecionado);
        disableShiftMode(bottomNavigationView);
    }

    private void disableShiftMode(BottomNavigationView view) {
        BottomNavigationMenuView menuView = (BottomNavigationMenuView) view.getChildAt(0);
        try {
            Field shiftingMode = menuView.getClass().getDeclaredField("mShiftingMode");
            shiftingMode.setAccessible(true);
            shiftingMode.setBoolean(menuView, false);
            shiftingMode.setAccessible(false);
            for (int i = 0; i < menuView.getChildCount(); i++) {
                BottomNavigationItemView item = (BottomNavigationItemView) menuView.getChildAt(i);
                item.setShiftingMode(false);
                // set once again checked value, so view will be updated
                item.setChecked(item.getItemData().isChecked());
            }
        } catch (NoSuchFieldException e) {
//            Log.e("BNVHelper", "Unable to get shift mode field", e);
        } catch (IllegalAccessException e) {
//            Log.e("BNVHelper", "Unable to change value of shift mode", e);
        }
    }

    private void selecionarItem(Integer item){
        Fragment selectedFragment = null;
        switch (item) {
            case 1:
                selectedFragment = ListaLivrosFragment.newInstance("rotaLivro", interacao);
                break;
            case 2:
                selectedFragment = ListaUsuariosFragment.newInstance("rotaUsuario", interacao);
                break;
            case 3:
                selectedFragment = ListaIteracoesFragment.newInstance();
                break;
            case 4:
                selectedFragment = ChatFragment.newInstance();
                break;
        }
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.frame_layout, selectedFragment);
        transaction.commit();
    }

    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.barra_titulo_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_item_configuracoes) {
            Intent intent = new Intent(getBaseContext(), PerfilUsuarioActivity.class);
            startActivity(intent);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
