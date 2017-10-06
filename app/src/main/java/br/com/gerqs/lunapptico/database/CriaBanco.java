package br.com.gerqs.lunapptico.database;

import android.content.Context;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class CriaBanco extends SQLiteOpenHelper {

    private static final String NOME_BANCO = "banco.db";
    public static final String TABELA = "pontuacao";
    public static final String ID = "_id";
    public static final String PONTOS = "pontos";

    private static final int VERSAO = 2;
    private static final String TAG = "DataAdapter";

    public CriaBanco(Context context){
        super(context, NOME_BANCO,null,VERSAO);
    }



    @Override
    public void onCreate(SQLiteDatabase db) {

        try {
            String sql = "CREATE TABLE " + TABELA + "("
                    + ID + " integer primary key autoincrement,"
                    + PONTOS + " PONTOS"
                    + ")";
            db.execSQL(sql);
            //iniciar a pontuação do usuário quando instalar o app
            String sql2 = "INSERT INTO " + TABELA + " VALUES(1,0)";
            db.execSQL(sql2);
        }catch (SQLException mSQLException){
            Log.e(TAG, "open >>" + mSQLException.toString());
            throw mSQLException;
        }
    }

     @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABELA);
        onCreate(db);
    }
}
