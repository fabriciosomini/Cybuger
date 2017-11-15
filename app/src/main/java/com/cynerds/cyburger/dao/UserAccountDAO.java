package com.cynerds.cyburger.dao;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;

import com.cynerds.cyburger.helpers.SQLiteDatabaseOpenHelper;
import com.cynerds.cyburger.models.account.UserAccount;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by fabri on 14/11/2017.
 */

public class UserAccountDAO extends AbstractDAO {
    // Cria o array de colunas.
    private final String[]
            colunas =
            {
                    UserAccount.COLUMN_ID,
                    UserAccount.COLUMN_EMAIL,
                    UserAccount.COLUMN_PASSWORD,

            };

    // Construtor para instanciar o Helper.
    public UserAccountDAO(Context ao_context) {
        sqLiteOpenHelper = new SQLiteDatabaseOpenHelper(ao_context);

    }

    private void CreateTable(String table) {
        SQLiteDatabase db = sqLiteOpenHelper.getWritableDatabase();
        String sql = "CREATE TABLE " + table
                + " ("
                + "id INTEGER PRIMARY KEY AUTOINCREMENT, "
                + "email  text not null, "
                + "password text not null "
                + ");";

        try {
            db.execSQL("DROP TABLE IF EXISTS " + table);
            db.execSQL(sql);

        } catch (SQLException e) {

        }
    }

    // Método para adicionar registro na base de dados.
    public long InsertOrUpdate(final String email, final String password) {
        DeleteAll();

        Open();

        if (!isTableExists(UserAccount.TABLE_NAME)) {
            CreateTable(UserAccount.TABLE_NAME);
        }


        long retorno = -1;


        // Adiciona os valores a serem adicionados na base.
        ContentValues values = new ContentValues();
        values.put(UserAccount.COLUMN_EMAIL, email);
        values.put(UserAccount.COLUMN_PASSWORD, password);

        int id = (int) db.insert(UserAccount.TABLE_NAME,
                null,
                values);

        Close();

        return id;
    }


    public void DeleteAll() {

        Open();
        if (isTableExists(UserAccount.TABLE_NAME)) {

            db.execSQL("delete from " + UserAccount.TABLE_NAME);
        }

        Close();
    }


    // Método para buscar todos os registros na base de dados.
    public List<UserAccount> SelectAll() {


        Open();
        ArrayList<UserAccount> userAccounts = new ArrayList<UserAccount>();

        if (isTableExists(UserAccount.TABLE_NAME)) {

            Cursor query = db.query(
                    UserAccount.TABLE_NAME,
                    colunas,
                    null,
                    null,
                    null,
                    null,
                    UserAccount.COLUMN_EMAIL);
            // Movimenta o cursor para a primeira linha.
            boolean hasContent = query.moveToFirst();

            while (!query.isAfterLast()) {

                // Monta a estrutura da pessoa.
                UserAccount model = new UserAccount();
                model.setEmail(query.getString(1));
                model.setPassword(query.getString(2));


                // Adiciona no Array.
                userAccounts.add(model);

                // Vai para a próxima linha.
                query.moveToNext();
            }

        }

        Close();

        return userAccounts;
    }

}
