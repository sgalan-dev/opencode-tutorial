---
title: prompt de exploración para implementar cursos y escuelas 
description: contiene el prompt inicial para hacer una exploración para crear 2 entidades nuevas de curso y centro educativo
author: sgalan
date: Oct 2026
version: 1.0
---

## Descripción necesidad funcional
Necesitamos añadir dos entidades nuevas en la aplicación: Curso (Course), Centro Educativo (Center). Actualmente, en la aplicación existen las entidades persona, estudiante y grupo.

El grupo es la entidad que representa a un conjunto de estudiantes que están siendo formados en un curso. Pueden haber N grupos de un mismo curso. Por lo tanto, un curso puede tener N grupos.

Por lo tanto, será necesario hacer una vinculación entre el curso y el grupo.

El curso, pertenece a un centro educativo.

Hay otras entidades que requieren vinculación con el centro educativo, como por ejemplo los estudiantes. Revisa qué entidades existentes deben estar vinculadas.

La jerarquía de entidades debe quedar reorganizada así:
* Centros
    * Cursos
        * Grupos
    * Estudiantes
* Personas


## Objetivo
1. Reorganizar los endpoints para que sigan una jerarquía coherente a nivel de urls.
2. Implementar los diferentes controladores y endpoints en la aplicación para las nuevas entidades curso y centro educativo.

## Consideraciones
Recuerda que existen scripts de liquibase que versionan el modelo de datos. Es imprescindible mantenerlos actualizados y coherentes con el desarrollo. Añade datos de ejemplo.

Los endpoints de la aplicación deben mantener coherencia con la jerarquía de entidades. Modifica los que consideres necesarios y aplica esta regla para los nuevos.

## Modelo de datos

### Curso

|campo|descripcion|
|-----|-----------|
|id|numérico autoincremental|
|name|alfanumérico que se corresponde con el nombre del curso|
|id_center|numérico que relaciona al curso con el centro educativo|

### Centro
|campo|descripcion|
|-----|-----------|
|id|numérico autoincremental|
|name|alfanumérico que se corresponde con el nombre del centro educativo|
