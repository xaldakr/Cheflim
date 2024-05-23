package sv.edu.catolica.cheflim;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.util.HashMap;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class PerfilDatos extends AppCompatActivity {

    private ApiService apiService;
    private TextView datouser, datoname;

    private EditText ETname, ETuser, ETmail, ETpass;

    private int id_usu;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_perfil_datos);
        SharedPreferences sharedPreferences = getSharedPreferences("DatosLogin", MODE_PRIVATE);
        // Aqui comprobariamos si existe el usuario
        if (sharedPreferences.getInt("id_usuario", -1) == -1){
            finish();
        }
        id_usu = sharedPreferences.getInt("id_usuario", -1);
        datouser = (TextView) findViewById(R.id.ETuserpd);
        datoname = (TextView) findViewById(R.id.ETnamepd);
        datouser.setText(sharedPreferences.getString("usuario", ""));
        datoname.setText(sharedPreferences.getString("nombre", ""));

        ETname = (EditText) findViewById(R.id.dataname);
        ETuser = (EditText) findViewById(R.id.datauser);
        ETmail = (EditText) findViewById(R.id.datamail);
        ETpass = (EditText) findViewById(R.id.datapass);

        ETname.setText(sharedPreferences.getString("nombre", ""));
        ETuser.setText(sharedPreferences.getString("usuario", ""));
        ETmail.setText(sharedPreferences.getString("correo", ""));
    }
    private void actualizarDato(){
        SharedPreferences sharedPreferences = getSharedPreferences("DatosLogin", MODE_PRIVATE);
        datouser.setText(sharedPreferences.getString("usuario", ""));
        datoname.setText(sharedPreferences.getString("nombre", ""));
    }

    public void SubmitEditarUsu(View view) {
        String txtnombre, txtusu, txtcorreo;
        txtnombre = ETname.getText().toString().trim();
        txtusu = ETuser.getText().toString().trim();
        txtcorreo = ETmail.getText().toString().trim();
        if(txtnombre.equals("")||txtusu.equals("")||txtcorreo.equals("")){
            Toast.makeText(this, "No deje campos vacios!!!", Toast.LENGTH_SHORT).show();
        }else {
            Map<String, Object> request = new HashMap<>();
            request.put("correo", txtcorreo);
            request.put("usuario", txtusu);
            request.put("nombre", txtnombre);
            Call<Map<String, Object>> call = apiService.editUser(id_usu,request);
            call.enqueue(new Callback<Map<String, Object>>() {
                @Override
                public void onResponse(Call<Map<String, Object>> call, Response<Map<String, Object>> response) {
                    Map<String, Object> responseBody = response.body();
                    if(response.isSuccessful()){
                        String usuario = (String) responseBody.get("usuario");
                        String nome = (String) responseBody.get("nombre");
                        String correo = (String) responseBody.get("correo");
                        SharedPreferences sharedPreferences = getSharedPreferences("DatosLogin", MODE_PRIVATE);
                        SharedPreferences.Editor editor = sharedPreferences.edit();
                        editor.putString("usuario", usuario);
                        editor.putString("nombre", nome);
                        editor.putString("correo", correo);
                        editor.apply();
                        actualizarDato();
                        Toast.makeText(PerfilDatos.this, "Datos editados con exito", Toast.LENGTH_SHORT).show();
                    }else {
                        Toast.makeText(PerfilDatos.this, "Error del servidor", Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onFailure(Call<Map<String, Object>> call, Throwable t) {
                    Toast.makeText(PerfilDatos.this, "Fallo de conexion", Toast.LENGTH_SHORT).show();
                }
            });
        }
    }

    public void ActualizarPass(View view){
        String txtpass;
        txtpass= ETpass.getText().toString().trim();

        if(txtpass.equals("")){
            Toast.makeText(this, "Ingrese su nueva contraseña!!!", Toast.LENGTH_SHORT).show();
        }else {
            Map<String, Object> request = new HashMap<>();
            request.put("contrasena", txtpass);
            Call<Map<String, Object>> call = apiService.changePassword(id_usu,request);
            call.enqueue(new Callback<Map<String, Object>>() {
                @Override
                public void onResponse(Call<Map<String, Object>> call, Response<Map<String, Object>> response) {
                    Map<String, Object> responseBody = response.body();
                    if(response.isSuccessful()){

                        Toast.makeText(PerfilDatos.this, "Contraseña editada con exito", Toast.LENGTH_SHORT).show();
                    }else {
                        Toast.makeText(PerfilDatos.this, "Error del servidor", Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onFailure(Call<Map<String, Object>> call, Throwable t) {
                    Toast.makeText(PerfilDatos.this, "Fallo de conexion", Toast.LENGTH_SHORT).show();
                }
            });
        }
    }

    public void CloseSession(View view){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Cerrar la sesión");
        builder.setCancelable(false);
        builder.setMessage("Desea salir de esta cuenta?");
        builder.setPositiveButton("Salir", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                SharedPreferences sharedPreferences = getSharedPreferences("DatosLogin", MODE_PRIVATE);
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putInt("id_usuario", -1);
                editor.putString("usuario", "");
                editor.putString("nombre", "");
                editor.putString("correo", "");
                editor.apply();
                Intent intento = new Intent(PerfilDatos.this, Login.class);
                startActivity(intento);
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
    public void IngresarListadelPerDatos(View view) {
        Intent intento = new Intent(this, Listaingredientes.class);
        startActivity(intento);
        finish();
    }
    public void IngresarIniciodelPerDatos(View view) {
        Intent intento = new Intent(this, Inicio.class);
        startActivity(intento);
        finish();
    }
    public void IngresarPerfildelPerDatos(View view) {
        Intent intento = new Intent(this, PerfilDatos.class);
        startActivity(intento);
        finish();
    }
    public void IngresarDescubredelPerDatos(View view) {
        Intent intento = new Intent(this, Descubre.class);
        startActivity(intento);
        finish();
    }


    public void IngresarPerRecetadelPerDatos(View view) {
        Intent intento = new Intent(this, PerfilRecetas.class);
        startActivity(intento);
        finish();
    }
    public void IngresarPerFavdelPerDatos(View view) {
        Intent intento = new Intent(this, PerfilFavoritos.class);
        startActivity(intento);
        finish();
    }
    public void IngresarPerDatosdelPerDatos(View view) {
        Intent intento = new Intent(this, PerfilDatos.class);
        startActivity(intento);
        finish();
    }


}