import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';

import { SectionSelectComponent } from './section-select/section-select.component';
import { AttendanceFormComponent } from './attendance-form/attendance-form.component';

const routes: Routes = [
  { path: '', component: SectionSelectComponent },
  { path: ':sectionId', component: AttendanceFormComponent }
];

@NgModule({
  imports: [RouterModule.forChild(routes)],
  exports: [RouterModule]
})
export class ProfesorRoutingModule {}
