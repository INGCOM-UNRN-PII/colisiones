# Explorando Diccionarios y Colisiones de Hash

Este proyecto implementa un diccionario (mapa hash) simple en Java y explora el concepto
de colisiones de `hashCode` utilizando clases de clave diseñadas para demostración.

Se aceptan PR's para mejorar la documentación, ejemplos, tests y casos.

## Componentes del Proyecto

El proyecto principal se centra en la implementación de un diccionario básico y en la
creación de tipos de datos que permiten investigar el comportamiento de las funciones
hash.

### 1. `Diccionario<K, V>`

Implementación de un diccionario (mapa hash) simple con las siguientes características:

* **Tamaño Fijo:** Utiliza un array de "baldes" (buckets) con un número fijo definido en
  (`NUM_BUCKETS`).
* **Hashing Simple:** La posición de un elemento se determina aplicando la operación de
  módulo (`Math.floorMod`) al `hashCode()` de la clave.
* **Manejo de Colisiones:** Si se intenta insertar un nuevo par clave-valor en un balde
  que ya está ocupado por *cualquier* otra entrada, la operación es rechazada y se lanza
  una `ColisionException`. No implementa estrategias de resolución de colisiones como
  encadenamiento o sondeo.
* **Claves Nulas:** No permite claves nulas. Intentar usar una clave nula en cualquier
  operación (`put`, `get`, `remove`, `containsKey`) lanza una `LlaveNulaException`.
* **Métodos Principales:** Incluye los métodos típicos de un mapa: `put`, `get`, `remove`,
  `containsKey`, `size`, `isEmpty`.

Esta implementación es intencionadamente simple para demostrar el impacto directo de las
colisiones en un diseño básico.

### 2. `LlaveDefectuosa`

Una clase de clave diseñada específicamente para pruebas y demostraciones de colisiones de
hash.

* **`hashCode()` Controlado:** Permite especificar un valor de `hashCode` fijo en su
  constructor, independientemente del contenido del objeto.
* **`equals()` Basado en Nombre:** La igualdad entre dos `LlaveDefectuosa` se basa
  únicamente en su campo `name`.
* **Utilidad:** Es útil para crear escenarios de prueba donde objetos diferentes (`equals`
  devuelve `false`) tienen el mismo `hashCode`, forzando colisiones en estructuras de
  datos basadas en hash.

### 3. `ObjetoSimple`

Otra clase de clave simple con un ID numérico y un nombre de cadena.

#### `equals()` y `hashCode()` Estándar:

Implementa `equals` y `hashCode` de manera estándar, utilizando ambos campos (`id` y
`nombre`).

#### Método Fábrica Estático (`crearSiguiente()`):

Incluye un método estático de fábrica que facilita la creación secuencial de instancias
únicas:

* Mantiene un contador interno estático.
* Asigna el siguiente valor del contador como `id`.
* Genera el `nombre` convirtiendo el `id` a una cadena en base 36, esto es, como una
  patente de auto; con números y letras.
* Este método es ideal para generar rápidamente un gran número de objetos *distintos*
  para pruebas de rendimiento y colisiones.

### 4. Búsqueda de Colisiones

La clase `ColisionApp` (o similar) demuestra cómo encontrar colisiones de `hashCode`
utilizando la fábrica de `ObjetoSimple`.

* **Generación Masiva:** Utiliza el método fábrica `ObjetoSimple.crearSiguiente()` para
  crear un gran número de instancias de `ObjetoSimple`.
* **Agrupación por Hash:** Almacena estos objetos en una estructura de datos (como un
  `HashMap`) donde la clave es el `hashCode` del objeto y el valor es una lista de todos
  los objetos que produjeron ese `hashCode`.
* **Detección:** Itera sobre la estructura agrupada e identifica aquellos `hashCode`s que
  tienen asociados más de un objeto. Cada `hashCode` con múltiples objetos representa una
  colisión.
* **Reporte:** Imprime los `hashCode`s donde se encontraron colisiones y los detalles de
  los objetos involucrados.

Este proceso ilustra que las colisiones de `hashCode` son normales y esperadas en
estructuras de datos basadas en hash (especialmente con un número finito de posibles
valores de hash), pero un buen diseño de `hashCode` busca minimizar su frecuencia para
mantener un rendimiento óptimo.

## Estructura del Proyecto y Herramientas

Este proyecto está configurado utilizando Gradle e incluye herramientas de análisis de
código y cobertura:

1. **Checkstyle:** Configurado para revisar el estilo del código según un conjunto de
   reglas.
2. **PMD:** Realiza análisis estático del código para encontrar problemas comunes.
3. **SpotBugs:** Busca posibles errores (bugs) en el código Java.
4. **JACOCO:** Mide la cobertura de código de los tests unitarios.

Las reglas de estas herramientas están adaptadas para un contexto académico y pueden ser
bastante detalladas.

## Cómo Ejecutar

1. Clona el repositorio.
2. Navega al directorio del proyecto en tu terminal.
3. Puedes construir el proyecto y ejecutar los tests con:
