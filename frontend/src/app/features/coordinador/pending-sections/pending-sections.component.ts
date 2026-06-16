import { Component, OnDestroy, OnInit } from '@angular/core';
import { Subject, interval } from 'rxjs';
import { startWith, switchMap, takeUntil } from 'rxjs/operators';

import { AttendanceService } from '../../../core/services/attendance.service';
import { DailySummary } from '../../../core/models/attendance.model';

const POLL_INTERVAL_MS = 30000;

@Component({
  selector: 'app-pending-sections',
  templateUrl: './pending-sections.component.html',
  styleUrls: ['./pending-sections.component.scss']
})
export class PendingSectionsComponent implements OnInit, OnDestroy {
  pending: DailySummary[] = [];
  today = new Date().toISOString().split('T')[0];
  loading = true;
  errorMessage = '';

  private destroy$ = new Subject<void>();

  constructor(private attendanceService: AttendanceService) {}

  ngOnInit(): void {
    interval(POLL_INTERVAL_MS)
      .pipe(
        startWith(0),
        switchMap(() => this.attendanceService.getDailySummary(this.today)),
        takeUntil(this.destroy$)
      )
      .subscribe({
        next: data => {
          this.pending = data.filter(d => !d.recorded);
          this.loading = false;
          this.errorMessage = '';
        },
        error: err => {
          this.errorMessage = err.message || 'Error al cargar secciones pendientes.';
          this.loading = false;
        }
      });
  }

  ngOnDestroy(): void {
    this.destroy$.next();
    this.destroy$.complete();
  }

  trackById(_: number, item: DailySummary): string {
    return item.sectionId;
  }
}
