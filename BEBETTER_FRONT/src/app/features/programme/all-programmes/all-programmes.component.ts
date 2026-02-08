import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';

import { ProgrammeServiceService } from '../programme-service/programme-service.service';
import { AuthService } from '../../../core/auth/auth.service';
import { Subject } from '../models/subject.model';
import { Mission } from '../models/mission.model';

@Component({
  selector: 'app-all-programmes',
  standalone: true,
  imports: [CommonModule],
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

     this.authService.currentUser.subscribe(user => {
      this.username = user ? user.username : "";
    });

    this.programmeService.getAllSubjects(this.username).subscribe({
      next: (data) => {
        this.subjects = data;
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

  toggleMissions(subject: Subject): void {
    subject.showMissions = !subject.showMissions;
  }


  onMissionToggle(subject: Subject, mission: Mission): void {
    if (!subject.id || !mission.id) {
  console.error('Missing IDs for mission update');
  return;
}
  // Optimistically toggle the mission state in the UI
  mission.etat = !mission.etat;

  this.programmeService
    .updateMissionStatus(this.username, subject.id, mission.id)
    .subscribe({
      next: (res) => {
        console.log('Mission status updated:', res);
      },
      error: (err) => {
        console.error('Failed to update mission', err);
        // Revert UI change if backend fails
        mission.etat = !mission.etat;
      }
    });
}
}
