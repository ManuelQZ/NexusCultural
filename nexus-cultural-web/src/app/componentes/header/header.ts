import { Component, inject } from '@angular/core';
import { Router, RouterLink, RouterLinkActive } from '@angular/router';
import { AuthService } from '../../servicios/auth';
import { CarritoService } from '../../servicios/carrito';

@Component({
  selector: 'app-header',
  imports: [RouterLink, RouterLinkActive],
  templateUrl: './header.html',
  styleUrl: './header.css'
})
export class Header {
  authService = inject(AuthService);
  carritoService = inject(CarritoService);
  private router = inject(Router);

  isLoggedIn = this.authService.isAuthenticated;
  usuario = this.authService.currentName;
  rol = this.authService.currentRole;

  cerrarSesion(): void {
    this.authService.logout();
  }
}
