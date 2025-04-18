package com.avathartech.es.servicios;


import com.avathartech.es.utils.DatosEstaticos;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import dev.morphia.Datastore;
import dev.morphia.Morphia;
import dev.morphia.config.ManualMorphiaConfig;
import dev.morphia.query.Query;
import dev.morphia.query.filters.Filters;
import org.bson.types.ObjectId;

public class GestionDB<T> {

    private final Class<T> claseEntidad;
    private final  MongoClient cliente = MongoDbConexion.getMongoClient();

    public GestionDB(Class<T> claseEntidad) {
        this.claseEntidad = claseEntidad;
    }

    protected Datastore getConexionMorphia(){
        return Morphia.createDatastore(cliente, new ManualMorphiaConfig().database(MongoDbConexion.getNombreBaseDatos()));
    }

    public T crear(T entidad){

        Datastore datastore = getConexionMorphia();
        try {
            return datastore.save(entidad);
        }catch (Exception  e){
            e.printStackTrace();
        }
        return null;
    }
    public Query<T> find(){
        Datastore datastore = getConexionMorphia();
        Query<T> query = datastore.find(claseEntidad);
        return query;
    }
    public T findByID(String id){
        Datastore datastore = getConexionMorphia();
        Query<T> query = datastore.find(claseEntidad).filter(Filters.eq("_id", new ObjectId(id)));
        return  query.first();
    }

    public void deleteById(String id){
        Datastore datastore = getConexionMorphia();
        datastore.find(claseEntidad).filter(Filters.eq("_id", new ObjectId(id))).delete();

    }

}
