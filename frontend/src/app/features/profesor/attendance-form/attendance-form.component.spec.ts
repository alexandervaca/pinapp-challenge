import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ActivatedRoute } from '@angular/router';
import { RouterTestingModule } from '@angular/router/testing';
import { FormsModule } from '@angular/forms';
import { of, throwError } from 'rxjs';

import { AttendanceFormComponent } from './attendance-form.component';
import { SectionService } from '../../../core/services/section.service';
import { AttendanceService } from '../../../core/services/attendance.service';
import { Section } from '../../../core/models/section.model';
import { SaveAttendanceRequest } from '../../../core/models/attendance.model';

describe('AttendanceFormComponent', () => {
  let fixture: ComponentFixture<AttendanceFormComponent>;
  let component: AttendanceFormComponent;
  let sectionServiceSpy: jasmine.SpyObj<SectionService>;
  let attendanceServiceSpy: jasmine.SpyObj<AttendanceService>;

  const sampleSection: Section = {
    id: '3A',
    name: '3°A',
    gradeId: 3,
    gradeName: '3°',
    students: [
      { id: 'S1', fullName: 'Ana Pérez' },
      { id: 'S2', fullName: 'Bruno Soto' },
      { id: 'S3', fullName: 'Carla Díaz' }
    ]
  };

  beforeEach(async () => {
    sectionServiceSpy = jasmine.createSpyObj('SectionService', ['getSections']);
    attendanceServiceSpy = jasmine.createSpyObj('AttendanceService', [
      'getDailySummary',
      'saveAttendance',
      'getSectionAttendance'
    ]);

    sectionServiceSpy.getSections.and.returnValue(of([sampleSection]));
    attendanceServiceSpy.getDailySummary.and.returnValue(of([]));
    attendanceServiceSpy.saveAttendance.and.returnValue(of(void 0));
    attendanceServiceSpy.getSectionAttendance.and.returnValue(of([]));

    await TestBed.configureTestingModule({
      imports: [RouterTestingModule, FormsModule],
      declarations: [AttendanceFormComponent],
      providers: [
        { provide: SectionService, useValue: sectionServiceSpy },
        { provide: AttendanceService, useValue: attendanceServiceSpy },
        {
          provide: ActivatedRoute,
          useValue: { snapshot: { paramMap: { get: () => '3A' } } }
        }
      ]
    }).compileComponents();

    fixture = TestBed.createComponent(AttendanceFormComponent);
    component = fixture.componentInstance;
  });

  it('debe renderizar la lista de estudiantes con sus estados', () => {
    fixture.detectChanges();

    const rows = fixture.nativeElement.querySelectorAll('.student-row');
    expect(rows.length).toBe(3);

    const names = Array.from(rows).map((r: any) =>
      r.querySelector('.student-row__name').textContent.trim()
    );
    expect(names).toEqual(['Ana Pérez', 'Bruno Soto', 'Carla Díaz']);

    for (const student of sampleSection.students) {
      expect(component.getStatus(student.id)).toBeNull();
    }

    const activeButtons = fixture.nativeElement.querySelectorAll('.status-btn.is-active');
    expect(activeButtons.length).toBe(0);
  });

  it('el botón guardar debe estar deshabilitado cuando no hay cambios', () => {
    fixture.detectChanges();

    expect(component.isDirty).toBeFalse();

    const saveBtn = fixture.nativeElement.querySelector('[data-test="save-button"]');
    expect(saveBtn.disabled).toBeTrue();
  });

  it('el botón guardar debe habilitarse al cambiar el estado de un estudiante', () => {
    fixture.detectChanges();

    component.setStatus('S1', 'PRESENT');
    fixture.detectChanges();

    expect(component.isDirty).toBeTrue();

    const saveBtn = fixture.nativeElement.querySelector('[data-test="save-button"]');
    expect(saveBtn.disabled).toBeFalse();
  });

  it('debe enviar el payload correcto al guardar', () => {
    fixture.detectChanges();
    component.setStatus('S1', 'PRESENT');
    component.setStatus('S2', 'ABSENT');

    component.save();

    expect(attendanceServiceSpy.saveAttendance).toHaveBeenCalledTimes(1);
    const payload = attendanceServiceSpy.saveAttendance.calls.mostRecent()
      .args[0] as SaveAttendanceRequest;
    expect(payload.sectionId).toBe('3A');
    expect(payload.records.length).toBe(3);
    const s1 = payload.records.find(r => r.studentId === 'S1');
    const s2 = payload.records.find(r => r.studentId === 'S2');
    expect(s1?.status).toBe('PRESENT');
    expect(s2?.status).toBe('ABSENT');
    expect(component.successMessage).toContain('guardada');
    expect(component.isDirty).toBeFalse();
  });

  it('debe mostrar mensaje de error si el backend rechaza el guardado', () => {
    fixture.detectChanges();
    component.setStatus('S1', 'PRESENT');

    attendanceServiceSpy.saveAttendance.and.returnValue(
      throwError(() => ({ status: 409, message: 'Ya existe un registro' }))
    );

    component.save();

    expect(component.errorMessage).toBe('Ya existe un registro');
    expect(component.isSaving).toBeFalse();
  });
});
