import { Component, OnInit, OnDestroy } from '@angular/core';
import { Router } from '@angular/router';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { Subscription } from 'rxjs';
import { ProgrammeServiceService } from '../../features/programme/programme-service/programme-service.service';
import { AuthService } from '../../core/auth/auth.service';

@Component({
  selector: 'app-home',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './home.component.html',
  styleUrl: './home.component.css'
})
export class HomeComponent implements OnInit, OnDestroy {
  
  userPrompt: string = '';
  isLoading: boolean = false;
  username: string = '';
  private userSubscription?: Subscription;

  topPicks = [
    {
      title: 'Communication & Public Speaking',
      description: 'Développez votre capacité à communiquer clairement et avec impact. Améliorez votre articulation, votre aisance, et apprenez à présenter vos idées avec confiance.'
    },
    {
      title: 'Emotional Intelligence',
      description: 'Apprenez à comprendre, gérer et utiliser vos émotions pour mieux collaborer. Développez votre empathie, votre gestion du stress et votre capacité à travailler avec n\'importe quel profil.'
    },
    {
      title: 'Time Management & Productivity',
      description: 'Maîtrisez votre temps, organisez vos journées, et éliminez la procrastination. Apprenez les méthodes efficaces : Pomodoro, Deep Work, Eisenhower Matrix…'
    }
  ];

  constructor(
    private programmeService: ProgrammeServiceService,
    private authService: AuthService,
    private router: Router
  ) {}

  ngOnInit(): void {
    this.userSubscription = this.authService.currentUser$.subscribe({
      next: (user) => {
        if (user) {
          this.username = user.username;
          console.log('Current user:', user);
        }
      },
      error: (error) => {
        console.error('Error getting user:', error);
      }
    });
  }

  ngOnDestroy(): void {
    if (this.userSubscription) {
      this.userSubscription.unsubscribe();
    }
  }

  generateProgramme(): void {
    if (!this.userPrompt.trim()) {
      alert('Please enter what\'s on your mind');
      return;
    }

    if (!this.username) {
      alert('User not authenticated');
      return;
    }

    this.isLoading = true;
    
    this.programmeService.addSubject(this.username, this.userPrompt)
      .subscribe({
        next: (response) => {
          console.log('Programme created:', response);
          this.isLoading = false;
          this.userPrompt = '';
          this.router.navigate(['/programmes/layout/allprogrammes']);
        },
        error: (error) => {
          console.error('Error creating programme:', error);
          this.isLoading = false;
          alert('Failed to create programme. Please try again.');
        }
      });
  }

  startProgramme(cardTitle: string, cardDescription: string): void {
    if (!this.username) {
      alert('User not authenticated');
      return;
    }

    this.isLoading = true;
    const prompt = `${cardTitle}: ${cardDescription}`;
    
    this.programmeService.addSubject(this.username, prompt)
      .subscribe({
        next: (response) => {
          console.log('Programme created from card:', response);
          this.isLoading = false;
          this.router.navigate(['/programmes/layout/allprogrammes']);
        },
        error: (error) => {
          console.error('Error creating programme:', error);
          this.isLoading = false;
          alert('Failed to create programme. Please try again.');
        }
      });
  }

  viewAllProgrammes(): void {
    this.router.navigate(['/programmes/layout/allprogrammes']);
  }
}