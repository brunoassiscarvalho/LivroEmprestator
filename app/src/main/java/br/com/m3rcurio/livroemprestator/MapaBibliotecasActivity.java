package br.com.m3rcurio.livroemprestator;

import android.location.LocationManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;

public class MapaBibliotecasActivity extends AppCompatActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    private LocationManager locationManager;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mapa_bibliotecas);
    }
}
