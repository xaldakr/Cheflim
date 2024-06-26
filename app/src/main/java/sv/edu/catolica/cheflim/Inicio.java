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
    private TextView titleTex, authorTex, descriptionTex, ratingTex;

    private TextView datouser;
    private ImageView img;

    private ImageButton img2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_inicio);

        li = findViewById(R.id.linear_layout_container);
        datouser = findViewById(R.id.InicioNameuser);
        img2 = findViewById(R.id.MenuLista);

        SharedPreferences sharedPreferences = getSharedPreferences("DatosLogin", MODE_PRIVATE);
        datouser.setText(sharedPreferences.getString("usuario", ""));

        
        if (sharedPreferences.getInt("id_usuario", -1) == -1){
            finish();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        actualizarRecetas();
    }

    private void actualizarRecetas() {
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
                    Toast.makeText(Inicio.this, getString(R.string.respuesta_fallida) + ": " + response.errorBody(), Toast.LENGTH_SHORT).show();

                }
            }

            @Override
            public void onFailure(Call<List<Recetas>> call, Throwable t) {
                Log.e("Inicio", "Error: " + t.getMessage());
            }
        });
    }

    private void mostrarRecetas(List<Recetas> recetas, ApiService apiService) {
        li.removeAllViews();
        LayoutInflater inflater = LayoutInflater.from(this);
        SharedPreferences sharedPreferences = getSharedPreferences("DatosLogin", MODE_PRIVATE);
        for (Recetas receta : recetas) {
            View cardView = inflater.inflate(R.layout.card_item, li, false);

            img = cardView.findViewById(R.id.image_view);
            titleTex = cardView.findViewById(R.id.title_text);
            authorTex = cardView.findViewById(R.id.author_text);
            descriptionTex = cardView.findViewById(R.id.description_text);
            ratingTex = cardView.findViewById(R.id.rating_text);

            ImageButton botorecipe = cardView.findViewById(R.id.see_recipe);

            String URL_IMG = "https://h2vr69d6-3000.use.devtunnels.ms/api/obtenerimg/" + receta.getImg();

            Glide.with(getApplication()).load(URL_IMG).into(img);
            titleTex.setText(receta.getDescripcion());
            authorTex.setText("de " + receta.getUsuarios().getNombre());
            descriptionTex.setText(receta.getPorciones() +" "+ getString(R.string.porciones)+" | " + receta.getTiempo() + " "+getString(R.string.mins));
            ratingTex.setText(String.format("%.1f (%d %s)", receta.getPromedioResenas(), receta.getCantidadResenas(), getString(R.string.res)));

            botorecipe.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    MostrarReceta(receta.getId_receta(), sharedPreferences.getInt("id_usuario", -1)); //Quitar el hardcoded
                }
            });
            img2.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    IngresarListadelInicio(receta.getId_receta(), sharedPreferences.getInt("id_usuario", -1));
                }
            });

            li.addView(cardView);
        }
    }

    private void MostrarReceta(int id_receta, int id_usuario) {
        if (id_usuario >= 1) {
            Intent intentocosa = new Intent(Inicio.this, Vistareceta.class);
            intentocosa.putExtra("id_receta", id_receta);
            intentocosa.putExtra("id_usuario", id_usuario);
            startActivity(intentocosa);
        } else {
            Toast.makeText(Inicio.this, getString(R.string.no_deberias_estar_aqui), Toast.LENGTH_SHORT).show();
        }
    }

    public void IngresarListadelInicio(int id_receta, int id_usuario) {
        Intent intento = new Intent(this, Listaingredientes.class);
        intento.putExtra("id_receta", id_receta);
        intento.putExtra("id_usuario", id_usuario);
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
    public void AgregarReceta(View view) {

            Intent intentocosa = new Intent(Inicio.this, CrudRecetas.class);
            startActivity(intentocosa);

    }
}