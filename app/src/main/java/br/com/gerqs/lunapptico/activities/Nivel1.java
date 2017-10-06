package br.com.gerqs.lunapptico.activities;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.Typeface;
import android.media.MediaPlayer;
import android.os.CountDownTimer;
import android.os.PowerManager;
import android.preference.PreferenceManager;
import android.support.constraint.ConstraintLayout;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.ScaleAnimation;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageSwitcher;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ViewSwitcher;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;

import br.com.gerqs.lunapptico.R;
import br.com.gerqs.lunapptico.fragmentController.FragmentEscolhaCenario;
import br.com.gerqs.lunapptico.soundManager.SoundPlayer;
import br.com.gerqs.lunapptico.tools.RoundCorners;


public class Nivel1 extends AppCompatActivity implements FragmentEscolhaCenario.InterfaceComunicao {

    private ImageSwitcher imageSwitcher;
    private ArrayList<String> cards1;
    private int count;
    private int currentIndex;
    private String cenario;
    private MediaPlayer mpBackgroundMusic;
    private TextView textView;
    private Button setaEsquerda, setaDireita, play;
    private boolean prefMusic;
    private boolean prefMic;
    private boolean prefSoundEffects;
    private ConstraintLayout fase1Content;
    private  Animation scale, in, out;
    private RoundCorners roundCorners;
    private SoundPlayer soundPlayer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fase1);

        //início das capturas
        Button home = (Button) findViewById(R.id.fase1Home);
        play = (Button)findViewById(R.id.playbutton);
        imageSwitcher = (ImageSwitcher)findViewById(R.id.imageSwitcher1);
        setaDireita = (Button) findViewById(R.id.arrowright);
        setaEsquerda = (Button) findViewById(R.id.arrowleft);
        textView = (TextView) findViewById(R.id.nomeDoAnimal);
        textView.setAllCaps(true);
        fase1Content = (ConstraintLayout) findViewById(R.id.fase1Content);
        roundCorners = new RoundCorners(Nivel1.this, getResources());

        //os players de música e palavras estão inicializados no onResume()

        //preferencia do estado da música e mic e soundeffects
        final SharedPreferences mSharedPreference = PreferenceManager.getDefaultSharedPreferences(getBaseContext());
        prefMusic = (mSharedPreference.getBoolean("prefMusic", true));
        prefMic = (mSharedPreference.getBoolean("prefMic", true));
        prefSoundEffects = mSharedPreference.getBoolean("prefSoundEffects", true);

        //preferencia de estado da fase (em progresso ou não)
        SharedPreferences sharedPreferences = getPreferences(MODE_PRIVATE);
        //boolean nivelEmAndamento = sharedPreferences.getBoolean("nivelEmAndamento", false);

        //inicializa o switcher
        imageSwitcher.setFactory(new ViewSwitcher.ViewFactory() {
            public View makeView() {
                // Create a new ImageView and set it's properties
                ImageView imageView = new ImageView(getApplicationContext());
                // set Scale type of ImageView to Fit Center
                imageView.setScaleType(ImageView.ScaleType.FIT_CENTER);
                // set the Height And Width of ImageView To FIll PARENT
                imageView.setLayoutParams(new ImageSwitcher.LayoutParams(ConstraintLayout.LayoutParams.MATCH_PARENT, ConstraintLayout.LayoutParams.MATCH_PARENT));
                return imageView;
            }
        });

        //iniciar uma animação global afim de evitar que fique uma instancia sempre que deixar o botão invisível
        scale = new ScaleAnimation(1, 0.8f, 1, 0.8f, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
        scale.setDuration(1500);
        scale.setRepeatCount(Animation.INFINITE);
        in = AnimationUtils.loadAnimation(this, android.R.anim.fade_in);
        out = AnimationUtils.loadAnimation(this, android.R.anim.fade_out);
        animaBotoes(true);

        if (sharedPreferences.getBoolean("nivelEmAndamento", false)) {
            escondeFragmentEscolhaCenario();
            loadPreferences();
            mostraUI();

            if(currentIndex < 0) currentIndex = 0;
            if(currentIndex > 0) mostraSetaEsquerda();
            if(currentIndex >= 0){
                play.setVisibility(View.VISIBLE);
                insereImagemNoSwitcher();
            }
            String s = cards1.get(currentIndex);
            s = s.replaceAll(".jpeg", "");
            textView.setText(s);
            soundPlayer = new SoundPlayer(Nivel1.this);
            reproduz(prefMic, prefSoundEffects, s);
        }else {
           //Esconde o layout principal para mostrar o fragmento de escolha de cenário
            escondeUI();
            currentIndex = -1;
            // Begin the transaction
            FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
            // Replace the contents of the container with the new fragment
            ft.replace(R.id.your_placeholder, new FragmentEscolhaCenario());
            ft.commit();
        }

        //mudança de fonte e efeito no textView que contém o nome do animal atual
        Typeface novaFonte = Typeface.createFromAsset(getAssets(),"fonts/ComingSoon.ttf");
        textView.setTypeface(novaFonte);
        textView.setTextSize(40);
        textView.setTextColor(Color.BLUE);
        textView.setShadowLayer(5, 3, 3, Color.GRAY);

        home.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                savePreferences();
                Intent intent =  new Intent(Nivel1.this, AprenderPalavras.class);
                startActivity(intent);
            }
        });

        setaDireita.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //o usuário pode sair da activity a qualquer momento, e, quando voltar, a seta esquerda estará invisível (definido no xml)
                if (currentIndex >= 0) mostraSetaEsquerda();

                if (play.getVisibility() == View.INVISIBLE) play.setVisibility(View.VISIBLE);

                //esconde temporariamente as setas para evitar que o usuário passe pelas imagens rapidamente
                escondeSetas();

                if (currentIndex >= 0) {
                    new CountDownTimer(1000, 1000) {
                        public void onTick(long millisUntilFinished) {
                        }

                        public void onFinish() {
                            mostraSetas();
                        }
                    }.start();
                }else{
                    new CountDownTimer(1000, 1000) {
                        public void onTick(long millisUntilFinished) {
                        }

                        public void onFinish() {
                            mostraSetaDireita();
                        }
                    }.start();
                }

                //incremento no index do array
                currentIndex++;

                //tocar nome no TextView
                if(currentIndex < count){
                    String s = cards1.get(currentIndex);
                    s = s.replaceAll(".jpeg", "");
                    textView.setText(s);
                   reproduz(prefMic, prefSoundEffects, s);
                }

                //  Checa se o index está no máximo do array
                if (currentIndex == count) {
                    if (mpBackgroundMusic != null) {
                        mpBackgroundMusic.release();
                        mpBackgroundMusic = null;
                    }
                    //se terminou a activity, reseta as preferências
                    resetPreferences();
                    Intent intent = new Intent(Nivel1.this, AprenderPalavras.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(intent);
                }else
                    insereImagemNoSwitcher();
            }
        });

        setaEsquerda.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(currentIndex > 0){
                    currentIndex--;

                    //esconde temporariamente as setas para evitar que o usuário passe pelas imagens rapidamente
                    escondeSetas();
                    new CountDownTimer(1000, 1000) {
                        public void onTick(long millisUntilFinished) {}
                        public void onFinish() {
                            mostraSetas();
                            if(currentIndex == 0) escondeSetaEsquerda();
                        }
                    }.start();

                    //tocar nome da foto
                    if(currentIndex < count) {
                        String s = cards1.get(currentIndex);
                        s = s.replaceAll(".jpeg", "");
                        textView.setText(s);
                        reproduz(prefMic, prefSoundEffects, s);
                    }
                    insereImagemNoSwitcher();
                }
            }
        });

        play.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String s = cards1.get(currentIndex);
                s = s.replaceAll(".jpeg", "");
                reproduz(true, true, s);
            }
        });
    }

    /**
     * recebe o código do cenário escolhido no fragmento para a activity
     * @param c código
     */
    @Override
    public void setCenario(String c) {
        cenario = c;
        escondeFragmentEscolhaCenario();
        escolheCenario();
    }

    private void escondeFragmentEscolhaCenario(){
        FrameLayout frag = (FrameLayout)findViewById(R.id.your_placeholder);
        frag.setVisibility(View.INVISIBLE);
    }

    private void insereImagemNoSwitcher() {
        if(imageSwitcher.getVisibility() == View.INVISIBLE)// isso é para evitar que o background que dá o contorno fique visível quando index é -1
           imageSwitcher.setVisibility(View.VISIBLE);

        InputStream inputstream = null;
        try {
            inputstream = getApplicationContext().getAssets().open(cenario+File.separator+cards1.get(currentIndex));
        } catch (IOException e) {
            e.printStackTrace();
            retornaParaMainActivity(2);
        }

        //arredonda os cantos da imgview e insere no switcher
        imageSwitcher.setImageDrawable(roundCorners.arredondarPorInputstream(inputstream));
        if(inputstream!=null) {
            try {
                inputstream.close();
            } catch (IOException e) {
                e.printStackTrace();
                retornaParaMainActivity(2);
            }
        }
    }

    private void savePreferences(){
        SharedPreferences sharedPreferences = getPreferences(MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();

        editor.putBoolean("nivelEmAndamento", true);
        editor.putInt("currentIndex",currentIndex);
        editor.putString("cenario",cenario);

        String stringCards1 = TextUtils.join(",", cards1);
        editor.putString("cards1", stringCards1);
        editor.apply();
    }

    private void resetPreferences(){
        SharedPreferences sharedPreferences = getPreferences(MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();

        editor.putBoolean("nivelEmAndamento", false);

        editor.apply();
    }

    private void loadPreferences(){
        SharedPreferences sharedPreferences = getPreferences(MODE_PRIVATE);
        currentIndex = sharedPreferences.getInt("currentIndex", -1);
        cenario = sharedPreferences.getString("cenario", null);

        //Set<String> set = sharedPreferences.getStringSet("cards1", null);
        //if(set != null)
        //cards1 = new ArrayList<String>(set);
        String stringCards1 = sharedPreferences.getString("cards1", null);

        if(stringCards1 != null) cards1 = new ArrayList<String>(Arrays.asList(stringCards1.split(",")));
        count = cards1.size();

    }

    private void retornaParaMainActivity(int n){
        Toast.makeText(this, "Erro N. 1 - "+String.valueOf(n), Toast.LENGTH_SHORT).show();
        Intent intent = new Intent(Nivel1.this, AprenderPalavras.class);
        startActivity(intent);
    }

    private void escolheCenario(){
        //Cenário 1: animais
        //Cenário 2: objetos
        criaArrayDeCartas();
        //UI pronta para iniciar
        fase1Content.setVisibility(View.VISIBLE);
    }

    private void ativaMediaPlayers() {
        soundPlayer = new SoundPlayer(Nivel1.this);
        if(prefMusic) {
            mpBackgroundMusic = MediaPlayer.create(Nivel1.this, R.raw.maintheme);
            mpBackgroundMusic.setWakeMode(getApplicationContext(), PowerManager.PARTIAL_WAKE_LOCK);
            mpBackgroundMusic.setLooping(true);
            mpBackgroundMusic.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                @Override
                public void onPrepared(MediaPlayer mp) {
                    mpBackgroundMusic.start();
                }
            });
        }
    }

    /**
     * adiciona o caminho das imagens da subpasta "animals", de assets à um array e o mistura
     */
    private void criaArrayDeCartas() {
        String[] images = new String[0];

        try {
            images = getAssets().list(cenario);
        } catch (IOException e) {
            e.printStackTrace();
            retornaParaMainActivity(3);
        }

        cards1 = new ArrayList<>(Arrays.asList(images));
        count = cards1.size();
        Collections.shuffle(cards1);
    }

    private void reproduz(boolean nome, boolean som, final String nomeDoArquivo) {
        if(cenario == "objects") soundPlayer.reproduz(nome, false, cenario, nomeDoArquivo);
        else soundPlayer.reproduz(nome, som, cenario, nomeDoArquivo);
    }

    private void escondeUI(){
        fase1Content.setVisibility(View.INVISIBLE);
    }

    private void mostraUI(){
        //if(cenario == 1) fase1Layout.setBackgroundResource(R.drawable.floresta);
        //else if(cenario == 2) fase1Layout.setBackgroundResource(R.drawable.fazenda);
        fase1Content.setVisibility(View.VISIBLE);

    }

    /**
     * cria e insere animações nos botões
     * @param b
     */
    private void animaBotoes(boolean b) {
        if(b) {
            setaDireita.startAnimation(scale);
            //a animação da seta esquerda é feita em mostraSetaEsquerda()

            imageSwitcher.setInAnimation(in);
            imageSwitcher.setOutAnimation(out);
        }
        else{
            setaDireita.clearAnimation();
            setaEsquerda.clearAnimation();
            imageSwitcher.clearAnimation();
        }

    }

    private void mostraSetaEsquerda(){
        setaEsquerda.startAnimation(scale);
        setaEsquerda.setVisibility(View.VISIBLE);
    }

    private void mostraSetaDireita(){
        setaDireita.startAnimation(scale);
        setaDireita.setVisibility(View.VISIBLE);
    }

    private void mostraSetas(){
        setaDireita.startAnimation(scale);
        setaDireita.setVisibility(View.VISIBLE);
        setaEsquerda.startAnimation(scale);
        setaEsquerda.setVisibility(View.VISIBLE);
    }

    private void escondeSetaEsquerda(){
        setaEsquerda.clearAnimation();
        setaEsquerda.setVisibility(View.INVISIBLE);
    }

    private void escondeSetas(){
        setaEsquerda.clearAnimation();
        setaEsquerda.setVisibility(View.INVISIBLE);
        setaDireita.clearAnimation();
        setaDireita.setVisibility(View.INVISIBLE);
    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent(Nivel1.this, AprenderPalavras.class);
        startActivity(intent);
        super.onBackPressed();
    }

    @Override
    protected void onResume() {
        ativaMediaPlayers();

        super.onResume();
    }

    @Override
    protected void onPause() {
        soundPlayer.stop();
        if(mpBackgroundMusic != null) if (mpBackgroundMusic.isPlaying()) mpBackgroundMusic.stop();

        super.onPause();
    }

    @Override
    protected void onStop() {
        if (mpBackgroundMusic !=null) mpBackgroundMusic.release();
        super.onStop();
    }

    @Override
    protected void onDestroy() {
        if(mpBackgroundMusic != null) mpBackgroundMusic.release();
        soundPlayer.release();
        super.onDestroy();
    }

}