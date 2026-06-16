import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';

import { environment } from '../../../environments/environment';
import {
  DailySummary,
  SaveAttendanceRequest,
  SectionAttendanceRecord,
  StudentHistoryEntry
} from '../models/attendance.model';

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

  getSectionAttendance(sectionId: string, date: string): Observable<SectionAttendanceRecord[]> {
    return this.http.get<SectionAttendanceRecord[]>(this.base, {
      params: { sectionId, date }
    });
  }

  getStudentHistory(
    studentId: string,
    from: string,
    to: string
  ): Observable<StudentHistoryEntry[]> {
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
