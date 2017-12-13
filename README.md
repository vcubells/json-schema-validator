# Ejemplo de validación de JSON en Java

Este ejemplo muestra como validar un documento en JSON utilizando un *JSON Schema Validator*.

Incluye la validación de las siguientes combinaciones:

* JSON y Schema disponibles en una URL.
* JSON y Schema disponibles en recursos locales.
* JSON en una URL y el Schema de manera local.
* JSON local y el Schema disponible en una URL.

## Pre-requisitos

* Java 1.8
* [Apache Maven](https://maven.apache.org/)
* [JSON Schema validator](https://github.com/java-json-tools/json-schema-validator)
* Servidor Web en caso de probar validaciones con archivos o esquemas que no se encuentren de manera local.

## Instrucciones de uso

1. Descargue el repositorio a una carpeta de su computadora utilizando el comando `git clone`.
2. Cámbiese a la carpeta del proyecto.
3. Compile el código con el comando: `mvn verify` ó utilice un IDE.
