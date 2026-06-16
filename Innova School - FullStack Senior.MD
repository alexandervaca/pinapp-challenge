# Prueba Técnica — Full-Stack Senior Developer
## Sistema de Asistencia Escolar "Asistec"

**Tiempo estimado:** 6–8 horas
**Stack:** Java 17+ / Spring Boot 3.x · Angular 15+ / TypeScript · H2 o PostgreSQL

---

## Antes de empezar

Usar IA (Copilot, Claude Code, Cursor, etc.) está **permitido y esperado**.

Lo que evaluamos no es si lograste hacer funcionar algo — evaluamos tu criterio sobre ese código: las decisiones que tomaste, por qué las tomaste, y que puedas defenderlas en la entrevista.

---

## Contexto del sistema

Los profesores de Innova Schools toman asistencia diaria por sección. Los coordinadores académicos necesitan visibilidad del estado de asistencia por aula y grado para tomar acciones tempranas con estudiantes en riesgo.

El sistema tiene **dos perfiles de uso**:

- **Profesor**: registra y corrige la asistencia de su sección para el día actual.
- **Coordinador**: consulta reportes de asistencia por sección, grado o estudiante.

No se requiere autenticación real — basta con seleccionar el perfil/rol al ingresar a la app.

---

## Qué construir

### Módulo A — Registro de asistencia (Profesor)

- El profesor selecciona su sección y ve la lista de estudiantes con su estado de asistencia del día (presente / ausente / tardanza). Si aún no se registró asistencia hoy, todos aparecen sin marcar.
- El profesor puede marcar/cambiar el estado de cada estudiante individualmente.
- Al guardar, el sistema persiste el registro completo con fecha y hora.
- Un mismo estudiante no puede tener dos registros de asistencia para el mismo día en la misma sección.
- El profesor puede corregir un registro del día actual, pero no de días anteriores.

### Módulo B — Reportes de asistencia (Coordinador)

- El coordinador puede ver el resumen de asistencia del día: cuántos presentes, ausentes y tardanzas por sección.
- Puede consultar el historial de asistencia de un estudiante específico en un rango de fechas.
- Puede ver qué secciones aún no han registrado asistencia en el día actual.
- Los datos deben reflejar el estado en tiempo real (sin recargar la página manualmente).

### Datos de prueba iniciales

El sistema debe arrancar con:

- 2 grados, cada uno con 2 secciones (ej: 3°A, 3°B, 4°A, 4°B).
- Entre 5 y 8 estudiantes por sección.
- Registros de asistencia de los últimos 5 días hábiles para al menos 2 secciones (para que los reportes tengan datos reales).
- Al menos una sección sin registros hoy (para probar el indicador de secciones pendientes).

---

## Entregables

### 1. Repositorio GitHub
Commits descriptivos que cuenten la historia del desarrollo.

### 2. `docs/attendance-spec.md` ⬅ empieza por aquí

Antes de codificar, escribir una spec breve del módulo de registro de asistencia:

- Flujo completo en pasos numerados: desde que el profesor abre su sección hasta que el registro queda persistido.
- Contrato del endpoint de guardado: método, URL, request body, posibles responses con HTTP status codes.
- Al menos 3 edge cases que el sistema debe manejar (pensar en: ¿qué pasa si se intenta guardar dos veces el mismo día? ¿si la sección no existe? ¿si el rango de fechas del reporte es inválido?).

> Esta es la primera cosa que vamos a mirar. Simula cómo trabajamos: spec primero, código después.

### 3. `README.md`

- Instrucciones de instalación y ejecución (un solo comando para levantar, un solo comando para tests).
- Capturas de pantalla de ambos módulos.
- Sección **"Decisiones de diseño"** respondiendo:
  - ¿Cómo modelaste la relación Sección → Estudiante → Registro de asistencia?
  - ¿Cómo garantizaste que no existan dos registros del mismo estudiante para el mismo día?
  - ¿Qué estrategia usaste para que el coordinador vea datos actualizados sin recargar?
  - ¿Qué dejaste fuera intencionalmente? ¿Qué harías diferente con más tiempo?

### 4. Tests

**Backend:** Tests unitarios de la capa de servicio cubriendo al menos: registro exitoso, intento de registro duplicado en el mismo día, intento de corrección de un día anterior, y consulta de secciones sin asistencia. Más un test de integración del flujo completo de guardado end-to-end.

**Frontend:** Al menos 3 tests de componente: lista de estudiantes con estados correctamente renderizados, botón de guardar deshabilitado si no hay cambios, y que el resumen del coordinador refleja los totales correctos.

Si los tests no corren con el comando documentado en el `README.md`, no cuentan.

### 5. App desplegada
Se revisará en vivo durante la entrevista técnica.

---

## Qué evaluamos

| Qué | Cómo lo medimos |
|---|---|
| **Spec-driven thinking** | Calidad de `attendance-spec.md`: ¿pensaste antes de codificar? |
| **Criterio arquitectural** | Sección "Decisiones de diseño": ¿las decisiones son conscientes y defendibles? |
| **Modelo de datos** | ¿El esquema soporta las reglas de negocio sin hacks? |
| **Manejo de errores** | Contratos explícitos, casos borde contemplados en ambas capas |
| **Testing** | Cobertura de los casos críticos, tests con sentido |
| **Ownership del código** | ¿Puedes extenderlo y explicarlo en la entrevista? |

---

*Dudas antes de comenzar: escribir a Manuel Tejeda. mtejeda@innovaschools.lat*