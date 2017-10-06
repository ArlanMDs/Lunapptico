package br.com.gerqs.lunapptico.activities;

import android.content.Intent;
import android.content.SharedPreferences;
import android.media.MediaPlayer;
import android.os.PowerManager;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import br.com.gerqs.lunapptico.R;

public  class AprenderPalavras extends AppCompatActivity implements View.OnClickListener {
    private MediaPlayer BGMusic;
    private boolean prefMusic;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_aprender_palavras);

        //início das capturas
        findViewsIds();

        //busca a preferencia do estado da música
        final SharedPreferences mSharedPreference = PreferenceManager.getDefaultSharedPreferences(getBaseContext());
        prefMusic = (mSharedPreference.getBoolean("prefMusic", true));

    }

    private void findViewsIds(){
        Button nivel1 = (Button) findViewById(R.id.buttonMainNivel1);
        nivel1.setOnClickListener(this);
        Button nivel2 = (Button) findViewById(R.id.buttonMainNivel2);
        nivel2.setOnClickListener(this);
        Button nivel3 = (Button) findViewById(R.id.buttonMainNivel3);
        nivel3.setOnClickListener(this);
        Button home = (Button) findViewById(R.id.buttonAprenderVoltar);
        home.setOnClickListener(this);
    }

    /**
     * manipula os clicks nos botões
     * @param v view
     */
    @Override
    public void onClick(View v) {
        Intent intent;
        switch (v.getId()) {

            case R.id.buttonMainNivel1:
                intent = new Intent(AprenderPalavras.this, Nivel1.class);
                startActivity(intent);
                break;

            case R.id.buttonMainNivel2:
                intent = new Intent(AprenderPalavras.this, Nivel2.class);
                startActivity(intent);
                break;

            case R.id.buttonMainNivel3:
                intent = new Intent(AprenderPalavras.this, Nivel3.class);
                startActivity(intent);
                break;

            case R.id.buttonAprenderVoltar:
                intent = new Intent(AprenderPalavras.this, MainActivity.class);
                startActivity(intent);

            default:
                break;
        }
    }

    private void startBGMusic(boolean tgpref) {
        if(tgpref) {
            if (BGMusic == null) {
                BGMusic = MediaPlayer.create(AprenderPalavras.this, R.raw.maintheme);
                BGMusic.setWakeMode(getApplicationContext(), PowerManager.PARTIAL_WAKE_LOCK);
                BGMusic.setLooping(true);
                BGMusic.start();
            }else{
                BGMusic.start();
            }
        }
        else{
            if(BGMusic != null)
                if(BGMusic.isPlaying())
                    BGMusic.pause();
        }
    }

    @Override
    protected void onResume() {
         startBGMusic(prefMusic);
         super.onResume();
    }
    //TODO rever padrão para MEdiaPlayer
    @Override
    protected void onPause() {
        if(BGMusic != null) if(BGMusic.isPlaying()) BGMusic.pause();
        super.onPause();
    }

    @Override
    protected void onStop() {
        if (BGMusic != null) if(BGMusic.isPlaying()) BGMusic.stop();
        super.onStop();
    }

    @Override
    protected void onDestroy() {
        if(BGMusic != null) BGMusic.release();
        super.onDestroy();
    }

}