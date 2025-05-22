package com.fct.we_chat.model;

import java.util.List;

public class Grupo {
    private String nombre;
    private List<String> miembros;

    public Grupo(String nombre, List<String> miembros) {
        this.nombre = nombre;
        this.miembros = miembros;
    }

    public String getNombre() {
        return nombre;
    }

    public List<String> getMiembros() {
        return miembros;
    }

    @Override
    public String toString() {
        return "Grupo: " + nombre + ", Miembros: " + miembros;
    }
}
