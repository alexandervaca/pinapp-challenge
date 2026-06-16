import { Student } from './student.model';

export interface Section {
  id: string;
  name: string;
  gradeId: number;
  gradeName: string;
  students: Student[];
}
