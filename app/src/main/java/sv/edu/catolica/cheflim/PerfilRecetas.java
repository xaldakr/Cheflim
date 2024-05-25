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
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;

import java.util.List;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class PerfilRecetas extends AppCompatActivity {
    private ApiService apiService;
    private TextView datouser, datoname;
    private TextView titleTex, descriptionTex,ratingTex;
    private ImageView img;
    private LinearLayout li;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_perfil_recetas);
        SharedPreferences sharedPreferences = getSharedPreferences("DatosLogin", MODE_PRIVATE);
        // Aqui comprobariamos si existe el usuario
        if (sharedPreferences.getInt("id_usuario", -1) == -1){
            finish();
        }

        datouser = (TextView) findViewById(R.id.ETuserpr);
        datoname = (TextView) findViewById(R.id.ETnamepr);
        datouser.setText(sharedPreferences.getString("usuario", ""));
        datoname.setText(sharedPreferences.getString("nombre", ""));

        li = findViewById(R.id.layoutmyrecipes);
        apiService = ApiClient.getRetrofitInstance().create(ApiService.class);

        CargarDatos(sharedPreferences.getInt("id_usuario", -1));
    }

    private void CargarDatos(int id_usu){
        Call<List<Recetas>> call = apiService.getUserRecetas(id_usu);
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
            View cardView = inflater.inflate(R.layout.carduser, li, false);

            img = cardView.findViewById(R.id.image_view);
            titleTex = cardView.findViewById(R.id.title_text);
            descriptionTex = cardView.findViewById(R.id.description_text);
            ratingTex = cardView.findViewById(R.id.rating_text);

            ImageButton botorecipe = (ImageButton) cardView.findViewById(R.id.see_recipe);
            ImageButton delerecipe = (ImageButton) cardView.findViewById(R.id.delete_recipe);
            ImageButton editrecipe = (ImageButton) cardView.findViewById(R.id.edit_recipe);

            String URL_IMG = "https://h2vr69d6-3000.use.devtunnels.ms/api/obtenerimg/"+ receta.getImg();

            Glide.with(getApplication()).load(URL_IMG).into(img);
            titleTex.setText(receta.getDescripcion());
            descriptionTex.setText(receta.getPorciones() + " Porciones  | " + receta.getTiempo() + " Minutos");
            ratingTex.setText(String.format("%.1f (%d Reseñas)", receta.getPromedioResenas(), receta.getCantidadResenas()));

            botorecipe.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    MostrarReceta(receta.getId_receta(), sharedPreferences.getInt("id_usuario", -1)); //Quitar el hardcoded
                }
            });

            editrecipe.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    EditarReceta(receta.getId_receta());
                }
            });

            delerecipe.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    DeleteReceta(receta.getId_receta(), receta.getDescripcion());
                }
            });
            li.addView(cardView);
        }
    }

    private void MostrarReceta(int id_receta, int id_usuario){
        if (id_usuario >= 1){
            Intent intentocosa = new Intent(PerfilRecetas.this, Vistareceta.class);
            intentocosa.putExtra("id_receta", id_receta);
            intentocosa.putExtra("id_usuario", id_usuario);
            startActivity(intentocosa);
        } else {
            Toast.makeText(PerfilRecetas.this, "No deberias estar aqui!", Toast.LENGTH_SHORT);
        }
    }

    private void EditarReceta(int id_receta){
        Intent intentocosa = new Intent(PerfilRecetas.this, CrudRecetas.class);
        intentocosa.putExtra("id_receta", id_receta);
        startActivity(intentocosa);
    }

    private void DeleteReceta(int idReceta, String nombrereceta){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Confirmar eliminación");
        builder.setCancelable(false);
        builder.setMessage("Desea eliminar la receta \"" + nombrereceta+"\"?");
        builder.setPositiveButton("Eliminar", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Call<Map<String,Object>> call = apiService.eliminarfile(idReceta);
                call.enqueue(new Callback<Map<String, Object>>() {
                    @Override
                    public void onResponse(Call<Map<String, Object>> call, Response<Map<String, Object>> response) {
                        //En realidad no hariamos nada pq la imagen es independiente
                    }

                    @Override
                    public void onFailure(Call<Map<String, Object>> call, Throwable t) {
                        //Same goes here
                    }
                });
                Call<Map<String, Object>> call1 = apiService.eliminarreceta(idReceta);
                call1.enqueue(new Callback<Map<String, Object>>() {
                    @Override
                    public void onResponse(Call<Map<String, Object>> call, Response<Map<String, Object>> response) {
                        if(response.isSuccessful()){
                            SharedPreferences sharedPreferences = getSharedPreferences("DatosLogin", MODE_PRIVATE);
                            CargarDatos(sharedPreferences.getInt("id_usuario", -1));
                            Toast.makeText(PerfilRecetas.this, "Receta eliminada correctamente", Toast.LENGTH_SHORT).show();

                        } else {
                            Toast.makeText(PerfilRecetas.this, "Error al eliminar la receta", Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onFailure(Call<Map<String, Object>> call, Throwable t) {
                        Toast.makeText(PerfilRecetas.this, "Error de conexion", Toast.LENGTH_SHORT).show();
                    }
                });
            }

        });
        builder.setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

            }
        });
        builder.create();
        builder.show();
    }
    public void IngresarListadelPerReceta(View view) {
        Intent intento = new Intent(this, Listaingredientes.class);
        startActivity(intento);
        finish();
    }
    public void IngresarIniciodelPerReceta(View view) {
        Intent intento = new Intent(this, Inicio.class);
        startActivity(intento);
        finish();
    }
    public void IngresarPerfildelPerReceta(View view) {
        Intent intento = new Intent(this, PerfilDatos.class);
        startActivity(intento);
        finish();
    }
    public void IngresarDescubredelPerReceta(View view) {
        Intent intento = new Intent(this, Descubre.class);
        startActivity(intento);
        finish();
    }


    public void IngresarPerRecetadelPerReceta(View view) {
        Intent intento = new Intent(this, PerfilRecetas.class);
        startActivity(intento);
        finish();
    }
    public void IngresarPerFavdelPerReceta(View view) {
        Intent intento = new Intent(this, PerfilFavoritos.class);
        startActivity(intento);
        finish();
    }
    public void IngresarPerDatosdelPerReceta(View view) {
        Intent intento = new Intent(this, PerfilDatos.class);
        startActivity(intento);
        finish();
    }
    public void AgregarReceta(View view) {

        Intent intentocosa = new Intent(this, CrudRecetas.class);
        startActivity(intentocosa);

    }
}