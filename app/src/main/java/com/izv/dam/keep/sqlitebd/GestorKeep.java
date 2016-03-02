package com.izv.dam.keep.sqlitebd;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.util.Log;

import com.izv.dam.keep.pojo.Keep;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by David on 19/02/2016.
 */
public class GestorKeep {
    private Ayudante abd;
    private SQLiteDatabase bd;

    public GestorKeep(Context c) {
        abd = new Ayudante(c);
    }
    public void open() {
        bd = abd.getWritableDatabase();
    }
    public void openRead() {
        bd = abd.getReadableDatabase();
    }
    public void close() {
        abd.close();
    }

    public long insert(Keep ag) {
        ContentValues valores = new ContentValues();
        valores.put(Contrato.TablaKeep.CONTENIDO,
                ag.getContenido());
        valores.put(Contrato.TablaKeep.ESTADO,
                ag.isEstado());
        long id = bd.insert(Contrato.TablaKeep.TABLA,
                null, valores);
        return id;

        // Devuelve -1 si falla.
    }

    public int delete(Keep ag) {

        return deleteId(ag.getId());
    }

    public int deleteId(long id) {
        String condicion = Contrato.TablaKeep._ID + " = ?";
        String[] argumentos = { id + "" };
        int cuenta = bd.delete(
                Contrato.TablaKeep.TABLA, condicion, argumentos);
        return cuenta;
    }

    public List<Keep> select(String condicion) {
        List<Keep> la = new ArrayList<>();
        Cursor cursor = bd.query(Contrato.TablaKeep.TABLA, null,
                condicion, null, null, null, null);
        cursor.moveToFirst();
        Keep ag;
        while (!cursor.isAfterLast()) {
            ag = getRow(cursor);
            la.add(ag);
            cursor.moveToNext();
        }
        cursor.close();
        return la;
    }

    public Keep getRow(Cursor c) {

        Keep ag = new Keep();
        if(c != null) {
            ag.setId(c.getInt(0));
            Log.v("xxx", c.getInt(0) + "");
            Log.v("xxx",c.getString(1) + "");
            ag.setContenido(c.getString(1));
            if (c.getInt(2)==1){
                ag.setEstado(true);
            }else{
                ag.setEstado(false);
            }

        }else{
            System.out.println("faiiiiiiiil");
        }

        return ag;
    }

    public void changeState(Keep ag){

            ContentValues valores = new ContentValues();
            valores.put(Contrato.TablaKeep.CONTENIDO, ag.getContenido());
            valores.put(Contrato.TablaKeep.ESTADO, 1);
            String condicion = Contrato.TablaKeep._ID + " = ?";
            String[] argumentos = { ag.getId() + "" };
            bd.update(Contrato.TablaKeep.TABLA, valores,
                    condicion, argumentos);
    }

    public void updateContenido(Keep ag){
        ContentValues valores = new ContentValues();
        valores.put(Contrato.TablaKeep.CONTENIDO, ag.getContenido());
        valores.put(Contrato.TablaKeep.ESTADO, 0);
        String condicion = Contrato.TablaKeep._ID + " = ?";
        String[] argumentos = { ag.getId() + "" };
        bd.update(Contrato.TablaKeep.TABLA, valores,
                condicion, argumentos);
    }

}
