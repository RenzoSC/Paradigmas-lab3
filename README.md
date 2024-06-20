Básicamente este repositorio tiene el ejemplo `JavaWordCount.java` que viene en la distribución de spark junto con el archivo `pom.xml` para poder compilarlo fácilmente. Esperamos que esto les sirva para poder empezar más fácilmente el lab3.

Pueden ver la consigna del laboratorio en https://docs.google.com/document/d/e/2PACX-1vQn5BpCPQ6jKMN-sz46261Qot82KbDZ1RUx8jNzAN4kBEAq_i97T3R6ZA0_yRA5elN66e-EArXQXuAh/pub

# Preparación del entorno
- [ ] Instalar [maven](https://maven.apache.org/guides/getting-started/maven-in-five-minutes.html) Es probable que sea un paquete de tu distribución (`$ sudo apt install maven` si estás en Ubuntu, Debian o derivados).
- [ ] Descargar [spark 3.5.1](https://www.apache.org/dyn/closer.lua/spark/spark-3.5.1/spark-3.5.1-bin-hadoop3.tgz) y descomprimirlo en el directorio `DIR`.
- [ ] Definir variable de entorno `export SPARK_HOME=<DIR>` (ahí `<DIR>` es el directorio donde descomprimieron spark).


# Informe

# Introducción
## ¿Que es Apache Spark?
Apache Spark es un motor unificado de analíticas para procesar datos a gran escala que integra módulos para SQL, streaming, aprendizaje automático y procesamiento de grafos.
En este laboratorio Spark es utilizado para paralelizar el computo de entidades nombradas utilizando un modelo Master/Slave donde el Master se encarga de distribuir entre los esclavos. 

En el laboratorio anterior la implementación era útil para textos pequeños/medianos donde no se necesitaba mucha potencia de calculo, ahora buscamos implementar computación paralela para poder calcular las entidades nombradas con una gran cantidad de información.

Para lograr implementar el manejo de bigData en nuestra aplicacion hacemos uso de la API de Spark para Java.

## Ventajas de usar Apache Spark

* Rapidez: Como ya explicamos paraleliza el trabajo para mayor velocidad de calculo.
* Facilidad de uso: Tiene un API´s para varios lenguajes de programación sencillas de utilizar y bien documentadas.
* Ampliamente utilizado: Es el framework de computacion paralela mas utilizado por lo que también tiene una gran comunidad lo que facilita buscar informacion sobre errores que otros ya tuvieron y fueron solucionados.
* Uso general: Spark permite usar una pila de bibliotecas que incluye SQL, DataFrame, MLlib para aprendizaje automático, GraphX y Spark Streaming. Además, puedes combinarlas sin problemas en la misma aplicación.
## ¿Como funciona la arquitectura Master/Slave?

Se utiliza un nodo principal padre (Master) que se va a encargar de procesar la petición del usuario enviada, dividir la big data entre sus Slaves y estos últimos se van a encargar de procesar la información que les llego para luego devolverle el resultado al Master que una vez que todos sus Slaves le hayan devuelto la información va a entregarle al usuario el resultado final del computo.

Esto representa una clara estrategia de divide y venceras para aumentar la velocidad a la que se realiza el calculo de entidades y de esta forma poder manejar grandes cantidades de información.

IMAGEN QUE MUESTRE COMO ES LA ARQUITECTURA ACA 

# Estructura del proyecto con Apache Maven

## ¿Que es Apache Maven?

**[Apache Maven](https://maven.apache.org/)** es una potente herramienta de gestión de proyectos que se utiliza para gestión de dependencias, como herramienta de compilación e incluso como herramienta de documentación. Es de código abierto y gratuita.

Aunque es utilizada en distintos lenguajes se usa principalmente con proyectos de Java. Maven utiliza convenciones sobre dónde colocar ciertos archivos para el proceso de _build_ de un proyecto.

Además, es una **herramienta declarativa**. Es decir, todo lo que definamos (dependencias en módulos y compontes externos, procesos, orden de compilación, _plugins_ del propio Maven...) se almacena en un archivo XML que Maven lee a la hora de funcionar.

Maven es capaz de:
* Gestionar dependencias
* Compilar código fuente
* Empaquetar código
* Instalar paquetes
* Generar documentación
* Gestionar fases del proyecto

## ¿Que es el archivo pom.xml?
La unidad básica de trabajo en Maven es el llamado **Modelo de Objetos de Proyecto** conocido simplemente como **POM** (de sus siglas en inglés: _Project Object Model_).

Se trata de un archivo XML llamado `pom.xml` que se encuentra por defecto en la raíz de los proyectos y que **contiene toda la información del proyecto**: su configuración, sus dependencias, etc.

Incluso, aunque nuestro proyecto, que usa Maven, tenga un archivo `pom.xml` sin opciones propias, prácticamente vacío, estará usando el modelo de objetos para definir los valores por defecto del mismo. Por ejemplo, por defecto, el directorio donde está el código fuente es `src/main/java`, donde se compila el proyecto es `target` y donde ubicamos los test unitarios es en `src/main/test`, etc... Al `pom.xml` global, con los valores predeterminados se le llama **Súper POM**.

Esta sería la estructura habitual de un proyecto Java que utiliza Maven:

IMAGEN DEL POM QUE ESTA EN DISCORD

## Carpeta target

La carpeta `target` es utilizada por Maven para almacenar todos los artefactos generados durante las fases de construcción, pruebas y empaquetado del proyecto. Actúa como un directorio temporal donde Maven maneja todo el trabajo pesado del proceso de construcción.

En esta carpeta se suelen encontrar las clases compiladas, archivos jar, registros, archivos temporales, etc. La estructura de nuestra carpeta es la siguiente:

target/
├── classes/ <- Se encuentran las clases utilizadas en el proyecto anterior
│    
├── maven_archiver/ <- Propiedades del proyecto
 |
├── maven_status/ <- Estado del proyecto
 |
├── target/ComputeEntities-0.1.jar <- .jar con el proyecto empaquetado

# Modificaciones al lab anterior para soportar Apache Spark

No se que modificaron para adaptar el lab jijo

# Implementación con API de Spark


 
# Cómo compilarlo

Sea `APP_HOME` el directorio donde está este archivo `README.md`

```bash
$ cd $APP_HOME
$ mvn install
```
Eso descarga las dependencias que necesitamos y crea el directorio `target` donde queda el jar que nos interesa.
En mi caso:
```
$ ls target
classes  generated-sources  generated-test-sources  maven-archiver  maven-status  test-classes  WordCount-0.1.jar
```

# Cómo usarlo

En el directorio `$SPARK_HOME` pueden usar `bin/spark-submit`:
```bash
$ bin/spark-submit  --master local[2] $APP_HOME/target/WordCount-0.1.jar  $APP_HOME/data/quijote.txt
```

Si no quieren ver la información de spark pueden redirigir `stderr` a `/dev/null`:
```bash
$ bin/spark-submit  --master local[2] $APP_HOME/target/WordCount-0.1.jar  $APP_HOME/data/quijote.txt 2>/dev/null
```