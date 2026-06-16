import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';

import { AttendanceService } from './attendance.service';
import {
  DailySummary,
  StudentHistoryEntry
} from '../models/attendance.model';

@Injectable({ providedIn: 'root' })
export class ReportService {
  constructor(private attendance: AttendanceService) {}

  dailySummary(date: string): Observable<DailySummary[]> {
    return this.attendance.getDailySummary(date);
  }

  studentHistory(
    studentId: string,
    from: string,
    to: string
  ): Observable<StudentHistoryEntry[]> {
    return this.attendance.getStudentHistory(studentId, from, to);
  }

  pendingSections(date: string): Observable<string[]> {
    return this.attendance.getPendingSections(date);
  }
}
