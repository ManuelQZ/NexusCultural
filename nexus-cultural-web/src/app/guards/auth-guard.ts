import { inject } from '@angular/core';
import { CanActivateFn, Router } from '@angular/router';
import { AuthService } from '../servicios/auth';

// Protege rutas que requieren cualquier sesión activa
export const authGuard: CanActivateFn = () => {
  const auth = inject(AuthService);
  const router = inject(Router);

  if (auth.isAuthenticated()) {
    return true;
  }
  return router.createUrlTree(['/login']);
};

// Rutas de invitados (login / registro). Si ya tiene sesión, lo envía a su panel
export const guestGuard: CanActivateFn = () => {
  const auth = inject(AuthService);
  const router = inject(Router);

  if (!auth.isAuthenticated()) {
    return true;
  }
  return router.createUrlTree([auth.obtenerRutaPorRol()]);
};

// Solo para rol Artesano
export const artisanGuard: CanActivateFn = () => {
  const auth = inject(AuthService);
  const router = inject(Router);

  if (auth.isAuthenticated() && auth.isArtisan()) {
    return true;
  }
  return router.createUrlTree([auth.obtenerRutaPorRol()]);
};

// Solo para rol Administrador
export const adminGuard: CanActivateFn = () => {
  const auth = inject(AuthService);
  const router = inject(Router);

  if (auth.isAuthenticated() && auth.isAdmin()) {
    return true;
  }
  return router.createUrlTree([auth.obtenerRutaPorRol()]);
};
