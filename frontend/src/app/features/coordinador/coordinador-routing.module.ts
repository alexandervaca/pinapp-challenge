import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';

import { CoordinadorShellComponent } from './coordinador-shell/coordinador-shell.component';
import { DailySummaryComponent } from './daily-summary/daily-summary.component';
import { StudentHistoryComponent } from './student-history/student-history.component';
import { PendingSectionsComponent } from './pending-sections/pending-sections.component';

const routes: Routes = [
  {
    path: '',
    component: CoordinadorShellComponent,
    children: [
      { path: '', redirectTo: 'resumen', pathMatch: 'full' },
      { path: 'resumen', component: DailySummaryComponent },
      { path: 'historial', component: StudentHistoryComponent },
      { path: 'pendientes', component: PendingSectionsComponent }
    ]
  }
];

@NgModule({
  imports: [RouterModule.forChild(routes)],
  exports: [RouterModule]
})
export class CoordinadorRoutingModule {}
