import { Injectable, inject } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { environment } from '../../environments/environment';
import { CrearArticuloRequest, ArticuloDetalleResponse } from '../modelos/nexus';

@Injectable({ providedIn: 'root' })
export class ArticuloService {
  private http = inject(HttpClient);
  private url = `${environment.apiUrl}/articulos`;

  publicar(request: CrearArticuloRequest) {
    return this.http.post<ArticuloDetalleResponse>(this.url, request);
  }

  actualizar(id: string, request: CrearArticuloRequest) {
    return this.http.put<ArticuloDetalleResponse>(`${this.url}/${id}`, request);
  }

  reponerStock(id: string, deltaCantidad: number) {
    return this.http.patch<ArticuloDetalleResponse>(`${this.url}/${id}/reponer`, {
      deltaCantidad
    });
  }

  inactivar(id: string, artesanoId?: string) {
    let params = new HttpParams();
    if (artesanoId) params = params.set('artesanoId', artesanoId);
    return this.http.patch<ArticuloDetalleResponse>(`${this.url}/${id}/inactivar`, {}, { params });
  }

  reactivar(id: string, artesanoId?: string) {
    let params = new HttpParams();
    if (artesanoId) params = params.set('artesanoId', artesanoId);
    return this.http.patch<ArticuloDetalleResponse>(`${this.url}/${id}/reactivar`, {}, { params });
  }

  eliminar(id: string, artesanoId?: string) {
    let params = new HttpParams();
    if (artesanoId) params = params.set('artesanoId', artesanoId);
    return this.http.delete<void>(`${this.url}/${id}`, { params });
  }
}

