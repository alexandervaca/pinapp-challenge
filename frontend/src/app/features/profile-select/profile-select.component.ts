import { Component } from '@angular/core';
import { Router } from '@angular/router';

@Component({
  selector: 'app-profile-select',
  templateUrl: './profile-select.component.html',
  styleUrls: ['./profile-select.component.scss']
})
export class ProfileSelectComponent {
  constructor(private router: Router) {}

  selectProfesor(): void {
    this.router.navigate(['/profesor']);
  }

  selectCoordinador(): void {
    this.router.navigate(['/coordinador']);
  }
}
