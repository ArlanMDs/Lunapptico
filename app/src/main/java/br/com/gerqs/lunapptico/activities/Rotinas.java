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

public class Rotinas extends AppCompatActivity implements View.OnClickListener{
    private MediaPlayer BGMusic;
    private boolean prefMusic;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rotinas);
        findViewsIds();

        //busca a preferencia do estado da música
        final SharedPreferences mSharedPreference = PreferenceManager.getDefaultSharedPreferences(getBaseContext());
        prefMusic = (mSharedPreference.getBoolean("prefMusic", true));
    }
    public void findViewsIds(){
        Button rotina = (Button) findViewById(R.id.buttonRotinasVestir);
        rotina.setOnClickListener(this);
        Button home = (Button) findViewById(R.id.buttonRotinashome);
        home.setOnClickListener(this);
        Button voltar = (Button) findViewById(R.id.buttonRotinasVoltar);
        voltar.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        Intent intent;
        switch (v.getId()){

            case R.id.buttonRotinasVestir:
                intent = new Intent(Rotinas.this, RotinaVestir.class);
                startActivity(intent);
                break;

            case R.id.buttonRotinasVoltar:
            case R.id.buttonRotinashome:
                intent = new Intent(Rotinas.this, MainActivity.class);
                startActivity(intent);
                break;
        }
    }

    private void startBGMusic(boolean tgpref) {
        if(tgpref) {
            if (BGMusic == null) {
                BGMusic = MediaPlayer.create(getApplicationContext(), R.raw.maintheme);
                BGMusic.setWakeMode(getApplicationContext(), PowerManager.PARTIAL_WAKE_LOCK);
                BGMusic.setLooping(true);
                BGMusic.start();
            }else{
                BGMusic.start();
            }
        }
        else{
            if(BGMusic != null) if(BGMusic.isPlaying()) BGMusic.pause();
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
