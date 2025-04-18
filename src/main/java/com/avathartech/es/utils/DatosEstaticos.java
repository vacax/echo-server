package com.avathartech.es.utils;

public enum DatosEstaticos {

    USUARIO("USUARIO"),
    TRAMA("tramas"),
    HEADER_SEGURIDAD_TOKEN("SEGURIDAD-TOKEN"),
    URL_MONGO("URL_MONGO"),
    DB_NOMBRE("DB_NOMBRE"),
    PUERTO_APP("PUERTO_APP");


    private String valor;

    DatosEstaticos(String valor){
        this.valor =  valor;
    }

    public String getValor() {
        return valor;
    }
}
