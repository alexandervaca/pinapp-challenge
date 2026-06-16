import { ComponentFixture, TestBed, fakeAsync, tick, discardPeriodicTasks } from '@angular/core/testing';
import { of } from 'rxjs';

import { DailySummaryComponent } from './daily-summary.component';
import { AttendanceService } from '../../../core/services/attendance.service';
import { DailySummary } from '../../../core/models/attendance.model';

describe('DailySummaryComponent', () => {
  let fixture: ComponentFixture<DailySummaryComponent>;
  let component: DailySummaryComponent;
  let attendanceServiceSpy: jasmine.SpyObj<AttendanceService>;

  const sample: DailySummary[] = [
    {
      sectionId: '3A',
      sectionName: '3°A',
      gradeName: '3°',
      date: '2026-06-15',
      present: 5,
      absent: 1,
      late: 0,
      recorded: true
    }
  ];

  beforeEach(async () => {
    attendanceServiceSpy = jasmine.createSpyObj('AttendanceService', ['getDailySummary']);
    attendanceServiceSpy.getDailySummary.and.returnValue(of(sample));

    await TestBed.configureTestingModule({
      declarations: [DailySummaryComponent],
      providers: [{ provide: AttendanceService, useValue: attendanceServiceSpy }]
    }).compileComponents();

    fixture = TestBed.createComponent(DailySummaryComponent);
    component = fixture.componentInstance;
  });

  it('debe mostrar los totales correctos por sección', fakeAsync(() => {
    fixture.detectChanges();
    tick();
    fixture.detectChanges();

    expect(component.summaries.length).toBe(1);

    const cells = fixture.nativeElement.querySelectorAll('tbody td');
    const text = Array.from(cells).map((c: any) => c.textContent.trim());
    expect(text).toContain('5');
    expect(text).toContain('1');
    expect(text).toContain('0');

    const pill = fixture.nativeElement.querySelector('.pill--ok');
    expect(pill?.textContent.trim()).toBe('Registrada');

    component.ngOnDestroy();
    discardPeriodicTasks();
  }));

  it('debe cancelar el polling al destruir el componente', fakeAsync(() => {
    fixture.detectChanges();
    tick();

    const destroy$ = (component as any).destroy$;
    spyOn(destroy$, 'next').and.callThrough();
    spyOn(destroy$, 'complete').and.callThrough();

    component.ngOnDestroy();

    expect(destroy$.next).toHaveBeenCalled();
    expect(destroy$.complete).toHaveBeenCalled();

    discardPeriodicTasks();
  }));
});
