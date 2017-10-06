package br.com.gerqs.lunapptico.database;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import br.com.gerqs.lunapptico.R;

public class InsereDado extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_insere_dado);

        Button botao = (Button)findViewById(R.id.buttonMainNivel1);

        botao.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                BancoController crud = new BancoController(getBaseContext());
                EditText pontos = (EditText)findViewById(R.id.editText);
                String pontosString = pontos.getText().toString();

                String resultado;

                resultado = crud.insereDado(pontosString);

                Toast.makeText(getApplicationContext(), resultado, Toast.LENGTH_LONG).show();
            }
        });
    }
}
