package sv.edu.catolica.cheflim;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import java.util.HashMap;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class Login extends AppCompatActivity {
    private ApiService apiService;
    private Button Log, Sign;
    private EditText Mail, Pass;
    private String txtcorreo, txtcontra;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login);

        apiService = ApiClient.getRetrofitInstance().create(ApiService.class);
        SharedPreferences sharedPreferences = getSharedPreferences("DatosLogin", MODE_PRIVATE);
        //Aqui se va a interrogar si ya hay una sesion activa
        if (sharedPreferences.getInt("id_usuario", -1) != -1){
            Intent intentamos = new Intent(Login.this, Inicio.class);
            startActivity(intentamos);
            finish();
        }


        Log = (Button) findViewById(R.id.loginButton);
        Sign = (Button) findViewById(R.id.gotosign);
        Mail = (EditText) findViewById(R.id.emailEditText);
        Pass = (EditText) findViewById(R.id.passwordEditText);
        /*
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putInt("id_usuario", -1);
        editor.putString("usuario", "");
        editor.putString("nombre", "");
        editor.putString("correo", "");
        editor.apply();
        */
    }

    public void Crearcue(View view) {
        Intent intentosign = new Intent(Login.this, Signup.class);
        startActivity(intentosign);
    }

    public void Ingresar(View view) {
        Sign.setEnabled(false);
        Log.setEnabled(false);
        txtcorreo = Mail.getText().toString().trim();
        txtcontra = Pass.getText().toString().trim();
        if (txtcontra.equals("") || txtcorreo.equals("")){
            Toast.makeText(Login.this, getString(R.string.llena_campos), Toast.LENGTH_SHORT).show();
            Sign.setEnabled(true);
            Log.setEnabled(true);
        } else {
            Map<String, Object> request = new HashMap<>();
            request.put(getString(R.string.correo_label), txtcorreo);
            request.put(getString(R.string.contrasena_label), txtcontra);
            Call<Map<String, Object>> call = apiService.login(request);
            call.enqueue(new Callback<Map<String, Object>>() {
                @Override
                public void onResponse(Call<Map<String, Object>> call, Response<Map<String, Object>> response) {
                    Map<String, Object> responseBody = response.body();
                    if(response.isSuccessful()){
                        String usuario = (String) responseBody.get(getString(R.string.usuario_label));
                        String nome = (String) responseBody.get(getString(R.string.nombre_label));
                        String correo = (String) responseBody.get(getString(R.string.correo_label));
                        //Aqui obtener los datos de login
                        int idusu = (int) ((double) responseBody.get("id_usuario"));
                        SharedPreferences sharedPreferences = getSharedPreferences("DatosLogin", MODE_PRIVATE);
                        SharedPreferences.Editor editor = sharedPreferences.edit();
                        editor.putInt(getString(R.string.id_usuario_key), idusu);
                        editor.putString(getString(R.string.usuario_label), usuario);
                        editor.putString(getString(R.string.nombre_label), nome);
                        editor.putString(getString(R.string.correo_label), correo);
                        editor.apply();
                        Sign.setEnabled(true);
                        Log.setEnabled(true);
                        Toast.makeText(Login.this, getString(R.string.bienvenido_mensaje) + " " + usuario, Toast.LENGTH_SHORT).show();
                        Intent intentomain = new Intent(Login.this, Inicio.class);
                        startActivity(intentomain);
                        finish();
                    } else {
                        Toast.makeText(Login.this, getString(R.string.usuario_no_encontrado), Toast.LENGTH_SHORT).show();
                        Sign.setEnabled(true);
                        Log.setEnabled(true);
                    }
                }

                @Override
                public void onFailure(Call<Map<String, Object>> call, Throwable t) {
                    Toast.makeText(Login.this, getString(R.string.error_comunicacion_servidor), Toast.LENGTH_SHORT).show();
                    Sign.setEnabled(true);
                    Log.setEnabled(true);
                }
            });
        }
    }
}