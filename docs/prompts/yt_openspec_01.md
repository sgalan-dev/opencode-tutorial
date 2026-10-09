---
title: prompt de exploración para implementar cursos y escuelas 
description: contiene el prompt inicial para hacer una exploración para crear 2 entidades nuevas de curso y escuela
author: sgalan
date: Oct 2026
version: 1.0
---

## Descripción necesidad funcional
Necesitamos añadir tres entidades nuevas en la aplicación: Curso (Course), Edición (Edition), Centro Educativo (Center). Actualmente, en la aplicación existen las entidades persona, estudiante y grupo.

El grupo es la entidad que representa a un conjunto de estudiantes que están siendo formados en un curso. Por lo tanto, un curso puede tener N grupos.

Un curso pertenece a una edición. Una edición representa una versión de ese curso. Por ejemplo: "Edición 2026-2027" representaría esa versión del curso concreto para el periodo lectivo 2026-2027. Pueden haber N ediciones de un mismo curso a modo de histórico.

Por lo tanto, será necesario hacer una vinculación entre el curso, la edición y el grupo.

El curso, pertenece a un centro educativo.

## Objetivo
Implementar los diferentes controladores y endpoints en la aplicación.

## Campos de las nuevas entidades

### Curso

|campo|descripcion|
|-----|-----------|
|id|numérico autoincremental|
|name|alfanumérico que se corresponde con el nombre del curso|

### Edición
|campo|descripcion|
|-----|-----------|
|id|numérico autoincremental|
|id_course|numérico vinculado al curso|
|name|alfanumérico que se corresponde con el nombre de la edición|

### Centro
|campo|descripcion|
|-----|-----------|
|id|numérico autoincremental|
|name|alfanumérico que se corresponde con el nombre del centro educativo|
