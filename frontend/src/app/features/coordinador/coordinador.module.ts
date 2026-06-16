import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';

import { CoordinadorRoutingModule } from './coordinador-routing.module';
import { SharedModule } from '../../shared/shared.module';
import { CoordinadorShellComponent } from './coordinador-shell/coordinador-shell.component';
import { DailySummaryComponent } from './daily-summary/daily-summary.component';
import { StudentHistoryComponent } from './student-history/student-history.component';
import { PendingSectionsComponent } from './pending-sections/pending-sections.component';

@NgModule({
  declarations: [
    CoordinadorShellComponent,
    DailySummaryComponent,
    StudentHistoryComponent,
    PendingSectionsComponent
  ],
  imports: [CommonModule, FormsModule, CoordinadorRoutingModule, SharedModule]
})
export class CoordinadorModule {}
