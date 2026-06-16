import { Component, OnInit } from '@angular/core';

import { SectionService } from '../../../core/services/section.service';
import { AttendanceService } from '../../../core/services/attendance.service';
import { Section } from '../../../core/models/section.model';
import { Student } from '../../../core/models/student.model';
import { StudentHistoryEntry } from '../../../core/models/attendance.model';

interface StudentOption extends Student {
  sectionId: string;
  sectionName: string;
}

@Component({
  selector: 'app-student-history',
  templateUrl: './student-history.component.html',
  styleUrls: ['./student-history.component.scss']
})
export class StudentHistoryComponent implements OnInit {
  students: StudentOption[] = [];
  selectedStudentId = '';
  fromDate = '';
  toDate = '';

  loadingStudents = true;
  loadingResults = false;
  errorMessage = '';
  results: StudentHistoryEntry[] = [];
  hasSearched = false;

  constructor(
    private sectionService: SectionService,
    private attendanceService: AttendanceService
  ) {}

  ngOnInit(): void {
    const today = new Date().toISOString().split('T')[0];
    const sevenDaysAgo = new Date();
    sevenDaysAgo.setDate(sevenDaysAgo.getDate() - 7);
    this.fromDate = sevenDaysAgo.toISOString().split('T')[0];
    this.toDate = today;

    this.sectionService.getSections().subscribe({
      next: sections => {
        this.students = this.flattenStudents(sections);
        this.loadingStudents = false;
      },
      error: err => {
        this.errorMessage = err.message || 'Error al cargar estudiantes.';
        this.loadingStudents = false;
      }
    });
  }

  get canSearch(): boolean {
    if (!this.selectedStudentId || !this.fromDate || !this.toDate) {
      return false;
    }
    return this.fromDate <= this.toDate;
  }

  search(): void {
    if (!this.canSearch || this.loadingResults) {
      return;
    }
    this.loadingResults = true;
    this.errorMessage = '';
    this.attendanceService
      .getStudentHistory(this.selectedStudentId, this.fromDate, this.toDate)
      .subscribe({
        next: data => {
          this.results = data;
          this.hasSearched = true;
          this.loadingResults = false;
        },
        error: err => {
          this.errorMessage = err.message || 'Error al consultar historial.';
          this.loadingResults = false;
          this.hasSearched = true;
          this.results = [];
        }
      });
  }

  private flattenStudents(sections: Section[]): StudentOption[] {
    const list: StudentOption[] = [];
    for (const section of sections) {
      for (const student of section.students) {
        list.push({
          ...student,
          sectionId: section.id,
          sectionName: section.name
        });
      }
    }
    return list.sort((a, b) => a.fullName.localeCompare(b.fullName));
  }
}
