import { Component, Input } from '@angular/core';

import { AttendanceStatus } from '../../../core/models/attendance.model';

@Component({
  selector: 'app-status-badge',
  templateUrl: './status-badge.component.html',
  styleUrls: ['./status-badge.component.scss']
})
export class StatusBadgeComponent {
  @Input() status: AttendanceStatus = null;

  get label(): string {
    const map: Record<string, string> = {
      PRESENT: 'Presente',
      ABSENT: 'Ausente',
      LATE: 'Tardanza'
    };
    return this.status ? map[this.status] : 'Sin registrar';
  }

  get cssClass(): string {
    return 'badge badge--' + (this.status ? this.status.toLowerCase() : 'null');
  }
}
