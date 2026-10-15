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

Una misma persona puede ser profesor en más de un centro. La persona no cambia.

No puede haber un profesor asociado a la misma persona dentro del mismo centro.

También necesitamos poder asignar tutores o responsables de un grupo (que serán profesores del centro). 

Puede haber un solo tutor por grupo, considera añadir una a columna al grupo para vincularlo con el profesor que hará de tutor.

Necesitamos un sistema que nos permita asignar y desasignar tutores a un grupo. 

Ya existe una asignación de estudiantes a grupos, puedes basarte en esta asignación ya existente para crear la nueva asignación de tutores a grupos.

## Objetivo
Implementar los diferentes controladores y endpoints en la aplicación para gestionar cada una de las nuevas entidades.

## Consideraciones
Recuerda que existen scripts de liquibase que versionan el modelo de datos. Es imprescindible mantenerlos actualizados y coherentes con el desarrollo. Añade datos de ejemplo

## Modelo de datos

### Professor

|campo|descripcion|
|-----|-----------|
|id|numérico autoincremental|
|id_person|numérico que relaciona al profesor con la persona|
|id_center|numérico que relaciona al profesor con el centro educativo|
