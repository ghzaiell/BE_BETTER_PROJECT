import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterLink } from '@angular/router';

import { ProgrammeServiceService } from '../programme-service/programme-service.service';
import { AuthService } from '../../../core/auth/auth.service';
import { Subject } from '../models/subject.model';
import { Progress, overallProgress, subjectProgress } from '../models/progress.util';

@Component({
  selector: 'app-programme-dashboard',
  standalone: true,
  imports: [CommonModule, RouterLink],
  templateUrl: './programme-dashboard.component.html',
  styleUrl: './programme-dashboard.component.css'
})
export class ProgrammeDashboardComponent implements OnInit {

  rows: { subject: Subject; progress: Progress }[] = [];
  overall: Progress = { done: 0, total: 0, percent: 0 };
  completedProgrammes = 0;
  loading = false;
  error: string | null = null;

  constructor(
    private programmeService: ProgrammeServiceService,
    private authService: AuthService
  ) {}

  ngOnInit(): void {
    this.loading = true;
    this.programmeService.getAllSubjects(this.authService.getUsername()).subscribe({
      next: (subjects) => {
        subjects = subjects ?? [];
        this.rows = subjects.map(subject => ({ subject, progress: subjectProgress(subject) }));
        this.overall = overallProgress(subjects);
        this.completedProgrammes = this.rows
          .filter(r => r.progress.total > 0 && r.progress.done === r.progress.total).length;
        this.loading = false;
      },
      error: (err) => {
        console.error(err);
        this.error = 'Failed to load dashboard';
        this.loading = false;
      }
    });
  }
}
