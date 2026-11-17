package com.ejemplo.listviewapp;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

public class MainActivity extends Activity {

    ListView listaProductos;
    String [] productos = {"Laptop", "Smartphone", "Tablet", "Monitor", "Teclado", "Ratón"};

    @Override
    public void onCreate (Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        listaProductos = findViewById(R.id.miListView);

        // 1. Crear el Adaptador (Conecta los Datos con la Interfaz)
        ArrayAdapter<String> adaptador = new ArrayAdapter<>(
                this,
                android.R.layout.simple_list_item_1,
                productos
        );
        listaProductos.setAdapter(adaptador);

        // 2. Manejar el clic usando un Listener
        listaProductos.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                // Obtener el nombre del producto tocado
                String productoSeleccionado = productos[position];

                // 3. Crear el Intent Explícito
                Intent intent = new Intent(MainActivity.this, DetalleActivity.class);

                // 4. Agregar equipaje (Extras) al Intent
                intent.putExtra("PRODUCTO_NOMBRE", productoSeleccionado);

                // 5. Iniciar el viaje a la otra pantalla
                startActivity(intent);
            }
        });
    }
}
