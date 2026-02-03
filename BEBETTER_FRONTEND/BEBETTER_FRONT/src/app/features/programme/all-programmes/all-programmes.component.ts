import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';

import { ProgrammeServiceService } from '../programme-service/programme-service.service';
import { AuthService } from '../../../core/auth/auth.service';
import { Subject } from '../models/subject.model';

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
}
