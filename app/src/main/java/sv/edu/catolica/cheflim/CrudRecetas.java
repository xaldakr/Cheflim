package sv.edu.catolica.cheflim;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.LinearLayout;

public class CrudRecetas extends AppCompatActivity {

    LinearLayout LayIngPrin, LayPasPrin, LayDatPrin;
    LinearLayout LiIng, LiPas;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_crud_recetas);
    }

}