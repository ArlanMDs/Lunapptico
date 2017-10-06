package br.com.gerqs.lunapptico.soundManager;

import android.content.Context;
import android.content.res.AssetFileDescriptor;
import android.media.MediaPlayer;
import android.widget.Toast;

import java.io.File;

public class SoundPlayer {
    private MediaPlayer mp;
    private Context mContext;

    public SoundPlayer(Context context){
        mp = new MediaPlayer();
        mContext = context;
    }

    public void reproduz(final boolean reproduzirNome, final boolean reproduzirSom, final String cenario, final String nomeDoArquivo) {
       if(reproduzirNome) {

           mp.reset();
           String pasta = cenario + "NamesSounds";
           try {
               AssetFileDescriptor descriptor = mContext.getAssets().openFd(pasta + File.separator + nomeDoArquivo + ".mp3");
               mp.setDataSource(descriptor.getFileDescriptor(), descriptor.getStartOffset(), descriptor.getLength());
               descriptor.close();
               mp.prepare();
           } catch (Exception e) {
               e.printStackTrace();
               //Toast.makeText(mContext, "Erro ao reproduzir nome do "+ cenario, Toast.LENGTH_LONG).show();
           }

           mp.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
               @Override
               public void onPrepared(MediaPlayer mp) {
                   mp.start();
               }
           });

           mp.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
               @Override
               public void onCompletion(MediaPlayer mp) {
                   if (reproduzirSom) reproduzSom(cenario, nomeDoArquivo);

               }
           });
       }else if(reproduzirSom){
           reproduzSom(cenario, nomeDoArquivo);
       }
    }

    private void reproduzSom(String cenario, String nomeDoArquivo) {
        mp.reset();

        String pasta = cenario + "Sounds";
        try {
            AssetFileDescriptor descriptor = mContext.getAssets().openFd(pasta + File.separator + nomeDoArquivo + ".mp3");
            mp.setDataSource(descriptor.getFileDescriptor(), descriptor.getStartOffset(), descriptor.getLength());
            descriptor.close();
            mp.prepare();
        } catch (Exception e) {
            e.printStackTrace();
            //Toast.makeText(mContext, "Erro ao reproduzir som do animal.", Toast.LENGTH_LONG).show();
        }

        mp.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
            @Override
            public void onPrepared(MediaPlayer mp) {
                mp.start();
            }
        });

        mp.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mp) {
            }
        });
    }

    public void stop(){
        if(mp != null) if(mp.isPlaying()) mp.stop();

    }

    public void release(){
        if (mp!= null) {
            mp.release();
            mp = null;
        }
    }

}
