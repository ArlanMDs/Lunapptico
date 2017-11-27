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

public class MainActivity extends AppCompatActivity implements View.OnClickListener {
    private boolean prefMusic;
    private MediaPlayer bgMusic;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        findViewsIds();

        //busca a preferencia do estado da música
        final SharedPreferences mSharedPreference = PreferenceManager.getDefaultSharedPreferences(getBaseContext());
        prefMusic = (mSharedPreference.getBoolean("prefMusic", true));

    }

    private void findViewsIds() {
        Button aprender = (Button) findViewById(R.id.aprenderPalavras);
        aprender.setOnClickListener(this);

        Button rotina = (Button) findViewById(R.id.rotinas);
        rotina.setOnClickListener(this);

        Button conf = (Button) findViewById(R.id.buttonMainConfiguracoes);
        conf.setOnClickListener(this);

        Button sobre = (Button) findViewById(R.id.buttonMainSobre);
        sobre.setOnClickListener(this);
    }

    /**
     * manipula os clicks nos botões
     * @param v button clickado
     */
    @Override
    public void onClick(View v) {
        Intent intent;
        switch (v.getId()) {

            case R.id.aprenderPalavras:
                intent = new Intent(MainActivity.this, AprenderPalavras.class);
                startActivity(intent);
                break;

            case R.id.rotinas:
                intent = new Intent(MainActivity.this, Rotinas.class);
                startActivity(intent);
                break;

            case R.id.buttonMainConfiguracoes:
                intent = new Intent(MainActivity.this, Configuracoes.class);
                startActivity(intent);
                break;

            case R.id.buttonMainSobre:
                intent = new Intent(MainActivity.this, Sobre.class);
                startActivity(intent);
                break;

            default:
                break;
        }
    }

    private void startBGMusic(boolean tgpref) {
        if(tgpref) {
            if (bgMusic == null) {
                bgMusic = MediaPlayer.create(MainActivity.this, R.raw.maintheme);
                bgMusic.setWakeMode(getApplicationContext(), PowerManager.PARTIAL_WAKE_LOCK);
                bgMusic.setLooping(true);
                bgMusic.start();
            }else{
                bgMusic.start();
            }
        }
        else{
            if(bgMusic != null) if(bgMusic.isPlaying()) bgMusic.pause();
        }
    }

    @Override
    protected void onResume() {
        startBGMusic(prefMusic);
        super.onResume();
    }

    @Override
    protected void onPause() {
        if(bgMusic != null) if(bgMusic.isPlaying()) bgMusic.pause();
        super.onPause();
    }

    @Override
    protected void onStop() {
        if (bgMusic != null) if(bgMusic.isPlaying()) bgMusic.stop();
        super.onStop();
    }

    @Override
    protected void onDestroy() {
        if(bgMusic != null) bgMusic.release();
        super.onDestroy();
    }

}
