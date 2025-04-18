package com.avathartech.es.entidades;

import dev.morphia.annotations.Entity;
import dev.morphia.annotations.Id;
import org.bson.types.ObjectId;

@Entity("usuarios")
public class Usuario {
    @Id
    ObjectId id;
    String username;
    String password;
    boolean administrador;
    String token;
    boolean habiltado;

    public Usuario() {
    }

    public Usuario(String username, String password) {
        this.username = username;
        this.password = password;
    }

    public Usuario(ObjectId id, String username, String password) {
        this.id = id;
        this.username = username;
        this.password = password;
        this.habiltado = true;
    }

    public Usuario(String username, String password, boolean administrador) {
        this.username = username;
        this.password = password;
        this.administrador = administrador;
        this.habiltado = true;
    }

    public Usuario(ObjectId id, String username, String password, boolean administrador) {
        this.id = id;
        this.username = username;
        this.password = password;
        this.administrador = administrador;
        this.habiltado = true;
    }

    public ObjectId getId() {
        return id;
    }

    public void setId(ObjectId id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public boolean isAdministrador() {
        return administrador;
    }

    public void setAdministrador(boolean administrador) {
        this.administrador = administrador;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public boolean isHabiltado() {
        return habiltado;
    }

    public void setHabiltado(boolean habiltado) {
        this.habiltado = habiltado;
    }

    @Override
    public String toString() {
        return "Usuario{" +
                "id=" + id +
                ", username='" + username + '\'' +
                ", password='" + password + '\'' +
                ", administrador=" + administrador +
                ", token='" + token + '\'' +
                ", habiltado=" + habiltado +
                '}';
    }
}
