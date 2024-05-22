package sv.edu.catolica.cheflim;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

public class Vistareceta extends AppCompatActivity {

    private int id_usuario = -1, id_receta = -1;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_vistareceta);

        Intent intento = getIntent();
        id_receta = intento.getIntExtra("id_receta", -1);
        id_usuario = intento.getIntExtra("id_usuario", -1);

        if (id_receta == -1 || id_usuario == -1){
            finish();
        }
    }
    public void Regresar(View view) {
        finish();
    }
}