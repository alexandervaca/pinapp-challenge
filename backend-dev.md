# PROMPT PARA CURSOR — Backend Spring Boot 3.x (Arquitectura Hexagonal)
# Sistema de Asistencia Escolar "Asistec" — Innova Schools
# ============================================================
# USO: Abrí Cursor en la carpeta /backend y ejecutá este prompt
# como contexto del agente. Seguí los pasos en orden.
# ============================================================

## CONTEXTO DEL PROYECTO

Estás desarrollando el backend del sistema "Asistec", una app de asistencia
escolar con dos perfiles: Profesor (registra asistencia) y Coordinador (consulta
reportes). No hay autenticación real — el perfil se selecciona en el frontend.

Stack obligatorio: Java 17+, Spring Boot 3.x, arquitectura hexagonal, H2 para
desarrollo, PostgreSQL para producción, JWT como capa opcional/deshabilitada.

---

## PASO 1 — Crear el proyecto Maven

Generá un proyecto Spring Boot 3.x con estas dependencias en el `pom.xml`:

- spring-boot-starter-web
- spring-boot-starter-data-jpa
- spring-boot-starter-security
- spring-boot-starter-validation
- com.h2database:h2 (scope: runtime)
- org.postgresql:postgresql (scope: runtime)
- io.jsonwebtoken:jjwt-api + jjwt-impl + jjwt-jackson (versión 0.11.5)
- springdoc-openapi-starter-webmvc-ui (versión 2.x)
- spring-boot-starter-test (scope: test)

Group: `com.innova`, Artifact: `asistec`, Package: `com.innova.asistec`

---

## PASO 2 — Estructura de packages (hexagonal estricta)

Creá la siguiente estructura de packages. No mezcles capas:

```
com.innova.asistec/
├── domain/
│   ├── model/
│   │   ├── Grade.java
│   │   ├── Section.java
│   │   ├── Student.java
│   │   ├── AttendanceRecord.java
│   │   └── AttendanceStatus.java
│   ├── port/
│   │   ├── in/
│   │   │   ├── SaveAttendanceUseCase.java       (interface)
│   │   │   ├── GetAttendanceSummaryUseCase.java (interface)
│   │   │   ├── GetStudentHistoryUseCase.java    (interface)
│   │   │   └── GetPendingSectionsUseCase.java   (interface)
│   │   └── out/
│   │       ├── AttendanceRecordRepository.java  (interface)
│   │       ├── SectionRepository.java           (interface)
│   │       └── StudentRepository.java           (interface)
│   └── service/
│       └── AttendanceService.java               (implementa los 4 casos de uso)
├── adapter/
│   ├── in/rest/
│   │   ├── AttendanceController.java
│   │   ├── ReportController.java
│   │   └── dto/
│   │       ├── SaveAttendanceRequest.java
│   │       ├── AttendanceRecordDto.java
│   │       ├── DailySummaryResponse.java
│   │       └── StudentHistoryResponse.java
│   └── out/persistence/
│       ├── AttendanceRecordJpaRepository.java   (extends JpaRepository)
│       ├── AttendanceRecordEntity.java          (@Entity JPA)
│       └── AttendanceRecordPersistenceAdapter.java (@Repository, implementa out ports)
└── config/
    ├── SecurityConfig.java
    ├── DataInitializer.java
    └── OpenApiConfig.java
```

---

## PASO 3 — Modelos de dominio

### `AttendanceStatus.java`
```java
public enum AttendanceStatus { PRESENT, ABSENT, LATE }
```

### `Grade.java` — Entidad JPA
Campos: `Long id` (auto), `String name` (ej: "3°").
Anotaciones: `@Entity`, `@Table(name="grade")`.

### `Section.java` — Entidad JPA
Campos: `String id` (ej: "3A"), `String name`, `Grade grade` (@ManyToOne).
Anotaciones: `@Entity`, `@Table(name="section")`.

### `Student.java` — Entidad JPA
Campos: `String id` (ej: "S1"), `String fullName`, `Section section` (@ManyToOne).
Anotaciones: `@Entity`, `@Table(name="student")`.

### `AttendanceRecord.java` — Entidad JPA
Campos:
- `Long id` (auto)
- `Student student` (@ManyToOne, @JoinColumn name="student_id")
- `Section section` (@ManyToOne, @JoinColumn name="section_id")
- `LocalDate date`
- `AttendanceStatus status` (@Enumerated(EnumType.STRING))
- `LocalDateTime createdAt` (@CreationTimestamp)

Anotaciones: `@Entity`, `@Table(name="attendance_record")`.

Restricción de unicidad (a nivel de entidad y BD):
```java
@Table(name = "attendance_record",
       uniqueConstraints = @UniqueConstraint(columnNames = {"student_id", "date"}))
```

---

## PASO 4 — Puertos de entrada (interfaces en `domain/port/in/`)

### `SaveAttendanceUseCase.java`
```java
public interface SaveAttendanceUseCase {
    void saveAttendance(String sectionId, LocalDate date, List<AttendanceRecordDto> records);
}
```

### `GetAttendanceSummaryUseCase.java`
```java
public interface GetAttendanceSummaryUseCase {
    List<DailySummaryResponse> getSummary(LocalDate date);
}
```

### `GetStudentHistoryUseCase.java`
```java
public interface GetStudentHistoryUseCase {
    List<StudentHistoryResponse> getHistory(String studentId, LocalDate from, LocalDate to);
}
```

### `GetPendingSectionsUseCase.java`
```java
public interface GetPendingSectionsUseCase {
    List<String> getPendingSections(LocalDate date);
}
```

---

## PASO 5 — Contratos REST completos

### `AttendanceController.java`

**POST `/api/attendance`** — Registrar asistencia del día

Request body:
```json
{
  "sectionId": "3A",
  "date": "2026-06-15",
  "records": [
    { "studentId": "S1", "status": "PRESENT" },
    { "studentId": "S2", "status": "ABSENT" },
    { "studentId": "S3", "status": "LATE" }
  ]
}
```

Responses:
- `201 Created` → registro exitoso
- `400 Bad Request` → body inválido o sectionId inexistente
- `403 Forbidden` → date es anterior a LocalDate.now()
- `409 Conflict` → ya existe registro para esa sección en esa fecha

---

### `ReportController.java`

**GET `/api/attendance/summary?date=2026-06-15`** — Resumen diario por sección

Response 200:
```json
[
  {
    "sectionId": "3A",
    "sectionName": "3° A",
    "date": "2026-06-15",
    "present": 6,
    "absent": 1,
    "late": 0,
    "recorded": true
  }
]
```
- `400 Bad Request` → fecha con formato inválido

---

**GET `/api/attendance/history?studentId=S1&from=2026-06-01&to=2026-06-15`** — Historial de estudiante

Response 200:
```json
[
  { "date": "2026-06-10", "sectionId": "3A", "status": "PRESENT" }
]
```
- `400 Bad Request` → from > to o formato inválido
- `404 Not Found` → studentId inexistente

---

**GET `/api/attendance/pending?date=2026-06-15`** — Secciones sin registrar hoy

Response 200:
```json
["3B", "4A"]
```
- `400 Bad Request` → fecha inválida

---

**GET `/api/sections`** — Listar todas las secciones con sus estudiantes

Response 200:
```json
[
  {
    "id": "3A",
    "name": "3° A",
    "gradeId": 1,
    "gradeName": "3°",
    "students": [
      { "id": "S1", "fullName": "Lucas Pérez" }
    ]
  }
]
```

---

## PASO 6 — Reglas de negocio en `AttendanceService.java`

Implementá estas validaciones en orden, lanzando excepciones de dominio propias:

1. Verificar que `sectionId` existe en BD → sino lanzar `SectionNotFoundException` (→ 400)
2. Verificar que `date` es igual a `LocalDate.now()` → sino lanzar `InvalidDateException` (→ 403)
3. Para cada studentId en records: verificar que no exista ya un AttendanceRecord con ese student+date → sino lanzar `DuplicateAttendanceException` (→ 409)
4. Si pasa todo → `saveAll()` los registros y retornar.

---

## PASO 7 — Manejo global de errores

Creá `GlobalExceptionHandler.java` con `@RestControllerAdvice`:

```java
@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(SectionNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleSectionNotFound(...)
    // → 400 Bad Request

    @ExceptionHandler(InvalidDateException.class)
    public ResponseEntity<ErrorResponse> handleInvalidDate(...)
    // → 403 Forbidden

    @ExceptionHandler(DuplicateAttendanceException.class)
    public ResponseEntity<ErrorResponse> handleDuplicate(...)
    // → 409 Conflict

    @ExceptionHandler(StudentNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleStudentNotFound(...)
    // → 404 Not Found

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleValidation(...)
    // → 400 Bad Request con detalle de campos
}
```

`ErrorResponse` debe tener: `timestamp`, `status`, `error`, `message`.

---

## PASO 8 — Configuración de perfiles

### `src/main/resources/application.yml`
```yaml
spring:
  profiles:
    active: dev
server:
  port: 8080
springdoc:
  api-docs:
    path: /api-docs
  swagger-ui:
    path: /swagger-ui.html
```

### `src/main/resources/application-dev.yml`
```yaml
spring:
  datasource:
    url: jdbc:h2:mem:asistecdb;DB_CLOSE_DELAY=-1;MODE=PostgreSQL
    driver-class-name: org.h2.Driver
    username: sa
    password:
  h2:
    console:
      enabled: true
      path: /h2-console
  jpa:
    hibernate:
      ddl-auto: create-drop
    show-sql: true
    properties:
      hibernate:
        format_sql: true
```

### `src/main/resources/application-prod.yml`
```yaml
spring:
  datasource:
    url: ${DB_URL}
    username: ${DB_USER}
    password: ${DB_PASS}
  jpa:
    hibernate:
      ddl-auto: validate
```

### `SecurityConfig.java` — deshabilitar seguridad en dev
```java
@Configuration
@EnableWebSecurity
public class SecurityConfig {
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http.csrf(csrf -> csrf.disable())
            .authorizeHttpRequests(auth -> auth.anyRequest().permitAll());
        return http.build();
    }
}
```

---

## PASO 9 — DataInitializer (datos de prueba críticos para la demo)

Creá `DataInitializer.java` como `@Component` que implementa `CommandLineRunner`.
Solo ejecutar si la BD está vacía (verificar con `gradeRepository.count() == 0`).

Datos a insertar:

**Grados:**
- Grade(1, "3°")
- Grade(2, "4°")

**Secciones:**
- Section("3A", "3° A", grade3)
- Section("3B", "3° B", grade3)
- Section("4A", "4° A", grade4)
- Section("4B", "4° B", grade4)

**Estudiantes (6 por sección):**
- 3A: S1-Lucas Pérez, S2-María García, S3-Juan López, S4-Ana Martínez, S5-Carlos Ruiz, S6-Laura Sánchez
- 3B: S7-Pedro Torres, S8-Sofía Díaz, S9-Diego Hernández, S10-Valentina Flores, S11-Matías Romero, S12-Camila Jiménez
- 4A: S13-Andrés Morales, S14-Isabella Castro, S15-Sebastián Vargas, S16-Lucía Mendoza, S17-Tomás Ramos, S18-Paula Silva
- 4B: S19-Nicolás Guerrero, S20-Martina Ortega, S21-Felipe Navarro, S22-Catalina Medina, S23-Rodrigo Aguilar, S24-Valeria Reyes

**Registros de asistencia — CRÍTICO:**
- Generar los últimos 5 días hábiles anteriores a hoy (saltear sábado y domingo).
- Insertar registros para secciones 3A y 4B en esos 5 días.
- NO insertar registros de HOY para ninguna sección (para que el coordinador vea secciones pendientes).
- Secciones 3B y 4A: sin ningún registro histórico tampoco.

Para calcular días hábiles:
```java
private List<LocalDate> getLastWorkingDays(int count) {
    List<LocalDate> days = new ArrayList<>();
    LocalDate date = LocalDate.now().minusDays(1);
    while (days.size() < count) {
        if (date.getDayOfWeek() != DayOfWeek.SATURDAY
            && date.getDayOfWeek() != DayOfWeek.SUNDAY) {
            days.add(date);
        }
        date = date.minusDays(1);
    }
    return days;
}
```

---

## PASO 10 — Tests unitarios (`AttendanceServiceTest.java`)

Generá tests con JUnit 5 + Mockito en `src/test/java/.../domain/service/`.
Mockeá los repositorios de salida (ports out). Cubrir exactamente estos 5 casos:

```java
@ExtendWith(MockitoExtension.class)
class AttendanceServiceTest {

    // 1. Registro exitoso
    @Test
    void saveAttendance_whenValidRequest_shouldSaveAllRecords()

    // 2. Sección inexistente → 400
    @Test
    void saveAttendance_whenSectionNotFound_shouldThrowSectionNotFoundException()

    // 3. Fecha anterior a hoy → 403
    @Test
    void saveAttendance_whenDateIsInThePast_shouldThrowInvalidDateException()

    // 4. Registro duplicado mismo día → 409
    @Test
    void saveAttendance_whenDuplicateForSameDay_shouldThrowDuplicateAttendanceException()

    // 5. Secciones pendientes
    @Test
    void getPendingSections_shouldReturnOnlySectionsWithoutTodayRecord()
}
```

---

## PASO 11 — Test de integración (`AttendanceControllerIT.java`)

Generá un test de integración en `src/test/java/.../adapter/in/rest/` usando
`@SpringBootTest(webEnvironment = RANDOM_PORT)` + `TestRestTemplate` o `MockMvc`.
Perfil activo: `dev` (H2 en memoria).

Flujo a testear end-to-end:
```
1. GET /api/sections → verificar que hay 4 secciones con estudiantes
2. POST /api/attendance con sectionId=3A, date=hoy, todos PRESENT → 201
3. POST /api/attendance misma sección y fecha → 409
4. GET /api/attendance/summary?date=hoy → 3A debe tener recorded=true
5. GET /api/attendance/pending?date=hoy → 3A NO debe estar en la lista
```

---

## PASO 12 — Configuración CORS

En `SecurityConfig.java` o en un `@Bean CorsConfigurationSource`, permitir:
- Origins: `http://localhost:4200`
- Methods: GET, POST, PUT, DELETE, OPTIONS
- Headers: `*`
- AllowCredentials: false

---

## Ejecución Local

```bash
cd backend
./mvnw spring-boot:run -Dspring-boot.run.profiles=dev
```

Verificar:
- API: http://localhost:8080/api/sections
- Swagger: http://localhost:8080/swagger-ui.html
- H2 Console: http://localhost:8080/h2-console

## Tests

```bash
./mvnw test
```
