package br.com.gerqs.lunapptico.database;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import br.com.gerqs.lunapptico.R;

public class Alterar extends Activity {
    private EditText pontuacao;
    private BancoController crud;
    private String codigo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_alterar);

        codigo = this.getIntent().getStringExtra("codigo");

        crud = new BancoController(getBaseContext());

        pontuacao = (EditText)findViewById(R.id.editText4);

        Cursor cursor = crud.carregaDadoById(Integer.parseInt(codigo));
        pontuacao.setText(cursor.getString(cursor.getColumnIndexOrThrow(CriaBanco.PONTOS)));

        Button alterar = (Button) findViewById(R.id.buttonMainNivel2);

        alterar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                crud.alteraPontos(Integer.parseInt(codigo), Integer.parseInt(pontuacao.getText().toString()));
                Intent intent = new Intent(Alterar.this,Consulta.class);
                startActivity(intent);
                finish();
            }
        });
/*
        deletar = (Button)findViewById(R.id.button3);
        deletar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                crud.deletaRegistro(Integer.parseInt(codigo));
                Intent intent = new Intent(Alterar.this,Consulta.class);
                startActivity(intent);
                finish();
            }
        });
   */
    }
}
