export type AttendanceStatus = 'PRESENT' | 'ABSENT' | 'LATE' | null;

export interface AttendanceRecordDto {
  studentId: string;
  status: AttendanceStatus;
}

export interface SaveAttendanceRequest {
  sectionId: string;
  date: string;
  records: AttendanceRecordDto[];
}

export interface DailySummary {
  sectionId: string;
  sectionName: string;
  gradeName?: string;
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

export interface SectionAttendanceRecord {
  studentId: string;
  status: AttendanceStatus;
}
