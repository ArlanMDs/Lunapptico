package br.com.gerqs.lunapptico.activities;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import br.com.gerqs.lunapptico.R;

public class Sobre extends AppCompatActivity implements View.OnClickListener{

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sobre);

        findViewsIds();
    }

    private void findViewsIds(){
        Button voltar = (Button) findViewById(R.id.buttonSobreVoltar);
        voltar.setOnClickListener(this);
        Button home = (Button) findViewById(R.id.buttonSobreHome);
        home.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        Intent intent;
        switch(v.getId()){

            case R.id.buttonSobreVoltar:
            case R.id.buttonSobreHome:
                intent = new Intent(Sobre.this, MainActivity.class);
                startActivity(intent);
                break;

            default:
                break;
        }

    }
}
