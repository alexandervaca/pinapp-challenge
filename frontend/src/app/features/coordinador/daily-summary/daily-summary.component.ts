import { Component, OnDestroy, OnInit } from '@angular/core';
import { Subject, interval } from 'rxjs';
import { startWith, switchMap, takeUntil } from 'rxjs/operators';

import { AttendanceService } from '../../../core/services/attendance.service';
import { DailySummary } from '../../../core/models/attendance.model';

const POLL_INTERVAL_MS = 30000;

@Component({
  selector: 'app-daily-summary',
  templateUrl: './daily-summary.component.html',
  styleUrls: ['./daily-summary.component.scss']
})
export class DailySummaryComponent implements OnInit, OnDestroy {
  summaries: DailySummary[] = [];
  today = new Date().toISOString().split('T')[0];
  loading = true;
  errorMessage = '';
  secondsToRefresh = POLL_INTERVAL_MS / 1000;

  private destroy$ = new Subject<void>();
  private countdownTimer: ReturnType<typeof setInterval> | null = null;

  constructor(private attendanceService: AttendanceService) {}

  ngOnInit(): void {
    interval(POLL_INTERVAL_MS)
      .pipe(
        startWith(0),
        switchMap(() => {
          this.resetCountdown();
          return this.attendanceService.getDailySummary(this.today);
        }),
        takeUntil(this.destroy$)
      )
      .subscribe({
        next: data => {
          this.summaries = data;
          this.loading = false;
          this.errorMessage = '';
        },
        error: err => {
          this.errorMessage = err.message || 'Error al cargar el resumen.';
          this.loading = false;
        }
      });

    this.startCountdown();
  }

  ngOnDestroy(): void {
    this.destroy$.next();
    this.destroy$.complete();
    if (this.countdownTimer) {
      clearInterval(this.countdownTimer);
      this.countdownTimer = null;
    }
  }

  trackById(_: number, item: DailySummary): string {
    return item.sectionId;
  }

  private startCountdown(): void {
    this.countdownTimer = setInterval(() => {
      this.secondsToRefresh = Math.max(0, this.secondsToRefresh - 1);
    }, 1000);
  }

  private resetCountdown(): void {
    this.secondsToRefresh = POLL_INTERVAL_MS / 1000;
  }
}
