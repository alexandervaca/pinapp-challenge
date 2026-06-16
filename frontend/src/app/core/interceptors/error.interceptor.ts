import { Injectable } from '@angular/core';
import {
  HttpErrorResponse,
  HttpEvent,
  HttpHandler,
  HttpInterceptor,
  HttpRequest
} from '@angular/common/http';
import { Observable, throwError } from 'rxjs';
import { catchError } from 'rxjs/operators';

@Injectable()
export class ErrorInterceptor implements HttpInterceptor {
  intercept(req: HttpRequest<unknown>, next: HttpHandler): Observable<HttpEvent<unknown>> {
    return next.handle(req).pipe(
      catchError((err: HttpErrorResponse) => {
        const message = this.mapError(err);
        return throwError(() => ({ status: err.status, message, original: err }));
      })
    );
  }

  private mapError(err: HttpErrorResponse): string {
    switch (err.status) {
      case 409:
        return 'Ya existe un registro de asistencia para esta sección hoy.';
      case 403:
        return 'No podés modificar registros de días anteriores.';
      case 400:
        return 'Datos inválidos. Revisá el formulario.';
      case 404:
        return 'Recurso no encontrado.';
      case 0:
      case 500:
        return 'Error de conexión. Verificá que el backend esté corriendo.';
      default:
        return err.error?.message || err.message || 'Error inesperado.';
    }
  }
}
