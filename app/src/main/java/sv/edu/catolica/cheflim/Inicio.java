package sv.edu.catolica.cheflim;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;

import java.util.List;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class Inicio extends AppCompatActivity {

    private LinearLayout li;
    private TextView titleTex, authorTex, descriptionTex,ratingTex;

    private TextView datouser;
    private ImageView img;

    SharedPreferences sharedPreferences = getSharedPreferences("DatosLogin", MODE_PRIVATE);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_inicio);

        // Aqui comprobariamos si existe el usuario
        if (sharedPreferences.getInt("id_usuario", -1) == -1){
            finish();
        }
        li = findViewById(R.id.linear_layout_container);

        datouser = (TextView) findViewById(R.id.InicioNameuser);

        datouser.setText(sharedPreferences.getString("clave_usuario", ""));
        ApiService apiService = ApiClient.getRetrofitInstance().create(ApiService.class);
        Call<List<Recetas>> call = apiService.getRecetas();
        call.enqueue(new Callback<List<Recetas>>() {
            @Override
            public void onResponse(Call<List<Recetas>> call, Response<List<Recetas>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    List<Recetas> recetas = response.body();
                    mostrarRecetas(recetas, apiService);
                } else {
                    // Manejar el caso en que la respuesta no es exitosa
                    Log.e("Inicio", "Respuesta fallida: " + response.errorBody());
                }
            }

            @Override
            public void onFailure(Call<List<Recetas>> call, Throwable t) {
                Log.e("Inicio", "Error: " + t.getMessage());
            }
        });

    }
    private void mostrarRecetas(List<Recetas> recetas, ApiService apiService) {
        LayoutInflater inflater = LayoutInflater.from(this);

        for (Recetas receta : recetas) {
            View cardView = inflater.inflate(R.layout.card_item, li, false);

            img = cardView.findViewById(R.id.image_view);
            titleTex = cardView.findViewById(R.id.title_text);
            authorTex = cardView.findViewById(R.id.author_text);
            descriptionTex = cardView.findViewById(R.id.description_text);
            ratingTex = cardView.findViewById(R.id.rating_text);

            ImageButton botorecipe = (ImageButton) findViewById(R.id.see_recipe);

            String URL_IMG = "https://h2vr69d6-3000.use.devtunnels.ms/api/obtenerimg/"+ receta.getImg();

            Glide.with(getApplication()).load(URL_IMG).into(img);
            titleTex.setText(receta.getDescripcion());
            authorTex.setText("de " + receta.getUsuarios().getNombre());
            descriptionTex.setText("Recetas fáciles para hacer en casa");
            ratingTex.setText(String.format("%.1f (%d Reseñas)", receta.getPromedioResenas(), receta.getCantidadResenas()));

            botorecipe.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    MostrarReceta(receta.getId_receta(), sharedPreferences.getInt("id_usuario", -1)); //Quitar el hardcoded
                }
            });

            li.addView(cardView);
        }
    }

    private void MostrarReceta(int id_receta, int id_usuario){
        if (id_usuario >= 1){
            Intent intentocosa = new Intent(Inicio.this, Vistareceta.class);
            startActivity(intentocosa);
        } else {
            Toast.makeText(Inicio.this, "No deberias estar aqui!", Toast.LENGTH_SHORT);
        }
    }

}