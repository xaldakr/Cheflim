package sv.edu.catolica.cheflim;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class Vistareceta extends AppCompatActivity {

    private int id_usuario = -1, id_receta = -1;
    private TextView h1;
    private ImageView h;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_vistareceta);

        Intent intento = getIntent();
        id_receta = intento.getIntExtra("id_receta", -1);
        id_usuario = intento.getIntExtra("id_usuario", -1);

        if (id_receta == -1 || id_usuario == -1){
            finish();
        }

        ApiService apiService = ApiClient.getRetrofitInstance().create(ApiService.class);
        Call<Recetas> call = apiService.getRecetas(id_usuario, id_receta);
        call.enqueue(new Callback<Recetas>() {
            @Override
            public void onResponse(Call<Recetas> call, Response<Recetas> response) {
                if (response.isSuccessful()) {
                    updateUI(response.body());
                } else {
                    Toast.makeText(Vistareceta.this, "Error al obtener datos", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<Recetas> call, Throwable t) {
                Toast.makeText(Vistareceta.this, "Fallo en la conexión", Toast.LENGTH_SHORT).show();
            }
        });

    }

    private void updateUI(Recetas receta) {
        runOnUiThread(() -> {
            // Actualizar el título del platillo
            TextView title = findViewById(R.id.title);
            title.setText(receta.getDescripcion());

            // Actualizar la cantidad de porciones
            TextView portions = findViewById(R.id.portions);
            portions.setText(receta.getPorciones() + " porciones");

            // Actualizar el tiempo estimado para preparar el platillo
            TextView time = findViewById(R.id.time);
            time.setText(receta.getTiempo() + " minutos");

            // Mostrar el nombre del autor
            TextView authorDescription = findViewById(R.id.authorDescription);
            authorDescription.setText("de " + receta.getUsuarios().getNombre());

            // Cargar y mostrar la imagen del platillo
            ImageView img = findViewById(R.id.image);
            String URL_IMG = "https://h2vr69d6-3000.use.devtunnels.ms/api/obtenerimg/" + receta.getImg();
            Glide.with(getApplicationContext()).load(URL_IMG).into(img);

            // Llenar dinámicamente la lista de ingredientes
            LinearLayout ingredientsContainer = findViewById(R.id.ingredientsContainer);
            ingredientsContainer.removeAllViews();
            for (Ingrediente ingrediente : receta.getIngredientes()) {
                TextView ingredientView = new TextView(Vistareceta.this);
                ingredientView.setLayoutParams(new LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.WRAP_CONTENT,
                        LinearLayout.LayoutParams.WRAP_CONTENT));
                ingredientView.setText("- " + ingrediente.getIngrediente());
                ingredientView.setTextColor(Color.BLACK);
                ingredientView.setTextSize(16f);
                ingredientsContainer.addView(ingredientView);
            }
            LinearLayout pasosContainer = findViewById(R.id.pasosContainer);
            for (Pasos pasos : receta.getPasos()) {
                TextView PasosView = new TextView(Vistareceta.this);
                PasosView.setLayoutParams(new LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.WRAP_CONTENT,
                        LinearLayout.LayoutParams.WRAP_CONTENT));
                PasosView.setText(pasos.getOrden()+ "- "+ pasos.getPaso());
                PasosView.setTextColor(Color.BLACK);
                PasosView.setTextSize(16f);
                pasosContainer.addView(PasosView);
            }
        });
    }




    public void Regresar(View view) {
        finish();
    }
}