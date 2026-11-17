import { Injectable, inject, signal } from '@angular/core';
import { HttpClient } from '@angular/common/http';
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
  private url = `${environment.apiUrl}/usuarios`;

  isAuthenticated = signal(!!localStorage.getItem('token'));
  currentUser = signal<string | null>(localStorage.getItem('nombre'));
  currentRole = signal<string | null>(localStorage.getItem('rol'));

  solicitarOtp(request: SolicitarOtpRequest) {
    return this.http.post<MensajeRespuestaResponse>(`${this.url}/solicitar`, request);
  }

  validarOtp(request: ValidarOtpRequest) {
    return this.http.post<AuthTokenResponse>(`${this.url}/verificar`, request);
  }

  guardarSesion(respuesta: AuthTokenResponse): void {
    localStorage.setItem('token', respuesta.token);
    localStorage.setItem('token_type', respuesta.tipo);
    localStorage.setItem('rol', respuesta.rol);
    if (respuesta.nombre) {
      localStorage.setItem('nombre', respuesta.nombre);
      this.currentUser.set(respuesta.nombre);
    }
    if (respuesta.nivelAcceso) {
      localStorage.setItem('nivelAcceso', respuesta.nivelAcceso);
    }
    this.currentRole.set(respuesta.rol);
    this.isAuthenticated.set(true);
  }

  getToken(): string | null {
    return localStorage.getItem('token');
  }

  getRol(): string | null {
    return localStorage.getItem('rol');
  }

  logout(): void {
    localStorage.removeItem('token');
    localStorage.removeItem('token_type');
    localStorage.removeItem('rol');
    localStorage.removeItem('nombre');
    localStorage.removeItem('nivelAcceso');
    this.currentUser.set(null);
    this.currentRole.set(null);
    this.isAuthenticated.set(false);
  }
}
