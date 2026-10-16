---
title: prompt de exploración para implementar profesores
description: contiene el prompt inicial para hacer una exploración para crear la entidad profesor
author: sgalan
date: Oct 2026
version: 1.0
---

## Descripción necesidad funcional
Necesitamos crear la entidad profesor (Professor). Esta nueva entidad, al igual que ocurre con los estudiantes, estará vinculada a una persona.

Los profesores, pertenecen a un centro.

Una misma persona puede ser profesor en más de un centro. La persona no cambia, pero el profesor en sí será distinto.

No puede haber un profesor asociado a la misma persona dentro del mismo centro.

También necesitamos poder asignar tutores o responsables de un grupo (que serán profesores del centro). 

Puede haber un solo tutor por grupo, considera añadir una a columna al grupo para vincularlo con el profesor que hará de tutor.

Necesitamos un sistema que nos permita asignar y desasignar tutores a un grupo. Sugiero añadir un campo nuevo al grupo con el identificador del profesor. Con el propio crud de grupos podríamos gestionarlo.

### Jerarquía de entidades y URLs
La jerarquía de la nueva entidad debe quedar al siguiente nivel:
* Centros
    * Profesores (nuevo)
* Personas

entidad:
|URL|descripción|
|-|-|
|/api/v1/centers/{centerId}/professors/|url base para profesores|

Adicionalmente, crea un endpoint para consultar el detalle del profesor usando la url /api/v1/centers/{centerId}/professors/{professorId}/detail donde podamos consultar los datos de la persona asociada. Debe funcionar de manera similar al endpoint de detalle del estudiante.

## Objetivo
Implementar los diferentes controladores y endpoints en la aplicación para gestionar cada una de las nuevas entidades.

## Consideraciones
Recuerda que existen scripts de liquibase que versionan el modelo de datos. Es imprescindible mantenerlos actualizados y coherentes con el desarrollo. Añade datos de ejemplo.

Recuerda que si añadimos un campo nuevo al grupo para indicar el tutor, también esa información debe aparecer en los endpoints de consulta donde se muestra información del grupo, tanto identificadores como en los detalles.

## Modelo de datos

### Professor

|campo|descripcion|
|-----|-----------|
|id|numérico autoincremental|
|id_person|numérico que relaciona al profesor con la persona|
|id_center|numérico que relaciona al profesor con el centro educativo|
