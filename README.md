# Sistema de IA con Spring Boot + MongoDB + Integración de APIs

Este proyecto es una API desarrollada con Spring Boot (WebFlux) que integra servicios de inteligencia artificial utilizando dos APIs externas:

- API de ChatGPT mediante RapidAPI para generación de respuestas conversacionales.
- API de imágenes (visión artificial) para procesamiento/análisis de imágenes.

Además, todas las interacciones se almacenan en una base de datos MongoDB de forma reactiva.

El sistema permite enviar mensajes a un modelo de IA, obtener respuestas automáticas, procesar imágenes mediante servicios externos y guardar los resultados en la base de datos.

---

## 1. APIs utilizadas

- RapidAPI - ChatGPT: generación de texto conversacional mediante modelos de lenguaje natural.
- API de imágenes (visión artificial): análisis y procesamiento de imágenes a través de servicios externos.

---

## 2. Tecnologías utilizadas

- Java JDK 17  
- Spring Boot  
- Spring WebFlux  
- Spring Data MongoDB Reactive  
- Project Reactor  
- WebClient  
- Maven  
- MongoDB  

---

## 3. Dependencias principales

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
