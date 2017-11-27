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
import android.widget.CompoundButton;
import android.widget.ToggleButton;

import br.com.gerqs.lunapptico.R;

public class Configuracoes extends AppCompatActivity implements View.OnClickListener {

    private ToggleButton toggleMusica, toggleEfeitosSonoros, toggleNarrador;
    private boolean prefMusic;
    private MediaPlayer mp;
    private SharedPreferences.Editor editor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_configuracoes);

        //capturas
        findViewsIds();

        //inicializa os objetos das preferências
        SharedPreferences mSharedPreference = PreferenceManager.getDefaultSharedPreferences(getBaseContext());
        editor = mSharedPreference.edit();//usado no onClick

        //preferencia do estado da música on/off
        prefMusic = (mSharedPreference.getBoolean("prefMusic", true));
        toggleMusica.setChecked(prefMusic);

        //ativa a música de acordo com as preferências
        ativaMusicaDeFundo(prefMusic);

        //preferencia do estado dos efeitosSonoros sonoros on/off
        toggleEfeitosSonoros.setChecked(mSharedPreference.getBoolean("prefSoundEffects", true));

        //preferencia da voz do narrador
        toggleNarrador.setChecked(mSharedPreference.getBoolean("prefMic", true));

        //acompanha o estado do toggle para mudar o estado da música de fundo imediatamente
        toggleMusica.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener(){
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                prefMusic = isChecked;
                ativaMusicaDeFundo(prefMusic);
            }
        });
    }

    public void findViewsIds(){
        Button musica = (Button) findViewById(R.id.buttonConfMusica);
        musica.setOnClickListener(this);
        Button efeitosSonoros = (Button) findViewById(R.id.buttonConfEfeitosSonoros);
        efeitosSonoros.setOnClickListener(this);
        Button narrador = (Button) findViewById(R.id.buttonConfNarrador);
        narrador.setOnClickListener(this);
        Button voltar = (Button) findViewById(R.id.buttonConfVoltar);
        voltar.setOnClickListener(this);
        toggleMusica = (ToggleButton) findViewById(R.id.toggleConfMusica);
        toggleMusica.setOnClickListener(this);
        toggleEfeitosSonoros = (ToggleButton) findViewById(R.id.toggleConfEfeitosSonoros);
        toggleEfeitosSonoros.setOnClickListener(this);
        toggleNarrador = (ToggleButton) findViewById(R.id.toggleConfNarrador);
        toggleNarrador.setOnClickListener(this);
        Button home = (Button) findViewById(R.id.buttonConfHome);
        home.setOnClickListener(this);
    }

    /**
     * manipula clicks dos botões
     */
    @Override
    public void onClick(View v) {

        switch (v.getId()){
            case R.id.buttonConfMusica:
                toggleMusica.setChecked(!toggleMusica.isChecked());
            case R.id.toggleConfMusica:
                editor.putBoolean("prefMusic", toggleMusica.isChecked());
                editor.apply();
                break;

            case R.id.buttonConfEfeitosSonoros:
                toggleEfeitosSonoros.setChecked(!toggleEfeitosSonoros.isChecked());
            case R.id.toggleConfEfeitosSonoros:
                editor.putBoolean("prefSoundEffects", toggleEfeitosSonoros.isChecked());
                editor.apply();
                break;

            case R.id.buttonConfNarrador:
                toggleNarrador.setChecked(!toggleNarrador.isChecked());
            case R.id.toggleConfNarrador:
                editor.putBoolean("prefMic", toggleNarrador.isChecked());
                editor.apply();
                break;

            case R.id.buttonConfVoltar:
            case R.id.buttonConfHome:
                Intent intent = new Intent(Configuracoes.this, MainActivity.class);
                startActivity(intent);
                break;

            default:
                break;
        }
    }

    private void ativaMusicaDeFundo(boolean tgpref) {
        if(tgpref) {
            if (mp == null) {
                mp = MediaPlayer.create(Configuracoes.this, R.raw.maintheme);
                mp.setWakeMode(getApplicationContext(), PowerManager.PARTIAL_WAKE_LOCK);
                mp.setLooping(true);
                mp.start();
            }else mp.start();
        }
        else if(mp != null) if(mp.isPlaying()) mp.pause();
    }

    @Override
    protected void onResume() {
        ativaMusicaDeFundo(prefMusic);
        super.onResume();
    }

    @Override
    protected void onPause() {
        if(mp != null) if(mp.isPlaying()) mp.pause();
        super.onPause();
    }

    @Override
    protected void onStop() {
        if (mp != null) if(mp.isPlaying()) mp.stop();
        super.onStop();
    }

    @Override
    protected void onDestroy() {
        if(mp != null) mp.release();
        super.onDestroy();
    }
}
