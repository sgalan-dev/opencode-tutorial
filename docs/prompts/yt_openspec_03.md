---
title: prompt de exploración para implementar asignaturas
description: contiene el prompt inicial para hacer una exploración para crear la entidad de asignatura
author: sgalan
date: Oct 2026
version: 1.0
---

## Descripción necesidad funcional
Necesitamos crear un sistema de gestión de asignaturas (Subject). 

Una asignatura es única y pertenece a un centro.

Una asignatura está asignada a un curso. Las asignaturas pueden estar vinculadas o no a un curso, es decir, pueden estar huérfanas (pero siempre pertenecerán al centro). Una asignatura también puede estar vinculada a más de un curso.

Una asignatura tiene asignada un profesor, que es quien imparte la materia de esa asignatura, pero no es obligatorio que tenga esa asignación.

Debe haber un mecanismo (tabla) que nos relacione el curso con la asignatura y con el profesor que la imparte.

## Objetivo
Implementar los diferentes controladores y endpoints en la aplicación para resolver esa necesidad funcional al completo.

## Consideraciones
Recuerda que existen scripts de liquibase que versionan el modelo de datos. Es imprescindible mantenerlos actualizados y coherentes con el desarrollo. Añade datos de ejemplo.

Recuerda implementar los endpoints de `/detail` para la consultas detalladas si fuera necesario. 

## Modelo de datos

### Subjects

|campo|descripcion|
|-----|-----------|
|id|numérico autoincremental identificador de la asignatura|
|name|texto con el nombre de la asignatura, por ejemplo Matemáticas|