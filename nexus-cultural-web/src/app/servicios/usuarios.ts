import { Injectable, inject } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { environment } from '../../environments/environment';
import {
  RegistrarCompradorRequest,
  CompradorResponse,
  RegistrarArtesanoRequest,
  ArtesanoResponse,
  CrearAdministradorRequest,
  AdministradorResponse
} from '../modelos/nexus';

@Injectable({ providedIn: 'root' })
export class UsuariosService {
  private http = inject(HttpClient);

  registrarComprador(request: RegistrarCompradorRequest) {
    return this.http.post<CompradorResponse>(`${environment.apiUrl}/compradores`, request);
  }

  consultarComprador(id: string) {
    return this.http.get<CompradorResponse>(`${environment.apiUrl}/compradores/${id}`);
  }

  registrarArtesano(request: RegistrarArtesanoRequest) {
    return this.http.post<ArtesanoResponse>(`${environment.apiUrl}/artesanos`, request);
  }

  registrarAdmin(request: CrearAdministradorRequest) {
    return this.http.post<AdministradorResponse>(`${environment.apiUrl}/administradores`, request);
  }

  listarAdmins() {
    return this.http.get<AdministradorResponse[]>(`${environment.apiUrl}/administradores`);
  }

  eliminarAdmin(id: string) {
    return this.http.delete<void>(`${environment.apiUrl}/administradores/${id}`);
  }
}

