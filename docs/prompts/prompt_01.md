---
title: prompt creación inicial de api en springboot
description: contiene el prompt inicial para crear una api en springboot mediante opencode
author: sgalan
date: Aug 2026
version: 1.0
---

# Objetivo
Crea una aplicación en java + springboot que nos permita gestionar las siguientes entidades: `student`, `group`, `person`.
Debe contener las operaciones básicas de un CRUD convencional para cada entidad.

# Definición del modelo
La entidad `student` debe tener los siguientes campos:
`id` autoincremental e identificador del estudiante.
`person_id` identificador de los datos administrativos del alumno relativos a su persona.
`group_id` identificador del grupo al que pertenece.

La entidad `group` debe tener los siguientes campos:
`id` autoincremental e identificador del grupo.
`name` nombre del grupo, por ejemplo `Grupo Programación 1`.

La entidad `person` debe tener los siguientes campos:
`id` autoincremental e identificador de la persona.
`name` nombre de la persona.
`surname` apellido de la persona.

# Tus tareas
Crear los controladores y endpoints para realizar la gestión de las entidades definidas.

# Reglas, convenciones y limitaciones
Un estudiante solo puede tener una persona.
Un estudiante puede pertenecer a más de un grupo al mismo tiempo.

NO conectaremos con ningun proveedor de bbdd externo, usaremos una base de datos local H2.
Utiliza `liquibase` para versonar la bbdd y para introducir valores iniciales.

Utiliza estádares y buenas practicas de java.
Utiliza DDD y arquitectura hexagonal pura.
Utiliza principios SOLID.

Añade en el código comentarios que aporten valor real y siempre en inglés.
Añade javadoc y documenta TODOS los métodos que crees, independientemente de si son publicos o privados.

NO generes ficheros de documentación.
NO implementes tests unitarios.

Si consideras que alguna de estas reglas debe ser considerada en cada iteración, puedes incluirlo en el AGENTS.md del proyecto.