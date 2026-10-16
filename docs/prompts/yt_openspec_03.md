---
title: prompt de exploración para implementar asignaturas
description: contiene el prompt inicial para hacer una exploración para crear la entidad de asignatura
author: sgalan
date: Oct 2026
version: 1.0
---

## Descripción necesidad funcional
Necesitamos crear un sistema de gestión de asignaturas (Subject). 

Pertenece a un curso.

Una asignatura tiene asignada un profesor, que es quien imparte la materia de esa asignatura, pero no es obligatorio que tenga esa asignación. 

Si una asignatura no tiene profesor, significa que la impartirá el tutor del grupo de ese curso, aunque esto se abordará en otro desarrollo.

Debe haber un mecanismo (tabla) que nos relacione el curso con la asignatura y con el profesor que la imparte.

### Jerarquía de entidades y URLs
La jerarquía de entidades debe quedar reorganizada así:
* Centros
    * Cursos
        * Grupos
        * Asignaturas (nuevo)
* Personas

A continuación pongo ejemplos de esta jerarquía de urls en base a su entidad:

|URL|descripción|
|-|-|
|/api/v1/centers/{centerId}/courses/{courseId}/subjects/|url base para grupos|

## Objetivo
Implementar los diferentes controladores y endpoints en la aplicación para resolver esa necesidad funcional al completo.

## Consideraciones
Recuerda que existen scripts de liquibase que versionan el modelo de datos. Es imprescindible mantenerlos actualizados y coherentes con el desarrollo. Añade datos de ejemplo.

## Modelo de datos

### Subjects

|campo|descripcion|
|-----|-----------|
|id|numérico autoincremental identificador de la asignatura|
|name|texto con el nombre de la asignatura, por ejemplo Matemáticas|