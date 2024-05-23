package sv.edu.catolica.cheflim;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class PerfilFavoritos extends AppCompatActivity {

    private ApiService apiService;
    private TextView datouser, datoname;
    private TextView titleTex, authorTex, descriptionTex,ratingTex;
    private ImageView img;
    private LinearLayout li;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_perfil_favoritos);
        SharedPreferences sharedPreferences = getSharedPreferences("DatosLogin", MODE_PRIVATE);
        // Aqui comprobariamos si existe el usuario
        if (sharedPreferences.getInt("id_usuario", -1) == -1){
            finish();
        }

        datouser = (TextView) findViewById(R.id.ETuserpf);
        datoname = (TextView) findViewById(R.id.ETnamepf);
        datouser.setText(sharedPreferences.getString("usuario", ""));
        datoname.setText(sharedPreferences.getString("nombre", ""));

        li = findViewById(R.id.layoutmyfavs);
        apiService = ApiClient.getRetrofitInstance().create(ApiService.class);

        CargarDatos(sharedPreferences.getInt("id_usuario", -1));
    }
    private void CargarDatos(int id_usu){
        Call<List<Recetas>> call = apiService.getFavRecetas(id_usu);
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

            ImageButton botorecipe = (ImageButton) cardView.findViewById(R.id.see_recipe);

            String URL_IMG = "https://h2vr69d6-3000.use.devtunnels.ms/api/obtenerimg/"+ receta.getImg();

            Glide.with(getApplication()).load(URL_IMG).into(img);
            titleTex.setText(receta.getDescripcion());
            authorTex.setText("de " + receta.getUsuarios().getNombre());
            descriptionTex.setText(receta.getPorciones() + " Porciones  | " + receta.getTiempo() + " Minutos");
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
            Intent intentocosa = new Intent(PerfilFavoritos.this, Vistareceta.class);
            intentocosa.putExtra("id_receta", id_receta);
            intentocosa.putExtra("id_usuario", id_usuario);
            startActivity(intentocosa);
        } else {
            Toast.makeText(PerfilFavoritos.this, "No deberias estar aqui!", Toast.LENGTH_SHORT);
        }
    }

    public void IngresarListadelPerFav(View view) {
        Intent intento = new Intent(this, Listaingredientes.class);
        startActivity(intento);
        finish();
    }
    public void IngresarIniciodelPerFav(View view) {
        Intent intento = new Intent(this, Inicio.class);
        startActivity(intento);
        finish();
    }
    public void IngresarPerfildelPerFav(View view) {
        Intent intento = new Intent(this, PerfilDatos.class);
        startActivity(intento);
        finish();
    }
    public void IngresarDescubredelPerFav(View view) {
        Intent intento = new Intent(this, Descubre.class);
        startActivity(intento);
        finish();
    }

    public void IngresarPerRecetadelPerFav(View view) {
        Intent intento = new Intent(this, PerfilRecetas.class);
        startActivity(intento);
        finish();
    }
    public void IngresarPerFavdelPerFav(View view) {
        Intent intento = new Intent(this, PerfilFavoritos.class);
        startActivity(intento);
        finish();
    }
    public void IngresarPerDatosdelPerFav(View view) {
        Intent intento = new Intent(this, PerfilDatos.class);
        startActivity(intento);
        finish();
    }
}