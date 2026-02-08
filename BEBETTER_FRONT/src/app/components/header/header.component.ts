import { CommonModule } from '@angular/common';
import { Component } from '@angular/core';
import { ProgrammeRoutingModule } from "../../features/programme/programme-routing.module";
import { RouterLink, RouterModule } from '@angular/router';
import { AuthService } from '../../core/auth/auth.service';

@Component({
  selector: 'app-header',
  imports: [CommonModule, ProgrammeRoutingModule,RouterModule],
  templateUrl: './header.component.html',
  styleUrls: ['./header.component.css']
})
export class HeaderComponent {

  username : string | null =null;

  constructor(private authService: AuthService) {
    this.authService.currentUser.subscribe(user => {
      this.username = user ? user.username : null;
    });
  }
}
