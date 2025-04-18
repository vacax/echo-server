# Proyecto Echo Server

Muestra por pantalla la información recibida por los diferentes servicios.

## Tecnologías

- Java 21
- Javalin
- MongoDB
- HTMX 2.0
- Gradle 8.12

## End Points

### /api/echo-json

Es necesario enviar el header SEGURIDAD-TOKEN con el valor enviado.

```
 curl --location 'https://DIRECCION-SERVIDOR/api/echo-json' \
        --header 'SEGURIDAD-TOKEN: indicar-su-token' \
        --header 'Content-Type: application/json' \
        --data '{
        "grupo": "1",
        "estacion": "1",
        "fecha": "29/07/2024 11:41:00",
        "temperatura": 25.7,
        "humedad": 56
        }'
```