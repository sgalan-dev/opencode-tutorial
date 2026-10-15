---
title: prompt de exploración para implementar cursos y escuelas 
description: contiene el prompt inicial para hacer una exploración para crear 2 entidades nuevas de curso y centro educativo
author: sgalan
date: Oct 2026
version: 1.0
---

## Descripción necesidad funcional
Necesitamos añadir dos entidades nuevas en la aplicación: Curso (Course), Centro Educativo (Center).

### Jerarquía de entidades y URLs
La jerarquía de entidades debe quedar reorganizada así:
* Centros
    * Cursos
        * Grupos
    * Estudiantes
* Personas

Descripción de la jerarquía:
1. El centro es la entidad principal de la que cuelgan el resto de entidades
2. Todos los cursos pertenecen a un centro educativo.
3. Todos los grupos pertenecen a curso, que a su vez pertenece a un centro educativo.
4. Los estudiantes pertenecen a un centro. Pueden estar asignados a N grupos, pero no a mas de un grupo del mismo curso. 
5. Las personas son totalmente independientes del centro educativo. Son entidades únicas y globales.


A continuación pongo ejemplos de esta jerarquía de urls en base a su entidad:
|URL|descripción|
|-|-|
|/api/v1/centers/|url base para centros|
|/api/v1/persons/|url base para personas|
|/api/v1/centers/{centerId}/students/|url base para estudiantes|
|/api/v1/centers/{centerId}/courses/|url base para cursos|
|/api/v1/centers/{centerId}/courses/{courseId}/groups/|url base para grupos|

### Operaciones adicionales

Adicionalmente de los cruds que nos permitan gestionar cada entidad, necesitamos:
1. Endpoint para matricular/desmatricular a un estudiante a un grupo. Estas operaciones serán POST y DELETE sobre la url /api/v1/centers/{centerId}/courses/{courseId}/groups/students/{studentId}
2. Endpoint para ver los estudiantes matriculados a un grupo. Será una operación GET sobre la url /api/v1/centers/{centerId}/courses/{courseId}/groups/students/
3. Endpoint con el detalle completo de un grupo, donde veremos los datos del curso, del grupo y estudiantes matriculados con sus datos personales sobre /api/v1/centers/{centerId}/courses/{courseId}/groups/{groupId}/students/detail
4. Endpoint con el detalle completo de un estudiante en /api/v1/centers/{centerId}/students/{studentId}/detail, donde veremos los datos del estudiante, los detalles del centro al que pertenece, los detalles de su persona asociada y el detalle de los grupos y cursos a los que pertenece.

## Objetivo
1. Reorganizar los endpoints para que sigan una jerarquía coherente a nivel de urls y tal y como se detalla en la necesidad funcional.
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
