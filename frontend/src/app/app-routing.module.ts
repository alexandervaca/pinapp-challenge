import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';

import { ProfileSelectComponent } from './features/profile-select/profile-select.component';

const routes: Routes = [
  { path: '', component: ProfileSelectComponent },
  {
    path: 'profesor',
    loadChildren: () =>
      import('./features/profesor/profesor.module').then(m => m.ProfesorModule)
  },
  {
    path: 'coordinador',
    loadChildren: () =>
      import('./features/coordinador/coordinador.module').then(m => m.CoordinadorModule)
  },
  { path: '**', redirectTo: '' }
];

@NgModule({
  imports: [RouterModule.forRoot(routes)],
  exports: [RouterModule]
})
export class AppRoutingModule {}
