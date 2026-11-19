import { inject } from '@angular/core';
import { HttpInterceptorFn, HttpErrorResponse } from '@angular/common/http';
import { Router } from '@angular/router';
import { catchError, throwError } from 'rxjs';
import { AuthService } from '../servicios/auth';
import { NotificacionesService } from '../servicios/notificaciones';

export const authInterceptor: HttpInterceptorFn = (req, next) => {
  const auth = inject(AuthService);
  const router = inject(Router);
  const avisos = inject(NotificacionesService);
  const token = auth.getToken();

  // No enviar cabecera Authorization a los endpoints públicos de autenticación,
  // registro, ni a peticiones externas como subidas a AWS S3 (Presigned URLs)
  const esS3 = req.url.includes('amazonaws.com') || req.url.includes('s3.');
  const esPublico = req.url.includes('/usuarios/solicitar') ||
                    req.url.includes('/usuarios/verificar') ||
                    (req.method === 'POST' && (req.url.includes('/compradores') || req.url.includes('/artesanos'))) ||
                    esS3;

  const peticion = (token && !esPublico)
    ? req.clone({ setHeaders: { Authorization: `Bearer ${token}` } })
    : req;

  return next(peticion).pipe(
    catchError((error: HttpErrorResponse) => {
      if ((error.status === 401 || error.status === 403) && token && !req.url.includes('/usuarios/')) {
        auth.logout();
        avisos.error('Tu sesión venció. Inicia sesión de nuevo.');
        router.navigate(['/login']);
      }
      return throwError(() => error);
    })
  );
};
