package com.avathartech.es.utils;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;

public class Utilidades {

    /**
     *
     * @param jsonString
     * @return
     */
    public static String formatearJson(String jsonString) {
        try {
            // Crear una instancia de Gson con formato bonito
            Gson gson = new GsonBuilder()
                    .setPrettyPrinting()
                    .create();

            // Parsear el String a JsonElement
            JsonElement jsonElement = JsonParser.parseString(jsonString);

            // Convertir de vuelta a String con formato
            return gson.toJson(jsonElement);
        } catch (Exception e) {
            throw new RuntimeException("Error al formatear el JSON: " + e.getMessage(), e);
        }
    }

    /**
     *
     * @param jsonString
     * @return
     */
    public static boolean esJsonValido(String jsonString) {
        try {
            new Gson().fromJson(jsonString, Object.class);
            return true;
        } catch (com.google.gson.JsonSyntaxException e) {
            return false;
        }
    }
}
