import { Component, inject } from '@angular/core';
import { RouterLink } from '@angular/router';
import { AuthService } from '../../servicios/auth';

@Component({
  selector: 'app-footer',
  imports: [RouterLink],
  templateUrl: './footer.html',
  styleUrl: './footer.css'
})
export class Footer {
  readonly authService = inject(AuthService);
  readonly year = new Date().getFullYear();
  readonly isLoggedIn = this.authService.isAuthenticated;
}

