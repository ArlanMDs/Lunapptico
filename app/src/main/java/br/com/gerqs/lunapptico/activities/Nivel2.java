package br.com.gerqs.lunapptico.activities;

import android.animation.ObjectAnimator;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.Typeface;
import android.media.MediaPlayer;
import android.os.CountDownTimer;
import android.os.PowerManager;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.constraint.ConstraintLayout;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextSwitcher;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ViewSwitcher;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Random;

import br.com.gerqs.lunapptico.R;
import br.com.gerqs.lunapptico.interpolator.BounceInterpolator;
import br.com.gerqs.lunapptico.fragmentController.FragmentEscolhaCenario;
import br.com.gerqs.lunapptico.soundManager.SoundPlayer;
import br.com.gerqs.lunapptico.tools.RoundCorners;
import br.com.gerqs.lunapptico.soundManager.SoundEffects;

public class Nivel2 extends AppCompatActivity implements FragmentEscolhaCenario.InterfaceComunicao, View.OnClickListener {
    private Button button1, button2, button3, button4, buttonPontos, home, play;
    private StringBuilder letrasDaPalavra;
    private ImageView imageView;
    private String alfabeto, palavra, cenario;
    private ArrayList<String> cards2;
    private int pontos, bonus, letraAtual, nLetrasRestantes, currentIndex, count;//TODO implementar bonus
    private TextSwitcher palavraSwitcher, pontosSwitcher;
    private SoundEffects mSoundPool;
    private int mSounds[] = new int[3];
    private final static int ERRADO = 0, CERTO = 1, VICTORY = 2;
    private MediaPlayer mpBackgroundMusic;
    private boolean prefSoundEffects;
    private boolean prefMic;
    private boolean prefMusic;
    private boolean ultimaPalavra;
    private ConstraintLayout fase2Content, estrelasLayout;
    private Animation bounceAnim, in;
    private ObjectAnimator fadeAnim;
    private RoundCorners roundCorners;
    private SoundPlayer soundPlayer;

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fase2);

        //faz algumas inicializações
        inicializaVariaveis();
        //captura os botões
        findViewsIds();

        //inicializa e anima switcher e buttons
        animaTextSwitchers();
        mudaFonteDosBotoes();

        //animaçoes dos botões (globais, afim de não reinicializar os objetos animadores sempre que o efeito for retirado dos botões)
        bounceAnim = AnimationUtils.loadAnimation(this, R.anim.bounce);
        BounceInterpolator interpolator = new BounceInterpolator(0.2, 20.0);
        bounceAnim.setInterpolator(interpolator);
        fadeAnim = ObjectAnimator.ofFloat(estrelasLayout, "alpha", 1f, 0f);
        animaBotoes(true);

        //busca preferência sobre efeitos sonoros
        final SharedPreferences mSharedPreference= PreferenceManager.getDefaultSharedPreferences(getBaseContext());
        prefSoundEffects = mSharedPreference.getBoolean("prefSoundEffects", true);  //default is true
        prefMic = (mSharedPreference.getBoolean("prefMic", true));
        prefMusic = (mSharedPreference.getBoolean("prefMusic", true));

        //preferencia de estado da fase (em progresso ou não)
        SharedPreferences sharedPreferences = getPreferences(MODE_PRIVATE);
        //boolean nivelEmAndamento = sharedPreferences.getBoolean("nivelEmAndamento", false);

        if (sharedPreferences.getBoolean("nivelEmAndamento", false))
            continuarDialog();
        else
            restauraActivityParaEstadoInicial();

    }

    private void findViewsIds() {
        button1 = (Button)findViewById(R.id.Button);
        button1.setOnClickListener(this);
        button2 = (Button)findViewById(R.id.Button2);
        button2.setOnClickListener(this);
        button3 = (Button)findViewById(R.id.Button3);
        button3.setOnClickListener(this);
        button4 = (Button)findViewById(R.id.Button4);
        button4.setOnClickListener(this);
        buttonPontos = (Button)findViewById(R.id.buttonPontos);
        buttonPontos.setOnClickListener(this);
        home = (Button) findViewById(R.id.fase2Home);
        home.setOnClickListener(this);
        play = (Button) findViewById(R.id.playButton2);
        play.setOnClickListener(this);

        imageView = (ImageView) findViewById(R.id.imageView);
        fase2Content = (ConstraintLayout) findViewById(R.id.fase2Content);
        estrelasLayout = (ConstraintLayout) findViewById(R.id.estrelasLayout);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.Button:

                //checar se acertou
                if(escolheLetra(button1.getText().charAt(0))){
                    //acertou
                    button1.startAnimation(bounceAnim);
                    atualizaInterface();
                    pontua10();
                    if(prefSoundEffects) mSoundPool.playSound(mSounds[CERTO], 0.1f,0.1f);

                }else {
                    if(prefSoundEffects) mSoundPool.playSound(mSounds[ERRADO], 0.1f,0.1f);
                }

                break;
            case R.id.Button2:

                if(escolheLetra(button2.getText().charAt(0))){
                    button2.startAnimation(bounceAnim);
                    atualizaInterface();
                    pontua10();
                    if(prefSoundEffects) mSoundPool.playSound(mSounds[CERTO], 0.1f,0.1f);

                }else {
                    if(prefSoundEffects) mSoundPool.playSound(mSounds[ERRADO], 0.1f,0.1f);
                }

                break;
            case R.id.Button3:

                if(escolheLetra(button3.getText().charAt(0))){
                    button3.startAnimation(bounceAnim);
                    atualizaInterface();
                    pontua10();
                    if(prefSoundEffects) mSoundPool.playSound(mSounds[CERTO], 0.1f,0.1f);

                }else {
                    if(prefSoundEffects) mSoundPool.playSound(mSounds[ERRADO], 0.1f,0.1f);
                }

                break;

            case R.id.Button4:

                if(escolheLetra(button4.getText().charAt(0))){
                    button4.startAnimation(bounceAnim);
                    atualizaInterface();
                    pontua10();
                    if(prefSoundEffects) mSoundPool.playSound(mSounds[CERTO], 0.1f,0.1f);

                }else {
                    if(prefSoundEffects) mSoundPool.playSound(mSounds[ERRADO], 0.1f,0.1f);
                }

                break;
            case R.id.buttonPontos:

                resetPreferences();
                if (mpBackgroundMusic != null){
                    mpBackgroundMusic.release();
                    mpBackgroundMusic = null;
                }
                Intent intent = new Intent(Nivel2.this, TelaPontuacao.class);
                intent.putExtra("pontos",pontos);
                intent.putExtra("bonus",bonus);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);

                break;
            case R.id.fase2Home:

                savePreferences();
                intent = new Intent(Nivel2.this, AprenderPalavras.class);
                startActivity(intent);

                break;
            case R.id.playButton2:

                String s = cards2.get(currentIndex);
                s = s.replaceAll(".jpeg", "");
                reproduz(true, true, s);

                break;
        }
    }


    private void continuarDialog() {

        new AlertDialog.Builder(Nivel2.this)
                .setMessage("Deseja continuar de onde parou?")
                .setCancelable(false)
                .setPositiveButton("Sim", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which)
                    {

                        escondeFragmentEscolhaCenario();
                        loadPreferences();
                        //recupera os pontos
                        pontosSwitcher.setText(String.valueOf(pontos) + " ");
                        //inicia o jogo
                        proximaImagem();
                        sorteiaLetrasDosButtons();
                        mostraUI();

                        dialog.cancel();

                    }
                })
                .setNegativeButton("Não", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which)
                    {
                        restauraActivityParaEstadoInicial();
                        dialog.cancel();
                    }
                }).show();
    }

    private void restauraActivityParaEstadoInicial(){
        SharedPreferences sharedPreferences = getPreferences(MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putBoolean("nivelEmAndamento", false);
        editor.apply();

        //Esconde o layout principal para mostrar o fragmento de escolha de cenário
        escondeUI();
        // Begin the transaction
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        // Replace the contents of the container with the new fragment
        ft.replace(R.id.your_placeholder, new FragmentEscolhaCenario());
        ft.commit();
    }

    private void inicializaVariaveis() {
        ultimaPalavra = false;
        alfabeto = "abcdefghijlmnopqrstuvxz";
        roundCorners = new RoundCorners(Nivel2.this, getResources());
        in = AnimationUtils.loadAnimation(this, android.R.anim.slide_in_left);

    }

    private void reproduz(boolean nome, boolean som, final String nomeDoArquivo) {
        if(cenario == "objects") soundPlayer.reproduz(nome, false, cenario, nomeDoArquivo);
        else soundPlayer.reproduz(nome, som, cenario, nomeDoArquivo);
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

    public void escondeFragmentEscolhaCenario(){
        FrameLayout frag = (FrameLayout)findViewById(R.id.your_placeholder);
        frag.setVisibility(View.INVISIBLE);
    }

    private void savePreferences(){
        SharedPreferences sharedPreferences = getPreferences(MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();

        editor.putBoolean("nivelEmAndamento", true);
        editor.putInt("currentIndex",currentIndex);
        editor.putString("cenario",cenario);
        editor.putInt("pontos",pontos);
        editor.putInt("bonus",bonus);
        editor.putInt("letraAtual",letraAtual);
        editor.putInt("nLetrasRestantes",nLetrasRestantes);

        //string da palavra em formação
        TextView currentlyShownTextView = (TextView) palavraSwitcher.getCurrentView();
        String s = currentlyShownTextView.getText().toString();
        editor.putString("txtPalavra",s);

        String stringCards2 = TextUtils.join(",", cards2);
        editor.putString("cards2", stringCards2);
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
        currentIndex = sharedPreferences.getInt("currentIndex", 0);
        cenario = sharedPreferences.getString("cenario", null);
        pontos = sharedPreferences.getInt("pontos", 0);
        bonus = sharedPreferences.getInt("bonus", 0);
        letraAtual = sharedPreferences.getInt("letraAtual", 0);
        nLetrasRestantes = sharedPreferences.getInt("nLetrasRestantes", 0);
        palavraSwitcher.setText(sharedPreferences.getString("txtPalavra", ""));

        String stringCards2 = sharedPreferences.getString("cards2", null);
        try{
            cards2 = new ArrayList<String>(Arrays.asList(stringCards2.split(",")));
            count = cards2.size();
        }catch (Exception e){
           e.printStackTrace();
           retornaParaMainActivity(2);
       }
    }

    public void retornaParaMainActivity(int n){
        Toast.makeText(this, "Erro N. 2 - "+String.valueOf(n), Toast.LENGTH_SHORT).show();
        Intent intent = new Intent(Nivel2.this, AprenderPalavras.class);
        startActivity(intent);
    }

    /**
     * Mostra opções de cenário para as imagens
     */
    private void escolheCenario(){
        //Cenário 1: animais
        //Cenário 2: objetos

        //inicializa o array de cartas de acordo com o cenário
        criaArrayDeCartas();

        //inicia o jogo
        proximaImagem();
        nLetrasRestantes = palavra.length();

        //primeiro sorteio de letras
        sorteiaLetrasDosButtons();

        //UI pronta para iniciar
        mostraUI();

    }

    /**
     * cria e insere animações nos botões
     * @param b boolean
     */
    private void animaBotoes(boolean b) {
        if(b) {
            //inicio das animaçoes
            button1.startAnimation(bounceAnim);
            button2.startAnimation(bounceAnim);
            button3.startAnimation(bounceAnim);
            button4.startAnimation(bounceAnim);
            imageView.setAnimation(in);
        }
        else{
            button1.clearAnimation();
            button2.clearAnimation();
            button3.clearAnimation();
            button4.clearAnimation();
        }
    }

    /**
     * adiciona o caminho das imagens contidas no drawable à um array e o mistura
     */
    private void criaArrayDeCartas() {
        String[] images = new String[0];

        try {
            images = getAssets().list(cenario);
        } catch (IOException e) {
            e.printStackTrace();
            retornaParaMainActivity(3);
        }

        cards2 = new ArrayList<>(Arrays.asList(images));
        count = cards2.size();
        Collections.shuffle(cards2);
    }

    /**
     * carrega os efeitos sonoros na memória
     */
    private void setupSoundEffects(){
        mSoundPool = new SoundEffects(Nivel2.this);
        mSounds[ERRADO] = mSoundPool.loadSounds("soundsEffects/errado.wav");
        mSounds[CERTO] = mSoundPool.loadSounds("soundsEffects/certo.wav");
        mSounds[VICTORY] = mSoundPool.loadSounds("soundsEffects/victory.wav");
    }

    /**
     * muda fonte, adiciona cor e fonte aos botões
     */
    private void mudaFonteDosBotoes(){
        Typeface novaFonte = Typeface.createFromAsset(getAssets(),"fonts/ComingSoon.ttf");
        button1.setTypeface(novaFonte);
        button2.setTypeface(novaFonte);
        button3.setTypeface(novaFonte);
        button4.setTypeface(novaFonte);
        button1.setTextSize(35);
        button2.setTextSize(35);
        button3.setTextSize(35);
        button4.setTextSize(35);
        button1.setAllCaps(true);
        button2.setAllCaps(true);
        button3.setAllCaps(true);
        button4.setAllCaps(true);
        button1.setTextColor(Color.BLUE);
        button2.setTextColor(Color.BLUE);
        button3.setTextColor(Color.BLUE);
        button4.setTextColor(Color.BLUE);
        button1.setShadowLayer(1, 1, 1, Color.GRAY);
        button2.setShadowLayer(1, 1, 1, Color.GRAY);
        button3.setShadowLayer(1, 1, 1, Color.GRAY);
        button4.setShadowLayer(1, 1, 1, Color.GRAY);
    }

    /**
     * adiciona animação de transição aos botões e a palavraSwitcher em formação
     */
    private void animaTextSwitchers() {
        //anima a palavraSwitcher sendo formada
        palavraSwitcher = new TextSwitcher(Nivel2.this);
        palavraSwitcher = (TextSwitcher) findViewById(R.id.palavra);
        // provide two TextViews for the TextSwitcher to use
        palavraSwitcher.setFactory(new ViewSwitcher.ViewFactory() {
            @Override
            public View makeView() {
                TextView switcherTextView = new TextView(getApplicationContext());
                //mudar fonte da palavraSwitcher em formação
                Typeface novaFonte = Typeface.createFromAsset(getAssets(),"fonts/ComingSoon.ttf");
                switcherTextView.setTypeface(novaFonte);
                switcherTextView.setTextSize(35);
                switcherTextView.setAllCaps(true);
                switcherTextView.setTextColor(Color.BLUE);
                switcherTextView.setShadowLayer(3, 3, 3, Color.GRAY);
                return switcherTextView;
            }
        });

        //Animation animationOut = AnimationUtils.loadAnimation(this, android.R.anim.slide_in_left);
        Animation animationIn = AnimationUtils.loadAnimation(this, android.R.anim.fade_in);

        //palavraSwitcher.setOutAnimation(animationOut);
        palavraSwitcher.setInAnimation(animationIn);

        //anima os pontos
        pontosSwitcher = new TextSwitcher(Nivel2.this);
        pontosSwitcher = (TextSwitcher) findViewById(R.id.textSwitcher);
        // provide two TextViews for the TextSwitcher to use
        pontosSwitcher.setFactory(new ViewSwitcher.ViewFactory() {
            @Override
            public View makeView() {
                TextView switcherTextView = new TextView(getApplicationContext());
                switcherTextView.setTextSize(24);
                switcherTextView.setTextColor(Color.BLUE);
                switcherTextView.setText("00");
                switcherTextView.setShadowLayer(2, 2, 2, Color.BLACK);
                return switcherTextView;
            }
        });

        Animation animationIn2 = AnimationUtils.loadAnimation(this, android.R.anim.slide_in_left);
        Animation animationout2 = AnimationUtils.loadAnimation(this, android.R.anim.slide_out_right);

        pontosSwitcher.setInAnimation(animationIn2);
        pontosSwitcher.setOutAnimation(animationout2);
    }

    /**
     * pontua 10 pontos no acerto e exibe no pontosSwitcher relativo à pontuação
     */
    private void pontua10(){
        pontos+=10;
        pontosSwitcher.setText(String.valueOf(pontos) + " ");
    }

    /**
     * coloca uma nova imagem na view, caso as imagens acabem, encerra a activity e
     * manda os pontos feitos para a activity que insere os pontos no banco
     */
    private void proximaImagem() {

        if (currentIndex < count) {
            InputStream inputstream = null;

            try {
                inputstream = getApplicationContext().getAssets().open(cenario+File.separator+cards2.get(currentIndex));
            } catch (IOException e) {
                e.printStackTrace();
                retornaParaMainActivity(5);
            }

            imageView.setImageDrawable(roundCorners.arredondarPorInputstream(inputstream));

            if(inputstream!=null) {
                try {
                    inputstream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                    retornaParaMainActivity(7);
                }
            }

            //pega o nome da imagem
            palavra = cards2.get(currentIndex);
            palavra = palavra.replaceAll(".jpeg", "");

            letrasDaPalavra = new StringBuilder(palavra);

            /*
            O incremento do index do array é dado quando o usuário acerta a palavra
             */

        } else {
            //acabou as imagens
            buttonPontos.setVisibility(View.VISIBLE);
            escondeUI();
            ultimaPalavra=true;
        }
    }

    /**
     * o método é chamado no click de um botão para palpitar se a letra desse botão é a próxima letra da palavraSwitcher.
     */
    @NonNull
    private Boolean escolheLetra(char letra){
        return letra == letrasDaPalavra.charAt(letraAtual);
    }

    /**
     * insere letras nos botões
     */
    private void sorteiaLetrasDosButtons(){
        button1.setText(String.valueOf(randomizaChar()));
        button2.setText(String.valueOf(randomizaChar()));
        button3.setText(String.valueOf(randomizaChar()));
        button4.setText(String.valueOf(randomizaChar()));

        //colocar a letra certa em um dos botões
        if(procuraLetraRepetida()) {
            Random r = new Random();
            switch (r.nextInt(4) + 1) {//4 é a quantidade de botões
                case 1:
                    button1.setText(String.valueOf(letrasDaPalavra.charAt(letraAtual)));
                    break;
                case 2:
                    button2.setText(String.valueOf(letrasDaPalavra.charAt(letraAtual)));
                    break;
                case 3:
                    button3.setText(String.valueOf(letrasDaPalavra.charAt(letraAtual)));
                    break;
                case 4:
                    button4.setText(String.valueOf(letrasDaPalavra.charAt(letraAtual)));
                    break;
                default:
                    Toast.makeText(getApplicationContext(), "ocorreu um erro", Toast.LENGTH_SHORT).show();
                    break;
            }
        }

    }

    /**
     * usado para garantir que não há repetição de letras nos botões
     * @return char não repetido
     */
    @NonNull
    private Boolean procuraLetraRepetida(){
        return     letrasDaPalavra.charAt(letraAtual) != button1.getText().charAt(0) && letrasDaPalavra.charAt(letraAtual) != button2.getText().charAt(0)
                && letrasDaPalavra.charAt(letraAtual) != button3.getText().charAt(0) && letrasDaPalavra.charAt(letraAtual) != button4.getText().charAt(0);
    }

    /**
     * randomiza um char com base nas letras permitidas na String do alfabeto
     * @return char aleatório
     */
    private char randomizaChar(){
        //a maneira usada para nao haver repetição é sempre que uma letra é atribuida a um button, ela é removida do alfabeto
        //quando o usuario acerta a letra da vez, o alfabeto é renovado
        int N = alfabeto.length();
        Random r = new Random();
        char c = alfabeto.charAt(r.nextInt(N));
        alfabeto = alfabeto.replaceAll(String.valueOf(c),"");
        return  c;
    }

    /**
     * Insere a letra acertada pelo usuário na palavraSwitcher em formação
     */
    private void atualizaTextView(){
        TextView currentlyShownTextView = (TextView) palavraSwitcher.getCurrentView();
        String s = currentlyShownTextView.getText().toString();
        //as strings vazias são para dar um espaço entre as letras
        s = letraAtual > 0 ?  s + String.valueOf(letrasDaPalavra.charAt(letraAtual-1)+ " ") : s + String.valueOf(letrasDaPalavra.charAt(letraAtual)+ " ");
        palavraSwitcher.setText(s);
    }

    /**
     * caso a palavraSwitcher seja formada, insere uma nova imagem e reseta a palavraSwitcher em formação
     */
    private void eventoDoFimDaPalavra(){
        //incrementa o index do array
        currentIndex++;
        letraAtual=0;

        palavraSwitcher.setText("");
        proximaImagem();
        sorteiaLetrasDosButtons();

        nLetrasRestantes = palavra.length();
        //mostra os botões
        fadeAnim.reverse();
        mostraBotoes(true);
        setButtonsClicaveis(true);
        home.setVisibility(View.VISIBLE);

    }

    /**
     * controla vários métodos que mudam o conteúdo da activity quando uma letra é acertada
     */
    private void atualizaInterface(){
        if(nLetrasRestantes > 1){
            //incremento no índice das letras
            letraAtual++;
            //exibir char no palavraSwitcher
            atualizaTextView();
            nLetrasRestantes--;
            //randomizar letras dos botoes novamente
            sorteiaLetrasDosButtons();
        }else{
            setButtonsClicaveis(false);//evitar que os botões sejam apertados depois que terminar a palavra
            fadeAnim.setDuration(500);
            fadeAnim.start();
            home.setVisibility(View.INVISIBLE);//evitar que salve durante o evento de som

            letraAtual++;
            atualizaTextView();

            String s = cards2.get(currentIndex);
            s = s.replaceAll(".jpeg", "");
            reproduz(prefMic, prefSoundEffects, s);

            //pausa antes de iniciar novas imagens
            new CountDownTimer(4000, 1000) {
                public void onTick(long millisUntilFinished) {}
                public void onFinish() {
                    if(prefSoundEffects) mSoundPool.playSound(mSounds[VICTORY], 0.1f,0.1f);
                    //incremento no array de imagens
                    eventoDoFimDaPalavra();
                }
            }.start();
        }
        //renova alfabeto
        alfabeto = "abcdefghijlmnopqrstuvxz";
    }

    public void mostraBotoes(boolean b){
        if(b){
            animaBotoes(b);
            button1.setVisibility(View.VISIBLE);
            button2.setVisibility(View.VISIBLE);
            button3.setVisibility(View.VISIBLE);
            button4.setVisibility(View.VISIBLE);
        }else{
            animaBotoes(b);
            button1.setVisibility(View.INVISIBLE);
            button2.setVisibility(View.INVISIBLE);
            button3.setVisibility(View.INVISIBLE);
            button4.setVisibility(View.INVISIBLE);
        }
    }

    private void escondeUI(){
        animaBotoes(false);
        fase2Content.setVisibility(View.INVISIBLE);
    }

    private void mostraUI(){
        animaBotoes(true);
        fase2Content.setVisibility(View.VISIBLE);

    }

    private void ativaMusicaDeFundo() {
        soundPlayer = new SoundPlayer(Nivel2.this);

        if(prefMusic) {
            mpBackgroundMusic = MediaPlayer.create(Nivel2.this, R.raw.maintheme);
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

    private void setButtonsClicaveis(boolean b){
        if (b) {
            button1.setClickable(true);
            button2.setClickable(true);
            button3.setClickable(true);
            button4.setClickable(true);
        }else{
            button1.setClickable(false);
            button2.setClickable(false);
            button3.setClickable(false);
            button4.setClickable(false);
        }

    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent(Nivel2.this, AprenderPalavras.class);
        startActivity(intent);
        super.onBackPressed();
    }

    @Override
    protected void onResume() {
            setupSoundEffects();
            ativaMusicaDeFundo();

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
        soundPlayer.release();
        if (mpBackgroundMusic !=null) mpBackgroundMusic.release();

        super.onStop();
    }

    @Override
    protected void onDestroy() {
        if(mSoundPool!=null) mSoundPool.release();

        super.onDestroy();
    }
}
