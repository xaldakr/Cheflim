package sv.edu.catolica.cheflim;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
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

public class Descubre extends AppCompatActivity {

    private ApiService apiService;
    private EditText datobusqueda;
    private TextView titleTex, authorTex, descriptionTex,ratingTex;
    private ImageView img;
    private LinearLayout li;
    private ImageButton img2;

    private String textbusqueda;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_descubre);

        SharedPreferences sharedPreferences = getSharedPreferences("DatosLogin", MODE_PRIVATE);
        // Aqui comprobariamos si existe el usuario
        if (sharedPreferences.getInt("id_usuario", -1) == -1){
            finish();
        }
        textbusqueda = "";
        datobusqueda = (EditText) findViewById(R.id.ETSearchRecipe);
        li = findViewById(R.id.discover_layout_container);

        apiService = ApiClient.getRetrofitInstance().create(ApiService.class);
        CargarDatos();


    }
    @Override
    protected void onResume() {
        super.onResume();
        CargarDatos();
    }
    private void CargarDatos(){
        Call<List<Recetas>> call = apiService.getRecetas(textbusqueda);
        call.enqueue(new Callback<List<Recetas>>() {
            @Override
            public void onResponse(Call<List<Recetas>> call, Response<List<Recetas>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    List<Recetas> recetas = response.body();
                    mostrarRecetas(recetas, apiService);
                } else {
                    Log.e("Inicio", "Error: " + getString(R.string.error_message));


                }
            }

            @Override
            public void onFailure(Call<List<Recetas>> call, Throwable t) {
                Toast.makeText(Descubre.this, "Error: " + getString(R.string.error_message), Toast.LENGTH_SHORT).show();

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

            String porcionesLabel = getString(R.string.porciones_label);
            String minutosLabel = getString(R.string.minutos_label);

            descriptionTex.setText(receta.getPorciones() + " " + porcionesLabel + " | " + receta.getTiempo() + " " + minutosLabel);

            ratingTex.setText(String.format("%.1f (%d Reseñas)", receta.getPromedioResenas(), receta.getCantidadResenas()));

            botorecipe.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    MostrarReceta(receta.getId_receta(), sharedPreferences.getInt("id_usuario", -1)); //Quitar el hardcoded
                }
            });
            img2 = findViewById(R.id.MenuLista);

            img2.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    IngresarListadelInicio( sharedPreferences.getInt("id_usuario", -1));
                }
            });

            li.addView(cardView);
        }
    }

    private void MostrarReceta(int id_receta, int id_usuario){
        if (id_usuario >= 1){
            Intent intentocosa = new Intent(Descubre.this, Vistareceta.class);
            intentocosa.putExtra("id_receta", id_receta);
            intentocosa.putExtra("id_usuario", id_usuario);
            startActivity(intentocosa);
        } else {
            Toast.makeText(Descubre.this, getString(R.string.no_deberias_estar_aqui), Toast.LENGTH_SHORT).show();
        }
    }
    public void IngresarListadelInicio(int id_usuario) {
        Intent intento = new Intent(this, Listaingredientes.class);

        intento.putExtra("id_usuario", id_usuario);
        startActivity(intento);
        finish();
    }
    public void IngresarIniciodelDescubre(View view) {
        Intent intento = new Intent(this, Inicio.class);
        startActivity(intento);
        finish();
    }
    public void IngresarPerfildelDescubre(View view) {
        Intent intento = new Intent(this, PerfilDatos.class);
        startActivity(intento);
        finish();
    }
    public void IngresarDescubredelDescubre(View view) {
        Intent intento = new Intent(this, Descubre.class);
        startActivity(intento);
        finish();
    }

    public void BuscarReceta(View view) {
        textbusqueda = datobusqueda.getText().toString().trim();
        CargarDatos();
    }
}