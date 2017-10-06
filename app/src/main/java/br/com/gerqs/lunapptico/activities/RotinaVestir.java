package br.com.gerqs.lunapptico.activities;

import android.annotation.SuppressLint;
import android.content.ClipData;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.media.MediaPlayer;
import android.os.CountDownTimer;
import android.os.PowerManager;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.DragEvent;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import br.com.gerqs.lunapptico.R;
import br.com.gerqs.lunapptico.soundManager.SoundEffects;

public class RotinaVestir extends AppCompatActivity implements View.OnClickListener {
    private ImageView pessoa, camisaDrop, calcaDrop, sapato1Drop, sapato2Drop, camisaDrag, calcaDrag, sapato1Drag, sapato2Drag;
    private MediaPlayer mpBackgroundMusic;
    private int flag, mSounds[] = new int[3];
    private SoundEffects mSoundPool;
    private final static int ERRADO = 0, CERTO = 1, VICTORY = 2;
    private boolean prefMusic, prefSoundEffects, prefMic;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rotina_vestir);

        findViewsIds();
        escondeDropTarget();

        //busca preferência sobre efeitos sonoros
        final SharedPreferences mSharedPreference= PreferenceManager.getDefaultSharedPreferences(getBaseContext());
        prefSoundEffects = mSharedPreference.getBoolean("prefSoundEffects", true);  //default is true
        prefMic = (mSharedPreference.getBoolean("prefMic", true));
        prefMusic = (mSharedPreference.getBoolean("prefMusic", true));

    }

    private void escondeDropTarget() {
        camisaDrop.setAlpha(0f);
        calcaDrop.setAlpha(0f);
        sapato1Drop.setAlpha(0f);
        sapato2Drop.setAlpha(0f);

    }

    private void findViewsIds() {
        pessoa = (ImageView) findViewById(R.id.pessoa);
        camisaDrop = (ImageView) findViewById(R.id.camisa);
        calcaDrop = (ImageView) findViewById(R.id.calca);
        sapato1Drop = (ImageView) findViewById(R.id.sapato1);
        sapato2Drop = (ImageView) findViewById(R.id.sapato2);
        camisaDrag = (ImageView) findViewById(R.id.camisaDrag);
        calcaDrag = (ImageView) findViewById(R.id.calcaDrag);
        sapato1Drag = (ImageView) findViewById(R.id.sapato1Drag);
        sapato2Drag = (ImageView) findViewById(R.id.sapato2Drag);


        //início dos drags
        camisaDrag.setOnTouchListener(new RotinaVestir.ChoiceTouchListener());
        calcaDrag.setOnTouchListener(new RotinaVestir.ChoiceTouchListener());
        sapato1Drag.setOnTouchListener(new RotinaVestir.ChoiceTouchListener());
        sapato2Drag.setOnTouchListener(new RotinaVestir.ChoiceTouchListener());

        //início dos drops
        camisaDrop.setOnDragListener(new RotinaVestir.ChoiceDragListener());
        calcaDrop.setOnDragListener(new RotinaVestir.ChoiceDragListener());
        sapato1Drop.setOnDragListener(new RotinaVestir.ChoiceDragListener());
        sapato2Drop.setOnDragListener(new RotinaVestir.ChoiceDragListener());

        Button menino = (Button)findViewById(R.id.buttonMenino);
        menino.setOnClickListener(this);
        Button menina = (Button)findViewById(R.id.buttonMenina);
        menina.setOnClickListener(this);
        Button home = (Button)findViewById(R.id.buttonVestirHome);
        home.setOnClickListener(this);


    }

    @Override
    public void onClick(View v) {

        switch(v.getId()){
            case R.id.buttonMenino:
                resetViews();
                pessoa.setImageResource(R.drawable.garoto);

                break;
            case R.id.buttonMenina:
                resetViews();
                pessoa.setImageResource(R.drawable.garota);

                break;

            case R.id.buttonVestirHome:
                Intent intent = new Intent(RotinaVestir.this, Rotinas.class);
                startActivity(intent);

                break;

        }
    }

    private final class ChoiceTouchListener implements View.OnTouchListener {
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
    private class ChoiceDragListener implements View.OnDragListener {

        @Override
        public boolean onDrag(View v, DragEvent event) {
            switch (event.getAction()) {
                case DragEvent.ACTION_DRAG_STARTED:
                    //no action necessary
                    break;
                case DragEvent.ACTION_DRAG_ENTERED:
                    //no action necessary
                    break;
                case DragEvent.ACTION_DRAG_EXITED:
                    //no action necessary
                    break;
                case DragEvent.ACTION_DROP:

                    //handle the dragged view being dropped over a drop view
                    final View view = (View) event.getLocalState();

                    //view dragged item is being dropped on
                    ImageView dropTarget = (ImageView) v;
                    //view being dragged and dropped
                    ImageView dropped = (ImageView) view;
                    Bitmap bitmap = ((BitmapDrawable)dropTarget.getDrawable()).getBitmap();
                    Bitmap bitmap2 = ((BitmapDrawable)dropped.getDrawable()).getBitmap();

                    //checa se sao iguais
                    if(bitmap.sameAs(bitmap2)){
                        if (prefSoundEffects) mSoundPool.playSound(mSounds[CERTO], 0.1f, 0.1f);

                        //stop displaying the view where it was before it was dragged
                        view.setVisibility(View.INVISIBLE);
                        //deixa o drop visível
                        dropTarget.setAlpha(1.0f);
                        //if an item has already been dropped here, there will be a tag
                        Object tag = dropTarget.getTag();
                        //if there is already an item here, set it back visible in its original place
                        if(tag!=null)
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

                        //a flag que indica se as imagens foram arrastadas com sucesso
                        flag++;
                        if(flag==4){
                            flag = 0;
                            if (prefSoundEffects) mSoundPool.playSound(mSounds[VICTORY], 0.1f, 0.1f);

                            //pausa antes do reset
                            new CountDownTimer(2000, 1000) {
                                public void onTick(long millisUntilFinished) {
                                }
                                public void onFinish() {
                                    resetViews();
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

    /**
     * carrega os efeitos sonoros na memória
     */
    private void setup(){
        mSoundPool = new SoundEffects(RotinaVestir.this);
        mSounds[ERRADO] = mSoundPool.loadSounds("soundsEffects/errado.wav");
        mSounds[CERTO] = mSoundPool.loadSounds("soundsEffects/certo.wav");
        mSounds[VICTORY] = mSoundPool.loadSounds("soundsEffects/victory.wav");
    }

    /**
     * reseta o estado dos drags e drops para o estado inicial
     */
    private void resetViews(){
        calcaDrag.setVisibility(ImageView.VISIBLE);
        camisaDrag.setVisibility(ImageView.VISIBLE);
        sapato1Drag.setVisibility(ImageView.VISIBLE);
        sapato2Drag.setVisibility(ImageView.VISIBLE);

        escondeDropTarget();

        calcaDrop.setTag(null);
        camisaDrop.setTag(null);
        sapato1Drop.setTag(null);
        sapato2Drop.setTag(null);

        calcaDrop.setOnDragListener(new ChoiceDragListener());
        camisaDrop.setOnDragListener(new ChoiceDragListener());
        sapato1Drop.setOnDragListener(new ChoiceDragListener());
        sapato2Drop.setOnDragListener(new ChoiceDragListener());

    }

    private void ativaMusicaDeFundo() {
        if(prefMusic) {
            mpBackgroundMusic = MediaPlayer.create(RotinaVestir.this, R.raw.maintheme);
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
    @Override
    protected void onResume() {
        ativaMusicaDeFundo();
        setup();

        super.onResume();
    }

    @Override
    protected void onPause() {

        if(mpBackgroundMusic != null) if (mpBackgroundMusic.isPlaying()) mpBackgroundMusic.pause();

        super.onPause();
    }

    protected void onStop() {

        if(mSoundPool!=null) mSoundPool.release();
        if(mpBackgroundMusic !=null) mpBackgroundMusic.release();

        super.onStop();
    }
}
