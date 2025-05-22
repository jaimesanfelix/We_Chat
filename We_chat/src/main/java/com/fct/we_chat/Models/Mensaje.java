package com.fct.we_chat.Models;

public class Mensaje {

    private String from;
    private String to;
    private String mensaje;

    public Mensaje(String mensaje, String from) {
        this.mensaje = mensaje;
        this.from = from;
    }

    public Mensaje() {}

    public String getFrom() {
        return from;
    }

    public void setFrom(String from) {
        this.from = from;
    }

    public String getTo() {
        return to;
    }

    public void setTo(String to) {
        this.to = to;
    }

    public String getMensaje() {
        return mensaje;
    }

    public void setMensaje(String message) {
        this.mensaje = message;
    }

}
