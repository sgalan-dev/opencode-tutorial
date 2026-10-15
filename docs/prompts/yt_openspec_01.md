---
title: prompt de exploración para implementar cursos y escuelas 
description: contiene el prompt inicial para hacer una exploración para crear 2 entidades nuevas de curso y centro educativo
author: sgalan
date: Oct 2026
version: 1.0
---

## Descripción necesidad funcional
Necesitamos añadir dos entidades nuevas en la aplicación: Curso (Course), Centro Educativo (Center).

La jerarquía de entidades debe quedar reorganizada así:
* Centros
    * Cursos
        * Grupos
    * Estudiantes
* Personas

Sobre la jerarquía:
1. El centro es la entidad principal de la que cuelgan el resto de entidades
2. Todos los cursos pertenecen a un centro educativo.
3. Todos los grupos pertenecen a curso, que a su vez pertenece a un centro educativo.
4. Los estudiantes pertenecen a un centro. Pueden estar asignados a N grupos, pero no a mas de un grupo del mismo curso. 
5. Las personas son totalmente independientes del centro educativo. Son entidades únicas.

Adicionalmente de los cruds que nos permitan gestionar cada entidad, necesitamos:
1. Endpoint para matricular/desmatricular a un estudiante a un grupo.
2. Endpoint que nos permita consultar los estudiantes matriculados a un grupo.
3. Endpoint que nos permita ver los grupos existentes para un curso concreto.
4. Para TODOS los endpoints de consulta que devuelvan una relación de ids, añade un endpoint `/detail` que incluya los detalles de las entidades relacionadas. Por ejemplo, el endpoint de consulta de estudiantes devuelve una relación de ids. Este nuevo endpoint de detalle devolverá el detalle completo del estudiante:
    * si tiene datos de centro, devolverá todos los campos del centro.
    * si tiene persona, devolverá todos los datos de la persona como el nombre, apellidos, etc.
    * así para todas las entidades asociadas.

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
