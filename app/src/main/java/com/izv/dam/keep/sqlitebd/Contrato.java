package com.izv.dam.keep.sqlitebd;

import android.provider.BaseColumns;

/**
 * Created by David on 19/02/2016.
 */
public class Contrato {
    private Contrato(){}

    public static abstract class TablaKeep implements BaseColumns{
        public static final String TABLA= "keep";
        public static final String CONTENIDO ="contenido";
        public static final String ESTADO= "estado";
    }
}
