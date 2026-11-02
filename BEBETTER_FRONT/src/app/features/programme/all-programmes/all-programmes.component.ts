import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterLink } from '@angular/router';

import { ProgrammeServiceService } from '../programme-service/programme-service.service';
import { AuthService } from '../../../core/auth/auth.service';
import { Subject } from '../models/subject.model';
import { Mission } from '../models/mission.model';
import { Progress, subjectProgress } from '../models/progress.util';

@Component({
  selector: 'app-all-programmes',
  standalone: true,
  imports: [CommonModule, RouterLink],
  templateUrl: './all-programmes.component.html',
  styleUrl: './all-programmes.component.css'
})
export class AllProgrammesComponent implements OnInit {

  subjects: Subject[] = [];
  loading = false;
  error: string | null = null;
  username: string = "";

  constructor(
    private programmeService: ProgrammeServiceService,
    private authService: AuthService
  ) {}

  ngOnInit(): void {
    this.loadSubjects();
  }

  loadSubjects(): void {
    this.loading = true;
    this.error = null ;

    // Read the username from the token: currentUser may still be null right after a page refresh
    this.username = this.authService.getUsername();

    this.programmeService.getAllSubjects(this.username).subscribe({
      next: (data) => {
        this.subjects = data ?? [];
        this.subjects.forEach(subject => subject.showMissions = false); // initialize showMissions flag
        this.loading = false;
      },
      error: (err) => {
        console.error(err);
        this.error = 'Failed to load subjects';
        this.loading = false;
      }
    });
  }

  progressOf(subject: Subject): Progress {
    return subjectProgress(subject);
  }

  toggleMissions(subject: Subject): void {
    subject.showMissions = !subject.showMissions;
  }

  // The backend only supports marking a mission as done (no undo), so ask before sending.
  confirmMission(subject: Subject, mission: Mission): void {
    if (!subject.id || !mission.id) {
      console.error('Missing IDs for mission update');
      return;
    }
    if (mission.etat) return;
    if (!window.confirm(`Confirm that you completed "${mission.name}"?`)) return;

    this.programmeService
      .updateMissionStatus(this.username, subject.id, mission.id)
      .subscribe({
        next: () => mission.etat = true,
        error: (err) => {
          console.error('Failed to update mission', err);
          this.error = 'Failed to confirm mission, please try again';
        }
      });
  }
}
