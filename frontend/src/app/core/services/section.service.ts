import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';

import { environment } from '../../../environments/environment';
import { Section } from '../models/section.model';

@Injectable({ providedIn: 'root' })
export class SectionService {
  private base = `${environment.apiUrl}/sections`;

  constructor(private http: HttpClient) {}

  getSections(): Observable<Section[]> {
    return this.http.get<Section[]>(this.base);
  }
}
