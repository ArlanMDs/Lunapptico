package br.com.gerqs.lunapptico.soundManager;

import android.content.Context;
import android.content.res.AssetFileDescriptor;
import android.media.AudioManager;
import android.media.SoundPool;
import android.os.Build;
import android.util.Log;

import java.io.IOException;

public class SoundEffects {
   private static final int MAX_SOUNDS = 16;
    private Context mContext = null;
    private SoundPool mSoundPool; //= new SoundPool(MAX_SOUNDS, AudioManager.STREAM_MUSIC,0);

    public SoundEffects(Context context){
        mContext = context;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            mSoundPool = new SoundPool.Builder()
                    .setMaxStreams(MAX_SOUNDS)
                    .build();
        } else {
            mSoundPool = new SoundPool(MAX_SOUNDS, AudioManager.STREAM_MUSIC, 1);
        }
    }

    public int loadSounds(String filename){
        try{
            AssetFileDescriptor descriptor;
            descriptor = mContext.getAssets().openFd(filename);
            return mSoundPool.load(descriptor,1);
        }catch (IOException e){
            String stringBuilder = "SoundPlayer.loadSound(): arquivo " +
                    filename +
                    " não encontrado!";

            Log.d("e", stringBuilder);

            return -1;
        }
    }

    public void playSound(int soundId, float volumeLeft, float volumeRight){
        mSoundPool.play(soundId,volumeLeft,volumeRight,0, 0, (float) 1);
    }

    public void unloadSound(int soundId){
        mSoundPool.unload(soundId);
    }

    public void release(){
        if(mSoundPool!=null){
            mSoundPool.release();
            mSoundPool=null;
        }
    }
}

