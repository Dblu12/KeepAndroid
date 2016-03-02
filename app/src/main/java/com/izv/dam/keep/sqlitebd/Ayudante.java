package com.izv.dam.keep.sqlitebd;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by David on 19/02/2016.
 */
public class Ayudante extends SQLiteOpenHelper{
    public static final String DATABASE_NAME = "keeps.sqlite";
    public static final int DATABASE_VERSION = 1;

    public Ayudante(Context context) {
        super(context, DATABASE_NAME, null,
                DATABASE_VERSION);

    }

    @Override
    public void onCreate(SQLiteDatabase db) {

        String sql;

        sql = "create table " + Contrato.TablaKeep.TABLA +
                " (" + Contrato.TablaKeep._ID +
                " integer primary key autoincrement, " +
                Contrato.TablaKeep.CONTENIDO + " text, " +
                Contrato.TablaKeep.ESTADO + " integer)";

        db.execSQL(sql);



    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

        String sql = "drop table if exists "
                + Contrato.TablaKeep.TABLA;
        db.execSQL(sql);

        onCreate(db);

    }
}
