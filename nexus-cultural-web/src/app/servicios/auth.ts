import { Injectable, inject, signal, computed } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Router } from '@angular/router';
import { environment } from '../../environments/environment';
import {
  SolicitarOtpRequest,
  ValidarOtpRequest,
  AuthTokenResponse,
  MensajeRespuestaResponse
} from '../modelos/auth';

@Injectable({ providedIn: 'root' })
export class AuthService {
  private http = inject(HttpClient);
  private router = inject(Router);
  private url = `${environment.apiUrl}/usuarios`;

  token = signal<string | null>(localStorage.getItem('token'));
  isAuthenticated = computed(() => !!this.token());
  isLoggedIn = this.isAuthenticated;

  userId = signal<string | null>(localStorage.getItem('user_id'));
  userEmail = signal<string | null>(localStorage.getItem('user_email'));
  currentRole = signal<string | null>(localStorage.getItem('rol'));
  currentName = signal<string | null>(localStorage.getItem('nombre'));
  nivelAcceso = signal<string | null>(localStorage.getItem('nivel_acceso'));

  isArtisan = computed(() => {
    const r = this.currentRole()?.toUpperCase().trim();
    return r === 'ARTESANO' || r === 'ARTISAN';
  });

  isBuyer = computed(() => {
    const r = this.currentRole()?.toUpperCase().trim();
    return r === 'COMPRADOR' || r === 'BUYER';
  });

  isAdmin = computed(() => {
    const r = this.currentRole()?.toUpperCase().trim();
    return r === 'ADMINISTRADOR' || r === 'ADMIN';
  });

  solicitarOtp(request: SolicitarOtpRequest) {
    return this.http.post<MensajeRespuestaResponse>(`${this.url}/solicitar`, request);
  }

  validarOtp(request: ValidarOtpRequest) {
    return this.http.post<AuthTokenResponse>(`${this.url}/verificar`, request);
  }

  guardarSesion(respuesta: AuthTokenResponse, emailSolicitado?: string): void {
    const tokenStr = respuesta.token;
    localStorage.setItem('token', tokenStr);
    this.token.set(tokenStr);

    // Intentamos extraer datos directamente del JWT si el backend los empacó
    const claims = this.decodificarToken(tokenStr);

    const rol = respuesta.rol || claims?.rol || 'COMPRADOR';
    const id = claims?.id || '';
    const email = emailSolicitado || claims?.sub || '';
    const nombre = respuesta.nombre || claims?.nombre || email.split('@')[0];
    const nivelAcceso = respuesta.nivelAcceso || claims?.nivelAcceso || '';

    localStorage.setItem('rol', rol);
    localStorage.setItem('user_id', id);
    localStorage.setItem('user_email', email);
    localStorage.setItem('nombre', nombre);
    if (nivelAcceso) localStorage.setItem('nivel_acceso', nivelAcceso);

    this.currentRole.set(rol);
    this.userId.set(id);
    this.userEmail.set(email);
    this.currentName.set(nombre);
    this.nivelAcceso.set(nivelAcceso);
  }

  obtenerRutaPorRol(): string {
    if (this.isArtisan()) return '/artesano';
    if (this.isAdmin()) return '/admin';
    return '/catalogo';
  }

  getToken(): string | null {
    return localStorage.getItem('token') ?? this.token();
  }

  logout(): void {
    localStorage.removeItem('token');
    localStorage.removeItem('token_type');
    localStorage.removeItem('rol');
    localStorage.removeItem('user_id');
    localStorage.removeItem('user_email');
    localStorage.removeItem('nombre');
    localStorage.removeItem('nivel_acceso');

    this.token.set(null);
    this.userId.set(null);
    this.userEmail.set(null);
    this.currentRole.set(null);
    this.currentName.set(null);
    this.nivelAcceso.set(null);

    this.router.navigate(['/']);
  }

  private decodificarToken(token: string): any {
    try {
      const parts = token.split('.');
      if (parts.length >= 2) {
        const payload = atob(parts[1].replace(/-/g, '+').replace(/_/g, '/'));
        return JSON.parse(decodeURIComponent(escape(payload)));
      }
    } catch (_) {}
    return null;
  }
}
