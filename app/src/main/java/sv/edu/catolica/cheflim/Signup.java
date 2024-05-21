package sv.edu.catolica.cheflim;

import androidx.appcompat.app.AppCompatActivity;

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

public class Signup extends AppCompatActivity {
    private ApiService apiService;
    private EditText Mail, Name, Nick, Pass, CheckPass;
    private String txtcorreo, txtnombre, txtnick, txtcontra, txtcontrarep;
    private Button Sign, Retu;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);

        apiService = ApiClient.getRetrofitInstance().create(ApiService.class);

        Mail = (EditText) findViewById(R.id.ETCorreoSU);
        Name = (EditText) findViewById(R.id.ETNombreSU);
        Nick = (EditText) findViewById(R.id.ETUsuarioSU);
        Pass = (EditText) findViewById(R.id.ETContraSU);
        CheckPass = (EditText) findViewById(R.id.ETRepContraSU);

        Sign = (Button) findViewById(R.id.SignBT);
        Retu = (Button) findViewById(R.id.BTRegresarLog);
    }

    public void Regresar(View view) {
        finish();
    }

    public void IniciarSign(View view){
        Sign.setEnabled(false);
        Retu.setEnabled(false);

        txtcorreo = Mail.getText().toString().trim();
        txtnombre = Name.getText().toString().trim();
        txtnick = Nick.getText().toString().trim();
        txtcontra = Pass.getText().toString().trim();
        txtcontrarep = CheckPass.getText().toString().trim();

        //Validamos que todo este en regla
        if (txtcorreo.equals("") || txtnombre.equals("") || txtnick.equals("") || txtcontra.equals("") || txtcontrarep.equals("")){
            Toast.makeText(Signup.this, "Llena todos los campos!", Toast.LENGTH_SHORT).show();
            Sign.setEnabled(true);
            Retu.setEnabled(true);
        } else {
            if (txtcontra.equals(txtcontrarep)){
                //Manejamos la primera request para verificar si el usuario existe
                Map<String, Object> request = new HashMap<>();
                request.put("correo", txtcorreo);
                request.put("nombre", txtnombre);
                request.put("usuario", txtnick);
                Call<Map<String, Object>> call = apiService.checkIfUser(request);
                call.enqueue(new Callback<Map<String, Object>>() {
                    @Override
                    public void onResponse(Call<Map<String, Object>> call, Response<Map<String, Object>> response) {
                        if (response.isSuccessful()) {
                            // Manejar la respuesta
                            Map<String, Object> responseBody = response.body();
                            if (responseBody != null && responseBody.containsKey("validacion")) {
                                Boolean validacion = (Boolean) responseBody.get("validacion");
                                if(!validacion){
                                    CrearCuen();
                                } else {
                                    Toast.makeText(Signup.this, "Usuario o Correo ya existentes!!!", Toast.LENGTH_SHORT).show();
                                    Sign.setEnabled(true);
                                    Retu.setEnabled(true);
                                }
                            }
                        } else {
                            Toast.makeText(Signup.this, "Error del servidor", Toast.LENGTH_SHORT).show();
                            Sign.setEnabled(true);
                            Retu.setEnabled(true);
                        }
                    }

                    @Override
                    public void onFailure(Call<Map<String, Object>> call, Throwable t) {
                        Toast.makeText(Signup.this, "Error al comunicarse con el servidor", Toast.LENGTH_SHORT).show();
                        Sign.setEnabled(true);
                        Retu.setEnabled(true);
                    }
                });
            } else {
                Toast.makeText(Signup.this, "Repita correctamente su contraseña!!!", Toast.LENGTH_SHORT).show();
                Sign.setEnabled(true);
                Retu.setEnabled(true);
            }
        }

    }
    private void CrearCuen(){
        Map<String, Object> request = new HashMap<>();
        request.put("correo", txtcorreo);
        request.put("nombre", txtnombre);
        request.put("usuario", txtnick);
        request.put("contrasena", txtcontra);
        Call<Map<String, Object>> call = apiService.createUser(request);
        call.enqueue(new Callback<Map<String, Object>>() {
            @Override
            public void onResponse(Call<Map<String, Object>> call, Response<Map<String, Object>> response) {
                if (response.isSuccessful()) {
                    Toast.makeText(Signup.this, "Usuario creado correctamente!!!", Toast.LENGTH_SHORT).show();
                    Sign.setEnabled(true);
                    Retu.setEnabled(true);
                    txtcorreo = "";
                    txtnombre = "";
                    txtnick = "";
                    txtcontra = "";
                    txtcontrarep = "";
                    finish();
                } else {
                    Toast.makeText(Signup.this, "Error al crear el usuario...", Toast.LENGTH_SHORT).show();
                    Sign.setEnabled(true);
                    Retu.setEnabled(true);
                }
            }

            @Override
            public void onFailure(Call<Map<String, Object>> call, Throwable t) {
                Toast.makeText(Signup.this, "Fallo al comunicarse con el servidor!!!", Toast.LENGTH_SHORT).show();
                Sign.setEnabled(true);
                Retu.setEnabled(true);
            }
        });
    }
}