import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { ActivatedRoute, RouterLink } from '@angular/router';

import { ProgrammeServiceService } from '../programme-service/programme-service.service';
import { AuthService } from '../../../core/auth/auth.service';
import { Subject } from '../models/subject.model';
import { Mission } from '../models/mission.model';
import { Progress, subjectProgress } from '../models/progress.util';

@Component({
  selector: 'app-programme-detail',
  standalone: true,
  imports: [CommonModule, RouterLink],
  templateUrl: './programme-detail.component.html',
  styleUrl: './programme-detail.component.css'
})
export class ProgrammeDetailComponent implements OnInit {

  subject: Subject | null = null;
  progress: Progress = { done: 0, total: 0, percent: 0 };
  loading = false;
  error: string | null = null;
  confirmingId: string | null = null;
  private username = '';

  constructor(
    private route: ActivatedRoute,
    private programmeService: ProgrammeServiceService,
    private authService: AuthService
  ) {}

  ngOnInit(): void {
    this.username = this.authService.getUsername();
    const subjectId = this.route.snapshot.paramMap.get('id');
    if (!subjectId) {
      this.error = 'Programme not found';
      return;
    }
    this.loadSubject(subjectId);
  }

  loadSubject(subjectId: string): void {
    this.loading = true;
    this.error = null;

    this.programmeService.getSubject(this.username, subjectId).subscribe({
      next: (data) => {
        this.subject = data;
        this.progress = subjectProgress(data);
        this.loading = false;
      },
      error: (err) => {
        console.error(err);
        this.error = 'Failed to load programme';
        this.loading = false;
      }
    });
  }

  // The backend only supports marking a mission as done (no undo), so ask before sending.
  confirmMission(mission: Mission): void {
    if (!this.subject?.id || !mission.id || mission.etat) return;
    if (!window.confirm(`Confirm that you completed "${mission.name}"?`)) return;

    this.confirmingId = mission.id;
    this.programmeService
      .updateMissionStatus(this.username, this.subject.id, mission.id)
      .subscribe({
        next: () => {
          mission.etat = true;
          this.progress = subjectProgress(this.subject!);
          this.confirmingId = null;
        },
        error: (err) => {
          console.error('Failed to update mission', err);
          this.error = 'Failed to confirm mission, please try again';
          this.confirmingId = null;
        }
      });
  }
}
