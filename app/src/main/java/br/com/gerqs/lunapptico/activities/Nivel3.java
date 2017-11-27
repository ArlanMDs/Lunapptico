package br.com.gerqs.lunapptico.activities;

import static com.nineoldandroids.view.ViewPropertyAnimator.animate;

import android.annotation.SuppressLint;
import android.content.ClipData;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.media.MediaPlayer;
import android.os.CountDownTimer;
import android.os.PowerManager;
import android.preference.PreferenceManager;
import android.support.constraint.ConstraintLayout;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.DragEvent;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;
import android.view.View.OnDragListener;
import android.view.View.OnTouchListener;
import com.nineoldandroids.animation.Animator;
import com.nineoldandroids.view.ViewPropertyAnimator;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;


import br.com.gerqs.lunapptico.R;
import br.com.gerqs.lunapptico.interpolator.ReverseInterpolator;
import br.com.gerqs.lunapptico.fragmentController.FragmentEscolhaCenario;
import br.com.gerqs.lunapptico.soundManager.SoundPlayer;
import br.com.gerqs.lunapptico.tools.FisherYates;
import br.com.gerqs.lunapptico.tools.RoundCorners;
import br.com.gerqs.lunapptico.soundManager.SoundEffects;

public class Nivel3 extends AppCompatActivity implements FragmentEscolhaCenario.InterfaceComunicao{
    private TextView drag1, drag2, drag3, drop1, drop2, drop3, mTextField;
    private ImageView imagem1, imagem2, imagem3;
    private ArrayList<String> cards3;
    private int flag, pontos, bonus, mSounds[] = new int[3], currentIndex, count, tempoDeEsperaParaProximasImagens;
    private SoundEffects mSoundPool;
    private final static int ERRADO = 0, CERTO = 1, VICTORY = 2;
    private MediaPlayer mpBackgroundMusic, mpTutorial;
    private boolean prefSoundEffects;
    private boolean prefMic;
    private boolean prefMusic;
    private boolean exibeTutorial;
    private ConstraintLayout fase3Content;
    private ImageView mao1, mao2, mao3;
    private String cenario;
    private RoundCorners roundCorners;
    private SoundPlayer soundPlayer;
    private Button home;

    @SuppressLint("ClickableViewAccessibility")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fase3);

        //início das capturas
        findViewsIds();

        //busca preferência sobre efeitos sonoros
        final SharedPreferences mSharedPreference= PreferenceManager.getDefaultSharedPreferences(getBaseContext());
        prefSoundEffects = mSharedPreference.getBoolean("prefSoundEffects", true);  //default is true
        prefMic = (mSharedPreference.getBoolean("prefMic", true));
        prefMusic = (mSharedPreference.getBoolean("prefMusic", true));

        //mais tempo é necessário se o narrador e efeitos sonoros estiverem ativos,
        //para não passar para as próximas imagens enquanto algum som está sendo executado
        tempoDeEsperaParaProximasImagens = 1000;
        if(prefSoundEffects) tempoDeEsperaParaProximasImagens += 1500;
        if(prefMic) tempoDeEsperaParaProximasImagens += 1000;

        //preferencia de estado da fase (em progresso ou não)//TODO rever global/local
        SharedPreferences sharedPreferences = getPreferences(MODE_PRIVATE);
        boolean nivelEmAndamento = sharedPreferences.getBoolean("nivelEmAndamento", false);
        exibeTutorial = !nivelEmAndamento;

        if (nivelEmAndamento)
            continuarDialog();
        else
            restauraActivityParaEstadoInicial();

        //início do player de efeitos sonoros
        setup();

        //início do timer do bonus
        mTextField = (TextView)findViewById(R.id.mTextField);
        new CountDownTimer(100000, 1000) {
            public void onTick(long millisUntilFinished) {
                if(bonus > 0) bonus -= 1;

                mTextField.setText("Bônus restante: " + bonus);

                }
            public void onFinish() {
                mTextField.setText("Acabou o bônus!");
            }
        }.start();

        home.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                savePreferences();
                Intent intent =  new Intent(Nivel3.this, AprenderPalavras.class);
                startActivity(intent);
            }
        });

    }

    private void continuarDialog() {

        new AlertDialog.Builder(Nivel3.this)
                .setMessage("Deseja continuar de onde parou?")
                .setCancelable(false)
                .setPositiveButton("Sim", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which)
                    {

                        loadPreferences();
                        escondeFragmentEscolhaCenario();
                        proximasImagens();
                        mostraUI();

                        dialog.cancel();

                    }
                })
                .setNegativeButton("Não", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which)
                    {
                        exibeTutorial = true;
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

        //Esconde o layout principal para a escolha do cenário
        escondeUI();
        bonus = 100;
        //a flag serve de sinal para quando as 3 palavras forem arrastadas corretamente
        flag = 0;
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        // Replace the contents of the container with the new fragment
        FragmentEscolhaCenario fragmentEscolhaCenario = new FragmentEscolhaCenario();
        ft.replace(R.id.your_placeholder, fragmentEscolhaCenario);
        ft.commit();
    }


    private void findViewsIds() {
        drag1 = (TextView)findViewById(R.id.drag1);
        drag2 = (TextView)findViewById(R.id.drag2);
        drag3 = (TextView)findViewById(R.id.drag3);
        drag1.setAllCaps(true);
        drag2.setAllCaps(true);
        drag3.setAllCaps(true);
        drop1 = (TextView)findViewById(R.id.drop1);
        drop2 = (TextView)findViewById(R.id.drop2);
        drop3 = (TextView)findViewById(R.id.drop3);
        drop1.setAllCaps(true);
        drop2.setAllCaps(true);
        drop3.setAllCaps(true);
        imagem1 = (ImageView)findViewById(R.id.imageView2);
        imagem2 = (ImageView)findViewById(R.id.imageView3);
        imagem3 = (ImageView)findViewById(R.id.imageView4);
        fase3Content = (ConstraintLayout) findViewById(R.id.fase3Content);
        mao1 = (ImageView) findViewById(R.id.mao1);
        mao2 = (ImageView) findViewById(R.id.mao2);
        mao3 = (ImageView) findViewById(R.id.mao3);
        roundCorners = new RoundCorners(Nivel3.this, getResources());
        home = (Button)findViewById(R.id.fase3Home);

        //início dos drags
        drag1.setOnTouchListener(new ChoiceTouchListener());
        drag2.setOnTouchListener(new ChoiceTouchListener());
        drag3.setOnTouchListener(new ChoiceTouchListener());

        //início dos drops
        drop1.setOnDragListener(new ChoiceDragListener());
        drop2.setOnDragListener(new ChoiceDragListener());
        drop3.setOnDragListener(new ChoiceDragListener());

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
        editor.putString("cenario",cenario);
        editor.putInt("pontos", pontos);
        editor.putInt("bonus", bonus);
        editor.putInt("currentIndex", currentIndex);
        String stringCards3 = TextUtils.join(",", cards3);
        editor.putString("cards3", stringCards3);
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

        cenario = sharedPreferences.getString("cenario", null);
        pontos = sharedPreferences.getInt("pontos", 0);
        bonus = sharedPreferences.getInt("bonus", 100);
        currentIndex = sharedPreferences.getInt("currentIndex", 0);

        String stringCards3 = sharedPreferences.getString("cards3", null);
        if(stringCards3 != null) cards3 = new ArrayList<String>(Arrays.asList(stringCards3.split(",")));
        count = cards3.size();

    }

    /**
     * Mostra opções de cenário para as imagens
     */
    private void escolheCenario() {
        //Cenário 1: Animais
        //Cenário 2: objetos

        //inicializa o array de cartas de acordo com o cenário
        criaArrayDeCartas();

        mostraUI();

        if (exibeTutorial) {
            mostraTutorial();
        }else{
            //inicia o jogo
            proximasImagens();
        }

    }

    public void mostraTutorial(){
        proximasImagens();
        drag1.setVisibility(View.INVISIBLE);
        drag2.setVisibility(View.INVISIBLE);
        drag3.setVisibility(View.INVISIBLE);
        //0 1 2
        //2 0 1
        mpTutorial = MediaPlayer.create(Nivel3.this, R.raw.somtutorial);


        if(prefMic){
            mpTutorial.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                @Override
                public void onCompletion(MediaPlayer mp) {
                    mpTutorial.release();
                    mpTutorial = null;
                }
            });
            mpTutorial.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                @Override
                public void onPrepared(MediaPlayer mp) {
                    mp.start();
                }
            });
        }
        animacaiMao1();
    }

    private void animacaiMao1(){
        mao1.setVisibility(View.VISIBLE);
        final int[] locationImagem3 = new int[2];
        imagem3.getLocationOnScreen(locationImagem3);

        animate(mao1).x(locationImagem3[0]*1.1f).y(locationImagem3[1]*1.5f).setDuration(4000).setListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {
                drag1.setVisibility(View.VISIBLE);

            }

            @Override
            public void onAnimationEnd(Animator animation) {
                mao1.setVisibility(View.GONE);
                animacaoMao2();

            }

            @Override
            public void onAnimationCancel(Animator animation) {

            }

            @Override
            public void onAnimationRepeat(Animator animation) {

            }
        }).start();
    }

    private void animacaoMao2(){
        mao2.setVisibility(View.VISIBLE);
        final int[] locationImagem2 = new int[2];
        imagem1.getLocationOnScreen(locationImagem2);

        animate(mao2).x(locationImagem2[0]*2.8f).y(locationImagem2[1]*1.7f).setDuration(4000).setListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {
                drag2.setVisibility(View.VISIBLE);

            }

            @Override
            public void onAnimationEnd(Animator animation) {
                mao2.setVisibility(View.GONE);
                animacaoMao3();

            }

            @Override
            public void onAnimationCancel(Animator animation) {

            }

            @Override
            public void onAnimationRepeat(Animator animation) {

            }
        }).start();
    }

    private void animacaoMao3(){
        mao3.setVisibility(View.VISIBLE);
        final int[] locationImagem2 = new int[2];
        imagem2.getLocationOnScreen(locationImagem2);

        animate(mao3).x(locationImagem2[0]*1.2f).y(locationImagem2[1]*1.5f).setDuration(4000).setListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {
                drag3.setVisibility(View.VISIBLE);
                exibeTutorial = false;

            }

            @Override
            public void onAnimationEnd(Animator animation) {
                mao3.setVisibility(View.GONE);
            }

            @Override
            public void onAnimationCancel(Animator animation) {
                mao3.clearAnimation();
            }

            @Override
            public void onAnimationRepeat(Animator animation) {

            }
        }).start();
    }

    private void criaArrayDeCartas() {
        String[] images = new String[0];

        try {
            images = getAssets().list(cenario);
        } catch (IOException e) {
            e.printStackTrace();
        }

        cards3 = new ArrayList<>(Arrays.asList(images));
        count = cards3.size();
        Collections.shuffle(cards3);
    }

    /**
     * carrega os efeitos sonoros na memória
     */
    private void setup(){
        mSoundPool = new SoundEffects(Nivel3.this);
        mSounds[ERRADO] = mSoundPool.loadSounds("soundsEffects/errado.wav");
        mSounds[CERTO] = mSoundPool.loadSounds("soundsEffects/certo.wav");
        mSounds[VICTORY] = mSoundPool.loadSounds("soundsEffects/victory.wav");
    }

    private final class ChoiceTouchListener implements OnTouchListener {
        @SuppressLint("NewApi")
        @Override
        public boolean onTouch(View view, MotionEvent motionEvent) {
            if (motionEvent.getAction() == MotionEvent.ACTION_DOWN) {
            /*
             * Drag details: we only need default behavior
             * - clip data could be set to pass data as part of drag
             * - shadow can be tailored
             */
                ClipData data = ClipData.newPlainText("", "");
                View.DragShadowBuilder shadowBuilder = new View.DragShadowBuilder(view);
                //start dragging the item touched
                //noinspection deprecation
                view.startDrag(data, shadowBuilder, view, 0);

                return true;
            } else {
                return false;
            }
        }
    }

    /**
     * DragListener will handle dragged views being dropped on the drop area
     * - only the drop action will have processing added to it as we are not
     * - amending the default behavior for other parts of the drag process
     *
     */
    @SuppressLint("NewApi")
    private class ChoiceDragListener implements OnDragListener {

        @Override
        public boolean onDrag(View v, DragEvent event) {
            final View view = (View) event.getLocalState();
            TextView dropped = (TextView) view;

            switch (event.getAction()) {
                case DragEvent.ACTION_DRAG_STARTED:

                    //reproduz o nome do animal
                    reproduz(prefMic, prefSoundEffects, dropped.getText().toString());

                    break;
                case DragEvent.ACTION_DRAG_ENTERED:
                    //no action necessary
                    break;
                case DragEvent.ACTION_DRAG_EXITED:
                    //no action necessary
                    break;
                case DragEvent.ACTION_DROP:

                    //handle the dragged view being dropped over a drop view
                    //final View view = (View) event.getLocalState();
                    //view dragged item is being dropped on
                    TextView dropTarget = (TextView) v;
                    //view being dragged and dropped

                    //checa se a string do dropTarget é igual ao do dropped
                    if(dropTarget.getText().toString().contains(dropped.getText().toString())){
                        //pontua
                        pontos += 10;
                        ViewPropertyAnimator.animate(dropTarget).cancel();
                        dropTarget.setAlpha(1);
                        //stop displaying the view where it was before it was dragged
                        view.setVisibility(View.INVISIBLE);
                        //deixa o texto do drop visível
                        dropTarget.setTypeface(Typeface.DEFAULT_BOLD);
                        dropTarget.setTextColor(ContextCompat.getColor(getApplicationContext(), R.color.colorPrimaryDark));
                        //if an item has already been dropped here, there will be a tag
                        Object tag = dropTarget.getTag();
                        //if there is already an item here, set it back visible in its original place
                        if(tag != null)
                        {
                            //the tag is the view id already dropped here
                            int existingID = (Integer)tag;
                            //set the original view visible again
                            findViewById(existingID).setVisibility(View.VISIBLE);
                        }
                        //set the tag in the target view being dropped on - to the ID of the view being dropped
                        dropTarget.setTag(dropped.getId());
                        //remove setOnDragListener by setting OnDragListener to null, so that no further drag & dropping on this TextView can be done
                        dropTarget.setOnDragListener(null);

                        //a flag que indica se as 3 imagens foram arrastadas com sucesso
                        flag++;
                        if(flag==3){

                            home.setVisibility(View.INVISIBLE);
                            mao3.clearAnimation();
                            mao3.setVisibility(View.GONE);

                            flag = 0;

                            //pausa antes de iniciar novas imagens
                                new CountDownTimer(tempoDeEsperaParaProximasImagens, 1000) {
                                    public void onTick(long millisUntilFinished) {
                                    }
                                    public void onFinish() {
                                        if (prefSoundEffects) mSoundPool.playSound(mSounds[VICTORY], 0.1f, 0.1f);
                                        resetViews();
                                        //incremento no array de imagens
                                        currentIndex += 3;
                                        home.setVisibility(View.VISIBLE);
                                        proximasImagens();
                                    }
                                }.start();

                        }
                    }
                    else
                        //dropTarget is not equal of dropped
                        if(prefSoundEffects) mSoundPool.playSound(mSounds[ERRADO], 0.1f,0.1f);
                    break;
                case DragEvent.ACTION_DRAG_ENDED:

                    break;
                default:
                    break;
            }
            return true;
        }
    }

    private void reproduz(boolean nome, boolean som, final String nomeDoArquivo) {
        if(cenario == "objects") soundPlayer.reproduz(nome, false, cenario, nomeDoArquivo);
        else soundPlayer.reproduz(nome, som, cenario, nomeDoArquivo);
    }

    private void proximasImagens(){
        if(count - currentIndex > 2){//caso ainda tenha 3 imagens//
            InputStream inputstream1 = null;
            InputStream inputstream2 = null;
            InputStream inputstream3 = null;
            //TODO um método que receba o imageview como argumento pode diminuir o tamanho disso

                try {
                    inputstream1 = getApplicationContext().getAssets().open(cenario+File.separator+cards3.get(currentIndex));
                    inputstream2 = getApplicationContext().getAssets().open(cenario+File.separator+cards3.get(currentIndex+1));
                    inputstream3 = getApplicationContext().getAssets().open(cenario+File.separator+cards3.get(currentIndex+2));
                } catch (IOException e) {
                    e.printStackTrace();
                }

            imagem1.setImageDrawable(roundCorners.arredondarPorInputstream(inputstream1));
            imagem2.setImageDrawable(roundCorners.arredondarPorInputstream(inputstream2));
            imagem3.setImageDrawable(roundCorners.arredondarPorInputstream(inputstream3));

            if(inputstream1!=null) {
                try {
                    inputstream1.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if(inputstream2!=null) {
                try {
                    inputstream2.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if(inputstream3!=null) {
                try {
                    inputstream3.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

            insereNomeDosAnimaisNosTextViews();

        }else{
            //fim do array de imagens
            if (mpBackgroundMusic != null){
                mpBackgroundMusic.release();
                mpBackgroundMusic = null;
            }
            resetPreferences();
            Intent intent = new Intent(Nivel3.this, TelaPontuacao.class);
            intent.putExtra("pontos", pontos);
            intent.putExtra("bonus", bonus);
            startActivity(intent);
        }
    }

    private void insereNomeDosAnimaisNosTextViews(){
        //obtem o nome da palavra do card
        String palavra1 = cards3.get(currentIndex);
        palavra1 = palavra1.replaceAll(".jpeg", "");

        String palavra2 = cards3.get(currentIndex+1);
        palavra2 = palavra2.replaceAll(".jpeg", "");

        String palavra3 = cards3.get(currentIndex+2);
        palavra3 = palavra3.replaceAll(".jpeg", "");

        //coloca o nome das imagens nos textviews transparentes
        drop1.setText(palavra1);
        drop2.setText(palavra2);
        drop3.setText(palavra3);
        //animação de aparecer lentamente
        animaDrops();

        if(exibeTutorial) {
            drag1.setText(cards3.get(2).replace(".jpeg", ""));
            drag2.setText(cards3.get(0).replace(".jpeg", ""));
            drag3.setText(cards3.get(1).replace(".jpeg", ""));
        }else{
            //aleatoriamente coloca os nomes nos textViews que irão ser arrastados
            int[] array = {0, 1, 2};
            FisherYates fisherYates = new FisherYates();
            array = fisherYates.shuffle(array);
            drag1.setText(cards3.get(currentIndex + array[0]).replace(".jpeg", ""));
            drag2.setText(cards3.get(currentIndex + array[1]).replace(".jpeg", ""));
            drag3.setText(cards3.get(currentIndex + array[2]).replace(".jpeg", ""));
            //O incremento no currentIndex é dado após o usuário acertar as 3 palavras
        }
    }

    /**
     * Animação para a palavra aparecer lentamente durante o jogo
     */
    private void animaDrops(){
        ViewPropertyAnimator.animate(drop1).alpha(0).setInterpolator(new ReverseInterpolator()).setDuration(300000).start();
        ViewPropertyAnimator.animate(drop2).alpha(0).setInterpolator(new ReverseInterpolator()).setDuration(300000).start();
        ViewPropertyAnimator.animate(drop3).alpha(0).setInterpolator(new ReverseInterpolator()).setDuration(300000).start();

    }

    /**
     * reseta o estado dos drags e drops para o estado inicial
     */
    private void resetViews(){

        drag1.setVisibility(TextView.VISIBLE);
        drag2.setVisibility(TextView.VISIBLE);
        drag3.setVisibility(TextView.VISIBLE);

        drop1.setTag(null);
        drop2.setTag(null);
        drop3.setTag(null);

        drop1.setText(null);
        drop2.setText(null);
        drop3.setText(null);

        drop1.setTypeface(Typeface.DEFAULT);
        drop2.setTypeface(Typeface.DEFAULT);
        drop3.setTypeface(Typeface.DEFAULT);

        drop1.setOnDragListener(new ChoiceDragListener());
        drop2.setOnDragListener(new ChoiceDragListener());
        drop3.setOnDragListener(new ChoiceDragListener());


    }

    private void ativaMusicaDeFundo() {
        soundPlayer = new SoundPlayer(Nivel3.this);
        if(prefMusic) {
            mpBackgroundMusic = MediaPlayer.create(Nivel3.this, R.raw.maintheme);
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

    private void escondeUI(){
        fase3Content.setVisibility(View.INVISIBLE);
    }

    private void mostraUI(){
       // if(cenario == 1) fase3Layout.setBackgroundResource(R.drawable.floresta);
       // else if(cenario == 2) fase3Layout.setBackgroundResource(R.drawable.fazenda);
        fase3Content.setVisibility(View.VISIBLE);

    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent(Nivel3.this, AprenderPalavras.class);
        startActivity(intent);
        super.onBackPressed();
    }

    @Override
    protected void onResume() {
        ativaMusicaDeFundo();
        setup();

        super.onResume();
    }

    @Override
    protected void onPause() {
        soundPlayer.stop();
        if(mpBackgroundMusic != null) if (mpBackgroundMusic.isPlaying()) mpBackgroundMusic.pause();

        super.onPause();
    }

    protected void onStop() {
        if(mSoundPool!=null) mSoundPool.release();
        soundPlayer.release();
        if(mpBackgroundMusic !=null) mpBackgroundMusic.release();
        if(mpTutorial !=null) mpTutorial.release();

        super.onStop();
    }

    @Override
    protected void onDestroy() {
        if(mSoundPool != null) mSoundPool.release();
        if(mpBackgroundMusic != null) mpBackgroundMusic.release();
        if(mpTutorial !=null) mpTutorial.release();

        super.onDestroy();
    }

}
