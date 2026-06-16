# 📌 Módulo de Registro de Asistencia (Profesor)

## Flujo completo

1. El profesor ingresa y selecciona el perfil **Profesor**.
2. Selecciona su sección (ej. `3°A`).
3. El sistema muestra la lista de estudiantes con los estados del día actual.
4. El profesor marca o corrige estados (**Presente**, **Ausente**, **Tardanza**).
5. Al guardar, se envía la lista completa al backend.
6. El backend valida unicidad y reglas de negocio.
7. Se persiste la información en base de datos con fecha y hora.
8. Se responde con **201 Created**.
9. El frontend muestra una confirmación y actualiza la vista.

---

## Contrato del Endpoint

### POST `/api/attendance`

### Request

```json
{
  "sectionId": "3A",
  "date": "2026-06-15",
  "records": [
    {
      "studentId": "S1",
      "status": "PRESENT"
    },
    {
      "studentId": "S2",
      "status": "ABSENT"
    }
  ]
}```

## Responses

| Código | Descripción |
|----------|-------------|
| `201 Created` | Éxito |
| `400 Bad Request` | Sección inexistente o body inválido |
| `409 Conflict` | Duplicado en el mismo día |
| `403 Forbidden` | Corrección de un día anterior |

---

## Edge Cases

- Guardar dos veces el mismo día → `409 Conflict`.
- Sección inexistente → `400 Bad Request`.
- Rango inválido en reportes → `400 Bad Request`.
- Corrección de un día anterior → `403 Forbidden`.
- Estudiante no pertenece a la sección → `400 Bad Request`.

---

## Contratos de Endpoints — Coordinador

### GET `/api/attendance/summary?date=2026-06-15`

Resumen del día: presentes, ausentes y tardanzas por sección.

**Response 200:**
```json
[
  {
    "sectionId": "3A",
    "date": "2026-06-15",
    "present": 6,
    "absent": 1,
    "late": 0,
    "recorded": true
  }
]
```

| Código | Descripción |
|--------|-------------|
| `200 OK` | Éxito |
| `400 Bad Request` | Fecha con formato inválido |

---

### GET `/api/attendance/history?studentId=S1&from=2026-06-01&to=2026-06-15`

Historial de asistencia de un estudiante en un rango de fechas.

**Response 200:**
```json
[
  {
    "date": "2026-06-10",
    "sectionId": "3A",
    "status": "PRESENT"
  }
]
```

| Código | Descripción |
|--------|-------------|
| `200 OK` | Éxito |
| `400 Bad Request` | Rango inválido (`from` > `to`) o fechas mal formateadas |
| `404 Not Found` | Estudiante inexistente |

---

### GET `/api/attendance/pending?date=2026-06-15`

Secciones que aún no registraron asistencia en la fecha indicada.

**Response 200:**
```json
["3B", "4A"]
```

| Código | Descripción |
|--------|-------------|
| `200 OK` | Éxito |
| `400 Bad Request` | Fecha con formato inválido |

---

## Backend

- Arquitectura hexagonal.
- H2 (desarrollo).
- DataInitializer.
- Tests unitarios.
- Tests de integración.

---

## Frontend

- Angular 15+.
- TypeScript.
- Pantalla principal: selección de perfil.

### Módulos

#### Profesor

- Visualización de lista de estudiantes.
- Registro y actualización de asistencia.

#### Coordinador

- Reportes.
- Historial de asistencia.
- Consulta de secciones pendientes.

### Integración

- Servicios HTTP para comunicación con el backend.

### Testing

- Tests de componentes.

---

## Datos Iniciales

### Estructura Académica

- 2 grados.
- 2 secciones por grado.

### Estudiantes

- Entre 5 y 8 estudiantes por sección.

### Asistencias

- Registros correspondientes a los últimos 5 días hábiles.
- Una sección sin registros en la fecha actual.