package br.com.gerqs.lunapptico.activities;

import android.content.Intent;
import android.database.Cursor;
import android.graphics.Color;
import android.graphics.Typeface;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.ScaleAnimation;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import br.com.gerqs.lunapptico.R;
import br.com.gerqs.lunapptico.database.BancoController;
import br.com.gerqs.lunapptico.database.CriaBanco;

public class TelaPontuacao extends AppCompatActivity {
    private TextView totalPontos;
    private  int pontos, bonus;
    private Button home;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tela_pontuacao);

        home = (Button)findViewById(R.id.home3);

        if (savedInstanceState == null) {
            Bundle extras = getIntent().getExtras();
            if(extras == null) {
                Toast.makeText(getApplicationContext(), "teste", Toast.LENGTH_SHORT).show();
                pontos = 0;
                bonus = 0;
            } else {
                pontos = extras.getInt("pontos");
                bonus = extras.getInt("bonus");

            }
        } else {
            Toast.makeText(getApplicationContext(), "bundle null", Toast.LENGTH_SHORT).show();
           // pontos= 0;
        }

        //pontos e bonus feitos pelo usuário
        TextView pontosFeitos = (TextView) findViewById(R.id.pontosFeitos);
        totalPontos = (TextView) findViewById(R.id.totalPontos);
        pontosFeitos.setText("Pontos: " +String.valueOf(pontos) +" Bônus: "+ String.valueOf(bonus));

        inserePontosNoBD(pontos+bonus);

        //muda a cor da fonte dos textViews
        TextView textViewPontos = (TextView) findViewById(R.id.textViewPontos);
        TextView textViewTotalPontos = (TextView) findViewById(R.id.textViewTotalPontos);

        Typeface novaFonte = Typeface.createFromAsset(getAssets(),"fonts/ComingSoon.ttf");
        textViewPontos.setTypeface(novaFonte);
        textViewPontos.setTextSize(20);
        textViewPontos.setTextColor(Color.BLUE);
        textViewPontos.setShadowLayer(5, 3, 3, Color.GRAY);


        textViewTotalPontos.setTypeface(novaFonte);
        textViewTotalPontos.setTextSize(20);
        textViewTotalPontos.setTextColor(Color.BLUE);
        textViewTotalPontos.setShadowLayer(5, 3, 3, Color.GRAY);

        pontosFeitos.setTypeface(novaFonte);
        pontosFeitos.setTextSize(20);
        pontosFeitos.setTextColor(Color.BLUE);
        pontosFeitos.setShadowLayer(5, 3, 3, Color.GRAY);

        totalPontos.setTypeface(novaFonte);
        totalPontos.setTextSize(20);
        totalPontos.setTextColor(Color.BLUE);
        totalPontos.setShadowLayer(5, 3, 3, Color.GRAY);

        //animação do botão home
        Animation scale = new ScaleAnimation(1, 0.8f, 1, 0.8f, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
        scale.setDuration(1500);
        scale.setRepeatCount(5000);
        home.startAnimation(scale);


        home.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), AprenderPalavras.class);
                startActivity(intent);
            }
        });

    }

    private void inserePontosNoBD(int pontosTotais){
        Cursor cursor;
        BancoController crud;
        crud = new BancoController(getBaseContext());

        cursor = crud.carregaDadoById(1);
        int pontuacao = Integer.parseInt(cursor.getString(cursor.getColumnIndexOrThrow(CriaBanco.PONTOS)));

        crud.alteraPontos(1, pontuacao+pontosTotais);
        totalPontos.setText(String.valueOf(pontosTotais+pontuacao));
        cursor.close();
    }

    //voltar para o menu principal quando apertar em voltar
    @Override
    public void onBackPressed() {
        Intent intent = new Intent(TelaPontuacao.this,AprenderPalavras.class);
        startActivity(intent);
    }
}
