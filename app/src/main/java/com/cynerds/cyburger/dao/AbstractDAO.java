package com.cynerds.cyburger.dao;

import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.cynerds.cyburger.helpers.SQLiteDatabaseOpenHelper;


public abstract class AbstractDAO {

    protected SQLiteDatabase db;
    protected SQLiteDatabaseOpenHelper sqLiteOpenHelper;

    // Método para executar a abertura do banco de dados.
    protected final void Open() throws SQLException {
        db = sqLiteOpenHelper.getWritableDatabase();
    }

    // Método para executar o fechamento do banco de dados.
    protected final void Close() throws SQLException
    {
        db.close();
    }

    protected final  boolean isTableExists(String table) {


        Cursor cursor = db.rawQuery("select DISTINCT tbl_name from sqlite_master where tbl_name = '"
                +table+"'", null);
        if(cursor!=null) {
            if(cursor.getCount()>0) {
                cursor.close();
                return true;
            }
            cursor.close();
        }
        return false;
    }




}

