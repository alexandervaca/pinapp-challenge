# 📌 Proyecto Asistec

Sistema de asistencia escolar con perfiles **Profesor** y **Coordinador**.

## 📂 Estructura

```text
challenge/
├── backend/
├── frontend/
├── docs/
└── README.md
```

## 🚀 Ejecución

### Requisitos previos

- Java 17+
- Node.js 18+
- Maven 3.8+

### Levantar todo en desarrollo (H2 en memoria)

```bash
# Terminal 1 — Backend
cd backend && ./mvnw spring-boot:run -Dspring-boot.run.profiles=dev

# Terminal 2 — Frontend
cd frontend && npm install && npm start
```

La app estará disponible en:
- Frontend: http://localhost:4200
- Backend API: http://localhost:8080/api
- H2 Console: http://localhost:8080/h2-console (datasource: `jdbc:h2:mem:asistecdb`)

## 🧪 Tests

### Backend

```bash
cd backend && ./mvnw test
```

Cubre: registro exitoso, duplicado mismo día, corrección día anterior, secciones pendientes, e integración end-to-end.

### Frontend

```bash
cd frontend && npm test -- --watch=false --browsers=ChromeHeadless
```

Cubre: lista renderizada con estados, botón guardar deshabilitado sin cambios, y resumen del coordinador con totales correctos.

## 🧩 Decisiones de Diseño

### ¿Cómo modelé la relación Sección → Estudiante → Registro?

- `Grade` representa el grado escolar (ej: 3°, 4°).
- `Section` pertenece a un `Grade` y agrupa estudiantes (ej: 3°A).
- `Student` pertenece a una sola `Section`.
- `AttendanceRecord` registra el estado de un `Student` en una `Section` para una `date` específica.

La relación `Section` en `AttendanceRecord` es redundante (se puede derivar desde `Student`), pero se incluyó para simplificar queries de reportes sin JOINs adicionales.

### ¿Cómo garanticé la unicidad por estudiante/día?

Índice único compuesto a nivel de base de datos:

```sql
ALTER TABLE attendance_record
  ADD CONSTRAINT uq_student_date UNIQUE (student_id, date);
```

Y validación en la capa de servicio antes de persistir, que lanza una excepción de dominio mapeada a `409 Conflict`. La restricción de BD es la última línea de defensa.

### ¿Qué estrategia usé para datos en tiempo real?

Polling desde el frontend cada 30 segundos en los componentes del Coordinador usando `interval` de RxJS combinado con `switchMap` y `HttpClient`. Se eligió polling sobre WebSockets por simplicidad y porque el caso de uso no requiere latencia sub-segundo. La arquitectura permite reemplazarlo por WebSockets sin cambios en el backend (solo agregar un `@MessageMapping`).

### ¿Qué dejé fuera intencionalmente?

- **Autenticación real**: el perfil se selecciona en el frontend sin validación JWT.
- **Autorización por sección**: cualquier profesor puede ver cualquier sección. Con más tiempo, agregaría `@PreAuthorize` por roles.
- **Paginación**: los reportes devuelven todos los registros. En producción, se necesitaría `Pageable` en los endpoints de historial.
- **Manejo de zona horaria**: las fechas se procesan en UTC. En un sistema real se haría en la zona del colegio de ser necesario.

### ¿Qué haría diferente con más tiempo?

- WebSockets con STOMP para actualizaciones push al Coordinador.
- Separar `AttendanceRecord` en un aggregate propio con eventos de dominio.
- Agregar Flyway para gestión de migraciones en lugar de scripts SQL manuales.
- Tests E2E con Cypress para el flujo completo profesor → coordinador.