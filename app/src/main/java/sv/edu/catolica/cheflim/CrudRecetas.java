package sv.edu.catolica.cheflim;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;

import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class CrudRecetas extends AppCompatActivity {
    private static final  int PICK_IMAGE_REQUEST = 1;
    private Uri imageUri;
    private ApiService apiService;
    private LinearLayout LayIngPrin, LayPasPrin, LayDatPrin;
    private LinearLayout LiIng, LiPas;
    private EditText NombreRec, Porciones, TiempoDato;
    private ImageView ImagenSubida;
    private ImageButton ImgIngre, ImgPasos, ImgDatos;
    private Recetas Recetaprin;
    private ArrayList<Ingrediente> ListaIngredientes;
    private ArrayList<Pasos> ListaPasos;
    private int idusus, id_receta;
    private ImageButton btnup, btndown, btnedit, btndelete;
    private TextView numer, datocar;
    private LinearLayout line;
    private static final int REQUEST_CODE = 100;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_crud_recetas);

        SharedPreferences sharedPreferences = getSharedPreferences("DatosLogin", MODE_PRIVATE);
        if (sharedPreferences.getInt("id_usuario", -1) == -1){
            finish();
        }

        imageUri = null;
        apiService = ApiClient.getRetrofitInstance().create(ApiService.class);

        LayIngPrin = findViewById(R.id.ingredients_layout);
        LayPasPrin = findViewById(R.id.procedures_layout);
        LayDatPrin = findViewById(R.id.informa_layout);

        LiIng = findViewById(R.id.LayoutCartaIngres);
        LiPas = findViewById(R.id.LayoutCartaProces);

        NombreRec = (EditText) findViewById(R.id.NombreReceta);
        Porciones = (EditText) findViewById(R.id.infoporcione);
        TiempoDato = (EditText) findViewById(R.id.infotiempo);

        ImgIngre = (ImageButton) findViewById(R.id.MisIngredientes);
        ImgPasos = (ImageButton) findViewById(R.id.Misprocedimientos);
        ImgDatos = (ImageButton) findViewById(R.id.MisDatos);

        ImagenSubida = (ImageView) findViewById(R.id.ImagenSubida);

        Intent intento = getIntent();
        id_receta = intento.getIntExtra("id_receta", -1);
        idusus = sharedPreferences.getInt("id_usuario", -1);

        if (id_receta == -1){
            Recetaprin = new Recetas();
            ListaIngredientes = new ArrayList<Ingrediente>();
            ListaPasos = new ArrayList<Pasos>();
            Recetaprin.setIngredientes(ListaIngredientes);
            Recetaprin.setPasos(ListaPasos);
        }
        else {
            Call<Recetas> call = apiService.getRecetas(idusus, id_receta);
            call.enqueue(new Callback<Recetas>() {
                @Override
                public void onResponse(Call<Recetas> call, Response<Recetas> response) {
                    if (response.isSuccessful()) {
                        Recetaprin = response.body();
                        ListaIngredientes = new ArrayList<>(Recetaprin.getIngredientes());
                        ListaPasos = new ArrayList<>(Recetaprin.getPasos());
                        RecargarDatos();
                        CargarInfo();
                    } else {
                        Toast.makeText(CrudRecetas.this, "Error al obtener datos", Toast.LENGTH_SHORT).show();
                        finish();
                    }
                }

                @Override
                public void onFailure(Call<Recetas> call, Throwable t) {
                    Toast.makeText(CrudRecetas.this, "Fallo en la conexión", Toast.LENGTH_SHORT).show();
                    finish();
                }
            });
        }

    }
    private void CargarInfo(){
        NombreRec.setText(Recetaprin.getDescripcion());
        Porciones.setText(Integer.toString(Recetaprin.getPorciones()));
        TiempoDato.setText(Integer.toString(Recetaprin.getTiempo()));
        //Cargar la imagen
        String URL_IMG = "https://h2vr69d6-3000.use.devtunnels.ms/api/obtenerimg/" + Recetaprin.getImg();
        Glide.with(getApplicationContext()).load(URL_IMG).into(ImagenSubida);
    }
    private void RecargarDatos(){
        LayoutInflater inflater = LayoutInflater.from(this);
        SharedPreferences sharedPreferences = getSharedPreferences("DatosLogin", MODE_PRIVATE);
        //Primero ingredientes
        LiIng.removeAllViews();
        for (int i = 0; i < ListaIngredientes.size(); i++){
            int datotactual = i;
            Ingrediente ingrediente = ListaIngredientes.get(i);
            View cardView = inflater.inflate(R.layout.lineingridient, LiIng, false);
            line = cardView.findViewById(R.id.LayoutCentraing);
            if (i%2 == 0){
                line.setBackgroundColor(ContextCompat.getColor(this, R.color.orange));
            }
            datocar = cardView.findViewById(R.id.IngDatoName);
            datocar.setText(ingrediente.getIngrediente());
            btnedit = cardView.findViewById(R.id.EditIng);
            btndelete = cardView.findViewById(R.id.DeleteIng);
            btnedit.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    EditIng(datotactual);
                }
            });
            btndelete.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    DelIng(datotactual);
                }
            });
            LiIng.addView(cardView);
        }
        //Ahora Pasos
        ArrayList<Pasos> nuevalistapasos = new ArrayList<>();
        LiPas.removeAllViews();
        for (int i = 0; i < ListaPasos.size(); i++){
            int datotactual = i;
            Pasos pasos = ListaPasos.get(i);
            pasos.setOrden(i+1);
            nuevalistapasos.add(pasos);
            View cardView = inflater.inflate(R.layout.lineprocedure, LiPas, false);
            line = cardView.findViewById(R.id.LayoutCentrapas);
            if (i%2 == 0){
                line.setBackgroundColor(ContextCompat.getColor(this, R.color.orange));
            }
            datocar = cardView.findViewById(R.id.PasDatoName);
            numer = cardView.findViewById(R.id.Pasnum);
            datocar.setText(pasos.getPaso());
            numer.setText(Integer.toString(i+1));
            btnedit = cardView.findViewById(R.id.EditPas);
            btndelete = cardView.findViewById(R.id.DeletePas);
            btnup = cardView.findViewById(R.id.UpPas);
            btndown = cardView.findViewById(R.id.DownPas);
            btnedit.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    EditPas(datotactual);
                }
            });
            btndelete.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    DelPas(datotactual);
                }
            });
            btnup.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    UPPAS(datotactual);
                }
            });
            btndown.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    DOWNPAS(datotactual);
                }
            });

            LiPas.addView(cardView);
        }
        ListaPasos = nuevalistapasos;
    }
    public void AddIng(View view){
        LayoutInflater inflador = LayoutInflater.from(this);
        View dialogview = inflador.inflate(R.layout.dialogseteditdata, null);
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Agrega un ingrediente");
        builder.setView(dialogview);
        builder.setPositiveButton("Agregar", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                EditText edit = dialogview.findViewById(R.id.edit_text);
                String input = edit.getText().toString().trim();
                if (input.equals("")){
                    Toast.makeText(CrudRecetas.this, "No se puede dejar el campo vacio!!!", Toast.LENGTH_SHORT).show();
                } else {
                    Ingrediente ingrediente = new Ingrediente();
                    ingrediente.setId_receta(-1);
                    ingrediente.setId_ingrediente(-1);
                    ingrediente.setIngrediente(input);
                    ListaIngredientes.add(ingrediente);
                    RecargarDatos();
                }
            }
        });
        builder.setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });
        builder.create().show();
    }
    public void AddPas(View view){
        LayoutInflater inflador = LayoutInflater.from(this);
        View dialogview = inflador.inflate(R.layout.dialogseteditdata, null);
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Agrega un Paso");
        builder.setView(dialogview);
        builder.setPositiveButton("Agregar", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                EditText edit = dialogview.findViewById(R.id.edit_text);
                String input = edit.getText().toString().trim();
                if (input.equals("")){
                    Toast.makeText(CrudRecetas.this, "No se puede dejar el campo vacio!!!", Toast.LENGTH_SHORT).show();
                } else {
                    Pasos pasos = new Pasos();
                    pasos.setId_receta(-1);
                    pasos.setId_paso(-1);
                    pasos.setOrden(-1);
                    pasos.setPaso(input);
                    ListaPasos.add(pasos);
                    RecargarDatos();
                }
            }
        });
        builder.setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });
        builder.create().show();
    }
    private void EditIng(int i){
        LayoutInflater inflador = LayoutInflater.from(this);
        View dialogview = inflador.inflate(R.layout.dialogseteditdata, null);
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Edita el ingrediente");
        builder.setView(dialogview);
        builder.setPositiveButton("Editar", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                EditText edit = dialogview.findViewById(R.id.edit_text);
                String input = edit.getText().toString().trim();
                if (input.equals("")){
                    Toast.makeText(CrudRecetas.this, "No se puede dejar el campo vacio!!!", Toast.LENGTH_SHORT).show();
                } else {
                    ListaIngredientes.get(i).setIngrediente(input);
                    RecargarDatos();
                }
            }
        });
        builder.setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });
        builder.create().show();
    }
    private void DelIng(int i){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Borrar el ingrediente");
        builder.setPositiveButton("Borrar", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                ListaIngredientes.remove(i);
                RecargarDatos();
            }
        });
        builder.setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });
        builder.create().show();
    }
    private void EditPas(int i){
        LayoutInflater inflador = LayoutInflater.from(this);
        View dialogview = inflador.inflate(R.layout.dialogseteditdata, null);
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Edita el paso");
        builder.setView(dialogview);
        builder.setPositiveButton("Editar", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                EditText edit = dialogview.findViewById(R.id.edit_text);
                String input = edit.getText().toString().trim();
                if (input.equals("")){
                    Toast.makeText(CrudRecetas.this, "No se puede dejar el campo vacio!!!", Toast.LENGTH_SHORT).show();
                } else {
                    ListaPasos.get(i).setPaso(input);
                    RecargarDatos();
                }
            }
        });
        builder.setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });
        builder.create().show();
    }
    private void DelPas(int i){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Borrar el ingrediente");
        builder.setPositiveButton("Borrar", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                ListaPasos.remove(i);
                RecargarDatos();
            }
        });
        builder.setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });
        builder.create().show();
    }
    private void UPPAS(int i){
        if ( i <= 0){
            Toast.makeText(this, "No se puede subir más", Toast.LENGTH_SHORT).show();
        }
        else {
            Pasos pasoant = ListaPasos.get(i-1);
            ListaPasos.set(i-1, ListaPasos.get(i));
            ListaPasos.set(i, pasoant);
            RecargarDatos();
        }
    }
    private void DOWNPAS(int i){
        if ( i >= ListaPasos.size()-1){
            Toast.makeText(this, "No se puede bajar más", Toast.LENGTH_SHORT).show();
        }
        else {
            Pasos pasoant = ListaPasos.get(i+1);
            ListaPasos.set(i+1, ListaPasos.get(i));
            ListaPasos.set(i, pasoant);
            RecargarDatos();
        }
    }
    private void ChangeViews(int view){ // 1. Ingredientes, 2. Pasos, 3. Datos
        ImgIngre.setBackgroundTintList(ContextCompat.getColorStateList(this, R.color.none));
        ImgPasos.setBackgroundTintList(ContextCompat.getColorStateList(this, R.color.none));
        ImgDatos.setBackgroundTintList(ContextCompat.getColorStateList(this, R.color.none));
        if (view != 1)
            LayIngPrin.setVisibility(View.GONE);
        if (view != 2)
            LayPasPrin.setVisibility(View.GONE);
        if (view != 3)
            LayDatPrin.setVisibility(View.GONE);
        if (view == 1)
        {
            LayIngPrin.setVisibility(View.VISIBLE);
            ImgIngre.setBackgroundTintList(ContextCompat.getColorStateList(this, R.color.red));
        }
        if (view == 2){
            LayPasPrin.setVisibility(View.VISIBLE);
            ImgPasos.setBackgroundTintList(ContextCompat.getColorStateList(this, R.color.red));
        }
        if (view == 3) {
            LayDatPrin.setVisibility(View.VISIBLE);
            ImgDatos.setBackgroundTintList(ContextCompat.getColorStateList(this, R.color.red));
        }
    }


    public void Salirdeaqui(View view) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Salir del editor de recetas");
        builder.setCancelable(false);
        builder.setMessage("Se perderán los datos, desea salir?");
        builder.setPositiveButton("Salir", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                finish();
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

    public void CambiarIngre(View view) {
        ChangeViews(1);
    }

    public void CambiarProce(View view) {
        ChangeViews(2);
    }

    public void CambiarDato(View view) {
        ChangeViews(3);
    }

    public void Upimage(View view) {
        //This one is gonna hurt
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)!= PackageManager.PERMISSION_GRANTED
        && ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE)!= PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions(CrudRecetas.this,
                    new String[]{
                            Manifest.permission.WRITE_EXTERNAL_STORAGE,
                            Manifest.permission.READ_EXTERNAL_STORAGE
                    },
                    REQUEST_CODE);
        }
        Intent intento = new Intent();
        intento.setType("image/*");
        intento.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(intento, PICK_IMAGE_REQUEST);

    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null){
            imageUri = data.getData();
            ImagenSubida.setImageURI(imageUri);
        }
    }
    private String getPathFromUri(Uri uri) {
        String[] projection = { MediaStore.Images.Media.DATA };
        Cursor cursor = getContentResolver().query(uri, projection, null, null, null);
        if (cursor != null) {
            int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
            cursor.moveToFirst();
            String path = cursor.getString(column_index);
            cursor.close();
            return path;
        }
        return null;
    }

    public void GuarEdit(View view) {
        //Seteamos
        String txtdescri = NombreRec.getText().toString().trim();
        int porciones = -1;
        int tiempo = -1;
        int errores = 0;
        try {
            porciones = Integer.parseInt(Porciones.getText().toString());
            tiempo = Integer.parseInt(TiempoDato.getText().toString());
        }catch(Exception e) {
            Toast.makeText(this, "Ingrese valores permitidos en los campos de Porciones y Tiempo", Toast.LENGTH_SHORT).show();
            errores++;
        }
        //Lluvia de validaciones, yei
        if(porciones <= 0 || tiempo <= 0){
            if (errores ==0){
                Toast.makeText(this, "Ingrese valores válidos", Toast.LENGTH_SHORT).show();
                errores++;
            }
        }
        if(txtdescri.equals("")){
            Toast.makeText(this, "No puedes dejar en blanco el nombre de la receta", Toast.LENGTH_SHORT).show();
            errores++;
        }
        if (ListaIngredientes.size()<=0){
            Toast.makeText(this, "Debe existir al menos un ingrediente", Toast.LENGTH_SHORT).show();
            errores++;
        }
        if (ListaPasos.size()<=0){
            Toast.makeText(this, "Debe existir al menos un paso", Toast.LENGTH_SHORT).show();
            errores++;
        }

        //Validaciones finales
        if (errores == 0){
            if(id_receta == -1){
                if(imageUri != null){
                    Guardar();
                } else {
                    Toast.makeText(this, "Suba una imagen para su receta!", Toast.LENGTH_SHORT).show();
                }
            } else {
                Editar(id_receta);
            }
        }


    }
    private void Guardar(){
        Recetaprin.setPasos(ListaPasos);
        Recetaprin.setIngredientes(ListaIngredientes);
        Recetaprin.setId_receta(-1);
        Recetaprin.setId_usuario(idusus);
        Recetaprin.setImg("");
        Recetaprin.setVideo("");
        Recetaprin.setDescripcion(NombreRec.getText().toString().trim());
        Recetaprin.setPorciones(Integer.parseInt(Porciones.getText().toString()));
        Recetaprin.setTiempo(Integer.parseInt(TiempoDato.getText().toString()));
        Call<Map<String,Object>> call = apiService.crearReceta(Recetaprin);
        call.enqueue(new Callback<Map<String, Object>>() {
            @Override
            public void onResponse(Call<Map<String, Object>> call, Response<Map<String, Object>> response) {
                Map<String, Object> responseBody = response.body();
                if(response.isSuccessful()){
                    int idrec = (int) ((double) responseBody.get("id_receta"));
                    Guardarimg(idrec);
                }else {
                    Toast.makeText(CrudRecetas.this, "Error al subir la imagen", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<Map<String, Object>> call, Throwable t) {
                Toast.makeText(CrudRecetas.this, "Fallo de internet", Toast.LENGTH_SHORT).show();
            }
        });
    }
    private void Guardarimg(int id){
        String filePath = getPathFromUri(imageUri);
        File file = new File(filePath);

        RequestBody requestFile = RequestBody.create(MediaType.parse(getContentResolver().getType(imageUri)), file);
        MultipartBody.Part body = MultipartBody.Part.createFormData("imagen", file.getName(), requestFile);

        Call<ResponseBody> call = apiService.subirImagen(id, body);
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                if (response.isSuccessful()) {
                    Toast.makeText(CrudRecetas.this, "Imagen subida exitosamente", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(CrudRecetas.this, "Error al subir la imagen", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                Toast.makeText(CrudRecetas.this, "Fallo al subir la imagen", Toast.LENGTH_SHORT).show();
            }
        });
        finish();
    }
    private void Editarimg(int id){
        String filePath = getPathFromUri(imageUri);
        File file = new File(filePath);

        RequestBody requestFile = RequestBody.create(MediaType.parse(getContentResolver().getType(imageUri)), file);
        MultipartBody.Part body = MultipartBody.Part.createFormData("imagen", file.getName(), requestFile);

        Call<ResponseBody> call = apiService.resubirImagen(id, body);
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                if (response.isSuccessful()) {
                    Toast.makeText(CrudRecetas.this, "Imagen subida exitosamente", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(CrudRecetas.this, "Error al subir la imagen", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                Toast.makeText(CrudRecetas.this, "Fallo al subir la imagen", Toast.LENGTH_SHORT).show();
            }
        });
        finish();
    }
    private void Editar(int id){
        Recetaprin.setPasos(ListaPasos);
        Recetaprin.setIngredientes(ListaIngredientes);
        Recetaprin.setId_receta(-1);
        Recetaprin.setId_usuario(idusus);
        Recetaprin.setVideo("");
        Recetaprin.setDescripcion(NombreRec.getText().toString().trim());
        Recetaprin.setPorciones(Integer.parseInt(Porciones.getText().toString()));
        Recetaprin.setTiempo(Integer.parseInt(TiempoDato.getText().toString()));
        Call<Map<String,Object>> call = apiService.updateReceta(id,Recetaprin);
        call.enqueue(new Callback<Map<String, Object>>() {
            @Override
            public void onResponse(Call<Map<String, Object>> call, Response<Map<String, Object>> response) {
                Map<String, Object> responseBody = response.body();
                if(response.isSuccessful()){
                    int idrec = (int) ((double) responseBody.get("id_receta"));
                    if(imageUri != null) {
                        Editarimg(idrec);
                    } else {
                        Toast.makeText(CrudRecetas.this, "Receta editada exitosamente", Toast.LENGTH_SHORT).show();
                        finish();
                    }
                }else {
                    Toast.makeText(CrudRecetas.this, "Error al subir la receta", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<Map<String, Object>> call, Throwable t) {
                Toast.makeText(CrudRecetas.this, "Fallo de internet", Toast.LENGTH_SHORT).show();
            }
        });
    }
}