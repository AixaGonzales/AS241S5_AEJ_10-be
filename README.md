# ChatGPT + Image Analyzer con Spring Boot

Servicio Cognitivo de RapidAPI basado en modelos de inteligencia artificial que permite generar respuestas conversacionales mediante ChatGPT y procesar imágenes mediante una API de visión artificial. El sistema envía solicitudes a servicios externos, obtiene respuestas automáticas y almacena las interacciones en una base de datos MongoDB de forma reactiva.

Devuelve respuestas de texto generadas por el modelo y resultados de análisis de imágenes según la API utilizada.

## 1. Cognitive Services

Rapid API - ChatGPT  
Generación de respuestas conversacionales mediante modelos de lenguaje natural.

API de Imágenes (Visión Artificial)  
Procesamiento y análisis de imágenes mediante servicios externos.

## 2. Spring Boot

Java: JDK 17  
IDE: IntelliJ IDEA | Visual Studio Code  
Maven: Apache Maven  
Frameworks: Spring Boot  

## 3. Maven Dependencias

Spring WebFlux | Data MongoDB Reactive | Project Reactor

```xml
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-webflux</artifactId>
</dependency>

<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-data-mongodb-reactive</artifactId>
</dependency>

<dependency>
    <groupId>io.projectreactor</groupId>
    <artifactId>reactor-test</artifactId>
    <scope>test</scope>
</dependency>
