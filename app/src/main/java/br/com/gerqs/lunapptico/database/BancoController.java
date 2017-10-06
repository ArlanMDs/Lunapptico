package br.com.gerqs.lunapptico.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;


public class BancoController {

    private SQLiteDatabase db;
    private final CriaBanco banco;

    public BancoController(Context context){
        banco = new CriaBanco(context);

    }


    String insereDado(String pontos){
        ContentValues valores;
        long resultado;

        db = banco.getWritableDatabase();
        valores = new ContentValues();
        valores.put(CriaBanco.PONTOS, pontos);

        resultado = db.insert(CriaBanco.TABELA, null, valores);
        db.close();

        if (resultado ==-1)
            return "Erro ao inserir registro";
        else
            return "Registro Inserido com sucesso";

    }

    Cursor carregaDados(){
        Cursor cursor;
        String[] campos =  {CriaBanco.ID, CriaBanco.PONTOS};
        db = banco.getReadableDatabase();
        cursor = db.query(CriaBanco.TABELA, campos, null, null, null, null, null, null);

        if(cursor!=null){
            cursor.moveToFirst();
        }
        db.close();
        return cursor;
    }

    public Cursor carregaDadoById(int id){
        Cursor cursor;
        String[] campos =  {CriaBanco.ID, CriaBanco.PONTOS};
        String where = CriaBanco.ID + "=" + id;
        db = banco.getReadableDatabase();
        cursor = db.query(CriaBanco.TABELA,campos,where, null, null, null, null, null);

        if(cursor!=null){
            cursor.moveToFirst();
        }
        db.close();
        return cursor;
    }
    //id do usuário 1, no momento nao há mais usuários e a nova quantidade de pontos
    public void alteraPontos(int id, int pontos){
        ContentValues valores;
        String where;
        db = banco.getWritableDatabase();
        where = CriaBanco.ID + "=" + id;
        valores = new ContentValues();
        valores.put(CriaBanco.PONTOS, pontos);
        db.update(CriaBanco.TABELA,valores,where,null);
        db.close();
    }
/*
    public void deletaRegistro(int id){
        String where = CriaBanco.ID + "=" + id;
        db = banco.getReadableDatabase();
        db.delete(CriaBanco.TABELA,where,null);
        db.close();
    }
*/


}
