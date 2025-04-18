package com.avathartech.es.servicios;

import com.avathartech.es.entidades.Usuario;
import dev.morphia.Datastore;
import dev.morphia.query.Query;
import dev.morphia.query.filters.Filters;
import dev.morphia.query.updates.UpdateOperators;
import org.bson.types.ObjectId;

import java.io.IOException;
import java.util.List;

public class UsuarioServices extends GestionDB<Usuario>{

    private static UsuarioServices instancia;

    private UsuarioServices() {
        super(Usuario.class);
    }

    public static UsuarioServices getInstancia(){
        if(instancia == null){
            instancia =new UsuarioServices();
        }
        return instancia;
    }

    /**
     *
     * @param usuario
     * @param password
     * @return
     */
    public Usuario autenticacion(String usuario, String password){
        Datastore conexionMorphia = getConexionMorphia();
        Query<Usuario> query = conexionMorphia.find(Usuario.class).filter(Filters.and(Filters.eq("username", usuario), Filters.eq("password", password)));
        if(query.count() == 0){
            System.out.println("Usuario y contraseña no coinciden");
            return null;
        }

        return query.first();
    }

    /**
     * Autenticación del usuario por Token.
     * @param token
     * @return
     */
    public Usuario autenticacion(String token){
        Datastore conexionMorphia = getConexionMorphia();
        Query<Usuario> query = conexionMorphia.find(Usuario.class).filter(Filters.and(Filters.eq("token", token)));
        if(query.count() == 0){
            System.out.println("Token no encontrado");
            return null;
        }

        return query.first();
    }

    /**
     *
     * @return
     */
    public List<Usuario> listaUsuarios(){
        Datastore con = getConexionMorphia();
        return con.find(Usuario.class).iterator().toList();
    }

    /**
     *
     * @param id
     * @return
     * @throws IOException
     * @throws InterruptedException
     */
    public Usuario eliminarUsuario(String id) throws IOException, InterruptedException {
        Usuario reg = findByID(id);
        deleteById(reg.getId().toHexString());
        return reg;
    }

    /**
     *
     * @param id
     * @param password
     * @return
     */
    public Usuario editarUsuario(String id, String password) throws IOException, InterruptedException {
        Usuario reg = null;
        long modifiedCount = getConexionMorphia().find(Usuario.class)
                .filter(Filters.eq("_id", new ObjectId(id))).update(UpdateOperators.set("password", password))
                .getModifiedCount();
        if(modifiedCount > 0){
            System.out.println("Usuario actualizado");
            reg = findByID(id);
        }

        return reg;
    }



}
