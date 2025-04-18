package com.avathartech.es.servicios;

import com.avathartech.es.utils.DatosEstaticos;
import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.result.InsertOneResult;
import org.bson.Document;

public class TramaServices {
    private static TramaServices instancia;

    private TramaServices() {

    }

    public static TramaServices getInstancia(){
        if(instancia == null){
            instancia =new TramaServices();
        }
        return instancia;
    }

    /**
     * Permite almacena un JSON en MongoDB.
     * @param jsonString
     */
    public String guardarJson(String jsonString) {
        try {
            // Validar que sea un JSON válido primero
            Gson gson = new Gson();
            JsonElement jsonElement = gson.fromJson(jsonString, JsonElement.class);

            // Convertir el JSON a Document de MongoDB
            Document document = Document.parse(jsonString);
            document.computeIfAbsent("date_create", k -> new java.util.Date());

            // Obtener la base de datos y la colección
            MongoCollection<Document> collection = MongoDbConexion.getBaseDatos().getCollection(DatosEstaticos.TRAMA.getValor());

            // Insertar el documento
            InsertOneResult insertOneResult = collection.insertOne(document);
            return insertOneResult.toString();

        } catch (Exception e) {
            throw new RuntimeException("Error al guardar el JSON en MongoDB: " + e.getMessage(), e);
        }
    }

}
