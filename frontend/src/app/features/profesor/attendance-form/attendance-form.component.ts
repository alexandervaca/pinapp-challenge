import { Component, OnInit } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';
import { forkJoin } from 'rxjs';

import { SectionService } from '../../../core/services/section.service';
import { AttendanceService } from '../../../core/services/attendance.service';
import { Section } from '../../../core/models/section.model';
import {
  AttendanceStatus,
  DailySummary,
  SectionAttendanceRecord,
  SaveAttendanceRequest
} from '../../../core/models/attendance.model';

@Component({
  selector: 'app-attendance-form',
  templateUrl: './attendance-form.component.html',
  styleUrls: ['./attendance-form.component.scss']
})
export class AttendanceFormComponent implements OnInit {
  sectionId!: string;
  section?: Section;
  today = new Date().toISOString().split('T')[0];

  currentStatus = new Map<string, AttendanceStatus>();
  initialStatus = new Map<string, AttendanceStatus>();

  loading = true;
  successMessage = '';
  errorMessage = '';
  isSaving = false;
  alreadyRecorded = false;

  constructor(
    private route: ActivatedRoute,
    private router: Router,
    private sectionService: SectionService,
    private attendanceService: AttendanceService
  ) {}

  ngOnInit(): void {
    this.sectionId = this.route.snapshot.paramMap.get('sectionId') ?? '';
    this.loadSection();
  }

  get isDirty(): boolean {
    for (const [id, status] of this.currentStatus) {
      if (status !== (this.initialStatus.get(id) ?? null)) {
        return true;
      }
    }
    return false;
  }

  setStatus(studentId: string, status: AttendanceStatus): void {
    this.currentStatus.set(studentId, status);
    this.successMessage = '';
    this.errorMessage = '';
  }

  getStatus(studentId: string): AttendanceStatus {
    return this.currentStatus.get(studentId) ?? null;
  }

  goBack(): void {
    this.router.navigate(['/profesor']);
  }

  save(): void {
    if (!this.isDirty || this.isSaving) {
      return;
    }

    this.isSaving = true;
    this.successMessage = '';
    this.errorMessage = '';

    const payload: SaveAttendanceRequest = {
      sectionId: this.sectionId,
      date: this.today,
      records: Array.from(this.currentStatus.entries()).map(([studentId, status]) => ({
        studentId,
        status
      }))
    };

    this.attendanceService.saveAttendance(payload).subscribe({
      next: () => {
        this.successMessage = 'Asistencia guardada correctamente.';
        this.initialStatus = new Map(this.currentStatus);
        this.alreadyRecorded = true;
        this.isSaving = false;
      },
      error: err => {
        this.errorMessage = err.message || 'Error al guardar.';
        this.isSaving = false;
      }
    });
  }

  private loadSection(): void {
    forkJoin({
      sections: this.sectionService.getSections(),
      summary: this.attendanceService.getDailySummary(this.today),
      records: this.attendanceService.getSectionAttendance(this.sectionId, this.today)
    }).subscribe({
      next: ({ sections, summary, records }) => {
        const section = sections.find(s => s.id === this.sectionId);
        if (!section) {
          this.errorMessage = 'Sección no encontrada.';
          this.loading = false;
          return;
        }

        this.section = section;
        const todaySummary = summary.find((s: DailySummary) => s.sectionId === this.sectionId);
        this.alreadyRecorded = !!todaySummary?.recorded;

        const recordMap = new Map(records.map((r: SectionAttendanceRecord) => [r.studentId, r.status]));

        for (const student of section.students) {
          const status = recordMap.get(student.id) ?? null;
          this.currentStatus.set(student.id, status);
          this.initialStatus.set(student.id, status);
        }

        this.loading = false;
      },
      error: err => {
        this.errorMessage = err.message || 'Error al cargar la sección.';
        this.loading = false;
      }
    });
  }
}
