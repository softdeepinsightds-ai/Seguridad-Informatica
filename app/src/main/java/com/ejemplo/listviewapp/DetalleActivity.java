package com.ejemplo.listviewapp;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;

public class DetalleActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detalle);

        TextView txtPantallaDetalle = findViewById(R.id.txtDetalle);

        // 1. Obtener el Intent que inició esta Actividad
        Intent intentRecibido = getIntent();

        // 2. Extraer el valor usando la misma clave ("PRODUCTO_NOMBRE")
        String nombreProducto = intentRecibido.getStringExtra("PRODUCTO_NOMBRE");

        // 3. Mostrarlo en la interfaz
        if (nombreProducto != null) {
            txtPantallaDetalle.setText(nombreProducto);
        }
    }
}
