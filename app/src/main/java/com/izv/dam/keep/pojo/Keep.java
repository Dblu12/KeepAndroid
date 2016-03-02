package com.izv.dam.keep.pojo;

/**
 * Created by izv on 10/02/2016.
 */
public class Keep {

    //id,contenido, estado
    private  long id;
    private  String contenido;
    private  boolean Estado;

    public Keep() {
    }


    public Keep(long id, String contenido, boolean estado) {
        this.id = id;
        this.contenido = contenido;
        Estado = estado;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getContenido() {
        return contenido;
    }

    public void setContenido(String contenido) {
        this.contenido = contenido;
    }

    public boolean isEstado() {
        return Estado;
    }

    public void setEstado(boolean estado) {
        Estado = estado;
    }

    @Override
    public String toString() {
        return "Keep{" +
                "id=" + id +
                ", contenido='" + contenido + '\'' +
                ", Estado=" + Estado +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Keep keep = (Keep) o;

        return id == keep.id;

    }

    @Override
    public int hashCode() {
        return (int) (id ^ (id >>> 32));
    }
}
