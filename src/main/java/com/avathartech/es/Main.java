package com.avathartech.es;

import com.avathartech.es.controladores.EchoControlador;
import com.avathartech.es.entidades.Usuario;
import com.avathartech.es.servicios.UsuarioServices;
import com.avathartech.es.utils.DatosEstaticos;
import io.javalin.Javalin;
import io.javalin.rendering.template.JavalinThymeleaf;
import org.eclipse.jetty.websocket.api.Session;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static io.javalin.apibuilder.ApiBuilder.*;
import static io.javalin.apibuilder.ApiBuilder.before;
import static io.javalin.apibuilder.ApiBuilder.get;
import static io.javalin.apibuilder.ApiBuilder.path;

import static j2html.TagCreator.*;

public class Main {

    public static List<Session> usuariosConectados = new ArrayList<>();

    public static void main(String[] args) {
        var URL_MONGO = System.getenv(DatosEstaticos.URL_MONGO.getValor());
        var BASE_DATOS = System.getenv(DatosEstaticos.DB_NOMBRE.getValor());

        if(URL_MONGO==null){
            System.out.println("Debe indicar la variable de ambiente URL_MONGO");
            System.exit(-1);
        }

        if(BASE_DATOS==null){
            System.out.println("Debe indicar la variable de ambiente BASE_DATOS");
            System.exit(-1);
        }

        /**
         * Creando el usuario admin por defecto para la primera ejecución
         */
        validarUsuarioPorDefecto();

        /**
         * Bloque de Javalin
         */
        var app = Javalin.create(javalinConfig -> {
                    // Definiendo los archivo public
                    javalinConfig.staticFiles.add("/publico");

                    //
                    javalinConfig.fileRenderer(new JavalinThymeleaf());

                    //Rutas para API
                    javalinConfig.router.apiBuilder(() -> {

                        path("/api", () -> {

                            get("/", ctx -> {
                                ctx.result("Servidor Echo Server");
                            });

                            post("/echo-json", EchoControlador::persistirTrama);
                        });

                        path("/admin/", () -> {

                            /**
                             * Filtro de autenticación
                             */
                            before(ctx -> {
                                //validando si existe el usuario logueado.
                                //System.out.println("Logueado: "+DatosEstaticos.USUARIO.name());
                                Usuario usuario = ctx.sessionAttribute(DatosEstaticos.USUARIO.name());
                                /**
                                 * Si, el usuario no está en la sesión, y la vista no es login.html y no es el endpoint de autenticar,
                                 * lo mando al login.html, lo contrario continuamos con la peticion.
                                 */
                                if(usuario== null && !(ctx.path().contains("login.html") || ctx.path().contains("/autenticar"))){
                                    ctx.redirect("/login.html");
                                }
                            });

                            //Enviando al templates
                            get("/", ctx -> {
                                Map<String, Object> modelo = new HashMap<>();
                                modelo.put("titulo", "Centro de Datos del Clima - ITT363");
                                ctx.render("/templates/index_admin.html", modelo);
                            });


                        });

                        /*path("/usuario", () -> {

                         *//**
                         * Únicamente los usuarios administradores tienen permisos.
                         *//*
                            before(ctx -> {
                                Usuario usuario = ctx.sessionAttribute(DatosEstaticos.USUARIO.name());
                                if(usuario==null || !usuario.isAdministrador()){
                                    throw new UnauthorizedResponse("No tiene acceso, debe ser administrador");
                                }
                            });

                            get("/", ctx -> {
                                Map<String, Object> modelo = new HashMap<>();
                                modelo.put("titulo", "Usuarios Registro DNS DigitalOcean - "+Dominio);
                                ctx.render("/templates/index_usuarios.html", modelo);
                            });
                            //Consulta generales
                            get("/lista", UsuarioController::listadoRegistroHtml);
                            get("/crear", UsuarioController::formularioCreacion);
                            get("/botones", UsuarioController::listadoBotonesPermisos);
                            post(UsuarioController::creacionUsuario);

                            //Consulta de un registro
                            path("/{id}",() -> {
                                get(UsuarioController::formularioEdicion);
                                delete("",UsuarioController::eliminarUsuario);
                                put(UsuarioController::editarUsuario);
                            });
                        });*/
                    });
                })

                /**
                 * Para nuestro ejemplo no importa los valores recibido, lo estaremos validando.
                 */
                .post("/autenticar", ctx -> {
                    //
                    String username = ctx.formParam("username");
                    String password = ctx.formParam("password");
                    //
                    Usuario usuario = UsuarioServices.getInstancia().autenticacion(username, password);
                    ctx.sessionAttribute(DatosEstaticos.USUARIO.getValor(), usuario);
                    //
                    ctx.redirect("/admin/");
                })

                //Salida del sistema.
                .get("/logout", ctx -> {
                    ctx.req().getSession().invalidate();
                    ctx.redirect("/");
                })

                //Enviando al template
                /* .get("/", ctx -> {
                    ctx.result("Aplicación Hub Climatica - Proyecto ITT363");
                 })*/

                .put("/messages", ctx -> {
                    ctx.result("Hola mundo HTMX");
                })

                .ws("/lecturas", wsConfig -> {

                    wsConfig.onConnect(wsConnectContext -> {
                        System.out.println("Conectando: "+wsConnectContext.session.toString());
                        wsConnectContext.enableAutomaticPings();
                        usuariosConectados.add(wsConnectContext.session);
                    });

                    wsConfig.onClose(wsCloseContext -> {
                        System.out.println("Desconectando: "+wsCloseContext.session.toString());
                        usuariosConectados.remove(wsCloseContext.session);
                    });

                    wsConfig.onMessage(wsMessageContext -> {
                        String mensaje  = wsMessageContext.message();
                        System.out.println("Mensaje Recibido: "+mensaje);
                        /*wsMessageContext.send("<div id=\"lecturas\" hx-swap-oob=\"beforeend\">Notificación</div>");
                        wsMessageContext.send("<div id=\"chat_room\" hx-swap-oob=\"morphdom\">En ChatRoom</div>");*/
                    });


                })

                .start(getPuertoAplicacion());

    }

    /**
     * Crea el usuario admin por defecto la primera vez que es ejecutado
     */
    private static void validarUsuarioPorDefecto(){
        var lista = UsuarioServices.getInstancia().listaUsuarios();
        if(lista.isEmpty()){
            UsuarioServices.getInstancia().crear(new Usuario("admin", "admin", true));
        }
    }

    /**
     * Enviando al información recibida a todos los subcriptores vía websocket.
     * @param json
     */
    public static void enviarMensajeSubcriptores(String json){
        //System.out.println(json);
        for(Session sesionConectada : usuariosConectados){
            try {
                String divTabla = div(
                        table(
                                tr(th("Información Recibida").withColspan("2")),
                                tr(
                                        td("json"), td(pre(json))
                                ),
                                tr(
                                        td("Fecha Lectura"), td(""+ new SimpleDateFormat("dd-mm-yyyy HH:mm:ss").format(System.currentTimeMillis()) + "")
                                )
                        ).withStyle("border: 1px solid black;")
                ).withId("lecturas").attr("hx-swap-oob", "beforeend").render();
                System.out.println("Enviando mensaje: "+divTabla);
                sesionConectada.getRemote().sendString(divTabla);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     *
     * @return
     */
    private static int getPuertoAplicacion() {
        ProcessBuilder processBuilder = new ProcessBuilder();
        if (processBuilder.environment().get(DatosEstaticos.PUERTO_APP.getValor()) != null) {
            return Integer.parseInt(processBuilder.environment().get(DatosEstaticos.PUERTO_APP.getValor()));
        }
        return 7000; //Retorna el puerto por defecto en caso de no estar en Heroku.
    }

}
