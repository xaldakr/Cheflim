package sv.edu.catolica.cheflim;

import static sv.edu.catolica.cheflim.R.drawable.bookmark_selected;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.gson.Gson;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class Vistareceta extends AppCompatActivity {

    private int id_usuario = -1, id_receta = -1;
    private TextView h1;
    private ImageButton im1, im2, im3, im4, im5;
    private Button list;


    FavoritoResponse fav = new FavoritoResponse();
    private ApiService apiService = ApiClient.getRetrofitInstance().create(ApiService.class);
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_vistareceta);
        im1 = findViewById(R.id.staruno);
        im2 = findViewById(R.id.stardos);
        im3 = findViewById(R.id.startres);
        im4 = findViewById(R.id.starcuatro);
        im5 = findViewById(R.id.starcinco);

        list = findViewById(R.id.addToListButton);

        Intent intento = getIntent();
        id_receta = intento.getIntExtra("id_receta", -1);
        id_usuario = intento.getIntExtra("id_usuario", -1);

        if (id_receta == -1 || id_usuario == -1){
            finish();
        }


        Call<Recetas> call = apiService.getRecetas(id_usuario, id_receta);
        Call<FavoritoResponse> call1 = apiService.checkFavorito(id_receta,id_usuario);
        call.enqueue(new Callback<Recetas>() {
            @Override
            public void onResponse(Call<Recetas> call, Response<Recetas> response) {
                if (response.isSuccessful()) {
                    updateUI(response.body());
                    defaulresena(response.body());
                    //primervalor(response.body());
                } else {
                    Toast.makeText(Vistareceta.this, "Error al obtener datos", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<Recetas> call, Throwable t) {
                Toast.makeText(Vistareceta.this, "Fallo en la conexión", Toast.LENGTH_SHORT).show();
            }
        });

        call1.enqueue(new Callback<FavoritoResponse>() {
            @Override
            public void onResponse(Call<FavoritoResponse> call, Response<FavoritoResponse> response) {
                if (response.isSuccessful()) {
                    FavoritoResponse favoritoResponse = response.body();
                    fav.setExiste(favoritoResponse.getExiste());
                    ImageButton bookmarkIcon = findViewById(R.id.bookmarkIcon);
                   if (favoritoResponse.getExiste()==1){
                       bookmarkIcon.setImageResource(R.drawable.bookmark_selected);
                   }else { bookmarkIcon.setImageResource(R.drawable.bookmark);}
                } else {
                    Log.d("API Error", "Error Code: " + response.code());
                    try {
                        Log.d("API Error Body", response.errorBody().string());
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    Toast.makeText(Vistareceta.this, "Error al obtener datos: " + response.code(), Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onFailure(Call<FavoritoResponse> call, Throwable t) {
                Toast.makeText(Vistareceta.this, "Fallo en la conexión", Toast.LENGTH_LONG).show();
            }
        });

    }


    private void updateUI(Recetas receta) {
        runOnUiThread(() -> {

            TextView title = findViewById(R.id.title);
            title.setText(receta.getDescripcion());


            TextView portions = findViewById(R.id.portions);
            portions.setText(receta.getPorciones() + " porciones");


            TextView time = findViewById(R.id.time);
            time.setText(receta.getTiempo() + " minutos");


            TextView authorDescription = findViewById(R.id.authorDescription);
            authorDescription.setText("de " + receta.getUsuarios().getNombre());

            list.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    addList(id_usuario, id_receta);
                }
            });


            ImageView img = findViewById(R.id.image);
            String URL_IMG = "https://h2vr69d6-3000.use.devtunnels.ms/api/obtenerimg/" + receta.getImg();
            Glide.with(getApplicationContext()).load(URL_IMG).into(img);


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

    public void addList(int id_usuario, int id_receta){
        Map<String, Object> request = new HashMap<>();
        request.put("id_usuario", id_usuario);
        request.put("id_receta", id_receta);
        Call<Map<String, Object>> call = apiService.anadirlista(request);

        call.enqueue(new Callback<Map<String, Object>>() {
            @Override
            public void onResponse(Call<Map<String, Object>> call, Response<Map<String, Object>> response) {
                if(response.isSuccessful()){
                    Toast.makeText(Vistareceta.this, "Lista añadida con exito", Toast.LENGTH_SHORT).show();
                }else {
                    Toast.makeText(Vistareceta.this, "Error al comunicarse con el servidor", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<Map<String, Object>> call, Throwable t) {
                Toast.makeText(Vistareceta.this, "Error dentro del guardado", Toast.LENGTH_SHORT).show();
            }
        });
    }

    public void Regresar(View view) {
        finish();
    }

    public void estadoFavorito(View view){

        ImageButton bookmarkIcon = findViewById(R.id.bookmarkIcon);

        Map<String, Object> request = new HashMap<>();
        request.put("idUsuario", id_usuario);
        request.put("idReceta", id_receta);
        Call<Map<String, Object>> call = apiService.createFav(request);

        call.enqueue(new Callback<Map<String, Object>>() {
            @Override
            public void onResponse(Call<Map<String, Object>> call, Response<Map<String, Object>> response) {
                if (response.isSuccessful()){
                    if (fav.getExiste()==1){
                        bookmarkIcon.setImageResource(R.drawable.bookmark);
                    }else { bookmarkIcon.setImageResource(R.drawable.bookmark_selected);}
                }else{
                    Toast.makeText(Vistareceta.this, "Error al comunicarse con el servidor", Toast.LENGTH_SHORT).show();
                }

            }

            @Override
            public void onFailure(Call<Map<String, Object>> call, Throwable t) {
                Toast.makeText(Vistareceta.this, "Error al comunicarse a la api", Toast.LENGTH_SHORT).show();
            }
        });
    }

    public void defaulresena(Recetas recetas){
        if (recetas.getUserResena()==1){
            im1.setImageResource(R.drawable.star_selected);
        } else if (recetas.getUserResena()==2) {
            im1.setImageResource(R.drawable.star_selected);
            im2.setImageResource(R.drawable.star_selected);
        } else if (recetas.getUserResena()==3) {
            im1.setImageResource(R.drawable.star_selected);
            im2.setImageResource(R.drawable.star_selected);
            im3.setImageResource(R.drawable.star_selected);

        } else if (recetas.getUserResena()==4) {
            im1.setImageResource(R.drawable.star_selected);
            im2.setImageResource(R.drawable.star_selected);
            im3.setImageResource(R.drawable.star_selected);
            im4.setImageResource(R.drawable.star_selected);
        }else if (recetas.getUserResena()==5) {
            im1.setImageResource(R.drawable.star_selected);
            im2.setImageResource(R.drawable.star_selected);
            im3.setImageResource(R.drawable.star_selected);
            im4.setImageResource(R.drawable.star_selected);
            im5.setImageResource(R.drawable.star_selected);
        }
    }

    public void newresena(int valor){
        if (valor ==1){
            im1.setImageResource(R.drawable.star_selected);
            im2.setImageResource(R.drawable.star);
            im3.setImageResource(R.drawable.star);
            im4.setImageResource(R.drawable.star);
            im5.setImageResource(R.drawable.star);
        } else if (valor==2) {
            im1.setImageResource(R.drawable.star_selected);
            im2.setImageResource(R.drawable.star_selected);
            im3.setImageResource(R.drawable.star);
            im4.setImageResource(R.drawable.star);
            im5.setImageResource(R.drawable.star);
        } else if (valor==3) {
            im1.setImageResource(R.drawable.star_selected);
            im2.setImageResource(R.drawable.star_selected);
            im3.setImageResource(R.drawable.star_selected);
            im4.setImageResource(R.drawable.star);
            im5.setImageResource(R.drawable.star);

        } else if (valor==4) {
            im1.setImageResource(R.drawable.star_selected);
            im2.setImageResource(R.drawable.star_selected);
            im3.setImageResource(R.drawable.star_selected);
            im4.setImageResource(R.drawable.star_selected);
            im5.setImageResource(R.drawable.star);

        }else if (valor==5) {
            im1.setImageResource(R.drawable.star_selected);
            im2.setImageResource(R.drawable.star_selected);
            im3.setImageResource(R.drawable.star_selected);
            im4.setImageResource(R.drawable.star_selected);
            im5.setImageResource(R.drawable.star_selected);
        }
    }

    public void primervalor(View view ){
        ImageButton starone = findViewById(R.id.staruno);
        Map<String, Object> request = new HashMap<>();
        request.put("id_usuario", id_usuario);
        request.put("id_receta", id_receta);
        request.put("valor", 1);
        Call<Map<String, Object>> call = apiService.createresena(request);
        call.enqueue(new Callback<Map<String, Object>>() {
            @Override
            public void onResponse(Call<Map<String, Object>> call, Response<Map<String, Object>> response) {
                if (response.isSuccessful()){
                    newresena(1);
                }else{
                    Toast.makeText(Vistareceta.this, "Error al comunicarse con el servidor", Toast.LENGTH_SHORT).show();
                }
            }
            @Override
            public void onFailure(Call<Map<String, Object>> call, Throwable t) {

            }
        });

    }
    public void segundovalor(View view){
        ImageButton starone = findViewById(R.id.staruno);
        Map<String, Object> request = new HashMap<>();
        request.put("id_usuario", id_usuario);
        request.put("id_receta", id_receta);
        request.put("valor", 2);
        Call<Map<String, Object>> call = apiService.createresena(request);
        call.enqueue(new Callback<Map<String, Object>>() {
            @Override
            public void onResponse(Call<Map<String, Object>> call, Response<Map<String, Object>> response) {
                if (response.isSuccessful()){
                    newresena(2);
                }else{
                    Toast.makeText(Vistareceta.this, "Error al comunicarse con el servidor", Toast.LENGTH_SHORT).show();
                }
            }
            @Override
            public void onFailure(Call<Map<String, Object>> call, Throwable t) {

            }
        });

    }
    public void tercervalor(View view ){
        ImageButton starone = findViewById(R.id.staruno);
        Map<String, Object> request = new HashMap<>();
        request.put("id_usuario", id_usuario);
        request.put("id_receta", id_receta);
        request.put("valor", 3);
        Call<Map<String, Object>> call = apiService.createresena(request);
        call.enqueue(new Callback<Map<String, Object>>() {
            @Override
            public void onResponse(Call<Map<String, Object>> call, Response<Map<String, Object>> response) {
                if (response.isSuccessful()){
                    newresena(3);
                }else{
                    Toast.makeText(Vistareceta.this, "Error al comunicarse con el servidor", Toast.LENGTH_SHORT).show();
                }
            }
            @Override
            public void onFailure(Call<Map<String, Object>> call, Throwable t) {

            }
        });

    }
    public void cuartovalor(View view){
        ImageButton starone = findViewById(R.id.staruno);
        Map<String, Object> request = new HashMap<>();
        request.put("id_usuario", id_usuario);
        request.put("id_receta", id_receta);
        request.put("valor", 4);
        Call<Map<String, Object>> call = apiService.createresena(request);
        call.enqueue(new Callback<Map<String, Object>>() {
            @Override
            public void onResponse(Call<Map<String, Object>> call, Response<Map<String, Object>> response) {
                if (response.isSuccessful()){
                    newresena(4);
                }else{
                    Toast.makeText(Vistareceta.this, "Error al comunicarse con el servidor", Toast.LENGTH_SHORT).show();
                }
            }
            @Override
            public void onFailure(Call<Map<String, Object>> call, Throwable t) {

            }
        });

    }
    public void quitovalor(View view){
        ImageButton starone = findViewById(R.id.staruno);
        Map<String, Object> request = new HashMap<>();
        request.put("id_usuario", id_usuario);
        request.put("id_receta", id_receta);
        request.put("valor", 5);
        Call<Map<String, Object>> call = apiService.createresena(request);
        call.enqueue(new Callback<Map<String, Object>>() {
            @Override
            public void onResponse(Call<Map<String, Object>> call, Response<Map<String, Object>> response) {
                if (response.isSuccessful()){
                    newresena(5);
                }else{
                    Toast.makeText(Vistareceta.this, "Error al comunicarse con el servidor", Toast.LENGTH_SHORT).show();
                }
            }
            @Override
            public void onFailure(Call<Map<String, Object>> call, Throwable t) {

            }
        });

    }

}