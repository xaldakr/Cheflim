package sv.edu.catolica.cheflim;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.util.List;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class Listaingredientes extends AppCompatActivity {
    private int id_usuario = -1, id_receta = -1;
    private TextView item;
    private Button eliminar;

    private LinearLayout in;
    private ApiService apiService = ApiClient.getRetrofitInstance().create(ApiService.class);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_listaingredientes);
        in = findViewById(R.id.contenedor_lista);
        SharedPreferences sharedPreferences = getSharedPreferences("DatosLogin", MODE_PRIVATE);
        Intent intento = getIntent();
        id_usuario = sharedPreferences.getInt("id_usuario", -1);

        if (id_usuario == -1){
            finish();
        }
        obtenerLista();

    }

    private void obtenerLista() {
        Call<List<Lista>> call = apiService.checkList(id_usuario);

        call.enqueue(new Callback<List<Lista>>() {
            @Override
            public void onResponse(Call<List<Lista>> call, Response<List<Lista>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    List<Lista> lista = response.body();
                    mostrarLista(lista);

                } else {
                    // Manejar el caso en que la respuesta no es exitosa
                    Log.e("Inicio", "Respuesta fallida: " + response.errorBody());
                }
            }

            @Override
            public void onFailure(Call<List<Lista>> call, Throwable t) {
                Log.e("Error", "Error en la llamada a la API: " + t.getMessage());
            }
        });
    }

    public void mostrarLista(List<Lista> lista){
        in.removeAllViews();
            LayoutInflater inflater = LayoutInflater.from(this);
            for (Lista listas : lista) {
                View cardView = inflater.inflate(R.layout.item_lista, in, false);
                item = cardView.findViewById(R.id.texto_item);
                eliminar = cardView.findViewById(R.id.boton_eliminar);

                item.setText(listas.getItem());

                in.addView(cardView);
                eliminar.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        eliminarlista(listas.getId_lista());
                    }
                });
            }
    }

    public void eliminarlista(int id_lista){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Confirmar eliminación");
        builder.setCancelable(false);
        builder.setMessage("Desea eliminar este Item de lista?");

        builder.setPositiveButton("Eliminar", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Call<Map<String,Object>> call = apiService.eliminarlista(id_lista);
                call.enqueue(new Callback<Map<String, Object>>() {
                    @Override
                    public void onResponse(Call<Map<String, Object>> call, Response<Map<String, Object>> response) {
                        Toast.makeText(Listaingredientes.this, "Item eliminado correctamente", Toast.LENGTH_LONG).show();
                        obtenerLista();
                    }

                    @Override
                    public void onFailure(Call<Map<String, Object>> call, Throwable t) {
                        Toast.makeText(Listaingredientes.this, "Error en la llamada a la API: " + t.getMessage(), Toast.LENGTH_LONG).show();
                    }
                });
            }
        });
        builder.setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                // No hacer nada, solo cerrar el diálogo
            }
        });
        builder.create();
        builder.show();
    }

    public void IngresarListadelInicio(View view) {
        Intent intento = new Intent(this, Listaingredientes.class);
        startActivity(intento);
        finish();
    }
    public void IngresarIniciodelInicio(View view) {
        Intent intento = new Intent(this, Inicio.class);
        startActivity(intento);
        finish();
    }
    public void IngresarPerfildelInicio(View view) {
        Intent intento = new Intent(this, PerfilDatos.class);
        startActivity(intento);
        finish();
    }
    public void IngresarDescubredelInicio(View view) {
        Intent intento = new Intent(this, Descubre.class);
        startActivity(intento);
        finish();
    }
}