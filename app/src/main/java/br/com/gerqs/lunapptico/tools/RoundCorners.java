package br.com.gerqs.lunapptico.tools;

import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.support.v4.graphics.drawable.RoundedBitmapDrawable;
import android.support.v4.graphics.drawable.RoundedBitmapDrawableFactory;
import android.widget.Toast;

import java.io.IOException;
import java.io.InputStream;

import br.com.gerqs.lunapptico.activities.AprenderPalavras;


public class RoundCorners {
    private Resources resources;
    private Context context;

    public RoundCorners(Context con, Resources res){
        context = con;
        resources = res;
    }

    public Drawable arredondarPorID(int id){
        Bitmap src = BitmapFactory.decodeResource(resources, id);
        RoundedBitmapDrawable imgComCantosArredondados = RoundedBitmapDrawableFactory.create(resources, src);
        try {
            imgComCantosArredondados.setCornerRadius(Math.max(src.getWidth(), src.getHeight()) / 7.0f);
        }catch (Exception e){
            e.printStackTrace();
            Toast.makeText(context, "Erro ao arredondarPorID", Toast.LENGTH_SHORT).show();
            Intent intent = new Intent(context.getApplicationContext(), AprenderPalavras.class);
            context.startActivity(intent);
        }
            return imgComCantosArredondados;
    }

    public Drawable arredondarPorInputstream(InputStream inputstream){

        Bitmap src = BitmapFactory.decodeStream(inputstream);
        RoundedBitmapDrawable imgComCantosArredondados = RoundedBitmapDrawableFactory.create(resources, src);

        try {
            imgComCantosArredondados.setCornerRadius(Math.max(src.getWidth(), src.getHeight()) / 10.0f);
        }catch(Exception e){
            e.printStackTrace();
            Toast.makeText(context, "Erro ao arredondarPorInputstream", Toast.LENGTH_SHORT).show();
            Intent intent = new Intent(context.getApplicationContext(), AprenderPalavras.class);
            context.startActivity(intent);
        }
        if(inputstream!=null) {
            try {
                inputstream.close();
            } catch (IOException e) {
                e.printStackTrace();
                return null;
            }
        }

        return imgComCantosArredondados;
    }

}
