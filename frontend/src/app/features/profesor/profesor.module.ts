import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';

import { ProfesorRoutingModule } from './profesor-routing.module';
import { SharedModule } from '../../shared/shared.module';
import { SectionSelectComponent } from './section-select/section-select.component';
import { AttendanceFormComponent } from './attendance-form/attendance-form.component';

@NgModule({
  declarations: [SectionSelectComponent, AttendanceFormComponent],
  imports: [CommonModule, FormsModule, ProfesorRoutingModule, SharedModule]
})
export class ProfesorModule {}
