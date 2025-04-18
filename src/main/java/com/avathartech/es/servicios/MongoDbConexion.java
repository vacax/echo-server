package com.avathartech.es.servicios;

import com.avathartech.es.utils.DatosEstaticos;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import org.bson.Document;

public class MongoDbConexion {
    private static MongoClient mongoClient;
    private static String nombreBaseDatos;

    public static MongoClient getMongoClient() {
        if(mongoClient == null){
            ProcessBuilder processBuilder = new ProcessBuilder();
            String URL_MONGODB = processBuilder.environment().get(DatosEstaticos.URL_MONGO.getValor());
            nombreBaseDatos = processBuilder.environment().get(DatosEstaticos.DB_NOMBRE.getValor());
            //
            System.out.println("URL: "+URL_MONGODB);
            System.out.println("BASE_DATOS: "+nombreBaseDatos);
            //
            mongoClient = MongoClients.create(URL_MONGODB);
        }
        return mongoClient;
    }


    /**
     *
     * @return
     */
    public static MongoDatabase getBaseDatos(){

        //Retomando la conexión
        MongoDatabase database = mongoClient.getDatabase(nombreBaseDatos);
        database.runCommand(new Document("ping", 1));
        System.out.println("Conexión completada");

        //
        return database;
    }

    public static String getNombreBaseDatos(){
        return nombreBaseDatos;
    }


}
