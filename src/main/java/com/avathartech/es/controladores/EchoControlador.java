package com.avathartech.es.controladores;

import com.avathartech.es.Main;
import com.avathartech.es.entidades.Usuario;
import com.avathartech.es.servicios.TramaServices;
import com.avathartech.es.servicios.UsuarioServices;
import com.avathartech.es.utils.DatosEstaticos;
import com.avathartech.es.utils.Utilidades;
import io.javalin.http.Context;
import io.javalin.http.UnauthorizedResponse;
import org.jetbrains.annotations.NotNull;

public class EchoControlador {

    /**
     * Recibe la información del cliente y la persiste en la base de datos.
     * @param ctx
     */
    public static void persistirTrama(@NotNull Context ctx){
        //Validando la petición
        String token = ctx.header(DatosEstaticos.HEADER_SEGURIDAD_TOKEN.getValor());
        Usuario usuario = UsuarioServices.getInstancia().autenticacion(token);
        if(usuario == null){
            throw new UnauthorizedResponse("Token no coincide");
        }

        //Validando el tipo de contenido.
        if(ctx.header("Content-Type") == null || !ctx.header("Content-Type").equals("application/json")){
            throw new UnauthorizedResponse("Tipo de contenido no es JSON");
        }
        //Recuperando el cuerpo del mensaje.
        String cuerpoMensaje = ctx.body();
        if (!Utilidades.esJsonValido(cuerpoMensaje)) {
            throw new UnauthorizedResponse("El cuerpo del mensaje no es un JSON válido");
        }

        //Almancenando en la base de datos.
        ctx.json(TramaServices.getInstancia().guardarJson(cuerpoMensaje));

        //Mostrando a los usuarios la información recibida.
        Main.enviarMensajeSubcriptores(Utilidades.formatearJson(cuerpoMensaje));
    }

}