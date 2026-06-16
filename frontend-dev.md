# PROMPT PARA CURSOR — Frontend Angular 15+ / TypeScript
# Sistema de Asistencia Escolar "Asistec" — Innova Schools
# ============================================================
# USO: Abrí Cursor en la carpeta /frontend y ejecutá este prompt
# como contexto del agente. Seguí los pasos en orden.
# ============================================================

## CONTEXTO DEL PROYECTO

Estás desarrollando el frontend del sistema "Asistec", una app de asistencia
escolar con dos perfiles seleccionables: Profesor y Coordinador. No hay login
real — el usuario elige su perfil en la pantalla principal.

El Profesor registra asistencia de su sección. El Coordinador consulta reportes
en tiempo real (sin recargar la página manualmente).

Stack obligatorio: Angular 15+, TypeScript, SCSS, lazy loading por módulo,
RxJS para reactividad, HttpClient para comunicación con el backend.

---

## PASO 1 — Crear el proyecto

```bash
ng new frontend --routing --style=scss --strict
cd frontend
```

Configurar proxy para evitar CORS en desarrollo. Crear `proxy.conf.json`:
```json
{
  "/api": {
    "target": "http://localhost:8080",
    "secure": false,
    "changeOrigin": true
  }
}
```

En `package.json`, modificar el script start:
```json
"start": "ng serve --proxy-config proxy.conf.json"
```

---

## PASO 2 — Estructura de carpetas

Generá la siguiente estructura completa dentro de `src/app/`:

```
src/app/
├── core/
│   ├── models/
│   │   ├── grade.model.ts
│   │   ├── section.model.ts
│   │   ├── student.model.ts
│   │   └── attendance.model.ts
│   ├── services/
│   │   ├── attendance.service.ts
│   │   ├── section.service.ts
│   │   └── report.service.ts
│   └── interceptors/
│       └── error.interceptor.ts
├── features/
│   ├── profile-select/
│   │   ├── profile-select.component.ts
│   │   ├── profile-select.component.html
│   │   └── profile-select.component.scss
│   ├── profesor/
│   │   ├── profesor.module.ts
│   │   ├── profesor-routing.module.ts
│   │   ├── section-select/
│   │   │   ├── section-select.component.ts
│   │   │   ├── section-select.component.html
│   │   │   └── section-select.component.scss
│   │   └── attendance-form/
│   │       ├── attendance-form.component.ts
│   │       ├── attendance-form.component.html
│   │       └── attendance-form.component.scss
│   └── coordinador/
│       ├── coordinador.module.ts
│       ├── coordinador-routing.module.ts
│       ├── daily-summary/
│       │   ├── daily-summary.component.ts
│       │   ├── daily-summary.component.html
│       │   └── daily-summary.component.scss
│       ├── student-history/
│       │   ├── student-history.component.ts
│       │   ├── student-history.component.html
│       │   └── student-history.component.scss
│       └── pending-sections/
│           ├── pending-sections.component.ts
│           ├── pending-sections.component.html
│           └── pending-sections.component.scss
└── shared/
    ├── shared.module.ts
    └── components/
        └── status-badge/
            ├── status-badge.component.ts
            ├── status-badge.component.html
            └── status-badge.component.scss
```

---

## PASO 3 — Modelos TypeScript (`core/models/`)

### `attendance.model.ts`
```typescript
export type AttendanceStatus = 'PRESENT' | 'ABSENT' | 'LATE' | null;

export interface AttendanceRecordDto {
  studentId: string;
  status: AttendanceStatus;
}

export interface SaveAttendanceRequest {
  sectionId: string;
  date: string;          // formato ISO: 'YYYY-MM-DD'
  records: AttendanceRecordDto[];
}

export interface DailySummary {
  sectionId: string;
  sectionName: string;
  date: string;
  present: number;
  absent: number;
  late: number;
  recorded: boolean;
}

export interface StudentHistoryEntry {
  date: string;
  sectionId: string;
  status: AttendanceStatus;
}
```

### `student.model.ts`
```typescript
export interface Student {
  id: string;
  fullName: string;
}
```

### `section.model.ts`
```typescript
import { Student } from './student.model';

export interface Section {
  id: string;
  name: string;
  gradeId: number;
  gradeName: string;
  students: Student[];
}
```

---

## PASO 4 — Environments

### `src/environments/environment.ts`
```typescript
export const environment = {
  production: false,
  apiUrl: '/api'   // el proxy redirige a http://localhost:8080/api
};
```

### `src/environments/environment.prod.ts`
```typescript
export const environment = {
  production: true,
  apiUrl: 'https://tu-dominio.com/api'
};
```

**Regla:** Nunca hardcodear URLs. Siempre usar `environment.apiUrl` como base.

---

## PASO 5 — Servicios HTTP (`core/services/`)

### `section.service.ts`
```typescript
@Injectable({ providedIn: 'root' })
export class SectionService {
  private base = `${environment.apiUrl}/sections`;

  constructor(private http: HttpClient) {}

  getSections(): Observable<Section[]> {
    return this.http.get<Section[]>(this.base);
  }
}
```

### `attendance.service.ts`
```typescript
@Injectable({ providedIn: 'root' })
export class AttendanceService {
  private base = `${environment.apiUrl}/attendance`;

  constructor(private http: HttpClient) {}

  saveAttendance(payload: SaveAttendanceRequest): Observable<void> {
    return this.http.post<void>(this.base, payload);
  }

  getDailySummary(date: string): Observable<DailySummary[]> {
    return this.http.get<DailySummary[]>(`${this.base}/summary`, {
      params: { date }
    });
  }

  getStudentHistory(studentId: string, from: string, to: string): Observable<StudentHistoryEntry[]> {
    return this.http.get<StudentHistoryEntry[]>(`${this.base}/history`, {
      params: { studentId, from, to }
    });
  }

  getPendingSections(date: string): Observable<string[]> {
    return this.http.get<string[]>(`${this.base}/pending`, {
      params: { date }
    });
  }
}
```

---

## PASO 6 — Interceptor de errores HTTP (`core/interceptors/`)

Creá `error.interceptor.ts` que intercepte errores y los muestre de forma
consistente. Mapear:
- `409` → "Ya existe un registro de asistencia para esta sección hoy."
- `403` → "No podés modificar registros de días anteriores."
- `400` → "Datos inválidos. Revisá el formulario."
- `404` → "Recurso no encontrado."
- `0` / `500` → "Error de conexión. Verificá que el backend esté corriendo."

Registrarlo en `AppModule` como `HTTP_INTERCEPTORS`.

---

## PASO 7 — Routing

### `app-routing.module.ts`
```typescript
const routes: Routes = [
  {
    path: '',
    component: ProfileSelectComponent
  },
  {
    path: 'profesor',
    loadChildren: () =>
      import('./features/profesor/profesor.module').then(m => m.ProfesorModule)
  },
  {
    path: 'coordinador',
    loadChildren: () =>
      import('./features/coordinador/coordinador.module').then(m => m.CoordinadorModule)
  },
  { path: '**', redirectTo: '' }
];
```

### `profesor-routing.module.ts`
```typescript
const routes: Routes = [
  { path: '', component: SectionSelectComponent },
  { path: ':sectionId', component: AttendanceFormComponent }
];
```

### `coordinador-routing.module.ts`
```typescript
const routes: Routes = [
  { path: '', redirectTo: 'resumen', pathMatch: 'full' },
  { path: 'resumen', component: DailySummaryComponent },
  { path: 'historial', component: StudentHistoryComponent },
  { path: 'pendientes', component: PendingSectionsComponent }
];
```

---

## PASO 8 — Componentes: Perfil y Sección

### `profile-select.component`
- Mostrar dos tarjetas o botones: "Profesor" y "Coordinador".
- Al hacer clic en "Profesor" → `router.navigate(['/profesor'])`.
- Al hacer clic en "Coordinador" → `router.navigate(['/coordinador'])`.
- Diseño centrado, visualmente claro.

### `section-select.component` (Profesor)
- En `ngOnInit`, llamar a `SectionService.getSections()`.
- Mostrar lista de secciones agrupadas por grado.
- Al seleccionar una sección → `router.navigate(['/profesor', section.id])`.

---

## PASO 9 — Componente principal del Profesor: `attendance-form`

Este es el componente más crítico. Implementar con esta lógica exacta:

```typescript
@Component({ ... })
export class AttendanceFormComponent implements OnInit {
  sectionId!: string;
  section?: Section;
  today = new Date().toISOString().split('T')[0]; // 'YYYY-MM-DD'

  // Estado actual del formulario
  currentStatus = new Map<string, AttendanceStatus>();

  // Estado inicial cargado del backend (para comparar si hay cambios)
  initialStatus = new Map<string, AttendanceStatus>();

  successMessage = '';
  errorMessage = '';
  isSaving = false;

  get isDirty(): boolean {
    for (const [id, status] of this.currentStatus) {
      if (status !== this.initialStatus.get(id)) return true;
    }
    return false;
  }

  setStatus(studentId: string, status: AttendanceStatus): void {
    this.currentStatus.set(studentId, status);
  }

  save(): void {
    if (!this.isDirty || this.isSaving) return;
    this.isSaving = true;

    const payload: SaveAttendanceRequest = {
      sectionId: this.sectionId,
      date: this.today,
      records: Array.from(this.currentStatus.entries()).map(([studentId, status]) => ({
        studentId, status
      }))
    };

    this.attendanceService.saveAttendance(payload).subscribe({
      next: () => {
        this.successMessage = 'Asistencia guardada correctamente.';
        this.initialStatus = new Map(this.currentStatus); // resetear dirty
        this.isSaving = false;
      },
      error: (err) => {
        this.errorMessage = err.message || 'Error al guardar.';
        this.isSaving = false;
      }
    });
  }
}
```

En el template HTML:
- Mostrar nombre de sección y fecha actual.
- Por cada estudiante: nombre + 3 botones (Presente / Ausente / Tardanza).
- El botón activo debe tener estilo diferenciado (clase CSS según estado).
- Botón **Guardar** con `[disabled]="!isDirty || isSaving"`.
- Mostrar `successMessage` en verde y `errorMessage` en rojo bajo el botón.

---

## PASO 10 — Componentes del Coordinador con polling en tiempo real

### `daily-summary.component`

Usar `interval` + `switchMap` de RxJS para actualización automática cada 30 segundos:

```typescript
@Component({ ... })
export class DailySummaryComponent implements OnInit, OnDestroy {
  summaries: DailySummary[] = [];
  private destroy$ = new Subject<void>();
  today = new Date().toISOString().split('T')[0];

  ngOnInit(): void {
    // Polling: carga inmediata + actualización cada 30 segundos
    interval(30000).pipe(
      startWith(0),
      switchMap(() => this.attendanceService.getDailySummary(this.today)),
      takeUntil(this.destroy$)
    ).subscribe(data => this.summaries = data);
  }

  ngOnDestroy(): void {
    this.destroy$.next();
    this.destroy$.complete();
  }
}
```

Template: tabla con columnas Sección | Grado | Presentes | Ausentes | Tardanzas | Estado.
La columna Estado muestra "✅ Registrada" o "⏳ Pendiente" según `recorded`.
Mostrar contador regresivo visual hasta la próxima actualización (opcional pero valorado).

### `pending-sections.component`

- Reusar la misma lógica de polling que `daily-summary`.
- Filtrar de `DailySummary[]` las secciones donde `recorded === false`.
- Mostrar lista con nombre de sección y grado.
- Si todas tienen registro: mostrar "✅ Todas las secciones han registrado asistencia hoy."

### `student-history.component`

- Formulario con: dropdown de estudiante, input date `desde`, input date `hasta`.
- Cargar lista de estudiantes en `ngOnInit` (de todas las secciones via `SectionService`).
- Deshabilitar botón "Consultar" si `from > to` o si algún campo está vacío.
- Al buscar: llamar a `AttendanceService.getStudentHistory()`.
- Mostrar resultados en tabla cronológica: Fecha | Sección | Estado (con `StatusBadgeComponent`).
- Si no hay resultados: mostrar "Sin registros para el período seleccionado."

---

## PASO 11 — Componente compartido: `status-badge`

```typescript
@Component({
  selector: 'app-status-badge',
  template: `<span [class]="'badge badge--' + status?.toLowerCase()">{{ label }}</span>`,
  styles: [`
    .badge { padding: 4px 10px; border-radius: 12px; font-size: 12px; font-weight: 600; }
    .badge--present { background: #d1fae5; color: #065f46; }
    .badge--absent  { background: #fee2e2; color: #991b1b; }
    .badge--late    { background: #fef3c7; color: #92400e; }
    .badge--null    { background: #f3f4f6; color: #6b7280; }
  `]
})
export class StatusBadgeComponent {
  @Input() status: AttendanceStatus = null;

  get label(): string {
    const map: Record<string, string> = {
      PRESENT: 'Presente',
      ABSENT: 'Ausente',
      LATE: 'Tardanza'
    };
    return this.status ? map[this.status] : 'Sin registrar';
  }
}
```

Exportar desde `SharedModule` e importar en `ProfesorModule` y `CoordinadorModule`.

---

## PASO 12 — Tests de componentes (mínimo requerido por el challenge)

### `attendance-form.component.spec.ts`

```typescript
describe('AttendanceFormComponent', () => {

  // TEST 1 — Lista renderizada correctamente con estados iniciales
  it('debe renderizar la lista de estudiantes con sus estados', () => {
    // Arrange: mockear SectionService para devolver una sección con 3 estudiantes
    // sin registros previos (todos null)
    // Act: fixture.detectChanges()
    // Assert: que existan 3 filas en el DOM con los nombres correctos
    // y que los 3 botones de cada fila estén sin seleccionar
  });

  // TEST 2 — Botón guardar deshabilitado sin cambios
  it('el botón guardar debe estar deshabilitado cuando no hay cambios', () => {
    // Arrange: cargar componente con estudiantes, sin modificar ningún estado
    // Assert: el botón con texto "Guardar" debe tener el atributo disabled
    // Verificar: component.isDirty === false
  });

  // TEST 3 — Botón guardar habilitado al cambiar un estado
  it('el botón guardar debe habilitarse al cambiar el estado de un estudiante', () => {
    // Arrange: cargar componente
    // Act: llamar a component.setStatus('S1', 'PRESENT')
    // Assert: component.isDirty === true
    // y el botón NO debe tener disabled
  });
});
```

### `daily-summary.component.spec.ts`

```typescript
describe('DailySummaryComponent', () => {

  // TEST 4 — Resumen con totales correctos
  it('debe mostrar los totales correctos por sección', () => {
    // Arrange: mockear AttendanceService.getDailySummary() para devolver:
    // [{ sectionId:'3A', present:5, absent:1, late:0, recorded:true }]
    // Act: fixture.detectChanges()
    // Assert: que en el DOM aparezcan los valores 5, 1, 0
    // y que la columna Estado muestre "Registrada"
  });

  // TEST 5 — Destruir suscripción al destruir el componente
  it('debe cancelar el polling al destruir el componente', () => {
    // Assert: que destroy$.next() sea llamado en ngOnDestroy
    // para evitar memory leaks
  });
});
```

Correr los tests con:
```bash
npm test -- --watch=false --browsers=ChromeHeadless
```

---

## PASO 13 — AppModule: registrar todo

En `app.module.ts` asegurar que estén registrados:
- `HttpClientModule`
- `{ provide: HTTP_INTERCEPTORS, useClass: ErrorInterceptor, multi: true }`
- `RouterModule` con `app-routing.module.ts`
- `SharedModule`

---

## Ejecución Local

```bash
cd frontend
npm install
npm start
# Disponible en http://localhost:4200
```

## Tests

```bash
npm test -- --watch=false --browsers=ChromeHeadless
```
