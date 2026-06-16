import { Component, OnInit } from '@angular/core';
import { Router } from '@angular/router';

import { SectionService } from '../../../core/services/section.service';
import { Section } from '../../../core/models/section.model';

interface SectionGroup {
  gradeName: string;
  sections: Section[];
}

@Component({
  selector: 'app-section-select',
  templateUrl: './section-select.component.html',
  styleUrls: ['./section-select.component.scss']
})
export class SectionSelectComponent implements OnInit {
  groups: SectionGroup[] = [];
  loading = true;
  errorMessage = '';

  constructor(private sectionService: SectionService, private router: Router) {}

  ngOnInit(): void {
    this.sectionService.getSections().subscribe({
      next: sections => {
        this.groups = this.groupByGrade(sections);
        this.loading = false;
      },
      error: err => {
        this.errorMessage = err.message || 'No fue posible cargar las secciones.';
        this.loading = false;
      }
    });
  }

  goBack(): void {
    this.router.navigate(['/']);
  }

  open(section: Section): void {
    this.router.navigate(['/profesor', section.id]);
  }

  private groupByGrade(sections: Section[]): SectionGroup[] {
    const map = new Map<string, SectionGroup>();
    for (const section of sections) {
      const key = section.gradeName ?? `Grado ${section.gradeId}`;
      const existing = map.get(key);
      if (existing) {
        existing.sections.push(section);
      } else {
        map.set(key, { gradeName: key, sections: [section] });
      }
    }
    return Array.from(map.values()).sort((a, b) =>
      a.gradeName.localeCompare(b.gradeName)
    );
  }
}
