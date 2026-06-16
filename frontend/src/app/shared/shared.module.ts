import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';

import { StatusBadgeComponent } from './components/status-badge/status-badge.component';

@NgModule({
  declarations: [StatusBadgeComponent],
  imports: [CommonModule],
  exports: [StatusBadgeComponent]
})
export class SharedModule {}
