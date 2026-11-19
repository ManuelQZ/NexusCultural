import { Component, inject, OnInit } from '@angular/core';
import { Router, RouterLink } from '@angular/router';
import { AuthService } from '../../servicios/auth';

@Component({
  selector: 'app-inicio',
  imports: [RouterLink],
  templateUrl: './inicio.html',
  styleUrl: './inicio.css'
})
export class Inicio implements OnInit {
  authService = inject(AuthService);
  private router = inject(Router);

  ngOnInit(): void {
    // Si ya está autenticado, redirigir automáticamente a su panel según rol
    if (this.authService.isAuthenticated()) {
      this.router.navigate([this.authService.obtenerRutaPorRol()]);
    }
  }
}
