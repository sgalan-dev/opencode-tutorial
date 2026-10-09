---
name: docs-catala
description: 'Política de documentació del projecte. Use this skill whenever the user asks to create, write, generate, update or document anything related to the application: READMEs, guides, tutorials, API docs, architecture notes, ADRs, onboarding instructions, Javadoc-heavy module docs, or any Markdown/text file whose purpose is to explain the project to humans. All documentation MUST live under the `docs/` directory at the project root AND be written in Catalan. Make sure to use this skill even when the user only asks for "a quick README" or "document this module" — folder location and language are non-negotiable.'
---

# Política de documentació

Aquest projecte té dues regles innegociables sobre tota la documentació que
es generi per a l'aplicació:

1. **Ubicació**: sempre dins la carpeta `docs/` a l'arrel del projecte.
2. **Idioma**: sempre en **català**.

Aquestes regles existeixen per mantenir la documentació coherent i trobable.
Sense elles, els fitxers tendeixen a escampar-se per `src/`, per l'arrel o per
subcarpetes aleatòries, i l'idioma canvia segons qui els escriu.

## Quan aplica aquesta skill

Activa-la sempre que la petició impliqués produir o modificar documentació
dirigida a persones:

- READMEs (de projecte, de mòdul, de feature)
- Guies d'ús, tutorials, how-tos
- Documentació d'API (endpoints, DTOs, codis d'error)
- Notes d'arquitectura, ADR, decisions de disseny
- Documents d'onboarding per a nous membres de l'equip
- Changelogs, notes de versió
- Qualsevol fitxer Markdown/text dins `docs/`

No aplica a: comentaris curts dins del codi, missatges de commit, PRs
(seguir la configuració del repo), o res que no sigui documentació
d'aplicació visible per humans.

## Regles

### 1. Ubicació

- Tots els fitxers de documentació van dins `docs/`.
- Si la carpeta `docs/` no existeix, crea-la.
- Suborganitza dins `docs/` segons el tema:
  - `docs/README.md` — índex general (crea'l sempre que existeixin altres docs).
  - `docs/architecture/` — arquitectura, patrons, diagrames.
  - `docs/api/` — documentació de l'API REST.
  - `docs/guides/` — guies d'ús i tutorials.
  - `docs/decisions/` — ADRs i decisions de disseny.
  - `docs/<modul>/` — documentació específica d'un mòdul o feature.
- **Mai** no posis documentació a:
  - l'arrel del projecte (`/README.md`, `/CHANGELOG.md` ...), excepte un
    `README.md` mínim a l'arrel que apunti a `docs/README.md`.
  - `src/` (la documentació no viu amb el codi).
  - qualsevol carpeta que no sigui `docs/`.

Si l'usuari demana explícitament un fitxer a una altra ubicació, explica-li
la política i proposa l'equivalent dins `docs/` abans de crear res.

### 2. Idioma

- Tots els documents nous s'escriuen en **català natural**.
- Això inclou: títols, encapçalaments, text del cos, descripcions, exemples
  dins de blocs de codi (comentaris i strings visibles), metadades YAML/Markdown
  (resum, autor...).
- Evita traduccions literals de l'anglès. Escriu com escriuries un text
  original en català.
- Si el projecte ja conté documentació en un altre idioma, tradueix-la o
  estandarditza't en català — no barregis idiomes dins el mateix document.
- Noms propis de tecnologia (Spring Boot, JPA, REST, DTO, Java...) es poden
  deixar en anglès perquè són termes tècnics reconeguts.

## Plantilles

Adapta-les al que calgui, però conserva l'estructura bàsica.

### Índex general (`docs/README.md`)

```markdown
# Documentació del projecte

## Contingut

- [Arquitectura](architecture/README.md)
- [API](api/README.md)
- [Guies](guides/README.md)
- [Decisions](decisions/README.md)

## Com començar

...
```

### Guia (`docs/guides/<nom>.md`)

```markdown
# <Títol de la guia>

## Objectiu
...

## Requisits previs
...

## Passos
1. ...
2. ...

## Exemples
\`\`\`
...
\`\`\`

## Solució de problemes
...
```

### ADR (`docs/decisions/NNNN-<titol-curt>.md`)

```markdown
# NNNN. <Títol de la decisió>

## Estat
Proposada | Acceptada | Rebutjada | Substituïda per NNNN

## Context
...

## Decisió
...

## Conseqüències
...
```

## Comandes útils

- Crear la carpeta: `mkdir -p docs/<subcarpeta>`
- Llistar l'estructura: `find docs -type f -name "*.md"`

## Anti-patrons a evitar

- Escriure un README a l'arrel en lloc d'apuntar a `docs/README.md`.
- Barrejar anglès i català dins del mateix document.
- Guardar notes soltes a `notes.md`, `TODO.md`, `idea.md` fora de `docs/`.
- Posar diagrames o captures dins `src/` o a l'arrel.
- Suposar que la regla de l'idioma és "opcional" per a fitxers petits.