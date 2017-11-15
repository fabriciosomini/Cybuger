package com.cynerds.cyburger.helpers;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by fabri on 14/11/2017.
 */

public class SQLiteDatabaseOpenHelper extends SQLiteOpenHelper {
    // Nome do banco de dados.
    private static final String
            DATABASE_NAME = "cyburger.db";

    // Versão do banco de dados.
    private static final int
            DATABASE_VER = 1;

    /*
    Construtor obrigatório da classe.
     */
    public SQLiteDatabaseOpenHelper(Context ao_context) {
        super(ao_context, DATABASE_NAME, null, DATABASE_VER);
    }

    /*
    Método responsável por criar as tabelas do banco.
     */
    @Override
    public void onCreate(SQLiteDatabase db) {

    }

    /*
    Método responsável por fazer a atualização do banco.
     */
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }


}
