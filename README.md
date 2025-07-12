# 📚 Libro y Punto - Redefiniendo la Experiencia de Lectura

# Libro y Punto
---

## 📄 Tabla de Contenidos

1.  [Acerca del Proyecto](#1-acerca-del-proyecto)
2.  [Visión y Propósito](#2-visión-y-propósito)
3.  [Características Principales](#3-características-principales)
    * [Sistema de Gamificación](#31-sistema-de-gamificación)
    * [QR-ID Personalizado](#32-qr-id-personalizado)
    * [Explorador y Catálogo de Libros](#33-explorador-y-catálogo-de-libros)
    * [Sección de Noticias](#34-sección-de-noticias)
    * [Desafíos](#35-desafíos)
    * [Nuevo Kiosco Inteligente](#36-nuevo-kiosco-inteligente)
    * [Gestión de Datos y Roles Administrativos](#37-gestión-de-datos-y-roles-administrativos)
    * [Eventos (EXPO Libro y Punto)](#38-eventos-expo-libro-y-punto)
    * [Reseñas de Libros](#39-reseñas-de-libros)
    * [Programa "¡Dona un libro!"](#310-programa-dona-un-libro)
    * [Sistema de Lista de Espera](#311-sistema-de-lista-de-espera)
6.  [Estado del Proyecto y Hoja de Ruta](#6-estado-del-proyecto-y-hoja-de-ruta)
7.  [Cómo Empezar (Desarrollo)](#7-cómo-empezar-desarrollo)
8.  [Contribuciones](#8-contribuciones)
9.  [Licencia](#9-licencia)
10. [Contacto](#10-contacto)

---

## 1. Acerca del Proyecto

"Libro y Punto" es una plataforma digital integral que está siendo desarrollada para transformar la experiencia de lectura y la gestión del Centro de Recursos para el Aprendizaje (CRA) en instituciones educativas, comenzando por la Escuela Francia. Su propósito es modernizar el acceso a los libros, fomentar el hábito de la lectura de una manera interactiva y gratificante, y construir una comunidad lectora activa.

Inicialmente concebida y prototipada con Glide, el proyecto está migrando hacia una arquitectura más robusta y escalable utilizando Firebase como backend y un stack moderno de desarrollo web (TypeScript, Next.js, Tailwind CSS) para un control total sobre el producto, optimización de costos y máxima flexibilidad en la implementación de funcionalidades.

## 2. Visión y Propósito

* **Modernización:** Digitalizar y modernizar los procesos de préstamo, devolución y exploración de libros.
* **Fomento Lector:** Incentivar la lectura a través de la gamificación, desafíos y una experiencia de usuario atractiva.
* **Comunidad:** Crear un espacio interactivo donde los estudiantes puedan compartir, colaborar y participar en eventos literarios.
* **Eficiencia:** Optimizar la administración del CRA mediante herramientas digitales intuitivas y seguras.
* **Escalabilidad:** Diseñar una plataforma que pueda ser adaptable y ofrecida a otras instituciones educativas en el futuro.

## 3. Características Principales

### 3.1. Sistema de Gamificación

* **Puntos:** Los usuarios ganan puntos por diversas interacciones: devoluciones de libros (limpios y a tiempo), lectura de libros destacados (puntos extra), participación en desafíos, escritura de reseñas válidas y donaciones de libros. Penalizaciones por devoluciones tardías o libros sucios.
* **Niveles:** Un sistema de progresión con 8 niveles (hasta 210 puntos), donde cada nivel desbloquea beneficios exclusivos.
* **Beneficios y Niveles:** Una sección dedicada en la pestaña "Tú" que detalla las recompensas de cada nivel, desde insignias y marcapáginas digitales hasta acceso prioritario en listas de espera, aumento de límite de préstamos, y voz en la selección de libros o contribución a noticias.

### 3.2. QR-ID Personalizado

* Cada usuario tiene un código QR único vinculado a su perfil (email, RUT, curso, etc.).
* Facilita la identificación rápida en el CRA y en el Kiosco, agilizando préstamos, devoluciones y la acumulación de puntos.

### 3.3. Explorador y Catálogo de Libros

* **Pestaña "Explorar":** Centraliza la búsqueda y descubrimiento de libros.
* **Catálogo:** Muestra todos los libros disponibles (físicos y digitales).
* **Destacados:** Libros recomendados o de alta popularidad que pueden otorgar puntos extra.
* **Digital:** Catálogo específico de libros en formato electrónico (`.epub`), con funcionalidad de descarga (experimental) y recomendaciones de lectores compatibles.
* **Detalle del Libro:** Información completa, disponibilidad, reseñas y estado de préstamo.

### 3.4. Sección de Noticias

* Un feed de noticias para mantener a los usuarios informados sobre actualizaciones de la app, nuevos libros, desafíos, eventos escolares y avisos importantes. Permite la colaboración de usuarios de alto nivel en la redacción de artículos.

### 3.5. Desafíos

* Eventos mensuales y globales que requieren la colaboración de toda la escuela para alcanzar un objetivo común, fomentando el uso de la app y la biblioteca de forma divertida y cooperativa. Los participantes ganan puntos al completar los desafíos.

### 3.6. Nuevo Kiosco Inteligente (v7.0.0b)

* Una interfaz dedicada en el CRA para agilizar las operaciones de la biblioteca. Permite:
    * **Devolución Rápida:** Registrar devoluciones de libros de forma eficiente.
    * **Préstamo Rápido:** Agilizar la salida de libros.
    * **Explorar Catálogo:** Navegar el catálogo de libros directamente desde el kiosco.
    * **Revisar Perfil:** Consultar puntos, nivel y préstamos activos del usuario.
    * **Acceso a "¡Dona un libro!":** Registrar donaciones de libros.
    * **Acceso a Noticias:** Ver los últimos anuncios de la app.
* Requiere la activación de un **PIN de Kiosco** personal desde la pestaña "Tú" del usuario para mayor seguridad.

### 3.7. Gestión de Datos y Roles Administrativos

* **Pestaña "Administrar":** Acceso restringido a usuarios con roles DLS o CGE.
* Permite la gestión de usuarios y la administración del catálogo de libros (añadir, editar, eliminar).
* **Validación de IDs Únicos:** Sistema crucial para asignar y validar IDs únicos a cada libro, vital para el correcto funcionamiento del sistema de puntos, préstamos y el catálogo.

### 3.8. Eventos (EXPO Libro y Punto)

* Un evento central que promueve la visibilidad y adopción de la app.
* Incluye un **"Triplicador de Puntos por EXPO"** para incentivar la participación activa.
* Ofrece diversas actividades: charlas con escritores, concursos, torneos, feria, shows en vivo, cosplay literario y el LYP Book Swap (intercambio de libros).
* Implementa una estricta política de **"tolerancia cero al acoso"** para garantizar un ambiente seguro.

### 3.9. Reseñas de Libros

* Los usuarios pueden escribir reseñas y calificar libros una vez que los han devuelto.
* Las reseñas válidas otorgan puntos y contribuyen a la visibilidad y al ranking de los libros en el catálogo ("Destacados", "TOP").

### 3.10. Programa "¡Dona un libro!"

* Facilita la contribución de libros a la biblioteca por parte de los usuarios, otorgando puntos por cada donación exitosa y fomentando la colaboración comunitaria.

### 3.11. Sistema de Lista de Espera

* Gestiona la demanda de libros populares. Los usuarios de Nivel 5 o superior tienen **acceso prioritario** para arrendar libros en lista de espera sin pasar por la cola.

## 6. Estado del Proyecto y Hoja de Ruta

El proyecto se encuentra en una fase de transición crucial, desarrollando la versión robusta y escalable sobre Firebase.

* **Versión Actual en Glide:** `libroypunto.glide.page` (versión operativa actual).
* **Versión 7.0.0b:** La última actualización beta, incorporando el nuevo Kiosco y mejoras administrativas, es un paso fundamental antes del lanzamiento de la Versión 1.0.0 (Release).
* **Próximas Versiones (Hoja de Ruta):** La versión 7.0.0b es la antesala a la 1.0.0. Ya se está trabajando en la **versión 6.2.0-TZ**, que incluirá la función de **"Favoritos"** para que los usuarios puedan guardar sus libros preferidos o pendientes.

## 7. Cómo Empezar (Desarrollo)
*Pronto!!*

## 8. Contribuciones

Idk JASJKAKJAKJASJKASK

## 9. Licencia

Este proyecto está licenciado bajo la Licencia MIT. Consulta el archivo `LICENSE` para más detalles.

## 10. Contacto

* **Creador:** Martín Ávila
* **Correo Electrónico:** martinavilads@gmail.com

---
